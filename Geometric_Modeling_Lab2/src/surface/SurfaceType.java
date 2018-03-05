package surface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

import curve.Curve;

public enum SurfaceType implements SurfaceOperation {
    REVOLUTION {

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {
            // TODO Auto-generated method stub

        }

        @Override
        public List<Point3i> generateRoute(List<Point3i> controlPoints,
                int numberOfSlices) {
            // TODO Auto-generated method stub
            return null;
        }

    },
    EXTRUSION {

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {
            // TODO Auto-generated method stub
            List<Point3i> route = this.generateRoute(curve.controlPoints(),
                    numberOfSlices);
            List<Point3i> poly1 = new LinkedList<>();
            poly1.addAll(curve.curveType().updateCurve(curve, g));

            List<Point3i> ctrl1 = new LinkedList<>();
            ctrl1.addAll(curve.controlPoints());

            /*
             * Generate second linked list
             */
            for (int i = 0; i < numberOfSlices / 5; i++) {
                /*
                 * Modified points
                 */
                List<Point3i> poly2 = new LinkedList<>();
                poly2.add(route.get(i));
                int distanceX = poly2.get(0).getX() - poly1.get(0).getX();
                int distanceY = poly2.get(0).getY() - poly1.get(0).getY();
                int distanceZ = poly2.get(0).getZ() - poly1.get(0).getZ();
                for (int j = 1; j < poly1.size(); j++) {
                    poly2.add(new Point3i(poly1.get(i).getX() + distanceX,
                            poly1.get(i).getY() + distanceY,
                            poly1.get(i).getZ() + distanceZ));
                }
                drawFacet(poly1, poly2, g);

                /*
                 * Control points
                 */
                List<Point3i> ctrl2 = new LinkedList<>();
                int dX = poly2.get(0).getX() - ctrl1.get(0).getX();
                int dY = poly2.get(0).getY() - ctrl1.get(0).getY();
                int dZ = poly2.get(0).getZ() - ctrl1.get(0).getZ();
                for (int j = 0; j < ctrl1.size(); j++) {
                    ctrl2.add(new Point3i(ctrl1.get(j).getX() + dX,
                            ctrl1.get(j).getY() + dY,
                            ctrl1.get(j).getZ() + dZ));
                }
                addMesh(ctrl1, ctrl2, g);
            }

        }

        @Override
        public List<Point3i> generateRoute(List<Point3i> controlPoints,
                int numberOfSlices) {
            /*
             * Generate a line along the positive direction of z-axis.
             *
             * Initial value: 5, Increment: 5
             */
            List<Point3i> generateRoute = new LinkedList<>();
            for (int i = 0; i < numberOfSlices / 5; i++) {
                generateRoute.add(new Point3i(controlPoints.get(0).getX(),
                        controlPoints.get(0).getY(), 3 * (i + 1)));
            }

            return generateRoute;
        }

    },
    SWEEP {

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {
            // TODO Auto-generated method stub

        }

        @Override
        public List<Point3i> generateRoute(List<Point3i> controlPoints,
                int numberOfSlices) {
            // TODO Auto-generated method stub
            return null;
        }

    };

    private static void drawFacet(List<Point3i> p1, List<Point3i> p2,
            Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.pink);

        for (int i = 0; i < p1.size() - 1; i++) {
            Point3i[] vertex = new Point3i[4];
            vertex[0] = p1.get(i);
            vertex[1] = p1.get(i + 1);
            vertex[2] = p2.get(i);
            vertex[3] = p2.get(i + 1);

            for (int j = 0; j < 4; j++) {
                double x = Calculator.getPositionX(vertex[0].getX(),
                        vertex[0].getY(), vertex[0].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                double y = Calculator.getPositionY(vertex[0].getX(),
                        vertex[0].getY(), vertex[0].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                vertex[j] = new Point3i((int) x, (int) y, 0);
            }

            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[1].getX(),
                    vertex[1].getY());
            g2.drawLine(vertex[2].getX(), vertex[2].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[2].getX(),
                    vertex[2].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[3].getX(), vertex[3].getY(), vertex[1].getX(),
                    vertex[1].getY());
        }

    }

    private static void addMesh(List<Point3i> p1, List<Point3i> p2,
            Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.YELLOW);

        for (int i = 0; i < p1.size() - 1; i++) {

            Point3i[] vertex = new Point3i[4];
            vertex[0] = p1.get(i);
            vertex[1] = p1.get(i + 1);
            vertex[2] = p2.get(i);
            vertex[3] = p2.get(i + 1);

            for (int j = 0; j < 4; j++) {
                double x = Calculator.getPositionX(vertex[0].getX(),
                        vertex[0].getY(), vertex[0].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                double y = Calculator.getPositionY(vertex[0].getX(),
                        vertex[0].getY(), vertex[0].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                vertex[j] = new Point3i((int) x, (int) y, 0);
            }

            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[1].getX(),
                    vertex[1].getY());
            g2.drawLine(vertex[2].getX(), vertex[2].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[2].getX(),
                    vertex[2].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[3].getX(), vertex[3].getY(), vertex[1].getX(),
                    vertex[1].getY());

            Surface.mesh.addFacet(p1.get(i).getX(), p1.get(i).getY(),
                    p1.get(i).getZ(), p1.get(i + 1).getX(),
                    p1.get(i + 1).getY(), p1.get(i + 1).getZ(),
                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());
            Surface.mesh.addFacet(p1.get(i + 1).getX(), p1.get(i + 1).getY(),
                    p1.get(i + 1).getZ(), p2.get(i + 1).getX(),
                    p2.get(i + 1).getY(), p2.get(i + 1).getZ(),
                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());
        }
    }
}
