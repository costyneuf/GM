package curve;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
     * Private constructor avoids class being instantiated.
     */
    private PointsOperation() {

    }

    /**
     * Paint a red point.
     *
     * @param g
     * @param point
     */
    private static void paintRed(Graphics g, Point point) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.RED);
        g2.drawOval(point.x, point.y, 5, 5);
    }

    /**
     * Paint a green point.
     *
     * @param g
     * @param point
     */
    private static void paintGreen(Graphics g, Point point) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GREEN);
        g2.drawOval(point.x, point.y, 5, 5);
    }

    private static void connectedPoints(Graphics g, Point p1, Point p2) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(1, 1, 1, (float) 0.2));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Add a point into the linked list and selected that point.
     *
     * @param curve
     * @param point
     */
    public static void addPoints(Curve curve, Point point, Graphics g) {
        curve.insert2InsertPoints(point);
        selectPoints(curve, point);
        curve.updateNextIndexOfInput(-1);
        paintGreen(g, point);
        if (curve.numberOfPoints() > 1) {
            paintRed(g, curve.lastPointInserted());
            connectedPoints(g, curve.lastPointInserted(), point);
        }
    }

    public static void selectPoints(Curve curve, Point point) {

    }

    public static void editPoints() {

    }

    public static void duplicatePoints() {

    }

    public static void deletePoints() {

    }

    public static void clearAll() {

    }

}
