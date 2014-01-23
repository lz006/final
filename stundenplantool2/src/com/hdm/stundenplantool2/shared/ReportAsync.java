package com.hdm.stundenplantool2.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hdm.stundenplantool2.shared.bo.Dozent;
import com.hdm.stundenplantool2.shared.bo.Raum;
import com.hdm.stundenplantool2.shared.bo.Semesterverband;
import com.hdm.stundenplantool2.shared.bo.Studiengang;
import com.hdm.stundenplantool2.shared.report.Dozentenplan;
import com.hdm.stundenplantool2.shared.report.Raumplan;
import com.hdm.stundenplantool2.shared.report.Studentenplan;

/**
 * Das asynchrone Gegenstück des Interface {@link Report}. Es wird
 * semiautomatisch durch das Google Plugin erstellt und gepflegt. Daher erfolgt
 * hier keine weitere Dokumentation. Für weitere Informationen siehe das
 * synchrone Interface {@link Report}.
 * 
 * @author 	Thies, Moser, Sonntag, Zanella
 * @version	1
 */
public interface ReportAsync {

	void createStudentenplan(Semesterverband sv, AsyncCallback<Studentenplan> callback) throws RuntimeException;
	
	void auslesenSemesterverbaendeNachStudiengang(Studiengang sg, AsyncCallback<Vector<Semesterverband>> callback) throws RuntimeException;
	
	void auslesenAlleStudiengaengeOhneSVuLV(AsyncCallback<Vector<Studiengang>> callback) throws RuntimeException;
	
	void createDozentenplan(Dozent dozent, AsyncCallback<Dozentenplan> callback) throws RuntimeException;
	
	void auslesenAlleDozenten(AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;
	
	void createRaumplan(Raum raum, AsyncCallback<Raumplan> callback) throws RuntimeException;
	
	void auslesenAlleRaeume(AsyncCallback<Vector<Raum>> callback) throws RuntimeException;
	
}
