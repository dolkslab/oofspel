package gui;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
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

public class Interface extends SimpleApplication {
    
    private Nifty nifty;
    private float angX = 0, angY=0, r=8, lastAngX=0.5f*FastMath.PI, lastAngY=0;
    private int camDir = 1;
    public Vector3f camcoord = new Vector3f();
    public boolean camEnabled;
    public Vector2f lastMousePos = new Vector2f(0, 0);
    public boolean flip;
    

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
        inputManager.addListener(camMove, "camMove"); 
        
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

    
    private final ActionListener camMove = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                lastMousePos= new Vector2f(inputManager.getCursorPosition().getX(), inputManager.getCursorPosition().getY());
                camEnabled=true;
                
 
                
            }
            else{
                inputManager.setCursorVisible(true);
                camEnabled=false;
                lastAngX = angX;
                lastAngY = angY;
            }
        }
    };
    
   
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("wheelUp")){
                r += value*0.5;
                camEnabled=true;
            }
            else if(name.equals("wheelDown")){
                r -= value*0.5;
                camEnabled=true;
            }
            else
                camEnabled=false;
                
        }
    };
    
    
    
    public void hide() {
        nifty.gotoScreen("start");

    }
    public void edit() {
        nifty.gotoScreen("select");
    }
    
    public Vector3f updateCamPos(){
        
        angY = lastAngY -((inputManager.getCursorPosition().y-lastMousePos.y)/100);
        
        
        if(angY > FastMath.PI)
            angY -= 2*FastMath.PI;
        
        else if(angY < -FastMath.PI)
            angY+= 2*FastMath.PI;
        
        if(angY > 0.5*FastMath.PI || angY < -0.5*FastMath.PI){
            camDir = -1;
            flip = true;
            
        }
        else {
            camDir = 1;
            if(flip){
                lastAngX = angX;
                flip = false;
            }
        }
        angX = lastAngX -((inputManager.getCursorPosition().x-lastMousePos.x)/100);
        
        FastMath.sphericalToCartesian(new Vector3f(r, angX*camDir*-1 ,angY), camcoord);
        return camcoord;
    }
    
    public void updateCam(String target){
        
        
        cam.setLocation(camcoord.add(rootNode.getChild(target).getLocalTranslation()));
        cam.lookAt(rootNode.getChild(target).getLocalTranslation(), new Vector3f(0, camDir ,0));

    }
    
    
}
