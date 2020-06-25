package gov.noaa.pmel.socatmetadata.shared.core;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Miscellaneous information about a dataset.
 */
public class MiscInfo implements Duplicable, Serializable, IsSerializable {

    private static final long serialVersionUID = 4539293840905120652L;

    private String datasetId;
    private String datasetName;
    private String sectionName;
    private String fundingAgency;
    private String fundingTitle;
    private String fundingId;
    private String researchProject;
    private String datasetDoi;
    private String accessId;
    private String website;
    private String downloadUrl;
    private String citation;
    private String synopsis;
    private String purpose;
    private MultiString references;
    private MultiString portsOfCall;
    private MultiString addnInfo;
    private ArrayList<Datestamp> history;

    /**
     * Create with empty or invalid values for all fields.
     */
    public MiscInfo() {
        datasetId = "";
        datasetName = "";
        sectionName = "";
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
        references = new MultiString();
        portsOfCall = new MultiString();
        addnInfo = new MultiString();
        history = new ArrayList<Datestamp>();
    }

    /**
     * @return list of field names that are currently invalid
     */
    public HashSet<String> invalidFieldNames() {
        HashSet<String> invalid = new HashSet<String>();
        if ( datasetId.isEmpty() )
            invalid.add("datasetId");

        return invalid;
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
     * @return the dataset section/leg name; never null but may be empty
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @param sectionName
     *         assign as dataset section/leg name; if null, an empty string is assigned
     */
    public void setSectionName(String sectionName) {
        this.sectionName = (sectionName != null) ? sectionName.trim() : "";
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
     * @return the references list used by this dataset; never null but may be empty.
     */
    public MultiString getReferences() {
        return new MultiString(references);
    }

    /**
     * @param references
     *         assign as the references list used by this dataset; if null, an empty list is assigned
     */
    public void setReferences(MultiString references) {
        this.references = new MultiString(references);
    }

    /**
     * @return the port-of-call list for this dataset; never null but may be empty.
     */
    public MultiString getPortsOfCall() {
        return new MultiString(portsOfCall);
    }

    /**
     * @param portsOfCall
     *         assign as the port-of-call list for this dataset; if null, an empty list is assigned
     */
    public void setPortsOfCall(MultiString portsOfCall) {
        this.portsOfCall = new MultiString(portsOfCall);
    }


    /**
     * @return the list of addition information strings for this dataset; never null but may be empty.
     */
    public MultiString getAddnInfo() {
        return new MultiString(addnInfo);
    }

    /**
     * @param addnInfo
     *         assign as the list of additional information strings for this dataset;
     *         if null, an empty list is assigned.
     */
    public void setAddnInfo(MultiString addnInfo) {
        this.addnInfo = new MultiString(addnInfo);
    }

    /**
     * @return the submission date history list for this dataset; never null but maybe be empty.
     *         Any dates present are guaranteed to be valid.
     */
    public ArrayList<Datestamp> getHistory() {
        ArrayList<Datestamp> dup = new ArrayList<Datestamp>(history.size());
        for (Datestamp datestamp : history) {
            dup.add(new Datestamp(datestamp));
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
    public void setHistory(ArrayList<Datestamp> history) throws IllegalArgumentException {
        this.history.clear();
        if ( history != null ) {
            for (Datestamp datestamp : history) {
                if ( datestamp == null )
                    throw new IllegalArgumentException("null datestamp given");
                if ( !datestamp.isValid(null) )
                    throw new IllegalArgumentException("invalid datestamp given");
                this.history.add(new Datestamp(datestamp));
            }
        }
    }

    @Override
    public Object duplicate(Object dup) {
        MiscInfo info;
        if ( dup == null )
            info = new MiscInfo();
        else
            info = (MiscInfo) dup;
        info.datasetId = datasetId;
        info.datasetName = datasetName;
        info.sectionName = sectionName;
        info.fundingAgency = fundingAgency;
        info.fundingTitle = fundingTitle;
        info.fundingId = fundingId;
        info.researchProject = researchProject;
        info.datasetDoi = datasetDoi;
        info.accessId = accessId;
        info.website = website;
        info.downloadUrl = downloadUrl;
        info.citation = citation;
        info.synopsis = synopsis;
        info.purpose = purpose;
        info.references = new MultiString(references);
        info.portsOfCall = new MultiString(portsOfCall);
        info.addnInfo = new MultiString(addnInfo);
        info.history = new ArrayList<Datestamp>(history.size());
        for (Datestamp datestamp : history) {
            info.history.add(new Datestamp(datestamp));
        }
        return info;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = datasetId.hashCode();
        result = result * prime + datasetName.hashCode();
        result = result * prime + sectionName.hashCode();
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
        result = result * prime + portsOfCall.hashCode();
        result = result * prime + addnInfo.hashCode();
        result = result * prime + history.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( null == obj )
            return false;
        if ( !(obj instanceof MiscInfo) )
            return false;

        MiscInfo other = (MiscInfo) obj;

        if ( !datasetId.equals(other.datasetId) )
            return false;
        if ( !datasetName.equals(other.datasetName) )
            return false;
        if ( !sectionName.equals(other.sectionName) )
            return false;
        if ( !fundingAgency.equals(other.fundingAgency) )
            return false;
        if ( !fundingTitle.equals(other.fundingTitle) )
            return false;
        if ( !fundingId.equals(other.fundingId) )
            return false;
        if ( !researchProject.equals(other.researchProject) )
            return false;
        if ( !datasetDoi.equals(other.datasetDoi) )
            return false;
        if ( !accessId.equals(other.accessId) )
            return false;
        if ( !website.equals(other.website) )
            return false;
        if ( !downloadUrl.equals(other.downloadUrl) )
            return false;
        if ( !citation.equals(other.citation) )
            return false;
        if ( !synopsis.equals(other.synopsis) )
            return false;
        if ( !purpose.equals(other.purpose) )
            return false;
        if ( !references.equals(other.references) )
            return false;
        if ( !portsOfCall.equals(other.portsOfCall) )
            return false;
        if ( !addnInfo.equals(other.addnInfo) )
            return false;
        if ( !history.equals(other.history) )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MiscInfo{" +
                " datasetId='" + datasetId + "'," +
                " datasetName='" + datasetName + "'," +
                " sectionName='" + sectionName + "'," +
                " fundingAgency='" + fundingAgency + "'," +
                " fundingTitle='" + fundingTitle + "'," +
                " fundingId='" + fundingId + "'," +
                " researchProject='" + researchProject + "'," +
                " datasetDoi='" + datasetDoi + "'," +
                " accessId='" + accessId + "'," +
                " website='" + website + "'," +
                " downloadUrl='" + downloadUrl + "'," +
                " citation='" + citation + "'," +
                " synopsis='" + synopsis + "'," +
                " purpose='" + purpose + "'," +
                " references=" + references + "," +
                " portsOfCall=" + portsOfCall + "," +
                " addnInfo=" + addnInfo + "," +
                " history=" + history +
                " }";
    }

}
