package uk.ac.uea.socat.metadata.OmeMetadata;

import java.util.ArrayList;
import java.util.List;

public class OMECompositeVariableEntry {
	private String itsName;
	private List<String> itsValues;
	
	protected OMECompositeVariableEntry(String name, String value) {
		this.itsName = name;
		itsValues = new ArrayList<String>();
		itsValues.add(value);
	}
	
	protected OMECompositeVariableEntry(String name) {
		this.itsName = name;
		itsValues = new ArrayList<String>();
	}
	
	protected void addValue(String value) {
		if (!itsValues.contains(value)) {
			itsValues.add(value);
		}
	}
	
	protected void addValues(List<String> values) {
		for (String value: values) {
			addValue(value);
		}
	}
	
	protected int getValueCount() {
		return itsValues.size();
	}
	
	protected String getName() {
		return itsName;
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
	
	protected Object clone() {
		OMECompositeVariableEntry clone = new OMECompositeVariableEntry(itsName);
		for (String value : itsValues) {
			clone.itsValues.add(value);
		}
		
		return clone;
	}

}
