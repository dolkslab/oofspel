/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package state;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.math.Line;
/**
 *
 * @author oofer
 */
public class teststate1 extends AbstractAppState {

    private final Node rootNode;
    private final Node localRootNode = new Node ("Test 1");
    private AssetManager assetManager;
    private final InputManager inputManager;
    private final float G = 6.67408f;//*FastMath.pow(10, -11);
    private float f;
    private final float EarthMass = 4f;
    private final float SunMass = 13f;
    private float theta;
    private Vector3f sf = new Vector3f(5f, 0f, 0f);
    private Vector3f v = new Vector3f(0f, 0f, 0f);
    private Vector3f gravV = new Vector3f();
    private float vx = 0;
    private float vy = 3f;
    private float fx;
    private float fy;
    private float sx = 5;
    private float sy;
    private Quaternion day = new Quaternion();

    

       
    public teststate1(SimpleApplication app){
        rootNode = app.getRootNode();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
    
   
        
      
        
        

        Sphere SunMesh = new Sphere(32,32, 1f);
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

        Sphere PlanetMesh = new Sphere(32,32, 0.1f);
        Geometry PlanetGeo = new Geometry("Planet", PlanetMesh);
        PlanetMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(SunMesh);           // for lighting effect
        Material PlanetMat = assetManager.loadMaterial("Materials/Planet.j3m");
        PlanetMat.setBoolean("UseMaterialColors",true);
        PlanetMat.setColor("Diffuse",ColorRGBA.White);
        PlanetMat.setColor("Specular",ColorRGBA.White);
        PlanetMat.setFloat("Shininess", 64f);  // [0,128]
        PlanetGeo.setMaterial(PlanetMat);
        PlanetGeo.setLocalTranslation(sf);
        localRootNode.attachChild(PlanetGeo);
        
        
        
        
        

        
        day.fromAngleAxis(0, new Vector3f(0,1,0));

        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(actionListener, "Pause");
        inputManager.addMapping("Test", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "Test");
        setEnabled(false);

 
    }
    private final ActionListener actionListener = new ActionListener() {
       @Override
       public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed){
                setEnabled(!isEnabled());
            }
            if (name.equals("Test") && !keyPressed){
               
            }


        }
    };
        
    
    @Override
    public void cleanup(){
        rootNode.detachChild(localRootNode);
        
        super.cleanup();
    }
    
    @Override
    public void update(float tpf) {
        Spatial SunGeo = localRootNode.getChild("Sun");
        Spatial PlanetGeo = localRootNode.getChild("Planet");
        Spatial LineGeo = localRootNode.getChild("line");
        
        if (SunGeo != null && PlanetGeo != null) { 
            if(PlanetGeo.getLocalTranslation().length() - 1.1 >  SunGeo.getLocalTranslation().length()){
                f = G *((SunMass*EarthMass)/FastMath.pow(PlanetGeo.getLocalTranslation().distance(SunGeo.getLocalTranslation()), 2));
                theta = FastMath.atan2(sy, sx) - FastMath.PI;
                fx = FastMath.cos(theta)*f;
                fy = FastMath.sin(theta)*f;
                vx = vx + ((fx / EarthMass) * 0.01f);
                vy = vy + ((fy / EarthMass) * 0.01f);
                sx = sx + (vx*0.01f);
                sy = sy + (vy*0.01f);
                sf = new Vector3f(sx, sy, 0);
                
                
                System.out.println(FastMath.cos(theta) + " "+FastMath.sin(theta) +" "+ theta);
                
                if(fx >= 0){
                   //setEnabled(false); 
                }
                
                
                PlanetGeo.setLocalTranslation(sf);
                day.fromAngleAxis(tpf*FastMath.PI*2, new Vector3f(0,0,1));
                PlanetGeo.rotate(day);
                day.fromAngleAxis(tpf*FastMath.PI*0.1f, new Vector3f(0,0,1));
                SunGeo.rotate(day);
            
            }
        }
    }
    }
