/**
 * 
 */
package gov.noaa.pmel.dashboard.server;

import gov.noaa.pmel.dashboard.actions.OmePdfGenerator;
import gov.noaa.pmel.dashboard.handlers.CruiseFileHandler;
import gov.noaa.pmel.dashboard.handlers.DatabaseRequestHandler;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.handlers.MetadataFileHandler;
import gov.noaa.pmel.dashboard.shared.DashboardCruise;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.QCEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Verify the post has the correct encoding
		if ( ! ServletFileUpload.isMultipartContent(request) ) {
			sendErrMsg(response, "Invalid request contents format for this service.");
			return;
		}

		// Get the contents from the post request
		String username = null;
		try {
			username = DashboardUtils.cleanUsername(request.getUserPrincipal().getName().trim());
		} catch (Exception ex) {
			; // leave username null for error message later
		}

		// Get the contents from the post request
		String expocodes = null;
		String uploadTimestamp = null;
		String omeIndicator = null;
		FileItem metadataItem = null;
		try {
			Map<String,List<FileItem>> paramMap = metadataUpload.parseParameterMap(request);
			try {
				List<FileItem> itemList;

				itemList = paramMap.get("expocodes");
				if ( (itemList != null) && (itemList.size() == 1) ) {
					expocodes = itemList.get(0).getString();
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
				for ( List<FileItem> itemList : paramMap.values() ) {
					for ( FileItem item : itemList ) {
						if ( ! item.equals(metadataItem) ) {
							item.delete();
						}
					}
				}
			}
		} catch (Exception ex) {
			if ( metadataItem != null )
				metadataItem.delete();
			sendErrMsg(response, "Error processing the request\n" + ex.getMessage());
			return;
		}

		// Verify page contents seem okay
		DashboardConfigStore configStore = DashboardConfigStore.get(true);
		if ( (username == null) || (expocodes == null) || (uploadTimestamp == null) ||
			 (omeIndicator == null) || (metadataItem == null) || 
			 ( ! (omeIndicator.equals("false") || omeIndicator.equals("true")) ) || 
			 ! configStore.validateUser(username) ) {
			if ( metadataItem != null )
				metadataItem.delete();
			sendErrMsg(response, "Invalid request contents for this service.");
			return;
		}
		// Extract the cruise expocodes from the expocodes string
		TreeSet<String> cruiseExpocodes = new TreeSet<String>(); 
		try {
			cruiseExpocodes.addAll(DashboardUtils.decodeStringArrayList(expocodes));
			if ( cruiseExpocodes.size() < 1 )
				throw new IllegalArgumentException();
		} catch ( IllegalArgumentException ex ) {
			metadataItem.delete();
			sendErrMsg(response, "Invalid request contents for this service.");
			return;
		}

		boolean isOme = omeIndicator.equals("true");
		String version = configStore.getSocatUploadVersion();

		MetadataFileHandler metadataHandler = configStore.getMetadataFileHandler();
		CruiseFileHandler cruiseHandler = configStore.getCruiseFileHandler();
		DatabaseRequestHandler dbHandler = configStore.getDatabaseRequestHandler();
		DsgNcFileHandler dsgFileHandler = configStore.getDsgNcFileHandler();
		OmePdfGenerator omePdfGenerator = configStore.getOmePdfGenerator();
		String uploadFilename;
		if ( isOme ) {
			// Save under the PI_OME_FILENAME at this time.
			// When CDIAC OME incorporated, change to OME_FILENAME
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
		for ( String expo : cruiseExpocodes ) {
			try {
				// Save the metadata document for this cruise
				if ( metadata == null ) {
					metadata = metadataHandler.saveMetadataFileItem(expo, 
							username, uploadTimestamp, uploadFilename, version, metadataItem);
				}
				else {
					metadata = metadataHandler.copyMetadataFile(expo, metadata, true);
				}
				// Update the metadata documents associated with this cruise
				DashboardCruise cruise;
				if ( isOme ) {
					// Make sure the contents are valid OME XML
					DashboardOmeMetadata omedata;
					try {
						omedata = new DashboardOmeMetadata(metadata, metadataHandler);
					} catch ( IllegalArgumentException ex ) {
						// Problems with the file - delete it
						metadataHandler.removeMetadata(username, expo, metadata.getFilename());
						throw new IllegalArgumentException("Invalid OME metadata file: " + ex.getMessage());
					}
					cruise = cruiseHandler.addAddlDocToCruise(expo, omedata);
					try {
						// This is using the PI OME XML file at this time
						omePdfGenerator.createPiOmePdf(expo);
					} catch ( Exception ex ) {
						throw new IllegalArgumentException(
								"Unable to create the PDF from the OME XML: " + ex.getMessage());
					}
				}
				else {
					cruise = cruiseHandler.addAddlDocToCruise(expo, metadata);
				}
				if ( ! Boolean.TRUE.equals(cruise.isEditable()) ) {
					QCEvent qcEvent = new QCEvent();
					qcEvent.setExpocode(expo);
					qcEvent.setFlag(DashboardUtils.QC_UPDATED_FLAG);
					qcEvent.setFlagDate(new Date());
					qcEvent.setRegionID(DashboardUtils.GLOBAL_REGION_ID);
					qcEvent.setVersion(version);
					qcEvent.setUsername(username);
					String comment;
					if ( isOme )
						comment = "Update of OME metadata.  ";
					else
						comment = "Update of metadata file \"" + uploadFilename + "\".  ";
					comment += "Data and WOCE flags were not changed.";
					qcEvent.setComment(comment);
					try {
						// Add the 'U' QC flag
						dbHandler.addQCEvent(qcEvent);
						dsgFileHandler.updateQCFlag(qcEvent);
						// Update the dashboard status for the 'U' QC flag
						cruise.setQcStatus(DashboardUtils.QC_STATUS_SUBMITTED);
						// If archived, reset the archived status so the updated metadata will be archived
						if ( cruise.getArchiveStatus().equals(DashboardUtils.ARCHIVE_STATUS_ARCHIVED) )
							cruise.setArchiveStatus(DashboardUtils.ARCHIVE_STATUS_WITH_SOCAT);
						cruiseHandler.saveCruiseInfoToFile(cruise, comment);
					} catch (Exception ex) {
						// Should not fail.  
						// If does, do not delete the file since it is okay, and ignore the failure.
						;
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
		respWriter.println(DashboardUtils.FILE_CREATED_HEADER_TAG);
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
