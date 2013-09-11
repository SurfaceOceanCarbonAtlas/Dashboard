/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.DashboardCruiseList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.tmatesoft.svn.core.SVNException;

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

		// Create a DashboardCruiseData from the contents of the file 
		DashboardCruiseData cruiseData;
		try {
			BufferedReader cruiseReader = new BufferedReader(
					new InputStreamReader(cruiseItem.getInputStream(), encoding));
			try {
				 cruiseData = cruiseHandler.getCruiseDataFromInput(
						 						username, filename, cruiseReader);
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

		// Make sure the expocode was obtained from the file
		String expocode = cruiseData.getExpocode();
		boolean cruiseFileExists;
		try {
			cruiseFileExists = cruiseHandler.cruiseFileExists(expocode);
		} catch ( IllegalArgumentException ex ) {
			// Respond with an error message containing partial file contents
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

		String tag;
		String message;
		if ( cruiseFileExists ) {
			DashboardCruiseData existingCruiseData = null;
			String owner = "";
			try {
				existingCruiseData = 
						cruiseHandler.getCruiseDataFromFile(expocode);
				owner = existingCruiseData.getOwner();
			} catch ( Exception ex ) {
				// deal with whatever was read
				;
			}
			boolean problem = false;
			// If the cruise file exists, make sure the request was for an overwrite
			if ( ! DashboardUtils.REQUEST_OVERWRITE_CRUISE_TAG.equals(action) )
				problem = true;
			else if ( ! ( owner.isEmpty() || owner.equals(username) ) ) {
				// verify username has permission to overwrite owner's cruise
				if ( dataStore.userManagesOver(username, owner) ) {
					// Preserve the original owner of the data
					cruiseData.setOwner(owner);
				}
				else
					problem = true;				
			}
			if ( problem ) {
				// Respond with an error message containing partial file contents 
				// of the existing file
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter respWriter = response.getWriter();
				respWriter.println(DashboardUtils.FILE_EXISTS_HEADER_TAG);
				respWriter.println("----------------------------------------");
				respWriter.println("(Partial) Contents of existing cruise file:");
				respWriter.println("Owned by: " + owner);
				if ( username.equals(owner) )
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
			tag = DashboardUtils.FILE_UPDATED_HEADER_TAG;
			message = "Cruise data for " + expocode + 
					  " updated by " + cruiseData.getOwner() +
					  " from uploaded file " + filename;
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
			tag = DashboardUtils.FILE_CREATED_HEADER_TAG;
			message = "Cruise data for " + expocode + 
					  " created by " + username +
					  " from uploaded file " + filename;
		}

		// Save the cruise file and commit it to version control
		try {
			cruiseHandler.saveCruiseDataToFile(cruiseData, message);
		} catch (IllegalArgumentException | SVNException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
		}

		// Update the list of cruises for the user
		try {
			DashboardUserFileHandler userHandler = dataStore.getUserFileHandler();
			// Get the list of cruises for the user
			DashboardCruiseList cruiseList = userHandler.getCruiseListing(username);
			// Create a cruise entry for this data
			DashboardCruise cruise = new DashboardCruise();
			cruise.setOwner(cruiseData.getOwner());
			cruise.setExpocode(expocode);
			cruise.setUploadFilename(filename);
			// Add or replace this cruise in the cruise list
			cruiseList.put(expocode, cruise);
			// Save the updated cruise listing
			userHandler.saveCruiseListing(cruiseList, message);
		} catch (IllegalArgumentException | SVNException ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
		}

		// Send the success response
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		respWriter.println(tag);
		respWriter.println(message);
		response.flushBuffer();
	}

}
