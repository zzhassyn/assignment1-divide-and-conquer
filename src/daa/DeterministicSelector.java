package daa;

/**
 * Deterministic order-statistic selection using the Median-of-Medians (BFPRT)
 * algorithm.
 *
 * Design points required by the assignment:
 * - Elements are split into groups of 5.
 * - The median of each group is found (with insertion sort on the tiny group),
 * and the median of those medians is used as a guaranteed "good" pivot.
 * - In-place Lomuto-style partitioning around that pivot.
 * - The algorithm recurses ONLY into the partition that must contain the
 * k-th element (never both sides), which is what keeps it linear.
 *
 * Complexity: Worst case Theta(n).
 * Recurrence: T(n) <= T(n/5) + T(7n/10) + Theta(n).
 * Since n/5 + 7n/10 = 9n/10 < n, the work shrinks geometrically at every
 * level (Akra-Bazzi / recursion-tree intuition: the total work across all
 * levels is a geometric series bounded by Theta(n) * (1 / (1 - 9/10)) =
 * Theta(n)).
 */
public class DeterministicSelector {

    private static final int GROUP_SIZE = 5;
    private final Metrics metrics;

    public DeterministicSelector(Metrics metrics) {
        this.metrics = metrics;
    }

    public int select(int[] a, int k) {
        if (a == null || a.length == 0) {
            throw new IllegalArgumentException("Array must be non-empty");
        }
        if (k < 0 || k >= a.length) {
            throw new IndexOutOfBoundsException("k out of range");
        }
        return select(a, 0, a.length - 1, k);
    }

    private int select(int[] a, int lo, int hi, int k) {
        metrics.enter();
        try {
            while (true) {
                if (lo == hi)
                    return a[lo];

                int pivotIndex = medianOfMedians(a, lo, hi);
                pivotIndex = partition(a, lo, hi, pivotIndex);

                if (k == pivotIndex) {
                    return a[k];
                } else if (k < pivotIndex) {
                    hi = pivotIndex - 1;
                } else {
                    lo = pivotIndex + 1;
                }
            }
        } finally {
            metrics.exit();
        }
    }

    private int medianOfMedians(int[] a, int lo, int hi) {
        int n = hi - lo + 1;
        if (n <= GROUP_SIZE) {
            insertionSort(a, lo, hi);
            return lo + n / 2;
        }

        int numGroups = (n + GROUP_SIZE - 1) / GROUP_SIZE;
        for (int g = 0; g < numGroups; g++) {
            int groupLo = lo + g * GROUP_SIZE;
            int groupHi = Math.min(groupLo + GROUP_SIZE - 1, hi);
            insertionSort(a, groupLo, groupHi);
            int medianIndex = groupLo + (groupHi - groupLo) / 2;
            swap(a, lo + g, medianIndex);
        }

        int medianOfMediansRank = lo + numGroups / 2;
        select(a, lo, lo + numGroups - 1, medianOfMediansRank);
        return medianOfMediansRank;
    }

    private int partition(int[] a, int lo, int hi, int pivotIndex) {
        int pivotValue = a[pivotIndex];
        swap(a, pivotIndex, hi);
        int store = lo;
        for (int i = lo; i < hi; i++) {
            metrics.comparisons++;
            if (a[i] < pivotValue) {
                swap(a, i, store);
                store++;
            }
        }
        swap(a, store, hi);
        return store;
    }

    private void insertionSort(int[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= lo) {
                metrics.comparisons++;
                if (a[j] > key) {
                    a[j + 1] = a[j];
                    metrics.shifts++;
                    j--;
                } else {
                    break;
                }
            }
            a[j + 1] = key;
        }
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
