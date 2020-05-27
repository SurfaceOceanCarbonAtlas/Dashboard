package gov.noaa.pmel.dashboard.client.metadata.instpanels;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.dashboard.client.metadata.EditSocatMetadataPage;
import gov.noaa.pmel.dashboard.client.metadata.LabeledListBox;
import gov.noaa.pmel.dashboard.client.metadata.varpanels.VariablePanel;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.instrument.Analyzer;
import gov.noaa.pmel.socatmetadata.shared.instrument.Equilibrator;
import gov.noaa.pmel.socatmetadata.shared.instrument.GasSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;
import gov.noaa.pmel.socatmetadata.shared.instrument.PressureSensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.SalinitySensor;
import gov.noaa.pmel.socatmetadata.shared.instrument.Sampler;
import gov.noaa.pmel.socatmetadata.shared.instrument.TemperatureSensor;

import java.util.ArrayList;
import java.util.Arrays;

public class InstrumentsTabPanel extends Composite {

    private static final ArrayList<String> instrTypeListNames = new ArrayList<String>(Arrays.asList(
            "Gas Sensor",
            "Pressure Sensor",
            "Salinity Sensor",
            "Temperature Sensor",
            "Generic Sensor",
            "Equilibrator",
            "Generic Sampler",
            "Generic Instrument"
    ));
    private static final ArrayList<String> instrTypeSimpleNames = new ArrayList<String>(Arrays.asList(
            new GasSensor().getSimpleName(),
            new PressureSensor().getSimpleName(),
            new SalinitySensor().getSimpleName(),
            new TemperatureSensor().getSimpleName(),
            new Analyzer().getSimpleName(),
            new Equilibrator().getSimpleName(),
            new Sampler().getSimpleName(),
            new Instrument().getSimpleName()
    ));

    interface InstrumentsTabPanelUiBinder extends UiBinder<FlowPanel,InstrumentsTabPanel> {
    }

    private static final InstrumentsTabPanelUiBinder uiBinder = GWT.create(InstrumentsTabPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField
    TabLayoutPanel mainPanel;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    ArrayList<InstrumentPanel> instrumentPanels;

    /**
     * Creates a {@link TabLayoutPanel} with add and remove buttons underneath.
     * The tab panel contains {@link InstrumentPanel} panels for each instrument.
     * The add button will add an new InstrumentPanel (and thus, a new Instrument) and display it.
     * The remove button will remove the currently selected InstrumentPanel (and its associated Instrument)
     * and show the next InstrumentPanel, or the last InstrumentPanel if there is no next InstrumentPanel.
     * <p>
     * A call to {@link #showPanel(int)} will need to be made to show a panel.
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param instruments
     *         the initial list of instruments to show
     */
    public InstrumentsTabPanel(DashboardDataset dataset, ArrayList<Instrument> instruments) {
        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.INSTRUMENTS_TAB_TEXT + " for " + dataset.getDatasetId());
        instrumentPanels = new ArrayList<InstrumentPanel>(instruments.size());
        // Add a panel for each instrument
        for (int k = 0; k < instruments.size(); k++) {
            addPanel(k, instruments.get(k));
        }
        addButton.setText("Add another");
        addButton.setTitle("Adds a new instrument description after the currently displayed one");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed instrument description");
    }

