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
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
/**
 *
 * @author oofer
 */
public class Mainstate extends AbstractAppState {

    private final Node rootNode;
    public final Node localRootNode = new Node ("Test 1");
    private AssetManager assetManager;
    private final InputManager inputManager;
    
    private final Body bodies[] = new Body[4];
    
    private Quaternion day = new Quaternion();
    private float[] total_f;
    private float total_fy;
    private float total_fx;
    private final float AU = 149.6f * FastMath.pow(10, 9);
    private final float scale = 4/AU;
    private final float timestep = 24*3600;
    private float speed = 1f;
    
    private final Interface app;
        
    public Mainstate(Interface app){
        rootNode = app.getRootNode();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        this.app = app;
        
    }
    

    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
        
   
        
      
        
        

        Sphere SunMesh = new Sphere(32,32, 0.3f);
        Geometry SunGeo = new Geometry("Sun", SunMesh);
        SunMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(SunMesh);           // for lighting effect
        Material SunMat = assetManager.loadMaterial("Materials/Sun.j3m");
        SunMat.setBoolean("UseMaterialColors",true);
        SunMat.setColor("Diffuse",ColorRGBA.White);
        SunMat.setColor("Specular",ColorRGBA.White);
        SunMat.setFloat("Shininess", 64f);  // [0,128]
        SunGeo.setMaterial(SunMat);
        localRootNode.attachChild(SunGeo);

        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        light.setColor(ColorRGBA.White);
        localRootNode.addLight(light);

        Sphere EarthMesh = new Sphere(32,32, 0.1f);
        Geometry EarthGeo = new Geometry("Earth", EarthMesh);
        EarthMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(EarthMesh);           // for lighting effect
        Material EarthMat = assetManager.loadMaterial("Materials/Planet.j3m");
        EarthMat.setBoolean("UseMaterialColors",true);
        EarthMat.setColor("Diffuse",ColorRGBA.White);
        EarthMat.setColor("Specular",ColorRGBA.White);
        EarthMat.setFloat("Shininess", 64f);  // [0,128]
        EarthGeo.setMaterial(EarthMat);
        localRootNode.attachChild(EarthGeo);
        
        Sphere VenusMesh = new Sphere(32, 32, 0.05f);
        Geometry VenusGeo = new Geometry("Venus", VenusMesh);
        VenusMesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(VenusMesh);
        VenusGeo.setMaterial(SunMat);
        localRootNode.attachChild(VenusGeo);
        
        Sphere MarsMesh = new Sphere(32, 32, 0.04f);
        Geometry MarsGeo = new Geometry("Mars", MarsMesh);
        MarsMesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(MarsMesh);
        MarsGeo.setMaterial(SunMat);
        localRootNode.attachChild(MarsGeo);
        

        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(actionListener, "Pause");
        inputManager.addMapping("Test", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "Test");
        inputManager.addMapping("speedup", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(actionListener, "speedup");
        inputManager.addMapping("slowdown", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(actionListener, "slowdown");
        inputManager.addMapping("mouseMove", new MouseAxisTrigger(MouseInput.AXIS_X, true), 
                                             new MouseAxisTrigger(MouseInput.AXIS_X, false), 
                                             new MouseAxisTrigger(MouseInput.AXIS_Y, true), 
                                             new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addListener(analogListener, "mouseMove");
        inputManager.addMapping("camMove"   , new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(camMove, "camMove");
     
        
        bodies[0] = new Body ("Sun", (1.98892f * FastMath.pow(10, 30)), 0f, 0f, 25.449f);
        bodies[1] = new Body ("Venus", (4.8685f * FastMath.pow(10, 24)), (0.723f*AU) ,(35.02f*1000f), 116.750f);
	bodies[2] = new Body ("Earth", (5.9742f * FastMath.pow(10, 24)), AU, (29.783f * 1000f), 1f);
        bodies[3] = new Body ("Mars", (0.64171f * FastMath.pow(10, 24)), (1.524f*AU) ,(24.07f*1000f), 1.027f);
	setEnabled(false);
    }
    private final ActionListener actionListener = new ActionListener() {
       @Override
       public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed){
                setEnabled(!isEnabled());
            }
            if (name.equals("Test") && !keyPressed){
                setEnabled(false);
                bodies[0] = new Body ("Sun", (1.98892f * FastMath.pow(10, 30)), 0f, 0f, 25.449f);
                bodies[1] = new Body ("Venus", (4.8685f * FastMath.pow(10, 24)), (0.723f*AU) ,(35.02f*1000f), 116.750f);
                bodies[2] = new Body ("Earth", (5.9742f * FastMath.pow(10, 24)), AU, (29.783f * 1000f), 1f);
                bodies[3] = new Body ("Mars", (0.64171f * FastMath.pow(10, 24)), (1.524f*AU) ,(24.07f*1000f), 1.027f);
                setEnabled(true);
            }
            if (name.equals("speedup") && !keyPressed){
                speed += 0.2;
            }
            if (name.equals("slowdown") && !keyPressed){
                if(speed>0.2){
                speed -= 0.2;
                }
            }

        }
    };
    
    private final ActionListener camMove = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                app.camFly(true);
                //inputManager.setCursorVisible(false);
                
            }
            else{
                app.camFly(false);
                inputManager.setCursorVisible(true);
            }
        }
    };
    
    
    
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float keyPressed, float tpf) {
            if(name.equals("mouseMove")){
               // System.out.println(inputManager.getCursorPosition());
            }
        }
    };
      
    
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
    
    public static void print(){
        System.out.println("is this working");
    }
    
    @Override
    public void update(float tpf) {
            
            //deze for loop loopt door alle elementen in de lijst "bodies".
            for(Body self:bodies){
                
                total_fy = total_fx = 0;
                    for(Body other:bodies){
                        
                        // Om de totale kracht te berekenen, moeten we voor elk lichaam de attractie
                        // met elke ander lichaam berekenen, deze loop loopt doet dat.
                        if(!other.name.equals(self.name)){
                            
                            //deze functie berekent de kracht tussen 2 lichamen.
                            total_f = Calculate.Attraction(self, other);
                            //System.out.println(total_f[0] + " " + total_f[1]+ " " +  self.name + " " + other.name);
                            total_fx += total_f[0];
                            total_fy += total_f[1];
                            
                    }
                }
                //bereken snelheid(f=ma).
                self.vx = self.vx + (total_fx/self.mass)*timestep*speed;
                self.vy = self.vy + (total_fy/self.mass)*timestep*speed;
                    
                //berken plaats met snelheid
                self.px = self.px + (self.vx * timestep*speed);
                self.py = self.py + (self.vy * timestep*speed);
                Spatial MoveGeo = localRootNode.getChild(self.name);
                MoveGeo.setLocalTranslation(new Vector3f(self.px*scale, self.py*scale, 0));
                day.fromAngleAxis(FastMath.PI*2*speed*(timestep/24*3600)*self.day, new Vector3f(0,0,1));
                MoveGeo.rotate(day);
           } 
    }
}
