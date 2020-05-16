package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

import java.util.HashSet;

public class InstrumentPanel extends Composite {

    interface InstrumentPanelUiBinder extends UiBinder<FlowPanel,InstrumentPanel> {
    }

    private static InstrumentPanelUiBinder uiBinder = GWT.create(InstrumentPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledTextBox nameValue;

    private Instrument inst;
    private HTML header;

    /**
     * Creates a FlowPanel associated with the given Instrument.
     *
     * @param inst
     *         associate this panel with this Instrument; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public InstrumentPanel(Instrument inst, HTML header) {
        nameValue = new LabeledTextBox("Name:", "7em", "20em", null, null);
        idValue = new LabeledTextBox("ID:", "7em", "20em", null, null);

        initWidget(uiBinder.createAndBindUi(this));

        this.inst = inst;
        this.header = header;

        // The following will assign the values in the labels and text fields
        getUpdatedInstrument();
    }

    @UiHandler("nameValue")
    void nameValueOnValueChange(ValueChangeEvent<String> event) {
        inst.setName(nameValue.getText());
        markInvalids();
    }

    @UiHandler("idValue")
    void idValueOnValueChange(ValueChangeEvent<String> event) {
        inst.setId(idValue.getText());
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = inst.invalidFieldNames();

        String oldVal = header.getHTML();
        SafeHtml val = SafeHtmlUtils.fromString(inst.getReferenceName());
        if ( !invalids.isEmpty() )
            val = UploadDashboard.invalidLabelHtml(val);
        if ( !val.asString().equals(oldVal) )
            header.setHTML(val);

        if ( invalids.contains("name") )
            nameValue.markInvalid();
        else
            nameValue.markValid();

        if ( invalids.contains("id") )
            idValue.markInvalid();
        else
            idValue.markValid();
    }

    /**
     * @return the updated Instrument; never null
     */
    public Instrument getUpdatedInstrument() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        nameValue.setText(inst.getName());
        idValue.setText(inst.getId());

        markInvalids();

        return inst;
    }

}
