/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadataList;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 * Service to receive the uploaded metadata file from the client
 * @author Karl Smith
 */
public class DashboardMetadataUploadService extends HttpServlet {

	private static final long serialVersionUID = 6095108321672612644L;

	private ServletFileUpload metadataUpload;

	public DashboardMetadataUploadService() {
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
		metadataUpload = new ServletFileUpload(factory);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
																throws IOException {
		// Verify the post has the correct encoding
		if ( ! ServletFileUpload.isMultipartContent(request) ) {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Invalid request contents format for this service.");
			return;
		}

		// Get the contents from the post request
		String username = null;
		String passhash = null;
		String expocodeFilename = null;
		Boolean overwrite = null;
		FileItem metadataItem = null;
		try {
			// Go through each item in the request
			for ( FileItem item : metadataUpload.parseRequest(request) ) {
				String itemName = item.getFieldName();
				if ( "username".equals(itemName) ) {
					username = item.getString();
					item.delete();
				}
				else if ( "passhash".equals(itemName) ) {
					passhash = item.getString();
					item.delete();
				}
				else if ( "expocode".equals(itemName) ) {
					// This is either the complete expocode filename or just 
					// the cruise expocode, depending on the value of overwrite.
					expocodeFilename = item.getString();
					item.delete();
				}
				else if ( "overwrite".equals(itemName) ) {
					String overwriteVal = item.getString();
					if ( "false".equals(overwriteVal) )
						overwrite = false;
					else if ( "true".equals(overwriteVal) )
						overwrite = true;
					item.delete();
				}
				else if ( "metadataupload".equals(itemName) ) {
					metadataItem = item;
				}
				else {
					item.delete();
				}
			}
		} catch (Exception ex) {
			if ( metadataItem != null )
				metadataItem.delete();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
					"Error processing the request: " + ex.getMessage());
			return;
		}

		// Verify page contents seem okay
		DashboardDataStore dataStore = DashboardDataStore.get();
		if ( (username == null) || (passhash == null) || (metadataItem == null) || 
			 (expocodeFilename == null) || (overwrite == null) ||
			 ! dataStore.validateUser(username, passhash) ) {
			metadataItem.delete();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid request contents for this service.");
			return;
		}

		// Save the metadata document
		DashboardMetadata metadata;
		String message;
		try {
			if ( overwrite ) {
				// expocodeFilename is the expocode filename
				metadata = dataStore.getMetadataFileHandler()
									.saveUpdatedMetadataFile(expocodeFilename, 
											username, metadataItem);
				message = "Updated metadata document " + 
						metadata.getExpocodeFilename() + " (" + 
						metadata.getUploadFilename() + ")";
			}
			else {
				// expocodeFilename is just the cruise expocode
				metadata = dataStore.getMetadataFileHandler()
									.saveNewMetadataFile(expocodeFilename, 
											username, metadataItem);
				message = "Created metadata document " + 
						metadata.getExpocodeFilename() + " (" + 
						metadata.getUploadFilename() + ")";
			}
			// Done with the uploaded file
			metadataItem.delete();
			// Update the available metadata documents for this user
			DashboardMetadataList metadataList = 
					dataStore.getUserFileHandler().getMetadataListing(username);
			metadataList.add(metadata);
			dataStore.getUserFileHandler()
					 .saveMetadataListing(metadataList, message);
		} catch (Exception ex) {
			metadataItem.delete();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								ex.getMessage());
			return;
		}

		
		// Send the success response
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		if ( overwrite ) {
			respWriter.println(DashboardUtils.FILE_UPDATED_HEADER_TAG);
			respWriter.println(message);
		}
		else {
			respWriter.println(DashboardUtils.FILE_CREATED_HEADER_TAG);
			respWriter.println(message);
		}
		response.flushBuffer();
	}

}
