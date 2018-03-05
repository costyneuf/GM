///**
// *
// */
//package surface;
//
//import java.awt.Color;
//
//import javax.vecmath.Point3i;
//
//import Core.MainClass;
//
///**
// * @author gaoxing
// *
// */
//public class Triangle3D {
//    Color c;
//    Point3i p1, p2, p3;
//
//    public Triangle3D(Point3i p1, Point3i p2, Point3i p3, Color c) {
//        this.p1 = p1;
//        this.p2 = p2;
//        this.p3 = p3;
//        this.c = c;
//
//        this.createTriangle();
//    }
//
//    void createTriangle() {
//
//        Point3i newP1 = new Point3i();
//        Point3i newP2 = new Point3i();
//        Point3i newP3 = new Point3i();
//
//        newP1.x = (int) (200 * Calculator.CalculatePositionX(MainClass.viewFrom,
//                MainClass.viewTo, this.p1.x, this.p1.y, this.p1.z));
//        newP1.y = (int) (200 * Calculator.CalculatePositionY(MainClass.viewFrom,
//                MainClass.viewTo, this.p1.x, this.p1.y, this.p1.z));
//        newP2.x = (int) (200 * Calculator.CalculatePositionX(MainClass.viewFrom,
//                MainClass.viewTo, this.p2.x, this.p2.y, this.p2.z));
//        newP2.y = (int) (200 * Calculator.CalculatePositionY(MainClass.viewFrom,
//                MainClass.viewTo, this.p2.x, this.p2.y, this.p2.z));
//        newP3.x = (int) (200 * Calculator.CalculatePositionX(MainClass.viewFrom,
//                MainClass.viewTo, this.p3.x, this.p3.y, this.p3.z));
//        newP3.y = (int) (200 * Calculator.CalculatePositionY(MainClass.viewFrom,
//                MainClass.viewTo, this.p3.x, this.p3.y, this.p3.z));
//
//        MainClass.drawableTriangles[MainClass.numberOfTriangles] = new Triangle2D(
//                newP1, newP2, newP3, this.c);
//    }
//
//}
