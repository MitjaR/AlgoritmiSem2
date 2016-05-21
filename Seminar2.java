import java.util.ArrayList;
import java.util.List;

public class Seminar2 {

    public class TockaIndeks{
        public final double x;
        public final double y;
        public final int indeks;
        
        public TockaIndeks(double[] tocka, int indeks){
            this.x=tocka[0];
            this.y=tocka[1];
            this.indeks=indeks;
        }
    }
    
    private TockaIndeks najdiNajboljLev(List<TockaIndeks> sezL) {
        double xMin;
        int tInd=0;
        {
            TockaIndeks tmp=sezL.get(0);
            xMin=tmp.x;
        }
        for (int i=0;i<sezL.size();i+=1){
            TockaIndeks tI=sezL.get(i);
            if (tI.x<xMin){
                xMin=tI.x;
                tInd=i;
            }
        }
        //remove je lahko dokaj draga operacija O(n)
        //vendar visje je manj je draga
        //tako ali tako pa je preostali del O(n)
        return sezL.remove(tInd);
    }

    private TockaIndeks najdiNajboljDes(List<TockaIndeks> sezD) {
        double xMax;
        int tInd=0;
        {
            TockaIndeks tmp=sezD.get(0);
            xMax=tmp.x;
        }
        for (int i=0;i<sezD.size();i+=1){
            TockaIndeks tI=sezD.get(i);
            if (tI.x>xMax){
                xMax=tI.x;
                tInd=i;
            }
        }
        return sezD.remove(tInd);
    }

    //bisekcija po urejeni komponeti y, vrne i da sez[i]<y
    private int bisekcija(List<TockaIndeks> sez, double y) {
        int a=0;
        int b=sez.size();
        while (b-a>8){
            int c=(b+a)/2;
            if (sez.get(c).y<y){
                a=c;
            }
            else{
                b=c;
            }
        }
        return a;
    }
    
    //poisce naslednjo tocko za pokrov (del ovojnice jarvis march iz dveh strani)
    private boolean poisciNaslednjo(List<TockaIndeks> sezL, List<TockaIndeks> sezD, List<TockaIndeks> pokLev, List<TockaIndeks> pokDes) {
        double x1=pokLev.get(pokLev.size()-1).x;
        double y1=pokLev.get(pokLev.size()-1).y;
        
        double x2=pokDes.get(pokDes.size()-1).x;
        double y2=pokDes.get(pokDes.size()-1).y;
        
        //yZv je zacetna vrednost, presecise premice skrajnih tock z y osjo
        //za boljso ucinkovitost se lahko to se prestavi (hitrejsa konvergenca)
        double yZv=y1-(x1*(y2-y1))/(x2-x1);
        
        int lokacijaSez=0;
        int indeksIzbranega=0;
        
        if (y1<y2){
            double kMax=(y2-y1)/(x2-x1);
            int indMin=bisekcija(sezL,y1);
            for (int i=indMin; i<sezL.size(); i+=1){
                double x;
                double y;
                {
                    TockaIndeks tmp=sezL.get(i);
                    x=tmp.x;
                    y=tmp.y;
                }
                if (x>x1){//enak ni ker bi bil ze izbran
                    if (kMax*(x-x1)<(y-y1)){
                        kMax=(y-y1)/(x-x1);
                        lokacijaSez=-1;
                        indeksIzbranega=i;
                    }
                }
            }
            
            //double yZv=y1-kMax*x1;
            indMin=bisekcija(sezD,yZv);
            for (int i=indMin; i<sezD.size(); i+=1){
                double x;
                double y;
                {
                    TockaIndeks tmp=sezD.get(i);
                    x=tmp.x;
                    y=tmp.y;
                }
                if (x<x2){
                    if (kMax*(x-x1)<(y-y1)){
                        kMax=(y-y1)/(x-x1);
                        lokacijaSez=1;
                        indeksIzbranega=i;
                    }
                }
            }
            if (lokacijaSez==-1){
                TockaIndeks tI=sezL.remove(indeksIzbranega);
                pokLev.add(tI);
                return false;
            }
            else{if(lokacijaSez==1){
                TockaIndeks tI=sezD.remove(indeksIzbranega);
                pokDes.add(tI);
                return true;
            }
            else{
                return true;
            }
            }
        }
        
        else{//y1>y2
            double kMin=(y2-y1)/(x2-x1);
            
            int indMin=bisekcija(sezD,y2);
            for (int i=indMin; i<sezD.size(); i+=1){
                double x;
                double y;
                {
                    TockaIndeks tmp=sezD.get(i);
                    x=tmp.x;
                    y=tmp.y;
                }
                if (x<x2){
                    if (kMin*(x2-x)>(y2-y)){
                        kMin=(y2-y)/(x2-x);
                        lokacijaSez=1;
                        indeksIzbranega=i;
                    }
                }
            }
            
            //double yZv=y2-kMin*x2;
            indMin=bisekcija(sezL,yZv);
            for (int i=indMin; i<sezL.size(); i+=1){
                double x;
                double y;
                {
                    TockaIndeks tmp=sezL.get(i);
                    x=tmp.x;
                    y=tmp.y;
                }
                if (x>x1){//enak ni ker bi bil ze izbran
                    if (kMin*(x2-x)>(y2-y)){
                        kMin=(y2-y)/(x2-x);
                        lokacijaSez=-1;
                        indeksIzbranega=i;
                    }
                }
            }
            if (lokacijaSez==-1){
                TockaIndeks tI=sezL.remove(indeksIzbranega);
                pokLev.add(tI);
                return true;
            }
            else{if(lokacijaSez==1){
                TockaIndeks tI=sezD.remove(indeksIzbranega);
                pokDes.add(tI);
                return false;
            }
            else{
                return true;
            }
            }
        }
    }
    
