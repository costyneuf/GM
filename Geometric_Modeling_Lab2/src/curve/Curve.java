package curve;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

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

    private static final int RELATIVEERROR = 100;

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
    private List<Point3i> insertPoints;

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
    public void insert2InsertPoints(Point3i point) {
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
     * @return the current index
     */
    public int updateIndexAndList(Point3i point) {

        if (this.pointType == PointType.ADD) {

            this.insertPoints.add(point);
            this.currentIndex++;

        } else if (this.pointType == PointType.EDIT) {

            for (Point3i p : this.insertPoints) {
                if ((p.x - point.x) * (p.x - point.x)
                        + (p.y - point.y) * (p.y - point.y) <= RELATIVEERROR) {
                    this.currentIndex = this.insertPoints.indexOf(p);
                }
            }

        } else if (this.pointType == PointType.INSERT) {

            /*
             * Use else-if for future updates.
             */

            this.insertPoints.add(this.currentIndex, point);
            this.currentIndex++;
        }
        return this.currentIndex;
    }

    /**
     * Return all control points in the curve.
     *
     * @return a linked list of control points
     */
    public List<Point3i> controlPoints() {
        return this.insertPoints;
    }

    /**
     * Update curve status and report the last status.
     *
     * @param curveStatus
     * @return last status
     */
    public Operation changeCurveStatus(Operation curveStatus) {
        Operation last = this.curveType;
        this.curveType = curveStatus;
        return last;
    }

    /**
     * Return the type of current curve.
     *
     * @return type of curve
     */
    public Operation curveType() {
        return this.curveType;
    }

    /**
     * Update point status.
     *
     * @param pointStatus
     */
    public void changePointStatus(Operation pointStatus) {
        this.pointType = pointStatus;
    }

    public Operation pointType() {
        return this.pointType;
    }

    /**
     * Report the current index of point selected.
     *
     * @return current index
     */
    public int currentIndex() {
        return this.currentIndex;
    }

    /**
     * Restore the curve.
     */
    public void clear() {
        this.createNewRep();
    }

    /**
     * Delete the selected point.
     */
    public void delete() {

        if (this.currentIndex >= 0
                && this.insertPoints.size() > this.currentIndex) {
            this.insertPoints.remove(this.currentIndex);
            this.currentIndex--;
        }

    }

    /**
     * Duplicate the selected point.
     */
    public void duplicate() {

        if (this.currentIndex >= 0
                && this.insertPoints.size() > this.currentIndex) {
            this.insertPoints.add(this.currentIndex + 1,
                    this.insertPoints.get(this.currentIndex));
        }

    }

    public void editUpdate(Point3i point) {
        this.insertPoints.remove(this.currentIndex);
        this.insertPoints.add(this.currentIndex, point);
    }

}
