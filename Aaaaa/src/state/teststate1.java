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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;
/**
 *
 * @author oofer
 */
public class teststate1 extends AbstractAppState {

    private final Node rootNode;
    private final Node localRootNode = new Node ("Test 1");
    private AssetManager assetManager;
    private final InputManager inputManager;
    private float r;
    private float sma = 6;
    private float ecc = 0.1f;
    private float theta = 0;
    private Vector3f sf = new Vector3f(6f, 0f, 0f);
    Quaternion pitch = new Quaternion();

    

       
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

        Sphere PlanetMesh = new Sphere(32,32, 0.5f);
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
        
        pitch.fromAngleAxis(0.25f*FastMath.PI, new Vector3f(1,0,0));

        
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
        if (SunGeo != null && PlanetGeo != null) { 
            if(PlanetGeo.getLocalTranslation().length() - 1.5 >  SunGeo.getLocalTranslation().length()){
                PlanetGeo.setLocalTranslation(sf);
                r = sma*(1-FastMath.pow(sma, 2f))/(1+ecc*FastMath.cos(theta));
                sf = FastMath.sphericalToCartesian(new Vector3f(r, theta, 0), sf);
                theta = theta+tpf;
            
            
            }
        }
    }
    }
