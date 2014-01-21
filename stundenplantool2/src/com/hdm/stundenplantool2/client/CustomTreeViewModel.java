package com.hdm.stundenplantool2.client;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.hdm.stundenplantool2.shared.*;
import com.hdm.stundenplantool2.shared.bo.*;

import java.util.Vector;
import java.util.List;

import com.google.gwt.view.client.ListDataProvider;

/**
 * Diese Klasse stellt die Funktionalität des im Projekt verwendeten CellTree
 * bereit. Sie definiert die Knoten eines Baumes und dessen Reaktionen auf
 * Ereignisse. 
 * 
 * @author Rathke (implement: Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella)
 * @version 1.0
 * 
 */
public class CustomTreeViewModel implements TreeViewModel {

	/**
	 * Referenzen auf alle Instanzen der "Formklassen" um Zugriff auf deren Methoden
	 * zu haben
	 */
	private DozentForm dF;
	private BelegungForm bF;
	private LehrveranstaltungForm lF;
	private StudiengangForm sgF;
	private SemesterverbandForm svF;
	private RaumForm rF;

	/**
	 * Referenz auf die Entry-Point-Klasse um Zugriff auf deren Methoden zu haben
	 */
	Stundenplantool2 spt2;

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	private VerwaltungAsync verwaltung = null;

	/**
	 * Container welche alle Objekte enthalten, welche im Tree als Blätter oder
	 * Knoten abgebildet werden
	 */
	private ListDataProvider<Lehrveranstaltung> lehrveranstaltungDataProvider;
	private ListDataProvider<Dozent> dozentDataProvider;
	private ListDataProvider<Raum> raumDataProvider;
	private ListDataProvider<Semesterverband> semesterVerbandDataProvider;
	private ListDataProvider<Studiengang> studiengangDataProvider;
	private ListDataProvider<String> dummyDataProvider;

	/**
	 * Flags die zur Bestimmung dienen, wann angezeigte Studiengänge "leaf" sind,
	 * da Studiengänge auch zu weiteren Untergliederung von Lehrveranstaltungen
	 * und Semesterverbände benutzt werden
	 */
	private boolean unterMenuLVzuSG = false;
	private boolean unterMenuSVzuSG = false;

	/**
	 * Referenz auf das CellTree-Objekt um Zugriff auf dessen Methoden zu haben
	 */
	private CellTree cellTree = null;

	/**
	 * Referenz auf das (Root)TreeNode-Objekt des CellTrees um Zugriff auf dessen 
	 * Kindknoten zu bekommen, damit Knoten automatisch geschlossen werden können.
	 * Dies dient dem Zweck, zu verhindern, dass Studiengänge mehrfach und unter
	 * verschiedenen Gesichtspunkten im CellTree sichtbar sind, was zu Problemen
	 * führt
	 */
	private TreeNode rootNode;

