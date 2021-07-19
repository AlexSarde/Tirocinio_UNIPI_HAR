
package MainApp;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

//Callable per il pottting dei grafici HeartBeat, NreathRate, Posture
public class HRBRPOS_Worker implements Callable<Object>{
    File file;
    public HRBRPOS_Worker(File file) {
        this.file=file;
    }
    
    @Override
    public Object call() throws Exception {
        
        final NumberAxis PxAxis = new NumberAxis();
        final NumberAxis HxAxis = new NumberAxis();
        final NumberAxis BxAxis = new NumberAxis();
        final NumberAxis PyAxis = new NumberAxis();
        PyAxis.setLabel("Degrees (°)");
        PxAxis.setLabel("Time (s)");
        HxAxis.setLabel("Time (s)");
        BxAxis.setLabel("Time (s)");
        LineChart PostureChart = new LineChart<Number,Number>(PxAxis,PyAxis);
        PostureChart.setTitle("Posture Chart");
        final NumberAxis HyAxis = new NumberAxis();
        HyAxis.setLabel("Beats per minute");
        LineChart HeartbeatChart = new LineChart<Number,Number>(HxAxis,HyAxis);  
        HeartbeatChart.setTitle("Heart beat chart");
        final NumberAxis ByAxis = new NumberAxis();
        ByAxis.setLabel("Breaths per minutes");
        LineChart BreathrateChart = new LineChart<Number,Number>(BxAxis,ByAxis);  
        BreathrateChart.setTitle("Breath rate chart");
        XYChart.Series seriesP = new XYChart.Series();
        seriesP.setName("Posture degrees");
        XYChart.Series seriesH = new XYChart.Series();
        seriesH.setName("Heart beats");
        XYChart.Series seriesB = new XYChart.Series();
        seriesB.setName("Breath rate");
        CSVReader reader;
        //Scarico tutti i dati dal file Accel.csv
        try {
            reader = new CSVReader(new FileReader(file));
            String [] nextLine=reader.readNext();
            double i=0;
            while (((nextLine = reader.readNext())!= null)) {
                double x=Double.valueOf(nextLine[4]);
                seriesP.getData().add(new XYChart.Data(i,x));
                double y=Double.valueOf(nextLine[1]);
                seriesH.getData().add(new XYChart.Data(i,y));
                double z=Double.valueOf(nextLine[2]);
                seriesB.getData().add(new XYChart.Data(i,z));
                //System.out.println(x+" "+y+" "+z);
                i++;
                    
            }
            reader.close();
        } catch (CsvValidationException | IOException e) {
                System.out.print("ECCEZIONE"+e);
                e.printStackTrace();
        }
        Scene PostureScene  = new Scene(PostureChart,800,600);
        PostureChart.getData().add(seriesP);
        Scene HeartbeatScene  = new Scene(HeartbeatChart,800,600);
        HeartbeatChart.getData().add(seriesH);
        Scene BreathrateScene  = new Scene(BreathrateChart,800,600);
        BreathrateChart.getData().add(seriesB);
        HrBrPosChart hrbrpos= new HrBrPosChart();
        hrbrpos.setBreathrateScene(BreathrateScene);
        hrbrpos.setHeartbeatScene(HeartbeatScene);
        hrbrpos.setPostureScene(PostureScene);
        hrbrpos.setBreathrateChart(BreathrateChart);
        hrbrpos.setHeartbeatChart(HeartbeatChart);
        hrbrpos.setPostureChart(PostureChart);
        System.out.println("HRBRPos fatto");
        //Restituisci l'oggetto contenente i grafici e le scene
        return hrbrpos;
    }
    
    
}
