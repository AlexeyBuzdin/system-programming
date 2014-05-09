package lv.abuzdin.systemprogramming.server.jobs;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lv.abuzdin.systemprogramming.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketConnectorJob {

    private static Logger logger = LoggerFactory.getLogger(SocketConnectorJob.class);

    public static final int MAX_THREADS = 5;

    private ExecutorService socketsExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    private ExecutorService openConnectionExecutor = Executors.newSingleThreadExecutor();

    @Inject
    Provider<MessengerJob> messengerJobProvider;

    @Inject
    ServerRunningState server;

    public void start(ServerSocket serverSocket) {
        openConnectionExecutor.execute(() -> {
            try {
                Socket client = serverSocket.accept();
                MessengerJob messengerJob = messengerJobProvider.get();
                server.addMessengerJob(messengerJob);

                socketsExecutor.execute(() -> messengerJob.start(client));
                start(serverSocket);
            } catch (SocketException e) {
                logger.info("ServerSocket closed");
            } catch (IOException e) {
                logger.warn("Waiting for ClientSocket failed");
            }
        });
    }

    public void stop() {
        openConnectionExecutor.shutdownNow();
        socketsExecutor.shutdownNow();
    }
}
