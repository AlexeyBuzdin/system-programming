package lv.abuzdin.systemprogramming.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatClient {

    private static Logger logger = LoggerFactory.getLogger(ChatClient.class);

    public static final int PORT = 8080;
    public static final String HOST = "localhost";

    private AtomicBoolean running = new AtomicBoolean(true);

    private Socket client;
    private DataInputStream inputStream;

    public void start() {
        try {
            client = new Socket(HOST, PORT);
            System.out.println("Client started");

            final Scanner scanner = new Scanner(System.in);

            System.out.println("Please enter your login: ");
            final String login = scanner.nextLine();
            System.out.println("Feel free to chat");

            new Thread(() -> {
                try {
                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);

                    out.writeUTF(login);
                    String line = scanner.nextLine();
                    while (running.get()) {
                        if (!"exit".equalsIgnoreCase(line)) {
                            out.writeUTF(line);
                            line = scanner.nextLine();
                        } else {
                            closeClient();
                        }
                    }
                } catch (IOException ignored) {}
            }).start();

            new Thread(() -> {
                try (DataInputStream inputStream = new DataInputStream(client.getInputStream())){
                    this.inputStream = inputStream;

                    while (running.get()) {
                        System.out.println(this.inputStream.readUTF());
                        Thread.sleep(1000);
                    }
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception e) {
            logger.error("Client failed to start. ", e);
        }
    }

    private void closeClient() throws IOException {
        running.set(false);
        client.close();
        inputStream.close();
    }
}