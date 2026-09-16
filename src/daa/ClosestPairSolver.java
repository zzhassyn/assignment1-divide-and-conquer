package daa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClosestPairSolver {

    public static class PointPair {
        public final Point a;
        public final Point b;
        public final double distance;

        public PointPair(Point a, Point b, double distance) {
            this.a = a;
            this.b = b;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return a + " <-> " + b + " : " + String.format("%.6f", distance);
        }
    }

    private final Metrics metrics;

    public ClosestPairSolver(Metrics metrics) {
        this.metrics = metrics;
    }

    public PointPair solve(Point[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }
        Point[] byX = points.clone();
        Arrays.sort(byX);
        Point[] byY = byX.clone();
        Arrays.sort(byY, (p1, p2) -> Double.compare(p1.y, p2.y));
        return closest(byX, byY, 0, byX.length - 1);
    }

    public PointPair bruteForce(Point[] points) {
        PointPair best = null;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double d = points[i].distanceTo(points[j]);
                if (best == null || d < best.distance) {
                    best = new PointPair(points[i], points[j], d);
                }
            }
        }
        return best;
    }

    private PointPair closest(Point[] byX, Point[] byY, int lo, int hi) {
        metrics.enter();
        try {
            int n = hi - lo + 1;
            if (n <= 3) {
                return bruteForce(Arrays.copyOfRange(byX, lo, hi + 1));
            }

            int mid = lo + (hi - lo) / 2;
            Point midPoint = byX[mid];

            java.util.IdentityHashMap<Point, Boolean> inLeft = new java.util.IdentityHashMap<>();
            for (int i = lo; i <= mid; i++) {
                inLeft.put(byX[i], Boolean.TRUE);
            }
            Point[] leftY = new Point[mid - lo + 1];
            Point[] rightY = new Point[hi - mid];
            int li = 0, ri = 0;
            for (Point p : byY) {
                if (inLeft.containsKey(p)) {
                    leftY[li++] = p;
                } else {
                    rightY[ri++] = p;
                }
            }

            PointPair leftBest = closest(byX, leftY, lo, mid);
            PointPair rightBest = closest(byX, rightY, mid + 1, hi);

            PointPair best = leftBest.distance <= rightBest.distance ? leftBest : rightBest;

            List<Point> strip = new ArrayList<>();
            for (Point p : byY) {
                metrics.comparisons++;
                if (Math.abs(p.x - midPoint.x) < best.distance) {
                    strip.add(p);
                }
            }

            for (int i = 0; i < strip.size(); i++) {
                for (int j = i + 1; j < strip.size()
                        && (strip.get(j).y - strip.get(i).y) < best.distance; j++) {
                    metrics.comparisons++;
                    double d = strip.get(i).distanceTo(strip.get(j));
                    if (d < best.distance) {
                        best = new PointPair(strip.get(i), strip.get(j), d);
                    }
                }
            }

            return best;
        } finally {
            metrics.exit();
        }
    }
}
