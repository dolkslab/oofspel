
package state;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import java.util.ArrayList;
import static state.Mainstate.scale;

/**
 *
 * @author florian
 */
public class Body {
	public String name;
	public float mass, day, obo;
        public Vector3f v = new Vector3f(0,0,0), p = new Vector3f(0,0,0);
        ArrayList<Vector3f> tracer_vertices = new ArrayList<>();
        Mesh tracer = new Mesh();
        Geometry tracer_geo = new Geometry();
	public Body(String n, Material mat, float a, float b, float c, float d, float e, float f) {  //constructor
		name = n;
                mass = a;
                p.x = b;
                v.y = c*FastMath.sin(FastMath.DEG_TO_RAD*d);
                v.z = c*FastMath.cos(FastMath.DEG_TO_RAD*d);
		day = e;
                obo = f;
                
                tracer.clearBuffer(VertexBuffer.Type.Index);
                tracer.clearBuffer(VertexBuffer.Type.Position);
                tracer_vertices.clear();
                tracer.setMode(Mesh.Mode.Lines);
                tracer.setDynamic();
                tracer.updateBound();
                tracer_geo.setName(name+"_tracer");
                tracer_geo.setMesh(tracer);
                tracer_geo.setMaterial(mat);
                tracer_vertices.add(p.mult((scale)));
                
	}

}
