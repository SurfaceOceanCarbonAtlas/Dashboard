package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.util.HashSet;
import java.util.TreeSet;

public class CoveragePanel extends Composite {

    interface CoveragePanelUiBinder extends UiBinder<FlowPanel,CoveragePanel> {
    }

    private static final CoveragePanelUiBinder uiBinder = GWT.create(CoveragePanelUiBinder.class);

    @UiField
    Grid coverageGrid;
    @UiField
    HTML spacialHtml;
    @UiField
    TextBox spacialValue;
    @UiField
    HTML southLatHtml;
    @UiField
    TextBox southLatValue;
    @UiField
    Label southLatUnit;
    @UiField
    HTML northLatHtml;
    @UiField
    TextBox northLatValue;
    @UiField
    Label northLatUnit;
    @UiField
    HTML westLonHtml;
    @UiField
    TextBox westLonValue;
    @UiField
    Label westLonUnit;
    @UiField
    HTML eastLonHtml;
    @UiField
    TextBox eastLonValue;
    @UiField
    Label eastLonUnit;
    @UiField
    HTML startDateHtml;
    @UiField
    TextBox startDateValue;
    @UiField
    Label startDateUnit;
    @UiField
    HTML endDateHtml;
    @UiField
    TextBox endDateValue;
    @UiField
    Label endDateUnit;
    @UiField
    HTML earlyDateHtml;
    @UiField
    TextBox earlyDateValue;
    @UiField
    Label earlyDateUnit;
    @UiField
    HTML lateDateHtml;
    @UiField
    TextBox lateDateValue;
    @UiField
    Label lateDateUnit;
    @UiField
    CaptionPanel regionsPanel;
    @UiField
    TextArea regionsBox;

    private static final String SPATIAL_REFERENCE_HTML = "Spatial reference:";
    private static final String SOUTHERN_LATITUDE_HTML = "Southern-most latitude:";
    private static final String NORTHERN_LATITUDE_HTML = "Northern-most latitude:";
    private static final String WESTERN_LONGITUDE_HTML = "Western-most longitude:";
    private static final String EASTERN_LONGITUDE_HTML = "Eastern-most longitude:";
    private static final String START_DATE_HTML = "Expedition starting date:";
    private static final String END_DATE_HTML = "Expedition ending date:";
    private static final String EARLY_DATE_HTML = "Earliest data date:";
    private static final String LATE_DATE_HTML = "Latest data date:";
    private static final String REGIONS_HTML = "Geographic names";

    private static final String DATE_UNIT_TEXT = "(yyyy-MM-dd)";

    private static final String INVALID_LATITUDE_MSG = "Invalid latitude";
    private static final String INVALID_LONGITUDE_MSG = "Invalid longitude";
    private static final String INVALID_DATESTAMP_MSG = "Invalid date";

    private static final String INVALID_HTML_PREFIX = "<span style='color:red; font-weight:bold; font-style:oblique'>";
    private static final String INVALID_HTML_SUFFIX = "</span>";

    private final Coverage coverage;
    private final Datestamp today;
    private final Datestamp origEarlyDate;
    private final Datestamp origLateDate;

    /**
     * Creates a FlowPanel associated with the given Coverage.
     *
     * @param coverage
     *         associate this panel with this Coverage; cannot be null
     */
    public CoveragePanel(Coverage coverage, Datestamp today) {
        initWidget(uiBinder.createAndBindUi(this));

        this.coverage = coverage;
        this.today = today;

        southLatUnit.setText(coverage.getSouthernLatitude().getUnitString());
        northLatUnit.setText(coverage.getNorthernLatitude().getUnitString());
        westLonUnit.setText(coverage.getWesternLongitude().getUnitString());
        eastLonUnit.setText(coverage.getEasternLongitude().getUnitString());
        startDateUnit.setText(DATE_UNIT_TEXT);
        endDateUnit.setText(DATE_UNIT_TEXT);
        earlyDateUnit.setText(DATE_UNIT_TEXT);
        lateDateUnit.setText(DATE_UNIT_TEXT);

        origEarlyDate = coverage.getEarliestDataDate();
        origLateDate = coverage.getLatestDataDate();

        // The following will assign the values in the text fields
        getUpdatedCoverage();
        // The following will assign the HTML to the labels before the text fields
        markInvalids(null);
    }

