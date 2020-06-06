package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class Tributary extends Container implements TributaryInterface {

	public Tributary(Element element) {
		super(element);
	}
	
	@Override
	public String getURL() {
		return getElement().getAttributeValue("url");
	}

	@Override
	public void setURL(String url) {
		getElement().setAttribute("url", url);
	}

	public String getTopLevelCategoryID() {
		return getID()+Constants.NAME_SPACE_SPARATOR+"Top_of_"+getID();
	}

}
