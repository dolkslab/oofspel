package gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.List;


public class InterfaceController implements ScreenController {

    private final Interface app;

    public InterfaceController(Interface app) {
        this.app = app;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        
    }

    @NiftyEventSubscriber(id = "sliderMass")
    public void onSliderChanged(final String id, final SliderChangedEvent event){
        app.selectedTarget.mass=app.selectedTarget.mass*event.getValue();
        System.out.println(app.selectedTarget.mass);
    }
    
    
    @NiftyEventSubscriber(id = "hideButton")
    public void onHideButtonClicked(final String id, final ButtonClickedEvent event) {
        app.hide();

    }
    
    @NiftyEventSubscriber(id = "editButton")
    public void onEditButtonClicked(final String id, final ButtonClickedEvent event) {
        app.edit();

    }
    
    public void testClick(){
        System.out.println("you clicked here");
    }
    
    @Override
    public void onStartScreen() {
    }
    
    @Override
    public void onEndScreen() {
    }
}
