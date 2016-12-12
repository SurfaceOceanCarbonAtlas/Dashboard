/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.handlers.DataFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardDatasetData;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DataColumnType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * Service to receive the uploaded cruise file from the client
 * 
 * @author Karl Smith
 */
public class DataUploadService extends HttpServlet {

	private ServletFileUpload datafileUpload;

	public DataUploadService() {
		File servletTmpDir;
		try {
			// Get the temporary directory used by the servlet
			servletTmpDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
		} catch (Exception ex) {
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
		if ( ! ServletFileUpload.isMultipartContent(request) ) {
			sendErrMsg(response, "Invalid request contents format for this service.");
			return;
		}

		String username = null;
		try {
			username = DashboardUtils.cleanUsername(request.getUserPrincipal().getName().trim());
		} catch (Exception ex) {
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
				for ( Entry<String,List<FileItem>> paramEntry : paramMap.entrySet() ) {
					if ( ! "datafiles".equals(paramEntry.getKey()) ) {
						for ( FileItem item : paramEntry.getValue() ) {
							item.delete();
						}
					}
				}
			}
		} catch (Exception ex) {
			// also delete the uploaded data files when an error occurs
			if ( datafiles != null ) {
				for ( FileItem item : datafiles )
					item.delete();
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
			 (action == null)   || (timestamp == null)  || ( ! configStore.validateUser(username) ) ||
			 ! ( action.equals(DashboardUtils.PREVIEW_REQUEST_TAG) ||
				 action.equals(DashboardUtils.NEW_DATASETS_REQUEST_TAG) ||
				 action.equals(DashboardUtils.APPEND_DATASETS_REQUEST_TAG) ||
				 action.equals(DashboardUtils.OVERWRITE_DATASETS_REQUEST_TAG) ) ) {
			for ( FileItem item : datafiles )
				item.delete();
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
				BufferedReader cruiseReader = new BufferedReader(new InputStreamReader(firstItem.getInputStream(), encoding));
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
			} catch (Exception ex) {
				sendErrMsg(response, "Error processing the uploaded file " + filename + "\n" + ex.getMessage());
				for ( FileItem item : datafiles )
					item.delete();
				return;
			}
			// done with the uploaded data files
			for ( FileItem item : datafiles )
				item.delete();

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
			for ( String dataline : contentsList )
				respWriter.println(dataline);
			response.flushBuffer();
			return;
		}

		DataFileHandler datasetHandler = configStore.getDataFileHandler();

		// List of all messages to be returned to the client
		ArrayList<String> messages = new ArrayList<String>(datafiles.size());

		for ( FileItem item : datafiles ) {
			// Get the datasets from this file
			TreeMap<String,DashboardDatasetData> datasetsMap;
			String filename = item.getName();
			try {
				BufferedReader cruiseReader = new BufferedReader(
						new InputStreamReader(item.getInputStream(), encoding));
				try {
					datasetsMap = datasetHandler.createDatasetsFromInput(cruiseReader, dataFormat, username, filename, timestamp);
				} finally {
					cruiseReader.close();
				}
			} catch (Exception ex) {
				// Mark as a failed file, and go on to the next
				messages.add(DashboardUtils.INVALID_FILE_HEADER_TAG + " " + filename);
				messages.add(ex.getMessage());
				messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
				item.delete();
				continue;
			}

			// done with the uploaded data file
			item.delete();

			// Process all the datasets created from this file
			for ( DashboardDatasetData datasetData : datasetsMap.values() ) {
				// Check if the dataset already exists
				String datasetId = datasetData.getDatasetId();
				boolean datasetExists = datasetHandler.dataFileExists(datasetId);
				if ( datasetExists ) {
					String owner;
					String status;
					try {
						// Read the original dataset info to get the current owner and submit status
						DashboardDataset oldDataset = datasetHandler.getDatasetFromInfoFile(datasetId);
						owner = oldDataset.getOwner();
						status = oldDataset.getSubmitStatus();
					} catch ( Exception ex ) {
						// Some problem with the properties file
						owner = "";
						status = "";
					}
					// If only create new datasets, add error message and skip the dataset
					if ( DashboardUtils.NEW_DATASETS_REQUEST_TAG.equals(action) ) {
						messages.add(DashboardUtils.DATASET_EXISTS_HEADER_TAG + " " + 
								filename + " ; " + datasetId + " ; " + owner + " ; " + status);
						continue;
					}
					// Make sure this user has permission to modify this dataset
					try {
						datasetHandler.verifyOkayToDeleteDataset(datasetId, username);
					} catch ( Exception ex ) {
						messages.add(DashboardUtils.DATASET_EXISTS_HEADER_TAG + " " + 
								filename + " ; " + datasetId + " ; " + owner + " ; " + status);
						continue;
					}
				}

				// Preserve the original owner of the data and the original QC status (for update)
				if ( ! owner.isEmpty() )
					cruiseData.setOwner(owner);
				cruiseData.setSubmitStatus(qcstatus);
			}

			// Create the OME XML stub file from the dataset, platform name, PI name(s), and platform type
			try {
				OmeMetadata omeMData = new OmeMetadata(expocode);
				DashboardOmeMetadata mdata = new DashboardOmeMetadata(omeMData,
						timestamp, username, cruiseData.getVersion());
				String msg = "New OME XML document from data file for " + expocode + " uploaded by " + username;
				MetadataFileHandler mdataHandler = configStore.getMetadataFileHandler();
				mdataHandler.saveMetadataInfo(mdata, msg, false);
				mdataHandler.saveAsOmeXmlDoc(mdata, msg);
			} catch (Exception ex) {
				// should not happen
				messages.add(DashboardUtils.UNEXPECTED_FAILURE_HEADER_TAG + " " + 
						filename + " ; " + expocode);
				messages.add(ex.getMessage());
				messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
				continue;
			}

			// Add any existing documents for this cruise
			ArrayList<DashboardMetadata> mdataList = configStore.getMetadataFileHandler().getMetadataFiles(expocode);
			TreeSet<String> addlDocs = new TreeSet<String>();
			for ( DashboardMetadata mdata : mdataList ) {
				if ( DashboardUtils.OME_FILENAME.equals(mdata.getFilename())) {
					// Ignore the OME XML stub file
				}
				else if ( DashboardUtils.PI_OME_FILENAME.equals(mdata.getFilename())) {
					cruiseData.setOmeTimestamp(mdata.getUploadTimestamp());					
				}
				else {
					addlDocs.add(mdata.getAddlDocsTitle());
				}
			}
			cruiseData.setAddlDocs(addlDocs);

			// Save the cruise file and commit it to version control
			try {
				String commitMsg;
				if ( cruiseExists ) 
					commitMsg = "file for " + expocode + " updated by " + 
							username + " from uploaded file " + filename;
				else
					commitMsg = "file for " + expocode + " added by " + 
							username + " from uploaded file " + filename;			
				datasetHandler.saveDatasetInfoToFile(cruiseData, "Cruise info " + commitMsg);
				datasetHandler.saveDatasetDataToFile(cruiseData, "Cruise data " + commitMsg);
			} catch (IllegalArgumentException ex) {
				messages.add(DashboardUtils.UNEXPECTED_FAILURE_HEADER_TAG + " " + 
							filename + " ; " + expocode);
				messages.add(ex.getMessage());
				messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
				continue;
			}

			// Success
			messages.add(DashboardUtils.SUCCESS_HEADER_TAG + " " + expocode);
			successes.add(expocode);
		}

		// Update the list of cruises for the user
		try {
			configStore.getUserFileHandler().addCruisesToListing(successes, username);
		} catch (IllegalArgumentException ex) {
			sendErrMsg(response, "Unexpected error updating list of cruises \n" + ex.getMessage());
			return;
		}

		// Send the success response
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		for ( String msg : messages )
			respWriter.println(msg);
		response.flushBuffer();
	}

	/**
	 * Returns an error message in the given Response object.  
	 * The response number is still 200 (SC_OK) so the message 
	 * goes through cleanly.
	 * 
	 * @param response
	 * 		write the error message here
	 * @param errMsg
	 * 		error message to return
	 * @throws IOException 
	 * 		if writing to the response object throws one
	 */
	private void sendErrMsg(HttpServletResponse response, String errMsg) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		respWriter.println(errMsg);
		response.flushBuffer();
	}

}
