/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 * Service to receive the uploaded cruise file from the client
 * 
 * @author Karl Smith
 */
public class CruiseUploadService extends HttpServlet {

	private static final long serialVersionUID = 273235043648709372L;

	private ServletFileUpload cruiseUpload;

	public CruiseUploadService() {
		File servletTmpDir;
		try {
			// Get the temporary directory used by the servlet
			servletTmpDir = (File) getServletContext().getAttribute(
					"javax.servlet.context.tempdir");
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
		cruiseUpload = new ServletFileUpload(factory);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
																throws IOException {
		// Verify the post has the correct encoding
		if ( ! ServletFileUpload.isMultipartContent(request) ) {
			sendErrMsg(response, "Invalid request contents format for this service.");
			return;
		}

		String username = null;
		try {
			username = request.getUserPrincipal().getName().trim();
		} catch (Exception ex) {
			; // leave username null for error message later
		}

		// Get the contents from the post request
		String timestamp = null;
		String dataFormat = null;
		String encoding = null;
		String action = null;
		ArrayList<FileItem> cruiseItems = new ArrayList<FileItem>();
		try {
			// Go through each item in the request
			for ( FileItem item : cruiseUpload.parseRequest(request) ) {
				String itemName = item.getFieldName();
				if ( "timestamp".equals(itemName) ) {
					timestamp = item.getString();
					item.delete();
				}
				else if ( "cruiseformat".equals(itemName) ) {
					dataFormat = item.getString();
					item.delete();
				}
				else if ( "cruiseencoding".equals(itemName) ) {
					encoding = item.getString();
					item.delete();
				}
				else if ( "cruiseaction".equals(itemName) ) {
					action = item.getString();
					item.delete();
				}
				else if ( "cruisedata".equals(itemName) ) {
					cruiseItems.add(item);
				}
				else {
					item.delete();
				}
			}
		} catch (Exception ex) {
			for ( FileItem item : cruiseItems )
				item.delete();
			sendErrMsg(response, "Error processing the request \n" + ex.getMessage());
			return;
		}

		// Verify contents seem okay
		if ( cruiseItems.isEmpty() ) {
			sendErrMsg(response, "No upload files specified");
			return;
		}
		DashboardConfigStore configStore = DashboardConfigStore.get();
		if ( (username == null) || (dataFormat == null) || (encoding == null) || 
			 (action == null)   || (timestamp == null)  || 
			 ( ! configStore.validateUser(username) ) ||
			 ! ( action.equals(DashboardUtils.REQUEST_PREVIEW_TAG) ||
				 action.equals(DashboardUtils.REQUEST_NEW_CRUISE_TAG) ||
				 action.equals(DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG) ) ) {
			for ( FileItem item : cruiseItems )
				item.delete();
			sendErrMsg(response, "Invalid request contents for this service.");
			return;
		}

		if ( DashboardUtils.REQUEST_PREVIEW_TAG.equals(action) ) {
			FileItem firstItem = cruiseItems.get(0);
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
			} catch (Exception ex) {
				sendErrMsg(response, "Error processing the uploaded file " + 
						filename + "\n" + ex.getMessage());
				for ( FileItem item : cruiseItems )
					item.delete();
				return;
			}
			// done with the uploaded files
			for ( FileItem item : cruiseItems )
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

		CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();

		ArrayList<String> successes = new ArrayList<String>(cruiseItems.size());
		ArrayList<String> messages = new ArrayList<String>(cruiseItems.size());
		for ( FileItem item : cruiseItems ) {
			String filename = item.getName();
			// Create a DashboardCruiseWithData from the contents of the uploaded data file 
			DashboardCruiseWithData cruiseData;
			try {
				BufferedReader cruiseReader = new BufferedReader(
						new InputStreamReader(item.getInputStream(), encoding));
				try {
					cruiseData = new DashboardCruiseWithData();
					cruiseData.setOwner(username);
					cruiseData.setUploadFilename(filename);
					cruiseData.setUploadTimestamp(timestamp);
					cruiseHandler.assignCruiseDataFromInput(cruiseData, 
							dataFormat, cruiseReader, 0, -1, true);
				} finally {
					cruiseReader.close();
				}
			} catch (Exception ex) {
				// Mark as a failed file, and go on to the next
				messages.add(DashboardUtils.FILE_INVALID_HEADER_TAG + " " + filename);
				messages.add(ex.getMessage());
				messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
				item.delete();
				continue;
			}

			// done with the uploaded data file
			item.delete();

			// Check if the cruise file exists, and in the process
			// check if a valid expocode was obtained from the file
			String expocode = cruiseData.getExpocode();
			boolean cruiseExists;
			try {
				cruiseExists = cruiseHandler.cruiseDataFileExists(expocode);
			} catch ( IllegalArgumentException ex ) {
				messages.add(DashboardUtils.NO_EXPOCODE_HEADER_TAG + " " + filename);
				continue;
			}

			if ( cruiseExists ) {
				// Read the original cruise info to get the current owner and QC status
				DashboardCruise cruise;
				String owner;
				String qcstatus;
				try {
					cruise = cruiseHandler.getCruiseFromInfoFile(expocode);
					owner = cruise.getOwner();
					qcstatus = cruise.getQcStatus();
				} catch ( Exception ex ) {
					owner = "";
					qcstatus = "";
				}
				// Make sure this user has permission to overwrite this cruise,
				// and the request was for an overwrite
				try {
					cruise = cruiseHandler.verifyOkayToDeleteCruise(expocode, username);
				} catch ( Exception ex ) {
					cruise = null;
				}
				if ( (cruise == null) ||
					 ( ! DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG.equals(action) ) ) {
					messages.add(DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG + " " + 
								filename + " ; " + expocode + " ; " + owner + " ; " + qcstatus);
					continue;
				}

				// Preserve the original owner of the data and the original QC status (for update)
				if ( ! owner.isEmpty() )
					cruiseData.setOwner(owner);
				cruiseData.setQcStatus(qcstatus);
			}
			else {
				// If the cruise file does not exist, make sure the request was for a new file
				if ( ! DashboardUtils.REQUEST_NEW_CRUISE_TAG.equals(action) ) {
					messages.add(DashboardUtils.NO_DATASET_HEADER_TAG + " " + 
								filename + " ; " + expocode);
					continue;
				}
			}

			// Check if an OME metadata file or supplemental documents already exist for this cruise
			ArrayList<DashboardMetadata> mdataList = 
					configStore.getMetadataFileHandler().getMetadataFiles(expocode);
			TreeSet<String> addlDocs = new TreeSet<String>();
			for ( DashboardMetadata mdata : mdataList ) {
				if ( DashboardMetadata.OME_FILENAME.equals(mdata.getFilename())) {
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
				cruiseHandler.saveCruiseInfoToFile(cruiseData, "Cruise info " + commitMsg);
				cruiseHandler.saveCruiseDataToFile(cruiseData, "Cruise data " + commitMsg);
			} catch (IllegalArgumentException ex) {
				messages.add(DashboardUtils.UNEXPECTED_FAILURE_HEADER_TAG + " " + 
							filename + " ; " + expocode);
				messages.add(ex.getMessage());
				messages.add(DashboardUtils.END_OF_ERROR_MESSAGE_TAG);
				continue;
			}

			// Success
			messages.add(DashboardUtils.FILE_CREATED_HEADER_TAG + " " + expocode);
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
