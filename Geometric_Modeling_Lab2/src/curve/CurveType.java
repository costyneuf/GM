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
        public void updateCurve(Curve curve, Graphics g) {

            BezierCurve c = new BezierCurve(curve.controlPoints());
            c.generateCurve(g);

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
        public void updateCurve(Curve curve, Graphics g) {

            CubicUniformBSpline c = new CubicUniformBSpline(
                    curve.controlPoints());
            c.generateCurve(g);

        }

        @Override
        public void updateCurve(List<Point3i> pt, Graphics g) {

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
            c.updatePoints(g);

        }

        @Override
        public void updateCurve(List<Point3i> pt, Graphics g) {
            // No code here
        }

    },

    /**
     * Subdivision Quadric B-Spline with uniform knot vector.
     */
    QUADRICBSPLINE {
        @Override
        public void updateCurve(Curve curve, Graphics g) {
            SubdivisionQuadricB c = new SubdivisionQuadricB(
                    curve.controlPoints());
            c.generateCurve(g);
        }

        @Override
        public void updateCurve(List<Point3i> pt, Graphics g) {
            // No code here

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
