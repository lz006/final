package com.hdm.stundenplantool2.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.hdm.stundenplantool2.shared.bo.Dozent;
import com.hdm.stundenplantool2.shared.bo.Raum;
import com.hdm.stundenplantool2.shared.bo.Semesterverband;
import com.hdm.stundenplantool2.shared.bo.Studiengang;


@RemoteServiceRelativePath("report")
public interface Report extends RemoteService {

	String createStudentenplan(Semesterverband sv) throws RuntimeException;
	
	Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException;
	
	Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException;
	
	String createDozentenplan(Dozent dozent) throws RuntimeException;
	
	Vector<Dozent> auslesenAlleDozenten() throws RuntimeException;
	
	String createRaumplan(Raum raum) throws RuntimeException;
	
	Vector<Raum> auslesenAlleRaeume() throws RuntimeException;
	
}
