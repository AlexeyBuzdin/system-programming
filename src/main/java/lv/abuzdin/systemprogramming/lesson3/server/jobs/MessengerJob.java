package lv.abuzdin.systemprogramming.lesson3.server.jobs;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessengerJob {

    private static Logger logger = LoggerFactory.getLogger(MessengerJob.class);

    @Inject
    ServerRunningState server;

    public void start(Socket client) {
        try(DataInputStream in = new DataInputStream(client.getInputStream())) {
            String login = in.readUTF();
            while (server.running()) {
                String line = in.readUTF();
                if (server.running()) {
                    String message = login + " : " + line;
                    logger.info(message);
                    sendMessageForClients(client.hashCode(), message);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to send a text message", e);
        }
    }

    private void sendMessageForClients(int hash, String message) throws IOException {
        for (Socket socket : server.getConnectedSockets()) {
            if(socket.hashCode() != hash) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(message);
            }
        }
    }
}
