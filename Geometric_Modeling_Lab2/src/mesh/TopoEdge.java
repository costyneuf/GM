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
    protected TopoEdge() {
        this.v1 = -1;
        this.v2 = -1;
        this.mIncFacets = new Vector<Integer>();
    };

    /*
     * protected members
     */
    protected int getVertex(int ind) {
        if (ind == 0) {
            return this.v1;
        }
        return this.v2;
    }

    protected void setVertex(int ind, int v) {
        if (ind == 0) {
            this.v1 = v;
        } else {
            this.v2 = v;
        }
    }

    protected void addIncFacet(int facet_ind) {
        this.mIncFacets.add(facet_ind);
    }

    protected int getNumberIncFacets() {
        return this.mIncFacets.size();
    }

    protected int getIncFacet(int facet_ind) {
        return this.mIncFacets.elementAt(facet_ind);
    }

    protected boolean operator(TopoEdge A) {
        return ((this.v1) == A.getVertex(0) && (this.v2) == A.getVertex(1))
                || ((this.v1) == A.getVertex(1) && (this.v2) == A.getVertex(0));
    }
}
