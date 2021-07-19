
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
//Callable per il pottting dei grafici della varianza
public class ACC_Worker implements Callable<Object>{
    File file;
    public ACC_Worker(File file) {
        this.file = file;
    }

    @Override
    public Object call() throws Exception {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        LineChart lineChart = new LineChart<Number,Number>(xAxis,yAxis);
       
        lineChart.setTitle("Acceleration on 3 axis");
        XYChart.Series seriesX = new XYChart.Series();
        seriesX.setName("Acc on x");
        XYChart.Series seriesY = new XYChart.Series();
        seriesY.setName("Acc on Y");
        XYChart.Series seriesZ = new XYChart.Series();
        seriesZ.setName("Acc on Z");
        CSVReader reader;
        try {
            //Plot dei dati grezzi sull'accelerazione sui tre assi
            reader = new CSVReader(new FileReader(file));
            String [] nextLine= reader.readNext();
            double i=0;
            while (((nextLine = reader.readNext())!= null)) {
                
                double x=Double.valueOf(nextLine[1])-2048;
                seriesX.getData().add(new XYChart.Data(i/100,x));
                double y=Double.valueOf(nextLine[2])-2048;
                seriesY.getData().add(new XYChart.Data(i/100,y));
                double z=Double.valueOf(nextLine[3])-2048;
                seriesZ.getData().add(new XYChart.Data(i/100,z));
                System.out.println("*"+i+"* "+x+" "+y+" "+z);
                i++;
            }
            reader.close();
        } catch (CsvValidationException | IOException e) {
                System.out.print("ECCEZIONE"+e);
                e.printStackTrace();
        }
        Scene scene  = new Scene(lineChart,800,600);
        
        lineChart.getData().addAll(seriesX,seriesY,seriesZ);
        lineChart.setCreateSymbols(false);
        seriesX.getNode().setStyle("-fx-stroke-width: 1px; -fx-effect: null;");
        seriesY.getNode().setStyle("-fx-stroke-width: 1px; -fx-effect: null;");
        seriesZ.getNode().setStyle("-fx-stroke-width: 1px; -fx-effect: null;");
        AccChart acc=new AccChart();
        acc.setLineChart(lineChart);
        acc.setScene(scene);
        System.out.println("ACC fatto");
        return acc;
    }   
}
