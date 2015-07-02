package uk.ac.uea.socat.omemetadata;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

class OMEPath {
	String itsElementName;
	OMEPath itsParent = null;
	
	protected OMEPath(String elementName) {
		itsElementName = elementName;
	}
	
	protected OMEPath(OMEPath parent, String elementName) {
		itsElementName = elementName;
		itsParent = parent;
	}
	
	protected OMEPath(OMEPath parent, String elementName, String subElement) {
		OMEPath parentPath = new OMEPath(parent, elementName);
		itsElementName = subElement;
		itsParent = parentPath;
	}
	
	protected String getElementName() {
		return itsElementName;
	}
	
	protected OMEPath getParent() {
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
		OMEPath clone = new OMEPath(itsElementName);
		if (null != itsParent) {
			clone.itsParent = (OMEPath) itsParent.clone();
		}
		
		return clone;
	}
}
