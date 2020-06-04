package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.core.NumericString;
import gov.noaa.pmel.socatmetadata.shared.instrument.CalibrationGas;

import java.util.HashSet;

public class CalibrationGasPanel extends Composite {

    interface CalibrationGasPanelUiBinder extends UiBinder<FlowPanel,CalibrationGasPanel> {
    }

    private static final CalibrationGasPanelUiBinder uiBinder = GWT.create(CalibrationGasPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox idValue;
    @UiField(provided = true)
    final LabeledTextBox typeValue;
    @UiField(provided = true)
    final LabeledTextBox supplierValue;
    @UiField(provided = true)
    final LabeledTextBox frequencyValue;
    @UiField(provided = true)
    final LabeledTextBox concValue;
    @UiField(provided = true)
    final LabeledTextBox accuracyValue;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;

    private final CalibrationGas calibrationGas;
    private final HTML header;
    private final GasSensorPanel parentPanel;

    /**
     * Create an appropriate panel for a calibration gas
     *
     * @param gas
     *         calibration gas to use
     * @param header
     *         header tab for this panel
     * @param parentPanel
     *         tab panel containing and controlling this panel
     */
    public CalibrationGasPanel(CalibrationGas gas, HTML header, GasSensorPanel parentPanel) {
        typeValue = new LabeledTextBox("Type:", "7em", "20em", null, null);
        idValue = new LabeledTextBox("ID:", "7em", "20em", null, null);
        frequencyValue = new LabeledTextBox("Frequency:", "7em", "20em", null, null);
        supplierValue = new LabeledTextBox("Supplier:", "7em", "20em", null, null);
        concValue = new LabeledTextBox("Concentration:", "7em", "17em", CalibrationGas.GAS_CONCENTRATION_UNIT, "3em");
        accuracyValue = new LabeledTextBox("Frequency:", "7em", "17em", CalibrationGas.GAS_CONCENTRATION_UNIT, "3em");
        initWidget(uiBinder.createAndBindUi(this));

        this.calibrationGas = gas;
        this.header = header;
        this.parentPanel = parentPanel;

        addButton.setText("Add another");
        addButton.setTitle("Adds a new calibration gas description after the currently displayed one");
        removeButton.setText("Remove current");
        removeButton.setTitle("Removes the currently displayed calibration gas description");

        typeValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setType(typeValue.getText());
                markInvalids();
            }
        });
        idValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setId(idValue.getText());
                markInvalids();
            }
        });
        frequencyValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setUseFrequency(frequencyValue.getText());
                markInvalids();
            }
        });
        supplierValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setSupplier(supplierValue.getText());
                markInvalids();
            }
        });
        concValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setConcentration(
                        new NumericString(concValue.getText(), CalibrationGas.GAS_CONCENTRATION_UNIT));
                markInvalids();
            }
        });
        accuracyValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                calibrationGas.setAccuracy(
                        new NumericString(accuracyValue.getText(), CalibrationGas.GAS_CONCENTRATION_UNIT));
                markInvalids();
            }
        });
    }

    @UiHandler("addButton")
    void addButtonOnClickEvent(ClickEvent event) {
        parentPanel.duplicatePanel(this);
    }

    @UiHandler("removeButton")
    void removeButtonOnClickEvent(ClickEvent event) {
        parentPanel.removePanel(this);
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     * Also appropriately assigns the tab for this panel and
     * notifies the parent panel to update its calibration gases.
     */
    private void markInvalids() {
        HashSet<String> invalids = calibrationGas.invalidFieldNames();
        // Set the contents of the tab for this panel
        String refname = calibrationGas.getId();
        if ( refname.isEmpty() )
            refname = "Unknown";
        String type = calibrationGas.getType();
        if ( !type.isEmpty() )
            refname += " - " + type;
        SafeHtml tabhtml = SafeHtmlUtils.fromString(refname);
        if ( invalids.isEmpty() )
            header.setHTML(UploadDashboard.invalidLabelHtml(tabhtml));
        else
            header.setHTML(tabhtml);
        // indicate which fields are invalid
        typeValue.markInvalid(invalids.contains("type"));
        idValue.markInvalid(invalids.contains("id"));
        frequencyValue.markInvalid(invalids.contains("useFrequency"));
        supplierValue.markInvalid(invalids.contains("supplier"));
        concValue.markInvalid(invalids.contains("concentration"));
        accuracyValue.markInvalid(invalids.contains("accuracy"));
        // notify the parent panel to update the calibration gases
        parentPanel.updateCalibrationGases();
    }

    /**
     * @return the updated calibration gas; never null
     */
    public CalibrationGas getUpdatedCalibrationGas() {
        return calibrationGas;
    }

}