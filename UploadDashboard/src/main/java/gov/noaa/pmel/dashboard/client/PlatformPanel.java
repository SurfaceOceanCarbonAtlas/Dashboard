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
import com.google.gwt.user.client.ui.ValuePicker;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;
import gov.noaa.pmel.socatmetadata.shared.platform.PlatformType;

import java.util.ArrayList;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

    @UiField
    Label idLabel;
    @UiField
    TextBox idBox;
    @UiField
    Label nameLabel;
    @UiField
    TextBox nameBox;
    @UiField
    Label typeLabel;
    @UiField
    ValuePicker<String> typeBox;
    @UiField
    Label ownerLabel;
    @UiField
    TextBox ownerBox;
    @UiField
    Label countryLabel;
    @UiField
    TextBox countryBox;

    private Platform platform;

    /**
     * Creates a FlowPanel associated with the given Platform.
     *
     * @param platform
     *         associate this panel with this Platform; cannot be null
     */
    public PlatformPanel(Platform platform) {
        initWidget(uiBinder.createAndBindUi(this));

        this.platform = platform;

        // If ID is empty, copy it from name; if name is empty, copy it from ID
        String id = platform.getPlatformId();
        String name = platform.getPlatformName();
        if ( id.isEmpty() )
            id = name;
        if ( name.isEmpty() )
            name = id;
        idLabel.setText("Unique name:");
        idBox.setText(id);
        nameLabel.setText("Common name:");
        nameBox.setText(name);

        typeLabel.setText("Platform type:");
        ArrayList<String> typeList = new ArrayList<String>();
        for (PlatformType type : PlatformType.values()) {
            if ( type != PlatformType.UNKNOWN )
                typeList.add(type.toString());
        }
        typeBox.setAcceptableValues(typeList);
        PlatformType selected = platform.getPlatformType();
        if ( selected == PlatformType.UNKNOWN )
            selected = PlatformType.SHIP;
        typeBox.setValue(selected.toString());

        ownerLabel.setText("Owner:");
        ownerBox.setText(platform.getPlatformOwner());

        countryLabel.setText("Country:");
        countryBox.setText(platform.getPlatformCountry());
    }

    @UiHandler("idBox")
    void idBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformId(idBox.getText());
    }

    @UiHandler("nameBox")
    void nameBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformName(nameBox.getText());
    }

    @UiHandler("typeBox")
    void typeBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformType(PlatformType.parse(typeBox.getValue()));
    }

    @UiHandler("ownerBox")
    void ownerBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformOwner(ownerBox.getText());
    }

    @UiHandler("countryBox")
    void countryBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformCountry(countryBox.getText());
    }

    /**
     * @return the updated Platform; never null
     */
    public Platform getUpdatedPlatform() {
        return platform;
    }

}
