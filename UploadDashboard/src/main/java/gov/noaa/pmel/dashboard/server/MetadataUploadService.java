package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.qc.QCEvent;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Service to receive the uploaded metadata file from the client
 *
 * @author Karl Smith
 */
public class MetadataUploadService extends HttpServlet {

    private static final long serialVersionUID = -1458504704372812166L;

    private ServletFileUpload metadataUpload;

    public MetadataUploadService() {
        File servletTmpDir;
        try {
            // Get the temporary directory used by the servlet
            servletTmpDir = (File) getServletContext().getAttribute(
                    "javax.servlet.context.tempdir");
        } catch ( Exception ex ) {
            // Just use the default system temp dir (less secure)
            servletTmpDir = null;
        }
        // Create a disk file item factory for processing requests
        DiskFileItemFactory factory = new DiskFileItemFactory();
        if ( servletTmpDir != null ) {
            // Use the temporary directory for the servlet for large files
            factory.setRepository(servletTmpDir);
        }
        // Create the file uploader using this factory
        metadataUpload = new ServletFileUpload(factory);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Verify the post has the correct encoding
        if ( !ServletFileUpload.isMultipartContent(request) ) {
            sendErrMsg(response, "Invalid request contents format for this service.");
            return;
        }

        // Get the contents from the post request
        String username = null;
        try {
            username = DashboardServerUtils.cleanUsername(request.getUserPrincipal().getName().trim());
        } catch ( Exception ex ) {
            // leave username null for error message later
        }

        // Get the contents from the post request
        String datasetIds = null;
        String uploadTimestamp = null;
        String omeIndicator = null;
        FileItem metadataItem = null;
        try {
            Map<String,List<FileItem>> paramMap = metadataUpload.parseParameterMap(request);
            try {
                List<FileItem> itemList;

                itemList = paramMap.get("datasetids");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    datasetIds = itemList.get(0).getString();
                }

                itemList = paramMap.get("timestamp");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    uploadTimestamp = itemList.get(0).getString();
                }

                itemList = paramMap.get("ometoken");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    omeIndicator = itemList.get(0).getString();
                }

