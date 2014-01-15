package com.hdm.stundenplantool2.shared.report;

import java.util.Vector;


public class CompositeParagraph extends Paragraph {

	Vector<SimpleParagraph> subParagraphs = new Vector<SimpleParagraph>();
	
	private static final long serialVersionUID = 1L;
	
	public void addSubParagraph(SimpleParagraph p) {
	    this.subParagraphs.addElement(p);
	  }
	
	public void removeSubParagraph(SimpleParagraph p) {
	    this.subParagraphs.removeElement(p);
	  }
	
	public Vector<SimpleParagraph> getSubParagraphs() {
	    return this.subParagraphs;
	  }
	
	public int getNumParagraphs() {
	    return this.subParagraphs.size();
	  }
	
	public SimpleParagraph getParagraphAt(int i) {
	    return this.subParagraphs.elementAt(i);
	  }
	
	public String toString() {
	    /*
	     * Wir legen einen leeren Buffer an, in den wir sukzessive sämtliche
	     * String-Repräsentationen der Unterabschnitte eintragen.
	     */
	    StringBuffer result = new StringBuffer();

	    // Schleife über alle Unterabschnitte
	    for (int i = 0; i < this.subParagraphs.size(); i++) {
	      SimpleParagraph p = this.subParagraphs.elementAt(i);

	      /*
	       * den jew. Unterabschnitt in einen String wandeln und an den Buffer hängen.
	       */
	      result.append(p.toString() + "\n");
	    }

	    /*
	     * Schließlich wird der Buffer in einen String umgewandelt und
	     * zurückgegeben.
	     */
	    return result.toString();
	  }
	
}
