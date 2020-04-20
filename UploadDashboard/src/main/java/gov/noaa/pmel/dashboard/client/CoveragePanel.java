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
import com.google.gwt.user.client.ui.TextArea;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

import java.util.HashSet;
import java.util.TreeSet;

public class CoveragePanel extends Composite {

    interface CoveragePanelUiBinder extends UiBinder<FlowPanel,CoveragePanel> {
    }

    private static final CoveragePanelUiBinder uiBinder = GWT.create(CoveragePanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox spacialValueBox;
    @UiField(provided = true)
    final LabeledTextBox southLatValueBox;
    @UiField(provided = true)
    final LabeledTextBox northLatValueBox;
    @UiField(provided = true)
    final LabeledTextBox westLonValueBox;
    @UiField(provided = true)
    final LabeledTextBox eastLonValueBox;
    @UiField(provided = true)
    final LabeledTextBox startDateBox;
    @UiField(provided = true)
    final LabeledTextBox endDateBox;
    @UiField(provided = true)
    final LabeledTextBox earlyDateBox;
    @UiField(provided = true)
    final LabeledTextBox lateDateBox;
    @UiField
    CaptionPanel regionsPanel;
    @UiField
    TextArea regionsBox;

    private static final String REGIONS_PANEL_CAPTION = " Geographic names ";
    private final Coverage coverage;
    private final Datestamp today;

    /**
     * Creates a FlowPanel associated with the given Coverage.
     *
     * @param coverage
     *         associate this panel with this Coverage; cannot be null
     */
    public CoveragePanel(Coverage coverage, Datestamp today) {
        spacialValueBox = new LabeledTextBox("Spatial reference:", "15em", 15, "");
        southLatValueBox = new LabeledTextBox("Southern-most latitude:", "15em", 15,
                coverage.getSouthernLatitude().getUnitString());
        northLatValueBox = new LabeledTextBox("Northern-most latitude:", "15em", 15,
                coverage.getNorthernLatitude().getUnitString());
        westLonValueBox = new LabeledTextBox("Western-most longitude:", "15em", 15,
                coverage.getWesternLongitude().getUnitString());
        eastLonValueBox = new LabeledTextBox("Eastern-most longitude:", "15em", 15,
                coverage.getEasternLongitude().getUnitString());
        startDateBox = new LabeledTextBox("Starting date:", "15em", 15, "");
        endDateBox = new LabeledTextBox("Ending date:", "15em", 15, "");
        earlyDateBox = new LabeledTextBox("Earliest data date/time:", "15em", 15, "");
        lateDateBox = new LabeledTextBox("Latest data date/time:", "15em", 15, "");
        initWidget(uiBinder.createAndBindUi(this));

        this.coverage = coverage;
        this.today = today;

        spacialValueBox.setText(coverage.getSpatialReference());
        southLatValueBox.setText(coverage.getSouthernLatitude().getValueString());
        northLatValueBox.setText(coverage.getNorthernLatitude().getValueString());
        westLonValueBox.setText(coverage.getWesternLongitude().getValueString());
        eastLonValueBox.setText(coverage.getEasternLongitude().getValueString());
        startDateBox.setText(coverage.getStartDatestamp().fullOrPartialString());
        endDateBox.setText(coverage.getEndDatestamp().fullOrPartialString());
        earlyDateBox.setText(coverage.getEarliestDataDate().fullOrPartialString());
        lateDateBox.setText(coverage.getLatestDataDate().fullOrPartialString());

        regionsPanel.setCaptionText(REGIONS_PANEL_CAPTION);
        String regions = "";
        for (String name : coverage.getGeographicNames()) {
            regions += name + "\n";
        }
        regionsBox.setText(regions);
    }

    @UiHandler("spacialValueBox")
    void spacialBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setSpatialReference(spacialValueBox.getText());
        markInvalids();
    }

