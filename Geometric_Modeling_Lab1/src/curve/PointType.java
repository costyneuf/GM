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
public enum PointType implements Operation {

    /**
     * Add a point after the current index.
     */
    ADD {

    },

    /**
     * Insert a point before the current index.
     */
    INSERT {

    },

    /**
     * Edit a point at the current index.
     */
    EDIT {

    };

    @Override
    public void updatePointsList(Point point, Curve curve) {
        curve.insert2InsertPoints(point);

    }

    @Override
    public void updateCurve(Curve curve, Graphics g) {
        // No code need here.

    }

    /**
     * Change the point status in {@code curve}.
     *
     * @param curve
     *            the curve needing the change of point status
     * @param type
     *            the type of points in curve
     */
    public static void setPointStatus(Curve curve, Operation type) {
        curve.changePointStatus(type);
    }

}
