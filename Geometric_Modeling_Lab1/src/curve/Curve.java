package curve;

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

    /*
     * Private members.
     */

    /**
     * Number of subdivisions given by users.
     */
    private int subdivisions;

    /**
     * Current index of point selected in insertPoints list. If the list is
     * empty, then the index is -1.
     */
    private int currentIndex;

    /**
     * Store all points inserted by sequence.
     */
    private List<Point> insertPoints;

    /**
     * Default type of curve is BEZIER.
     */
    private Operation curveType;

    /**
     * Default type of point is add after the current index.
     */
    private Operation pointType;

    /*
     * Private methods.
     */

    /**
     * Create a new representation for Curve.
     */
    private void createNewRep() {
        this.insertPoints = new LinkedList<>();
        this.subdivisions = 4; // default subdivisions is 4
        this.currentIndex = -1;
        this.curveType = CurveType.BEZIER;
        this.pointType = PointType.ADD;
    }

    /**
     * Constructor method.
     */
    public Curve() {
        this.createNewRep();
    }

    /*
     * Public methods.
     */

    /**
     * Insert points into insertPoints with the specific index.
     *
     * @param point
     */
    public void insert2InsertPoints(Point point) {
        this.insertPoints.add(this.currentIndex, point);
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
     * Update index of point selected in linked list. If the type of points is
     * ADD, then currentIndex++; else if the type of points is EDIT, then
     * currentIndex = index of {@point}.
     *
     * @param point
     *            point will be added, deleted, or changed
     */
    public void updateIndex(Point point) {

        if (this.pointType == PointType.ADD) {
            this.currentIndex++;
        } else if (this.pointType == PointType.EDIT) {
            this.currentIndex = this.insertPoints.indexOf(point);
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
     * Restore the curve.
     */
    public void clear() {
        this.createNewRep();
    }

}
