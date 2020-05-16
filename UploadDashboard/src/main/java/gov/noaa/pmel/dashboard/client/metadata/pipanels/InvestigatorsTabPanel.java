package gov.noaa.pmel.dashboard.client.metadata.pipanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.dashboard.client.metadata.EditSocatMetadataPage;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;

import java.util.ArrayList;

public class InvestigatorsTabPanel extends Composite {
    interface InvestigatorsTabPanelUiBinder extends UiBinder<ScrollPanel,InvestigatorsTabPanel> {
    }

    private static InvestigatorsTabPanelUiBinder uiBinder = GWT.create(InvestigatorsTabPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField
    TabLayoutPanel mainPanel;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    ArrayList<InvestigatorPanel> investigatorPanels;

    /**
     * Creates a {@link TabLayoutPanel} with add and remove buttons underneath.
     * The tab panel contains {@link InvestigatorPanel} panels for each investigator.
     * The add button will append an new InvestigatorPanel (and thus, a new Investigator) and display it.
     * The remove button will remove the currently selected InvestigatorPanel (and its associated Investigator)
     * and show the next InvestigatorPanel, or the last InvestigatorPanel if there is no next InvestigatorPanel.
     * <p>
     * A call to {@link #showPanel(int)} will need to be made to show a panel.
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param investigators
     *         the initial list of investigators to show
     */
    public InvestigatorsTabPanel(DashboardDataset dataset, ArrayList<Investigator> investigators) {
        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.INVESTIGATOR_TAB_TEXT + " for " + dataset.getDatasetId());
        investigatorPanels = new ArrayList<InvestigatorPanel>(investigators.size());
        for (Investigator pi : investigators) {
            HTML header = new HTML();
            InvestigatorPanel panel = new InvestigatorPanel(pi, header);
            investigatorPanels.add(panel);
            mainPanel.add(panel, header);
        }
        addButton.setText("Append another");
        addButton.setTitle("Adds a new principal investigator description to the end of the list");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed principal investigator description");
    }

    /**
     * @param index
     *         show the InvestigatorPanel at this index; does nothing if invalid
     */
    public void showPanel(int index) {
        if ( (index < 0) || (index >= investigatorPanels.size()) )
            return;
        mainPanel.selectTab(index, true);
    }

    /**
     * @return the list of updated investigators
     */
    public ArrayList<Investigator> getUpdatedInvestigators() {
        ArrayList<Investigator> investigators = new ArrayList<Investigator>(investigatorPanels.size());
        for (InvestigatorPanel panel : investigatorPanels) {
            investigators.add(panel.getUpdatedInvestigator());
        }
        return investigators;
    }

    @UiHandler("addButton")
    void addButtonOnClick(ClickEvent event) {
        int numPanels = investigatorPanels.size();
        Investigator pi;
        int index = mainPanel.getSelectedIndex();
        if ( (index >= 0) && (index < numPanels) ) {
            // make a copy of the currently selected Investigator
            pi = investigatorPanels.get(index).getUpdatedInvestigator();
            pi = (Investigator) (pi.duplicate(null));
            // erase data that must be specific for this investigator
            pi.setFirstName(null);
            pi.setMiddle(null);
            pi.setLastName(null);
            pi.setId(null);
            pi.setEmail(null);
            pi.setPhone(null);
        }
        else {
            // Should never happen
            pi = new Investigator();
        }
        HTML header = new HTML();
        InvestigatorPanel panel = new InvestigatorPanel(pi, header);
        investigatorPanels.add(panel);
        mainPanel.add(panel, header);
        mainPanel.selectTab(numPanels, true);
    }

    @UiHandler("removeButton")
    void removeButtonOnClick(ClickEvent event) {
        int numPanels = investigatorPanels.size();
        if ( numPanels < 2 ) {
            UploadDashboard.showMessage("There must be at least one investigator");
            return;
        }
        int index = mainPanel.getSelectedIndex();
        if ( (index < 0) || (index >= numPanels) )
            return;
        investigatorPanels.remove(index);
        mainPanel.remove(index);
        numPanels--;
        if ( index == numPanels )
            index--;
        mainPanel.selectTab(index, true);
    }

}
