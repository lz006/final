package com.hdm.stundenplantool2.shared.report;

public class HTMLReportWriter {
	
	StringBuffer result = new StringBuffer();
	
	public String getHTMLString(SimpleReport plan) {
		result.append("<table style=\"background-color: black; width:1600px;height: 80px;rules: all; text-align: center; font-family: bold\"> ");
		for (int i = 0; i < 8; i++) {
			if (i == 4) {
				for (int j = 0; j < 8; j++) {
					if(j == 0) {
						result.append("<td style=\"background-color: white; width:450px;height; 80px;rules: all; text-align: center; font-family: bold\"> ");
						result.append("13:15 - 14:15");
						result.append("</td>");
					}
					else {
						result.append("<td style=\"background-color: white; width:450px;height; 80px;rules: all; text-align: center; font-family: bold\"> ");
						result.append("Pause");
						result.append("</td>");
					}
				}
			}
			
			result.append("<tr style=\"background-color: white; width:450px;height; 80px;rules: all; text-align: center; font-family: bold\"> ");
			for (int j = 0; j < 8; j++) {
				result.append("<td style=\"background-color: white; width:450px;height; 80px;rules: all; text-align: center; font-family: bold\"> ");
				result.append(plan.getPlan().elementAt(j).elementAt(i));
				result.append("</td>");
			}
			result.append("</tr>");
		}
		result.append("</table>");
		return result.toString();
	}

}
