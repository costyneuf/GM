/**
 *
 */
package curve;

import java.awt.Graphics;
import java.util.List;

import javax.vecmath.Point3i;

/**
 * @author gaoxing
 *
 */
public interface Operation {

    /**
     * Update the linked list for control points. Add, change, or delete
     * {@code point} based on point status in {@code curve}.
     *
     * @param point
     *            the point will be added, changed, or deleted
     * @param curve
     *            the curve containing the linked list
     */
    void updatePointsList(Point3i point, Curve curve);

    /**
     * Update the curve based on curve status in {@code curve} and redraw it on
     * the canvas.
     *
     * @param curve
     *            the curve will be updated
     * @param g
     *            the media for updating the canvas
     */
    List<Point3i> updateCurve(Curve curve, Graphics g);

    /**
     * Update the curve based on curve status in {@code pt} and redraw it on the
     * canvas.
     *
     * @param pt
     *            the control points list
     * @param g
     *            the media for updating the canvas
     */
    void updateCurve(List<Point3i> pt, Graphics g);

}
