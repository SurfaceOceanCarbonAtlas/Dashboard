/**
 * 
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.server.KnownDataTypes;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.WoceType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of cruise data in files.
 * 
 * @author Karl Smith
 */
public class CruiseFileHandler extends VersionedFileHandler {

	private static final String CRUISE_DATA_FILENAME_EXTENSION = ".tsv";
	private static final String CRUISE_INFO_FILENAME_EXTENSION = ".properties";
	private static final String DATA_OWNER_ID = "dataowner";
	private static final String VERSION_ID = "version";
	private static final String UPLOAD_FILENAME_ID = "uploadfilename";
	private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
	private static final String ORIG_DOI_ID = "origdatadoi";
	private static final String ENHANCED_DOI_ID = "enhanceddatadoi";
	private static final String DATA_CHECK_STATUS_ID = "datacheckstatus";
	private static final String OME_TIMESTAMP_ID = "ometimestamp";
	private static final String ADDL_DOC_TITLES_ID = "addldoctitles";
	private static final String QC_STATUS_ID = "qcstatus";
	private static final String ARCHIVE_STATUS_ID = "archivestatus";
	private static final String ARCHIVAL_DATE_ID = "archivaldate";
	private static final String NUM_DATA_ROWS_ID = "numdatarows";
	private static final String NUM_ERROR_ROWS_ID = "numerrrows";
	private static final String NUM_WARN_ROWS_ID = "numwarnrows";
	private static final String DATA_COLUMN_TYPES_ID = "datacolumntypes";
	private static final String USER_COLUMN_NAMES_ID = "usercolumnnames";
	private static final String DATA_COLUMN_UNITS_ID = "datacolumnunits";
	private static final String MISSING_VALUES_ID = "missingvalues";
	private static final String CHECKER_WOCE_THREES = "checkerwocethrees";
	private static final String CHECKER_WOCE_FOURS = "checkerwocefours";
	private static final String USER_WOCE_THREES = "userwocethrees";
	private static final String USER_WOCE_FOURS = "userwocefours";

	private static final int MIN_NUM_DATA_COLUMNS = 6;

