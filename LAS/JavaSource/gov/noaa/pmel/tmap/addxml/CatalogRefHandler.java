package gov.noaa.pmel.tmap.addxml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class CatalogRefHandler extends DefaultHandler {
	
	Map<String, String> catalogs = new HashMap<String, String>();


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ( qName.equals("catalogRef")) {
			String name = attributes.getValue("xlink:title");
			String catalog = attributes.getValue("xlink:href");
			catalogs.put(name, catalog);
		}
	}

	public Map<String, String> getCatalogs() {
		return catalogs;
	}

	

}
