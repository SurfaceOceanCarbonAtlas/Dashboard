/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of cruise data in files.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseFileHandler extends VersionedFileHandler {

	private static final String CRUISE_FILE_NAME_EXTENSION = ".tsv";
	
	private Pattern[] expocodePatterns;
	private Pattern[] createdPatterns;
	private Pattern invalidExpocodePattern;

	/**
	 * Handles storage and retrieval of cruise data in files 
	 * under the given cruise files directory.
	 * 
	 * @param cruiseFilesDirName
	 * 		name of the cruise files directory
	 * @throws SVNException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, or is not under SVN 
	 * 		version control
	 */
	DashboardCruiseFileHandler(String cruiseFilesDirName) throws SVNException {
		super(cruiseFilesDirName);
		expocodePatterns = new Pattern[] {
				Pattern.compile("\\s*Expocode\\s*:\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Cruise\\s*Expocode\\s*:\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Expocode\\s*=\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Cruise\\s*Expocode\\s*=\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE)
		};
		createdPatterns = new Pattern[] {
				Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+cruise\\s+file\\s+created",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+dashboard\\s+cruise\\s+file\\s+created",
						Pattern.CASE_INSENSITIVE)
		};
		invalidExpocodePattern= Pattern.compile("[^A-Z0-9]");
	}

	/**
	 * @param expocode
	 * 		cruise expocode to use
	 * @return
	 * 		the cruise data file associated with the cruise expcode
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseDataFile(String expocode) throws IllegalArgumentException {
		if ( expocode == null )
			throw new IllegalArgumentException("Cruise expocode not given");
		// Do some automatic clean-up
		String upperExpo = expocode.trim().toUpperCase();
		// Make sure it is the proper length
		if ( (upperExpo.length() < 12) || (upperExpo.length() > 14) )
			throw new IllegalArgumentException(
					"Invalid cruise Expocode length");
		// Make sure there are no invalid characters
		Matcher mat = invalidExpocodePattern.matcher(upperExpo);
		if ( mat.find() )
			throw new IllegalArgumentException(
					"Invalid characters in the cruise Expocode");
		// Get the name of the saved cruise data file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_FILE_NAME_EXTENSION);
	}

	/**
	 * Creates and returns a DashboardCruiseData instance
	 * initialized with data read from the given BufferedReader.
	 * 
	 * This method is in this class instead of DashboardCruiseData
	 * because the latter is in the shared package, and this method
	 * can only be run on the server.
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
	 * @return
	 * 		DashboardCruiseData instance; never null but may not be
	 * 		complete
	 * @throws IOException
	 * 		if reading from cruiseReader throws one,
	 * 		if there is a blank data column header,
	 * 		or if there is an inconsistent number tab-separated values
	 */
	public DashboardCruiseData getCruiseDataFromInput(BufferedReader cruiseReader) 
														throws IOException {
		DashboardCruiseData cruiseData = new DashboardCruiseData();

		// Read the metadata preamble
		// Just directly add the metadata strings to the list in cruiseData
		ArrayList<String> preamble = cruiseData.getPreamble();
		boolean expocodeSet = false;
		int numDataColumns = 0;
		String dataline = cruiseReader.readLine();
		while ( dataline != null ) {
			// Check if we have gotten to tab-separated data values 
			String[] datavals = dataline.split("\t");
			if ( datavals.length > 3 ) {
				// These are the column headers;
				// clean them up and make sure there are no blank values
				for (int k = 0; k < datavals.length; k++) {
					datavals[k] = datavals[k].trim();
					if ( datavals[k].isEmpty() )
						throw new IOException("Data column header " + 
								(k+1) + " is blank");
				}
				// Just directly add the column names to the list in cruiseData
				ArrayList<String> colNames = cruiseData.getColumnNames();
				colNames.addAll(Arrays.asList(datavals));
				numDataColumns = datavals.length;
				// Treat the rest of the lines as tab-separated data value lines
				break;
			}
			// Still in the preamble - save the line
			preamble.add(dataline);
			// If no expocode found yet, check for the expocode
			if ( ! ( dataline.trim().isEmpty() || expocodeSet ) ) {
				for ( Pattern pat : expocodePatterns ) {
					Matcher mat = pat.matcher(dataline);
					if ( mat.matches() ) {
						cruiseData.setExpocode(mat.group(1).toUpperCase());
						expocodeSet = true;
					}
				}
			}
			// Read the next line
			dataline = cruiseReader.readLine();
		}

		// Read the tab-separated column values
		// Just directly add them to the list in cruiseData
		ArrayList<ArrayList<String>> dataValues = cruiseData.getDataValues();
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
				// Convert missing values to NaN
				for (int k = 0; k < datavals.length; k++) {
					datavals[k] = datavals[k].trim();
					if ( datavals[k].isEmpty() ||
						 datavals[k].toLowerCase().equals("null") ||
						 datavals[k].toLowerCase().equals("nan") ) {
						datavals[k] = "NaN";
					}
				}
				dataValues.add(new ArrayList<String>(Arrays.asList(datavals)));
			}
			// Read the next line
			dataline = cruiseReader.readLine();
		}
		return cruiseData;
	}

	/**
	 * Get cruise data saved from a previous session
	 * 
	 * @param expocode
	 * 		expocode of the cruise to get data for
	 * @return
	 * 		the saved cruise data, which may be null if there is no data 
	 * 		saved for this cruise.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or if there was a error reading 
	 * 		data for this cruise
	 */
	public DashboardCruiseData getCruiseDataFromFile(String expocode) 
										throws IllegalArgumentException {
		File cruiseFile = cruiseDataFile(expocode);
		// Make sure we read the latest version
		try {
			updateVersion(cruiseFile);
		} catch ( SVNException ex ) {
			// May not exist or maybe not yet under version control  
			;
		}
		// Read the cruise data from the saved cruise data file
		DashboardCruiseData cruiseData;
		try {
			BufferedReader cruiseReader = 
					new BufferedReader(new FileReader(cruiseFile));
			try {
				cruiseData = getCruiseDataFromInput(cruiseReader);
				if ( ! expocode.equals(cruiseData.getExpocode()) )
					throw new IllegalArgumentException(
							"unexpected expocode associated with saved cruise data");
			} finally {
				cruiseReader.close();
			}
		} catch ( FileNotFoundException ex ) {
			cruiseData = null;
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems reading data for cruise " + expocode + 
					" from " + cruiseFile.getPath() + ": " + ex.getMessage());
		}
		return cruiseData;
	}

	/**
	 * Saves and commits the cruise data to file.
	 * 
	 * @param cruiseData
	 * 		cruise data to save
	 * @param username
	 * 		user instigating this update
	 * @param message
	 * 		additional commit message
	 * @throws IllegalArgumentException
	 * 		if the expocode for the cruise is invalid, or
	 * 		if there was an error writing data for this cruise to file
	 * @throws SVNException
	 * 		if there was an error committing the updated file to version control
	 */
	public void saveCruiseDataToFile(DashboardCruiseData cruiseData, String username,
					String message) throws IllegalArgumentException, SVNException {
		// Get the update date for the cruise data file
		String datestamp = (new Timestamp(System.currentTimeMillis()))
							.toString().substring(0, 10);
		// Get the cruise file name
		String expocode = cruiseData.getExpocode();
		File cruiseFile = cruiseDataFile(expocode);
		File parentFile = cruiseFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		try {
			PrintWriter writer = new PrintWriter(cruiseFile);
			try {
				// Print the standard creation date and expocode headers
				writer.println("SOCAT version " + DashboardDataStore.get().getSocatVersion() + 
						" dashboard cruise file created: " + datestamp);
				writer.println("Cruise Expocode: " + expocode);
				// Print the headers, except for any creation data or expocode headers
				boolean lastLineBlank = false;
				for ( String metaline : cruiseData.getPreamble() ) {
					boolean found = false;
					for ( Pattern pat : createdPatterns ) {
						Matcher mat = pat.matcher(metaline);
						if ( mat.lookingAt() ) {
							found = true;
							break;
						}
					}
					if ( found )
						continue;
					for ( Pattern pat : expocodePatterns ) {
						Matcher mat = pat.matcher(metaline);
						if ( mat.matches() ) {
							found = true;
							break;
						}
					}
					if ( found )
						continue;
					writer.println(metaline);
					if ( metaline.trim().isEmpty() )
						lastLineBlank = true;
					else
						lastLineBlank = false;
				}
				if ( ! lastLineBlank )
					writer.println();
				// Print the data column headers
				String dataline = "";
				boolean first = true;
				for ( String name : cruiseData.getColumnNames() ) {
					if ( ! first )
						dataline += "\t";
					else
						first = false;
					dataline += name;
				}
				writer.println(dataline);
				// Print the rows of data
				for ( ArrayList<String> datarow : cruiseData.getDataValues() ) {
					dataline = "";
					first = true;
					for ( String datum : datarow ) {
						if ( ! first )
							dataline += "\t";
						else
							first = false;
						dataline += datum;
					}
					writer.println(dataline);
				}
			} finally {
				writer.close();
			}
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems writing data for cruise " + expocode + 
					" to " + cruiseFile.getPath() + ": " + ex.getMessage());
		}
		// Submit the updated file to version control
		commitVersion(cruiseFile, username, message);
	}

}
