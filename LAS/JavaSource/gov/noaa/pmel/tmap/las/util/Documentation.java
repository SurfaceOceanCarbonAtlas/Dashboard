package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class Documentation {
	Element element;
    public Documentation (Element documentation) {
    	this.element = documentation;
    }
    public String getSummary() {
    	return this.element.getChildTextNormalize("summary");
    }
    public List<Contact> getContacts() {
    	List contactsChildren = this.element.getChildren("contact");
    	List<Contact> contacts = new ArrayList<Contact>();
    	for (Iterator contIt = contactsChildren.iterator(); contIt.hasNext();) {
			Element contact = (Element) contIt.next();
			contacts.add(new Contact(contact));
		}
    	return contacts;
    }
}
