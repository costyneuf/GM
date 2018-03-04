/**
 *
 */
package curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * @author gaoxing
 *
 */
public class SubdivisionQuadricB {
    /*
     * Private members.
     */

    private static final double INCREMENT = 1E-2;

    /**
     * Store a copy of ctrlPoints list.
     */
    private List<Point> points = new LinkedList<>();

    /**
     * Constructor of SubdivisionQuadricB.
     *
     * @param ctrlPoints
     * @param subdivisions
     */
    public SubdivisionQuadricB(List<Point> ctrlPoints) {

        /*
         * Copy all points in ctrlPoints to points.
         */
        for (int i = 0; i < ctrlPoints.size(); i++) {
            this.points.add(ctrlPoints.get(i));
        }

    }

    /*
     * Private methods.
     */

    /**
     * Calculate the basis for each N(i,3)(u).
     *
     * @param i
     * @param u
     * @return
     */
    private static double basis(int i, double u) {

        double result = 0;

        switch (i) {
            case 1:
                result = 1 / 2.0 * (u * u - 2 * u + 1);
                break;
            case 2:
                result = 1 / 2.0 * (-2 * u * u + 2 * u + 1);
                break;
            case 3:
                result = 1 / 2.0 * u * u;
                break;

            default:
                break;
        }

        return result;
    }

    private static void updatePoints(List<Point> points) {

        for (int i = 1; i <= points.size() - 2; i += 5) {

            Point[] pt = new Point[3];
            pt[0] = points.get(i - 1);
            pt[1] = points.get(i);
            pt[2] = points.get(i + 1);

            Point q1 = new Point(3 * pt[0].x / 4 + pt[1].x / 4,
                    3 * pt[0].y / 4 + pt[1].y / 4);
            Point q2 = new Point(3 * pt[1].x / 4 + pt[0].x / 4,
                    3 * pt[1].y / 4 + pt[0].y / 4);
            Point r1 = new Point(3 * pt[1].x / 4 + pt[2].x / 4,
                    3 * pt[1].y / 4 + pt[2].y / 4);
            Point r2 = new Point(3 * pt[2].x / 4 + pt[1].x / 4,
                    3 * pt[2].y / 4 + pt[1].y / 4);

            points.add(i, q1);
            points.add(i + 1, q2);
            points.add(i + 3, r1);
            points.add(i + 4, r2);
        }

    }

    /**
     * Generate Subdivision Quadratic B-Spline curve with uniform knot vector.
     *
     * @param g
     */
    public void generateCurve(Graphics g) {

        /*
         * Set up color.
         */
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);

        updatePoints(this.points);

        /*
         * # ctrl points - 1
         */
        int n = this.points.size() - 1;

        if (n > 1) {

            for (int i = 1; i <= n - 1; i++) {

                Point[] pt = new Point[3];
                pt[0] = this.points.get(i - 1);
                pt[1] = this.points.get(i);
                pt[2] = this.points.get(i + 1);

                double u = INCREMENT;
                Point p = null;
                while (u < 1) {
                    double pX = 0, pY = 0;

                    for (int j = 1; j <= 3; j++) {
                        pX += basis(j, u) * pt[j - 1].x;
                        pY += basis(j, u) * pt[j - 1].y;
                    }

                    if (p != null) {
                        g2.drawLine(p.x, p.y, (int) Math.round(pX),
                                (int) Math.round(pY));
                    }
                    p = new Point((int) Math.round(pX), (int) Math.round(pY));
                    u += INCREMENT;
                }
            }

        } else if (n == 1) {
            g2.drawLine(this.points.get(0).x, this.points.get(0).y,
                    this.points.get(1).x, this.points.get(1).y);
        }

    }

}
