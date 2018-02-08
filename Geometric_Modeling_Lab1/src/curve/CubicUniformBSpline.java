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
    private static final double INCREMENT = 1E-3;

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
     * Calculate the basis for each N(i,4)(u).
     *
     * @param i
     * @param u
     * @return
     */
    private static double basis(int i, double u) {

        double result = 0;

        switch (i) {
            case 1:
                result = 1 / 6.0 * (-u * u * u + 3 * u * u - 3 * u + 1);
                break;
            case 2:
                result = 1 / 6.0 * (3 * u * u * u - 6 * u * u + 4);
                break;
            case 3:
                result = 1 / 6.0 * (-3 * u * u * u + 3 * u * u + 3 * u + 1);
                break;
            case 4:
                result = 1 / 6.0 * u * u * u;
                break;
            default:
                break;
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
         * # ctrl points - 1
         */
        int n = this.points.size() - 1;

        if (n > 2) {

            for (int i = 1; i <= n - 2; i++) {

                Point[] pt = new Point[4];
                pt[0] = this.points.get(i - 1);
                pt[1] = this.points.get(i);
                pt[2] = this.points.get(i + 1);
                pt[3] = this.points.get(i + 2);

                double u = INCREMENT;
                Point p = null;
                while (u < 1) {
                    double pX = 0, pY = 0;

                    for (int j = 1; j <= 4; j++) {
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

        } else if (n == 2) {
            BezierCurve c = new BezierCurve(this.points);
            c.generateCurve(g);
        } else if (n == 1) {
            g2.drawLine(this.points.get(0).x, this.points.get(0).y,
                    this.points.get(1).x, this.points.get(1).y);
        }

    }

}
