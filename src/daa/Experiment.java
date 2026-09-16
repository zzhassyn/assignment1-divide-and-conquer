package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Experiment {

    private static final int[] SIZES = { 1_000, 5_000, 10_000, 50_000, 100_000, 500_000 };
    private static final InputGenerator.Type[] TYPES = {
            InputGenerator.Type.RANDOM,
            InputGenerator.Type.SORTED,
            InputGenerator.Type.REVERSE_SORTED,
            InputGenerator.Type.DUPLICATE_HEAVY
    };

    static class Row {
        String algorithm;
        String inputType;
        int n;
        double timeMs;
        int maxDepth;
        long comparisons;
        long swaps;
        long shifts;
    }

    private final List<Row> rows = new ArrayList<>();

    public void runAll() {
        for (InputGenerator.Type type : TYPES) {
            for (int n : SIZES) {
                runMergeSort(type, n);
                runQuickSort(type, n);
                runSelect(type, n);
            }
        }
        int[] closestPairSizes = { 500, 1_000, 2_000, 5_000, 10_000, 50_000, 100_000 };
        for (int n : closestPairSizes) {
            runClosestPair(n);
        }
    }

    private void runMergeSort(InputGenerator.Type type, int n) {
        InputGenerator gen = new InputGenerator(42);
        int[] a = gen.generate(type, n);
        Metrics m = new Metrics();
        MergeSorter sorter = new MergeSorter(m);

        long start = System.nanoTime();
        sorter.sort(a);
        m.elapsedNanos = System.nanoTime() - start;

        assertSorted(a);
        record("MergeSort", type, n, m);
    }

    private void runQuickSort(InputGenerator.Type type, int n) {
        InputGenerator gen = new InputGenerator(42);
        int[] a = gen.generate(type, n);
        Metrics m = new Metrics();
        QuickSorter sorter = new QuickSorter(m, 7);

        long start = System.nanoTime();
        sorter.sort(a);
        m.elapsedNanos = System.nanoTime() - start;

        assertSorted(a);
        record("QuickSort", type, n, m);
    }

    private void runSelect(InputGenerator.Type type, int n) {
        InputGenerator gen = new InputGenerator(42);
        int[] a = gen.generate(type, n);
        Metrics m = new Metrics();
        DeterministicSelector selector = new DeterministicSelector(m);
        int k = n / 2;

        long start = System.nanoTime();
        selector.select(a, k);
        m.elapsedNanos = System.nanoTime() - start;

        record("DeterministicSelect", type, n, m);
    }

    private void runClosestPair(int n) {
        InputGenerator gen = new InputGenerator(42);
        Point[] pts = gen.generatePoints(n);
        Metrics m = new Metrics();
        ClosestPairSolver solver = new ClosestPairSolver(m);

        long start = System.nanoTime();
        solver.solve(pts);
        m.elapsedNanos = System.nanoTime() - start;

        record("ClosestPair", InputGenerator.Type.RANDOM, n, m);
    }

    private void assertSorted(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                throw new IllegalStateException("Array not sorted at index " + i);
            }
        }
    }

    private void record(String algorithm, InputGenerator.Type type, int n, Metrics m) {
        Row r = new Row();
        r.algorithm = algorithm;
        r.inputType = type.name();
        r.n = n;
        r.timeMs = m.elapsedNanos / 1_000_000.0;
        r.maxDepth = m.maxDepth;
        r.comparisons = m.comparisons;
        r.swaps = m.swaps;
        r.shifts = m.shifts;
        rows.add(r);
        System.out.printf("%-20s %-16s n=%-8d time=%8.3fms depth=%-5d comparisons=%-10d swaps=%-8d shifts=%d%n",
                algorithm, type, n, r.timeMs, r.maxDepth, r.comparisons, r.swaps, r.shifts);
    }

    public void saveCsv(String path) throws IOException {
        Path p = Path.of(path);
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p))) {
            pw.println("algorithm,input_type,n,time_ms,max_depth,comparisons,swaps,shifts");
            for (Row r : rows) {
                pw.printf("%s,%s,%d,%.4f,%d,%d,%d,%d%n",
                        r.algorithm, r.inputType, r.n, r.timeMs, r.maxDepth, r.comparisons, r.swaps, r.shifts);
            }
        }
    }
}
