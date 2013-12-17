/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of metadata files.
 *  
 * @author Karl Smith
 */
public class DashboardMetadataFileHandler extends VersionedFileHandler {

	private static final String METADATA_INFOFILE_SUFFIX = ".properties";
	private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
	private static final String METADATA_OWNER_ID = "metadataowner";

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
	public DashboardMetadataFileHandler(String metadataFilesDirName, 
							String svnUsername, String svnPassword) 
									throws IllegalArgumentException {
		super(metadataFilesDirName, svnUsername, svnPassword);
	}

	/**
	 * Validates that a user has permission to delete or overwrite
	 * and existing metadata document.
	 * 	
	 * @param username
	 * 		name of user wanting to delete or overwrite the metadata document
	 * @param metadataName
	 * 		name of the metadata document to be deleted or overwritten
	 * @throws IllegalArgumentException
	 * 		if the user is not permitted to overwrite the metadata document
	 */
	private void verifyOkayToDelete(String username, String metadataName) 
											throws IllegalArgumentException {
		DashboardMetadata oldMData = getMetadataInfo(metadataName);
		if ( oldMData == null )
			return;
		DashboardDataStore dataStore;
		try {
			dataStore = DashboardDataStore.get();
		} catch (IOException ex) {
			throw new IllegalArgumentException(
					"Unexpected error obtaining the dashboard configuration");
		}
		String oldOwner = oldMData.getOwner();
		if ( ! dataStore.userManagesOver(username, oldOwner) )
			throw new IllegalArgumentException(
					"Not permitted to update metadata document " + 
					metadataName + " owned by " + oldOwner);
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
			String owner, String uploadTimestamp, FileItem uploadFileItem) 
											throws IllegalArgumentException {
		// Create the metadata filename
		String uploadFilename = uploadFileItem.getName();
		String metadataFilename = 
				DashboardUtils.metadataFilename(cruiseExpocode, uploadFilename);

		// Create the full path name for the metadata document 
		File metadataFile = new File(filesDir, metadataFilename.substring(0,4));
		if ( ! metadataFile.exists() ) {
			if ( ! metadataFile.mkdirs() )
				throw new IllegalArgumentException(
						"Problems creating the parent directory for " + 
						metadataFilename);
		}
		metadataFile = new File(metadataFile, metadataFilename);

		// Create the appropriate check-in message
		String message;
		if ( metadataFile.exists() ) {
			verifyOkayToDelete(owner, metadataFilename);
			message = "Updated metadata document " + metadataFilename + 
					" for " + owner;
		}
		else {
			message = "Added metadata document " + metadataFilename + 
					" for " + owner;
		}

		// Copy the uploaded data to the metadata document
		try {
			uploadFileItem.write(metadataFile);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems creating the new metadata document " +
					metadataFilename + ": " + ex.getMessage());
		}

		// Commit the new/updated metadata document to version control
		try {
			commitVersion(metadataFile, message);
		} catch ( SVNException ex ) {
			throw new IllegalArgumentException("Problems committing " + 
					metadataFile.getPath() + " to version control: " + 
					ex.getMessage());
		}

		// Create the DashboardMetadata to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setOwner(owner);
		metadata.setFilename(metadataFilename);
		metadata.setUploadTimestamp(uploadTimestamp);

		// Save the metadata properties
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
	 * @param uploadFilename
	 * 		name of the uploaded file
	 *  	(the user's unmodified filename)
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
			String uploadFilename, DashboardMetadata srcMetadata) 
									throws IllegalArgumentException {
		// Get the source metadata document information
		String owner = srcMetadata.getOwner();
		String srcName = srcMetadata.getFilename();
		File srcFile = new File(filesDir, srcName.substring(0, 4) + 
								File.separator + srcName);
		if ( ! srcFile.exists() )
			throw new IllegalArgumentException(
					"Source metadata file " + srcName + " does not exist");

		// Create the metadata filename
		String destName = 
				DashboardUtils.metadataFilename(destCruiseExpo, uploadFilename);
		// Create the full path name for the destination metadata document 
		File destFile = new File(filesDir, destName.substring(0,4));
		if ( ! destFile.exists() ) {
			if ( ! destFile.mkdirs() )
				throw new IllegalArgumentException(
						"Problems creating the parent directory for " + 
						destName);
		}
		destFile = new File(destFile, destName);

		// Create the appropriate check-in message
		String message;
		if ( destFile.exists() ) {
			verifyOkayToDelete(owner, destName);
			message = "Updated metadata document " + destName + 
					" for " + owner;
		}
		else {
			message = "Added metadata document " + destName + 
					" for " + owner;
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
					srcName + " to " + destName + ": " + ex.getMessage());
		}

