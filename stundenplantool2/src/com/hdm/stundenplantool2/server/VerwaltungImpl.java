package com.hdm.stundenplantool2.server;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.hdm.stundenplantool2.server.db.*;
import com.hdm.stundenplantool2.shared.Verwaltung;
import com.hdm.stundenplantool2.shared.bo.*;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class VerwaltungImpl extends RemoteServiceServlet implements Verwaltung {
	
	public BelegungMapper belegungMapper = BelegungMapper.belegungMapper();
	public DozentMapper dozentMapper = DozentMapper.dozentMapper();
	public LehrveranstaltungMapper lehrveranstaltungMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
	public RaumMapper raumMapper = RaumMapper.raumMapper();
	public SemesterverbandMapper semesterverbandMapper = SemesterverbandMapper.semesterverbandMapper();
	public StudiengangMapper studiengangMapper= StudiengangMapper.studiengangMapper();
	public ZeitslotMapper zeitslotMapper = ZeitslotMapper.zeitslotMapper();

	// ------------------------------------------------------------------------------------------------------------------------------------
	
	
	public VerwaltungImpl getVerwaltungImpl() {
		return this;
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 * Auslesen der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Vector<Semesterverband> auslesenAlleSemesterverbaende() throws RuntimeException {
		return semesterverbandMapper.findAll(false);
	}
	
	public Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException {
		return semesterverbandMapper.findByStudiengang(sg, false);
	}
	
	public Vector<Semesterverband> auslesenSemesterverband(Semesterverband sv) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(sv.getId());
		return semesterverbandMapper.findByKey(vi, false);
	}
		
	public Vector<Dozent> auslesenAlleDozenten() throws RuntimeException {
		return dozentMapper.findAll(false);
	}
	
	public Vector<Dozent> auslesenDozentenNachLV(Lehrveranstaltung lv) throws RuntimeException {
		return dozentMapper.findByLV(lv, false);
	}
	
	public Vector<Dozent> auslesenDozentenNachZeitslot(Zeitslot lv) throws RuntimeException {
		Vector<Belegung> zeitslotBelegungen = belegungMapper.findByZeitslot(lv);
		Vector<Dozent> alleDozenten = dozentMapper.findAll(false);
		
		Vector<Dozent> freieDozenten = new Vector<Dozent>();
		
		for (int i = 0; i < alleDozenten.size(); i++) {
			boolean check1 = true;
			if (zeitslotBelegungen != null) {
				for (int j = 0; j < zeitslotBelegungen.size(); j++) {
					boolean check2 = true;
					if (zeitslotBelegungen.elementAt(j).getDozenten() != null) {
						for (int k = 0; k < zeitslotBelegungen.elementAt(j).getDozenten().size(); k++) {
							if(alleDozenten.elementAt(i).getId() == zeitslotBelegungen.elementAt(j).getDozenten().elementAt(k).getId()) {
								check1 = false;
								check2 = false;
								break;
							}
						}
					}
					if (!check2) {
						break;
					}
				}
			}
			if (check1) {
				freieDozenten.add(alleDozenten.elementAt(i));
			}
		}
		
		if (freieDozenten.size() > 0) {
			return freieDozenten;
		}
		else {
			throw new RuntimeException("Es sind leider keine Dozenten zu diesem Zeitslot verfügbar");
		}
	}
	
	public Vector<Dozent> auslesenDozent(Dozent dozent) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(dozent.getId());
		return dozentMapper.findByKey(vi, true);
	}
	
	public Vector<Zeitslot> auslesenAlleZeitslots() throws RuntimeException {
		return zeitslotMapper.findAll();
	}
	
	public Vector<Lehrveranstaltung> auslesenAlleLehrveranstaltungen() throws RuntimeException {
		return lehrveranstaltungMapper.findAll(false);
	}
	
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltung(Lehrveranstaltung lv) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(lv.getId());
		return lehrveranstaltungMapper.findByKey(vi, true);
	}
	
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSV(Semesterverband sv, Studiengang sg) throws RuntimeException {
		Vector<Lehrveranstaltung> tempLVVector = lehrveranstaltungMapper.findByStudiengang(sg, false);
		Vector<Lehrveranstaltung> ergebnisLVVector = new Vector<Lehrveranstaltung>();
		
		// Aussortieren der Lehrveranstaltungen, welche mit Studiensemester des Semesterverbands vereinbar sind
		
		for (int i = 0; i < tempLVVector.size(); i++) {
			
			Integer semesterAlt = null;
			Integer semesterNeu = null;
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (sv.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (sv.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(sv.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};

			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {
				
			
			int zaehler = 0;
			
			for (int j = semMonat-1; j < calendar.length; j++ ) {
				
				zaehler++;
				
				if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
					break;
				}
				if(calendar[j] == 12) {
					j = -1;
					jahresDiff--;
				}
			}
			
			int studienSem = zaehler / 6;
			int studienSemMonat = zaehler;
			
			if (zaehler != 0) {
				studienSemMonat = zaehler % 6;
			}
			
			

			
			if (studienSem == 0) {
				semesterAlt = 1;
				semesterNeu = 0;
			}
			
			else if (studienSemMonat == 0) {
				semesterAlt = studienSem;
				semesterNeu = studienSem + 1;
			}
			
					
			else {
				semesterAlt = studienSem + 1;
			}
			}
			else {
				semesterAlt = 1;
			}
		
		
			if (semesterNeu == null || semesterNeu == 0) {
				if (tempLVVector.elementAt(i).getStudiensemester() != semesterAlt) {
					continue;
				}
			}
		
			else if (semesterAlt == null || semesterAlt == 0) {
				if (tempLVVector.elementAt(i).getStudiensemester() != semesterNeu) {
					continue;
				}
			}		
		
			else {
				if ((tempLVVector.elementAt(i).getStudiensemester() != semesterAlt) && (tempLVVector.elementAt(i).getStudiensemester() != semesterNeu)) {
					continue;
				}
			}
			ergebnisLVVector.add(tempLVVector.elementAt(i));
		}
		
		if (ergebnisLVVector.size() <= 0) {
			throw new RuntimeException("Für den gewählten Studiengang existiert noch keine passende Lehrveranstaltung.\nIn Folge dessen existieren auch keine Belegungen bzw. können auch nicht angelegt werden");
		}
		return ergebnisLVVector;
	} 
	
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSG(Studiengang sg) throws RuntimeException {
		return lehrveranstaltungMapper.findByStudiengang(sg, false);
	}
	
	public Vector<Belegung> auslesenAlleBelegungen() throws RuntimeException {
		return belegungMapper.findAll(false);
	}
	
	public Vector<Belegung> auslesenBelegung(Belegung belegung) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getId());
		return belegungMapper.findByKey(vi, true);
	}
	
	public Vector<Belegung> auslesenBelegungenNachSV(Semesterverband semesterverband) throws RuntimeException {
		return belegungMapper.findBySemesterverband(semesterverband);
	}
	
	public Vector<Studiengang> auslesenAlleStudiengaenge() throws RuntimeException {
		return studiengangMapper.findAll(true);
	}
	
	public Vector<Studiengang> auslesenStudiengang(Studiengang studiengang) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(studiengang.getId());
		return studiengangMapper.findByKey(vi, true);
	}
	
	public Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException {
		return studiengangMapper.findAll(false);
	}
	
	public Vector<Raum> auslesenAlleRaeume() throws RuntimeException {
		return raumMapper.findAll();
	}
	
	public Vector<Raum> auslesenRaum(Raum raum) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(raum.getId());
		return raumMapper.findByKey(vi);
	}
	
	public Vector<Raum> auslesenVerfuegbareRaeumeZuZeitslotuSV(Zeitslot zeitslot, Vector<Semesterverband> sv) throws RuntimeException {
		
		Vector<Belegung> tempBelegungVector = belegungMapper.findAll(false);
		
		Vector<Raum> besetzteRaeumeVector = new Vector<Raum>();
		Vector<Raum> freieRaeumeVector = new Vector<Raum>();
		
		// Errechnen der Studentenanzahl
		
		int studentenzahl = 0;
		
		for (Semesterverband tempSV : sv) {
			studentenzahl = studentenzahl + tempSV.getAnzahlStudenten();
		}
		
		// Auslesen aller besetzten Räume zum gewünschten Zeitslot
		
		for (int i = 0; i < tempBelegungVector.size(); i++) {
			if (tempBelegungVector.elementAt(i).getZeitslot().getId() == zeitslot.getId()) {
				besetzteRaeumeVector.add(tempBelegungVector.elementAt(i).getRaum());
			}
		}
		
		// Besetzte Räume und Räume deren Kapazität ausreichend für die Stundentenanzahl des gewählten Semesterverbands ist, aus allen Räumen herausfiltern
		
		if (besetzteRaeumeVector.size() > 0) {
			Vector<Raum> alleRaeumeVector = raumMapper.findAll();
			System.out.println(new Date());
			if (alleRaeumeVector.size() != besetzteRaeumeVector.size()) {
				for (int i = 0; i < alleRaeumeVector.size(); i++) {
					boolean check = false;
					for (int j = 0; j < besetzteRaeumeVector.size(); j++) {
						if ((alleRaeumeVector.elementAt(i).getKapazitaet() < studentenzahl) || (alleRaeumeVector.elementAt(i).getId() == besetzteRaeumeVector.elementAt(j).getId())) {
							check = true;
							break;
						}
					}
					if (!check) {
						freieRaeumeVector.add(alleRaeumeVector.elementAt(i));
					}
				}
				
				return freieRaeumeVector;
			}
			else {
				throw new RuntimeException("Kein Raum verfügbar\nBitte wählen Sie einen anderen Zeitslot");
			}
		}
		else {
			return raumMapper.findAll();
		}
		
	}
	
	/*
	 * Löschen der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	public void loeschenSemesterverband(Semesterverband semesterverband) throws RuntimeException {
		
		// Ein Semesterverband kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist
		
		if (semesterverband.getBelegungen() == null) {
			semesterverbandMapper.delete(semesterverband);
		}
		else {
			StringBuffer eText = new StringBuffer();
			eText.append("Bitte löschen Sie zuerst alle Belegungen von " + semesterverband.getStudiengang().getKuerzel() + " " + semesterverband.getJahrgang()+ "\n");
			eText.append("Folgende Belegungen wurden identifiziert:\n");
			eText.append("\n");
			for(int i = 0; i < semesterverband.getBelegungen().size(); i++) {
				eText.append(semesterverband.getBelegungen().elementAt(i).getZeitslot().getWochentag() + "\t\t");
				eText.append(semesterverband.getBelegungen().elementAt(i).getLehrveranstaltung().getBezeichnung());
				eText.append("\n");
			}			
			throw new RuntimeException(eText.toString());
		}
	}
	
	public void loeschenDozent(Dozent dozent) throws RuntimeException {
		
		// Ein Dozent kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist
		
		if (dozent.getBelegungen() == null) {
			dozentMapper.delete(dozent);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von " + dozent.getTitel() + " " + dozent.getVorname() + " " + dozent.getNachname());
		}		
	}
	
	public void loeschenZeitslot(Zeitslot zeitslot) throws RuntimeException {
		// Das Löschen eines Zeitslots ist bis dato nicht vorgesehen Stand: 06.12.2013
	}
	
	public void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException {
		
		// Ein Dozent kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist
		
		if (lehrveranstaltung.getBelegungen() == null) {
			lehrveranstaltungMapper.delete(lehrveranstaltung);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von " + lehrveranstaltung.getBezeichnung());
		}	
	}
	
	public void loeschenBelegungen(Belegung belegung, Semesterverband semesterverband) throws RuntimeException {
		
		/*
		 *  Wenn mehrere Semesterverbände die Belegung referenzieren, wird nur die Referenz des aus des 
		 *  vom Client gewählten Semesterverbandes zu der gemeinten Belegung gelöscht
		 */
		if (belegung.getSemesterverbaende().size() > 1) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(belegung.getId());
			Vector<Belegung> vb = this.belegungMapper.findByKey(vi, true);
			for (int i = 0; i < vb.elementAt(0).getSemesterverbaende().size(); i++) {
				if (vb.elementAt(0).getSemesterverbaende().elementAt(i).getId() == semesterverband.getId()) {
					vb.elementAt(0).getSemesterverbaende().removeElementAt(i);
					this.belegungMapper.update(vb.elementAt(0));
					break;
				}
			}
		}
		else {
			belegungMapper.delete(belegung);
		}
	}
	
	public void loeschenStudiengang(Studiengang studiengang) throws RuntimeException {
		
		// Ein Studiengang kann nur gelöscht werden, wenn er durch keine Lehrveranstaltungen und Semesterverbände mehr referenziert wird
		
		if(studiengang.getLehrveranstaltungen() == null && studiengang.getSemesterverbaende() == null) {
			studiengangMapper.delete(studiengang);
		}
		else {
			StringBuffer eText = new StringBuffer();
			eText.append("Bitte loeschen Sie zuerst alle Referenzen auf " + studiengang.getBezeichnung() + "\n\n");
			if (studiengang.getLehrveranstaltungen() != null) {
				eText.append("Folgende Lehrveranstaltungen sind verknüpft:\n");
				for (int i = 0; i < studiengang.getLehrveranstaltungen().size(); i++) {
					eText.append(studiengang.getLehrveranstaltungen().elementAt(i).getBezeichnung() + "\n");					
				}
				eText.append("\n\n");
			}
			if (studiengang.getSemesterverbaende() != null) {
				eText.append("Folgende Jahrgänge sind verknüpft:\n");
				for (int i = 0; i < studiengang.getSemesterverbaende().size(); i++) {
					eText.append(studiengang.getSemesterverbaende().elementAt(i).getJahrgang() + "\n");					
				}
			}
			throw new RuntimeException(eText.toString());
		}		
	}
	
	public void loeschenRaum(Raum raum) throws RuntimeException {
		
		// Ein Raum kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist
		
		if (this.belegungMapper.findByRaum(raum) == null) {
			raumMapper.delete(raum);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von Raum" + raum.getBezeichnung());
		}
	}
	
	/*
	 * ändern der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Semesterverband aendernSemesterverband(Semesterverband semesterverband) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(semesterverband.getId());
		
		Vector<Semesterverband> altSemesterverband = semesterverbandMapper.findByKey(vi, true);
		
		boolean check = true;
		
		// Zunöchst wird geprüft ob zu dem gemeinten Semesterverband Belegungen bestehen
		
		if (altSemesterverband.elementAt(0).getBelegungen() != null && altSemesterverband.elementAt(0).getBelegungen().size() > 0) {
			check = false;
		}
		
		// Prüfung ob der Studiengang geändert wurde
		
		if(semesterverband.getStudiengang().getId() != altSemesterverband.elementAt(0).getStudiengang().getId() && !check) {
			throw new RuntimeException("Der Studiengang eines Semesterverbandes kann nur geändert werden wenn keine Belegungen mehr refernziert werden");
		}
		
		// Prüfung ob der Jahrgang geändert wurde
		
		if(!semesterverband.getJahrgang().equals(altSemesterverband.elementAt(0).getJahrgang()) && !check) {
			throw new RuntimeException("Der Jahrgang eines Semesterverbandes kann nur geändert werden wenn keine Belegungen mehr refernziert werden");
		}
		
		// Pröfung ob die Anzahl der Studenten korrekt angegeben wurde
		
		if (!new Integer(semesterverband.getAnzahlStudenten()).toString().matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Bitten geben Sie die Anzahl der Studenten an");
		}
		
		// Prüfen ob dieser Jahrgang in dem Studiengang bereits vorhanden ist
		
		Vector<Semesterverband> sgNachSV = this.auslesenSemesterverbaendeNachStudiengang(semesterverband.getStudiengang());
		
		for (Semesterverband sv : sgNachSV) {
			if (semesterverband.getJahrgang() == sv.getJahrgang() && semesterverband.getId() != sv.getId()) {
				throw new IllegalArgumentException("Dieser Semesterverband existiert bereits");
			}
		}
		
		// Prüfung ob eine neue Studentenanzahl mit bestehenden Belegungen vereinbar ist
		
		if (!check) {
			if (semesterverband.getAnzahlStudenten() != altSemesterverband.elementAt(0).getAnzahlStudenten()) {
				for (Belegung belegung : altSemesterverband.elementAt(0).getBelegungen()) {
					if (belegung.getRaum().getKapazitaet() < semesterverband.getAnzahlStudenten()) {
						throw new IllegalArgumentException("Die Anzahl der Studenten ist zu hoch für bestehende Belegungen");
					}
				}
			}
		}
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde
		
		StringBuffer jahrgang = new StringBuffer();
		jahrgang.append(semesterverband.getJahrgang());
		
		if (jahrgang.length() == 0) {
			throw new IllegalArgumentException("Das Feld \"Jahrgang\" darf nicht leer sein"); 
		}
		
		if (!jahrgang.substring(0,2).equals("SS") && !jahrgang.substring(0,2).equals("WS") || !jahrgang.substring(2,4).equals("20")) {
			throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nBeachten Sie auch die Gross-/Kleinschreibweise");			
		}
		
		if ((jahrgang.substring(0,2).equals("SS") && jahrgang.length() != 6) || (jahrgang.substring(0,2).equals("WS") && jahrgang.length() != 9) ||
				(jahrgang.substring(0,2).equals("WS") && jahrgang.charAt(6) != new Character('/'))) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
		}
		
		if (jahrgang.substring(0,2).equals("SS")) {
			try {
				Integer.parseInt(jahrgang.substring(4,6));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
		}
		
		if (jahrgang.substring(0,2).equals("WS")) {
			int vJahr;
			int nJahr;
			try {
				vJahr = Integer.parseInt(jahrgang.substring(4,6));
				nJahr = Integer.parseInt(jahrgang.substring(7,9));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
			if (nJahr - vJahr != 1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nNur ein Jahreswechsel erlaubt");
			}
		}
		
		altSemesterverband.elementAt(0).setAnzahlStudenten(semesterverband.getAnzahlStudenten());
		altSemesterverband.elementAt(0).setJahrgang(semesterverband.getJahrgang());
		altSemesterverband.elementAt(0).setStudiengang(semesterverband.getStudiengang());
		
		this.semesterverbandMapper.update(altSemesterverband.elementAt(0));
			
		return semesterverband;
	}
	
	public Dozent aendernDozent(Dozent dozent) throws RuntimeException {
		
		// Pröfung ob Vor- und Nachname angegeben wurden
		
		StringBuffer tempVorname = new StringBuffer();
		tempVorname.append(dozent.getVorname());
		StringBuffer tempNachname = new StringBuffer();
		tempNachname.append(dozent.getNachname());
		
						
		if ((tempVorname.length() == 0) || (tempNachname.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie Vor- und Nachname an");
		}
		
		
		// Pröfung des Vor- und Nachnamens auf Zahlen und bestimmte Sonderzeichen, diese sind nicht erlaubt
		
		if (!dozent.getVorname().matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\ö\\\"\\!\\^\\ö\\<\\>\\|\\;\\:\\#\\~\\@\\ö\\?\\(\\)\\ö\\ö]*") || 
				!dozent.getNachname().matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\ö\\\"\\!\\^\\ö\\<\\>\\|\\;\\:\\#\\~\\@\\ö\\?\\(\\)\\ö\\ö]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen im Vor- bzw. Nachnamen");
		}
		
		// Pröfung der Personalnummer auf fönfstellige Ziffernfolge
		
		if (!new Integer(dozent.getPersonalnummer()).toString().matches("[0-9]{5}")) {
			throw new IllegalArgumentException("Die Personalnummer ist nicht fünfstellig\noder es befinden sich darin nicht erlaubte Zeichen");
		}
		
		// Prüfen ob die Personalnummer bereits vergeben ist
		
		Vector<Dozent> alleDozenten = this.dozentMapper.findAll(false);
		
		for (Dozent d : alleDozenten) {
			if (dozent.getPersonalnummer() == d.getPersonalnummer() && d.getId() != dozent.getId()) {
				throw new IllegalArgumentException("Die Personalnummer ist bereits vergeben");
			}
		}
		
		// Laden der "alten" Version des Dozenten um einen Vergleich der Lehrveranstaltungen zu ermöglichen
		Vector<Integer> dozentID = new Vector<Integer>();
		dozentID.add(dozent.getId());
		Vector<Dozent> oldDozent = this.dozentMapper.findByKey(dozentID, true);
		
		// Vector mit ID's der Lehrveranstaltungen, welche nicht mehr vom Dozenten gehalten werden
		Vector<Integer> tempLehrveranstalungIDs = new Vector<Integer>();
		
		// Auslesen der ID's derer Lehrveranstaltungen, welche nicht mehr vom Dozenten gehalten werden
		if (oldDozent.elementAt(0).getLehrveranstaltungen() != null && oldDozent.elementAt(0).getLehrveranstaltungen().size() > 0) {
			
			Integer a = null;
			
			for (int i = 0; i < oldDozent.elementAt(0).getLehrveranstaltungen().size(); i++) {
				for (int j = 0; j < dozent.getLehrveranstaltungen().size(); j++) {
					if (oldDozent.elementAt(0).getLehrveranstaltungen().elementAt(i).getId() != dozent.getLehrveranstaltungen().elementAt(j).getId()) {
						a = oldDozent.elementAt(0).getLehrveranstaltungen().elementAt(i).getId();
					}
					else {
						a = null;
						break;
					}
				}
				if (a != null) {
					tempLehrveranstalungIDs.add(a);
					a = null;
				}
			}
		}
		
		/*
		 *  Prüfwert, falls Referenzen vom Dozent auf bestimmte Lehrveranstaltungen nicht gelöscht werden können,
		 *  da sie Belegungen mit einzig "diesem" Dozenten referenzieren -> false = kann nicht gelöscht werden
		 */
		
		Boolean check = true;
		
		StringBuffer eText = new StringBuffer("Die nicht mehr gehaltenen Lehrveranstaltungen können nicht gelöscht werden.\nFolgende Belegungen muessen zuerst "
				+ "entfernt oder von einem anderen Dozenten übernommen werden: \n\nID\t\t\tWochentag\t\t\tZeit\t\t\tLV\n");
		
		// Zuerst wird gepröft, ob der Dozent bestimmte Lehrveranstaltungen nun nicht mehr hält		
		if (tempLehrveranstalungIDs.size() > 0) {
			// Die Lehrveranstaltungen, die der Dozent nicht halten soll bzw. wird, werden nun geladen
			Vector<Lehrveranstaltung> tempLehrveranstalungen = this.lehrveranstaltungMapper.findByKey(tempLehrveranstalungIDs, true);
			// Jede dieser Lehrveranstaltungen wird nun gepröft, ob sie Belegungen enthölt
			for (int i = 0; i < tempLehrveranstalungen.size(); i++) {
				if (tempLehrveranstalungen.elementAt(i).getBelegungen() != null) {
					// Sollte eine Lehrveranstaltung Belegungen referenzieren, werden diese nun geladen
					Vector<Belegung> tempBelegungen = this.belegungMapper.findByLehrveranstaltung(tempLehrveranstalungen.elementAt(i));
					/*
					 *  Bei jeder Belegung wird schließlich gepröft, ob sie nur von einem Dozenten durchgeführt wird 
					 *  und ob es sich dabei um den hier (Methoden-Parameter) zu ändernden Dozenten hält.
					 *  Sollte dies der Fall sein, kann eine Änderung nicht durchgeführt werden, da zuerst die
					 *  Belegungen der zu löschenden Lehrveranstaltung gelöscht werden müssen oder aber sie
					 *  werden von einem anderen Dozenten gehalten
					 */
					for (int j = 0; j < tempBelegungen.size(); j++) {
						if(tempBelegungen.elementAt(j).getDozenten().size() <= 1 && (tempBelegungen.elementAt(j).getDozenten().elementAt(0).getId() == dozent.getId())) {
							check = false;
							eText.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + " " + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t");
							eText.append(tempLehrveranstalungen.elementAt(i).getBezeichnung() + "\n");
						}
					}
				}
			}
		}
		
		if(!check) {
			throw new RuntimeException(eText.toString());
		}
		else {
			this.dozentMapper.update(dozent);
			return dozent;
		}
	}
	
	public Zeitslot aendernZeitslot(Zeitslot zeitslot) throws RuntimeException {
		
		// Prüfung der Zeitangaben auf syntaktische Korrektheit
		if (!new Integer(zeitslot.getAnfangszeit()).toString().matches("[0-9]{3,4}") || !new Integer(zeitslot.getEndzeit()).toString().matches("[0-9]{3,4}")) {
			throw new IllegalArgumentException("Zeiten dürfen nur Zahlen von 0 bis 9 enthalten und müssen sich zwischen 08:15 und 21:00 Uhr befinden");
		}
		
		// Prüfung des Wochentags auf semantische Korrektheit
		if (!zeitslot.getWochentag().equals("Montag") || !zeitslot.getWochentag().equals("Dienstag") || !zeitslot.getWochentag().equals("Mittwoch") || 
				!zeitslot.getWochentag().equals("Donnerstag") || !zeitslot.getWochentag().equals("Freitag") || !zeitslot.getWochentag().equals("Samstag") || 
				!zeitslot.getWochentag().equals("Sonntag")) {
			throw new IllegalArgumentException("Wochentage muessen ausgeschrieben sein");
			 }
		
		if ((zeitslot.getEndzeit() - zeitslot.getAnfangszeit()) != 90) {
			throw new IllegalArgumentException("Ein Zeitslot muss einer Dauer von 90 Minuten entsprechen");
		}
		
		if ((zeitslot.getAnfangszeit() % 15) != 0 || (zeitslot.getAnfangszeit() % 15) != 0) {
			throw new IllegalArgumentException("Ein Zeitslot muss sich im Viertelstundentakt bewegen - XX:00/:15/:30/:45");
		}
		
		// Laden des vorhergehenden und nachfolgenden Zeitslots
		
		Vector<Zeitslot> davorZeitslot = null;
		Vector<Zeitslot> danachZeitslot = this.zeitslotMapper.findByKey(new Vector<Integer>(zeitslot.getId()+1));
		if(zeitslot.getId() > 1) {
			davorZeitslot = this.zeitslotMapper.findByKey(new Vector<Integer>(zeitslot.getId()-1));
		}

		// Prüfung des geänderten Zeitslots, ob dieser mit den vorgeschriebenen Pausen von 15 Minuten im Konflikt steht
		
		if (davorZeitslot != null) {
			if((zeitslot.getAnfangszeit() - davorZeitslot.elementAt(0).getEndzeit()) < 15) {
				throw new RuntimeException("Zum vorherigen Zeitslot müssen mindestens 15 Minuten Abstand sein");
			}
		}
		
		if((danachZeitslot.elementAt(0).getAnfangszeit() - zeitslot.getEndzeit()) < 15) {
			throw new RuntimeException("Zum nachfolgenden Zeitslot müssen mindestens 15 Minuten Abstand sein");
		}
		
		this.zeitslotMapper.update(zeitslot);
		return zeitslot;
	}
	
	public Lehrveranstaltung aendernLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException {
		
		// Prüfung ob min ein Studiengang angegeben wurde
		
		if (lehrveranstaltung.getStudiengaenge() == null || lehrveranstaltung.getStudiengaenge().size() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie mindestens einen Studiengang an");
		}
		
		// Prüfen ob eine Bezeichnung angegeben wurde
		
		if (lehrveranstaltung.getBezeichnung() == null || lehrveranstaltung.getBezeichnung().length() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie eine Bezeichnung an");
		}
		
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (!lehrveranstaltung.getBezeichnung().matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen in der Bezeichnung");
		}
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (lehrveranstaltung.getBezeichnung().matches("[0-9]*") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[0-9]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf keine Zahl sein oder mit einer beginnen");
		}
				
		if (lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[ ]{1}") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[/]{1}") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[-]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf mit keinem Leerzeichen oder Sonderzeichen beginnen");
		}
		
		// Prüfung ob die Bezichung mit einem Buchstabe beginnt und ob am ende nur eine Ziffer verwendet wurde
		/*
		if (!lehrveranstaltung.getBezeichnung().matches("[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}[ ]{0,1}[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}")) {
			throw new IllegalArgumentException("Bitte beginnen Sie die Bezeichnung mit einem Buchstaben\n"
					+ "Bitte verwenden Sie nur ein Leerzeichen in Folge\nBitte benutzen Sie nur eine Ziffer in Folge");
		}
		*/		
		// Prüfung ob am Ende ein Lehrzeichen steht
				
		if (lehrveranstaltung.getBezeichnung().lastIndexOf(" ") == lehrveranstaltung.getBezeichnung().length() - 1) {
			throw new IllegalArgumentException("Bitte entfernen Sie das Leerzeichen am Ende der Bezeichnung");
		}
		
		// Laden der "alten" Version der Lehrveranstaltung
		Vector<Integer> vI = new Vector<Integer>();
		vI.add(lehrveranstaltung.getId());
		Vector<Lehrveranstaltung> oldLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vI, true);
		
		// Prüfen ob die Bezeichnung oder das Kürzel bereits vorhanden sind
		
		Vector<Lehrveranstaltung> alleLVs = this.lehrveranstaltungMapper.findAll(false);
		
		for (Lehrveranstaltung l : alleLVs) {
			if (lehrveranstaltung.getBezeichnung().equals(l.getBezeichnung()) && lehrveranstaltung.getId() != l.getId()) {
				throw new IllegalArgumentException("Die Bezeichnung ist bereits vergeben, bitte ändern Sie diese");
			}
		}
		
		// Vector mit ID's der Studiengänge, welche nicht mehr der hier zu ändernden Lehrveranstaltung zugeordnet sind
		Vector<Integer> tempStudiengangIDs = new Vector<Integer>();
				
		// Auslesen der ID's derer Studiengänge, welche nicht mehr der hier zu ändernden Lehrveranstaltung zugeordnet sind
				
		Integer a1 = null;
		
		if ((oldLehrveranstaltung.elementAt(0).getStudiengaenge() != null && oldLehrveranstaltung.elementAt(0).getStudiengaenge().size() > 0) &&
				(lehrveranstaltung.getStudiengaenge() != null && lehrveranstaltung.getStudiengaenge().size() > 0)) {
			for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getStudiengaenge().size(); i++) {
				for (int j = 0; j < lehrveranstaltung.getStudiengaenge().size(); j++) {
					if (oldLehrveranstaltung.elementAt(0).getStudiengaenge().elementAt(i).getId() != lehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
						a1 = oldLehrveranstaltung.elementAt(0).getStudiengaenge().elementAt(i).getId();
					}
					else {
						a1 = null;
						break;
					}
				}
				if (a1 != null) {
					tempStudiengangIDs.add(a1);
					a1 = null;
				}
			}
		}
		
		/*
		 *  Prüfwert, falls Referenzen von der Lehrveranstaltung auf bestimmte Studiengänge nicht gelöscht werden können,
		 *  da Semesterverbände dieser Studiengänge eine Belegung mit "dieser" Lehrveranstaltung referenzieren
		 *  -> false = kann nicht gelöscht werden
		 */
		
		Boolean check1 = true;
		
		StringBuffer eText1 = new StringBuffer("Verbindungen zu Studiengängen können nicht entfernt werden.\nFolgende Belegungen müssen zuerst "
				+ "entfernt oder einem anderen Semesterverband zugeordnet werden: \n\n");
		
		// Zuerst wird geprüft ob fehlende Studiengönge identifiziert wurden
		if (tempStudiengangIDs.size() > 0) {
			// Nun werden die Belegungen dieser Lehrveranstaltung geladen
			Vector<Belegung> tempBelegungen = this.belegungMapper.findByLehrveranstaltung(lehrveranstaltung);
			// Für jeden Studiengang, welcher "dieser" Lehrveranstaltung nicht mehr zugeordnet sein soll... 
			if (tempBelegungen != null && tempBelegungen.size() > 0) {
				for (int i = 0; i < tempStudiengangIDs.size(); i++) {
					// ...werden alle Belegungen "dieser" Lehrveranstaltung durchsucht und in jeder dieser Belegung...
					for (int j = 0; j < tempBelegungen.size(); j++) {
						// ...werden alle zugeordneten Semesterverbände durchsucht
						if (tempBelegungen.elementAt(j).getSemesterverbaende() != null && tempBelegungen.elementAt(j).getSemesterverbaende().size() > 0) {
							for (int k = 0; k < tempBelegungen.elementAt(j).getSemesterverbaende().size(); k++) {
								/*
								 *  Sollte ein Semesterverband den "entfernten" Studiengängen entsprechen,
								 *  wird check auf "false" gesetzt und die Lehrveranstaltung kann nicht
								 *  geändert werden
								 */
								if(tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getStudiengang().getId() == tempStudiengangIDs.elementAt(i)) {
									check1 = false;
									eText1.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + "\t\t" + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t" + tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getStudiengang().getKuerzel()+ " " + tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getJahrgang() + "\n");
								}
							}
						}
					}
				}
			}			
		}
		
		if(!check1) {
			throw new RuntimeException(eText1.toString());
		}
		
		// Vector mit ID's der Dozenten, welche nicht mehr der Lehrveranstaltung zugeordnet sein sollen
		Vector<Integer> tempDozentIDs = new Vector<Integer>();
				
		// Auslesen der ID's derer Dozenten, welche nicht mehr der Lehrveranstaltung zugeordnet sein sollen
				
		Integer a2 = null;
		
		if (oldLehrveranstaltung.elementAt(0).getDozenten() != null && oldLehrveranstaltung.elementAt(0).getDozenten().size() > 0) {
			
			if (lehrveranstaltung.getDozenten() == null || lehrveranstaltung.getDozenten().size() == 0) {
				for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getDozenten().size(); i++) {
					tempDozentIDs.add(oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId());
				}
			}
			else {
				for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getDozenten().size(); i++) {
					for (int j = 0; j < lehrveranstaltung.getDozenten().size(); j++) {						
						if (oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId() != lehrveranstaltung.getDozenten().elementAt(j).getId()) {							
							a2 = oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId();
						}
						else {
							a2 = null;
							break;
						}
					}
					if (a2 != null) {
						tempDozentIDs.add(a2);
						a2 = null;
					}
				}
			}
		}
				
		/*
		 *  Prüfwert, falls Referenzen vom Dozent auf bestimmte Lehrveranstaltungen nicht gelöscht werden können,
		 *  da sie Belegungen mit einzig "diesem" Dozenten referenzieren -> false = kann nicht gelöscht werden
		 */
				
		Boolean check2 = true;
				
		StringBuffer eText2 = new StringBuffer("Die Verbindung zu bestimmten Dozenten kann nicht gelöscht werden.\nFolgende Belegungen müssen zuerst "
				+ "entfernt oder von einem anderen Dozenten übernommen werden: ");
				
				// Zuerst wird gepröft, ob Dozenten "dieser" Lehrveranstaltung nicht mehr zugeordnet sein sollen 		
				if (tempDozentIDs.size() > 0) {
					// Die Dozenten die "dieser" Lehrveranstaltung nicht mehr zugeordnet sein sollen werden nun geladen
					Vector<Dozent> tempDozenten = this.dozentMapper.findByKey(tempDozentIDs, true);
					// För jede dieser Dozenten wird nun auf vorhandene Belegungen geprüft
					if (tempDozenten != null && tempDozenten.size() > 0) {
						for (int i = 0; i < tempDozenten.size(); i++) {
							if (tempDozenten.elementAt(i).getBelegungen() != null && tempDozenten.elementAt(i).getBelegungen().size() > 0) {
								// Sollte ein Dozent Belegungen referenzieren, werden diese nun geladen
								Vector<Belegung> tempBelegungen = this.belegungMapper.findByDozent(tempDozenten.elementAt(i));
								/*
								 *  Bei jeder Belegung wird schlieölich gepröft, ob diese der "hier" zu öndernden
								 *  Lehrveranstaltung (Methoden-Parameter) entspricht. Sollte dies der Fall sein, kann 
								 *  eine önderung nicht durchgeföhrt werden, da zuerst die Belegungen der zu löschenden 
								 *  Dozenten gelöscht werden mössen oder aber sie mössen einem anderen Dozenten zugeordnet
								 *  werden
								 */
								for (int j = 0; j < tempBelegungen.size(); j++) {
									if(tempBelegungen.elementAt(j).getLehrveranstaltung().getId() == lehrveranstaltung.getId()) {
										check2 = false;
										eText2.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + " " + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t");
										eText2.append(tempDozenten.elementAt(i).getTitel() + " ");
										eText2.append(tempDozenten.elementAt(i).getVorname() + " ");
										eText2.append(tempDozenten.elementAt(i).getNachname() + "\n");
									}
								}
							}
						}
					}
				}		
		
		if(!check2) {
			throw new RuntimeException(eText2.toString());
		}
		
		if((lehrveranstaltung.getUmfang() != oldLehrveranstaltung.elementAt(0).getUmfang()) && (oldLehrveranstaltung.elementAt(0).getBelegungen() != null)) {
			throw new RuntimeException("Es müssen erst alle Belegungen dieser Lehrveranstaltung gelöscht werden, bevor deren Umfang geändert werden kann");
		}
		
		if((lehrveranstaltung.getStudiensemester() != oldLehrveranstaltung.elementAt(0).getStudiensemester()) && (oldLehrveranstaltung.elementAt(0).getBelegungen() != null)) {
			throw new RuntimeException("Es müssen erst alle Belegungen dieser Lehrveranstaltung gelöscht werden, bevor deren Studiensemester geändert werden kann");
		}
				
		this.lehrveranstaltungMapper.update(lehrveranstaltung);
		return lehrveranstaltung;						
		
	}
	
	public Belegung aendernBelegung(Belegung belegung) throws RuntimeException {
		
		// Prüfung ob ein Dozent ausgwählt wurde
		
		if(belegung.getDozenten().size() <= 0) {
			throw new RuntimeException("Bitte fügen Sie einen Dozenten hinzu");
		}
		
		// Prüfung ob ein Dozent mehrmals ausgewählt wurde
		
		if(belegung.getDozenten().size() > 1) {
			for (int i = 0; i < belegung.getDozenten().size(); i++) {
				int multiplzitaetDozent = 0;
				for (int j = 0; j < belegung.getDozenten().size(); j++) {
					if (belegung.getDozenten().elementAt(i).getId() == belegung.getDozenten().elementAt(j).getId()) {
						multiplzitaetDozent = multiplzitaetDozent + 1;
					}
				}
				if(multiplzitaetDozent > 1) {
					throw new RuntimeException("Bitte fögen Sie einen Dozenten nur einfach zur Belegung");
				}
			}
		}
				
		// Prüfung ob das Studiensemester der gewünschten Lehrveranstaltung mit dem des Semesterverbandsvereinbar ist
		
		Integer semesterAlt = null;
		Integer semesterNeu = null;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			
			Semesterverband tempSemVerband = belegung.getSemesterverbaende().elementAt(i);
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(tempSemVerband.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};

			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {
				
				
			int zaehler = 0; 
		
			for (int j = semMonat-1; j < calendar.length; j++ ) {
				
				zaehler++;
				
				if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
					break;
				}
				if(calendar[j] == 12) {
					j = -1;
					jahresDiff--;
				}
			}

			int studienSem = zaehler / 6;
			int studienSemMonat = zaehler;
			
			if (zaehler != 0) {
				studienSemMonat = zaehler % 6;
			}
			
			
					
			if (studienSem == 0) {
				semesterAlt = 1;
				semesterNeu = 0;
			}
			
			else if (studienSemMonat == 0) {
				semesterAlt = studienSem;
				semesterNeu = studienSem + 1;
			}
			
					
			else {
				semesterAlt = studienSem + 1;
			}
			}
			else {
				semesterAlt = 1;
			}
		}
		
		
		if (semesterNeu == null || semesterNeu == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
			}
		}
		
		else if (semesterAlt == null || semesterAlt == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
			}
		}		
		
		else {
			if ((belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) && (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu)) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
		}
		}
				
		// Prüfen ob der Raum genügend Kapazität aufweist för die referenzierten Semesterverbände
		
		/*
		 * Pröfung deaktiviert, da nun nur Räume mit genögend Kapazität dem Client zur Verfügung gestellt werden
		 * Stand 06.01.2014
		 *
		
		int studenten = 0;
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			studenten = studenten + belegung.getSemesterverbaende().elementAt(i).getAnzahlStudenten();
		}
		if (belegung.getRaum().getKapazitaet() < studenten) {
			throw new RuntimeException("Der gewuenschte Raum hat nicht genuegend Plaetze");
		}
		*/
		
		// Prüfen ob der Raum zum gewönschten Zeitslot noch verfügbar ist
		
		/*
		 * Prüfung deaktiviert, da nun nur freie Räume dem Client zur Verfügung gestellt werden
		 * Stand: 05.01.2013
		
		Vector<Belegung> raumBelegungen = this.belegungMapper.findByRaum(belegung.getRaum());
		
		for (int i = 0; i < raumBelegungen.size(); i++) {
			if ((belegung.getZeitslot().getId() == raumBelegungen.elementAt(i).getZeitslot().getId()) && (belegung.getId() != raumBelegungen.elementAt(i).getId())) {
				
				throw new RuntimeException("Der gewönschte Raum ist zum gewönschten Zeitslot schon belegt");
			}
		}
		*/
		
		// Prüfen ob der Semesterverband zum gewünschten Zeitslot verfügbar ist
		
		/*
		 * Pröfung deaktiviert, da Zeitslots vom Client nicht gewöhlt werden können
		 * Stand: 05.01.2013
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> semVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			for (int j = 0; j < semVerBelegungen.size(); j++) {
				if ((belegung.getZeitslot().getId() == semVerBelegungen.elementAt(j).getZeitslot().getId()) && (belegung.getId() != semVerBelegungen.elementAt(j).getId())) {
					throw new RuntimeException("Der Semesterverband ist zu diesem Zeitpunkt bereits eingeteilt");
				}
			}
		}
		*/
		
		// Pröfen ob der Dozent zum gewönschten Zeitslot verfögbar ist
		
		/*
		 * Prüfung deaktiviert, da nun nur freie Dozenten dem Client zur Verfügung gestellt werden
		 * Stand 06.01.2014
		
		for (int i = 0; i < belegung.getDozenten().size(); i++) {			
			Vector<Belegung> dozentenBelegungen = this.belegungMapper.findByDozent(belegung.getDozenten().elementAt(i));
			if (dozentenBelegungen != null && dozentenBelegungen.size() > 0) {
				for (int j = 0; j < dozentenBelegungen.size(); j++) {
					if ((belegung.getZeitslot().getId() == dozentenBelegungen.elementAt(j).getZeitslot().getId()) && (belegung.getId() != dozentenBelegungen.elementAt(j).getId()) && (belegung.getLehrveranstaltung().getId() != dozentenBelegungen.elementAt(j).getLehrveranstaltung().getId())) {
						throw new RuntimeException("Der Dozent ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		*/
		
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getLehrveranstaltung().getId());
		
		// Pröfung ob sich die Lehrveranstaltung und die Semesterverbände im gleichen Studiengang befinden
		
		Lehrveranstaltung tempLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vi, true).elementAt(0);
		
		boolean check = false;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			check = false;
			if (tempLehrveranstaltung.getStudiengaenge() != null && tempLehrveranstaltung.getStudiengaenge().size() > 0) {
				for (int j = 0; j < tempLehrveranstaltung.getStudiengaenge().size(); j++) {
					if (belegung.getSemesterverbaende().elementAt(i).getStudiengang().getId() == tempLehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
						check = true;
						break;
					}
				}
			}
			else {
				throw new RuntimeException("Die Lehrveranstaltung ist keinem Studiengang zugeordnet");
			}
		}
		
		if (!check) {
			throw new RuntimeException("Lehrveranstaltung und Semesterverband befinden sich nicht im gleichen Studiengang");
		}
			
		// Pröfung ob der Umfang (SWS) einer Lehrveranstaltung für einen Semesterverband bereits erreicht wurde
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> tempSemVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			if (tempSemVerBelegungen != null && tempSemVerBelegungen.size() > 0) {
				int countSWS = 0;
				for (int j = 0; j < tempSemVerBelegungen.size(); j++) {
					if((belegung.getLehrveranstaltung().getId() == tempSemVerBelegungen.elementAt(j).getLehrveranstaltung().getId()) && (belegung.getId() != tempSemVerBelegungen.elementAt(j).getId())) {
						countSWS = countSWS + 2;
					}
				}
				if (belegung.getLehrveranstaltung().getUmfang() < (countSWS + 2)) {
					throw new RuntimeException("Der vorgesehene Umfang (SWS) der Lehrveranstaltung wurde für Semesterverband " + belegung.getSemesterverbaende().elementAt(i).getStudiengang().getKuerzel() + " " + belegung.getSemesterverbaende().elementAt(i).getJahrgang());
				}
			}
		}
		
		// Dozenten hinsichtlich ihrer Referenzen zu Lehrveranstaltungen aktualisieren
		
		/*
		 * Funktion deaktiviert, da die Programmpolitik derzeit es nicht vorsieht auch temporör gehaltene Lehrveranstaltungen
		 * zum Repertoir eines Dozenten hinzuzufögen
		 * Stand: 06.01.2014

		Vector<Integer> vi2 = new Vector<Integer>();
		for (int i = 0; i < belegung.getDozenten().size(); i++) {
			vi2.add(belegung.getDozenten().elementAt(i).getId());
		}
		Vector<Dozent> vd = this.dozentMapper.findByKey(vi2, true);
		for (int i = 0; i < vd.size(); i++) {
			boolean check1 = true;
			if (vd.elementAt(i).getLehrveranstaltungen() != null && vd.elementAt(i).getLehrveranstaltungen().size() > 0) {
				for (int j = 0; j < vd.elementAt(i).getLehrveranstaltungen().size(); j++) {
					if (belegung.getLehrveranstaltung().getId() == vd.elementAt(i).getLehrveranstaltungen().elementAt(j).getId()) {
						check1 = false;
					}
				}
				if (check1) {
					vd.elementAt(i).getLehrveranstaltungen().add(belegung.getLehrveranstaltung());
					this.dozentMapper.update(vd.elementAt(i));
				}
			}
		}
		
		*/
		
		
		return this.belegungMapper.update(belegung);
				
	}
	
	public Studiengang aendernStudiengang(Studiengang studiengang) throws RuntimeException {
		
		// Prüfung ob Bezeichung und Kuerzel angegeben wurden
		
		StringBuffer bezeichnung = new StringBuffer();
		bezeichnung.append(studiengang.getBezeichnung());
		StringBuffer kuerzel = new StringBuffer();
		kuerzel.append(studiengang.getKuerzel());
		
		if ((bezeichnung.length() == 0) || (kuerzel.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie die Bezeichnung und das Kürzel an");
		}
		
		if (studiengang.getKuerzel().matches("[0-9]*")) {
			throw new IllegalArgumentException("Das Kürzel darf nicht aus reinen Zahlen bestehen");
		}
		
		if (!studiengang.getKuerzel().matches("[A-Z]{2,4}[1-20]{0,1}")) {
			throw new IllegalArgumentException("Das Kürzel muss anfangs 2 bis 4 Großbuchstaben enthalten und darf am Ende eine Zahl"
					+ " von 1 bis 20 enthalten");
		}
		
		// Prüfen ob die Bezeichnung und/oder das Kürzel bereits vergeben sind
		
		Vector<Studiengang> alleSGs = this.studiengangMapper.findAll(false);
				
		for (Studiengang s : alleSGs) {
			if((bezeichnung.equals(s.getBezeichnung()) || kuerzel.equals(s.getKuerzel())) && studiengang.getId() != s.getId()) {
				throw new IllegalArgumentException("Die Bezeichnung und/oder das Kürzel bereits sind vergeben");
			}
		}
		
		// Prüfung ob eine entfernte Lehrveranstaltung noch durch einen anderen Studiengang referenziert wird
		
		// Laden der "alten" Version des Studiengangs
		Vector<Integer> vI = new Vector<Integer>();
		vI.add(studiengang.getId());
		Vector<Studiengang> oldStudiengang = this.studiengangMapper.findByKey(vI, true);
				
		// Vector mit ID's der Lehrveranstaltungen, welche nicht mehr dem hier zu ändernden Studiengang zugeordnet sind
		Vector<Integer> tempLehrveranstaltungIDs = new Vector<Integer>();
						
		// Auslesen der ID's derer Lehrveranstaltungen, welche nicht mehr dem hier zu ändernden Studiengang zugeordnet sind
						
		Integer a1 = null;
				
		if ((oldStudiengang.elementAt(0).getLehrveranstaltungen() != null && oldStudiengang.elementAt(0).getLehrveranstaltungen().size() > 0) &&
				(studiengang.getLehrveranstaltungen() != null && studiengang.getLehrveranstaltungen().size() > 0)) {
			for (int i = 0; i < oldStudiengang.elementAt(0).getLehrveranstaltungen().size(); i++) {
				for (int j = 0; j < studiengang.getLehrveranstaltungen().size(); j++) {
					if (oldStudiengang.elementAt(0).getLehrveranstaltungen().elementAt(i).getId() != studiengang.getLehrveranstaltungen().elementAt(j).getId()) {
						a1 = oldStudiengang.elementAt(0).getLehrveranstaltungen().elementAt(i).getId();
					}
					else {
						a1 = null;
						break;
					}
				}
				if (a1 != null) {
					tempLehrveranstaltungIDs.add(a1);
					a1 = null;
				}
			}
		}
		
		// Auslesen der Lehrveranstaltungen aus der DB, welche nicht mehr dem Studiengang zugeordnet sein sollen
		
		if (tempLehrveranstaltungIDs.size() > 0) {
			Vector<Lehrveranstaltung> lostLV = this.lehrveranstaltungMapper.findByKey(tempLehrveranstaltungIDs, true);
			for (Lehrveranstaltung lv : lostLV) {
				if (lv.getStudiengaenge().size() == 1) {
					throw new RuntimeException("Die Lehrveranstaltung" + lv.getBezeichnung() + "kann nicht entfernt werden, da sie durch keinen anderen Studiengang referenziert wird\n"
							+ "Bei Bedarf löschen Sie bitte diese Lehrveranstaltung oder weisen ihr einen anderen Studiengang zu");
				}
			}
		}
		
		
		return this.studiengangMapper.update(studiengang);
		
	}
	
	public Raum aendernRaum(Raum raum) throws RuntimeException {
		
		if (!raum.getBezeichnung().matches("[W]{1}[0-9]{3}|[W-N]{1}[0-9]{3}|[N]{1}[0-9]{3}")) {
			throw new IllegalArgumentException("Die Bezeichnung entspricht nicht den Vorgaben");
		}
		
		
		if (raum.getKapazitaet() == 0) {
			throw new IllegalArgumentException("Die Kapazität darf nicht 0 sein");
		}
		
		
		// Prüfziffer ("0" steht für Akzeptiert)
		int accepted = 0;

		
		// Auslesen aller Belegungen die den Raum referenzieren, der aktualisiert werden soll
		
		Vector<Belegung> tempBelegungen = this.belegungMapper.findByRaum(raum);
		
		// Lokale Variable zum festhalten der SemesterverbandID's, welche von "vB" referenziert werden 
		Vector<Integer> vI = new Vector<Integer>();
		if (tempBelegungen != null && tempBelegungen.size() > 0) {
			// Hinzufögen der SemesterverbandID's zu "vI", falls sie nicht bereits enthalten sind
			for(int i = 0; i < tempBelegungen.size(); i++) {
				for (int j = 0; j < tempBelegungen.elementAt(i).getSemesterverbaende().size(); j++){
				
					if (!vI.contains((Integer)tempBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getId())) {
						vI.add(tempBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getId());
					}
				}			
			}
			//Laden der betroffenen Semesterverbönde mittels "vI"		
			Vector<Semesterverband> vS = SemesterverbandMapper.semesterverbandMapper().findByKey(vI, false);

			//Pröfen ob eine Kapazitätsänderung mit den referenzierten Semesterverbandsgrößen vereinbar ist
			for(int i = 0; i < vS.size(); i++) {
				if (raum.getKapazitaet() < vS.elementAt(i).getAnzahlStudenten()) {
					accepted++;
				}
			}
		}
		
		Vector<Raum> alleRaeume = raumMapper.findAll();
		
		for (Raum r : alleRaeume) {
			if (raum.getBezeichnung().equals(r.getBezeichnung()) && raum.getId() != r.getId()) {
				throw new IllegalArgumentException("Bitte wählen Sie eine andere Bezeichung, dieser Raum existiert bereits");
			}
		}

		if ( accepted == 0) {
			return this.raumMapper.update(raum);
		}
		else {
			throw new RuntimeException("Die Kapazität ist för Semesterverbönde, welche bereits Lehrveranstaltungen in diesem Raum zugeordnet sind, zu klein");
		}

	}
	
	/*
	 * ändern der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Semesterverband anlegenSemesterverband (String anzahlStudenten, String jahrgang, Studiengang studiengang) throws RuntimeException {
		StringBuffer tempJahrgang = new StringBuffer();
		tempJahrgang.append(jahrgang);
		
		// Es wird gepröft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde
		
		if (!tempJahrgang.substring(0,2).equals("SS") && !tempJahrgang.substring(0,2).equals("WS") || !tempJahrgang.substring(2,4).equals("20")) {
			throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nBeachten Sie auch die Gross-/Kleinschreibweise");			
		}
		
		if ((tempJahrgang.substring(0,2).equals("SS") && tempJahrgang.length() != 6) || (tempJahrgang.substring(0,2).equals("WS") && tempJahrgang.length() != 9) ||
				(tempJahrgang.substring(0,2).equals("WS") && tempJahrgang.charAt(6) != new Character('/'))) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
		}
		
		if (tempJahrgang.substring(0,2).equals("SS")) {
			try {
				Integer.parseInt(tempJahrgang.substring(4,6));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
		}
		
		if (tempJahrgang.substring(0,2).equals("WS")) {
			int vJahr;
			int nJahr;
			try {
				vJahr = Integer.parseInt(jahrgang.substring(4,6));
				nJahr = Integer.parseInt(jahrgang.substring(7,9));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
			if (nJahr - vJahr != 1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nNur ein Jahreswechsel erlaubt");
			}
		}
		
		// Prüfung ob das Feld "anzahlStudenten" nur Zahlen enthält
		
		if (!anzahlStudenten.matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Bitten geben Sie die Anzahl der Studenten an");
		}
		
		if (studiengang == null) {
			throw new IllegalArgumentException("Bitte geben Sie einen Studiengang an");
		}
		
		Vector<Semesterverband> sgNachSV = this.auslesenSemesterverbaendeNachStudiengang(studiengang);
		
		for (Semesterverband sv : sgNachSV) {
			if (jahrgang == sv.getJahrgang()) {
				throw new IllegalArgumentException("Dieser Semesterverband existiert bereits");
			}
		}
		
		// Neues Semesterverband-Objekt erzeugen
		
		Semesterverband neuSemVerband = new Semesterverband();
		neuSemVerband.setJahrgang(jahrgang);
		neuSemVerband.setAnzahlStudenten(Integer.parseInt(anzahlStudenten));
		neuSemVerband.setStudiengang(studiengang);
		
		return this.semesterverbandMapper.insertIntoDB(neuSemVerband);
	}
	
	public Dozent anlegenDozent(String vorname, String nachname, String personalnummer, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException {
		
		// Pröfung ob Vor- und Nachname angegeben wurden
		
		StringBuffer tempVorname = new StringBuffer();
		tempVorname.append(vorname);
		StringBuffer tempNachname = new StringBuffer();
		tempNachname.append(nachname);
				
		if ((tempVorname.length() == 0) || (tempNachname.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie Vor- und Nachname an");
		}
		
		
		// Pröfung des Vor- und Nachnamens auf Zahlen und bestimmte Sonderzeichen, diese sind nicht erlaubt
		
		if (!vorname.matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\ö\\\"\\!\\^\\ö\\<\\>\\|\\;\\:\\#\\~\\@\\ö\\?\\(\\)\\ö\\ö]*") || 
				!nachname.matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\ö\\\"\\!\\^\\ö\\<\\>\\|\\;\\:\\#\\~\\@\\ö\\?\\(\\)\\ö\\ö]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen im Vor- bzw. Nachnamen");
		}
				
		// Pröfung der Personalnummer auf fünfstellige Ziffernfolge
				
		if (!personalnummer.matches("[0-9]{5}")) {
			throw new IllegalArgumentException("Die Personalnummer ist nicht fünfstellig\noder es befinden sich darin nicht erlaubte Zeichen");
		}
		
		// Prüfen ob die Personalnummer bereits vergeben ist
		
		Vector<Dozent> alleDozenten = this.dozentMapper.findAll(false);
				
		for (Dozent d : alleDozenten) {
			if (new Integer(personalnummer) == d.getPersonalnummer()) {
				throw new IllegalArgumentException("Die Personalnummer ist bereits vergeben");
			}
		}
		
		// Neues Dozent-Objekt erzeugen
		
		Dozent neuDozent = new Dozent();
		neuDozent.setVorname(vorname);
		neuDozent.setNachname(nachname);
		neuDozent.setPersonalnummer(new Integer(personalnummer));
		if (lehrveranstaltungen != null && lehrveranstaltungen.size() > 0) {
			neuDozent.setLehrveranstaltungen(lehrveranstaltungen);
		}
		
		return this.dozentMapper.insertIntoDB(neuDozent);
	}
	
	/*
	public Dozent anlegenDozent(String vorname, String nachname, int personalnummer) throws RuntimeException {
		
		Vector<Lehrveranstaltung> keineLV = null;
		
		return this.anlegenDozent(vorname, nachname, personalnummer, keineLV);
	}
	*/
		
	public Zeitslot anlegenZeitslot(int anfangszeit, int endzeit, String wochentag) throws RuntimeException {
		
		// Das Anlegen eines neuen Zeitslots ist bis dato nicht vorgesehen - Stand: 12.12.2013
		
		return null;
	}
	
	public Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, Vector<Dozent> dozenten) throws RuntimeException {
		
		// Prüfung ob mindestens ein Studiengang angegeben wurde
		
		if (studiengaenge == null || studiengaenge.size() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie mindestens einen Studiengang an");
		}
		
		// Prüfung ob eine Bezeichnung angeben wurde
		
		if (bezeichnung == null || bezeichnung.length() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie eine Bezeichnung an");
		}
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (!bezeichnung.matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen in der Bezeichnung");
		}
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (bezeichnung.matches("[0-9]*") || bezeichnung.substring(0, 1).matches("[0-9]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf keine Zahl sein oder mit einer beginnen");
		}
		
		if (bezeichnung.substring(0, 1).matches("[ ]{1}") || bezeichnung.substring(0, 1).matches("[/]{1}") || bezeichnung.substring(0, 1).matches("[-]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf mit keinem Leerzeichen oder Sonderzeichen beginnen");
		}
		
		
		// Prüfung ob die Bezichung mit einem Buchstabe beginnt und ob am ende nur eine Ziffer verwendet wurde
		/*
		if (!bezeichnung.matches("[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}[ ]{0,1}[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}")) {
			throw new IllegalArgumentException("Bitte beginnen Sie die Bezeichnung mit einem Buchstaben\n"
					+ "Bitte verwenden Sie nur ein Leerzeichen in Folge\nBitte benutzen Sie nur eine Ziffer in Folge");
		}
		*/
		// Prüfung ob am Ende ein Lehrzeichen steht
		
		if (bezeichnung.lastIndexOf(" ") == bezeichnung.length() - 1) {
			throw new IllegalArgumentException("Bitte entfernen Sie das Leerzeichen am Ende der Bezeichnung");
		}
		
		/* Prüfungen deaktiviert, da keine freie Eingabe mehr möglich
		 * Stand: 08.01.2014
		 *
		
		// Pröfung ob das Feld "umfang" nur Zahlen enthält und nicht leer ist
		
		if (!new Integer(umfang).toString().matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Bitten geben Sie die Anzahl der Studenten an");
		}
		
		// Pröfung ob der Inahlt Feld "umfang" durch 2 teilbar ist
		
		if ((umfang % 2) != 0) {
			throw new IllegalArgumentException("Der Umfang muss durch 2 teilbar sein");
		}
		
		// Pröfung ob das Feld "Studiensemester" nur Zahlen enthölt und nicht leer ist
		
		if (!new Integer(umfang).toString().matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Bitten geben Sie das Studiensemester an");
		}
		*/
		
		// Prüfen ob die Bezeichnung oder das Kürzel bereits vorhanden sind
		
		Vector<Lehrveranstaltung> alleLVs = this.lehrveranstaltungMapper.findAll(false);
				
		for (Lehrveranstaltung l : alleLVs) {
			if (bezeichnung.equals(l.getBezeichnung())) {
				throw new IllegalArgumentException("Die Bezeichnung ist bereits vergeben, bitte ändern Sie diese");
			}
		}
		
		// Neues Lehrveranstaltung-Objekt erzeugen
		
		Lehrveranstaltung neuLehrveranstaltung = new Lehrveranstaltung();
		neuLehrveranstaltung.setUmfang(umfang);
		neuLehrveranstaltung.setBezeichnung(bezeichnung);
		neuLehrveranstaltung.setStudiensemester(studiensemester);
		neuLehrveranstaltung.setStudiengaenge(studiengaenge);
		if (dozenten != null) {
			neuLehrveranstaltung.setDozenten(dozenten);
		}
		
		return this.lehrveranstaltungMapper.insertIntoDB(neuLehrveranstaltung);
		
	}
	
	public Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge) throws RuntimeException {
		
		Vector<Dozent> keineDozenten = null;
		
		return this.anlegenLehrveranstaltung(umfang, bezeichnung, studiensemester, studiengaenge, keineDozenten);
	}
	
	public Belegung anlegenBelegung(Lehrveranstaltung lehrveranstaltung, Raum raum, Zeitslot zeitslot, Vector<Dozent> dozenten, Vector<Semesterverband> semesterverbaende) throws RuntimeException {
		
		// Neues Belegung-Objekt erzeugen
		
		Belegung belegung = new Belegung();
		belegung.setLehrveranstaltung(lehrveranstaltung);
		belegung.setRaum(raum);
		belegung.setZeitslot(zeitslot);
		belegung.setDozenten(dozenten);
		belegung.setSemesterverbaende(semesterverbaende);
		
		// Pröfung ob ein Dozent mehrmals ausgewöhlt wurde
		
		if(belegung.getDozenten().size() > 1) {
			for (int i = 0; i < belegung.getDozenten().size(); i++) {
				int multiplzitaetDozent = 0;
				for (int j = 0; j < belegung.getDozenten().size(); j++) {
					if (belegung.getDozenten().elementAt(i).getId() == belegung.getDozenten().elementAt(j).getId()) {
						multiplzitaetDozent = multiplzitaetDozent + 1;
					}
				}
				if(multiplzitaetDozent > 1) {
					throw new RuntimeException("Bitte fügen Sie einen Dozenten nur einfach zur Belegung");
				}
			}
		}
		
		// Pröfung ob das Studiensemester der gewünschten Lehrveranstaltung mit dem der Semesterverbände vereinbar ist
		
		Integer semesterAlt = null;
		Integer semesterNeu = null;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			
			Semesterverband tempSemVerband = belegung.getSemesterverbaende().elementAt(i);
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(tempSemVerband.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};

			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {
				
			
			int zaehler = 0;
			
			for (int j = semMonat-1; j < calendar.length; j++ ) {
				
				zaehler++;
				
				if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
					break;
				}
				if(calendar[j] == 12) {
					j = -1;
					jahresDiff--;
				}
			}
			
			int studienSem = zaehler / 6;
			int studienSemMonat = zaehler;
			
			if (zaehler != 0) {
				studienSemMonat = zaehler % 6;
			}
			
			

			
			if (studienSem == 0) {
				semesterAlt = 1;
				semesterNeu = 0;
			}
			
			else if (studienSemMonat == 0) {
				semesterAlt = studienSem;
				semesterNeu = studienSem + 1;
			}
			
					
			else {
				semesterAlt = studienSem + 1;
			}
			}
			else {
				semesterAlt = 1;
			}
		}
		
		if (semesterNeu == null || semesterNeu == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
			}
		}
		
		else if (semesterAlt == null || semesterAlt == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
			}
		}		
		
		else {
			if ((belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) && (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu)) {
				throw new RuntimeException("Die Lehrveranstaltung ist för ein anderes Studiensemester vorgesehen");
			}
		}
				
		
		
		// Pröfen ob der Raum zum gewönschten Zeitslot schon noch verfögbar ist
		
		/*
		 * Pröfung deaktiviert, da dem Client nur freie Röume zur Verfögung gestellt werden
		 * Stand: 07.01.2014
		
		Vector<Belegung> raumBelegungen = this.belegungMapper.findByRaum(belegung.getRaum());
		if (raumBelegungen != null) {
			for (int i = 0; i < raumBelegungen.size(); i++) {
				if (belegung.getZeitslot().getId() == raumBelegungen.elementAt(i).getZeitslot().getId()) {
					throw new RuntimeException("Der gewönschte Raum ist zum gewönschten Zeitslot schon belegt");
				}
			}
		}
		*/
		
		// Pröfen ob die Semesterverbönde zum gewönschten Zeitslot verfögbar sind
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> semVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			if (semVerBelegungen != null) {
				for (int j = 0; j < semVerBelegungen.size(); j++) {
					if (belegung.getZeitslot().getId() == semVerBelegungen.elementAt(j).getZeitslot().getId()) {
						throw new RuntimeException("Ein Semesterverband ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		
		// Pröfen ob der Dozent zum gewönschten Zeitslot verfögbar ist
		
		for (int i = 0; i < belegung.getDozenten().size(); i++) {
			Vector<Belegung> dozentenBelegungen = this.belegungMapper.findByDozent(belegung.getDozenten().elementAt(i));
			if (dozentenBelegungen != null) {
				for (int j = 0; j < dozentenBelegungen.size(); j++) {
					if (belegung.getZeitslot().getId() == dozentenBelegungen.elementAt(j).getZeitslot().getId()) {
						throw new RuntimeException("Ein Dozent ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getLehrveranstaltung().getId());
		
		// Pröfung ob sich die Lehrveranstaltung und die Semesterverbönde im gleichen Studiengang sind
		
		Lehrveranstaltung tempLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vi, true).elementAt(0);
		
		boolean check = false;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			check = false;
			for (int j = 0; j < tempLehrveranstaltung.getStudiengaenge().size(); j++) {
				if (belegung.getSemesterverbaende().elementAt(i).getStudiengang().getId() == tempLehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
					check = true;
					break;
				}
			}
		}
		
		if (!check) {
			throw new RuntimeException("Lehrveranstaltung und Semesterverband befinden sich nicht im gleichen Studiengang");
		}
			
		// Pröfung ob der Umfang (SWS) einer Lehrveranstaltung för einen Semesterverband bereits erreicht wurde
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> tempSemVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			int countSWS = 0;
			if (tempSemVerBelegungen != null) {
				for (int j = 0; j < tempSemVerBelegungen.size(); j++) {
					if(belegung.getLehrveranstaltung().getId() == tempSemVerBelegungen.elementAt(j).getLehrveranstaltung().getId()) {
						countSWS = countSWS + 2;
					}
				}
			}
			if (belegung.getLehrveranstaltung().getUmfang() < (countSWS + 2)) {
				throw new RuntimeException("Der vorgesehene Umfang (SWS) der Lehrveranstaltung wurde für Semesterverband " + belegung.getSemesterverbaende().elementAt(i).getStudiengang().getKuerzel() + " " + belegung.getSemesterverbaende().elementAt(i).getJahrgang());
			}
		}
		
		return this.belegungMapper.insertIntoDB(belegung);
				
	}
		
	public Studiengang anlegenStudiengang(String bezeichnung, String kuerzel, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException {
		
		// Pröfung ob Bezeichung und Kuerzel angegeben wurden
		
		StringBuffer tempBezeichnung = new StringBuffer();
		tempBezeichnung.append(bezeichnung);
		StringBuffer tempKuerzel = new StringBuffer();
		tempKuerzel.append(kuerzel);
		
		if ((tempBezeichnung.length() == 0) || (tempKuerzel.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie die Bezeichnung und das Kürzel an");
		}
		
		if (kuerzel.matches("[0-9]*")) {
			throw new IllegalArgumentException("Das Kürzel darf nicht aus reinen Zahlen bestehen");
		}
		
		if (!kuerzel.matches("[A-Z]{2,4}[1-20]{0,1}")) {
			throw new IllegalArgumentException("Das Kürzel muss anfangs 2 bis 4 Großbuchstaben enthalten und darf am Ende eine Zahl"
					+ " von 1 bis 20 enthalten");
		}
		
		// Prüfen ob die Bezeichnung und/oder das Kürzel bereits vergeben sind
		
		Vector<Studiengang> alleSGs = this.studiengangMapper.findAll(false);
		
		for (Studiengang s : alleSGs) {
			if(bezeichnung.equals(s.getBezeichnung()) || kuerzel.equals(s.getKuerzel())) {
				throw new IllegalArgumentException("Die Bezeichnung und/oder das Kürzel bereits sind vergeben");
			}
		}
		
		// Neues Studiengang-Objekt erzeugen
		
		Studiengang neuStudiengang = new Studiengang();
		neuStudiengang.setBezeichnung(bezeichnung);
		neuStudiengang.setKuerzel(kuerzel);
		if (lehrveranstaltungen != null) {
			neuStudiengang.setLehrveranstaltungen(lehrveranstaltungen);
		}
		
		return this.studiengangMapper.insertIntoDB(neuStudiengang);
		
	}
	
	public Studiengang anlegenStudiengang(String bezeichnung, String kuerzel) throws RuntimeException {
		
		Vector<Lehrveranstaltung> keineLV = null;
		
		return anlegenStudiengang(bezeichnung, kuerzel, keineLV);
		
	}
	
	public Raum anlegenRaum(String bezeichnung, String kapazitaet) throws RuntimeException {
		
		// Prüfung ob Bezeichung und Kapazität angegeben wurden
		
		StringBuffer tempBezeichnung = new StringBuffer();
		tempBezeichnung.append(bezeichnung);
		StringBuffer tempKapazitaet = new StringBuffer();
		tempKapazitaet.append(kapazitaet);
				
		if ((tempBezeichnung.length() == 0) || (tempKapazitaet.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie die Bezeichnung und die Kapazität an");
		}
		
		if (!bezeichnung.matches("[W]{1}[0-9]{3}|[W-N]{1}[0-9]{3}|[N]{1}[0-9]{3}")) {
			throw new IllegalArgumentException("Die Bezeichnung entspricht nicht den Vorgaben");
		}
		
		try {
			if (Integer.parseInt(kapazitaet) == 0) {
				throw new IllegalArgumentException("Die Kapazität darf nicht 0 sein");
			}
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Bitte geben Sie nur Zahlen für die Kapazität ein");
		}
		
		Raum neuRaum = new Raum();
		neuRaum.setBezeichnung(bezeichnung);
		neuRaum.setKapazitaet(Integer.parseInt(kapazitaet));
		
		
		// Prüfen ob ein Raum mit der identischen Bezeichnung bereits existiert
		
		Vector<Raum> alleRaeume = raumMapper.findAll();
		
		for (Raum r : alleRaeume) {
			if (bezeichnung.equals(r.getBezeichnung())) {
				throw new IllegalArgumentException("Der Raum existiert bereits");
			}
		}
		
		return this.raumMapper.insertIntoDB(neuRaum);
		
	}
	
	
}
