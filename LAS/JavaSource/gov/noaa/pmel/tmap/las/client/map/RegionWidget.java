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

import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
/**
 * A widget that allows the user to set the map selection by choosing a named region.  These regions are derived from our application
 <pre>
                 These are the default regions from LAS 
                        wLon, eLon,sLat, nLat
                         "-20, 60, -40, 40"  Africa
                         "40, 180, 0, 80"    Asia
                         "110, 180, -50, 0"  Australia
                         "-10, 40, 30, 75"   Europe
                         "-170, -50, 10, 75" North America 
                         "-90, -30, -60, 15" South America
                         "20,120,-75,30"     Indian Ocean
                         "-80, 20, 0, 70"    North Atlantic
                         "-80, 20, -30, 30"  Equatorial Atlantic
                         "-70,25,-75,10"     South Atlantic
                         "110, -100, 0, 70"  North Pacific
                         "135,-75,-30,30"    Equatorial Pacific
                         "150, -70, -75, 0"  South Pacific
            </pre>
 *
 * @author rhs
 *
 */
public class RegionWidget extends ListBox {
    Map<String, double[]> regions = new HashMap<String, double[]>();
    String title = "Select Region";
    public RegionWidget() {
    	super();
    	init( null, false);
    }
    public RegionWidget(String title, boolean global) {
    	super();
    	init(title, global);
    }
    public double[] getRegion(int i, String name) {
    	double[] region = null;
    	if ( i >= 1 && getItemText(i).equals(name) ) {
    		region = regions.get(getValue(i));
    	}	
    	return region;
    }
    /**
     * Start up the region widget
     * @param map the map to which this widget applies
     * @param title the title for the first entry in the list, can be null
     * @param global whether you want the "Global" region to appear in the list
     */
    private void init(String title, boolean global) {
    	
    	if (title == null ) {
    	    addItem(this.title, "none");
    	} else {
    		this.title = title;
    		addItem(this.title, "none");
    	}
//    	if ( global ) {
//    		regions.put("global", new double[]{-90., 90, -180., 180.0});
//    		addItem("Global", "global");
//    	}
    	// These are s, n, w, e
    	regions.put("africa", new double[]{-40, 40, -20, 60});
    	addItem("Africa", "africa");
    	regions.put("asia", new double[]{0, 80, 40, 180});
    	addItem("Asia", "asia");
    	regions.put("australia", new double[]{-50, 0, 110, 180});
    	addItem("Australia", "australia");
    	regions.put("europe", new double[]{30, 75, -10, 40});
    	addItem("Europe", "europe");
    	regions.put("north america", new double[]{10, 75, -170, -50});
    	addItem("North America", "north america");
    	regions.put("south america", new double[]{-60, 15, -90, -30});
    	addItem("South America", "south america");
    	regions.put("indian ocean", new double[]{-75, 30, 20, 120});
    	addItem("Indian Ocean", "indian ocean");
    	regions.put("north atlantic", new double[] {0, 70, -80, 20});
    	addItem("North Atlantic", "north atlantic");
    	regions.put("equatorial atlantic", new double[] {-30, 30, -80, 20});
    	addItem("Equatorial Atlantic", "equatorial atlantic");
    	regions.put("south atlantic", new double[]{-75, 10, -70, 25});
    	addItem("South Atlantic", "south atlantic");
    	regions.put("north pacific", new double[]{0, 70, 110, 260});
    	addItem("North Pacific", "north pacific");
    	regions.put("equatorial pacific", new double[]{-30, 30, 135, 285});
    	addItem("Equatorial Pacific", "equatorial pacific");
    	regions.put("south pacific", new double[]{-75, 0, 150, 290});
    	addItem("South Pacific", "south pacific");
    	setVisibleItemCount(regions.size()+1);
    }
	/**
	 * Remove the default listener and add the listener
	 * @param listener the listener that will replace the default
	 */
	public void setChangeListener(ChangeListener listener) {
		addChangeListener(listener);
	}
	public void setRegions(RegionSerializable[] regions_in) {
		regions.clear();
		clear();
		if (title == null ) {
    	    addItem(this.title, "none");
    	} else {
    		addItem(this.title, "none");
    	}
		for( int i = 0; i < regions_in.length; i++) {
			regions.put(regions_in[i].getName(), new double[]{regions_in[i].getSouthLat(), regions_in[i].getNorthLat(), regions_in[i].getWestLon(), regions_in[i].getEastLon()});
	    	addItem(regions_in[i].getName(), regions_in[i].getName());
		}
		setVisibleItemCount(regions.size()+1);
	}
}
