package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.variable.BioData;

import java.util.HashSet;

public class BioDataVarPanel extends InstDataVarPanel {

    interface BioDataVarPanelUiBinder extends UiBinder<ScrollPanel,BioDataVarPanel> {
    }

    private static final BioDataVarPanelUiBinder uiBinder = GWT.create(BioDataVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox bioSubjectValue;
    @UiField(provided = true)
    final LabeledTextBox speciesIdValue;
    @UiField(provided = true)
    final LabeledTextBox lifeStageValue;

    /**
     * Creates a FlowPanel associated with the given BioData metadata.
     * The {@link #initialize()} method must be called prior to using this FlowPanel.
     *
     * @param biovar
     *         associate this panel with this BioData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public BioDataVarPanel(BioData biovar, HTML header, VariablesTabPanel parentPanel) {
        super(biovar, header, parentPanel);
        //
        bioSubjectValue = new LabeledTextBox("Life stage:", "7em", "56em", null, null);
        //
        speciesIdValue = new LabeledTextBox("Species ID:", "7em", "23em", null, null);
        lifeStageValue = new LabeledTextBox("Life stage:", "8em", "23em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        BioData biovar = (BioData) vari;

        bioSubjectValue.setText(biovar.getBiologicalSubject());
        speciesIdValue.setText(biovar.getSpeciesId());
        lifeStageValue.setText(biovar.getLifeStage());

        bioSubjectValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                biovar.setBiologicalSubject(bioSubjectValue.getText());
                markInvalids(null);
            }
        });
        speciesIdValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                biovar.setSpeciesId(speciesIdValue.getText());
                markInvalids(null);
            }
        });
        lifeStageValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                biovar.setLifeStage(lifeStageValue.getText());
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((BioData) vari).invalidFieldNames();

        bioSubjectValue.markInvalid(invalids.contains("biologicalSubject"));
        speciesIdValue.markInvalid(invalids.contains("speciesId"));
        lifeStageValue.markInvalid(invalids.contains("lifeStage"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
