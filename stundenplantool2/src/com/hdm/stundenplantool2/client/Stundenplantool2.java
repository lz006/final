package com.hdm.stundenplantool2.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.hdm.stundenplantool2.shared.*;

public class Stundenplantool2 implements EntryPoint {

	private final VerwaltungAsync verwaltung = GWT.create(Verwaltung.class);
	private final ReportAsync report = GWT.create(Report.class);
	private CellTree cellTree;
	private DozentTreeViewModel dtvm;
	private DockLayoutPanel p;
	private Image image;
	private ScrollPanel navi;
	private VerticalPanel footPanel;
	private HTML copyright;
	private HTML titel;
	private HorizontalPanel traeger;
	private HorizontalPanel left;
	private HorizontalPanel right;
	private VerticalPanel traegerInfoPanel;
	private VerticalPanel obenInfoPanel;
	private VerticalPanel untenInfoPanel;

	private DozentForm dF;
	private BelegungForm bF;
	private LehrveranstaltungForm lF;
	private StudiengangForm sgF;
	private SemesterverbandForm svF;
	private RaumForm rF;

	private StudentenPlanForm spF;
	private DozentenPlanForm dpF;
	private RaumPlanForm rpF;

	private ScrollPanel mainPanel;
	Button visibilityTreeButton;
	Button visibilityInfoPanelsButton;
	Grid buttonGrid;
	boolean check1 = false;
	boolean check2 = false;

