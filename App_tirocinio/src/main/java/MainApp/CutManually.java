/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainApp;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author Alessandro
 */
public class CutManually {
    File acc;
    File sum;
    int in;
    int fin;
    public CutManually(File acc, File sum ) {
        this.acc = acc;
        this.sum = sum;
        
    }
    public void cut(int in, int fin){
        this.in=in;
        this.fin=fin;
        try {
            CSVReader reader = new CSVReader(new FileReader(sum));
            List<String[]> list=reader.readAll();
            String [] nextLine;
            Iterator it =list.iterator();
            String name = acc.getName().replaceFirst("[.][^.]+$", "");
            String namesum = sum.getName().replaceFirst("[.][^.]+$", "");
            String[] headerRecord = (String [])it.next();
            java.nio.file.Path newdir=Paths.get(acc.getParentFile().getAbsolutePath()+"\\FileCut"+name);
            Files.createDirectories(newdir);
            int size= list.size()-1; 
            
            if(size>in && in<fin){
                int i=0;
                File fi=new File(sum.getParentFile().getAbsolutePath()+"\\FileCut"+name+"\\"+namesum+"_cut"+i+".csv");
                while(fi.exists()){
                    i++;
                    fi=new File(sum.getParentFile().getAbsolutePath()+"\\FileCut"+name+"\\"+namesum+"_cut"+i+".csv");
                }
                Writer writer = Files.newBufferedWriter(Paths.get(sum.getParentFile().getAbsolutePath()+"\\FileCut"+name+"\\"+namesum+"_cut"+i+".csv"));
                CSVWriter csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
                );
                int k=0;
                while(k<in){
                    String[] line =(String [])it.next();
                    k++;
                }
                csvWriter.writeNext(headerRecord);
                while(k<fin && it.hasNext()){
                    String[] line=(String [])it.next();
                    k++;
                    csvWriter.writeNext(line);
                    writer.flush();  
                }
                reader = new CSVReader(new FileReader(acc));
                list=reader.readAll();
                it =list.iterator();
                headerRecord = (String [])it.next();
                writer = Files.newBufferedWriter(Paths.get(sum.getParentFile().getAbsolutePath()+"\\FileCut"+name+"\\"+name+"_cut"+i+".csv"));
                csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END
                );
                in=in*100;
                fin=fin*100;
                k=0;
                while(k<in){
                    String[] line =(String [])it.next();
                    k++;
                }
                csvWriter.writeNext(headerRecord);
                while(k<fin && it.hasNext()){
                    String[] line=(String [])it.next();
                    k++;
                    csvWriter.writeNext(line);
                    writer.flush();
                    
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Valori non compatibili con la durata della sessione");
                alert.showAndWait();
            }
            
        }
        catch (CsvException | IOException e) {
            System.out.print("ECCEZIONE"+e);
            e.printStackTrace();
        }
    }
    
    
    
}
