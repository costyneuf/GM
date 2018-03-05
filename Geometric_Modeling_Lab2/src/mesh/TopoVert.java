package mesh;

import java.util.Arrays;
import java.util.Vector;

import components.set.Set;
import components.set.Set1L;

class TopoVert {

    /*
     * Private members
     */

    private Set<Integer> mIncVerts;
    private Vector<Integer> mIncEdges;
    private Vector<Integer> mIncFacets;

    /*
     * Constructor
     */
    protected TopoVert() {
        this.mIncVerts = new Set1L<>();
        this.mIncEdges = new Vector<Integer>();
        this.mIncFacets = new Vector<Integer>();
    };

    /*
     * protected members
     */
    protected void addIncVert(int vert_ind) {
        if (!this.mIncVerts.contains(vert_ind)) {
            this.mIncVerts.add(vert_ind);
        }

    }

    protected void addIncEdge(int edge_ind) {
        this.mIncEdges.add(edge_ind);
    }

    protected void addIncFacet(int facet_ind) {
        this.mIncFacets.add(facet_ind);
    }

    protected int getNumberIncVertices() {
        return this.mIncVerts.size();
    }

    protected int getNumberIncEdges() {
        return this.mIncEdges.size();
    }

    protected int getNumberIncFacets() {
        return this.mIncFacets.size();
    }

    protected int getIncVertex(int vert_ind) {

        int[] toArray = new int[this.mIncVerts.size()];
        int i = 0;
        for (int vert : this.mIncVerts) {
            toArray[i] = vert;
            i++;
        }
        Arrays.sort(toArray);
        return toArray[vert_ind];
    }

    protected int getIncEdge(int edge_ind) {
        return this.mIncEdges.elementAt(edge_ind);
    }

    protected int getIncFacet(int facet_ind) {
        return this.mIncFacets.elementAt(facet_ind);
    }
}
