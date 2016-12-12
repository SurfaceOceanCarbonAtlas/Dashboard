package gov.noaa.pmel.dashboard.test.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.ferret.SocatTool;
import gov.noaa.pmel.dashboard.server.DsgNcFile;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DsgData;

import java.util.ArrayList;

import org.junit.Test;

public class DerivedVariablesTest {

	@Test
	public void derivedVariablesTest() throws Exception {
		System.setProperty("CATALINA_BASE", System.getenv("HOME"));
		System.setProperty("UPLOAD_DASHBOARD_SERVER_NAME", "OAPUploadDashboard");
		DashboardConfigStore confStore = DashboardConfigStore.get(false);
		FerretConfig ferret = confStore.getFerretConfig();
		DsgNcFileTest fileTest = new DsgNcFileTest();
		fileTest.testCreate();
		DsgNcFile dsgFile = fileTest.dsgNcFile;
		String expocode = dsgFile.getMetadata().getDatasetId();
		int numData = dsgFile.getDataList().size();
		ArrayList<Double> longitudes = new ArrayList<Double>(numData);
		ArrayList<Double> latitudes = new ArrayList<Double>(numData);
		ArrayList<Integer> hours = new ArrayList<Integer>(numData);
		ArrayList<Integer> minutes = new ArrayList<Integer>(numData);
		for (int k = 0; k < numData; k++) {
			DsgData dataVals = dsgFile.getDataList().get(k);
			longitudes.add(dataVals.getLongitude());
			latitudes.add(dataVals.getLatitude());
			hours.add(dataVals.getHour());
			minutes.add(dataVals.getMinute());
		}

		SocatTool tool = new SocatTool(ferret);
		String fullDataFilename = dsgFile.getPath();
		ArrayList<String> scriptArgs = new ArrayList<String>(1);
		scriptArgs.add(fullDataFilename);
		tool.init(scriptArgs, expocode, FerretConfig.Action.COMPUTE);
		tool.run();
		assertFalse(tool.hasError());

		ArrayList<String> unknownNames = dsgFile.readMetadata(DsgNcFileTest.KNOWN_SOCAT_METADATA_FILE_TYPES);
		assertEquals(0, unknownNames.size());
		unknownNames = dsgFile.readData(DsgNcFileTest.KNOWN_SOCAT_DATA_FILE_TYPES);
		assertEquals(0, unknownNames.size());
		assertEquals(expocode, dsgFile.getMetadata().getDatasetId());
		assertEquals(numData, dsgFile.getDataList().size());
		for (int k = 0; k < numData; k++) {
			DsgData dataVals = dsgFile.getDataList().get(k);
			assertEquals(Integer.valueOf(k+1), dataVals.getSampleNumber());
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

		DsgNcFile decDsgFile = new DsgNcFile(decDataFilename);
		unknownNames = decDsgFile.readMetadata(DsgNcFileTest.KNOWN_SOCAT_METADATA_FILE_TYPES);
		assertEquals(0, unknownNames.size());
		unknownNames = decDsgFile.readData(DsgNcFileTest.KNOWN_SOCAT_DATA_FILE_TYPES);
		assertEquals(0, unknownNames.size());
		assertEquals(expocode, dsgFile.getMetadata().getDatasetId());
		int lastRowNum = 0;
		for ( DsgData dataVals : dsgFile.getDataList() ) {
			int thisRowNum = dataVals.getSampleNumber();
			assertTrue( lastRowNum < thisRowNum );
			assertTrue( thisRowNum <= numData );
			lastRowNum = thisRowNum;
		}
	}

}
