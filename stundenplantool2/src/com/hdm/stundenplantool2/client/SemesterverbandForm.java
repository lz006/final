package com.hdm.stundenplantool2.client;


import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.*;
import com.hdm.stundenplantool2.shared.bo.*;

public class SemesterverbandForm extends VerticalPanel {
	
	VerwaltungAsync verwaltung = null;
	
	DozentTreeViewModel dtvm = null;
	
	Semesterverband shownSemesterverband = null;
	
	Label jahrgangLabel = new Label("Jahrgang: ");
	TextBox jahrgangTextBox = new TextBox();
	
	Label anzahlStudentenLabel = new Label("Anzahl Studenten: ");
	TextBox anzahlStudentenTextBox = new TextBox();
	
	Label studiengangLabel = new Label("Studiengang: ");
	ListBox studiengangListBox = new ListBox();
	
	Button speichernAnlegenButton;
	Button loeschenButton;
	
	Vector<Studiengang> studiengangVector = null;
	
	Grid grid;
	
	HorizontalPanel buttonPanel;
	
	public SemesterverbandForm(VerwaltungAsync verwaltungA) {
		
		this.verwaltung = verwaltungA;
		
		grid = new Grid(3, 2);
		
		grid.setWidget(0, 0, jahrgangLabel);
		grid.setWidget(0, 1, jahrgangTextBox);
		grid.setWidget(1, 0, anzahlStudentenLabel);
		grid.setWidget(1, 1, anzahlStudentenTextBox);
		grid.setWidget(2, 0, studiengangLabel);
		grid.setWidget(2, 1, studiengangListBox);
		
		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);
		
		this.add(grid);
		this.add(buttonPanel);
		
	}
	
	public void ladenStudiengaenge() {
		
		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}
			
			public void onSuccess(Vector<Studiengang> result) {
				
				if (studiengangVector == null) {
					studiengangVector = new Vector<Studiengang>();
				}
				
				for (Studiengang studiengang : result) {					
					studiengangVector.add(studiengang);
					studiengangListBox.addItem(studiengang.getBezeichnung());
				}
				
				if (shownSemesterverband != null) {
					fillForm();
				}
			}
		});
		
	}
	
	public void setDtvm(DozentTreeViewModel dtvm) {	
		this.dtvm = dtvm;	
		setInfoText();
	}
	
	void setInfoText() {
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("Anleitung: </br>"
				+ "Hier können Sie viele bunte Dinge tun.");
		
		jahrgangTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("Für die Bearbeitung der Eintrittsjahrgangs eines Semesterverbands bitte folgende Restriktionen beachten:"
						+ "</br>Viele bunte Smarties"
						+ "</br>Viele bunte Smarties");
			}
		});
		
		anzahlStudentenTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("Für die Bearbeitung der Studentenanzahl eines Semesterverbands bitte folgende Restriktionen beachten:"
						+ "</br>Viele bunte Smarties"
						+ "</br>Viele bunte Smarties");
			}
		});
	}
	
	public void setShownSemesterverband(Semesterverband sv) {
		this.shownSemesterverband = sv;
	}
	
	public void fillForm() {
		this.jahrgangTextBox.setText(shownSemesterverband.getJahrgang());
		this.anzahlStudentenTextBox.setText(new Integer(shownSemesterverband.getAnzahlStudenten()).toString());
		
		for (int i = 0; i < studiengangVector.size(); i++) {
			if (shownSemesterverband.getStudiengang().getId() == studiengangVector.elementAt(i).getId()) {
				studiengangListBox.setSelectedIndex(i);
				break;
			}
		}
		
	}
	
	public void aendernMaske() {
		speichernAnlegenButton.setText("Speichern");
		
		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			
			public void onClick (ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);
				
				shownSemesterverband.setJahrgang(jahrgangTextBox.getText());
				try {
					shownSemesterverband.setAnzahlStudenten(Integer.parseInt(anzahlStudentenTextBox.getText()));
				}
				catch (NumberFormatException e) {
					Window.alert("Bitte geben Sie nur Zahlen bei der Studentenanzahl ein");
				}
				
				verwaltung.aendernSemesterverband(shownSemesterverband, new AsyncCallback<Semesterverband>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
						verwaltung.auslesenSemesterverband(shownSemesterverband, new AsyncCallback<Vector<Semesterverband>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
							public void onSuccess(Vector<Semesterverband> result) {
								dtvm.setSelectedSemesterverband(result.elementAt(0));
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
						});
					}
					public void onSuccess(Semesterverband result) {
						Window.alert("Der Semesterverband wurde erfolgreich geändert");
						dtvm.updateSemesterverband(result);
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
					}
				});
			}
		});
		
		loeschenButton = new Button("Löschen");
		buttonPanel.add(loeschenButton);
		
		loeschenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);
							
				verwaltung.loeschenSemesterverband(shownSemesterverband, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
						
					}
					public void onSuccess(Void result) {
						Window.alert("Semesterverband wurde erfolgreich gelöscht");
						dtvm.loeschenSemesterverband(shownSemesterverband);
						clearForm();
					}
				});
			}
		});
	}
	
	public void anlegenMaske() {
		
		speichernAnlegenButton.setText("Anlegen");
		
		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				
				verwaltung.anlegenSemesterverband(anzahlStudentenTextBox.getText(), jahrgangTextBox.getText(), studiengangVector.elementAt(studiengangListBox.getSelectedIndex()), 
						new AsyncCallback<Semesterverband>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}
					public void onSuccess(Semesterverband result) {
						Window.alert("Semesterverband wurde erfolgreich angelegt");
						dtvm.addSemesterverband(result);
						speichernAnlegenButton.setEnabled(true);
						clearForm();
					}
				});
			}
		});
	}
	
	public void clearForm() {
		this.shownSemesterverband = null;
		this.anzahlStudentenTextBox.setText("");
		this.jahrgangTextBox.setText("");
		this.studiengangListBox.setSelectedIndex(0);
		
	}

}
