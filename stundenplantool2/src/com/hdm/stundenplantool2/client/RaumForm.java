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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.*;
import com.hdm.stundenplantool2.shared.bo.*;

public class RaumForm extends VerticalPanel {
	
	VerwaltungAsync verwaltung = null;
	
	DozentTreeViewModel dtvm = null;
	
	Raum shownRaum = null;
	
	Label bezeichnungLabel = new Label("Bezeichnung: ");
	TextBox bezeichnungTextBox = new TextBox();
	
	Label kapazitaetLabel = new Label("Kapazität: ");
	TextBox kapazitaetTextBox = new TextBox();
	
	Button speichernAnlegenButton;
	Button loeschenButton;
	
	Grid grid;
	
	HorizontalPanel buttonPanel;
	
	public RaumForm(VerwaltungAsync verwaltungA) {
		
		this.verwaltung = verwaltungA;
		
		grid = new Grid(3, 2);
		
		grid.setWidget(0, 0, bezeichnungLabel);
		grid.setWidget(0, 1, bezeichnungTextBox);
		grid.setWidget(1, 0, kapazitaetLabel);
		grid.setWidget(1, 1, kapazitaetTextBox);
		
		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);
		
		this.add(grid);
		this.add(buttonPanel);
		
	}
	
	public void setDtvm(DozentTreeViewModel dtvm) {	
		this.dtvm = dtvm;	
		setInfoText();
	}
	
	void setInfoText() {
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("Anleitung blblblblblblblblblblblb lblblblblblblblbldjblakdfj bjhfbjhybdfjklbhskjdfgbjkfhbkj"
				+ "fhblfdlsflgshflgghslfhgilfhgilhfgujs flhgbusgbluhgklhflkhlfuh");
		
		bezeichnungTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("dfgakdfhgk dfhgkhsf duigshk fhgos ifuhgkisfuh giusdhfgs hkodfuighs uidfhguisdfhg iosdhfgushf"
						+ "jlnkfjbnl kfnbkljfngjdfgjn dkljgnkldfgnbkldfnfgbdn flghdsfulfghsdilufh ilffgikfgfgksfgklsfglsfjlsfghlsfh"
						+ "lsfhglks hgihjf igulhj sfidluh ilfudgh lifugh lifhgil fghiusf hgiushf giluhfig luhdfighd filguhsflig hilsfhgils dhfgi");
			}
		});
		
		kapazitaetTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("dfgakdfhgk dfhgkhsf duigshk fhgos ifuhgkisfuh giusdhfgs hkodfuighs uidfhguisdfhg iosdhfgushf"
						+ "jlnkfjbnl kfnbkljfngjdfgjn dkljgnkldfgnbkldfnfgbdn flghdsfulfghsdilufh ilffgikfgfgksfgklsfglsfjlsfghlsfh"
						+ "lsfhglks hgihjf igulhj sfidluh ilfudgh lifugh lifhgil fghiusf hgiushf giluhfig luhdfighd filguhsflig hilsfhgils dhfgi");
			}
		});
	}
	
	public void setShownRaum(Raum raum) {
		this.shownRaum = raum;
	}
	
	public void fillForm() {
		this.bezeichnungTextBox.setText(shownRaum.getBezeichnung());
		this.kapazitaetTextBox.setText(new Integer(shownRaum.getKapazitaet()).toString());
	}
	
	public void aendernMaske() {
		speichernAnlegenButton.setText("Speichern");
		
		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick (ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);
				
				shownRaum.setBezeichnung(bezeichnungTextBox.getText());
				try {
					shownRaum.setKapazitaet(Integer.parseInt(kapazitaetTextBox.getText()));
				}
				catch (NumberFormatException e) {
					Window.alert("Bitte geben Sie nur Zahlen bei der Kapazität ein");
				}
				
				verwaltung.aendernRaum(shownRaum, new AsyncCallback<Raum>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
						verwaltung.auslesenRaum(shownRaum, new AsyncCallback<Vector<Raum>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
							public void onSuccess(Vector<Raum> result) {
								dtvm.setSelectedRaum(result.elementAt(0));	
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
						});
					}
					public void onSuccess(Raum result) {
						Window.alert("Der Raum wurde erfolgreich geändert");
						dtvm.updateRaum(result);
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
							
				verwaltung.loeschenRaum(shownRaum, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
						
					}
					public void onSuccess(Void result) {
						Window.alert("Raum wurde erfolgreich gelöscht");
						dtvm.loeschenRaum(shownRaum);
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
				
				verwaltung.anlegenRaum(bezeichnungTextBox.getText(), kapazitaetTextBox.getText(), new AsyncCallback<Raum>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}
					public void onSuccess(Raum result) {
						Window.alert("Raum wurde erfolgreich angelegt");
						dtvm.addRaum(result);
						speichernAnlegenButton.setEnabled(true);
						clearForm();
					}
				});
			}
		});
	}
	
	public void clearForm() {
		this.shownRaum = null;
		this.bezeichnungTextBox.setText("");
		this.kapazitaetTextBox.setText("");
	}

}
