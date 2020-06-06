package gov.noaa.pmel.tmap.las.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import gov.noaa.pmel.dashboard.datatype.DashDataType;
import gov.noaa.pmel.dashboard.datatype.KnownDataTypes;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.service.TemplateTool;
import org.jdom.Element;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Save user-assigned WOCE (data QC) flags.
 */
public class SaveEdits extends LASAction {

    private static final long serialVersionUID = -2069025251560349247L;

    private static Logger log = LoggerFactory.getLogger(SaveEdits.class.getName());
    private static final String DATABASE_CONFIG = "DatabaseBackendConfig.xml";
    // TODO: get the database name from the <database_access><db_name> field for this data collection
    private static final String DATABASE_NAME = "SOCATFlags";

    private static String ERROR = "error";
    private static String EDITS = "edits";


    private String socatQCVersion;
    private DsgNcFileHandler dsgHandler;
    private DatabaseRequestHandler databaseHandler;
    private TreeSet<DashDataType<?>> dataTypesSet;

    /**
     * Creates with the SOCAT UploadDashboard DsgNcFileHandler and DatabaseRequestHandler
     *
     * @throws IllegalArgumentException
     *         if parameters are invalid
     * @throws SQLException
     *         if one is thrown connecting to the database
     * @throws LASException
     *         if unable to get the database parameters
     */
    public SaveEdits() throws IllegalArgumentException, SQLException, LASException {
        super();
        log.debug("Initializing SaveEdits from database configuraton");

        Element dbParams;
        try {
            LASDocument dbConfig = new LASDocument();
            TemplateTool tempTool = new TemplateTool("database", DATABASE_CONFIG);
            JDOMUtils.XML2JDOM(tempTool.getConfigFile(), dbConfig);
            dbParams = dbConfig.getElementByXPath(
                    "/databases/database[@name='" + DATABASE_NAME + "']");
        } catch ( Exception ex ) {
            throw new LASException(
                    "Could not parse " + DATABASE_CONFIG + ": " + ex.toString());
        }
        if ( dbParams == null )
            throw new LASException("No database definition found for database " +
                    DATABASE_NAME + " in " + DATABASE_CONFIG);

        String databaseDriver = dbParams.getAttributeValue("driver");
        log.debug("driver=" + databaseDriver);
        String databaseUrl = dbParams.getAttributeValue("connectionURL");
        log.debug("databaseUrl=" + databaseUrl);
        String selectUsername = dbParams.getAttributeValue("user");
        log.debug("selectUsername=" + selectUsername);
        String selectPassword = dbParams.getAttributeValue("password");
        // Logging this sets off security alarm bells...                                   log.debug("selectPassword=" + selectPassword);
        String updateUsername = dbParams.getAttributeValue("updateUser");
        log.debug("updateUsername=" + updateUsername);
        String updatePassword = dbParams.getAttributeValue("updatePassword");
        // Logging this sets off security alarm bells...                                   log.debug("updatePassword=" + updatePassword);
        if ( (updateUsername != null) && (updatePassword != null) ) {
            // The database URLs in the LAS config files do not have the jdbc: prefix
            databaseHandler = new DatabaseRequestHandler(databaseDriver, "jdbc:" + databaseUrl,
                    selectUsername, selectPassword, updateUsername, updatePassword);
            log.debug("database request handler configuration successful");
        }
        else {
            databaseHandler = null;
            log.debug("database request handler not created");
        }

        socatQCVersion = dbParams.getAttributeValue("socatQCVersion");
        log.debug("socatQCVersion=" + socatQCVersion);

        String dsgFileDir = dbParams.getAttributeValue("dsgFileDir");
        log.debug("dsgFileDir=" + dsgFileDir);
        String decDsgFileDir = dbParams.getAttributeValue("decDsgFileDir");
        log.debug("decDsgFileDir=" + decDsgFileDir);
        String erddapDsgFlag = dbParams.getAttributeValue("erddapDsgFlag");
        log.debug("erddapDsgFlag=" + erddapDsgFlag);
        String erddapDecDsgFlag = dbParams.getAttributeValue("erddapDecDsgFlag");
        log.debug("erddapDecDsgFlag=" + erddapDecDsgFlag);
        String dataTypesFilename = dbParams.getAttributeValue("addDataTypes");
        log.debug("addDataTypes=" + dataTypesFilename);
        String ferretConfigFilename = dbParams.getAttributeValue("ferretConfig");
        log.debug("ferretConfig=" + ferretConfigFilename);
        if ( (dsgFileDir != null) && (decDsgFileDir != null) &&
                (erddapDsgFlag != null) && (erddapDecDsgFlag != null) &&
                (ferretConfigFilename != null) ) {
            // Actual metadata and data types not needed for just assigning WOCE flags, but
            // data types needed for converting upper-cased names to actual variable names.
            // FerretConfig needed for decimating the full-data DSG file after assigning WOCE flags
            KnownDataTypes dataTypes = new KnownDataTypes();
            dataTypes.addStandardTypesForDataFiles();
            try {
                Properties typeProps = new Properties();
                FileInputStream input = new FileInputStream(dataTypesFilename);
                try {
                    typeProps.load(input);
                } finally {
                    input.close();
                }
                dataTypes.addTypesFromProperties(typeProps, DashDataType.Role.FILE_DATA, null);
            } catch ( Exception ex ) {
                log.debug("adding data types possible problem: " + ex.getMessage());
            }
            log.debug("dataTypes=" + dataTypes.toString());
            dataTypesSet = dataTypes.getKnownTypesSet();

            // Ferret configuration
            FerretConfig ferretConf;
            try {
                InputStream stream = new FileInputStream(ferretConfigFilename);
                try {
                    SAXBuilder sb = new SAXBuilder();
                    Document jdom = sb.build(stream);
                    ferretConf = new FerretConfig();
                    ferretConf.setRootElement(jdom.getRootElement().clone());
                } finally {
                    stream.close();
                }
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("ferret configuration problem: " + ex.getMessage(), ex);
            }
            log.debug("ferretConfig=" + ferretConf.toString());

            dsgHandler = new DsgNcFileHandler(dsgFileDir, decDsgFileDir, erddapDsgFlag, erddapDecDsgFlag,
                    ferretConf, null, dataTypes, null, null);
            log.debug("DSG file handler configuration successful");
        }
        else {
            dataTypesSet = null;
            dsgHandler = null;
            log.debug("DSG file handler not created");
        }
    }

