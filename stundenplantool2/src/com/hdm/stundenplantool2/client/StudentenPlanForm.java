package com.hdm.stundenplantool2.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.ReportAsync;
import com.hdm.stundenplantool2.shared.bo.*;


public class StudentenPlanForm extends VerticalPanel {
	
	private ReportAsync report = null;
	
	Label sgLabel = new Label("Studiengang: ");
	ListBox sgListBox = new ListBox();
	
	Label svLabel = new Label("Semesterverband: ");
	ListBox svListBox = new ListBox();
	
	Grid grid;
	
	Button generateButton = new Button("Report generieren");
	
	Vector<Studiengang> sgVector = null;
	Vector<Semesterverband> svVector = null;
	
	VerticalPanel reportPanel;
	
	public StudentenPlanForm(ReportAsync reportA) {
		this.report = reportA;
		
		studiengaengeLaden();
		
		grid = new Grid(2, 2);
		
		grid.setWidget(0, 0, sgLabel);
		grid.setWidget(0, 1, sgListBox);
		grid.setWidget(1, 0, svLabel);
		grid.setWidget(1, 1, svListBox);
		
		generateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				generateButton.setEnabled(false);
				report.createStudentenplan(svVector.elementAt(svListBox.getSelectedIndex()), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						generateButton.setEnabled(true);
					}
					public void onSuccess(String result) {
						generateButton.setEnabled(true);
						if (result != null && result.length() > 1) {
							reportPanel.clear();
							reportPanel.add(new HTML(result));
							createWindow(result);
						}
						else {
							Window.alert("Zum gewählten Semesterverband sind keine Belegungen vorhanden");
						}
					}
				});
			}
		});
		
		sgListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				generateButton.setEnabled(false);
				sgListBox.setEnabled(false);
				svListBox.clear();
				ladenSemesterverbaende();
			}
		});
		
		sgListBox.setEnabled(false);
		generateButton.setEnabled(false);
		
		this.add(grid);
		this.add(generateButton);
		
		reportPanel = new VerticalPanel();
		this.add(reportPanel);
	}
	
	public void studiengaengeLaden() {
		report.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
				sgListBox.setEnabled(true);
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Studiengang> result) {
				sgListBox.setEnabled(true);
				sgVector = result;
				for(Studiengang sg : result) {
					sgListBox.addItem(sg.getBezeichnung());
				}
				ladenSemesterverbaende();
			}
			
		});
	}
	
	public void ladenSemesterverbaende() {
		report.auslesenSemesterverbaendeNachStudiengang(sgVector.elementAt(sgListBox.getSelectedIndex()), new AsyncCallback<Vector<Semesterverband>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				sgListBox.setEnabled(true);
			}
			public void onSuccess(Vector<Semesterverband> result) {
				generateButton.setEnabled(true);
				sgListBox.setEnabled(true);
				svListBox.clear();
				
				if (result != null && result.size() > 0) {
					svVector = result;
					for (Semesterverband sv : result) {
						svListBox.addItem(sv.getJahrgang());
					}
				}
				else {
					Window.alert("Zum gewählten Studiengang sind keine Semesterverbände vorhanden");
				}
			}
		});
	}
	
	public static native void createWindow(String html) /*-{ 
		var myWidth = 1366, myHeight = 768;  
		 	myWidth = window.innerWidth;
   			myHeight = window.innerHeight;
    	var win = window.open("", "win", myWidth,myHeight); 
			win.document.open("text/html", "replace");
			win.document.write("<HTML><HEAD><TITLE>New Document</TITLE></HEAD><BODY>" + html + "</BODY></HTML>");
			win.document.close();
    }-*/;
	
}
