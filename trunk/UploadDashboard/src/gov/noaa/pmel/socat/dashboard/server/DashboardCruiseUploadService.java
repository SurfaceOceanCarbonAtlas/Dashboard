/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseWithData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.ServletException;
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
public class DashboardCruiseUploadService extends HttpServlet {

	private static final long serialVersionUID = -6964079374165945117L;

	private ServletFileUpload cruiseUpload;

	public DashboardCruiseUploadService() throws IOException {
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
											throws ServletException, IOException {
		// Verify the post has the correct encoding
		if ( ! ServletFileUpload.isMultipartContent(request) ) {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Invalid request contents format for this service.");
			return;
		}

		// Get the contents from the post request
		String username = null;
		String passhash = null;
		String encoding = null;
		String action = null;
		FileItem cruiseItem = null;
		try {
			// Go through each item in the request
			for ( FileItem item : cruiseUpload.parseRequest(request) ) {
				String itemName = item.getFieldName();
				if ( "username".equals(itemName) ) {
					username = item.getString();
					item.delete();
				}
				else if ( "passhash".equals(itemName) ) {
					passhash = item.getString();
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
				else if ( "cruiseupload".equals(itemName) ) {
					cruiseItem = item;
				}
				else {
					item.delete();
				}
			}
		} catch (Exception ex) {
			if ( cruiseItem != null )
				cruiseItem.delete();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
			return;
		}

		// Verify contents seem okay
		DashboardDataStore dataStore = DashboardDataStore.get();
		if ( (username == null) || (passhash == null) || 
			 (encoding == null) || (action == null) || (cruiseItem == null) || 
			 ( ! dataStore.validateUser(username, passhash) ) ||
			 ! ( DashboardUtils.REQUEST_PREVIEW_TAG.equals(action) ||
				 DashboardUtils.REQUEST_NEW_CRUISE_TAG.equals(action) ||
				 DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG.equals(action) ) ) {
			cruiseItem.delete();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid request contents for this service.");
			return;
		}

		// name of the user's uploaded file
		String filename = cruiseItem.getName();

		if ( DashboardUtils.REQUEST_PREVIEW_TAG.equals(action) ) {
			// if preview, just return up to 50 lines 
			/// of interpreted contents of the uploaded file
			ArrayList<String> contentsList = new ArrayList<String>(50);
			try {
				BufferedReader cruiseReader = new BufferedReader(
						new InputStreamReader(cruiseItem.getInputStream(), encoding));
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
				response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
						"Error processing the uploaded file: " + ex.getMessage());
				cruiseItem.delete();
				return;
			}
			// done with the uploaded file
			cruiseItem.delete();

			// Respond with some info and the interpreted contents
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
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

		DashboardCruiseFileHandler cruiseHandler = dataStore.getCruiseFileHandler();

		// Create a DashboardCruiseWithData from the contents of the uploaded data file 
		DashboardCruiseWithData cruiseData;
		try {
			BufferedReader cruiseReader = new BufferedReader(
					new InputStreamReader(cruiseItem.getInputStream(), encoding));
			try {
				cruiseData = new DashboardCruiseWithData();
				cruiseData.setOwner(username);
				cruiseData.setUploadFilename(filename);
				cruiseHandler.assignCruiseDataFromInput(cruiseData, cruiseReader, 0, -1);
			} finally {
				cruiseReader.close();
			}
		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Error processing the uploaded file: " + ex.getMessage());
			cruiseItem.delete();
			return;
		}
		// done with the uploaded data file
		cruiseItem.delete();

		// Check if the cruise file exists, and in the process
		// check if a valid expocode was obtained from the file
		String expocode = cruiseData.getExpocode();
		boolean cruiseExists;
		try {
			cruiseExists = cruiseHandler.cruiseDataFileExists(expocode);
		} catch ( IllegalArgumentException ex ) {
			// Invalid expocode - respond with an error message containing partial file contents
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter respWriter = response.getWriter();
			respWriter.println(DashboardUtils.NO_EXPOCODE_HEADER_TAG);
			respWriter.println("----------------------------------------");
			respWriter.println("Filename: " + filename);
			respWriter.println("Encoding: " + encoding);
			respWriter.println("(Partial) Contents:");
			respWriter.println("----------------------------------------");
			for ( String dataline : 
					cruiseHandler.getPartialCruiseDataContents(cruiseData) )
				respWriter.println(dataline);
			response.flushBuffer();
			return;
		}

		if ( cruiseExists ) {
			String owner;
			try {
				owner = cruiseHandler.verifyOkayToDeleteCruise(expocode, username)
									 .getOwner();
			} catch ( Exception ex ) {
				owner = null;
			}
			// If the cruise file exists, make sure the request was for an overwrite
			// and that this user has permission to overwrite this cruise
			if ( (owner == null) ||
					DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG.equals(action) ) {
				// Respond with an error message containing partial file contents 
				// of the existing file
				DashboardCruiseWithData existingCruiseData;
				try {
					existingCruiseData = 
							cruiseHandler.getCruiseDataFromFile(expocode, 0, 25);
				} catch ( Exception ex ) {
					// just report the error without data
					existingCruiseData = null;
				}
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter respWriter = response.getWriter();
				if ( owner == null ) {
					respWriter.println(DashboardUtils.CANNOT_OVERWRITE_HEADER_TAG);
					if ( existingCruiseData != null )
						owner = existingCruiseData.getOwner();
				}
				else
					respWriter.println(DashboardUtils.FILE_EXISTS_HEADER_TAG);
				respWriter.println("----------------------------------------");
				respWriter.println("(Partial) Contents of existing cruise file:");
				respWriter.println("Owned by: " + owner);
				if ( existingCruiseData != null )
					respWriter.println("Name of uploaded file: " + 
							existingCruiseData.getUploadFilename());
				respWriter.println("----------------------------------------");
				if ( existingCruiseData != null ) {
					for ( String dataline : 
							cruiseHandler.getPartialCruiseDataContents(existingCruiseData) )
						respWriter.println(dataline);
				}
				response.flushBuffer();
				return;
			}

			// Preserve the original owner of the data
			if ( ! owner.isEmpty() )
				cruiseData.setOwner(owner);
		}
		else {
			// If the cruise file does not exist, make sure the request was for a new file
			if ( ! DashboardUtils.REQUEST_NEW_CRUISE_TAG.equals(action) ) {
				// Respond with an error message containing the partial file contents
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter respWriter = response.getWriter();
				respWriter.println(DashboardUtils.NO_FILE_HEADER_TAG);
				respWriter.println("----------------------------------------");
				respWriter.println("Filename: " + filename);
				respWriter.println("Encoding: " + encoding);
				respWriter.println("(Partial) Contents:");
				respWriter.println("----------------------------------------");
				for ( String dataline : 
						cruiseHandler.getPartialCruiseDataContents(cruiseData) )
					respWriter.println(dataline);
				response.flushBuffer();
				return;
			}
		}

		// Save the cruise file and commit it to version control
		String message;
		if ( cruiseExists )
			message = "Cruise data for " + expocode + " updated by " + 
					username + " from uploaded file " + filename;
		else
			message = "Cruise data for " + expocode + " created by " + 
					username + " from uploaded file " + filename;			
		try {
			cruiseHandler.saveCruiseDataToFile(cruiseData, message);
		} catch (IllegalArgumentException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
		}

		// Update the list of cruises for the user
		try {
			HashSet<String> expocodeSet = new HashSet<String>();
			expocodeSet.add(expocode);
			dataStore.getUserFileHandler()
					 .addCruisesToListing(expocodeSet, username);
		} catch (IllegalArgumentException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
		}

		// Send the success response
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		if ( cruiseExists )
			respWriter.println(DashboardUtils.FILE_UPDATED_HEADER_TAG);
		else
			respWriter.println(DashboardUtils.FILE_CREATED_HEADER_TAG);
		respWriter.println(message);
		response.flushBuffer();
	}

}
