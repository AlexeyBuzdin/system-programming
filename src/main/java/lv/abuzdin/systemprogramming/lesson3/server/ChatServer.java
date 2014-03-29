package lv.abuzdin.systemprogramming.lesson3.server;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.jobs.ConsoleControlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer {

    public static final int PORT = 8080;
    public static final int MAX_THREADS = 5;

    private static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private ExecutorService socketsExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private ExecutorService openConnectionExecutor = Executors.newSingleThreadExecutor();

    private Deque<Socket> deque = new ConcurrentLinkedDeque<>();

    private AtomicBoolean pendingSocketListener = new AtomicBoolean();

    @Inject
    ConsoleControlJob consoleControl;

    @Inject
    ServerRunningState server;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            consoleControl.start();
            startServer(serverSocket);

        } catch (Exception e) {
            logger.error("Server crashed: ", e);
        } finally {
            socketsExecutor.shutdownNow();
            openConnectionExecutor.shutdownNow();
            logger.info("Server shutdown successfully");
        }
    }

    private void startServer(ServerSocket serverSocket) throws IOException, InterruptedException {
        logger.info("Server started; Listening port " + PORT);
        while (server.running()) {
            if(!pendingSocketListener.get()) {
                pendingSocketListener.set(true);
                openConnectionExecutor.execute(() -> {
                    try {
                        Socket client = listenForClient(serverSocket);
                        deque.add(client);
                        socketsExecutor.execute((startChat(client)));
                        pendingSocketListener.set(false);
                    } catch (IOException e) {
                        logger.warn("Waiting for ClientSocket failed");
                    }
                });
            }
            Thread.sleep(1000);
        }
    }

    private Socket listenForClient(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    private Runnable startChat(Socket client) {
        return () -> {
            try(DataInputStream in = new DataInputStream(client.getInputStream())) {
                String login = in.readUTF();
                while (server.running()) {
                    String line = in.readUTF();
                    String message = login + " : " + line;
                    logger.info(message);
                    sendMessageForClients(client.hashCode(), message);
                }
            } catch (IOException e) {
                logger.error("Failed to send a text message", e);
            }
        };
    }

    private void sendMessageForClients(int hash, String message) throws IOException {
        for (Socket socket : deque) {
            if(socket.hashCode() != hash) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(message);
            }
        }
    }
}
