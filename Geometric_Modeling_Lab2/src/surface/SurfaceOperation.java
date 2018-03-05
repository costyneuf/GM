/**
 *
 */
package surface;

import java.awt.Graphics;
import java.util.List;

import javax.vecmath.Point3i;

import curve.Curve;

/**
 * @author gaoxing
 *
 */
public interface SurfaceOperation {

    void updateCanvas3D(Curve curve, int numberOfSlices, Graphics g);

    List<Point3i> generateRoute(List<Point3i> controlPoints,
            int numberOfSlices);

}
