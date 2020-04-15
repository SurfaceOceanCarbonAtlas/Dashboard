package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;
import gov.noaa.pmel.socatmetadata.shared.core.Datestamp;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;

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
    TextBox southLatUnitBox;
    @UiField
    Label northLatLabel;
    @UiField
    TextBox northLatValueBox;
    @UiField
    TextBox northLatUnitBox;
    @UiField
    Label westLonLabel;
    @UiField
    TextBox westLonValueBox;
    @UiField
    TextBox westLonUnitBox;
    @UiField
    Label eastLonLabel;
    @UiField
    TextBox eastLonValueBox;
    @UiField
    TextBox eastLonUnitBox;
    @UiField
    Label earlyDateLabel;
    @UiField
    TextBox earlyDateBox;
    @UiField
    Label lateDateLabel;
    @UiField
    TextBox lateDateBox;
    @UiField
    Label regionsLabel;
    @UiField
    TextArea regionsBox;

    private Coverage coverage;

    /**
     * Creates a FlowPanel associated with the given Coverage.
     *
     * @param coverage
     *         associate this panel with this Coverage; cannot be null
     */
    public CoveragePanel(Coverage coverage) {
        initWidget(uiBinder.createAndBindUi(this));

        this.coverage = coverage;

        spacialLabel.setText("Spatial reference:");
        spacialBox.setText(coverage.getSpatialReference());

        southLatLabel.setText("Southern-most latitude:");
        southLatValueBox.setText(coverage.getSouthernLatitude().getValueString());
        southLatUnitBox.setText(coverage.getSouthernLatitude().getUnitString());

        northLatLabel.setText("Northern-most latitude:");
        northLatValueBox.setText(coverage.getNorthernLatitude().getValueString());
        northLatUnitBox.setText(coverage.getNorthernLatitude().getUnitString());

        westLonLabel.setText("Western-most longitude:");
        westLonValueBox.setText(coverage.getWesternLongitude().getValueString());
        westLonUnitBox.setText(coverage.getWesternLongitude().getUnitString());

        eastLonLabel.setText("Eastern-most longitude:");
        eastLonValueBox.setText(coverage.getEasternLongitude().getValueString());
        eastLonUnitBox.setText(coverage.getEasternLongitude().getUnitString());

        earlyDateLabel.setText("Starting date:");
        String datetime = "";
        try {
            Datestamp stamp = coverage.getEarliestDataDate();
            datetime = stamp.dateString();
            datetime += " " + stamp.timeString();
        } catch ( IllegalArgumentException ex ) {
            // Use the datetime as-is (may be blank or just have the date)
        }
        earlyDateBox.setText(datetime);

        lateDateLabel.setText("Ending date:");
        datetime = "";
        try {
            Datestamp stamp = coverage.getLatestDataDate();
            datetime = stamp.dateString();
            datetime += " " + stamp.timeString();
        } catch ( IllegalArgumentException ex ) {
            // Use the datetime as-is (may be blank or just have the date)
        }
        lateDateBox.setText(datetime);

        regionsLabel.setText("Geographic names:");
        String regions = "";
        for (String name : coverage.getGeographicNames()) {
            regions += name + "\n";
        }
        regionsBox.setText(regions);
    }

    @UiHandler("spacialBox")
    void spacialBoxOnValueChange(ValueChangeEvent<String> event) {
        coverage.setSpatialReference(spacialBox.getText());
    }

    @UiHandler("southLatValueBox")
    void southLatValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getSouthernLatitude();
        val.setValueString(southLatValueBox.getText());
        coverage.setSouthernLatitude(val);
    }

    @UiHandler("southLatUnitBox")
    void southLatUnitBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getSouthernLatitude();
        val.setUnitString(southLatUnitBox.getText());
        coverage.setSouthernLatitude(val);
    }

    @UiHandler("northLatValueBox")
    void northLatValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getNorthernLatitude();
        val.setValueString(northLatValueBox.getText());
        coverage.setNorthernLatitude(val);
    }

    @UiHandler("northLatUnitBox")
    void northLatUnitBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getNorthernLatitude();
        val.setUnitString(northLatUnitBox.getText());
        coverage.setNorthernLatitude(val);
    }

    @UiHandler("westLonValueBox")
    void westLonValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getWesternLongitude();
        val.setValueString(westLonValueBox.getText());
        coverage.setWesternLongitude(val);
    }

    @UiHandler("westLonUnitBox")
    void westLonUnitBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getWesternLongitude();
        val.setUnitString(westLonUnitBox.getText());
        coverage.setWesternLongitude(val);
    }

    @UiHandler("eastLonValueBox")
    void eastLonValueBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getEasternLongitude();
        val.setValueString(eastLonValueBox.getText());
        coverage.setEasternLongitude(val);
    }

    @UiHandler("eastLonUnitBox")
    void eastLonUnitBoxOnValueChange(ValueChangeEvent<String> event) {
        NumericString val = coverage.getEasternLongitude();
        val.setUnitString(eastLonUnitBox.getText());
        coverage.setEasternLongitude(val);
    }

    @UiHandler("earlyDateBox")
    void earlyDateBoxOnValueChange(ValueChangeEvent<String> event) {
        Datestamp stamp;
        String[] pieces = earlyDateBox.getText().split("[ /:-]");
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
        coverage.setEarliestDataDate(stamp);
    }

    @UiHandler("lateDateBox")
    void lateDateBoxOnValueChange(ValueChangeEvent<String> event) {
        Datestamp stamp;
        String[] pieces = lateDateBox.getText().split("[ /:-]");
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
        coverage.setLatestDataDate(stamp);
    }

    @UiHandler("regionsBox")
    void regionsBoxOnValueChanged(ValueChangeEvent<String> event) {
        String[] pieces = regionsBox.getText().split("\n");
        TreeSet<String> regions = new TreeSet<String>();
        for (String str : pieces) {
            regions.add(str.trim());
        }
        coverage.setGeographicNames(regions);
    }

    /**
     * @return the updated Coverage; never null
     */
    public Coverage getUpdatedCoverage() {
        return coverage;
    }
}
