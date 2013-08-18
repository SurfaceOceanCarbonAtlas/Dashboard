/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

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

import org.apache.tomcat.util.http.fileupload.FileCleaningTracker;
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
	
	public DashboardCruiseUploadService() {
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
		// Automatically delete these temporary files with garbage collection
		factory.setFileCleaningTracker(new FileCleaningTracker());
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
		String userhash = null;
		String passhash = null;
		String encoding = null;
		String preview = null;
		FileItem cruiseItem = null;
		try {
			// Go through each item in the request
			for ( FileItem item : cruiseUpload.parseRequest(request) ) {
				String itemName = item.getFieldName();
				if ( "username".equals(itemName) ) {
					username = item.getString();
				}
				else if ( "userhash".equals(itemName) ) {
					userhash = item.getString();
				}
				else if ( "passhash".equals(itemName) ) {
					passhash = item.getString();
				}
				else if ( "cruiseencoding".equals(itemName) ) {
					encoding = item.getString();
				}
				else if ( "cruisepreview".equals(itemName) ) {
					preview = item.getString();
				}
				else if ( "cruiseupload".equals(itemName) ) {
					cruiseItem = item;
				}
			}
		} catch (Exception ex) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
		}

		// Verify contents seem okay
		if ( (username == null) || (userhash == null) || (passhash == null) || 
			 (encoding == null) || (preview == null) || (cruiseItem == null) ) {
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
					"Incomplete request contents for this service.");
		}
		if ( (DashboardDataStore.get() == null) ||
			 ( ! username.equals(DashboardDataStore.get()
					   .getUsernameFromHashes(userhash, passhash)) ) ||
			 ( ! ("true".equals(preview) || "false".equals(preview)) ) ) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid request contents for this service.");
			return;
		}

		if ( "true".equals(preview) ) {
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
				return;
			}

			String filename = cruiseItem.getName();
			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter respWriter = response.getWriter();
			respWriter.println("----------------------------------------");
			respWriter.println("Filename: " + filename);
			respWriter.println("Encoding: " + encoding);
			respWriter.println("Contents: (up to 50 lines)");
			respWriter.println("----------------------------------------");
			for ( String dataline : contentsList )
				respWriter.println(dataline);
			response.flushBuffer();
			return;
		}

		// TODO: deal with the file
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,
				"Method not yet implemented");
		return;
	}

}
