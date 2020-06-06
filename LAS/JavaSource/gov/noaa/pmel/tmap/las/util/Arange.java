package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.client.serializable.ArangeSerializable;

import org.jdom.Element;

public class Arange extends Container implements ArangeInterface {
    public Arange(Element element) {
        super(element);
    }

	public String getStart() {
		return element.getAttributeValue("start");
	}
	public String getStep() {
		return element.getAttributeValue("step");
	}
    public String getSize() {
    	return element.getAttributeValue("size");
    }
    public ArangeSerializable getArangeSerializable() {
    	ArangeSerializable a = new ArangeSerializable();
    	a.setSize(getSize());
    	a.setStart(getStart());
    	a.setStep(getStep());
    	return a;
    }
}
