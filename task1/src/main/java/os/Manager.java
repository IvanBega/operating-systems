package os;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class Manager {
    private BufferedReader fReader;
    private BufferedReader gReader;
    private BufferedWriter fWriter;
    private BufferedWriter gWriter;
    Process fProcess;
    Process gProcess;
    private Optional<Integer> fResult = Optional.empty();
    private Optional<Integer> gResult = Optional.empty();
    private final int maxAttempts = 3;
    private String result1;
    private String result2;
    private FunctionStatus fStatus;
    private FunctionStatus gStatus;
    String argument = "";

    public void start() throws IOException, InterruptedException {
        /*
        to compile with external jar(not used)
        ProcessBuilder pb1 = new ProcessBuilder("java", "-cp",
                ".;C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\src\\main\\resources\\lab1.jar", "os.ComputationProcess", "F");
        ProcessBuilder pb2 = new ProcessBuilder("java", "-cp",
                ".;C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\src\\main\\resources\\lab1.jar", "os.ComputationProcess", "G");
         */
        ProcessBuilder pb1 = new ProcessBuilder("java", "os.ComputationProcess", "F");
        ProcessBuilder pb2 = new ProcessBuilder("java", "os.ComputationProcess", "G");
        pb1.directory(new File("C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\target\\classes"));
        pb2.directory(new File("C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\target\\classes"));

        try {
            fProcess = pb1.start();
            gProcess = pb2.start();
            System.out.println("[Manager]: Successfully started F and G");
        } catch (IOException e) {
            System.out.println("[Manager]: Error! Could not start processes");
            e.printStackTrace();
        }
        fReader = new BufferedReader(new InputStreamReader(fProcess.getInputStream()));
        gReader = new BufferedReader(new InputStreamReader(gProcess.getInputStream()));
        fWriter = new BufferedWriter(new OutputStreamWriter(fProcess.getOutputStream()));
        gWriter = new BufferedWriter(new OutputStreamWriter(gProcess.getOutputStream()));

        askForInputAndRun();
        Cancellator cancellator = new Cancellator();
        Thread cancelThread = new Thread(() -> {
            while (cancellator.isActive()) {
                try {

                    boolean result = cancellator.check();
                    if (result) {
                        // if user decided to cancel
                        killProcesses();
                    }
                } catch (InterruptedException | NoSuchElementException e) {
                    killProcesses();
                    System.exit(1);
                }
            }
        });
        cancelThread.start();

        Thread gThread = new Thread(() -> {
            try {
                result2 = gReader.readLine();
                if (result2 != null) {
                    gStatus = analyze(result2, 'g');
                    if (isStatusFail(gStatus)) {
                        killProcesses();
                    }
                }
            } catch (IOException e) {

            }
        });
        gThread.start();

        result1 = fReader.readLine();
        if (result1 != null) {
            fStatus = analyze(result1, 'f');
            if (isStatusFail(fStatus)) {
                killProcesses();
            }
        }
        gThread.join();

        // to not intervene user prompt
        while (cancellator.isBlocked()) {
            Thread.sleep(50);
        }

        if (cancellator.wasInterruptedByUser()) {
            trySummarize(fStatus, gStatus);
        } else {
            summarize(fStatus, gStatus);
        }

        cancellator.setActive(false);
        cancelThread.interrupt();


    }

    private void trySummarize(FunctionStatus fStatus, FunctionStatus gStatus) {
        if (fStatus == FunctionStatus.VALUE && gStatus == FunctionStatus.VALUE) {
            // both F and G finished and returned a value - result can be computed
            summarize(fStatus, gStatus);
        } else {
            // result can not be computed
            if (!(isStatusFail(fStatus) || isStatusFail(gStatus))) {
                // if neither of function failed - report that computation was canceled by user
                fStatus = FunctionStatus.FAIL_CANCELLED;
                gStatus = FunctionStatus.FAIL_CANCELLED;
            }
            // if any function failed - report that computation was because of
            // function failure
            summarize(fStatus, gStatus);

        }
    }

    private void summarize(FunctionStatus fStatus, FunctionStatus gStatus) {
        killProcesses();
        if (isStatusFail(fStatus)) {
            failedComputation('F', fStatus);
            return;
        }
        if (isStatusFail(gStatus)) {
            failedComputation('G', gStatus);
            return;
        }
        if (fStatus == FunctionStatus.VALUE && gStatus == FunctionStatus.VALUE) {
            System.out.println("[Manager]: Result of computation is " +
                    fResult.get() * gResult.get());
            return;
        }
        System.out.println("[Manager]: Result of computation is not defined");

    }

    private boolean isStatusFail(FunctionStatus status) {
        return status == FunctionStatus.FAIL_CANCELLED || status == FunctionStatus.FAIL_HARD ||
                status == FunctionStatus.FAIL_LIMIT_REACHED;
    }
    private void sendCommand(String line, BufferedWriter writer) throws IOException {
        writer.write(line);
        writer.newLine();
        writer.flush();
        //System.out.println("[Manager]: Sent " + line);
    }

    private String askForInput() {
        Scanner sc = new Scanner(System.in);
        String input = "";
        while (!isInt(input)) {
            System.out.println("Enter x: ");
            try {
                input = sc.nextLine();
            } catch (NoSuchElementException e) {
                killProcesses();
                System.exit(1);
            }

        }
        return input;
    }

    private boolean isInt(String value) {
        if (value.startsWith("q")) return true; // exit condition
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void askForInputAndRun() throws IOException {
        argument = askForInput();
        sendCommand(argument, fWriter);
        sendCommand(argument, gWriter);
    }

    private void failedComputation(char funcType, FunctionStatus status) {
        switch (status) {
            case FAIL_CANCELLED:
                System.out.println("[Manager]: Computation failed! Reason: cancelled by user");
                break;
            case FAIL_HARD:
                System.out.println("[Manager]: Computation failed! Reason: " + funcType + " hard fail");
                break;
            case FAIL_LIMIT_REACHED:
                System.out.println("[Manager]: Computation failed! Reason: " + funcType + " soft fail - 3/3 attempts reached");
                break;
        }
    }

    private void killProcesses() {
        if (fProcess.isAlive()) fProcess.destroy();
        if (gProcess.isAlive()) gProcess.destroy();
    }

    private FunctionStatus analyze(String result, char funcType) {
        /*
        max amount of attempts reached - "hard_limit_reached"
        hard fail - "hard"
         */
        if (result == null) {
            return FunctionStatus.FAIL_CANCELLED;
        }
        if (!result.startsWith("fail")) {
            // value
            if (funcType == 'f')
                fResult = Optional.of(Integer.parseInt(result));
            else
                gResult = Optional.of(Integer.parseInt(result));
            return FunctionStatus.VALUE;
        }

        if (result.equals("fail_limit_reached")) {
            return FunctionStatus.FAIL_LIMIT_REACHED;
        }
        return FunctionStatus.FAIL_HARD;
    }

}
