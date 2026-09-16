package daa;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "all";

        if (mode.equals("demo") || mode.equals("all")) {
            runDemo();
        }
        if (mode.equals("experiment") || mode.equals("all")) {
            System.out.println("\n=== Running full experiment suite ===");
            Experiment experiment = new Experiment();
            experiment.runAll();
            experiment.saveCsv("results/results.csv");
            System.out.println("Results saved to results/results.csv");
        }
    }

    private static void runDemo() {
        System.out.println("=== Divide-and-Conquer Algorithms: Demo ===\n");

        int[] a1 = { 9, 3, 7, 1, 8, 2, 5, 4, 6, 0 };
        System.out.println("Input:            " + Arrays.toString(a1));

        int[] mergeInput = a1.clone();
        new MergeSorter(new Metrics()).sort(mergeInput);
        System.out.println("MergeSort result: " + Arrays.toString(mergeInput));

        int[] quickInput = a1.clone();
        new QuickSorter(new Metrics(), 1).sort(quickInput);
        System.out.println("QuickSort result: " + Arrays.toString(quickInput));

        int[] selectInput = a1.clone();
        int k = 4;
        int kth = new DeterministicSelector(new Metrics()).select(selectInput, k);
        System.out.println("DeterministicSelect: element at rank " + k + " (0-indexed) = " + kth
                + " (expected " + kth + " matches sorted array value "
                + sortedCopy(a1)[k] + ")");

        Point[] pts = {
                new Point(0, 0), new Point(3, 4), new Point(1, 1),
                new Point(9, 9), new Point(1.1, 1.1), new Point(50, 50)
        };
        ClosestPairSolver.PointPair pair = new ClosestPairSolver(new Metrics()).solve(pts);
        System.out.println("ClosestPair result: " + pair);
    }

    private static int[] sortedCopy(int[] a) {
        int[] b = a.clone();
        Arrays.sort(b);
        return b;
    }
}
