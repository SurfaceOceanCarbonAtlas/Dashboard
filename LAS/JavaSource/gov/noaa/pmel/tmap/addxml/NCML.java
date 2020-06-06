package gov.noaa.pmel.tmap.addxml;

import org.jdom.Element;

import ucar.nc2.dataset.CoordinateAxis1D;
import ucar.nc2.dt.GridDataset;
import ucar.nc2.dt.GridDatatype;

public class NCML {
	public static Element getRootElement() {
		/*
		 * Make an element that looks like this:
		 * <netcdf xmlns="http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2">
		 */
		Element ncml = new Element("netcdf", "http://www.unidata.ucar.edu/namespaces/netcdf/ncml-2.2");
		return ncml;
	}
	/*
	 * Make an element that looks like this:
     *    <aggregation dimName="TAX" type="joinExisting">
	 */
	public static void addAggregationElement(Element root, GridDataset dataset) {
		Element agg = new Element("aggregation");
		agg.setAttribute("type", "joinExisting");
		GridDatatype grid = dataset.getGrids().get(0);
		CoordinateAxis1D time =  grid.getCoordinateSystem().getTimeAxis1D();
		if ( time != null ) {
			String tname = time.getName();
			agg.setAttribute("dimName", tname);
		}
		root.addContent(agg);
	}
	/*
	 * Add an element to the aggregation that looks like this:
	 *
     *    <netcdf location="http://osmc.noaa.gov:8180/thredds/dodsC/all_plat/2004_NCEP_OBS.cdf" /> 
	 */
	public static void addDataset(Element root, GridDataset dataset) {
		Element agg = root.getChild("aggregation");
		Element netcdf = new Element("netcdf");
		netcdf.setAttribute("location", dataset.getLocationURI());
		GridDatatype grid = dataset.getGrids().get(0);
		CoordinateAxis1D time =  grid.getCoordinateSystem().getTimeAxis1D();
		if ( time != null ) {
			long tsize =time.getSize();
			netcdf.setAttribute("ncoords", String.valueOf(tsize));
		}
		agg.addContent(netcdf);
	}
}
