package com.hdm.stundenplantool2.shared.report;

public class HTMLReportWriter {
	
	StringBuffer result = new StringBuffer();
	
	public String getHTMLString(SimpleReport plan) {
		result.append("<table>");
		for (int i = 0; i < 9; i++) {
			result.append("<tr>");
			for (int j = 0; j < 9; j++) {
				result.append("<td>");
				result.append(plan.getPlan().elementAt(j).elementAt(i));
				result.append("</td>");
			}
			result.append("</tr>");
		}
		result.append("</table>");
		return result.toString();
	}

}
