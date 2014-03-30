package lv.abuzdin.systemprogramming.lesson3.server.jobs;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketConnectorJob {

    private static Logger logger = LoggerFactory.getLogger(SocketConnectorJob.class);

    public static final int MAX_THREADS = 5;

    private ExecutorService socketsExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private ExecutorService openConnectionExecutor = Executors.newSingleThreadExecutor();

    @Inject
    MessengerJob messengerJob;

    @Inject
    ServerRunningState server;

    public void start(ServerSocket serverSocket) {
        openConnectionExecutor.execute(() -> {
            try {
                Socket client = serverSocket.accept();
                server.addConnectedSocket(client);
                socketsExecutor.execute(() -> {messengerJob.start(client);});
                start(serverSocket);
            } catch (IOException e) {
                logger.warn("Waiting for ClientSocket failed");
            }
        });
    }
}
