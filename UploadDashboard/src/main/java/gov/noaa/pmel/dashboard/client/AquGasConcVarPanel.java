package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;

import java.util.HashSet;

public class AquGasConcVarPanel extends GenDataVarPanel {

    interface AquGasConcVarPanelUiBinder extends UiBinder<FlowPanel,AquGasConcVarPanel> {
    }

    private static final AquGasConcVarPanelUiBinder uiBinder = GWT.create(AquGasConcVarPanelUiBinder.class);

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
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        AquGasConc conc = (AquGasConc) vari;
        // TODO: Assign the values in the text fields added in this panel

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    // TODO: Handlers for widgets added by this panel

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((AquGasConc) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
