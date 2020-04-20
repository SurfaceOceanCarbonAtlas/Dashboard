package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;

import java.util.ArrayList;
import java.util.HashSet;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

    @UiField
    HTML idHtml;
    @UiField
    TextBox idValue;
    @UiField
    HTML nameHtml;
    @UiField
    TextBox nameValue;
    @UiField
    HTML typeHtml;
    @UiField
    ListBox typeList;
    @UiField
    HTML ownerHtml;
    @UiField
    TextBox ownerValue;
    @UiField
    HTML countryHtml;
    @UiField
    TextBox countryValue;

    private static final String PLATFORM_ID_HTML = "NODC code or other ID:";
    private static final String PLATFORM_NAME_HTML = "Name of plaform:";
    private static final String PLATFORM_TYPE_HTML = "Type of platform:";
    private static final String OWNER_HTML = "Owner of platform:";
    private static final String COUNTRY_HTML = "Country of registration:";

    private static final String INVALID_HTML_PREFIX = "<span style='color:red; font-weight:bold; font-style:oblique'>";
    private static final String INVALID_HTML_SUFFIX = "</span>";

    private Platform platform;
    private ArrayList<PlatformType> platformTypeList;

    /**
     * Creates a FlowPanel associated with the given Platform.
     *
     * @param platform
     *         associate this panel with this Platform; cannot be null
     */
    public PlatformPanel(Platform platform) {
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
            idHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + PLATFORM_ID_HTML + INVALID_HTML_SUFFIX));
        else
            idHtml.setHTML(SafeHtmlUtils.fromSafeConstant(PLATFORM_ID_HTML));

        if ( invalids.contains("platformName") )
            nameHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + PLATFORM_NAME_HTML + INVALID_HTML_SUFFIX));
        else
            nameHtml.setHTML(SafeHtmlUtils.fromSafeConstant(PLATFORM_NAME_HTML));

        if ( invalids.contains("platformType") )
            typeHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + PLATFORM_TYPE_HTML + INVALID_HTML_SUFFIX));
        else
            typeHtml.setHTML(SafeHtmlUtils.fromSafeConstant(PLATFORM_TYPE_HTML));

        if ( invalids.contains("owner") )
            ownerHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + OWNER_HTML + INVALID_HTML_SUFFIX));
        else
            ownerHtml.setHTML(SafeHtmlUtils.fromSafeConstant(OWNER_HTML));

        if ( invalids.contains("country") )
            countryHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
                    INVALID_HTML_PREFIX + COUNTRY_HTML + INVALID_HTML_SUFFIX));
        else
            countryHtml.setHTML(SafeHtmlUtils.fromSafeConstant(COUNTRY_HTML));
    }

    /**
     * @return the updated Platform; never null
     */
    public Platform getUpdatedPlatform() {
        // Because erroneous input can leave mismatches,
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
