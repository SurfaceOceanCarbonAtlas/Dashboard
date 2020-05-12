package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.BioDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.InstDataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.ArrayList;
import java.util.Arrays;

public class VariablesTabPanel extends Composite {

    private static final ArrayList<String> varTypeListNames = new ArrayList<String>(Arrays.asList(
            "Air Pressure",
            "Aqueous Gas Conc.",
            "Atmospheric Gas Conc.",
            "Biological",
            "Temperature",
            "Other measured",
            "Other generic"
    ));
    private static final ArrayList<String> varTypeSimpleNames = new ArrayList<String>(Arrays.asList(
            new AirPressure().getSimpleName(),
            new AquGasConc().getSimpleName(),
            new GasConc().getSimpleName(),
            new BioDataVar().getSimpleName(),
            new Temperature().getSimpleName(),
            new InstDataVar().getSimpleName(),
            new Variable().getSimpleName()
    ));

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

        headerLabel.setText(EditSocatMetadataPage.VARIABLES_TAB_TEXT + " (variables) for " + dataset.getDatasetId());
        variablePanels = new ArrayList<VariablePanel>(variables.size());
        // Add a panel for each variable
        for (int k = 0; k < variables.size(); k++)
            replacePanel(k, variables.get(k));
        addButton.setText("Append another");
        addButton.setTitle("Adds a new variable description to the end of the list");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed variable description");
    }

    /**
     * Generate a VariablePanel for the given variable with the given header. If the index specified
     * is the current number of panels, the panel is added to the end of the list of variable panels;
     * otherwise, the panel replaces the current panel at the given index.
     *
     * @param index
     *         index of the panel to be replaced; if the size of the current list,
     *         append the panel instead of replacing a panel
     * @param vari
     *         variable to associate with this panel
     */
    private void replacePanel(int index, Variable vari) {
        // Allow the addition of a panel; otherwise it must replace an existing panel
        if ( (index < 0) || (index > variablePanels.size()) ) {
            UploadDashboard.showMessage("Unexpected invalid replacement panel index of " + index);
            return;
        }
        VariablePanel panel;
        String simpleName = vari.getSimpleName();
        HTML header = new HTML();
        switch ( simpleName ) {
            case "AirPressure":
                panel = new AirPressureVarPanel((AirPressure) vari, header, this);
                break;
            case "AquGasConc":
                panel = new AquGasConcVarPanel((AquGasConc) vari, header, this);
                break;
            case "GasConc":
                panel = new GasConcVarPanel((GasConc) vari, header, this);
                break;
            case "BioDataVar":
                panel = new BioDataVarPanel((BioDataVar) vari, header, this);
                break;
            case "Temperature":
                panel = new TemperatureVarPanel((Temperature) vari, header, this);
                break;
            case "InstDataVar":
                panel = new DataVarPanel((InstDataVar) vari, header, this);
                break;
            case "Variable":
                panel = new GenericVarPanel(vari, header, this);
                break;
            default:
                UploadDashboard.showMessage("Unexpect variable type of " + SafeHtmlUtils.htmlEscape(simpleName));
                return;
        }
        if ( index == variablePanels.size() ) {
            variablePanels.add(panel);
            mainPanel.add(panel, header);
        }
        else {
            variablePanels.set(index, panel);
            mainPanel.remove(index);
            mainPanel.insert(panel, header, index);
        }

    }

    /**
     * Initialized the type list for a VariablePanel instance.  Assumes the type list has not yet been
     * initialized.  Appropriate values are assigned to the variable type list and selects the appropriate
     * value for the type of variable given.  Also adds the callback to the type list to change to the
     * appropriate panel for a newly selected variable type.
     */
    public void assignVariableTypeList(LabeledListBox typeList, Variable vari, VariablePanel panel) {
        for (String name : varTypeListNames)
            typeList.addItem(name);
        int k = varTypeSimpleNames.indexOf(vari.getSimpleName());
        if ( k < 0 ) {
            UploadDashboard.showMessage("Unexpected variable type of " +
                    SafeHtmlUtils.htmlEscape(vari.getSimpleName()));
        }
        typeList.setSelectedIndex(k);
        typeList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                changeVariableType(variablePanels.indexOf(panel), typeList.getSelectedIndex());
            }
        });
    }

    /**
     * Change the variable and associated panel to the selected type.
     *
     * @param varIdx index of the variable and associate panel to change
     * @param typeIdx index (in the varType lists) of the variable type to change to
     */
    private void changeVariableType(int varIdx, int typeIdx) {
        if ( varIdx < 0 ) {
            UploadDashboard.showMessage("Unexpected unknown variable panel to replace");
            return;
        }
        if ( typeIdx < 0 ) {
            UploadDashboard.showMessage("No variable type selected");
            return;
        }
        Variable oldVar = variablePanels.get(varIdx).getUpdatedVariable();
        Variable vari;
        String simpleName = varTypeSimpleNames.get(typeIdx);
        switch ( simpleName ) {
            case "AirPressure":
                vari = new AirPressure(oldVar);
                break;
            case "AquGasConc":
                vari = new AquGasConc(oldVar);
                break;
            case "GasConc":
                vari = new GasConc(oldVar);
                break;
            case "BioDataVar":
                vari = new BioDataVar(oldVar);
                break;
            case "Temperature":
                vari = new Temperature(oldVar);
                break;
            case "InstDataVar":
                vari = new InstDataVar(oldVar);
                break;
            case "Variable":
                vari = new Variable(oldVar);
                break;
            default:
                UploadDashboard.showMessage("Unexpect variable type of " + SafeHtmlUtils.htmlEscape(simpleName));
                return;
        }
        replacePanel(varIdx, vari);
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
        replacePanel(numPanels, vari);
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
