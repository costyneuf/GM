package surface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

import Core.MainClass;
import curve.Curve;

public enum SurfaceType implements SurfaceOperation {
    REVOLUTION {

        /**
         * Constant for the number of revolutions.
         */
        private static final int NUMBER_OF_REVOLUTION = 50;

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {

            List<Point3i> slicedPoints = this
                    .generateRoute(curve.controlPoints(), 50 * numberOfSlices);
            double angle = (2 * Math.PI / NUMBER_OF_REVOLUTION);

            List<Point3i> slicedPoints1 = new LinkedList<>();
            slicedPoints1.addAll(slicedPoints);

            /*
             * Calculate circles
             */
            for (int i = 1; i < NUMBER_OF_REVOLUTION; i++) {
                List<Point3i> slicedPoints2 = new LinkedList<>();

                System.out.println("Update");
                for (int j = 0; j < slicedPoints.size(); j++) {

                    System.out.println("(" + slicedPoints.get(j).getX() + ", "
                            + slicedPoints.get(j).getY() + ", "
                            + slicedPoints.get(j).getZ() + ")");
                    slicedPoints2.add(new Point3i(slicedPoints.get(j).getX(),
                            (int) (slicedPoints.get(j).getY()
                                    * Math.cos(angle * i)),
                            (int) (slicedPoints.get(j).getY()
                                    * Math.sin(angle * i))));
                    System.out.println(
                            slicedPoints.get(j).getY() * Math.sin(angle * i));
                    System.out.println("(" + slicedPoints2.get(j).getX() + ", "
                            + slicedPoints2.get(j).getY() + ", "
                            + slicedPoints2.get(j).getZ() + ")");

                }

                drawFacet(slicedPoints1, slicedPoints2, g);
                addMesh(slicedPoints1, slicedPoints2, g);

                slicedPoints1.clear();
                slicedPoints1.addAll(slicedPoints2);
            }

            drawFacet(slicedPoints1, slicedPoints, g);
            addMesh(slicedPoints1, slicedPoints, g);

        }

        private List<Point3i> generateRoute(List<Point3i> controlPoints,
                int numberOfSlices) {

            List<Point3i> slicedPoints = new LinkedList<>();

            int increment = controlPoints.size() / numberOfSlices;
            for (int i = 0; i < numberOfSlices; i++) {
                slicedPoints.add(controlPoints.get(i * increment));
            }
            slicedPoints.add(controlPoints.get(controlPoints.size() - 1));

            return slicedPoints;
        }

    },
    EXTRUSION {

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {
            List<Point3i> route = this.generateRoute(curve.controlPoints(),
                    numberOfSlices);
            List<Point3i> poly1 = new LinkedList<>();
            poly1.addAll(curve.curveType().updateCurve(curve, g));
            g.clearRect(0, 0, CoordinateSystem.originX * 2,
                    CoordinateSystem.originY * 2);

            //List<Point3i> ctrl1 = new LinkedList<>();
            List<Point3i> ctrl3 = new LinkedList<>();
            //ctrl1.addAll(curve.controlPoints());
            ctrl3.addAll(curve.controlPoints());

            /*
             * Generate second linked list
             */
            for (int i = 0; i < numberOfSlices; i++) {
                /*
                 * Modified points
                 */
                List<Point3i> poly2 = new LinkedList<>();
                poly2.add(route.get(i));
                int distanceX = poly2.get(0).getX() - poly1.get(0).getX();
                int distanceY = poly2.get(0).getY() - poly1.get(0).getY();
                int distanceZ = poly2.get(0).getZ() - poly1.get(0).getZ();
                for (int j = 1; j < poly1.size(); j++) {
                    poly2.add(new Point3i(poly1.get(j).getX() + distanceX,
                            poly1.get(j).getY() + distanceY,
                            poly1.get(j).getZ() + distanceZ));
                }
                drawFacet(poly1, poly2, g);
                poly1.clear();
                poly1.addAll(poly2);

                /*
                 * Control points
                 */
                List<Point3i> ctrl2 = new LinkedList<>();
                int dX = route.get(i).getX() - ctrl3.get(0).getX();
                int dY = route.get(i).getY() - ctrl3.get(0).getY();
                int dZ = route.get(i).getZ() - ctrl3.get(0).getZ();
                for (int j = 0; j < ctrl3.size(); j++) {
                    ctrl2.add(new Point3i(ctrl3.get(j).getX() + dX,
                            ctrl3.get(j).getY() + dY,
                            ctrl3.get(j).getZ() + dZ));
                }

                addMesh(ctrl3, ctrl2, g);
                ctrl3.clear();
                ctrl3.addAll(ctrl2);

            }

        }

        private List<Point3i> generateRoute(List<Point3i> controlPoints,
                int numberOfSlices) {
            /*
             * Generate a line along the positive direction of z-axis.
             *
             * Initial value: 5, Increment: 5
             */
            List<Point3i> generateRoute = new LinkedList<>();
            for (int i = 0; i < numberOfSlices; i++) {
                generateRoute.add(new Point3i(controlPoints.get(0).getX(),
                        controlPoints.get(0).getY(), 100 * (i + 1)));
                System.out.println("(" + generateRoute.get(i).getX() + ", "
                        + generateRoute.get(i).getY() + ", "
                        + generateRoute.get(i).getZ() + ")");
            }
            System.out.println("____________");

            return generateRoute;
        }

    },
    SWEEP {

        @Override
        public void updateCanvas3D(Curve curve, int numberOfSlices,
                Graphics g) {
            List<Point3i> route = this.generateRoute(curve.controlPoints());
            List<Point3i> poly1 = new LinkedList<>();
            poly1.addAll(curve.curveType().updateCurve(curve, g));
            g.clearRect(0, 0, CoordinateSystem.originX * 2,
                    CoordinateSystem.originY * 2);

            //List<Point3i> ctrl1 = new LinkedList<>();
            List<Point3i> ctrl3 = new LinkedList<>();
            //ctrl1.addAll(curve.controlPoints());
            ctrl3.addAll(curve.controlPoints());

            /*
             * Generate second linked list
             */
            for (int i = 0; i < numberOfSlices; i++) {
                /*
                 * Modified points
                 */
                List<Point3i> poly2 = new LinkedList<>();
                poly2.add(route.get(i));
                int distanceX = poly2.get(0).getX() - poly1.get(0).getX();
                int distanceY = poly2.get(0).getY() - poly1.get(0).getY();
                int distanceZ = poly2.get(0).getZ() - poly1.get(0).getZ();
                for (int j = 1; j < poly1.size(); j++) {
                    poly2.add(new Point3i(poly1.get(j).getX() + distanceX,
                            poly1.get(j).getY() + distanceY,
                            poly1.get(j).getZ() + distanceZ));
                }
                drawFacet(poly1, poly2, g);
                poly1.clear();
                poly1.addAll(poly2);

                /*
                 * Control points
                 */
                List<Point3i> ctrl2 = new LinkedList<>();
                int dX = route.get(i).getX() - ctrl3.get(0).getX();
                int dY = route.get(i).getY() - ctrl3.get(0).getY();
                int dZ = route.get(i).getZ() - ctrl3.get(0).getZ();
                for (int j = 0; j < ctrl3.size(); j++) {
                    ctrl2.add(new Point3i(ctrl3.get(j).getX() + dX,
                            ctrl3.get(j).getY() + dY,
                            ctrl3.get(j).getZ() + dZ));
                }

                addMesh(ctrl3, ctrl2, g);
                ctrl3.clear();
                ctrl3.addAll(ctrl2);

            }

        }

        private List<Point3i> generateRoute(List<Point3i> controlPoints) {
            List<Point3i> route = this.convertTrajectory();
            int distanceX = controlPoints.get(0).getX() - route.get(0).getX();
            int distanceY = controlPoints.get(0).getY() - route.get(0).getY();
            int distanceZ = controlPoints.get(0).getZ() - route.get(0).getZ();

            for (int i = 1; i < route.size(); i++) {
                Point3i point = route.remove(i);
                route.add(i, new Point3i(point.getX() + distanceX,
                        point.getY() + distanceY, point.getZ() + distanceZ));
            }

            return route;
        }

        private List<Point3i> convertTrajectory() {
            List<Point3i> convertTrajectory = new LinkedList<>();
            for (int i = 0; i < MainClass.trajectoryPoints.size(); i++) {
                convertTrajectory.add(
                        new Point3i(0, MainClass.trajectoryPoints.get(i).getY(),
                                MainClass.trajectoryPoints.get(i).getX()));
            }
            return convertTrajectory;
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

            System.out.println(
                    "----------------------------------------------------");
            System.out.println("(" + vertex[0].getX() + ", " + vertex[0].getY()
                    + ", " + vertex[0].getZ() + ")");
            System.out.println("(" + vertex[1].getX() + ", " + vertex[1].getY()
                    + ", " + vertex[1].getZ() + ")");
            System.out.println("(" + vertex[2].getX() + ", " + vertex[2].getY()
                    + ", " + vertex[2].getZ() + ")");
            System.out.println("(" + vertex[3].getX() + ", " + vertex[3].getY()
                    + ", " + vertex[3].getZ() + ")");
            System.out.println("---");
            for (int j = 0; j < 4; j++) {
                double x = Calculator.getPositionX(vertex[j].getX(),
                        vertex[j].getY(), vertex[j].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                double y = Calculator.getPositionY(vertex[j].getX(),
                        vertex[j].getY(), vertex[j].getZ(),
                        CoordinateSystem.originX, CoordinateSystem.originY);
                vertex[j] = new Point3i((int) x, (int) y, 0);
                System.out.println(
                        "(" + vertex[j].getX() + ", " + vertex[j].getY() + ")");
            }

            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[1].getX(),
                    vertex[1].getY());
            g2.drawLine(vertex[2].getX(), vertex[2].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[2].getX(),
                    vertex[2].getY());
            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[3].getX(),
                    vertex[3].getY());
            g2.drawLine(vertex[1].getX(), vertex[1].getY(), vertex[3].getX(),
                    vertex[3].getY());
        }
    }

    private static void addMesh(List<Point3i> p1, List<Point3i> p2,
            Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.YELLOW);

        for (int i = 0; i < p1.size() - 1; i++) {

            Surface.mesh.addFacet(p1.get(i).getX(), p1.get(i).getY(),
                    p1.get(i).getZ(), p1.get(i + 1).getX(),
                    p1.get(i + 1).getY(), p1.get(i + 1).getZ(),
                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());
            Surface.mesh.addFacet(p1.get(i + 1).getX(), p1.get(i + 1).getY(),
                    p1.get(i + 1).getZ(), p2.get(i + 1).getX(),
                    p2.get(i + 1).getY(), p2.get(i + 1).getZ(),
                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());

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

            //            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[1].getX(),
            //                    vertex[1].getY());
            //            g2.drawLine(vertex[2].getX(), vertex[2].getY(), vertex[3].getX(),
            //                    vertex[3].getY());
            //            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[2].getX(),
            //                    vertex[2].getY());
            //            g2.drawLine(vertex[1].getX(), vertex[0].getY(), vertex[2].getX(),
            //                    vertex[3].getY());
            //            g2.drawLine(vertex[3].getX(), vertex[3].getY(), vertex[1].getX(),
            //                    vertex[1].getY());

        }
    }

}
