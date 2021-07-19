
package MainApp;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
//Classe con le scene e i grafici sulle accelerazioni
public class AccChart{
    Scene scene;
    LineChart<Number,Number> lineChart;
    
    public Scene getScene(){
        return scene;
    }
    public LineChart getLineChart(){
        return lineChart;
    }

    public void setLineChart(LineChart<Number, Number> lineChart) {
        this.lineChart = lineChart;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
