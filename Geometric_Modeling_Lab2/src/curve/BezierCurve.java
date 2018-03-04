/**
 *  Class for generating Bezier Curve.
 */
package curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Point3i;

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
    private List<Point3i> points = new LinkedList<>();

    /**
     * Store a new set of points.
     */
    private Vector<Point3i> modifiedPoints = new Vector<Point3i>();

    /**
     * Constructor of BezierCurve.
     *
     * @param ctrlPoints
     */
    public BezierCurve(List<Point3i> ctrlPoints) {

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
        if (n <= 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }

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
        int p1Z = this.points.get(0).z;
        this.modifiedPoints.add(this.points.get(0));

        /*
         * Deal with real curves only.
         */
        if (this.points.size() > 2) {

            double j = INITIAL_U;

            while (j < 1) {

                double p2X = 0, p2Y = 0, p2Z = 0;

                for (int i = 0; i < this.points.size(); i++) {
                    p2X += (this.points.get(i).x
                            * bernstein(this.points.size() - 1, i, j));
                    p2Y += (this.points.get(i).y
                            * bernstein(this.points.size() - 1, i, j));
                    p2Z += (this.points.get(i).z
                            * bernstein(this.points.size() - 1, i, j));
                }

                g2.drawLine(p1X, p1Y, (int) Math.round(p2X),
                        (int) Math.round(p2Y));
                p1X = (int) Math.round(p2X);
                p1Y = (int) Math.round(p2Y);
                p1Z = (int) Math.round(p2Z);
                this.modifiedPoints.add(new Point3i(p1X, p1Y, p1Z));

                j += INITIAL_U;
            }

        }

        /*
         * Connected points.
         */
        g2.drawLine(p1X, p1Y, this.points.get(this.points.size() - 1).x,
                this.points.get(this.points.size() - 1).y);
        this.modifiedPoints.add(this.points.get(this.points.size() - 1));

    }

    public Vector<Point3i> modifiedPoints() {
        return this.modifiedPoints;
    }
}
