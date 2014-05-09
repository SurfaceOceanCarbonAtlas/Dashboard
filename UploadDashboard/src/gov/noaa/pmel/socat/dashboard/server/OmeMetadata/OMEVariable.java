package gov.noaa.pmel.socat.dashboard.server.OmeMetadata;

import org.jdom2.Element;

class OMEVariable {

	private Path itsPath;
	private String itsValue;
	
	protected OMEVariable(Path parentPath, Element parentElement, String name) {
		itsPath = new Path(parentPath, name);
		if (null != parentElement) {
			itsValue = parentElement.getChildTextTrim(name);
		}
	}
	
	protected OMEVariable(Path path, String value) {
		itsPath = path;
		itsValue = value;
	}
	
	protected String getValue() {
		String result = itsValue;
		if (null == result) {
			result = "";
		}
		
		return itsValue;
	}
	
	protected Path getPath() {
		return itsPath;
	}
	
	protected Element getElement() {
		Element elem = new Element(itsPath.getElementName());
		if (null != itsValue) {
			elem.setText(itsValue);
		}
		
		return elem;
	}

}
