package gov.noaa.pmel.tmap.las.ui;

import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;
import gov.noaa.pmel.tmap.las.product.server.LASAction;
import gov.noaa.pmel.tmap.las.service.TemplateTool;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Save user-assigned regional/global dataset QC flags.
 */
public class SaveQC extends LASAction {

    private static final long serialVersionUID = -535623169838258140L;

    private static final Logger log = LoggerFactory.getLogger(SaveQC.class.getName());
    private static final String DATABASE_CONFIG = "DatabaseBackendConfig.xml";
    private static final String ERROR = "error";
    private static final String QC = "qc";
    // TODO: get the database name from the <database_access><db_name> field for this data collection
    private static final String DATABASE_NAME = "SOCATFlags";

    private String socatQCVersion;
    private DsgNcFileHandler dsgHandler;
    private DatabaseRequestHandler databaseHandler;

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
    public SaveQC() throws IllegalArgumentException, SQLException, LASException {
        super();
        log.debug("Initializing SaveQC from database configuraton");

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
        // Logging this sets off security alarm bells...         log.debug("selectPassword=" + selectPassword);
        String updateUsername = dbParams.getAttributeValue("updateUser");
        log.debug("updateUsername=" + updateUsername);
        String updatePassword = dbParams.getAttributeValue("updatePassword");
        // Logging this sets off security alarm bells...         log.debug("updatePassword=" + updatePassword);
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
        if ( (dsgFileDir != null) && (decDsgFileDir != null) &&
                (erddapDsgFlag != null) && (erddapDecDsgFlag != null) ) {
            // FerretConfig object not needed for just assigning QC flags
            // Actual metadata and data types not needed for updating QC flags
            dsgHandler = new DsgNcFileHandler(dsgFileDir, decDsgFileDir, erddapDsgFlag, erddapDecDsgFlag,
                    null, null, null, null, null);
            log.debug("DSG file handler configuration successful");
        }
        else {
            dsgHandler = null;
            log.debug("DSG file handler not created");
        }
    }

