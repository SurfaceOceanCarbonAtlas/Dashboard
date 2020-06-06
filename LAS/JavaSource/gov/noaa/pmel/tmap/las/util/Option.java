/**
 * This software was developed by the Thermal Modeling and Analysis
 * Project(TMAP) of the National Oceanographic and Atmospheric
 * Administration's (NOAA) Pacific Marine Environmental Lab(PMEL),
 * hereafter referred to as NOAA/PMEL/TMAP.
 *
 * Access and use of this software shall impose the following
 * obligations and understandings on the user. The user is granted the
 * right, without any fee or cost, to use, copy, modify, alter, enhance
 * and distribute this software, and any derivative works thereof, and
 * its supporting documentation for any purpose whatsoever, provided
 * that this entire notice appears in all copies of the software,
 * derivative works and supporting documentation. Further, the user
 * agrees to credit NOAA/PMEL/TMAP in any publications that result from
 * the use of this software or in any product that includes this
 * software. The names TMAP, NOAA and/or PMEL, however, may not be used
 * in any advertising or publicity to endorse or promote any products
 * or commercial entity unless specific written permission is obtained
 * from NOAA/PMEL/TMAP. The user also understands that NOAA/PMEL/TMAP
 * is not obligated to provide the user with any support, consulting,
 * training or assistance of any kind with regard to the use, operation
 * and performance of this software nor to provide the user with any
 * updates, revisions, new versions or "bug fixes".
 *
 * THIS SOFTWARE IS PROVIDED BY NOAA/PMEL/TMAP "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL NOAA/PMEL/TMAP BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTUOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE 
 */
package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.client.serializable.OptionSerializable;
import gov.noaa.pmel.tmap.las.ui.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Roland Schweitzer
 *
 */
public class Option extends Container implements OptionInterface {
    
    public Option(Element element) {
        super(element);
        container_type = "array";
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#getHelp()
     */
    public String getHelp() {
        return element.getChildTextNormalize("help");
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#setHelp(java.lang.String)
     */
    public void setHelp(String help) {
    	Element helpE = element.getChild("help");
    	if ( helpE == null ) {
    		helpE = new Element("help");
    		element.addContent(helpE);
    	}
    	helpE.setText(help);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#getMenu()
     */
    public ArrayList<NameValuePair> getMenu() {
    	ArrayList<NameValuePair> menu = new ArrayList<NameValuePair>();
    	Element menuElement = element.getChild("menu");
    	if ( menuElement != null ) {
    		List items = element.getChild("menu").getChildren("item");
    		if ( items != null && items.size() > 0 ) {
    			for (Iterator itemsIt = items.iterator(); itemsIt.hasNext();) {
    				Element item = (Element) itemsIt.next();
    				menu.add(new NameValuePair(item.getTextNormalize(), item.getAttributeValue("values")));
    			}
    		}
    	}
    	return menu;
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#setMenu(java.util.ArrayList)
     */
    public void setMenu(ArrayList<NameValuePair> menu) {
        Element menuE = new Element("menu");
        for (Iterator menuIt = menu.iterator(); menuIt.hasNext();) {
            NameValuePair menuItem = (NameValuePair) menuIt.next();
            Element item = new Element("item");
            Element name = new Element("name");
            Element value = new Element("value");
            name.setText(menuItem.getName());
            value.setText(menuItem.getValue());
            item.addContent(name);
            item.addContent(value);
            menuE.addContent(item);
        }
        element.removeContent(element.getChild("menu"));
        element.addContent(menuE);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#getTitle()
     */
    public String getTitle() {
        return element.getChildTextNormalize("title");
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
    	Element titleE = element.getChild("title");
    	if ( titleE == null ) {
    		titleE = new Element("title");
    		element.addContent(titleE);
    	}
        titleE.setText(title);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OptionInterface#getType()
     */
    public String getType() {
    	if ( getMenu().size() > 0 ) {
    		return "menu";
    	} else { 
    		return "textfield";
    	}
    }
    
    public String getTextField() {
    	Element textfield = element.getChild("textfield");
    	if ( textfield != null ) {
    		return textfield.getAttributeValue("name");
    	} else {
    		return null;
    	}
    }
    
    public void setType(String type) {
    	// a no-op.  Really?
    }
    public String getName() {
    	if ( getType().equals("menu") ) {
    		return element.getChild("menu").getAttributeValue("name");
    	} else {
    		return element.getChild("textfield").getAttributeValue("name");
    	}
    }
    public JSONObject toJSON() throws JSONException {
        ArrayList<String> asArrays = new ArrayList<String>();
        asArrays.add("option");
        return Util.toJSON(element, asArrays);
    }
    public OptionSerializable getOptionSerializable() {
		OptionSerializable os = new OptionSerializable();
		os.setHelp(getHelp());
		os.setType(getType());
		if ( getMenu() != null && getMenu().size() > 0 ) {
		    os.setMenu(getMenuAsMap());
		} else if ( getTextField() != null ) {
			os.setTextField(getTextField());
		}
		os.setTitle(getTitle());
		os.setName(getName());
		os.setAttributes(getAttributesAsMap());
		os.setProperties(getPropertiesAsMap());
		return os;
	}
    public Map<String, String> getMenuAsMap() {
    	Map<String, String> menu = new HashMap<String, String>();
    	ArrayList menuItems = getMenu();
    	for (Iterator menuIt = menuItems.iterator(); menuIt.hasNext();) {
			NameValuePair item = (NameValuePair) menuIt.next();
			menu.put(item.getName(), item.getValue());
		}
    	return menu;
    }
}
