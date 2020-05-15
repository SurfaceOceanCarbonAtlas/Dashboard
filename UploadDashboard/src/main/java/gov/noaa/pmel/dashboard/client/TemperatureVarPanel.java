package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;

import java.util.HashSet;

public class TemperatureVarPanel extends GenDataVarPanel {

    interface TemperatureVarPanelUiBinder extends UiBinder<FlowPanel,TemperatureVarPanel> {
    }

    private static final TemperatureVarPanelUiBinder uiBinder = GWT.create(TemperatureVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given Temperature metadata.
     *
     * @param temper
     *         associate this panel with this Temperature; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public TemperatureVarPanel(Temperature temper, HTML header, VariablesTabPanel parentPanel) {
        super(temper, header, parentPanel);
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        Temperature temper = (Temperature) vari;
        // TODO: Assign the values in the text fields added in this panel

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    // TODO: Handlers for widgets added by this panel

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Temperature) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
