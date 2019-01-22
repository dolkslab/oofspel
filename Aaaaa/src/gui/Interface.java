package gui;

import com.jme3.app.SimpleApplication;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;

import state.Mainstate;

public class Interface extends SimpleApplication {
    
    private Nifty nifty;

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
        cam.getLocation();
        inputManager.setCursorVisible(true);
        stateManager.attach(new Mainstate(this));
        
    }
    
     
    
    
    
    public void hide() {
        nifty.gotoScreen("start");

    }
    public void edit() {
        nifty.gotoScreen("select");
    }
    
    public void camFly(boolean enabled){
        System.out.println(enabled);
        flyCam.setEnabled(enabled);
    }
    
}
