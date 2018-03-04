/**
 * Use all the preceding classes to represent a mesh with
 * adjacency.connectivity information.
 */
package mesh;

import java.util.Vector;

/**
 * @author gaoxing
 *
 */
public class Mesh {

    /*
     * Constructor
     */
    public Mesh() {

    }

    /*
     * Private members
     *
     */
    private Vector<GeomVert> mGeomVerts;
    private Vector<TopoVert> mTopoVerts;
    private Vector<TopoEdge> mTopoEdges;
    private Vector<TopoFacet> mTopoFacets;

    /**
     * Search for a geometric vertex in the mesh.
     *
     * @param v
     * @return index of {@code v} if found, -1 otherwise.
     */
    private int findGeomVertex(GeomVert v) {

        for (int i = 0; i < this.mGeomVerts.size(); i++) {
            if (this.mGeomVerts.elementAt(i).operator(v)) {
                return i;
            }

        }
        return -1;
    }

    /**
     * Add a facet with arbitrary number of vertices to mesh.
     *
     * @param geomFacet
     */
    private void addFacet(Vector<GeomVert> geomFacet) {
        int i;

        /*
         * Create topo facet (list of geom vertex indices)
         */
        TopoFacet topoFacet = new TopoFacet();

        /*
         * Look for facet vertices in mesh If they don't exist in mesh then add
         * them.
         */
        for (i = 0; i < geomFacet.size(); i++) {
            int v_ind = this.findGeomVertex(geomFacet.elementAt(i));
            if (v_ind == -1) {

                /*
                 * New vertex: add geometric vertex
                 *
                 */
                v_ind = this.mGeomVerts.size();
                this.mGeomVerts.add(geomFacet.elementAt(i));

                /*
                 * Add topo vertex
                 */
                TopoVert topoVert = new TopoVert();
                this.mTopoVerts.add(topoVert);
            }

            /*
             * Add vertex indice to topo facet
             *
             */
            topoFacet.addIncVertex(v_ind);
        }

        /*
         * Add this new topo facet to mesh
         */

        int facet_ind = this.mTopoFacets.size();
        this.mTopoFacets.add(topoFacet);

        /*
         * Add edges of facet to mesh, and check if they alread exsit.
         */
        for (i = 0; i < topoFacet.getNumberVertices(); i++) {
            int prev = (i == 0) ? topoFacet.getNumberVertices() - 1 : i - 1;

            /*
             * Create edge
             */
            TopoEdge e = new TopoEdge();
            e.setVertex(0, topoFacet.getVertexInd(prev));
            e.setVertex(1, topoFacet.getVertexInd(i));

            /*
             * Check if exists
             */
            int e_ind = this.findTopoEdge(e);

            if (e_ind == -1) {
                /*
                 * Add to mesh
                 */
                e_ind = this.mTopoEdges.size();
                this.mTopoVerts.elementAt(e.getVertex(0)).addIncEdge(e_ind);
                this.mTopoVerts.elementAt(e.getVertex(1)).addIncEdge(e_ind);
                this.mTopoEdges.add(e);
            }

            /*
             * Point edge to this facet
             */
            this.mTopoEdges.elementAt(facet_ind).addIncFacet(e_ind);
        }

        /*
         * Compute other connectivity
         */
        for (i = 0; i < topoFacet.getNumberVertices(); i++) {
            /*
             * Add vertex-facet topology
             */
            this.mTopoVerts.elementAt(topoFacet.getVertexInd(i))
                    .addIncFacet(facet_ind);

            /*
             * Add vertex-vertex (edge) topology
             */
            int prev = (i == 0) ? topoFacet.getNumberVertices() - 1 : i - 1;
            int next = (i == topoFacet.getNumberVertices() - 1) ? 0 : i + 1;

            this.mTopoVerts.elementAt(topoFacet.getVertexInd(i))
                    .addIncVert(topoFacet.getVertexInd(prev));
            this.mTopoVerts.elementAt(topoFacet.getVertexInd(i))
                    .addIncVert(topoFacet.getVertexInd(next));
        }

        /*
         * Facet-facet adjacency
         */
        for (i = 0; i < this.mTopoFacets.elementAt(facet_ind)
                .getNumberEdges(); i++) {
            TopoEdge edge = this.mTopoEdges.elementAt(
                    this.mTopoFacets.elementAt(facet_ind).getIncEdge(i));
            for (int j = 0; j < edge.getNumberIncFacets(); j++) {
                if (edge.getIncFacet(j) != facet_ind) {
                    this.mTopoFacets.elementAt(facet_ind)
                            .addIncFacet(edge.getIncFacet(j));
                    this.mTopoFacets.elementAt(edge.getIncFacet(j))
                            .addIncFacet(facet_ind);
                }
            }
        }

    }

    /**
     * Search for an edge in the mesh.
     *
     * @param e
     * @return index of {@code e} if found, -1 otherwise
     */
    private int findTopoEdge(TopoEdge e) {
        for (int i = 0; i < this.mTopoEdges.size(); i++) {
            if (this.mTopoEdges.elementAt(i).operator(e)) {
                return i;
            }

        }
        return -1;
    }

    /*
     * Public members
     */

    /**
     * Add a triangle to the mesh.
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param x3
     * @param y3
     * @param z3
     */
    public void addFacet(float x1, float y1, float z1, float x2, float y2,
            float z2, float x3, float y3, float z3) {

        Vector<GeomVert> geomFacet = new Vector<GeomVert>();
        geomFacet.add(new GeomVert(x1, y1, z1));
        geomFacet.add(new GeomVert(x2, y2, z2));
        geomFacet.add(new GeomVert(x3, y3, z3));
        this.addFacet(geomFacet);
    }

    public int getNumberVertices() {
        return this.mGeomVerts.size();
    }

    public int getNumberEdges() {
        return this.mTopoEdges.size();
    }

    public int getNumberFacets() {
        return this.mTopoFacets.size();
    }

    public TopoVert getVertex(int vert_ind) {
        return this.mTopoVerts.elementAt(vert_ind);
    }

    public TopoEdge getEdge(int edge_ind) {
        return this.mTopoEdges.elementAt(edge_ind);
    }

    public TopoFacet getFacet(int facet_ind) {
        return this.mTopoFacets.elementAt(facet_ind);
    }

    public GeomVert getGeomVertex(int vert_ind) {
        return this.mGeomVerts.elementAt(vert_ind);
    }

}