    //resi del celotnega problema (super je ce se problem da cimbolj razdeliti)
    private List<Integer[]> resiDel(List<TockaIndeks> sezL, List<TockaIndeks> sezD) {
        TockaIndeks tMinX=najdiNajboljLev(sezL);
        TockaIndeks tMaxX=najdiNajboljDes(sezD);
        
        List<TockaIndeks> pokLev=new ArrayList<>();
        List<TockaIndeks> pokDes=new ArrayList<>();
        
        pokLev.add(tMinX);
        pokDes.add(tMaxX);
                
        List<Integer[]> sezPremic=new ArrayList<>();
        boolean zaPremico;//=false;
        //pomembno size funkcija je O(1)
        for(;sezL.size()+sezD.size()>0;){
            zaPremico=poisciNaslednjo(sezL,sezD,pokLev,pokDes);
            if (zaPremico){
                sezPremic.add(new Integer[] {pokLev.remove(pokLev.size()-1).indeks,
                                             pokDes.remove(pokDes.size()-1).indeks
                                            });
                if (pokLev.isEmpty()){
                    tMinX=najdiNajboljLev(sezL);
                    pokLev.add(tMinX);
                }
                if (pokDes.isEmpty()){
                    tMaxX=najdiNajboljDes(sezD);
                    pokDes.add(tMaxX);
                }                
            }
        }
        
        for(int i=0; i<pokLev.size(); i+=1){
            sezPremic.add(new Integer[] {pokLev.get(i).indeks,
                                         pokDes.get(i).indeks
                                        });
        }
        return sezPremic;
    }
    
    //algoritem je O(n**2) ce se delitev posreci dela dosti hitreje
    //odvisno od primera, za nakljucne tocke dela zelo hitro
    private List<List<Integer[]>> razbijResi(TockaIndeks[] sezLev, TockaIndeks[] sezDes) {
        List<List<Integer[]>> sezSezPremic=new ArrayList<>();
        {//lokalni
            int iLev=0;
            int iDes=0;
            List<TockaIndeks> sezL=new ArrayList<>();
            List<TockaIndeks> sezD=new ArrayList<>();
            
            for(;;){
                if (iLev>=sezLev.length || 
                   (iDes<sezDes.length && sezLev[iLev].y>sezDes[iDes].y)){
                    sezD.add(sezDes[iDes]);
                    iDes+=1;
                }
                else{
                    sezL.add(sezLev[iLev]);
                    iLev+=1;
                }
                
                if (iLev==iDes){
                    sezSezPremic.add(resiDel(sezL,sezD));
                    sezL=new ArrayList<>();
                    sezD=new ArrayList<>();
                    if (iLev==sezLev.length){
                        break;
                    }
                }
            }
        }
        
        return sezSezPremic; 
    }
    
    public int[][] solve(double[][] points){
        TockaIndeks[] sezLev=new TockaIndeks[points.length/2];
        TockaIndeks[] sezDes=new TockaIndeks[points.length/2];
        {//lokalni
            int iLev=0;
            int iDes=0;
            for (int i=0; i<points.length; i+=1){
                double[] t = points[i];
                if (t[0]<0){
                    sezLev[iLev]=new TockaIndeks(t,i);
                    iLev+=1;
                }
                else{
                    sezDes[iDes]=new TockaIndeks(t,i);
                    iDes+=1;
                }
            }
        }
        java.util.Arrays.sort(sezLev, (tI1,tI2)-> Double.compare(tI1.y, tI2.y));
        java.util.Arrays.sort(sezDes, (tI1,tI2)-> Double.compare(tI1.y, tI2.y));
        
        List<List<Integer[]>> sezSezPremic=razbijResi(sezLev,sezDes);
        
        int[][] sezPremic=new int[points.length/2][2];
        {
            int i=0;
            for (List<Integer[]> sez: sezSezPremic){
                for (Integer[] premica: sez){
                    sezPremic[i][0]=premica[0];
                    sezPremic[i][1]=premica[1];
                    i+=1;
                }
            }
        }
        
        return sezPremic;
    }
}
