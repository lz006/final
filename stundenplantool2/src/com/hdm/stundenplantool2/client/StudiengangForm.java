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

public class StudiengangForm extends VerticalPanel {

	VerwaltungAsync verwaltung = null;

	DozentTreeViewModel dtvm = null;

	Studiengang shownStudiengang = null;

	Label bezeichnungLabel = new Label("Bezeichnung :");
	TextBox bezeichnungTb = new TextBox();

	Label kuerzelLabel = new Label("Kürzel :");
	TextBox kuerzelTb = new TextBox();

	Label lvLabel = new Label("Lehreranstaltungen:");
	ListBox lvListBox;

	Label gesamtStudentenLabel = new Label("Gesamtzahl Studierender: ");

	Button hinzufuegenButton;
	Button speichernAnlegenButton;
	Button loeschenButton;

	FlexTable lvTable;
	FlexTable svTable;

	Vector<Lehrveranstaltung> lvVector = null;
	Vector<Lehrveranstaltung> lvVonNeuemSGVector = null;
	Vector<Semesterverband> svVector = null;

	Grid obenGrid;
	Grid mitteGrid;
	Grid untenGrid;

	VerticalPanel obenPanel;
	VerticalPanel mittePanel;
	VerticalPanel untenPanel;
	HorizontalPanel abschlussPanel;

	public StudiengangForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		ladenLehrveranstaltungen();

		obenPanel = new VerticalPanel();

		obenGrid = new Grid(2, 2);
		obenGrid.setWidget(0, 0, bezeichnungLabel);
		obenGrid.setWidget(0, 1, bezeichnungTb);
		obenGrid.setWidget(1, 0, kuerzelLabel);
		obenGrid.setWidget(1, 1, kuerzelTb);

		obenPanel.add(obenGrid);
		this.add(obenPanel);

		mittePanel = new VerticalPanel();
		lvListBox = new ListBox();

		mitteGrid = new Grid(1, 3);
		mitteGrid.setWidget(0, 0, lvLabel);
		mitteGrid.setWidget(0, 1, lvListBox);

		hinzufuegenButton = new Button("Hinzufügen");

		hinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (shownStudiengang != null) {
					boolean check = true;
					if (shownStudiengang.getLehrveranstaltungen() != null) {
						for (Lehrveranstaltung lv : shownStudiengang
								.getLehrveranstaltungen()) {
							if (lv.getId() == lvVector.elementAt(
									lvListBox.getSelectedIndex()).getId()) {
								Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					if (check) {
						if (shownStudiengang.getLehrveranstaltungen() == null) {
							shownStudiengang
									.setLehrveranstaltungen(new Vector<Lehrveranstaltung>());
						}
						shownStudiengang.getLehrveranstaltungen()
								.addElement(
										lvVector.elementAt(lvListBox
												.getSelectedIndex()));
						lvAnzeigen();
					}
				} else {
					if (lvVonNeuemSGVector == null) {
						lvVonNeuemSGVector = new Vector<Lehrveranstaltung>();
					}
					boolean check = true;
					for (Lehrveranstaltung lv : lvVonNeuemSGVector) {
						if (lv.getId() == lvVector.elementAt(
								lvListBox.getSelectedIndex()).getId()) {
							Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						lvVonNeuemSGVector.addElement(lvVector
								.elementAt(lvListBox.getSelectedIndex()));
						lvAnzeigen();
					}
				}

			}
		});

		mitteGrid.setWidget(0, 2, hinzufuegenButton);

		lvTable = new FlexTable();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "Studiensemster");

		mittePanel.add(mitteGrid);
		mittePanel.add(lvTable);
		this.add(mittePanel);

		untenGrid = new Grid(1, 2);
		untenGrid.setWidget(0, 0, gesamtStudentenLabel);

		svTable = new FlexTable();

		untenPanel = new VerticalPanel();
		untenPanel.add(untenGrid);
		untenPanel.add(svTable);
		this.add(untenPanel);

		abschlussPanel = new HorizontalPanel();

		speichernAnlegenButton = new Button();

