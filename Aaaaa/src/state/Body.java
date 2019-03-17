
package state;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author florian
 */
public class Body {
	public String name;
	public float mass, day;
        public Vector3f v = new Vector3f(0,0,0), p = new Vector3f(0,0,0);
	public Body(String n, float a, float b, float c, float d, float e) {  //constructor
		name = n;
                mass = a;
                p.x = b;
                v.y = c*FastMath.sin(FastMath.DEG_TO_RAD*d);
                v.z = c*FastMath.cos(FastMath.DEG_TO_RAD*d);
		day = e;
	}

}
