/**
 *
 */
package curve;

import java.awt.Graphics;

import org.eclipse.swt.graphics.Point;

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
    void updatePointsList(Point point, Curve curve);

    /**
     * Update the curve based on curve status in {@code curve} and redraw it on
     * the canvas.
     *
     * @param curve
     *            the curve will be updated
     * @param g
     *            the media for updating the canvas
     */
    void updateCurve(Curve curve, Graphics g);

    /**
     * Change the point status in {@code curve}.
     *
     * @param curve
     *            the curve needing the change of point status
     */
    void setPointStatus(Curve curve);

    /**
     * Change the curve status in {@code curve}.
     *
     * @param curve
     *            the curve needing the change of curve status
     */
    void setCurveStatus(Curve curve);

}
