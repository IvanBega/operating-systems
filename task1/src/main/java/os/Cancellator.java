package os;

import java.io.IOException;
import java.util.Scanner;

public class Cancellator {
    private boolean active = true;
    private boolean blocked = false;
    private boolean interrupted = false;
    private final int PERIOD = 5000;
    public Cancellator() {
    }
    public boolean isBlocked() {
        return blocked;
    }
    public boolean check() throws InterruptedException {
        Thread.sleep(PERIOD);
        if (!active) return false;
        printOptions();
        Scanner scanner = new Scanner(System.in);
        blocked = true;
        int option = 3;
        option = scanner.nextInt();
        blocked = false;
        switch (option) {
            case 1:
                break;
            case 2:
                active = false;
                break;
            case 3:
                active = false;
                interrupted = true;
                return true;
        }
        return false;
    }
    private void printOptions() {
        System.out.println("Please choose one of the following options: ");
        System.out.println("1 - continue");
        System.out.println("2 - continue without prompt");
        System.out.println("3 - cancel");
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }
    public boolean wasInterruptedByUser() { return interrupted; }
}
