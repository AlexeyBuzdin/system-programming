package lv.abuzdin.systemprogramming;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lv.abuzdin.systemprogramming.client.ChatClient;
import lv.abuzdin.systemprogramming.guice.GuiceModule;
import lv.abuzdin.systemprogramming.server.ChatServer;

public class Main {

    public static final String CLIENT = "--client";
    public static final String SERVER = "--server";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        if (args.length < 1 || args[0].equals(SERVER)) {
            ChatServer chatServer = injector.getInstance(ChatServer.class);
            chatServer.start();
        } else if(args[0].equals(CLIENT)) {
            ChatClient chatClient = injector.getInstance(ChatClient.class);
            chatClient.start();
        } else {
            throw new IllegalArgumentException("Application failed to find right configuration. Please review your input");
        }
    }
}