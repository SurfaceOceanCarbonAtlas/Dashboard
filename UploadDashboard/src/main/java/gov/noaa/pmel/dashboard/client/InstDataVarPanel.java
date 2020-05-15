package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.InstDataVar;

import java.util.HashSet;

public class InstDataVarPanel extends GenDataVarPanel {

    interface InstDataVarPanelUiBinder extends UiBinder<FlowPanel,InstDataVarPanel> {
    }

    private static final InstDataVarPanelUiBinder uiBinder = GWT.create(InstDataVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given InstDataVar metadata.
     *
     * @param instvar
     *         associate this panel with this InstDataVar; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public InstDataVarPanel(InstDataVar instvar, HTML header, VariablesTabPanel parentPanel) {
        super(instvar, header, parentPanel);
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        InstDataVar instvar = (InstDataVar) vari;
        // TODO: Assign the values in the text fields added in this panel

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    // TODO: Handlers for widgets added by this panel

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((InstDataVar) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
