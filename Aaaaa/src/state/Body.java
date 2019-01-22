
package state;

/**
 *
 * @author florian
 */
public class Body {
	public String name;
	public float vx, vy, py, px, mass, day;
	public Body(String n, float a, float b, float c, float d) {  //constructor
		name = n;
                mass = a;
		px = b;
                vy = c;
		day = d;
	}

}
