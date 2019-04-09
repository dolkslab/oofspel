    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package state;
import gui.Interface;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import de.lessvoid.nifty.controls.Slider;
import java.util.ArrayList;
/**
 *
 * @author oofer
 */

public class Mainstate extends AbstractAppState {

    private final Node root_node;
    public final Node local_root_node = new Node ("Test 1");
    private final AssetManager asset_manager;
    private final InputManager input_manager;
    
    private final Quaternion day = new Quaternion(),pitch = new Quaternion();
    public static Body bodies[] = new Body[4];
    public Vector3f total_f;
    public static final float AU = 149.6f * FastMath.pow(10, 9);
    public static final float scale = 400/AU;
    public float time_step;
    private final Interface app;
    
    
    Mesh earth_tracer = new Mesh();
    ArrayList<Vector3f> vertices_l = new ArrayList<>();
    ArrayList<Integer> indexes_l = new ArrayList<>();
    int tracer_length = 200000;
        
    public Mainstate(Interface app){
        root_node = app.getRootNode();
        input_manager = app.getInputManager();
        asset_manager = app.getAssetManager();
        this.app = app;
    }
    

    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        root_node.attachChild(local_root_node);

        Sphere sun_mesh = new Sphere(32,32, 10f);
        Geometry sun_geo = new Geometry("Sun", sun_mesh);
        sun_mesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sun_mesh);           // for lighting effect
        Material sun_mat = asset_manager.loadMaterial("Materials/Sun.j3m");
        sun_mat.setBoolean("UseMaterialColors",true);
        sun_mat.setColor("Diffuse",ColorRGBA.White);
        sun_mat.setColor("Specular",ColorRGBA.White);
        sun_mat.setFloat("Shininess", 64f);  // [0,128]
        sun_mat.setColor("GlowColor", new ColorRGBA(1, 1, 1, 1));
        sun_geo.setLocalScale(0.5f);
       
        sun_geo.setMaterial(sun_mat);
        local_root_node.attachChild(sun_geo);

        PointLight light = new PointLight();
        light.setPosition(new Vector3f(0, 0 ,0));
        light.setRadius(10000f);
        light.setColor(ColorRGBA.White.mult(2f));
        local_root_node.addLight(light);
        
        Material unshaded_sun = new Material(asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_sun.setColor("Color", new ColorRGBA(1f,0.7f,0.1f,1f));
        Geometry sun_far = new Geometry("Sun_far", sun_mesh);
        sun_far.setMaterial(unshaded_sun);
        local_root_node.attachChild(sun_far);
        

        Sphere earth_mesh = new Sphere(32,32, 10f);
        Geometry earth_geo = new Geometry("Earth", earth_mesh);
        earth_mesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(earth_mesh);           // for lighting effect
        Material earth_mat = asset_manager.loadMaterial("Materials/Earth.j3m");
        earth_mat.setBoolean("UseMaterialColors",true);
        earth_mat.setColor("Diffuse",ColorRGBA.White);
        earth_mat.setColor("Specular",ColorRGBA.White);
        earth_mat.setFloat("Shininess", 64f);  // [0,128]
        earth_geo.setMaterial(earth_mat);
        earth_geo.setLocalScale(0.1f);
        local_root_node.attachChild(earth_geo);
        
        Material unshaded_earth = new Material(asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_earth.setColor("Color", new ColorRGBA(0f,0.4f,1f,1f));
        Geometry earth_far = new Geometry("Earth_far", earth_mesh);
        earth_far.setMaterial(unshaded_earth);
        local_root_node.attachChild(earth_far);
        
        
        Sphere venus_mesh = new Sphere(32, 32, 10f);
        Geometry venus_geo = new Geometry("Venus", venus_mesh);
        venus_mesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(venus_mesh);
        Material venus_mat = asset_manager.loadMaterial("Materials/Venus.j3m");
        venus_mat.setBoolean("UseMaterialColors",true);
        venus_mat.setColor("Diffuse",ColorRGBA.White);
        venus_mat.setColor("Specular",ColorRGBA.White);
        venus_mat.setFloat("Shininess", 64f);  // [0,128]
        venus_geo.setMaterial(venus_mat);
        venus_geo.setLocalScale(0.07f);
        local_root_node.attachChild(venus_geo);
        
        Material unshaded_venus = new Material(asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_venus.setColor("Color", new ColorRGBA(0.7f,0.25f,0.0f,1f));
        Geometry venus_far = new Geometry("Venus_far", venus_mesh);
        venus_far.setMaterial(unshaded_venus);
        local_root_node.attachChild(venus_far);
        
        
        Sphere mars_mesh = new Sphere(32, 32, 10f);
        Geometry mars_geo = new Geometry("Mars", mars_mesh);
        mars_mesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(mars_mesh);
        Material mars_mat = asset_manager.loadMaterial("Materials/Mars.j3m");
        mars_mat.setBoolean("UseMaterialColors",true);
        mars_mat.setColor("Diffuse",ColorRGBA.White);
        mars_mat.setColor("Specular",ColorRGBA.White);
        mars_mat.setFloat("Shininess", 64f);  // [0,128]
        mars_geo.setMaterial(mars_mat);
        mars_geo.setLocalScale(0.03f);
        local_root_node.attachChild(mars_geo);
        
        Material unshaded_mars = new Material(asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_mars.setColor("Color", new ColorRGBA(0.5f,0.1f,0.05f,1f));
        Geometry mars_far = new Geometry("Mars_far", mars_mesh);
        mars_far.setMaterial(unshaded_mars);
        local_root_node.attachChild(mars_far);

        

        
        local_root_node.attachChild(SkyFactory.createSky(asset_manager, "Textures/spacemap.dds", SkyFactory.EnvMapType.CubeMap));
        
        input_manager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        input_manager.addListener(action_listener, "Pause");
        input_manager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_T));
        input_manager.addListener(action_listener, "Reset");
        input_manager.addMapping("test", new KeyTrigger(KeyInput.KEY_I));
        input_manager.addListener(action_listener, "test");


        
        earth_tracer.setMode(Mesh.Mode.Lines);
        earth_tracer.updateBound();
        unshaded_earth.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        init_bodies();
        Geometry earth_tracer_geo = new Geometry("earth_tracer", earth_tracer);
        earth_tracer_geo.setMaterial(unshaded_earth);
        local_root_node.attachChild(earth_tracer_geo);
        earth_tracer.setDynamic();

        
    }
    
    public void init_bodies(){
        
        System.out.println("kanekr");
        //(naam, massa, startplaats, startsnelheid, inclinatie(deg), daglengte(dagen), hoek van rotatieas)
        bodies[0] = new Body ("Sun", (1.98892f * FastMath.pow(10, 30)), 0f, 0f, 0f, 25.449f, 7.25f);
        bodies[1] = new Body ("Venus", (4.8685f * FastMath.pow(10, 24)), (0.723f*AU) ,(35.02f*1000f), 3.39f, 116.750f, 177.36f);
	bodies[2] = new Body ("Earth", (5.9742f * FastMath.pow(10, 24)), AU, (29.783f * 1000f), 0f, 1f, 23.44f);
        bodies[3] = new Body ("Mars", (0.64171f * FastMath.pow(10, 24)), (1.524f*AU) ,(24.07f*1000f),1.850f, 1.027f, 25.19f);
        if(app.selected_target == null)
            app.selected_target = bodies[0];
        for(Body body:bodies){
            Spatial MoveGeo = local_root_node.getChild(body.name);
                MoveGeo.setLocalTranslation(new Vector3f(body.p.x*scale, 0, body.p.y*scale));
                pitch.fromAngleAxis((-FastMath.HALF_PI)-FastMath.DEG_TO_RAD*-body.obo, new Vector3f(1, 0, 0));
                MoveGeo.rotate(pitch);
                System.out.println(body.name + " " + body.p.length()/AU);
        }
        for(Body body:bodies){
            Spatial resize = local_root_node.getChild((body.name+"_far"));
            if(resize != null)
                resize.setLocalScale((resize.getLocalTranslation().distance(app.cam_coord.add(app.target_coord)))/1500);
        }
        
        vertices_l.add(bodies[2].p.mult(scale));
        
        
    }
    
    int test_count = 1;
    
  
    private final ActionListener action_listener = new ActionListener() {
       @Override
       public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed){
                app.update_enabled=(!app.update_enabled);

            }
            if (name.equals("Reset") && !keyPressed){
                app.update_enabled=false;
                init_bodies();
                if(!app.gui_hidden)
                    app.update_target_values();
                
               //app.update_enabled=true;
            }
            if (name.equals("test") && !keyPressed){
                
            
                
                
                
            }
            

        }
    };
    
    public static int[] convertIntegers(ArrayList<Integer> integers)
{
    int[] ret = new int[integers.size()];
    for (int i=0; i < ret.length; i++)
    {
        ret[i] = integers.get(i);
    }
    return ret;
}
    
    

      
    
    @Override
    public void cleanup(){
        root_node.detachChild(local_root_node);
        
        super.cleanup();
    }
    

        
        
        
    @Override
    public void update(float tpf) {
        
        for(Body body:bodies){
            Spatial resize = local_root_node.getChild((body.name+"_far"));
            if(resize != null)
                resize.setLocalScale((resize.getLocalTranslation().distance(app.cam_coord.add(app.target_coord)))/1500);
        }
            
        if(app.update_enabled){
            time_step = app.time_per_second*tpf;

                //deze for loop loopt door alle elementen in de lijst "bodies".
                for(Body self:bodies){

                    total_f = Vector3f.ZERO;
                        for(Body other:bodies){

                            // Om de totale kracht te berekenen, moeten we voor elk lichaam de attractie
                            // met elke ander lichaam berekenen, deze loop loopt doet dat.
                            if(!other.name.equals(self.name)){

                                //deze functie berekent de kracht tussen 2 lichamen.
                                total_f = total_f.add(Calculate.Attraction(self, other));        

                        }
                    }
                    //bereken snelheid(f=ma).
                    self.v = self.v.add(total_f.divide(self.mass).mult(time_step));
                    if(app.selected_target.equals(self) && !app.gui_hidden){
                        for(Slider slider:app.sliders){
                            if(slider.getValue() != 0){
                                app.update_target_values();
                                break;
                            }
                        }
                    }     

                    //berken plaats met snelheid
                    self.p = self.p.add(self.v.mult(time_step)); 

                    Spatial move_geo = local_root_node.getChild(self.name);
                    move_geo.setLocalTranslation(self.p.mult(scale));
                    day.fromAngleAxis(FastMath.PI*2*(time_step/(24*3600))/self.day, new Vector3f(0,0,-1));
                    move_geo.rotate(day);
                    
                    Spatial move_geo_far = local_root_node.getChild(self.name + "_far");
                    move_geo_far.setLocalTranslation(move_geo.getLocalTranslation());
                    if(self.name.equals("Earth")){
                        vertices_l.add(self.p.mult(scale));
                        Vector3f [] vertices = new Vector3f[vertices_l.size()];
                        vertices_l.toArray(vertices);
                        indexes_l.add(test_count-1);
                        indexes_l.add(test_count);

                        int [] indexes = convertIntegers(indexes_l);
 
                        earth_tracer.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                        earth_tracer.setBuffer(VertexBuffer.Type.Index, 2, BufferUtils.createIntBuffer(indexes));
                

                        earth_tracer.updateBound(); 
                        test_count++;
                        if (test_count>tracer_length/((int)app.time_per_second/86400)){
                            vertices_l.remove(0);
                            indexes_l.remove(indexes_l.size()-1);
                        }
                        if (vertices_l.size()>tracer_length/((int)app.time_per_second/86400))
                            indexes_l.remove(indexes_l.size()-1);
                    }
                    
                }
                 
            }
            Spatial earth_tracer_geo = local_root_node.getChild("earth_tracer");
            if(app.cam_enabled)
                app.update_cam_pos();
            app.update_zoom(tpf);

            app.update_cam(); 
            
            System.out.println(app.time_per_second/86400);

            
            
    }
}
