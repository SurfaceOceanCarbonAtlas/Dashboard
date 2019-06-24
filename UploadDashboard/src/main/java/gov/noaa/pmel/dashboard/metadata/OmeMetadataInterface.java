package gov.noaa.pmel.dashboard.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for dealing with various types of OME (metadata of a well-known format)
 * objects in the dashboard.
 */
public interface OmeMetadataInterface {

    /**
     * Assigns this OME object from the contents of the given metadata file.
     *
     * @param datasetId
     *         dataset ID (expocode) associated with this metadata
     * @param mdataFile
     *         read metadata from this file
     *
     * @throws IllegalArgumentException
     *         if the given dataset ID does not match that specified in the metadata file, or
     *         if the contents of the metadata files are invalid for assigning this OME object
     * @throws FileNotFoundException
     *         if the metadata file does not exist
     */
    void read(String datasetId, File mdataFile) throws IllegalArgumentException, FileNotFoundException;

    /**
     * Saves the contents of the OME object in the specified metadata file.
     * The contents of this file should allow regeneration of this OME object
     * from an appropriate call to {@link #read(String, File)}
     *
     * @param mdataFile
     *         metadata file to create or overwrite
     *
     * @throws IOException
     *         if there are problems writing the metadata file
     */
    void write(File mdataFile) throws IOException;

    /**
     * @return if the current contents this OME object are acceptable; in particular,
     *         all required fields have acceptable values and there are no conflicting field values.
     */
    boolean isAcceptable();

    /**
     * @return the dataset ID (expocode)
     */
    String getDatasetId();

    /**
     * @param newId
     *         dataset ID (expocode) to assign
     */
    void setDatasetId(String newId);

    /**
     * @return the PI's name for the dataset
     */
    String getDatasetName();

    /**
     * @param datasetName
     *         the PI's name for the dataset to set
     */
    void setDatasetName(String datasetName);

    /**
     * @return the name of the platform (ship name, mooring name)
     */
    String getPlatformName();

    /**
     * @param platformName
     *         the name of the platform (ship name, mooring name) to set
     */
    void setPlatformName(String platformName);

    /**
     * @return the platform type; one of "Ship", "Mooring", or "Drifting Buoy"
     */
    String getPlatformType();

    /**
     * @param platformType
     *         the platform type to assign; one of "Ship", "Mooring", or "Drifting Buoy"
     */
    void setPlatformType(String platformType);

    /**
     * @return list of principal investigators in citation order; never null but may be empty
     */
    ArrayList<String> getInvestigators();

    /**
     * @return list of organizations in citation order associated with the principal investigators;
     *         never null but may be empty
     */
    ArrayList<String> getOrganizations();

    /**
     * Assigns the principal investigators and their associated organizations
     *
     * @param investigators
     *         list of principal investigators in citation order
     * @param organizations
     *         list of the organization associated with each of the above principal investigators
     */
    void setInvestigatorsAndOrganizations(List<String> investigators, List<String> organizations);

    /**
     * @return the DOI of this dataset
     */
    String getDatasetDOI();

    /**
     * @param datasetDOI
     *         the DOI of this dataset to assign
     */
    void setDatasetDOI(String datasetDOI);

    /**
     * @return the http reference for this dataset
     */
    String getDatasetLink();

    /**
     * @param datasetLink
     *         the http reference to assign for this dataset
     */
    void setDatasetLink(String datasetLink);

    /**
     * @return the longitude, in units of degrees east, of the western-most data point in a dataset
     */
    Double getWesternLongitude();

    /**
     * @param westernLongitude
     *         the longitude, in units of degrees east, to set as the western-most data point in a dataset
     */
    void setWesternLongitude(Double westernLongitude);

    /**
     * @return the longitude, in units of degrees east, of the eastern-most data point in a dataset
     */
    Double getEasternLongitude();

    /**
     * @param easternLongitude
     *         the longitude, in units of degrees east, to set as the eastern-most data point in a dataset
     */
    void setEasternLongitude(Double easternLongitude);

    /**
     * @return the latitude, in units of degrees north, of the southern-most data point in a dataset
     */
    Double getSouthernLatitude();

    /**
     * @param southernLatitude
     *         the latitude, in units of degrees north, to set as the southern-most data point in a dataset
     */
    void setSouthernLatitude(Double southernLatitude);

    /**
     * @return the latitude, in units of degrees north, of the northern-most data point in a dataset
     */
    Double getNorthernLatitude();

    /**
     * @param northernLatitude
     *         the latitude, in units of degrees north, to set as the northern-most data point in a dataset
     */
    void setNorthernLatitude(Double northernLatitude);

    /**
     * @return the UTC time, in units of seconds since Jan 1 1970 00:00:00, of the earliest (starting) data point
     */
    Double getDataStartTime();

    /**
     * @param dataStartTime
     *         the UTC time, in units of seconds since Jan 1 1970 00:00:00, to set as the earliest (starting) data point
     */
    void setDataStartTime(Double dataStartTime);

    /**
     * @return the UTC time, in units of seconds since Jan 1 1970 00:00:00, of the latest (ending) data point
     */
    Double getDataEndTime();

    /**
     * @param dataEndTime
     *         the UTC time, in units of seconds since Jan 1 1970 00:00:00, to set as the latest (ending) data point
     */
    void setDataEndTime(Double dataEndTime);

}
