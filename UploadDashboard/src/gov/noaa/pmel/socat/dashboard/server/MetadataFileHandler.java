/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.ome.OmeMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of metadata files.
 *  
 * @author Karl Smith
 */
public class MetadataFileHandler extends VersionedFileHandler {

	private static final String METADATA_INFOFILE_SUFFIX = ".properties";
	private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
	private static final String METADATA_OWNER_ID = "metadataowner";
	private static final String METADATA_CONFLICTED_ID = "metadataconflicted";

	/**
	 * Handles storage and retrieval of metadata files 
	 * under the given metadata files directory.
	 * 
	 * @param metadataFilesDirName
	 * 		name of the metadata files directory
	 * @param svnUsername
	 * 		username for SVN authentication
	 * @param svnPassword
	 * 		password for SVN authentication
	 * @throws IllegalArgumentException
	 * 		if the specified directory does not exist,
	 * 		is not a directory, or is not under SVN 
	 * 		version control
	 */
	public MetadataFileHandler(String metadataFilesDirName, 
							String svnUsername, String svnPassword) 
									throws IllegalArgumentException {
		super(metadataFilesDirName, svnUsername, svnPassword);
	}

	/**
	 * Generates the cruise-specific metadata file for a metadata document
	 * from the cruise expocode and the upload filename.
	 * 
	 * @param cruiseExpocode
	 * 		expocode of the cruise associated with this metadata document
	 * @param uploadName
	 * 		user's name of the uploaded metadata document 
	 * @return
	 * 		cruise-specific metadata file on the server
	 * @throws IllegalArgumentException
	 * 		if uploadName is null or ends in a slash or backslash, or 
	 * 		if the expocode is invalid
	 */
	public File getMetadataFile(String cruiseExpocode, String uploadName) 
											throws IllegalArgumentException {
		// Check and standardize the expocode
		String expocode = DashboardServerUtils.checkExpocode(cruiseExpocode);
		// Remove any path from uploadName
		String basename = DashboardUtils.baseName(uploadName);
		if ( basename.isEmpty() )
			throw new IllegalArgumentException(
					"Invalid metadate document name " + uploadName);
		// Generate the full path filename for this cruise metadata
		File metadataFile = new File(filesDir, expocode.substring(0,4) +
				File.separator + expocode + "_" + basename);
		return metadataFile;
	}

