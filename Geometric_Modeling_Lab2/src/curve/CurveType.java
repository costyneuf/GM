package curve;

import java.awt.Graphics;
import java.util.List;

import javax.vecmath.Point3i;

public enum CurveType implements Operation {

    /**
     * Bezier curve - one curve for all the points.
     */
    BEZIER {
        @Override
        public List<Point3i> updateCurve(Curve curve, Graphics g) {

            BezierCurve c = new BezierCurve(curve.controlPoints());
            c.generateCurve(g);
            return c.modifiedPoints();

        }

        @Override
        public void updateCurve(List<Point3i> pt, Graphics g) {

            BezierCurve c = new BezierCurve(pt);
            c.generateCurve(g);

        }

    },

    /**
     * Cubic B-spline with uniform knot vector.
     */
    CUBICBSPLINE {
        @Override
        public List<Point3i> updateCurve(Curve curve, Graphics g) {

            CubicUniformBSpline c = new CubicUniformBSpline(
                    curve.controlPoints());
            c.generateCurve(g);
            return c.modifiedPoints();
        }

        @Override
        public void updateCurve(List<Point3i> pt, Graphics g) {

            CubicUniformBSpline c = new CubicUniformBSpline(pt);
            c.generateCurve(g);

        }

    };

    @Override
    public void updatePointsList(Point3i point, Curve curve) {
        // No code here.

    }

    /**
     * Change the curve status in {@code curve}.
     *
     * the curve needing the change of curve status
     *
     * @param type
     *            the type of the curve
     */
    public static void setCurveStatus(Curve curve, Operation type) {
        curve.changeCurveStatus(type);

    }

}
