package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

public class InstrumentPanel extends Composite {

    interface InstrumentPanelUiBinder extends UiBinder<FlowPanel,InstrumentPanel> {
    }

    private static InstrumentPanelUiBinder uiBinder = GWT.create(InstrumentPanelUiBinder.class);

    private Instrument inst;
    private Label header;

    /**
     * Creates a FlowPanel associated with the given Instrument.
     *
     * @param inst
     *         associate this panel with this Instrument; cannot be null
     * @param header
     *         header label that should be updated when appropriate values change; cannot be null
     */
    public InstrumentPanel(Instrument inst, Label header) {
        initWidget(uiBinder.createAndBindUi(this));

        this.inst = inst;
        header.setText(inst.getName());
    }

    /**
     * @return the updated Instrument; never null
     */
    public Instrument getUpdatedInstrument() {
        return inst;
    }

}