    @Override
    public String execute() throws Exception {
        // Make sure this is configured for setting WOCE flags
        if ( (socatQCVersion == null) || (dsgHandler == null) || (databaseHandler == null) ) {
            logerror(request, "LAS not configured to allow editing of QC flags", "Illegal action");
            return ERROR;
        }

        // Get the request from the query parameter.
        String requestXML = request.getParameter("xml");

        // LASConfig lasConfig = (LASConfig)servlet.getServletContext().getAttribute(LASConfigPlugIn.LAS_CONFIG_KEY);
        // Same for the ServerConfig
        // ServerConfig serverConfig = (ServerConfig)servlet.getServletContext().getAttribute(ServerConfigPlugIn.SERVER_CONFIG_KEY);
        // Get the global cache object.
        // Cache cache = (Cache) servlet.getServletContext().getAttribute(ServerConfigPlugIn.CACHE_KEY);

        // Get the lasRequest object from the request.  It was placed there by the RequestInputFilter.
        LASUIRequest lasRequest = (LASUIRequest) request.getAttribute("las_request");

        // If it wasn't built in the filter try to build it here
        if ( lasRequest == null && (requestXML != null && !requestXML.equals("")) ) {
            try {
                String temp = JDOMUtils.decode(requestXML, "UTF-8");
                requestXML = temp;
            } catch ( UnsupportedEncodingException e ) {
                LASAction.logerror(request, "Error decoding the XML request query string.", e);
                return ERROR;
            }

            // Create a lasRequest object.
            lasRequest = new LASUIRequest();
            try {
                JDOMUtils.XML2JDOM(requestXML, lasRequest);
                // Set the lasRequest object in the HttpServletRequest so the product server does not have to rebuild it.
                request.setAttribute("las_request", lasRequest);
            } catch ( Exception e ) {
                LASAction.logerror(request, "Error parsing the request XML. ", e);
                return ERROR;
            }
        }

        // getProperty returns a trimmed string; an empty string if not found
        String expocode = lasRequest.getProperty("qc", "cruise_ID");
        String regionIDs = lasRequest.getProperty("qc", "region_IDs");
        String flag = lasRequest.getProperty("qc", "flag");
        String comment;
        try {
            String encodedComment = lasRequest.getProperty("qc", "comment");
            comment = new String(DatatypeConverter.parseHexBinary(encodedComment), "UTF-16");
        } catch ( Exception ex ) {
            logerror(request, "Unable to get the comment for QC flagging", ex);
            return ERROR;
        }
        String reviewer = lasRequest.getProperty("qc", "reviewer");
        // String override = lasRequest.getProperty("qc", "override");

        try {
            String upperExpocode = DashboardServerUtils.checkDatasetID(expocode);
            expocode = upperExpocode;
        } catch ( Exception ex ) {
            logerror(request, "Invalid expocode '" + expocode + "' specified", "");
            return ERROR;
        }
        if ( regionIDs.isEmpty() ) {
            logerror(request, "No region ID specified", "");
            return ERROR;
        }
        if ( flag.isEmpty() ) {
            // Empty flag is acceptable - just a QC comment
            flag = " ";
        }
        if ( comment.isEmpty() ) {
            logerror(request, "No comment provided", "");
            return ERROR;
        }

        // Validate the reviewer assigning this QC flag
        String username;
        try {
            log.debug("Validating SaveQC reviewer");
            username = request.getUserPrincipal().getName();
        } catch ( Exception ex ) {
            logerror(request, "Unable to get the username for QC flagging", ex);
            return ERROR;
        }
        if ( (username == null) || !username.equalsIgnoreCase(reviewer) ) {
            logerror(request, "Invalid username " + username + " for reviewer " + reviewer, "");
            return ERROR;
        }

        // If a global flag is in the list of regions,
        // only assign the global flag and ignore other regions
        if ( regionIDs.contains("G") ) {
            regionIDs = "G";
        }

        // Create the QC event
        ArrayList<QCEvent> qcEvents = new ArrayList<QCEvent>(regionIDs.length());
        Date now = new Date();
        for (int k = 0; k < regionIDs.length(); k++) {
            QCEvent qc = new QCEvent();
            qc.setVersion(socatQCVersion);
            qc.setDatasetId(expocode);
            qc.setUsername(username);
            qc.setComment(comment);
            qc.setFlagValue(flag);
            qc.setFlagDate(now);
            qc.setRegionId(regionIDs.substring(k, k + 1));
            qcEvents.add(qc);
        }
        // Add the QC events to the database
        try {
            databaseHandler.addDatasetQCEvents(qcEvents);
            log.info("QC events " + qcEvents.toString() + " added to the database");
        } catch ( Exception ex ) {
            logerror(request, "Unable to record the QC events " +
                    qcEvents.toString() + " in the database", ex);
            return ERROR;
        }

        // Get the new QC status from the database
        DatasetQCStatus qcStatus;
        try {
            qcStatus = databaseHandler.getDatasetQCFlag(expocode);
            log.debug("dataset QC status '" + qcStatus.toString() + "' returned by the database handler");
        } catch ( Exception ex ) {
            logerror(request, "Unable to obtain the new QC status from the database ", ex);
            return ERROR;
        }

        // Get the new version number with new/updated marker from the database
        // The number should be socatQCVersion, but don't know if it should be followed by an N or U
        String version;
        try {
            version = databaseHandler.getVersionStatus(expocode);
            log.debug("version '" + version + "' returned by the database handler");
        } catch ( Exception ex ) {
            logerror(request, "Unable to obtain the new version number from the database ", ex);
            return ERROR;
        }

        // Update the QC flag in the DSG files
        try {
            dsgHandler.updateDatasetQCFlagAndVersionStatus(expocode, qcStatus, version);
            log.info("DSG files update with QC status " + qcStatus.toString() + " and version " + version);
        } catch ( Exception ex ) {
            logerror(request, "Unable to update the DSG files with the QC status " +
                    qcStatus.toString() + " and version " + version, ex);
            return ERROR;
        }

        return QC;
    }

}
