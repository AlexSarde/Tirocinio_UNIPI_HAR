
package MainApp;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.stat.StatUtils;

//Classe Per il calcolo delle feature per i file di input per modelli di Machine Learning
public class CalcFeatures {
    
    File acc;
    File sum;
    String es;
    String[] line;
    int Xzero=0;
    int Yzero=0;
    int Zzero=0;
    
    public CalcFeatures(File a, File s,String es){
        this.acc=a;
        this.sum=s;
        CSVReader reader;
        double time=0;
        double meanX=0;
        double meanY=0;
        double meanZ=0;
        double varX=0;
        double varY=0;
        double varZ=0;
        double meanHB=0;
        double meanBR=0;
        double meanPos=0;
        double maxPos=-99999;
        double minPos=99999;
        double maxX=-99999;
        double maxY=-99999;
        double maxZ=-99999;
        double minX=99999;
        double minY=99999;
        double minZ=99999;
        try{
            reader = new CSVReader(new FileReader(acc));

            List<String[]> list=reader.readAll();
            int size= list.size()-1;

            double [] xVal=new double[size];
            double [] yVal=new double[size];
            double [] zVal=new double[size];


            int i=0;
            Iterator it =list.iterator();
            it.next();
            //memorizzo tutti i valori dell'accelerazione
            //e calcolo massimo e minimo dei dati dell'accelerazione su tutti e tre gli assi
            while(it.hasNext()) {
                String[] line =(String [])it.next();
                xVal[i]=Double.valueOf(line[1]);
                yVal[i]=Double.valueOf(line[2]);
                zVal[i]=Double.valueOf(line[3]);
                if(xVal[i]>maxX) maxX=xVal[i];
                else if(xVal[i]<minX) minX=xVal[i];
                if(yVal[i]>maxY) maxY=yVal[i];
                else if(yVal[i]<minY) minY=yVal[i];
                if(zVal[i]>maxZ) maxZ=zVal[i];
                else if(zVal[i]<minZ) minZ=zVal[i];
                i++;   
            }


            time=(double)i/100;
            //Calcolo la media e la varianza sui dati memorizzati in precedenza
            meanX=StatUtils.mean(xVal);
            meanY=StatUtils.mean(yVal);
            meanZ=StatUtils.mean(zVal);
            calcZeros();
            varX=StatUtils.populationVariance(xVal);
            varY=StatUtils.populationVariance(yVal);
            varZ=StatUtils.populationVariance(zVal);
            System.out.println("X: "+meanX+" Y: "+meanY+" Z: "+meanZ+" ");

        } catch (CsvException | IOException e) {
                System.out.print("ECCEZIONE"+e);
                e.printStackTrace();
        }
        try{
            
            reader = new CSVReader(new FileReader(sum));
            List<String[]> list=reader.readAll();
            int size= list.size()-1;
            double [] HBVal=new double[size];
            double [] BRVal=new double[size];
            double [] PosVal=new double[size];
            int i=0;
            Iterator it =list.iterator();
            it.next();
            //immagazzino tutti i dati relativi al battito cardiaco, respiri al minuto e postura
            while(it.hasNext()){
                String[] line =(String [])it.next();
                double hb=Double.valueOf(line[1]);
                if(hb<50) hb=65;
                HBVal[i]=hb;
                BRVal[i]=Double.valueOf(line[2]);
                PosVal[i]=Double.valueOf(line[4]);
                if(PosVal[i]>maxPos) maxPos=PosVal[i];
                    else if(PosVal[i]<minPos) minPos=PosVal[i];
                i++;
            }
            //Calcolo media HeartBeat, Breath Rate, Posture values
            meanHB=StatUtils.mean(HBVal);
            meanBR=StatUtils.mean(BRVal);
            meanPos=StatUtils.mean(PosVal);
            System.out.println("HB: "+meanHB+" BR: "+meanBR+" Pos: "+meanPos);
                
        } catch (CsvException | IOException e) {
            System.out.print("ECCEZIONE"+e);
            e.printStackTrace();
        }
        String[] result={String.valueOf(meanX),
                        String.valueOf(meanY),
                        String.valueOf(meanZ),
                        String.valueOf(meanHB),
                        String.valueOf(meanBR),
                        String.valueOf(meanPos),
                        String.valueOf(Xzero),
                        String.valueOf(Yzero),
                        String.valueOf(Zzero),
                        String.valueOf(varX),
                        String.valueOf(varY),
                        String.valueOf(varZ),
                        String.valueOf(time),
                        String.valueOf(maxPos),
                        String.valueOf(minPos),
                        String.valueOf(maxX),
                        String.valueOf(minX),
                        String.valueOf(maxY),
                        String.valueOf(minY),
                        String.valueOf(maxZ),
                        String.valueOf(minZ),
                        es};
        for(int j=0; j<result.length;j++){
            System.out.print(result[j]+"  ");
        }
        System.out.println();
        this.line=result;
    }
    public void calcZeros(){
        CSVReader reader;
        int window=11;
        int range=1;
        ArrayList meansX=new ArrayList();
        ArrayList meansY=new ArrayList();
        ArrayList meansZ=new ArrayList();
        double meanX=0;
        double meanY=0;
        double meanZ=0;
        double fix=0;//window/range/2;
        try {
            reader = new CSVReader(new FileReader(acc));
            
            String [] nextLine= reader.readNext();
            
            double [] Xdata= new double[window];            
            double [] Ydata= new double[window];
            double [] Zdata= new double[window];
          
            double i=0;
            int j=0;
            double maxMeanX=-99999;
            double minMeanX=99999;
            double maxMeanY=-99999;
            double minMeanY=99999;
            double maxMeanZ=-99999;
            double minMeanZ=99999;
            
            //riempire la finestra la prima volta
            if(nextLine!=null) {
                while (((nextLine = reader.readNext())!= null && j<window)) {
                        double x=Double.valueOf(nextLine[1]);
                        double y=Double.valueOf(nextLine[2]);
                        double z=Double.valueOf(nextLine[3]);
                        
                        Xdata[j]=x;
                        Ydata[j]=y;
                        Zdata[j]=z;					
                        j++;
                        i++;
                }
            }
            //Calcolo primo punto delle medie
            meanX=StatUtils.mean(Xdata);
            meanY=StatUtils.mean(Ydata);
            meanZ=StatUtils.mean(Zdata);
            meansX.add(meanX);
            meansY.add(meanY);
            meansZ.add(meanZ);
            //aggiorno il valore dei massimi e minimi delle medie
            maxMeanX=meanX;
            minMeanX=meanX;
            maxMeanY=meanY;
            minMeanY=meanY;
            maxMeanZ=meanZ;
            minMeanZ=meanZ;
            
            //calcola i restanti punti delle medie
            while(nextLine!=null) {
                j=0;
                
                while (((nextLine = reader.readNext())!= null && j<range)) {
                        double x=Double.valueOf(nextLine[1]);
                        double y=Double.valueOf(nextLine[2]);
                        double z=Double.valueOf(nextLine[3]);
                        Xdata[(int)(i%window)]=x;
                        Ydata[(int)(i%window)]=y;
                        Zdata[(int)(i%window)]=z;					
                        j++;
                        i++;
                }
                //Aggiorno i valori massimi e minimi delle medie
                double temp;
                temp=StatUtils.mean(Xdata);
                if(temp>maxMeanX) maxMeanX=temp;
                else if(temp<minMeanX) minMeanX=temp;
                meanX=meanX+temp;
                meansX.add(temp);
                temp=StatUtils.mean(Ydata);
                if(temp>maxMeanY) maxMeanY=temp;
                else if(temp<minMeanY) minMeanY=temp;
                meanY=meanY+temp;
                meansY.add(temp);
                temp=StatUtils.mean(Zdata);
                if(temp>maxMeanZ) maxMeanZ=temp;
                else if(temp<minMeanZ) minMeanZ=temp;
                meanZ=meanZ+temp;
                meansZ.add(temp);
                
            }
            //Prendo un punto di mezzo tra il massimo e il minimo che traccera la linea con la quale calcolare i passaggi per lo 0
            meanX=(maxMeanX+minMeanX)/2;
            meanY=(maxMeanY+minMeanY)/2;
            meanZ=(maxMeanZ+minMeanZ)/2;
        }catch(IOException | CsvValidationException e){
            System.out.print(e);
        }
        System.out.println("Media X:"+ meanX+ " Media Y: " + meanY +" Media X: " + meanZ);
        //Calcolo il numero dei passaggi per lo zero dei grafici della media
        for (int j = 0; j < meansX.size(); j++) {    
            meansX.set(j, (double)meansX.get(j)-meanX);
            meansY.set(j, (double)meansY.get(j)-meanY);
            meansZ.set(j, (double)meansZ.get(j)-meanZ);
            if(j>0){
                if((double)meansX.get(j)*(double)meansX.get(j-1)<0) Xzero++;
                if((double)meansY.get(j)*(double)meansY.get(j-1)<0) Yzero++;
                if((double)meansZ.get(j)*(double)meansZ.get(j-1)<0) Zzero++;
            }
        }
        System.out.println("FINE FUNZ");

    }
    public String[] getFeatures(){
        return line;
    }
         
}
