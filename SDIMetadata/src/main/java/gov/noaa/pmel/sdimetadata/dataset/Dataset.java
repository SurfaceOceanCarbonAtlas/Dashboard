package gov.noaa.pmel.sdimetadata.dataset;

import java.util.ArrayList;

public class Dataset implements Cloneable {

    protected String datasetId;
    protected String datasetName;
    protected String description;
    protected String funding;
    protected String datasetDoi;
    protected String website;
    protected String citation;
    protected ArrayList<String> addnInfo;
    protected Datestamp startDatestamp;
    protected Datestamp endDatestamp;
    protected ArrayList<Datestamp> history;
    protected Coverage coverage;

    /**
     * Create with empty or invalid values for all fields.
     */
    public Dataset() {
        datasetId = "";
        datasetName = "";
        description = "";
        funding = "";
        datasetDoi = "";
        website = "";
        citation = "";
        addnInfo = new ArrayList<String>();
        startDatestamp = new Datestamp();
        endDatestamp = new Datestamp();
        history = new ArrayList<Datestamp>();
        coverage = new Coverage();
    }

    /**
     * @return the unique ID for this dataset; never null but may be empty
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId
     *         assign as the unique ID for this dataset; if null, an empty string is assigned
     */
    public void setDatasetId(String datasetId) {
        this.datasetId = (datasetId != null) ? datasetId.trim() : "";
    }

    /**
     * @return the PI's name for this dataset; never null but may be empty
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * @param datasetName
     *         assign as the PI's name for this dataset; if null, an empty string is assigned
     */
    public void setDatasetName(String datasetName) {
        this.datasetName = (datasetName != null) ? datasetName.trim() : "";
    }

