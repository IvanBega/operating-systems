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
    private static void beginComputation(Timeout timeout) throws InterruptedException {
        Optional<Optional<Integer>> result = null;
        try {
            if (type == 'F') result = IntOps.trialF(x); else result = IntOps.trialG(x);
            if (!result.isPresent()) {
                // soft fail
                if (attempts == MAX_ATTEMPTS - 1) {
                    finished = true;
                    command = Character.toString(type).toLowerCase();
                }
            } else if (result.get().isPresent()) {
                // value
                x = result.get().get();
                command = Character.toString(type) + x;
                finished = true;
            } else {
                // undefined
                command = Character.toString(type);
                finished = true;

            }
            timeout.setActive(false);
        } catch (InterruptedException e) {
            // hard fail
            finished = true;
            command = (Character.toString(type) + Character.toString(type)).toLowerCase();
        }
    }
    private static void listenForCommand() throws IOException, InterruptedException {
        Timeout timeout = new Timeout(10000);
        computationThread = new Thread(() -> {
            try {

                beginComputation(timeout);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        });
        computationThread.start();
        timeout.start(); // main thread blocked until computation finished or timeout reached
        if (timeout.wasInterrupted()) {
            // was interrupted - function not defined
            finished = true;
            command = Character.toString(type);
        }
    }
    private static void sendCommand(String cmd) {
        System.out.println(cmd);
    }
}
