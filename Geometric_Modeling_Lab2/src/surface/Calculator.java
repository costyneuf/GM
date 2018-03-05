/**
 *
 */
package surface;

/**
 * @author gaoxing
 *
 */
public class Calculator {
    //    static double DrawX = 0, DrawY = 0;

    /**
     * Calculate the x coordinate in 3D canvas.
     *
     * @param x
     * @param y
     * @param z
     * @param originX
     * @param originY
     * @return
     */
    static double getPositionX(double x, double y, double z, double originX,
            double originY) {

        return (x + originX - z / 2 * Math.sqrt(3));

    }

    /**
     * Calculate the y coordinate in 3D canvas.
     *
     * @param x
     * @param y
     * @param z
     * @param originX
     * @param originY
     * @return
     */
    static double getPositionY(double x, double y, double z, double originX,
            double originY) {

        return (y - originY + z / 2);

    }

    //    static double CalculatePositionX(double[] ViewFrom, double[] ViewTo,
    //            double x, double y, double z) {
    //        setStuff(ViewFrom, ViewTo, x, y, z);
    //        return DrawX;
    //    }
    //
    //    static double CalculatePositionY(double[] ViewFrom, double[] ViewTo,
    //            double x, double y, double z) {
    //        setStuff(ViewFrom, ViewTo, x, y, z);
    //        return DrawY;
    //    }
    //
    //    static void setStuff(double[] ViewFrom, double[] ViewTo, double x, double y,
    //            double z) {
    //        Vector1L ViewVector = new Vector1L(ViewTo[0] - ViewFrom[0],
    //                ViewTo[1] - ViewFrom[1], ViewTo[2] - ViewFrom[2]);
    //        Vector1L DirectionVector = new Vector1L(1, 1, 1);
    //        Vector1L PlaneVector1 = ViewVector.CrossProduct(DirectionVector);
    //        Vector1L PlaneVector2 = ViewVector.CrossProduct(PlaneVector1);
    //
    //        Vector1L RotationVector = GetRotationVector(ViewFrom, ViewTo);
    //        Vector1L WeirdVector1 = ViewVector.CrossProduct(RotationVector);
    //        Vector1L WeirdVector2 = ViewVector.CrossProduct(WeirdVector1);
    //
    //        Vector1L ViewToPoint = new Vector1L(x - ViewFrom[0], y - ViewFrom[1],
    //                z - ViewFrom[2]);
    //
    //        double t = (ViewVector.x * ViewTo[0] + ViewVector.y * ViewTo[1]
    //                + ViewVector.z * ViewTo[2]
    //                - (ViewVector.x * ViewFrom[0] + ViewVector.y * ViewFrom[1]
    //                        + ViewVector.z * ViewFrom[2]))
    //                / (ViewVector.x * ViewToPoint.x + ViewVector.y * ViewToPoint.y
    //                        + ViewVector.z * ViewToPoint.z);
    //
    //        x = ViewFrom[0] + ViewToPoint.x * t;
    //        y = ViewFrom[1] + ViewToPoint.y * t;
    //        z = ViewFrom[2] + ViewToPoint.z * t;
    //
    //        if (t > 0) {
    //            DrawX = WeirdVector2.x * x + WeirdVector2.y * y
    //                    + WeirdVector2.z * z;
    //            DrawY = WeirdVector1.x * x + WeirdVector1.y * y
    //                    + WeirdVector1.z * z;
    //        }
    //    }
    //
    //    static Vector1L GetRotationVector(double[] ViewFrom, double[] ViewTo) {
    //        double dx = Math.abs(ViewFrom[0] - ViewTo[0]);
    //        double dy = Math.abs(ViewFrom[1] - ViewTo[1]);
    //        double xRot, yRot;
    //
    //        xRot = dy / (dx + dy);
    //        yRot = dx / (dx + dy);
    //
    //        if (ViewFrom[1] > ViewTo[1]) {
    //            xRot = -xRot;
    //        }
    //        if (ViewFrom[0] < ViewTo[0]) {
    //            yRot = -yRot;
    //        }
    //
    //        Vector1L V = new Vector1L(xRot, yRot, 0);
    //        return V;
    //    }
}
