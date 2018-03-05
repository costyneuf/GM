/**
 *
 */
package surface;

import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point3i;

import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
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
    static Mesh mesh;
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
        mesh = new Mesh();
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

    public void outputASCII() {
        if (this.outputASCII) {
            SimpleWriter out = new SimpleWriter1L("ASCII.txt");
            out.println(
                    mesh.getNumberVertices() + " " + mesh.getNumberFacets());

            for (int i = 0; i < mesh.getNumberVertices(); i++) {
                out.println(mesh.getGeomVertex(i).getCo(0) + " "
                        + mesh.getGeomVertex(i).getCo(1) + " "
                        + mesh.getGeomVertex(i).getCo(2));
            }

            for (int i = 0; i < mesh.getNumberFacets(); i++) {
                for (int j = 0; j < mesh.getFacet(i).getNumberVertices(); j++) {
                    out.print(mesh.getFacet(i).getNumberVertices() + " ");
                    out.print(mesh.getFacet(i).getVertexInd(j) + " ");
                }
                out.println(" ");

            }
            out.close();
        }
    }
}
