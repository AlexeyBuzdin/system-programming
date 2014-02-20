package lv.abuzdin.systemprogramming.lesson1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    static final AtomicBoolean stopThreads = new AtomicBoolean(false);
    static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                while (!stopThreads.get()) {
                    String value = in.readLine();
                    if(value.trim().equalsIgnoreCase("q")) {
                        stopThreads.getAndSet(true);
                    } else {
                        queue.add(value);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {
            try {
                while (!stopThreads.get()) {
                    for (String value : queue) {
                        queue.remove(value);
                        System.out.println(value);
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
