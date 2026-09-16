package daa;

import java.util.Random;

public class InputGenerator {

    public enum Type {
        RANDOM, SORTED, REVERSE_SORTED, DUPLICATE_HEAVY
    }

    private final Random random;

    public InputGenerator(long seed) {
        this.random = new Random(seed);
    }

    public int[] generate(Type type, int n) {
        int[] a = new int[n];
        switch (type) {
            case RANDOM:
                for (int i = 0; i < n; i++)
                    a[i] = random.nextInt(1_000_000);
                break;
            case SORTED:
                for (int i = 0; i < n; i++)
                    a[i] = i;
                break;
            case REVERSE_SORTED:
                for (int i = 0; i < n; i++)
                    a[i] = n - i;
                break;
            case DUPLICATE_HEAVY:
                int distinctValues = Math.max(1, n / 20); // ~5% distinct values
                for (int i = 0; i < n; i++)
                    a[i] = random.nextInt(distinctValues);
                break;
        }
        return a;
    }

    public Point[] generatePoints(int n) {
        Point[] pts = new Point[n];
        for (int i = 0; i < n; i++) {
            pts[i] = new Point(random.nextDouble() * 100_000, random.nextDouble() * 100_000);
        }
        return pts;
    }
}
