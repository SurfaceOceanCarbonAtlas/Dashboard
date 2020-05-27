package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;

import java.util.HashSet;


public class EquilibratorPanel extends GenericSamplerPanel {

    interface EquilibratorPanelUiBinder extends UiBinder<ScrollPanel,EquilibratorPanel> {
    }

    private static final EquilibratorPanelUiBinder uiBinder = GWT.create(EquilibratorPanelUiBinder.class);

    /**
     * Creates a FlowPanel associated with the given equilibrator metadata
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param instr
     *         associate this panel with this equilibrator; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     * @param parentPanel
     *         parent TabPanel controlling this panel
     */
    public EquilibratorPanel(Equilibrator instr, HTML header, InstrumentsTabPanel parentPanel) {
        super(instr, header, parentPanel);

        // TODO: Create the provided widgets added by this panel
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        Equilibrator equilibrator = (Equilibrator) instr;

        // TODO: Assign the values in the text fields added in this panel

        // TODO: Add the handlers for widgets added by this panel (UiHandler not seen in subclasses)

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((Equilibrator) instr).invalidFieldNames();

        // TODO: Appropriately mark the labels of fields added in this panel

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
