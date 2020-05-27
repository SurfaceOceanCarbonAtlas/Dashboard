package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;

import java.util.HashSet;

public class AirPressureVarPanel extends InstDataVarPanel {

    interface AirPressureVarPanelUiBinder extends UiBinder<ScrollPanel,AirPressureVarPanel> {
    }

    private static final AirPressureVarPanelUiBinder uiBinder = GWT.create(AirPressureVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextArea pressCorrectValue;

    /**
     * Creates a FlowPanel associated with the given AirPressure metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param press
     *         associate this panel with this AirPressure; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public AirPressureVarPanel(AirPressure press, HTML header, VariablesTabPanel parentPanel) {
        super(press, header, parentPanel);
        //
        pressCorrectValue = new LabeledTextArea("Pressure correction:", "7em", "4em", "50em");
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        AirPressure press = (AirPressure) vari;

        pressCorrectValue.setText(press.getPressureCorrection());

        pressCorrectValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                press.setPressureCorrection(pressCorrectValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((AirPressure) vari).invalidFieldNames();

        if ( invalids.contains("pressureCorrection") )
            pressCorrectValue.markInvalid();
        else
            pressCorrectValue.markValid();

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
