package com.hdm.stundenplantool2.shared.report;

import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {

	Date aktuellesDatum = new Date();
	
	Paragraph headerData = null;
	
	String title;
	
	private static final long serialVersionUID = 1L;

	public Date getAktuellesDatum() {
		return aktuellesDatum;
	}

	public void setAktuellesDatum(Date aktuellesDatum) {
		this.aktuellesDatum = aktuellesDatum;
	}

	public Paragraph getHeaderData() {
		return headerData;
	}

	public void setHeaderData(Paragraph headerData) {
		this.headerData = headerData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
