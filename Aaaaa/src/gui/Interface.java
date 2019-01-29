package gui;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

import state.Mainstate;

public class Interface extends SimpleApplication {
    
    private Nifty nifty;
    private float a = 0, b=0, r=8;
    private int camDir = 1;
    public Vector3f camcoord = new Vector3f();
    public boolean camEnabled;
    public Vector2f lastMousePos = new Vector2f(0, 0);
    

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
        
    }

    
   private final ActionListener camMove = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                camEnabled=true;
                
            }
            else{
                inputManager.setCursorVisible(true);
                camEnabled=false;
            }
        }
    };
    
    
    
    
    public void hide() {
        nifty.gotoScreen("start");

    }
    public void edit() {
        nifty.gotoScreen("select");
    }
    
    public void updateCamPos(){
        a = -((inputManager.getCursorPosition().x-(this.settings.getWidth()/2))/100) + 0.5f*FastMath.PI;
        b = ((inputManager.getCursorPosition().y-(this.settings.getHeight()/2))/100);
        FastMath.sphericalToCartesian(new Vector3f(r, a ,b), camcoord);
        cam.setLocation(camcoord);
        rootNode.getChild("Test").setLocalTranslation(camcoord);
        if(b > 0.5*FastMath.PI || b < -0.5*FastMath.PI)
            camDir = -1;
        else
            camDir = 1;
        
        cam.lookAt(rootNode.getChild("Sun").getLocalTranslation(), new Vector3f(0, camDir ,0));

    }
    
    public void camFly(boolean enabled){
        //flyCam.setEnabled(enabled);

    }
    
}
