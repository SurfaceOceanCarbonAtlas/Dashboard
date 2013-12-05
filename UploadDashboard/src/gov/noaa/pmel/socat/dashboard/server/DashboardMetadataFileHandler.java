/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.socat.dashboard.shared.DashboardUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.tmatesoft.svn.core.SVNException;

/**
 * Handles storage and retrieval of metadata files.
 *  
 * @author Karl Smith
 */
public class DashboardMetadataFileHandler extends VersionedFileHandler {

	private static final String METADATA_EXPOCODE_SUFFIX = "_metadata";
	private static final String METADATA_INFOFILE_EXTENSION = ".properties";
	private static final String METADATA_OWNER_ID = "metadataowner";
	private static final String UPLOAD_FILENAME_ID = "uploadfilename";
	private static final String EXPOCODE_FILENAME_ID = "expocodefilename";
	private static final String CRUISE_EXPOCODES_ID = "cruiseexpocodes";
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
			String svnUsername, String svnPassword) throws IllegalArgumentException {
		super(metadataFilesDirName, svnUsername, svnPassword);
	}

	/**
	 * Creates a new metadata expocode filename using the given cruise 
	 * expocode for first part of the expocode filename and the extension 
	 * of the upload filename for the extension to the expocode filename.
	 * Appends "_nn", where nn is some number, to the root name to give
	 * an filename root not already used.
	 * 
	 * @param cruiseExpocode
	 * 		cruise expocode to use
	 * @param uploadFilename
	 * 		upload filename of this metadata file
	 * @return
	 * 		a new expocode filename for this metadata file
	 */
	private String newMetadataExpocodeFilename(String cruiseExpocode, 
											   String uploadFilename) {
		// Make sure the expocode is uppercase
		String upperExpocode = cruiseExpocode.toUpperCase();
		// Root of the expocode filename
		final String prefix = upperExpocode + METADATA_EXPOCODE_SUFFIX;
		// Start building the filename
		String filename = prefix;
		// Look in the NODC code subdirectory for other files with this root 
		final File expoDir = new File(filesDir, upperExpocode.substring(0, 4));
		if ( expoDir.exists() ) {
			// Get a list of all filenames starting with the expocode metadata prefix 
			String[] matchingFilenames = expoDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if ( dir.equals(expoDir) && name.startsWith(prefix) )
						return true;
					return false;
				}
			});
			// null returned if problems
			if ( matchingFilenames == null )
				return null;
			// empty array returned if no matches
			if ( matchingFilenames.length > 0 ) {
				// Get an alphabetized set of all root names (ignore extensions)
				TreeSet<String> existingNames = new TreeSet<String>();
				for ( String fname : matchingFilenames ) {
					int idx = fname.indexOf(".");
					if ( idx > 0 )
						existingNames.add(fname.substring(0, idx));
				}
				// Get the last entry from the alphabetized set
				String lastFilename = existingNames.last();
				// Increment the unique ID number 
				int uid;
				if ( lastFilename.length() == prefix.length() )
					uid = 2;
				else
					uid = Integer.parseInt(
							lastFilename.substring(prefix.length()+1) ) + 1;
				filename += String.format("_%02d", uid);
			}
		}
		// Copy the filename extension from the upload filename
		int idx = uploadFilename.lastIndexOf(".");
		if ( idx > 0 )
			filename += uploadFilename.substring(idx);
		return filename;
	}

	/**
	 * @param expocodeFilename
	 * 		expocode filename of the metadata document
	 * @return
	 * 		the metadata properties (abstract) file associated
	 * 		with this metadata document
	 */
	private File getMetadataInfoFile(String expocodeFilename) {
		// Get the name of the associated properties file
		int idx = expocodeFilename.lastIndexOf(".");
		String rootName;
		if ( idx > 0 ) 
			rootName = expocodeFilename.substring(0, idx);
		else 
			rootName = expocodeFilename;
		File metaPropsFile = new File(filesDir, expocodeFilename.substring(0,4) + 
				File.separatorChar + rootName + METADATA_INFOFILE_EXTENSION);
		return metaPropsFile;
	}

	/**
	 * Create a new metadata document from the contents of a file upload.
	 * 
	 * @param cruiseExpocode
	 * 		cruise expocode to be associated with this metadata.
	 * 		This is used to create the root of the expocode filename.
	 * @param owner
	 * 		owner of the metadata.  This is only used when creating 
	 * 		the returned DashboardMetadata object.
	 * @param uploadFileItem
	 * 		upload file item providing the metadata contents as well
	 * 		as the name of the upload file.  This upload filename is 
	 * 		used to create the extension of the expocode filename.
	 * @return
	 * 		a DashboardMetadata describing the new metadata document 
	 * @throws IllegalArgumentException
	 * 		if unable to create the metadata document,
	 * 		if problems reading from the file upload stream,
	 * 		if problems writing to the new metadata document, or
	 * 		if problems committing the new metadata document to version control
	 */
	public DashboardMetadata saveNewMetadataFile(String cruiseExpocode, 
			String owner, FileItem uploadFileItem) throws IllegalArgumentException {
		// Note: potential clash in the code below in the unlikely situation 
		// where two threads are simultaneously creating metadata files for 
		// the same cruise

		// Create a new metadata filename
		String uploadFilename = uploadFileItem.getName();
		String expocodeFilename = 
				newMetadataExpocodeFilename(cruiseExpocode, uploadFilename);

		// Create the new metadata file from the uploaded contents
		File metadataFile = new File(filesDir, expocodeFilename.substring(0,4));
		if ( ! metadataFile.exists() ) {
			if ( ! metadataFile.mkdirs() )
				throw new IllegalArgumentException(
						"Problems creating the parent directory for " + 
								expocodeFilename);
		}
		metadataFile = new File(metadataFile, expocodeFilename);
		try {
			uploadFileItem.write(metadataFile);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems creating the new metadata document " +
					expocodeFilename + " (" + uploadFilename + "): " + 
					ex.getMessage());
		}

		// Commit the new file to version control
		String message = "New metadata document " + expocodeFilename + 
				" (" + uploadFilename + ") added for " + owner;
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
		metadata.setUploadFilename(uploadFilename);
		metadata.setExpocodeFilename(expocodeFilename);
		// No cruises associated with this new metadata file

		// Save the metadata properties
		saveMetadataInfo(metadata, message);

		return metadata;
	}

	/**
	 * Update an existing metadata document from the contents of a file upload.
	 * 
	 * @param expocodeFilename
	 * 		expocode filename of the metadata document to update.
	 * @param owner
	 * 		owner of the metadata.
	 * @param uploadFileItem
	 * 		upload file item providing the metadata contents as well
	 * 		as the name of the upload file.
	 * @return
	 * 		a DashboardMetadata describing the updated metadata document 
	 * @throws IllegalArgumentException
	 * 		if metadata document does not already exist,
	 * 		if problems reading from the file upload stream,
	 * 		if problems writing to the metadata document, or
	 * 		if problems committing the metadata document to version control
	 */
	public DashboardMetadata saveUpdatedMetadataFile(String expocodeFilename,
			String owner, FileItem uploadFileItem) throws IllegalArgumentException {
		// Check the metadata document exists on the system
		File metadataFile = new File(filesDir, expocodeFilename.substring(0,4) +
									 File.separatorChar + expocodeFilename);
		if ( ! metadataFile.exists() ) 
			throw new IllegalArgumentException("metadata document " + 
					expocodeFilename + " does not not exist");

		// Create the new metadata file from the uploaded contents
		String uploadFilename = uploadFileItem.getName();
		try {
			uploadFileItem.write(metadataFile);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems creating the new metadata document " +
					expocodeFilename + " (" + uploadFilename + "): " + 
					ex.getMessage());
		}

		String message = "Updated metadata document " + expocodeFilename + 
				" (" + uploadFilename + ") added for " + owner;
		try {
			commitVersion(metadataFile, message);
		} catch ( SVNException ex ) {
			throw new IllegalArgumentException("Problems committing " + 
					metadataFile.getPath() + " to version control: " + 
					ex.getMessage());
		}

		// Update the metadata properties file
		DashboardMetadata metadata = getMetadataInfo(expocodeFilename);
		if ( metadata == null )
			throw new IllegalArgumentException(
					"Unexpected error: metadata properties file for " + 
					expocodeFilename + " does not exist");
		boolean needsUpdate = false;
		// Update the upload filename
		if ( ! uploadFilename.equals(metadata.getUploadFilename()) ) {
			metadata.setUploadFilename(uploadFilename);
			needsUpdate = true;
		}
		// Update the owner if there is no owner of this file
		if ( metadata.getOwner().isEmpty() ) {
			metadata.setOwner(owner);
			needsUpdate = true;
		}
		// Expocode filename and associated cruises should not change

		// Save the updated metadata properties
		if ( needsUpdate ) 
			saveMetadataInfo(metadata, message);
		
		return metadata;
	}

	/**
	 * @param expocodeFilename
	 * 		expocode filename of the metadata file
	 * @return
	 * 		DashboardMetadata assigned from the properties file for the given 
	 * 		metadata document.  It will not be "selected".  If the properties 
	 * 		file does not exist, null is returned.
	 * @throws IllegalArgumentException
	 * 		if there were problems reading from the properties file, or
	 * 		if the expocode filename for the metadata document does not match 
	 * 		that in the properties file
	 */
	public DashboardMetadata getMetadataInfo(String expocodeFilename) 
											throws IllegalArgumentException {
		// Read the properties associated with this metadata document
		Properties metaProps = new Properties();
		try {
			BufferedReader propsReader = new BufferedReader(
					new FileReader(getMetadataInfoFile(expocodeFilename)));
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

		// Make sure the expocode filename matches
		String value = metaProps.getProperty(EXPOCODE_FILENAME_ID);
		if ( ! expocodeFilename.equals(value) ) 
			throw new IllegalArgumentException("Saved expocode filename (" + 
					value + ") does not match given expocode filename (" + 
					expocodeFilename + ")");

		// Create and assign the DashboardMetadata object to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setExpocodeFilename(expocodeFilename);
		// metadata owner
		value = metaProps.getProperty(METADATA_OWNER_ID);
		metadata.setOwner(value);
		// upload filename
		value = metaProps.getProperty(UPLOAD_FILENAME_ID);
		metadata.setUploadFilename(value);
		// associated cruise expocodes
		value = metaProps.getProperty(CRUISE_EXPOCODES_ID);
		if ( value != null ) {
			// Directly assign the set in the metadata object
			TreeSet<String> cruiseExpocodes = metadata.getAssociatedCruises();
			cruiseExpocodes.addAll(DashboardUtils.decodeStringArrayList(value));
		}

		return metadata;
	}

	/**
	 * Saves the properties for a metadata document to the appropriate
	 * metadata properties file.  A new properties file is saved and
	 * committed, even if there are no changes from what is currently saved. 
	 * 
	 * @param metadata
	 * 		metadata to save
	 * @param message
	 * 		version control commit message; if null, the commit is not
	 * 		performed
	 * @throws IllegalArgumentException
	 * 		if metadata does not have an expocode filename, 
	 * 		if there were problems saving the properties to file, or
	 * 		if there were problems commiting the properties file 
	 */
	public void saveMetadataInfo(DashboardMetadata metadata, String message) 
												throws IllegalArgumentException {
		// Get the abstract properties file for this metadata document
		String expocodeFilename = metadata.getExpocodeFilename();
		if ( expocodeFilename.isEmpty() )
			throw new IllegalArgumentException(
					"No metadata expocode filename given");
		File metaPropsFile = getMetadataInfoFile(expocodeFilename);
		// Make sure the parent subdirectory exists
		File parentFile = metaPropsFile.getParentFile();
		if ( ! parentFile.exists() )
			parentFile.mkdirs();
		// Create the properties for this metadata properties file
		Properties metaProps = new Properties();
		// Metadata expocode filename
		metaProps.setProperty(EXPOCODE_FILENAME_ID, expocodeFilename);
		// Owner of the metadata
		metaProps.setProperty(METADATA_OWNER_ID, metadata.getOwner());
		// Upload filename
		metaProps.setProperty(UPLOAD_FILENAME_ID, metadata.getUploadFilename());
		// Associated cruise expocodes
		String cruiseExpocodes = DashboardUtils.encodeStringArrayList(
				new ArrayList<String>(metadata.getAssociatedCruises()));
		metaProps.setProperty(CRUISE_EXPOCODES_ID, cruiseExpocodes);
		// Save the properties to the metadata properties file
		try {
			PrintWriter propsWriter = new PrintWriter(metaPropsFile);
			try {
				metaProps.store(propsWriter, null);
			} finally {
				propsWriter.close();
			}
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems writing metadata information for " + 
					expocodeFilename + " to " + metaPropsFile.getPath() + 
					": " + ex.getMessage());
		}
		
		if ( (message == null) || message.trim().isEmpty() )
			return;

		// Submit the updated information file to version control
		try {
			commitVersion(metaPropsFile, message);
		} catch ( Exception ex ) {
			throw new IllegalArgumentException(
					"Problems committing updated metadata information for  " + 
							expocodeFilename + ": " + ex.getMessage());
		}
	}

}