	/**
	 * ProvidesKey<Object>-Objekt welches der Konstruktor der SingleSelectionModel<Object>-Klasse
	 * als Argument verlangt. Dies dient zur eindeutigen Identifikation eines jeden Kindelements
	 * im CellTree
	 */
	private ProvidesKey<Object> keyProvider = new ProvidesKey<Object>() {
		public Integer getKey(Object object) {

			if (object == null) {
				return null;
			} 
			else if (object instanceof Lehrveranstaltung) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Dozent) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Raum) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Semesterverband) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Studiengang) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Belegung) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Editor") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Report") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Anlegen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozent") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String
					&& (String) object == "Lehrveranstaltung") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String
					&& (String) object == "Belegung") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Raum") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Semesterverband") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studiengang") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Verwalten") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozenten") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Lehrveranstaltungen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Belegungen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Räume") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Semesterverbände") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studiengänge") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozentenplan") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Raumplan") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studentenplan") {
				return new Integer(object.hashCode());
			} 
			else {
				return null;
			}
		}
	};
	
	/**
	 * SingleSelectionModel<Object>-Objekt welches der Konstruktor der DefaultNodeInfo<String>-Klasse
	 * als Argument verlangt. Das SingleSelectionModel<Object>-Objekt enthält einen "SelectionChangeHandler",
	 * in welchem die Reaktionen definiert werden, welche bei Auswahl eines spezifischen Kind-Element-Typs
	 * angestoßen werden
	 */
	private SingleSelectionModel<Object> selectionModel = new SingleSelectionModel<Object>(
			keyProvider);

	/**
	 * Komstruktor welcher den "SelectionChangeHandler" definiert und dem 
	 * "selectionModel" hinzufügt, so dass der CellTree-Funktionsbereit ist
	 * 
	 * @param	Referenz auf ein Proxy-Objekt. 
	 */
	public CustomTreeViewModel(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		// Initialisieren, definieren und "adden" eines "SelectionChangeHandlers"
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {
				Object selection = selectionModel.getSelectedObject();

				/*
				 *  Bei Klick auf ein "Dozent-Objekt", wird dieses erneut geladen und
				 *  mittelbar die DozentForm in den Arbeitsbereich der GUI geladen
				 */
				if (selection instanceof Dozent) {

					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");

					verwaltung.auslesenDozent((Dozent) selection, new AsyncCallback<Vector<Dozent>>() {
						public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
						}
	
						public void onSuccess(Vector<Dozent> result) {
							setSelectedDozent(result.elementAt(0));
						}
					});

				} 
				/*
				 *  Bei Klick auf ein "Lehrveranstaltung-Objekt", wird dieses erneut geladen und
				 *  mittelbar die LehrveranstaltungForm in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Lehrveranstaltung) {

					verwaltung.auslesenLehrveranstaltung((Lehrveranstaltung) selection,	new AsyncCallback<Vector<Lehrveranstaltung>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Vector<Lehrveranstaltung> result) {
								setSelectedLehrveranstaltung(result.elementAt(0));
							}
					});

				} 
				/*
				 *  Bei Klick auf ein "Raum-Objekt", wird  mittelbar die RaumForm 
				 *  in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Raum) {
					setSelectedRaum((Raum) selection);
					
				} 
				/*
				 *  Bei Klick auf ein "Semesterverband-Objekt", wird dieses erneut geladen und
				 *  mittelbar die SemesterverbandForm in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Semesterverband) {

					verwaltung.auslesenSemesterverband((Semesterverband) selection,	new AsyncCallback<Vector<Semesterverband>>() {
						public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
						}
						
						public void onSuccess(Vector<Semesterverband> result) {
								setSelectedSemesterverband(result.elementAt(0));
						}
					});
				} 
				/*
				 *  Bei Klick auf ein "Semesterverband-Objekt", wird dieses erneut geladen und
				 *  mittelbar die SemesterverbandForm in den Arbeitsbereich der GUI geladen
				 *  (Voraussetzung: unterMenuLVzuSG = false && unterMenuSVzuSG = false)
				 */
				else if (selection instanceof Studiengang && !unterMenuLVzuSG && !unterMenuSVzuSG) {

					verwaltung.auslesenStudiengang((Studiengang) selection,	new AsyncCallback<Vector<Studiengang>>() {
						public void onFailure(Throwable caught) {
							DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
							Window.alert(caught.getMessage());
						}

						public void onSuccess(Vector<Studiengang> result) {
							setSelectedStudiengang(result.elementAt(0));
						}
					});

				} 
				
				/*
				 *  Bei Klick auf ein "String-Objekt" wird die entsprechende Formklasse in der
				 *  "Anlegen-Variante" in die in den Arbeitsbereich GUI geladen
				 */
				else if (selection instanceof String && (String) selection == "Belegungen") {
					setSelectedBelegung();
				} 
				else if (selection instanceof String && (String) selection == "Belegung") {
					belegungAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Dozent") {
					dozentAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Lehrveranstaltung") {
					lehrveranstaltungAnlegenMaske();
				}
				else if (selection instanceof String && (String) selection == "Studiengang") {
					studiengangAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Semesterverband") {
					semesterverbandAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Raum") {
					raumAnlegenMaske();
				}
				else if (selection instanceof String && (String) selection == "Studentenplan") {
					setSelectedStudentenplan();
				}
				else if (selection instanceof String && (String) selection == "Dozentenplan") {
					setSelectedDozentenplan();
				}
				else if (selection instanceof String && (String) selection == "Raumplan") {
					setSelectedRaumplan();
				}
			}
		});

	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Dozenten zu bearbeiten
	 * 
	 * @param	Referenz auf ein Dozent-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedDozent(Dozent dozent) {
		spt2.clearInfoPanels();
		spt2.setDozentFormToMain();
		this.dF.setDtvm(this);
		this.dF.visibiltyAendernButtons();
		this.dF.setShownDozent(dozent);
		this.dF.fillForm();
		this.dF.lehrveranstaltungenAnzeigen();
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Dozenten anzulegen
	 */
	public void dozentAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setDozentFormToMain();
		this.dF.setDtvm(this);
		dF.noVisibiltyAendernButtons();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Lehrveranstaltung zu bearbeiten
	 * 
	 * @param	Referenz auf ein Lehrveranstaltung-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {
		spt2.clearInfoPanels();
		spt2.setLehrveranstaltungFormToMain();
		this.lF.setDtvm(this);
		this.lF.setShownLehrveranstaltung(lehrveranstaltung);
		this.lF.fillForm();
		this.lF.dozentenAnzeigen();
		this.lF.studiengaengeAnzeigen();
		this.lF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Lehrveranstaltung anzulegen
	 */
	public void lehrveranstaltungAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setLehrveranstaltungFormToMain();
		this.lF.setDtvm(this);
		this.lF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Raum zu bearbeiten
	 * 
	 * @param	Referenz auf ein Raum-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedRaum(Raum raum) {
		spt2.clearInfoPanels();
		spt2.setRaumFormToMain();
		this.rF.setDtvm(this);
		this.rF.setShownRaum(raum);
		this.rF.fillForm();
		this.rF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Raum anzulegen
	 */
	public void raumAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setRaumFormToMain();
		this.rF.setDtvm(this);
		this.rF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Semesterverband zu bearbeiten
	 * 
	 * @param	Referenz auf ein Semesterverband-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedSemesterverband(Semesterverband semesterverband) {
		spt2.clearInfoPanels();
		spt2.setSemesterverbandFormToMain();
		this.svF.setDtvm(this);
		this.svF.setShownSemesterverband(semesterverband);
		this.svF.ladenStudiengaenge();
		this.svF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Semesterverband anzulegen
	 */
	public void semesterverbandAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setSemesterverbandFormToMain();
		this.svF.setDtvm(this);
		this.svF.ladenStudiengaenge();
		this.svF.anlegenMaske();

	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Studiengang zu bearbeiten
	 * 
	 * @param	Referenz auf ein Studiengang-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedStudiengang(Studiengang studiengang) {
		spt2.clearInfoPanels();
		spt2.setStudiengangFormToMain();
		this.sgF.setDtvm(this);
		this.sgF.setShownStudiengang(studiengang);
		this.sgF.fillForm();
		this.sgF.lvAnzeigen();
		this.sgF.semesterverbaendeAnzeigen();
		this.sgF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Studiengang anzulegen
	 */
	public void studiengangAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setStudiengangFormToMain();
		this.sgF.setDtvm(this);
		this.sgF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Belegung zu bearbeiten
	 */
	public void setSelectedBelegung() {
		spt2.clearInfoPanels();
		this.spt2.setBelegungFormToMain();
		this.bF.setDtvm(this);
		this.bF.aendernMaske(false);
		this.bF.ladenStudiengaenge();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Belegung anzulegen
	 */
	public void belegungAnlegenMaske() {
		spt2.clearInfoPanels();
		this.spt2.setBelegungFormToMain();
		this.bF.setDtvm(this);
		this.bF.aendernMaske(true);
		this.bF.ladenStudiengaenge();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Studentenplan
	 * erzeugen zu lassen
	 */
	public void setSelectedStudentenplan() {
		this.spt2.setStudentenPlanFormToMain();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Dozentenplan
	 * erzeugen zu lassen
	 */
	public void setSelectedDozentenplan() {
		this.spt2.setDozentenPlanFormToMain();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Raumplan
	 * erzeugen zu lassen
	 */
	public void setSelectedRaumplan() {
		this.spt2.setRaumPlanFormToMain();
	}

	/**
	 * Methode welche das Interface "TreeViewModel" vorschreibt. Hier werden die Kind-Elemente
	 * eines jeden Knoten im CellTree definiert
	 * 
	 * @param	Generic, welches das gegenwärtig gewählte Knoten-Element repräsentiert  
	 */
	public <T> NodeInfo<?> getNodeInfo(T value) {

		// Wurzelknoten enthält "Editor" und "Report" als Kind-Elemente
		if (value instanceof String && (String) value == "Root") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dummyDataProvider = new ListDataProvider<String>();

			String editor = "Editor";
			String report = "Report";

			dummyDataProvider.getList().add(editor);
			dummyDataProvider.getList().add(report);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}
		
		// "Editor" enthält "Anlegen" und "Verwalten" als Kind-Elemente		
		if (value instanceof String && (String) value == "Editor") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dummyDataProvider = new ListDataProvider<String>();

			String anlegen = "Anlegen";
			String verwalten = "Verwalten";

			dummyDataProvider.getList().add(anlegen);
			dummyDataProvider.getList().add(verwalten);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Anlegen" enthält die anlegbaren BusinessObjects in String-Repräsentation als Kind-Elemente
		if (value instanceof String && (String) value == "Anlegen") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dummyDataProvider = new ListDataProvider<String>();

			String lehrveranstaltung = "Lehrveranstaltung";
			String dozent = "Dozent";
			String belegung = "Belegung";
			String raum = "Raum";
			String semesterverband = "Semesterverband";
			String studiengang = "Studiengang";

			dummyDataProvider.getList().add(lehrveranstaltung);
			dummyDataProvider.getList().add(dozent);
			dummyDataProvider.getList().add(belegung);
			dummyDataProvider.getList().add(raum);
			dummyDataProvider.getList().add(semesterverband);
			dummyDataProvider.getList().add(studiengang);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Anlegen" enthält die editierbaren BusinessObjects in String-Repräsentation als Kind-Elemente
		if (value instanceof String && (String) value == "Verwalten") {

			addOpenHandler();

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dummyDataProvider = new ListDataProvider<String>();

			String lehrveranstaltungen = "Lehrveranstaltungen";
			String dozenten = "Dozenten";
			String belegungen = "Belegungen";
			String raeume = "Räume";
			String semesterverbaende = "Semesterverbände";
			String studiengaenge = "Studiengänge";

			dummyDataProvider.getList().add(lehrveranstaltungen);
			dummyDataProvider.getList().add(dozenten);
			dummyDataProvider.getList().add(belegungen);
			dummyDataProvider.getList().add(raeume);
			dummyDataProvider.getList().add(semesterverbaende);
			dummyDataProvider.getList().add(studiengaenge);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		/*
		 *  "Lehrveranstaltungen" enthält alle Studiengang-Objekte als Kind-Elemente,
		 *  diese dienen nur zu weiteren Untergliederung der eigentlichen Lehrveranstaltungen
		 */
		if (value instanceof String && (String) value == "Lehrveranstaltungen") {

			unterMenuLVzuSG = true;
			unterMenuSVzuSG = false;

			studiengangDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangDataProvider, new StudiengangCell(), selectionModel, null);
		}

		/*
		 *  Studiengang-Objekte enthalten alle Lehrveranstaltungs-Objekte als Kind-Elemente
		 *  (Voraussetzung: unterMenuLVzuSG = true)
		 */
		if (value instanceof Studiengang && unterMenuLVzuSG) {

			unterMenuLVzuSG = true;
			unterMenuSVzuSG = false;

			lehrveranstaltungDataProvider = new ListDataProvider<Lehrveranstaltung>();
			verwaltung.auslesenLehrveranstaltungenNachSG((Studiengang) value,
					new AsyncCallback<Vector<Lehrveranstaltung>>() {
						public void onFailure(Throwable t) {
							Window.alert(t.toString());
						}

						public void onSuccess(Vector<Lehrveranstaltung> lehrveranstaltungen) {
							for (Lehrveranstaltung lehrveranstaltung : lehrveranstaltungen) {
								lehrveranstaltungDataProvider.getList().add(lehrveranstaltung);
							}
						}
					});

			return new DefaultNodeInfo<Lehrveranstaltung>(lehrveranstaltungDataProvider, new LehrveranstaltungCell(),
					selectionModel, null);
		}

		// "Dozenten" enthält Dozenten-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Dozenten") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dozentDataProvider = new ListDataProvider<Dozent>();

			verwaltung.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Dozent> dozenten) {
					for (Dozent dozent : dozenten) {
						dozentDataProvider.getList().add(dozent);
					}
				}
			});

			return new DefaultNodeInfo<Dozent>(dozentDataProvider, new DozentCell(), selectionModel, null);
		}

		// "Räume" enthält Raum-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Räume") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			raumDataProvider = new ListDataProvider<Raum>();
			verwaltung.auslesenAlleRaeume(new AsyncCallback<Vector<Raum>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Raum> raeume) {
					for (Raum raum : raeume) {
						raumDataProvider.getList().add(raum);
					}
				}
			});

			return new DefaultNodeInfo<Raum>(raumDataProvider, new RaumCell(), selectionModel, null);
		}

		/*
		 *  "Semesterverbände" enthält alle Studiengang-Objekte als Kind-Elemente,
		 *  diese dienen nur zu weiteren Untergliederung der eigentlichen Semesterverbände
		 */
		if (value instanceof String && (String) value == "Semesterverbände") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = true;

			studiengangDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangDataProvider, new StudiengangCell(), selectionModel, null);
		}

		/*
		 *  Studiengang-Objekte enthalten alle Semesterverband-Objekte als Kind-Elemente
		 *  (Voraussetzung: unterMenuSVzuSG = true)
		 */
		if (value instanceof Studiengang && unterMenuSVzuSG) {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = true;

			semesterVerbandDataProvider = new ListDataProvider<Semesterverband>();
			verwaltung.auslesenSemesterverbaendeNachStudiengang((Studiengang) value, new AsyncCallback<Vector<Semesterverband>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Semesterverband> semesterverbaende) {
					for (Semesterverband semesterverband : semesterverbaende) {
						semesterVerbandDataProvider.getList().add(semesterverband);
					}
				}
			});

			return new DefaultNodeInfo<Semesterverband>(semesterVerbandDataProvider, new SemesterverbandCell(),	selectionModel, null);
		}

		// "Studiengänge" enthält Studiengang-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Studiengänge") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			studiengangDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangDataProvider, new StudiengangCell(), selectionModel, null);
		}

		// "Report" enthält "Studentenplan", "Dozentenplan" und "Raumplan" als Kind-Elemente
		if (value instanceof String && (String) value == "Report") {

			spt2.popupInfo();

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			dummyDataProvider = new ListDataProvider<String>();

			String a = "Studentenplan";
			String b = "Dozentenplan";
			String c = "Raumplan";

			dummyDataProvider.getList().add(a);
			dummyDataProvider.getList().add(b);
			dummyDataProvider.getList().add(c);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		return null;

	}

	public void updateDozent(Dozent dozent) {
		List<Dozent> dozentList = dozentDataProvider.getList();
		int i = 0;
		for (Dozent a : dozentList) {
			if (a.getId() == dozent.getId()) {
				dozentList.set(i, dozent);
				dozentDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void loeschenDozent(Dozent dozent) {

		int i = 0;

		for (Dozent d : dozentDataProvider.getList()) {
			if (d.getId() == dozent.getId()) {

				dozentDataProvider.getList().remove(i);
				dozentDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}

	}

	public void addDozent(Dozent dozent) {
		if (dozentDataProvider != null) {
			dozentDataProvider.getList().add(
					dozentDataProvider.getList().size(), dozent);
			dozentDataProvider.refresh();
		}
	}

	public void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {

		int i = 0;

		for (Lehrveranstaltung d : lehrveranstaltungDataProvider.getList()) {
			if (d.getId() == lehrveranstaltung.getId()) {

				lehrveranstaltungDataProvider.getList().remove(i);
				lehrveranstaltungDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void updateLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {
		List<Lehrveranstaltung> lehrveranstaltungList = lehrveranstaltungDataProvider.getList();
		int i = 0;
		for (Lehrveranstaltung a : lehrveranstaltungList) {
			if (a.getId() == lehrveranstaltung.getId()) {
				lehrveranstaltungList.set(i, lehrveranstaltung);
				lehrveranstaltungDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void addLehrveranstaltung(Lehrveranstaltung lv) {
		if (lehrveranstaltungDataProvider != null) {
			lehrveranstaltungDataProvider.getList().add(
					lehrveranstaltungDataProvider.getList().size(), lv);
			lehrveranstaltungDataProvider.refresh();
		}
	}

	public void loeschenStudiengang(Studiengang studiengang) {

		int i = 0;

		for (Studiengang sg : studiengangDataProvider.getList()) {
			if (sg.getId() == studiengang.getId()) {

				studiengangDataProvider.getList().remove(i);
				studiengangDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void updateStudiengang(Studiengang studiengang) {
		List<Studiengang> studiengangList = studiengangDataProvider.getList();
		int i = 0;
		for (Studiengang sg : studiengangDataProvider.getList()) {
			if (sg.getId() == studiengang.getId()) {
				studiengangList.set(i, studiengang);
				studiengangDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void addStudiengang(Studiengang sg) {
		if (studiengangDataProvider != null) {
			studiengangDataProvider.getList().add(
					studiengangDataProvider.getList().size(), sg);
			studiengangDataProvider.refresh();
		}
	}

	public void loeschenSemesterverband(Semesterverband semesterverband) {

		int i = 0;

		for (Semesterverband sv : semesterVerbandDataProvider.getList()) {
			if (sv.getId() == semesterverband.getId()) {

				semesterVerbandDataProvider.getList().remove(i);
				semesterVerbandDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void updateSemesterverband(Semesterverband semesterverband) {
		List<Semesterverband> semesterverbandList = semesterVerbandDataProvider.getList();
		int i = 0;
		for (Semesterverband sv : semesterVerbandDataProvider.getList()) {
			if (sv.getId() == semesterverband.getId()) {
				semesterverbandList.set(i, semesterverband);
				semesterVerbandDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void addSemesterverband(Semesterverband sv) {
		if (semesterVerbandDataProvider != null) {
			semesterVerbandDataProvider.getList().add(
					semesterVerbandDataProvider.getList().size(), sv);
			semesterVerbandDataProvider.refresh();
		}
	}

	public void loeschenRaum(Raum raum) {

		int i = 0;

		for (Raum r : raumDataProvider.getList()) {
			if (r.getId() == raum.getId()) {

				raumDataProvider.getList().remove(i);
				raumDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void updateRaum(Raum raum) {
		List<Raum> raumList = raumDataProvider.getList();
		int i = 0;
		for (Raum r : raumDataProvider.getList()) {
			if (r.getId() == raum.getId()) {
				raumList.set(i, raum);
				raumDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	public void addRaum(Raum raum) {
		if (raumDataProvider != null) {
			raumDataProvider.getList().add(raumDataProvider.getList().size(),
					raum);
			raumDataProvider.refresh();
		}
	}

	public boolean isLeaf(Object value) {

		if (value instanceof String && (String) value == "Dozent") {
			return true;
		}

		else if (value instanceof String && (String) value == "Lehrveranstaltung") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Belegung") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Belegungen") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Raum") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Semesterverband") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Studiengang") {
			return true;
		}
		else if (value instanceof String && (String) value == "Studentenplan") {
			return true;
		}
		else if (value instanceof String && (String) value == "Dozentenplan") {
			return true;
		} else if (value instanceof String && (String) value == "Raumplan") {
			return true;
		}
		else if (value instanceof Dozent) {
			return true;
		}
		else if (value instanceof Lehrveranstaltung) {
			return true;
		}
		else if (value instanceof Semesterverband) {
			return true;
		}
		else if (value instanceof Raum) {
			return true;
		}
		else if (value instanceof Studiengang && !unterMenuLVzuSG && !unterMenuSVzuSG) {
			return true;
		} 
		else {
			return false;
		}

	}

	public void setBelegungForm(BelegungForm bF) {
		this.bF = bF;
	}

	public void setDozentForm(DozentForm dF) {
		this.dF = dF;
	}

	public void setLehrveranstaltungForm(LehrveranstaltungForm lF) {
		this.lF = lF;
	}

	public void setStudiengangForm(StudiengangForm sgF) {
		this.sgF = sgF;
	}

	public void setSemesterverbandForm(SemesterverbandForm svF) {
		this.svF = svF;
	}

	public void setRaumForm(RaumForm rF) {
		this.rF = rF;
	}

	public void setRootNode(TreeNode tn) {
		this.rootNode = tn;
	}

	public void setCellTree(CellTree ct) {
		this.cellTree = ct;
	}

	public void addOpenHandler() {
		cellTree.addOpenHandler(new OpenHandler<TreeNode>() {
			public void onOpen(OpenEvent<TreeNode> event) {

				if (rootNode.isChildOpen(0)) {
					if (rootNode.setChildOpen(0, true).isChildOpen(1)) {
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(0)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, true).equals(event.getTarget())) {
							unterMenuLVzuSG = true;
							unterMenuSVzuSG = false;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, false);
						}
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(4)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, true).equals(event.getTarget())) {
							unterMenuLVzuSG = false;
							unterMenuSVzuSG = true;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, false);
						}
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(5)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, true).equals(event.getTarget())) {
							unterMenuLVzuSG = false;
							unterMenuSVzuSG = false;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, false);
						}
					}
				}

			}
		});
	}

	public void setStundenplantool2(Stundenplantool2 spt2) {
		this.spt2 = spt2;
	}

	public Stundenplantool2 getStundenplantool2() {
		return this.spt2;
	}

}
