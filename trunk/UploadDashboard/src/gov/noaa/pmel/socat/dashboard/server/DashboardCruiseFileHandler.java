/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseData;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles storage and retrieval of cruise data in files.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseFileHandler extends VersionedFileHandler {

	/**
	 * Handles storage and retrieval of cruise data in files 
	 * under the given cruise files directory.
	 * 
	 * @param cruiseFilesDirName
	 * 		name of the cruise files directory
	 * @throws IllegalArgumentException
	 * 		if the directory does not exist
	 */
	DashboardCruiseFileHandler(String cruiseFilesDirName) 
									throws IllegalArgumentException {
		super(cruiseFilesDirName);
	}

	/**
	 * Creates and returns a DashboardCruiseData instance
	 * initialized  with data read from the given BufferedReader.
	 * 
	 * Blank lines in the file are ignored.
	 * The data read can (should) have a preamble of metadata
	 * containing at least the expocode on a line such as one
	 * of the following (case and space insensitive):
	 * <pre>
	 * Expocode :
	 * Expocode = 
	 * Cruise Expocode :
	 * Cruise Expocode =
	 * </pre>
	 * The first line containing at least five tab-separated values
	 * with be taken to be the line of data column headers.  No data 
	 * column header can be blank.  All remaining (non-blank) lines 
	 * are considered data lines and should have the same number of 
	 * tab-separated values.  Any blank data value strings, or data 
	 * value strings matching "null" or "NaN" (case insensitive), 
	 * are set the null to indicate a missing value.
	 * 
	 * @param cruiseReader
	 * 		read data from here
	 * @throws IOException
	 * 		if reading from cruiseReader throws one,
	 * 		if there is a blank data column header,
	 * 		or if there is an inconsistent number tab-separated values
	 */
	DashboardCruiseData createCruiseDataFromInput(BufferedReader cruiseReader) 
														throws IOException {
		DashboardCruiseData cruiseData = new DashboardCruiseData();

		// Read the metadata preamble
		// Just add them to the list in cruiseData
		ArrayList<String> preamble = cruiseData.getPreamble();
		boolean expocodeSet = false;
		int numDataColumns = 0;
		String dataline = cruiseReader.readLine();
		while ( dataline != null ) {
			// Check if we have gotten to tab-separated data values 
			String[] datavals = dataline.split("\t");
			if ( datavals.length > 4 ) {
				// These are the column headers;
				// clean them up and make sure not blank headers
				for (int k = 0; k < datavals.length; k++) {
					datavals[k] = datavals[k].trim();
					if ( datavals[k].isEmpty() )
						throw new IOException("Data column header " + 
								(k+1) + "is blank");
				}
				cruiseData.setColumnNames(datavals);
				numDataColumns = datavals.length;
				// Treat the rest of the lines as tab-separated data value lines
				break;
			}
			// Still in the preamble
			dataline = dataline.trim();
			// Ignore blank lines
			if ( ! dataline.isEmpty() ) {
				// Save the preamble line
				preamble.add(dataline);
				// If no expocode found yet, check for the expocode
				if ( ! expocodeSet ) {
					// Expocodes should always use upper-case letters
					dataline = dataline.toUpperCase();
					if ( dataline.matches("^EXPOCODE\\s*:") ||
						 dataline.matches("^CRUISE\\s*EXPOCODE\\s*:")) {
						cruiseData.setExpocode(dataline.substring(
								dataline.indexOf(":")).trim());
						expocodeSet = true;
					}
					else if ( dataline.matches("^EXPOCODE\\s*=") ||
							  dataline.matches("^CRUISE\\s*EXPOCODE\\s*=")) {
						cruiseData.setExpocode(dataline.substring(
								dataline.indexOf("=")).trim());
						expocodeSet = true;
					}
				}
			}
			// Read the next line
			dataline = cruiseReader.readLine();
		}

		// Read the tab-separated column headers
		// Just add them to the list in cruiseData
		ArrayList<String[]> dataValues = cruiseData.getDataValues();
		dataline = cruiseReader.readLine();
		while ( dataline != null ) {
			// Ignore blank lines
			if ( ! dataline.trim().isEmpty() ) {
				// Get the values from this data line
				String[] datavals = dataline.split("\t");
				if ( datavals.length != numDataColumns )
					throw new IOException("Inconsistent number of data columns (" + 
							datavals.length + " instead of " + numDataColumns + 
							") in \n" + dataline);
				// Convert missing values to null
				for (int k = 0; k < datavals.length; k++) {
					datavals[k] = datavals[k].trim();
					if ( datavals[k].isEmpty() ||
						 datavals[k].toLowerCase().equals("null") ||
						 datavals[k].toLowerCase().equals("nan") ) {
						datavals[k] = null;
					}
				}
				dataValues.add(datavals);
			}
			// Read the next line
			dataline = cruiseReader.readLine();
		}
		return cruiseData;
	}

	DashboardCruiseData getCruiseDataFile(String expocode) 
										throws IllegalArgumentException {
		// TODO:
		return null;
	}

	void saveCruiseData(DashboardCruiseData cruiseData) 
										throws IllegalArgumentException {
		// TODO:
	}

}
