package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.BioDataVar;

import java.util.HashSet;

public class BioDataVarPanel extends GenDataVarPanel {

    interface BioDataVarPanelUiBinder extends UiBinder<FlowPanel,BioDataVarPanel> {
    }

    private static final BioDataVarPanelUiBinder uiBinder = GWT.create(BioDataVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given BioDataVar metadata.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param biovar
     *         associate this panel with this BioDataVar; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public BioDataVarPanel(BioDataVar biovar, HTML header, VariablesTabPanel parentPanel) {
        super(biovar, header, parentPanel);
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        BioDataVar biovar = (BioDataVar) vari;
        // TODO: Assign the values in the text fields added in this panel

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    // TODO: Handlers for widgets added by this panel

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((BioDataVar) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
