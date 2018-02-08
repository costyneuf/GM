package curve;

import java.awt.Graphics;

import org.eclipse.swt.graphics.Point;

public enum CurveType implements Operation {

    BEZIER {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            // TODO Auto-generated method stub

        }

    },

    CUBICBSPLINE {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            // TODO Auto-generated method stub

        }

    },

    DECASTELJAU {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            // TODO Auto-generated method stub

        }

    },

    QUADRICBSPLINE {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            // TODO Auto-generated method stub

        }

    };

    @Override
    public void updatePointsList(Point point, Curve curve) {
        // No code here.

    }

    /**
     * Change the curve status in {@code curve}.
     *
     * @param curve
     *            the curve needing the change of curve status
     * @param type
     *            the type of the curve
     */
    public static void setCurveStatus(Curve curve, Operation type) {
        // TODO set up a curve's current status

    }

}
