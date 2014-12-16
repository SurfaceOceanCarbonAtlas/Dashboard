package uk.ac.uea.socat.metadata.OmeMetadata;

import org.jdom2.Element;

class Path {
	String itsElementName;
	Path itsParent = null;
	
	protected Path(String elementName) {
		itsElementName = elementName;
	}
	
	protected Path(Path parent, String elementName) {
		itsElementName = elementName;
		itsParent = parent;
	}
	
	protected String getElementName() {
		return itsElementName;
	}
	
	protected Path getParent() {
		return itsParent;
	}
	
	protected Element buildElementTree(String rootElementName, Element childElement) {
		
		Element result = null;
		
		if (null == itsParent) {
			result = new Element(rootElementName);
			result.addContent(childElement);
		} else {
			Element parentElement = new Element(itsParent.getElementName());
			parentElement.addContent(childElement);
			
			result = itsParent.buildElementTree(rootElementName, parentElement);
		}
		
		return result;
	}
	
	public Object clone() {
		Path clone = new Path(itsElementName);
		if (null != itsParent) {
			clone.itsParent = (Path) itsParent.clone();
		}
		
		return clone;
	}
}
