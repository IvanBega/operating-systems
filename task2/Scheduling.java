

// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

public class Scheduling {

    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static int arrival = 0;
    private static Vector processVector = new Vector();
    private static Results result = new Results("null","null",0);
    private static String resultsFile = "Summary-Results";

    private static void Init(String file) {
        File f = new File(file);
        String line;
        String tmp;
        int cputime = 0;
        double X = 0.0;

        try {
            //BufferedReader in = new BufferedReader(new FileReader(f));
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Common.s2i(st.nextToken());
                }
                if (line.startsWith("time_slice")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    Process.setTimeSlice(Common.s2i(st.nextToken()));
                }
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Common.s2i(st.nextToken());
                }
                if (line.startsWith("meandev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    meanDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("standdev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    standardDev = Common.s2i(st.nextToken());
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    arrival = Common.s2i(st.nextToken());
                    X = Common.R1();
                    while (X == -1.0) {
                        X = Common.R1();
                    }
                    X = X * standardDev;
                    cputime = (int) X + meanDev;
                    processVector.addElement(new Process(cputime, arrival));
                }
            }
            in.close();
        } catch (IOException e) { /* Handle exceptions */ }
    }
    public static void main(String[] args) {
        int i = 0;
        String[] arg = new String[1];
        arg[0] = "scheduling.conf";
        if (arg.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File f = new File(arg[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(arg[0]);
        if (processVector.size() < processnum) {
            i = 0;
            while (processVector.size() < processnum) {
                double X = Common.R1();
                while (X == -1.0) {
                    X = Common.R1();
                }
                X = X * standardDev;
                int cputime = (int) X + meanDev;
                processVector.addElement(new Process(cputime,i*100));
                i++;
            }
        }
        result = SchedulingAlgorithm.Run(runtime, processVector, result);
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Mean: " + meanDev);
            out.println("Standard Deviation: " + standardDev);
            out.println("Process #\tCPU Time\tCPU Completed\tCPU Blocked\tArrival");
            for (i = 0; i < processVector.size(); i++) {
                Process process = (Process) processVector.elementAt(i);
                out.print(Integer.toString(i));
                if (i < 100) { out.print("\t\t"); } else { out.print("\t"); }
                out.print(Integer.toString(process.getCpuTime()));
                if (process.getCpuTime() < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
                out.print(Integer.toString(process.getCpuDone()));
                if (process.getCpuDone() < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
                out.println(process.getNumBlocked() + " times");
                if (process.getNumBlocked() < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t"); }
                out.println(process.getArrival());
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        System.out.println("Completed.");
    }
}

