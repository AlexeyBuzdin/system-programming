package lv.abuzdin.systemprogramming.lesson2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LoggingServer {

    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started; Listening port " + PORT);

        Socket client = serverSocket.accept();
        try(DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            while (true) {
                String line = in.readUTF();
                System.out.println(line);
                out.writeUTF(line);
                Thread.sleep(100);
            }
        } catch (IOException e) {}
        serverSocket.close();
    }
}
