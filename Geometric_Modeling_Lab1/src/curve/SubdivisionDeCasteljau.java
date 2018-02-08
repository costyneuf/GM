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
public class SubdivisionDeCasteljau {

    /*
     * Private members.
     */

    private static final double U = 0.5;

    /**
     * Store a copy of ctrlPoints list.
     */
    private List<Point> ctrlPoints = new LinkedList<>();

    /**
     * Subdivisions of the curve.
     */
    private int subdivisions;

    /**
     * Constructor of SubdivisionDeCasteljau.
     *
     * @param ctrlPoints
     * @param subdivisions
     */
    public SubdivisionDeCasteljau(List<Point> ctrlPoints, int subdivisions) {

        /*
         * Copy all points in ctrlPoints to points.
         */
        for (int i = 0; i < ctrlPoints.size(); i++) {
            this.ctrlPoints.add(ctrlPoints.get(i));
        }

        this.subdivisions = subdivisions;
    }

    /*
     * Private methods.
     */

    /**
     * Subdivide the polygon once at the parameter value u.
     *
     * @param ctrlPoints
     *            list of control points
     * @param poly1
     * @param poly2
     * @param u
     * @update ctrlPoints
     */
    private static void oneSubdivide(List<Point> ctrlPoints, List<Point> poly1,
            List<Point> poly2, double u) {

        int n = ctrlPoints.size() - 1;

        /*
         * if n = 0 output poly1.{p0}.poly2
         */
        if (n == 0) {

            Point p0 = ctrlPoints.remove(n);
            while (!poly1.isEmpty()) {
                ctrlPoints.add(poly1.remove(0));
            }
            ctrlPoints.add(p0);
            while (!poly2.isEmpty()) {
                ctrlPoints.add(poly2.remove(0));
            }

        } else {

            /*
             * (1) poly1 := poly1.p0; poly2 := pn.poly2;
             */
            poly1.add(ctrlPoints.get(0));
            poly2.add(0, ctrlPoints.get(n));

            /*
             * (2) compute q[i] = p[i] + u * (p[i + 1] - p[i]), where i = 0, 1,
             * ..., n - 1
             */
            List<Point> temp = new LinkedList<>();
            while (!ctrlPoints.isEmpty()) {
                temp.add(ctrlPoints.remove(0));
            }

            for (int i = 0; i < n; i++) {
                int x = (int) Math.round(temp.get(i).x
                        + u * (temp.get(i + 1).x - temp.get(i).x));
                int y = (int) Math.round(temp.get(i).y
                        + u * (temp.get(i + 1).y - temp.get(i).y));
                ctrlPoints.add(i, new Point(x, y));
            }

            /*
             * (3) oneSubdivide(q, poly1, poly2, u)
             */
            oneSubdivide(ctrlPoints, poly1, poly2, u);

        }
    }

    private static void subdivide(List<Point> ctrlPoints, int m, double u) {

        /*
         * if m = 1 oneSubdivide(ctrlPoints, {}, {}, u)
         */

        oneSubdivide(ctrlPoints, new LinkedList<Point>(),
                new LinkedList<Point>(), u);
        int n = ctrlPoints.size() - 1;

        if (m != 1) {

            List<Point> temp = new LinkedList<>();

            while (ctrlPoints.size() > n + 1) {
                temp.add(ctrlPoints.remove(n + 1));
            }
            temp.add(0, ctrlPoints.get(n));

            subdivide(ctrlPoints, m - 1, u);
            subdivide(temp, m - 1, u);

            while (!temp.isEmpty()) {
                ctrlPoints.add(temp.remove(0));
            }

        }
    }

    /*
     * Public methods.
     */
    public void updatePoints(Graphics g) {

        /*
         * Set up color.
         */
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);

        subdivide(this.ctrlPoints, this.subdivisions, U);

        for (int i = 0; i < this.ctrlPoints.size() - 1; i++) {
            g2.drawLine(this.ctrlPoints.get(i).x, this.ctrlPoints.get(i).y,
                    this.ctrlPoints.get(i + 1).x, this.ctrlPoints.get(i + 1).y);
        }
    }

}
