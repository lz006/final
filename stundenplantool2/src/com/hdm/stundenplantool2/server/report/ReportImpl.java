package com.hdm.stundenplantool2.server.report;

import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hdm.stundenplantool2.server.VerwaltungImpl;
import com.hdm.stundenplantool2.shared.bo.*;
import com.hdm.stundenplantool2.shared.report.*;
import com.hdm.stundenplantool2.shared.Report;

@SuppressWarnings("serial")
public class ReportImpl extends RemoteServiceServlet implements Report {

	VerwaltungImpl verwaltung = new VerwaltungImpl();
	
	public String createStudentenplan(Semesterverband sv) throws RuntimeException {
		
		Vector<Belegung> svBelegungen = verwaltung.belegungMapper.findBySemesterverband(sv);
		
		if (svBelegungen == null || svBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen zu diesem Semesterverband vorhanden");
		}
		
		Studentenplan sPlan = new Studentenplan();
		sPlan.init();
		
		sPlan.getPlan().elementAt(0).set(0, "Zeiten");
		sPlan.getPlan().elementAt(0).set(1, "08:15 - 09:45");
		sPlan.getPlan().elementAt(0).set(2, "10:00 - 11:30");
		sPlan.getPlan().elementAt(0).set(3, "11:45 - 13:15");
		sPlan.getPlan().elementAt(0).set(4, "14:15 - 15:45");
		sPlan.getPlan().elementAt(0).set(5, "16:00 - 17:30");
		sPlan.getPlan().elementAt(0).set(6, "17:45 - 19:15");
		sPlan.getPlan().elementAt(0).set(7, "19:30 - 21:00");
		
		sPlan.getPlan().elementAt(1).set(0, "Montag");
		sPlan.getPlan().elementAt(2).set(0, "Dienstag");
		sPlan.getPlan().elementAt(3).set(0, "Mittwoch");
		sPlan.getPlan().elementAt(4).set(0, "Donnerstag");
		sPlan.getPlan().elementAt(5).set(0, "Freitag");
		sPlan.getPlan().elementAt(6).set(0, "Samstag");
		sPlan.getPlan().elementAt(7).set(0, "Sonntag");
		
		for (int i = 0; i < svBelegungen.size(); i++) {
			
			int rowPointer = 0;
			int columnPointer = 0;
			
			if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 7 && svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {				
				columnPointer = 2;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}
			
			StringBuffer tempSB = new StringBuffer();
			tempSB.append(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");
			tempSB.append(svBelegungen.elementAt(i).getRaum().getBezeichnung());
			tempSB.append("<br />");
			
			for (int j = 0; j < svBelegungen.elementAt(i).getDozenten().size() - 1; j++) {
				tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(j).getNachname());
				tempSB.append("<br />");
			}
			
			tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(svBelegungen.elementAt(i).getDozenten().size() -1).getNachname());
			
			sPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());
			
		}
		
		StringBuffer  x = new StringBuffer();
		x.append("<p>");
		x.append(sv.getJahrgang());
		x.append(sv.getStudiengang().getBezeichnung());
		x.append("</p>");
		
		String tempHtml = new HTMLReportWriter().getHTMLString(sPlan);
		
		x.append(tempHtml);
		
		return x.toString();
	}
	
	public Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException {
		return verwaltung.auslesenAlleStudiengaengeOhneSVuLV();
	}
	
	public Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException {
		return verwaltung.auslesenSemesterverbaendeNachStudiengang(sg);
	}
	
	public String createDozentenplan(Dozent dozent) throws RuntimeException {
		
		Vector<Belegung> dozentBelegungen = verwaltung.belegungMapper.findByDozent(dozent);
		
		if (dozentBelegungen == null || dozentBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen für diesem Dozent vorhanden");
		}
		
		Dozentenplan dPlan = new Dozentenplan();
		dPlan.init();
		
		dPlan.getPlan().elementAt(0).set(0, "Zeiten");
		dPlan.getPlan().elementAt(0).set(1, "08:15 - 09:45");
		dPlan.getPlan().elementAt(0).set(2, "10:00 - 11:30");
		dPlan.getPlan().elementAt(0).set(3, "11:45 - 13:15");
		dPlan.getPlan().elementAt(0).set(4, "14:15 - 15:45");
		dPlan.getPlan().elementAt(0).set(5, "16:00 - 17:30");
		dPlan.getPlan().elementAt(0).set(6, "17:45 - 19:15");
		dPlan.getPlan().elementAt(0).set(7, "19:30 - 21:00");
		
		dPlan.getPlan().elementAt(1).set(0, "Montag");
		dPlan.getPlan().elementAt(2).set(0, "Dienstag");
		dPlan.getPlan().elementAt(3).set(0, "Mittwoch");
		dPlan.getPlan().elementAt(4).set(0, "Donnerstag");
		dPlan.getPlan().elementAt(5).set(0, "Freitag");
		dPlan.getPlan().elementAt(6).set(0, "Samstag");
		dPlan.getPlan().elementAt(7).set(0, "Sonntag");
		
		for (int i = 0; i < dozentBelegungen.size(); i++) {
			
			int rowPointer = 0;
			int columnPointer = 0;
			
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 7 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 14) {				
				columnPointer = 2;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 14 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 21 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 28 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 35 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 42 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}
			
			StringBuffer tempSB = new StringBuffer();
			tempSB.append(dozentBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");
			tempSB.append(dozentBelegungen.elementAt(i).getRaum().getBezeichnung());
			tempSB.append("<br />");
			
			if (dozentBelegungen.elementAt(i).getSemesterverbaende() != null && dozentBelegungen.elementAt(i).getSemesterverbaende().size() > 0) {
				for (int j = 0; j < dozentBelegungen.elementAt(i).getSemesterverbaende().size() - 1; j++) {
					tempSB.append(verwaltung.auslesenSemesterverband(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(j)).elementAt(0).getStudiengang().getKuerzel());
					tempSB.append(" ");
					tempSB.append(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getJahrgang());
					tempSB.append("<br />");
				}
				
				tempSB.append(verwaltung.auslesenSemesterverband(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(dozentBelegungen.elementAt(i).getSemesterverbaende().size() -1 )).elementAt(0).getStudiengang().getKuerzel());
				tempSB.append(" ");
				tempSB.append(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(dozentBelegungen.elementAt(i).getSemesterverbaende().size() -1 ).getJahrgang());
			}
			
			dPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());
			
		}
		
		return new HTMLReportWriter().getHTMLString(dPlan);
	}
	
	public Vector<Dozent> auslesenAlleDozenten() throws RuntimeException {
		return verwaltung.auslesenAlleDozenten();
	}
	
	public String createRaumplan(Raum raum) throws RuntimeException {
		
		Vector<Belegung> raumBelegungen = verwaltung.belegungMapper.findByRaum(raum);
		
		if (raumBelegungen == null || raumBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen zu diesem Raum vorhanden");
		}
		
		Studentenplan rPlan = new Studentenplan();
		rPlan.init();
		
		rPlan.getPlan().elementAt(0).set(0, "Zeiten");
		rPlan.getPlan().elementAt(0).set(1, "08:15 - 09:45");
		rPlan.getPlan().elementAt(0).set(2, "10:00 - 11:30");
		rPlan.getPlan().elementAt(0).set(3, "11:45 - 13:15");
		rPlan.getPlan().elementAt(0).set(4, "14:15 - 15:45");
		rPlan.getPlan().elementAt(0).set(5, "16:00 - 17:30");
		rPlan.getPlan().elementAt(0).set(6, "17:45 - 19:15");
		rPlan.getPlan().elementAt(0).set(7, "19:30 - 21:00");
		
		rPlan.getPlan().elementAt(1).set(0, "Montag");
		rPlan.getPlan().elementAt(2).set(0, "Dienstag");
		rPlan.getPlan().elementAt(3).set(0, "Mittwoch");
		rPlan.getPlan().elementAt(4).set(0, "Donnerstag");
		rPlan.getPlan().elementAt(5).set(0, "Freitag");
		rPlan.getPlan().elementAt(6).set(0, "Samstag");
		rPlan.getPlan().elementAt(7).set(0, "Sonntag");
		
		for (int i = 0; i < raumBelegungen.size(); i++) {
			
			int rowPointer = 0;
			int columnPointer = 0;
			
			if (raumBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 7 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 14) {				
				columnPointer = 2;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 14 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 21 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 28 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 35 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 42 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}
			
			StringBuffer tempSB = new StringBuffer();
			tempSB.append(raumBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");
			
			for (int j = 0; j < raumBelegungen.elementAt(i).getDozenten().size() - 1; j++) {
				tempSB.append(raumBelegungen.elementAt(i).getDozenten().elementAt(j).getNachname());
				tempSB.append("<br />");
			}
			
			tempSB.append(raumBelegungen.elementAt(i).getDozenten().elementAt(raumBelegungen.elementAt(i).getDozenten().size() -1).getNachname());
			tempSB.append("<br />");
			
			if (raumBelegungen.elementAt(i).getSemesterverbaende() != null && raumBelegungen.elementAt(i).getSemesterverbaende().size() > 0) {
				for (int j = 0; j < raumBelegungen.elementAt(i).getSemesterverbaende().size() - 1; j++) {
					tempSB.append(verwaltung.auslesenSemesterverband(raumBelegungen.elementAt(i).getSemesterverbaende().elementAt(j)).elementAt(0).getStudiengang().getKuerzel());
					tempSB.append(" ");
					tempSB.append(raumBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getJahrgang());
					tempSB.append("<br />");
				}
				
				tempSB.append(verwaltung.auslesenSemesterverband(raumBelegungen.elementAt(i).getSemesterverbaende().elementAt(raumBelegungen.elementAt(i).getSemesterverbaende().size() -1 )).elementAt(0).getStudiengang().getKuerzel());
				tempSB.append(" ");
				tempSB.append(raumBelegungen.elementAt(i).getSemesterverbaende().elementAt(raumBelegungen.elementAt(i).getSemesterverbaende().size() -1 ).getJahrgang());
			}
			
			rPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());
			
		}
		
		return new HTMLReportWriter().getHTMLString(rPlan);
	}
	
	public Vector<Raum> auslesenAlleRaeume() throws RuntimeException {
		return verwaltung.auslesenAlleRaeume();
	}
	
}
