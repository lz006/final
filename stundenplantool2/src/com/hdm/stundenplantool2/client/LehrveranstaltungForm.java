package com.hdm.stundenplantool2.client;




import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.*;
import com.hdm.stundenplantool2.shared.bo.*;

public class LehrveranstaltungForm extends VerticalPanel {

	VerwaltungAsync verwaltung = null;
	
	DozentTreeViewModel dtvm;
	
	Lehrveranstaltung shownLehrveranstaltung = null;
	
	Label bezeichnungLabel = new Label("Bezeichnung");
	TextBox lvBezeichnungTb = new TextBox();
	
	Label studiensemesterLabel = new Label("Studiensemester");
	ListBox studiensemesterListBox = new ListBox();
	
	Label umfangLabel = new Label("Umfang in SWS");
	ListBox umfangListBox = new ListBox();
	
	Label dozentLabel = new Label("Dozenten");
	ListBox dozentListBox = new ListBox();
	Button dozentHinzufuegenButton = new Button("Hinzufügen");
	
	Label studiengangLabel = new Label("Studiengänge");
	ListBox studiengangListBox = new ListBox();
	Button studiengangHinzufuegenButton = new Button("Hinzufügen");
	
	Button speichernAnlegenButton = new Button();
	Button loeschenButton;
	
	Vector<Studiengang> studiengangVector = null;
	Vector<Dozent> dozentenVector = null;
	
	Vector<Studiengang> studiengaengeVonNeuerLVVector = null;
	Vector<Dozent> dozentenVonNeuerLVVector = null;
	
	FlexTable dozentTable;
	FlexTable sgTable;

	VerticalPanel obenPanel;
	VerticalPanel mittePanel;
	VerticalPanel untenPanel;
	HorizontalPanel abschlussPanel;
	
