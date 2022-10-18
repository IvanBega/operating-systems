package os;

import java.io.*;
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
    private int fAttempts = 0;
    private int gAttempts = 0;
    private boolean fFinished = false;
    private boolean gFinished = false;
    private boolean computationFailed = false;
    String argument = "";

    public void start() throws IOException, InterruptedException {
        ProcessBuilder pb1 = new ProcessBuilder("java", "-cp",
                ".;C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\src\\main\\resources\\lab1.jar", "os.ComputationProcess", "F");
        ProcessBuilder pb2 = new ProcessBuilder("java", "-cp",
                ".;C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\src\\main\\resources\\lab1.jar", "os.ComputationProcess", "G");
        pb1.directory(new File("C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\target\\classes"));
        pb1.redirectErrorStream(true);
        pb2.directory(new File("C:\\DOCS\\Учеба\\5 semester\\OS\\task1\\target\\classes"));
        pb2.redirectErrorStream(true);


        //pb1.inheritIO();
        //pb2.inheritIO();
        try {
            fProcess = pb1.start();
            gProcess = pb2.start();
            //p2 = pb2.start();
            System.out.println("[Manager]: Successfully started F and G");
        } catch (IOException e) {
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
                    Thread.sleep(5000);
                    boolean result = cancellator.check();
                    if (result) {
                        killProcesses();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cancelThread.start();
        String result1 = fReader.readLine();

        FunctionStatus fStatus = analyze(result1, 'f');
        System.out.println(result1);
        System.out.println(fStatus);
        String result2 = gReader.readLine();
        FunctionStatus gStatus = analyze(result2, 'g');
        System.out.println(result2);
        System.out.println(gStatus);

        while (cancellator.isBlocked()) {
            Thread.sleep(50);
        }
        if (cancellator.wasInterrupted()) {
            trySummarize(fStatus, gStatus);
        } else {
            summarize(fStatus, gStatus);
        }
        killProcesses();
        cancellator.setActive(false);
    }

    private void trySummarize(FunctionStatus fStatus, FunctionStatus gStatus) {
        if (fStatus == FunctionStatus.VALUE && gStatus == FunctionStatus.VALUE) {
            summarize(fStatus, gStatus);
        }
    }

    private void summarize(FunctionStatus fStatus, FunctionStatus gStatus) {
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
        while (!isIntOrQuit(input)) {
            System.out.println("Enter x or 'q' to exit: ");
            input = sc.nextLine();
        }
        return input;
    }

    private boolean isIntOrQuit(String value) {
        if (value.startsWith("q")) return true; // exit condition
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void askForInputAndRun() throws IOException, InterruptedException {
        argument = askForInput();
        if (argument.startsWith("q")) return;
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
                System.out.println("[Manager]: Computation failed! Reason: " + funcType + " hard fail - 3/3 attempts reached");
                break;
        }
    }

    private void killProcesses() {
        if (fProcess.isAlive()) fProcess.destroy();
        if (gProcess.isAlive()) gProcess.destroy();
    }

    private FunctionStatus analyze(String result, char funcType) {
        /*
        value - "v1"
        undefined - "undefined"
        max amount of attempts reached - "hard_limit_reached"
        hard fail - "hard"
         */
        if (result == null) {
            return FunctionStatus.FAIL_CANCELLED;
        }
        if (result.startsWith("v")) {
            if (funcType == 'f')
                fResult = Optional.of(Integer.parseInt(result.substring(1)));
            else
                gResult = Optional.of(Integer.parseInt(result.substring(1)));
            return FunctionStatus.VALUE;
        }

        if (result.equals("undefined")) {
            return FunctionStatus.UNDEFINED;
        }
        if (result.equals("hard_limit_reached")) {
            return FunctionStatus.FAIL_LIMIT_REACHED;
        }
        return FunctionStatus.FAIL_HARD;
    }

}
