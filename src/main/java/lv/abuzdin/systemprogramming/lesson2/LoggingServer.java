package lv.abuzdin.systemprogramming.lesson2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class LoggingServer {

    public static final int PORT = 8080;
    public static final int MAX_THREADS = 5;

    private static Executor executor = Executors.newFixedThreadPool(MAX_THREADS);
    private static final ConcurrentLinkedDeque<Socket> deque = new ConcurrentLinkedDeque<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started; Listening port " + PORT);

        while (!exit()) {
            Socket client = listenForClient(serverSocket);
            deque.add(client);
            executor.execute(startChat(client));
        }
        serverSocket.close();
    }

    private static boolean exit() {
        return false;
    }

    private static Socket listenForClient(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    private static Runnable startChat(Socket client) {
        return () -> {
            try(DataInputStream in = new DataInputStream(client.getInputStream())) {
                String login = in.readUTF();
                while (true) {
                    String line = in.readUTF();
                    String message = login + " : " + line;
                    System.out.println(message);
                    sendMessageForClients(client.hashCode(), message);

                    Thread.sleep(100);
                }
            } catch (IOException | InterruptedException e) {}
        };
    }

    private static void sendMessageForClients(int hash, String message) throws IOException {
        for (Socket socket : deque) {
            if(socket.hashCode() != hash) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(message);
            }
        }
    }
}
