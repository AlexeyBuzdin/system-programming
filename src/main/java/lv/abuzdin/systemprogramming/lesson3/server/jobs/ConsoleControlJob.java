package lv.abuzdin.systemprogramming.lesson3.server.jobs;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleControlJob {

    private static final int MAX_THREADS = 5;
    private static final String EXIT_COMMAND = "exit";

    private static Logger logger = LoggerFactory.getLogger(ConsoleControlJob.class);

    private ExecutorService toolsExecutor = Executors.newFixedThreadPool(MAX_THREADS);

    @Inject
    ServerRunningState server;

    public void start() {
        toolsExecutor.execute(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String command;
                while (server.running()) {
                    command = reader.readLine();
                    if (command.trim().equalsIgnoreCase(EXIT_COMMAND)) {
                        server.stop();
                        logger.info("Server started shutting down");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
