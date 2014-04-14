package lv.abuzdin.systemprogramming.lesson3.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static Logger logger = LoggerFactory.getLogger(ChatClient.class);

    public static final int PORT = 8080;
    public static final String HOST = "localhost";

    public void start() {
        try {
            Socket client = new Socket(HOST, PORT);
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
                    for (String line = scanner.nextLine(); !line.equalsIgnoreCase("q"); line = scanner.nextLine()) {
                        out.writeUTF(line);
                    }
                } catch (IOException ignored) {}
            }).start();

            new Thread(() -> {
                try {
                    InputStream inputStream = client.getInputStream();
                    DataInputStream in = new DataInputStream(inputStream);

                    while (true) {
                        System.out.println(in.readUTF());
                        Thread.sleep(1000);
                    }
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception e) {
            logger.error("Client failed to start. ", e);
        }
    }
}