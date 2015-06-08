package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class DerivedVariablesTest {

	public static final String CONFIG_FILENAME = 
			"/home/flat/ksmith/content/SocatUploadDashboard/FerretConfig.xml";

	@Test
	public void derivedVariablesTest() throws Exception {
		CruiseDsgNcFileTest fileTest = new CruiseDsgNcFileTest();
		fileTest.testCreate();
		CruiseDsgNcFile dsgFile = fileTest.dsgNcFile;
		String expocode = dsgFile.getMetadata().getExpocode();
		int numData = dsgFile.getDataList().size();
		ArrayList<Double> longitudes = new ArrayList<Double>(numData);
		ArrayList<Double> latitudes = new ArrayList<Double>(numData);
		ArrayList<Integer> hours = new ArrayList<Integer>(numData);
		ArrayList<Integer> minutes = new ArrayList<Integer>(numData);
		for (int k = 0; k < numData; k++) {
			SocatCruiseData dataVals = dsgFile.getDataList().get(k);
			longitudes.add(dataVals.getLongitude());
			latitudes.add(dataVals.getLatitude());
			hours.add(dataVals.getHour());
			minutes.add(dataVals.getMinute());
		}

		FerretConfig ferret;
		InputStream stream = new FileInputStream(new File(CONFIG_FILENAME));
		try {
			SAXBuilder sb = new SAXBuilder();
			Document jdom = sb.build(stream);
			ferret = new FerretConfig();
			ferret.setRootElement((Element)jdom.getRootElement().clone());
		} finally {
			stream.close();
		}
		SocatTool tool = new SocatTool(ferret);
		String fullDataFilename = dsgFile.getPath();
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(fullDataFilename);
		tool.init(scriptArgs, expocode, FerretConfig.Action.COMPUTE);
		tool.run();
		assertFalse(tool.hasError());

		ArrayList<String> unknownNames = dsgFile.read(false);
		assertEquals(0, unknownNames.size());
		assertEquals(expocode, dsgFile.getMetadata().getExpocode());
		assertEquals(numData, dsgFile.getDataList().size());
		for (int k = 0; k < numData; k++) {
			SocatCruiseData dataVals = dsgFile.getDataList().get(k);
			assertEquals(Integer.valueOf(k+1), dataVals.getRowNum());
			assertEquals(longitudes.get(k), dataVals.getLongitude(), 1.0E-6);
			assertEquals(latitudes.get(k), dataVals.getLatitude(), 1.0E-6);
			assertEquals(hours.get(k), dataVals.getHour());
			assertEquals(minutes.get(k), dataVals.getMinute());
		}

		String decDataFilename = fullDataFilename.replace(expocode + ".nc", expocode + "_decimated.nc");
		tool = new SocatTool(ferret);
		scriptArgs.add(decDataFilename);
		tool.init(scriptArgs, expocode, FerretConfig.Action.DECIMATE);
		tool.run();
		assertFalse(tool.hasError());

		CruiseDsgNcFile decDsgFile = new CruiseDsgNcFile(decDataFilename);
		unknownNames = decDsgFile.read(false);
		assertEquals(0, unknownNames.size());
		assertEquals(expocode, dsgFile.getMetadata().getExpocode());
		int lastRowNum = 0;
		for ( SocatCruiseData dataVals : dsgFile.getDataList() ) {
			int thisRowNum = dataVals.getRowNum();
			assertTrue( lastRowNum < thisRowNum );
			assertTrue( thisRowNum <= numData );
			lastRowNum = thisRowNum;
		}
	}

}