	@Override
	public void onModuleLoad() {

		visibilityTreeButton = new Button("Navigation ausblenden");
		visibilityTreeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				visibilityTree();
			}
		});

		visibilityInfoPanelsButton = new Button("Infotext ausblenden");
		visibilityInfoPanelsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				visibilityInfoPanels();
			}
		});

		buttonGrid = new Grid(1, 2);
		buttonGrid.setWidget(0, 0, visibilityTreeButton);
		buttonGrid.setWidget(0, 1, visibilityInfoPanelsButton);

		// Initialisierung der Info-Panels am rechten Bildschirrand
		traegerInfoPanel = new VerticalPanel();
		obenInfoPanel = new VerticalPanel();
		obenInfoPanel.setStyleName("infotextOben");

		untenInfoPanel = new VerticalPanel();
		untenInfoPanel.setStyleName("infotextUnten");

		traegerInfoPanel.add(obenInfoPanel);
		traegerInfoPanel.add(untenInfoPanel);

		// Initialisierung des Hauptpanels in der Bildschirmmitte und des Panels
		// für den CellTree am linken Bildschirmrand
		mainPanel = new ScrollPanel();
		navi = new ScrollPanel();

		dtvm = new DozentTreeViewModel(verwaltung);
		cellTree = new CellTree(dtvm, "Root");
		navi.add(cellTree);
		dtvm.setRootNode(cellTree.getRootTreeNode());
		dtvm.setCellTree(cellTree);

		image = new Image();
		image.setUrl("http://www.abload.de/img/widmlogo-frei8qjj3.png");
		image.setHeight("10em");
		image.setAltText("WI-Logo");

		copyright = new HTML(
				"IT-Projekt im 4. Semester &copy; by Timm Roth(tr047), Tobias Moser(tm066), Lucas Zanella(lz006), Stefan Sonntag(ss305), Gino Sidney(gk024) und Mathias Zimmermann(mz048)");
		copyright.addStyleName("cpLabel");

		footPanel = new VerticalPanel();
		footPanel.add(buttonGrid);
		footPanel.add(copyright);
		footPanel.addStyleName("foot");

		titel = new HTML(
				"Godfather Stundenplantool powerd by Bitch Nigga Corp.");
		titel.addStyleName("Titel");

		// Logo und Überschrift
		left = new HorizontalPanel();
		left.add(image);

		right = new HorizontalPanel();
		right.add(titel);

		traeger = new HorizontalPanel();
		traeger.add(left);
		traeger.add(right);

		p = new DockLayoutPanel(Unit.EM);
		p.addNorth(traeger, 10);
		p.addSouth(footPanel, 5);
		p.addWest(navi, 25);
		p.addEast(traegerInfoPanel, 20);
		p.add(mainPanel);

		// Das Info-Panel wird zu anfangs ausgeblendet
		p.setWidgetHidden(traegerInfoPanel, true);

		RootLayoutPanel rlp = RootLayoutPanel.get();
		rlp.add(p);
		RootPanel.get().add(rlp);

		dtvm.setStundenplantool2(this);

	}

	public void visibilityTree() {
		if (!check1) {
			p.setWidgetHidden(navi, true);
			visibilityTreeButton.setText("Navigation einblenden");
			check1 = true;
			return;
		}
		if (check1) {
			p.setWidgetHidden(navi, false);
			visibilityTreeButton.setText("Navigation ausblenden");
			check1 = false;
			return;
		}
	}

	public void visibilityInfoPanels() {
		if (!check2) {
			p.setWidgetHidden(traegerInfoPanel, true);
			visibilityInfoPanelsButton.setText("Infotext einblenden");
			check2 = true;
			return;
		}
		if (check2) {
			p.setWidgetHidden(traegerInfoPanel, false);
			visibilityInfoPanelsButton.setText("Infotext ausblenden");
			check2 = false;
			return;
		}
	}

	public void setDozentFormToMain() {
		dF = new DozentForm(verwaltung);
		dtvm.setDozentForm(dF);
		mainPanel.clear();
		mainPanel.add(dF);
	}

	public void setBelegungFormToMain() {
		bF = new BelegungForm(verwaltung);
		dtvm.setBelegungForm(bF);
		mainPanel.clear();
		mainPanel.add(bF);
	}

	public void setLehrveranstaltungFormToMain() {
		lF = new LehrveranstaltungForm(verwaltung);
		dtvm.setLehrveranstaltungForm(lF);
		mainPanel.clear();
		mainPanel.add(lF);
	}

	public void setStudiengangFormToMain() {
		sgF = new StudiengangForm(verwaltung);
		dtvm.setStudiengangForm(sgF);
		mainPanel.clear();
		mainPanel.add(sgF);
	}

	public void setSemesterverbandFormToMain() {
		svF = new SemesterverbandForm(verwaltung);
		dtvm.setSemesterverbandForm(svF);
		mainPanel.clear();
		mainPanel.add(svF);
	}

	public void setRaumFormToMain() {
		rF = new RaumForm(verwaltung);
		dtvm.setRaumForm(rF);
		mainPanel.clear();
		mainPanel.add(rF);
	}

	public void setStudentenPlanFormToMain() {
		spF = new StudentenPlanForm(report);
		mainPanel.clear();
		mainPanel.add(spF);
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setVisible(false);
	}

	public void setDozentenPlanFormToMain() {
		dpF = new DozentenPlanForm(report);
		mainPanel.clear();
		mainPanel.add(dpF);
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setVisible(false);
	}

	public void setRaumPlanFormToMain() {
		rpF = new RaumPlanForm(report);
		mainPanel.clear();
		mainPanel.add(rpF);
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setVisible(false);
	}

	public void popupInfo() {
		Window.alert("Bitte aktivieren Sie Popups in Ihrem Browser wenn Sie den Report im Vollbild betrachten möchten");
	}

	public void setTextToInfoPanelOben(String anleitung) {
		p.setWidgetHidden(traegerInfoPanel, false);
		obenInfoPanel.clear();
		HTML infoTextObenLabel = new HTML(anleitung);
		infoTextObenLabel.setStyleName("infoTextObenLabel");
		obenInfoPanel.add(infoTextObenLabel);
	}

	public void setTextToInfoPanelUnten(String restricts) {
		p.setWidgetHidden(traegerInfoPanel, false);
		untenInfoPanel.clear();
		HTML infoTextUntenLabel = new HTML(restricts);
		infoTextUntenLabel.setStyleName("infoTextUntenLabel");
		untenInfoPanel.add(infoTextUntenLabel);
	}

	public void clearInfoPanels() {
		p.setWidgetHidden(traegerInfoPanel, true);
		obenInfoPanel.clear();
		untenInfoPanel.clear();
		if (check2) {
			check2 = false;
			visibilityInfoPanelsButton.setText("Infotext ausblenden");
		}
	}

	public CellTree getCellTree() {
		return cellTree;
	}

}
