
package state;

/**
 *
 * @author florian
 */
public class Body {
	public String name;
	public float vx, vy, py, px, mass;
	public Body(String n, float a, float b, float c) {  //constructor
		name = n;
                mass = a;
		px = b;
                vy = c;
		 
	}

}
