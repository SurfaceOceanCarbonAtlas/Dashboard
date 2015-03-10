package uk.ac.uea.socat.metadata.OmeMetadata;

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

	private Path itsPath;
	private List<String> itsValues;
	
	/**
	 * Create an OMEVariable object based on the contents of an XML element.
	 * If the element is empty or null, the variable is created with no value.
	 * 
	 * @param parentPath The path to the parent of this variable
	 * @param parentElement The XML element containing the variable value
	 * @param name The name of the variable.
	 */
	protected OMEVariable(Path path, Element parentElement) {
		itsPath = path;
		itsValues = new ArrayList<String>();
		if (null != parentElement) {
			String value = parentElement.getChildTextTrim(path.getElementName());
			if (null != value) {
				itsValues.add(value);
			}
		}
	}
	
	/**
	 * Create an OMEVariable object with the specified path and value
	 * @param path The variable path
	 * @param value The value
	 */
	protected OMEVariable(Path path, String value) {
		itsPath = path;
		itsValues = new ArrayList<String>();
		if (null != value) {
			itsValues.add(value);
		}
	}
	
	private OMEVariable(Path path) {
		itsPath = path;
		itsValues = new ArrayList<String>();
	}
	
	protected void addValue(String value) {
		if (!itsValues.contains(value)) {
			itsValues.add(value);
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
	
	protected Path getPath() {
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
	
	public Object clone() {
		OMEVariable clone = new OMEVariable((Path) itsPath.clone());
		for (String value : itsValues) {
			clone.addValue(value);
		}
		
		return clone;
	}
}