    @Override
    public String execute() throws Exception {
        // Make sure this is configured for setting WOCE flags
        if ( (socatQCVersion == null) || (dsgHandler == null) || (databaseHandler == null) ) {
            logerror(request, "LAS not configured to allow editing of WOCE flags", "Illegal action");
            return ERROR;
        }

        // Parser to convert Ferret date strings into Date objects
        SimpleDateFormat fullDateParser = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        fullDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Get the username of the reviewer assigning these WOCE flags
        String username;
        try {
            log.debug("Assigning SaveEdits username");
            username = request.getUserPrincipal().getName();
        } catch ( Exception ex ) {
            logerror(request, "Unable to get the username for WOCE flagging", ex);
            return ERROR;
        }

        JsonStreamParser parser = new JsonStreamParser(request.getReader());
        JsonObject message = (JsonObject) parser.next();

        // LAS temporary DSG file to update
        String tempname;
        try {
            tempname = message.get("temp_file").getAsString();
        } catch ( Exception ex ) {
            logerror(request, "Unable to get temp_file for WOCE flagging", ex);
            return ERROR;
        }

        // WOCE flag comment
        String comment;
        try {
            String encodedComment = message.get("comment").getAsString();
            comment = new String(DatatypeConverter.parseHexBinary(encodedComment), "UTF-16");
        } catch ( Exception ex ) {
            logerror(request, "Unable to get the comment for WOCE flagging", ex);
            return ERROR;
        }

        // List of data points getting the WOCE flag
        JsonArray edits;
        try {
            edits = (JsonArray) message.get("edits");
            if ( edits.size() < 1 )
                throw new IllegalArgumentException("No edits given");
        } catch ( Exception ex ) {
            logerror(request, "Unable to get the edits for WOCE flagging", ex);
            return ERROR;
        }

        // Create the list of (incomplete) data locations for the WOCE event
        String expocode = null;
        String woceFlag = null;
        String woceName = null;
        String dataName = null;
        ArrayList<DataLocation> locations = new ArrayList<DataLocation>(edits.size());
        try {
            for (JsonElement rowValues : edits) {
                DataLocation datumLoc = new DataLocation();
                for (Entry<String,JsonElement> rowEntry : ((JsonObject) rowValues).entrySet()) {
                    // Neither the name nor the value should be null.
                    // Because of going through Ferret, everything will be uppercase
                    // but just to be sure....
                    String name = rowEntry.getKey().trim().toUpperCase(Locale.ENGLISH);
                    String value = rowEntry.getValue().getAsString().trim().toUpperCase(Locale.ENGLISH);
                    if ( name.equals("EXPOCODE") || name.equals("EXPOCODE_") ) {
                        if ( expocode == null )
                            expocode = value;
                        else if ( !expocode.equals(value) )
                            throw new IllegalArgumentException("Mismatch of expocodes; " +
                                    "previous: '" + expocode + "'; current: '" + value + "'");
                    }
                    else if ( name.equals("DATE") ) {
                        Date dataDate = fullDateParser.parse(value);
                        datumLoc.setDataDate(dataDate);
                    }
                    else if ( name.equals("LONGITUDE") ) {
                        Double longitude = Double.parseDouble(value);
                        datumLoc.setLongitude(longitude);
                    }
                    else if ( name.equals("LATITUDE") ) {
                        Double latitude = Double.parseDouble(value);
                        datumLoc.setLatitude(latitude);
                    }
                    else if ( name.startsWith("WOCE_") ) {
                        // Name and value of the WOCE flag to assign
                        if ( woceName == null )
                            woceName = name;
                        else if ( !woceName.equals(name) )
                            throw new IllegalArgumentException("Mismatch of WOCE names; " +
                                    "previous: '" + woceName + "'; current: '" + name + "'");
                        if ( value.length() != 1 )
                            throw new IllegalArgumentException("Invalid WOCE flag value '" + value + "'");
                        if ( woceFlag == null )
                            woceFlag = value;
                        else if ( !woceFlag.equals(value) )
                            throw new IllegalArgumentException("Mismatch of WOCE flags; " +
                                    "previous: '" + woceFlag + "'; current: '" + value + "'");
                    }
                    else {
                        // Assume it is the data variable name.
                        // Note that WOCE from just lat/lon/date plots will not have this column
                        if ( dataName == null )
                            dataName = name;
                        else if ( !dataName.equals(name) )
                            throw new IllegalArgumentException("Mismatch of data names; " +
                                    "previous: '" + dataName + "'; current: '" + name + "'");
                        Double dataValue = Double.parseDouble(value);
                        datumLoc.setDataValue(dataValue);
                    }
                }
                locations.add(datumLoc);
            }
        } catch ( Exception ex ) {
            logerror(request, "Problems interpreting the WOCE flags", ex);
            if ( expocode != null )
                logerror(request, "expocode = " + expocode, "");
            if ( dataName != null )
                logerror(request, "dataName = " + dataName, "");
            if ( woceName != null )
                logerror(request, "woceName = " + woceName, "");
            if ( woceFlag != null )
                logerror(request, "woceFlag = " + woceFlag, "");
            return ERROR;
        }

        if ( expocode == null ) {
            logerror(request, "No EXPOCODE given in the WOCE flags", "");
            return ERROR;
        }
        if ( woceName == null ) {
            logerror(request, "No WOCE flag name given in the WOCE flags", "");
            return ERROR;
        }
        else {
            String varName = null;
            for (DashDataType dtype : dataTypesSet) {
                if ( dtype.typeNameEquals(woceName) ) {
                    varName = dtype.getVarName();
                    break;
                }
            }
            if ( varName == null ) {
                logerror(request, "Unknown WOCE flag name '" + woceName + "'", "");
                return ERROR;
            }
            woceName = varName;
        }
        if ( woceFlag == null ) {
            logerror(request, "No WOCE flag value given in the WOCE flags", "");
            return ERROR;
        }
        if ( dataName != null ) {
            // data variable name probably upper-cased, so get actual-cased name
            String varName = null;
            for (DashDataType dtype : dataTypesSet) {
                if ( dtype.typeNameEquals(dataName) ) {
                    varName = dtype.getVarName();
                    break;
                }
            }
            if ( varName == null ) {
                logerror(request, "Unknown data variable '" + dataName + "'", "");
                return ERROR;
            }
            dataName = varName;
        }

        // Create the WOCE event without row numbers
        DataQCEvent woceEvent = new DataQCEvent();
        woceEvent.setVersion(socatQCVersion);
        woceEvent.setUsername(username);
        woceEvent.setComment(comment);
        woceEvent.setDatasetId(expocode);
        woceEvent.setFlagName(woceName);
        woceEvent.setVarName(dataName);
        woceEvent.setFlagValue(woceFlag);
        woceEvent.setFlagDate(new Date());
        woceEvent.setLocations(locations);

        // Update the full-data DSG file with the WOCE flags, filling in the missing data row numbers,
        // and regenerate the decimated DSG file from the full-data DSG fiile.
        ArrayList<DataLocation> unidentified;
        try {
            unidentified = dsgHandler.updateDataQCFlags(woceEvent, true);
            log.debug("full-data DSG file updated");
        } catch ( Exception ex ) {
            logerror(request, "Unable to update the full-data DSG file with the WOCE flags", ex);
            logerror(request, "expocode = " + expocode +
                    "; dataName = " + dataName +
                    "; woceFlag = " + woceFlag, "");
            return ERROR;
        }
        if ( !unidentified.isEmpty() ) {
            logerror(request, "Unable to identify the following data points: ", "");
            for (DataLocation loc : unidentified) {
                logerror(request, "    " + loc.toString(), "");
            }
            return ERROR;
        }

        try {
            DsgNcFile tempFile = new DsgNcFile(tempname);
            // Ignore any unidentified data points - temp file may not be complete
            tempFile.updateDataQCFlags(woceEvent, false);
            log.debug("temporary DSG file updated");
        } catch ( Exception ex ) {
            logerror(request, "Unable to update the temporary DSG file with the WOCE flags", ex);
            logerror(request, "expocode = " + expocode +
                    "; dataName = " + dataName +
                    "; woceFlag = " + woceFlag, "");
            return ERROR;
        }

        // Save the WOCE event with the row numbers to the database
        try {
            databaseHandler.addDataQCEvent(Collections.singletonList(woceEvent));
            log.debug("WOCE event added to the database");
        } catch ( Exception ex ) {
            logerror(request, "Unable to record the WOCE event in the database", ex);
            logerror(request, "expocode = " + expocode +
                    "; dataName = " + dataName +
                    "; woceFlag = " + woceFlag, "");
            return ERROR;
        }

        log.info("Assigned WOCE event (also updated " + tempname + "): \n" +
                woceEvent.toString());

        request.setAttribute("expocode", expocode);
        return EDITS;
    }

}
