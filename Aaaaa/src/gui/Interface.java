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
    public float azimuth = FastMath.HALF_PI, pitch=2f, zoom=Math.min(Mainstate.scale*Mainstate.AU*10, 100), time_per_second = 10*24*3600, zoom_v = 0, zoom_a = 0;
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
    public boolean update_enabled = false;
    
    

    @Override
    public void simpleInitApp() {    
        System.out.println(zoom);
        
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
        cam.setFrustumFar(10000);
        cam.setFrustumNear(0.9f);
        cam.setFrustumPerspective(70, (16f/9f), 0.9f, 10000f);
        
        
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
        //update_enabled=true;
        
        
    }
    
    
    //Deze functie is voor het selecteren van een hemmellichaam, de "selected_target".
    private final ActionListener LMB = new ActionListener(){
        @Override
        public void onAction(String name, boolean keyPressed, float tpf){
            
            if(keyPressed){
                
                nearest_to_target=3000;
                
                //Deze loop kijkt welk lichaam het dichtste bij de muis is.
                for(Body body:Mainstate.bodies){
                    //Eerst worden de 3d coördinaten geprojecteerd op op het scherm om zo 2d coördinaten te krijgen.
                    Vector2f screenPos = new Vector2f(cam.getScreenCoordinates(body.p.mult(Mainstate.scale)).x,
                                                      cam.getScreenCoordinates(body.p.mult(Mainstate.scale)).y);
                    float mouseDistance = screenPos.distance(mouse_pos);
                    
                    //Elke keer als iets dichterbij dan alle andere lichamen wordt het opgeslachen in een variabele.
                    if(mouseDistance < nearest_to_target && mouseDistance < 50){
                        selected_target=body;
                        nearest_to_target=mouseDistance;
                        label_target_name.setText(selected_target.name);
                        update_target_values();
                    } 
                    
                }
                //Als het lichaam dichtbij genoeg is is het de nieuwe selected_target.
                if(nearest_to_target != 3000){
                target_coord=rootNode.getChild(selected_target.name).getLocalTranslation();
                update_cam();
                }
            }
            
        }
        };
    
    
    //De camera moet aleen bewogen worden als de rechtermuisknop ingedrukt is. Hier wordt dat gechecked.
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
    
    
    //De AnalogListener detecteert als je scrollt en geeft dan welke richting je scrollt.
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            Spatial target_body_spatial = rootNode.getChild(selected_target.name);
            if(name.equals("wheelUp")){
                zoom_v += FastMath.sqrt((zoom-((target_body_spatial.getLocalScale().x*10)+cam.getFrustumNear()))+1);
            }
            else if(name.equals("wheelDown")){
                zoom_v -= FastMath.sqrt((zoom-((target_body_spatial.getLocalScale().x*10)+cam.getFrustumNear()))+1);
                
            }
        }
    };
    
    //Deze functie wordt aangeroepen als je de rechtermuisknop ingedrukt houdt. De functie zet de XY beweging van de muis om in XYZ coördinaten voor de camera.
    //De camera werkt als "orbital camera" wat betekent dat een object omcirkelt. De beweging is dus opgebouwt uit drie variabelen: de afstand tot het object(zoom),
    //de rotatie op en neer(pitch) en van rechts naar links(azimuth).
    public Vector3f update_cam_pos(){
 
        mouse_pos=inputManager.getCursorPosition();
        
        //De XY beweging van muis wordt hier omgezet in pitch en azimuth.
        azimuth += (mouse_pos.x-last_mouse_pos.x)/100;
        pitch -= (mouse_pos.y-last_mouse_pos.y)/100;
        
        //Hier wordt gezorgd dat de pitch niet verder dan recht omhoog of recht omlaag gaat.
        if(pitch > FastMath.HALF_PI){
            pitch = FastMath.HALF_PI-0.001f;
        }
        else if(pitch < -1*FastMath.HALF_PI){
            pitch = -FastMath.HALF_PI+0.001f;
        }
        
        //De positie die nu voor de camera opgebouwd is(zoom, azimuth, pitch) wordt ook wel een spherische coördinaat genoemd.
        //Die wordt hier omgezet naar een XYZ coördinaat.
        FastMath.sphericalToCartesian(new Vector3f(zoom, azimuth ,pitch), cam_coord);
       
        last_mouse_pos=new Vector2f(mouse_pos.getX(), mouse_pos.getY());
        
        return cam_coord;
    }

    //Deze functie regelt de zoom. Hij wordt aangeroepen als je scrollt.
    public void update_zoom(float tpf){
        Spatial target_body_spatial = rootNode.getChild(selected_target.name);
        
        //Om de zoom effen te maken wordt de zoomafstand niet direct bepaald door het scrollwiel, maar via een snelheid. Deze snelheid wordt groter of kleiner op basis
        //van het scrollwiel. De zoomsnelheid wordt ook afgremend door een ingestelde afremmingsfactor.
        zoom_a = -2f*zoom_v;
        zoom_v += zoom_a*tpf;
        //Als de snelheid kleiner is dan 1 wordt de snelheid op 0 gezet.
        if(FastMath.abs(zoom_v) < 1)
            zoom_v=0;
        
        //We willen niet dat je verder dan het oppervlak van een object kan zoomen. Als dat wordt geprobeert wordt dat hier geblokkeerd.
        if((target_body_spatial.getLocalScale().x*10)+cam.getFrustumNear()<=zoom)
            zoom += zoom_v*tpf;
        else{
            zoom_v = zoom_a = 0;
            zoom = (target_body_spatial.getLocalScale().x*10)+cam.getFrustumNear();
            
        }
            
        
        FastMath.cartesianToSpherical(cam_coord, new Vector3f(zoom, azimuth, pitch));
        FastMath.sphericalToCartesian(new Vector3f(zoom, azimuth, pitch), cam_coord);
        
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
