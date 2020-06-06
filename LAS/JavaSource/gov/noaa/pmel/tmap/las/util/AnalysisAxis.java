package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.client.serializable.AnalysisAxisSerializable;

import org.jdom.Element;

public class AnalysisAxis extends Container implements AnalysisAxisInterface {

	public AnalysisAxis(Element element) {
		super(element);
	}

	@Override
	public String getHi() {
		return element.getAttributeValue("hi");
	}

	@Override
	public String getLo() {
		return element.getAttributeValue("lo");
	}

	@Override
	public String getOp() {
		return element.getAttributeValue("op");
	}

	@Override
	public String getType() {
		return element.getAttributeValue("type");
	}

	@Override
	public void setHi(String hi) {
		element.setAttribute("hi", hi);
	}

	@Override
	public void setLo(String lo) {
		element.getChild("axis").setAttribute("lo", lo);
	}

	@Override
	public void setOp(String op) {
		element.getChild("axis").setAttribute("op", op);
	}

	@Override
	public void setType(String type) {
		element.getChild("axis").setAttribute("type", type);
	}
	
	public AnalysisAxisSerializable getAnalysisAxisSerializable() {
		AnalysisAxisSerializable wireAxis = new AnalysisAxisSerializable();
		wireAxis.setHi(getHi());
		wireAxis.setLo(getLo());
		wireAxis.setOp(getOp());
		wireAxis.setType(getType());
		return wireAxis;
	}
}
