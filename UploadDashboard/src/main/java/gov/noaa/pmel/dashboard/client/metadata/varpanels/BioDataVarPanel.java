package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.BioData;

import java.util.HashSet;

public class BioDataVarPanel extends InstDataVarPanel {

    interface BioDataVarPanelUiBinder extends UiBinder<FlowPanel,BioDataVarPanel> {
    }

    private static final BioDataVarPanelUiBinder uiBinder = GWT.create(BioDataVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given BioData metadata.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param biovar
     *         associate this panel with this BioData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public BioDataVarPanel(BioData biovar, HTML header, VariablesTabPanel parentPanel) {
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
        BioData biovar = (BioData) vari;
        // TODO: Assign the values in the text fields added in this panel

        // TODO: Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((BioData) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
