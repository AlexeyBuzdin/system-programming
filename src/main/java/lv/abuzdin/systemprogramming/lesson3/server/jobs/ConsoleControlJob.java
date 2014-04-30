package lv.abuzdin.systemprogramming.lesson3.server.jobs;

import com.google.inject.Inject;
import lv.abuzdin.systemprogramming.lesson3.server.ServerRunningState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleControlJob {

    private static final String EXIT_COMMAND = "exit";

    private static Logger logger = LoggerFactory.getLogger(ConsoleControlJob.class);

    @Inject
    ServerRunningState server;

    public void start() {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String command;
                while (server.running()) {
                    command = reader.readLine();
                    if (EXIT_COMMAND.equalsIgnoreCase(command)) {
                        logger.info("Server started shutting down");
                        server.stop();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