	Grid obenGrid;
	Grid mitteGrid;
	Grid untenGrid;

	
	public LehrveranstaltungForm(VerwaltungAsync verwaltungA) {
		
		this.verwaltung = verwaltungA;
		
		obenPanel = new VerticalPanel();
		
		obenGrid = new Grid(3, 2);
		obenGrid.setWidget(0, 0, bezeichnungLabel);
		obenGrid.setWidget(0, 1, lvBezeichnungTb);
		obenGrid.setWidget(1, 0, studiensemesterLabel);
		obenGrid.setWidget(1, 1, studiensemesterListBox);
		obenGrid.setWidget(2, 0, umfangLabel);
		obenGrid.setWidget(2, 1, umfangListBox);
		
		studiensemesterListBox.addItem("1.Sem");
		studiensemesterListBox.addItem("2.Sem");
		studiensemesterListBox.addItem("3.Sem");
		studiensemesterListBox.addItem("4.Sem");
		studiensemesterListBox.addItem("5.Sem");
		studiensemesterListBox.addItem("6.Sem");
		studiensemesterListBox.addItem("7.Sem");
		studiensemesterListBox.addItem("8.Sem");
		studiensemesterListBox.addItem("9.Sem");
		studiensemesterListBox.addItem("10.Sem");
		studiensemesterListBox.addItem("11.Sem");
		
		umfangListBox.addItem("2");
		umfangListBox.addItem("4");
		umfangListBox.addItem("6");
		umfangListBox.addItem("8");
		umfangListBox.addItem("10");
		umfangListBox.addItem("12");
		
		obenPanel.add(obenGrid);
		this.add(obenPanel);
		
		mittePanel = new VerticalPanel();
		
		mitteGrid = new Grid(1, 3);
		mitteGrid.setWidget(0, 0, dozentLabel);
		mitteGrid.setWidget(0, 1, dozentListBox);
		mitteGrid.setWidget(0, 2, dozentHinzufuegenButton);
		
		ladenDozenten();
		
		dozentHinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				if (shownLehrveranstaltung != null) {
					boolean check = true;
					if (shownLehrveranstaltung.getDozenten() != null) {
						for (Dozent dozent : shownLehrveranstaltung.getDozenten()) {
							if (dozent.getId() == dozentenVector.elementAt(dozentListBox.getSelectedIndex()).getId()) {
								Window.alert("Der Dozent ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					if (check) {
						if (shownLehrveranstaltung.getDozenten() == null) {
							shownLehrveranstaltung.setDozenten(new Vector<Dozent>());
						}
						shownLehrveranstaltung.getDozenten().addElement(dozentenVector.elementAt(dozentListBox.getSelectedIndex()));
						dozentenAnzeigen();
					}
				}
				else {
					if (dozentenVonNeuerLVVector == null) {
						dozentenVonNeuerLVVector = new Vector<Dozent>();
					}
					boolean check = true;
					for (Dozent dozent : dozentenVonNeuerLVVector) {
						if (dozent.getId() == dozentenVector.elementAt(dozentListBox.getSelectedIndex()).getId()) {
							Window.alert("Der Dozent ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						dozentenVonNeuerLVVector.addElement(dozentenVector.elementAt(dozentListBox.getSelectedIndex()));
						dozentenAnzeigen();
					}
				}			
				
			}
		});
		
		dozentTable = new FlexTable();
		dozentTable.setText(0, 0, "Dozent");
		
		mittePanel.add(mitteGrid);
		mittePanel.add(dozentTable);
		this.add(mittePanel);
		
		untenPanel = new VerticalPanel();
		
		untenGrid = new Grid(1, 3);
		untenGrid.setWidget(0, 0, studiengangLabel);
		untenGrid.setWidget(0, 1, studiengangListBox);
		untenGrid.setWidget(0, 2, studiengangHinzufuegenButton);
		
		studiengangHinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				if (shownLehrveranstaltung != null) {
					boolean check = true;	
					if (shownLehrveranstaltung.getStudiengaenge() != null) {
						for (Studiengang sg : shownLehrveranstaltung.getStudiengaenge()) {
							if (sg.getId() == studiengangVector.elementAt(studiengangListBox.getSelectedIndex()).getId()) {
								Window.alert("Der Studiengang ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					if (check) {
						if (shownLehrveranstaltung.getStudiengaenge() == null) {
							shownLehrveranstaltung.setStudiengaenge(new Vector<Studiengang>());
						}
						shownLehrveranstaltung.getStudiengaenge().addElement(studiengangVector.elementAt(studiengangListBox.getSelectedIndex()));
						studiengaengeAnzeigen();
					}
				}
				else {
					if (studiengaengeVonNeuerLVVector == null) {
						studiengaengeVonNeuerLVVector = new Vector<Studiengang>();
					}
					boolean check = true;
					for (Studiengang sg : studiengaengeVonNeuerLVVector) {
						if (sg.getId() == studiengangVector.elementAt(studiengangListBox.getSelectedIndex()).getId()) {
							Window.alert("Lie Lehrveranstaltung ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						studiengaengeVonNeuerLVVector.addElement(studiengangVector.elementAt(studiengangListBox.getSelectedIndex()));
						studiengaengeAnzeigen();
					}
				}			
				
			}
		});
		
		sgTable = new FlexTable();
		sgTable.setText(0, 0, "Studiengang");
		
		untenPanel.add(untenGrid);
		untenPanel.add(sgTable);
		this.add(untenPanel);
		
		abschlussPanel = new HorizontalPanel();
		abschlussPanel.add(speichernAnlegenButton);
		this.add(abschlussPanel);

		
	}
	
	public void ladenDozenten() {
		verwaltung.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Dozent> result) {
				dozentenVector = result;
				for (Dozent dozent : result) {
					dozentListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
				}
				ladenStudiengaenge();
			}
		});
	}
	
	public void ladenStudiengaenge() {
		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Studiengang> result) {
				studiengangVector = result;
				for (Studiengang studiengang : result) {
					studiengangListBox.addItem(studiengang.getBezeichnung());
				}
			}
		});
	}
	
	public void setDtvm(DozentTreeViewModel dtvm) {
		this.dtvm = dtvm;
	}
	
	public void setShownLehrveranstaltung(Lehrveranstaltung lv) {
		this.shownLehrveranstaltung = lv;
	}
	
	public void dozentenAnzeigen() {
		dozentTable.removeAllRows();
		dozentTable.setText(0, 0, "Dozent");
		
		if (shownLehrveranstaltung != null) {
			if ((shownLehrveranstaltung.getDozenten() != null) && (shownLehrveranstaltung.getDozenten().size() > 0)) {
				for(Dozent dozent : shownLehrveranstaltung.getDozenten()) {
					final int row = dozentTable.getRowCount();
					dozentTable.setWidget(row, 0, new Label(dozent.getNachname() + " " + dozent.getVorname()));
					
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							int rowIndex = dozentTable.getCellForEvent(event).getRowIndex();
							dozentTable.removeRow(rowIndex);
							
							shownLehrveranstaltung.getDozenten().removeElementAt(rowIndex - 1);
								
						}
					});
					
					dozentTable.setWidget(row, 1, loeschenButton);
					
				}
			}
		}			
		else {
			if ((dozentenVonNeuerLVVector != null) && (dozentenVonNeuerLVVector.size() > 0)) {
				for(Dozent dozent : dozentenVonNeuerLVVector) {
					final int row = dozentTable.getRowCount();
					dozentTable.setWidget(row, 0, new Label(dozent.getNachname() + " " + dozent.getVorname()));
					
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							int rowIndex = dozentTable.getCellForEvent(event).getRowIndex();
							dozentTable.removeRow(rowIndex);
							
							dozentenVonNeuerLVVector.removeElementAt(rowIndex - 1);
								
						}
					});
					
					dozentTable.setWidget(row, 1, loeschenButton);
					
				}
			}
		}
	}
	
	public void studiengaengeAnzeigen() {
		sgTable.removeAllRows();
		sgTable.setText(0, 0, "Studiengang");
		
		if (shownLehrveranstaltung != null) {
			if ((shownLehrveranstaltung.getStudiengaenge() != null) && (shownLehrveranstaltung.getStudiengaenge().size() > 0)) {
				for(Studiengang sg : shownLehrveranstaltung.getStudiengaenge()) {
					final int row = sgTable.getRowCount();
					sgTable.setWidget(row, 0, new Label(sg.getBezeichnung()));
					
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							int rowIndex = sgTable.getCellForEvent(event).getRowIndex();
							sgTable.removeRow(rowIndex);
							
							shownLehrveranstaltung.getStudiengaenge().removeElementAt(rowIndex - 1);
								
						}
					});
					
					sgTable.setWidget(row, 1, loeschenButton);
					
				}
			}
		}			
		else {
			if ((studiengaengeVonNeuerLVVector != null) && (studiengaengeVonNeuerLVVector.size() > 0)) {
				for(Studiengang sg : studiengaengeVonNeuerLVVector) {
					final int row = sgTable.getRowCount();
					sgTable.setWidget(row, 0, new Label(sg.getBezeichnung()));
					
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							int rowIndex = sgTable.getCellForEvent(event).getRowIndex();
							sgTable.removeRow(rowIndex);
							
							studiengaengeVonNeuerLVVector.removeElementAt(rowIndex - 1);
								
						}
					});
					
					sgTable.setWidget(row, 1, loeschenButton);
					
				}
			}
		}
	}
	
	public void fillForm() {
		this.lvBezeichnungTb.setText(shownLehrveranstaltung.getBezeichnung());
		this.studiensemesterListBox.setSelectedIndex(shownLehrveranstaltung.getStudiensemester() - 1);
		this.umfangListBox.setSelectedIndex((shownLehrveranstaltung.getUmfang() / 2) - 1);
				
	}
	
	public void aendernMaske() {
		
		speichernAnlegenButton.setText("Speichern");
		
		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);
				
				shownLehrveranstaltung.setBezeichnung(lvBezeichnungTb.getText());
				shownLehrveranstaltung.setStudiensemester(studiensemesterListBox.getSelectedIndex() + 1);
				shownLehrveranstaltung.setUmfang((umfangListBox.getSelectedIndex() * 2) + 2);
				
				verwaltung.aendernLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Lehrveranstaltung>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
						verwaltung.auslesenLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Vector<Lehrveranstaltung>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
							public void onSuccess(Vector<Lehrveranstaltung> result) {
								dozentTable.clear();
								sgTable.clear();
								dtvm.setSelectedLehrveranstaltung(result.elementAt(0));	
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
						});
						
					}
					public void onSuccess(Lehrveranstaltung result) {
						Window.alert("Lehrveranstaltung wurde erfolgreich geändert");
						dtvm.updateLehrveranstaltung(shownLehrveranstaltung);
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
					}
				});
			}
		});
		
		loeschenButton = new Button("Löschen");
		abschlussPanel.add(loeschenButton);
		
		loeschenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);
							
				verwaltung.loeschenLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
						
					}
					public void onSuccess(Void result) {
						Window.alert("Lehrveranstaltung wurde erfolgreich gelöscht");
						dtvm.loeschenLehrveranstaltung(shownLehrveranstaltung);
						clearForm();
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
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
				
				if (dozentenVonNeuerLVVector != null && dozentenVonNeuerLVVector.size() > 0) {
					verwaltung.anlegenLehrveranstaltung((umfangListBox.getSelectedIndex() * 2) + 2, lvBezeichnungTb.getText(), studiensemesterListBox.getSelectedIndex() + 1, 
							studiengaengeVonNeuerLVVector, dozentenVonNeuerLVVector, new AsyncCallback<Lehrveranstaltung>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							speichernAnlegenButton.setEnabled(true);
							
						}
						public void onSuccess(Lehrveranstaltung result) {
							Window.alert("Lehrveranstaltung wurde erfolgreich angelegt");
							dtvm.addLehrveranstaltung(result);
							clearForm();
							speichernAnlegenButton.setEnabled(true);
						}
					});
				}
				else {
					verwaltung.anlegenLehrveranstaltung((umfangListBox.getSelectedIndex() * 2) + 2, lvBezeichnungTb.getText(), studiensemesterListBox.getSelectedIndex() + 1, 
							studiengaengeVonNeuerLVVector, new AsyncCallback<Lehrveranstaltung>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							speichernAnlegenButton.setEnabled(true);
							
						}
						public void onSuccess(Lehrveranstaltung result) {
							Window.alert("Lehrveranstaltung wurde erfolgreich angelegt");
							dtvm.addLehrveranstaltung(shownLehrveranstaltung);
							clearForm();
							speichernAnlegenButton.setEnabled(true);
						}
					});
				}
			}
		});
	}
	
		
	public void clearForm() {
		this.shownLehrveranstaltung = null;
		this.lvBezeichnungTb.setText("");
		this.studiensemesterListBox.setSelectedIndex(0);
		this.umfangListBox.setSelectedIndex(0);
		this.dozentListBox.setSelectedIndex(0);
		this.dozentTable.removeAllRows();
		this.studiengangListBox.setSelectedIndex(0);
		this.sgTable.removeAllRows();
		this.studiengaengeVonNeuerLVVector = null;
		this.dozentenVonNeuerLVVector = null;
	}

	
}

