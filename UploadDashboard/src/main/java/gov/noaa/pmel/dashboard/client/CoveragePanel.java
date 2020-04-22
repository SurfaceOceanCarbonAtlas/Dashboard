package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.util.HashSet;
import java.util.TreeSet;

public class CoveragePanel extends Composite {

    private static final String DATE_UNIT_TEXT = "(yyyy-MM-dd)";

    interface CoveragePanelUiBinder extends UiBinder<ScrollPanel,CoveragePanel> {
    }

    private static final CoveragePanelUiBinder uiBinder = GWT.create(CoveragePanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox spacialValue;
    @UiField(provided = true)
    final LabeledTextBox southLatValue;
    @UiField(provided = true)
    final LabeledTextBox northLatValue;
    @UiField(provided = true)
    final LabeledTextBox westLonValue;
    @UiField(provided = true)
    final LabeledTextBox eastLonValue;
    @UiField(provided = true)
    final LabeledTextBox startDateValue;
    @UiField(provided = true)
    final LabeledTextBox endDateValue;
    @UiField(provided = true)
    final LabeledTextBox earlyDateValue;
    @UiField(provided = true)
    final LabeledTextBox lateDateValue;
    @UiField(provided = true)
    final LabeledTextArea regionsValue;

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
        spacialValue = new LabeledTextBox("Spatial reference:", "12em", "12em", null, null);
        //
        southLatValue = new LabeledTextBox("Southern-most latitude:", "12em", "12em",
                coverage.getSouthernLatitude().getUnitString(), "8em");
        northLatValue = new LabeledTextBox("Northern-most latitude:", "12em", "12em",
                coverage.getNorthernLatitude().getUnitString(), "8em");
        //
        westLonValue = new LabeledTextBox("Western-most longitude:", "12em", "12em",
                coverage.getWesternLongitude().getUnitString(), "8em");
        eastLonValue = new LabeledTextBox("Eastern-most longitude:", "12em", "12em",
                coverage.getEasternLongitude().getUnitString(), "8em");
        //
        startDateValue = new LabeledTextBox("Expedition starting date:", "12em", "12em", DATE_UNIT_TEXT, "8em");
        endDateValue = new LabeledTextBox("Expedition ending date:", "12em", "12em", DATE_UNIT_TEXT, "8em");
        //
        earlyDateValue = new LabeledTextBox("Earliest data date:", "12em", "12em", DATE_UNIT_TEXT, "8em");
        lateDateValue = new LabeledTextBox("Latest data date:", "12em", "12em", DATE_UNIT_TEXT, "8em");
        //
        regionsValue = new LabeledTextArea("Geographic names", "10em", "60em");

        initWidget(uiBinder.createAndBindUi(this));

        this.coverage = coverage;
        this.today = today;

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
            UploadDashboard.showFailureMessage("Invalid southern-most latitude", ex);
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
            UploadDashboard.showFailureMessage("Invalid northern-most latitude", ex);
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
            UploadDashboard.showFailureMessage("Invalid western-most longitude", ex);
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
            UploadDashboard.showFailureMessage("Invalid eastern-most longitude", ex);
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
            UploadDashboard.showFailureMessage("Invalid expedition start date", ex);
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
            UploadDashboard.showFailureMessage("Invalid expedition end date", ex);
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
            UploadDashboard.showFailureMessage("Invalid earliest data date", ex);
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
            UploadDashboard.showFailureMessage("Invalid latest data date", ex);
            // lateDateValue.setValue(coverage.getLatestDataDate().fullOrPartialString(), false);
            addnInvalid = "latestDataDate";
        }
        markInvalids(addnInvalid);
    }

    @UiHandler("regionsValue")
    void regionsValueOnValueChanged(ValueChangeEvent<String> event) {
        String[] pieces = regionsValue.getText().split("\n");
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
            spacialValue.markInvalid();
        else
            spacialValue.markValid();

        if ( invalids.contains("southernLatitude") )
            southLatValue.markInvalid();
        else
            southLatValue.markValid();

        if ( invalids.contains("northernLatitude") )
            northLatValue.markInvalid();
        else
            northLatValue.markValid();

        if ( invalids.contains("westernLongitude") )
            westLonValue.markInvalid();
        else
            westLonValue.markValid();

        if ( invalids.contains("easternLongitude") )
            eastLonValue.markInvalid();
        else
            eastLonValue.markValid();

        if ( invalids.contains("startDatestamp") )
            startDateValue.markInvalid();
        else
            startDateValue.markValid();

        if ( invalids.contains("endDatestamp") )
            endDateValue.markInvalid();
        else
            endDateValue.markValid();

        if ( invalids.contains("earliestDataDate") )
            earlyDateValue.markInvalid();
        else
            earlyDateValue.markValid();

        if ( invalids.contains("latestDataDate") )
            lateDateValue.markInvalid();
        else
            lateDateValue.markValid();

        if ( invalids.contains("geographicNames") )
            regionsValue.markInvalid();
        else
            regionsValue.markValid();
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
        regionsValue.setText(regions);

        return coverage;
    }

}
