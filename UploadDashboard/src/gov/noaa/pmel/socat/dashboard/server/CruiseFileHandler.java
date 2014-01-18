/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socat.dashboard.shared.DataColumnType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles storage and retrieval of cruise data in files.
 * 
 * @author Karl Smith
 */
public class CruiseFileHandler extends VersionedFileHandler {

	private static final String CRUISE_DATA_FILENAME_EXTENSION = ".tsv";
	private static final String CRUISE_INFO_FILENAME_EXTENSION = ".properties";
	private static final String DATA_OWNER_ID = "dataowner";
	private static final String UPLOAD_FILENAME_ID = "uploadfilename";
	private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
	private static final String DATA_CHECK_STATUS_ID = "datacheckstatus";
	private static final String OME_FILENAME_ID = "omefilename";
	private static final String METADATA_FILENAMES_ID = "metadatafilenames";
	private static final String QC_STATUS_ID = "qcstatus";
	private static final String ARCHIVE_STATUS_ID = "archivestatus";
	private static final String CDIAC_DATE_ID = "cdiacdate";
	private static final String NUM_DATA_ROWS_ID = "numdatarows";
	private static final String DATA_COLUMN_TYPES_ID = "datacolumntypes";
	private static final String USER_COLUMN_NAMES_ID = "usercolumnnames";
	private static final String DATA_COLUMN_UNITS_ID = "datacolumnunits";
	private static final String MISSING_VALUES_ID = "missingvalues";
	private static final String DATA_COLUMN_QUALITIES_ID = "datacolumnqualities";

	private static final int MIN_NUM_DATA_COLUMNS = 6;
	private static final String MISSING_VALUE_STRING = "NaN";

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
	CruiseFileHandler(String cruiseFilesDirName, String svnUsername, 
						String svnPassword) throws IllegalArgumentException {
		super(cruiseFilesDirName, svnUsername, svnPassword);
	}

	/**
	 * Checks and standardized a given expocode.
	 * 
	 * @param expocode
	 * 		expocode to check
	 * @return
	 * 		standardized (uppercase) expocode
	 * @throws IllegalArgumentException
	 * 		if the expocode is unreasonable
	 * 		(invalid characters, too short, too long)
	 */
	public static String checkExpocode(String expocode) throws IllegalArgumentException {
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
		return upperExpo;
	}

	/**
	 * @param expocode
	 * 		cruise expocode to use
	 * @return
	 * 		the cruise information file associated with the cruise expocode
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseInfoFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = checkExpocode(expocode);
		// Get the name of the saved cruise properties file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_INFO_FILENAME_EXTENSION);
	}

	/**
	 * @param expocode
	 * 		cruise expocode to use
	 * @return
	 * 		the cruise data file associated with the cruise expocode
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseDataFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = checkExpocode(expocode);
		// Get the name of the saved cruise data file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_DATA_FILENAME_EXTENSION);
	}

	/**
	 * Determines if a cruise information file exists for a cruise
	 * @param expocode
	 * 		expcode of the cruise to check
	 * @return
	 * 		true if the cruise information file exists
	 * @throws IllegalArgumentException
	 * 		if expocode is an invalid cruise expocode 
	 */
	public boolean cruiseInfoFileExists(String expocode)
										throws IllegalArgumentException {
		File cruiseFile = cruiseInfoFile(expocode);
		return cruiseFile.exists();
	}

