package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;

import java.util.HashSet;

public class GasConcVarPanel extends GenDataVarPanel {

    interface GasConcVarPanelUiBinder extends UiBinder<FlowPanel,GasConcVarPanel> {
    }

    private static final GasConcVarPanelUiBinder uiBinder = GWT.create(GasConcVarPanelUiBinder.class);

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
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        GasConc conc = (GasConc) vari;
        // TODO: Assign the values in the text fields added in this panel

        // TODO: Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((GasConc) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
