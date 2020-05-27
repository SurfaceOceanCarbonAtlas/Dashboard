package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;

import java.util.HashSet;

public class GasConcVarPanel extends InstDataVarPanel {

    interface GasConcVarPanelUiBinder extends UiBinder<ScrollPanel,GasConcVarPanel> {
    }

    private static final GasConcVarPanelUiBinder uiBinder = GWT.create(GasConcVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextArea dryMethodValue;
    @UiField(provided = true)
    final LabeledTextArea waterCorrectValue;

    /**
     * Creates a FlowPanel associated with the given GasConc metadata.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param conc
     *         associate this panel with this GasConc; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public GasConcVarPanel(GasConc conc, HTML header, VariablesTabPanel parentPanel) {
        super(conc, header, parentPanel);

        dryMethodValue = new LabeledTextArea("Drying method:", "7em", "4em", "50em");
        waterCorrectValue = new LabeledTextArea("Water vapor correction:", "7em", "4em", "50em");
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        GasConc conc = (GasConc) vari;

        dryMethodValue.setText(conc.getDryingMethod());
        waterCorrectValue.setText(conc.getWaterVaporCorrection());

        dryMethodValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                conc.setDryingMethod(dryMethodValue.getText());
                markInvalids(null);
            }
        });
        waterCorrectValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                conc.setWaterVaporCorrection(waterCorrectValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((GasConc) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel
        if ( invalids.contains("dryingMethod") )
            dryMethodValue.markInvalid();
        else
            dryMethodValue.markValid();

        if ( invalids.contains("waterVaporCorrection") )
            waterCorrectValue.markInvalid();
        else
            waterCorrectValue.markValid();

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
