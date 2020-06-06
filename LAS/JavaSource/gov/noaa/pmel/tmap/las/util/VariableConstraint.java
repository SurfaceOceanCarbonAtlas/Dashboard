package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class VariableConstraint extends Container {

	public VariableConstraint(Element element) {
		super(element);
	}

	public String getVariableXPath() {
		return getElement().getChild("link").getAttributeValue("match");
	}
	public String getOp() {
		return getElement().getAttributeValue("op");
	}
	public String getValue() {
		return getElement().getChild("v").getTextNormalize();
	}
	public String getHTMLOp() {
		String op = getOp();
		String htmlop = "";
		if ( op.equals(">") ) {
			htmlop = "&gt;";
		} else if ( op.equals(">=") ) {
			htmlop = "&gt;=";
		} else if ( op.equals("=") ) {
			htmlop = "=";
		} else if ( op.equals("<") ) {
			htmlop = "&lt;";
		} else if ( op.equals("<=") ) {
			htmlop = "&lt=";
		}
		return htmlop;
	}
}
