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
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

import java.util.ArrayList;

public class InstrumentsTabPanel extends Composite {
    interface InstrumentsTabPanelUiBinder extends UiBinder<ScrollPanel,InstrumentsTabPanel> {
    }

    private static InstrumentsTabPanelUiBinder uiBinder = GWT.create(InstrumentsTabPanelUiBinder.class);

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
     * The add button will append an new InstrumentPanel (and thus, a new Instrument) and display it.
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
        for (Instrument instr : instruments) {
            HTML header = new HTML();
            InstrumentPanel panel = new InstrumentPanel(instr, header);
            instrumentPanels.add(panel);
            mainPanel.add(panel, header);
        }
        addButton.setText("Append another");
        addButton.setTitle("Adds a new instrument description to the end of the list");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed instrument description");
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
        int numPanels = instrumentPanels.size();
        Instrument instr;
        int index = mainPanel.getSelectedIndex();
        if ( (index >= 0) && (index < numPanels) ) {
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
        HTML header = new HTML();
        InstrumentPanel panel = new InstrumentPanel(instr, header);
        instrumentPanels.add(panel);
        mainPanel.add(panel, header);
        mainPanel.selectTab(numPanels, true);
    }

    @UiHandler("removeButton")
    void removeButtonOnClick(ClickEvent event) {
        int numPanels = instrumentPanels.size();
        if ( numPanels < 2 ) {
            UploadDashboard.showMessage("There must be at least one instrument");
            return;
        }
        int index = mainPanel.getSelectedIndex();
        if ( (index < 0) || (index >= numPanels) )
            return;
        instrumentPanels.remove(index);
        mainPanel.remove(index);
        numPanels--;
        if ( index == numPanels )
            index--;
        mainPanel.selectTab(index, true);
    }

}
