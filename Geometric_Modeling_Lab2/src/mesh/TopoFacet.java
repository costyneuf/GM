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

public class TopoFacet {

    /*
     * Private members
     */

    private Vector<Integer> mIncVerts;
    private Vector<Integer> mIncEdges;
    private Set<Integer> mIncFacets;

    /*
     * Constructor
     */
    public TopoFacet() {
        this.mIncVerts = new Vector<Integer>();
        this.mIncEdges = new Vector<Integer>();
        this.mIncFacets = new Set1L<Integer>();
    };

    /*
     * protected members
     */
    public void addIncVertex(int v_ind) {
        this.mIncVerts.add(v_ind);
    }

    public void addIncEdge(int e_ind) {
        this.mIncEdges.add(e_ind);
    }

    public void addIncFacet(int f_ind) {
        if (!this.mIncFacets.contains(f_ind)) {
            this.mIncFacets.add(f_ind);
        }
    }

    public int getNumberVertices() {
        return this.mIncVerts.size();
    }

    public int getVertexInd(int vert_ind) {
        return this.mIncVerts.elementAt(vert_ind);
    }

    public int getNumberEdges() {
        return this.mIncEdges.size();
    }

    public int getIncEdge(int edge_ind) {
        return this.mIncEdges.elementAt(edge_ind);
    }

    public int getNumberFacets() {
        return this.mIncFacets.size();
    }

    public int getIncFacets(int facet_ind) {
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
