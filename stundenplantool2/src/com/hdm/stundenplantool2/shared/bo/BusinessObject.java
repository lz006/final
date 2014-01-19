package com.hdm.stundenplantool2.shared.bo;

import java.io.Serializable;

/**
 * <p>
 * Die Klasse <code>BusinessObject</code> stellt die Basisklasse aller in diesem
 * Projekt für die Umsetzung des Fachkonzepts relevanten Klassen dar.
 * </p>
 * <p>
 * Zentrales Merkmal ist, dass jedes <code>BusinessObject</code> eine Nummer
 * besitzt, die man in einer relationalen Datenbank auch als Primärschlüssel
 * bezeichnen würde. Fernen ist jedes <code>BusinessObject</code> als
 * {@link Serializable} gekennzeichnet. Durch diese Eigenschaft kann jedes
 * <code>BusinessObject</code> automatisch in eine textuelle Form überführt und
 * z.B. zwischen Client und Server transportiert werden. Bei GWT RPC ist diese
 * textuelle Notation in JSON (siehe http://www.json.org/) kodiert.
 * </p>
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */

public abstract class BusinessObject implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	/**
	   * Die eindeutige Identifikationsnummer einer Instanz dieser Klasse.
	   */
	private int id;	
	
	/**
	   * Auslesen der ID.
	   */
	public int getId() {
		return id;
	}

	/**
	   * Setzen der ID
	   */
	public void setId(int id) {
		this.id = id;
	}
 
}
