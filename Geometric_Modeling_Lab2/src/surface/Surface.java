/**
 *
 */
package surface;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

import curve.Operation;
import mesh.Mesh;

/**
 * @author gaoxing
 *
 */
public class Surface {

    /*
     * Private members
     */

    private Operation curveType;
    private SurfaceType surfaceType;
    private boolean outputASCII;
    private Mesh mesh;
    /**
     * Store all points inserted by sequence.
     */
    private List<Point3i> insertPoints;

    /**
     * Create a new representation for Surface.
     */
    private void createNewRep(Operation curveType, SurfaceType surfaceType,
            boolean outputASCII) {
        this.insertPoints = new LinkedList<>();
        this.curveType = curveType;
        this.surfaceType = surfaceType;
        this.outputASCII = outputASCII;
        this.mesh = new Mesh();
    }

    /**
     * Constructor method.
     */
    public Surface(Operation curveType, SurfaceType surfaceType,
            boolean outputASCII) {
        this.createNewRep(curveType, surfaceType, outputASCII);
    }

    /*
     * Public members
     */

}
