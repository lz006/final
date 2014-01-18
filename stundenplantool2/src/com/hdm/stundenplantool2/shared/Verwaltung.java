package com.hdm.stundenplantool2.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hdm.stundenplantool2.shared.bo.Belegung;
import com.hdm.stundenplantool2.shared.bo.Dozent;
import com.hdm.stundenplantool2.shared.bo.Lehrveranstaltung;
import com.hdm.stundenplantool2.shared.bo.Raum;
import com.hdm.stundenplantool2.shared.bo.Semesterverband;
import com.hdm.stundenplantool2.shared.bo.Studiengang;
import com.hdm.stundenplantool2.shared.bo.Zeitslot;




@RemoteServiceRelativePath("verwaltung")
public interface Verwaltung extends RemoteService {
		
	/*
	 * Auslesen der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	Vector<Semesterverband> auslesenAlleSemesterverbaende() throws RuntimeException;
	
	Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException;
	
	Vector<Semesterverband> auslesenSemesterverband(Semesterverband sv) throws RuntimeException;
	
	Vector<Dozent> auslesenAlleDozenten() throws RuntimeException;
	
	Vector<Dozent> auslesenDozentenNachLV(Lehrveranstaltung lv) throws RuntimeException;
	
	Vector<Dozent> auslesenDozentenNachZeitslot(Zeitslot lv) throws RuntimeException;
	
	Vector<Dozent> auslesenDozent(Dozent dozent) throws RuntimeException;
	
	Vector<Zeitslot> auslesenAlleZeitslots() throws RuntimeException;
	
	Vector<Lehrveranstaltung> auslesenAlleLehrveranstaltungen() throws RuntimeException;
	
	Vector<Lehrveranstaltung> auslesenLehrveranstaltung(Lehrveranstaltung lv) throws RuntimeException;
	
	Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSV(Semesterverband sv, Studiengang sg) throws RuntimeException;
	
	Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSG(Studiengang sg) throws RuntimeException;
	
	Vector<Belegung> auslesenAlleBelegungen() throws RuntimeException;
	
	Vector<Belegung> auslesenBelegung(Belegung belegung) throws RuntimeException;
	
	Vector<Belegung> auslesenBelegungenNachSV(Semesterverband semesterverband) throws RuntimeException;
	
	Vector<Studiengang> auslesenAlleStudiengaenge() throws RuntimeException;
	
	Vector<Studiengang> auslesenStudiengang(Studiengang studiengang) throws RuntimeException;
	
	Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException;
	
	Vector<Raum> auslesenAlleRaeume() throws RuntimeException;
	
	Vector<Raum> auslesenRaum(Raum raum) throws RuntimeException;
	
	Vector<Raum> auslesenVerfuegbareRaeumeZuZeitslotuSV(Zeitslot zeitslot, Vector<Semesterverband> sv) throws RuntimeException;
	
	/*
	 * Löschen der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	void loeschenSemesterverband(Semesterverband semesterverband) throws RuntimeException;
	
	void loeschenDozent(Dozent dozent) throws RuntimeException;
	
	void loeschenZeitslot(Zeitslot zeitslot) throws IllegalArgumentException;
	
	void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws IllegalArgumentException;
	
	void loeschenBelegungen(Belegung belegung, Semesterverband semesterverband) throws IllegalArgumentException;
	
	void loeschenStudiengang(Studiengang studiengang) throws IllegalArgumentException;
	
	void loeschenRaum(Raum raum) throws IllegalArgumentException;
	
	/*
	 * Ändern der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */
	
	Semesterverband aendernSemesterverband(Semesterverband semesterverband) throws RuntimeException;
	
	Dozent aendernDozent(Dozent dozent) throws RuntimeException;
	
	Lehrveranstaltung aendernLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException;
	
	Belegung aendernBelegung(Belegung belegung) throws RuntimeException;
	
	Studiengang aendernStudiengang(Studiengang studiengang) throws RuntimeException;
	
	Raum aendernRaum(Raum raum) throws RuntimeException;
	
	/*
	 * Anlegen der Business-Objects ---------------------------------------------------------------------------------------------------------------------------
	 */

	Semesterverband anlegenSemesterverband (String anzahlStudenten, String jahrgang, Studiengang studiengang) throws RuntimeException;
	
	Dozent anlegenDozent(String vorname, String nachname, String personalnummer, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException;
	
	//Dozent anlegenDozent(String vorname, String nachname, int personalnummer) throws RuntimeException;
	
	Zeitslot anlegenZeitslot(int anfangszeit, int endzeit, String wochentag) throws RuntimeException;
	
	Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, Vector<Dozent> dozenten) throws RuntimeException;
	
	Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge) throws RuntimeException;
	
	Belegung anlegenBelegung(Lehrveranstaltung lehrveranstaltung, Raum raum, Zeitslot zeitslot, Vector<Dozent> dozenten, Vector<Semesterverband> semesterverbaende) throws RuntimeException;
	
	Studiengang anlegenStudiengang(String bezeichnung, String kuerzel, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException;
	
	Studiengang anlegenStudiengang(String bezeichnung, String kuerzel) throws RuntimeException;
	
	Raum anlegenRaum(String bezeichnung, String kapazitaet) throws RuntimeException;
}
