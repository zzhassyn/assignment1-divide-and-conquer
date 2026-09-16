package daa;

public class MergeSorter {

    public static final int CUTOFF = 16;

    private int[] buffer;
    private final Metrics metrics;

    public MergeSorter(Metrics metrics) {
        this.metrics = metrics;
    }

    public void sort(int[] a) {
        if (a == null || a.length < 2)
            return;
        buffer = new int[a.length];
        sort(a, 0, a.length - 1);
    }

    private void sort(int[] a, int lo, int hi) {
        metrics.enter();
        try {
            if (hi - lo + 1 <= CUTOFF) {
                insertionSort(a, lo, hi);
                return;
            }
            int mid = lo + (hi - lo) / 2;
            sort(a, lo, mid);
            sort(a, mid + 1, hi);
            merge(a, lo, mid, hi);
        } finally {
            metrics.exit();
        }
    }

    private void merge(int[] a, int lo, int mid, int hi) {
        System.arraycopy(a, lo, buffer, lo, hi - lo + 1);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            metrics.comparisons++;
            if (buffer[i] <= buffer[j]) {
                a[k++] = buffer[i++];
            } else {
                a[k++] = buffer[j++];
            }
            metrics.arrayAccesses += 2;
        }
        while (i <= mid) {
            a[k++] = buffer[i++];
            metrics.arrayAccesses += 2;
        }
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
}
