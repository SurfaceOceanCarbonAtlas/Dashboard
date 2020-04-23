package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;

import java.util.ArrayList;
import java.util.HashSet;

public class InvestigatorPanel extends Composite {

    interface InvestigatorPanelUiBinder extends UiBinder<FlowPanel,InvestigatorPanel> {
    }

    private static final InvestigatorPanelUiBinder uiBinder = GWT.create(InvestigatorPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox firstNameValue;
    @UiField(provided = true)
    final LabeledTextBox middleInitValue;
    @UiField(provided = true)
    final LabeledTextBox lastNameValue;
    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledTextBox idTypeValue;
    @UiField(provided = true)
    final LabeledTextBox orgValue;
    @UiField(provided = true)
    final LabeledTextBox firstStreetValue;
    @UiField(provided = true)
    final LabeledTextBox secondStreetValue;
    @UiField(provided = true)
    final LabeledTextBox cityValue;
    @UiField(provided = true)
    final LabeledTextBox regionValue;
    @UiField(provided = true)
    final LabeledTextBox zipValue;
    @UiField(provided = true)
    final LabeledTextBox countryValue;
    @UiField(provided = true)
    final LabeledTextBox emailValue;
    @UiField(provided = true)
    final LabeledTextBox phoneValue;


    private final Investigator investigator;
    private TextBox header;

    /**
     * Creates a FlowPanel associated with the given Investigator.
     *
     * @param investigator
     *         associate this panel with this Investigator; cannot be null
     * @param header
     *         headerabel that should be updated when appropriate values change; cannot be null.
     */
    public InvestigatorPanel(Investigator investigator, TextBox header) {
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
        firstNameValue = new LabeledTextBox("First name:", "8em", "10em", null, null);
        middleInitValue = new LabeledTextBox("Middle initial(s):", "8em", "6em", null, null);
        lastNameValue = new LabeledTextBox("Last name:", "8em", "10em", null, null);
        //
        idTypeValue = new LabeledTextBox("ID type:", "8em", "21.5em", null, null);
        idValue = new LabeledTextBox("ID:", "4.5em", "21.5em", null, null);
        //
        orgValue = new LabeledTextBox("Organization:", "8em", "51.25em", null, null);
        //
        firstStreetValue = new LabeledTextBox("Street/Box:", "8em", "51.25em", null, null);
        //
        secondStreetValue = new LabeledTextBox("Street:", "8em", "51.25em", null, null);
        //
        cityValue = new LabeledTextBox("City:", "8em", "21.5em", null, null);
        regionValue = new LabeledTextBox("Region:", "4.5em", "21.5em", null, null);
        //
        zipValue = new LabeledTextBox("Postal code:", "8em", "21.5em", null, null);
        countryValue = new LabeledTextBox("Country:", "4.5em", "21.5em", null, null);
        //
        emailValue = new LabeledTextBox("E-mail:", "8em", "21.5em", null, null);
        phoneValue = new LabeledTextBox("Phone:", "4.5em", "21.5em", null, null);

        initWidget(uiBinder.createAndBindUi(this));

        this.investigator = investigator;
        this.header = null;

        // The following will assign the values in the text fields
        getUpdatedInvestigator();
        // The following will assign the HTML to the labels before the text fields
        markInvalids();
    }

    @UiHandler("firstNameValue")
    void firstNameValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setFirstName(firstNameValue.getText());
        if ( header != null )
            header.setValue(investigator.getReferenceName(), true);
        markInvalids();
    }

