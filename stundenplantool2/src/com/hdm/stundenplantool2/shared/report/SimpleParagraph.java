package com.hdm.stundenplantool2.shared.report;

public class SimpleParagraph extends Paragraph {
	
	private static final long serialVersionUID = 1L;
	
	private String text = "";
	
	public SimpleParagraph() {
	  }
	
	public SimpleParagraph(String value) {
	  this.text = value;
	  }

	String getText() {
	  return this.text;
	}

	public void setText(String text) {
		this.text = text;
	  }


	public String toString() {
	   return this.text;
	}

}
