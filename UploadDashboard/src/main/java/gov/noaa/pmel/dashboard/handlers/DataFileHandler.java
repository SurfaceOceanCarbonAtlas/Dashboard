/**
 *
 */
package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import gov.noaa.pmel.dashboard.shared.QCFlag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.tmatesoft.svn.core.SVNException;

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

/**
 * Handles storage and retrieval of cruise data in files.
 *
 * @author Karl Smith
 */
public class DataFileHandler extends VersionedFileHandler {

    private static final String INFO_FILENAME_EXTENSION = ".properties";
    private static final String DATA_FILENAME_EXTENSION = ".tsv";
    private static final String DATA_OWNER_ID = "dataowner";
    private static final String VERSION_ID = "version";
    private static final String UPLOAD_FILENAME_ID = "uploadfilename";
    private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
    private static final String SOURCE_DOI_ID = "sourcedoi";
    private static final String ENHANCED_DOI_ID = "enhanceddoi";
    private static final String DATA_CHECK_STATUS_ID = "datacheckstatus";
    private static final String OME_TIMESTAMP_ID = "ometimestamp";
    private static final String ADDL_DOC_TITLES_ID = "addldoctitles";
    private static final String SUBMIT_STATUS_ID = "submitstatus";
    private static final String ARCHIVE_STATUS_ID = "archivestatus";
    private static final String ARCHIVAL_DATE_ID = "archivaldate";
    private static final String NUM_DATA_ROWS_ID = "numdatarows";
    private static final String NUM_ERROR_ROWS_ID = "numerrrows";
    private static final String NUM_WARN_ROWS_ID = "numwarnrows";
    private static final String DATA_COLUMN_TYPES_ID = "datacolumntypes";
    private static final String USER_COLUMN_NAMES_ID = "usercolumnnames";
    private static final String DATA_COLUMN_UNITS_ID = "datacolumnunits";
    private static final String MISSING_VALUES_ID = "missingvalues";
    private static final String CHECKER_FLAGS = "checkerflags";
    private static final String USER_FLAGS = "userflags";

    private static final int MIN_NUM_DATA_COLUMNS = 6;

