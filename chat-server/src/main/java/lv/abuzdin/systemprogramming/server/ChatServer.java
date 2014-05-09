package lv.abuzdin.systemprogramming.server;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.server.jobs.ConsoleControlJob;
import lv.abuzdin.systemprogramming.server.jobs.SocketConnectorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class ChatServer {

    private static Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static final int PORT = 8080;

    @Inject
    ConsoleControlJob consoleControl;

    @Inject
    SocketConnectorJob socketConnector;

    @Inject
    ServerRunningState server;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            server.setServerSocket(serverSocket);
            logger.info("Server started; Listening port " + PORT);

            consoleControl.start();
            socketConnector.start(serverSocket);
            server.mainThreadSleep();
        } catch (Exception e) {
            logger.error("Server crashed on startup: ", e);
        }
    }
}
