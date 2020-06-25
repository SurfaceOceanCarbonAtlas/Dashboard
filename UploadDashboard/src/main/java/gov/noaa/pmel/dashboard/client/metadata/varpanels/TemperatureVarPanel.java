package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;

import java.util.HashSet;

public class TemperatureVarPanel extends InstDataVarPanel {

    interface TemperatureVarPanelUiBinder extends UiBinder<ScrollPanel,TemperatureVarPanel> {
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

        // Nothing to add
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        // Nothing to add

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Temperature) vari).invalidFieldNames();

        // Nothing to add

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
