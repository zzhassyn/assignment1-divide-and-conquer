package daa;

import java.util.Random;

public class QuickSorter {

    private final Metrics metrics;
    private final Random random;

    public QuickSorter(Metrics metrics) {
        this(metrics, new Random());
    }

    public QuickSorter(Metrics metrics, long seed) {
        this(metrics, new Random(seed));
    }

    private QuickSorter(Metrics metrics, Random random) {
        this.metrics = metrics;
        this.random = random;
    }

    public void sort(int[] a) {
        if (a == null || a.length < 2)
            return;
        sort(a, 0, a.length - 1);
    }

    private void sort(int[] a, int lo, int hi) {
        metrics.enter();
        try {
            while (lo < hi) {
                int p = partition(a, lo, hi);

                int leftSize = p - lo;
                int rightSize = hi - p - 1;

                if (leftSize < rightSize) {
                    sort(a, lo, p - 1);
                    lo = p + 1;
                } else {
                    sort(a, p + 1, hi);
                    hi = p - 1;
                }
            }
        } finally {
            metrics.exit();
        }
    }

    private int partition(int[] a, int lo, int hi) {
        int pivotIndex = lo + random.nextInt(hi - lo + 1);
        swap(a, pivotIndex, hi);
        int pivot = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            metrics.comparisons++;
            if (a[j] <= pivot) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    private void swap(int[] a, int i, int j) {
        if (i == j)
            return;
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
        metrics.swaps++;
    }
}
