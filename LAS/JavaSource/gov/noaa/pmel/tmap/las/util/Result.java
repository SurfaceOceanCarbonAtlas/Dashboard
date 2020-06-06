package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class Result extends Container implements ResultInterface {

	public Result (Element element) {
		super(element);
	}
	
	@Override
	public void setName(String name) {		
	}
	
	@Override
	public String getName() {
		return getID()+"_"+getType();
	}
	@Override
	public String getFile() {
		return getAttributeValue("file");
	}

	@Override
	public String getType() {
		return getAttributeValue("type");
	}

	@Override
	public String getURL() {
		return getAttributeValue("url");
	}

}
