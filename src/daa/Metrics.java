package daa;

public class Metrics {
    public long comparisons = 0;
    public long swaps = 0;
    public long shifts = 0;
    public long recursiveCalls = 0;
    public long arrayAccesses = 0;
    public int maxDepth = 0;
    public int currentDepth = 0;
    public long elapsedNanos = 0;

    public void enter() {
        currentDepth++;
        recursiveCalls++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public void exit() {
        currentDepth--;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        shifts = 0;
        recursiveCalls = 0;
        arrayAccesses = 0;
        maxDepth = 0;
        currentDepth = 0;
        elapsedNanos = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "time=%.3fms, maxDepth=%d, comparisons=%d, swaps=%d, shifts=%d, calls=%d",
                elapsedNanos / 1_000_000.0, maxDepth, comparisons, swaps, shifts, recursiveCalls);
    }
}
