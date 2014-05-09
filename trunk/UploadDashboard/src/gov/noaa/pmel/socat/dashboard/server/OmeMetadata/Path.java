package gov.noaa.pmel.socat.dashboard.server.OmeMetadata;

class Path {
	String itsElementName;
	Path itsParent = null;
	
	protected Path(String elementName) {
		itsElementName = elementName;
	}
	
	protected Path(Path parent, String elementName) {
		itsElementName = elementName;
		itsParent = parent;
	}
	
	protected String getElementName() {
		return itsElementName;
	}
	
	protected Path getParent() {
		return itsParent;
	}
}
