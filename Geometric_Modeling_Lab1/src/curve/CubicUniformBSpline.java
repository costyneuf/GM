package curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

public class CubicUniformBSpline {
    /*
     * Private members.
     */

    /**
     * Initial value of u.
     */
    private static final double INCREMENT = 0.01;

    /**
     * The degree of B-Spline is 3.
     */
    private static final int D = 4;

    /**
     * Store a copy of ctrlPoints list.
     */
    private List<Point> points = new LinkedList<>();

    /**
     * Constructor of CubicUniformBSpline.
     *
     * @param ctrlPoints
     */
    public CubicUniformBSpline(List<Point> ctrlPoints) {

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
     * Create an interval array.
     *
     * @param n
     * @return interval array
     */
    private static int[] interval(int n) {

        int[] t = new int[n + 6];

        for (int i = 3; i <= n + 2; i++) {
            t[i] = i - 3;
        }

        t[0] = t[3];
        t[1] = t[3];
        t[2] = t[3];

        t[n + 4] = t[n + 2];
        t[n + 5] = t[n + 2];
        t[n + 3] = t[n + 2];

        return t;
    }

    /**
     * Calculate the basis for each N(i,d)(u).
     *
     * @param i
     * @param d
     * @param u
     * @param t
     * @return N(i,d)(u)
     */
    private static double basis(int i, int d, double u, int[] t) {

        double result = 0;

        /*
         * Base cases
         */
        if (d == 1) {
            result = (u >= t[i] && u < t[i + 1]) ? 1 : 0;

        } else if (t[i + d - 1] != t[i] && t[i + d] != t[i + 1]) {
            result = (u - t[i]) * basis(i, d - 1, u, t) / (t[i + d - 1] - t[i]);
            result += (t[i + d] - u) * basis(i + 1, d - 1, u, t)
                    / (t[i + d] - t[i + 1]);

        }

        return result;
    }

    /**
     * Generate Cubic B-Spline with uniform knot vector.
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
        int p1X = this.points.get(1).x;
        int p1Y = this.points.get(1).y;
        /*
         * # ctrl points - 1
         */
        int n = this.points.size() - 1;

        if (n < 2) {

            g2.drawLine(this.points.get(0).x, this.points.get(0).y, p1X, p1Y);

        } else {

            int[] t = interval(n);

            double u = 0;
            while (u < t[n + 3]) {

                double p2X = 0, p2Y = 0;

                for (int i = 0; i <= n; i++) {
                    p2X += (this.points.get(i).x * basis(i, D, u, t));
                    p2Y += (this.points.get(i).y * basis(i, D, u, t));
                }

                if (u != 0) {
                    g2.drawLine(p1X, p1Y, (int) Math.round(p2X),
                            (int) Math.round(p2Y));
                }
                p1X = (int) Math.round(p2X);
                p1Y = (int) Math.round(p2Y);

                u += INCREMENT;
            }
        }

    }

}
