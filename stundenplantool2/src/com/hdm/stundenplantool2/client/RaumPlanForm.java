package com.hdm.stundenplantool2.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.ReportAsync;
import com.hdm.stundenplantool2.shared.bo.Raum;


public class RaumPlanForm extends VerticalPanel{
	
private ReportAsync report = null;
	
	Label raumLabel = new Label("Raum: ");
	ListBox raumListBox = new ListBox();
	
	Grid grid;
	
	Button generateButton = new Button("Report generieren");
	
	Vector<Raum> raumVector = null;
	
	VerticalPanel reportPanel;
	
	public RaumPlanForm(ReportAsync reportA) {
		this.report = reportA;
		
		raeumeLaden();
		
		grid = new Grid(1, 2);
		
		grid.setWidget(0, 0, raumLabel);
		grid.setWidget(0, 1, raumListBox);
		
		generateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
				raumListBox.setEnabled(false);
				generateButton.setEnabled(false);
				report.createRaumplan(raumVector.elementAt(raumListBox.getSelectedIndex()), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
						Window.alert(caught.getMessage());
						generateButton.setEnabled(true);
						raumListBox.setEnabled(true);
					}
					public void onSuccess(String result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
						raumListBox.setEnabled(true);
						generateButton.setEnabled(true);
						if (result != null && result.length() > 1) {
							reportPanel.clear();
							reportPanel.add(new HTML(result));
							createWindow(result);
						}
						else {
							Window.alert("Zum gewählten Raum sind keine Belegungen vorhanden");
						}
					}
				});
			}
		});
		
		this.add(grid);
		this.add(generateButton);
		
		reportPanel = new VerticalPanel();
		this.add(reportPanel);
	}
	
	public void raeumeLaden() {
		raumListBox.setEnabled(false);
		report.auslesenAlleRaeume(new AsyncCallback<Vector<Raum>>() {
			public void onFailure(Throwable caught) {
				raumListBox.setEnabled(true);
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Raum> result) {
				generateButton.setEnabled(true);
				raumListBox.setEnabled(true);
				raumVector = result;
				for(Raum raum : result) {
					raumListBox.addItem(raum.getBezeichnung());
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
			win.document.write("<HTML><HEAD><TITLE>Raumplan</TITLE></HEAD><BODY>" + html + "</BODY></HTML>");
			win.document.close();
    }-*/;
	
}

