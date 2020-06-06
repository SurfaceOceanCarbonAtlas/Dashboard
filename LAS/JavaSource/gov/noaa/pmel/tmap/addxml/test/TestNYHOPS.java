package gov.noaa.pmel.tmap.addxml.test;

import gov.noaa.pmel.tmap.addxml.ADDXMLProcessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dods.DODSNetcdfFile;


public class TestNYHOPS extends TestCase {
	@Test
	public final void testNYHOPS() {
		// This data set is a high frequency (possibly irregular) time series an therefore should have the irregular flag applied.
		// New code attempts to detect the need for this automatically.  If the auto detection would fail the units would be "seconds"
		// which LAS does not support.  If it works the units are hours (as asserted below).
		String url = DODSNetcdfFile.canonicalURL("http://colossus.dl.stevens-tech.edu:8080/thredds/dodsC/fmrc/NYBight/NYHOPS_Forecast_Collection_for_the_New_York_Bight_best.ncd");
		NetcdfDataset ncds;
		try {
			ADDXMLProcessor addxml = new ADDXMLProcessor();
			HashMap<String, String> options= new HashMap<String, String>();
			addxml.setOptions(options);
			ncds = ucar.nc2.dataset.NetcdfDataset.openDataset(url);
			Document nyhops = addxml.createXMLfromNetcdfDataset(ncds , url);
			Element axes = nyhops.getRootElement().getChild("axes");
			List<Element> axisList = axes.getChildren();
			assertTrue(axisList.size() > 0);
			for (Iterator axisIt = axisList.iterator(); axisIt.hasNext();) {
				Element axis = (Element) axisIt.next();
				String type = axis.getAttributeValue("type");
				if ( type.equals("t") ) {
					assertTrue(axis.getAttributeValue("units").contains("hour"));
				}
			}
		} catch (IOException e) {
			fail("Unable to connect to OPeNDAP server.");
		}
	}
}
