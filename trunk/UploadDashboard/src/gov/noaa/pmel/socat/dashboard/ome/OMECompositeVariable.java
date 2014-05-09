package gov.noaa.pmel.socat.dashboard.ome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

class OMECompositeVariable {
	private Path itsPath;
	private List<String> itsId;
	private List<Value> itsValues = new ArrayList<Value>();
	private Iterator<Value> itsValuesIterator;
	
	protected OMECompositeVariable(Path parentPath, String idElement) {
		itsPath = parentPath;
		itsId = new ArrayList<String>();
		itsId.add(idElement);
	}
	
	protected OMECompositeVariable(Path parentPath, List<String> idElements) {
		itsPath = parentPath;
		itsId = idElements;
	}

	protected void addValue(String name, Element element) {
		String value = null;
		if (null != element) {
			value = element.getChildTextTrim(name);
		}
		
		itsValues.add(new Value(name, value));
	}
	
	protected boolean hasMoreValues() {
		if (null == itsValuesIterator) {
			itsValuesIterator = itsValues.iterator();
		}
		
		return itsValuesIterator.hasNext();
	}
	
	protected Element getNextValueElement() {
		if (null == itsValuesIterator) {
			itsValuesIterator = itsValues.iterator();
		}
		
		Value nextValue = itsValuesIterator.next();
		
		Element elem = new Element(nextValue.name);
		elem.setText(nextValue.value);
		
		return elem;
	}
	
	private Value getNextValue() {
		if (null == itsValuesIterator) {
			itsValuesIterator = itsValues.iterator();
		}
		
		return itsValuesIterator.next();
	}
	
	protected String getValue(String valueName) {
		String result = "";
		
		Iterator<Value> iterator = itsValues.iterator();
		boolean foundValue = false;
		while (!foundValue && iterator.hasNext()) {
			Value value = iterator.next();
			if (value.name.equals(valueName)) {
				result = value.value;
				foundValue = true;
			}
		}
		
		return result;
	}
	
	protected void resetIterator() {
		itsValuesIterator = null;
	}
	
	private class Value {
		private String name;
		private String value;
		
		private Value(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

}
