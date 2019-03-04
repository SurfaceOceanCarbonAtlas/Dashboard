/**
 *
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.metadata.CdiacOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to receive the uploaded cruise file from the client
 *
 * @author Karl Smith
 */
public class DataUploadService extends HttpServlet {

    private static final long serialVersionUID = 1547524322159252520L;

    // Patterns for getting the PI name(s) from the metadata preamble
    private static final Pattern[] PI_NAMES_PATTERNS = new Pattern[] {
            Pattern.compile("Investigator\\s*Names?\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Investigators?\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("PI\\s*Names?\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("PIs?\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE)
    };

    // Patterns for getting the platform name from the metadata preamble
    private static final Pattern[] PLATFORM_NAME_PATTERNS = new Pattern[] {
            Pattern.compile("Platform\\s*Name\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Platform\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Vessel\\s*Name\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Vessel\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Ship\\s*Name\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Ship\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE)
    };

    // Patterns for getting the platform type from the metadata preamble
    private static final Pattern[] PLATFORM_TYPE_PATTERNS = new Pattern[] {
            Pattern.compile("Platform\\s*Type\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Vessel\\s*Type\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Type\\s*[=:]\\s*(.+)", Pattern.CASE_INSENSITIVE)
    };


    private ServletFileUpload datafileUpload;

    public DataUploadService() {
        File servletTmpDir;
        try {
            // Get the temporary directory used by the servlet
            servletTmpDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
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
        datafileUpload = new ServletFileUpload(factory);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Verify the post has the correct encoding
        if ( !ServletFileUpload.isMultipartContent(request) ) {
            sendErrMsg(response, "Invalid request contents format for this service.");
            return;
        }

        String username = null;
        try {
            username = DashboardServerUtils.cleanUsername(request.getUserPrincipal().getName().trim());
        } catch ( Exception ex ) {
            ; // leave username null for error message later
        }

        // Get the contents from the post request
        String timestamp = null;
        String dataFormat = null;
        String encoding = null;
        String action = null;
        List<FileItem> datafiles = null;
        try {
            Map<String,List<FileItem>> paramMap = datafileUpload.parseParameterMap(request);
            try {
                List<FileItem> itemList;

                itemList = paramMap.get("timestamp");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    timestamp = itemList.get(0).getString();
                }

                itemList = paramMap.get("dataaction");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    action = itemList.get(0).getString();
                }

                itemList = paramMap.get("dataencoding");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    encoding = itemList.get(0).getString();
                }

                itemList = paramMap.get("dataformat");
                if ( (itemList != null) && (itemList.size() == 1) ) {
                    dataFormat = itemList.get(0).getString();
                }

                datafiles = paramMap.get("datafiles");

            } finally {
                // Delete everything except for the uploaded data files
                for (Entry<String,List<FileItem>> paramEntry : paramMap.entrySet()) {
                    if ( !"datafiles".equals(paramEntry.getKey()) ) {
                        for (FileItem item : paramEntry.getValue()) {
                            item.delete();
                        }
                    }
                }
            }
        } catch ( Exception ex ) {
            // also delete the uploaded data files when an error occurs
            if ( datafiles != null ) {
                for (FileItem item : datafiles) {
                    item.delete();
                }
            }
            sendErrMsg(response, "Error processing the request \n" + ex.getMessage());
            return;
        }

        // Verify contents seem okay
        if ( (datafiles == null) || datafiles.isEmpty() ) {
            sendErrMsg(response, "No upload files specified");
            return;
        }
        DashboardConfigStore configStore = DashboardConfigStore.get(true);
        if ( (username == null) || (dataFormat == null) || (encoding == null) ||
                (action == null) || (timestamp == null) || (!configStore.validateUser(username)) ||
                !(action.equals(DashboardUtils.PREVIEW_REQUEST_TAG) ||
                        action.equals(DashboardUtils.NEW_DATASETS_REQUEST_TAG) ||
                        action.equals(DashboardUtils.OVERWRITE_DATASETS_REQUEST_TAG)) ) {
            for (FileItem item : datafiles) {
                item.delete();
            }
            sendErrMsg(response, "Invalid request contents for this service.");
            return;
        }

        if ( DashboardUtils.PREVIEW_REQUEST_TAG.equals(action) ) {
            FileItem firstItem = datafiles.get(0);
            String filename = firstItem.getName();

            // if preview, just return up to 50 lines
            // of interpreted contents of the first uploaded file
            ArrayList<String> contentsList = new ArrayList<String>(50);
            try {
                BufferedReader cruiseReader = new BufferedReader(
                        new InputStreamReader(firstItem.getInputStream(), encoding));
                try {
                    for (int k = 0; k < 50; k++) {
                        String dataline = cruiseReader.readLine();
                        if ( dataline == null )
                            break;
                        contentsList.add(dataline);
                    }
                } finally {
                    cruiseReader.close();
                }
            } catch ( Exception ex ) {
                sendErrMsg(response, "Error processing the uploaded file " + filename + "\n" + ex.getMessage());
                for (FileItem item : datafiles) {
                    item.delete();
                }
                return;
            }
            // done with the uploaded data files
            for (FileItem item : datafiles) {
                item.delete();
            }

            // Respond with some info and the interpreted contents
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter respWriter = response.getWriter();
            respWriter.println(DashboardUtils.FILE_PREVIEW_HEADER_TAG);
            respWriter.println("----------------------------------------");
            respWriter.println("Filename: " + filename);
            respWriter.println("Encoding: " + encoding);
            respWriter.println("(Partial) Contents:");
            respWriter.println("----------------------------------------");
            for (String dataline : contentsList) {
                respWriter.println(dataline);
            }
            response.flushBuffer();
            return;
        }

        DataFileHandler datasetHandler = configStore.getDataFileHandler();

        // List of all messages to be returned to the client
        ArrayList<String> messages = new ArrayList<String>(datafiles.size());

        // Set of IDs for successfully processed datasets
        TreeSet<String> successes = new TreeSet<String>();

        for (FileItem item : datafiles) {
            // Create a DashboardDatasetData from the contents of the uploaded data file
            DashboardDatasetData dsetData;
            String filename = item.getName();
            try {
                InputStreamReader reader = new InputStreamReader(item.getInputStream(), encoding);
                try {
                    dsetData = datasetHandler.assignDatasetDataFromInput(null, reader, dataFormat, username, 0, -1);
                    dsetData.setUploadFilename(filename);
                    dsetData.setUploadTimestamp(timestamp);
                } finally {
                    reader.close();
                }
            } catch ( Exception ex ) {
                // Mark as a failed file, and go on to the next
                messages.add(DashboardUtils.INVALID_FILE_HEADER_TAG + " " + filename);
                messages.add(ex.getMessage());
                messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
                item.delete();
                continue;
            }

            // done with the uploaded data file
            item.delete();

            // Check if the dataset file exists, and in the process
            // check if a valid expocode was obtained from the file
            String datasetId = dsetData.getDatasetId();
            boolean dataExists;
            try {
                dataExists = datasetHandler.dataFileExists(datasetId);
            } catch ( IllegalArgumentException ex ) {
                messages.add(DashboardUtils.NO_DATASET_ID_HEADER_TAG + " " + filename);
                continue;
            }

            if ( dataExists ) {
                // Read the original dataset info to get the current owner and QC status
                DashboardDataset dset;
                String owner;
                String status;
                try {
                    dset = datasetHandler.getDatasetFromInfoFile(datasetId);
                    owner = dset.getOwner();
                    status = dset.getSubmitStatus();
                } catch ( Exception ex ) {
                    owner = "";
                    status = "";
                }
                // Make sure this user has permission to overwrite this cruise,
                // and the request was for an overwrite
                try {
                    dset = datasetHandler.verifyOkayToDeleteDataset(datasetId, username);
                } catch ( Exception ex ) {
                    dset = null;
                }
                if ( (dset == null) || (!DashboardUtils.OVERWRITE_DATASETS_REQUEST_TAG.equals(action)) ) {
                    messages.add(DashboardUtils.DATASET_EXISTS_HEADER_TAG + " " +
                            filename + " ; " + datasetId + " ; " + owner + " ; " + status);
                    continue;
                }

                // Preserve the original owner of the data and the original QC status (for update)
                if ( !owner.isEmpty() )
                    dsetData.setOwner(owner);
                dsetData.setSubmitStatus(status);
            }
            else {
                // If the cruise file does not exist, make sure the request was for a new file
                if ( !DashboardUtils.NEW_DATASETS_REQUEST_TAG.equals(action) ) {
                    messages.add(DashboardUtils.DATASET_DOES_NOT_EXIST_HEADER_TAG + " " +
                            filename + " ; " + datasetId);
                    continue;
                }
            }

            String platformName = null;
            ArrayList<String> piNames = null;
            String platformType = null;
            // Get the ship name and PI names from the metadata preamble
            for (String metaline : dsetData.getPreamble()) {
                boolean lineMatched = false;
                if ( platformName == null ) {
                    for (Pattern pat : PLATFORM_NAME_PATTERNS) {
                        Matcher mat = pat.matcher(metaline);
                        if ( !mat.matches() )
                            continue;
                        lineMatched = true;
                        platformName = mat.group(1);
                        if ( (platformName != null) && !platformName.isEmpty() )
                            break;
                        platformName = null;
                    }
                }
                if ( (piNames == null) && !lineMatched ) {
                    for (Pattern pat : PI_NAMES_PATTERNS) {
                        Matcher mat = pat.matcher(metaline);
                        if ( !mat.matches() )
                            continue;
                        lineMatched = true;
                        String allNames = mat.group(1);
                        if ( allNames != null ) {
                            piNames = new ArrayList<String>();
                            for (String name : allNames.split(";")) {
                                name = name.trim();
                                if ( !name.isEmpty() )
                                    piNames.add(name);
                            }
                            if ( !piNames.isEmpty() )
                                break;
                            piNames = null;
                        }
                    }
                }
                if ( (platformType == null) && !lineMatched ) {
                    for (Pattern pat : PLATFORM_TYPE_PATTERNS) {
                        Matcher mat = pat.matcher(metaline);
                        if ( !mat.matches() )
                            continue;
                        lineMatched = true;
                        platformType = mat.group(1);
                        if ( (platformType != null) && !platformType.isEmpty() )
                            break;
                        platformType = null;
                    }
                }
            }
            // If platform name not found in preamble, check if there is a matching column type
            if ( platformName == null ) {
                int colIdx = -1;
                int k = 0;
                for (DataColumnType dtype : dsetData.getDataColTypes()) {
                    if ( DashboardServerUtils.PLATFORM_NAME.typeNameEquals(dtype) ) {
                        colIdx = k;
                        break;
                    }
                    k++;
                }
                if ( colIdx >= 0 ) {
                    platformName = dsetData.getDataValues().get(0).get(colIdx);
                    if ( platformName.isEmpty() )
                        platformName = null;
                }
            }
            // If PI names not found in preamble, check if there is a matching column type
            if ( piNames == null ) {
                int colIdx = -1;
                int k = 0;
                for (DataColumnType dtype : dsetData.getDataColTypes()) {
                    if ( DashboardServerUtils.INVESTIGATOR_NAMES.typeNameEquals(dtype) ) {
                        colIdx = k;
                        break;
                    }
                    k++;
                }
                if ( colIdx >= 0 ) {
                    piNames = new ArrayList<String>();
                    for (String name : dsetData.getDataValues().get(0).get(colIdx).split(";")) {
                        name = name.trim();
                        if ( !name.isEmpty() )
                            piNames.add(name);
                    }
                    if ( piNames.isEmpty() )
                        piNames = null;
                }
            }
            // If platform type not found in preamble, check if there is a matching column type
            if ( platformType == null ) {
                int colIdx = -1;
                int k = 0;
                for (DataColumnType dtype : dsetData.getDataColTypes()) {
                    if ( DashboardServerUtils.PLATFORM_TYPE.typeNameEquals(dtype) ) {
                        colIdx = k;
                        break;
                    }
                    k++;
                }
                if ( colIdx >= 0 ) {
                    platformType = dsetData.getDataValues().get(0).get(colIdx);
                    if ( platformType.isEmpty() )
                        platformType = null;
                }
            }

            // Verify there is a platform name and a PI name
            if ( platformName == null ) {
                messages.add(DashboardUtils.NO_PLATFORM_NAME_HEADER_TAG + " " + filename);
                continue;
            }
            if ( piNames == null ) {
                messages.add(DashboardUtils.NO_PI_NAMES_HEADER_TAG + " " + filename);
                continue;
            }
            // If the platform type is not given, make an educated guess
            if ( platformType == null ) {
                platformType = DashboardServerUtils.guessPlatformType(datasetId, platformName);
            }

            // Create the OME XML stub file for this dataset
            try {
                CdiacOmeMetadata omeMData = new CdiacOmeMetadata();
                omeMData.setDatasetId(datasetId);
                DashboardOmeMetadata mdata =
                        new DashboardOmeMetadata(omeMData, timestamp, username, dsetData.getVersion());
                String msg = "New OME metadata created from data file for " + datasetId + " uploaded by " + username;
                MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();
                mdataHandler.saveMetadataInfo(mdata, msg, false);
                mdataHandler.saveAsOmeXmlDoc(mdata, msg);
            } catch ( Exception ex ) {
                // should not happen
                messages.add(DashboardUtils.UNEXPECTED_FAILURE_HEADER_TAG + " " + filename + " ; " + datasetId);
                messages.add(ex.getMessage());
                messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
                continue;
            }

            // Add any existing documents for this cruise
            ArrayList<DashboardMetadata> mdataList = configStore.getMetadataFileHandler().getMetadataFiles(datasetId);
            TreeSet<String> addlDocs = new TreeSet<String>();
            for (DashboardMetadata mdata : mdataList) {
                if ( DashboardUtils.OME_FILENAME.equals(mdata.getFilename()) ) {
                    // Ignore the OME XML stub file
                }
                else if ( DashboardUtils.PI_OME_FILENAME.equals(mdata.getFilename()) ) {
                    dsetData.setOmeTimestamp(mdata.getUploadTimestamp());
                }
                else {
                    addlDocs.add(mdata.getAddlDocsTitle());
                }
            }
            dsetData.setAddlDocs(addlDocs);

            // Save the cruise file and commit it to version control
            try {
                String commitMsg;
                if ( dataExists )
                    commitMsg = "file for " + datasetId + " updated by " + username + " from uploaded file " + filename;
                else
                    commitMsg = "file for " + datasetId + " created by " + username + " from uploaded file " + filename;
                datasetHandler.saveDatasetInfoToFile(dsetData, "Dataset info " + commitMsg);
                datasetHandler.saveDatasetDataToFile(dsetData, "Dataset data " + commitMsg);
            } catch ( IllegalArgumentException ex ) {
                messages.add(DashboardUtils.UNEXPECTED_FAILURE_HEADER_TAG + " " + filename + " ; " + datasetId);
                messages.add(ex.getMessage());
                messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
                continue;
            }

            // Success
            messages.add(DashboardUtils.SUCCESS_HEADER_TAG + " " + datasetId);
            successes.add(datasetId);
        }

        // Update the list of datasets for the user
        try {
            configStore.getUserFileHandler().addDatasetsToListing(successes, username);
        } catch ( IllegalArgumentException ex ) {
            sendErrMsg(response, "Unexpected error updating list of datasets \n" + ex.getMessage());
            return;
        }

        // Send the success response
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter respWriter = response.getWriter();
        for (String msg : messages) {
            respWriter.println(msg);
        }
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
