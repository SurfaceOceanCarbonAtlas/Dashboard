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

import com.google.gwt.i18n.client.NumberFormat;

public class GeoUtil {
	public static double normalizeLon(double lon) {
		if ( lon == lon ) {
			while ( (lon < -180.0) || (lon > 180. ) ) {
				lon = IEEEremainder(lon, 360.);
			}
		}
		return lon;
	}
	public static String compassLon(double lon) {
		NumberFormat lonFormat = NumberFormat.getFormat("####.##");
		String compass_lon;
		if ( lon < 0.0 ) {
			compass_lon = lonFormat.format(Math.abs(lon))+" W";
		} else {
			compass_lon = lonFormat.format(lon)+" E";
		}
		return compass_lon;
	}
	public static String compassLat(double lat) {
		NumberFormat latFormat = NumberFormat.getFormat("###.##");
		String compass_lat;
		if ( lat <= 0.0 ) {
			compass_lat = latFormat.format(Math.abs(lat))+" S";
		} else {
			compass_lat = latFormat.format(lat)+" N";
		}
		return compass_lat;
	}
	public static double IEEEremainder(double f1, double f2) {
		double r = Math.abs(f1 % f2);
		if(Double.isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
			return r;
		}
		else {
			return Math.signum(f1) * (r - f2);
		}
	}
	public static String format_two(int i) {
		// Really an error for i<10 and i>99, but these are 1<days<31 and 0<hours<23.
		if ( i < 10 ) {
			return "0"+i;
		} else {
			return String.valueOf(i);
		}
	}
	public static String format_four (int i) {
		// Really an error for i<100 and i>9999, but these are years which start at 0001 or at worst 0000.
		if ( i < 10 ) {
			return "000"+i;
		} else if ( i >= 10 && i < 100 ) {
			return "00"+i;
		} else if ( i >= 100 && i < 1000 ) {
			return "0"+i;
		} else {
			return String.valueOf(i);
		}
	}
}
