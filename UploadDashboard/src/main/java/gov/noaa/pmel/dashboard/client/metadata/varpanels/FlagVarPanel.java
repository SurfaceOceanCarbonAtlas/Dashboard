package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledListBox;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.HashSet;

public class FlagVarPanel extends VariablePanel {

    interface FlagVarPanelUiBinder extends UiBinder<ScrollPanel, FlagVarPanel> {
    }

    private static final FlagVarPanelUiBinder uiBinder = GWT.create(FlagVarPanelUiBinder.class);

    @UiField(provided = true)
    protected final LabeledTextBox columnNameValue;
    @UiField(provided = true)
    protected final LabeledTextBox fullNameValue;
    @UiField(provided = true)
    protected final LabeledListBox varTypeList;
    @UiField(provided = true)
    protected final LabeledTextBox unitValue;
    @UiField(provided = true)
    protected final LabeledTextBox missingValue;
    @UiField(provided = true)
    protected final LabeledTextArea addnInfoValue;

    /**
     * Creates but does not initialize a FlowPanel associated with the given Variable.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param vari
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public FlagVarPanel(Variable vari, HTML header, VariablesTabPanel parentPanel) {
        super(vari, header, parentPanel);
        // Create the provided widgets added by this panel
        columnNameValue = new LabeledTextBox("Column name:", "7em", "18.5em", null, null);
        fullNameValue = new LabeledTextBox("Full name:", "9em", "24em", null, null);
        //
        varTypeList = new LabeledListBox("Type:", "7em", null, null, null);
        //
        unitValue = new LabeledTextBox("Units:", "7em", "10em", null, null);
        //
        missingValue = new LabeledTextBox("Missing value:", "7em", "10em", null, null);
        //
        addnInfoValue = new LabeledTextArea("Other info:", "7em", "8em", "54em");
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        // Assign the values in the text fields added in this panel
        columnNameValue.setText(vari.getColName());
        fullNameValue.setText(vari.getFullName());
        unitValue.setText(vari.getVarUnit());
        missingValue.setText(vari.getMissVal());
        addnInfoValue.setText(vari.getAddnInfo().asOneString());

        // Assign the variable types list and callback
        parentPanel.assignVariableTypeList(varTypeList, vari, this);

        // Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        columnNameValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                vari.setColName(columnNameValue.getText());
                markInvalids(null);
            }
        });
        fullNameValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                vari.setFullName(fullNameValue.getText());
                markInvalids(null);
            }
        });
        unitValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                vari.setVarUnit(unitValue.getText());
                markInvalids(null);
            }
        });
        missingValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                vari.setMissVal(missingValue.getText());
                markInvalids(null);
            }
        });
        addnInfoValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                vari.setAddnInfo(new MultiString(addnInfoValue.getText()));
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = vari.invalidFieldNames();

        // Appropriately mark the labels of fields added in this panel
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

        if ( invalids.contains("missVal") )
            missingValue.markInvalid();
        else
            missingValue.markValid();

        if ( invalids.contains("addnInfo") )
            addnInfoValue.markInvalid();
        else
            addnInfoValue.markValid();

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
