package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.HashSet;

public class VariablePanel extends Composite {

    interface VariablePanelUiBinder extends UiBinder<FlowPanel,VariablePanel> {
    }

    private static VariablePanelUiBinder uiBinder = GWT.create(VariablePanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox colNameValue;
    @UiField(provided = true)
    final LabeledTextBox fullNameValue;

    private Variable var;
    private HTML header;

    /**
     * Creates a FlowPanel associated with the given Variable.
     *
     * @param var
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public VariablePanel(Variable var, HTML header) {
        colNameValue = new LabeledTextBox("ID:", "12em", "20em", null, null);
        fullNameValue = new LabeledTextBox("Name:", "12em", "20em", null, null);
        initWidget(uiBinder.createAndBindUi(this));

        this.var = var;
        this.header = header;

        // The following will assign the values in the labels and text fields
        getUpdatedVariable();
    }

    @UiHandler("colNameValue")
    void colNameValueOnValueChange(ValueChangeEvent<String> event) {
        var.setColName(colNameValue.getText());
        markInvalids();
    }

    @UiHandler("fullNameValue")
    void fullNameValueOnValueChange(ValueChangeEvent<String> event) {
        var.setFullName(fullNameValue.getText());
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = var.invalidFieldNames();

        String oldVal = header.getHTML();
        SafeHtml val = SafeHtmlUtils.fromString(var.getReferenceName());
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

    /**
     * @return the updated Variable; never null
     */
    public Variable getUpdatedVariable() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        colNameValue.setText(var.getColName());
        fullNameValue.setText(var.getFullName());

        markInvalids();

        return var;
    }

}
