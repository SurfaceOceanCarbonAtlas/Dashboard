package uk.ac.uea.socat.metadata.OmeMetadata;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

class OMECompositeVariable {
	private Path itsPath;
	private List<String> itsIdFields;
	private List<Entry> itsEntries = new ArrayList<Entry>();
	
	protected OMECompositeVariable(Path parentPath, String idElement) {
		itsPath = parentPath;
		itsIdFields = new ArrayList<String>();
		itsIdFields.add(idElement);
	}
	
	protected OMECompositeVariable(Path parentPath, List<String> idElements) {
		itsPath = parentPath;
		itsIdFields = idElements;
	}
	
	private OMECompositeVariable(Path parentPath) {
		itsPath = parentPath;
	}

	protected void addEntry(String name, Element element) throws IllegalArgumentException {
		String value = null;
		if (null != element) {
			value = element.getChildTextTrim(name);
			if (null != value) {
				addEntry(name, value);
			}
		}
	}
	
	protected void addEntry(String name, String value) throws IllegalArgumentException {
		
		boolean foundEntry = false;
		for (Entry searchEntry : itsEntries) {
			if (searchEntry.name.equals(name)) {
				
				if (itsIdFields.contains(name) && searchEntry.getValueCount() > 0) {
					throw new IllegalArgumentException("Cannot add multiple values to an identifier in a composite variable");
				} else {
					searchEntry.addValue(value);
					foundEntry = true;
					break;
				}
			}
		}
		
		if (!foundEntry) {
			itsEntries.add(new Entry(name, value));
		}
	}
	
	protected void addEntries(List<Entry> entries) {
		
		for (Entry entry : entries) {
			
			boolean entryStored = false;
			for (Entry existingEntry : itsEntries) {
				if (existingEntry.name.equals(entry.name)) {
					existingEntry.addValues(entry.values);
					entryStored = true;
					break;
				}
			}
			
			if (!entryStored) {
				itsEntries.add(entry);
			}
			
			
		}
	}
	
	protected static List<OMECompositeVariable> mergeVariables(List<OMECompositeVariable> dest, List<OMECompositeVariable> newValues) {
		
		// Copy the dest list to the output.
		List<OMECompositeVariable> merged = new ArrayList<OMECompositeVariable>();
		
		// Now copy in the new values
		for (OMECompositeVariable newValue : newValues) {
			
			OMECompositeVariable destVar = findById(dest, newValue);
			if (null == destVar) {
				merged.add((OMECompositeVariable) newValue.clone());
			} else {
				
				OMECompositeVariable mergedVar = (OMECompositeVariable) destVar.clone();
				mergedVar.addEntries(newValue.getAllValues());
				merged.add(mergedVar);
			}
		}
		
		// Anything in dest but not in new can now be added
		for (OMECompositeVariable destValue : dest) {
			OMECompositeVariable matchingNew = findById(newValues, destValue);
			if (null == matchingNew) {
				merged.add((OMECompositeVariable) destValue.clone());
			}
		}
		
		return merged;
	}
	
	private List<Entry> getAllValues() {
		return itsEntries;
	}
	
	private static OMECompositeVariable findById(List<OMECompositeVariable> variables, OMECompositeVariable criteria) {
		OMECompositeVariable found = null;
		
		for (OMECompositeVariable variable : variables) {
			
			boolean match = true;
			for (String idField : variable.itsIdFields) {
				if (!valuesEqual(variable.getValue(idField), criteria.getValue(idField))) {
					match = false;
				}
			}
			
			if (match) {
				found = variable;
				break;
			}
		}
		
		return found;
	}
	
	private static boolean valuesEqual(String val1, String val2) {
		boolean result = false;
		
		if (null == val1 && null == val2) {
			result = true;
		} else if (null == val1 && null != val2 && val2.equals("")) {
			result = true;
		} else if (null != val1 && val1.equals("") && null == val2) {
			result = true;
		} else if (val1.equals(val2)) {
			result = true;
		}
		
		return result;
	}
		
	private Element getElement() {
		Element element = new Element(itsPath.getElementName());
		for (Entry subValue : itsEntries) {
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
				String value = getValue(id);
				if (null == value) {
					value = "";
				}
				
				variableElement.setAttribute(id, value);
			}
			
			for (Entry value : itsEntries) {
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
		
		for (Entry value : itsEntries) {
			if (value.getValueCount() > 1) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	protected String getValue(String valueName) {
		String result = "";
		
		for (Entry value : itsEntries) {
			if (value.name.equals(valueName)) {
				result = value.getValue();
				break;
			}
		}
		
		return result;
	}
	
	public Object clone() {
		OMECompositeVariable clone = new OMECompositeVariable((Path) itsPath.clone());
		clone.itsIdFields = new ArrayList<String>();
		for (String id : itsIdFields) {
			clone.itsIdFields.add(id);
		}
		
		for (Entry value : itsEntries) {
			clone.itsEntries.add((Entry) value.clone());
		}
		
		return clone;
	}
	
	private class Entry {
		private String name;
		private List<String> values;
		
		private Entry(String name, String value) {
			this.name = name;
			values = new ArrayList<String>();
			values.add(value);
		}
		
		private Entry(String name) {
			this.name = name;
			values = new ArrayList<String>();
		}
		
		private void addValue(String value) {
			if (!values.contains(value)) {
				values.add(value);
			}
		}
		
		private void addValues(List<String> values) {
			for (String value: values) {
				addValue(value);
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
		
		public Object clone() {
			Entry clone = new Entry(name);
			for (String value : values) {
				clone.values.add(value);
			}
			
			return clone;
		}
	}
}
