
package MainApp;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

//Classe per la creazione dei file per librerie di Machine Learning
public class generateARFF {
    File dir;
    public generateARFF(File dir){
        this.dir= dir;
        File newcsv=new File(dir.getAbsolutePath()+"\\fileAARF"+".csv");
        File [] files=dir.listFiles();
        
        //crea file csv e inserisci header meanX, meanY, ...
        String [] headerRecord= {"meanX", "meanY","meanZ","meanHB","meanBR","MeanPos","Xzero","Yzero","Zzero","VarX","VarY","VarZ","Time","maxPos","minPos","maxX","minX","maxY","minY","maxZ","minZ","Class"};
        try{
            //Creo file CSV
            Writer writer = Files.newBufferedWriter(Paths.get(dir.getAbsolutePath()+"\\fileAARF"+".csv"));
            
            CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
            );
            csvWriter.writeNext(headerRecord);
        
            //Seleziono tutti file Accel.csv e Summer.csv e li memorizzo
            for(File file: files){
                File[] accs = file.listFiles(new FilenameFilter() {		
                    //apply a filter
                    @Override
                    public boolean accept(File dir, String name) {
                        boolean result;
                        if(name.contains("Accel")&& name.contains(".csv")){
                            result=true;
                        }
                        else{
                            result=false;
                        }
                        return result;
                    }
                });
                File[] sums = file.listFiles(new FilenameFilter() {		
                    //apply a filter
                    @Override
                    public boolean accept(File dir, String name) {
                        boolean result;
                        if(name.contains("Summary") && name.contains(".csv") ){
                            result=true;
                        }
                        else{
                            result=false;
                        }
                        return result;
                    }
                });
                //Controllo che esista almeno un file
                //E procedo al calcolo delle feature per ognuno di questi
                if(accs!=null && accs.length!=0){
                    for(int i=0; i< accs.length; i++){
                        String es;
                        if(file.getName().compareTo("Test")==0)
                            es="?";
                        else
                            es=file.getName();
                        CalcFeatures cf=new CalcFeatures(accs[i],sums[i],es);
                        String [] line=cf.getFeatures();
                        csvWriter.writeNext(line);
                        writer.flush();
                    }
                }
            }
            //Genero il file ARFF per weka
            writer.close();
            CSVLoader loader = new CSVLoader();
            loader.setSource(newcsv);
            Instances data = loader.getDataSet();
            
            // save ARFF
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(dir.getAbsolutePath()+"\\fileAARF"+".arff"));
            //saver.setDestination(new File(dir.getAbsolutePath()+"\\fileAARF"+".arff"));
            saver.writeBatch();
        }catch(IOException e){
            System.out.println(e);
        }
    }   
}
