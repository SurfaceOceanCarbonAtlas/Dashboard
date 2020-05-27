package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledListBox;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

import java.util.HashSet;


public class GenericInstPanel extends InstrumentPanel {

    interface GenericInstPanelUiBinder extends UiBinder<ScrollPanel,GenericInstPanel> {
    }

    private static final GenericInstPanelUiBinder uiBinder = GWT.create(GenericInstPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox nameValue;
    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledListBox instTypeList;
    @UiField(provided = true)
    final LabeledTextBox manufacturerValue;
    @UiField(provided = true)
    final LabeledTextBox modelValue;
    @UiField(provided = true)
    final LabeledTextArea addnInfoValue;

    /**
     * Creates a FlowPanel associated with the given generic instrument metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param instr
     *         associate this panel with this Instrument; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     * @param parentPanel
     *         parent TabPanel controlling this panel
     */
    public GenericInstPanel(Instrument instr, HTML header, InstrumentsTabPanel parentPanel) {
        super(instr, header, parentPanel);

        nameValue = new LabeledTextBox("Name:", "7em", "23em", null, null);
        idValue = new LabeledTextBox("Serial/ID:", "8em", "23em", null, null);
        //
        instTypeList = new LabeledListBox("Instrument type:", "7em", null, null, null);
        //
        manufacturerValue = new LabeledTextBox("Manufacturer:", "7em", "23em", null, null);
        modelValue = new LabeledTextBox("Model:", "8em", "23em", null, null);
        //
        addnInfoValue = new LabeledTextArea("Other info:", "7em", "8em", "56em");
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        nameValue.setText(instr.getName());
        idValue.setText(instr.getId());
        manufacturerValue.setText(instr.getManufacturer());
        modelValue.setText(instr.getModel());
        addnInfoValue.setText(instr.getAddnInfo().asOneString());

        // Assign the instrument types list and callback
        parentPanel.assignInstrumentTypeList(instTypeList, instr, this);

        nameValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instr.setName(nameValue.getText());
                markInvalids(null);
            }
        });
        idValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instr.setId(idValue.getText());
                markInvalids(null);
            }
        });
        manufacturerValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instr.setManufacturer(manufacturerValue.getText());
                markInvalids(null);
            }
        });
        modelValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instr.setModel(modelValue.getText());
                markInvalids(null);
            }
        });
        addnInfoValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instr.setAddnInfo(new MultiString(addnInfoValue.getText()));
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = instr.invalidFieldNames();

        nameValue.markInvalid(invalids.contains("name"));
        idValue.markInvalid(invalids.contains("id"));
        manufacturerValue.markInvalid(invalids.contains("manufacturer"));
        modelValue.markInvalid(invalids.contains("model"));
        addnInfoValue.markInvalid(invalids.contains("addnInfo"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
