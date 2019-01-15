package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import java.util.HashMap;
import java.util.Map;
import state.teststate1;

public class Interface extends SimpleApplication {
    
    private Nifty nifty;
    Material selectedColor;
    Map<String, ColorRGBA> colorSelections = new HashMap<String, ColorRGBA>();
    @Override
    public void simpleInitApp() {        
        colorSelections.put("Red", ColorRGBA.Red);
        colorSelections.put("Blue", ColorRGBA.Blue);
        colorSelections.put("Yellow", ColorRGBA.Yellow);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort, 2048, 2048);
        nifty = niftyDisplay.getNifty();
        InterfaceController controller = new InterfaceController(this);
        nifty.fromXml("Interface/gui.xml", "select", controller);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setEnabled(false);
        inputManager.setCursorVisible(true);
        stateManager.attach(new teststate1(this));
    }
    
    public void doneSelecting() {
        nifty.gotoScreen("start");
        flyCam.setEnabled(true);
        inputManager.setCursorVisible(false);
    }
    
    public void colorSelected(String color) {
       selectedColor.setColor("Color", colorSelections.get(color)); 
       System.out.println("oof");
    }
    
}
