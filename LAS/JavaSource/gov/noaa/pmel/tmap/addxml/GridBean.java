package gov.noaa.pmel.tmap.addxml;

import java.util.*;

import org.jdom.Element;

/**
 * <p>Title: addXML</p>
 *
 * <p>Description: Reads local or OPeNDAP netCDF files and generates LAS XML
 * configuration information.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: NOAA/PMEL/TMAP</p>
 *
 * @author RHS
 * @version 1.0
 */
public class GridBean extends LasBean{
	private List<AxisBean> axes;

	public GridBean() {
	}

	public void setAxes(List<AxisBean> axes) {
		this.axes = axes;
	}

	public List<AxisBean> getAxes() {
		return axes;
	}
    public Element toXml(boolean seven) {
        Element grid;
        if ( seven ) {
            grid = new Element("grid");
            grid.setAttribute("ID", this.getElement());
        } else {
            grid = new Element(this.getElement());
        }
        Iterator ait = axes.iterator();
        while (ait.hasNext()) {
            AxisBean ab = (AxisBean)ait.next();
            if ( seven ) {
                 Element a = new Element("axis");
                 a.setAttribute("IDREF", ab.getElement());
                 grid.addContent(a);
            } else {
                Element link = new Element("link");
                link.setAttribute("match","/lasdata/axes/"+ab.getElement());
                grid.addContent(link);
            }
        }
        return grid;
    }
	public Element toXml() {
		return toXml(false);
	}

	@Override
	public boolean equals(LasBean bean) {
		GridBean b;
		if ( !(bean instanceof GridBean) ) {
			return false;
		} else {
			b = (GridBean) bean;
		}
		if ( axes.size() != b.getAxes().size() ) return false;
		boolean match = true;
		for (Iterator axesIt = axes.iterator(); axesIt.hasNext();) {
			AxisBean a = (AxisBean) axesIt.next();
			boolean amatch = false;
			for (Iterator bAxesIt = b.getAxes().iterator(); bAxesIt.hasNext();) {
				AxisBean baxis = (AxisBean) bAxesIt.next();
				if ( a.equals(baxis) ) amatch = true;
			}
			match = match && amatch;
		}
		return match;
	}
}
