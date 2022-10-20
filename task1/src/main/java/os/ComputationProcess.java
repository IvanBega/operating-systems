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
            beginComputation();
        }
        sendCommand(command);

    }

    private static void initialize() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
    private static void beginComputation() {
        Optional<Optional<Integer>> result = null;
        try {
            if (type == 'F') result = IntOps.trialF(x);
            else result = IntOps.trialG(x);
        }
        catch (InterruptedException e) {
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
            // hard fail
            command = "hard";
            finished = true;

        }
    }
    private static void listenForCommand() throws IOException, InterruptedException {
    }
    private static void sendCommand(String cmd) {
        System.out.println(cmd);
    }
}
