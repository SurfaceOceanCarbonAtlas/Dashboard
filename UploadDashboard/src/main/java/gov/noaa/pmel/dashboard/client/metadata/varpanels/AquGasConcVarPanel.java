package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;

import java.util.HashSet;

public class AquGasConcVarPanel extends GasConcVarPanel {

    interface AquGasConcVarPanelUiBinder extends UiBinder<ScrollPanel,AquGasConcVarPanel> {
    }

    private static final AquGasConcVarPanelUiBinder uiBinder = GWT.create(AquGasConcVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox reportTempValue;
    @UiField(provided = true)
    final LabeledTextBox tempCorrectValue;

    /**
     * Creates a FlowPanel associated with the given AquGasConc metadata.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param conc
     *         associate this panel with this AquGasConc; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public AquGasConcVarPanel(AquGasConc conc, HTML header, VariablesTabPanel parentPanel) {
        super(conc, header, parentPanel);

        reportTempValue = new LabeledTextBox("Report temperature:", "7em", "23em", null, null);
        tempCorrectValue = new LabeledTextBox("Temperature correction:", "8em", "23em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        AquGasConc conc = (AquGasConc) vari;

        reportTempValue.setText(conc.getReportTemperature());
        tempCorrectValue.setText(conc.getTemperatureCorrection());

        reportTempValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                conc.setReportTemperature(reportTempValue.getText());
                markInvalids(null);
            }
        });
        tempCorrectValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                conc.setTemperatureCorrection(tempCorrectValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((AquGasConc) vari).invalidFieldNames();

        reportTempValue.markInvalid(invalids.contains("reportTemperature"));
        tempCorrectValue.markInvalid(invalids.contains("temperatureCorrection"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