    // Patterns for getting the expocode from the metadata preamble
    private static final Pattern[] EXPOCODE_PATTERNS = new Pattern[] {
            Pattern.compile("Dataset\\s*Expocode\\s*[=:]\\s*([\\p{javaUpperCase}\\p{Digit}-]+)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("Cruise\\s*Expocode\\s*[=:]\\s*([\\p{javaUpperCase}\\p{Digit}-]+)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("Expocode\\s*[=:]\\s*([\\p{javaUpperCase}\\p{Digit}-]+)",
                    Pattern.CASE_INSENSITIVE)
    };

    private KnownDataTypes userTypes;
    private UserFileHandler userFileHandler;
    private String uploadVersion;


    /**
     * Handles storage and retrieval of dataset data in files under the given data files directory.
     *
     * @param dataFilesDirName
     *         name of the data files directory
     * @param svnUsername
     *         username for SVN authentication
     * @param svnPassword
     *         password for SVN authentication
     * @param userTypes
     *         known user data column types
     * @param userFileHandler
     *         handler for user-specific configuraton files
     * @param uploadVersion
     *         version to assign to new and updated datasets
     *
     * @throws IllegalArgumentException
     *         if the specified directory does not exist, is not a directory, or is not under SVN version control
     */
    public DataFileHandler(String dataFilesDirName, String svnUsername, String svnPassword, KnownDataTypes userTypes,
            UserFileHandler userFileHandler, String uploadVersion) throws IllegalArgumentException {
        super(dataFilesDirName, svnUsername, svnPassword);
        this.userTypes = userTypes;
        this.userFileHandler = userFileHandler;
        this.uploadVersion = uploadVersion;
    }

    /**
     * @param datasetId
     *         the ID of the dataset
     *
     * @return the file of properties associated with the dataset
     *
     * @throws IllegalArgumentException
     *         if datasetId is not a valid dataset ID
     */
    File datasetInfoFile(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String upperExpo = DashboardServerUtils.checkDatasetID(datasetId);
        // Create the file with the full path name of the properties file
        File parentDir = new File(filesDir, upperExpo.substring(0, 4));
        File propsFile = new File(parentDir, upperExpo + INFO_FILENAME_EXTENSION);
        return propsFile;
    }

    /**
     * @param datasetId
     *         the ID of the dataset
     *
     * @return the data file associated with the dataset
     *
     * @throws IllegalArgumentException
     *         if datasetId is not a valid dataset ID
     */
    File datasetDataFile(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset ID
        String upperExpo = DashboardServerUtils.checkDatasetID(datasetId);
        // Create the file with the full path name of the properties file
        File parentDir = new File(filesDir, upperExpo.substring(0, 4));
        File dataFile = new File(parentDir, upperExpo + DATA_FILENAME_EXTENSION);
        return dataFile;
    }

    /**
     * Searches all existing datasets and returns the dataset IDs of those that match
     * the given dataset ID containing wildcards and/or regular expressions.
     *
     * @param wildDatasetId
     *         dataset ID, possibly with wildcards * and ?, to use;
     *         letters are converted to uppercase,
     *         "*" is turned in the regular expression "[\p{javaUpperCase}\p{Digit}-]*", and
     *         "?" is turned in the regular expression "[\p{javaUpperCase}\p{Digit}-]{1}".
     *
     * @return list of dataset IDs of existing datasets that match the given wildcard dataset ID;
     *         never null, but may be empty
     *
     * @throws IllegalArgumentException
     *         if wildDatasetId is not a valid dataset ID pattern
     */
    public HashSet<String> getMatchingDatasetIds(String wildDatasetId)
            throws IllegalArgumentException {
        HashSet<String> matchingIds = new HashSet<String>();
        final Pattern filenamePattern;
        try {
            String filenameRegEx = wildDatasetId.toUpperCase();
            filenameRegEx = filenameRegEx.replace("*", "[\\p{javaUpperCase}\\p{Digit}-]*");
            filenameRegEx = filenameRegEx.replace("?", "[\\p{javaUpperCase}\\p{Digit}-]{1}");
            filenameRegEx += INFO_FILENAME_EXTENSION;
            filenamePattern = Pattern.compile(filenameRegEx);
        } catch ( PatternSyntaxException ex ) {
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
        for (File subDir : subDirs) {
            File[] matchFiles = subDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if ( filenamePattern.matcher(name).matches() )
                        return true;
                    return false;
                }
            });
            for (File match : matchFiles) {
                String name = match.getName();
                String datasetId = name.substring(0, name.length() - INFO_FILENAME_EXTENSION.length());
                matchingIds.add(datasetId);
            }
        }
        return matchingIds;
    }

    /**
     * Determines if a dataset properties file exists
     *
     * @param datasetId
     *         ID of the dataset to check
     *
     * @return true if the dataset properties file exists
     *
     * @throws IllegalArgumentException
     *         if datasetId is not a valid dataset ID
     */
    public boolean infoFileExists(String datasetId) throws IllegalArgumentException {
        File infoFile = datasetInfoFile(datasetId);
        return infoFile.exists();
    }

    /**
     * Determines if a dataset data file exists
     *
     * @param datasetId
     *         ID of the dataset to check
     *
     * @return true if the dataset data file exists
     *
     * @throws IllegalArgumentException
     *         if datasetId is not a valid dataset ID
     */
    public boolean dataFileExists(String datasetId) throws IllegalArgumentException {
        File dataFile = datasetDataFile(datasetId);
        return dataFile.exists();
    }

    /**
     * Creates a DashboardDatasetData object with data read from the given BufferedReader.
     * <p>
     * The data read should have a preamble of metadata containing the ID (expocode)
     * for the dataset on a line such as one of the following (case and space insensitive):
     * <pre>
     * Expocode :
     * Expocode =
     * Cruise Expocode :
     * Cruise Expocode =
     * Dataset Expocode :
     * Dataset Expocode =
     * </pre>
     * Alternatively, the dataset ID can be given in an appropriate data column.
     * In this case, only the first ID in the column is used.
     * <p>
     * The first line containing at least {@value #MIN_NUM_DATA_COLUMNS} non-blank values will be taken to be the
     * line of data column headers, possibly with units as part of their names.  No data column header can be blank.
     * Optionally, the column headers line can be immediately followed by a line with no pure-numeric values, which
     * will be interpreted as units for the data columns.  All remaining (non-blank) lines are considered data lines
     * and should have the same number of values as in the column headers line.  All data values are read as Strings.
     * Blank lines and ines with all-blank values are ignored.
     *
     * @param dataReader
     *         read metadata preamble and data lines from here
     * @param dataFormat
     *         format of the data lines read; one of {@link DashboardUtils#COMMA_FORMAT_TAG},
     *         {@link DashboardUtils#SEMICOLON_FORMAT_TAG}, or {@link DashboardUtils#TAB_FORMAT_TAG}.
     * @param owner
     *         name of the owner to record in the datasets created or appended to;
     *         also used to guess data column types from column names
     * @param filename
     *         upload filename to record in the dataset created
     * @param timestamp
     *         upload timestamp to record in the dataset created
     *
     * @return the dataset data object created
     *
     * @throws IOException
     *         if reading from datasetsReader throws one, if the dataFormat string is not recognized if there is an
     *         inconsistent number of data values (columns) in a data sample (row) if a dataset/cruise name column was
     *         not found, if a dataset ID derived from the dataset name is not valid (too short or too long), if there
     *         are too few data columns, or if no data samples (rows) were found.
     */
    public DashboardDatasetData createDatasetFromInput(BufferedReader dataReader, String dataFormat,
            String owner, String filename, String timestamp) throws IOException {
        char spacer;
        if ( DashboardUtils.TAB_FORMAT_TAG.equals(dataFormat) ) {
            spacer = '\t';
        }
        else if ( DashboardUtils.COMMA_FORMAT_TAG.equals(dataFormat) ) {
            spacer = ',';
        }
        else if ( DashboardUtils.SEMICOLON_FORMAT_TAG.equals(dataFormat) ) {
            spacer = ';';
        }
        else
            throw new IOException("Unexpected invalid data format '" + dataFormat + "'");

        CSVFormat format = CSVFormat.EXCEL.withIgnoreSurroundingSpaces()
                                          .withIgnoreEmptyLines()
                                          .withDelimiter(spacer);
        CSVParser dataParser = new CSVParser(dataReader, format);

        String expocode = null;
        ArrayList<String> preamble = new ArrayList<String>();
        ArrayList<String> columnNames = null;
        int numDataColumns = 0;
        ArrayList<ArrayList<String>> allDataVals = new ArrayList<ArrayList<String>>();
        try {
            boolean checkForUnits = false;

            for (CSVRecord record : dataParser) {

                // Still looking for headers?
                if ( columnNames == null ) {
                    if ( record.size() >= MIN_NUM_DATA_COLUMNS ) {
                        // Check if these are the column names headers; column names must not be blank or pure numeric
                        boolean isHeader = true;
                        for (String val : record) {
                            if ( val.isEmpty() ) {
                                isHeader = false;
                                break;
                            }
                            try {
                                Double.parseDouble(val);
                                isHeader = false;
                                break;
                            } catch ( Exception ex ) {
                                // Expected result for a name
                            }
                        }
                        if ( isHeader ) {
                            // These indeed are the column headers
                            numDataColumns = record.size();
                            columnNames = new ArrayList<String>(numDataColumns);
                            for (String val : record) {
                                columnNames.add(val);
                            }
                            // Check for units in the next record
                            checkForUnits = true;
                            continue;
                        }
                    }
                }

                // Still reading metadata?
                if ( columnNames == null ) {
                    // Put this line of metadata back together using spacer
                    String metaline = rebuildDataline(record, spacer);

                    // Examine this metadata line for the expocode
                    if ( (expocode == null) && !metaline.isEmpty() ) {
                        for (Pattern pat : EXPOCODE_PATTERNS) {
                            Matcher mat = pat.matcher(metaline);
                            if ( !mat.matches() )
                                continue;
                            expocode = mat.group(1).toUpperCase();
                            break;
                        }
                    }

                    // Save this metadata line in the preamble for the cruise
                    preamble.add(metaline);
                    continue;
                }

                // Check that the number of columns is consistent
                if ( record.size() != numDataColumns )
                    throw new IOException("Inconsistent number of data columns (" + record.size() +
                            " instead of " + numDataColumns + ") for measurement " + dataParser.getRecordNumber() +
                            ":\n    " + rebuildDataline(record, spacer));

                if ( checkForUnits ) {
                    // Check if the line immediately following the data column names are units
                    checkForUnits = false;

                    boolean isUnits = true;
                    // A unit specification cannot be pure numeric
                    for (String val : record) {
                        try {
                            Double.valueOf(val);
                            isUnits = false;
                            break;
                        } catch ( NumberFormatException ex ) {
                            // Expected result for a units specification
                        }
                    }
                    if ( isUnits ) {
                        // Add the units to the column header names
                        int k = 0;
                        for (String units : record) {
                            if ( !units.isEmpty() ) {
                                String name = columnNames.get(k);
                                name += " [" + units + "]";
                                columnNames.set(k, name);
                            }
                            k++;
                        }
                        // the next line is the first line of data values to parse
                        continue;
                    }
                    // not units; this is the first line of data value to parse
                }

                // read the data in this line
                ArrayList<String> datavals = new ArrayList<String>(numDataColumns);
                boolean allBlank = true;
                for (String val : record) {
                    datavals.add(val);
                    if ( allBlank && !val.isEmpty() )
                        allBlank = false;
                }
                if ( !allBlank ) {
                    allDataVals.add(datavals);
                }
            }
        } finally {
            dataParser.close();
        }

        if ( numDataColumns < MIN_NUM_DATA_COLUMNS )
            throw new IOException("No data columns found, possibly due to incorrect format");
        if ( allDataVals.isEmpty() )
            throw new IOException("No data values found");

        // Create the dataset with data to return
        DashboardDatasetData datasetData = new DashboardDatasetData();
        datasetData.setPreamble(preamble);
        datasetData.setOwner(owner);
        datasetData.setVersion(uploadVersion);
        datasetData.setUserColNames(columnNames);
        datasetData.setDataValues(allDataVals);

        int numRows = allDataVals.size();
        datasetData.setNumDataRows(numRows);
        ArrayList<Integer> rowNums = new ArrayList<Integer>(numRows);
        for (int k = 1; k <= numRows; k++) {
            rowNums.add(k);
        }
        datasetData.setRowNums(rowNums);

        // Assign the data column types from the column names (including customizations for this user)
        userFileHandler.assignDataColumnTypes(datasetData);

        // If no expocode found in the metadata preamble,
        // try to get it from the first value of an appropriate data column
        if ( expocode == null ) {
            int k = 0;
            for (DataColumnType dtype : datasetData.getDataColTypes()) {
                if ( DashboardUtils.DATASET_ID.typeNameEquals(dtype) ) {
                    expocode = allDataVals.get(0).get(k).trim().toUpperCase();
                    break;
                }
                k++;
            }
        }
        datasetData.setDatasetId(expocode);


        return datasetData;
    }

    /**
     * Returns a new DashboardDataset assigned from the dataset information file without reading any of the data in
     * dataset data file.
     *
     * @param datasetId
     *         ID of the dataset to read
     *
     * @return new DashboardDataset assigned from the information file, or null if the dataset information file does not
     *         exist
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is not valid or if there are problems accessing the information file
     */
    public DashboardDataset getDatasetFromInfoFile(String datasetId) throws IllegalArgumentException {
        DashboardDataset dataset = new DashboardDataset();
        dataset.setDatasetId(datasetId);
        // Read the information saved in the properties file
        try {
            assignDatasetFromInfoFile(dataset);
        } catch ( FileNotFoundException ex ) {
            return null;
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Problems reading dataset information for " +
                    datasetId + ": " + ex.getMessage());
        }
        return dataset;
    }

    /**
     * Get a dataset with data saved to file
     *
     * @param datasetId
     *         ID of the dataset to read
     * @param firstDataRow
     *         index of the first data row to return
     * @param numDataRows
     *         maximum number of data rows to return; if negative, no limit is imposed (all remaining data is returned)
     *
     * @return the dataset with data, or null if there is no information or data saved for this dataset.
     *
     * @throws IllegalArgumentException
     *         if the dataset is invalid or if there was a error reading information or data for this cruise
     */
    public DashboardDatasetData getDatasetDataFromFiles(String datasetId,
            int firstDataRow, int numDataRows) throws IllegalArgumentException {
        // Create the cruise and assign the dataset
        DashboardDatasetData cruiseData = new DashboardDatasetData();
        cruiseData.setDatasetId(datasetId);
        try {
            // Assign values from the cruise information file
            assignDatasetFromInfoFile(cruiseData);
        } catch ( FileNotFoundException ex ) {
            return null;
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Problems reading dataset information for " +
                    datasetId + ": " + ex.getMessage());
        }
        // Read the cruise data file
        File dataFile = datasetDataFile(datasetId);
        try {
            BufferedReader cruiseReader = new BufferedReader(new FileReader(dataFile));
            try {
                // Assign values from the cruise data file
                assignDataFromInput(cruiseData, cruiseReader, firstDataRow, numDataRows);
            } finally {
                cruiseReader.close();
            }
        } catch ( FileNotFoundException ex ) {
            return null;
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Problems reading cruise data for " +
                    datasetId + ": " + ex.getMessage());
        }
        return cruiseData;
    }

    /**
     * Saves and commits only the dataset properties to the information file. This does not save the dataset data of a
     * DashboardDatasetData. This first checks the currently saved properties for the cruise, and writes and commits a
     * new properties file only if there are changes.
     *
     * @param dataset
     *         save properties of this dataset
     * @param message
     *         version control commit message; if null or blank, the commit will not be performed
     *
     * @throws IllegalArgumentException
     *         if the ID of the dataset is not valid, if there was an error writing information for this dataset to
     *         file, or if there was an error committing the updated file to version control
     */
    public void saveDatasetInfoToFile(DashboardDataset dataset, String message)
            throws IllegalArgumentException {
        // Get the dataset information filename
        String datasetId = dataset.getDatasetId();
        File infoFile = datasetInfoFile(datasetId);
        // First check if there are any changes from what is saved to file
        try {
            DashboardDataset savedDataset = getDatasetFromInfoFile(datasetId);
            if ( (savedDataset != null) && savedDataset.equals(dataset) )
                return;
        } catch ( IllegalArgumentException ex ) {
            // Some problem with the saved data
            ;
        }
        // Create the directory tree if it does not exist
        File parentFile = infoFile.getParentFile();
        if ( !parentFile.exists() )
            parentFile.mkdirs();
        // Create the properties for this dataset information file
        Properties datasetProps = new Properties();
        // Owner of the dataset
        datasetProps.setProperty(DATA_OWNER_ID, dataset.getOwner());
        // Version
        datasetProps.setProperty(VERSION_ID, dataset.getVersion());
        // Upload filename
        datasetProps.setProperty(UPLOAD_FILENAME_ID, dataset.getUploadFilename());
        // Upload timestamp
        datasetProps.setProperty(UPLOAD_TIMESTAMP_ID, dataset.getUploadTimestamp());
        // DOIs
        datasetProps.setProperty(SOURCE_DOI_ID, dataset.getSourceDOI());
        datasetProps.setProperty(ENHANCED_DOI_ID, dataset.getEnhancedDOI());
        // Data-check status string
        datasetProps.setProperty(DATA_CHECK_STATUS_ID, dataset.getDataCheckStatus());
        // OME metadata timestamp
        datasetProps.setProperty(OME_TIMESTAMP_ID, dataset.getOmeTimestamp());
        // Metadata documents
        datasetProps.setProperty(ADDL_DOC_TITLES_ID, DashboardUtils.encodeStringTreeSet(dataset.getAddlDocs()));
        // QC-submission status string
        datasetProps.setProperty(SUBMIT_STATUS_ID, dataset.getSubmitStatus());
        // Archive status string
        datasetProps.setProperty(ARCHIVE_STATUS_ID, dataset.getArchiveStatus());
        // Date of request to archive original data and metadata files
        datasetProps.setProperty(ARCHIVAL_DATE_ID, dataset.getArchiveDate());
        // Total number of data measurements (rows of data)
        datasetProps.setProperty(NUM_DATA_ROWS_ID, Integer.toString(dataset.getNumDataRows()));
        // Number of data rows with error messages
        datasetProps.setProperty(NUM_ERROR_ROWS_ID, Integer.toString(dataset.getNumErrorRows()));
        // Number of data rows with warning messages
        datasetProps.setProperty(NUM_WARN_ROWS_ID, Integer.toString(dataset.getNumWarnRows()));
        // Data column name in the original upload data file
        datasetProps.setProperty(USER_COLUMN_NAMES_ID, DashboardUtils.encodeStringArrayList(dataset.getUserColNames()));
        // Data column type information
        int numCols = dataset.getDataColTypes().size();
        ArrayList<String> colTypeNames = new ArrayList<String>(numCols);
        ArrayList<String> colUnitNames = new ArrayList<String>(numCols);
        ArrayList<String> colMissValues = new ArrayList<String>(numCols);
        for (DataColumnType colType : dataset.getDataColTypes()) {
            colTypeNames.add(colType.getVarName());
            colUnitNames.add(colType.getUnits().get(colType.getSelectedUnitIndex()));
            colMissValues.add(colType.getSelectedMissingValue());
        }
        // Data column type/variable name
        datasetProps.setProperty(DATA_COLUMN_TYPES_ID, DashboardUtils.encodeStringArrayList(colTypeNames));
        // Unit for each data column
        datasetProps.setProperty(DATA_COLUMN_UNITS_ID, DashboardUtils.encodeStringArrayList(colUnitNames));
        // Missing value for each data column
        datasetProps.setProperty(MISSING_VALUES_ID, DashboardUtils.encodeStringArrayList(colMissValues));

        // Flags
        datasetProps.setProperty(CHECKER_FLAGS, DashboardServerUtils.encodeQCFlagSet(dataset.getCheckerFlags()));
        datasetProps.setProperty(USER_FLAGS, DashboardServerUtils.encodeQCFlagSet(dataset.getUserFlags()));

        // Save the properties to the cruise information file
        try {
            PrintWriter propsWriter = new PrintWriter(infoFile);
            try {
                datasetProps.store(propsWriter, null);
            } finally {
                propsWriter.close();
            }
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Problems writing dataset information for " +
                    datasetId + " to " + infoFile.getPath() + ": " + ex.getMessage());
        }

        if ( (message == null) || message.trim().isEmpty() )
            return;

        // Submit the updated information file to version control
        try {
            commitVersion(infoFile, message);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems committing updated dataset information for  " +
                    datasetId + ": " + ex.getMessage());
        }
    }

    /**
     * Saves and commits the dataset data to the data file. The dataset information file needs to be saved using {@link
     * #saveDatasetInfoToFile(DashboardDataset, String)}.
     *
     * @param dataset
     *         dataset data to save
     * @param message
     *         version control commit message; if null or blank, the commit will not be performed
     *
     * @throws IllegalArgumentException
     *         if the ID for the dataset is  not valid, if there was an error writing data for this dataset to file, or
     *         if there was an error committing the updated file to version control
     */
    public void saveDatasetDataToFile(DashboardDatasetData dataset, String message) throws IllegalArgumentException {
        // Get the dataset data filename
        String datasetId = dataset.getDatasetId();
        File dataFile = datasetDataFile(datasetId);
        // Create the directory tree for this file if it does not exist
        File parentFile = dataFile.getParentFile();
        if ( !parentFile.exists() ) {
            parentFile.mkdirs();
        }
        else {
            // Delete the messages file (for old data) if it exists
            DashboardConfigStore configStore;
            try {
                configStore = DashboardConfigStore.get(false);
            } catch ( IOException ex ) {
                throw new IllegalArgumentException("Unexpected failure to get the dashboard configuration");
            }
            configStore.getCheckerMsgHandler().deleteMsgsFile(datasetId);
        }

        // Save the data to the data file
        try {
            PrintWriter writer = new PrintWriter(dataFile);
            try {
                // The data column headers
                String dataline = "";
                boolean first = true;
                for (String name : dataset.getUserColNames()) {
                    if ( !first )
                        dataline += "\t";
                    else
                        first = false;
                    dataline += name;
                }
                writer.println(dataline);
                // The data measurements (rows of data)
                for (ArrayList<String> datarow : dataset.getDataValues()) {
                    dataline = "";
                    first = true;
                    for (String datum : datarow) {
                        if ( !first )
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
            throw new IllegalArgumentException("Problems writing dataset data for " +
                    datasetId + " to " + dataFile.getPath() + ": " + ex.getMessage());
        }

        if ( (message == null) || message.trim().isEmpty() )
            return;

        // Submit the updated data file to version control
        try {
            commitVersion(dataFile, message);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems committing updated dataset data for " +
                    datasetId + ": " + ex.getMessage());
        }
    }

    /**
     * Adds the title of an OME metadata document or a supplemental document to the documents list associated with a
     * dataset.
     *
     * @param datasetId
     *         add the document to the dataset with this ID
     * @param addlDoc
     *         document to add to the dataset; if an instance of OmeMetadata, this will be added as the OME metadata
     *         document for the dataset (just updates omeTimestamp for the dataset), otherwise adds the document as an
     *         supplemental document to the dataset (adds the upload filename and timestamp to addnDocs for the
     *         dataset)
     *
     * @return the updated dataset information
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if there are problems accessing the information file for the dataset, if
     *         there are problems updating and committing the dataset information, if the filename of the document is
     *         the OME filename but the document is not an instance of OmeMetadata
     */
    public DashboardDataset addAddlDocTitleToDataset(String datasetId, DashboardMetadata addlDoc)
            throws IllegalArgumentException {
        DashboardDataset dataset = getDatasetFromInfoFile(datasetId);
        if ( dataset == null )
            throw new IllegalArgumentException("No dataset with the ID " + datasetId);
        String timestamp = addlDoc.getUploadTimestamp();
        if ( addlDoc instanceof DashboardOmeMetadata ) {
            // Assign the OME metadata timestamp for this cruise and save
            if ( !dataset.getOmeTimestamp().equals(timestamp) ) {
                dataset.setOmeTimestamp(timestamp);
                saveDatasetInfoToFile(dataset, "Assigned new OME metadata file " +
                        "timestamp '" + timestamp + "' to dataset " + datasetId);
            }
        }
        else {
            String uploadFilename = addlDoc.getFilename();
            if ( DashboardUtils.OME_FILENAME.equals(uploadFilename) )
                throw new IllegalArgumentException("Supplemental documents cannot have the upload filename of " +
                        DashboardUtils.OME_FILENAME);
            if ( DashboardUtils.PI_OME_FILENAME.equals(uploadFilename) )
                throw new IllegalArgumentException("Supplemental documents cannot have the upload filename of " +
                        DashboardUtils.PI_OME_FILENAME);
            // Work directly on the additional documents list in the cruise object
            TreeSet<String> addlDocTitles = dataset.getAddlDocs();
            String titleToDelete = null;
            for (String title : addlDocTitles) {
                if ( uploadFilename.equals((DashboardMetadata.splitAddlDocsTitle(title))[0]) ) {
                    titleToDelete = title;
                    break;
                }
            }
            String commitMsg;
            if ( titleToDelete != null ) {
                addlDocTitles.remove(titleToDelete);
                commitMsg = "Update additional document " + uploadFilename +
                        " (" + timestamp + ") for dataset " + datasetId;
            }
            else {
                commitMsg = "Add additional document " + uploadFilename +
                        " (" + timestamp + ") to dataset " + datasetId;
            }
            addlDocTitles.add(addlDoc.getAddlDocsTitle());
            saveDatasetInfoToFile(dataset, commitMsg);
        }
        return dataset;
    }

    /**
     * Appropriately renames and modifies the dataset data and info files from one cruise/dataset name to another.
     *
     * @param oldName
     *         old cruise/dateset name
     * @param newName
     *         new cruise/dataset name
     *
     * @throws IllegalArgumentException
     *         if the data or info file for the old name does not exist, if a data or info file for the new name already
     *         exists, if the data file has no column associated with the cruise/dataset name, or if unable to rename or
     *         update the data or info files
     */
    public void renameDatasetFiles(String oldName, String newName) throws IllegalArgumentException {
        // Get the dataset IDs for the given names
        String oldId = DashboardServerUtils.getDatasetIDFromName(oldName);
        String newId = DashboardServerUtils.getDatasetIDFromName(newName);
        // Verify old files exist and new files do not
        File oldDataFile = datasetDataFile(oldId);
        if ( !oldDataFile.exists() )
            throw new IllegalArgumentException("Data file for " + oldId + " does not exist");
        File oldInfoFile = datasetInfoFile(oldId);
        if ( !oldInfoFile.exists() )
            throw new IllegalArgumentException("Info file for " + oldId + " does not exist");
        File newDataFile = datasetDataFile(newId);
        if ( newDataFile.exists() )
            throw new IllegalArgumentException("Data file for " + newId + " already exists");
        File newInfoFile = datasetInfoFile(newId);
        if ( newInfoFile.exists() )
            throw new IllegalArgumentException("Info file for " + newId + " already exists");

        // Make sure the parent directory for the new files exists
        File parentFile = newDataFile.getParentFile();
        if ( !parentFile.exists() )
            parentFile.mkdirs();

        // Easiest is to read all the data, modify the cruise/dataset name in the data,
        // move the files, and save under the new dataset ID
        DashboardDatasetData datasetData = getDatasetDataFromFiles(oldId, 0, -1);
        datasetData.setDatasetId(newId);
        int k = -1;
        int nameIdx = -1;
        for (DataColumnType type : datasetData.getDataColTypes()) {
            k++;
            if ( DashboardServerUtils.DATASET_NAME.typeNameEquals(type) ) {
                nameIdx = k;
                break;
            }
        }
        if ( nameIdx < 0 )
            throw new IllegalArgumentException("Unexpected error: no column associated with the cruise/dataset name");
        for (ArrayList<String> dataVals : datasetData.getDataValues()) {
            dataVals.set(k, newName);
        }

        // Move the old dataset files to the new location and name
        String commitMsg = "Rename from " + oldName + " to " + newName;
        try {
            moveVersionedFile(oldDataFile, newDataFile, commitMsg);
            moveVersionedFile(oldInfoFile, newInfoFile, commitMsg);
        } catch ( SVNException ex ) {
            throw new IllegalArgumentException("Problems renaming the dateaset files from " +
                    oldId + " to " + newId + ": " + ex.getMessage());
        }

        // Save under the new dataset
        saveDatasetInfoToFile(datasetData, commitMsg);
        saveDatasetDataToFile(datasetData, commitMsg);
    }

    /**
     * Verify a user can overwrite or delete a dataset.  This checks the submission state of the dataset as well as
     * ownership of the dataset. If not permitted, an IllegalArgumentException is thrown with reason for the failure.
     *
     * @param datasetId
     *         ID of the data to check
     * @param username
     *         user wanting to overwrite or delete the dataset
     *
     * @return the dataset being overwritten or deleted; never null
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if there are problems reading the dataset properties file
     */
    public DashboardDataset verifyOkayToDeleteDataset(String datasetId, String username)
            throws IllegalArgumentException {
        // Get the dataset information
        DashboardDataset dataset = getDatasetFromInfoFile(datasetId);
        // Check if the dataset is in a submitted or published state
        if ( !Boolean.TRUE.equals(dataset.isEditable()) )
            throw new IllegalArgumentException("dataset status is " + dataset.getSubmitStatus());
        // Check if the user has permission to delete the dataset
        try {
            String owner = dataset.getOwner();
            if ( !DashboardConfigStore.get(false).userManagesOver(username, owner) )
                throw new IllegalArgumentException("dataset owner is " + owner);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("unexpected failure to get the dashboad configuration");
        }
        return dataset;
    }

    /**
     * Deletes the information and data files for a dataset after verifying the user is permitted to delete this
     * dataset.
     *
     * @param datasetId
     *         ID of the dataset to delete
     * @param username
     *         user wanting to delete the dataset
     * @param deleteMetadata
     *         also delete metadata and additional documents?
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is not valid, if there were problems access the dataset files, if the user is not
     *         permitted to delete the dataset, or if there were problems deleting a file or committing the deletion in
     *         version control
     */
    public void deleteDatasetFiles(String datasetId, String username,
            Boolean deleteMetadata) throws IllegalArgumentException {
        // Verify this cruise can be deleted
        DashboardDataset dataset;
        try {
            dataset = verifyOkayToDeleteDataset(datasetId, username);
        } catch ( IllegalArgumentException ex ) {
            throw new IllegalArgumentException("Not permitted to delete dataset " +
                    datasetId + ": " + ex.getMessage());
        }

        DashboardConfigStore configStore;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected failure to get the dashboard configuration");
        }

        // If they exist, delete the DSG files and notify ERDDAP
        DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
        if ( dsgHandler.deleteCruise(datasetId) )
            dsgHandler.flagErddap(true, true);

        // If it exists, delete the messages file
        configStore.getCheckerMsgHandler().deleteMsgsFile(datasetId);

        // Delete the cruise data file
        String commitMsg = "Cruise file for " + datasetId + " owned by " +
                dataset.getOwner() + " deleted by " + username;
        try {
            deleteVersionedFile(datasetDataFile(datasetId), commitMsg);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems deleting the cruise data file: " +
                    ex.getMessage());
        }
        // Delete the cruise information file
        try {
            deleteVersionedFile(datasetInfoFile(datasetId), commitMsg);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems deleting the cruise information file: " +
                    ex.getMessage());
        }

        if ( deleteMetadata ) {
            // Delete the metadata and additional documents associated with this cruise
            MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
            try {
                metadataHandler.deleteMetadata(username, datasetId, DashboardUtils.OME_FILENAME);
            } catch ( Exception ex ) {
                // Ignore - may not exist
                ;
            }
            try {
                metadataHandler.deleteMetadata(username, datasetId, DashboardUtils.PI_OME_FILENAME);
            } catch ( Exception ex ) {
                // Ignore - may not exist
                ;
            }
            for (String mdataTitle : dataset.getAddlDocs()) {
                String filename = DashboardMetadata.splitAddlDocsTitle(mdataTitle)[0];
                try {
                    metadataHandler.deleteMetadata(username, datasetId, filename);
                } catch ( Exception ex ) {
                    // Ignore
                    ;
                }
            }
        }
    }

    /**
     * Assigns a DashboardDataset (or DashboardDatasetData) from the dataset properties file.  The ID of the dataset is
     * obtained from the DashboardDataset.
     *
     * @param dataset
     *         assign dataset information here
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, or if the dataset properties file is invalid
     * @throws FileNotFoundException
     *         if the dataset properties file does not exist
     * @throws IOException
     *         if there are problems reading the dataset properties file
     */
    private void assignDatasetFromInfoFile(DashboardDataset dataset)
            throws IllegalArgumentException, FileNotFoundException, IOException {
        // Get the dataset properties file
        File infoFile = datasetInfoFile(dataset.getDatasetId());
        // Get the properties given in this file
        Properties cruiseProps = new Properties();
        FileReader infoReader = new FileReader(infoFile);
        try {
            cruiseProps.load(infoReader);
        } finally {
            infoReader.close();
        }

        // Assign the DashboardDataset from the values in the properties file

        // Owner of the data file
        String value = cruiseProps.getProperty(DATA_OWNER_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    DATA_OWNER_ID + " given in " + infoFile.getPath());
        dataset.setOwner(value);

        // version
        value = cruiseProps.getProperty(VERSION_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    VERSION_ID + " given in " + infoFile.getPath());
        dataset.setVersion(value);

        // Name of uploaded file
        value = cruiseProps.getProperty(UPLOAD_FILENAME_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    UPLOAD_FILENAME_ID + " given in " + infoFile.getPath());
        dataset.setUploadFilename(value);

        // Time of uploading the file
        value = cruiseProps.getProperty(UPLOAD_TIMESTAMP_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    UPLOAD_TIMESTAMP_ID + " given in " + infoFile.getPath());
        dataset.setUploadTimestamp(value);

        // DOIs
        value = cruiseProps.getProperty(SOURCE_DOI_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    SOURCE_DOI_ID + " given in " + infoFile.getPath());
        dataset.setSourceDOI(value);
        value = cruiseProps.getProperty(ENHANCED_DOI_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    ENHANCED_DOI_ID + " given in " + infoFile.getPath());
        dataset.setEnhancedDOI(value);

        // Data check status
        value = cruiseProps.getProperty(DATA_CHECK_STATUS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    DATA_CHECK_STATUS_ID + " given in " + infoFile.getPath());
        dataset.setDataCheckStatus(value);

        // OME metadata timestamp
        value = cruiseProps.getProperty(OME_TIMESTAMP_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    OME_TIMESTAMP_ID + " given in " + infoFile.getPath());
        dataset.setOmeTimestamp(value);

        // Metadata documents
        value = cruiseProps.getProperty(ADDL_DOC_TITLES_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    ADDL_DOC_TITLES_ID + " given in " + infoFile.getPath());
        dataset.setAddlDocs(DashboardUtils.decodeStringTreeSet(value));

        // Submit status
        value = cruiseProps.getProperty(SUBMIT_STATUS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    SUBMIT_STATUS_ID + " given in " + infoFile.getPath());
        dataset.setSubmitStatus(value);

        // Archive status
        value = cruiseProps.getProperty(ARCHIVE_STATUS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    ARCHIVE_STATUS_ID + " given in " + infoFile.getPath());
        dataset.setArchiveStatus(value);

        // Date of request to archive data and metadata
        value = cruiseProps.getProperty(ARCHIVAL_DATE_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    ARCHIVAL_DATE_ID + " given in " + infoFile.getPath());
        dataset.setArchiveDate(value);

        // Number of rows of data (number of samples)
        value = cruiseProps.getProperty(NUM_DATA_ROWS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    NUM_DATA_ROWS_ID + " given in " + infoFile.getPath());
        try {
            dataset.setNumDataRows(Integer.parseInt(value));
        } catch ( NumberFormatException ex ) {
            throw new IllegalArgumentException(ex);
        }

        // Number of error messages
        value = cruiseProps.getProperty(NUM_ERROR_ROWS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    NUM_ERROR_ROWS_ID + " given in " + infoFile.getPath());
        try {
            dataset.setNumErrorRows(Integer.parseInt(value));
        } catch ( NumberFormatException ex ) {
            throw new IllegalArgumentException(ex);
        }

        // Number of warning messages
        value = cruiseProps.getProperty(NUM_WARN_ROWS_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    NUM_WARN_ROWS_ID + " given in " + infoFile.getPath());
        try {
            dataset.setNumWarnRows(Integer.parseInt(value));
        } catch ( NumberFormatException ex ) {
            throw new IllegalArgumentException(ex);
        }

        // User-provided data column names
        value = cruiseProps.getProperty(USER_COLUMN_NAMES_ID);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    USER_COLUMN_NAMES_ID + " given in " + infoFile.getPath());
        dataset.setUserColNames(DashboardUtils.decodeStringArrayList(value));
        int numCols = dataset.getUserColNames().size();

        // Data column type information
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
            DashDataType<?> dataType = userTypes.getDataType(colTypeNames.get(k));
            if ( dataType == null )
                throw new IllegalArgumentException("unknown data type \"" + colTypeNames.get(k) + "\"");
            DataColumnType dctype = dataType.duplicate();
            if ( !dctype.setSelectedUnit(colTypeUnits.get(k)) )
                throw new IllegalArgumentException("unknown unit \"" + colTypeUnits.get(k) +
                        "\" for data type \"" + dctype.getVarName() + "\"");
            dctype.setSelectedMissingValue(colMissValues.get(k));
            dataColTypes.add(dctype);
        }
        dataset.setDataColTypes(dataColTypes);

        value = cruiseProps.getProperty(CHECKER_FLAGS);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    CHECKER_FLAGS + " given in " + infoFile.getPath());
        TreeSet<QCFlag> checkerFlags = DashboardServerUtils.decodeQCFlagSet(value);
        dataset.setCheckerFlags(checkerFlags);

        value = cruiseProps.getProperty(USER_FLAGS);
        if ( value == null )
            throw new IllegalArgumentException("No property value for " +
                    USER_FLAGS + " given in " + infoFile.getPath());
        TreeSet<QCFlag> userFlags = DashboardServerUtils.decodeQCFlagSet(value);
        dataset.setUserFlags(userFlags);
    }

    /**
     * Assigns a DashboardDatasetData with data read from the given buffered reader.  The data should be tab-separated
     * values.  Empty lines are ignored.  The first (non-empty) line should be a header line of data column names with
     * units.  The expected number of data columns is determined from this line but otherwise is ignored.  The remaining
     * (non-empty) lines should be data lines with exactly the same number of values as there are column names.
     *
     * @param datasetData
     *         assign column names with units and data to this object
     * @param datasetReader
     *         read data from here
     * @param firstDataRow
     *         index of the first data row to return; to return all data for this dataset, set to zero
     * @param numDataRows
     *         maximum number of data rows to return; if negative, no limit is applied (all remaining data rows are
     *         returned)
     *
     * @throws IOException
     *         if reading from datasetReader throws one, if there is a blank data column name with units, if there is an
     *         inconsistent number of data values, if there are too few data columns read
     */
    private void assignDataFromInput(DashboardDatasetData datasetData, BufferedReader datasetReader,
            int firstDataRow, int numDataRows) throws IOException {
        // data row numbers
        ArrayList<Integer> rowNums = new ArrayList<Integer>();
        // data values
        ArrayList<ArrayList<String>> dataValues = new ArrayList<ArrayList<String>>();
        // Create the parser for the data lines
        CSVFormat format = CSVFormat.EXCEL.withIgnoreSurroundingSpaces()
                                          .withIgnoreEmptyLines()
                                          .withDelimiter('\t');
        CSVParser dataParser = new CSVParser(datasetReader, format);
        try {
            int numDataColumns = 0;
            boolean firstLine = true;
            int dataRowNum = 0;
            for (CSVRecord record : dataParser) {
                if ( firstLine ) {
                    // Column headers
                    numDataColumns = record.size();
                    if ( numDataColumns < MIN_NUM_DATA_COLUMNS )
                        throw new IOException("Too few data column (" + numDataColumns + ") read");
                    if ( numDataRows == 0 ) {
                        // No reading of the data requested - done
                        break;
                    }
                    firstLine = false;
                    continue;
                }

                // Data line
                if ( record.size() != numDataColumns )
                    throw new IOException("Inconsistent number of data columns (" + record.size() + " instead of " +
                            numDataColumns + ") for measurement " + dataParser.getRecordNumber() + ":\n    " +
                            rebuildDataline(record, '\t'));

                dataRowNum++;
                if ( dataRowNum > firstDataRow ) {
                    ArrayList<String> datavals = new ArrayList<String>(numDataColumns);
                    for (String val : record) {
                        datavals.add(val);
                    }
                    rowNums.add(dataRowNum);
                    dataValues.add(datavals);
                    if ( (numDataRows > 0) && (dataValues.size() == numDataRows) )
                        break;
                }
            }
        } finally {
            dataParser.close();
        }

        datasetData.setRowNums(rowNums);
        datasetData.setDataValues(dataValues);
    }

    /**
     * Returns a version of the string that was parsed to create the given record but using the given spacer between the
     * entries in the record.
     *
     * @param record
     *         record to use
     * @param spacer
     *         spacer to use
     *
     * @return recreated string for this record
     */
    private String rebuildDataline(CSVRecord record, char spacer) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String val : record) {
            if ( !val.isEmpty() ) {
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

}
