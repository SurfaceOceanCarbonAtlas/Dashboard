package gov.noaa.pmel.dashboard.client.metadata.pipanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import gov.noaa.pmel.dashboard.client.metadata.EditSocatMetadataPage;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.person.Submitter;

public class SubmitterPanel extends Composite {

    interface SubmitterPanelUiBinder extends UiBinder<FlowPanel,SubmitterPanel> {
    }

    private static final SubmitterPanelUiBinder uiBinder = GWT.create(SubmitterPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField(provided = true)
    final InvestigatorPanel subPanel;

    /**
     * Creates a FlowPanel associated with the given Submitter
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param submitter
     *         associate this panel with this Submitter; cannot be null
     */
    public SubmitterPanel(DashboardDataset dataset, Submitter submitter) {
        subPanel = new InvestigatorPanel(submitter, null);

        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.SUBMITTER_TAB_TEXT + " for " + dataset.getDatasetId());
    }

    // Since there are no additional fields in Submitter, there is not much to add here.

    /**
     * @return the updated Submitter; never null
     */
    public Submitter getUpdatedSumitter() {
        return (Submitter) (subPanel.getUpdatedInvestigator());
    }

}

