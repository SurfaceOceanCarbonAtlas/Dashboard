package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.util.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

public class LASJDOMUtils {
	public static ArrayList<Category> getCategories(String catxml) throws IOException, JDOMException {
		LASDocument catdoc = new LASDocument();
		ArrayList<Category> cats = new ArrayList<Category>();
		JDOMUtils.XML2JDOM(catxml, catdoc);
		List catElements = catdoc.getRootElement().getChildren("category");
		for (Iterator catIt = catElements.iterator(); catIt.hasNext();) {
			Element catE = (Element) catIt.next();
			cats.add(new Category(catE));		
		}
		return cats;
	}
}
