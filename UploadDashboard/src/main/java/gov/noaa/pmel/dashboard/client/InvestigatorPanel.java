package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;

import java.util.HashSet;

public class InvestigatorPanel extends Composite {

    interface InvestigatorPanelUiBinder extends UiBinder<FlowPanel,InvestigatorPanel> {
    }

    private static InvestigatorPanelUiBinder uiBinder = GWT.create(InvestigatorPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox firstValue;
    @UiField(provided = true)
    final LabeledTextBox middleValue;
    @UiField(provided = true)
    final LabeledTextBox lastValue;
    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledTextBox idTypeValue;
    @UiField(provided = true)
    final LabeledTextBox orgValue;

    private Investigator investigator;
    private Label header;

    /**
     * Creates a FlowPanel associated with the given Investigator.
     *
     * @param investigator
     *         associate this panel with this Investigator; cannot be null
     * @param header
     *         header label that should be updated when appropriate values change; cannot be null.
     */
    public InvestigatorPanel(Investigator investigator, Label header) {
        this(investigator);

        this.header = header;
        header.setText(investigator.getReferenceName());
    }

    /**
     * Creates a FlowPanel associated with the given Investigator
     * but without an associated header label.
     *
     * @param investigator
     *         associate this panel with this Investigator; cannot be null
     */
    protected InvestigatorPanel(Investigator investigator) {
        firstValue = new LabeledTextBox("First name:", "10em", "12em", null, null);
        middleValue = new LabeledTextBox("Middle initial(s):", "10em", "8em", null, null);
        lastValue = new LabeledTextBox("Last name:", "10em", "12em", null, null);
        idValue = new LabeledTextBox("ID:", "10em", "26em", null, null);
        idTypeValue = new LabeledTextBox("ID type:", "6em", "26em", null, null);
        orgValue = new LabeledTextBox("Organization:", "10em", "62.25em", null, null);
        initWidget(uiBinder.createAndBindUi(this));

        this.investigator = investigator;
        this.header = null;

        // The following will assign the values in the text fields
        getUpdatedInvestigator();
        // The following will assign the HTML to the labels before the text fields
        markInvalids();
    }

    @UiHandler("firstValue")
    void firstValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setFirstName(firstValue.getText());
        if ( header != null )
            header.setText(investigator.getReferenceName());
    }

    @UiHandler("middleValue")
    void middleValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setMiddle(middleValue.getText());
        if ( header != null )
            header.setText(investigator.getReferenceName());
    }

    @UiHandler("lastValue")
    void lastValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setLastName(lastValue.getText());
        if ( header != null )
            header.setText(investigator.getReferenceName());
    }

    @UiHandler("idValue")
    void idValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setId(idValue.getText());
    }

    @UiHandler("idTypeValue")
    void idTypeValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setIdType(idTypeValue.getText());
    }

    @UiHandler("orgValue")
    void organizationValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setOrganization(orgValue.getText());
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = investigator.invalidFieldNames();

        if ( invalids.contains("firstName") )
            firstValue.markInvalid();
        else
            firstValue.markValid();

        if ( invalids.contains("middle") )
            middleValue.markInvalid();
        else
            middleValue.markValid();

        if ( invalids.contains("lastName") )
            lastValue.markInvalid();
        else
            lastValue.markValid();

        if ( invalids.contains("id") )
            idValue.markInvalid();
        else
            idValue.markValid();

        if ( invalids.contains("idType") )
            idTypeValue.markInvalid();
        else
            idTypeValue.markValid();

        if ( invalids.contains("organization") )
            orgValue.markInvalid();
        else
            orgValue.markValid();
    }

    /**
     * @return the updated Investigator; never null
     */
    public Investigator getUpdatedInvestigator() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        firstValue.setText(investigator.getFirstName());
        middleValue.setText(investigator.getMiddle());
        lastValue.setText(investigator.getLastName());
        idValue.setText(investigator.getId());
        idTypeValue.setText(investigator.getIdType());
        orgValue.setText(investigator.getOrganization());

        return investigator;
    }

}