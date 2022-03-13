package com.example.aplikasitremor;

public class FuzzyMamdani implements MamdaniInterface {

    private String[][] rule;
    private double dsedang [];
    private double drendah[];
    private double dtinggi [];
    private double test [][];
    private double aPredikat [];
    private double dfrekuensi [];
    private double momen [];
    private double luas[];
    private double max1,max2, max3;
    private double a1,a2,a3,a4,a5,a6;
    private double jumlahMomen,jumlahLuas;
    private double rendah, sedang, tinggi,resultfreq;

    @Override
    public void Rule() {
        rule = new String[3][2];
        rule[0][0]="rendah";
        rule[1][0]="sedang";
        rule[2][0]="tinggi";


        rule[0][1]="rendah";
        rule[1][1]="sedang";
        rule[2][1]="tinggi";

    }

    @Override
    public void Fuzzyfikasi(double frekuensi) {
        this.drendah     = new double[3];
        this.dsedang  = new double[3];
        this.dtinggi           = new double[3];

        this.test           = new double[3][4];
        double pA = 4;
        double pB = 6;
        double pC = 8;
        double pD = 11;
        this.rendah      = rendah;
        this.sedang     = sedang;
        this.tinggi = tinggi;

        rendah = frekuensi;
        sedang = frekuensi;
        tinggi = frekuensi;

        for(int i=0;i<drendah.length;i++) {
            if (rule[i][0].equalsIgnoreCase("rendah")) {
                if (rendah <= pA) {
                    drendah[i] = 1;
                } else if (rendah > pA && rendah < pB) {
                    drendah[i] = (pB - rendah) / 2;
                } else
                    drendah[i] = 0;
            }
        }
        for(int i=0;i<dsedang.length;i++) {
            if (rule[i][0].equalsIgnoreCase("sedang")) {
                if (sedang <= pA || sedang >= pD) {
                    dsedang[i] = 0;
                } else if (sedang > pA && sedang < pB) {
                    dsedang[i] = (sedang - pA) / 2;
                } else if (sedang > pC && sedang < pD) {
                    dsedang[i] = (pD - sedang) / 2;
                } else
                    dsedang[i] = 1;
            }
        }
        for(int i=0;i<dtinggi.length;i++) {
            if (rule[i][0].equalsIgnoreCase("tinggi")) {
                if(tinggi <= pC){
                    dtinggi[i]=0;
                }
                else if(tinggi > pC && tinggi < pD){
                    dtinggi[i]=(tinggi-pC)/2;
                }
                else
                    dtinggi[i]=1;
            }
        }





        for(int i=0;i<3;i++){
            test [i][0]=drendah[i];
        }

        for(int i=0;i<3;i++){
            test [i][1]=dsedang[i];
        }

        for(int i=0;i<3;i++){
            test [i][2]=dtinggi[i];
        }

    }

    @Override
    public void ImplikasiMin() {
        aPredikat = new double [3];
        for(int i=0;i<3;i++){
            aPredikat[i]=Math.min(drendah[i],Math.min(dsedang[i],dtinggi[i]));
            test [i][3]=aPredikat[i];
        }
    }

    @Override
    public void AgregasiMax() {
        this.dfrekuensi = new double[3];
        max1 = 0;
        max2 = 0;
        max3 = 0;
        for(int i=0;i<3;i++){
            if(rule[i][1].equalsIgnoreCase("rendah")){
                if(aPredikat[i]> max1){
                    max1=aPredikat[i];
                }

            }

            else if(rule[i][1].equalsIgnoreCase("sedang")){
                if(aPredikat[i]>max2){
                    max2=aPredikat[i];
                }

            }
            else{
                if(aPredikat[i]>max3){
                    max3=aPredikat[i];
                }

            }
        }

        dfrekuensi[0]=max1;
        dfrekuensi[1]=max2;
        dfrekuensi[2]=max3;
    }

