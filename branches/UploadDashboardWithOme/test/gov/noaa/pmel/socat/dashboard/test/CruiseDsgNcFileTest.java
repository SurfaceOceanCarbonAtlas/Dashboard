/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class CruiseDsgNcFileTest {
    CruiseDsgNcFile dsgNcFile = null;
 
    /**
	 * Test method for successfully creating a DSG file using 
	 * {@link gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile#create}.
	 */
	@Test
	public void testCreate() throws Exception {
		ArrayList<DataColumnType> testTypes = new ArrayList<DataColumnType>(Arrays.asList(
				DataColumnType.EXPOCODE,
				DataColumnType.CRUISE_NAME,
				DataColumnType.MONTH, 
				DataColumnType.DAY, 
				DataColumnType.YEAR, 
				DataColumnType.HOUR, 
				DataColumnType.MINUTE, 
				DataColumnType.LATITUDE, 
				DataColumnType.LONGITUDE, 
				DataColumnType.SEA_SURFACE_TEMPERATURE,
				DataColumnType.SALINITY,
				DataColumnType.XCO2_WATER_SST_DRY,
				DataColumnType.PCO2_WATER_TEQU_WET,
				DataColumnType.SEA_LEVEL_PRESSURE,
				DataColumnType.SHIP_SPEED));
		String[] dataValueStrings = {
				"31B520060606,GM0606,6,10,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3", 
				"31B520060606,GM0606,6,10,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3", 
				"31B520060606,GM0606,6,10,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2", 
				"31B520060606,GM0606,6,10,2006,23,51,29.0517,-92.7592,28.99,33.44,399.7,382.7,1009.302,0.3", 
				"31B520060606,GM0606,6,10,2006,23,52,29.0516,-92.7592,28.9,33.39,397.9,381,1009.29,0.3", 
				"31B520060606,GM0606,6,10,2006,23,53,29.0516,-92.7593,28.93,33.38,397.1,380.3,1009.283,0.3", 
				"31B520060606,GM0606,6,10,2006,23,54,29.0515,-92.7593,28.96,33.38,395.8,379,1009.272,0.3", 
				"31B520060606,GM0606,6,10,2006,23,55,29.051,-92.76,28.88,33.38,395.7,378.9,1009.264,3", 
				"31B520060606,GM0606,6,10,2006,23,56,29.0502,-92.7597,29.08,33.4,395.3,378.3,1009.264,3.1", 
				"31B520060606,GM0606,6,10,2006,23,57,29.0494,-92.7593,29.35,33.3,392.1,375.1,1009.255,3.1", 
				"31B520060606,GM0606,6,10,2006,23,58,29.0486,-92.759,29.34,33.28,391,374,1009.246,3.1", 
				"31B520060606,GM0606,6,10,2006,23,59,29.0478,-92.7587,29.29,33.28,390.5,373.6,1009.223,3.1", 
				"31B520060606,GM0606,6,11,2006,0,00,29.0478,-92.7538,29.29,33.32,390.9,374,1009.23,17.6", 
				"31B520060606,GM0606,6,11,2006,0,01,29.0492,-92.7522,29.35,33.41,390.3,373.3,1009.255,7.8", 
				"31B520060606,GM0606,6,11,2006,0,02,29.0506,-92.7505,29.39,33.47,393,375.9,1009.266,7.8", 
				"31B520060606,GM0606,6,11,2006,0,03,29.052,-92.7489,29.43,33.55,395.7,378.4,1009.28,7.8", 
				"31B520060606,GM0606,6,11,2006,0,04,29.0534,-92.7472,29.73,33.64,399.7,382,1009.3,7.8", 
				"31B520060606,GM0606,6,11,2006,0,05,29.0577,-92.7492,29.84,33.64,402.9,385,1009.302,16.9", 
				"31B520060606,GM0606,6,11,2006,0,06,29.0587,-92.7512,29.67,33.55,406.9,388.9,1009.305,8.2", 
				"31B520060606,GM0606,6,11,2006,0,07,29.0597,-92.7533,29.66,33.52,408.1,390.2,1009.308,8.2", 
				"31B520060606,GM0606,6,11,2006,0,08,29.0608,-92.7553,29.82,33.42,408.1,390,1009.306,8.2", 
				"31B520060606,GM0606,6,11,2006,0,09,29.0618,-92.7574,29.81,33.31,408.2,390,1009.31,8.2", 
				"31B520060606,GM0606,6,11,2006,0,10,29.0648,-92.7623,29.82,33.22,405.9,387.9,1009.304,20.8", 
				"31B520060606,GM0606,6,11,2006,0,11,29.0641,-92.7641,29.9,33.14,404,386,1009.26,7.1", 
				"31B520060606,GM0606,6,11,2006,0,12,29.0634,-92.766,29.89,32.97,402.9,384.9,1009.237,7.1"
			};
		String expocode = "31B520060606";
		ArrayList<ArrayList<String>> testValues = new ArrayList<ArrayList<String>>();
		for ( String valsString : dataValueStrings ) {
			ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",",-1)));
			testValues.add(dataVals);
		}

		// Create the DashboardCruiseWithData from the above data
		DashboardCruiseWithData cruise = new DashboardCruiseWithData();
		cruise.setDataColTypes(testTypes);
		cruise.setDataValues(testValues);
		ArrayList<HashSet<Integer>> woceThrees = cruise.getWoceThreeRowIndices();
		ArrayList<HashSet<Integer>> woceFours = cruise.getWoceFourRowIndices();
		for (int k = 0; k < testTypes.size(); k++) {
			woceThrees.add(new HashSet<Integer>());
			woceFours.add(new HashSet<Integer>());
		}

		// Create the list of SocatCruiseData from the DashboardCruiseWithData
		ArrayList<SocatCruiseData> dataList = SocatCruiseData.dataListFromDashboardCruise(cruise);

		// Create the SocatMetadata for this cruise
		SocatMetadata metadata = new SocatMetadata();
		metadata.setExpocode(expocode);
		metadata.setSocatVersion("3.0");
		metadata.setCruiseName("GM0606");
		metadata.setScienceGroup("Public, Nancy S.; Public, John Q.");
		metadata.setVesselName("Caribbean Cruiser");
		metadata.setOrigDataRef("doi:cdiac12345");
		metadata.setSouthmostLatitude(20.04);
		metadata.setNorthmostLatitude(29.07);
		metadata.setWestmostLongitude(-92.77);
		metadata.setEastmostLongitude(-92.74);
		SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
		metadata.setBeginTime(dateFmt.parse("2006-06-10 23:48 UTC"));
		metadata.setEndTime(dateFmt.parse("2006-06-11 00:12 UTC"));

		File parentDir = new File("/var/tmp/socat");
		if ( ! parentDir.exists() )
			parentDir.mkdir();
		dsgNcFile = new CruiseDsgNcFile(parentDir, expocode + ".nc");
		try {
			dsgNcFile.create(metadata, dataList);
		} catch ( Exception ex ) {
			dsgNcFile.delete();
		}
		assertTrue( dsgNcFile.exists() );
		assertEquals(expocode, dsgNcFile.getMetadata().getExpocode());
		assertEquals(dataValueStrings.length, dsgNcFile.getDataList().size());
	}

    /**
	 * Test method for checking expected failures to a DSG file using 
	 * {@link gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile#create}.
	 */
	@Test
	public void testBadMissingValuesFail() throws Exception {
		ArrayList<DataColumnType> testTypes = new ArrayList<DataColumnType>(Arrays.asList(
				DataColumnType.EXPOCODE,
				DataColumnType.CRUISE_NAME,
				DataColumnType.MONTH, 
				DataColumnType.DAY, 
				DataColumnType.YEAR, 
				DataColumnType.HOUR, 
				DataColumnType.MINUTE, 
				DataColumnType.LATITUDE, 
				DataColumnType.LONGITUDE, 
				DataColumnType.SEA_SURFACE_TEMPERATURE,
				DataColumnType.SALINITY,
				DataColumnType.XCO2_WATER_SST_DRY,
				DataColumnType.PCO2_WATER_TEQU_WET,
				DataColumnType.SEA_LEVEL_PRESSURE,
				DataColumnType.SHIP_SPEED));
		String[][] badTimeDataValueStringsSets = {
				{
					"11B520060606,GM0606,2,28,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3", 
					"11B520060606,GM0606,2,29,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3", 
					"11B520060606,GM0606,3,1,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2"
				},
				{
					"11B520060606,GM0606,2,28,2006,23,48,29.0514,-92.759,28.78,33.68,409.7,392.5,1009.281,0.3", 
					"11B520060606,GM0606,2,NaN,2006,23,49,29.0513,-92.759,28.9,33.56,405.5,388.3,1009.298,0.3", 
					"11B520060606,GM0606,3,1,2006,23,50,29.0518,-92.7591,28.94,33.48,402.1,385.1,1009.314,2"
				}
			};
		String expocode = "11B520060606";
		for ( String[] dataValueStrings : badTimeDataValueStringsSets ) {
			ArrayList<ArrayList<String>> testValues = new ArrayList<ArrayList<String>>();
			for ( String valsString : dataValueStrings ) {
				ArrayList<String> dataVals = new ArrayList<String>(Arrays.asList(valsString.split(",",-1)));
				testValues.add(dataVals);
			}

			// Create the DashboardCruiseWithData from the above data
			DashboardCruiseWithData cruise = new DashboardCruiseWithData();
			cruise.setDataColTypes(testTypes);
			cruise.setDataValues(testValues);
			ArrayList<HashSet<Integer>> woceThrees = cruise.getWoceThreeRowIndices();
			ArrayList<HashSet<Integer>> woceFours = cruise.getWoceFourRowIndices();
			for (int k = 0; k < testTypes.size(); k++) {
				woceThrees.add(new HashSet<Integer>());
				woceFours.add(new HashSet<Integer>());
			}

			// Create the list of SocatCruiseData from the DashboardCruiseWithData
			ArrayList<SocatCruiseData> dataList = SocatCruiseData.dataListFromDashboardCruise(cruise);

			// Create the SocatMetadata for this cruise
			SocatMetadata metadata = new SocatMetadata();
			metadata.setExpocode(expocode);
			metadata.setSocatVersion("3.0");
			metadata.setCruiseName("GM0606");
			metadata.setScienceGroup("Public, Nancy S.; Public, John Q.");
			metadata.setVesselName("Caribbean Cruiser");
			metadata.setOrigDataRef("doi:cdiac12345");
			metadata.setSouthmostLatitude(20.04);
			metadata.setNorthmostLatitude(29.07);
			metadata.setWestmostLongitude(-92.77);
			metadata.setEastmostLongitude(-92.74);
			SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
			metadata.setBeginTime(dateFmt.parse("2006-02-28 23:48 UTC"));
			metadata.setEndTime(dateFmt.parse("2006-03-01 23:50 UTC"));

			File parentDir = new File("/var/tmp/socat");
			if ( ! parentDir.exists() )
				parentDir.mkdir();
			dsgNcFile = new CruiseDsgNcFile(parentDir, expocode + ".nc");
			try {
				dsgNcFile.create(metadata, dataList);
			} catch ( IllegalArgumentException ex ) {
				dsgNcFile.delete();
			}
			assertFalse( dsgNcFile.exists() );
		}
	}
}
