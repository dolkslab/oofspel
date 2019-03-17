/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package state;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 *
 * @author florian
 */
public class Calculate {
    final static float G = 6.67408f*FastMath.pow(10, -11);

    public static Vector3f Attraction(Body self, Body other){

        float d = self.p.distance(other.p);
        
        float f = (G * other.mass)*(self.mass/FastMath.sqr(d));
        
        Vector3f forcevec = other.p.subtract(self.p).normalize().mult(f);
       
        return forcevec;
    }
}
