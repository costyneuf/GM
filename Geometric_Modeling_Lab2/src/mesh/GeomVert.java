/**
 * GeomVert: this class holds the geometric coordinates of a vertex.
 */

package mesh;

public class GeomVert {

    /*
     * Private members
     */
    private float[] mCo = new float[3];

    /*
     * Constructor
     */
    public GeomVert(float x, float y, float z) {
        this.mCo[0] = x;
        this.mCo[1] = y;
        this.mCo[2] = z;
    }

    /*
     * protected members
     */

    public float getCo(int axis) {
        return this.mCo[axis];
    }

    public boolean operator(GeomVert A) {
        return !((this.mCo[0]) != A.getCo(0)) && !((this.mCo[1]) != A.getCo(1))
                && !((this.mCo[2]) != A.getCo(2));
    }

}
