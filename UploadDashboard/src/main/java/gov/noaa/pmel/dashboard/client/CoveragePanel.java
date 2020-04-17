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

    private static CoveragePanelUiBinder uiBinder = GWT.create(CoveragePanelUiBinder.class);

    @UiField
    Label spacialLabel;
    @UiField
    TextBox spacialBox;
    @UiField
    Label southLatLabel;
    @UiField
    TextBox southLatValueBox;
    @UiField
    Label southLatUnitLabel;
    @UiField
    Label northLatLabel;
    @UiField
    TextBox northLatValueBox;
    @UiField
    Label northLatUnitLabel;
    @UiField
    Label westLonLabel;
    @UiField
    TextBox westLonValueBox;
    @UiField
    Label westLonUnitLabel;
    @UiField
    Label eastLonLabel;
    @UiField
    TextBox eastLonValueBox;
    @UiField
    Label eastLonUnitLabel;
    @UiField
    Label startDateLabel;
    @UiField
    TextBox startDateBox;
    @UiField
    Label endDateLabel;
    @UiField
    TextBox endDateBox;
    @UiField
    Label earlyDateLabel;
    @UiField
    TextBox earlyDateBox;
    @UiField
    Label lateDateLabel;
    @UiField
    TextBox lateDateBox;
    @UiField
    CaptionPanel regionsPanel;
    @UiField
    TextArea regionsBox;

    private Coverage coverage;
    private Datestamp today;

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

        spacialLabel.setText("Spatial reference:");
        spacialBox.setText(coverage.getSpatialReference());

        southLatLabel.setText("Southern-most latitude:");
        southLatValueBox.setText(coverage.getSouthernLatitude().getValueString());
        southLatUnitLabel.setText(coverage.getSouthernLatitude().getUnitString());

        northLatLabel.setText("Northern-most latitude:");
        northLatValueBox.setText(coverage.getNorthernLatitude().getValueString());
        northLatUnitLabel.setText(coverage.getNorthernLatitude().getUnitString());

        westLonLabel.setText("Western-most longitude:");
        westLonValueBox.setText(coverage.getWesternLongitude().getValueString());
        westLonUnitLabel.setText(coverage.getWesternLongitude().getUnitString());

        eastLonLabel.setText("Eastern-most longitude:");
        eastLonValueBox.setText(coverage.getEasternLongitude().getValueString());
        eastLonUnitLabel.setText(coverage.getEasternLongitude().getUnitString());

        startDateLabel.setText("Starting date:");
        startDateBox.setText(coverage.getStartDatestamp().fullOrPartialString());

        endDateLabel.setText("Ending date:");
        endDateBox.setText(coverage.getEndDatestamp().fullOrPartialString());

        earlyDateLabel.setText("Earliest data date/time:");
        earlyDateBox.setText(coverage.getEarliestDataDate().fullOrPartialString());

        lateDateLabel.setText("Latest data date/time:");
        lateDateBox.setText(coverage.getLatestDataDate().fullOrPartialString());

        regionsPanel.setCaptionText("Geographic names:");
        String regions = "";
        for (String name : coverage.getGeographicNames()) {
            regions += name + "\n";
        }
        regionsBox.setText(regions);
    }

    @UiHandler("spacialBox")
    void spacialBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setSpatialReference(spacialBox.getText());
        markInvalid();
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
        markInvalid();
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
        markInvalid();
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
        markInvalid();
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
        markInvalid();
    }

    @UiHandler("startDateBox")
    void startDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setStartDatestamp(parseDatestampString(startDateBox.getText()));
        markInvalid();
    }

    @UiHandler("endDateBox")
    void endDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setEndDatestamp(parseDatestampString(endDateBox.getText()));
        markInvalid();
    }

    @UiHandler("earlyDateBox")
    void earlyDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setEarliestDataDate(parseDatestampString(earlyDateBox.getText()));
        markInvalid();
    }

    @UiHandler("lateDateBox")
    void lateDateBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setLatestDataDate(parseDatestampString(lateDateBox.getText()));
        markInvalid();
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
        markInvalid();
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
     * Update including the "-invalid" style on all text boxes depending on the
     * current invalid fields in the coverage object.
     */
    private void markInvalid() {
        spacialBox.removeStyleDependentName("invalid");
        southLatValueBox.removeStyleDependentName("invalid");
        northLatValueBox.removeStyleDependentName("invalid");
        westLonValueBox.removeStyleDependentName("invalid");
        eastLonValueBox.removeStyleDependentName("invalid");
        startDateBox.removeStyleDependentName("invalid");
        endDateBox.removeStyleDependentName("invalid");
        earlyDateBox.removeStyleDependentName("invalid");
        lateDateBox.removeStyleDependentName("invalid");
        regionsBox.removeStyleDependentName("invalid");
        HashSet<String> invalids = coverage.invalidFieldNames(today);
        for (String name : invalids) {
            switch ( name ) {
                case "spatialReference":
                    spacialBox.addStyleDependentName("invalid");
                    break;
                case "southernLatitude":
                    southLatValueBox.addStyleDependentName("invalid");
                    break;
                case "northernLatitude":
                    northLatValueBox.addStyleDependentName("invalid");
                    break;
                case "westernLongitude":
                    westLonValueBox.addStyleDependentName("invalid");
                    break;
                case "easternLongitude":
                    eastLonValueBox.addStyleDependentName("invalid");
                    break;
                case "startDatestamp":
                    startDateBox.addStyleDependentName("invalid");
                    break;
                case "endDatestamp":
                    endDateBox.addStyleDependentName("invalid");
                    break;
                case "earliestDataDate":
                    earlyDateBox.addStyleDependentName("invalid");
                    break;
                case "latestDataDate":
                    lateDateBox.addStyleDependentName("invalid");
                    break;
                case "geographicNames":
                    regionsBox.addStyleDependentName("invalid");
                    break;
                default:
                    UploadDashboard.showMessage("Unexpected invalid field name of " +
                            SafeHtmlUtils.htmlEscape(name) + " for Coverage");
                    break;
            }
        }
    }

    /**
     * @return the updated Coverage; never null
     */
    public Coverage getUpdatedCoverage() {
        return coverage;
    }
}
