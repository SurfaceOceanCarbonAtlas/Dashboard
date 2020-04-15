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
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

    @UiField
    Label nameLabel;
    @UiField
    TextBox nameBox;

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

        nameLabel.setText("Name:");
        nameBox.setText(platform.getPlatformName());
    }

    @UiHandler("nameBox")
    void nameBoxOnValueChange(ValueChangeEvent<String> event) {
        platform.setPlatformName(nameBox.getText());
    }

    /**
     * @return the updated Platform; never null
     */
    public Platform getUpdatedPlatform() {
        return platform;
    }

}
