/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package state;

import com.jme3.math.FastMath;

/**
 *
 * @author florian
 */
public class Calculate {
    final static float G = 6.67408f*FastMath.pow(10, -11);
    
    public static float[] Attraction(Body self, Body other){
        float dx = other.px-self.px;
        float dy = other.py-self.py;
        
        float d = FastMath.sqrt((FastMath.sqr(dx))+(FastMath.sqr(dy)));
        float f = (G * other.mass)*(self.mass/FastMath.sqr(d));
        
        float theta = FastMath.atan2(dy, dx);
        float[] forces = {(FastMath.cos(theta)*f), (FastMath.sin(theta)*f)};
        //System.out.println(dx+ " " + f +" "+ self.name);
       
        return forces;
    }
}
