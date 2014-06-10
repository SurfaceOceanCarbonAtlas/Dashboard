package gov.noaa.pmel.socat.dashboard.ome;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

class OMECompositeVariable {
	private Path itsPath;
	private List<String> itsIdFields;
	private List<Value> itsValues = new ArrayList<Value>();
	
	protected OMECompositeVariable(Path parentPath, String idElement) {
		itsPath = parentPath;
		itsIdFields = new ArrayList<String>();
		itsIdFields.add(idElement);
	}
	
	protected OMECompositeVariable(Path parentPath, List<String> idElements) {
		itsPath = parentPath;
		itsIdFields = idElements;
	}

	protected void addValue(String name, Element element) throws IllegalArgumentException {
		String valueText = null;
		if (null != element) {
			valueText = element.getChildTextTrim(name);
			addValue(name, valueText);
		}
	}
	
	protected void addValue(String name, String valueText) throws IllegalArgumentException {
		
		boolean foundValue = false;
		for (Value searchValue : itsValues) {
			if (searchValue.name.equals(name)) {
				
				if (itsIdFields.contains(name) && searchValue.getValueCount() > 0) {
					throw new IllegalArgumentException("Cannot add multiple values to an identifier in a composite variable");
				} else {
					searchValue.addValue(valueText);
					foundValue = true;
					break;
				}
			}
		}
		
		if (!foundValue) {
			itsValues.add(new Value(name, valueText));
		}
	}
	
	
		
	private Element getElement() {
		Element element = new Element(itsPath.getElementName());
		for (Value subValue : itsValues) {
			Element subElement = new Element(subValue.name);
			subElement.setText(subValue.getValue());
			
			element.addContent(subElement);
		}
		
		return element;
	}
	
	protected void generateXMLContent(Element parent, ConflictElement conflictParent) {
		parent.addContent(getElement());
		if (hasConflict()) {
			conflictParent.addContent(generateConflictElement());
		}
	}

	private Element generateConflictElement() {
		Element conflictElement = null;
		
		if (hasConflict()) {
			
			Element variableElement = new Element(itsPath.getElementName());
			for (String id : itsIdFields) {
				variableElement.setAttribute(id, getValue(id));
			}
			
			for (Value value : itsValues) {
				if (value.getValueCount() > 1) {
					Element valuesElement = new Element(value.name);
					
					for (String valueString : value.getAllValues()) {
						Element valueElement = new Element("VALUE");
						valueElement.setText(valueString);
						valuesElement.addContent(valueElement);
					}
					
					variableElement.addContent(valuesElement);
				}
			}
			
			conflictElement = itsPath.buildElementTree("Conflict", variableElement);
		}
		
		return conflictElement;
	}
	
	protected boolean hasConflict() {
		boolean result = false;
		
		for (Value value : itsValues) {
			if (value.getValueCount() > 1) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	protected String getValue(String valueName) {
		String result = "";
		
		for (Value value : itsValues) {
			if (value.name.equals(valueName)) {
				result = value.getValue();
				break;
			}
		}
		
		return result;
	}
	
	private class Value {
		private String name;
		private List<String> values;
		
		private Value(String name, String value) {
			this.name = name;
			values = new ArrayList<String>();
			values.add(value);
		}
		
		private void addValue(String value) {
			if (!values.contains(value)) {
				values.add(value);
			}
		}
		
		private int getValueCount() {
			return values.size();
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
		private String getValue() {
			
			String result;
			
			switch (values.size()) {
			case 0:
			{
				result = "";
				break;
			}
			case 1:
			{
				result = values.get(0);
				break;
			}
			default:
			{
				result = OmeMetadata.CONFLICT_STRING;
			}
			}
			
			return result;
		}
		
		private List<String> getAllValues() {
			return values;
		}
	}
}