    @UiHandler("middleInitValue")
    void middleInitValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setMiddle(middleInitValue.getText());
        if ( header != null )
            header.setValue(investigator.getReferenceName(), true);
        markInvalids();
    }

    @UiHandler("lastNameValue")
    void lastNameValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setLastName(lastNameValue.getText());
        if ( header != null )
            header.setValue(investigator.getReferenceName(), true);
        markInvalids();
    }

    @UiHandler("idTypeValue")
    void idTypeValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setIdType(idTypeValue.getText());
        markInvalids();
    }

    @UiHandler("idValue")
    void idValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setId(idValue.getText());
        markInvalids();
    }

    @UiHandler("orgValue")
    void orgValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setOrganization(orgValue.getText());
        markInvalids();
    }

    @UiHandler("firstStreetValue")
    void firstStreetValueOnValueChange(ValueChangeEvent<String> event) {
        assignInvestigatorStreets();
        markInvalids();
    }

    @UiHandler("secondStreetValue")
    void secondStreetValueOnValueChange(ValueChangeEvent<String> event) {
        assignInvestigatorStreets();
        markInvalids();
    }

    @UiHandler("cityValue")
    void cityValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setCity(cityValue.getText());
        markInvalids();
    }

    @UiHandler("regionValue")
    void regionValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setRegion(regionValue.getText());
        markInvalids();
    }

    @UiHandler("zipValue")
    void zipValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setZipCode(zipValue.getText());
        markInvalids();
    }

    @UiHandler("countryValue")
    void countryValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setCountry(countryValue.getText());
        markInvalids();
    }

    @UiHandler("emailValue")
    void emailValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setEmail(emailValue.getText());
        markInvalids();
    }

    @UiHandler("phoneValue")
    void phoneValueOnValueChange(ValueChangeEvent<String> event) {
        investigator.setPhone(phoneValue.getText());
        markInvalids();
    }

    /**
     * Assigns the investigator.streets from the values in firstStreetValue and secondStreetValue
     */
    private void assignInvestigatorStreets() {
        String first = firstStreetValue.getText().trim();
        String second = secondStreetValue.getText();
        ArrayList<String> streets = new ArrayList<String>(2);
        if ( !first.isEmpty() )
            streets.add(first);
        if ( !second.isEmpty() )
            streets.add(second);
        investigator.setStreets(streets);
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = investigator.invalidFieldNames();

        if ( invalids.contains("firstName") )
            firstNameValue.markInvalid();
        else
            firstNameValue.markValid();

        if ( invalids.contains("middle") )
            middleInitValue.markInvalid();
        else
            middleInitValue.markValid();

        if ( invalids.contains("lastName") )
            lastNameValue.markInvalid();
        else
            lastNameValue.markValid();

        if ( invalids.contains("idType") )
            idTypeValue.markInvalid();
        else
            idTypeValue.markValid();

        if ( invalids.contains("id") )
            idValue.markInvalid();
        else
            idValue.markValid();

        if ( invalids.contains("organization") )
            orgValue.markInvalid();
        else
            orgValue.markValid();

        // Only mark the first street invalid (ie, not given);
        // the second street is optional and so it always valid (the default)
        if ( invalids.contains("streets") )
            firstStreetValue.markInvalid();
        else
            firstStreetValue.markValid();

        if ( invalids.contains("city") )
            cityValue.markInvalid();
        else
            cityValue.markValid();

        if ( invalids.contains("region") )
            regionValue.markInvalid();
        else
            regionValue.markValid();

        if ( invalids.contains("zipCode") )
            zipValue.markInvalid();
        else
            zipValue.markValid();

        if ( invalids.contains("country") )
            countryValue.markInvalid();
        else
            countryValue.markValid();

        if ( invalids.contains("email") )
            emailValue.markInvalid();
        else
            emailValue.markValid();

        if ( invalids.contains("phone") )
            phoneValue.markInvalid();
        else
            phoneValue.markValid();
    }

    /**
     * @return the updated Investigator; never null
     */
    public Investigator getUpdatedInvestigator() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        firstNameValue.setText(investigator.getFirstName());
        middleInitValue.setText(investigator.getMiddle());
        lastNameValue.setText(investigator.getLastName());
        idValue.setText(investigator.getId());
        idTypeValue.setText(investigator.getIdType());
        orgValue.setText(investigator.getOrganization());
        ArrayList<String> streets = investigator.getStreets();
        switch ( streets.size() ) {
            case 0:
                firstStreetValue.setText("");
                secondStreetValue.setText("");
                break;
            case 1:
                firstStreetValue.setText(streets.get(0));
                secondStreetValue.setText("");
                break;
            case 2:
                firstStreetValue.setText(streets.get(0));
                secondStreetValue.setText(streets.get(1));
                break;
            default:
                // Hack for now but probably good enough
                firstStreetValue.setText(streets.get(0));
                String rest = streets.get(1);
                for (int k = 2; k < streets.size(); k++) {
                    rest += "; " + streets.get(k);
                }
                secondStreetValue.setText(rest);
        }
        cityValue.setText(investigator.getCity());
        regionValue.setText(investigator.getRegion());
        zipValue.setText(investigator.getZipCode());
        countryValue.setText(investigator.getCountry());
        emailValue.setText(investigator.getEmail());
        phoneValue.setText(investigator.getPhone());

        if ( header != null )
            header.setValue(investigator.getReferenceName(), true);

        return investigator;
    }

}