		// Commit the new/updated metadata document to version control
		try {
			commitVersion(destFile, message);
		} catch ( SVNException ex ) {
			throw new IllegalArgumentException("Problems committing " + 
					destFile.getPath() + " to version control: " + 
					ex.getMessage());
		}
		
		// Create the DashboardMetadata to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setOwner(owner);
		metadata.setFilename(destName);
		metadata.setUploadTimestamp(srcMetadata.getUploadTimestamp());

		// Save the metadata properties
		saveMetadataInfo(metadata, message);

		return metadata;
	}

	/**
	 * @param metadataFilename
	 * 		name of the metadata document 
	 * 		(as returned by DashboardUtils.metadataFilename)
	 * @return
	 * 		DashboardMetadata assigned from the properties file for the given 
	 * 		metadata document.  It will not be "selected".  If the properties 
	 * 		file does not exist, null is returned.
	 * @throws IllegalArgumentException
	 * 		if there were problems reading from the properties file
	 */
	public DashboardMetadata getMetadataInfo(String metadataFilename) 
											throws IllegalArgumentException {
		// Read the properties associated with this metadata document
		Properties metaProps = new Properties();
		try {
			FileReader propsReader = new FileReader(new File(filesDir,
					metadataFilename.substring(0,4) + File.separator + 
					metadataFilename + METADATA_INFOFILE_SUFFIX));
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
		// Metadata document filename
		metadata.setFilename(metadataFilename);
		// Upload timestamp
		String value = metaProps.getProperty(UPLOAD_TIMESTAMP_ID);
		metadata.setUploadTimestamp(value);
		// Owner
		value = metaProps.getProperty(METADATA_OWNER_ID);
		metadata.setOwner(value);

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
		String propsFilename = metadata.getFilename() + METADATA_INFOFILE_SUFFIX;
		// Make sure the parent subdirectory exists
		File propsFile = new File(filesDir, propsFilename.substring(0,4));
		if ( ! propsFile.exists() )
			propsFile.mkdirs();
		propsFile = new File(propsFile, propsFilename);
		// Create the properties for this metadata properties file
		Properties metaProps = new Properties();
		// Upload timestamp
		metaProps.setProperty(UPLOAD_TIMESTAMP_ID, metadata.getUploadTimestamp());
		// Owner 
		metaProps.setProperty(METADATA_OWNER_ID, metadata.getOwner());
		// Save the properties to the metadata properties file
		try {
			PrintWriter propsWriter = new PrintWriter(propsFile);
			try {
				metaProps.store(propsWriter, null);
			} finally {
				propsWriter.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems writing metadata information for " + 
					metadata.getFilename() + " to " + propsFile.getPath() + 
					": " + ex.getMessage());
		}
		
		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated information file to version control
		try {
			commitVersion(propsFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated metadata information for  " + 
					metadata.getFilename() + ": " + ex.getMessage());
		}
	}

	/**
	 * Removes (deletes) a metadata document (including its properties
	 * file), committing the change to version control.
	 * 
	 * @param username
	 * 		name of the user wanting to remove the metadata document
	 * @param metadataName
	 * 		filename of the metadata document to remove
	 * 		(as returned by DashboardUtils.metadataFilename)
	 * @throws IllegalArgumentException 
	 * 		if the user is not permitted to delete the metadata document
	 * 		if there are problems deleting the document, or 
	 * 		if either of the document files do not exist.
	 */
	public void removeMetadata(String username, String mdataName) 
										throws IllegalArgumentException {
		File parentDir = new File(filesDir, mdataName.substring(0, 4));
		File mdataFile = new File(parentDir, mdataName);
		if ( ! mdataFile.exists() ) 
			throw new IllegalArgumentException(
					"Metadata file " + mdataFile.getPath() + 
					" does not exist");
		verifyOkayToDelete(username, mdataName);
		try {
			deleteVersionedFile(mdataFile, 
					"Deleted metadata document " + mdataName);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Unable to delete metadata file " + 
							mdataFile.getPath());
		}
		String propsName = mdataName + METADATA_INFOFILE_SUFFIX;
		File propsFile = new File(parentDir, propsName);
		if ( ! propsFile.exists() ) 
			throw new IllegalArgumentException(
					"Metadata properties file " + propsFile.getPath() + 
					" does not exist");
		try {
			deleteVersionedFile(propsFile, 
					"Deleted metadata properties " + propsName);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Unable to delete metadata file " + 
							mdataFile.getPath());
		}
	}

}
