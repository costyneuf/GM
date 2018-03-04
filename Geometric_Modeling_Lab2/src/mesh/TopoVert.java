package mesh;

import java.util.Arrays;
import java.util.Vector;

import components.set.Set;
import components.set.Set1L;

public class TopoVert {

    /*
     * Private members
     */

    private Set<Integer> mIncVerts;
    private Vector<Integer> mIncEdges;
    private Vector<Integer> mIncFacets;

    /*
     * Constructor
     */
    public TopoVert() {
        this.mIncVerts = new Set1L<>();
        this.mIncEdges = new Vector<Integer>();
        this.mIncFacets = new Vector<Integer>();
    };

    /*
     * Public members
     */
    public void addIncVert(int vert_ind) {
        this.mIncVerts.add(vert_ind);

    }

    public void addIncEdge(int edge_ind) {
        this.mIncEdges.add(edge_ind);
    }

    public void addIncFacet(int facet_ind) {
        this.mIncFacets.add(facet_ind);
    }

    public int getNumberIncVertices() {
        return this.mIncVerts.size();
    }

    public int getNumberIncEdges() {
        return this.mIncEdges.size();
    }

    public int getNumberIncFacets() {
        return this.mIncFacets.size();
    }

    public int getIncVertex(int vert_ind) {

        int[] toArray = new int[this.mIncVerts.size()];
        int i = 0;
        for (int vert : this.mIncVerts) {
            toArray[i] = vert;
            i++;
        }
        Arrays.sort(toArray);
        return toArray[vert_ind];
    }

    public int getIncEdge(int edge_ind) {
        return this.mIncEdges.elementAt(edge_ind);
    }

    public int getIncFacet(int facet_ind) {
        return this.mIncFacets.elementAt(facet_ind);
    }
}
