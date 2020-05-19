package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.socatmetadata.shared.variable.InstData;

import java.util.HashSet;

public class InstDataVarPanel extends GenDataVarPanel {

    interface InstDataVarPanelUiBinder extends UiBinder<ScrollPanel, InstDataVarPanel> {
    }

    private static final InstDataVarPanelUiBinder uiBinder = GWT.create(InstDataVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given InstData metadata.
     *
     * @param instvar
     *         associate this panel with this InstData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public InstDataVarPanel(InstData instvar, HTML header, VariablesTabPanel parentPanel) {
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
        InstData instvar = (InstData) vari;
        // TODO: Assign the values in the text fields added in this panel

        // TODO: Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((InstData) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