		abschlussPanel.add(speichernAnlegenButton);
		this.add(abschlussPanel);

	}

	public void setDtvm(DozentTreeViewModel dtvm) {
		this.dtvm = dtvm;
		setInfoText();
	}

	void setInfoText() {
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben(
				"Anleitung: </br>" + "Hier können Sie viele bunte Dinge tun.");

		bezeichnungTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2()
						.setTextToInfoPanelUnten(
								"Für die Bearbeitung der Studiengangsbezeichnung bitte folgende Restriktionen beachten:"
										+ "</br>Viele bunte Smarties"
										+ "</br>Viele bunte Smarties");
			}
		});

		kuerzelTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2()
						.setTextToInfoPanelUnten(
								"Für die Bearbeitung der Kapazität eines Studiengangs bitte folgende Restriktionen beachten:"
										+ "</br>Viele bunte Smarties"
										+ "</br>Viele bunte Smarties");
			}
		});

	}

	public void setShownStudiengang(Studiengang sg) {
		this.shownStudiengang = sg;
	}

	public void ladenLehrveranstaltungen() {
		verwaltung
				.auslesenAlleLehrveranstaltungen(new AsyncCallback<Vector<Lehrveranstaltung>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					public void onSuccess(Vector<Lehrveranstaltung> result) {
						if (lvVector == null) {
							lvVector = new Vector<Lehrveranstaltung>();
						}

						for (Lehrveranstaltung lv : result) {
							lvVector.add(lv);
							lvListBox.addItem(lv.getBezeichnung());
						}
					}
				});
	}

	public void fillForm() {
		this.bezeichnungTb.setText(shownStudiengang.getBezeichnung());
		this.kuerzelTb.setText(shownStudiengang.getKuerzel());
	}

	public void lvAnzeigen() {
		lvTable.removeAllRows();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "Studiensemster");

		if (shownStudiengang != null) {
			if ((shownStudiengang.getLehrveranstaltungen() != null)
					&& (shownStudiengang.getLehrveranstaltungen().size() > 0)) {
				for (Lehrveranstaltung lv : shownStudiengang
						.getLehrveranstaltungen()) {
					final int row = lvTable.getRowCount();
					lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					lvTable.setWidget(
							row,
							1,
							new Label(new Integer(lv.getStudiensemester())
									.toString()));

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event)
									.getRowIndex();
							lvTable.removeRow(rowIndex);

							shownStudiengang.getLehrveranstaltungen()
									.removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 2, loeschenButton);

				}
			}
		} else {
			if ((lvVonNeuemSGVector != null) && (lvVonNeuemSGVector.size() > 0)) {
				for (Lehrveranstaltung lv : lvVonNeuemSGVector) {
					final int row = lvTable.getRowCount();
					lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					lvTable.setWidget(
							row,
							1,
							new Label(new Integer(lv.getStudiensemester())
									.toString()));

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event)
									.getRowIndex();
							lvTable.removeRow(rowIndex);

							lvVonNeuemSGVector.removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 1, loeschenButton);

				}
			}
		}
	}

	public void semesterverbaendeAnzeigen() {
		svTable.removeAllRows();
		svTable.setText(0, 0, "Semesterverband");
		svTable.setText(0, 1, "Studierende");

		if (shownStudiengang != null) {

			if ((shownStudiengang.getSemesterverbaende() != null)
					&& (shownStudiengang.getSemesterverbaende().size() > 0)) {

				int tempGesamtStudenten = 0;

				for (Semesterverband sv : shownStudiengang
						.getSemesterverbaende()) {

					tempGesamtStudenten = tempGesamtStudenten
							+ sv.getAnzahlStudenten();

					final int row = svTable.getRowCount();
					svTable.setWidget(row, 0, new Label(sv.getJahrgang()));
					svTable.setWidget(
							row,
							1,
							new Label(new Integer(sv.getAnzahlStudenten())
									.toString()));

					untenGrid.setText(0, 1,
							new Integer(tempGesamtStudenten).toString());

				}
			} else {
				untenGrid.setText(0, 1, "0");
			}
		}
	}

	public void aendernMaske() {

		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownStudiengang.setBezeichnung(bezeichnungTb.getText());
				shownStudiengang.setKuerzel(kuerzelTb.getText());

				verwaltung.aendernStudiengang(shownStudiengang,
						new AsyncCallback<Studiengang>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());

								verwaltung
										.auslesenStudiengang(
												shownStudiengang,
												new AsyncCallback<Vector<Studiengang>>() {
													public void onFailure(
															Throwable caught) {
														DOM.setStyleAttribute(
																RootPanel
																		.getBodyElement(),
																"cursor",
																"default");
														Window.alert(caught
																.getMessage());
														speichernAnlegenButton
																.setEnabled(true);
														loeschenButton
																.setEnabled(true);
													}

													public void onSuccess(
															Vector<Studiengang> result) {
														lvTable.clear();
														svTable.clear();
														dtvm.setSelectedStudiengang(result
																.elementAt(0));
														speichernAnlegenButton
																.setEnabled(true);
														loeschenButton
																.setEnabled(true);
													}
												});
							}

							public void onSuccess(Studiengang result) {
								Window.alert("Der Studiengang wurde erfolgreich geändert");
								dtvm.updateStudiengang(result);
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

				verwaltung.loeschenStudiengang(shownStudiengang,
						new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);

							}

							public void onSuccess(Void result) {
								Window.alert("Studiengang wurde erfolgreich gelöscht");
								dtvm.loeschenStudiengang(shownStudiengang);
								clearForm();
							}
						});
			}
		});
	}

	public void anlegenMaske() {

		untenGrid.setText(0, 1, "0");

		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				if (lvVonNeuemSGVector != null && lvVonNeuemSGVector.size() > 0) {
					verwaltung.anlegenStudiengang(bezeichnungTb.getText(),
							kuerzelTb.getText(), lvVonNeuemSGVector,
							new AsyncCallback<Studiengang>() {
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									speichernAnlegenButton.setEnabled(true);

								}

								public void onSuccess(Studiengang result) {
									Window.alert("Studiengang wurde erfolgreich angelegt");
									dtvm.addStudiengang(shownStudiengang);
									speichernAnlegenButton.setEnabled(true);
									clearForm();
								}
							});
				} else {
					verwaltung.anlegenStudiengang(bezeichnungTb.getText(),
							kuerzelTb.getText(),
							new AsyncCallback<Studiengang>() {
								public void onFailure(Throwable caught) {
									speichernAnlegenButton.setEnabled(true);
									Window.alert(caught.getMessage());

								}

								public void onSuccess(Studiengang result) {
									Window.alert("Studiengang wurde erfolgreich angelegt");
									dtvm.addStudiengang(result);
									speichernAnlegenButton.setEnabled(true);
									clearForm();
								}
							});
				}
			}
		});
	}

	public void clearForm() {
		this.shownStudiengang = null;
		this.bezeichnungTb.setText("");
		this.kuerzelTb.setText("");
		this.lvListBox.setSelectedIndex(0);
		this.lvTable.removeAllRows();
		this.lvVonNeuemSGVector = null;
	}

}
