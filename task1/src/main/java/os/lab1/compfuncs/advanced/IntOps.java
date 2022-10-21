package os.lab1.compfuncs.advanced;

import java.util.Optional;
import java.util.Random;

public class IntOps {
    private static Random random = new Random();
    public IntOps() {
    }
    public static Optional<Optional<Integer>> trialF(int x) throws InterruptedException {
        if (x % 4 == 0) {
            Thread.sleep(10000000);
        }
        if (x > 10) {
            return Optional.of(Optional.empty());
        }
        Thread.sleep(random.nextInt(3)*1000);
        int option = random.nextInt(10);
        if (option < 5) {
            return Optional.empty();
        }

        return Optional.of(Optional.of(x * 2));
    }

    public static Optional<Optional<Integer>> trialG(int x) throws InterruptedException {
        if (x < 3) {
            Thread.sleep(10000000);
        }
        if (x > 8) {
            return Optional.of(Optional.empty());
        }
        Thread.sleep(random.nextInt(5)*1000);
        int option = random.nextInt(10);
        if (option < 8) {
            return Optional.empty();
        }

        return Optional.of(Optional.of(x + 4));
    }
}
