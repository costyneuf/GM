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

    ADD {
        @Override
        public void updatePointsList(Point point, Curve curve) {
            // TODO Auto-generated method stub

        }
    },

    INSERT {
        @Override
        public void updatePointsList(Point point, Curve curve) {
            // TODO Auto-generated method stub

        }
    },

    EDIT {
        @Override
        public void updatePointsList(Point point, Curve curve) {
            // TODO Auto-generated method stub

        }
    };

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
        // TODO: set up points' current status
    }

}
