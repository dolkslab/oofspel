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
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.TextField;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;


import state.Mainstate;
import state.Body;

public class Interface extends SimpleApplication {
    
    public Nifty nifty;
    public float azimuth = FastMath.HALF_PI;
    public float pitch=2, r=Math.min(Mainstate.scale*Mainstate.AU*4, 100);
    public Vector3f cam_coord = new Vector3f();
    public boolean cam_enabled = false;
    public Vector2f mouse_pos, last_mouse_pos = new Vector2f(0, 0);
    public Vector3f target_coord = new Vector3f(0, 0, 0);
    private float nearest_to_target;
    public Body selected_target;
    public Slider[] sliders = new Slider[3];
    public TextField[] text_fields = new TextField[3];
    public Label label_target_name;
    public boolean gui_hidden = false;
    public float time_per_second = 10*24*3600;
    public boolean update_enabled = false;
    
    

    @Override
    public void simpleInitApp() {    
        System.out.println(r);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort, 2048, 2048);
        nifty = niftyDisplay.getNifty();
        InterfaceController controller = new InterfaceController(this);
        nifty.fromXml("Interface/gui.xml", "start", controller);
        nifty.fromXml("Interface/gui.xml", "edit", controller);
        
        guiViewPort.addProcessor(niftyDisplay);
        inputManager.setCursorVisible(true);
        stateManager.attach(new Mainstate(this));
        flyCam.setEnabled(false);
        update_cam_pos();
        inputManager.addMapping("camMove" , new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(RMB, "camMove"); 
        inputManager.addMapping("LMB", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(LMB, "LMB");
        
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        cam.setFrustumFar(5000);
        cam.onFrameChange();
        
        inputManager.addMapping("wheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("wheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addListener(analogListener, "wheelUp");
        inputManager.addListener(analogListener, "wheelDown");
        inputManager.getCursorPosition(); 
        Logger.getLogger("de.lessvoid.nifty.Nifty").setFilter(new Filter(){
        @Override
        public boolean isLoggable(LogRecord lr) {
            boolean isReregisterMessage = lr.getMessage().contains("The new definition will override the previous.");
            return !isReregisterMessage;
            }
        });
        Logger.getLogger("").setLevel(Level.SEVERE);
        System.out.println("1");
        //update_enabled=true;
        
        
    }
    
    
    
    private final ActionListener LMB = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            
            if(keyPressed){
                
                nearest_to_target=3000;
                
                for(Body body:Mainstate.bodies){
                    Vector2f screenPos = new Vector2f(cam.getScreenCoordinates(body.p.mult(Mainstate.scale)).x,
                                                      cam.getScreenCoordinates(body.p.mult(Mainstate.scale)).y);
                    float mouseDistance = screenPos.distance(mouse_pos);
                    
                    if(mouseDistance < nearest_to_target && mouseDistance < 50){
                        selected_target=body;
                        nearest_to_target=mouseDistance;
                        label_target_name.setText(selected_target.name);
                        update_target_values();
                    } 
                    
                }
                if(nearest_to_target != 3000){
                target_coord=rootNode.getChild(selected_target.name).getLocalTranslation();
                update_cam();
                }
            }
            
        }
        };
    
    
    private final ActionListener RMB = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            if(keyPressed){
                last_mouse_pos=new Vector2f(inputManager.getCursorPosition().getX(), inputManager.getCursorPosition().getY());
                cam_enabled=true;     
            }
            else {
                inputManager.setCursorVisible(true);
                cam_enabled=false;
                
            }
        }
    };
    
   
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("wheelUp")){
                update_cam_distance(value);
            }
            else if(name.equals("wheelDown")){
                update_cam_distance(-value);
                
            }
        }
    };
    

    
    public Vector3f update_cam_distance(float zoom){
        Spatial target_body = rootNode.getChild(selected_target.name);
        System.out.println(target_body.getLocalScale());
        r+=zoom*cam_coord.distance(target_coord)*0.05;
       // if (cam_coord.distance(target_coord)<= target_body.getLocalScale().x)
            //r -= zoom*(cam_coord.distance(target_coord))*0.15;
        FastMath.cartesianToSpherical(cam_coord, new Vector3f(r, azimuth, pitch));
        FastMath.sphericalToCartesian(new Vector3f(r, azimuth, pitch), cam_coord);
        return cam_coord;
    }
    
    public Vector3f update_cam_pos(){
        
        mouse_pos=inputManager.getCursorPosition();
        
        azimuth += (mouse_pos.x-last_mouse_pos.x)/100;
        pitch -= (mouse_pos.y-last_mouse_pos.y)/100;
        
        if(pitch > FastMath.HALF_PI){
            pitch = FastMath.HALF_PI-0.00001f;
        }
        else if(pitch < -1*FastMath.HALF_PI){
            pitch = -FastMath.HALF_PI+0.00001f;
        }
        
        FastMath.sphericalToCartesian(new Vector3f(r, azimuth ,pitch), cam_coord);
       
        last_mouse_pos=new Vector2f(mouse_pos.getX(), mouse_pos.getY());
        
        return cam_coord;
    }
    
    public void update_cam(){
        
        
        cam.setLocation(cam_coord.add(target_coord));
        cam.lookAt(target_coord, new Vector3f(0, 1 ,0));
        
    }
    
    public void update_target_values(){
        selected_target.mass += (selected_target.mass/100)*sliders[0].getValue();
        selected_target.v = selected_target.v.add(selected_target.v.mult(sliders[1].getValue()/500));
        time_per_second += (time_per_second/50)*sliders[2].getValue();
        text_fields[0].setText(Float.toString(selected_target.mass));
        text_fields[1].setText(Float.toString(selected_target.v.length()));
        text_fields[2].setText(Float.toString(time_per_second/(24*3600)));
    }
    
    public void hide() {
        gui_hidden = true;
        nifty.gotoScreen("start");

    }
    public void edit() {
        gui_hidden = false;
        nifty.gotoScreen("edit");
    }
    
}
