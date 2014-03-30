package lv.abuzdin.systemprogramming.lesson3.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerRunningState {

    private static Logger logger = LoggerFactory.getLogger(ServerRunningState.class);

    private ServerSocket serverSocket;

    private AtomicBoolean running = new AtomicBoolean(true);
    private Deque<Socket> connectedSockets = new ConcurrentLinkedDeque<>();

    public boolean running() {
        return running.get();
    }

    public synchronized void stop() {
        try {
            running.set(false);
            serverSocket.close();
            notifyAll();
            System.exit(0);
        } catch (IOException e) {
            logger.error("Failed to stop Server", e);
        }
    }

    public void addConnectedSocket(Socket client) {
        connectedSockets.add(client);
    }

    public Deque<Socket> getConnectedSockets() {
        return connectedSockets;
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
