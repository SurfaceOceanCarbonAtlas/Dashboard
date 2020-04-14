package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.noaa.pmel.socatmetadata.shared.platform.Platform;

public class PlatformPanel extends Composite {

    interface PlatformPanelUiBinder extends UiBinder<FlowPanel,PlatformPanel> {
    }

    private static PlatformPanelUiBinder uiBinder = GWT.create(PlatformPanelUiBinder.class);

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
    }

    /**
     * @return the updated Platform; never null
     */
    public Platform getUpdatedPlatform() {
        return platform;
    }

}
