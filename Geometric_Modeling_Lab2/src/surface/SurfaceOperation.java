/**
 *
 */
package surface;

import java.awt.Graphics;

import curve.Curve;

/**
 * @author gaoxing
 *
 */
public interface SurfaceOperation {

    void updateCanvas3D(Curve curve, int numberOfSlices, Graphics g);

}