                itemList = paramMap.get("metadataupload");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    metadataItem = itemList.get(0);
                }

            } finally {
                // Delete everything except for the uploaded metadata file
                for (List<FileItem> itemList : paramMap.values()) {
                    for (FileItem item : itemList) {
                        if ( !item.equals(metadataItem) ) {
                            item.delete();
                        }
                    }
                }
            }
        } catch ( Exception ex ) {
            if ( metadataItem != null )
                metadataItem.delete();
            sendErrMsg(response, "Error processing the request\n" + ex.getMessage());
            return;
        }

        // Verify page contents seem okay
        DashboardConfigStore configStore = DashboardConfigStore.get(true);
        if ( (username == null) || (datasetIds == null) || (uploadTimestamp == null) || (omeIndicator == null) ||
                (metadataItem == null) || (!(omeIndicator.equals("false") || omeIndicator.equals("true"))) ||
                !configStore.validateUser(username) ) {
            if ( metadataItem != null )
                metadataItem.delete();
            sendErrMsg(response, "Invalid request contents for this service.");
            return;
        }
        // Extract the set of dataset ID from the datasetIds String
        TreeSet<String> idSet;
        try {
            idSet = DashboardUtils.decodeStringTreeSet(datasetIds);
            if ( idSet.size() < 1 )
                throw new IllegalArgumentException();
        } catch ( IllegalArgumentException ex ) {
            metadataItem.delete();
            sendErrMsg(response, "Invalid request contents for this service.");
            return;
        }

        boolean isOme = omeIndicator.equals("true");
        String version = configStore.getUploadVersion();

        MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
        DataFileHandler dataFileHandler = configStore.getDataFileHandler();
        OmePdfGenerator omePdfGenerator = configStore.getOmePdfGenerator();
        DatabaseRequestHandler databaseHandler = configStore.getDatabaseRequestHandler();
        DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();

        String uploadFilename;
        if ( isOme ) {
            // Save under the PI_OME_FILENAME at this time.
            uploadFilename = DashboardUtils.PI_OME_FILENAME;
        }
        else {
            uploadFilename = DashboardUtils.baseName(metadataItem.getName());
            if ( uploadFilename.equals(DashboardUtils.OME_FILENAME) ||
                    uploadFilename.equals(DashboardUtils.OME_PDF_FILENAME) ||
                    uploadFilename.equals(DashboardUtils.PI_OME_FILENAME) ||
                    uploadFilename.equals(DashboardUtils.PI_OME_PDF_FILENAME) ) {
                metadataItem.delete();
                sendErrMsg(response, "Name of the uploaded file cannot be " +
                        DashboardUtils.OME_FILENAME +
                        " nor " + DashboardUtils.OME_PDF_FILENAME +
                        " nor " + DashboardUtils.PI_OME_FILENAME +
                        " nor " + DashboardUtils.PI_OME_PDF_FILENAME +
                        "\nPlease rename the file and try again.");
            }
        }

        DashboardMetadata metadata = null;
        for (String id : idSet) {
            try {
                // Save the metadata document for this cruise
                if ( metadata == null ) {
                    metadata = metadataHandler.saveMetadataFileItem(id, username, uploadTimestamp,
                            uploadFilename, version, metadataItem);
                }
                else {
                    metadata = metadataHandler.copyMetadataFile(id, metadata, true);
                }
                // Update the metadata documents associated with this cruise
                DashboardDataset dataset;
                if ( isOme ) {
                    // Make sure the contents are valid OME XML
                    DashboardOmeMetadata omedata;
                    try {
                        omedata = metadataHandler.getOmeFromFile(metadata);
                    } catch ( IllegalArgumentException ex ) {
                        // Problems with the file - delete it
                        metadataHandler.deleteMetadata(username, id, metadata.getFilename());
                        throw new IllegalArgumentException("Invalid OME metadata file: " + ex.getMessage());
                    }
                    dataset = dataFileHandler.addAddlDocTitleToDataset(id, omedata);
                    try {
                        // This is using the PI OME XML file at this time
                        omePdfGenerator.createPiOmePdf(id);
                    } catch ( Exception ex ) {
                        throw new IllegalArgumentException("Unable to create the PDF from the OME XML: " +
                                ex.getMessage());
                    }
                    DatasetQCStatus.Status autoSuggest = omedata.suggestedDatasetStatus(dataset);
                    if ( DatasetQCStatus.Status.isAcceptable(autoSuggest) ) {
                        DatasetQCStatus status = dataset.getSubmitStatus();
                        if ( !autoSuggest.equals(status.getAutoSuggested()) ) {
                            status.setAutoSuggested(autoSuggest);
                            dataset.setSubmitStatus(status);
                            dataFileHandler.saveDatasetInfoToFile(dataset,
                                    "Update of automation-suggested dataset QC flag");
                        }
                    }
                }
                else {
                    dataset = dataFileHandler.addAddlDocTitleToDataset(id, metadata);
                }

                // If the dataset is submitted (possibly even archived), add dataset QC indicating the change
                if ( !Boolean.TRUE.equals(dataset.isEditable()) ) {
                    Date now = new Date();
                    String comment;
                    if ( isOme )
                        comment = "Update of OME metadata.  ";
                    else
                        comment = "Update of metadata file \"" + uploadFilename + "\".  ";
                    comment += "Data and WOCE flags were not changed.";
                    String allRegionIds = "G";
                    try {
                        allRegionIds += dsgHandler.updateAllRegionIds(id);
                    } catch ( Exception ex ) {
                        throw new RuntimeException("Unexpect failure to obtain all the region IDs for " +
                                id + ": " + ex.getMessage());
                    }
                    DatasetQCStatus status = dataset.getSubmitStatus();
                    status.setActual(DatasetQCStatus.Status.UPDATED_AWAITING_QC);
                    dataset.setSubmitStatus(status);
                    // Add the update flags to global, then all the regions
                    ArrayList<QCEvent> qcEventList = new ArrayList<>(allRegionIds.length());
                    for (int k = 0; k < allRegionIds.length(); k++) {
                        QCEvent qcEvent = new QCEvent();
                        qcEvent.setDatasetId(id);
                        qcEvent.setFlagValue(status.flagString());
                        qcEvent.setFlagDate(now);
                        qcEvent.setRegionId(allRegionIds.substring(k, k + 1));
                        qcEvent.setVersion(version);
                        qcEvent.setUsername(username);
                        qcEvent.setComment(comment);
                        qcEventList.add(qcEvent);
                    }
                    try {
                        // Add the 'U' QC flags
                        databaseHandler.addDatasetQCEvents(qcEventList);
                        // If archived, reset the archived status so the updated metadata will be archived
                        if ( dataset.getArchiveStatus().equals(DashboardUtils.ARCHIVE_STATUS_ARCHIVED) )
                            dataset.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_NEXT_RELEASE);
                        dataFileHandler.saveDatasetInfoToFile(dataset, comment);
                        // Update the DSG files
                        String versionStatus = databaseHandler.getVersionStatus(id);
                        dsgHandler.updateDatasetQCFlagAndVersionStatus(id, status.flagString(), versionStatus);
                    } catch ( Exception ex ) {
                        // Should not fail. If does, do not delete the file since it is okay;
                        // just record but otherwise ignore the failure.
                        Logger itsLogger = configStore.getLogger();
                        if ( itsLogger != null )
                            itsLogger.error("failed to update QC status after adding metadata " +
                                    uploadFilename + " to " + id + " for " + username + ": " + ex.getMessage());
                    }
                }

            } catch ( Exception ex ) {
                metadataItem.delete();
                sendErrMsg(response, ex.getMessage());
                return;
            }
        }

        // Send the success response
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter respWriter = response.getWriter();
        respWriter.println(DashboardUtils.SUCCESS_HEADER_TAG);
        response.flushBuffer();
    }

    /**
     * Returns an error message in the given Response object.
     * The response number is still 200 (SC_OK) so the message goes through cleanly.
     *
     * @param response
     *         write the error message here
     * @param errMsg
     *         error message to return
     *
     * @throws IOException
     *         if writing to the response object throws one
     */
    private void sendErrMsg(HttpServletResponse response, String errMsg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter respWriter = response.getWriter();
        respWriter.println(errMsg);
        response.flushBuffer();
    }

}
