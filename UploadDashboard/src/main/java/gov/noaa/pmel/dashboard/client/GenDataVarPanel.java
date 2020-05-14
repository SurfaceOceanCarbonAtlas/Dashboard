package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.variable.GenDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.HashSet;

public class GenDataVarPanel extends VariablePanel {

    interface GenDataVarPanelUiBinder extends UiBinder<FlowPanel,GenDataVarPanel> {
    }

    private static final GenDataVarPanelUiBinder uiBinder = GWT.create(GenDataVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox columnNameValue;
    @UiField(provided = true)
    final LabeledTextBox fullNameValue;
    @UiField(provided = true)
    final LabeledListBox varTypeList;
    @UiField(provided = true)
    final LabeledTextBox unitValue;
    @UiField(provided = true)
    final LabeledTextBox accuracyValue;
    @UiField(provided = true)
    final LabeledTextBox precisionValue;
    @UiField(provided = true)
    final LabeledTextBox missingValue;
    @UiField(provided = true)
    final LabeledTextBox flagNameValue;
    @UiField(provided = true)
    final LabeledTextArea addnInfoValue;

    private final GenDataVar vari;
    private final HTML header;

    /**
     * Creates a FlowPanel associated with the given Variable.
     *
     * @param vari
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public GenDataVarPanel(GenDataVar vari, HTML header, VariablesTabPanel parentPanel) {
        columnNameValue = new LabeledTextBox("Column name:", "11em", "20em", null, null);
        fullNameValue = new LabeledTextBox("Full name:", "10em", "20em", null, null);
        //
        varTypeList = new LabeledListBox("Type:", "11em", "20.75em", null, null);
        unitValue = new LabeledTextBox("Units:", "10em", "20em", null, null);
        //
        accuracyValue = new LabeledTextBox("Accuracy:", "11em", "20em", null, null);
        precisionValue = new LabeledTextBox("Precision:", "10em", "20em", null, null);
        //
        missingValue = new LabeledTextBox("Missing value:", "11em", "20em", null, null);
        flagNameValue = new LabeledTextBox("QC column:", "10em", "20em", null, null);
        //
        addnInfoValue = new LabeledTextArea("Additional information", "10em", "54.5em");

        initWidget(uiBinder.createAndBindUi(this));

        this.vari = vari;
        this.header = header;

        // Assign the variable types list - labels and callback
        parentPanel.assignVariableTypeList(varTypeList, vari, this);

        // The following will assign the values in the labels and text fields
        getUpdatedVariable();
    }

    @UiHandler("columnNameValue")
    void columnNameValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setColName(columnNameValue.getText());
        markInvalids();
    }

    @UiHandler("fullNameValue")
    void fullNameValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setFullName(fullNameValue.getText());
        markInvalids();
    }

    @UiHandler("unitValue")
    void unitValueOnValueChange(ValueChangeEvent<String> event) {
        String unit = unitValue.getText();
        accuracyValue.setSuffix(unit);
        precisionValue.setSuffix(unit);
        vari.setVarUnit(unit);
        NumericString numstr = vari.getAccuracy();
        numstr.setUnitString(unit);
        vari.setAccuracy(numstr);
        numstr = vari.getPrecision();
        numstr.setUnitString(unit);
        vari.setPrecision(numstr);
        markInvalids();
    }

    @UiHandler("accuracyValue")
    void accuracyValueOnValueChange(ValueChangeEvent<String> event) {
        NumericString value;
        try {
            value = new NumericString(accuracyValue.getText(), unitValue.getText());
        } catch ( IllegalArgumentException ex ) {
            value = new NumericString(null, unitValue.getText());
        }
        vari.setAccuracy(value);
        markInvalids();
    }

    @UiHandler("precisionValue")
    void precisionValueOnValueChange(ValueChangeEvent<String> event) {
        NumericString value;
        try {
            value = new NumericString(precisionValue.getText(), unitValue.getText());
        } catch ( IllegalArgumentException ex ) {
            value = new NumericString(null, unitValue.getText());
        }
        vari.setPrecision(value);
        markInvalids();
    }

    @UiHandler("missingValue")
    void missingValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setMissVal(missingValue.getText());
        markInvalids();
    }

    @UiHandler("flagNameValue")
    void flagNameValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setFlagColName(flagNameValue.getText());
        markInvalids();
    }

    @UiHandler("addnInfoValue")
    void addnInfoValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setAddnInfo(new MultiString(addnInfoValue.getText()));
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = vari.invalidFieldNames();

        String oldVal = header.getHTML();
        SafeHtml val = SafeHtmlUtils.fromString(vari.getReferenceName());
        if ( !invalids.isEmpty() )
            val = UploadDashboard.invalidLabelHtml(val);
        if ( !val.asString().equals(oldVal) )
            header.setHTML(val);

        if ( invalids.contains("colName") )
            columnNameValue.markInvalid();
        else
            columnNameValue.markValid();

        if ( invalids.contains("fullName") )
            fullNameValue.markInvalid();
        else
            fullNameValue.markValid();

        if ( invalids.contains("varUnit") )
            unitValue.markInvalid();
        else
            unitValue.markValid();

        if ( invalids.contains("accuracy") )
            accuracyValue.markInvalid();
        else
            accuracyValue.markValid();

        if ( invalids.contains("precision") )
            precisionValue.markInvalid();
        else
            precisionValue.markValid();

        if ( invalids.contains("missVal") )
            missingValue.markInvalid();
        else
            missingValue.markValid();

        if ( invalids.contains("flagColName") )
            flagNameValue.markInvalid();
        else
            flagNameValue.markValid();

        if ( invalids.contains("addnInfo") )
            addnInfoValue.markInvalid();
        else
            addnInfoValue.markValid();
    }

    @Override
    public Variable getUpdatedVariable() {
        columnNameValue.setText(vari.getColName());
        fullNameValue.setText(vari.getFullName());
        // varType handled by the parent panel
        unitValue.setText(vari.getVarUnit());
        accuracyValue.setText(vari.getAccuracy().getValueString());
        precisionValue.setText(vari.getPrecision().getValueString());
        missingValue.setText(vari.getMissVal());
        flagNameValue.setText(vari.getFlagColName());
        addnInfoValue.setText(vari.getAddnInfo().asOneString());

        markInvalids();
        return vari;
    }

}
