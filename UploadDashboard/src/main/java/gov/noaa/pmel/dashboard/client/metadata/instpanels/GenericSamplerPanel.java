package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;

import java.util.HashSet;


public class GenericSamplerPanel extends GenericInstPanel {

    interface GenericSamplerPanelUiBinder extends UiBinder<ScrollPanel,GenericSamplerPanel> {
    }

    private static final GenericSamplerPanelUiBinder uiBinder = GWT.create(GenericSamplerPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox sensorsValue;

    /**
     * Creates a FlowPanel associated with the given generic sampler metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param instr
     *         associate this panel with this sampler; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     * @param parentPanel
     *         parent TabPanel controlling this panel
     */
    public GenericSamplerPanel(Sampler instr, HTML header, InstrumentsTabPanel parentPanel) {
        super(instr, header, parentPanel);

        sensorsValue = new LabeledTextBox("Sensor names:", "7em", "56em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        Sampler sampler = (Sampler) instr;

        sensorsValue.setText(sampler.getInstrumentNames().asOneString());
        sensorsValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                sampler.setInstrumentNames(new MultiNames(sensorsValue.getText()));
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Sampler) instr).invalidFieldNames();

        sensorsValue.markInvalid(invalids.contains("instrumentNames"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
