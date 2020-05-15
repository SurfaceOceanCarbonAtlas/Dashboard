package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.GenDataVar;

import java.util.HashSet;

public class GenDataVarPanel extends FlagVarPanel {

    interface GenDataVarPanelUiBinder extends UiBinder<FlowPanel,GenDataVarPanel> {
    }

    private static final GenDataVarPanelUiBinder uiBinder = GWT.create(GenDataVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox accuracyValue;
    @UiField(provided = true)
    final LabeledTextBox precisionValue;
    @UiField(provided = true)
    final LabeledTextBox flagNameValue;

    /**
     * Creates a FlowPanel associated with the given GetDataVar metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param gendata
     *         associate this panel with this GetData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public GenDataVarPanel(GenDataVar gendata, HTML header, VariablesTabPanel parentPanel) {
        super(gendata, header, parentPanel);
        // Create the provided widgets added by this panel
        accuracyValue = new LabeledTextBox("Accuracy:", "11em", "20em", null, null);
        precisionValue = new LabeledTextBox("Precision:", "10em", "20em", null, null);
        //
        flagNameValue = new LabeledTextBox("QC column:", "10em", "20em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        GenDataVar gendata = (GenDataVar) vari;
        // Assign the values in the text fields added in this panel
        accuracyValue.setText((gendata.getAccuracy().getValueString()));
        precisionValue.setText(gendata.getPrecision().getValueString());
        flagNameValue.setText(gendata.getFlagColName());

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    // Handlers for widgets added by this panel

    @UiHandler("unitValue")
    void unitValueAccuracyPrecisionOnValueChange(ValueChangeEvent<String> event) {
        GenDataVar gendata = (GenDataVar) vari;
        String unit = unitValue.getText();
        accuracyValue.setSuffix(unit);
        gendata.setAccuracyUnit(unit);
        precisionValue.setSuffix(unit);
        gendata.setPrecisionUnit(unit);
        markInvalids(null);
    }

    @UiHandler("accuracyValue")
    void accuracyValueOnValueChange(ValueChangeEvent<String> event) {
        GenDataVar gendata = (GenDataVar) vari;
        try {
            gendata.setAccuracy(new NumericString(accuracyValue.getText(), unitValue.getText()));
        } catch ( IllegalArgumentException ex ) {
            gendata.setAccuracy(null);
            gendata.setAccuracyUnit(unitValue.getText());
        }
        markInvalids(null);
    }

    @UiHandler("precisionValue")
    void precisionValueOnValueChange(ValueChangeEvent<String> event) {
        GenDataVar gendata = (GenDataVar) vari;
        try {
            gendata.setPrecision(new NumericString(precisionValue.getText(), unitValue.getText()));
        } catch ( IllegalArgumentException ex ) {
            gendata.setPrecision(null);
            gendata.setPrecisionUnit(unitValue.getText());
        }
        markInvalids(null);
    }

    @UiHandler("flagNameValue")
    void flagNameValueOnValueChange(ValueChangeEvent<String> event) {
        GenDataVar gendata = (GenDataVar) vari;
        gendata.setFlagColName(flagNameValue.getText());
        markInvalids(null);
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((GenDataVar) vari).invalidFieldNames();

        // Appropriately mark the labels of fields added in this panel
        if ( invalids.contains("accuracy") )
            accuracyValue.markInvalid();
        else
            accuracyValue.markValid();

        if ( invalids.contains("precision") )
            precisionValue.markInvalid();
        else
            precisionValue.markValid();

        if ( invalids.contains("flagColName") )
            flagNameValue.markInvalid();
        else
            flagNameValue.markValid();

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
