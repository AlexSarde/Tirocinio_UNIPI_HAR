package MainApp;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


//Classe per inizializzare le interfacce

public class App extends Application {
    public File[] files=null;
    ChartClass CC;
    public Label notify;
    Button Clear;
    Button Procedi;
    
    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    private void initUI(Stage stage) {
        HomeClass HC;
        try {
            HC = new HomeClass(stage);
            Scene home = HC.getScene();
            stage.setTitle("Charts viewer");
            stage.setScene(home);
            stage.show(); 
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
         
    }

    public static void main(String[] args) {
        launch(args);
    }    
}

