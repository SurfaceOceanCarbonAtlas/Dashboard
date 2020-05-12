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
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.HashSet;

public class TemperatureVarPanel extends VariablePanel {

    interface TemperatureVarPanelUiBinder extends UiBinder<FlowPanel,TemperatureVarPanel> {
    }

    private static final TemperatureVarPanelUiBinder uiBinder = GWT.create(TemperatureVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox colNameValue;
    @UiField(provided = true)
    final LabeledTextBox fullNameValue;
    @UiField(provided = true)
    final LabeledListBox varTypeList;

    private final Variable vari;
    private final HTML header;

    /**
     * Creates a FlowPanel associated with the given Variable.
     *
     * @param vari
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public TemperatureVarPanel(Variable vari, HTML header, VariablesTabPanel parentPanel) {
        colNameValue = new LabeledTextBox("Column name:", "12em", "20em", null, null);
        fullNameValue = new LabeledTextBox("Full name:", "12em", "20em", null, null);
        varTypeList = new LabeledListBox("Type:", "12em", "20em", null, null);

        initWidget(uiBinder.createAndBindUi(this));

        this.vari = vari;
        this.header = header;

        // Assign the variable types list - labels and callback
        parentPanel.assignVariableTypeList(varTypeList, vari, this);

        // The following will assign the values in the labels and text fields
        getUpdatedVariable();
    }

    @UiHandler("colNameValue")
    void colNameValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setColName(colNameValue.getText());
        markInvalids();
    }

    @UiHandler("fullNameValue")
    void fullNameValueOnValueChange(ValueChangeEvent<String> event) {
        vari.setFullName(fullNameValue.getText());
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
            colNameValue.markInvalid();
        else
            colNameValue.markValid();

        if ( invalids.contains("fullName") )
            fullNameValue.markInvalid();
        else
            fullNameValue.markValid();
    }

    @Override
    public Variable getUpdatedVariable() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue.
        // But do not mess with the variable type as this is handled by the parent tab panel
        colNameValue.setText(vari.getColName());
        fullNameValue.setText(vari.getFullName());

        markInvalids();
        return vari;
    }

}
