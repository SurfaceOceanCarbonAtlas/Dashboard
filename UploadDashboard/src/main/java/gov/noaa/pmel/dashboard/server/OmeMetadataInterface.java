package gov.noaa.pmel.dashboard.server;

import org.jdom2.Document;

import java.util.ArrayList;
import java.util.List;

public interface OmeMetadataInterface {

    /**
     * @return a {@link Document} containing all relevant information in this OME object.
     *         This Document should be populated such that this OME object can be recreated
     *         from {@link #assignFromDocument(Document)}
     */
    Document createDocument();

    /**
     * Assign fields in this OME object from values obtained in the given {@link Document}.
     *
     * @param doc
     *         Document to use
     *
     * @throws IllegalArgumentException
     *         if there is a problem interpreting the given Document
     */
    void assignFromDocument(Document doc) throws IllegalArgumentException;

    /**
     * Creates an new OME object that is the result of merging this OME object with another
     * OME object with the same dataset ID / Expocode.  In cases where there are conflicting
     * values in the two objects, the two conflicting values are stored and a conflict flag is set.
     *
     * @param other
     *         OME object to merge with this OME object
     *
     * @return new OME object containing the merged contents
     *
     * @throws IllegalArgumentException
     *         if the dataset IDs do not match, or
     *         if unable to interpret the contents of the OME object to merge in
     *         (e.g., unknown implementation of OmeMetadataInterface)
     */
    OmeMetadataInterface merge(OmeMetadataInterface other) throws IllegalArgumentException;

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
     *         the DOI to assign for this dataset
     */
    void setDatasetDOI(String datasetDOI);

    /**
     * @return the citation for this dataset
     */
    String getDatasetRefs();

    /**
     * @param datasetRefs
     *         the citation to assign for this dataset
     */
    void setDatasetRefs(String datasetRefs);

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
     * @return the UTC time, in units of seconds since Jan 1 1970 00:00:00, of the earlier (starting) data point
     */
    Double getDataStartTime();

    /**
     * @param dataStartTime
     *         the UTC time, in units of seconds since Jan 1 1970 00:00:00, to set as the earlier (starting) data point
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
