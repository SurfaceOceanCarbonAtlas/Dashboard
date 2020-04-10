package gov.noaa.pmel.dashboard.metadata;

import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.socatmetadata.shared.Coverage;
import gov.noaa.pmel.socatmetadata.shared.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.SocatMetadata;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;
import gov.noaa.pmel.socatmetadata.translate.DocumentHandler;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of OmeMetadataInterface that uses an SDIMetadata object
 * as its foundation.  Intended to be the working class for OME metadata,
 * with other metadata formats being converted to or produced from
 * SDIMetadata objects.
 */
public class SdiOmeMetadata implements OmeMetadataInterface {

    private SocatMetadata mdata;

    public SdiOmeMetadata() {
        mdata = new SocatMetadata();
    }

    @Override
    public void read(String datasetId, File mdataFile) throws IllegalArgumentException, FileNotFoundException {
        String stdId = DashboardServerUtils.checkDatasetID(datasetId);
        XMLDecoder xdec = new XMLDecoder(new FileInputStream(mdataFile));
        try {
            mdata = (SocatMetadata) xdec.readObject();
        } catch ( Exception ex ) {
            throw new IllegalArgumentException("Problems reading the SDIMetadata object from the metadata file " +
                    mdataFile.getName() + " for dataset " + datasetId + ": " + ex.getMessage());
        }
        String givenId = mdata.getMiscInfo().getDatasetId();
        try {
            String givenStdId = DashboardServerUtils.checkDatasetID(givenId);
            if ( !stdId.equals(givenStdId) )
                throw new IllegalArgumentException();
        } catch ( IllegalArgumentException ex ) {
            throw new IllegalArgumentException("Inappropriate dataset ID (" + givenId +
                    ") in the metadata file " + mdataFile.getName() + " for dataset " + datasetId);
        }
    }

    @Override
    public void write(File mdataFile) throws IOException {
        XMLEncoder xenc = new XMLEncoder(new FileOutputStream(mdataFile));
        xenc.writeObject(mdata);
        xenc.close();
    }

