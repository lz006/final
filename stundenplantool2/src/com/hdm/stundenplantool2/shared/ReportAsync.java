package com.hdm.stundenplantool2.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hdm.stundenplantool2.shared.bo.Dozent;
import com.hdm.stundenplantool2.shared.bo.Raum;
import com.hdm.stundenplantool2.shared.bo.Semesterverband;
import com.hdm.stundenplantool2.shared.bo.Studiengang;

public interface ReportAsync {

	void createStudentenplan(Semesterverband sv, AsyncCallback<String> callback) throws RuntimeException;
	
	void auslesenSemesterverbaendeNachStudiengang(Studiengang sg, AsyncCallback<Vector<Semesterverband>> callback) throws RuntimeException;
	
	void auslesenAlleStudiengaengeOhneSVuLV(AsyncCallback<Vector<Studiengang>> callback) throws RuntimeException;
	
	void createDozentenplan(Dozent dozent, AsyncCallback<String> callback) throws RuntimeException;
	
	void auslesenAlleDozenten(AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;
	
	void createRaumplan(Raum raum, AsyncCallback<String> callback) throws RuntimeException;
	
	void auslesenAlleRaeume(AsyncCallback<Vector<Raum>> callback) throws RuntimeException;
	
}