	/**
	 * Returns the list of metadata (including supplemental) documents associated
	 * with the given expocode.
	 * 
	 * @param cruiseExpocode
	 * 		get metadata documents for this expocode
	 * @return
	 * 		list of metadata documents; never null but may be empty
	 * @throws IllegalArgumentException
	 * 		if the expocode is invalid
	 */
	public ArrayList<DashboardMetadata> getMetadataFiles(String cruiseExpocode)
											throws IllegalArgumentException {
		ArrayList<DashboardMetadata> metadataList = new ArrayList<DashboardMetadata>();
		// Check and standardize the expocode
		final String expocode = DashboardServerUtils.checkExpocode(cruiseExpocode);
		// Get the parent directory for these metadata documents;
		// if it does not exist, return the empty list
		File parentDir = new File(filesDir, expocode.substring(0,4));
		if ( ! parentDir.isDirectory() )
			return metadataList;
		// Get all the metadata files for this expocode 
		File[] metafiles = parentDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if ( name.startsWith(expocode + "_") && 
					 name.endsWith(METADATA_INFOFILE_SUFFIX) )
					return true;
				return false;
			}
		});
		// Add the metadata info for all the metadata files (may be an empty array)
		for ( File mfile : metafiles ) {
			String basename = mfile.getName().substring(expocode.length() + 1, 
					mfile.getName().length() - METADATA_INFOFILE_SUFFIX.length());
			try {
				DashboardMetadata mdata = getMetadataInfo(expocode, basename);
				if ( mdata != null )
					metadataList.add(mdata);
			} catch ( Exception ex ) {
				// Ignore this entry if there are problems
			}
		}
		return metadataList;
	}

	/**
	 * Validates that a user has permission to delete or overwrite
	 * and existing metadata document.
	 * 	
	 * @param username
	 * 		name of user wanting to delete or overwrite the metadata document
	 * @param expocode
	 * 		expocode of the cruise associated with this metadata document
	 * @param metaname
	 * 		name of the metadata document to be deleted or overwritten
	 * @throws IllegalArgumentException
	 * 		if expocode or metaname are invalid, or
	 * 		if the user is not permitted to overwrite the metadata document
	 */
	private void verifyOkayToDelete(String username, String expocode, 
							String metaname) throws IllegalArgumentException {
		// If the info file does not exist, okay to delete the metadata
		DashboardMetadata oldMetadata = getMetadataInfo(expocode, metaname);
		if ( oldMetadata == null )
			return;
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected error obtaining the dashboard configuration");
		}
		String oldOwner = oldMetadata.getOwner();
		if ( ! dataStore.userManagesOver(username, oldOwner) )
			throw new IllegalArgumentException(
					"Not permitted to update metadata document " + 
					oldMetadata.getFilename() + " for cruise " + 
					oldMetadata.getExpocode() + " owned by " + oldOwner);
	}

	/**
	 * Create or update a metadata document from the contents of a file upload.
	 * 
	 * @param cruiseExpocode
	 * 		expocode of the cruise associated with this metadata document.
	 * @param owner
	 * 		owner of this metadata document.
	 * @param uploadTimestamp
	 * 		timestamp giving the time of the upload.  This should be 
	 * 		generated on the client and sent to the server so it is 
	 * 		in local time for the user.
	 * @param uploadFilename
	 * 		upload filename to use for this metadata document; 
	 * 		may or may not match the basename of uploadFileItem.getName()
	 * @param uploadFileItem
	 * 		upload file item providing the metadata contents as well
	 * 		as the name of the upload file.
	 * @return
	 * 		a DashboardMetadata describing the new or updated metadata document 
	 * @throws IllegalArgumentException
	 * 		if unable to create the metadata document,
	 * 		if problems reading from the file upload stream,
	 * 		if problems writing to the new metadata document, or
	 * 		if problems committing the new metadata document to version control
	 */
	public DashboardMetadata saveMetadataFile(String cruiseExpocode, 
			String owner, String uploadTimestamp, String uploadFilename,
			FileItem uploadFileItem) throws IllegalArgumentException {
		// Create the metadata filename
		File metadataFile = getMetadataFile(cruiseExpocode, uploadFilename);

		// Make sure the parent directory exists 
		File parentDir = metadataFile.getParentFile();
		if ( ! parentDir.exists() ) {
			if ( ! parentDir.mkdirs() )
				throw new IllegalArgumentException(
						"Problems creating the parent directory for " + 
						metadataFile.getPath());
		}

		// Check if this will overwrite existing metadata
		boolean isUpdate;
		if ( metadataFile.exists() ) {
			verifyOkayToDelete(owner, cruiseExpocode, uploadFilename);
			isUpdate = true;
		}
		else {
			isUpdate = false;
		}

		// Copy the uploaded data to the metadata document
		try {
			uploadFileItem.write(metadataFile);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems creating/updating the metadata document " +
					metadataFile.getPath() + ":\n    " + ex.getMessage());
		}

		// Create the appropriate check-in message
		String message;
		if ( isUpdate ) {
			message = "Updated metadata document " + uploadFilename + 
					  " for cruise " + cruiseExpocode + " and owner " + owner;
		}
		else {
			message = "Added metadata document " + uploadFilename + 
					  " for cruise " + cruiseExpocode + " and owner " + owner;
		}

		// Commit the new/updated metadata document to version control
		try {
			commitVersion(metadataFile, message);
		} catch ( SVNException ex ) {
			throw new IllegalArgumentException("Problems committing " + 
					metadataFile.getPath() + " to version control:\n    " + 
					ex.getMessage());
		}

		// Create the DashboardMetadata to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setExpocode(cruiseExpocode);
		metadata.setFilename(uploadFilename);
		metadata.setUploadTimestamp(uploadTimestamp);
		metadata.setOwner(owner);

		// Save the metadata properties
		if ( isUpdate ) {
			message = "Updated properties of metadata document " + uploadFilename + 
					  " for cruise " + cruiseExpocode + " and owner " + owner;
		}
		else {
			message = "Added properties of metadata document " + uploadFilename + 
					  " for cruise " + cruiseExpocode + " and owner " + owner;
		}
		saveMetadataInfo(metadata, message);

		return metadata;
	}

	/**
	 * Copy a metadata document to another cruise.  The document,
	 * as well as the owner and upload timestamp properties, is 
	 * copied under appropriate names for the new cruise.
	 * 
	 * @param destCruiseExpo
	 * 		expocode of the cruise to be associated with the 
	 * 		copy of the metadata file
	 * @param srcMetadata
	 * 		metadata document to be copied
	 * @return
	 * 		a DashboardMetadata describing the new or updated 
	 * 		metadata document copied from the another cruise
	 * @throws IllegalArgumentException
	 * 		if the metadata document to be copied does not exist, or
	 * 		if there were problems reading from the source metadata
	 * 		document, or if there were problems writing to the 
	 * 		destination metadata document.
	 */
	public DashboardMetadata copyMetadataFile(String destCruiseExpo,
			DashboardMetadata srcMetadata) throws IllegalArgumentException {
		String owner = srcMetadata.getOwner();
		String uploadName = srcMetadata.getFilename();
		// Get the source metadata document
		File srcFile = getMetadataFile(srcMetadata.getExpocode(), uploadName);
		if ( ! srcFile.exists() )
			throw new IllegalArgumentException("Source metadata file " + 
					srcFile.getPath() + " does not exist");

		// Get the destination metadata document 
		File destFile = getMetadataFile(destCruiseExpo, uploadName);
		File parentDir = destFile.getParentFile();
		if ( ! parentDir.exists() ) {
			if ( ! parentDir.mkdirs() )
				throw new IllegalArgumentException(
						"Problems creating the parent directory for " + 
						destFile.getPath());
		}

		// Check if this will overwrite existing metadata
		boolean isUpdate;
		if ( destFile.exists() ) {
			verifyOkayToDelete(owner, destCruiseExpo, uploadName);
			isUpdate = true;
		}
		else {
			isUpdate = false;
		}

		// Copy the metadata document
		try {
			FileInputStream src = null;
			FileOutputStream dest = null;
			try {
				src = new FileInputStream(srcFile);
				dest = new FileOutputStream(destFile);
				byte[] buff = new byte[4096];
				int numRead = src.read(buff);
				while ( numRead > 0 ) {
					dest.write(buff, 0, numRead);
					numRead = src.read(buff);
				}
			} finally {
				if ( dest != null )
					dest.close();
				if ( src != null )
					src.close();
			}
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(
					"Problems copying the metadata document " + 
					srcFile.getPath() + " to " + destFile.getPath() + 
					":\n    " + ex.getMessage());
		}

		// Create the appropriate check-in message
		String message;
		if ( isUpdate ) {
			message = "Updated metadata document " + uploadName + 
					  " for cruise " + destCruiseExpo + " and owner " + owner;
		}
		else {
			message = "Added metadata document " + uploadName + 
					  " for cruise " + destCruiseExpo + " and owner " + owner;
		}

		// Commit the new/updated metadata document to version control
		try {
			commitVersion(destFile, message);
		} catch ( SVNException ex ) {
			throw new IllegalArgumentException("Problems committing " + 
					destFile.getPath() + " to version control:\n    " + 
					ex.getMessage());
		}
		
		// Create the DashboardMetadata to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setExpocode(destCruiseExpo);
		metadata.setFilename(uploadName);
		metadata.setUploadTimestamp(srcMetadata.getUploadTimestamp());
		metadata.setOwner(owner);

		// Create the appropriate check-in message
		if ( isUpdate ) {
			message = "Updated properties of metadata document " + uploadName + 
					  " for cruise " + destCruiseExpo + " and owner " + owner;
		}
		else {
			message = "Added properties of metadata document " + uploadName + 
					  " for cruise " + destCruiseExpo + " and owner " + owner;
		}

		// Save the metadata properties
		saveMetadataInfo(metadata, message);

		return metadata;
	}

	/**
	 * Generates a DashboardMetadata initialized with the contents of
	 * the information (properties) file for the metadata.  It will not 
	 * be "selected".
	 * 
	 * @param expocode
	 * 		expocode of the cruise associated with this metadata
	 * @param metaname
	 * 		name of the metadata document
	 * @return
	 * 		DashboardMetadata assigned from the properties file for the 
	 * 		given metadata document.  If the properties file does not 
	 * 		exist, null is returned.
	 * @throws IllegalArgumentException
	 * 		if expocode or metaname is invalid, or
	 * 		if there were problems reading from the properties file
	 */
	public DashboardMetadata getMetadataInfo(String expocode, String metaname) 
											throws IllegalArgumentException {
		// Get the full path filename of the metadata file
		File metadataFile = getMetadataFile(expocode, metaname);
		// Read the properties associated with this metadata document
		Properties metaProps = new Properties();
		try {
			FileReader propsReader = new FileReader(
					new File(metadataFile.getPath() + METADATA_INFOFILE_SUFFIX));
			try {
				metaProps.load(propsReader);
			} finally {
				propsReader.close();
			}
		} catch ( FileNotFoundException ex ) {
			return null;
		} catch ( IOException ex ) {
			throw new IllegalArgumentException(ex);
		}

		// Create and assign the DashboardMetadata object to return
		DashboardMetadata metadata = new DashboardMetadata();
		// Cruise expocode
		metadata.setExpocode(expocode);
		// Metadata document name
		metadata.setFilename(metaname);
		// Upload timestamp
		String value = metaProps.getProperty(UPLOAD_TIMESTAMP_ID);
		metadata.setUploadTimestamp(value);
		// Owner
		value = metaProps.getProperty(METADATA_OWNER_ID);
		metadata.setOwner(value);
		// Conflicted flag
		value = metaProps.getProperty(METADATA_CONFLICTED_ID);
		metadata.setConflicted(Boolean.valueOf(value));

		return metadata;
	}

	/**
	 * Saves the properties for a metadata document to the appropriate
	 * metadata properties file.  A new properties file is saved and
	 * committed, even if there are no changes from what is currently 
	 * saved. 
	 * 
	 * @param metadata
	 * 		metadata to save
	 * @param message
	 * 		version control commit message; if null, the commit is not
	 * 		performed
	 * @throws IllegalArgumentException
	 * 		if there were problems saving the properties to file, or
	 * 		if there were problems committing the properties file 
	 */
	public void saveMetadataInfo(DashboardMetadata metadata, String message) 
											throws IllegalArgumentException {
		// Get full path name of the properties file
		File metadataFile = getMetadataFile(metadata.getExpocode(), 
											metadata.getFilename());
		File propsFile = new File(metadataFile.getPath() + METADATA_INFOFILE_SUFFIX);
		// Make sure the parent subdirectory exists
		File parentDir = propsFile.getParentFile();
		if ( ! parentDir.exists() )
			parentDir.mkdirs();
		// Create the properties for this metadata properties file
		Properties metaProps = new Properties();
		// Upload timestamp
		metaProps.setProperty(UPLOAD_TIMESTAMP_ID, metadata.getUploadTimestamp());
		// Owner 
		metaProps.setProperty(METADATA_OWNER_ID, metadata.getOwner());
		// Conflicted flag
		metaProps.setProperty(METADATA_CONFLICTED_ID, Boolean.toString(metadata.isConflicted()));
		// Save the properties to the metadata properties file
		try {
			FileWriter propsWriter = new FileWriter(propsFile);
			try {
				metaProps.store(propsWriter, null);
			} finally {
				propsWriter.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems writing metadata information for " + 
					metadata.getFilename() + " to " + propsFile.getPath() + 
					":\n    " + ex.getMessage());
		}
		
		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated information file to version control
		try {
			commitVersion(propsFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated metadata information for  " + 
					metadata.getFilename() + ":\n    " + ex.getMessage());
		}
	}

	/**
	 * Removes (deletes) a metadata document and its properties
	 * file, committing the change to version control.
	 * 
	 * @param username
	 * 		name of the user wanting to remove the metadata document
	 * @param expocode
	 * 		expocode of the cruise associated with this metadata
	 * @param metaname
	 * 		name of the metadata document
	 * @throws IllegalArgumentException 
	 * 		if expocode or metaname is invalid, 
	 * 		if the user is not permitted to delete the metadata document,
	 * 		if there are problems deleting the document.
	 */
	public void removeMetadata(String username, String expocode,
			String metaname) throws IllegalArgumentException {
		File metadataFile = getMetadataFile(expocode, metaname);
		File propsFile = new File(metadataFile.getPath() + METADATA_INFOFILE_SUFFIX);
		// Do not throw an error if the props file does not exist
		if ( propsFile.exists() ) { 
			// Throw an exception if not allowed to overwrite
			verifyOkayToDelete(username, expocode, metaname);
			try {
				deleteVersionedFile(propsFile, 
						"Deleted metadata properties " + propsFile.getPath());
			} catch ( Exception ex ) {
				throw new IllegalArgumentException(
						"Unable to delete metadata properties file " + propsFile.getPath());
			}
		}
		// Do not throw an error if the metadata file does not exist.
		// If the props file does not exist, assume it is okay to delete the metadata file.
		if ( metadataFile.exists() ) { 
			try {
				deleteVersionedFile(metadataFile, 
						"Deleted metadata document " + metadataFile.getPath());
			} catch ( Exception ex ) {
				throw new IllegalArgumentException(
						"Unable to delete metadata file " + metadataFile.getPath());
			}
		}
	}

	/**
	 * Save the pseudo-OME XML document created by {@link #createMinimalOmeXmlDoc()} 
	 * from the given OmeDocument as the document file for this metadata.  
	 * The parent directory for this file is expected to exist and this method will 
	 * overwrite any existing OME metadata file.
	 * 
	 * @param mdata
	 * 		OME metadata to save as a pseudo-OME XML document
	 * @param message
	 * 		version control commit message; if null, the commit is not
	 * 		performed
	 * @throws IllegalArgumentException
	 * 		if the expocode or uploadFilename in this object is invalid, or
	 * 		writing the metadata document file generates one.
	 */
	public void saveAsMinimalOmeXmlDoc(OmeMetadata mdata, String message) 
											throws IllegalArgumentException {
		// Get the metadata document file
		MetadataFileHandler mdataHandler;
		try {
			mdataHandler = DashboardDataStore.get().getMetadataFileHandler();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected failure to get the metadata handler");
		}
		File mdataFile = mdataHandler.getMetadataFile(mdata.getExpocode(), 
													  mdata.getFilename());

		// Generate the pseudo-OME XML document
		Document omeDoc = mdata.createMinimalOmeXmlDoc();

		// Save the XML document to the metadata document file
		try {
			FileOutputStream out = new FileOutputStream(mdataFile);
			try {
				(new XMLOutputter(Format.getPrettyFormat())).output(omeDoc, out);
			} finally {
				out.close();
			}
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Problems writing the OME metadata document: " +
					ex.getMessage());
		}

		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated information file to version control
		try {
			commitVersion(mdataFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated pseudo-OME metadata information " + 
					mdataFile.getPath() + ":\n    " + ex.getMessage());
		}
	}

}
