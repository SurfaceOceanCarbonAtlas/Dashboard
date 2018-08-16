package gov.noaa.pmel.dashboard.server;

import org.jdom2.Document;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CdiacOmeMetadata extends OmeMetadata implements OmeMetadataInterface {

    private static final SimpleDateFormat TIME_PARSER;

    static {
        TIME_PARSER = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        TIME_PARSER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public CdiacOmeMetadata() {
        super("");
    }

    @Override
    public Document createDocument() {
        return this.createOmeXmlDoc();
    }

    @Override
    public void assignFromDocument(Document doc) throws IllegalArgumentException {
        try {
            this.assignFromOmeXmlDoc(doc);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public OmeMetadataInterface merge(OmeMetadataInterface other) throws IllegalArgumentException {
        if ( !(other instanceof OmeMetadata) )
            throw new IllegalArgumentException("Unknown class of other OME object");
        OmeMetadata merged;
        try {
            merged = OmeMetadata.merge(this, (OmeMetadata) other);
        } catch ( Exception ex ) {
            throw new IllegalArgumentException(ex);
        }
        return (CdiacOmeMetadata) merged;
    }

    @Override
    public String getDatasetId() {
        return this.getExpocode();
    }

    @Override
    public void setDatasetId(String newId) {
        String value = (newId != null) ? newId : "";
        this.setExpocode(value);
    }

    @Override
    public String getDatasetName() {
        return this.getExperimentName();
    }

    @Override
    public void setDatasetName(String datasetName) {
        String value = (datasetName != null) ? datasetName : "";
        try {
            this.replaceValue(OmeMetadata.EXPERIMENT_NAME_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public String getPlatformName() {
        return this.getVesselName();
    }

    @Override
    public void setPlatformName(String platformName) {
        String value = (platformName != null) ? platformName : "";
        try {
            this.replaceValue(OmeMetadata.VESSEL_NAME_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public String getPlatformType() {
        String platformType;
        try {
            platformType = this.getValue(OmeMetadata.PLATFORM_TYPE_STRING);
        } catch ( Exception e ) {
            // Should never happen
            platformType = null;
        }
        return platformType;
    }

    @Override
    public void setPlatformType(String platformType) {
        String value = (platformType != null) ? platformType : "";
        try {
            this.replaceValue(OmeMetadata.PLATFORM_TYPE_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public void setInvestigatorsAndOrganizations(List<String> investigators, List<String> organizations) {
        // TODO:
    }

    @Override
    public String getDatasetRefs() {
        try {
            // Could be null
            return this.getValue(OmeMetadata.DATA_SET_REFS_STRING);
        } catch ( Exception ex ) {
            // Should never happen
            return null;
        }
    }

    @Override
    public void setDatasetRefs(String datasetRefs) {
        try {
            this.replaceValue(OmeMetadata.DATA_SET_REFS_STRING, datasetRefs, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public String getDatasetDOI() {
        // TODO: DOI not stored in CDIAC OME XML
        return null;
    }

    @Override
    public void setDatasetDOI(String datasetDOI) {
        // TODO: DOI not stored in CDIAC OME XML
    }

    @Override
    public Double getWesternLongitude() {
        Double value;
        try {
            value = Double.parseDouble(this.getWestmostLongitude());
            if ( (value >= -540.0) && (value <= 540.0) ) {
                while ( value <= -180.0 ) {
                    value += 360.0;
                }
                while ( value > 180.0 ) {
                    value -= 360.0;
                }
            }
            else {
                value = null;
            }
        } catch ( Exception ex ) {
            return null;
        }
        return value;
    }

    @Override
    public void setWesternLongitude(Double westernLongitude) {
        String value;
        try {
            value = String.format("%#.3f", westernLongitude);
        } catch ( Exception ex ) {
            value = "";
        }
        try {
            this.replaceValue(OmeMetadata.WEST_BOUND_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public Double getEasternLongitude() {
        Double value;
        try {
            value = Double.parseDouble(this.getEastmostLongitude());
            if ( (value >= -540.0) && (value <= 540.0) ) {
                while ( value <= -180.0 ) {
                    value += 360.0;
                }
                while ( value > 180.0 ) {
                    value -= 360.0;
                }
            }
            else {
                value = null;
            }
        } catch ( Exception ex ) {
            value = null;
        }
        return value;
    }

    @Override
    public void setEasternLongitude(Double easternLongitude) {
        String value;
        try {
            value = String.format("%#.3f", easternLongitude);
        } catch ( Exception ex ) {
            value = "";
        }
        try {
            this.replaceValue(OmeMetadata.EAST_BOUND_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public Double getSouthernLatitude() {
        Double value;
        try {
            value = Double.parseDouble(this.getSouthmostLatitude());
            if ( !((value >= -90.0) && (value <= 90.0)) )
                value = null;
        } catch ( Exception ex ) {
            value = null;
        }
        return value;
    }

    @Override
    public void setSouthernLatitude(Double southernLatitude) {
        String value;
        try {
            value = String.format("%#.3f", southernLatitude);
        } catch ( Exception ex ) {
            value = "";
        }
        try {
            this.replaceValue(OmeMetadata.SOUTH_BOUND_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public Double getNorthernLatitude() {
        Double value;
        try {
            value = Double.parseDouble(this.getNorthmostLatitude());
            if ( !((value >= -90.0) && (value <= 90.0)) )
                value = null;
        } catch ( Exception ex ) {
            value = null;
        }
        return value;
    }

    @Override
    public void setNorthernLatitude(Double northernLatitude) {
        String value;
        try {
            value = String.format("%#.3f", northernLatitude);
        } catch ( Exception ex ) {
            value = "";
        }
        try {
            this.replaceValue(OmeMetadata.NORTH_BOUND_STRING, value, -1);
        } catch ( Exception ex ) {
            // Should never happen
        }
    }

    @Override
    public Double getDataStartTime() {
        String dateString = this.getTemporalCoverageStartDate();
        if ( (dateString == null) || dateString.isEmpty() || OmeMetadata.CONFLICT_STRING.equals(dateString) )
            return null;
        try {
            Date startTime = TIME_PARSER.parse(dateString + " 00:00:00");
            return startTime.getTime() / 1000.0;
        } catch ( Exception ex ) {
            return null;
        }
    }

    @Override
    public void setDataStartTime(Double dataStartTime) {
        // TODO:
    }

    @Override
    public Double getDataEndTime() {
        String dateString = this.getTemporalCoverageEndDate();
        if ( (dateString == null) || dateString.isEmpty() || OmeMetadata.CONFLICT_STRING.equals(dateString) )
            return null;
        try {
            Date endTime = TIME_PARSER.parse(dateString + " 23:59:59");
            return endTime.getTime() / 1000.0;
        } catch ( Exception ex ) {
            return null;
        }
    }

    @Override
    public void setDataEndTime(Double dataEndTime) {
        // TODO:
    }

}

