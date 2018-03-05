package surface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.vecmath.Point3i;

import curve.Curve;

public class CoordinateSystem {

    //    double[] x = { 1, 0, 0 };
    //    double[] y = { 0, 1, 0 };
    //    double[] z = { 0, 0, 1 };
    //    double[] origin = { 0, 0, 0 };
    //
    //    double[] drawX = new double[3];
    //    double[] drawY = new double[3];
    //    double[] drawOrigin = new double[2];
    //
    //    double[] viewFrom, viewTo;
    //
    //    public CoordinateSystem(double[] viewFrom, double[] viewTo) {
    //        this.viewFrom = viewFrom;
    //        this.viewTo = viewTo;
    //    }
    //
    //    private void updateCoordinate() {
    //
    //        /*
    //         * Update x,y, and z axis
    //         */
    //        for (int i = 0; i < 3; i++) {
    //            this.drawX[i] = Calculator.CalculatePositionX(this.viewFrom,
    //                    this.viewTo, this.x[i], this.y[i], this.z[i]);
    //            this.drawY[i] = Calculator.CalculatePositionY(this.viewFrom,
    //                    this.viewTo, this.x[i], this.y[i], this.z[i]);
    //        }
    //
    //        /*
    //         * Update origin
    //         */
    //        this.drawOrigin[0] = Calculator.CalculatePositionX(this.viewFrom,
    //                this.viewTo, this.origin[0], this.origin[1], this.origin[2]);
    //        this.drawOrigin[1] = Calculator.CalculatePositionY(this.viewFrom,
    //                this.viewTo, this.origin[0], this.origin[1], this.origin[2]);
    //    }
    //
    //    /**
    //     * Draw coordinate systems on 2D canvas.
    //     *
    //     * @param g
    //     */
    //    public void drawCoordinateSystem(Graphics g) {
    //
    //        this.updateCoordinate();
    //
    //        Graphics2D g2 = (Graphics2D) g;
    //
    //        /*
    //         * Draw x-axis
    //         */
    //        g2.setColor(Color.RED);
    //        g2.drawLine(400, 175,
    //                20 * (int) (this.drawX[0] + (400 - this.drawOrigin[0])),
    //                20 * (int) (this.drawY[0] + 175 - this.drawOrigin[1]));
    //
    //        /*
    //         * Draw y-axis
    //         */
    //        g2.setColor(Color.GREEN);
    //        g2.drawLine(400, 175,
    //                20 * (int) (this.drawX[1] + (400 - this.drawOrigin[0])),
    //                20 * (int) (this.drawY[1] + 175 - this.drawOrigin[1]));
    //
    //        /*
    //         * Draw z-axis
    //         */
    //        g2.setColor(Color.BLUE);
    //        g2.drawLine(400, 175,
    //                20 * (int) (this.drawX[2] + (400 - this.drawOrigin[0])),
    //                20 * (int) (this.drawY[2] + 175 - this.drawOrigin[1]));
    //
    //        System.out.println(
    //                (int) this.drawOrigin[0] + "\t" + (int) this.drawOrigin[1]);
    //        System.out.println((int) (this.drawX[0] + (400 - this.drawOrigin[0]))
    //                + "\t" + (int) (this.drawY[0] + 175 - this.drawOrigin[1]));
    //        System.out.println((int) (this.drawX[1] + (400 - this.drawOrigin[0]))
    //                + "\t" + (int) (this.drawY[1] + 175 - this.drawOrigin[1]));
    //        System.out.println((int) (this.drawX[2] + (400 - this.drawOrigin[0]))
    //                + "\t" + (int) (this.drawY[2] + 175 - this.drawOrigin[1]));
    //    }

    /**
     * Copy curve from canvas2D to canvas3D.
     *
     * @param curve
     * @param g
     * @param originX
     * @param originY
     */
    private static void copyCurve(Curve curve, Graphics g, int originX,
            int originY) {

        Graphics2D g2 = (Graphics2D) g;

        List<Point3i> controlPoints = curve.controlPoints();

        for (int i = 0; i < controlPoints.size(); i++) {
            Point3i p = controlPoints.remove(i);
            System.out.print("(" + p.getX() + ", " + p.getY() + ")");
            int x = (int) Calculator.getPositionX(p.getX(), p.getY(), p.getZ(),
                    originX, originY);
            int y = (int) Calculator.getPositionY(p.getX(), p.getY(), p.getZ(),
                    originX, originY);
            System.out.println("\t\t(" + x + ", " + y + ")");
            Point3i p1 = new Point3i(x, y, 0);
            controlPoints.add(i, p1);
            /*
             * Draw circle on the canvas and output numbers
             */
            g2.setColor(Color.RED);
            g2.drawOval(p1.x, p1.y, 7, 7);
            g2.drawString(Integer.toString(i), x + 12, y + 7);

            /*
             * Draw lines between points
             */
            if (i != 0) {
                g2.setColor(new Color(1, 1, 1, (float) 0.2));
                g2.drawLine(controlPoints.get(i - 1).getX(),
                        controlPoints.get(i - 1).getY(), x, y);
            }
        }

        List<Point3i> modifiedPoints = curve.curveType().updateCurve(curve, g);
        //        for (int i = 0; i < modifiedPoints.size(); i++) {
        //            Point3i p = modifiedPoints.remove(i);
        //            int x = (int) Calculator.getPositionX(p.getX(), p.getY(), p.getZ(),
        //                    originX, originY);
        //            int y = (int) Calculator.getPositionX(p.getX(), p.getY(), p.getZ(),
        //                    originX, originY);
        //            Point3i p1 = new Point3i(x, y, 0);
        //
        //            modifiedPoints.add(i, p1);
        //
        //            /*
        //             * Draw lines between points
        //             */
        //            if (i != 0) {
        //                g2.setColor(Color.WHITE);
        //                g2.drawLine(modifiedPoints.get(i - 1).getX(),
        //                        modifiedPoints.get(i - 1).getY(), x, y);
        //            }

        //}

    }

    /**
     * Generate a xyz coordiante system.
     *
     * @param g
     * @param width
     * @param height
     */
    public static void buildSystem(Graphics g, int width, int height,
            Curve curve) {
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, width, height);
        /*
         * Draw x-axis
         */
        g2.setColor(Color.RED);
        g2.drawLine(width / 2, height / 2, width - 50, height / 2);
        g2.drawLine(width - 50, height / 2, width - 60, height / 2 + 10);
        g2.drawLine(width - 50, height / 2, width - 60, height / 2 - 10);
        g2.drawString("x", width - 50, height / 2 + 30);

        /*
         * Draw y-axis
         */
        g2.setColor(Color.GREEN);
        g2.drawLine(width / 2, height / 2, width / 2, 50);
        g2.drawLine(width / 2, 50, width / 2 - 10, 60);
        g2.drawLine(width / 2, 50, width / 2 + 10, 60);
        g2.drawString("y", width / 2 + 30, 50);

        /*
         * Draw z-axis
         */
        g2.setColor(Color.BLUE);
        g2.drawLine(width / 2, height / 2, 100,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3)));
        g2.drawLine(70,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3)), 110,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3)) - 15);
        g2.drawLine(70,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3)), 125,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3)) + 15);
        g2.drawString("z", 70,
                (int) (height / 2 + (width / 2 - 100) / 3 * Math.sqrt(3) - 30));

        if (!curve.controlPoints().isEmpty()) {
            copyCurve(curve, g, width / 2, height / 2);
        }

    }

}
