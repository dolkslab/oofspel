package mygame;

import com.jme3.app.SimpleApplication;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main {

    public static void main(String[] args) {
        SimpleApplication app = new Interface();
        app.start();
    }


}
