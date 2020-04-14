package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import gov.noaa.pmel.socatmetadata.shared.core.Coverage;

public class CoveragePanel extends Composite {

    interface CoveragePanelUiBinder extends UiBinder<FlowPanel,CoveragePanel> {
    }

    private static CoveragePanelUiBinder uiBinder = GWT.create(CoveragePanelUiBinder.class);

    private Coverage coverage;

    /**
     * Creates a FlowPanel associated with the given Coverage.
     *
     * @param coverage
     *         associate this panel with this Coverage; cannot be null
     */
    public CoveragePanel(Coverage coverage) {
        initWidget(uiBinder.createAndBindUi(this));

        this.coverage = coverage;
    }

    /**
     * @return the updated Coverage; never null
     */
    public Coverage getUpdatedCoverage() {
        return coverage;
    }
}
