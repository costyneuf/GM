package curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.vecmath.Point3i;

/**
 *
 */

/**
 * @author gaoxing
 *
 */
public final class PointsOperation {

    /**
     * The radius of each point painted on canvas.
     */
    private static final int RADIUS = 7;

    /**
     * Private constructor avoids class being instantiated.
     */
    private PointsOperation() {
        // No code here
    }

    /**
     * Paint each point in {@code ctrlPoints} after modifying points.
     *
     * @param g
     * @param ctrlPoints
     * @param currentIndex
     */
    private static void paintPoints(Graphics g, List<Point3i> ctrlPoints,
            int currentIndex) {
        assert currentIndex != -1 && ctrlPoints
                .size() > 0 : "Violation of: The control point list is empty.";

        Graphics2D g2 = (Graphics2D) g;

        for (Point3i p : ctrlPoints) {

            if (ctrlPoints.indexOf(p) != currentIndex) {
                g2.setColor(Color.RED);

            } else {
                g2.setColor(Color.GREEN);
            }

            g2.drawOval(p.x, p.y, RADIUS, RADIUS);
        }

        g2.setColor(Color.RED);
        g2.drawString("Max # of points for Bezier Curve is 21", 20, 20);
        g2.drawString("x-y plane", 300, 270);

    }

    /**
     * Draw a white line between {@code p1} and {@code p2}.
     *
     * @param g
     * @param p1
     * @param p2
     */
    private static void connectedPoints(Graphics g, Point3i p1, Point3i p2) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(1, 1, 1, (float) 0.2));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Reset canvas.
     *
     * @param g
     * @param width
     * @param height
     */
    private static void clearCanvas(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, width, height);
    }

    /**
     * Update points and lines on canvas. Draw a green oval if there is only one
     * point.
     *
     * @param g
     * @param ctrlPoints
     * @param currentIndex
     * @param width
     * @param height
     */
    public static void updatePoints(List<Point3i> ctrlPoints, int currentIndex,
            int width, int height, Graphics... g) {

        for (int i = 0; i < g.length; i++) {
            clearCanvas(g[i], width, height);
        }

        if (ctrlPoints.size() > 0) {

            /*
             * Check whether intersection
             */
            for (int i = 0; i < currentIndex - 2
                    && ctrlPoints.size() > 5; i++) {
                int ax = ctrlPoints.get(i).getX();
                int ay = ctrlPoints.get(i).getY();
                int bx = ctrlPoints.get(1 + i).getX();
                int by = ctrlPoints.get(1 + i).getY();
                int dx = ctrlPoints.get(currentIndex).getX();
                int dy = ctrlPoints.get(currentIndex).getY();
                int cx = ctrlPoints.get(currentIndex - 1).getX();
                int cy = ctrlPoints.get(currentIndex - 1).getY();

                boolean intersection = true;
                if (!(Math.min(ax, bx) <= Math.max(cx, dx)
                        && Math.min(cy, dy) <= Math.max(ay, by)
                        && Math.min(cx, dx) <= Math.max(ax, bx)
                        && Math.min(ay, by) <= Math.max(cy, dy))) {
                    intersection = false;
                }

                if (intersection) {
                    double u, v, w, z;
                    u = (cx - ax) * (by - ay) - (bx - ax) * (cy - ay);
                    v = (dx - ax) * (by - ay) - (bx - ax) * (dy - ay);
                    w = (ax - cx) * (dy - cy) - (dx - cx) * (ay - cy);
                    z = (bx - cx) * (dy - cy) - (dx - cx) * (by - cy);
                    if (u * v <= 0.00000001 && w * z <= 0.00000001) {
                        System.err.println("Warining: potential intersection!");
                    }
                }
            }

            paintPoints(g[0], ctrlPoints, currentIndex);
            if (ctrlPoints.size() > 1) {

                for (int i = 0; i < ctrlPoints.size() - 1; i++) {
                    connectedPoints(g[0], ctrlPoints.get(i),
                            ctrlPoints.get(i + 1));
                }
            }
        }

    }

}
