package lv.abuzdin.systemprogramming.lesson3.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    public static final String EXIT_COMMAND = "exit";

    private static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private ExecutorService toolsExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private ExecutorService socketsExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private ExecutorService openConnectionExecutor = Executors.newSingleThreadExecutor();

    private Deque<Socket> deque = new ConcurrentLinkedDeque<>();

    private AtomicBoolean pendingSocketListener = new AtomicBoolean();
    private AtomicBoolean exitFlag = new AtomicBoolean();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            startListeningConsole();
            startServer(serverSocket);

        } catch (Exception e) {
            logger.error("Server crashed: ", e);
        } finally {
            toolsExecutor.shutdownNow();
            socketsExecutor.shutdownNow();
            openConnectionExecutor.shutdownNow();
            logger.info("Server shutdown successfully");
        }
    }

    private void startListeningConsole() {
        toolsExecutor.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
                String command;
                while (!exit()) {
                    command = reader.readLine();
                    if (command.trim().equalsIgnoreCase(EXIT_COMMAND)) {
                        exitFlag.set(true);
                        logger.info("Server started shutting down");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void startServer(ServerSocket serverSocket) throws IOException, InterruptedException {
        logger.info("Server started; Listening port " + PORT);
        while (!exit()) {
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

    private boolean exit() {
        return exitFlag.get();
    }

    private Socket listenForClient(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    private Runnable startChat(Socket client) {
        return () -> {
            try(DataInputStream in = new DataInputStream(client.getInputStream())) {
                String login = in.readUTF();
                while (!exit()) {
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
