package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.client.serializable.AxisSerializable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;

public class Axis extends Container implements AxisInterface {
    public Axis(Element element){
        super(element);
    }
    
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#getType()
     */
    public String getType() {
        return getAttributeValue("type");
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#setType(java.lang.String)
     */
    public void setType(String type) {
        setAttribute("type",type);
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#getVerticies()
     */
    public ArrayList<NameValuePair> getVerticies() {
        ArrayList<NameValuePair> verticies = new ArrayList<NameValuePair>();
        List vs = element.getChildren("v");
        for (Iterator vsIt = vs.iterator(); vsIt.hasNext();) {
            Element v = (Element) vsIt.next();
            String name = v.getAttributeValue("label");
            String value = v.getTextNormalize();
            if ( name == null ) {
               name = value;	
            }
            verticies.add(new NameValuePair(name,value));
        }
        return verticies;
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#setVerticies(java.util.ArrayList)
     */
     public void setVerticies(ArrayList<NameValuePair> verticies) {
         element.removeChildren("v");
         for (Iterator vertIt = verticies.iterator(); vertIt.hasNext();) {
            NameValuePair vert = (NameValuePair) vertIt.next();
            Element v = new Element("v");
            v.setAttribute(new Attribute("label", vert.getName()));
            element.addContent(v);
        }
     }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#getArange()
     */
    public Arange getArange() {
        Element arange = element.getChild("arange");
        if ( arange != null ) {
            return new Arange(arange);
        }
        return null;
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#setArange(gov.noaa.pmel.tmap.las.util.Arange)
     */
    public void setArange(Arange arange) {
        element.removeChild("arange");
        element.addContent(arange.getElement());
    }
    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.AxisInterface#hasV()
     */
    public boolean hasV() {
        List v = element.getChildren("v");
        if ( v != null && v.size() > 0) {
            return true;
        }
        return false;
    }
    
    public String getHi() {
       Arange arange = getArange();
       if ( arange != null ) {
    	   double start = Double.valueOf(arange.getStart()).doubleValue();
    	   double size = Double.valueOf(arange.getSize()).doubleValue();
    	   double step = Double.valueOf(arange.getStep()).doubleValue();
    	   double hi = start + (size-1.0)*step;
    	   DecimalFormat format = new DecimalFormat("#####.##");
    	   return format.format(hi);
       } else {
    	   ArrayList<NameValuePair> v = getVerticies();
    	   return v.get(v.size()-1).getValue();
       }
    }
    
    public String getLo() {
    	Arange arange = getArange();
    	if ( arange != null ) {
    		return arange.getStart();
    	} else {
    		return (String) getVerticies().get(0).getValue();
    	}
    }
    public AxisSerializable getAxisSerializable() {
		AxisSerializable a = new AxisSerializable();
		
		a.setType(getType());
		a.setID(getID());
		a.setHi(getHi());
		a.setID(getID());
		a.setLo(getLo());
		a.setName(getName());
		a.setLabel(getLabel());
		a.setUnits(getAttributesAsMap().get("units"));
		a.setAttributes(getAttributesAsMap());
		if (hasV()) {
			ArrayList<NameValuePair> vlist = getVerticies();
			String[] names = new String[vlist.size()];
			String[] values = new String[vlist.size()];
			for (int i = 0; i < vlist.size(); i++) {
				NameValuePair p = vlist.get(i);
				names[i] = p.getName();
				values[i] = p.getValue();
			}
			a.setNames(names);
			a.setValues(values);
		} else {
			a.setArangeSerializable(getArange().getArangeSerializable());
		}
		return a;
	}
}
