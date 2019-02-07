package gui;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import de.lessvoid.nifty.Nifty;

import state.Mainstate;
import state.Body;

public class Interface extends SimpleApplication {
    
    private Nifty nifty;
    private float azimuth = FastMath.HALF_PI, pitch=0, r=8, mouseX, mouseY;
    private int camDir = 1;
    public Vector3f camCoord = new Vector3f();
    public boolean camEnabled = false;
    public Vector2f mousePos, lastMousePos = new Vector2f(0, 0);
    public Vector3f targetCoord = new Vector3f(0, 0, 0);
    private float nearestToTarget;
    private Body selectedTarget = Mainstate.bodies[0];
    
    

    @Override
    public void simpleInitApp() {        
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort, 2048, 2048);
        nifty = niftyDisplay.getNifty();
        InterfaceController controller = new InterfaceController(this);
        nifty.fromXml("Interface/gui.xml", "select", controller);
        guiViewPort.addProcessor(niftyDisplay);
        inputManager.setCursorVisible(true);
        stateManager.attach(new Mainstate(this));
        flyCam.setEnabled(false);
        inputManager.addMapping("camMove" , new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(RMB, "camMove"); 
        inputManager.addMapping("LMB", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(LMB, "LMB");
        
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        
        inputManager.addMapping("wheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("wheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addListener(analogListener, "wheelUp");
        inputManager.addListener(analogListener, "wheelDown");
        inputManager.getCursorPosition();
        
    }
    private final ActionListener LMB = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                nearestToTarget=3000;
                
                for(Body body:Mainstate.bodies){
                    Vector2f screenPos = new Vector2f(cam.getScreenCoordinates(new Vector3f(body.px*Mainstate.scale, 0, body.py*Mainstate.scale)).x,
                                                      cam.getScreenCoordinates(new Vector3f(body.px*Mainstate.scale, 0, body.py*Mainstate.scale)).y );
                    float mouseDistance = screenPos.distance(mousePos);
                    
                    if(mouseDistance < nearestToTarget && mouseDistance < 50){
                        selectedTarget=body;
                        nearestToTarget=mouseDistance;
                    } 
                    
                }
                if(nearestToTarget != 3000){
                targetCoord=rootNode.getChild(selectedTarget.name).getLocalTranslation();
                System.out.println(selectedTarget.name);
                updateCam();
                }
            }
        }
        };
    
    
    private final ActionListener RMB = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                lastMousePos=new Vector2f(inputManager.getCursorPosition().getX(), inputManager.getCursorPosition().getY());
                camEnabled=true;     
            }
            else {
                inputManager.setCursorVisible(true);
                camEnabled=false;
                
            }
        }
    };
    
   
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("wheelUp")){
                updateCamDistance(value);
            }
            else if(name.equals("wheelDown")){
                updateCamDistance(-value);
                
            }
        }
    };
    
    
    
    public void hide() {
        nifty.gotoScreen("start");

    }
    public void edit() {
        nifty.gotoScreen("select");
    }
    
    public Vector3f updateCamDistance(float zoom){
        r+=zoom*(camCoord.distance(targetCoord)-1)*0.05;
        FastMath.cartesianToSpherical(camCoord, new Vector3f(r, azimuth, pitch));
        FastMath.sphericalToCartesian(new Vector3f(r, azimuth, pitch), camCoord);
        return camCoord;
    }
    
    public Vector3f updateCamPos(){
        
        mousePos=inputManager.getCursorPosition();
        
        azimuth += (mousePos.x-lastMousePos.x)/100;
        pitch -= (mousePos.y-lastMousePos.y)/100;
        
        if(pitch > FastMath.HALF_PI){
            pitch = FastMath.HALF_PI-0.00001f;
        }
        else if(pitch < -1*FastMath.HALF_PI){
            pitch = -FastMath.HALF_PI+0.00001f;
        }
        
        FastMath.sphericalToCartesian(new Vector3f(r, azimuth ,pitch), camCoord);
       
        lastMousePos=new Vector2f(mousePos.getX(), mousePos.getY());
        
        return camCoord;
    }
    
    public void updateCam(){
        
        
        cam.setLocation(camCoord.add(targetCoord));
        cam.lookAt(targetCoord, new Vector3f(0, 1 ,0));

    }
    
    
}
