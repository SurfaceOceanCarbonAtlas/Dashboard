package uk.ac.uea.socat.metadata.OmeMetadata;

import org.jdom2.Content;
import org.jdom2.Element;

public class ConflictElement extends Element {
	
	private static final long serialVersionUID = 4576877291545734657L;

	private boolean hasConflicts = false;
	
	protected ConflictElement() {
		super("CONFLICTS");
	}
	
	public Element addContent(Content content) {
		hasConflicts = true;
		return super.addContent(content);
	}
	
	protected boolean conflictsExist() {
		return hasConflicts;
	}

}
