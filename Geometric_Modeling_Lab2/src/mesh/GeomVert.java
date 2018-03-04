/**
 * GeomVert: this class holds the geometric coordinates of a vertex.
 */

package mesh;

class GeomVert {

    /*
     * Private members
     */
    private float[] mCo = new float[3];

    /*
     * Constructor
     */
    protected GeomVert(float x, float y, float z) {
        this.mCo[0] = x;
        this.mCo[1] = y;
        this.mCo[2] = z;
    }

    /*
     * protected members
     */

    protected float getCo(int axis) {
        return this.mCo[axis];
    }

    protected boolean operator(GeomVert A) {
        return !((this.mCo[0]) != A.getCo(0)) && !((this.mCo[1]) != A.getCo(1))
                && !((this.mCo[2]) != A.getCo(2));
    }

}
