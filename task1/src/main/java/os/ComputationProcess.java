package os;

import os.lab1.compfuncs.advanced.IntOps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class ComputationProcess {
    private static BufferedReader reader;
    private static final int MAX_ATTEMPTS = 3;
    private static int attempts = 0;
    private static int x;
    private static Thread computationThread;
    private static char type;
    private static boolean finished = false;
    private static String command;
    public static void main(String[] args) throws InterruptedException, IOException {
        type = args[0].charAt(0);
        initialize();
        String input = reader.readLine();
        x = Integer.parseInt(input);
        while(!finished) {
            listenForCommand();
        }
        sendCommand(command);

    }

    private static void initialize() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
    private static void beginComputation(Timeout timeout) {
        Optional<Optional<Integer>> result = null;
        try {
            if (type == 'F') result = IntOps.trialF(x);
            else result = IntOps.trialG(x);
        }
        catch (InterruptedException e) {
            // hard fail
            finished = true;
            command = "hard";
        }
        if (!result.isPresent()) {
            // soft fail
            attempts++;
            if (attempts == MAX_ATTEMPTS) {
                // hard fail if max amount of attempts reached
                finished = true;
                command = "hard_limit_reached";

            }
        } else if (result.get().isPresent()) {
            // value
            x = result.get().get();
            command = "v" + x;
            finished = true;
        } else {
            // undefined
            command = "undefined";
            finished = true;

        }
        timeout.setActive(false);
    }
    private static void listenForCommand() throws IOException, InterruptedException {
        Timeout timeout = new Timeout(10000);
        computationThread = new Thread(() -> {
            beginComputation(timeout);
        });
        computationThread.start();
        timeout.start(); // main thread blocked until computation finished or timeout reached
        if (timeout.wasInterrupted()) {
            // was interrupted - function not defined
            finished = true;
            command = "undefined";
        }
    }
    private static void sendCommand(String cmd) {
        System.out.println(cmd);
    }
}
