

// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {
    private static PrintStream out;
	private static int comptime = 0;
    private static void log(ProcessState state, int currentProcess,int cputime, int cpudone, int arrival) {
        switch (state) {
            case ARRIVED:
                out.println(comptime + " | Process: " + currentProcess + " arrived... (" + cputime + " " + cpudone + " " + arrival + ")");
                break;
            case INTERRUPTED:
                out.println(comptime + " | Process: " + currentProcess + " interrupted... (" + cputime + " " + cpudone + " " + arrival + ")");
                break;
            case REGISTERED:
                out.println(comptime + " | Process: " + currentProcess + " registered... (" + cputime + " " + cpudone + " " + arrival + ")");
                break;
            case COMPLETED:
                out.println(comptime + " | Process: " + currentProcess + " completed... (" + cputime + " " + cpudone + " " + arrival + ")");
                break;
        }
    }

    public static Results run(int runtime, Vector processVector, Results result) {
        int i = 0;
        int size = processVector.size();
        int completed = 0;
        int timeCount = 0;
        String resultsFile = "Summary-Processes";

        result.schedulingType = "Preemptive";
        result.schedulingName = "Round-Robin";
        try {
            out = new PrintStream(new FileOutputStream(resultsFile));
            Process currentProcess = null;
            Queue<Process> queue = new LinkedList<>();
            Vector<Process> processes = (Vector<Process>) processVector.clone();
            processes.sort(new ProcessComparator());
            while (comptime < runtime) {
                while (i < size && processes.elementAt(i).getArrival() == comptime) {
                    processes.elementAt(i).setId(i);
                    queue.add(processes.elementAt(i));
                    log(ProcessState.ARRIVED, i, processes.elementAt(i).getCpuTime(), processes.elementAt(i).getCpuDone(),
                            processes.elementAt(i).getArrival());
                    i++;
                }
                if (currentProcess == null && !queue.isEmpty()) {
                    currentProcess = queue.remove();
                    timeCount = 0;
                    log(ProcessState.REGISTERED, currentProcess.getId(), currentProcess.getCpuTime(),
                            currentProcess.getCpuDone(), currentProcess.getArrival());
                }
                if (currentProcess != null && currentProcess.getCpuDone() == currentProcess.getCpuTime()) {
                    timeCount = 0;
                    completed++;
                    log(ProcessState.COMPLETED, currentProcess.getId(), currentProcess.getCpuTime(),
                            currentProcess.getCpuDone(), currentProcess.getArrival());
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }
                    if (!queue.isEmpty()) {
                        currentProcess = queue.remove();
                        log(ProcessState.REGISTERED, currentProcess.getId(), currentProcess.getCpuTime(),
                                currentProcess.getCpuDone(), currentProcess.getArrival());
                    }
                    else {
                        currentProcess = null;
                    }
                }
                if (currentProcess != null && timeCount == Process.getTimeSlice()) {
                    timeCount = 0;
                    log(ProcessState.INTERRUPTED, currentProcess.getId(), currentProcess.getCpuTime(),
                            currentProcess.getCpuDone(), currentProcess.getArrival());
                    currentProcess.setNumInterrupted(currentProcess.getNumInterrupted()+1);
                    queue.add(currentProcess);
                    if (!queue.isEmpty()) {
                        currentProcess = queue.remove();
                        log(ProcessState.REGISTERED, currentProcess.getId(), currentProcess.getCpuTime(),
                                currentProcess.getCpuDone(), currentProcess.getArrival());
                    } else {
                        currentProcess = null;
                    }
                }
                if (currentProcess != null) {
                    timeCount++;
                    currentProcess.setCpuDone(currentProcess.getCpuDone()+1);
                }
                comptime++;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }
}
