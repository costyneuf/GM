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

    private int subdivisions = 0;

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

    public BezierCurve(List<Point> ctrlPoints, int subdivisions) {

        /*
         * Copy all points in ctrlPoints to points.
         */
        for (int i = 0; i < ctrlPoints.size(); i++) {
            this.points.add(ctrlPoints.get(i));
        }
        this.subdivisions = subdivisions;

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
        try {
            result *= (factorial(n) / factorial(n - i) / factorial(i));
        } catch (ArithmeticException e) {
            System.out.println(n + "\t" + (n - i) + "\t" + i);
        }
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

        /*
         * Deal with real curves only.
         */
        if (this.points.size() > 2) {

            if (this.subdivisions > 0) {

                SubdivisionDeCasteljau c = new SubdivisionDeCasteljau(
                        this.points, this.subdivisions);
                List<Point> temp = c.updatePoints(0.5);
                this.points.clear();
                this.points.addAll(temp);

                for (int i = 1; i < this.points.size() - 1; i++) {

                    double j = INITIAL_U;

                    Point p0 = this.points.get(i - 1);
                    Point p1 = this.points.get(i);
                    Point p2 = this.points.get(i + 1);

                    while (j < 1) {

                        double pX = (1 - j) * (1 - j) * p0.x
                                + 2 * j * (1 - j) * p1.x + j * j * p2.x;
                        double pY = (1 - j) * (1 - j) * p0.y
                                + 2 * j * (1 - j) * p1.y + j * j * p2.y;

                        g2.drawLine(p1X, p1Y, (int) Math.round(pX),
                                (int) Math.round(pY));

                        p1X = (int) Math.round(pX);
                        p1Y = (int) Math.round(pY);

                        j += INITIAL_U;

                    }
                }

            } else {
                double j = INITIAL_U;

                while (j < 1) {

                    double p2X = 0, p2Y = 0;

                    for (int i = 0; i < this.points.size(); i++) {
                        p2X += (this.points.get(i).x
                                * bernstein(this.points.size() - 1, i, j));
                        p2Y += (this.points.get(i).y
                                * bernstein(this.points.size() - 1, i, j));
                    }

                    g2.drawLine(p1X, p1Y, (int) Math.round(p2X),
                            (int) Math.round(p2Y));
                    p1X = (int) Math.round(p2X);
                    p1Y = (int) Math.round(p2Y);

                    j += INITIAL_U;
                }
            }
        }

        /*
         * Connected points.
         */
        g2.drawLine(p1X, p1Y, this.points.get(this.points.size() - 1).x,
                this.points.get(this.points.size() - 1).y);

    }
}
