package gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class InterfaceController implements ScreenController {

    private final Interface app;
    
    
    

    public InterfaceController(Interface app) {
        this.app = app;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        if(app.nifty.getScreen("edit") == screen){
        app.sliders[0] = screen.findNiftyControl("sliderMass", Slider.class);
        app.sliders[1] = screen.findNiftyControl("sliderVel", Slider.class);
        app.sliders[2] = screen.findNiftyControl("sliderTmscl", Slider.class);
        app.text_fields[0] = screen.findNiftyControl("inputMass", TextField.class);
        app.text_fields[1] = screen.findNiftyControl("inputVel", TextField.class);
        app.text_fields[2] = screen.findNiftyControl("inputTmscl", TextField.class);
        app.label_target_name = screen.findNiftyControl("targetName", Label.class);
        //app.update_target_values();
        }
        
        
    }

    @NiftyEventSubscriber(id = "hideButton")
    public void onHideButtonClicked(final String id, final ButtonClickedEvent event) {
        app.hide();

    }
    
    @NiftyEventSubscriber(id = "editButton")
    public void onEditButtonClicked(final String id, final ButtonClickedEvent event) {
        
        app.edit();
        bind(app.nifty, app.nifty.getScreen("edit"));
        app.update_target_values();

    }
    

    public void onRelease(){
        
        for(Slider slider:app.sliders){
            if(slider.getValue() != 0)
                slider.setValue(0);
        }
        
    }
    
    
   
    
    
    @Override
    public void onStartScreen() {
    }
    
    @Override
    public void onEndScreen() {
        }
}
