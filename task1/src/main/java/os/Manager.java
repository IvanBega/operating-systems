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
        System.out.println(result1);
        String result2 = gReader.readLine();
        System.out.println(result2);
        killProcesses();
        cancellator.setActive(false);
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
    private void failedComputation(char funcType, String funcReason) throws IOException {
        System.out.println("[Manager]: Function " + funcType + " failed! Reason: " + funcReason);
        if (!fFinished) {
            fFinished = true;
            sendCommand("b", fWriter);
        }
        if (!gFinished) {
            fFinished = true;
            sendCommand("b", gWriter);
        }
        System.out.println("[Manager]: Computation failed! Reason: " + funcType + " hard failed");
        computationFailed = true;
    }
    private void killProcesses() {
        if (fProcess.isAlive()) fProcess.destroy();
        if (gProcess.isAlive()) gProcess.destroy();
    }
}