    @Override
    public boolean isAcceptable() {
        String[] pieces = OmeUtils.SIMPLE_DATE_FORMAT_UTC.format(new Date()).split(" ");
        Datestamp today = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);
        HashSet<String> invalidFieldNames = mdata.invalidFieldNames(today);
        return invalidFieldNames.isEmpty();
    }

    @Override
    public String getDatasetId() {
        return mdata.getMiscInfo().getDatasetId();
    }

    @Override
    public void setDatasetId(String newId) {
        MiscInfo miscInfo = mdata.getMiscInfo();
        miscInfo.setDatasetId(newId);
        mdata.setMiscInfo(miscInfo);
    }

    @Override
    public String getDatasetName() {
        return mdata.getMiscInfo().getDatasetName();
    }

    @Override
    public void setDatasetName(String datasetName) {
        MiscInfo miscInfo = mdata.getMiscInfo();
        miscInfo.setDatasetName(datasetName);
        mdata.setMiscInfo(miscInfo);
    }

    @Override
    public String getPlatformName() {
        return mdata.getPlatform().getPlatformName();
    }

    @Override
    public void setPlatformName(String platformName) {
        Platform platform = mdata.getPlatform();
        platform.setPlatformName(platformName);
        mdata.setPlatform(platform);
    }

    @Override
    public String getPlatformType() {
        return mdata.getPlatform().getPlatformType().toString();
    }

    @Override
    public void setPlatformType(String platformType) {
        Platform platform = mdata.getPlatform();
        platform.setPlatformType(PlatformType.parse(platformType));
        mdata.setPlatform(platform);
    }

    @Override
    public ArrayList<String> getInvestigators() {
        ArrayList<Investigator> investigators = mdata.getInvestigators();
        ArrayList<String> names = new ArrayList<String>(investigators.size());
        for (Investigator inv : investigators) {
            // Skip any Investigator instances that have invalid fields
            if ( inv.invalidFieldNames().isEmpty() ) {
                String lastFirstMiddle = inv.getLastName() + ", " + inv.getFirstName() + " " + inv.getMiddle();
                names.add(lastFirstMiddle.trim());
            }
        }
        return names;
    }

    @Override
    public ArrayList<String> getOrganizations() {
        ArrayList<Investigator> investigators = mdata.getInvestigators();
        ArrayList<String> orgs = new ArrayList<String>(investigators.size());
        for (Investigator inv : investigators) {
            // Skip any Investigator instances that have invalid fields
            if ( inv.invalidFieldNames().isEmpty() ) {
                orgs.add(inv.getOrganization());
            }
        }
        return orgs;
    }

    @Override
    public void setInvestigatorsAndOrganizations(List<String> investigators, List<String> organizations) {
        if ( investigators.size() != organizations.size() )
            throw new RuntimeException("Unexpected error: " +
                    "investigators list and organizations list are not the same size");
        ArrayList<Investigator> oldList = mdata.getInvestigators();
        ArrayList<Investigator> invList = new ArrayList<Investigator>(investigators.size());
        for (int k = 0; k < investigators.size(); k++) {
            Person person = DocumentHandler.getPersonNames(investigators.get(k));
            person.setOrganization(organizations.get(k));
            Investigator inv = new Investigator(person);
            // If an existing investigator matches this limited information,
            // use the existing investigator information which may be more complete
            for (Investigator oldInv : oldList) {
                if ( person.getLastName().equals(oldInv.getLastName()) &&
                        person.getFirstName().equals(oldInv.getFirstName()) &&
                        person.getMiddle().equals(oldInv.getMiddle()) &&
                        person.getOrganization().equals(oldInv.getOrganization()) ) {
                    inv = oldInv;
                    break;
                }
            }
            invList.add(inv);
        }
        mdata.setInvestigators(invList);
    }

    @Override
    public String getDatasetDOI() {
        return mdata.getMiscInfo().getDatasetDoi();
    }

    @Override
    public void setDatasetDOI(String datasetDOI) {
        MiscInfo miscInfo = mdata.getMiscInfo();
        miscInfo.setDatasetDoi(datasetDOI);
        mdata.setMiscInfo(miscInfo);
    }

    @Override
    public String getDatasetLink() {
        String link = mdata.getMiscInfo().getWebsite();
        if ( link.isEmpty() )
            link = mdata.getMiscInfo().getDownloadUrl();
        return link;
    }

    @Override
    public void setDatasetLink(String datasetLink) {
        MiscInfo miscInfo = mdata.getMiscInfo();
        miscInfo.setWebsite(datasetLink);
        mdata.setMiscInfo(miscInfo);
    }

    @Override
    public Double getWesternLongitude() {
        return mdata.getCoverage().getWesternLongitude().getNumericValue();
    }

    @Override
    public void setWesternLongitude(Double westernLongitude) {
        Coverage coverage = mdata.getCoverage();
        if ( (westernLongitude == null) || Double.isNaN(westernLongitude) || Double.isInfinite(westernLongitude) )
            coverage.setWesternLongitude(null);
        else
            coverage.setWesternLongitude(new NumericString(
                    String.format("%#.6f", westernLongitude), Coverage.LONGITUDE_UNITS));
        mdata.setCoverage(coverage);
    }

    @Override
    public Double getEasternLongitude() {
        return mdata.getCoverage().getEasternLongitude().getNumericValue();
    }

    @Override
    public void setEasternLongitude(Double easternLongitude) {
        Coverage coverage = mdata.getCoverage();
        if ( (easternLongitude == null) || Double.isNaN(easternLongitude) || Double.isInfinite(easternLongitude) )
            coverage.setEasternLongitude(null);
        else
            coverage.setEasternLongitude(new NumericString(
                    String.format("%#.6f", easternLongitude), Coverage.LONGITUDE_UNITS));
        mdata.setCoverage(coverage);
    }

    @Override
    public Double getSouthernLatitude() {
        return mdata.getCoverage().getSouthernLatitude().getNumericValue();
    }

    @Override
    public void setSouthernLatitude(Double southernLatitude) {
        Coverage coverage = mdata.getCoverage();
        if ( (southernLatitude == null) || Double.isNaN(southernLatitude) || Double.isInfinite(southernLatitude) )
            coverage.setSouthernLatitude(null);
        else
            coverage.setSouthernLatitude(new NumericString(
                    String.format("%#.6f", southernLatitude), Coverage.LATITUDE_UNITS));
        mdata.setCoverage(coverage);
    }

    @Override
    public Double getNorthernLatitude() {
        return mdata.getCoverage().getNorthernLatitude().getNumericValue();
    }

    @Override
    public void setNorthernLatitude(Double northernLatitude) {
        Coverage coverage = mdata.getCoverage();
        if ( (northernLatitude == null) || Double.isNaN(northernLatitude) || Double.isInfinite(northernLatitude) )
            coverage.setNorthernLatitude(null);
        else
            coverage.setNorthernLatitude(new NumericString(
                    String.format("%#.6f", northernLatitude), Coverage.LATITUDE_UNITS));
        mdata.setCoverage(coverage);
    }

    @Override
    public Double getDataStartTime() {
        String[] pieces = OmeUtils.SIMPLE_DATE_FORMAT_UTC.format(new Date()).split(" ");
        Datestamp today = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);

        Datestamp startDate = mdata.getCoverage().getEarliestDataDate();
        if ( !startDate.isValid(today) )
            return Double.NaN;
        try {
            String startString = startDate.dateString().replace('-', ' ') +
                    ' ' + startDate.timeString().replace(':', ' ');
            Date startTime = OmeUtils.SIMPLE_DATE_FORMAT_UTC.parse(startString);
            return startTime.getTime() / 1000.0;
        } catch ( ParseException ex ) {
            throw new RuntimeException("Unexpected error parsing Datestamp.stampString: " + ex.getMessage());
        }
    }

    @Override
    public void setDataStartTime(Double dataStartTime) {
        Coverage coverage = mdata.getCoverage();
        if ( (dataStartTime == null) || Double.isNaN(dataStartTime) || Double.isInfinite(dataStartTime) ) {
            coverage.setEarliestDataDate(null);
        }
        else {
            String[] pieces = OmeUtils.SIMPLE_DATE_FORMAT_UTC.format(new Date(Math.round(dataStartTime * 1000.0)))
                                                             .split(" ");
            coverage.setEarliestDataDate(
                    new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]));
        }
        mdata.setCoverage(coverage);
    }

    @Override
    public Double getDataEndTime() {
        String[] pieces = OmeUtils.SIMPLE_DATE_FORMAT_UTC.format(new Date()).split(" ");
        Datestamp today = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);

        Datestamp endDate = mdata.getCoverage().getLatestDataDate();
        if ( !endDate.isValid(today) )
            return Double.NaN;
        try {
            String endString = endDate.dateString().replace('-', ' ') +
                    ' ' + endDate.timeString().replace(':', ' ');
            Date endTime = OmeUtils.SIMPLE_DATE_FORMAT_UTC.parse(endString);
            return endTime.getTime() / 1000.0;
        } catch ( ParseException ex ) {
            throw new RuntimeException("Unexpected error parsing Datestamp.stampString: " + ex.getMessage());
        }
    }

    @Override
    public void setDataEndTime(Double dataEndTime) {
        Coverage coverage = mdata.getCoverage();
        if ( (dataEndTime == null) || Double.isNaN(dataEndTime) || Double.isInfinite(dataEndTime) ) {
            coverage.setLatestDataDate(null);
        }
        else {
            String[] pieces = OmeUtils.SIMPLE_DATE_FORMAT_UTC.format(new Date(Math.round(dataEndTime * 1000.0)))
                                                             .split("-");
            coverage.setLatestDataDate(
                    new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]));

        }
        mdata.setCoverage(coverage);
    }

    @Override
    public DatasetQCStatus suggestedDatasetStatus(DashboardOmeMetadata metadata, DashboardDataset dataset)
            throws IllegalArgumentException {
        return OmeUtils.suggestDatasetQCFlag(mdata, dataset);
    }

}
