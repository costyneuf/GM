/**
 * TopoEdge.
 */

package mesh;

import java.util.Vector;

class TopoEdge {

    /*
     * Private members
     */

    private int v1, v2;
    private Vector<Integer> mIncFacets;

    /*
     * Constructor
     */
    public TopoEdge() {
        this.v1 = -1;
        this.v2 = -1;
        this.mIncFacets = new Vector<Integer>();
    };

    /*
     * Public members
     */
    public int getVertex(int ind) {
        if (ind == 0) {
            return this.v1;
        }
        return this.v2;
    }

    public void setVertex(int ind, int v) {
        if (ind == 0) {
            this.v1 = v;
        } else {
            this.v2 = v;
        }
    }

    public void addIncFacet(int facet_ind) {
        this.mIncFacets.add(facet_ind);
    }

    public int getNumberIncFacets() {
        return this.mIncFacets.size();
    }

    public int getIncFacet(int facet_ind) {
        return this.mIncFacets.elementAt(facet_ind);
    }

    public boolean operator(TopoEdge A) {
        return ((this.v1) == A.getVertex(0) && (this.v2) == A.getVertex(1))
                || ((this.v1) == A.getVertex(1) && (this.v2) == A.getVertex(0));
    }
}
