/**
 *
 */
package curve;

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

    private static final double INCREMENT = 0.00001;

    /**
     * Store a copy of ctrlPoints list.
     */
    private List<Point> points = new LinkedList<>();

    /**
     * Store points for generating Bezier Curves.
     */
    private List<Point> functionPoints = new LinkedList<>();

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

        /*
         * Set up the start and end points.
         */
        this.functionPoints.add(0, ctrlPoints.get(0));
        this.functionPoints.add(1, ctrlPoints.get(ctrlPoints.size() - 1));
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
    private static int factorial(int n) {
        int fact = 1;
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
     *
     * @return the ith value
     */
    private static int bernstein(int n, int i) {
        return factorial(n) / (factorial(n - i) * factorial(i));
    }

    /*
     * Public methods.
     */
}
