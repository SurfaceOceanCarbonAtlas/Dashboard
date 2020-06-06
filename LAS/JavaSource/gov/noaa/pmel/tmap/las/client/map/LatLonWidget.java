/**
 * This software was developed by Roland Schweitzer of Weathertop Consulting, LLC 
 * (http://www.weathertopconsulting.com/) as part of work performed for
 * NOAA Contracts AB113R-04-RP-0068 and AB113R-09-CN-0182.  
 * 
 * The NOAA licensing terms are explained below.
 * 
 * 
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * A widget that displays the N and S latitude and E and W longitude of the current selection.
 * @author rhs
 *
 */
public class LatLonWidget extends Composite {
	double xlo;
	double xhi;
	double ylo;
	double yhi;
	TextBox southLat;
	TextBox northLat;
	TextBox westLon;
	TextBox eastLon;
	Image rose;
	VerticalPanel middle;
	Grid panel;
	
	private static final String boxWidth = "70px";
	/**
	 * Constructs an empty widget.
	 */
	public LatLonWidget() {
		panel = new Grid(1,3);
		middle = new VerticalPanel();
		
		rose = new Image(GWT.getModuleBaseURL()+"../images/compass_rose.png");
		rose.addStyleName("OL_MAP-middle");

		southLat = new TextBox();
		northLat = new TextBox();

		southLat.setWidth(boxWidth);
		northLat.setWidth(boxWidth);

		eastLon = new TextBox();
		westLon = new TextBox();

		eastLon.setWidth(boxWidth);
		westLon.setWidth(boxWidth);
        
		panel.setWidget(0, 0, westLon);	
		middle.add(northLat);
		middle.add(rose);
		middle.add(southLat);
		panel.setWidget(0, 1, middle);
		panel.setWidget(0, 2, eastLon);
//		panel.getFlexCellFormatter().setColSpan(2, 0, 2);
		
		initWidget(panel);
	}
	/**
	 * Sets the values of the N and S latitude and the E and W longitude, use a degenerate bounds when the tool type is a line or point.
	 * @param selectionBounds
	 */
	public void setText(double nLat, double sLat, double eLon, double wLon) {
		ylo = sLat;
		String slat_f = GeoUtil.compassLat(ylo);
		southLat.setText(slat_f);
		yhi = nLat;
		String nlat_f = GeoUtil.compassLat(yhi);
		northLat.setText(nlat_f);
		xlo = wLon;
		xhi = eLon;
		String wlon_f = GeoUtil.compassLon(GeoUtil.normalizeLon(xlo));
		String elon_f = GeoUtil.compassLon(GeoUtil.normalizeLon(xhi));
		westLon.setText(wlon_f);
		eastLon.setText(elon_f);
		
	}
	/**
	 * Get the East longitude value as a String formatted to 2 decimal places.
	 * @return xhi The current East longitude as a String formatted to 2 decimal places.
	 */
	public String getXhiFormatted() {
		return eastLon.getText();
	}
	/**
	 * Get the West longitude value as a String formatted to 2 decimal places.
	 * @return xlo The current West longitude as a String formatted to 2 decimal places.
	 */
	public String getXloFormatted() {
		return westLon.getText();
	}
	/**
	 * Get the South latitude value as a String formatted to 2 decimal places.
	 * @return ylo The current South latitude as a String formatted to 2 decimal places.
	 */
	public String getYloFormatted() {
		return southLat.getText();
	}
	/**
	 * Get the North latitude value as a String formatted to 2 decimal places.
	 * @return yhi The North latitude as a String formatted to 2 decimal places.
	 */
	public String getYhiFormatted() {
		return northLat.getText();
	}
	/**
	 * Get the current east longitude.
	 * @return xhi the current east longitude
	 */
	public double getXhi() {
		return xhi;
	}
	/**
	 * Get the current west longitude
	 * @return xlo the current west longitude
	 */
	public double getXlo() {
		return xlo;
	}
	/**
	 * Get the current north latitude
	 * @return yhi the current north latitude
	 */
	public double getYhi() {
		return yhi;
	}
	/**
	 * Get the current south latitude
	 * @return ylo the current south latitude
	 */
	public double getYlo() {
		return ylo;
	}
	/**
	 * Adds a listener to the south latitude TextBox.  Supplied by the ReferenceMap so it can update all the other widgets when the value changes.
	 * @param southChangeListener the listener for the south latitude TextBox
	 */
	public void addSouthChangeListener(ChangeListener southChangeListener) {
		southLat.addChangeListener(southChangeListener);
	}
	/**
	 * Adds a listener to the north latitude TextBox.  Supplied by the ReferenceMap so it can update all the other widgets when the value changes.
	 * @param northChangeListener the listener for the north latitude TextBox
	 */
	public void addNorthChangeListener(ChangeListener northChangeListener) {
		northLat.addChangeListener(northChangeListener);
	}
	/**
	 * Adds a listener to the east longitude TextBox.  Supplied by the ReferenceMap so it can update all the other widgets when the value changes.
	 * @param eastChangeListener the listener for the east longitude TextBox
	 */
	public void addEastChangeListener(ChangeListener eastChangeListener) {
		eastLon.addChangeListener(eastChangeListener);
	}
	/**
	 * Adds a listener to the west longitude TextBox.  Supplied by the ReferenceMap so it can update all the other widgets when the value changes.
	 * @param westChangeListener the listener for the west longitude TextBox
	 */
	public void addWestChangeListener(ChangeListener westChangeListener) {
		westLon.addChangeListener(westChangeListener);
	}
}