    /**
     * @return the brief description for this dataset; never null but may be empty
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         assign as the brief description for this dataset; if null, an empty string is assigned
     */
    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }

    /**
     * @return the funding source for this dataset; never null but may be empty
     */
    public String getFunding() {
        return funding;
    }

    /**
     * @param funding
     *         assign as the funding source for this dataset; if null, an empty string is assigned
     */
    public void setFunding(String funding) {
        this.funding = (funding != null) ? funding.trim() : "";
    }

    /**
     * @return the DOI for this dataset; never null but may be empty
     */
    public String getDatasetDoi() {
        return datasetDoi;
    }

    /**
     * @param datasetDoi
     *         assign as the DOI for this dataset; if null, an empty string is assigned
     */
    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = (datasetDoi != null) ? datasetDoi.trim() : "";
    }

    /**
     * @return the web site for this dataset; never null but may be empty
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website
     *         assign as the website for this dataset; if null, an empty string is assigned
     */
    public void setWebsite(String website) {
        this.website = (website != null) ? website.trim() : "";
    }

    /**
     * @return the citation for this dataset; never null but may be empty
     */
    public String getCitation() {
        return citation;
    }

    /**
     * @param citation
     *         assign as the citation for this dataset; if null, an empty string is assigned
     */
    public void setCitation(String citation) {
        this.citation = (citation != null) ? citation.trim() : "";
    }

    /**
     * @return the list of addition information strings for this dataset; never null but may be empty.
     *         Any strings given are guaranteed to have some content (not null, not blank).
     */
    public ArrayList<String> getAddnInfo() {
        return new ArrayList<String>(addnInfo);
    }

    /**
     * @param addnInfo
     *         assign as the list of additional information strings for this dataset;
     *         if null, an empty list is assigned.
     *
     * @throws IllegalArgumentException
     *         if any of the additional information strings are null or empty
     */
    public void setAddnInfo(Iterable<String> addnInfo) throws IllegalArgumentException {
        this.addnInfo.clear();
        if ( addnInfo != null ) {
            for (String info : addnInfo) {
                if ( info == null )
                    throw new IllegalArgumentException("null additional information string given");
                info = info.trim();
                if ( info.isEmpty() )
                    throw new IllegalArgumentException("blank additional information string given");
                this.addnInfo.add(info);
            }
        }
    }

    /**
     * @return the starting date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getStartDatestamp() {
        return startDatestamp.clone();
    }

    /**
     * @param startDatestamp
     *         assign as the starting date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setStartDatestamp(Datestamp startDatestamp) {
        this.startDatestamp = (startDatestamp != null) ? startDatestamp.clone() : new Datestamp();
    }

    /**
     * @return the ending date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getEndDatestamp() {
        return endDatestamp.clone();
    }

    /**
     * @param endDatestamp
     *         assign as the ending date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setEndDatestamp(Datestamp endDatestamp) {
        this.endDatestamp = (endDatestamp != null) ? endDatestamp.clone() : new Datestamp();
    }

    /**
     * @return the submission date history list for this dataset; never null but maybe be empty.
     *         Any dates present are guaranteed to be valid.
     */
    public ArrayList<Datestamp> getHistory() {
        ArrayList<Datestamp> dup = new ArrayList<Datestamp>(history.size());
        for (Datestamp datestamp : history) {
            dup.add(datestamp.clone());
        }
        return dup;
    }

    /**
     * @param history
     *         assign as the submission date history list for this dataset; if null, an empty list is assigned.
     *
     * @throws IllegalArgumentException
     *         if any of the dates given are invalid
     */
    public void setHistory(Iterable<Datestamp> history) throws IllegalArgumentException {
        this.history.clear();
        if ( history != null ) {
            for (Datestamp datestamp : history) {
                if ( datestamp == null )
                    throw new IllegalArgumentException("null datestamp given");
                try {
                    datestamp.getEarliestTime();
                } catch ( Exception ex ) {
                    throw new IllegalArgumentException("invalid datestamp given: " + ex.getMessage(), ex);
                }
                this.history.add(datestamp.clone());
            }
        }
    }

    /**
     * @return the coverage of the data in this dataset; never null but may be unassigned
     */
    public Coverage getCoverage() {
        return coverage.clone();
    }

    /**
     * @param coverage
     *         assign as the coverage of the data in this dataset;
     *         if null, an unassigned coverage object is assigned
     */
    public void setCoverage(Coverage coverage) {
        this.coverage = (coverage != null) ? coverage.clone() : new Coverage();
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     */
    public boolean isValid() {
        if ( datasetId.isEmpty() )
            return false;
        double startTime = startDatestamp.getEarliestTime();
        double endTime = endDatestamp.getEarliestTime();
        if ( startTime > endTime )
            return false;
        if ( !coverage.isValid() )
            return false;
        // set end time to the end of the day
        endTime += 24.0 * 60.0 * 60.0;
        // check that the data times are all within the time range for the dataset
        if ( coverage.getEarliestDataTime() < startTime )
            return false;
        if ( coverage.getLatestDataTime() > endTime )
            return false;
        return true;
    }

    @Override
    public Dataset clone() {
        Dataset dup;
        try {
            dup = (Dataset) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.datasetId = datasetId;
        dup.datasetName = datasetName;
        dup.description = description;
        dup.funding = funding;
        dup.datasetDoi = datasetDoi;
        dup.website = website;
        dup.citation = citation;
        dup.addnInfo = new ArrayList<String>(addnInfo);
        dup.startDatestamp = startDatestamp.clone();
        dup.endDatestamp = endDatestamp.clone();
        dup.history = new ArrayList<Datestamp>(history.size());
        for (Datestamp datestamp : history) {
            dup.history.add(datestamp.clone());
        }
        dup.coverage = coverage.clone();
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Dataset) )
            return false;

        Dataset dataset = (Dataset) obj;

        if ( !datasetId.equals(dataset.datasetId) )
            return false;
        if ( !datasetName.equals(dataset.datasetName) )
            return false;
        if ( !description.equals(dataset.description) )
            return false;
        if ( !funding.equals(dataset.funding) )
            return false;
        if ( !datasetDoi.equals(dataset.datasetDoi) )
            return false;
        if ( !website.equals(dataset.website) )
            return false;
        if ( !citation.equals(dataset.citation) )
            return false;
        if ( !addnInfo.equals(dataset.addnInfo) )
            return false;
        if ( !startDatestamp.equals(dataset.startDatestamp) )
            return false;
        if ( !endDatestamp.equals(dataset.endDatestamp) )
            return false;
        if ( !history.equals(dataset.history) )
            return false;
        if ( !coverage.equals(dataset.coverage) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = datasetId.hashCode();
        result = result * prime + datasetName.hashCode();
        result = result * prime + description.hashCode();
        result = result * prime + funding.hashCode();
        result = result * prime + datasetDoi.hashCode();
        result = result * prime + website.hashCode();
        result = result * prime + citation.hashCode();
        result = result * prime + addnInfo.hashCode();
        result = result * prime + startDatestamp.hashCode();
        result = result * prime + endDatestamp.hashCode();
        result = result * prime + history.hashCode();
        result = result * prime + coverage.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "datasetId='" + datasetId + '\'' +
                ", datasetName='" + datasetName + '\'' +
                ", description='" + description + '\'' +
                ", funding='" + funding + '\'' +
                ", datasetDoi='" + datasetDoi + '\'' +
                ", website='" + website + '\'' +
                ", citation='" + citation + '\'' +
                ", addnInfo=" + addnInfo +
                ", startDatestamp=" + startDatestamp +
                ", endDatestamp=" + endDatestamp +
                ", history=" + history +
                ", coverage=" + coverage +
                '}';
    }

}