    @UiHandler("southLatValueBox")
    void southLatValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getSouthernLatitude();
        val.setValueString(southLatValueBox.getText());
        try {
            coverage.setSouthernLatitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage("Invalid latitude: ", ex);
            southLatValueBox.setValue(coverage.getSouthernLatitude().getValueString(), false);
        }
        markInvalids();
    }

    @UiHandler("northLatValueBox")
    void northLatValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getNorthernLatitude();
        val.setValueString(northLatValueBox.getText());
        try {
            coverage.setNorthernLatitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage("Invalid latitude: ", ex);
            northLatValueBox.setValue(coverage.getNorthernLatitude().getValueString(), false);
        }
        markInvalids();
    }

    @UiHandler("westLonValueBox")
    void westLonValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getWesternLongitude();
        val.setValueString(westLonValueBox.getText());
        try {
            coverage.setWesternLongitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage("Invalid longitude: ", ex);
            westLonValueBox.setValue(coverage.getWesternLongitude().getValueString(), false);
        }
        markInvalids();
    }

    @UiHandler("eastLonValueBox")
    void eastLonValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getEasternLongitude();
        val.setValueString(eastLonValueBox.getText());
        try {
            coverage.setEasternLongitude(val);
        } catch ( IllegalArgumentException ex ) {
            UploadDashboard.showFailureMessage("Invalid longitude: ", ex);
            eastLonValueBox.setValue(coverage.getEasternLongitude().getValueString(), false);
        }
        markInvalids();
    }

    @UiHandler("startDateBox")
    void startDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setStartDatestamp(parseDatestampString(startDateBox.getText()));
        markInvalids();
    }

    @UiHandler("endDateBox")
    void endDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setEndDatestamp(parseDatestampString(endDateBox.getText()));
        markInvalids();
    }

    @UiHandler("earlyDateBox")
    void earlyDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setEarliestDataDate(parseDatestampString(earlyDateBox.getText()));
        markInvalids();
    }

    @UiHandler("lateDateBox")
    void lateDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setLatestDataDate(parseDatestampString(lateDateBox.getText()));
        markInvalids();
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
        markInvalids();
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
     */
    private void markInvalids() {
        HashSet<String> invalids = coverage.invalidFieldNames(today);
        if ( invalids.contains("spatialReference") )
            spacialValueBox.markInvalid();
        else
            spacialValueBox.markValid();

        if ( invalids.contains("southernLatitude") )
            southLatValueBox.markInvalid();
        else
            southLatValueBox.markValid();

        if ( invalids.contains("northernLatitude") )
            northLatValueBox.markInvalid();
        else
            northLatValueBox.markValid();

        if ( invalids.contains("westernLongitude") )
            westLonValueBox.markInvalid();
        else
            westLonValueBox.markValid();

        if ( invalids.contains("easternLongitude") )
            eastLonValueBox.markInvalid();
        else
            eastLonValueBox.markValid();

        if ( invalids.contains("startDatestamp") )
            startDateBox.markInvalid();
        else
            startDateBox.markValid();

        if ( invalids.contains("endDatestamp") )
            endDateBox.markInvalid();
        else
            endDateBox.markValid();

        if ( invalids.contains("earliestDataDate") )
            earlyDateBox.markInvalid();
        else
            earlyDateBox.markValid();

        if ( invalids.contains("latestDataDate") )
            lateDateBox.markInvalid();
        else
            lateDateBox.markValid();

        if ( invalids.contains("geographicNames") )
            regionsPanel.setCaptionHTML(SafeHtmlUtils.fromSafeConstant(
                    "<b><em><color:red>" + REGIONS_PANEL_CAPTION + "</color></em></b>"));
        else
            regionsPanel.setCaptionText(REGIONS_PANEL_CAPTION);
    }

    /**
     * @return the update Coverage object; never null
     */
    public Coverage getUpdatedCoverage() {
        return coverage;
    }

}
