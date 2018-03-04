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
        this.ctrlPoints.addAll(ctrlPoints);
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

        if (n == 0) {

            /*
             * base case: output poly1.{p0}.poly2
             */

            ctrlPoints.addAll(0, poly1);
            ctrlPoints.addAll(poly2);

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

            for (int i = 0; i < n; i++) {
                int x = (int) Math.round(ctrlPoints.get(i).x
                        + u * (ctrlPoints.get(i + 1).x - ctrlPoints.get(i).x));
                int y = (int) Math.round(ctrlPoints.get(i).y
                        + u * (ctrlPoints.get(i + 1).y - ctrlPoints.get(i).y));
                temp.add(new Point(x, y));
            }
            ctrlPoints.clear();
            ctrlPoints.addAll(temp);

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

        int n = ctrlPoints.size() - 1;
        oneSubdivide(ctrlPoints, new LinkedList<Point>(),
                new LinkedList<Point>(), u);

        if (m != 1) {

            List<Point> temp = new LinkedList<>();
            while (ctrlPoints.size() > n + 1) {
                temp.add(ctrlPoints.remove(n + 1));
            }
            temp.add(0, ctrlPoints.get(n));

            subdivide(ctrlPoints, m - 1, u);
            subdivide(temp, m - 1, u);
            ctrlPoints.addAll(temp);

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

        for (double u = U; u <= 1; u += U) {
            subdivide(this.ctrlPoints, this.subdivisions, u);
        }

        for (int i = 0; i < this.ctrlPoints.size() - 1; i++) {
            g2.drawLine(this.ctrlPoints.get(i).x, this.ctrlPoints.get(i).y,
                    this.ctrlPoints.get(i + 1).x, this.ctrlPoints.get(i + 1).y);
        }

    }

}
