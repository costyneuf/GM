//package surface;
//
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.util.LinkedList;
//import java.util.List;
//
//import javax.vecmath.Point3i;
//
//import curve.Curve;
//
//public enum SurfaceType1 implements SurfaceOperation {
//    REVOLUTION {
//
//        @Override
//        public List<Point3i> generateRoute(List<Point3i> controlPoints,
//                int numberOfSlices) {
//
//            List<Point3i> generateRoute = new LinkedList<>();
//            for (int i = 0; i <= numberOfSlices; i++) {
//                generateRoute.add(new Point3i(controlPoints.get(0).getX(),
//                        (int) (controlPoints.get(0).getY()
//                                * Math.cos(Math.PI / numberOfSlices * i)),
//                        (int) (controlPoints.get(0).getY()
//                                * Math.sin(Math.PI / numberOfSlices * i))));
//                System.out.println("(" + generateRoute.get(i).getX() + ", "
//                        + generateRoute.get(i).getY() + ", "
//                        + generateRoute.get(i).getZ() + ")");
//            }
//
//            System.out.println("____________");
//            return generateRoute;
//        }
//
//    },
//    EXTRUSION {
//
//        public List<Point3i> generateRoute(List<Point3i> controlPoints,
//                int numberOfSlices) {
//            /*
//             * Generate a line along the positive direction of z-axis.
//             *
//             * Initial value: 5, Increment: 5
//             */
//            List<Point3i> generateRoute = new LinkedList<>();
//            for (int i = 0; i < numberOfSlices; i++) {
//                generateRoute.add(new Point3i(controlPoints.get(0).getX(),
//                        controlPoints.get(0).getY(), 100 * (i + 1)));
//                System.out.println("(" + generateRoute.get(i).getX() + ", "
//                        + generateRoute.get(i).getY() + ", "
//                        + generateRoute.get(i).getZ() + ")");
//            }
//            System.out.println("____________");
//
//            return generateRoute;
//        }
//
//    },
//    SWEEP {
//
//        public List<Point3i> generateRoute(List<Point3i> controlPoints,
//                int numberOfSlices) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    };
//
//    private static void drawFacet(List<Point3i> p1, List<Point3i> p2,
//            Graphics g) {
//
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor(Color.pink);
//
//        for (int i = 0; i < p1.size() - 1; i++) {
//            Point3i[] vertex = new Point3i[4];
//            vertex[0] = p1.get(i);
//            vertex[1] = p1.get(i + 1);
//            vertex[2] = p2.get(i);
//            vertex[3] = p2.get(i + 1);
//
//            for (int j = 0; j < 4; j++) {
//                double x = Calculator.getPositionX(vertex[0].getX(),
//                        vertex[0].getY(), vertex[0].getZ(),
//                        CoordinateSystem.originX, CoordinateSystem.originY);
//                double y = Calculator.getPositionY(vertex[0].getX(),
//                        vertex[0].getY(), vertex[0].getZ(),
//                        CoordinateSystem.originX, CoordinateSystem.originY);
//                vertex[j] = new Point3i((int) x, (int) y, 0);
//            }
//
//            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[1].getX(),
//                    vertex[1].getY());
//            g2.drawLine(vertex[2].getX(), vertex[2].getY(), vertex[3].getX(),
//                    vertex[3].getY());
//            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[2].getX(),
//                    vertex[2].getY());
//            g2.drawLine(vertex[0].getX(), vertex[0].getY(), vertex[3].getX(),
//                    vertex[3].getY());
//            g2.drawLine(vertex[1].getX(), vertex[1].getY(), vertex[3].getX(),
//                    vertex[3].getY());
//        }
//
//    }
//
//    @Override
//    public void updateCanvas3D(Curve curve, int numberOfSlices, Graphics g) {
//
//        List<Point3i> route = this.generateRoute(curve.controlPoints(),
//                numberOfSlices);
//        List<Point3i> poly1 = new LinkedList<>();
//        poly1.addAll(curve.curveType().updateCurve(curve, g));
//        g.clearRect(0, 0, CoordinateSystem.originX * 2,
//                CoordinateSystem.originY * 2);
//
//        //List<Point3i> ctrl1 = new LinkedList<>();
//        List<Point3i> ctrl3 = new LinkedList<>();
//        //ctrl1.addAll(curve.controlPoints());
//        ctrl3.addAll(curve.controlPoints());
//
//        /*
//         * Generate second linked list
//         */
//        for (int i = 0; i < numberOfSlices; i++) {
//            /*
//             * Modified points
//             */
//            List<Point3i> poly2 = new LinkedList<>();
//            poly2.add(route.get(i));
//            int distanceX = poly2.get(0).getX() - poly1.get(0).getX();
//            int distanceY = poly2.get(0).getY() - poly1.get(0).getY();
//            int distanceZ = poly2.get(0).getZ() - poly1.get(0).getZ();
//            for (int j = 1; j < poly1.size(); j++) {
//                poly2.add(new Point3i(poly1.get(j).getX() + distanceX,
//                        poly1.get(j).getY() + distanceY,
//                        poly1.get(j).getZ() + distanceZ));
//            }
//            drawFacet(poly1, poly2, g);
//            poly1.clear();
//            poly1.addAll(poly2);
//
//            /*
//             * Control points
//             */
//            List<Point3i> ctrl2 = new LinkedList<>();
//            int dX = route.get(i).getX() - ctrl3.get(0).getX();
//            int dY = route.get(i).getY() - ctrl3.get(0).getY();
//            int dZ = route.get(i).getZ() - ctrl3.get(0).getZ();
//            for (int j = 0; j < ctrl3.size(); j++) {
//                ctrl2.add(new Point3i(ctrl3.get(j).getX() + dX,
//                        ctrl3.get(j).getY() + dY, ctrl3.get(j).getZ() + dZ));
//            }
//
//            addMesh(ctrl3, ctrl2, g);
//            ctrl3.clear();
//            ctrl3.addAll(ctrl2);
//
//        }
//
//    }
//
//    private static void addMesh(List<Point3i> p1, List<Point3i> p2,
//            Graphics g) {
//
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setColor(Color.YELLOW);
//
//        for (int i = 0; i < p1.size() - 1; i++) {
//
//            Point3i[] vertex = new Point3i[4];
//            vertex[0] = p1.get(i);
//            vertex[1] = p1.get(i + 1);
//            vertex[2] = p2.get(i);
//            vertex[3] = p2.get(i + 1);
//
//            for (int j = 0; j < 4; j++) {
//                double x = Calculator.getPositionX(vertex[0].getX(),
//                        vertex[0].getY(), vertex[0].getZ(),
//                        CoordinateSystem.originX, CoordinateSystem.originY);
//                double y = Calculator.getPositionY(vertex[0].getX(),
//                        vertex[0].getY(), vertex[0].getZ(),
//                        CoordinateSystem.originX, CoordinateSystem.originY);
//                vertex[j] = new Point3i((int) x, (int) y, 0);
//            }
//
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
//
//            Surface.mesh.addFacet(p1.get(i).getX(), p1.get(i).getY(),
//                    p1.get(i).getZ(), p1.get(i + 1).getX(),
//                    p1.get(i + 1).getY(), p1.get(i + 1).getZ(),
//                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());
//            Surface.mesh.addFacet(p1.get(i + 1).getX(), p1.get(i + 1).getY(),
//                    p1.get(i + 1).getZ(), p2.get(i + 1).getX(),
//                    p2.get(i + 1).getY(), p2.get(i + 1).getZ(),
//                    p2.get(i).getX(), p2.get(i).getY(), p2.get(i).getZ());
//        }
//    }
//}
