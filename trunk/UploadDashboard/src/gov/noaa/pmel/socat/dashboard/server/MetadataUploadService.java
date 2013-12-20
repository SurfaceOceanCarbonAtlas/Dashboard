/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;

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
public class MetadataUploadService extends HttpServlet {

	private static final long serialVersionUID = 6620559111563840485L;

	private ServletFileUpload metadataUpload;

	public MetadataUploadService() {
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
		String expocodes = null;
		String uploadTimestamp = null;
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
				else if ( "expocodes".equals(itemName) ) {
					expocodes = item.getString();
					item.delete();
				}
				else if ( "timestamp".equals(itemName) ) {
					uploadTimestamp = item.getString();
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
		if ( (username == null) || (passhash == null) || 
			 (expocodes == null) || (uploadTimestamp == null) ||
			 (metadataItem == null) || 
			 ! dataStore.validateUser(username, passhash) ) {
			metadataItem.delete();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid request contents for this service.");
			return;
		}
		// Extract the cruise expocodes from the expocodes string
		TreeSet<String> cruiseExpocodes = new TreeSet<String>(); 
		try {
			cruiseExpocodes.addAll(
					DashboardUtils.decodeStringArrayList(expocodes));
			if ( cruiseExpocodes.size() < 1 )
				throw new IllegalArgumentException();
		} catch ( IllegalArgumentException ex ) {
			metadataItem.delete();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
					"Invalid request contents for this service.");
			return;
		}

		MetadataFileHandler metadataHandler = 
				dataStore.getMetadataFileHandler();
		CruiseFileHandler cruiseHandler = 
				dataStore.getCruiseFileHandler();
		String uploadFilename = metadataItem.getName();

		DashboardMetadata metadata = null;
		for ( String expo : cruiseExpocodes ) {
			try {
				// Save the metadata document for this cruise
				if ( metadata == null ) {
					metadata = metadataHandler.saveMetadataFile(expo, 
							username, uploadTimestamp, metadataItem);
				}
				else {
					metadata = metadataHandler.copyMetadataFile(expo,
							uploadFilename, metadata);
				}
				// Update the metadata documents associated with this cruise
				DashboardCruise cruise = cruiseHandler.getCruiseFromInfoFile(expo);
				if ( cruise == null )
					throw new IllegalArgumentException(
							"Cruise " + expo + " does not exist");
				// Directly modify the metadata listing in the cruise
				if ( cruise.getMetadataFilenames().add(metadata.getFilename()) ) {
					// New metadata document added
					cruiseHandler.saveCruiseToInfoFile(cruise, 
							"Added metadata document " + metadata.getFilename() + 
							" to cruise " + expo);
				}
			} catch ( Exception ex ) {
				metadataItem.delete();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
									ex.getMessage());
				return;
			}
		}

		// Generate the message for the success response
		StringBuffer sb = new StringBuffer();
		sb.append("Added/updated metadata document ");
		sb.append(uploadFilename);
		if ( cruiseExpocodes.size() == 1 )
			sb.append(" for cruise ");
		else
			sb.append(" for cruises: ");
		boolean first = true;
		for ( String expo : cruiseExpocodes ) {
			if ( first )
				first = false;
			else
				sb.append(", ");
			sb.append(expo);
		}
		String message = sb.toString();

		// Send the success response
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter respWriter = response.getWriter();
		respWriter.println(DashboardUtils.FILE_CREATED_HEADER_TAG);
		respWriter.println(message);
		response.flushBuffer();
	}

}
