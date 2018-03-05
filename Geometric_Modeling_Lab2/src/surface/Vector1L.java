/**
 *
 */
package surface;

/**
 * @author gaoxing
 *
 */
public class Vector1L {

    double x = 0, y = 0, z = 0;

    public Vector1L(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length > 0) {
            this.x = x / length;
            this.y = y / length;
            this.z = z / length;
        }
    }

    Vector1L CrossProduct(Vector1L V) {
        Vector1L CrossVector = new Vector1L(this.y * V.z - this.z * V.y,
                this.z * V.x - this.x * V.z, this.x * V.y - this.y * V.x);
        return CrossVector;
    }
}
