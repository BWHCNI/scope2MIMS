/*
 * Holder_Ref_Data_App.java
 */

package holder_ref_data;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class Holder_Ref_Data_App extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new Holder_Ref_Data_View(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of Holder_Ref_Data_App
     */
    public static Holder_Ref_Data_App getApplication() {
        return Application.getInstance(Holder_Ref_Data_App.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(Holder_Ref_Data_App.class, args);
    }
}
