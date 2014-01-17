package com.hdm.stundenplantool2.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.VerwaltungAsync;
import com.hdm.stundenplantool2.shared.bo.*;
import com.google.gwt.user.client.ui.Label;

public class BelegungForm extends VerticalPanel{

	VerwaltungAsync verwaltung = null;
	
	DozentTreeViewModel dtvm;
	
	boolean anlegenMaske = false;
	
	// Container für die geladenen Daten
	
	Belegung auswahlBelegung = null;
	Vector<Dozent> dozentenVectorforListBox = null;
	Vector<Raum> raeumeVectorforListBox = null;
	Vector<Lehrveranstaltung> lvVectorforListBox = null;
	Vector<Studiengang> studiengaengeVectorForListBox = null;
	Vector<Semesterverband> semesterverbaendeVectorForListBox = null;
	Vector<Semesterverband> semesterverbaendeVectorForListBoxAnlegen = null;
	Vector<Zeitslot> zeitslotsVectorForListBox = null;
	Vector<Belegung> svBelegungen = null;
	Vector<Semesterverband> SVvonNeuerBelegung = null;
	Vector<String> SGKuerzel = null;
		
	// Widgets der oberen Auswahl
	
	Label studiengang;
	Label semesterverband;	
	Grid auswahlGrid;
	ListBox studiengangListBox;
	ListBox semesterverbandListBox;
	Button ladenBelegungenButton;
	Button tabVisibilityButton;
	Button tabsLeerenButton;
	Button auswahlAufhebenButton;
	VerticalPanel auswahlPanel;
	
	// Widgets der unteren Auswahl zum Anlegen von Belegungen
	
	Grid anlegenGrid;
	Grid anlegenWSVGrid;
	ListBox anlegenTagListBox;
	ListBox anlegenUhrzeitListBox;
	ListBox anlegenLVListBox;
	ListBox anlegenRaumListBox;
	ListBox anlegenDozent1ListBox;
	ListBox anlegenDozent2ListBox;
	ListBox anlegenDozent3ListBox;
	Button anlegenButton;
	VerticalPanel anlegenPanel;
	VerticalPanel anlegenWSVPanel;
	
	
	
	Button weitereSV;
	
	Label studiengangAnlegen;
	Label semesterverbandAnlegen;	
	ListBox studiengangListBoxAnlegen;
	ListBox semesterverbandListBoxAnlegen;
	Button hinzufuegenButtonAnlegen;
	FlexTable svTable;
	
	// Widgets f�r den Inhalt des mittleren TabPanels
	
	Vector<Grid> gridVector;
	Grid montagGrid;
	Grid dienstagGrid;
	Grid mittwochGrid;
	Grid donnerstagGrid;
	Grid freitagGrid;
	Grid samstagGrid;
	Grid sonntagGrid;
	TabPanel tabPanel;
	
	// Container f�r die einzelnen Widgets des TabPanels um diese einzeln ansprechen zu k�nnen
	
	Vector<ListBox> lbv = new Vector<ListBox>();
	Vector<Button> bv = new Vector<Button>();
	
	// Pointer(Zeiger) f�r die Adressierung der einzelnen Widgets im �ndern-TabPanel
	
	int belegungPointer = 0;
	
	int lvListBoxPointer = 0;
	int raumListBoxPointer = 1;
	int dozent1ListBoxPointer = 2;
	int dozent2ListBoxPointer = 3;
	int dozent3ListBoxPointer = 4;
	
	int aendernButtonPointer = 0;
	int loeschenButtonPointer = 1;
	
	int gridPointer;
	int rowPointer;
	
	Integer focusBelegungPointer = null;
	Integer focusRaumListBoxPointer = null;
	Integer focusDozentListBoxPointer = null;
	Integer focusOneOfThreeDozentListBoxPointer = null;	
	Integer focusAendernButtonPointer = null;
	
