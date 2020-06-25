package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;

import java.util.HashSet;


public class GenericSensorPanel extends GenericInstPanel {

    interface GenericSensorPanelUiBinder extends UiBinder<ScrollPanel,GenericSensorPanel> {
    }

    private static final GenericSensorPanelUiBinder uiBinder = GWT.create(GenericSensorPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextArea calibrationValue;

    /**
     * Creates a FlowPanel associated with the given generic sensor metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param instr
     *         associate this panel with this sensor; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     * @param parentPanel
     *         parent TabPanel controlling this panel
     */
    public GenericSensorPanel(Analyzer instr, HTML header, InstrumentsTabPanel parentPanel) {
        super(instr, header, parentPanel);

        calibrationValue = new LabeledTextArea("Calibration", "7em", "8em", "56em");
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        Analyzer sensor = (Analyzer) instr;

        calibrationValue.setText(sensor.getCalibration());
        calibrationValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                sensor.setCalibration(calibrationValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Analyzer) instr).invalidFieldNames();

        calibrationValue.markInvalid(invalids.contains("calibration"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