	/**
	 * Determines if a cruise data file exists for a cruise
	 * @param expocode
	 * 		expcode of the cruise to check
	 * @return
	 * 		true if the cruise data file exists
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
	 * given BufferedReader. 
	 * 
	 * The data read should have a preamble of metadata containing at least 
	 * the expocode on a line such as one of the following (case and space 
	 * insensitive):
	 * <pre>
	 * Expocode :
	 * Expocode = 
	 * Cruise Expocode :
	 * Cruise Expocode =
	 * </pre>
	 * The first line containing at least {@value #MIN_NUM_DATA_COLUMNS}
	 * tab- or comma-separated (depending on dataForm) non-blank values 
	 * will be taken to be the line of data column headers.  No data 
	 * column headers can be blank.  All remaining (non-blank) lines are 
	 * considered data lines and should have the same number of tab- or 
	 * comma-separated values as in the column headers line.  Any blank 
	 * data value strings, or data value strings matching "null", "NaN", 
	 * "NA", "N/A" (all case insensitive), are set to 
	 * {@value #MISSING_VALUE_STRING} to indicate a missing value.
	 * 
	 * @param cruiseData
	 * 		assign cruise data here
	 * @param dataFormat 
	 * 		format of the cruise data table; one of the 
	 * 		DashboardUtils.CRUISE_FORMAT_* strings
	 * @param cruiseReader
	 * 		read cruise data from here;
	 * 		if reading from a cruise data file, must be positioned
	 * 		after any cruise information read by 
	 * 		{@link #assignCruiseFromInput(DashboardCruise, BufferedReader)}
	 * @param firstDataRow
	 * 		index of the first data row to return; for all data for the
	 * 		cruise, specify zero
	 * @param numDataRows
	 * 		number of data rows to return; for all data for the cruise,
	 * 		specify -1 (or any negative integer)
	 * @param assignCruiseInfo
	 * 		assign values in the superclass DashboardCruise from the data?
	 * 		This includes the expocode, total number of rows of data, 
	 * 		and data column information.  If true, values are assigned from
	 * 		the data read (so firstDataRow should be zero, numDataRows should
	 * 		be -1).  If false, the expocode and number of data columns are
	 * 		validated against what is read from the data file.
	 * @throws IOException
	 * 		if reading from cruiseReader throws one,
	 * 		if there is a blank data column header, 
	 * 		if there is an inconsistent number of data values, 
	 * 		if there are no data columns recognized, or
	 * 		if the dataFormat string is not recognized. 
	 */
	public void assignCruiseDataFromInput(DashboardCruiseWithData cruiseData,
			String dataFormat, BufferedReader cruiseReader, int firstDataRow, 
			int numDataRows, boolean assignCruiseInfo) throws IOException {
		boolean expocodeFound = false;
		int numDataColumns = 0;

		String separator;
		if ( DashboardUtils.CRUISE_FORMAT_TAB.equals(dataFormat) )
			separator = "\t";
		else if ( DashboardUtils.CRUISE_FORMAT_COMMA.equals(dataFormat) )
			separator = ",";
		else
			throw new IOException(
					"Unexpected invalid data format '" + dataFormat + "'");
		// Directly add the metadata strings to the list in cruiseData
		ArrayList<String> preamble = cruiseData.getPreamble();
		preamble.clear();

		// Read the metadata preamble
		String dataline = cruiseReader.readLine();
		while ( dataline != null ) {
			// Check if we have gotten to non-blank header values 
			String[] datavals = dataline.split(separator, -1);
			if ( (   datavals.length >= MIN_NUM_DATA_COLUMNS ) &&
				 ( ! datavals[0].trim().isEmpty() ) &&
				 ( ! datavals[1].trim().isEmpty() ) &&
				 ( ! datavals[2].trim().isEmpty() ) &&
				 ( ! datavals[3].trim().isEmpty() ) &&
				 ( ! datavals[4].trim().isEmpty() ) &&
				 ( ! datavals[5].trim().isEmpty() ) ) {
				// These are the column headers;
				// clean them up and make sure there are no blank values
				for (int k = 0; k < datavals.length; k++) {
					datavals[k] = datavals[k].trim();
					if ( datavals[k].isEmpty() )
						throw new IOException("Data column header " + 
												(k+1) + " is blank");
				}
				numDataColumns = datavals.length;
				if ( assignCruiseInfo ) {
					// Just directly add the column names to the list in cruiseData
					ArrayList<String> colNames = cruiseData.getUserColNames();
					colNames.clear();
					colNames.addAll(Arrays.asList(datavals));
					// Just directly add the default column quality of 2 ("okay")
					// to the list in cruiseData
					ArrayList<Integer> colQualities = cruiseData.getDataColQualities();
					for (int k = 0; k < numDataColumns; k++)
						colQualities.add(2);
				}
				else if ( cruiseData.getUserColNames().size() != numDataColumns ) {
					throw new IOException("Unexpected number of data columns (" + 
							numDataColumns + " instead of " + 
							cruiseData.getUserColNames().size()  + ")");
				}
				else if ( cruiseData.getDataColQualities().size() != numDataColumns ) {
					throw new IOException("Unexpected number of data column qualities (" + 
							numDataColumns + " instead of " + 
							cruiseData.getDataColQualities().size()  + ")");
				}
				// Treat the rest of the lines as tab-separated data value lines
				break;
			}
			if ( ! dataline.trim().isEmpty() ) {
				if ( ! expocodeFound ) {
					// Check if this is an expocode identification line
					for ( Pattern pat : expocodePatterns ) {
						Matcher mat = pat.matcher(dataline);
						if ( mat.matches() ) {
							String expocode = mat.group(1).toUpperCase();
							if ( assignCruiseInfo ) {
								// Get the expocode from this line
								cruiseData.setExpocode(expocode);
							}
							else if ( ! cruiseData.getExpocode().equals(expocode) ) {
								throw new IOException("Unexpected expocode (" +
										expocode + " instead of " +
										cruiseData.getExpocode());
							}
							expocodeFound = true;
							break;
						}
					}
				}
			}

			// Still a metadata preamble line; save it and read the next line
			preamble.add(dataline);
			dataline = cruiseReader.readLine();
		}

		if ( numDataColumns < MIN_NUM_DATA_COLUMNS )
			throw new IOException(
					"No data columns found, possibly due to incorrect format");

		if ( assignCruiseInfo ) {
			// Assign the data column types and units from the data column names
			try {
				DashboardDataStore.get().getUserFileHandler()
										.assignDataColumnTypes(cruiseData);
			} catch ( IOException ex ) {
				throw new IOException(
						"Unexpected failure to get the user file handler");
			}
		}

		// Read the tab-separated column values
		// Just directly add them to the list in cruiseData
		ArrayList<ArrayList<String>> dataValues = cruiseData.getDataValues();
		dataValues.clear();
		if ( numDataRows == 0 ) {
			if ( assignCruiseInfo )
				cruiseData.setNumDataRows(0);
			return;
		}
		int dataRowNum = 0;
		dataline = cruiseReader.readLine();
		while ( dataline != null ) {
			// Ignore blank lines
			if ( ! dataline.trim().isEmpty() ) {
				if ( dataRowNum >= firstDataRow ) {
					// Get the values from this data line
					String[] datavals = dataline.split(separator, -1);
					if ( datavals.length != numDataColumns )
						throw new IOException("Inconsistent number of data columns (" + 
								datavals.length + " instead of " + numDataColumns + 
								") in \n" + dataline);
					// Convert an obvious missing values to MISSING_VALUE_STRING
					for (int k = 0; k < datavals.length; k++) {
						datavals[k] = datavals[k].trim();
						if ( datavals[k].isEmpty() ||
							 datavals[k].toLowerCase().equals("null") ||
							 datavals[k].toLowerCase().equals("nan") ||
							 datavals[k].toLowerCase().equals("n/a") ||
							 datavals[k].toLowerCase().equals("na") ) {
							datavals[k] = MISSING_VALUE_STRING;
						}
					}
					dataValues.add(new ArrayList<String>(Arrays.asList(datavals)));
					if ( (numDataRows > 0) && (dataValues.size() == numDataRows) )
						break;
				}
				dataRowNum++;
			}
			// Read the next line
			dataline = cruiseReader.readLine();
		}
		if ( assignCruiseInfo )
			cruiseData.setNumDataRows(dataValues.size());
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
		for ( String name : cruiseData.getUserColNames() ) {
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
	 * @param firstDataRow
	 * 		index of the first data row to return; for all data for the
	 * 		cruise, specify zero
	 * @param numDataRows
	 * 		number of data rows to return; for all data for the cruise,
	 * 		specify -1 (or any negative integer)
	 * @return
	 * 		the saved cruise data, 
	 * 		or null if there is no information or data saved for this cruise.
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or if there was a error reading 
	 * 		information or data for this cruise
	 */
	public DashboardCruiseWithData getCruiseDataFromFiles(String expocode,
			int firstDataRow, int numDataRows) throws IllegalArgumentException {
		// Create the cruise and assign the expocode
		DashboardCruiseWithData cruiseData = new DashboardCruiseWithData();
		cruiseData.setExpocode(expocode);
		try {
			// Assign values from the cruise information file
			assignCruiseFromInfoFile(cruiseData);
		} catch ( FileNotFoundException ex ) {
			return null;
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems reading cruise information for " + expocode + 
					": " + ex.getMessage());
		}
		// Read the cruise data file
		File cruiseFile = cruiseDataFile(expocode);
		try {
			BufferedReader cruiseReader = 
					new BufferedReader(new FileReader(cruiseFile));
			try {
				// Assign values from the cruise data file
				assignCruiseDataFromInput(cruiseData, 
						DashboardUtils.CRUISE_FORMAT_TAB, cruiseReader, 
						firstDataRow, numDataRows, false);
			} finally {
				cruiseReader.close();
			}
		} catch ( FileNotFoundException ex ) {
			return null;
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems reading cruise data for " + expocode + 
					": " + ex.getMessage());
		}
		return cruiseData;
	}

