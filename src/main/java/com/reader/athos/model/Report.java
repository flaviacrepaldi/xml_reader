package com.reader.athos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Report {
    private String cnpj;
    private Object dhEmi; 
    private String xNome;
    private String nFat;
    private String vOrig;
    private String vDesc;
    private String vLiq;
    private List<Duplicata> duplicatas = new ArrayList<Duplicata>();
    private String indPag;
    private String tPag;
    private String vPag;
    private String status;
    private String cnpjNodes;
    
    
    public List<Duplicata> getDuplicatas() {
		return Optional.ofNullable(this.duplicatas).orElse(new ArrayList<Duplicata>());
	}
    
    public void addDuplicata(Duplicata duplicata) {
    	getDuplicatas().add(duplicata);
    }

	public String getPaymentType(){
		if(this.getIndPag() != "Não encontrado") {
			switch(this.getIndPag()){
	            case "1":
	                return "Prazo"; 
	            case "0":
	                return "Vista";
	            case "2":
	                return "Outros";
			}
		}
		return "Não encontrado";
    }

    public String changeDateFormat (String dVenc){
        return dVenc.replace("-", "/");
    }
    
    public String getCnpjNodes() {
		return cnpjNodes;
	}

	public void setCnpjNodes(String cnpjNodes) {
		this.cnpjNodes = cnpjNodes;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public Object getDhEmi() {
		return dhEmi;
	}

	public void setDhEmi(Object dhEmi) {
		this.dhEmi = dhEmi;
	}

	public String getxNome() {
		return xNome;
	}

	public void setxNome(String xNome) {
		this.xNome = xNome;
	}

	public String getnFat() {
		return nFat;
	}

	public void setnFat(String nFat) {
		this.nFat = nFat;
	}

	public String getvOrig() {
		return vOrig;
	}

	public void setvOrig(String vOrig) {
		this.vOrig = vOrig;
	}

	public String getvDesc() {
		return vDesc;
	}

	public void setvDesc(String vDesc) {
		this.vDesc = vDesc;
	}

	public String getvLiq() {
		return vLiq;
	}

	public void setvLiq(String vLiq) {
		this.vLiq = vLiq;
	}

	public String getIndPag() {
		return indPag;
	}

	public void setIndPag(String indPag) {
		this.indPag = indPag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String gettPag() {
		return tPag;
	}

	public void settPag(String tPag) {
		this.tPag = tPag;
	}

	public String getvPag() {
		return vPag;
	}

	public void setvPag(String vPag) {
		this.vPag = vPag;
	}
   
}