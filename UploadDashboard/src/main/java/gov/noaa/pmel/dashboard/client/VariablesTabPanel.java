package gov.noaa.pmel.dashboard.client;

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
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.ArrayList;

public class VariablesTabPanel extends Composite {
    interface VariablesTabPanelUiBinder extends UiBinder<ScrollPanel,VariablesTabPanel> {
    }

    private static VariablesTabPanelUiBinder uiBinder = GWT.create(VariablesTabPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField
    TabLayoutPanel mainPanel;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    ArrayList<VariablePanel> variablePanels;

    /**
     * Creates a {@link TabLayoutPanel} with add and remove buttons underneath.
     * The tab panel contains {@link VariablePanel} panels for each variable.
     * The add button will append an new VariablePanel (and thus, a new Variable) and display it.
     * The remove button will remove the currently selected VariablePanel (and its associated Variable)
     * and show the next VariablePanel, or the last VariablePanel if there is no next VariablePanel.
     * <p>
     * A call to {@link #showPanel(int)} will need to be made to show a panel.
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param variables
     *         the initial list of variables to show
     */
    public VariablesTabPanel(DashboardDataset dataset, ArrayList<Variable> variables) {
        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.VARIABLES_TAB_TEXT + " for " + dataset.getDatasetId());
        variablePanels = new ArrayList<VariablePanel>(variables.size());
        for (Variable vari : variables) {
            HTML header = new HTML();
            VariablePanel panel = new VariablePanel(vari, header);
            variablePanels.add(panel);
            mainPanel.add(panel, header);
        }
        addButton.setText("Append another");
        addButton.setTitle("Adds a new variable description to the end of the list");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed variable description");
    }

    /**
     * @param index
     *         show the VariablePanel at this index; does nothing if invalid
     */
    public void showPanel(int index) {
        if ( (index < 0) || (index >= variablePanels.size()) )
            return;
        mainPanel.selectTab(index, true);
    }

    /**
     * @return the list of updated variables
     */
    public ArrayList<Variable> getUpdatedVariables() {
        ArrayList<Variable> variables = new ArrayList<Variable>(variablePanels.size());
        for (VariablePanel panel : variablePanels) {
            variables.add(panel.getUpdatedVariable());
        }
        return variables;
    }

    @UiHandler("addButton")
    void addButtonOnClick(ClickEvent event) {
        int numPanels = variablePanels.size();
        Variable vari;
        int index = mainPanel.getSelectedIndex();
        if ( (index >= 0) && (index < numPanels) ) {
            // make a copy of the currently selected Variable
            vari = variablePanels.get(index).getUpdatedVariable();
            vari = (Variable) (vari.duplicate(null));
            // erase data that must be specific for this variable
            vari.setColName(null);
            vari.setFullName(null);
        }
        else {
            // Should never happen
            vari = new Variable();
        }
        HTML header = new HTML();
        VariablePanel panel = new VariablePanel(vari, header);
        variablePanels.add(panel);
        mainPanel.add(panel, header);
        mainPanel.selectTab(numPanels, true);
    }

    @UiHandler("removeButton")
    void removeButtonOnClick(ClickEvent event) {
        int numPanels = variablePanels.size();
        if ( numPanels < 2 ) {
            UploadDashboard.showMessage("There must be at least one variable");
            return;
        }
        int index = mainPanel.getSelectedIndex();
        if ( (index < 0) || (index >= numPanels) )
            return;
        variablePanels.remove(index);
        mainPanel.remove(index);
        numPanels--;
        if ( index == numPanels )
            index--;
        mainPanel.selectTab(index, true);
    }

}
