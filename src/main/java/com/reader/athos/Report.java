package com.reader.athos;

public class Report {
    String cnpj;
    String xNome;
    String nFat;
    String vOrig;
    String vDesc;
    String vLiq;
    String nDup;
    String dVenc;
    String vDup;
    String indPag;
    String status;

    public String setPaymentType (int type){
        switch(type){
            case 1:
                return "Prazo";
            case 0:
                return "Vista";
            case 2:
                return "Outros";
        }
        return "";
    }

    public String changeDateFormat (String dVenc){
        return dVenc.replace("-", "/");
    }
}