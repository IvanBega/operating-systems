package os;

public class Timeout {
    private long startTime = 0;
    private int timeout = 0;
    private boolean active = false;
    private boolean interrupted = false;
    public void start() {
        active = true;
        startTime = System.currentTimeMillis();
        while (active) {
            if (!active) break;
            long now = System.currentTimeMillis();
            if (now - startTime > timeout) {
                interrupted = true;
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public Timeout(int timeout) {
        this.timeout = timeout;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean wasInterrupted() {
        return interrupted;
    }
}
