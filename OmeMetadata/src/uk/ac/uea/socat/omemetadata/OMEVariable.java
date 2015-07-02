package uk.ac.uea.socat.omemetadata;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

/**
 * Represents a single variable of OME Metadata,
 * and its path in the corresponding XML document.
 * 
 * A variable can contain multiple values to handle conflicts
 * during merging with other OME Metadata objects.
 * 
 * 
 * @author uuk07qzu
 *
 */
class OMEVariable {

	private OMEPath itsPath;
	private List<String> itsValues;
	
	/**
	 * Create an OMEVariable object based on the contents of an XML element.
	 * If the element is empty or null, the variable is created with no value.
	 * 
	 * @param parentPath The path to the parent of this variable
	 * @param parentElement The XML element containing the variable value
	 * @param fullDocument The complete XML document - used to extract conflict information
	 */
	protected OMEVariable(OMEPath path, Element parentElement, Element conflictsElement) throws InvalidConflictException {
		itsPath = path;
		itsValues = new ArrayList<String>();
		if (null != parentElement) {
			String value = parentElement.getChildTextTrim(path.getElementName());
			if (null != value) {
				if (value.equals(OmeMetadata.CONFLICT_STRING)) {
					
					Element variableConflictElement = getVariableConflictElement(path, conflictsElement);
					for (Element valueElement : variableConflictElement.getChildren(OmeMetadata.CONFLICT_VALUE_ELEMENT_NAME)) {
						addValue(valueElement.getText());
					}
					
				} else {
					addValue(value);
				}
			}
		}
	}
	
	/**
	 * Create an OMEVariable object with the specified path and value
	 * @param path The variable path
	 * @param value The value
	 */
	protected OMEVariable(OMEPath path, String value) {
		itsPath = path;
		itsValues = new ArrayList<String>();
		addValue(value);
	}
	
	private OMEVariable(OMEPath path) {
		itsPath = path;
		itsValues = new ArrayList<String>();
	}
	
	protected void addValue(String value) {
		String trimmedValue = value.trim();
		
		if (trimmedValue.length() > 0) {
			if (!itsValues.contains(trimmedValue)) {
				itsValues.add(trimmedValue);
			}
		}
	}
	
	protected void addValues(List<String> values) {
		for (String value : values) {
			addValue(value);
		}
	}
	
	/**
	 * Gets the value of this variable for placing in an XML document.
	 * If the variable has no values, an empty string is returned.
	 * If the variable has exactly one value, that value is returned.
	 * If the variable has more than one value, this represents a conflict
	 * and {@link OmeMetadata#CONFLICT_STRING} is returned.
	 * 
	 * @return The value of the variable
	 */
	protected String getValue() {
		
		String result;
		
		switch (itsValues.size()) {
		case 0:
		{
			result = "";
			break;
		}
		case 1:
		{
			result = itsValues.get(0);
			break;
		}
		default:
		{
			result = OmeMetadata.CONFLICT_STRING;
		}
		}
		
		return result;
	}
	
	protected List<String> getAllValues() {
		return itsValues;
	}
	
	protected boolean hasConflict() {
		return (itsValues.size() > 1);
	}
	
	protected OMEPath getPath() {
		return itsPath;
	}
	
	/**
	 * Creates an XML element representing this variable.
	 * The element is just for this variable, and does not
	 * create any of the parent XML structure.
	 * 
	 * The contents of the element will be determined as per
	 * the {@link #getValue()} method.
	 * 
	 * @return
	 */
	private Element getElement() {
		Element elem = new Element(itsPath.getElementName());
		elem.setText(getValue());
		
		return elem;
	}
	
	private Element generateConflictElement() {
		Element conflictElement = null;
		
		if (hasConflict()) {
			Element pathElement = new Element(itsPath.getElementName());
			for (String value : itsValues) {
				Element valueElement = new Element("VALUE");
				valueElement.setText(value);
				pathElement.addContent(valueElement);
			}
			
			conflictElement = itsPath.buildElementTree("Conflict", pathElement);
		}
		
		return conflictElement;
	}
	
	protected void generateXMLContent(Element parent, ConflictElement conflictParent) {
		parent.addContent(getElement());
		if (hasConflict()) {
			conflictParent.addContent(generateConflictElement());
		}
	}
	
	private Element getVariableConflictElement(OMEPath path, Element conflictsElement) throws InvalidConflictException {
		
		List<String> variablePath = path.getPathTree();
		List<Element> testElements = conflictsElement.getChildren(OmeMetadata.CONFLICT_ELEMENT_NAME);
		
		boolean foundConflict = false;
		Element conflictElement = null;
		
		for (int i = 0; !foundConflict && i < testElements.size(); i++) {
			Element testElement = testElements.get(i);
			
			for (int j = 0; j < variablePath.size(); j++) {
				Element childElement = testElement.getChild(variablePath.get(j));
				if (null == childElement) {
					// This isn't the conflict element we're looking for. Move to the next one
					foundConflict = false;
					break;
				} else {
					
					// If this is the last entry in the path, record the element					
					if ((j + 1) == variablePath.size()) {
						foundConflict = true;
						conflictElement = childElement;
					} else {
						testElement = childElement;
					}
				}
			}
		}
		
		if (!foundConflict) {
			throw new InvalidConflictException("A conflict was reported for '" + path.getElementName() + "', but no details were found in the CONFLICTS section");
		}
		
		return conflictElement;
	}

	
	public Object clone() {
		OMEVariable clone = new OMEVariable((OMEPath) itsPath.clone());
		for (String value : itsValues) {
			clone.addValue(value);
		}
		
		return clone;
	}
}