    /**
     * Initialized the type list for a InstrumentPanel instance.  Assumes the type list has not yet been
     * initialized.  Appropriate values are assigned to the instrument type list and selects the appropriate
     * value for the type of instrument given.  Also adds the callback to the type list to change to the
     * appropriate panel for a newly selected instrument type.
     */
    public void assignInstrumentTypeList(LabeledListBox typeList, Instrument instr, VariablePanel panel) {
        for (String name : instrTypeListNames) {
            typeList.addItem(name);
        }
        int k = instrTypeSimpleNames.indexOf(instr.getSimpleName());
        if ( k < 0 ) {
            UploadDashboard.showMessage("Unexpected instrument type of " +
                    SafeHtmlUtils.htmlEscape(instr.getSimpleName()));
        }
        typeList.setSelectedIndex(k);
        typeList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                changeInstrumentType(instrumentPanels.indexOf(panel), typeList.getSelectedIndex());
            }
        });
    }

    /**
     * @param index
     *         show the InstrumentPanel at this index; does nothing if invalid
     */
    public void showPanel(int index) {
        if ( (index < 0) || (index >= instrumentPanels.size()) )
            return;
        mainPanel.selectTab(index, true);
    }

    /**
     * @return the list of updated instruments
     */
    public ArrayList<Instrument> getUpdatedInstruments() {
        ArrayList<Instrument> instruments = new ArrayList<Instrument>(instrumentPanels.size());
        for (InstrumentPanel panel : instrumentPanels) {
            instruments.add(panel.getUpdatedInstrument());
        }
        return instruments;
    }

    @UiHandler("addButton")
    void addButtonOnClick(ClickEvent event) {
        Instrument instr;
        int index = mainPanel.getSelectedIndex();
        if ( (index >= 0) && (index < instrumentPanels.size()) ) {
            // make a copy of the currently selected Instrument
            instr = instrumentPanels.get(index).getUpdatedInstrument();
            instr = (Instrument) (instr.duplicate(null));
            // erase data that must be specific for this instrument
            instr.setName(null);
            instr.setId(null);
        }
        else {
            // Should never happen
            instr = new Instrument();
        }
        index++;
        addPanel(index, instr);
        mainPanel.selectTab(index, true);
    }

    @UiHandler("removeButton")
    void removeButtonOnClick(ClickEvent event) {
        removePanel(mainPanel.getSelectedIndex());
    }

    /**
     * Removes the indicated panel from the tab panel.  On return, the selected panel will be
     * the next panel in the list, or the last panel if the panel removed was the last panel.
     * This will not remove the panel if there is no other panel remaining; instead, an error
     * message is presented to the user.
     *
     * @param index
     *         remove the panel at this index; if invalid, does nothing
     */
    private void removePanel(int index) {
        int numPanels = instrumentPanels.size();
        if ( numPanels < 2 ) {
            UploadDashboard.showMessage("There must be at least one instrument");
            return;
        }
        if ( (index < 0) || (index >= numPanels) )
            return;
        instrumentPanels.remove(index);
        mainPanel.remove(index);
        numPanels--;
        if ( index == numPanels )
            index--;
        mainPanel.selectTab(index, true);
    }

    /**
     * Adds a panel appropriate for the given instrument at the given index in the tab panel.
     *
     * @param index
     *         insert panel at this index; if invalid, an error message is presented to the user
     * @param instr
     *         instrument to be associated with this panel
     */
    private void addPanel(int index, Instrument instr) {
        if ( (index < 0) || (index > instrumentPanels.size()) ) {
            UploadDashboard.showMessage("Unexpected invalid replacement panel index of " + index);
            return;
        }
        InstrumentPanel panel;
        String simpleName = instr.getSimpleName();
        HTML header = new HTML();
        switch ( simpleName ) {
            case "GasSensor":
                panel = new GasSensorPanel((GasSensor) instr, header, this);
                break;
            case "PressureSensor":
                panel = new PressureSensorPanel((PressureSensor) instr, header, this);
                break;
            case "SalinitySensor":
                panel = new SalinitySensorPanel((SalinitySensor) instr, header, this);
                break;
            case "TemperatureSensor":
                panel = new TemperatureSensorPanel((TemperatureSensor) instr, header, this);
                break;
            case "Analyzer":
                panel = new GenericSensorPanel((Analyzer) instr, header, this);
                break;
            case "Equilibrator":
                panel = new EquilibratorPanel((Equilibrator) instr, header, this);
                break;
            case "Sampler":
                panel = new GenericSamplerPanel((Sampler) instr, header, this);
                break;
            case "Instrument":
                panel = new GenericInstPanel(instr, header, this);
                break;
            default:
                UploadDashboard.showMessage("Unexpect instrument type of " + SafeHtmlUtils.htmlEscape(simpleName));
                return;
        }
        panel.initialize();
        instrumentPanels.add(index, panel);
        mainPanel.insert(panel, header, index);
    }

    /**
     * Change the instrument and associated panel to the selected type.
     *
     * @param instrIdx
     *         index of the instrument and associate panel to change
     * @param typeIdx
     *         index (in the instrType lists) of the instrument type to change to
     */
    private void changeInstrumentType(int instrIdx, int typeIdx) {
        if ( instrIdx < 0 ) {
            UploadDashboard.showMessage("Unexpected unknown instrument panel to replace");
            return;
        }
        if ( typeIdx < 0 ) {
            UploadDashboard.showMessage("No instrument type selected");
            return;
        }
        Instrument oldInstr = instrumentPanels.get(instrIdx).getUpdatedInstrument();
        Instrument instr;
        String simpleName = instrTypeSimpleNames.get(typeIdx);
        switch ( simpleName ) {
            case "GasSensor":
                instr = new GasSensor(oldInstr);
                break;
            case "PressureSensor":
                instr = new PressureSensor(oldInstr);
                break;
            case "SalinitySensor":
                instr = new SalinitySensor(oldInstr);
                break;
            case "TemperatureSensor":
                instr = new TemperatureSensor(oldInstr);
                break;
            case "Analyzer":
                instr = new Analyzer(oldInstr);
                break;
            case "Equilibrator":
                instr = new Equilibrator(oldInstr);
                break;
            case "Sampler":
                instr = new Sampler(oldInstr);
                break;
            case "Instrument":
                instr = new Instrument(oldInstr);
                break;
            default:
                UploadDashboard.showMessage("Unexpect instrument type of " + SafeHtmlUtils.htmlEscape(simpleName));
                return;
        }
        // Add the new panel first and then remove the old panel to avoid problems if there is only one panel
        addPanel(instrIdx + 1, instr);
        // Since the new panel was positioned after the one to be removed,
        // then new panel will then be the one selected after removal
        removePanel(instrIdx);
    }

}
