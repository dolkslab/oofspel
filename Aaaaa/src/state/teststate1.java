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
    //private KeyTrigger k1 = new KeyTrigger(KeyInput.KEY_P);
    private final InputManager inputManager;
    private float Rv = 0.0f;
    private float Rh = 0.0f;

       
    public teststate1(SimpleApplication app){
        rootNode = app.getRootNode();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        rootNode.attachChild(localRootNode);
    
   
        
      
        Box b = new Box(1, 1, 1);
        Geometry geomA = new Geometry("Box", b);
        

        Sphere sphereMesh = new Sphere(32,32, 2f);
        Geometry sphereGeo = new Geometry("Sphere", sphereMesh);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sphereMesh);           // for lighting effect
        Material sphereMat = assetManager.loadMaterial("Materials/Planet.j3m");
        //sphereMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        //sphereMat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        sphereMat.setBoolean("UseMaterialColors",true);
        sphereMat.setColor("Diffuse",ColorRGBA.White);
        sphereMat.setColor("Specular",ColorRGBA.White);
        sphereMat.setFloat("Shininess", 64f);  // [0,128]
        sphereGeo.setMaterial(sphereMat);
        sphereGeo.setLocalTranslation(0,2,-2); // Move it a bit
        sphereGeo.rotate(0, 0, 0);          // Rotate it a bit
        localRootNode.attachChild(sphereGeo);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        localRootNode.addLight(sun);

        //localRootNode.attachChild(geomA);
        
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(actionListener, "Pause");
        inputManager.addMapping("Ru", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(actionListener, "Ru");
        inputManager.addMapping("Rd", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(actionListener, "Rd");
        inputManager.addMapping("Rr", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addListener(actionListener, "Rr");
        inputManager.addMapping("Rl", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addListener(actionListener, "Rl");
 
    }
    private final ActionListener actionListener = new ActionListener() {
       @Override
       public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed){
            setEnabled(!isEnabled());
            }

            if (name.equals("Ru") && keyPressed){  
                Rv = -1;
            }
            else if(name.equals("Rd") && keyPressed){
                Rv = 1;
            }
            else if(!keyPressed){
                Rv = 0;
            }
            
            if (name.equals("Rr") && keyPressed){  
                Rh = -1;
            }
            else if(name.equals("Rl") && keyPressed){
                Rh = 1;
            }
            else if(!keyPressed){
                Rh = 0;
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
        Spatial sphereGeo = localRootNode.getChild("Sphere");
        if (sphereGeo != null) { 
            float speed = 1.0f;
            sphereGeo.rotate(Rv*tpf, Rh*tpf, 0);
            System.out.println(sphereGeo.getLocalRotation());
        }
        
    }
}
