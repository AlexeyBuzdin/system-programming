package lv.abuzdin.systemprogramming.lesson3;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lv.abuzdin.systemprogramming.lesson3.guice.GuiceModule;
import lv.abuzdin.systemprogramming.lesson3.server.ChatServer;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new GuiceModule());
        ChatServer chatServer = injector.getInstance(ChatServer.class);
        chatServer.start();
    }

}
