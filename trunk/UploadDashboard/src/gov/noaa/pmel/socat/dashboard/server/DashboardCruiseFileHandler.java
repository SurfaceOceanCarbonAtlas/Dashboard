/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

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

/**
 * Handles storage and retrieval of cruise data in files.
 * 
 * @author Karl Smith
 */
public class DashboardCruiseFileHandler extends VersionedFileHandler {

	private static final String CRUISE_FILE_NAME_EXTENSION = ".tsv";
	private static final String DATA_OWNER_ID = "dataowner=";
	private static final String UPLOAD_FILENAME_ID = "uploadfilename=";
	private static final String DATA_CHECK_STATUS_ID = "datacheckstatus=";
	private static final String META_CHECK_STATUS_ID = "metadatacheckstatus=";
	private static final String QC_STATUS_ID = "qcstatus=";
	private static final String ARCHIVE_STATUS_ID = "archivestatus=";

	// Patterns for getting the expocode from the metadata header
	private static final Pattern[] expocodePatterns = new Pattern[] {
		Pattern.compile("\\s*Cruise\\s*Expocode\\s*:\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)\\s*", 
				Pattern.CASE_INSENSITIVE),
		Pattern.compile("\\s*Expocode\\s*:\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)\\s*", 
				Pattern.CASE_INSENSITIVE),
		Pattern.compile("\\s*Cruise\\s*Expocode\\s*=\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)\\s*", 
				Pattern.CASE_INSENSITIVE),
		Pattern.compile("\\s*Expocode\\s*=\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)\\s*", 
				Pattern.CASE_INSENSITIVE)
	};
	// Patterns for file creation date in the metadata header
	private static final Pattern[] createdPatterns = new Pattern[] {
		Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+dashboard\\s+" +
				"cruise\\s+file\\s+created", Pattern.CASE_INSENSITIVE),
		Pattern.compile("\\s*SOCAT\\s+version\\s+\\S+\\s+" +
				"cruise\\s+file\\s+created", Pattern.CASE_INSENSITIVE)
	};
	// Pattern for checking for invalid characters in the expocode
	private static final Pattern invalidExpocodePattern = 
			Pattern.compile("[^" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]");

	/**
	 * Handles storage and retrieval of cruise data in files 
	 * under the given cruise files directory.
	 * 
	 * @param cruiseFilesDirName
	 * 		name of the cruise files directory
	 * @param svnUsername
	 * 		username for SVN authentication
	 * @param svnPassword
	 * 		password for SVN authentication
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, or is not under SVN 
	 * 		version control
	 */
	DashboardCruiseFileHandler(String cruiseFilesDirName, String svnUsername, 
						String svnPassword) throws IllegalArgumentException {
		super(cruiseFilesDirName, svnUsername, svnPassword);
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
		if ( (upperExpo.length() < DashboardUtils.MIN_EXPOCODE_LENGTH) || 
			 (upperExpo.length() > DashboardUtils.MAX_EXPOCODE_LENGTH) )
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
	public boolean cruiseDataFileExists(String expocode) 
										throws IllegalArgumentException {
		File cruiseFile = cruiseDataFile(expocode);
		return cruiseFile.exists();
	}

	/**
	 * Assigns a DashboardCruiseWithData from data read from the 
	 * given BufferedReader.  Only the expocode field of the superclass 
	 * DashboardCruise is assigned by this method.
	 * 
	 * Blank lines in the file are ignored.  The data read can (should) 
	 * have a preamble of metadata containing at least the expocode on 
	 * a line such as one of the following (case and space insensitive):
	 * <pre>
	 * Expocode :
	 * Expocode = 
	 * Cruise Expocode :
	 * Cruise Expocode =
	 * </pre>
	 * The first line containing at least four tab-separated values
	 * with be taken to be the line of data column headers.  No data 
	 * column headers can be blank.  All remaining (non-blank) lines 
	 * are considered data lines and should have the same number of 
	 * tab-separated values as there are column header.  Any blank 
	 * data value strings, or data value strings matching "null" or 
	 * "NaN" (case insensitive), are set to "NaN" to indicate a missing 
	 * value.
	 * 
	 * @param cruiseData
	 * 		assign cruise data here
	 * @param cruiseReader
	 * 		read cruise data from here;
	 * 		if reading from a cruise data file, must be positioned
	 * 		after any cruise information read by 
	 * 		{@link #assignCruiseFromInput(DashboardCruise, BufferedReader)}
	 * @throws IOException
	 * 		if reading from cruiseReader throws one,
	 * 		if there is a blank data column header, or
	 * 		if there is an inconsistent number of tab-separated values
	 */
	public void assignCruiseDataFromInput(DashboardCruiseWithData cruiseData,
						BufferedReader cruiseReader) throws IOException {
		boolean creationDateFound = false;
		boolean expocodeFound = false;
		int numDataColumns = 0;

		// Directly add the metadata strings to the list in cruiseData
		ArrayList<String> preamble = cruiseData.getPreamble();
		preamble.clear();

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
				colNames.clear();
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

			// Still a metadata preamble line; save it and read the next line
			preamble.add(dataline);
			dataline = cruiseReader.readLine();
		}

		// Read the tab-separated column values
		// Just directly add them to the list in cruiseData
		ArrayList<ArrayList<String>> dataValues = cruiseData.getDataValues();
		dataValues.clear();
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
	}

	/**
	 * Creates and returns a list of strings representing the metadata
	 * preamble and the first 25 lines of data.
	 * 
	 * This method is in this class instead of DashboardCruiseWithData
	 * to reduce payload since it is only used on the server side.
	 * 
	 * @param cruiseData
	 * 		cruise to use
	 * @return
	 * 		partial contents of the cruise file
	 */
	public ArrayList<String> getPartialCruiseDataContents(
									DashboardCruiseWithData cruiseData) {
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
	 * Get cruise data saved to file
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
	public DashboardCruiseWithData getCruiseDataFromFile(String expocode) 
										throws IllegalArgumentException {
		File cruiseFile = cruiseDataFile(expocode);
		// Read the cruise data from the saved cruise data file
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		try {
			BufferedReader cruiseReader = 
					new BufferedReader(new FileReader(cruiseFile));
			try {
				// Assign the cruise information at the start of the file
				assignCruiseFromInput(cruiseData, cruiseReader);
				// Assign the cruise data in the rest of the file
				assignCruiseDataFromInput(cruiseData, cruiseReader);
				// Make sure the expocode in the file is the one requested
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
	 * Returns a new DashboardCruise assigned from the header lines 
	 * in the cruise data file, without reading any of the data in 
	 * the file.
	 * 
	 * @param expocode
	 * 		cruise whose data file to examine
	 * @return
	 * 		new DashboardCruise assigned from the data file
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or
	 * 		if there are problems accessing the data file
	 * @throws FileNotFoundException
	 * 		if the cruise data file does not exist
	 */
	public DashboardCruise getCruiseFromDataFile(String expocode) 
				throws IllegalArgumentException, FileNotFoundException {
		File cruiseFile = cruiseDataFile(expocode);
		// Create a cruise for this data and assign the expocode given
		DashboardCruise cruise = new DashboardCruise();
		cruise.setExpocode(expocode);
		// Read the information saved in the first few lines 
		// of the cruise data file without reading all the data
		BufferedReader cruiseReader = 
				new BufferedReader(new FileReader(cruiseFile));
		try {
			assignCruiseFromInput(cruise, cruiseReader);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems accessing the data for cruise " + expocode + 
					": " + ex.getMessage());
		} finally {
			try {
				cruiseReader.close();
			} catch (IOException ex) {
				// don't care
				;
			}
		}
		return cruise;
	}

	/**
	 * Deletes a cruise data file after verifying the user is permitted
	 * to delete this cruise.
	 * 
	 * @param expocode
	 * 		cruise to delete
	 * @param username
	 * 		user wanting to delete the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is not valid, if there were
	 * 		problems access the cruise, if the user is not permitted 
	 * 		to delete the cruise, or if there were problems deleting
	 * 		the file or committing the deletion from version control
	 * @throws FileNotFoundException
	 * 		if the cruise file does not exist
	 */
	public void deleteCruiseDataFile(String expocode, String username) 
				throws IllegalArgumentException, FileNotFoundException {
		DashboardCruise cruise;
		try {
			cruise = verifyOkayToDeleteCruise(expocode, username);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(
					"Not permitted to delete cruise " + expocode +
					": " + ex.getMessage());
		}
		// Delete the cruise file
		try {
			deleteVersionedFile(cruiseDataFile(expocode), 
					"Cruise data file for " + expocode + 
					" owned by " + cruise.getOwner() + 
					" deleted by " + username);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems deleting the cruise data file: " + 
					ex.getMessage());
		}
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
	 * 		if the expocode for the cruise is invalid, if there was 
	 * 		an error writing data for this cruise to file, or if there 
	 * 		was an error committing the updated file to version control
	 */
	public void saveCruiseDataToFile(DashboardCruiseWithData cruiseData, 
						String message) throws IllegalArgumentException {
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
				// First write the CruiseData values
				writer.println(DATA_OWNER_ID + cruiseData.getOwner());
				writer.println(UPLOAD_FILENAME_ID + cruiseData.getUploadFilename());
				writer.println(DATA_CHECK_STATUS_ID + cruiseData.getDataCheckStatus());
				writer.println(META_CHECK_STATUS_ID + cruiseData.getMetadataCheckStatus());
				writer.println(QC_STATUS_ID + cruiseData.getQCStatus());
				writer.println(ARCHIVE_STATUS_ID + cruiseData.getArchiveStatus());
				// Print the standard creation date and expocode header lines
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
		try {
			commitVersion(cruiseFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated data for cruise " + expocode +
					": " + ex.getMessage());
		}
	}

	/**
	 * Assigns a DashboardCruise with data from the first few lines 
	 * read from an input reader.
	 * 
	 * @param cruise
	 * 		assign cruise information to this cruise object
	 * @param cruiseReader
	 * 		read cruise information from here;
	 * 		must be positioned at the start of the cruise data file 
	 * @throws IOException
	 * 		if reading data from the reader throws one or
	 * 		if the expected first few lines with cruise information
	 * 		are not found
	 */
	private void assignCruiseFromInput(DashboardCruise cruise, 
						BufferedReader cruiseReader) throws IOException {
		// Get the username for this cruise
		String dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(DATA_OWNER_ID) )
			throw new IOException(
					"first line does not start with " + DATA_OWNER_ID);
		cruise.setOwner(dataline.substring(DATA_OWNER_ID.length()).trim());

		// Get the filename for this cruise
		dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(UPLOAD_FILENAME_ID) )
			throw new IOException(
					"second line does not start with " + UPLOAD_FILENAME_ID);
		cruise.setUploadFilename(
				dataline.substring(UPLOAD_FILENAME_ID.length()).trim());

		// Get the data check status for this cruise
		dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(DATA_CHECK_STATUS_ID) )
			throw new IOException(
					"third line does not start with " + DATA_CHECK_STATUS_ID);
		cruise.setDataCheckStatus(
				dataline.substring(DATA_CHECK_STATUS_ID.length()).trim());

		// Get the metadata check status for this cruise
		dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(META_CHECK_STATUS_ID) )
			throw new IOException(
					"fourth line does not start with " + META_CHECK_STATUS_ID);
		cruise.setMetadataCheckStatus(
				dataline.substring(META_CHECK_STATUS_ID.length()).trim());

		// Get the QC status for this cruise
		dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(QC_STATUS_ID) )
			throw new IOException(
					"fifth line does not start with " + QC_STATUS_ID);
		cruise.setQCStatus(dataline.substring(QC_STATUS_ID.length()).trim());

		// Get the archive status for this cruise
		dataline = cruiseReader.readLine();
		if ( ! dataline.startsWith(ARCHIVE_STATUS_ID) )
			throw new IOException(
					"sixth line does not start with " + ARCHIVE_STATUS_ID);
		cruise.setArchiveStatus(
				dataline.substring(ARCHIVE_STATUS_ID.length()).trim());
	}

	/**
	 * Verify a user can overwrite or delete a cruise.  This checks
	 * the submission state of the cruise as well as ownership of the
	 * cruise.  If not permitted, an IllegalArgumentException is
	 * thrown with reason for the failure.
	 * 
	 * @param expocode
	 * 		cruise to check
	 * @param username
	 * 		user wanting to overwrite or delete the cruise
	 * @return 
	 * 		the cruise being overwritten or deleted; never null
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid, 
	 * 		if there are problems accessing the cruise data file
	 * @throws FileNotFoundException
	 * 		if there is no cruise data file for this cruise
	 */
	public DashboardCruise verifyOkayToDeleteCruise(String expocode, String username) 
				throws IllegalArgumentException, FileNotFoundException {
		// Get the cruise information from the data file
		DashboardCruise cruise = getCruiseFromDataFile(expocode);
		// Check if the cruise is in a submitted or accepted state
		String status = cruise.getQCStatus();
		if ( ! ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
				 status.equals(DashboardUtils.QC_STATUS_AUTOFAIL) ||
				 status.equals(DashboardUtils.QC_STATUS_UNACCEPTABLE) ||
				 status.equals(DashboardUtils.QC_STATUS_SUSPENDED) ||
				 status.equals(DashboardUtils.QC_STATUS_EXCLUDED) ) )
			throw new IllegalArgumentException("cruise status is " + status);
		// Check if the user has permission to delete the cruise
		try {
			String owner = cruise.getOwner();
			if ( ! DashboardDataStore.get().userManagesOver(username, owner) )
				throw new IllegalArgumentException("cruise owner is " + owner);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the user file handler");
		}
		return cruise;
	}

}
