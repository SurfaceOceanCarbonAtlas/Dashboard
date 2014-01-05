/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author Karl Smith
 */
public class CruiseFileHandlerTest {

	/**
	 * Test method mainly for {@link gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler#checkExpocode(java.lang.String)}
	 * by calling {@link gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler#cruiseDataFileExists(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testCruiseFileExists() throws IOException {
		CruiseFileHandler handler = 
				DashboardDataStore.get().getCruiseFileHandler();
		assertNotNull( handler );

		assertFalse( handler.cruiseDataFileExists("FAKE_EXPOCODE") );
		assertFalse( handler.cruiseDataFileExists("FAKE-EXPOCODE") );

		boolean exceptionMissed = false;
		try {
			handler.cruiseDataFileExists("TOOSHORT");
			exceptionMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Excepted outcome
			;
		}
		assertFalse( exceptionMissed );

		try {
			handler.cruiseDataFileExists("TOOLONGEXPOCODE");
			exceptionMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected outcome
			;
		}
		assertFalse( exceptionMissed );

		try {
			handler.cruiseDataFileExists("INVALID*CHAR");
			exceptionMissed = true;
		} catch ( IllegalArgumentException ex ) {
			// Expected outcome
			;
		}
		assertFalse( exceptionMissed );
	}

	/**
	 * Test method for methods in {@link gov.noaa.pmel.socat.dashboard.server.CruiseFileHandler}.
	 * @throws IOException 
	 */
	@Test
	public void testDashboardCruiseFileHandler() throws IOException {
		final String username = "socatuser";
		final String filename = "fake20031205_revised.tsv";
		final String expocode = "FAKE20031205";
		final String metadataString = 
				"Cruise Expocode: " + expocode + " \n" +
				"Cruise Name: FAKE0312 \n" +
				"Ship/Vessel Name: FakeShip \n" +
				"Principal Investigator(s): Fake Scientist \n" +
				" \n" +
				"Additional metadata reference(s): \n" +
				"    http://www.aoml.noaa.gov/ocd/gcc/skogafoss/socatmetadata/readme_312_608_FOR_SOCAT.doc \n" +
				" \n" +
				"Longitude range: 75.52W to 22.72W \n" +
				"Latitude range: 37.12N to 64.11N \n" +
				"Time range: 2003-12-05 22:12 to 2003-12-21 10:48 UTC \n" +
				" \n" +
				"Explanation of data columns: \n" +
				"Expocode: unique identifier for the cruise from which this data was obtained \n" +
				"SOCAT_DOI: DOI for this SOCAT-enhanced cruise data \n" +
				"QC_ID: Cruise QC flag ID (11 = A, 12 = B, 13 = C, 14 = D) \n" +
				"yr: 4-digit year of the time (UTC) of the measurement \n" +
				"mon: month of the time (UTC) of the measurement \n" +
				"day: day of the time (UTC) of the measurement \n" +
				"hh: hour of the time (UTC) of the measurement \n" +
				"mm: minute of the time (UTC) of the measurement \n" +
				"ss: second of the time (UTC) of the measurement (may include decimal places) \n" +
				"longitude: measurement longitude, from zero to 360, in decimal degrees East \n" +
				"latitude: measurement latitude in decimal degrees North \n" +
				"sample_depth: water sampling depth in meters \n" +
				"sal: measured sea surface salinity on the Practical Salinity Scale \n" +
				"SST: measured sea surface temperature in degrees Celcius \n" +
				"Tequ: equilibrator chamber temperature in degrees Celcius \n" +
				"PPPP: measured atmospheric pressure in hectopascals \n" +
				"Pequ: equilibrator chamber pressure in hectopascals \n" +
				"WOA_SSS: sea surface salinity on the Practical Salinity Scale interpolated from the \n" +
				"    World Ocean Atlas 2005 (see: //http://www.nodc.noaa.gov/OC5/WOA05/pr_woa05.html) \n" +
				"NCEP_SLP: sea level pressure in hectopascals interpolated from the NCEP/NCAR 40-Year \n" +
				"    Reanalysis Project (see: http://www.esrl.noaa.gov/psd/data/gridded/data.ncep.reanalysis.surface.html) \n" +
				"ETOPO2_depth: bathymetry in meters interpolated from the ETOPO2 2 arc-minute Gridded \n" +
				"    Global Relief Data (see: http://www.ngdc.noaa.gov/mgg/global/etopo2.html) \n" +
				"d2l: estimated distance to major land mass in kilometers (up to 1000 km) \n" +
				"GVCO2: atmospheric xCO2 in micromole per mole interpolated from GlobalView-CO2, 2012 \n" +
				"    1979-01-01 to 2012-01-01 data (see: http://www.esrl.noaa.gov/gmd/ccgg/globalview/index.html) \n" +
				"xCO2water_equ_dry: measured xCO2 (water) in micromole per mole at equilibrator temperature (dry air) \n" +
				"xCO2water_SST_dry: measured xCO2 (water) in micromole per mole at sea surface temperature (dry air) \n" +
				"pCO2water_equ_wet: measured pCO2 (water) in microatmospheres at equilibrator temperature (wet air) \n" +
				"pCO2water_SST_wet: measured pCO2 (water) in microatmospheres at sea surface temperature (wet air) \n" +
				"fCO2water_equ_wet: measured fCO2 (water) in microatmospheres at equilibrator temperature (wet air) \n" +
				"fCO2water_SST_wet: measured fCO2 (water) in microatmospheres at sea surface temperature (wet air) \n" +
				"fCO2rec: fCO2 in microatmospheres recomputed from the measured CO2 data (see below) \n" +
				"fCO2rec_src: algorithm for generating fCO2rec from the measured CO2 data (0:not generated; 1-14, see below) \n" +
				"fCO2rec_flag: WOCE flag for this fCO2rec value (2:good, 3:questionable, 4:bad, 9:not generated; see below) \n" +
				" \n" +
				"Missing values are indicated by 'NaN' \n" +
				" \n";
		ArrayList<String> expectedPreamble = 
				new ArrayList<String>(Arrays.asList(metadataString.split("\n")));
		final String headerString = 
				"Expocode	SOCAT_DOI	QC_ID	yr	mon	day	hh	mm	ss	longitude [dec.deg.E]	latitude [dec.deg.N]	sample_depth [m]	sal	SST [deg.C]	Tequ [deg.C]	PPPP [hPa]	Pequ [hPa]	WOA_SSS	NCEP_SLP [hPa]	ETOPO2_depth [m]	d2l [km]	GVCO2 [umol/mol]	xCO2water_equ_dry [umol/mol]	xCO2water_SST_dry [umol/mol]	pCO2water_equ_wet [uatm]	pCO2water_SST_wet [uatm]	fCO2water_equ_wet [uatm]	fCO2water_SST_wet [uatm]	fCO2rec [uatm]	fCO2rec_src	fCO2rec_flag \n";
		ArrayList<String> expectedHeaders = 
				new ArrayList<String>(Arrays.asList(headerString.trim().split("\t", -1)));
		ArrayList<DataColumnType> expectedColumnTypes = 
				new ArrayList<DataColumnType>(Arrays.asList(
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.YEAR,
						DataColumnType.MONTH,
						DataColumnType.DAY,
						DataColumnType.HOUR,
						DataColumnType.MINUTE,
						DataColumnType.SECOND,
						DataColumnType.LONGITUDE,
						DataColumnType.LATITUDE,
						DataColumnType.SAMPLE_DEPTH,
						DataColumnType.SALINITY,
						DataColumnType.SEA_SURFACE_TEMPERATURE,
						DataColumnType.EQUILIBRATOR_TEMPERATURE,
						DataColumnType.SEA_LEVEL_PRESSURE,
						DataColumnType.EQUILIBRATOR_PRESSURE,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.XCO2WATER_EQU,
						DataColumnType.XCO2WATER_SST,
						DataColumnType.PCO2WATER_EQU,
						DataColumnType.PCO2WATER_SST,
						DataColumnType.FCO2WATER_EQU,
						DataColumnType.FCO2WATER_SST,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN,
						DataColumnType.UNKNOWN
				));
		ArrayList<Integer> expectedQualities = new ArrayList<Integer>(expectedColumnTypes.size());
		for (int k = 0; k < expectedColumnTypes.size(); k++)
			expectedQualities.add(2);
		final String dataString = 
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	12	00.00	337.28101	64.10700	5.	26.910	5.410	5.700	NaN	1026.500	NaN	1022.900	39.	40.	380.341	373.740	NaN	NaN	NaN	NaN	369.260	369.196	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	18	00.00	337.23901	64.09700	5.	28.360	5.390	5.680	NaN	1026.100	NaN	1022.900	17.	42.	380.340	374.390	NaN	NaN	NaN	NaN	369.770	369.700	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	24	00.00	337.20499	64.08300	5.	28.700	5.440	5.730	NaN	1026.100	NaN	1022.900	31.	43.	380.340	374.510	NaN	NaN	NaN	NaN	369.860	369.809	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	30	00.00	337.17499	64.06900	5.	28.690	5.630	5.920	NaN	1025.800	NaN	1022.900	45.	43.	380.339	372.710	NaN	NaN	NaN	NaN	367.870	367.884	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	36	00.00	337.14499	64.05500	5.	28.750	5.710	6.000	NaN	1025.900	NaN	1022.900	51.	44.	380.339	370.480	NaN	NaN	NaN	NaN	365.660	365.702	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	42	00.00	337.11499	64.04000	5.	28.810	5.940	6.240	NaN	1025.700	NaN	1022.900	61.	45.	380.338	371.240	NaN	NaN	NaN	NaN	366.210	366.175	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	48	00.00	337.08499	64.02600	5.	28.630	6.290	6.590	NaN	1025.700	NaN	1022.900	79.	45.	380.338	370.930	NaN	NaN	NaN	NaN	365.710	365.793	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	22	54	00.00	337.05499	64.01200	5.	28.880	6.200	6.510	NaN	1025.600	NaN	1022.900	87.	46.	380.337	371.050	NaN	NaN	NaN	NaN	365.840	365.739	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	00	00.00	337.02399	63.99700	5.	28.890	6.310	6.610	NaN	1025.700	NaN	1022.900	99.	47.	380.337	370.960	NaN	NaN	NaN	NaN	365.730	365.819	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	06	00.00	336.99399	63.98200	5.	28.800	6.620	6.930	NaN	1025.600	35.254	1022.900	101.	47.	380.336	371.400	NaN	NaN	NaN	NaN	365.960	365.991	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	12	00.00	336.96301	63.96700	5.	28.640	6.660	6.980	NaN	1025.800	35.254	1022.900	101.	48.	380.336	372.050	NaN	NaN	NaN	NaN	366.640	366.537	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	22	00.00	336.91199	63.94600	5.	28.910	6.610	6.920	NaN	1026.000	35.254	1022.900	107.	50.	380.335	370.260	NaN	NaN	NaN	NaN	364.980	365.013	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	28	00.00	336.88101	63.93200	5.	28.910	6.590	6.900	NaN	1026.100	35.254	1022.900	111.	50.	380.334	369.310	NaN	NaN	NaN	NaN	364.090	364.117	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	34	00.00	336.84799	63.91800	5.	28.650	6.660	6.970	NaN	1026.100	35.254	1022.900	111.	51.	380.334	369.430	NaN	NaN	NaN	NaN	364.170	364.219	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	40	00.00	336.81699	63.90400	5.	28.730	6.960	7.280	NaN	1026.100	35.254	1022.900	111.	52.	380.333	374.780	NaN	NaN	NaN	NaN	369.270	369.267	1	2 \n" +
				"AGSK20031205	doi:10.1594/PANGAEA.814792	13	2003	12	05	23	46	00.00	336.78601	63.89000	5.	28.740	6.990	7.310	NaN	1026.000	35.254	1022.900	111.	53.	380.333	376.500	NaN	NaN	NaN	NaN	370.910	370.918	1	2 \n";
		String[] observations = dataString.split("\n");
		ArrayList<ArrayList<String>> expectedDatavals = 
				new ArrayList<ArrayList<String>>(observations.length);
		for ( String obs : observations )
			expectedDatavals.add(
					new ArrayList<String>(Arrays.asList(obs.trim().split("\t", -1))));

		CruiseFileHandler handler = 
				DashboardDataStore.get().getCruiseFileHandler();
		assertNotNull( handler );

		// Generate the cruise data from a BufferedReader wrapping the String data
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		cruiseData.setOwner(username);
		cruiseData.setUploadFilename(filename);
		BufferedReader reader = new BufferedReader(new StringReader(
				metadataString + headerString + dataString));
		try {
			handler.assignCruiseDataFromInput(cruiseData, 
					DashboardUtils.CRUISE_FORMAT_TAB, 
					reader, 0, observations.length, true);
		} finally {
			reader.close();
		}

		// Check for differences
		assertEquals(expocode, cruiseData.getExpocode());
		assertEquals(username, cruiseData.getOwner());
		assertEquals(filename, cruiseData.getUploadFilename());

		// These are checked item by item to make it easier to report differences
		ArrayList<DataColumnType> colTypes = cruiseData.getDataColTypes();
		for (int k = 0; (k < colTypes.size()) && (k < expectedColumnTypes.size()); k++)
			assertEquals(expectedColumnTypes.get(k), colTypes.get(k));
		assertEquals(expectedColumnTypes.size(), colTypes.size());

		ArrayList<String> preamble = cruiseData.getPreamble();
		for (int k = 0; (k < preamble.size()) && (k < expectedPreamble.size()); k++)
			assertEquals(expectedPreamble.get(k), preamble.get(k));
		assertEquals(expectedPreamble.size(), preamble.size());

		ArrayList<String> headers = cruiseData.getUserColNames();
		for (int k = 0; (k < headers.size()) && (k < expectedHeaders.size()); k++)
			assertEquals(expectedHeaders.get(k), headers.get(k));
		assertEquals(expectedHeaders.size(), headers.size());

		ArrayList<ArrayList<String>> datavals = cruiseData.getDataValues();
		for (int k = 0; (k < datavals.size()) && (k < expectedDatavals.size()); k++) {
			ArrayList<String> datalist = datavals.get(k);
			ArrayList<String> expectedDatalist = expectedDatavals.get(k);
			for (int j = 0; (j < datalist.size()) && (j < expectedDatalist.size()); j++)
				assertEquals(expectedDatalist.get(j), datalist.get(j));
			assertEquals(expectedDatalist.size(), datalist.size());
		}
		assertEquals(expectedDatavals.size(), datavals.size());

		// Save and commit the cruise data to file
		handler.saveCruiseInfoToFile(cruiseData, "test check-in of fake cruise info file");
		handler.saveCruiseDataToFile(cruiseData, "test check-in of fake cruise data file");

		// Test that the file exists
		assertTrue( handler.cruiseInfoFileExists(expocode) );
		assertTrue( handler.cruiseDataFileExists(expocode) );

		// Generate the cruise data from the saved file
		DashboardCruiseWithData fileData = handler.getCruiseDataFromFiles(expocode, 
															0, observations.length);

		// Check for differences - version string will differ
		assertEquals(username, fileData.getOwner());
		assertEquals(filename, fileData.getUploadFilename());
		assertEquals(expocode, fileData.getExpocode());

		preamble = fileData.getPreamble();
		for (int k = 0; (k < preamble.size()) && (k < expectedPreamble.size()); k++)
			assertEquals(expectedPreamble.get(k).trim(), preamble.get(k).trim());
		assertEquals(expectedPreamble.size(), preamble.size());

		headers = fileData.getUserColNames();
		for (int k = 0; (k < headers.size()) && (k < expectedHeaders.size()); k++)
			assertEquals(expectedHeaders.get(k), headers.get(k));
		assertEquals(expectedHeaders.size(), headers.size());

		assertEquals(expectedQualities, fileData.getDataColQualities());

		datavals = fileData.getDataValues();
		for (int k = 0; (k < datavals.size()) && (k < expectedDatavals.size()); k++) {
			ArrayList<String> datalist = datavals.get(k);
			ArrayList<String> expectedDatalist = expectedDatavals.get(k);
			for (int j = 0; (j < datalist.size()) && (j < expectedDatalist.size()); j++)
				assertEquals(expectedDatalist.get(j), datalist.get(j));
			assertEquals(expectedDatalist.size(), datalist.size());
		}
		assertEquals(expectedDatavals.size(), datavals.size());

		// Check the partial contents listing
		ArrayList<String> partialContents = handler.getPartialCruiseDataContents(cruiseData);
		// Metadata preamble
		int k = 0;
		for (int j = 0; j < expectedPreamble.size(); j++, k++)
			assertEquals(expectedPreamble.get(j), partialContents.get(k));
		// Column headers line
		assertEquals(headerString.trim(), partialContents.get(k).trim());
		k++;
		// Fewer than 25 data points, so they all should be in there
		for (int j = 0; j < observations.length; j++, k++)
			assertEquals(observations[j].trim(), partialContents.get(k).trim());
		// And that should be all there is
		assertEquals(k, partialContents.size());

		// Check createCruiseFromInfoFile
		DashboardCruise expectedCruise = new DashboardCruise();
		expectedCruise.setExpocode(expocode);
		expectedCruise.setOwner(username);
		expectedCruise.setUploadFilename(filename);
		expectedCruise.setNumDataRows(observations.length);
		expectedCruise.setUserColNames(expectedHeaders);
		expectedCruise.setDataColTypes(expectedColumnTypes);
		expectedCruise.setDataColUnits(fileData.getDataColUnits());
		expectedCruise.setDataColQualities(expectedQualities);
		DashboardCruise cruise = handler.getCruiseFromInfoFile(expocode);
		assertEquals(expectedCruise, cruise);
	}

}
