package lv.abuzdin.systemprogramming.lesson3.server;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerRunningState {

    private AtomicBoolean running = new AtomicBoolean(true);

    public boolean running() {
        return running.get();
    }

    public void stop() {
        running.set(false);
    }
}
