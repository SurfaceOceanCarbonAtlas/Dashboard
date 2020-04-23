package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

public class InstrumentPanel extends Composite {

    interface InstrumentPanelUiBinder extends UiBinder<FlowPanel,InstrumentPanel> {
    }

    private static InstrumentPanelUiBinder uiBinder = GWT.create(InstrumentPanelUiBinder.class);

    @UiField
    Label nameLabel;
    @UiField
    TextBox nameBox;

    private Instrument inst;
    private TextBox header;

    /**
     * Creates a FlowPanel associated with the given Instrument.
     *
     * @param inst
     *         associate this panel with this Instrument; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public InstrumentPanel(Instrument inst, TextBox header) {
        initWidget(uiBinder.createAndBindUi(this));

        this.inst = inst;
        this.header = header;

        nameLabel.setText("Name:");
        String name = inst.getName();
        nameBox.setText(name);
        if ( name.isEmpty() )
            name = "Unknown";
        header.setText(name);
    }

    @UiHandler("nameBox")
    void nameBoxOnValueChange(ValueChangeEvent<String> event) {
        String name = nameBox.getText();
        inst.setName(name);
        if ( name.isEmpty() )
            name = "Unknown";
        header.setValue(name, true);
    }

    /**
     * @return the updated Instrument; never null
     */
    public Instrument getUpdatedInstrument() {
        return inst;
    }

}
