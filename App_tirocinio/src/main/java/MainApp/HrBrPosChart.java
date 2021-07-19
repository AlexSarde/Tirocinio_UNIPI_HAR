
package MainApp;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;

//Classe con le scene e i grafici sul battito cardiaco, respiri al minuto e postura
public class HrBrPosChart {
    Scene PostureScene;
    LineChart<Number,Number> PostureChart;
    Scene HeartbeatScene;
    LineChart<Number,Number> HeartbeatChart;
    Scene BreathrateScene;
    LineChart<Number,Number> BreathrateChart;
    //1 HR,2 BR,4 Pos

    public LineChart<Number, Number> getBreathrateChart() {
        return BreathrateChart;
    }

    public Scene getBreathrateScene() {
        return BreathrateScene;
    }

    public LineChart<Number, Number> getHeartbeatChart() {
        return HeartbeatChart;
    }

    public Scene getHeartbeatScene() {
        return HeartbeatScene;
    }

    public LineChart<Number, Number> getPostureChart() {
        return PostureChart;
    }

    public Scene getPostureScene() {
        return PostureScene;
    }
    public void setPostureChart(LineChart p){
        this.PostureChart=p;
    }
    public void setHeartbeatChart(LineChart p){
        this.HeartbeatChart=p;
    }
    public void setBreathrateChart(LineChart p){
        this.BreathrateChart=p;
    }
    public void setBreathrateScene(Scene BreathrateScene) {
        this.BreathrateScene = BreathrateScene;
    }

    public void setHeartbeatScene(Scene HeartbeatScene) {
        this.HeartbeatScene = HeartbeatScene;
    }

    public void setPostureScene(Scene PostureScene) {
        this.PostureScene = PostureScene;
    }
    
    
    
}
