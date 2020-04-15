package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.noaa.pmel.socatmetadata.shared.core.MiscInfo;

public class MiscInfoPanel extends Composite {

    interface MiscInfoPanelUiBinder extends UiBinder<FlowPanel,MiscInfoPanel> {
    }

    private static MiscInfoPanelUiBinder uiBinder = GWT.create(MiscInfoPanelUiBinder.class);

    private MiscInfo info;

    /**
     * Creates a FlowPanel associated with the given MiscInfo.
     *
     * @param info
     *         associate this panel with this MiscInfo; cannot be null
     */
    public MiscInfoPanel(MiscInfo info) {
        initWidget(uiBinder.createAndBindUi(this));

        this.info = info;
    }

    /**
     * @return the updated MiscInfo; never null
     */
    public MiscInfo getUpdatedMiscInfo() {
        return info;
    }

}
