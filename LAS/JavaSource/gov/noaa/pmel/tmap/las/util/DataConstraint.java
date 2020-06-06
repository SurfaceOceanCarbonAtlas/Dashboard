/**
 * 
 */
package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author rhs
 *
 */
public class DataConstraint extends Container implements DataConstraintInterface {

	/**
	 * @param element
	 */
	public DataConstraint(Element element) {
		super(element);
	}
	private ArrayList<NameValuePair> getPairs(String position) {
		List menus = element.getChildren("menu");
		Element theMenu = null;
		for (Iterator menuIt = menus.iterator(); menuIt.hasNext();) {
			Element menu = (Element) menuIt.next();
			if ( menu.getAttributeValue("position").equals(position) ) {
				theMenu = menu;
			}
		}

		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if ( theMenu != null ) {
			List items = theMenu.getChildren("item");
			for (Iterator itemIt = items.iterator(); itemIt.hasNext();) {
				Element item = (Element) itemIt.next();
				pairs.add(new NameValuePair(item.getTextTrim(), item.getAttributeValue("values")));
			}
		}
		return pairs;
	}
	public ArrayList<NameValuePair> getLhs() {
		return getPairs("lhs");
	}
	public ArrayList<NameValuePair> getOps() {
		return getPairs("ops");
	}
	public ArrayList<NameValuePair> getRhs() {
        return getPairs("rhs");
	}
	public ArrayList<NameValuePair> getLhsSorted() {
		ArrayList<NameValuePair> pairs = getPairs("lhs");
		Collections.sort(pairs);
		return pairs;
	}
	public ArrayList<NameValuePair> getOpsSorted() {
		ArrayList<NameValuePair> pairs = getPairs("ops");
		Collections.sort(pairs);
		return pairs;
	}
	public ArrayList<NameValuePair> getRhsSorted() {
		ArrayList<NameValuePair> pairs = getPairs("rhs");
		Collections.sort(pairs);
        return pairs;
	}
	public String getType() {
		return element.getAttributeValue("type");
	}
        public String getId() {
                return element.getAttributeValue("ID");
        }

}
