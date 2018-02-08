package curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import org.eclipse.swt.graphics.Point;

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
    private static final int RADIUS = 6;

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
    private static void paintPoints(Graphics g, List<Point> ctrlPoints,
            int currentIndex) {
        assert currentIndex != -1 && ctrlPoints
                .size() > 0 : "Violation of: The control point list is empty.";

        Graphics2D g2 = (Graphics2D) g;

        for (Point p : ctrlPoints) {

            if (ctrlPoints.indexOf(p) != currentIndex) {
                g2.setColor(Color.RED);

            } else {
                g2.setColor(Color.GREEN);
            }

            g2.drawOval(p.x, p.y, RADIUS, RADIUS);
        }

    }

    /**
     * Draw a white line between {@code p1} and {@code p2}.
     *
     * @param g
     * @param p1
     * @param p2
     */
    private static void connectedPoints(Graphics g, Point p1, Point p2) {

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
    public static void updatePoints(Graphics g, List<Point> ctrlPoints,
            int currentIndex, int width, int height) {

        clearCanvas(g, width, height);

        if (ctrlPoints.size() > 0) {

            paintPoints(g, ctrlPoints, currentIndex);
            if (ctrlPoints.size() > 1) {

                for (int i = 0; i < ctrlPoints.size() - 1; i++) {
                    connectedPoints(g, ctrlPoints.get(i),
                            ctrlPoints.get(i + 1));
                }
            }
        }

    }

}
