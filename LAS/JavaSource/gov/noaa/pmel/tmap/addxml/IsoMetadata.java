package gov.noaa.pmel.tmap.addxml;

import java.text.DecimalFormat;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class IsoMetadata extends Document {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520276039235605252L;
	private final Namespace gmd = Namespace.getNamespace("gmd", "http://www.isotc211.org/2005/gmd");
	private final Namespace gco = Namespace.getNamespace("gco", "http://www.isotc211.org/2005/gco");
	private final Namespace gml = Namespace.getNamespace("gml", "http://www.opengis.net/gml/3.2");
	private Element geographic;
	private Element vertical;
	private Element time;
	private DecimalFormat format = new DecimalFormat("#########.##");
	
	public void init() {
		Iterator extentIt = getDescendants(new ElementFilter("extent", gmd));
		Element extent;
		if ( extentIt.hasNext() ) {
			extent = (Element) extentIt.next();
			geographic = extent.getChild("EX_Extent", gmd).getChild("geographicElement", gmd).getChild("EX_GeographicBoundingBox", gmd);
			Element verticalParent = extent.getChild("EX_Extent", gmd).getChild("verticalElement", gmd);
			if ( verticalParent != null ) {
				vertical = verticalParent.getChild("EX_VerticalExtent", gmd);
			}
			Element timeouter = extent.getChild("EX_Extent", gmd).getChild("temporalElement", gmd);
			if ( timeouter != null ) {
			    time = timeouter.getChild("EX_TemporalExtent", gmd);
			}
		}
	}
	/*
	 * Z is a pain. It is always reported as altitude, even if the values represent a measure of distance below the surface.
	 * 
	 * In that case, the both values will be negative.
	 * So to report them as positive down, zLo -s 
	 *
	 */
	public String getZlo() {
		if ( vertical != null ) {
			String zmin = vertical.getChild("minimumValue", gmd).getChild("Real", gco).getText();
			double zminD = Double.valueOf(zmin);
			String zmax = vertical.getChild("maximumValue", gmd).getChild("Real", gco).getText();
			double zmaxD = Double.valueOf(zmax);
			// max below or near the surface - kludge
			if ( zminD < 0.0 && zmaxD < 0.5 ) {
				double v = zmaxD;
				if ( zmaxD < 0 ) {
				    v = -zmaxD;
				}
				return format.format(v);
			} else {
				return zmin;
			}
		}
		return null;
	}
	public String getZhi() {
		if ( vertical != null ) {
			String zmin = vertical.getChild("minimumValue", gmd).getChild("Real", gco).getText();
			double zminD = Double.valueOf(zmin);
			String zmax = vertical.getChild("maximumValue", gmd).getChild("Real", gco).getText();
			double zmaxD = Double.valueOf(zmax);
			// max below or near the surface -- kludge
			if ( zminD < 0.0 && zmaxD < 0.5 ) {
				double v = -zminD;
				return format.format(v);
			} else {
				return zmax;
			}
		}
		return null;
	}
	public String getTlo() {
		if ( time != null ) {
			return time.getChild("extent", gmd).getChild("TimePeriod", gml).getChild("beginPosition", gml).getText();
		}
		return null;
	}
	public String getThi() {
        String end = null;
		if ( time != null ) {
		    Element endContainer = time.getChild("extent", gmd).getChild("TimePeriod", gml).getChild("endPosition", gml);
			end = endContainer.getText();
			if ( end.isEmpty() ) {
				String pos = endContainer.getAttributeValue("indeterminatePosition");
				if ( pos.equals("now") ) {
                    DateTime dt = new DateTime().withZone(DateTimeZone.UTC);
                    DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
                    end = fmt.print(dt);
				}
			}
		}
		return end;
	}
	public String getXlo() {
		if ( geographic != null ) {
		   return geographic.getChild("westBoundLongitude", gmd).getChild("Decimal", gco).getText();
		}
		return null;
	}
	public String getXhi() {
		if ( geographic != null ) {
			   return geographic.getChild("eastBoundLongitude", gmd).getChild("Decimal", gco).getText();
			}
			return null;
	}
	public String getYlo() {
		if ( geographic != null ) {
			   return geographic.getChild("southBoundLatitude", gmd).getChild("Decimal", gco).getText();
			}
			return null;
	}
	public String getYhi() {
		if ( geographic != null ) {
			   return geographic.getChild("northBoundLatitude", gmd).getChild("Decimal", gco).getText();
			}
			return null;
	}
}
