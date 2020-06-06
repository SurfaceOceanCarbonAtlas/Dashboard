package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.util.List;

import org.jdom.Element;

public class LASIconWebRowSet extends LASDocument {
	public LASIconWebRowSet() {
		super();
		Element webrowset = new Element("webrowset");
		setRootElement(webrowset);
		Element data = new Element("data");
		webrowset.addContent(data);
	}
	public void addId(String id) {
		List<Element> rows = getRootElement().getChild("data").getChildren("currentRow");
		Element idColumnValue = new Element("columnValue");
		idColumnValue.setText(id);
		Element iconColumnValue = new Element("columnValue");
		iconColumnValue.setText(String.valueOf(rows.size()+1));
		Element currentRow = new Element("currentRow");
		currentRow.addContent(idColumnValue);
		currentRow.addContent(iconColumnValue);
		getRootElement().getChild("data").addContent(currentRow);
	}
}