	/**
	 * Returns a new DashboardCruise assigned from the cruise information
	 * file without reading any of the data in cruise data file.
	 * 
	 * @param expocode
	 * 		cruise whose information file to examine
	 * @return
	 * 		new DashboardCruise assigned from the information file,
	 * 		or null if the cruise information file does not exist
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid or
	 * 		if there are problems accessing the information file
	 */
	public DashboardCruise getCruiseFromInfoFile(String expocode) 
									throws IllegalArgumentException {
		// Create a cruise for this data and assign the expocode given
		DashboardCruise cruise = new DashboardCruise();
		cruise.setExpocode(expocode);
		// Read the information saved cruise information file
		try {
			assignCruiseFromInfoFile(cruise);
		} catch ( FileNotFoundException ex ) {
			return null;
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems reading cruise information for " + 
							expocode + ": " + ex.getMessage());
		}
		return cruise;
	}

	/**
	 * Deletes the information and data files for a cruise 
	 * after verifying the user is permitted to delete this cruise.
	 * 
	 * @param expocode
	 * 		cruise to delete
	 * @param username
	 * 		user wanting to delete the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is not valid, if there were
	 * 		problems access the cruise, if the user is not permitted 
	 * 		to delete the cruise, or if there were problems deleting
	 * 		a file or committing the deletion from version control
	 * @throws FileNotFoundException
	 * 		if the cruise information file does not exist
	 */
	public void deleteCruiseFiles(String expocode, String username) 
				throws IllegalArgumentException, FileNotFoundException {
		DashboardCruise cruise;
		try {
			cruise = verifyOkayToDeleteCruise(expocode, username);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(
					"Not permitted to delete cruise " + expocode +
					": " + ex.getMessage());
		}
		// Delete the cruise data file
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
		// Delete the cruise information file
		try {
			deleteVersionedFile(cruiseInfoFile(expocode), 
					"Cruise information file for " + expocode + 
					" owned by " + cruise.getOwner() + 
					" deleted by " + username);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems deleting the cruise information file: " + 
					ex.getMessage());
		}
		// Delete the metadata documents associated with this cruise
		MetadataFileHandler metadataHandler;
		try {
			metadataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to obtain the metadata file handler");
		}
		String omeFilename = cruise.getOmeFilename();
		if ( ! omeFilename.isEmpty() )
			metadataHandler.removeMetadata(username, expocode, cruise.getOmeFilename());
		for ( String mdataName : cruise.getMetadataFilenames() )
			metadataHandler.removeMetadata(username, expocode, mdataName);
	}

	/**
	 * Saves and commits only the cruise information to the information file.
	 * This does not save the cruise data of a DashboardCruiseWithData.
	 * This first checks the currently saved properties for the cruise, and 
	 * writes and commits a new properties file only if there are changes.
	 * 
	 * @param cruise
	 * 		cruise information to save
	 * @param message
	 * 		version control commit message; 
	 * 		if null or blank, the commit will not be performed 
	 * @throws IllegalArgumentException
	 * 		if the expocode for the cruise is invalid, if there was an 
	 * 		error writing information for this cruise to file, or if there 
	 * 		was an error committing the updated file to version control
	 */
	public void saveCruiseInfoToFile(DashboardCruise cruise, String message)
										throws IllegalArgumentException {
		// Get the cruise information filename
		String expocode = cruise.getExpocode();
		File infoFile = cruiseInfoFile(expocode);
		// First check if there are any changes from what is saved to file
		try {
			DashboardCruise savedCruise = getCruiseFromInfoFile(expocode);
			if ( (savedCruise != null) && savedCruise.equals(cruise) )
				return;
		} catch ( IllegalArgumentException ex ) {
			// Some problem with the saved data
			;
		}
		// Create the NODC subdirectory if it does not exist
		File parentFile = infoFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		// Create the properties for this cruise information file
		Properties cruiseProps = new Properties();
		// Owner of the cruise
		cruiseProps.setProperty(DATA_OWNER_ID, cruise.getOwner());
		// Upload filename
		cruiseProps.setProperty(UPLOAD_FILENAME_ID, cruise.getUploadFilename());
		// Upload timestamp
		cruiseProps.setProperty(UPLOAD_TIMESTAMP_ID, cruise.getUploadTimestamp());
		// Data-check status string
		cruiseProps.setProperty(DATA_CHECK_STATUS_ID, cruise.getDataCheckStatus());
		// OME metadata filename
		cruiseProps.setProperty(OME_FILENAME_ID, cruise.getOmeFilename());
		// Metadata documents
		// a little arguably-unnecessary overhead going through an ArrayList<String>
		cruiseProps.setProperty(METADATA_FILENAMES_ID, 
				DashboardUtils.encodeStringArrayList(new ArrayList<String>(
						cruise.getMetadataFilenames())));
		// QC-submission status string
		cruiseProps.setProperty(QC_STATUS_ID, cruise.getQcStatus());
		// Archive status string
		cruiseProps.setProperty(ARCHIVE_STATUS_ID, cruise.getArchiveStatus());
		// Date of request to archive original data and metadata files with CDIAC
		cruiseProps.setProperty(CDIAC_DATE_ID, cruise.getCdiacDate());
		// Total number of data measurements (rows of data)
		cruiseProps.setProperty(NUM_DATA_ROWS_ID, 
				Integer.toString(cruise.getNumDataRows()));
		// Data column types - encoded using the enumerated names
		ArrayList<String> colTypeNames = 
				new ArrayList<String>(cruise.getDataColTypes().size());
		for ( DataColumnType colType : cruise.getDataColTypes() )
			colTypeNames.add(colType.name());
		cruiseProps.setProperty(DATA_COLUMN_TYPES_ID, 
				DashboardUtils.encodeStringArrayList(colTypeNames));
		// Data column name in the original upload data file
		cruiseProps.setProperty(USER_COLUMN_NAMES_ID, 
				DashboardUtils.encodeStringArrayList(cruise.getUserColNames()));
		// Unit for each data column
		cruiseProps.setProperty(DATA_COLUMN_UNITS_ID, 
				DashboardUtils.encodeStringArrayList(cruise.getDataColUnits()));
		// Missing value for each data column
		cruiseProps.setProperty(MISSING_VALUES_ID, 
				DashboardUtils.encodeStringArrayList(cruise.getMissingValues()));
		// Qualities of each data column
		cruiseProps.setProperty(DATA_COLUMN_QUALITIES_ID, 
				DashboardUtils.encodeIntegerArrayList(cruise.getDataColQualities()));
		// Save the properties to the cruise information file
		try {
			PrintWriter cruiseWriter = new PrintWriter(infoFile);
			try {
				cruiseProps.store(cruiseWriter, null);
			} finally {
				cruiseWriter.close();
			}
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems writing cruise information for " + expocode + 
					" to " + infoFile.getPath() + ": " + ex.getMessage());
		}
		
		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated information file to version control
		try {
			commitVersion(infoFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated cruise information for  " + 
							expocode + ": " + ex.getMessage());
		}
	}

	/**
	 * Saves and commits the cruise the cruise data to data file.
	 * The cruise information file needs to be saved using 
	 * {@link #saveCruiseInfoToFile(DashboardCruise, String)}.
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
		// Get the cruise data filename
		String expocode = cruiseData.getExpocode();
		File dataFile = cruiseDataFile(expocode);
		// Create the NODC subdirectory if it does not exist 
		// Should be the same as the info file, but just in case not
		File parentFile = dataFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		// Print the cruise data to the data file
		try {
			PrintWriter writer = new PrintWriter(dataFile);
			try {
				// Print the normal data file contents to the cruise data file
				// The saved metadata preamble
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
				// The data column headers
				String dataline = "";
				boolean first = true;
				for ( String name : cruiseData.getUserColNames() ) {
					if ( ! first )
						dataline += "\t";
					else
						first = false;
					dataline += name;
				}
				writer.println(dataline);
				// The data measurements (rows of data)
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
					"Problems writing cruise data for " + expocode + 
					" to " + dataFile.getPath() + ": " + ex.getMessage());
		}

		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated data file to version control
		try {
			commitVersion(dataFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated cruise data for " + 
							expocode + ": " + ex.getMessage());
		}
	}

	/**
	 * Assigns a DashboardCruise from the cruise information file.  
	 * The expocode of the cruise is obtained from the cruise object. 
	 * 
	 * @param cruise
	 * 		assign cruise information to this cruise object 
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid, or if the properties
	 * 		file is invalid
	 * @throws FileNotFoundException
	 * 		if the cruise information file does not exist
	 * @throws IOException
	 * 		if there are problems reading the properties given in
	 * 		the cruise information file
	 */
	private void assignCruiseFromInfoFile(DashboardCruise cruise) 
			throws IllegalArgumentException, FileNotFoundException, IOException {
		// Get the cruise information file
		File infoFile = cruiseInfoFile(cruise.getExpocode());
		// Get the properties given in this file
		Properties cruiseProps = new Properties();
		FileReader infoReader = new FileReader(infoFile);
		try {
			cruiseProps.load(infoReader);
		} finally {
			infoReader.close();
		}

		// Assign the DashboardCruise from the values in the properties file

		// Owner of the data file
		String value = cruiseProps.getProperty(DATA_OWNER_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_OWNER_ID + " given in " + infoFile.getPath());
		cruise.setOwner(value);

		// Name of uploaded file
		value = cruiseProps.getProperty(UPLOAD_FILENAME_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					UPLOAD_FILENAME_ID + " given in " + infoFile.getPath());			
		cruise.setUploadFilename(value);

		// Time of uploading the file
		value = cruiseProps.getProperty(UPLOAD_TIMESTAMP_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					UPLOAD_TIMESTAMP_ID + " given in " + infoFile.getPath());			
		cruise.setUploadTimestamp(value);

		// Data check status
		value = cruiseProps.getProperty(DATA_CHECK_STATUS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_CHECK_STATUS_ID + " given in " + infoFile.getPath());			
		cruise.setDataCheckStatus(value);

		// OME metadata filename
		value = cruiseProps.getProperty(OME_FILENAME_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					OME_FILENAME_ID + " given in " + infoFile.getPath());			
		cruise.setOmeFilename(value);

		// Metadata documents
		value = cruiseProps.getProperty(METADATA_FILENAMES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					METADATA_FILENAMES_ID + " given in " + infoFile.getPath());			
		// a little arguably-unnecessary overhead going through an ArrayList<String>
		cruise.setMetadataFilenames(new TreeSet<String>(
				DashboardUtils.decodeStringArrayList(value)));

		// QC status
		value = cruiseProps.getProperty(QC_STATUS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					QC_STATUS_ID + " given in " + infoFile.getPath());			
		cruise.setQcStatus(value);

		// Archive status
		value = cruiseProps.getProperty(ARCHIVE_STATUS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					ARCHIVE_STATUS_ID + " given in " + infoFile.getPath());			
		cruise.setArchiveStatus(value);

		// Date of request to archive original data and metadata with CDIAC
		value = cruiseProps.getProperty(CDIAC_DATE_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					CDIAC_DATE_ID + " given in " + infoFile.getPath());			
		cruise.setCdiacDate(value);

		// Number of rows of data (number of samples)
		value = cruiseProps.getProperty(NUM_DATA_ROWS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					NUM_DATA_ROWS_ID + " given in " + infoFile.getPath());
		try {
			cruise.setNumDataRows(Integer.parseInt(value));
		} catch ( NumberFormatException ex ) {
			throw new IllegalArgumentException(ex);
		}

		// Data column types - encoded using the enumerated names
		value = cruiseProps.getProperty(DATA_COLUMN_TYPES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_COLUMN_TYPES_ID + " given in " + infoFile.getPath());
		ArrayList<String> colTypeNames = DashboardUtils.decodeStringArrayList(value);
		// Assign the column types directly to the array in the cruise 
		ArrayList<DataColumnType> colTypes = cruise.getDataColTypes();
		colTypes.clear();
		for ( String name : colTypeNames )
			colTypes.add(DataColumnType.valueOf(name));

		// Data column name in the original upload data file
		value = cruiseProps.getProperty(USER_COLUMN_NAMES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					USER_COLUMN_NAMES_ID + " given in " + infoFile.getPath());
		cruise.setUserColNames(DashboardUtils.decodeStringArrayList(value));
		if ( cruise.getUserColNames().size() != colTypes.size() )
			throw new IllegalArgumentException(
					"number of user column names different from " +
					"number of data column types");

		// Unit for each data column
		value = cruiseProps.getProperty(DATA_COLUMN_UNITS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_COLUMN_UNITS_ID + " given in " + infoFile.getPath());
		cruise.setDataColUnits(DashboardUtils.decodeStringArrayList(value));
		if ( cruise.getDataColUnits().size() != colTypes.size() )
			throw new IllegalArgumentException(
					"number of data column units different from " +
					"number of data column types");

		// Missing valeues for each data column
		value = cruiseProps.getProperty(MISSING_VALUES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					MISSING_VALUES_ID + " given in " + infoFile.getPath());
		cruise.setMissingValues(DashboardUtils.decodeStringArrayList(value));
		if ( cruise.getMissingValues().size() != colTypes.size() )
			throw new IllegalArgumentException(
					"number of data column missing-value values different from " +
					"number of data column types");

		// Quality of each data column
		value = cruiseProps.getProperty(DATA_COLUMN_QUALITIES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_COLUMN_QUALITIES_ID + " given in " + infoFile.getPath());
		try {
			cruise.setDataColQualities(DashboardUtils.decodeIntegerArrayList(value));
		} catch ( NumberFormatException ex ) {
			throw new IllegalArgumentException(ex);
		}
		if ( cruise.getDataColQualities().size() != colTypes.size() )
			throw new IllegalArgumentException(
					"number of data column qualities different from " +
					"number of data column types");
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
	 * 		if there is no cruise information file for this cruise
	 */
	public DashboardCruise verifyOkayToDeleteCruise(String expocode, String username) 
				throws IllegalArgumentException, FileNotFoundException {
		// Get the cruise information from the data file
		DashboardCruise cruise = getCruiseFromInfoFile(expocode);
		// Check if the cruise is in a submitted or accepted state
		String status = cruise.getQcStatus();
		if ( ! ( status.equals(DashboardUtils.QC_STATUS_NOT_SUBMITTED) || 
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
