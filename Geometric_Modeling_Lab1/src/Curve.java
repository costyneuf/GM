import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * Generate curves based on the points input and curve type.
 */

/**
 * @author Gao, Xing
 *
 */
public class Curve {

    /**
     * Number of subdivisions given by users.
     */
    private int subdivisions;

    /**
     * Next index of point selected in insertPoints list.
     */
    private int nextIndexOfInput;

    /**
     * Store all points inserted by sequence.
     */
    private List<Point> insertPoints;

    private void newRep() {
        this.insertPoints = new LinkedList<>();
        this.subdivisions = 4; // default subdivisions is 4
        this.nextIndexOfInput = 0;
    }

    /**
     * Constructor method.
     */
    public Curve() {
        this.newRep();
    }

    /**
     * Insert points into insertPoints.
     *
     * @param point
     */
    public void insert2InsertPoints(Point point) {
        this.insertPoints.add(point);
    }

    /**
     * Change subdivisions based on user input.
     *
     * @param input
     */
    public void changeSubdivision(int input) {
        this.subdivisions = input;
    }

    /**
     * Return subdivisions.
     *
     * @return subdivision
     */
    public int getSubdivision() {
        return this.subdivisions;
    }

    /**
     * Update index of point selected in linked list.
     *
     * @param index
     *
     */
    public void updateNextIndexOfInput(int index) {
        if (index != -1) {
            // Simply add a point
            this.nextIndexOfInput = index;
        } else {
            this.nextIndexOfInput++;
        }
    }

    /**
     * Return the number of points that have been inserted.
     *
     * @return number of points inserted
     */
    public int numberOfPoints() {
        return this.insertPoints.size();
    }

    /**
     * Report the last point inserted before inserting a new point.
     *
     * @return
     */
    public Point lastPointInserted() {
        return this.insertPoints.get(this.insertPoints.size() - 2);
    }

    public void clear() {
        this.newRep();
    }

}
