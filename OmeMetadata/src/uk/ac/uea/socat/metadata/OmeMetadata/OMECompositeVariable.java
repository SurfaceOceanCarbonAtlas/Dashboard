package uk.ac.uea.socat.metadata.OmeMetadata;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

class OMECompositeVariable {
	private Path itsPath;
	private List<String> itsAllowedEntries;
	private List<String> itsIdFields;
	private List<OMECompositeVariableEntry> itsEntries = new ArrayList<OMECompositeVariableEntry>();
	
	protected OMECompositeVariable(Path parentPath, List<String> allowedEntries, String idElement) {
		itsPath = parentPath;
		itsAllowedEntries = allowedEntries;
		itsIdFields = new ArrayList<String>();
		itsIdFields.add(idElement);
	}
	
	protected OMECompositeVariable(Path parentPath, List<String> allowedEntries, List<String> idElements) {
		itsPath = parentPath;
		itsAllowedEntries = allowedEntries;
		itsIdFields = idElements;
	}
	
	private OMECompositeVariable(Path parentPath) {
		itsPath = parentPath;
	}

	protected void addEntry(String name, Element element) throws BadEntryNameException {
		String value = null;
		if (null != element) {
			value = element.getChildTextTrim(name);
			if ( (null == value) && Character.isLowerCase(name.charAt(0)) ) {
				// Try again with the first letter capitalized
				String capName = name.substring(0, 1).toUpperCase() + name.substring(1);
				value = element.getChildTextTrim(capName);
			}
			if ( (null == value) && Character.isUpperCase(name.charAt(0)) ) {
				// Try again with the first letter lower-cased
				String lowName = name.substring(0, 1).toLowerCase() + name.substring(1);
				value = element.getChildTextTrim(lowName);
			}
			if (null != value) {
				addEntry(name, value);
			}
		}
	}
	
	protected void addEntry(String name, String value) throws BadEntryNameException {
		
		if (!itsAllowedEntries.contains(name)) {
			throw new BadEntryNameException("Cannot add an entry '" + name + "' to composite value '" + itsPath.getElementName() + "'");
		}
		
		boolean foundEntry = false;
		for (OMECompositeVariableEntry searchEntry : itsEntries) {
			if (searchEntry.getName().equals(name)) {
				
				if (itsIdFields.contains(name) && searchEntry.getValueCount() > 0) {
					throw new BadEntryNameException("Cannot add multiple values to an identifier in a composite variable");
				} else {
					searchEntry.addValue(value);
					foundEntry = true;
					break;
				}
			}
		}
		
		if (!foundEntry) {
			itsEntries.add(new OMECompositeVariableEntry(name, value));
		}
	}
	
	protected void addEntries(List<OMECompositeVariableEntry> entries) throws BadEntryNameException {
		
		for (OMECompositeVariableEntry entry : entries) {
			
			if (!itsAllowedEntries.contains(entry.getName())) {
				throw new BadEntryNameException("Cannot add an entry '" + entry.getName() + "' to composite value '" + itsPath.getElementName() + "'");
			}

			
			boolean entryStored = false;
			for (OMECompositeVariableEntry existingEntry : itsEntries) {
				if (existingEntry.getName().equalsIgnoreCase(entry.getName())) {
					existingEntry.addValues(entry.getAllValues());
					entryStored = true;
					break;
				}
			}
			
			if (!entryStored) {
				itsEntries.add(entry);
			}
			
			
		}
	}
	
	protected static ArrayList<OMECompositeVariable> mergeVariables(List<OMECompositeVariable> dest, List<OMECompositeVariable> newValues) throws BadEntryNameException {
		
		// Copy the dest list to the output.
		ArrayList<OMECompositeVariable> merged = new ArrayList<OMECompositeVariable>();
		
		// Now copy in the new values
		for (OMECompositeVariable newValue : newValues) {
			
			OMECompositeVariable destVar = findById(dest, newValue);
			if (null == destVar) {
				merged.add((OMECompositeVariable) newValue.clone());
			} else {
				
				OMECompositeVariable mergedVar = (OMECompositeVariable) destVar.clone();
				mergedVar.addEntries(newValue.getAllEntries());
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
	
	protected List<OMECompositeVariableEntry> getAllEntries() {
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
		for (OMECompositeVariableEntry subValue : itsEntries) {
			Element subElement = new Element(subValue.getName());
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
			
			for (OMECompositeVariableEntry value : itsEntries) {
				if (value.getValueCount() > 1) {
					Element valuesElement = new Element(value.getName());
					
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
		
		for (OMECompositeVariableEntry value : itsEntries) {
			if (value.getValueCount() > 1) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	protected String getValue(String valueName) {
		String result = "";
		
		for (OMECompositeVariableEntry value : itsEntries) {
			if (value.getName().equals(valueName)) {
				result = value.getValue();
				break;
			}
		}
		
		return result;
	}
	
	public Object clone() {
		OMECompositeVariable clone = new OMECompositeVariable((Path) itsPath.clone());
		clone.itsAllowedEntries = new ArrayList<String>(itsAllowedEntries);
		clone.itsIdFields = new ArrayList<String>(itsIdFields);
		for (OMECompositeVariableEntry value : itsEntries) {
			clone.itsEntries.add((OMECompositeVariableEntry) value.clone());
		}
		
		return clone;
	}
}
