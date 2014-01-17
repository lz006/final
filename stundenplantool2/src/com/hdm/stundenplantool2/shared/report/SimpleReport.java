package com.hdm.stundenplantool2.shared.report;

import java.util.Vector;

public class SimpleReport extends Report{

	private static final long serialVersionUID = 1L;
	
	Vector<Vector<String>> plan = new Vector<Vector<String>>();
	
	public SimpleReport() {
		
	}
	
	public void init() {
		for (int i = 0; i < 8; i++) {
			Vector<String> tempColumn = new Vector<String>();
			for (int j = 0; j < 8; j++) {
				tempColumn.add("");
			}
			plan.add(tempColumn);
		}
	}
	
	public Vector<Vector<String>> getPlan() {
		return plan;
	}
}
