package daa;

import java.util.Arrays;
import java.util.Random;

public class CorrectnessTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testMergeSort();
        testQuickSort();
        testDeterministicSelect();
        testClosestPair();

        System.out.println("\n=== Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------------------------------------------------------------- MergeSort

    private static void testMergeSort() {
        System.out.println("-- MergeSort vs Arrays.sort() --");
        Random rnd = new Random(1);
        int[][] cases = {
                new int[] {},
                new int[] { 42 },
                randomArray(rnd, 1000),
                sortedArray(1000),
                reverseSortedArray(1000),
                duplicateHeavyArray(rnd, 1000)
        };
        String[] names = { "empty", "single", "random", "sorted", "reverse-sorted", "duplicate-heavy" };
        for (int i = 0; i < cases.length; i++) {
            int[] expected = cases[i].clone();
            Arrays.sort(expected);
            int[] actual = cases[i].clone();
            new MergeSorter(new Metrics()).sort(actual);
            check("MergeSort[" + names[i] + "]", Arrays.equals(expected, actual));
        }

        boolean leftExhaustsFirstOk = true;
        for (int trial = 0; trial < 50; trial++) {
            int n = 2 + rnd.nextInt(2000);
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = (i < n / 2) ? i : 1_000_000 + i;
            }
            shuffleWithinHalves(a, rnd);
            int[] expected = a.clone();
            Arrays.sort(expected);
            int[] actual = a.clone();
            new MergeSorter(new Metrics()).sort(actual);
            if (!Arrays.equals(expected, actual)) {
                leftExhaustsFirstOk = false;
                System.out.println("  MISMATCH on left-exhausts-first case, n=" + n);
            }
        }
        check("MergeSort[left-half-exhausts-first regression, 50 trials]", leftExhaustsFirstOk);
    }

    private static void shuffleWithinHalves(int[] a, Random rnd) {
        int mid = a.length / 2;
        for (int i = mid - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        for (int i = a.length - 1; i > mid; i--) {
            int j = mid + rnd.nextInt(i - mid + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }

    // ---------------------------------------------------------------- QuickSort

    private static void testQuickSort() {
        System.out.println("-- QuickSort vs Arrays.sort() --");
        Random rnd = new Random(2);
        int[][] cases = {
                new int[] {},
                new int[] { 7 },
                randomArray(rnd, 1000),
                sortedArray(1000),
                reverseSortedArray(1000),
                duplicateHeavyArray(rnd, 1000)
        };
        String[] names = { "empty", "single", "random", "sorted", "reverse-sorted", "duplicate-heavy" };
        for (int i = 0; i < cases.length; i++) {
            int[] expected = cases[i].clone();
            Arrays.sort(expected);
            int[] actual = cases[i].clone();
            new QuickSorter(new Metrics(), 99).sort(actual);
            check("QuickSort[" + names[i] + "]", Arrays.equals(expected, actual));
        }
    }

    // ----------------------------------------------------------------
    // DeterministicSelect

    private static void testDeterministicSelect() {
        System.out.println("-- DeterministicSelect vs Arrays.sort(a)[k] (100+ random trials) --");
        Random rnd = new Random(3);
        int trials = 200;
        boolean allOk = true;
        for (int t = 0; t < trials; t++) {
            int n = 1 + rnd.nextInt(500);
            int[] a = randomArray(rnd, n);
            int k = rnd.nextInt(n);

            int[] reference = a.clone();
            Arrays.sort(reference);
            int expected = reference[k];

            int[] copy = a.clone();
            int actual = new DeterministicSelector(new Metrics()).select(copy, k);

            if (actual != expected) {
                allOk = false;
                System.out.println("  MISMATCH at trial " + t + ": n=" + n + " k=" + k
                        + " expected=" + expected + " actual=" + actual);
            }
        }
        check("DeterministicSelect[" + trials + " random trials]", allOk);
    }

    // ---------------------------------------------------------------- ClosestPair

    private static void testClosestPair() {
        System.out.println("-- ClosestPair (D&C) vs O(n^2) brute force --");
        Random rnd = new Random(4);
        boolean allOk = true;
        int[] sizes = { 2, 3, 4, 10, 50, 200, 1000, 2000 };
        for (int n : sizes) {
            Point[] pts = new Point[n];
            for (int i = 0; i < n; i++) {
                pts[i] = new Point(rnd.nextDouble() * 1000, rnd.nextDouble() * 1000);
            }
            ClosestPairSolver solver = new ClosestPairSolver(new Metrics());
            ClosestPairSolver.PointPair dc = solver.solve(pts);
            ClosestPairSolver.PointPair bf = solver.bruteForce(pts);

            boolean ok = Math.abs(dc.distance - bf.distance) < 1e-9;
            if (!ok) {
                allOk = false;
                System.out.println("  MISMATCH at n=" + n + ": dc=" + dc.distance + " bf=" + bf.distance);
            }
        }
        check("ClosestPair[divide-and-conquer vs brute force, n<=2000]", allOk);
    }

    // ---------------------------------------------------------------- helpers

    private static int[] randomArray(Random rnd, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = rnd.nextInt(100_000);
        return a;
    }

    private static int[] sortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = i;
        return a;
    }

    private static int[] reverseSortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = n - i;
        return a;
    }

    private static int[] duplicateHeavyArray(Random rnd, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++)
            a[i] = rnd.nextInt(Math.max(1, n / 20));
        return a;
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + label);
        } else {
            failed++;
            System.out.println("  [FAIL] " + label);
        }
    }
}
