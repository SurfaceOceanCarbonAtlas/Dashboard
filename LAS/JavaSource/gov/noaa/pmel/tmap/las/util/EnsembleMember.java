package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class EnsembleMember extends Container implements EnsembleMemberInterface {

	public EnsembleMember(Element element) {
		super(element);
	}

	@Override
	public String getURL() {
		return getElement().getAttributeValue("url");
	}

	@Override
	public String getID() {
		return getElement().getAttributeValue("IDREF");
	}
	
	public void addVariable(String name, String id) {
		Element var = new Element("variable");
		var.setAttribute("IDREF", id);
		var.setAttribute("short_name", name);
		getElement().addContent(var);
	}
}
