package gov.noaa.pmel.tmap.addxml;

import java.util.*;

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
public class UniqueVector extends Vector {

	public boolean addUnique(LasBean bean) {

		if (this.size() <= 0) {
			super.add(bean);
			return true;
		}
		else {
			for (int i = 0; i < this.size(); i++) {
				LasBean lb = (LasBean) this.get(i);
				if (bean.getElement().equals(lb.getElement())) {
					return true;
				}
			}
			this.add(bean);
		}
		return true;
	}

	public boolean contains(LasBean bean) {
		boolean contains = false;
		for (int i = 0; i < this.size(); i++) {
			LasBean t = (LasBean) this.get(i);
			if ( t.equals(bean) ) {
				contains = true;
			}
		}
		return contains;
	}
	public String getMatchingID(LasBean bean) {
		String id = null;
		for (int i = 0; i < this.size(); i++) {
			LasBean t = (LasBean) this.get(i);
			if ( t.equals(bean) ) {
				id = t.getElement();
			}
		}
		return id;
	}
}
