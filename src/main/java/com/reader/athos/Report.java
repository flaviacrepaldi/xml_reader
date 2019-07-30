package com.reader.athos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Report {
	private static final String DEFAULT_PATTERN_TO_PARSE = "yyyy-MM-dd'T'HH:mm:ssX";
	String cnpj;
	Object dhEmi;
	String xNome;
	String nFat;
	String vOrig;
	String vDesc;
	String vLiq;
	String nDup;
	String dVenc;
	String vDup;
	String indPag;
	String tPag;
	String vPag;
	String status;

	public String setPaymentType(int type) {
		switch (type) {
		case 1:
			return "Prazo";
		case 0:
			return "Vista";
		case 2:
			return "Outros";
		}
		return "";
	}

	public String changeDateFormat(String dVenc) {
		return dVenc.replace("-", "/");
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

	public Date convertDhEmiInDate() {
		// formato padrao de data recebido 2019-05-31T15:41:47-03:00
		
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN_TO_PARSE);
			date = formatter.parse(getDhEmi().toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
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

	public String getnDup() {
		return nDup;
	}

	public void setnDup(String nDup) {
		this.nDup = nDup;
	}

	public String getdVenc() {
		return dVenc;
	}

	public void setdVenc(String dVenc) {
		this.dVenc = dVenc;
	}

	public String getvDup() {
		return vDup;
	}

	public void setvDup(String vDup) {
		this.vDup = vDup;
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