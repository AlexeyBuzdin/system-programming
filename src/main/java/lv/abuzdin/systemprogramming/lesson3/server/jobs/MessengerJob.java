package lv.abuzdin.systemprogramming.lesson3.server.jobs;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class MessengerJob {

    private static Logger logger = LoggerFactory.getLogger(MessengerJob.class);

    @Inject
    ServerRunningState server;

    private String login;

    private Socket client;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public void start(Socket clientSocket) {
        try(DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {
            this.client = clientSocket;
            this.dataInputStream = in;
            this.dataOutputStream = out;

            doHandshake(in);
            while (server.running()) {
                String line = in.readUTF();
                if (server.running()) {
                    String message = login + " : " + line;
                    sendMessageForClients(message);
                }
            }
        } catch (SocketException | EOFException e) {
            clientDisconnected();
        } catch (IOException e) {
            logger.error("Failed to send a text message", e);
        }
    }

    private void doHandshake(DataInputStream in) throws IOException {
        login = in.readUTF();
        String loginInfo = login + " has entered the chatroom";
        sendMessageForClients(loginInfo);
    }

    private void sendMessageForClients(String message) {
        logger.info(message);
        for (MessengerJob messenger : server.getMessengers()) {
            if(messenger.hashCode() != hashCode()) {
                try {
                    DataOutputStream out = messenger.getDataOutputStream();
                    out.writeUTF(message);
                } catch (IOException e) {
                    logger.error("Failed to send message to messenger " + messenger.toString());
                }
            }
        }
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    private void clientDisconnected() {
        sendMessageForClients(login + " has disconnected");
        server.removeMessengerJob(this);
    }

    public void forceStop() {
        try {
            client.close();
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            logger.error("Socket connection failed to force close: ", e);
        }
    }
}
