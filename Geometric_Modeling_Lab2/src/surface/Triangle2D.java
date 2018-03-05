/**
 *
 */
package surface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.vecmath.Point3i;

import Core.MainClass;

/**
 * @author gaoxing
 *
 */
public class Triangle2D {

    Polygon p;
    Color c;

    public Triangle2D(Point3i p1, Point3i p2, Point3i p3, Color c) {

        MainClass.numberOfTriangles++;

        this.p = new Polygon();
        this.p.addPoint(p1.x, p1.y);
        this.p.addPoint(p2.x, p2.y);
        this.p.addPoint(p3.x, p3.y);

        this.c = c;
    }

    public void drawTriangle(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(this.c);
        g2.fillPolygon(this.p);
    }

    public static void paintTriangle(Graphics g) {
        for (int i = 0; i < MainClass.numberOfTriangles; i++) {
            MainClass.drawableTriangles[i].drawTriangle(g);
        }
    }
}