    @Override
    public void BatasArea() {
        a1=2;
        if(dfrekuensi[0]>dfrekuensi[1]){
            a2=5-(dfrekuensi[0]*(5-4));
            a3=5-(dfrekuensi[1]*(5-4));
        }
        else{
            a2=(dfrekuensi[0]*(5-4))+4;
            a3=(dfrekuensi[1]*(5-4))+4;
        }
        if(dfrekuensi[1]>dfrekuensi[2]) {
            a4 = 7 - (dfrekuensi[1] * (7 - 6));
            a5= 7 -(dfrekuensi[2]*(7-6));
        }else{
            a4=(dfrekuensi[1]*(7-6))+6;
            a5=(dfrekuensi[2]*(7-6))+6;
        }
    a6=11;

    }

    @Override
    public void Momen() {
        momen = new double [5];
        momen[0]=(dfrekuensi[0]/2*Math.pow(a2,2))-(dfrekuensi[0]/2*Math.pow(a1,2));
        if(dfrekuensi[0]>dfrekuensi[1]){
            momen[1] = (1./(5-4))*((((5*Math.pow(a3,2))/2)-((1./3)*Math.pow(a3,3)))-(((5*Math.pow(a2,2))/2)-((1./3)*Math.pow(a2,3))));
        }
        else{
            momen[1] = (1./(5-4))*((((1./3)*Math.pow(a3,3))-((5*Math.pow(a3,2))/2))-(((1./3)*Math.pow(a2,3))-((4*Math.pow(a2,2))/2)));
        }

        momen[2]=(dfrekuensi[1]/2*Math.pow(a4,2))-(dfrekuensi[1]/2*Math.pow(a3,2));
        if(dfrekuensi[1]>dfrekuensi[2]){
            momen[3] =(1./(7-6))*((((7*Math.pow(a5,2))/2)-((1./3)*Math.pow(a5,3)))-(((7*Math.pow(a4,2))/2)-((1./3)*Math.pow(a4,3))));
        }
        else{
            momen[3] = (1./(7-6))*((((1./3)*Math.pow(a5,3))-((6*Math.pow(a5,2))/2))-(((1./3)*Math.pow(a4,3))-((6*Math.pow(a4,2))/2)));
        }
        momen[4]=(dfrekuensi[2]/2*Math.pow(a6,2))-(dfrekuensi[2]/2*Math.pow(a5,2));



    }

    @Override
    public void Luas() {
        luas = new double [5];
        luas[0]=(dfrekuensi[0]*a2)-(dfrekuensi[0]*a1);

        if(dfrekuensi[0] > dfrekuensi[1]){
            luas[1] = ((Math.pow(a3, 2)/2)-(5*a3)) - ((Math.pow(a2, 2)/2)-(5*a2));
        }

        else {
            luas[1] = ((6*a3)-(Math.pow(a3,2)/3)) - ((6*a2)-(Math.pow(a2,2)/2));
        }

        luas[2]=(dfrekuensi[1]*a4)-(dfrekuensi[1]*a3);
        if(dfrekuensi[1] > dfrekuensi[2]){
            luas[3] = ((4*a5)-((a5*a5)/2)) - ((5*a4)-((a4*a4)/2));
        }

        else {
            luas[3] = ((Math.pow(a5,2)/2)-((5*a5)))-((Math.pow(a4,2)/2)-((5*a4)));
        }

        luas[4]=(dfrekuensi[2]*a6)-(dfrekuensi[2]*a5);


    }

    @Override
    public void MomenLuas() {
        jumlahMomen=0;
        jumlahLuas=0;
        for(int i=0;i<momen.length;i++){
            jumlahMomen+=momen[i];
        }

        for(int i=0;i<luas.length;i++){
            jumlahLuas+=luas[i];
        }

    }



    public void resultfuzzy() {
        resultfreq= jumlahMomen/jumlahLuas;
    }
    public double getresultfuzzy() {
        return resultfreq;
    }
}
