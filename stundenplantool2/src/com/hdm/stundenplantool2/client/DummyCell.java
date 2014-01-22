package com.hdm.stundenplantool2.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.hdm.stundenplantool2.shared.bo.Dozent;

/**
 * Diese Klasse ist eine konkrete Ausprägung der abstrakten Klasse
 * AbstractCell<T>. Sie definiert den sichtbaren Inhalt einer Zelle
 * des CellTrees für einen spezifischen Kind-Element-Typ, in diesem 
 * Fall einem Kind-Element vom Typ String. Eine Instanz einer solchen 
 * Klasse wird vom Konstruktor der DefaultNodeInfo<T>-Klasse verlangt 
 * und wird diesem konkret in der Methode "getNodeInfo(T value)" in 
 * der Klasse "CustomTreeViewModel" {@link CustomTreeViewModel} als 
 * Argument übergeben.
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class DummyCell extends AbstractCell<String> {

	public void render(Context context, String value, SafeHtmlBuilder sb) {

		if (value == null) {
			return;
		}

		sb.appendHtmlConstant("<div>");
		sb.appendEscaped(value);
		sb.appendHtmlConstant("</div>");

	}

}