package uk.ac.uea.socat.omemetadata;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

class OMECompositeVariable {
	private OMEPath itsPath;
	private List<String> itsAllowedEntries;
	private List<String> itsIdFields;
	private List<OMECompositeVariableEntry> itsEntries = new ArrayList<OMECompositeVariableEntry>();
	private Element itsConflictsElement = null;
	
	protected OMECompositeVariable(OMEPath parentPath, List<String> allowedEntries, String idElement, Element conflictsElement) {
		itsPath = parentPath;
		itsAllowedEntries = allowedEntries;
		itsIdFields = new ArrayList<String>();
		itsIdFields.add(idElement);
		itsConflictsElement = conflictsElement;
	}
	
	protected OMECompositeVariable(OMEPath parentPath, List<String> allowedEntries, List<String> idElements, Element conflictsElement) {
		itsPath = parentPath;
		itsAllowedEntries = allowedEntries;
		itsIdFields = idElements;
		itsConflictsElement = conflictsElement;
	}
	
	protected OMECompositeVariable(OMEPath parentPath, List<String> allowedEntries, List<String> idElements) {
		itsPath = parentPath;
		itsAllowedEntries = allowedEntries;
		itsIdFields = idElements;
	}
	
	private OMECompositeVariable(OMEPath parentPath) {
		itsPath = parentPath;
	}

	protected void addEntry(String name, Element element) throws BadEntryNameException, InvalidConflictException {
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

				if (value.equals(OmeMetadata.CONFLICT_STRING)) {
					// If the conflict is in one of the ID fields, we can't deal with it.
					if (itsIdFields.contains(name)) {
						throw new InvalidConflictException("Cannot handle conflicts on field '" + name + "'");
					} else {
						Element entryConflictElement = getEntryConflictElement(name);
						for (Element valueElement : entryConflictElement.getChildren(OmeMetadata.CONFLICT_VALUE_ELEMENT_NAME)) {
							addEntry(name, valueElement.getText());
						}
					}
				} else {
					addEntry(name, value);
				}
			}
		}
	}
	
	private Element getEntryConflictElement(String entryName) throws InvalidConflictException {
		
		List<String> variablePath = itsPath.getPathTree();
		List<Element> testElements = itsConflictsElement.getChildren(OmeMetadata.CONFLICT_ELEMENT_NAME);
		
		boolean foundConflict = false;
		Element conflictElement = null;
		
		for (int i = 0; !foundConflict && i < testElements.size(); i++) {
			Element testElement = testElements.get(i);
			
			for (int j = 0; j < variablePath.size(); j++) {
				
				if ((j + 1) < variablePath.size()) {
					Element childElement = testElement.getChild(variablePath.get(j));
					if (null == childElement) {
						// This isn't the conflict element we're looking for. Move to the next one
						foundConflict = false;
						break;
					} else {
						testElement = childElement;
					}
				} else {
					
					// Get all the child elements and loop through them in turn
					List<Element> childElements = testElement.getChildren(variablePath.get(j));

					for (Element variableElement : childElements) {
					
						// We've found the top level of the composite variable.
						// Checks its attributes to make sure we have the right one
						boolean idsMatch = true;
						for (String idField : itsIdFields) {
							String attributeValue = variableElement.getAttributeValue(idField);
							if (!attributeValue.equals(getValue(idField))) {
								idsMatch = false;
							}
						}
						
						if (idsMatch) {
							foundConflict = true;
							Element entryElement = variableElement.getChild(entryName);
							if (null == entryElement) {
								throw new InvalidConflictException("Could not find conflict data for entry '" + entryName + "'");
							} else {
								conflictElement = entryElement;
								break;
							}
						}
					}
				}
			}
		}
		
		return conflictElement;
	}

	protected void addEntry(String name, String value) throws BadEntryNameException, InvalidConflictException {
		
		if (!itsAllowedEntries.contains(name)) {
			throw new BadEntryNameException("Cannot add an entry '" + name + "' to composite value '" + itsPath.getElementName() + "'");
		}
		
		boolean foundEntry = false;
		for (OMECompositeVariableEntry searchEntry : itsEntries) {
			if (searchEntry.getName().equals(name)) {
				
				if (itsIdFields.contains(name) && searchEntry.getValueCount() > 0) {
					throw new InvalidConflictException("Cannot add multiple values to an identifier in a composite variable");
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
		OMECompositeVariable clone = new OMECompositeVariable((OMEPath) itsPath.clone());
		clone.itsAllowedEntries = new ArrayList<String>(itsAllowedEntries);
		clone.itsIdFields = new ArrayList<String>(itsIdFields);
		for (OMECompositeVariableEntry value : itsEntries) {
			clone.itsEntries.add((OMECompositeVariableEntry) value.clone());
		}
		
		return clone;
	}
}
