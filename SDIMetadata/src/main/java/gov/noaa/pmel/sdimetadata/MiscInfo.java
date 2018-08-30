package gov.noaa.pmel.sdimetadata;

import gov.noaa.pmel.sdimetadata.util.Datestamp;

import java.util.ArrayList;

/**
 * Miscellaneous information about a dataset.
 */
public class MiscInfo implements Cloneable {

    protected String datasetId;
    protected String datasetName;
    protected String fundingAgency;
    protected String fundingTitle;
    protected String fundingId;
    protected String researchProject;
    protected String datasetDoi;
    protected String accessId;
    protected String website;
    protected String downloadUrl;
    protected String citation;
    protected String synopsis;
    protected String purpose;
    protected ArrayList<String> references;
    protected ArrayList<String> addnInfo;
    protected Datestamp startDatestamp;
    protected Datestamp endDatestamp;
    protected ArrayList<Datestamp> history;

    /**
     * Create with empty or invalid values for all fields.
     */
    public MiscInfo() {
        datasetId = "";
        datasetName = "";
        fundingAgency = "";
        fundingTitle = "";
        fundingId = "";
        researchProject = "";
        datasetDoi = "";
        accessId = "";
        website = "";
        downloadUrl = "";
        citation = "";
        synopsis = "";
        purpose = "";
        references = new ArrayList<String>();
        addnInfo = new ArrayList<String>();
        startDatestamp = new Datestamp();
        endDatestamp = new Datestamp();
        history = new ArrayList<Datestamp>();
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
     * @return the funding agency name; never null but may be empty
     */
    public String getFundingAgency() {
        return fundingAgency;
    }

    /**
     * @param fundingAgency
     *         assign as the funding agency name; if null, an empty string is assigned
     */
    public void setFundingAgency(String fundingAgency) {
        this.fundingAgency = (fundingAgency != null) ? fundingAgency.trim() : "";
    }

    /**
     * @return the funding grant title; never null but may be empty
     */
    public String getFundingTitle() {
        return fundingTitle;
    }

    /**
     * @param fundingTitle
     *         assign as the funding grant title; if null, an empty string is assigned
     */
    public void setFundingTitle(String fundingTitle) {
        this.fundingTitle = (fundingTitle != null) ? fundingTitle.trim() : "";
    }

    /**
     * @return the funding grant ID; never null but may be empty
     */
    public String getFundingId() {
        return fundingId;
    }

    /**
     * @param fundingId
     *         assign as the funding grant ID; if null, an empty string is assigned
     */
    public void setFundingId(String fundingId) {
        this.fundingId = (fundingId != null) ? fundingId.trim() : "";
    }

    /**
     * @return the research project title; never null but may be empty
     */
    public String getResearchProject() {
        return researchProject;
    }

    /**
     * @param researchProject
     *         assign as the research project ID; if null, an empty string is assigned
     */
    public void setResearchProject(String researchProject) {
        this.researchProject = (researchProject != null) ? researchProject.trim() : "";
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
     * @return the access ID for this dataset; never null but may be empty
     */
    public String getAccessId() {
        return accessId;
    }

    /**
     * @param accessId
     *         assign as the access ID for this dataset; if null, an empty string is assigned
     */
    public void setAccessId(String accessId) {
        this.accessId = (accessId != null) ? accessId.trim() : "";
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
     * @return the download URL String for this dataset; never null but may be empty
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * @param downloadUrl
     *         assign as the download URL String for this dataset; if null, an empty string is assigned
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = (downloadUrl != null) ? downloadUrl.trim() : "";
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
     * @return the synopsis / abstract for this dataset; never null but may be empty
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * @param synopsis
     *         assign as the synposis / abstract for this dataset; if null, an empty string is assigned
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = (synopsis != null) ? synopsis.trim() : "";
    }

    /**
     * @return the purpose for this dataset; never null but may be empty
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose
     *         assign as the purpose for this dataset; if null, an empty string is assigned
     */
    public void setPurpose(String purpose) {
        this.purpose = (purpose != null) ? purpose.trim() : "";
    }

    /**
     * @return the list of references used by this dataset; never null but may be empty.
     *         Any reference given is guaranteed to be a valid (non-blank) string.
     */
    public ArrayList<String> getReferences() {
        return new ArrayList<String>(references);
    }

    /**
     * @param references
     *         assign as the list of references used by this dataset; if null, an empty list is assigned
     *
     * @throws IllegalArgumentException
     *         if any reference given is null or blank
     */
    public void setReferences(Iterable<String> references) throws IllegalArgumentException {
        this.references.clear();
        if ( references != null ) {
            for (String ref : references) {
                if ( ref == null )
                    throw new IllegalArgumentException("null reference given");
                ref = ref.trim();
                if ( ref.isEmpty() )
                    throw new IllegalArgumentException("blank reference given");
                this.references.add(ref);
            }
        }
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
     * @return whether all the required fields are assigned with valid values.
     */
    public boolean isValid() {
        if ( datasetId.isEmpty() )
            return false;
        if ( startDatestamp.getEarliestTime() > endDatestamp.getEarliestTime() )
            return false;
        return true;
    }

    @Override
    public MiscInfo clone() {
        MiscInfo dup;
        try {
            dup = (MiscInfo) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.datasetId = datasetId;
        dup.datasetName = datasetName;
        dup.fundingAgency = fundingAgency;
        dup.fundingTitle = fundingTitle;
        dup.fundingId = fundingId;
        dup.researchProject = researchProject;
        dup.datasetDoi = datasetDoi;
        dup.accessId = accessId;
        dup.website = website;
        dup.downloadUrl = downloadUrl;
        dup.citation = citation;
        dup.synopsis = synopsis;
        dup.purpose = purpose;
        dup.references = new ArrayList<String>(references);
        dup.addnInfo = new ArrayList<String>(addnInfo);
        dup.startDatestamp = startDatestamp.clone();
        dup.endDatestamp = endDatestamp.clone();
        dup.history = new ArrayList<Datestamp>(history.size());
        for (Datestamp datestamp : history) {
            dup.history.add(datestamp.clone());
        }
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof MiscInfo) )
            return false;

        MiscInfo miscInfo = (MiscInfo) obj;

        if ( !datasetId.equals(miscInfo.datasetId) )
            return false;
        if ( !datasetName.equals(miscInfo.datasetName) )
            return false;
        if ( !fundingAgency.equals(miscInfo.fundingAgency) )
            return false;
        if ( !fundingTitle.equals(miscInfo.fundingTitle) )
            return false;
        if ( !fundingId.equals(miscInfo.fundingId) )
            return false;
        if ( !researchProject.equals(miscInfo.researchProject) )
            return false;
        if ( !datasetDoi.equals(miscInfo.datasetDoi) )
            return false;
        if ( !accessId.equals(miscInfo.accessId) )
            return false;
        if ( !website.equals(miscInfo.website) )
            return false;
        if ( !downloadUrl.equals(miscInfo.downloadUrl) )
            return false;
        if ( !citation.equals(miscInfo.citation) )
            return false;
        if ( !synopsis.equals(miscInfo.synopsis) )
            return false;
        if ( !purpose.equals(miscInfo.purpose) )
            return false;
        if ( !references.equals(miscInfo.references) )
            return false;
        if ( !addnInfo.equals(miscInfo.addnInfo) )
            return false;
        if ( !startDatestamp.equals(miscInfo.startDatestamp) )
            return false;
        if ( !endDatestamp.equals(miscInfo.endDatestamp) )
            return false;
        if ( !history.equals(miscInfo.history) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = datasetId.hashCode();
        result = result * prime + datasetName.hashCode();
        result = result * prime + fundingAgency.hashCode();
        result = result * prime + fundingTitle.hashCode();
        result = result * prime + fundingId.hashCode();
        result = result * prime + researchProject.hashCode();
        result = result * prime + datasetDoi.hashCode();
        result = result * prime + accessId.hashCode();
        result = result * prime + website.hashCode();
        result = result * prime + downloadUrl.hashCode();
        result = result * prime + citation.hashCode();
        result = result * prime + synopsis.hashCode();
        result = result * prime + purpose.hashCode();
        result = result * prime + references.hashCode();
        result = result * prime + addnInfo.hashCode();
        result = result * prime + startDatestamp.hashCode();
        result = result * prime + endDatestamp.hashCode();
        result = result * prime + history.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "datasetId='" + datasetId + '\'' +
                ", datasetName='" + datasetName + '\'' +
                ", fundingAgency='" + fundingAgency + '\'' +
                ", fundingTitle='" + fundingTitle + '\'' +
                ", fundingId='" + fundingId + '\'' +
                ", researchProject='" + researchProject + '\'' +
                ", datasetDoi='" + datasetDoi + '\'' +
                ", accessId='" + accessId + '\'' +
                ", website='" + website + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", citation='" + citation + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", purpose='" + purpose + '\'' +
                ", references=" + references +
                ", addnInfo=" + addnInfo +
                ", startDatestamp=" + startDatestamp +
                ", endDatestamp=" + endDatestamp +
                ", history=" + history +
                '}';
    }

}

