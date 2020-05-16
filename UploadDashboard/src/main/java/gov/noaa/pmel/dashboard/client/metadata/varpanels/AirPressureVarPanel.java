package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;

import java.util.HashSet;

public class AirPressureVarPanel extends InstDataVarPanel {

    interface AirPressureVarPanelUiBinder extends UiBinder<FlowPanel,AirPressureVarPanel> {
    }

    private static final AirPressureVarPanelUiBinder uiBinder = GWT.create(AirPressureVarPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given AirPressure metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param press
     *         associate this panel with this AirPressure; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public AirPressureVarPanel(AirPressure press, HTML header, VariablesTabPanel parentPanel) {
        super(press, header, parentPanel);
        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        AirPressure press = (AirPressure) vari;
        // TODO: Assign the values in the text fields added in this panel

        // TODO: Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((AirPressure) vari).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
