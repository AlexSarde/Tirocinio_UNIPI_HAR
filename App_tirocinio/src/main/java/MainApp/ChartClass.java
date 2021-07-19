
package MainApp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author Alessandro
 */
public class ChartClass {
    public Scene scene;
    File acc;
    File sum;
    CutManually CM;
    private ExecutorService tp;
    public ChartClass(Stage stage, File[] files, Scene homeScene)throws InterruptedException,ExecutionException{
        
        //CREA tutti i grafici
        if(files[0].getName().contains("Accel")){
            this.acc=files[0];
            this.sum=files[1];
        }
        else{
            this.acc=files[1];
            this.sum=files[0];
        }
        AccChart A;
        HrBrPosChart H;
        VarChart V;
        //implementazione multithread di Callable che fa acc, hrbrpos e var e restituiscono un oggetto contenente le scene dei grafici
        tp=Executors.newCachedThreadPool ( );
        String name = acc.getName().replaceFirst("[.][^.]+$", "");
        //Thread che si occupa dei grafici su accelerazione grezzi
        ACC_Worker wk1= new ACC_Worker(acc);
        Future<Object> accs =tp.submit(wk1);
        //Thread che si occupa dei grafici su HR BR POS
        HRBRPOS_Worker wk2= new HRBRPOS_Worker(sum);
        Future<Object> HBP =tp.submit(wk2);
        //Thread che si occupa dei grafici sui dati dell'accelerazione manipolati
        VAR_Worker wk3=new VAR_Worker(acc);
        Future<Object> VAR =tp.submit(wk3);
        // Classe per la segmentazione manuale del file
        CM= new CutManually(acc,sum);
        A=(AccChart)accs.get();
        H=(HrBrPosChart)HBP.get();
        V=(VarChart)VAR.get();
        //Fare future.get per vedere quando sono finiti
        GridPane  root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.BASELINE_LEFT);
        //Pulsante Per grafico accelerazione
        Button AccChart = new Button("Accelerazione sugli assi");
        AccChart.setOnAction((ActionEvent event) -> {
            Stage sta = new Stage();
            sta.setScene(A.getScene());
            sta.show();
        });
        Label lab=new Label("Inserisci i secondi di inizio e fine per tagliare il file");
        TextField inizio=new TextField("Inizio");
        TextField fine=new TextField("Fine");
        //Pulsante Per tagliare manualmente il file
        Button Cut = new Button("Taglia file");
        Cut.setOnAction((ActionEvent event) -> {
            try{
                int in=Integer.parseInt(inizio.getCharacters().toString());
                int fin=Integer.parseInt(fine.getCharacters().toString());
                CM.cut(in,fin);
            }
            catch(NumberFormatException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hai immesso valori non compatibili");
                alert.showAndWait();
            }
            
            
        });
        //Pulsante Per grafico sui dati del battito cardiaco
        Button HBChart = new Button("Battiti al minuto");
        HBChart.setOnAction((ActionEvent event) -> {
            Stage sta = new Stage();
            sta.setScene(H.getHeartbeatScene());
            sta.show();
        });
        //Pulsante Per grafico sui dati dei respiri al minuto
        Button BRChart = new Button("Respiri al minuto");
        BRChart.setOnAction((ActionEvent event) -> {
            Stage sta = new Stage();
            sta.setScene(H.getBreathrateScene());
            sta.show();
        });
        //Pulsante Per grafico sui dati della postura
        Button Posture = new Button("Grafico Postura");
        Posture.setOnAction((ActionEvent event) -> {
            Stage sta = new Stage();
            sta.setScene(H.getPostureScene());
            sta.show();
        });
        //Pulsante per grafico sulle varianze dei valori dell'accelerazione
        Button varChart = new Button("Grafici Varianze");
        varChart.setOnAction((ActionEvent event) -> {  
            Stage sta = new Stage();
            sta.setScene(V.getScene());
            sta.show();
        });
        //Pulsante per grafico sulle medie dei valori dell'accelerazione
        Button MediaChart = new Button("Grafici Media Mobile");
        MediaChart.setOnAction((ActionEvent event) -> {  
            Stage sta = new Stage();
            sta.setScene(V.getMediaScene());
            sta.show();
        });
        //Pulsante per grafico sulle mediane dei valori dell'accelerazione
        Button MedianaChart = new Button("Grafici Mediana Mobile");
        MedianaChart.setOnAction((ActionEvent event) -> {  
            Stage sta = new Stage();
            sta.setScene(V.getMedianaScene());
            sta.show();
        });
        //Pulsante per tornare alla schermata home
        Button Indietro = new Button("Torna alla selezione file");
        Indietro.setOnAction((ActionEvent event) -> {  
            stage.setScene(homeScene);
        });
        //Pulsante per salvare grafici in formato png nella cartella di origine dei file Accel e Summary
        Button Salva = new Button("Salva grafici");
        Salva.setOnAction((ActionEvent event) -> {   
            System.out.print(acc.getParent());
            String dirpath=acc.getParent();
            try {
                java.nio.file.Path newdir=Paths.get(dirpath+"\\"+name+"_Charts\\");
                Files.createDirectories(newdir);
                Scene ch=A.getScene();
                Scene chBR= H.getBreathrateScene();
                Scene chHR= H.getHeartbeatScene();
                Scene chPO= H.getPostureScene();
                Scene chVar= V.getScene();
                Scene chMedian= V.getMedianaScene();
                Scene chMean= V.getMediaScene();
                WritableImage image1 = ch.snapshot(null);
                WritableImage image2 = chBR.snapshot(null);
                WritableImage image3 = chHR.snapshot(null);
                WritableImage image4 = chPO.snapshot(null);
                WritableImage image5 = chVar.snapshot(null); 
                WritableImage image6 = chMedian.snapshot(null);
                WritableImage image7 = chMean.snapshot(null);
               
                
                File file1 = new File(dirpath+"\\"+name+"_Charts\\"+"\\ACC3axisCH.png");
                File file2 = new File(dirpath+"\\"+name+"_Charts\\"+"\\BreathrateCH.png");
                File file3 = new File(dirpath+"\\"+name+"_Charts\\"+"\\HeartbeatCH.png");
                File file4 = new File(dirpath+"\\"+name+"_Charts\\"+"\\PostureCH.png");
                File file5 = new File(dirpath+"\\"+name+"_Charts\\"+"\\VarianceCH.png");
                File file6 = new File(dirpath+"\\"+name+"_Charts\\"+"\\MedianCH.png");
                File file7 = new File(dirpath+"\\"+name+"_Charts\\"+"\\MeanCH.png");
                ImageIO.write(SwingFXUtils.fromFXImage(image1, null), "PNG", file1);
                ImageIO.write(SwingFXUtils.fromFXImage(image2, null), "PNG", file2);
                ImageIO.write(SwingFXUtils.fromFXImage(image3, null), "PNG", file3);
                ImageIO.write(SwingFXUtils.fromFXImage(image4, null), "PNG", file4);
                ImageIO.write(SwingFXUtils.fromFXImage(image5, null), "PNG", file5);
                ImageIO.write(SwingFXUtils.fromFXImage(image6, null), "PNG", file6);
                ImageIO.write(SwingFXUtils.fromFXImage(image7, null), "PNG", file7);
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Le immagini sono state salvate correttamente");
                alert.showAndWait();
            }catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Qualcosa e' andato storto");
                alert.showAndWait();
            }
        });
        //Layout scena
        AccChart.setPrefSize(200, 24);
        HBChart.setPrefSize(200, 24);
        BRChart.setPrefSize(200, 24);
        Posture.setPrefSize(200, 24);
        varChart.setPrefSize(200, 24);
        MediaChart.setPrefSize(200, 24);
        MedianaChart.setPrefSize(200, 24);
        Indietro.setPrefSize(200, 24);
        Salva.setPrefSize(200, 24);
        lab.setPrefSize(400, 24);
        inizio.setPrefSize(50, 24);
        fine.setPrefSize(50, 24);
        root.add(AccChart, 0, 0,1,1);
        root.add(HBChart, 0, 1,1,1);
        root.add(BRChart, 0, 2,1,1);
        root.add(Posture, 0, 3,1,1);
        root.add(varChart, 0, 4,1,1);
        root.add(MedianaChart, 0, 5,1,1);
        root.add(MediaChart, 0, 6,1,1);
        root.add(Indietro, 0, 15,1,1);
        root.add(Salva, 10, 15,3,1);
        root.add(lab, 10, 0,3,1);
        root.add(inizio, 10, 1);
        root.add(fine, 11, 1);
        root.add(Cut, 12, 1);
        
        var scene = new Scene(root, 600, 400); 
        
        
        this.scene=scene;
    }
    public Scene getScene(){
        return scene;
    }
}
   
