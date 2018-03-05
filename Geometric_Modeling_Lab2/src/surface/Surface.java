/**
 *
 */
package surface;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

import curve.Curve;
import mesh.Mesh;

/**
 * @author gaoxing
 *
 */
public class Surface {

    /*
     * Private members
     */

    private Curve curve;
    private SurfaceOperation surfaceType;
    private boolean outputASCII;
    private Mesh mesh;
    /**
     * Store all points inserted by sequence.
     */
    private List<Point3i> insertPoints;

    /**
     * Create a new representation for Surface.
     */
    private void createNewRep(Curve curve, SurfaceOperation surfaceType,
            boolean outputASCII) {
        this.insertPoints = new LinkedList<>();
        this.curve = curve;
        this.surfaceType = surfaceType;
        this.outputASCII = outputASCII;
        this.mesh = new Mesh();
    }

    /**
     * Constructor method.
     */
    public Surface(Curve curve, SurfaceOperation surfaceType,
            boolean outputASCII) {
        this.createNewRep(curve, surfaceType, outputASCII);
    }

    /*
     * Public members
     */

    public SurfaceOperation surfaceType() {
        return this.surfaceType;
    }
}