	public BelegungForm(VerwaltungAsync verwaltung) {
		this.verwaltung = verwaltung;
		
		studiengang = new Label("Studiengang :");
		studiengangListBox = new ListBox();
				
		semesterverband = new Label("Semesterverband :");
		semesterverbandListBox = new ListBox();
		semesterverbandListBox.setEnabled(false);
				
		ladenBelegungenButton = new Button("Laden der Belegungen");
		ladenBelegungenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ladenLehrveranstaltungen();
				ladenBelegungenButton.setEnabled(false);
				tabsLeerenButton.setEnabled(false);
				disableAllTabWidgets();
				
				studiengangListBox.setEnabled(false);
				semesterverbandListBox.setEnabled(false);
				
				//Styling der Listboxen
				anlegenTagListBox.addStyleName("BListBox");
				anlegenLVListBox.addStyleName("BListBox");
				anlegenRaumListBox.addStyleName("BListBox");
				anlegenDozent1ListBox.addStyleName("BListBox");
				anlegenDozent2ListBox.addStyleName("BListBox");
				anlegenDozent3ListBox.addStyleName("BListBox");
				anlegenUhrzeitListBox.addStyleName("BListBox");
			}
		});
		
		tabVisibilityButton = new Button("Übersicht verdecken");
		tabVisibilityButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				tabVisibility();
			}
		});
		
		tabsLeerenButton = new Button("Tabs Leeren");
		tabsLeerenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				clearEntries();
			}
		});
		
		auswahlAufhebenButton = new Button("Auswahl aufheben");
		auswahlAufhebenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				enableAllTabWidgets();
			}
		});		
		auswahlAufhebenButton.setVisible(false);
		
		auswahlGrid = new Grid(3, 4);
		auswahlGrid.setWidget(0, 0, studiengang);
		auswahlGrid.setWidget(0, 1, studiengangListBox);
		auswahlGrid.setWidget(1, 0, semesterverband);
		auswahlGrid.setWidget(1, 1, semesterverbandListBox);
		auswahlGrid.setWidget(2, 0, ladenBelegungenButton);
		auswahlGrid.setWidget(2, 1, tabsLeerenButton);
		auswahlGrid.setWidget(2, 2, tabVisibilityButton);
		auswahlGrid.setWidget(2, 3, auswahlAufhebenButton);
			
		auswahlPanel = new VerticalPanel();
		auswahlPanel.add(auswahlGrid);
		
		
		this.add(auswahlPanel);
		
		gridVector = new Vector<Grid>();
		
		montagGrid = new Grid(8,8);
		gridVector.add(montagGrid);
		
		dienstagGrid = new Grid(8,8);
		gridVector.add(dienstagGrid);
		
		mittwochGrid = new Grid(8,8);
		gridVector.add(mittwochGrid);
		
		donnerstagGrid = new Grid(8,8);
		gridVector.add(donnerstagGrid);
		
		freitagGrid = new Grid(8,8);
		gridVector.add(freitagGrid);
		
		samstagGrid = new Grid(8,8);
		gridVector.add(samstagGrid);
		
		sonntagGrid = new Grid(8,8);
		gridVector.add(sonntagGrid);
		
		tabPanel = new TabPanel();
		tabPanel.add(gridVector.elementAt(0), "Montag");
		tabPanel.add(gridVector.elementAt(1), "Dienstag");
		tabPanel.add(gridVector.elementAt(2), "Mittwoch");
		tabPanel.add(gridVector.elementAt(3), "Donnerstag");
		tabPanel.add(gridVector.elementAt(4), "Freitag");
		tabPanel.add(gridVector.elementAt(5), "Samstag");
		tabPanel.add(gridVector.elementAt(6), "Sonntag");
		tabPanel.selectTab(0);
		
		
		this.add(tabPanel);
		
		
	}
	
	public void addChangeHandlerTosStudiengangListBox() {
		studiengangListBox.addChangeHandler( new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				ladenSemesterverbaende(false);
			}
		});
	}
	
	public void addChangeHandlerTosStudiengangListBoxAnlegen() {
		studiengangListBoxAnlegen.addChangeHandler( new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				ladenSemesterverbaende(true);
			}
		});
	}
	
	public void addChangeHandlerToAnlegenTagListBox() {
		anlegenTagListBox.addChangeHandler( new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				if (anlegenTagListBox.getSelectedIndex() == 0) {
					anlegenUhrzeitListBox.setSelectedIndex(0);
					anlegenUhrzeitListBox.setEnabled(false);
				}
				else if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
					anlegenRaumListBox.setEnabled(true);
					anlegenRaumListBox.clear();
					
					anlegenLVListBox.setEnabled(false);
					
					ladenRaeume();
				}
				else {
					anlegenUhrzeitListBox.setEnabled(true);
				}
				
				raeumeVectorforListBox = null;
				anlegenRaumListBox.clear();
			}
		});
	}
	
	public void addChangeHandlerToAnlegenUhrzeitListBox() {
		anlegenUhrzeitListBox.addChangeHandler( new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				raeumeVectorforListBox = null;
				anlegenRaumListBox.clear();
				
				anlegenLVListBox.setEnabled(false);
				
				ladenRaeume();
			}
		});
	}
	
	public void addChangeHandlerToAnlegenLVListBox() {
		anlegenLVListBox.addChangeHandler( new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				anlegenLVListBox.setEnabled(false);
				dozentenVectorforListBox = null;
				anlegenDozent1ListBox.clear();
				anlegenDozent2ListBox.clear();
				anlegenDozent3ListBox.clear();
				
				anlegenTagListBox.setEnabled(false);
				anlegenUhrzeitListBox.setEnabled(false);
				
				ladenDozenten();
			}
		});
	}
	
	
	public void ladenStudiengaenge() {
		
		ladenBelegungenButton.setEnabled(false);
		studiengangListBox.setEnabled(false);
		
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		studiengaengeVectorForListBox = new Vector<Studiengang>();
		studiengangListBox.clear();
		semesterverbandListBox.clear();
		
		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Studiengang> result) {
				addChangeHandlerTosStudiengangListBox();
				studiengangListBox.addItem("Bitte wählen");
				if (anlegenMaske) {
					studiengangListBoxAnlegen.addItem("Bitte wählen");
				}
				for(Studiengang sG : result) {
					studiengangListBox.addItem(sG.getBezeichnung());
					studiengaengeVectorForListBox.add(sG);
					if (anlegenMaske) {
						studiengangListBoxAnlegen.addItem(sG.getBezeichnung());
					}
				}
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
				ladenZeitslots();
			}
		});
	}
	
	public void ladenSemesterverbaende(boolean info) {
		
		if (!info) {
			semesterverbandListBox.clear();
			if (studiengangListBox.getSelectedIndex() != 0) {
				verwaltung.auslesenSemesterverbaendeNachStudiengang(studiengaengeVectorForListBox.elementAt(studiengangListBox.getSelectedIndex() -1), new AsyncCallback<Vector<Semesterverband>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
					public void onSuccess(Vector<Semesterverband> result) {
						if (result == null || result.size() == 0) {
							Window.alert("Dem Studiengang sind momentan keine Semesterverbaende zugeordnet");
							
						}
						else {
							semesterverbaendeVectorForListBox = result;
							
							for(Semesterverband sv : semesterverbaendeVectorForListBox) {
								semesterverbandListBox.addItem(sv.getJahrgang());
							}
							semesterverbandListBox.setEnabled(true);
							ladenBelegungenButton.setEnabled(true);
						}
					}
				});
			}
		}
		if (info) {	
			semesterverbandListBoxAnlegen.clear();
			if(studiengangListBoxAnlegen.getSelectedIndex() != 0) {
				verwaltung.auslesenSemesterverbaendeNachStudiengang(studiengaengeVectorForListBox.elementAt(studiengangListBoxAnlegen.getSelectedIndex() -1), new AsyncCallback<Vector<Semesterverband>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
					public void onSuccess(Vector<Semesterverband> result) {
						if (result == null || result.size() == 0) {
							Window.alert("Dem Studiengang sind momentan keine Semesterverbaende zugeordnet");
							semesterverbandListBoxAnlegen.clear();
						}
						else {
							semesterverbaendeVectorForListBoxAnlegen = result;
							semesterverbandListBoxAnlegen.clear();
							for(Semesterverband sv : semesterverbaendeVectorForListBoxAnlegen) {
								semesterverbandListBoxAnlegen.addItem(sv.getJahrgang());
							}
							semesterverbandListBoxAnlegen.setEnabled(true);
						}
					}
				});
			}
		}
	}
	
	public void ladenBelegungen() {
		
		ladenBelegungenButton.setEnabled(false);
		
		if (semesterverbaendeVectorForListBox != null) {
			verwaltung.auslesenBelegungenNachSV(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()), new AsyncCallback<Vector<Belegung>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
					
					studiengangListBox.setEnabled(true);
					semesterverbandListBox.setEnabled(true);
					if(anlegenMaske) {
						anlegenButton.setEnabled(true);
					}
				}
				public void onSuccess(Vector<Belegung> result) {
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
					
					if (result == null || result.size() == 0) {
						if(!anlegenMaske) {
							Window.alert("Es sind keine Belegungen zum gewählten Semesterverband angelegt");
							studiengangListBox.setEnabled(true);
							semesterverbandListBox.setEnabled(true);
						}
						else {
							Window.alert("Hinweis: Es sind noch keine Belegungen zum gewählten Semesterverband angelegt");
							anlegenTagListBox.setEnabled(true);
							anlegenLVListBox.setEnabled(true);
							
						}
					}
					else {
						svBelegungen = result;  
						if(!anlegenMaske) {
							multipleSemesterverbaendeWarning();
							fillContentTab();
						}
						else {
							fillContentTabAnlegen();
							
							anlegenButton.setEnabled(true);
							
							anlegenTagListBox.setEnabled(true);
							anlegenLVListBox.setEnabled(true);
							
							anlegenTagListBox.setSelectedIndex(0);
							anlegenUhrzeitListBox.setSelectedIndex(0);
							anlegenLVListBox.setSelectedIndex(0);
							if (raeumeVectorforListBox != null) {
								anlegenRaumListBox.setSelectedIndex(0);
							}
							if (dozentenVectorforListBox != null) {
								anlegenDozent1ListBox.setSelectedIndex(0);
								anlegenDozent2ListBox.setSelectedIndex(0);
								anlegenDozent3ListBox.setSelectedIndex(0);
							}
						}
					}
				}
			});
		}
		else {
			Window.alert("Kein Semesterverband vorhanden");
			tabsLeerenButton.setEnabled(true);
		}
	}
	
	public void multipleSemesterverbaendeWarning() {
			
		for (Belegung belegung : svBelegungen) {
			if (belegung.getSemesterverbaende().size() > 1) {
				Window.alert("Es sind Belegungen vorhanden, welche mehrere Semesterverbäende referenzieren\nÄndern-Button wird dann in roter Schrift angezeigt"
						+ "\nÄnderungen wirken sich auf alle referenzierten Semesterverbände aus\nDas Löschen einer Belegung hat jedoch nur auf den gewählten Semesterverband Einfluss");
				break;
			}
		}
	}
	
	public void ladenZeitslots() {
		verwaltung.auslesenAlleZeitslots(new AsyncCallback<Vector<Zeitslot>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			public void onSuccess(Vector<Zeitslot> result) {
				zeitslotsVectorForListBox = result;
				fillContextTab();
			}
		});
	}
	
	public void fillContextTab() {
		
		int AnfangsWochentagZaehler = 0;
		int EndWochentagZaehler = 6;
		int gridRowZaehler = 0;
		
		for (int i = 0; i < 7; i++) {
			
			Grid tempGrid = (Grid)tabPanel.getWidget(i);
			tempGrid.setWidget(0, 0, new Label("Zeit"));
			tempGrid.setWidget(0, 1, new Label("Lehrveranstaltung"));
			tempGrid.setWidget(0, 2, new Label("Raum"));
			tempGrid.setWidget(0, 3, new Label("Dozent"));
			tempGrid.setWidget(0, 4, new Label("Dozent"));
			tempGrid.setWidget(0, 5, new Label("Dozent"));
			
			
			
			
			for (int j = AnfangsWochentagZaehler; j <= EndWochentagZaehler; j++) {
				int azStunde = zeitslotsVectorForListBox.elementAt(j).getAnfangszeit() / 60;
				int azMinute = zeitslotsVectorForListBox.elementAt(j).getAnfangszeit() % 60;
				int ezStunde = zeitslotsVectorForListBox.elementAt(j).getEndzeit() / 60;
				int ezMinute = zeitslotsVectorForListBox.elementAt(j).getEndzeit() % 60;
				
				StringBuffer tempAzStunde = new StringBuffer();
				tempAzStunde.append(azStunde);
				if (tempAzStunde.length() < 2) {
					tempAzStunde.insert(0, "0");
				}
				StringBuffer tempAzMinute = new StringBuffer();
				tempAzMinute.append(azMinute);
				if (tempAzMinute.length() < 2) {
					tempAzMinute.insert(0, "0");
				}
				StringBuffer tempEzStunde = new StringBuffer();
				tempEzStunde.append(ezStunde);
				if (tempEzStunde.length() < 2) {
					tempEzStunde.insert(0, "0");
				}
				StringBuffer tempEzMinute = new StringBuffer();
				tempEzMinute.append(ezMinute);
				if (tempEzMinute.length() < 2) {
					tempEzMinute.insert(0, "0");
				}
				
				StringBuffer zeitAnzeige = new StringBuffer();
				zeitAnzeige.append(tempAzStunde);
				zeitAnzeige.append(":");
				zeitAnzeige.append(tempAzMinute);
				zeitAnzeige.append(" bis ");
				zeitAnzeige.append(tempEzStunde);
				zeitAnzeige.append(":");
				zeitAnzeige.append(tempEzMinute);
								
				tempGrid.setWidget(gridRowZaehler + 1, 0, new Label(zeitAnzeige.toString()));
				gridRowZaehler++;
			}
			
			AnfangsWochentagZaehler = AnfangsWochentagZaehler + 7;
			EndWochentagZaehler = EndWochentagZaehler + 7;
			gridRowZaehler = 0;
		}	
		studiengangListBox.setEnabled(true);
		if (anlegenMaske) {
			studiengangListBoxAnlegen.setEnabled(true);
		}
	}
	
	public void ladenDozenten() {
		
		ladenBelegungenButton.setEnabled(false);
		tabsLeerenButton.setEnabled(false);
		
		if (!anlegenMaske) {
			verwaltung.auslesenDozentenNachZeitslot(svBelegungen.elementAt(focusBelegungPointer).getZeitslot(), new AsyncCallback<Vector<Dozent>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					auswahlAufhebenButton.setVisible(true);
					
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
				public void onSuccess(Vector<Dozent> result) {
					dozentenVectorforListBox = result;
					for (Dozent dozent : result) {
						lbv.elementAt(focusDozentListBoxPointer).addItem(dozent.getNachname() + " " + dozent.getVorname());
					}
					
					lbv.elementAt(focusDozentListBoxPointer).addItem("kein Dozent");
					
					if (result != null && result.size() > 0) {
						if (focusOneOfThreeDozentListBoxPointer == 1) {
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 2).setEnabled(true);
						}
						else if (focusOneOfThreeDozentListBoxPointer == 2) {
							lbv.elementAt(focusDozentListBoxPointer - 2).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 1).setEnabled(true);
						}
						else {
							lbv.elementAt(focusDozentListBoxPointer - 3).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 2).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
						}
					}					
					
					bv.elementAt(focusAendernButtonPointer).setEnabled(true);
					bv.elementAt(focusAendernButtonPointer + 1).setEnabled(true);
					
					auswahlAufhebenButton.setVisible(true);
					
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
			});
		}
		if (anlegenMaske) {
			if (anlegenLVListBox.getSelectedIndex() == 0) {
				Window.alert("Bitte wählen Sie eine Lehrveranstaltung aus");
				dozentenVectorforListBox = null;
				anlegenDozent1ListBox.setEnabled(false);
				anlegenDozent2ListBox.setEnabled(false);
				anlegenDozent3ListBox.setEnabled(false);
				
				anlegenTagListBox.setEnabled(true);
				anlegenUhrzeitListBox.setEnabled(true);
				
				anlegenDozent1ListBox.clear();
				anlegenDozent2ListBox.clear();
				anlegenDozent3ListBox.clear();
				
				ladenBelegungenButton.setEnabled(true);
				tabsLeerenButton.setEnabled(true);
			}
			else {
				verwaltung.auslesenDozentenNachLV(lvVectorforListBox.elementAt(anlegenLVListBox.getSelectedIndex() - 1), new AsyncCallback<Vector<Dozent>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						anlegenTagListBox.setEnabled(true);
						anlegenUhrzeitListBox.setEnabled(true);
						
						ladenBelegungenButton.setEnabled(true);
						tabsLeerenButton.setEnabled(true);
						anlegenLVListBox.setEnabled(true);
					}
					public void onSuccess(Vector<Dozent> result) {
						dozentenVectorforListBox = result;
						
						anlegenDozent1ListBox.clear();
						anlegenDozent2ListBox.clear();
						anlegenDozent3ListBox.clear();
						
						anlegenDozent1ListBox.addItem("bitte wählen");
						anlegenDozent2ListBox.addItem("optional");
						anlegenDozent3ListBox.addItem("optional");
						for (Dozent dozent : dozentenVectorforListBox) {
							anlegenDozent1ListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
							anlegenDozent2ListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
							anlegenDozent3ListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
						}
						
						anlegenTagListBox.setEnabled(true);
						anlegenUhrzeitListBox.setEnabled(true);
						
						ladenBelegungenButton.setEnabled(true);
						tabsLeerenButton.setEnabled(true);
						anlegenLVListBox.setEnabled(true);
						
						if (result != null && result.size() > 0) {
							anlegenDozent1ListBox.setEnabled(true);
							anlegenDozent2ListBox.setEnabled(true);
							anlegenDozent3ListBox.setEnabled(true);
						}
						else {
							Window.alert("Zur gewünschten Lehrveranstaltung ist kein Dozent vorgesehen");
							if (anlegenUhrzeitListBox.getSelectedIndex() == 0) {
								anlegenUhrzeitListBox.setEnabled(false);
							}
						}
										
					}
				});
			}
		}
				
	}
	
	public void ladenRaeume() {
		
		ladenBelegungenButton.setEnabled(false);
		tabsLeerenButton.setEnabled(false);
		
		if (!anlegenMaske) {
			verwaltung.auslesenVerfuegbareRaeumeZuZeitslotuSV(svBelegungen.elementAt(focusBelegungPointer).getZeitslot(), 
					svBelegungen.elementAt(focusBelegungPointer).getSemesterverbaende(), new AsyncCallback<Vector<Raum>>() {
				public void onFailure(Throwable caught) {
					
					if (caught.getMessage().substring(0, 1).equals("K")) {
						Window.alert("Es ist kein anderer Raum zu diesem Zeitslot verf�gbar");
					}
					else {
						Window.alert(caught.getMessage());
					}
					auswahlAufhebenButton.setVisible(true);
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
				public void onSuccess(Vector<Raum> result) {
					raeumeVectorforListBox = result;
					for(Raum raum : result) {
						lbv.elementAt(focusRaumListBoxPointer).addItem(raum.getBezeichnung());
					}
					
					lbv.elementAt(focusRaumListBoxPointer + 1).setEnabled(true);
					lbv.elementAt(focusRaumListBoxPointer + 2).setEnabled(true);
					lbv.elementAt(focusRaumListBoxPointer + 3).setEnabled(true);
					
					bv.elementAt(focusAendernButtonPointer).setEnabled(true);
					bv.elementAt(focusAendernButtonPointer + 1).setEnabled(true);
					
					auswahlAufhebenButton.setVisible(true);
					
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
			});
		}
		if (anlegenMaske) {
						
			int zeitslotIndex;
						
			if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
				zeitslotIndex = ((anlegenTagListBox.getSelectedIndex() - 1) * 7) + anlegenUhrzeitListBox.getSelectedIndex();
			}
			else {
				Window.alert("Bitte wählen Sie einen Tag und eine Uhrzeit aus");
				anlegenTagListBox.setSelectedIndex(0);
				anlegenUhrzeitListBox.setSelectedIndex(0);
				anlegenUhrzeitListBox.setEnabled(false);
				
				anlegenLVListBox.setEnabled(true);
				
				ladenBelegungenButton.setEnabled(true);
				tabsLeerenButton.setEnabled(true);
				
				return;
			}
			
			Vector<Semesterverband> vSV = new Vector<Semesterverband>();
			vSV.add(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()));
												
			verwaltung.auslesenVerfuegbareRaeumeZuZeitslotuSV(zeitslotsVectorForListBox.elementAt(zeitslotIndex - 1), vSV, new AsyncCallback<Vector<Raum>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					anlegenTagListBox.setSelectedIndex(0);
					anlegenUhrzeitListBox.setSelectedIndex(0);
					anlegenUhrzeitListBox.setEnabled(false);
					
					raeumeVectorforListBox = null;
					anlegenRaumListBox.clear();
					
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
				public void onSuccess(Vector<Raum> result) {
					raeumeVectorforListBox = result;
					anlegenRaumListBox.clear();
					
					anlegenRaumListBox.addItem("bitte wählen");
					for (Raum raum : raeumeVectorforListBox) {
						anlegenRaumListBox.addItem(raum.getBezeichnung());
					}
					anlegenRaumListBox.setEnabled(true);
					
					anlegenLVListBox.setEnabled(true);
					
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
			});
		}
		
	}
	
	public void ladenLehrveranstaltungen() {
		verwaltung.auslesenLehrveranstaltungenNachSV(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()), 
				studiengaengeVectorForListBox.elementAt(studiengangListBox.getSelectedIndex() - 1), new AsyncCallback<Vector<Lehrveranstaltung>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				tabsLeerenButton.setEnabled(true);
			}
			public void onSuccess(Vector<Lehrveranstaltung> result) {
				lvVectorforListBox = result;
				if (anlegenMaske) {
					anlegenLVListBox.clear();
					anlegenLVListBox.addItem("bitte wählen");
					for (Lehrveranstaltung lv : lvVectorforListBox) {
						anlegenLVListBox.addItem(lv.getBezeichnung());
					}
					anlegenGrid.setWidget(1, 2, anlegenLVListBox);
				}
				ladenBelegungen();
			}
		});
	}
	
	public void enableAllTabWidgets() {
		for (int i = 0; i < lbv.size(); i++) {
			lbv.elementAt(i).setSelectedIndex(0);
			lbv.elementAt(i).setEnabled(true);
		}
		for (int i = 0; i < bv.size(); i++) {
			bv.elementAt(i).setEnabled(true);
			
		}
	}
	
	public void disableAllTabWidgets() {
		for (int i = 0; i < lbv.size(); i++) {
			lbv.elementAt(i).setEnabled(false);
		}
		for (int i = 0; i < bv.size(); i++) {
			bv.elementAt(i).setEnabled(false);
			
		}
	}
	
	public void disableTabWidgetsDueRaumListBox() {
		for (int i = 0; i < lbv.size(); i++) {
			if(!((i > focusRaumListBoxPointer - 2) && (i < focusRaumListBoxPointer + 4))) {
				lbv.elementAt(i).setEnabled(false);
			}
		}
		for (int i = 0; i < bv.size(); i++) {
			if(!((i > focusAendernButtonPointer - 1) && (i < focusAendernButtonPointer + 2))) {
				bv.elementAt(i).setEnabled(false);
			}
		}
	}
	
	public void disableTabWidgetsDueDozentListBox(int dlb) {
		
		int leftward;
		int rightward;
		
		if(dlb == 1) {
			leftward = -3;
			rightward = 3;
		}
		else if (dlb == 2) {
			leftward = -4;
			rightward = 2;
		}
		else {
			leftward = -5;
			rightward = 1;
		}
		
		for (int i = 0; i < lbv.size(); i++) {
			if(!((i > focusDozentListBoxPointer + leftward) && (i < focusDozentListBoxPointer + rightward))) {
				lbv.elementAt(i).setEnabled(false);
			}
		}
		for (int i = 0; i < bv.size(); i++) {
			if(!((i > focusAendernButtonPointer - 1) && (i < focusAendernButtonPointer + 2))) {
				bv.elementAt(i).setEnabled(false);
			}
		}
	}
	
	public void dynamicWidgets(int listboxen, int buttons) {
		lbv.clear();
		for(int i = 0; i < listboxen; i++) {
			ListBox tempListBox = new ListBox();
			lbv.add(tempListBox);
		}
		bv.clear();
		for(int i = 0; i < buttons; i++) {
			Button tempAendernButton = new Button("aendern");
			bv.add(tempAendernButton);
			Button tempLoeschenButton = new Button("loeschen");
			bv.add(tempLoeschenButton);
		}
	}
	
	public void fillContentTab() {		
		
		belegungPointer = 0;
		
		lvListBoxPointer = 0;
		raumListBoxPointer = 1;
		dozent1ListBoxPointer = 2;
		dozent2ListBoxPointer = 3;
		dozent3ListBoxPointer = 4;
		
		aendernButtonPointer = 0;
		loeschenButtonPointer = 1;
		
				
		dynamicWidgets(svBelegungen.size() * 5, svBelegungen.size());
		
		for (int i = 0; i < svBelegungen.size(); i++) {
			belegungPointer = i;
									
			if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				gridPointer = 0;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 7 && svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {				
				gridPointer = 1;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				gridPointer = 2;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				gridPointer = 3;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				gridPointer = 4;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				gridPointer = 5;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				gridPointer = 6;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}
				lbv.elementAt(lvListBoxPointer).clear();
				lbv.elementAt(lvListBoxPointer).addItem(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());	
				for(Lehrveranstaltung lv : lvVectorforListBox) {
					lbv.elementAt(lvListBoxPointer).addItem(lv.getBezeichnung());
				}
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 1, lbv.elementAt(lvListBoxPointer));
				
				lbv.elementAt(raumListBoxPointer).clear();
				lbv.elementAt(raumListBoxPointer).addItem(svBelegungen.elementAt(i).getRaum().getBezeichnung());
								
				lbv.elementAt(raumListBoxPointer).addFocusHandler(new FocusHandler() {
					
					int tempAendernButtonPointer = aendernButtonPointer;
					int tempRaumListBoxPointer = raumListBoxPointer;
					int tempBelegungPointer = belegungPointer;
					
					public void onFocus (FocusEvent event) {
						focusBelegungPointer = tempBelegungPointer;
						focusRaumListBoxPointer = tempRaumListBoxPointer;
						focusAendernButtonPointer = tempAendernButtonPointer;
						
						String firstItem = lbv.elementAt(tempRaumListBoxPointer).getValue(0);
						lbv.elementAt(tempRaumListBoxPointer).clear();
						lbv.elementAt(tempRaumListBoxPointer).addItem(firstItem);
						
						lbv.elementAt(tempRaumListBoxPointer + 1).setEnabled(false);
						lbv.elementAt(tempRaumListBoxPointer + 2).setEnabled(false);
						lbv.elementAt(tempRaumListBoxPointer + 3).setEnabled(false);
						
						bv.elementAt(tempAendernButtonPointer).setEnabled(false);
						bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);
						
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						ladenRaeume();
						
						disableTabWidgetsDueRaumListBox();
					}
				});
				
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 2, lbv.elementAt(raumListBoxPointer));
				
				lbv.elementAt(dozent1ListBoxPointer).clear();
				lbv.elementAt(dozent1ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(0).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(0).getVorname());
				if (svBelegungen.elementAt(i).getDozenten().size() > 1) {
					lbv.elementAt(dozent2ListBoxPointer).clear();
					lbv.elementAt(dozent2ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(1).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(1).getVorname());
				}
				else {
					lbv.elementAt(dozent2ListBoxPointer).clear();
					lbv.elementAt(dozent2ListBoxPointer).addItem("---");
				}
				if (svBelegungen.elementAt(i).getDozenten().size() > 2) {
					lbv.elementAt(dozent3ListBoxPointer).clear();
					lbv.elementAt(dozent3ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(2).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(2).getVorname());
				}
				else {
					lbv.elementAt(dozent3ListBoxPointer).clear();
					lbv.elementAt(dozent3ListBoxPointer).addItem("---");
				}	
				
				lbv.elementAt(dozent1ListBoxPointer).addFocusHandler(new FocusHandler() {
					
					int tempAendernButtonPointer = aendernButtonPointer;
					int tempDozent1ListBoxPointer = dozent1ListBoxPointer;
					int tempBelegungPointer = belegungPointer;
					
					public void onFocus(FocusEvent event) {
						focusBelegungPointer = tempBelegungPointer;
						focusDozentListBoxPointer = tempDozent1ListBoxPointer;
						focusAendernButtonPointer = tempAendernButtonPointer;
						
						String firstItem = lbv.elementAt(tempDozent1ListBoxPointer).getValue(0);
						lbv.elementAt(tempDozent1ListBoxPointer).clear();
						lbv.elementAt(tempDozent1ListBoxPointer).addItem(firstItem);
						
						lbv.elementAt(tempDozent1ListBoxPointer - 1).setEnabled(false);
						lbv.elementAt(tempDozent1ListBoxPointer + 1).setEnabled(false);
						lbv.elementAt(tempDozent1ListBoxPointer + 2).setEnabled(false);
						
						bv.elementAt(tempAendernButtonPointer).setEnabled(false);
						bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);
						
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						focusOneOfThreeDozentListBoxPointer = 1;
						ladenDozenten();
						
						disableTabWidgetsDueDozentListBox(1);
						
					}
				});
				
				lbv.elementAt(dozent2ListBoxPointer).addFocusHandler(new FocusHandler() {
					
					int tempAendernButtonPointer = aendernButtonPointer;
					int tempDozent2ListBoxPointer = dozent2ListBoxPointer;
					int tempBelegungPointer = belegungPointer;
					
					public void onFocus(FocusEvent event) {
						focusBelegungPointer = tempBelegungPointer;
						focusDozentListBoxPointer = tempDozent2ListBoxPointer;
						focusAendernButtonPointer = tempAendernButtonPointer;
						
						String firstItem = lbv.elementAt(tempDozent2ListBoxPointer).getValue(0);
						lbv.elementAt(tempDozent2ListBoxPointer).clear();
						lbv.elementAt(tempDozent2ListBoxPointer).addItem(firstItem);
						
						lbv.elementAt(tempDozent2ListBoxPointer - 2).setEnabled(false);
						lbv.elementAt(tempDozent2ListBoxPointer - 1).setEnabled(false);
						lbv.elementAt(tempDozent2ListBoxPointer + 1).setEnabled(false);
						
						bv.elementAt(tempAendernButtonPointer).setEnabled(false);
						bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);
						
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						focusOneOfThreeDozentListBoxPointer = 2;
						ladenDozenten();
						
						disableTabWidgetsDueDozentListBox(2);
						
					}
				});
				
				lbv.elementAt(dozent3ListBoxPointer).addFocusHandler(new FocusHandler() {
					
					int tempAendernButtonPointer = aendernButtonPointer;
					int tempDozent3ListBoxPointer = dozent3ListBoxPointer;
					int tempBelegungPointer = belegungPointer;
					
					public void onFocus(FocusEvent event) {
						focusBelegungPointer = tempBelegungPointer;
						focusDozentListBoxPointer = tempDozent3ListBoxPointer;
						focusAendernButtonPointer = tempAendernButtonPointer;
						
						String firstItem = lbv.elementAt(tempDozent3ListBoxPointer).getValue(0);
						lbv.elementAt(tempDozent3ListBoxPointer).clear();
						lbv.elementAt(tempDozent3ListBoxPointer).addItem(firstItem);
						
						lbv.elementAt(tempDozent3ListBoxPointer - 3).setEnabled(false);
						lbv.elementAt(tempDozent3ListBoxPointer - 2).setEnabled(false);
						lbv.elementAt(tempDozent3ListBoxPointer - 1).setEnabled(false);
						
						bv.elementAt(tempAendernButtonPointer).setEnabled(false);
						bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);
						
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						focusOneOfThreeDozentListBoxPointer = 3;
						ladenDozenten();
						
						disableTabWidgetsDueDozentListBox(3);
						
					}
				});
				
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 3, lbv.elementAt(dozent1ListBoxPointer));
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, lbv.elementAt(dozent2ListBoxPointer));
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 5, lbv.elementAt(dozent3ListBoxPointer));
				
				if (svBelegungen.elementAt(i).getSemesterverbaende().size() > 1) {
					bv.elementAt(aendernButtonPointer).setStyleName("gwt-MultipleSVs-Button");
				}
				bv.elementAt(aendernButtonPointer).addClickHandler(new ClickHandler() {
					
					
					int tempBelegungPointer = belegungPointer;
					
					int tempLvListBoxPointer = lvListBoxPointer;
					int tempRaumListBoxPointer = raumListBoxPointer;
					int tempDozent1ListBoxPointer = dozent1ListBoxPointer;
					int tempDozent2ListBoxPointer = dozent2ListBoxPointer;
					int tempDozent3ListBoxPointer = dozent3ListBoxPointer;
					
															
					public void onClick(ClickEvent event) {
						
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						auswahlAufhebenButton.setVisible(false);
						disableAllTabWidgets();
						
						boolean check1 = true;
						
						if (lbv.elementAt(tempLvListBoxPointer).getSelectedIndex() > 0) {
							svBelegungen.elementAt(tempBelegungPointer).setLehrveranstaltung(lvVectorforListBox.elementAt(lbv.elementAt(tempLvListBoxPointer).getSelectedIndex() - 1));
							check1 = false;
						}
						if (lbv.elementAt(tempRaumListBoxPointer).getSelectedIndex() > 0 && tempBelegungPointer == focusBelegungPointer) {
							svBelegungen.elementAt(tempBelegungPointer).setRaum(raeumeVectorforListBox.elementAt(lbv.elementAt(tempRaumListBoxPointer).getSelectedIndex() - 1));
							check1 = false;
						}
						
						Vector<Dozent> tempDozenten = new Vector<Dozent>();
						
						if (lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() > 0 && (!(lbv.elementAt(tempDozent1ListBoxPointer).getValue(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
							tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() - 1));
							check1 = false;
							
						}
						if (lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() == 0) {
							tempDozenten.add(svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(0));
													
						}
						
						if(lbv.elementAt(tempDozent1ListBoxPointer).getValue(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex()).equals("kein Dozent")) {
							check1 = false;
						}
						if (lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex() > 0 && (!(lbv.elementAt(tempDozent2ListBoxPointer).getValue(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
							tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex() - 1));
							check1 = false;
							
						}
						if (lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex() > 0 && (!(lbv.elementAt(tempDozent2ListBoxPointer).getValue(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
							tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex() - 1));
							check1 = false;
							
						}
						
						svBelegungen.elementAt(tempBelegungPointer).setDozenten(tempDozenten);
						
						if (!check1) {
							verwaltung.aendernBelegung(svBelegungen.elementAt(tempBelegungPointer), new AsyncCallback<Belegung>() {
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
									enableAllTabWidgets();
									ladenBelegungenButton.setEnabled(true);
									tabsLeerenButton.setEnabled(true);
																		
									verwaltung.auslesenBelegung(svBelegungen.elementAt(tempBelegungPointer), new AsyncCallback<Vector<Belegung>>() {
										public void onFailure(Throwable caught) {
											Window.alert(caught.getMessage());
										}
										public void onSuccess(Vector<Belegung> result) {
											svBelegungen.setElementAt(result.elementAt(0), tempBelegungPointer);
											
											raeumeVectorforListBox = null;
											focusBelegungPointer = null;
											focusRaumListBoxPointer = null;
											focusAendernButtonPointer = null;
										}
									});
								}
								public void onSuccess(Belegung result) {
									Window.alert("Belegung wurde erfolgreich geaendert");
									
									
									if (focusRaumListBoxPointer != null) {
										String tempFirstRaumItem = lbv.elementAt(focusRaumListBoxPointer).getValue(lbv.elementAt(focusRaumListBoxPointer).getSelectedIndex());
										lbv.elementAt(focusRaumListBoxPointer).clear();
										lbv.elementAt(focusRaumListBoxPointer).addItem(tempFirstRaumItem);
										
										raeumeVectorforListBox = null;
										focusBelegungPointer = null;
										focusRaumListBoxPointer = null;
										focusAendernButtonPointer = null;
									}
									
									if (focusDozentListBoxPointer != null) {
										String tempFirstDozent1Item;
										String tempFirstDozent2Item;
										String tempFirstDozent3Item;
										
										if (focusOneOfThreeDozentListBoxPointer == 1) {
											tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
										    tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer + 1).getValue(lbv.elementAt(focusDozentListBoxPointer + 1).getSelectedIndex());
											tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer + 2).getValue(lbv.elementAt(focusDozentListBoxPointer + 2).getSelectedIndex());
											
											lbv.elementAt(focusDozentListBoxPointer).clear();
											lbv.elementAt(focusDozentListBoxPointer + 1).clear();
											lbv.elementAt(focusDozentListBoxPointer + 2).clear();
											
											lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent1Item);
											lbv.elementAt(focusDozentListBoxPointer + 1).addItem(tempFirstDozent2Item);
											lbv.elementAt(focusDozentListBoxPointer + 2).addItem(tempFirstDozent3Item);
										}
										else if (focusOneOfThreeDozentListBoxPointer == 2) {
											tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer - 1).getValue(lbv.elementAt(focusDozentListBoxPointer - 1).getSelectedIndex());
											tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
											tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer + 1).getValue(lbv.elementAt(focusDozentListBoxPointer + 1).getSelectedIndex());
											
											lbv.elementAt(focusDozentListBoxPointer - 1).clear();
											lbv.elementAt(focusDozentListBoxPointer).clear();
											lbv.elementAt(focusDozentListBoxPointer + 1).clear();
											
											lbv.elementAt(focusDozentListBoxPointer - 1).addItem(tempFirstDozent1Item);
											lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent2Item);
											lbv.elementAt(focusDozentListBoxPointer + 1).addItem(tempFirstDozent3Item);
										}
										else {
											tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer - 2).getValue(lbv.elementAt(focusDozentListBoxPointer - 2).getSelectedIndex());
											tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer - 1).getValue(lbv.elementAt(focusDozentListBoxPointer - 1).getSelectedIndex());
											tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
											
											lbv.elementAt(focusDozentListBoxPointer - 2).clear();
											lbv.elementAt(focusDozentListBoxPointer - 1).clear();
											lbv.elementAt(focusDozentListBoxPointer).clear();
											
											lbv.elementAt(focusDozentListBoxPointer - 2).addItem(tempFirstDozent1Item);
											lbv.elementAt(focusDozentListBoxPointer - 1).addItem(tempFirstDozent2Item);
											lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent3Item);
										}
										
										dozentenVectorforListBox = null;
										focusBelegungPointer = null;
										focusDozentListBoxPointer = null;
										focusOneOfThreeDozentListBoxPointer = null;
										focusAendernButtonPointer = null;										
									}
									
									String itemLVListBox = lbv.elementAt(tempLvListBoxPointer).getValue(lbv.elementAt(tempLvListBoxPointer).getSelectedIndex());
									
									enableAllTabWidgets();
									
									ladenBelegungenButton.setEnabled(true);
									tabsLeerenButton.setEnabled(true);
									
									lbv.elementAt(tempLvListBoxPointer).clear();
									lbv.elementAt(tempLvListBoxPointer).addItem(itemLVListBox);
									
									for(Lehrveranstaltung lv : lvVectorforListBox) {
										lbv.elementAt(tempLvListBoxPointer).addItem(lv.getBezeichnung());
									}
								
								}
							});
						}
						if (check1) {
							Window.alert("Es wurde keine Aenderung vorgenommen");
							
							enableAllTabWidgets();
							ladenBelegungenButton.setEnabled(true);
							tabsLeerenButton.setEnabled(true);							
						}
					}
				});
				
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 6, bv.elementAt(aendernButtonPointer));
				
				bv.elementAt(loeschenButtonPointer).addClickHandler(new ClickHandler() {
					
					int tempRowPointer = rowPointer;
					int tempGridPointer = gridPointer;
					int tempBelegungPointer = belegungPointer;
					
					public void onClick(ClickEvent event) {
						auswahlAufhebenButton.setVisible(false);
						disableAllTabWidgets();
						ladenBelegungenButton.setEnabled(false);
						tabsLeerenButton.setEnabled(false);
						
						verwaltung.loeschenBelegungen(svBelegungen.elementAt(tempBelegungPointer), semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()), new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								
								enableAllTabWidgets();
								ladenBelegungenButton.setEnabled(true);
								tabsLeerenButton.setEnabled(true);
								
							}
							public void onSuccess(Void result) {
								Window.alert("Die Belegung wurde erfolgreich geloescht!");
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 1, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 2, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 3, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 4, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 5, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 6, null);
								gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 7, null);
								//svBelegungen.removeElementAt(tempBelegungPointer);
								
								enableAllTabWidgets();
								
								ladenBelegungenButton.setEnabled(true);
								tabsLeerenButton.setEnabled(true);
								
							}
						});
					}
				});
				
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 7, bv.elementAt(loeschenButtonPointer));
			
				
				lvListBoxPointer = lvListBoxPointer + 5;
				raumListBoxPointer = raumListBoxPointer + 5;
				dozent1ListBoxPointer = dozent1ListBoxPointer + 5;
				dozent2ListBoxPointer = dozent2ListBoxPointer + 5;
				dozent3ListBoxPointer = dozent3ListBoxPointer + 5;
				
				aendernButtonPointer = aendernButtonPointer + 2;
				loeschenButtonPointer = loeschenButtonPointer + 2;
				
				gridPointer = 0;
				rowPointer = 0;
		}
	}
	
	public void fillContentTabAnlegen() {		
		
			belegungPointer = 0;
		
			for (int i = 0; i < svBelegungen.size(); i++) {
				belegungPointer = i;
									
				if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
					gridPointer = 0;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 7 && svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {				
					gridPointer = 1;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
					gridPointer = 2;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
					gridPointer = 3;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
					gridPointer = 4;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
					gridPointer = 5;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
				}
				if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
					gridPointer = 6;
					rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
				}
				
				Label lvLabel = new Label();
				lvLabel.setText(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 1, lvLabel);
				
				Label raumLabel = new Label();
				raumLabel.setText(svBelegungen.elementAt(i).getRaum().getBezeichnung());
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 2, raumLabel);
				
				Label dozentLabel1 = new Label();
				dozentLabel1.setText(svBelegungen.elementAt(i).getDozenten().elementAt(0).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(0).getVorname());
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 3, dozentLabel1);
								
				if (svBelegungen.elementAt(i).getDozenten().size() > 1) {
					Label dozentLabel2 = new Label();
					dozentLabel2.setText(svBelegungen.elementAt(i).getDozenten().elementAt(1).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(1).getVorname());
					gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, dozentLabel2);
				}
				
				if (svBelegungen.elementAt(i).getDozenten().size() > 2) {
					Label dozentLabel3 = new Label();
					dozentLabel3.setText(svBelegungen.elementAt(i).getDozenten().elementAt(2).getNachname() + " " + svBelegungen.elementAt(i).getDozenten().elementAt(2).getVorname());
					gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, dozentLabel3);
				}
								
				gridPointer = 0;
				rowPointer = 0;
		}
	}
	
	public void aendernMaske(boolean info) {
		this.anlegenMaske = info;
		if (info) {
						
			anlegenGrid = new Grid(4, 6);
			Label tagLabel = new Label("Tag");
			Label uhrzeitLabel = new Label("Uhrzeit");
			Label lvLabel = new Label("Lehrveranstaltung");
			Label raumLabel = new Label("Raum");
			Label dozentLabel = new Label("Dozent");
			
			anlegenGrid.setWidget(0, 0, tagLabel);
			anlegenGrid.setWidget(0, 1, uhrzeitLabel);
			anlegenGrid.setWidget(0, 2, lvLabel);
			anlegenGrid.setWidget(0, 3, raumLabel);
			anlegenGrid.setWidget(0, 4, dozentLabel);
			
			anlegenTagListBox = new ListBox();
			anlegenTagListBox.addItem("bitte wählen");
			anlegenTagListBox.addItem("Montag");
			anlegenTagListBox.addItem("Dienstag");
			anlegenTagListBox.addItem("Mittwoch");
			anlegenTagListBox.addItem("Donnerstag");
			anlegenTagListBox.addItem("Freitag");
			anlegenTagListBox.addItem("Samstag");
			anlegenTagListBox.addItem("Sonntag");
			
			addChangeHandlerToAnlegenTagListBox();
			anlegenGrid.setWidget(1, 0, anlegenTagListBox);
			
			anlegenUhrzeitListBox = new ListBox();
			anlegenUhrzeitListBox.addItem("bitte wählen");
			anlegenUhrzeitListBox.addItem("08:15 - 09:45");
			anlegenUhrzeitListBox.addItem("10:00 - 11:30");
			anlegenUhrzeitListBox.addItem("11:45 - 13:15");
			anlegenUhrzeitListBox.addItem("14:15 - 15:45");
			anlegenUhrzeitListBox.addItem("16:00 - 17:30");
			anlegenUhrzeitListBox.addItem("17:45 - 19:15");
			anlegenUhrzeitListBox.addItem("19:30 - 21:00");
			
			addChangeHandlerToAnlegenUhrzeitListBox();
			anlegenGrid.setWidget(1, 1, anlegenUhrzeitListBox);
			
			anlegenLVListBox = new ListBox();
			anlegenRaumListBox = new ListBox();
			anlegenDozent1ListBox = new ListBox(); 
			anlegenDozent2ListBox = new ListBox(); 
			anlegenDozent3ListBox = new ListBox(); 
			
			addChangeHandlerToAnlegenLVListBox();
			anlegenGrid.setWidget(1, 2, anlegenLVListBox);
			
			anlegenGrid.setWidget(1, 3, anlegenRaumListBox);
			anlegenGrid.setWidget(1, 4, anlegenDozent1ListBox);
			anlegenGrid.setWidget(2, 4, anlegenDozent2ListBox);
			anlegenGrid.setWidget(3, 4, anlegenDozent3ListBox);
			
			anlegenTagListBox.setEnabled(false);
			anlegenUhrzeitListBox.setEnabled(false);
			anlegenLVListBox.setEnabled(false);
			anlegenRaumListBox.setEnabled(false);
			anlegenDozent1ListBox.setEnabled(false);
			anlegenDozent2ListBox.setEnabled(false);
			anlegenDozent3ListBox.setEnabled(false);
			
			anlegenButton = new Button("Belegung anlegen");
			anlegenButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					belegungAnlegen();
					anlegenButton.setEnabled(false);
					ladenBelegungenButton.setEnabled(false);
					tabsLeerenButton.setEnabled(false);
					anlegenTagListBox.setEnabled(false);
					anlegenUhrzeitListBox.setEnabled(false);
					anlegenLVListBox.setEnabled(false);
					anlegenRaumListBox.setEnabled(false);
					anlegenDozent1ListBox.setEnabled(false);
					anlegenDozent2ListBox.setEnabled(false);
					anlegenDozent3ListBox.setEnabled(false);
				}
			});
			
			anlegenGrid.setWidget(3, 5, anlegenButton);
			
			this.anlegenPanel = new VerticalPanel();
			this.anlegenPanel.add(anlegenGrid);
			
			weitereSV = new Button("Weitere Semesterverbände hinzufügen");
			weitereSV.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {					
					if (studiengangListBox.getSelectedIndex() != 0) {
						anlegenWSVPanel.setVisible(true);
						studiengangListBox.setEnabled(false);
						semesterverbandListBox.setEnabled(false);
					}
					else {
						Window.alert("Bitte wählen Sie zuerst einen Hauptsemesterverband");
					}
				}
			});
			this.anlegenPanel.add(weitereSV);
			
			this.add(anlegenPanel);
			
			studiengangAnlegen = new Label("Studiengang :");
			studiengangListBoxAnlegen = new ListBox();
			addChangeHandlerTosStudiengangListBoxAnlegen();
			
			semesterverbandAnlegen = new Label("Semesterverband :");
			semesterverbandListBoxAnlegen = new ListBox();
			semesterverbandListBoxAnlegen.setEnabled(false);
			
			hinzufuegenButtonAnlegen = new Button("Hinzufuegen");
			hinzufuegenButtonAnlegen.addClickHandler(new ClickHandler() {
				public void onClick (ClickEvent event) {
					if (SVvonNeuerBelegung == null) {
						SVvonNeuerBelegung = new Vector<Semesterverband>();
					}
					if (SGKuerzel == null) {
						SGKuerzel = new Vector<String>();
					}
					if (semesterverbaendeVectorForListBoxAnlegen == null || semesterverbaendeVectorForListBoxAnlegen.size() < 1) {
						Window.alert("Bitte wählen Sie zuerst einen Semesterverband");
					}
					else {
						boolean check = true;
						if(semesterverbaendeVectorForListBox == null || semesterverbaendeVectorForListBox.size() < 1) {
							Window.alert("Bitte wählen Sie zuerst den Hauptsemesterverband aus");
							check = false;
							
							studiengangListBox.setEnabled(true);
							semesterverbandListBox.setEnabled(true);
						}
						if (check) {
							for (Semesterverband sv : SVvonNeuerBelegung) {
								if(sv.getId() == semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()).getId() ||
										sv.getId() == semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()).getId()) {
									Window.alert("Der Semesterverband ist bereits hinzugefuegt");
									check = false;
									break;
								}
							}
						}
						if (check) {
							if (semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()).getId() == 
									semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()).getId()) {
								Window.alert("Der Semesterverband ist bereits hinzugefuegt");
								check = false;
							}
						}
						if (check) {
							SVvonNeuerBelegung.addElement(semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()));
							SGKuerzel.addElement(studiengaengeVectorForListBox.elementAt(studiengangListBoxAnlegen.getSelectedIndex() - 1).getKuerzel());
							createFlexTable();
						}
						
					}
				}
			});
			
			anlegenWSVGrid = new Grid(3, 3);
			anlegenWSVGrid.setWidget(0, 0, studiengangAnlegen);
			anlegenWSVGrid.setWidget(0, 1, studiengangListBoxAnlegen);
			anlegenWSVGrid.setWidget(1, 0, semesterverbandAnlegen);
			anlegenWSVGrid.setWidget(1, 1, semesterverbandListBoxAnlegen);
			anlegenWSVGrid.setWidget(2, 0, hinzufuegenButtonAnlegen);
			
			this.anlegenWSVPanel = new VerticalPanel();
			this.anlegenWSVPanel.add(anlegenWSVGrid);
			
			svTable = new FlexTable();
			
						
			this.anlegenWSVPanel.add(svTable);			
			this.anlegenWSVPanel.setVisible(false);
			
			this.add(anlegenWSVPanel);
		}
	
	}
	
	public void createFlexTable() {		
		svTable.removeAllRows();
		svTable.setText(0, 0, "Studiengang");
		svTable.setText(0, 1, "Semesterverband");
		
		if (SVvonNeuerBelegung != null && SVvonNeuerBelegung.size() > 0) {
			for (int i = 0; i < SVvonNeuerBelegung.size(); i++) {
				final int row = svTable.getRowCount();
				svTable.setWidget(row, 0, new Label(SGKuerzel.elementAt(i)));
				svTable.setWidget(row, 1, new Label(SVvonNeuerBelegung.elementAt(i).getJahrgang()));
				
				Button loeschenButton = new Button("X");
				loeschenButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						
						int rowIndex = svTable.getCellForEvent(event).getRowIndex();
						svTable.removeRow(rowIndex);
						
						SVvonNeuerBelegung.removeElementAt(rowIndex - 1);
						SGKuerzel.removeElementAt(rowIndex - 1);
					}
				});
				
				svTable.setWidget(row, 2, loeschenButton);
			}
		}
	}
	
	public void belegungAnlegen() {
		
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		
		int zeitslotIndex = 0;
		Vector<Dozent> vd = new Vector<Dozent>();
		Vector<Semesterverband> vsv = new Vector<Semesterverband>();
		boolean check = true;
		
		if(anlegenLVListBox.getSelectedIndex() <= 0) {
			check = false;
		}	
		if(anlegenRaumListBox.getSelectedIndex() <= 0) {
			check = false;
		}
		if((anlegenDozent1ListBox.getSelectedIndex() <= 0) && (anlegenDozent2ListBox.getSelectedIndex() <= 0) && (anlegenDozent3ListBox.getSelectedIndex() <= 0)) {
			check = false;
		}
		if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
			zeitslotIndex = ((anlegenTagListBox.getSelectedIndex() - 1) * 7) + anlegenUhrzeitListBox.getSelectedIndex();
		}
		else {
			check = false;
		}
		
		vsv.addElement(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex()));
		if(SVvonNeuerBelegung != null) {
			for (Semesterverband sv : SVvonNeuerBelegung) {
				vsv.addElement(sv);
			}
		}
		
		if (anlegenDozent1ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent1ListBox.getSelectedIndex() - 1));
		}
		if (anlegenDozent2ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent2ListBox.getSelectedIndex() - 1));
		}
		if (anlegenDozent3ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent3ListBox.getSelectedIndex() - 1));
		}
		if(check) {
			verwaltung.anlegenBelegung(lvVectorforListBox.elementAt(anlegenLVListBox.getSelectedIndex() - 1), raeumeVectorforListBox.elementAt(anlegenRaumListBox.getSelectedIndex() - 1), 
					zeitslotsVectorForListBox.elementAt(zeitslotIndex - 1), vd, vsv, new AsyncCallback<Belegung>() {
				public void onFailure(Throwable caught) {
					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
					Window.alert(caught.getMessage());
					svTable.removeAllRows();
					if (SVvonNeuerBelegung != null) {
						SVvonNeuerBelegung.clear();
					}
					anlegenButton.setEnabled(true);
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
					anlegenTagListBox.setEnabled(true);
					anlegenUhrzeitListBox.setEnabled(true);
					anlegenLVListBox.setEnabled(true);
					anlegenRaumListBox.setEnabled(true);
					anlegenDozent1ListBox.setEnabled(true);
					anlegenDozent2ListBox.setEnabled(true);
					anlegenDozent3ListBox.setEnabled(true);
				}
				public void onSuccess(Belegung result) {
					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
					Window.alert("Belegung wurde erfolgreich angelegt");
					ladenBelegungen();
					svTable.removeAllRows();
					if (SVvonNeuerBelegung != null) {
						SVvonNeuerBelegung.clear();
					}
					ladenBelegungenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
				}
			});
		}
		else {
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
			Window.alert("Bitte vervollstaendigen Sie die neue Belegung");
			
			ladenBelegungenButton.setEnabled(true);
			tabsLeerenButton.setEnabled(true);
		}
	}
	
	public void tabVisibility() {
		if(tabVisibilityButton.getText().equals("Übersicht verdecken")) {
			tabPanel.setVisible(false);
			tabVisibilityButton.setText("Übersicht einblenden");
			return;
		}
		if(tabVisibilityButton.getText().equals("Übersicht einblenden")) {
			tabPanel.setVisible(true);
			tabVisibilityButton.setText("Übersicht verdecken");
			return;
		}
	}
	
	public void clearEntries() {
		semesterverbandListBox.clear();
		svBelegungen = null;
		
		if (!anlegenMaske) {
			auswahlAufhebenButton.setVisible(false);
		}
		
		if (anlegenMaske) {
			semesterverbandListBoxAnlegen.clear();
			SVvonNeuerBelegung = null;
			SGKuerzel = null;
			if (svTable != null) {
				svTable.removeAllRows();
			}
			anlegenWSVPanel.setVisible(false);
		}
		for (int i = 0; i < gridVector.size(); i++) {
			for (int j = 1; j < gridVector.elementAt(i).getRowCount(); j++) {
				for (int k = 1; k < gridVector.elementAt(i).getColumnCount(); k++) {
					gridVector.elementAt(i).setWidget(j, k, null);
				}
			}
		}
		if (anlegenMaske) {
			anlegenTagListBox.setSelectedIndex(0);
			anlegenUhrzeitListBox.setSelectedIndex(0);
			anlegenLVListBox.setSelectedIndex(0);
			anlegenRaumListBox.setSelectedIndex(0);
			anlegenDozent1ListBox.setSelectedIndex(0);
			anlegenDozent2ListBox.setSelectedIndex(0);
			anlegenDozent3ListBox.setSelectedIndex(0);
			
			anlegenTagListBox.setEnabled(false);
			anlegenUhrzeitListBox.setEnabled(false);
			anlegenLVListBox.setEnabled(false);
			anlegenRaumListBox.setEnabled(false);
			anlegenDozent1ListBox.setEnabled(false);
			anlegenDozent2ListBox.setEnabled(false);
			anlegenDozent3ListBox.setEnabled(false);
			
			studiengangListBoxAnlegen.setSelectedIndex(0);
			ladenSemesterverbaende(true);
			semesterverbandListBoxAnlegen.setSelectedIndex(0);
			semesterverbandListBoxAnlegen.setEnabled(false);
		}
		
		studiengangListBox.setSelectedIndex(0);
		ladenSemesterverbaende(false);
		semesterverbandListBox.setSelectedIndex(0);
		semesterverbandListBox.setEnabled(false);
		
				
		studiengangListBox.setEnabled(true);
		semesterverbandListBox.setEnabled(true);
		
		if (semesterverbaendeVectorForListBox == null || semesterverbaendeVectorForListBox.size() == 0) {
			semesterverbandListBox.setEnabled(false);
		}
	}
	
	void setDtvm(DozentTreeViewModel dtvm) {
		this.dtvm = dtvm;
	}
	
}
