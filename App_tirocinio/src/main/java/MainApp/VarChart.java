
package MainApp;


import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
//Classe con le scene e i grafici sulle varianze
public class VarChart {
    Scene scene;
    Scene Mediascene;
    Scene Medianascene;
    LineChart<Number,Number> VarChart;
    LineChart<Number,Number> MediaChart;
    LineChart<Number,Number> MedianaChart;
    
    public Scene getScene(){
        return scene;
    }
    public Scene getMedianaScene(){
        return Medianascene;
    }
    public Scene getMediaScene(){
        return Mediascene;
    }
    public LineChart getVarChart(){
        return VarChart;
    }
    public LineChart getMediaChart(){
        return MediaChart;
    }
    public LineChart getMedianaChart(){
        return MedianaChart;
    }

    public void setMediaChart(LineChart<Number, Number> MediaChart) {
        this.MediaChart = MediaChart;
    }
    public void setVarChart(LineChart<Number, Number> VarChart) {
        this.VarChart = VarChart;
    }

    public void setMedianaChart(LineChart<Number, Number> MedianaChart) {
        this.MedianaChart = MedianaChart;
    }
    public void setMedianascene(Scene Medianascene) {
        this.Medianascene = Medianascene;
    }          

    public void setMediascene(Scene Mediascene) {
        this.Mediascene = Mediascene;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
}