	// Patterns for getting the expocode from the metadata preamble
	private static final Pattern[] EXPOCODE_PATTERNS = new Pattern[] {
		Pattern.compile("Cruise\\s*Expocode\\s*[=:]\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)", 
				Pattern.CASE_INSENSITIVE),
		Pattern.compile("Expocode\\s*[=:]\\s*([" + 
				DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+)", 
				Pattern.CASE_INSENSITIVE)
	};

	private KnownDataTypes userTypes;

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
	 * @param userTypes
	 * 		known user data column types
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, or is not under SVN 
	 * 		version control
	 */
	public CruiseFileHandler(String cruiseFilesDirName, String svnUsername, 
			String svnPassword, KnownDataTypes userTypes) throws IllegalArgumentException {
		super(cruiseFilesDirName, svnUsername, svnPassword);
		this.userTypes = userTypes;
	}

	/**
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		the cruise information file associated with the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	private File cruiseInfoFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Get the name of the cruise properties file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_INFO_FILENAME_EXTENSION);
	}

	/**
	 * @param expocode
	 * 		expocode of the cruise
	 * @return
	 * 		the cruise data file associated with the cruise
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is invalid
	 */
	File cruiseDataFile(String expocode) throws IllegalArgumentException {
		// Check that the expocode is somewhat reasonable
		String upperExpo = DashboardServerUtils.checkExpocode(expocode);
		// Get the name of the saved cruise data file
		return new File(filesDir, upperExpo.substring(0,4) + 
				File.separatorChar + upperExpo + CRUISE_DATA_FILENAME_EXTENSION);
	}

	/**
	 * Searches all existing cruises and returns the expocodes of those that
	 * match the given expocode with wildcards and/or regular expressions.
	 * 
	 * @param wildExpocode
	 * 		expocode, possibly with wildcards * and ?, to use;
	 * 		any characters are converted to uppercase,
	 * 		"*" is turned in the regular expression "[(expocodechars)]+", and
	 * 		"?" is turned in the regular expression "[(expocodechars)]{1}", where
	 * 		(expocodechars) are all valid expocode characters
	 * @return
	 * 		list of expocodes of existing cruises that match 
	 * 		the given wildcard expocode; never null, but may be empty
	 * @throws IllegalArgumentException
	 * 		if the given expocode is invalid
	 */
	public HashSet<String> getMatchingExpocodes(String wildExpocode) 
											throws IllegalArgumentException {
		HashSet<String> matchingExpocodes = new HashSet<String>();
		final Pattern filenamePattern;
		try {
			String filenameRegEx = wildExpocode.toUpperCase();
			filenameRegEx = filenameRegEx.replace("*", 
					"[" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]+");
			filenameRegEx = filenameRegEx.replace("?", 
					"[" + DashboardUtils.VALID_EXPOCODE_CHARACTERS + "]{1}");
			filenameRegEx += CRUISE_INFO_FILENAME_EXTENSION;
			filenamePattern = Pattern.compile(filenameRegEx);
		} catch (PatternSyntaxException ex) {
			throw new IllegalArgumentException(ex);
		}
		File[] subDirs = filesDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if ( pathname.isDirectory() )
					return true;
				return false;
			}
		});
		for ( File subDir : subDirs ) {
			File[] matchFiles = subDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if ( filenamePattern.matcher(name).matches() )
						return true;
					return false;
				}
			});
			for ( File match : matchFiles ) {
				String name = match.getName();
				String expocode = name.substring(0, name.length() - 
						CRUISE_INFO_FILENAME_EXTENSION.length());
				matchingExpocodes.add(expocode);
			}
		}
		return matchingExpocodes;
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
	 * Alternatively, the expocode can be given in an expocode data column.
	 * In this case, only the first expocode in the column is used.
	 * 
	 * The first line containing at least {@value #MIN_NUM_DATA_COLUMNS}
	 * tab- or comma-separated (depending on dataForm) non-blank values 
	 * will be taken to be the line of data column headers.  No data 
	 * column headers can be blank.  All remaining (non-blank) lines are 
	 * considered data lines and should have the same number of tab- or 
	 * comma-separated values as in the column headers line.  Any blank 
	 * data value strings, or data value strings matching "null", "NaN", 
	 * "NA", "N/A" (all case insensitive), are set to 
	 * {@value #MISSING_VALUE_STRING} to indicate a missing value.
	 * Note: blank lines are not detected when skipping initial data lines
	 * (when firstDataRow > 0).
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
	 * 		if there are no data columns recognized, 
	 * 		if the dataFormat string is not recognized, or
	 * 		if different expocodes are given in the header and in a column 
	 */
	public void assignCruiseDataFromInput(DashboardCruiseWithData cruiseData,
			String dataFormat, BufferedReader cruiseReader, int firstDataRow, 
			int numDataRows, boolean assignCruiseInfo) throws IOException {
		CSVFormat format = CSVFormat.EXCEL.withIgnoreSurroundingSpaces().withIgnoreEmptyLines();
		String spacer;
		if ( DashboardUtils.CRUISE_FORMAT_TAB.equals(dataFormat) ) {
			format = format.withDelimiter('\t');
			spacer = " ";
		}
		else if ( DashboardUtils.CRUISE_FORMAT_COMMA.equals(dataFormat) ) {
			format = format.withDelimiter(',');
			spacer = ",";
		}
		else if ( DashboardUtils.CRUISE_FORMAT_SEMICOLON.equals(dataFormat) ) {
			format = format.withDelimiter(';');
			spacer = ";";
		}
		else
			throw new IOException("Unexpected invalid data format '" + dataFormat + "'");

		// metadata strings in cruise data
		ArrayList<String> preamble = new ArrayList<String>();
		// data row numbers
		ArrayList<Integer> rowNums = new ArrayList<Integer>();
		// data values in cruise data
		ArrayList<ArrayList<String>> dataValues = new ArrayList<ArrayList<String>>();

		boolean expocodeFound = false;
		int numDataColumns = 0;
		int dataRowNum = 0;
		boolean checkForUnits = false;
		
		CSVParser dataParser = new CSVParser(cruiseReader, format);
		try {
			for ( CSVRecord record : dataParser ) {

				// Still looking for headers?
				if ( (numDataColumns == 0) && (record.size() >= MIN_NUM_DATA_COLUMNS) ) {
					// These could be the column headers
					// Check for empty entries or numeric entries
					// Headers must be given and non-numeric
					boolean isHeader = true;
					for ( String val : record ) {
						if ( val.isEmpty() ) {
							isHeader = false;
							break;
						}
						try {
							Double.parseDouble(val);
							// Numeric - appears to be data
							isHeader = false;
							break;
						} catch (Exception ex) {
							// Expected result
							;
						}
					}
					if ( isHeader ) {
						// These indeed are the column headers
						if ( assignCruiseInfo ) {
							// column names in cruise data
							ArrayList<String> colNames = new ArrayList<String>();
							for ( String val : record ) {
								colNames.add(val);
							}
							cruiseData.setUserColNames(colNames);
						}
						numDataColumns = record.size();
						// Check for units in the next record
						checkForUnits = true;
						continue;
					}
				}

				// Still reading metadata?
				if ( numDataColumns == 0 ) {
					// Put this line of metadata back together using spacer
					String metaline = rebuildDataline(record, spacer);

					// Examine this metadata line for the expocode
					if ( ! ( metaline.isEmpty() || expocodeFound ) ) {
						// Check if this is an expocode identification line
						for ( Pattern pat : EXPOCODE_PATTERNS ) {
							Matcher mat = pat.matcher(metaline);
							if ( ! mat.matches() )
								continue;
							String expocode = mat.group(1).toUpperCase();
							if ( expocode == null )
								continue;
							if ( assignCruiseInfo ) {
								cruiseData.setExpocode(expocode);
							}
							else if ( ! expocode.equals(cruiseData.getExpocode()) ) {
								throw new IOException("Unexpected expocode (" + expocode + 
										" instead of " + cruiseData.getExpocode());
							}
							expocodeFound = true;
							break;
						}
					}

					// Save this metadata line in the preamble for the cruise
					preamble.add(metaline);
					continue;
				}

				if ( record.size() != numDataColumns )
					throw new IOException("Inconsistent number of data columns (" + 
							record.size() + " instead of " + numDataColumns + 
							") for measurement " + dataParser.getRecordNumber() + ":\n    " +
							rebuildDataline(record, spacer));

				// Checking for second header with units?
				if ( checkForUnits ) {
					boolean isUnits = true;
					// A unit specification cannot be pure numeric
					for ( String val : record ) {
						try {
							Double.valueOf(val);
							isUnits = false;
							break;
						} catch (NumberFormatException ex) {
							continue;
						}
					}
					if ( assignCruiseInfo ) {
						if ( isUnits ) {
							// Add the units to the column header names 
							ArrayList<String> colNames = cruiseData.getUserColNames();
							ArrayList<String> colNamesWithUnits = new ArrayList<String>(colNames.size());
							for (int k = 0; k < colNames.size(); k++) {
								String units = record.get(k);
								if ( ! units.isEmpty() )
									colNamesWithUnits.add(colNames.get(k) + " [" + units + "]");
								else
									colNamesWithUnits.add(colNames.get(k));
							}
							cruiseData.setUserColNames(colNamesWithUnits);
						}
						// Assign the data column types, units, and missing values 
						// from the data column names, possibly with user-provided units
						DashboardConfigStore configStore;
						try {
							configStore = DashboardConfigStore.get(false);
						} catch ( IOException ex ) {
							throw new IOException(
									"Unexpected failure to get the dashboard configuration");
						}
						configStore.getUserFileHandler().assignDataColumnTypes(cruiseData);
						cruiseData.setVersion(configStore.getUploadVersion());
					}
					// Rest of the records must be data
					checkForUnits = false;
					if ( isUnits )
						continue;
				}

				// Must be a data record

				if ( numDataRows == 0 ) {
					// No reading of the data requested - done
					cruiseData.setPreamble(preamble);
					cruiseData.setRowNums(null);
					cruiseData.setDataValues(null);
					if ( assignCruiseInfo )
						cruiseData.setNumDataRows(0);
					return;
				}

				dataRowNum++;
				// "No-data" lines are NOT detected when skipping lines,
				// but lines are skipped only with already-processed data,
				// so "no-data" lines should already have been eliminated.
				if ( dataRowNum > firstDataRow ) {
					ArrayList<String> datavals = new ArrayList<String>(numDataColumns);
					boolean allBlank = true;
					for ( String val : record ) {
						datavals.add(val);
						if ( allBlank && ( ! val.trim().isEmpty() ) )
							allBlank = false;
					}
					if ( ! allBlank ) {
						rowNums.add(dataRowNum);
						dataValues.add(datavals);
						if ( (numDataRows > 0) && (dataValues.size() == numDataRows) )
							break;
					}
					else {
						dataRowNum--;
					}
				}

			}
		} finally {
			dataParser.close();
		}

		if ( numDataColumns < MIN_NUM_DATA_COLUMNS )
			throw new IOException("No data columns found, possibly due to incorrect format");

		cruiseData.setPreamble(preamble);
		cruiseData.setRowNums(rowNums);
		cruiseData.setDataValues(dataValues);

		if ( assignCruiseInfo )
			cruiseData.setNumDataRows(dataValues.size());

		// Check if there was an expocode column
		String expocode = "";
		int k = 0;
		for ( DataColumnType dctype : cruiseData.getDataColTypes() ) {
			if ( DashboardServerUtils.EXPOCODE.typeNameEquals(dctype) ) {
				expocode = cruiseData.getDataValues().get(0).get(k).toUpperCase();
				break;
			}
			k++;
		}
		if ( ! expocode.isEmpty() ) {
			if ( assignCruiseInfo && ! expocodeFound ) {
				cruiseData.setExpocode(expocode);
			}
			else if ( ! expocode.equals(cruiseData.getExpocode()) ){
				throw new IOException("First expocode given in the expocode data " +
						"column does not match the expocode given for the dataset");
			}
		}
	}

	/**
	 * Returns a version of the string that was parsed to create the given record 
	 * but using the given spacer between the entries in the record.
	 * 
	 * @param record
	 * 		record to use
	 * @param spacer
	 * 		spacer to use
	 * @return
	 * 		recreated string for this record
	 */
	private String rebuildDataline(CSVRecord record, String spacer) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for ( String val : record ) {
			if ( ! val.isEmpty() ) {
				if ( first ) {
					first = false;
				}
				else {
					builder.append(spacer);							
				}
				builder.append(val);
			}
		}
		return builder.toString();
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
		// SOCAT version 
		cruiseProps.setProperty(VERSION_ID, cruise.getVersion());
		// Upload filename
		cruiseProps.setProperty(UPLOAD_FILENAME_ID, cruise.getUploadFilename());
		// Upload timestamp
		cruiseProps.setProperty(UPLOAD_TIMESTAMP_ID, cruise.getUploadTimestamp());
		// DOI of the original version of this data document
		cruiseProps.setProperty(ORIG_DOI_ID, cruise.getOrigDoi());
		// DOI of the SOCAT-enhanced version of this data document
		cruiseProps.setProperty(ENHANCED_DOI_ID, cruise.getSocatDoi());
		// Data-check status string
		cruiseProps.setProperty(DATA_CHECK_STATUS_ID, cruise.getDataCheckStatus());
		// OME metadata filename
		cruiseProps.setProperty(OME_TIMESTAMP_ID, cruise.getOmeTimestamp());
		// Metadata documents
		// a little arguably-unnecessary overhead going through an ArrayList<String>
		cruiseProps.setProperty(ADDL_DOC_TITLES_ID, 
				DashboardUtils.encodeStringArrayList(new ArrayList<String>(
						cruise.getAddlDocs())));
		// QC-submission status string
		cruiseProps.setProperty(QC_STATUS_ID, cruise.getQcStatus());
		// Archive status string
		cruiseProps.setProperty(ARCHIVE_STATUS_ID, cruise.getArchiveStatus());
		// Date of request to archive original data and metadata files with CDIAC
		cruiseProps.setProperty(ARCHIVAL_DATE_ID, cruise.getCdiacDate());
		// Total number of data measurements (rows of data)
		cruiseProps.setProperty(NUM_DATA_ROWS_ID, 
				Integer.toString(cruise.getNumDataRows()));
		// Number of data rows with error messages
		cruiseProps.setProperty(NUM_ERROR_ROWS_ID, 
				Integer.toString(cruise.getNumErrorRows()));
		// Number of data rows with warning messages
		cruiseProps.setProperty(NUM_WARN_ROWS_ID, 
				Integer.toString(cruise.getNumWarnRows()));
		// Data column name in the original upload data file
		cruiseProps.setProperty(USER_COLUMN_NAMES_ID, 
				DashboardUtils.encodeStringArrayList(cruise.getUserColNames()));
		// Data column type information
		int numCols = cruise.getDataColTypes().size();
		ArrayList<String> colTypeNames = new ArrayList<String>(numCols);
		ArrayList<String> colUnitNames = new ArrayList<String>(numCols);
		ArrayList<String> colMissValues = new ArrayList<String>(numCols);
		for ( DataColumnType colType : cruise.getDataColTypes() ) {
			colTypeNames.add(colType.getVarName());
			colUnitNames.add(colType.getUnits().get(colType.getSelectedUnitIndex()));
			colMissValues.add(colType.getSelectedMissingValue());
		}
		// Data column type/variable name
		cruiseProps.setProperty(DATA_COLUMN_TYPES_ID, 
				DashboardUtils.encodeStringArrayList(colTypeNames));
		// Unit for each data column
		cruiseProps.setProperty(DATA_COLUMN_UNITS_ID, 
				DashboardUtils.encodeStringArrayList(colUnitNames));
		// Missing value for each data column
		cruiseProps.setProperty(MISSING_VALUES_ID, 
				DashboardUtils.encodeStringArrayList(colMissValues));

		// WOCE flags
		cruiseProps.setProperty(CHECKER_WOCE_THREES, 
				DashboardUtils.encodeWoceTypeSet(cruise.getCheckerWoceThrees()));
		cruiseProps.setProperty(CHECKER_WOCE_FOURS, 
				DashboardUtils.encodeWoceTypeSet(cruise.getCheckerWoceFours()));
		cruiseProps.setProperty(USER_WOCE_THREES, 
				DashboardUtils.encodeWoceTypeSet(cruise.getUserWoceThrees()));
		cruiseProps.setProperty(USER_WOCE_FOURS, 
				DashboardUtils.encodeWoceTypeSet(cruise.getUserWoceFours()));

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
		if ( ! parentFile.exists() ) {
			parentFile.mkdirs();
		}
		else {
			// Delete the messages file (for old data) if it exists
			DashboardConfigStore configStore;
			try {
				configStore = DashboardConfigStore.get(false);
			} catch ( IOException ex ) {
				throw new IllegalArgumentException(
						"Unexpected failure to get the dashboard configuration");
			}
			configStore.getCheckerMsgHandler().deleteMsgsFile(expocode);
		}

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
	 * Adds an OME metadata document or a supplemental document to a cruise.
	 * 
	 * @param expocode
	 * 		add the document to the cruise with this expocode
	 * @param addlDoc
	 * 		document to add to the cruise; if an instance of OmeMetadata,
	 * 		this will be added as the OME metadata document for the cruise
	 * 		(just sets the OmeTimestamp for the cruise), otherwise adds
	 * 		the document as an additional document to the cruise (adds the 
	 * 		upload filename and timestamp to the additional documents list)
	 * @return
	 * 		the updated cruise information, or 
	 * 		null if there is no cruise with this expocode
	 * @throws IllegalArgumentException
	 * 		if the cruise exists and one of:
	 * 		if the expocode is invalid,
	 * 		if there are problems accessing the information file for the cruise,
	 * 		if there are problems updating and committing the cruise information,
	 * 		if the filename of the addition document is the OME filename but 
	 * 		the additional document is not an instance of OmeMetadata
	 */
	public DashboardCruise addAddlDocToCruise(String expocode, 
			DashboardMetadata addlDoc) throws IllegalArgumentException {
		// Check if the cruise exists
		DashboardCruise cruise = getCruiseFromInfoFile(expocode);
		if ( cruise == null )
			return null;
		String timestamp = addlDoc.getUploadTimestamp();
		if ( addlDoc instanceof DashboardOmeMetadata ) {
			// Assign the OME metadata timestamp for this cruise and save
			if ( ! cruise.getOmeTimestamp().equals(timestamp) ) {
				cruise.setOmeTimestamp(timestamp);
				saveCruiseInfoToFile(cruise, "Assigned new OME metadata file " +
						"timestamp '" + timestamp + "' to cruise " + expocode);
			}
		}
		else {
			String uploadFilename = addlDoc.getFilename();
			if ( DashboardUtils.OME_FILENAME.equals(uploadFilename) )
				throw new IllegalArgumentException("Supplemental documents cannot " +
						"have the upload filename of " + DashboardUtils.OME_FILENAME);
			if ( DashboardUtils.PI_OME_FILENAME.equals(uploadFilename) )
				throw new IllegalArgumentException("Supplemental documents cannot " +
						"have the upload filename of " + DashboardUtils.PI_OME_FILENAME);
			// Work directly on the additional documents list in the cruise object
			TreeSet<String> addlDocTitles = cruise.getAddlDocs();
			String titleToDelete = null;
			for ( String title : addlDocTitles ) {
				if ( uploadFilename.equals(
						(DashboardMetadata.splitAddlDocsTitle(title))[0]) ) {
					titleToDelete = title;
					break;
				}
			}
			String commitMsg; 
			if ( titleToDelete != null ) {
				addlDocTitles.remove(titleToDelete);
				commitMsg = "Update additional document " + uploadFilename + 
							" (" + timestamp + ") for cruise " + expocode;
			}
			else {
				commitMsg = "Add additional document " + uploadFilename + 
							" (" + timestamp + ") to cruise " + expocode;
			}
			addlDocTitles.add(addlDoc.getAddlDocsTitle());
			saveCruiseInfoToFile(cruise, commitMsg);
		}
		return cruise;
	}

	/**
	 * Appropriately renames the cruise data and info files for a change 
	 * in cruise expocode.  Renames the expocode in the data file.
	 * 
	 * @param oldExpocode
	 * 		standardized old expocode of the cruise
	 * @param newExpocode
	 * 		standardized new expocode for the cruise
	 * @throws IllegalArgumentException
	 * 		if the data or info file for the old expocode does not exist, 
	 * 		if a data or info file for the new expocode already exists, or
	 * 		if unable to rename or update the data or info file
	 */
	public void renameCruiseFiles(String oldExpocode, String newExpocode) 
										throws IllegalArgumentException {
		// Verify old file exist and new file do not exist
		File oldDataFile = cruiseDataFile(oldExpocode);
		if ( ! oldDataFile.exists() ) 
			throw new IllegalArgumentException(
					"Data file for " + oldExpocode + " does not exist");
		File oldInfoFile = cruiseInfoFile(oldExpocode);
		if ( ! oldInfoFile.exists() )
			throw new IllegalArgumentException(
					"Info file for " + oldExpocode + " does not exist");

		File newDataFile = cruiseDataFile(newExpocode);
		if ( newDataFile.exists() )
			throw new IllegalArgumentException(
					"Data file for " + newExpocode + " already exist");
		File newInfoFile = cruiseInfoFile(newExpocode);
		if ( newInfoFile.exists() )
			throw new IllegalArgumentException(
					"Info file for " + newExpocode + " already exist");

		// Make sure the parent directory for the new files exists
		File parentFile = newDataFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();

		// Easiest is to read all the data, modify the expocode in the data, 
		// move the files, and save under the new expocode
		DashboardCruiseWithData cruiseData = getCruiseDataFromFiles(oldExpocode, 0, -1);
		cruiseData.setExpocode(newExpocode);

		// Modify the expocode, if any, in the preamble
		ArrayList<String> preamble = cruiseData.getPreamble();
		for (int k = 0; k < preamble.size(); k++) {
			String dataline = preamble.get(k);
			// Check if this is an expocode identification line
			for ( Pattern pat : EXPOCODE_PATTERNS ) {
				Matcher mat = pat.matcher(dataline);
				if ( mat.matches() ) {
					preamble.set(k, dataline.replace(oldExpocode, newExpocode));
					break;
				}
			}
		}

		// Modify the expocode if there is a column identified as the expocode
		int k = -1;
		boolean hasExpoColumn = false;
		for ( DataColumnType type : cruiseData.getDataColTypes() ) {
			k++;
			if ( DashboardServerUtils.EXPOCODE.typeNameEquals(type) ) {
				hasExpoColumn = true;
				break;
			}
		}
		if ( hasExpoColumn ) {
			for ( ArrayList<String> dataVals : cruiseData.getDataValues() ) {
				dataVals.set(k, newExpocode);
			}
		}

		// Move the old expocode files to the new expocode filenames
		String commitMsg = "Rename from " + oldExpocode + " to " + newExpocode;
		try {
			moveVersionedFile(oldDataFile, newDataFile, commitMsg);
			moveVersionedFile(oldInfoFile, newInfoFile, commitMsg);
		} catch (SVNException ex) {
			throw new IllegalArgumentException("Problems renaming the cruise files from " + 
					oldExpocode + " to " + newExpocode + ": " + ex.getMessage());
		}

		// Save under the new expocode
		saveCruiseInfoToFile(cruiseData, commitMsg);
		saveCruiseDataToFile(cruiseData, commitMsg);
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
		// Check if the cruise is in a submitted or published state
		if ( ! Boolean.TRUE.equals(cruise.isEditable()) )
			throw new IllegalArgumentException("cruise status is " + cruise.getQcStatus());
		// Check if the user has permission to delete the cruise
		try {
			String owner = cruise.getOwner();
			if ( ! DashboardConfigStore.get(false).userManagesOver(username, owner) )
				throw new IllegalArgumentException("cruise owner is " + owner);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the user file handler");
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
	 * @param deleteMetadata 
	 * 		also delete metadata and additional documents?
	 * @throws IllegalArgumentException
	 * 		if the cruise expocode is not valid, if there were
	 * 		problems access the cruise, if the user is not permitted 
	 * 		to delete the cruise, or if there were problems deleting
	 * 		a file or committing the deletion from version control
	 * @throws FileNotFoundException
	 * 		if the cruise information file does not exist
	 */
	public void deleteCruiseFiles(String expocode, String username, 
			Boolean deleteMetadata) throws IllegalArgumentException, 
												FileNotFoundException {
		// Verify this cruise can be deleted
		DashboardCruise cruise;
		try {
			cruise = verifyOkayToDeleteCruise(expocode, username);
		} catch ( IllegalArgumentException ex ) {
			throw new IllegalArgumentException(
					"Not permitted to delete cruise " + expocode +
					": " + ex.getMessage());
		}

		DashboardConfigStore configStore;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the dashboard configuration");
		}

		// If they exist, delete the DSG files and notify ERDDAP
		DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
		if ( dsgHandler.deleteCruise(expocode) )
			dsgHandler.flagErddap(true, true);

		// If it exists, delete the messages file
		configStore.getCheckerMsgHandler().deleteMsgsFile(expocode);
			
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

		if ( deleteMetadata ) {
			// Delete the metadata and additional documents associated with this cruise
			MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
			try {
				metadataHandler.removeMetadata(username, expocode, DashboardUtils.OME_FILENAME);
			} catch (Exception ex) {
				// Ignore - may not exist
				;
			}
			try {
				metadataHandler.removeMetadata(username, expocode, DashboardUtils.PI_OME_FILENAME);
			} catch (Exception ex) {
				// Ignore - may not exist
				;
			}
			for ( String mdataTitle : cruise.getAddlDocs() ) {
				String filename = DashboardMetadata.splitAddlDocsTitle(mdataTitle)[0];
				metadataHandler.removeMetadata(username, expocode, filename);
			}
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

		// SOCAT version 
		value = cruiseProps.getProperty(VERSION_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					VERSION_ID + " given in " + infoFile.getPath());	
		try {
			cruise.setVersion(value);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid property value for " +
					VERSION_ID + " given in " + infoFile.getPath());	
		}

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

		// original data DOI - allow it to be missing
		value = cruiseProps.getProperty(ORIG_DOI_ID);
		cruise.setOrigDoi(value);

		// SOCAT enhanced data DOI - allow it to be missing
		value = cruiseProps.getProperty(ENHANCED_DOI_ID);
		cruise.setSocatDoi(value);

		// Data check status
		value = cruiseProps.getProperty(DATA_CHECK_STATUS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_CHECK_STATUS_ID + " given in " + infoFile.getPath());			
		cruise.setDataCheckStatus(value);

		// OME metadata filename
		value = cruiseProps.getProperty(OME_TIMESTAMP_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					OME_TIMESTAMP_ID + " given in " + infoFile.getPath());			
		cruise.setOmeTimestamp(value);

		// Metadata documents
		value = cruiseProps.getProperty(ADDL_DOC_TITLES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					ADDL_DOC_TITLES_ID + " given in " + infoFile.getPath());			
		// a little arguably-unnecessary overhead going through an ArrayList<String>
		cruise.setAddlDocs(new TreeSet<String>(
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
		value = cruiseProps.getProperty(ARCHIVAL_DATE_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					ARCHIVAL_DATE_ID + " given in " + infoFile.getPath());			
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

		// Number of error messages
		value = cruiseProps.getProperty(NUM_ERROR_ROWS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					NUM_ERROR_ROWS_ID + " given in " + infoFile.getPath());
		try {
			cruise.setNumErrorRows(Integer.parseInt(value));
		} catch ( NumberFormatException ex ) {
			throw new IllegalArgumentException(ex);
		}

		// Number of warning messages
		value = cruiseProps.getProperty(NUM_WARN_ROWS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					NUM_WARN_ROWS_ID + " given in " + infoFile.getPath());
		try {
			cruise.setNumWarnRows(Integer.parseInt(value));
		} catch ( NumberFormatException ex ) {
			throw new IllegalArgumentException(ex);
		}

		// Data column name in the original upload data file
		value = cruiseProps.getProperty(USER_COLUMN_NAMES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					USER_COLUMN_NAMES_ID + " given in " + infoFile.getPath());
		cruise.setUserColNames(DashboardUtils.decodeStringArrayList(value));
		int numCols =  cruise.getUserColNames().size();

		// Get the data column type information
		value = cruiseProps.getProperty(DATA_COLUMN_TYPES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_COLUMN_TYPES_ID + " given in " + infoFile.getPath());
		ArrayList<String> colTypeNames = DashboardUtils.decodeStringArrayList(value);
		if ( colTypeNames.size() != numCols )
			throw new IllegalArgumentException("number of data column types " +
					"different from number of user column names");
		value = cruiseProps.getProperty(DATA_COLUMN_UNITS_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					DATA_COLUMN_UNITS_ID + " given in " + infoFile.getPath());
		ArrayList<String> colTypeUnits = DashboardUtils.decodeStringArrayList(value);
		if ( colTypeUnits.size() != numCols )
			throw new IllegalArgumentException("number of data column units " +
					"different from number of user column names");
		value = cruiseProps.getProperty(MISSING_VALUES_ID);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					MISSING_VALUES_ID + " given in " + infoFile.getPath());
		ArrayList<String> colMissValues = DashboardUtils.decodeStringArrayList(value);
		if ( colMissValues.size() != numCols )
			throw new IllegalArgumentException("number of data column missing values " +
					"different from number of user column names");

		// Assign the data column types 
		ArrayList<DataColumnType> dataColTypes = new ArrayList<DataColumnType>(numCols);
		for (int k = 0; k < numCols; k++) {
			DataColumnType dctype = userTypes.getDataColumnType(colTypeNames.get(k));
			if ( dctype == null ) {
				// See if there is a modified version of this type name
				String newName = DashboardServerUtils.RENAMED_DATA_TYPES.get(colTypeNames.get(k));
				dctype = userTypes.getDataColumnType(newName);
			}
			if ( dctype == null )
				throw new IllegalArgumentException("unknown data type \"" + colTypeNames.get(k) + "\"");
			int index = dctype.getUnits().indexOf(colTypeUnits.get(k));
			if ( index < 0 ) {
				// see if there is a modified version of this unit
				String newName = DashboardServerUtils.RENAMED_UNITS.get(colTypeUnits.get(k));
				if ( newName != null )
					index = dctype.getUnits().indexOf(newName);
			}
			if ( index < 0 )
				throw new IllegalArgumentException("unknown unit \"" + 
						colTypeUnits.get(k) + "\" for data type \"" + 
						dctype.getVarName() + "\"");
			dctype.setSelectedUnitIndex(index);
			dctype.setSelectedMissingValue(colMissValues.get(k));
			dataColTypes.add(dctype);
		}
		cruise.setDataColTypes(dataColTypes);

		// WOCE-3 flags
		value = cruiseProps.getProperty(CHECKER_WOCE_THREES);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					CHECKER_WOCE_THREES + " given in " + infoFile.getPath());
		// Must be new-style WOCE-3 flags
		TreeSet<WoceType> woceThrees = DashboardUtils.decodeWoceTypeSet(value);
		cruise.setCheckerWoceThrees(woceThrees);
		// Should also have user-provided WOCE-3 flags
		value = cruiseProps.getProperty(USER_WOCE_THREES);
		if ( value == null ) 
			throw new IllegalArgumentException("No property value for " + 
					USER_WOCE_THREES + " given in " + infoFile.getPath());
		woceThrees = DashboardUtils.decodeWoceTypeSet(value);
		cruise.setUserWoceThrees(woceThrees);

		// WOCE-4 flags
		value = cruiseProps.getProperty(CHECKER_WOCE_FOURS);
		if ( value == null )
			throw new IllegalArgumentException("No property value for " + 
					CHECKER_WOCE_FOURS + " given in " + infoFile.getPath());
		// Must be new-style WOCE-4 flags
		TreeSet<WoceType> woceFours = DashboardUtils.decodeWoceTypeSet(value);
		cruise.setCheckerWoceFours(woceFours);
		// Should also have user-provided WOCE-4 flags
		value = cruiseProps.getProperty(USER_WOCE_FOURS);
		if ( value == null ) 
			throw new IllegalArgumentException("No property value for " + 
					USER_WOCE_FOURS + " given in " + infoFile.getPath());
		woceFours = DashboardUtils.decodeWoceTypeSet(value);
		cruise.setUserWoceFours(woceFours);
	}

	/**
	 * Updates the UploadDashboard status of the cruise based on the QC flag
	 * provided for the cruise.
	 * 
	 * @param expocode
	 * 		Update the status of the cruise with this expocode.
	 * @param qcFlag
	 * 		QC flag assigned to this cruise; if null or whitespace,
	 * 		the status assigned will be unsubmitted
	 * @return
	 * 		true if the status was updated (needed to be updated); 
	 * 		otherwise false
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid,
	 * 		if there are problems accessing the cruise info file, or
	 * 		if the QC flag given is invalid
	 */
	public boolean updateCruiseDashboardStatus(String expocode, Character qcFlag) 
											throws IllegalArgumentException {
		DashboardCruise cruise = getCruiseFromInfoFile(expocode);
		String oldStatus = cruise.getQcStatus();
		String newStatus;
		// Special check for null or blank QC flag == not submitted
		if ( (qcFlag == null) || Character.isWhitespace(qcFlag) ) {
			newStatus = DashboardUtils.QC_STATUS_NOT_SUBMITTED;
		}
		else {
			newStatus = DashboardUtils.FLAG_STATUS_MAP.get(qcFlag);
			if ( newStatus == null ) {
				// QCEvent.QC_COMMENT or unknown flag
				throw new IllegalArgumentException("Unexpected QC flag of '" + 
						qcFlag + "' given to updateCruiseDashboardStatus");
			}
		}
		if ( oldStatus.equals(newStatus) )
			return false;
		cruise.setQcStatus(newStatus);
		saveCruiseInfoToFile(cruise, "Update cruise dashboard status for " + 
				expocode + " from '" + oldStatus + "' to '" + newStatus + "'");
		return true;
	}

}
