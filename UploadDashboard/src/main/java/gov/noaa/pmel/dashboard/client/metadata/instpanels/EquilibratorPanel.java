package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;

import java.util.HashSet;


public class EquilibratorPanel extends GenericSamplerPanel {

    interface EquilibratorPanelUiBinder extends UiBinder<ScrollPanel,EquilibratorPanel> {
    }

    private static final EquilibratorPanelUiBinder uiBinder = GWT.create(EquilibratorPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox equilTypeValue;
    @UiField(provided = true)
    final LabeledTextBox volumeValue;
    @UiField(provided = true)
    final LabeledTextBox waterVolValue;
    @UiField(provided = true)
    final LabeledTextBox gasVolValue;
    @UiField(provided = true)
    final LabeledTextBox waterFlowValue;
    @UiField(provided = true)
    final LabeledTextBox gasFlowValue;
    @UiField(provided = true)
    final LabeledTextBox ventingValue;

    /**
     * Creates a FlowPanel associated with the given equilibrator metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param instr
     *         associate this panel with this equilibrator; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     * @param parentPanel
     *         parent TabPanel controlling this panel
     */
    public EquilibratorPanel(Equilibrator instr, HTML header, InstrumentsTabPanel parentPanel) {
        super(instr, header, parentPanel);

        equilTypeValue = new LabeledTextBox("Equil. type:", "7em", "56em", null, null);
        //
        volumeValue = new LabeledTextBox("Chamber volume:", "7em", "23em", null, null);
        ventingValue = new LabeledTextBox("Venting:", "8em", "23em", null, null);
        //
        waterVolValue = new LabeledTextBox("Water volume:", "7em", "23em", null, null);
        gasVolValue = new LabeledTextBox("Gas volume:", "8em", "23em", null, null);
        //
        waterFlowValue = new LabeledTextBox("Water flow rate:", "7em", "23em", null, null);
        gasFlowValue = new LabeledTextBox("Gas flow rate:", "8em", "23em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        Equilibrator equilibrator = (Equilibrator) instr;

        equilTypeValue.setText(equilibrator.getEquilibratorType());
        volumeValue.setText(equilibrator.getChamberVol());
        ventingValue.setText(equilibrator.getVenting());
        waterVolValue.setText(equilibrator.getChamberWaterVol());
        gasVolValue.setText(equilibrator.getChamberGasVol());
        waterFlowValue.setText(equilibrator.getWaterFlowRate());
        gasFlowValue.setText(equilibrator.getGasFlowRate());

        equilTypeValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setEquilibratorType(equilTypeValue.getText());
                markInvalids(null);
            }
        });
        volumeValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setChamberVol(volumeValue.getText());
                markInvalids(null);
            }
        });
        ventingValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setVenting(ventingValue.getText());
                markInvalids(null);
            }
        });
        waterVolValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setChamberWaterVol(waterVolValue.getText());
                markInvalids(null);
            }
        });
        gasVolValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setChamberGasVol(gasVolValue.getText());
                markInvalids(null);
            }
        });
        waterFlowValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setWaterFlowRate(waterFlowValue.getText());
                markInvalids(null);
            }
        });
        gasFlowValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                equilibrator.setGasFlowRate(gasFlowValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Equilibrator) instr).invalidFieldNames();

        equilTypeValue.markInvalid(invalids.contains("equilibratorType"));
        volumeValue.markInvalid(invalids.contains("chamberVol"));
        ventingValue.markInvalid(invalids.contains("venting"));
        waterVolValue.markInvalid(invalids.contains("chamberWaterVol"));
        gasVolValue.markInvalid(invalids.contains("chamberGasVol"));
        waterFlowValue.markInvalid(invalids.contains("waterFlowRate"));
        gasFlowValue.markInvalid(invalids.contains("gasFlowRate"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
