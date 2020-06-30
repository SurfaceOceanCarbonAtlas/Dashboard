package gov.noaa.pmel.dashboard.handlers;

import gov.noaa.pmel.dashboard.metadata.CdiacOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.DashboardOmeMetadata;
import gov.noaa.pmel.dashboard.metadata.SocatOmeMetadata;
import gov.noaa.pmel.dashboard.qc.DataLocation;
import gov.noaa.pmel.dashboard.qc.DataQCEvent;
import gov.noaa.pmel.dashboard.qc.RowNumSet;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardMetadata;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.core.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.tmatesoft.svn.core.SVNException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * Handles storage and retrieval of metadata files.
 *
 * @author Karl Smith
 */
public class MetadataFileHandler extends VersionedFileHandler {

    private static final String STD_METADATA_SUFFIX = "_SocatMetadata.xml";
    private static final String INFOFILE_SUFFIX = ".properties";
    private static final String UPLOAD_TIMESTAMP_ID = "uploadtimestamp";
    private static final String METADATA_OWNER_ID = "metadataowner";
    private static final String METADATA_CONFLICTED_ID = "metadataconflicted";
    private static final String METADATA_VERSION_ID = "metadataversion";
    private static final String FLAG_MSGS_FILENAME = "WOCE_flags.tsv";
    private static final SimpleDateFormat DATETIME_FORMATTER;

