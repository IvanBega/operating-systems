package os.lab1.compfuncs.advanced;

import java.util.Optional;
import java.util.Random;

public class IntOps {
    private static Random random = new Random();
    public IntOps() {
    }
    public static Optional<Optional<Integer>> trialF(int x) throws InterruptedException {
        if (x % 4 == 0) {
            throw new InterruptedException();
        }
        Thread.sleep(random.nextInt(3)*1000);
        int option = random.nextInt(10);
        if (option < 5) {
            return Optional.empty();
        }

        if (x > 10) {
            return Optional.of(Optional.empty());
        }

        return Optional.of(Optional.of(x * 2));
    }

    public static Optional<Optional<Integer>> trialG(int x) throws InterruptedException {
        if (x < 3) {
            throw new InterruptedException();
        }
        Thread.sleep(random.nextInt(5)*1000);
        int option = random.nextInt(10);
        if (option < 5) {
            return Optional.empty();
        }

        if (x % 5 == 0) {
            return Optional.of(Optional.empty());
        }

        return Optional.of(Optional.of(x + 4));
    }
}