    @UiHandler("spacialValue")
    void spacialBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setSpatialReference(spacialValue.getText());
        markInvalids(null);
    }

    @UiHandler("southLatValue")
    void southLatValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            NumericString val = coverage.getSouthernLatitude();
            val.setValueString(southLatValue.getText());
            coverage.setSouthernLatitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_LATITUDE_MSG, ex);
            // southLatValue.setValue(coverage.getSouthernLatitude().getValueString(), false);
            addnInvalid = "southernLatitude";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("northLatValue")
    void northLatValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            NumericString val = coverage.getNorthernLatitude();
            val.setValueString(northLatValue.getText());
            coverage.setNorthernLatitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_LATITUDE_MSG, ex);
            // northLatValue.setValue(coverage.getNorthernLatitude().getValueString(), false);
            addnInvalid = "northernLatitude";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("westLonValue")
    void westLonValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            NumericString val = coverage.getWesternLongitude();
            val.setValueString(westLonValue.getText());
            coverage.setWesternLongitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_LONGITUDE_MSG, ex);
            // westLonValue.setValue(coverage.getWesternLongitude().getValueString(), false);
            addnInvalid = "westernLongitude";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("eastLonValue")
    void eastLonValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            NumericString val = coverage.getEasternLongitude();
            val.setValueString(eastLonValue.getText());
            coverage.setEasternLongitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_LONGITUDE_MSG, ex);
            // eastLonValue.setValue(coverage.getEasternLongitude().getValueString(), false);
            addnInvalid = "easternLongitude";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("startDateValue")
    void startDateValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            Datestamp newStamp = parseDatestampString(startDateValue.getText());
            if ( !newStamp.isValid(today) )
                throw new IllegalArgumentException("improper date/time format or not an actual date/time");
            if ( origEarlyDate.isValid(today) && newStamp.after(origEarlyDate) )
                throw new IllegalArgumentException("start date cannot be later than the earliest data date");
            coverage.setStartDatestamp(newStamp);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_DATESTAMP_MSG, ex);
            // startDateValue.setValue(coverage.getStartDatestamp().fullOrPartialString(), false);
            addnInvalid = "startDatestamp";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("endDateValue")
    void endDateValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            Datestamp newStamp = parseDatestampString(endDateValue.getText());
            if ( !newStamp.isValid(today) )
                throw new IllegalArgumentException("improper date/time format or not an actual date/time");
            if ( origLateDate.isValid(today) && newStamp.before(origLateDate) )
                throw new IllegalArgumentException("end date cannot be earlier than the latest data date");
            coverage.setEndDatestamp(newStamp);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_DATESTAMP_MSG, ex);
            // endDateValue.setValue(coverage.getEndDatestamp().fullOrPartialString(), false);
            addnInvalid = "endDatestamp";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("earlyDateValue")
    void earlyDateValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            Datestamp newStamp = parseDatestampString(earlyDateValue.getText());
            if ( !newStamp.isValid(today) )
                throw new IllegalArgumentException("improper date/time format or not an actual date/time");
            if ( origEarlyDate.isValid(today) && (newStamp.before(origEarlyDate) || newStamp.after(origEarlyDate)) )
                throw new IllegalArgumentException("date and whatever time given must match " +
                        origEarlyDate.fullOrPartialString());
            coverage.setEarliestDataDate(newStamp);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_DATESTAMP_MSG, ex);
            // earlyDateValue.setValue(coverage.getEarliestDataDate().fullOrPartialString(), false);
            addnInvalid = "earliestDataDate";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("lateDateValue")
    void lateDateValueOnValueChange(ValueChangeEvent<String> event) {
        String addnInvalid = null;
        try {
            Datestamp newStamp = parseDatestampString(lateDateValue.getText());
            if ( !newStamp.isValid(today) )
                throw new IllegalArgumentException("improper date/time format or not an actual date/time");
            if ( origLateDate.isValid(today) && (newStamp.before(origLateDate) || newStamp.after(origLateDate)) )
                throw new IllegalArgumentException("date and whatever time given must match " +
                        origLateDate.fullOrPartialString());
            coverage.setLatestDataDate(newStamp);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage(INVALID_DATESTAMP_MSG, ex);
            // lateDateValue.setValue(coverage.getLatestDataDate().fullOrPartialString(), false);
            addnInvalid = "latestDataDate";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("regionsBox")
    void regionsBoxOnValueChanged(ValueChangeEvent<String> event) {
        String[] pieces = regionsBox.getText().split("\n");
        TreeSet<String> regions = new TreeSet<String>();
        for (String str : pieces) {
            String reg = str.trim();
            if ( !reg.isEmpty() )
                regions.add(reg);
        }
        coverage.setGeographicNames(regions);
        markInvalids(null);
    }

    /**
     * Returns a Datetime object representing the given date/time string in the format yyyy-MM-dd HH:mm:ss, or
     * as much of that format as can be interpreted.  The separators in this format be any combination of slash,
     * hyphen, space, or colon (but only one separator between each piece) for both the date and the time.
     *
     * @param text
     *         Date/time string to parse
     *
     * @return Datestamp representing the date/time given in the string; never null but may not be valid
     */
    private Datestamp parseDatestampString(String text) {
        Datestamp stamp;
        String[] pieces = text.split("[ /:-]");
        switch ( pieces.length ) {
            case 1:
                stamp = new Datestamp(pieces[0], null, null, null, null, null);
                break;
            case 2:
                stamp = new Datestamp(pieces[0], pieces[1], null, null, null, null);
                break;
            case 3:
                stamp = new Datestamp(pieces[0], pieces[1], pieces[2], null, null, null);
                break;
            case 4:
                stamp = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], null, null);
                break;
            case 5:
                stamp = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], null);
                break;
            default:
                stamp = new Datestamp(pieces[0], pieces[1], pieces[2], pieces[3], pieces[4], pieces[5]);
                break;
        }
        return stamp;
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     *
     * @param addnField
     *         if not null or blank, always mark this field as invalid
     */
    private void markInvalids(String addnField) {
        HashSet<String> invalids = coverage.invalidFieldNames(today);
        if ( (addnField != null) && !addnField.trim().isEmpty() )
            invalids.add(addnField.trim());
        if ( invalids.contains("spatialReference") )
            spacialHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + SPATIAL_REFERENCE_HTML + INVALID_HTML_SUFFIX));
        else
            spacialHtml.setHTML(SafeHtmlUtils.fromSafeConstant(SPATIAL_REFERENCE_HTML));


        if ( invalids.contains("southernLatitude") )
            southLatHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + SOUTHERN_LATITUDE_HTML + INVALID_HTML_SUFFIX));
        else
            southLatHtml.setHTML(SafeHtmlUtils.fromSafeConstant(SOUTHERN_LATITUDE_HTML));

        if ( invalids.contains("northernLatitude") )
            northLatHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + NORTHERN_LATITUDE_HTML + INVALID_HTML_SUFFIX));
        else
            northLatHtml.setHTML(SafeHtmlUtils.fromSafeConstant(NORTHERN_LATITUDE_HTML));

        if ( invalids.contains("westernLongitude") )
            westLonHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + WESTERN_LONGITUDE_HTML + INVALID_HTML_SUFFIX));
        else
            westLonHtml.setHTML(SafeHtmlUtils.fromSafeConstant(WESTERN_LONGITUDE_HTML));

        if ( invalids.contains("easternLongitude") )
            eastLonHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + EASTERN_LONGITUDE_HTML + INVALID_HTML_SUFFIX));
        else
            eastLonHtml.setHTML(SafeHtmlUtils.fromSafeConstant(EASTERN_LONGITUDE_HTML));

        if ( invalids.contains("startDatestamp") )
            startDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + START_DATE_HTML + INVALID_HTML_SUFFIX));
        else
            startDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(START_DATE_HTML));

        if ( invalids.contains("endDatestamp") )
            endDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + END_DATE_HTML + INVALID_HTML_SUFFIX));
        else
            endDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(END_DATE_HTML));

        if ( invalids.contains("earliestDataDate") )
            earlyDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + EARLY_DATE_HTML + INVALID_HTML_SUFFIX));
        else
            earlyDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(EARLY_DATE_HTML));

        if ( invalids.contains("latestDataDate") )
            lateDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + LATE_DATE_HTML + INVALID_HTML_SUFFIX));
        else
            lateDateHtml.setHTML(SafeHtmlUtils.fromSafeConstant(LATE_DATE_HTML));

        if ( invalids.contains("geographicNames") )
            regionsPanel.setCaptionHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + REGIONS_HTML + INVALID_HTML_SUFFIX));
        else
            regionsPanel.setCaptionHTML(SafeHtmlUtils.fromSafeConstant(REGIONS_HTML));
    }

    /**
     * @return the update Coverage object; never null
     */
    public Coverage getUpdatedCoverage() {
        // Because erroneous input can leave mismatches,
        // first update the displayed content in case this is from a save-and-continue
        spacialValue.setText(coverage.getSpatialReference());
        southLatValue.setText(coverage.getSouthernLatitude().getValueString());
        northLatValue.setText(coverage.getNorthernLatitude().getValueString());
        westLonValue.setText(coverage.getWesternLongitude().getValueString());
        eastLonValue.setText(coverage.getEasternLongitude().getValueString());
        startDateValue.setText(coverage.getStartDatestamp().fullOrPartialString());
        endDateValue.setText(coverage.getEndDatestamp().fullOrPartialString());
        earlyDateValue.setText(coverage.getEarliestDataDate().fullOrPartialString());
        lateDateValue.setText(coverage.getLatestDataDate().fullOrPartialString());
        String regions = "";
        for (String name : coverage.getGeographicNames()) {
            regions += name + "\n";
        }
        regionsBox.setText(regions);

        return coverage;
    }

}
