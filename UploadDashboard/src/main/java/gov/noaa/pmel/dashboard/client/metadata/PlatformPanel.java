package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;

import java.util.ArrayList;
import java.util.HashSet;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static final PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

    @UiField
    Label headerLabel;
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
     * @param dataset
     *         the dataset associated with this metadata
     * @param platform
     *         associate this panel with this Platform; cannot be null
     */
    public PlatformPanel(DashboardDataset dataset, Platform platform) {
        idValue = new LabeledTextBox("Platform ID:", "11em", "20em", null, null);
        nameValue = new LabeledTextBox("Name of platform:", "11em", "20em", null, null);
        typeList = new LabeledListBox("Type of platform:", "11em", "20.5em", null, null);
        ownerValue = new LabeledTextBox("Owner of platform:", "11em", "20em", null, null);
        countryValue = new LabeledTextBox("Country of registration:", "11em", "20em", null, null);

        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.PLATFORM_TAB_TEXT + " for " + dataset.getDatasetId());

        this.platform = platform;

        // Create a list of platform types and add the corresponding nice-looking type names to typeList
        platformTypeList = new ArrayList<PlatformType>();
        for (PlatformType type : PlatformType.values()) {
            platformTypeList.add(type);
            typeList.addItem(type.toString());
        }

        // The following will assign the values in the labels and text fields
        getUpdatedPlatform();
    }

    @UiHandler("idValue")
    void idValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformId(idValue.getText());
        // If the platform type is unknown, see if it can guess the type
        possiblyUpdatePlatformType();
        markInvalids();
    }

    @UiHandler("nameValue")
    void nameValueOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformName(nameValue.getText());
        // If the platform type is unknown, see if it can guess the type
        possiblyUpdatePlatformType();
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
     * If the platform type is unknown, but there is a NODC code (and maybe also a platform name), guess the type
     */
    private void possiblyUpdatePlatformType() {
        String platformId = platform.getPlatformId();
        if ( (platform.getPlatformType() == PlatformType.UNKNOWN) && (platformId.length() >= 4) ) {
            String platformName = platform.getPlatformName();
            PlatformType type = PlatformType.parse(DashboardUtils.guessPlatformType(platformId, platformName));
            platform.setPlatformType(type);
            int idx = platformTypeList.indexOf(type);
            if ( idx >= 0 )
                typeList.setSelectedIndex(idx);
        }
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

        // If the platform type is unknown, see if it can guess the type
        possiblyUpdatePlatformType();

        markInvalids();

        return platform;
    }

}
