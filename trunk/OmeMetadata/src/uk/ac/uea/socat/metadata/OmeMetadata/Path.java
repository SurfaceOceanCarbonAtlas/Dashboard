package uk.ac.uea.socat.metadata.OmeMetadata;

import java.util.ArrayList;
import java.util.List;

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
	
	protected Path(Path parent, String elementName, String subElement) {
		Path parentPath = new Path(parent, elementName);
		itsElementName = subElement;
		itsParent = parentPath;
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
	
	protected List<String> getPathTree() {
		List<String> pathTree = new ArrayList<String>();
		pathTree.add(itsElementName);
		
		if (hasParent()) {
			itsParent.addParentToPathTree(pathTree);
		}
		
		return pathTree;
	}
	
	protected void addParentToPathTree(List<String> pathTree) {
		pathTree.add(0, itsElementName);
		
		if (hasParent()) {
			itsParent.addParentToPathTree(pathTree);
		}
	}
	
	private boolean hasParent() {
		return !(null == itsParent);
	}
	
	public Object clone() {
		Path clone = new Path(itsElementName);
		if (null != itsParent) {
			clone.itsParent = (Path) itsParent.clone();
		}
		
		return clone;
	}
}
