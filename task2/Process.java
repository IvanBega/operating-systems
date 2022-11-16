

public class Process {

    private int cputime;
    private int cpudone = 0;
    private int numblocked = 0;
    private int arrival;
    private int id;

    private static int time_slice;

    public Process (int cputime, int arrival) {
        this.cputime = cputime;
        this.arrival = arrival;
    }
    public int getArrival() {
        return arrival;
    }
    public int getCpuTime() {
        return cputime;
    }
    public int getCpuDone() {
        return cpudone;
    }
    public int getNumBlocked() {
        return numblocked;
    }
    public void setCpuTime(int time) {
        this.cputime = time;
    }
    public void setCpuDone(int done) {
        this.cpudone = done;
    }
    public void setNumBlocked(int numblocked) {
        this.numblocked = numblocked;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public static int getTimeSlice() {
        return time_slice;
    }
    public static void setTimeSlice(int timeSlice) {
        time_slice = timeSlice;
    }
}
