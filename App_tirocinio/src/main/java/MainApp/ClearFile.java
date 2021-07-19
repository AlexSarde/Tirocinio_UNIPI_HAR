/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainApp;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.stat.StatUtils;

//Classe per la segmentazione dei file Accel.csv e Summary.csv 
public class ClearFile {
    File acc; 
    File sum;
    public class Range implements Comparable {
        public int x;
        public int y;
        public Range(int x, int y){
            this.x=x;
            this.y=y;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }     
        public int dif(){
            return y-x;
        }
        //ridefinisco il metodo compare to per confrontare gli oggetti di tipo range e usare il metodo sort
        @Override
        public int compareTo(Object o)
        {
            if ((o != null) && (o instanceof Range)){
                Range r = (Range) o;
                if(this.getX()<r.getX()){
                    return -1;
                }
                return 1;
            }
            return -1;
        }
    }
    //Metodo che genera i File nuovi tagliati
    public ArrayList generateCuts(File fileacc, File filesum){
        File dir=fileacc.getParentFile(); 
        String name = fileacc.getName().replaceFirst("[.][^.]+$", "");
        String namesum = filesum.getName().replaceFirst("[.][^.]+$", "");
        ArrayList<File> cuts=new ArrayList<File>();
        //utilizzo la funzione cut per ottenere i range dove effettuare i tagli
        ArrayList cut=this.cut(fileacc);
        acc=fileacc;
        sum=filesum;
               
        int k=0;
        int j=0;
        while(j<cut.size()){
            System.out.println(cut.get(j));
            j++;
        }
        j=0;
        //eseguo i tagli finche' ho un range da tagliare
        if(!cut.isEmpty()){
            try {
                CSVReader reader = new CSVReader(new FileReader(fileacc));
                CSVReader readersum = new CSVReader(new FileReader(filesum));
                String [] nextLine;
                String[] headerRecord = reader.readNext();
                String[] headerRecordSum = readersum.readNext();
                java.nio.file.Path newdir=Paths.get(dir.getAbsolutePath()+"\\FileCut");
                Files.createDirectories(newdir);
                for(int i=0; i<cut.size(); i++){
                    //Creo file con lo stesso nome con l'aggiunta di "cut" piu il numero del taglio j
                    Writer writer = Files.newBufferedWriter(Paths.get(dir.getAbsolutePath()+"\\FileCut\\"+name+"_cut"+j+".csv"));
                    cuts.add(new File(dir.getAbsolutePath()+"\\"+name+"_cut"+j+".csv"));
                    CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END
                    );
                    csvWriter.writeNext(headerRecord);
                    //Scrivo  nel file i valori compresi nel range da cut(i) a cut(i+1) 
                    while(k<(int)cut.get(i)){
                        nextLine = reader.readNext();
                        k++;
                    }
                    System.out.println("Comincio a scrivere da "+ k);
                    i++;
                    
                    while(k<(int)cut.get(i)){
                        nextLine = reader.readNext();
                        k++;
                        csvWriter.writeNext(nextLine);
                        writer.flush();
                    }
                    System.out.println("Finisco di scrivere a "+ k);
                    j++;
                }
                k=0;
                j=0;
                for(int i=0; i<cut.size(); i++){
                    //Creo file con lo stesso nome con l'aggiunta di "cut" piu il numero del taglio i
                    Writer writer = Files.newBufferedWriter(Paths.get(dir.getAbsolutePath()+"\\FileCut\\"+namesum+"_cut"+j+".csv"));
                    cuts.add(new File(dir.getAbsolutePath()+"\\"+namesum+"_cut"+j+".csv"));
                    CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END
                    );
                    //Scrivo  nel file i valori compresi nel range da cut(i) a cut(i+1) 
                    // si divide i per 100 perche' si ha una frequenza di 1Hz invece di 100
                    csvWriter.writeNext(headerRecordSum);
                    while(k<(int)cut.get(i)/100){
                        nextLine = readersum.readNext();
                        k++;
                    }
                    System.out.println("Comincio a scrivere da "+ k);
                    i++;
                    while(k<(int)cut.get(i)/100){
                        nextLine = readersum.readNext();
                        k++;
                        csvWriter.writeNext(nextLine);
                        writer.flush();
                    }
                    System.out.println("Finisco a scrivere a "+ k);
                    j++;
                }
            } catch (CsvValidationException | IOException e) {
                    System.out.print("ECCEZIONE"+e);
                    e.printStackTrace();
            }
        }
        else{
            return null;
        }
        return cuts;
    }
    
    public ArrayList cut(File file){
        
        CSVReader reader;
        //Valori della finestra e del valore dello spostamento di questa
        int window=11;
        int range=1;
        ArrayList varX=new ArrayList();
        ArrayList varY=new ArrayList();
        ArrayList varZ=new ArrayList();
        double [] xVal=null;
        double [] yVal=null;
        double [] zVal=null;
        try {
            //Calcolo la media mobile su ogni asse
            reader = new CSVReader(new FileReader(file));
            List<String[]> list=reader.readAll();
            int size= list.size()-1;

            xVal=new double[size];
            yVal=new double[size];
            zVal=new double[size];
            double i=0;
            //immagazzino tutti i valori
            Iterator it =list.iterator();
            it.next();
            while(it.hasNext()) {
                String[] line =(String [])it.next();
                xVal[(int)i]=Double.valueOf(line[1]);
                yVal[(int)i]=Double.valueOf(line[2]);
                zVal[(int)i]=Double.valueOf(line[3]);
                i++;   
            }
            int meansize=((size-window)/range)+1;
            double [] xMeans=new double[meansize];
            double [] yMeans=new double[meansize];
            double [] zMeans=new double[meansize];
            
            //Calcolo la media mobile
            i=0;
            int j=0;
           
            while (j<size-window) {
                double mediax=StatUtils.mean(xVal,j,window);
                double mediay=StatUtils.mean(yVal,j,window);
                double mediaz=StatUtils.mean(zVal,j,window);
                xMeans[(int)i]=mediax;
                yMeans[(int)i]=mediay;
                zMeans[(int)i]=mediaz;
                j=j+range;
                i++;
            }
            //Calcolo la varianza mobile sui valori della media mobile
            range=50;
            window=300;
            j=0;
            i=0;
            while (j<xMeans.length-window) {
                double varx=StatUtils.populationVariance(xMeans,j,window);
                double vary=StatUtils.populationVariance(yMeans,j,window);
                double varz=StatUtils.populationVariance(zMeans,j,window);
                System.out.println("**"+i+ " Vsr X Y Z "+ varx+"*"+vary+"*"+varz+"NUM VAL "+xVal.length );
                varX.add(varx);
                varY.add(vary);
                varZ.add(varz);
                j=j+range;
                i++;
            }
            
        }catch (CsvException | IOException e) {
            System.out.print("ECCEZIONE"+e);
            e.printStackTrace();
        }
        // 3 cicli che mi prendono gli intervalli di riposo per ogni asse
        double i=0;
        ArrayList intX=new ArrayList();
        ArrayList intY=new ArrayList();
        ArrayList intZ=new ArrayList();
        double start=0,end=0;
        //Limit=n, si prendono i riposi di durata n*50/100 secondi
        int limit=4;
        
        int sizeX=varX.size();
        int sizeY=varY.size();
        int sizeZ=varZ.size();
        System.out.println("DURATA VAR X: "+sizeX+"DURATA Mean X: ");
        System.out.println("DURATA VAR Y: "+sizeY+"DURATA Mean Y: ");
        System.out.println("DURATA VAR Z: "+sizeZ+"DURATA Mean Z: ");
        //Mi prendo gli intervalli (start-end) con valore della varianza minore di 100, rappresentano le fasi di riposo
        while(i<varX.size()){
            start=i;
            while(i<varX.size() && (double)varX.get((int)i)<100)i++;
            end=i;
            if((end-start)>limit)intX.add(new Range(150+(int)start*50,150+(int)end*50));
            i++;
        }
        i=0;
        
        while(i<varY.size()){
            start=i;
            while(i<varY.size() && (double)varY.get((int)i)<100)i++;
            end=i;
            if((end-start)>limit) intY.add(new Range(150+(int)start*50,150+(int)end*50));
            i++;
            
        }
        i=0;
        
        while(i<varZ.size()){
            start=i;
                while(i<varZ.size() && (double)varZ.get((int)i)<100) i++;
                end=i;
                if((end-start)>limit) intZ.add(new Range(150+(int)start*50,150+(int)end*50));
                i++;
                
        }
        
        
        //print di controllo
        for(int k=0; k<intX.size(); k++){
            
            Range r=(Range)intX.get(k);
            System.out.println("Range X:"+ r.getX() +"--"+r.getY());
        }
        for(int k=0; k<intY.size(); k++){
            
            Range r=(Range)intY.get(k);
            System.out.println("Range Y:"+ r.getX() +"--"+r.getY());
        }
        for(int k=0; k<intZ.size(); k++){
            
            Range r=(Range)intZ.get(k);
            System.out.println("Range Z:"+ r.getX() +"--"+r.getY());
        }
        ArrayList Pausa =new ArrayList<Range>();
        int k=0;
        ArrayList cuts=new ArrayList();
        //Faccio l'intersezione tra gli intervalli su tutti e tre gli assi, le prendo e le definisco 
        //come fasi dove l'atleta non si allena
        while(!intX.isEmpty() && !intY.isEmpty() && !intZ.isEmpty() ){
            Range rX=(Range)intX.get(0);
            Range rY=(Range)intY.get(0);
            Range rZ=(Range)intZ.get(0);
            System.out.println("PRESI INT:"+ "Range X:"+ rX.getX() +"--"+rX.getY());
            System.out.println("PRESI INT:"+ "Range Y:"+ rY.getX() +"--"+rY.getY());
            System.out.println("PRESI INT:"+ "Range Z:"+ rZ.getX() +"--"+rZ.getY());
            int min=Math.min(rX.getY(),rY.getY());
            int max=Math.max(rX.getX(),rY.getX());
            if(max>min){
                System.out.println("NON trovato "+ max +"  "+ min);
                if(min==rX.getY())
                    intX.remove(rX);
                else
                    intY.remove(rY);
            }
            else{
                Range es=new Range(max, min);
                min=Math.min(es.getY(),rZ.getY());
                max=Math.max(es.getX(),rZ.getX());
                if(max>min){
                    System.out.println("NON trovato "+ max +"  "+ min);
                    if(min==rX.getY())
                        intX.remove(rX);
                    else if(min==rY.getY())
                        intY.remove(rY);
                        else
                            intZ.remove(rZ);
                }
                else{
                    System.out.println("trovato"+ max +"  "+ min);
                    Range newpa=new Range(max,min);
                    if(min==rX.getY())
                        intX.remove(rX);
                    else if(min==rY.getY())
                        intY.remove(rY);
                        else
                            intZ.remove(rZ);
                    Pausa.add(newpa);
                }
            }
        }
       
        //metto tutti i possibili esercizi in Cuts
        //prendendo gli intervalli dove non c'e' riposo
        for(k=0;k<Pausa.size()-1;k++){
            Range es1=(Range)Pausa.get(k);
            Range es2=(Range)Pausa.get(k+1);
            cuts.add(new Range(es1.getY(),es2.getX()));
            
            
            System.out.println("ES "+k+" da+es.getY() "+es1.getY()+" " +es2.getX());
        }
        
        //Per ogni range in cuts, calcolo le medie e li confronto con tutti gli altri, se sono simili li aggiungo a un array esercizi e li tolgo da cuts
        i=0;
        k=0;
        ArrayList exerc=new ArrayList();
        int size=cuts.size();
        //Caratteristiche da usare per confrontare i possibili esercizi tra loro
        List<Double> MX=new ArrayList<Double>();
        List<Double> MY=new ArrayList<Double>();
        List<Double> MZ=new ArrayList<Double>();
        List<Double> VX=new ArrayList<Double>();
        List<Double> VY=new ArrayList<Double>();
        List<Double> VZ=new ArrayList<Double>();
        //Calcolo delle caratteristiche
        while(k<cuts.size()){
            Range r1=(Range)cuts.get((int)k);
            int init=r1.getX();
            int fin=r1.getY();
            
            double mediax=StatUtils.mean(xVal, init, fin-init);
            double mediay=StatUtils.mean(yVal, init, fin-init);
            double mediaz=StatUtils.mean(zVal, init, fin-init);
            double varx=StatUtils.populationVariance(xVal, init, fin-init);
            double vary=StatUtils.populationVariance(yVal, init, fin-init);
            double varz=StatUtils.populationVariance(zVal, init, fin-init);
            MX.add(mediax);
            MY.add(mediay);
            MZ.add(mediaz);
            VX.add(varx);
            VY.add(vary);
            VZ.add(varz);
            System.out.println("MEDIAX : "+ mediax+"MEDIAY : "+mediay+"MEDIAZ : "+mediaz);
            System.out.println("Var X : "+ varx+"Var Y : "+vary+"Var Z : "+varz);
            k++;
                    
        }
        //Confronto ogni esercizio con tutti gli altri, se ne trovo due simili li prendo come esercizi e cosi via
        ArrayList ex=new ArrayList();
        k=0;
        int j=1;
        int count =0;
        while(k<cuts.size()-1){
            ex.add(cuts.get(k));
            while(j<cuts.size()){
                System.out.println("Confronto "+k+" con "+j);
                System.out.println("MEDIAX : "+ MX.get(k)+" "+MX.get(j)+" MEDIAY : "+ MY.get(k)+" "+MY.get(j)+"MEDIAZ : "+ MZ.get(k)+" "+MZ.get(j));
                System.out.println("Var X : "+ VX.get(k)+"*"+VX.get(j)+"Var Y : "+VY.get(k)+"*"+VY.get(j)+"Var Z : "+VZ.get(k)+"*"+VZ.get(j));
                if(Math.sqrt(Math.pow(MX.get(k)-MX.get(j), 2))<15){
                    if(Math.sqrt(Math.pow(MY.get(k)-MY.get(j), 2))<15){
                        if(Math.sqrt(Math.pow(MZ.get(k)-MZ.get(j), 2))<15){
                            if((VX.get(k)>VX.get(j) && VX.get(k)/VX.get(j)<=2) || (VX.get(j)>VX.get(k) && VX.get(j)/VX.get(k)<=2)){
                                if((VY.get(k)>VY.get(j) && VY.get(k)/VY.get(j)<=2) || (VY.get(j)>VY.get(k) && VY.get(j)/VY.get(k)<=2)){
                                    if((VZ.get(k)>VZ.get(j) && VZ.get(k)/VZ.get(j)<=2) || (VZ.get(j)>VZ.get(k) && VZ.get(j)/VZ.get(k)<=2)){
                                        System.out.println("PRESO "+j+ "range "+ ((Range)cuts.get(j)).getX()+ "_"+ ((Range)cuts.get(j)).getY());
                                        Range nuovo=new Range(((Range)cuts.get(j)).getX(),((Range)cuts.get(j)).getY());
                                        ex.add(nuovo);
                                        cuts.remove(j);
                                        MX.remove(j);
                                        MY.remove(j);
                                        MZ.remove(j); 
                                        VX.remove(j);
                                        VY.remove(j);
                                        VZ.remove(j);
                                        count++;
                                        
                                    }else{
                                        System.out.println("Var X : "+ VX.get(k)+"*"+VX.get(j)+"Var Y : "+VY.get(k)+"*"+VY.get(j)+"Var Z : "+VZ.get(k)+"*"+VZ.get(j));
                                        j++;
                                    }
                                }else{
                                    System.out.println("Var X : "+ VX.get(k)+"*"+VX.get(j)+"Var Y : "+VY.get(k)+"*"+VY.get(j)+"Var Z : "+VZ.get(k)+"*"+VZ.get(j));
                                    j++;
                                }
                            } else{
                                System.out.println("Var X : "+ VX.get(k)+"*"+VX.get(j)+"Var Y : "+VY.get(k)+"*"+VY.get(j)+"Var Z : "+VZ.get(k)+"*"+VZ.get(j));
                                j++;
                            }
                        }else j++;
                    }else j++;
                }else j++;

            }
            if(count==0) ex.remove(cuts.get(k));
            
            count=0;
            k++;
            j=k+1;
        }
        k=0;
        Collections.sort(ex);
        while(k<ex.size()){
            Range r1=(Range)ex.get((int)k);
            int init=r1.getX();
            int fin=r1.getY();
            System.out.println("es: "+init +"--"+fin);
            exerc.add(init);
            exerc.add(fin);
            k++;
        }
        //Restituisci i Range di tutti gli esercizi trovati
        return exerc; 
    }
    
        
}
