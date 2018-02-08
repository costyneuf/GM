package curve;

import java.awt.Graphics;
import java.util.List;

import org.eclipse.swt.graphics.Point;

public enum CurveType implements Operation {

    /**
     * Bezier curve - one curve for all the points.
     */
    BEZIER {
        @Override
        public void updateCurve(Curve curve, Graphics g) {

            BezierCurve c = new BezierCurve(curve.controlPoints());
            c.generateCurve(g);

        }

        @Override
        public void updateCurve(List<Point> pt, Graphics g) {

            BezierCurve c = new BezierCurve(pt);
            c.generateCurve(g);

        }

    },

    /**
     * Cubic B-spline with uniform knot vector.
     */
    CUBICBSPLINE {
        @Override
        public void updateCurve(Curve curve, Graphics g) {

            CubicUniformBSpline c = new CubicUniformBSpline(
                    curve.controlPoints());
            c.generateCurve(g);

        }

        @Override
        public void updateCurve(List<Point> pt, Graphics g) {

            CubicUniformBSpline c = new CubicUniformBSpline(pt);
            c.generateCurve(g);

        }

    },

    /**
     * Subdivision curves using repeated de Casteljau method.
     */
    DECASTELJAU {
        @Override
        public void updateCurve(Curve curve, Graphics g) {

            SubdivisionDeCasteljau c = new SubdivisionDeCasteljau(
                    curve.controlPoints(), curve.getSubdivision());

            c.generateCurve(g);

        }

        @Override
        public void updateCurve(List<Point> pt, Graphics g) {
            // TODO Auto-generated method stub

        }

    },

    /**
     * Subdivision Quadric B-Spline with uniform knot vector.
     */
    QUADRICBSPLINE {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateCurve(List<Point> pt, Graphics g) {
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
        curve.changeCurveStatus(type);

    }

}
