/**
 * TopoFacet:  this class holds a facet's topological connectivity)
 * information.
 * Facets are represented as a list of vertex indices.
 */

package mesh;

import java.util.Arrays;
import java.util.Vector;

import components.set.Set;
import components.set.Set1L;

class TopoFacet {

    /*
     * Private members
     */

    private Vector<Integer> mIncVerts;
    private Vector<Integer> mIncEdges;
    private Set<Integer> mIncFacets;

    /*
     * Constructor
     */
    protected TopoFacet() {
        this.mIncVerts = new Vector<Integer>();
        this.mIncEdges = new Vector<Integer>();
        this.mIncFacets = new Set1L<Integer>();
    };

    /*
     * protected members
     */
    protected void addIncVertex(int v_ind) {
        this.mIncVerts.add(v_ind);
    }

    protected void addIncEdge(int e_ind) {
        this.mIncEdges.add(e_ind);
    }

    protected void addIncFacet(int f_ind) {
        this.mIncFacets.add(f_ind);
    }

    protected int getNumberVertices() {
        return this.mIncVerts.size();
    }

    protected int getVertexInd(int vert_ind) {
        return this.mIncVerts.elementAt(vert_ind);
    }

    protected int getNumberEdges() {
        return this.mIncEdges.size();
    }

    protected int getIncEdge(int edge_ind) {
        return this.mIncEdges.elementAt(edge_ind);
    }

    protected int getNumberFacets() {
        return this.mIncFacets.size();
    }

    protected int getIncFacets(int facet_ind) {
        int[] toArray = new int[this.mIncFacets.size()];
        int i = 0;
        for (int facet : this.mIncFacets) {
            toArray[i] = facet;
            i++;
        }
        Arrays.sort(toArray);
        return toArray[facet_ind];
    }

}