    static {
        DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DATETIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    /**
     * Handles storage and retrieval of metadata files under the given metadata files directory.
     *
     * @param metadataFilesDirName
     *         name of the metadata files directory
     * @param svnUsername
     *         username for SVN authentication
     * @param svnPassword
     *         password for SVN authentication
     *
     * @throws IllegalArgumentException
     *         if the specified directory does not exist, is not a directory, or is not under SVN version control
     */
    public MetadataFileHandler(String metadataFilesDirName, String svnUsername, String svnPassword)
            throws IllegalArgumentException {
        super(metadataFilesDirName, svnUsername, svnPassword);
    }

    /**
     * Generates the virtual file for the SocatMetadata metadata file associated with this dataset.
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata document
     *
     * @return virtual file for the SocatMetadata metadata file
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid
     */
    public File getSocatMetadataFile(String datasetId) throws IllegalArgumentException {
        // Check and standardize the dataset
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Generate the full path filename for this metadata file
        File grandParentDir = new File(filesDir, stdId.substring(0, 4));
        File parentDir = new File(grandParentDir, stdId);
        return new File(parentDir, stdId + STD_METADATA_SUFFIX);
    }

    /**
     * Generates the virtual file for a metadata document from the dataset ID and the upload filename.
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata document
     * @param uploadName
     *         user's name of the uploaded metadata document
     *
     * @return virtual metadata file for this document
     *
     * @throws IllegalArgumentException
     *         if uploadName is null or ends in a slash or backslash, or if the dataset ID is invalid
     */
    public File getMetadataFile(String datasetId, String uploadName) throws IllegalArgumentException {
        // Check and standardize the dataset
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Remove any path from uploadName
        String basename = DashboardUtils.baseName(uploadName);
        if ( basename.isEmpty() )
            throw new IllegalArgumentException("Invalid metadate file name " + uploadName);
        // Generate the full path filename for this metadata file
        File grandParentDir = new File(filesDir, stdId.substring(0, 4));
        File parentDir = new File(grandParentDir, stdId);
        return new File(parentDir, basename);
    }

    /**
     * Returns the list of valid metadata files (including supplemental documents) associated with the given dataset.
     *
     * @param datasetId
     *         get metadata documents for the dataset with this ID
     *
     * @return list of metadata documents; never null but may be empty
     *
     * @throws IllegalArgumentException
     *         if the dataset Id is invalid
     */
    public ArrayList<DashboardMetadata> getMetadataFiles(String datasetId) throws IllegalArgumentException {
        ArrayList<DashboardMetadata> metadataList = new ArrayList<DashboardMetadata>();
        // Check and standardize the dataset
        final String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        // Get the parent directory for these metadata documents;
        File parentDir = getMetadataFile(stdId, "junk.txt").getParentFile();
        if ( !parentDir.isDirectory() )
            return metadataList;
        // Get all the metadata info files for this dataset
        File[] metafiles = parentDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(INFOFILE_SUFFIX);
            }
        });
        if ( metafiles == null )
            return metadataList;
        // Record the metadata file for each metadata info file (may be empty)
        for (File mfile : metafiles) {
            String basename = mfile.getName().substring(0,
                    mfile.getName().length() - INFOFILE_SUFFIX.length());
            try {
                DashboardMetadata mdata = getMetadataInfo(stdId, basename);
                if ( mdata != null )
                    metadataList.add(mdata);
            } catch ( Exception ex ) {
                // Ignore this entry if there are problems
            }
        }
        return metadataList;
    }

    /**
     * Validates that a user has permission to delete or overwrite an existing metadata document.
     *
     * @param username
     *         name of user wanting to delete or overwrite the metadata document
     * @param datasetId
     *         ID of the dataset associated with this metadata document
     * @param metaname
     *         name of the metadata document to be deleted or overwritten
     *
     * @throws IllegalArgumentException
     *         if the dataset ID or metaname are invalid, or
     *         if the user is not permitted to overwrite the metadata document
     */
    private void verifyOkayToDelete(String username, String datasetId, String metaname)
            throws IllegalArgumentException {
        // If the info file does not exist, okay to delete the metadata
        DashboardMetadata oldMetadata = getMetadataInfo(datasetId, metaname);
        if ( oldMetadata == null )
            return;

        DashboardConfigStore configStore;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected error obtaining the dashboard configuration");
        }

        String oldOwner = oldMetadata.getOwner();
        if ( !configStore.userManagesOver(username, oldOwner) )
            throw new IllegalArgumentException("Not permitted to update metadata document " +
                    oldMetadata.getFilename() + " for dataset " +
                    oldMetadata.getDatasetId() + " owned by " + oldOwner);
    }

    /**
     * Create or update a metadata document from the contents of a file upload.
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata document.
     * @param owner
     *         owner of this metadata document.
     * @param uploadTimestamp
     *         client-side timestamp giving the time of the upload.
     * @param uploadFilename
     *         upload filename to use for this metadata document;
     *         may or may not match the basename of uploadFileItem.getName()
     * @param version
     *         version for this metadata item
     * @param uploadFileItem
     *         upload file item providing the metadata contents
     *
     * @return a DashboardMetadata describing the new or updated metadata document; never null
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid,
     *         if problems reading from the file upload stream,
     *         if problems writing to the new metadata document, or
     *         if problems committing the new metadata document to version control
     */
    public DashboardMetadata saveMetadataFileItem(String datasetId, String owner, String uploadTimestamp,
            String uploadFilename, String version, FileItem uploadFileItem) throws IllegalArgumentException {
        // Create the metadata filename
        File metadataFile = getMetadataFile(datasetId, uploadFilename);

        // Make sure the parent directory exists
        File parentDir = metadataFile.getParentFile();
        if ( !parentDir.exists() ) {
            if ( !parentDir.mkdirs() )
                throw new IllegalArgumentException("Problems creating the parent directory for " +
                        metadataFile.getPath());
        }

        // Check if this will overwrite existing metadata
        boolean isUpdate;
        if ( metadataFile.exists() ) {
            verifyOkayToDelete(owner, datasetId, uploadFilename);
            isUpdate = true;
        }
        else {
            isUpdate = false;
        }

        // Copy the uploaded data to the metadata document
        try {
            uploadFileItem.write(metadataFile);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems creating/updating the metadata document " +
                    metadataFile.getPath() + ":\n    " + ex.getMessage());
        }

        // Create the appropriate check-in message
        String message;
        if ( isUpdate ) {
            message = "Updated metadata document " + uploadFilename + " for dataset " + datasetId + " and owner " + owner;
        }
        else {
            message = "Added metadata document " + uploadFilename + " for dataset " + datasetId + " and owner " + owner;
        }

        // Commit the new/updated metadata document to version control
        try {
            commitVersion(metadataFile, message);
        } catch ( SVNException ex ) {
            throw new IllegalArgumentException("Problems committing " + metadataFile.getPath() +
                    " to version control:\n    " + ex.getMessage());
        }

        // Create the DashboardMetadata to return
        DashboardMetadata metadata = new DashboardMetadata();
        metadata.setDatasetId(datasetId);
        metadata.setFilename(uploadFilename);
        metadata.setUploadTimestamp(uploadTimestamp);
        metadata.setOwner(owner);
        metadata.setVersion(version);

        // Save the metadata properties
        if ( isUpdate ) {
            message = "Updated properties of metadata document " + uploadFilename + " for dataset " + datasetId + " and owner " + owner;
        }
        else {
            message = "Added properties of metadata document " + uploadFilename + " for dataset " + datasetId + " and owner " + owner;
        }
        saveMetadataInfo(metadata, message, false);

        return metadata;
    }

    /**
     * Copy a metadata document to another dataset.  The document, as well as the owner, upload timestamp,
     * and version properties, are copied under appropriate names for the new dataset.
     *
     * @param destDatasetId
     *         ID of the dataset to be associated with the copy of the metadata file
     * @param srcMetadata
     *         metadata document to be copied
     * @param allowOverwrite
     *         allow overwrite an existing metadata file?
     *         If false and the metadata file exists, an IllegalArgumentException is raised
     *
     * @return a DashboardMetadata describing the new or updated metadata document
     *         copied from the another cruise; never null
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid, if the metadata document to be copied does not exist,
     *         if there were problems reading from the source metadata document, or
     *         if there were problems writing to the destination metadata document.
     */
    public DashboardMetadata copyMetadataFile(String destDatasetId, DashboardMetadata srcMetadata,
            boolean allowOverwrite) throws IllegalArgumentException {
        String owner = srcMetadata.getOwner();
        String uploadName = srcMetadata.getFilename();
        // Get an input stream for source metadata document file
        File srcFile = getMetadataFile(srcMetadata.getDatasetId(), uploadName);
        DashboardMetadata mdata;
        try {
            FileInputStream src = new FileInputStream(srcFile);
            try {
                // Create the metadata document from this input stream
                // allowing overwrite if permissions permit it
                mdata = saveMetadataInputStream(destDatasetId, owner, uploadName, srcMetadata.getUploadTimestamp(),
                        srcMetadata.getVersion(), src, allowOverwrite);
            } finally {
                src.close();
            }
        } catch ( IOException ex ) {
            // file not found; negligible possibility comes from close()
            throw new IllegalArgumentException("Problems with the metadata source file " +
                    srcFile.getPath() + ":\n    " + ex.getMessage());
        }
        return mdata;
    }

    /**
     * Creates or updates a metadata document from the contents of the file at the given URL.
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata document
     * @param owner
     *         owner of this metadata document
     * @param version
     *         version for this metadata document
     * @param urlString
     *         URL String of the document to download
     * @param allowOverwrite
     *         allow overwrite an existing metadata file?  If false and the metadata file exists,
     *         an IllegalArgumentException is raised and no data will have been read from src.
     *
     * @return a DashboardMetadata describing the new or updated metadata document; never null.
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid,
     *         if the URL String is invalid,
     *         if problems reading the metadata from the given URL if problems writing to the new metadata document, or
     *         if problems committing the new metadata document to version control
     * @throws IOException
     *         if problems opening the given URL for reading
     */
    public DashboardMetadata saveMetadataURL(String datasetId, String owner, String version,
            String urlString, boolean allowOverwrite) throws IllegalArgumentException, IOException {
        if ( urlString.endsWith("/") )
            throw new IllegalArgumentException("Invalid link document: " + urlString +
                    "\n    Not a file (ends in slash)");
        URL link;
        try {
            link = new URL(urlString);
        } catch ( MalformedURLException ex ) {
            throw new IllegalArgumentException("Invalid document link: " + urlString +
                    "\n    " + ex.getMessage());
        }
        String origName = (new File(link.getPath())).getName();
        if ( origName.trim().isEmpty() )
            throw new IllegalArgumentException("Invalid link document: " + urlString +
                    "\n    Not a file (empty name)");
        if ( origName.equalsIgnoreCase("index.html") || origName.equalsIgnoreCase("index.htm") )
            throw new IllegalArgumentException("Invalid link document: " + urlString +
                    "\n    index.html unlikely to be valid");
        String timestamp = DATETIME_FORMATTER.format(new Date());
        DashboardMetadata mdata;
        InputStream src = link.openStream();
        try {
            try {
                mdata = saveMetadataInputStream(datasetId, owner, origName, timestamp, version, src, allowOverwrite);
            } finally {
                src.close();
            }
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unable to read from the URL: " + urlString +
                    "\n    " + ex.getMessage());
        }
        return mdata;
    }

    /**
     * Create or update a metadata document from the given input stream
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata document.
     * @param owner
     *         owner of this metadata document.
     * @param origName
     *         "original" or "upload" filename to use for this metadata document
     * @param timestamp
     *         "upload" timestamp to assign for this metadata document
     * @param version
     *         version for this metadata document
     * @param src
     *         source to read for the contents of this metadata file
     * @param allowOverwrite
     *         allow overwrite an existing metadata file?  If false and the metadata file exists,
     *         an IllegalArgumentException is raised and no data will have been read from src.
     *
     * @return a DashboardMetadata describing the new or updated metadata document; never null.
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid,
     *         if problems reading the given metadata file data,
     *         if problems writing to the new metadata document, or
     *         if problems committing the new metadata document to version control
     */
    public DashboardMetadata saveMetadataInputStream(String datasetId, String owner, String origName, String timestamp,
            String version, InputStream src, boolean allowOverwrite) throws IllegalArgumentException {

        // Get the destination metadata document
        File destFile = getMetadataFile(datasetId, origName);
        File parentDir = destFile.getParentFile();
        if ( !parentDir.exists() ) {
            if ( !parentDir.mkdirs() )
                throw new IllegalArgumentException("Problems creating the parent directory for " + destFile.getPath());
        }

        // Check if this will overwrite existing metadata
        boolean isUpdate;
        if ( destFile.exists() ) {
            if ( !allowOverwrite )
                throw new IllegalArgumentException("Destination metdata file " + destFile.getName() + "already exists");
            verifyOkayToDelete(owner, datasetId, origName);
            isUpdate = true;
        }
        else {
            isUpdate = false;
        }

        // Copy the metadata document
        try {
            FileOutputStream dest = new FileOutputStream(destFile);
            try {
                byte[] buff = new byte[4096];
                int numRead = src.read(buff);
                while ( numRead > 0 ) {
                    dest.write(buff, 0, numRead);
                    numRead = src.read(buff);
                }
            } finally {
                dest.close();
            }
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Problems copying the metadata document " + origName +
                    " to " + destFile.getName() + ":\n    " + ex.getMessage());
        }

        // Create the appropriate check-in message
        String message;
        if ( isUpdate ) {
            message = "Updated metadata document " + origName + " for dataset " + datasetId;
        }
        else {
            message = "Added metadata document " + origName + " for dataset " + datasetId;
        }
        if ( (owner != null) && !owner.trim().isEmpty() ) {
            message += " with owner " + owner;
        }

        // Commit the new/updated metadata document to version control
        try {
            commitVersion(destFile, message);
        } catch ( SVNException ex ) {
            throw new IllegalArgumentException("Problems committing " + destFile.getName() +
                    " to version control:\n    " + ex.getMessage());
        }

        // Create the DashboardMetadata to return
        DashboardMetadata metadata = new DashboardMetadata();
        metadata.setDatasetId(datasetId);
        metadata.setFilename(origName);
        metadata.setUploadTimestamp(timestamp);
        metadata.setOwner(owner);
        metadata.setVersion(version);

        // Create the appropriate check-in message
        if ( isUpdate ) {
            message = "Updated properties of metadata document " + origName + " for dataset " + datasetId;
        }
        else {
            message = "Added properties of metadata document " + origName + " for dataset " + datasetId;
        }
        if ( (owner != null) && !owner.trim().isEmpty() ) {
            message += " with owner " + owner;
        }

        // Save the metadata properties
        saveMetadataInfo(metadata, message, false);

        return metadata;
    }

    /**
     * Generates a DashboardMetadata initialized with the contents of the information (properties) file
     * for the metadata.  It will not be "selected".
     *
     * @param datasetId
     *         ID of the dataset associated with this metadata
     * @param metaname
     *         name of the metadata document
     *
     * @return DashboardMetadata assigned from the properties file for the given metadata document.
     *         If the properties file does not exist, null is returned.
     *
     * @throws IllegalArgumentException
     *         if dataset ID or metaname is invalid, or
     *         if there were problems reading from the properties file
     */
    public DashboardMetadata getMetadataInfo(String datasetId, String metaname) throws IllegalArgumentException {
        // Get the full path filename of the metadata file
        File metadataFile = getMetadataFile(datasetId, metaname);
        // Read the properties associated with this metadata document
        Properties metaProps = new Properties();
        try {
            FileReader propsReader = new FileReader(new File(metadataFile.getPath() + INFOFILE_SUFFIX));
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
        metadata.setDatasetId(datasetId);
        metadata.setFilename(metaname);
        String value = metaProps.getProperty(UPLOAD_TIMESTAMP_ID);
        metadata.setUploadTimestamp(value);
        value = metaProps.getProperty(METADATA_OWNER_ID);
        metadata.setOwner(value);
        value = metaProps.getProperty(METADATA_CONFLICTED_ID);
        metadata.setConflicted(Boolean.parseBoolean(value));
        value = metaProps.getProperty(METADATA_VERSION_ID);
        metadata.setVersion(value);

        return metadata;
    }

    /**
     * Saves the properties for a metadata document to the appropriate metadata properties file.  A new properties file
     * is saved and committed, even if there are no changes from what is currently saved.
     *
     * @param metadata
     *         metadata to save
     * @param message
     *         version control commit message; if null, the commit is not performed
     * @param alsoCommitFile
     *         also commit the metadata file itself?
     *
     * @throws IllegalArgumentException
     *         if there were problems saving the properties to file, or
     *         if there were problems committing the properties file
     */
    public void saveMetadataInfo(DashboardMetadata metadata, String message, boolean alsoCommitFile)
            throws IllegalArgumentException {
        // Get full path name of the metadata file
        File metadataFile = getMetadataFile(metadata.getDatasetId(), metadata.getFilename());
        // Commit this metadata file if requested
        if ( alsoCommitFile && (message != null) && (!message.trim().isEmpty()) ) {
            // Submit the metadata file to version control
            try {
                commitVersion(metadataFile, message);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Problems committing the metadata file  " +
                        metadataFile.getPath() + ":\n    " + ex.getMessage());
            }
        }
        // Create the full path name of the metadata properties file
        File propsFile = new File(metadataFile.getPath() + INFOFILE_SUFFIX);
        // Make sure the parent subdirectory exists
        File parentDir = propsFile.getParentFile();
        if ( !parentDir.exists() )
            parentDir.mkdirs();
        // Create the properties for this metadata properties file
        Properties metaProps = new Properties();
        metaProps.setProperty(UPLOAD_TIMESTAMP_ID, metadata.getUploadTimestamp());
        metaProps.setProperty(METADATA_OWNER_ID, metadata.getOwner());
        metaProps.setProperty(METADATA_CONFLICTED_ID, Boolean.toString(metadata.isConflicted()));
        metaProps.setProperty(METADATA_VERSION_ID, metadata.getVersion());
        // Save the properties to the metadata properties file
        try {
            FileWriter propsWriter = new FileWriter(propsFile);
            try {
                metaProps.store(propsWriter, null);
            } finally {
                propsWriter.close();
            }
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems writing metadata information for " +
                    metadata.getFilename() + " to " + propsFile.getPath() +
                    ":\n    " + ex.getMessage());
        }

        if ( (message == null) || message.trim().isEmpty() )
            return;

        // Submit the updated information file to version control
        try {
            commitVersion(propsFile, message);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems committing updated metadata information for  " +
                    metadata.getFilename() + ":\n    " + ex.getMessage());
        }
    }

    /**
     * Appropriately renames any metadata documents and info files for a change in dataset ID.
     * Renames the dataset in the OME metadata file if it exists.
     *
     * @param oldId
     *         standardized old ID of the dataset
     * @param newId
     *         standardized new ID for the dataset
     *
     * @throws IllegalArgumentException
     *         if a metadata or info file for the new ID already exists,
     *         if the OME metadata exists but is invalid, or
     *         if unable to rename a metadata or info file
     */
    public void renameMetadataFiles(String oldId, String newId) throws IllegalArgumentException {
        // Rename all the metadata documents associated with the old dataset
        DashboardOmeMetadata omeMData = null;
        DashboardOmeMetadata piOmeMData = null;
        for (DashboardMetadata metaDoc : getMetadataFiles(oldId)) {
            String uploadFilename = metaDoc.getFilename();

            File oldMetaFile = getMetadataFile(oldId, uploadFilename);
            if ( !oldMetaFile.exists() )
                throw new RuntimeException("Unexpected failure: metadata file " +
                        oldMetaFile.getName() + " does not exist");

            // If this is an OME metadata file, read the contents
            if ( DashboardServerUtils.OME_FILENAME.equals(uploadFilename) ) {
                omeMData = new DashboardOmeMetadata(CdiacOmeMetadata.class, metaDoc, oldMetaFile);
            }
            else if ( DashboardServerUtils.PI_OME_FILENAME.equals(uploadFilename) ) {
                piOmeMData = new DashboardOmeMetadata(CdiacOmeMetadata.class, metaDoc, oldMetaFile);
            }

            File oldMetaInfoFile = new File(oldMetaFile.getPath() + INFOFILE_SUFFIX);
            if ( !oldMetaInfoFile.exists() )
                throw new RuntimeException("Unexpected failure: metadata info file " +
                        oldMetaInfoFile.getName() + " does not exist");

            File newMetaFile = getMetadataFile(newId, uploadFilename);
            if ( newMetaFile.exists() )
                throw new IllegalArgumentException("Metadata file " + uploadFilename + " already exists for " + newId);

            File newMetaInfoFile = new File(newMetaFile.getPath() + INFOFILE_SUFFIX);
            if ( newMetaInfoFile.exists() )
                throw new IllegalArgumentException("Metadata info file for " + uploadFilename +
                        " already exists for " + newId);

            // Make sure the parent directory exists for the new file
            File parent = newMetaFile.getParentFile();
            if ( !parent.exists() )
                parent.mkdirs();

            String commitMsg = "Move metadata document " + uploadFilename + " from " + oldId + " to " + newId;
            try {
                moveVersionedFile(oldMetaFile, newMetaFile, commitMsg);
                moveVersionedFile(oldMetaInfoFile, newMetaInfoFile, commitMsg);
            } catch ( SVNException ex ) {
                throw new IllegalArgumentException(ex);
            }
        }

        if ( omeMData != null ) {
            omeMData.changeDatasetID(newId);
            saveOmeToFile(omeMData, "Change dataset for OME XML document from " + oldId + " to " + newId);
        }
        if ( piOmeMData != null ) {
            piOmeMData.changeDatasetID(newId);
            saveOmeToFile(piOmeMData, "Change dataset for PI OME XML document from " + oldId + " to " + newId);
            // The PI_OME.pdf file will have been moved (as a normal metadata document)
            // but the dataset ID it contains needs to be updated, so regenerate it.
            try {
                DashboardConfigStore configStore = DashboardConfigStore.get(false);
                configStore.getOmePdfGenerator().createPiOmePdf(newId);
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Unable to create the PDF from the OME XML: " + ex.getMessage());
            }
        }
    }

    /**
     * Deletes a metadata document and its properties file, committing the change to version control.
     *
     * @param username
     *         name of the user wanting to remove the metadata document
     * @param datasetId
     *         ID of the dataset associated with this metadata
     * @param metaname
     *         name of the metadata document
     *
     * @throws IllegalArgumentException
     *         if the dataset ID or metaname is invalid,
     *         if the user is not permitted to delete the metadata document, or
     *         if there are problems deleting the document.
     */
    public void deleteMetadata(String username, String datasetId, String metaname) throws IllegalArgumentException {
        File metadataFile = getMetadataFile(datasetId, metaname);
        File propsFile = new File(metadataFile.getPath() + INFOFILE_SUFFIX);
        // Do not throw an error if the props file does not exist
        if ( propsFile.exists() ) {
            // Throw an exception if not allowed to overwrite
            verifyOkayToDelete(username, datasetId, metaname);
            try {
                deleteVersionedFile(propsFile, "Deleted metadata properties " + propsFile.getPath());
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Unable to delete metadata properties file " + propsFile.getPath());
            }
        }
        // Do not throw an error if the metadata file does not exist.
        // If the props file does not exist, assume it is okay to delete the metadata file.
        if ( metadataFile.exists() ) {
            try {
                deleteVersionedFile(metadataFile, "Deleted metadata document " + metadataFile.getPath());
            } catch ( Exception ex ) {
                throw new IllegalArgumentException("Unable to delete metadata file " + metadataFile.getPath());
            }
        }
    }

    /**
     * Save the OME object to the contents of this metadata file.  The parent directory for this file
     * is expected to exist and this method will overwrite any existing metadata file.  Note that this
     * does NOT save the information about this metadata file to the properties file; use a separate call
     * to {@link #saveMetadataInfo(DashboardMetadata, String, boolean)}
     *
     * @param mdata
     *         OME metadata to save
     * @param message
     *         version control commit message; if null, the commit is not performed
     *
     * @throws IllegalArgumentException
     *         if the dataset ID (expocode) or filename in the metadata properties is invalid, or
     *         if there are problems writing the metadata document to file
     */
    public void saveOmeToFile(DashboardOmeMetadata mdata, String message) throws IllegalArgumentException {
        File mdataFile = getMetadataFile(mdata.getDatasetId(), mdata.getFilename());
        mdata.saveOmeToFile(mdataFile);

        if ( (message == null) || message.trim().isEmpty() )
            return;

        // Submit the updated information file to version control
        try {
            commitVersion(mdataFile, message);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems committing updated OME metadata information " +
                    mdataFile.getPath() + ":\n    " + ex.getMessage());
        }
    }

    /**
     * Read an OME metadata file (metadata file of a well-known format) to create an OME metadata object.
     * This is a convenience function calling {@link #getOmeFromFile(DashboardMetadata)}.
     *
     * @param datasetId
     *         read the OME metadata file for the dataset with this ID (expocode)
     * @param metaname
     *         read the OME metadata file with this (upload) filename
     *
     * @return OME metadata object created from the contents of the OME metadata file
     *
     * @throws IllegalArgumentException
     *         if the dataset ID (expocode) is invalid,
     *         if the specified metadata file or its properties file does not exist,
     *         if the contents of the metadata file are invalid for all known OME classes, or
     *         if the dataset ID (expocode) in the metadata file does not match that given
     */
    public DashboardOmeMetadata getOmeFromFile(String datasetId, String metaname) throws IllegalArgumentException {
        return getOmeFromFile(getMetadataInfo(datasetId, metaname));
    }

    /**
     * Read an OME metadata file (metadata file of a well-known format) to create an OME metadata object.
     *
     * @param mdata
     *         properties of the OME metadata file to read
     *
     * @return OME metadata object created from the contents of the OME metadata file
     *
     * @throws IllegalArgumentException
     *         if the dataset ID (expocode) in the of the metadata properties file is invalid,
     *         if the metadata file indicated by the metadata properties file does not exist,
     *         if the contents of the metadata file are invalid for all known OME classes, or
     *         if the dataset ID (expocode) in the metadata file does not match that in the metadata properties
     */
    public DashboardOmeMetadata getOmeFromFile(DashboardMetadata mdata) throws IllegalArgumentException {
        File mdataFile = getMetadataFile(mdata.getDatasetId(), mdata.getFilename());
        DashboardOmeMetadata omeMData = null;
        IllegalArgumentException socatException = null;
        IllegalArgumentException cdiacException = null;
        try {
            // Try reading it as a SocatMetadata XML
            omeMData = new DashboardOmeMetadata(SocatOmeMetadata.class, mdata, mdataFile);
        } catch ( IllegalArgumentException ex ) {
            socatException = ex;
        }
        if ( omeMData == null ) {
            try {
                // Try reading it as CDIAC XML
                omeMData = new DashboardOmeMetadata(CdiacOmeMetadata.class, mdata, mdataFile);
            } catch ( IllegalArgumentException ex ) {
                cdiacException = ex;
            }
        }
        // TODO: try reading it as OCADS XML
        // If nothing can read the file, raise an IllegalArgumentException
        if ( omeMData == null )
            throw new IllegalArgumentException("Cannot read uploaded file as SocatMetadata or CDIAC XML: " +
                    "\n -- " + socatException.getMessage() +
                    "\n -- " + cdiacException.getMessage());
        return omeMData;
    }

    /**
     * Create the WOCE flags messages file from the WOCE flags in the database.
     * This file is NOT added as a metadata document.
     *
     * @param expocode
     *         create the WOCE flags messages files for the dataset with this ID
     * @param dbHandler
     *         get the WOCE flags from the database using this handler
     *
     * @throws IllegalArgumentException
     *         if the expocode is invalid,
     *         if unable to create the WOCE flags messages file, or
     *         if unable to commit the WOCE flags messages file to version control
     * @throws SQLException
     *         if there are problems getting the WOCE flags from the database
     */
    public void generateWoceFlagMsgsFile(String expocode, DatabaseRequestHandler dbHandler)
            throws IllegalArgumentException, SQLException {
        File msgsFile = getMetadataFile(expocode, FLAG_MSGS_FILENAME);
        PrintWriter msgsWriter;
        try {
            msgsWriter = new PrintWriter(msgsFile);
        } catch ( FileNotFoundException ex ) {
            throw new IllegalArgumentException("Unexpected error opening WOCE flag messages file " +
                    msgsFile.getPath() + "\n    " + ex.getMessage(), ex);
        }
        try {
            RowNumSet rowNums = new RowNumSet();
            // Get the current WOCE flags for this cruise and print them to file
            msgsWriter.println("Expocode: " + expocode);
            msgsWriter.println("WOCE-3 and WOCE-4 flags as of: " +
                    (new SimpleDateFormat("yyyy-MM-dd HH:mm Z")).format(new Date()));
            msgsWriter.println("WOCE Name\tWOCE Flag\tData Name\tNum Rows\tMessage\tRows");
            ArrayList<DataQCEvent> woceEventsList = dbHandler.getDataQCEvents(expocode, true);
            for (DataQCEvent woceEvent : woceEventsList) {
                // Only report '3' and '4' - skip 'Q' and 'B' which are for old versions
                String woceFlag = woceEvent.getFlagValue();
                if ( !(woceFlag.equals(DashboardServerUtils.WOCE_QUESTIONABLE) ||
                        woceFlag.equals(DashboardServerUtils.WOCE_BAD)) )
                    continue;
                rowNums.clear();
                for (DataLocation dloc : woceEvent.getLocations()) {
                    rowNums.add(dloc.getRowNumber());
                }
                msgsWriter.print(woceEvent.getFlagName());
                msgsWriter.print('\t');
                msgsWriter.print(woceFlag);
                msgsWriter.print('\t');
                String dataColName = woceEvent.getVarName();
                if ( dataColName.trim().isEmpty() )
                    dataColName = "(none)";
                msgsWriter.print(dataColName);
                msgsWriter.print('\t');
                msgsWriter.print(rowNums.size());
                msgsWriter.print('\t');
                msgsWriter.print(woceEvent.getComment().replaceAll("\n", "  ").replaceAll("\t", " "));
                msgsWriter.print('\t');
                msgsWriter.print(rowNums.toString());
                msgsWriter.println();
            }
        } finally {
            msgsWriter.close();
        }
        try {
            commitVersion(msgsFile, "(re)generating WOCE flags messages file for " + expocode);
        } catch ( SVNException ex ) {
            throw new IllegalArgumentException("Problems committing WOCE flag messages file " +
                    msgsFile.getPath() + ":\n    " + ex.getMessage());
        }
    }

    /**
     * Moves and updates the WOCE flag messages file appropriately for a change in the unique ID for a dataset.
     * Does nothing (other than check the validity of the old ID) if the WOCE flag messages file under the old ID
     * does not exist (for example, when the dataset has not yet been submitted for QC).
     *
     * @param oldId
     *         old unique ID (expocode) of the dataset
     * @param newId
     *         new unique ID (expocode) of the dataset
     * @param dbHandler
     *         get the WOCE flags from the database using this handler
     *
     * @throws IllegalArgumentException
     *         if either unique ID (expocode) is invalid,
     *         if a WOCE flag messages file already exists for the new ID,
     *         if unable to rename or update the WOCE flag messages file,
     *         if unable to commit the WOCE flags messages file to version control
     * @throws SQLException
     *         if there are problems getting the WOCE flags from the database
     */
    public void renameWoceFlagMsgsFile(String oldId, String newId, DatabaseRequestHandler dbHandler)
            throws IllegalArgumentException, SQLException {
        File oldMsgsFile = getMetadataFile(oldId, FLAG_MSGS_FILENAME);
        if ( oldMsgsFile.exists() ) {
            File newMsgsFile = getMetadataFile(newId, FLAG_MSGS_FILENAME);
            if ( newMsgsFile.exists() )
                throw new IllegalArgumentException("WOCE flag messages file already exists for " + newId);

            // Make sure the parent directory exists for the new file
            File parent = newMsgsFile.getParentFile();
            if ( !parent.exists() )
                parent.mkdirs();

            String commitMsg = "Move WOCE flag messages file from " + oldId + " to " + newId;
            try {
                moveVersionedFile(oldMsgsFile, newMsgsFile, commitMsg);
            } catch ( SVNException ex ) {
                throw new IllegalArgumentException(ex);
            }
            generateWoceFlagMsgsFile(newId, dbHandler);
        }
    }

    /**
     * Deletes the WOCE flags messages file and its properties file for a dataset
     *
     * @param username
     *         name of the user wanting to remove the WOCE flags messages
     * @param datasetId
     *         ID of the dataset associated with this WOCE flags messages file
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid,
     *         if the user is not permitted to delete the WOCE flags messages file, or
     *         if there are problems deleting the document.
     */
    public void deleteWoceFlagMsgsFile(String username, String datasetId) throws IllegalArgumentException {
        deleteMetadata(username, datasetId, FLAG_MSGS_FILENAME);
    }

    /**
     * Save the given SocatMetadata as the standard metadata file for the dataset.
     *
     * @param username
     *         name of user requesting to save the metadata
     * @param datasetId
     *         ID of the dataset associated with this metadata
     * @param metadata
     *         metadata to save
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid or does not match that given in the metadata,
     *         if the user is not permitted to overwrite the standard metadata file, or
     *         if there are problems writing the metadata to file
     */
    public void saveSocatMetadata(String username, String datasetId, SocatMetadata metadata)
            throws IllegalArgumentException {
        String mdataId = metadata.getMiscInfo().getDatasetId();
        if ( !mdataId.equals(datasetId) )
            throw new IllegalArgumentException("The dataset ID given in the metadata (" + mdataId +
                    ") does not match the ID of the dataset to associate with this metadata (" + datasetId + ")");

        // Get the full path name for the standard metadata file for this dataset
        File metadataFile = getSocatMetadataFile(datasetId);

        // Make sure the parent directory exists
        File parentDir = metadataFile.getParentFile();
        if ( !parentDir.exists() ) {
            if ( !parentDir.mkdirs() )
                throw new IllegalArgumentException("Problems creating the parent directory for " +
                        metadataFile.getPath());
        }

        // Check if this will overwrite existing metadata
        String message;
        if ( metadataFile.exists() ) {
            verifyOkayToDelete(username, datasetId, metadataFile.getName());
            message = "Updated SocatMetadata document for dataset " + datasetId + " and owner " + username;
        }
        else {
            message = "Added SocatMetadata document for dataset " + datasetId + " and owner " + username;
        }

        // Get the current SOCAT upload version number
        DashboardConfigStore configStore;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected error obtaining the dashboard configuration");
        }
        String version = configStore.getUploadVersion();

        // Use the current time for the timestamp
        String timestamp = DATETIME_FORMATTER.format(new Date());

        // Create the metadata properties for the SocatMetadata file
        DashboardOmeMetadata omeinfo = new DashboardOmeMetadata(new SocatOmeMetadata(metadata),
                metadataFile.getName(), timestamp, username, version);

        // Save the SocatMetadata as the XML file
        omeinfo.saveOmeToFile(metadataFile);
        // Save the properties file for this XML file
        saveMetadataInfo(omeinfo, message, true);
    }

    /**
     * Modify fields in the given metadata so they are appropriate for the given dataset ID
     *
     * @param datasetId
     *         dataset ID to use
     * @param metadata
     *         metadata to update
     *
     * @throws IllegalArgumentException
     *         if the dataset ID is invalid
     */
    private static void setSocatMetadataDatasetId(String datasetId, SocatMetadata metadata)
            throws IllegalArgumentException {
        MiscInfo miscInfo = metadata.getMiscInfo();
        miscInfo.setDatasetId(datasetId);
        metadata.setMiscInfo(miscInfo);
        Platform platform = metadata.getPlatform();
        platform.setPlatformId(DashboardServerUtils.getShipCodeFromDatasetID(datasetId));
        metadata.setPlatform(platform);
        Coverage coverage = metadata.getCoverage();
        coverage.setStartDatestamp(DashboardServerUtils.getDatestampFromDatasetID(datasetId));
        metadata.setCoverage(coverage);
    }

    /**
     * Read the standard metadata for a dataset from file, and update appropriate fields from the dataset data.
     * If the standard metadata file does not exist, new standard metadata is created.
     * In either case, the appropriate metadata values are updated from the dataset data.
     *
     * @param datasetId
     *         get the standard metadata for the dataset with this ID
     *
     * @return the standard metadata for the indicated dataset
     */
    public SocatMetadata getSocatMetadata(String datasetId) {
        File metadataFile = getSocatMetadataFile(datasetId);
        SocatMetadata metadata;
        try {
            SocatOmeMetadata omeMetadata = new SocatOmeMetadata(datasetId, metadataFile);
            metadata = omeMetadata.createSocatMetadataFromContents();
        } catch ( Exception ex ) {
            metadata = new SocatMetadata();
            setSocatMetadataDatasetId(datasetId, metadata);
        }

        DashboardConfigStore configStore;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected error obtaining the dashboard configuration");
        }
        configStore.getDataFileHandler().updateSocatMetadataFromData(metadata);

        return metadata;
    }

    /**
     * Read the standard metadata for one dataset, appropriately modifies it for use by another dataset,
     * and saves it as the standard dataset for the other dataset.
     *
     * @param username
     *         name of the user requesting the copy
     * @param toId
     *         save the modified standard metadata for this dataset
     * @param fromId
     *         read the standard metadata for this dataset
     *
     * @throws IllegalArgumentException
     *         if either dataset ID is invalid,
     *         if there are problems reading the standard metdata for fromId,
     *         if the user is not permitted to overwrite the standard metadata for this dataset, or
     *         if there are problems writing the standard metadata for toId
     */
    public void copySocatMetadata(String username, String toId, String fromId) {
        // Get the standard metadata for fromId
        SocatMetadata metadata = getSocatMetadata(fromId);
        // Modify fields associated with the dataset ID
        setSocatMetadataDatasetId(toId, metadata);
        // Update the appropriate metadata fields from the data
        DashboardConfigStore configStore;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( IOException ex ) {
            throw new IllegalArgumentException("Unexpected error obtaining the dashboard configuration");
        }
        configStore.getDataFileHandler().updateSocatMetadataFromData(metadata);
        // Save as the standard metadata for toId
        saveSocatMetadata(username, toId, metadata);
    }

    /**
     * Return the list of dataset IDs which have standard metadata which can be modified by the specified user.
     *
     * @param username
     *         name of the user
     *
     * @return list of dataset IDs; never null but could be empty
     */
    public ArrayList<String> getDatasetIdsWithSocatMetadata(String username) {
        File[] socatMetadataFiles = filesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(STD_METADATA_SUFFIX);
            }
        });
        if ( socatMetadataFiles == null )
            return new ArrayList<String>(0);
        ArrayList<String> datasetIds = new ArrayList<String>(socatMetadataFiles.length);
        for (File mdataFile : socatMetadataFiles) {
            String filename = mdataFile.getName();
            String id = filename.substring(0, filename.indexOf(STD_METADATA_SUFFIX));
            // Only return the IDs of the metadata modifiable by the user
            try {
                verifyOkayToDelete(username, id, filename);
                // allowed, so add this entry
                datasetIds.add(id);
            } catch ( Exception ex ) {
                // not allowed, so ignore this entry
            }
        }
        return datasetIds;
    }

}
