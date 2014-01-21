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
import com.hdm.stundenplantool2.shared.bo.Dozent;

/**
 * Diese Klasse stellt die zum Anfordern eines bestimmten Dozentenplans erforderliche
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class DozentenPlanForm extends VerticalPanel {

	private ReportAsync report = null;

	Label dozentLabel = new Label("Dozent: ");
	ListBox dozentListBox = new ListBox();

	Grid grid;

	Button generateButton = new Button("Report generieren");

	Vector<Dozent> dozentenVector = null;

	VerticalPanel reportPanel;

	public DozentenPlanForm(ReportAsync reportA) {
		this.report = reportA;

		dozentenLaden();

		grid = new Grid(1, 2);

		grid.setWidget(0, 0, dozentLabel);
		grid.setWidget(0, 1, dozentListBox);

		generateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
				dozentListBox.setEnabled(false);
				generateButton.setEnabled(false);
				report.createDozentenplan(dozentenVector.elementAt(dozentListBox.getSelectedIndex()), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						Window.alert(caught.getMessage());
						generateButton.setEnabled(true);
						dozentListBox.setEnabled(true);
					}

					public void onSuccess(String result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						dozentListBox.setEnabled(true);
						generateButton.setEnabled(true);
						if (result != null && result.length() > 1) {
						reportPanel.clear();
						reportPanel.add(new HTML(result));
						createWindow(result);
						} 
						else {
								Window.alert("Zum gewählten Dozent sind keine Belegungen vorhanden");
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

	public void dozentenLaden() {
		generateButton.setEnabled(false);
		report.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				generateButton.setEnabled(true);
				dozentListBox.setEnabled(true);
			}

			public void onSuccess(Vector<Dozent> result) {
				generateButton.setEnabled(true);
				dozentListBox.setEnabled(true);
				dozentenVector = result;
				for (Dozent dozent : result) {
					dozentListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
				}
			}

		});
	}

	public static native void createWindow(String html) /*-{
		var myWidth = 1366, myHeight = 768;
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
		var win = window.open("", "win", myWidth, myHeight);
		win.document.open("text/html", "replace");
		win.document.write("<HTML><HEAD><TITLE>Dozentenplan</TITLE></HEAD><BODY>"+ html + "</BODY></HTML>");
		win.document.close();
	}-*/;

}
