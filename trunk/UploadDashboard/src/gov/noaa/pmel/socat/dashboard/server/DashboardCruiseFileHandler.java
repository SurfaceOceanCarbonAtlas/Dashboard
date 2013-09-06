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

	private static final String USERNAME_ID = "username=";
	private static final String FILENAME_ID = "filename=";
	
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
				Pattern.compile("\\s*Cruise\\s*Expocode\\s*:\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Expocode\\s*:\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Cruise\\s*Expocode\\s*=\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*Expocode\\s*=\\s*([A-Z0-9]+)\\s*", 
						Pattern.CASE_INSENSITIVE)
		};
		createdPatterns = new Pattern[] {
				Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+dashboard\\s+cruise\\s+file\\s+created",
						Pattern.CASE_INSENSITIVE),
				Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+cruise\\s+file\\s+created",
						Pattern.CASE_INSENSITIVE)
		};
		invalidExpocodePattern= Pattern.compile("[^A-Z0-9_-]");
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
	 * Determines if a cruise file exists for a cruise
	 * @param expocode
	 * 		expcode of the cruise to check
	 * @return
	 * 		true if the cruise file exists
	 * @throws IllegalArgumentException
	 * 		if expocode is an invalid cruise expocode 
	 */
	public boolean cruiseFileExists(String expocode) throws IllegalArgumentException {
		File cruiseFile = cruiseDataFile(expocode);
		// Make sure we at the latest version
		try {
			updateVersion(cruiseFile.getParentFile());
		} catch ( SVNException ex ) {
			// May not exist or maybe not yet under version control  
			;
		}
		return cruiseFile.exists();
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
	 * @param username
	 * 		username to associate with this data
	 * @param filename
	 * 		filename to asscicate with this data
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
	public DashboardCruiseData getCruiseDataFromInput(String username, 
			String filename, BufferedReader cruiseReader) throws IOException {
		DashboardCruiseData cruiseData = new DashboardCruiseData();
		cruiseData.setUsername(username);
		cruiseData.setFilename(filename);

		boolean creationDateFound = false;
		boolean expocodeFound = false;
		int numDataColumns = 0;

		// Directly add the metadata strings to the list in cruiseData
		ArrayList<String> preamble = cruiseData.getPreamble();

		// Read the metadata preamble
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
			if ( ! dataline.trim().isEmpty() ) {
				if ( ! creationDateFound ) {
					// Check if this is a creation date line
					for ( Pattern pat : createdPatterns ) {
						Matcher mat = pat.matcher(dataline);
						if ( mat.lookingAt() ) {
							// Save this creation date line as the cruise version
							cruiseData.setVersion(dataline.trim());
							creationDateFound = true;
							break;
						}
					}
					if ( creationDateFound ) {
						// go on to the next line
						dataline = cruiseReader.readLine();
						continue;
					}
				}
				if ( ! expocodeFound ) {
					// Check if this is an expocode identification line
					for ( Pattern pat : expocodePatterns ) {
						Matcher mat = pat.matcher(dataline);
						if ( mat.matches() ) {
							// Get the expocode from this line
							cruiseData.setExpocode(mat.group(1).toUpperCase());
							expocodeFound = true;
							break;
						}
					}
					if ( expocodeFound ) {
						// go on to the next line
						dataline = cruiseReader.readLine();
						continue;
					}
				}
			}

			// Still a metadata preamble line; store it
			preamble.add(dataline);
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
	 * Creates and returns a list of strings representing the metadata
	 * preamble and the first 25 lines of data.
	 * 
	 * This method is in this class instead of DashboardCruiseData
	 * to reduce payload since it is only used on the server side.
	 * 
	 * @param cruiseData
	 * 		cruise to use
	 * @return
	 * 		partial contents of the cruise file
	 */
	public ArrayList<String> getPartialCruiseDataContents(
										DashboardCruiseData cruiseData) {
		ArrayList<String> partialContents =
				new ArrayList<String>(cruiseData.getPreamble().size() + 30);
		String version = cruiseData.getVersion();
		if ( ! version.trim().isEmpty() )
			partialContents.add(version);
		String expocode = cruiseData.getExpocode();
		if ( ! expocode.trim().isEmpty() )
			partialContents.add("Cruise Expocode: " + expocode);
		// Add all the preamble contents, check if the last line was blank
		boolean lastLineBlank = false;
		for ( String dataline : cruiseData.getPreamble() ) {
			partialContents.add(dataline);
			if ( dataline.trim().isEmpty() )
				lastLineBlank = true;
			else
				lastLineBlank = false;
		}
		if ( ! lastLineBlank )
			partialContents.add("");
	
		// Add the data column headers line
		String dataline = "";
		boolean first = true;
		for ( String name : cruiseData.getColumnNames() ) {
			if ( ! first )
				dataline += "\t";
			else
				first = false;
			dataline += name;
		}
		partialContents.add(dataline);

		// Add up to 25 rows of data
		int k = 0;
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
			partialContents.add(dataline);
			k++;
			if ( k >= 25 )
				break;
		}
		return partialContents;
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
			updateVersion(cruiseFile.getParentFile());
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
				// Get the username for this cruise
				String dataline = cruiseReader.readLine();
				if ( ! dataline.startsWith(USERNAME_ID) )
					throw new IOException("first line does not start with " + USERNAME_ID);
				String username = dataline.substring(USERNAME_ID.length()).trim();
				// Get the filename for this cruise
				dataline = cruiseReader.readLine();
				if ( ! dataline.startsWith(FILENAME_ID) )
					throw new IOException("second line does not start with " + FILENAME_ID);
				String filename = dataline.substring(FILENAME_ID.length()).trim();
				// Create the cruise
				cruiseData = getCruiseDataFromInput(username, filename, cruiseReader);
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
	 * @param message
	 * 		version control commit message; 
	 * 		if null or blank, the commit will not be performed 
	 * @throws IllegalArgumentException
	 * 		if the expocode for the cruise is invalid, or
	 * 		if there was an error writing data for this cruise to file
	 * @throws SVNException
	 * 		if there was an error committing the updated file 
	 * 		to version control
	 */
	public void saveCruiseDataToFile(DashboardCruiseData cruiseData, 
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
				writer.println(USERNAME_ID + cruiseData.getUsername());
				writer.println(FILENAME_ID + cruiseData.getFilename());
				// Print the standard creation date and expocode headers
				writer.println("SOCAT version " + 
						DashboardDataStore.get().getSocatVersion() + 
						" dashboard cruise file created: " + datestamp);
				writer.println("Cruise Expocode: " + expocode);
				// Print the saved metadata preamble
				boolean lastLineBlank = false;
				for ( String metaline : cruiseData.getPreamble() ) {
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

		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated file to version control
		commitVersion(cruiseFile, message);
	}

}
