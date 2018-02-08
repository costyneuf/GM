/**
 *  Class for generating Bezier Curve.
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
public class BezierCurve {

    /*
     * Private members.
     */

    /**
     * Initial value of u.
     */
    private static final double INITIAL_U = 1E-2;

    /**
     * Store a copy of ctrlPoints list.
     */
    private List<Point> points = new LinkedList<>();

    /**
     * Constructor of BezierCurve.
     *
     * @param ctrlPoints
     */
    public BezierCurve(List<Point> ctrlPoints) {

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
     * Calculate the factorial of n.
     *
     * @param n
     * @return 1 * 2 * 3 * ... * n
     */
    private static long factorial(int n) {

        /*
         * if n = 0, then factorial(0) = 1
         */
        long fact = 1;

        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }

    /**
     * Calculate the ith value in the n-degree of Bernstein polynomials.
     *
     * @param n
     *            degree of Bernstein polynomial
     * @param i
     * @param u
     * @return the ith value
     */
    private static double bernstein(int n, int i, double u) {

        double result = 1.0;
        result *= (factorial(n) / factorial(n - i) / factorial(i));
        result *= Math.pow(u, i);
        result *= Math.pow(1.0 - u, n - i);
        return result;
    }

    /*
     * Public methods.
     */

    /**
     * Generate Bezier Curve.
     *
     * @param g
     */
    public void generateCurve(Graphics g) {

        /*
         * Set up color.
         */
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);

        /*
         * Initialize p1.
         */
        int p1X = this.points.get(0).x;
        int p1Y = this.points.get(0).y;
        int n = this.points.size();

        /*
         * Deal with real curves only.
         */
        if (n > 2) {

            double j = INITIAL_U;

            while (j < 1) {

                double p2X = 0, p2Y = 0;

                for (int i = 0; i < n; i++) {
                    p2X += (this.points.get(i).x * bernstein(n - 1, i, j));
                    p2Y += (this.points.get(i).y * bernstein(n - 1, i, j));
                }

                g2.drawLine(p1X, p1Y, (int) Math.round(p2X),
                        (int) Math.round(p2Y));
                p1X = (int) Math.round(p2X);
                p1Y = (int) Math.round(p2Y);

                j += INITIAL_U;
            }
        }

        /*
         * Connect the end point with the last but one point in curve.
         */
        g2.drawLine(p1X, p1Y, this.points.get(n - 1).x,
                this.points.get(n - 1).y);

    }
}
