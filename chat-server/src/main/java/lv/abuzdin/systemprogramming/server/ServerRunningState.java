package lv.abuzdin.systemprogramming.server;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.server.jobs.MessengerJob;
import lv.abuzdin.systemprogramming.server.jobs.SocketConnectorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerRunningState {

    private static Logger logger = LoggerFactory.getLogger(ServerRunningState.class);

    @Inject
    SocketConnectorJob socketConnector;

    private ServerSocket serverSocket;

    private AtomicBoolean running = new AtomicBoolean(true);
    private Deque<MessengerJob> messengers = new ConcurrentLinkedDeque<>();

    public boolean running() {
        return running.get();
    }

    public synchronized void stop() {
        try {
            running.set(false);
            serverSocket.close();

            getMessengers().forEach(MessengerJob::forceStop);

            socketConnector.stop();

            notifyAll();
        } catch (IOException e) {
            logger.error("Failed to forceStop Server", e);
        }
    }

    public void addMessengerJob(MessengerJob messengerJob) {
        messengers.add(messengerJob);
    }

    public void removeMessengerJob(MessengerJob messengerJob) {
        messengers.remove(messengerJob);
    }

    public Deque<MessengerJob> getMessengers() {
        return messengers;
    }

    public synchronized void mainThreadSleep() throws InterruptedException {
        wait();
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
