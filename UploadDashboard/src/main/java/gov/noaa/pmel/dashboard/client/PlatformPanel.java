package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;

import java.util.ArrayList;
import java.util.HashSet;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static final PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledTextBox nameValue;
    @UiField(provided = true)
    final LabeledListBox typeList;
    @UiField(provided = true)
    final LabeledTextBox ownerValue;
    @UiField(provided = true)
    final LabeledTextBox countryValue;

    private final Platform platform;
    private final ArrayList<PlatformType> platformTypeList;

    /**
     * Creates a FlowPanel associated with the given Platform.
     *
     * @param platform
     *         associate this panel with this Platform; cannot be null
     */
    public PlatformPanel(Platform platform) {
        idValue = new LabeledTextBox("NODC code or other ID:", "12em", "15em", null, null);
        nameValue = new LabeledTextBox("Name of plaform:", "12em", "15em", null, null);
        typeList = new LabeledListBox("Type of platform:", "12em", null, null, null);
        ownerValue = new LabeledTextBox("Owner of platform:", "12em", "15em", null, null);
        countryValue = new LabeledTextBox("Country of registration:", "12em", "15em", null, null);

        initWidget(uiBinder.createAndBindUi(this));

        this.platform = platform;

        // Create a list of platform types and add the corresponding nice-looking type names to typeList
        platformTypeList = new ArrayList<PlatformType>();
        for (PlatformType type : PlatformType.values()) {
            if ( type != PlatformType.UNKNOWN ) {
                platformTypeList.add(type);
                typeList.addItem(type.toString());
            }
        }

        // Make sure there is an appropriate platform type assigned
        if ( platform.getPlatformType() == PlatformType.UNKNOWN ) {
            // Guess the type from the name and NODC code of the expocode; returns SHIP if cannot determine
            PlatformType type = PlatformType.parse(DashboardUtils.guessPlatformType(
                    platform.getPlatformId() + "ZZZZ", platform.getPlatformName()));
            // Should not return UNKNOWN, but just in case...
            if ( type == PlatformType.UNKNOWN )
                type = PlatformType.SHIP;
            platform.setPlatformType(type);
        }

        // The following will assign the values in the text fields
        getUpdatedPlatform();
        // The following will assign the HTML to the labels before the text fields
        markInvalids();
    }

    @UiHandler("idValue")
    void idValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformId(idValue.getText());
        markInvalids();
    }

    @UiHandler("nameValue")
    void nameValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformName(nameValue.getText());
        markInvalids();
    }

    @UiHandler("typeList")
    void typeListOnChange(ChangeEvent event) {
        int idx = typeList.getSelectedIndex();
        if ( idx >= 0 )
            platform.setPlatformType(platformTypeList.get(idx));
        markInvalids();
    }

    @UiHandler("ownerValue")
    void ownerValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformOwner(ownerValue.getText());
        markInvalids();
    }

    @UiHandler("countryValue")
    void countryValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformCountry(countryValue.getText());
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = platform.invalidFieldNames();

        if ( invalids.contains("platformId") )
            idValue.markInvalid();
        else
            idValue.markValid();

        if ( invalids.contains("platformName") )
            nameValue.markInvalid();
        else
            nameValue.markValid();

        if ( invalids.contains("platformType") )
            typeList.markInvalid();
        else
            typeList.markValid();

        if ( invalids.contains("owner") )
            ownerValue.markInvalid();
        else
            ownerValue.markValid();

        if ( invalids.contains("country") )
            countryValue.markInvalid();
        else
            countryValue.markValid();
    }

    /**
     * @return the updated Platform; never null
     */
    public Platform getUpdatedPlatform() {
        // In case erroneous input leaves mismatches,
        // first update the displayed content in case this is from a save-and-continue
        idValue.setText(platform.getPlatformId());
        nameValue.setText(platform.getPlatformName());
        int idx = platformTypeList.indexOf(platform.getPlatformType());
        if ( idx >= 0 )
            typeList.setSelectedIndex(idx);
        ownerValue.setText(platform.getPlatformOwner());
        countryValue.setText(platform.getPlatformCountry());

        return platform;
    }

}
