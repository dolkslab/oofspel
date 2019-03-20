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
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
import de.lessvoid.nifty.controls.Slider;
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
    public static final float scale = 40/AU;
    public float time_step;

    
    
    private final Interface app;
        
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

        Sphere sun_mesh = new Sphere(32,32, 5f);
        Geometry sun_geo = new Geometry("Sun", sun_mesh);
        sun_mesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sun_mesh);           // for lighting effect
        Material sun_mat = asset_manager.loadMaterial("Materials/Sun.j3m");
        sun_mat.setBoolean("UseMaterialColors",true);
        sun_mat.setColor("Diffuse",ColorRGBA.White);
        sun_mat.setColor("Specular",ColorRGBA.White);
        sun_mat.setFloat("Shininess", 64f);  // [0,128]
        sun_mat.setColor("GlowColor", new ColorRGBA(1, 1, 1, 1));
       
        sun_geo.setMaterial(sun_mat);
        local_root_node.attachChild(sun_geo);

        PointLight light = new PointLight();
        light.setPosition(new Vector3f(0, 0 ,0));
        light.setRadius(100f);
        light.setColor(ColorRGBA.White.mult(2.5f));
        local_root_node.addLight(light);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(10.3f));
        local_root_node.addLight(al);

        Sphere earth_mesh = new Sphere(32,32, 1f);
        Geometry earth_geo = new Geometry("Earth", earth_mesh);
        earth_mesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(earth_mesh);           // for lighting effect
        Material earth_mat = asset_manager.loadMaterial("Materials/Planet.j3m");
        earth_mat.setBoolean("UseMaterialColors",true);
        earth_mat.setColor("Diffuse",ColorRGBA.White);
        earth_mat.setColor("Specular",ColorRGBA.White);
        earth_mat.setFloat("Shininess", 64f);  // [0,128]
        earth_geo.setMaterial(earth_mat);
        local_root_node.attachChild(earth_geo);
        earth_geo.setLocalTranslation(3, 0, 0);
        
        Sphere venus_mesh = new Sphere(32, 32, 0.7f);
        Geometry venus_geo = new Geometry("Venus", venus_mesh);
        venus_mesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(venus_mesh);
        venus_geo.setMaterial(sun_mat);
        local_root_node.attachChild(venus_geo);
        
        Sphere mars_mesh = new Sphere(32, 32, 0.3f);
        Geometry mars_geo = new Geometry("Mars", mars_mesh);
        mars_mesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(mars_mesh);
        mars_geo.setMaterial(sun_mat);
        local_root_node.attachChild(mars_geo);
        
        Sphere test_mesh = new Sphere (32, 32, 0.2f);
        Geometry test_geo = new Geometry("Test", test_mesh);

        Material unshaded_mat = new Material(asset_manager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded_mat.setColor("Color", ColorRGBA.Blue);
        test_geo.setMaterial(unshaded_mat);
        
        local_root_node.attachChild(test_geo);
        
        
        
        input_manager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        input_manager.addListener(action_listener, "Pause");
        input_manager.addMapping("Test", new KeyTrigger(KeyInput.KEY_T));
        input_manager.addListener(action_listener, "Test");
        input_manager.addMapping("speedup", new KeyTrigger(KeyInput.KEY_I));
        input_manager.addListener(action_listener, "speedup");
        input_manager.addMapping("slowdown", new KeyTrigger(KeyInput.KEY_K));
        input_manager.addListener(action_listener, "slowdown");
        
        init_bodies();
        
    }
    
    public void init_bodies(){
        
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
        }
        
    }
    
    public static void testf(){
        
    }
    
    private final ActionListener action_listener = new ActionListener() {
       @Override
       public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed){
                app.update_enabled=(!app.update_enabled);

            }
            if (name.equals("Test") && !keyPressed){
                app.update_enabled=false;
                init_bodies();
                if(!app.gui_hidden)
                    app.update_target_values();
                
               //app.update_enabled=true;
            }
            

        }
    };
    
    
    
    
    
    ;
      
    
    @Override
    public void cleanup(){
        root_node.detachChild(local_root_node);
        
        super.cleanup();
    }
    
    public static void print(){
        System.out.println("is this working");
    }
    
    @Override
    public void update(float tpf) {
        Spatial test_spatial = local_root_node.getChild("Test");
            test_spatial.setLocalScale(app.r/100);
            System.out.println(app.r);
            
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

                    Spatial MoveGeo = local_root_node.getChild(self.name);
                    MoveGeo.setLocalTranslation(self.p.mult(scale));
                    day.fromAngleAxis(FastMath.PI*2*(time_step/(24*3600))/self.day, new Vector3f(0,0,-1));
                    MoveGeo.rotate(day);
                    if(self.name.equals("Earth"))
                        test_spatial.setLocalTranslation(MoveGeo.getLocalTranslation());
                }
                 
            }
            if(app.cam_enabled)
                app.update_cam_pos(); 
            app.update_cam(); 
            
    }
}
