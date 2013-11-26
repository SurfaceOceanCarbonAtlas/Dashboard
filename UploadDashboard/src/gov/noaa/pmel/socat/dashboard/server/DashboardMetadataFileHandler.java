/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.server;

import gov.noaa.pmel.socat.dashboard.shared.DashboardMetadata;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
	 * @throws IOException
	 * 		if unable to create the metadata document,
	 * 		if problems reading from the file upload stream,
	 * 		if problems writing to the new metadata document, or
	 * 		if problems committing the new metadata document to version control
	 */
	public DashboardMetadata saveNewMetadataFile(String cruiseExpocode, 
			String owner, FileItem uploadFileItem) throws IOException {
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
				throw new IOException(
						"Problems creating the parent directory for " + 
								expocodeFilename);
		}
		metadataFile = new File(metadataFile, expocodeFilename);
		try {
			uploadFileItem.write(metadataFile);
		} catch (Exception ex) {
			throw new IOException(
					"Problems creating the new metadata document " +
					expocodeFilename + " (" + uploadFilename + "): " + 
					ex.getMessage());
		}

		// Commit the new file to version control
		try {
			commitVersion(metadataFile, "New metadata document " + 
					expocodeFilename + " (" + uploadFilename + 
					") added for " + owner);
		} catch (SVNException ex) {
			throw new IOException("Problems committing " + 
					metadataFile.getPath() + " to version control: " + 
					ex.getMessage());
		}

		// Create the DashboardMetadata to return
		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setOwner(owner);
		metadata.setUploadFilename(uploadFilename);
		metadata.setExpocodeFilename(expocodeFilename);
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
	 * @throws IOException
	 * 		if metadata document does not already exist,
	 * 		if problems reading from the file upload stream,
	 * 		if problems writing to the metadata document, or
	 * 		if problems committing the metadata document to version control
	 */
	public DashboardMetadata saveUpdatedMetadataFile(String expocodeFilename,
			String owner, FileItem uploadFileItem) throws IOException {
		// Check the metadata document exists on the system
		File metadataFile = new File(filesDir, expocodeFilename.substring(0,4) +
									 File.separatorChar + expocodeFilename);
		if ( ! metadataFile.exists() ) 
			throw new IOException("metadata document " + expocodeFilename +
								  " does not not exist");

		// Create the new metadata file from the uploaded contents
		String uploadFilename = uploadFileItem.getName();
		try {
			uploadFileItem.write(metadataFile);
		} catch (Exception ex) {
			throw new IOException(
					"Problems creating the new metadata document " +
					expocodeFilename + " (" + uploadFilename + "): " + 
					ex.getMessage());
		}

		try {
			commitVersion(metadataFile, "Updated metadata document " + 
					expocodeFilename + " (" + uploadFilename + 
					") added for " + owner);
		} catch (SVNException ex) {
			throw new IOException("Problems committing " + 
					metadataFile.getPath() + " to version control: " + 
					ex.getMessage());
		}

		DashboardMetadata metadata = new DashboardMetadata();
		metadata.setOwner(owner);
		metadata.setUploadFilename(uploadFilename);
		metadata.setExpocodeFilename(expocodeFilename);
		return metadata;
	}

}
