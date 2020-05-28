package gov.noaa.pmel.dashboard.client.metadata.varpanels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.client.metadata.LabeledListBox;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextArea;
import gov.noaa.pmel.dashboard.client.metadata.LabeledTextBox;
import gov.noaa.pmel.socatmetadata.shared.core.MultiNames;
import gov.noaa.pmel.socatmetadata.shared.person.Person;
import gov.noaa.pmel.socatmetadata.shared.variable.InstData;
import gov.noaa.pmel.socatmetadata.shared.variable.MethodType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class InstDataVarPanel extends GenDataVarPanel {

    private static final ArrayList<String> MEASURE_METHOD_NAMES = new ArrayList<String>(Arrays.asList(
            "unspecified",
            "discrete measurements",
            "in-situ measurements",
            "manipulations",
            "response",
            "computed"
    ));

    private static final ArrayList<MethodType> MEASURE_METHOD_TYPES = new ArrayList<MethodType>(Arrays.asList(
            MethodType.UNSPECIFIED,
            MethodType.MEASURED_DISCRETE,
            MethodType.MEASURED_INSITU,
            MethodType.MANIPULATION,
            MethodType.RESPONSE,
            MethodType.COMPUTED
    ));

    interface InstDataVarPanelUiBinder extends UiBinder<ScrollPanel,InstDataVarPanel> {
    }

    private static final InstDataVarPanelUiBinder uiBinder = GWT.create(InstDataVarPanelUiBinder.class);

    @UiField(provided = true)
    final LabeledTextBox sampleLocValue;
    @UiField(provided = true)
    final LabeledTextBox sampleElevValue;
    @UiField(provided = true)
    final LabeledTextBox observeTypeValue;
    @UiField(provided = true)
    final LabeledListBox measureMethodValue;
    @UiField(provided = true)
    final LabeledTextArea methodDescValue;
    @UiField(provided = true)
    final LabeledTextArea methodRefsValue;
    @UiField(provided = true)
    final LabeledTextArea manipDescValue;
    @UiField(provided = true)
    final LabeledTextBox storageMethodValue;
    @UiField(provided = true)
    final LabeledTextBox durationValue;
    @UiField(provided = true)
    final LabeledTextBox analysisTempValue;
    @UiField(provided = true)
    final LabeledTextBox replicationValue;
    @UiField(provided = true)
    final LabeledTextBox researcherValue;
    @UiField(provided = true)
    final LabeledTextBox instrumentsValue;

    /**
     * Creates a FlowPanel associated with the given InstData metadata.
     *
     * @param instvar
     *         associate this panel with this InstData; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public InstDataVarPanel(InstData instvar, HTML header, VariablesTabPanel parentPanel) {
        super(instvar, header, parentPanel);
        //
        sampleLocValue = new LabeledTextBox("Sampling loc.:", "7em", "23em", null, null);
        sampleElevValue = new LabeledTextBox("Sampling elev.:", "8em", "23em", null, null);
        //
        observeTypeValue = new LabeledTextBox("Obs. type:", "7em", "23em", null, null);
        measureMethodValue = new LabeledListBox("Method:", "8em", null, null, null);
        //
        methodDescValue = new LabeledTextArea("Method description:", "7em", "8em", "56em");
        //
        methodRefsValue = new LabeledTextArea("Method references:", "7em", "4em", "56em");
        //
        manipDescValue = new LabeledTextArea("Manipulations:", "7em", "4em", "56em");
        //
        storageMethodValue = new LabeledTextBox("Storage method:", "7em", "23em", null, null);
        durationValue = new LabeledTextBox("Duration:", "8em", "23em", null, null);
        //
        analysisTempValue = new LabeledTextBox("Analysis temp:", "7em", "23em", null, null);
        replicationValue = new LabeledTextBox("Replication:", "8em", "23em", null, null);
        //
        researcherValue = new LabeledTextBox("Investigator:", "7em", "23em", null, null);
        instrumentsValue = new LabeledTextBox("Instrument(s):", "8em", "23em", null, null);
    }

    @Override
    public void initialize() {
        initWidget(uiBinder.createAndBindUi(this));
        finishInitialization();
    }

    @Override
    protected void finishInitialization() {
        InstData instvar = (InstData) vari;
        sampleLocValue.setText(instvar.getSamplingLocation());
        sampleElevValue.setText(instvar.getSamplingElevation());
        observeTypeValue.setText(instvar.getObserveType());

        for (String name : MEASURE_METHOD_NAMES) {
            measureMethodValue.addItem(name);
        }
        int idx = MEASURE_METHOD_TYPES.indexOf(instvar.getMeasureMethod());
        if ( idx < 0 )
            idx = 0;
        measureMethodValue.setSelectedIndex(idx);

        methodDescValue.setText(instvar.getMethodDescription());
        methodRefsValue.setText(instvar.getMethodReference());
        manipDescValue.setText(instvar.getManipulationDescription());
        storageMethodValue.setText(instvar.getStorageMethod());
        durationValue.setText(instvar.getDuration());
        analysisTempValue.setText(instvar.getAnalysisTemperature());
        replicationValue.setText(instvar.getReplication());
        researcherValue.setText(instvar.getResearcher().getReferenceName());
        instrumentsValue.setText(instvar.getInstrumentNames().asOneString());

        sampleLocValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setSamplingLocation(sampleLocValue.getText());
                markInvalids(null);
            }
        });
        sampleElevValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setSamplingElevation(sampleElevValue.getText());
                markInvalids(null);
            }
        });
        observeTypeValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setObserveType(observeTypeValue.getText());
                markInvalids(null);
            }
        });
        measureMethodValue.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int idx = measureMethodValue.getSelectedIndex();
                if ( idx < 0 )
                    idx = 0;
                instvar.setMeasureMethod(MEASURE_METHOD_TYPES.get(idx));
                markInvalids(null);
            }
        });
        methodDescValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setMethodDescription(methodDescValue.getText());
                markInvalids(null);
            }
        });
        methodRefsValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setMethodReference(methodRefsValue.getText());
                markInvalids(null);
            }
        });
        manipDescValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setManipulationDescription(manipDescValue.getText());
                markInvalids(null);
            }
        });
        storageMethodValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setStorageMethod(storageMethodValue.getText());
                markInvalids(null);
            }
        });
        durationValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setDuration(durationValue.getText());
                markInvalids(null);
            }
        });
        analysisTempValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setAnalysisTemperature(analysisTempValue.getText());
                markInvalids(null);
            }
        });
        replicationValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setReplication(replicationValue.getText());
                markInvalids(null);
            }
        });
        researcherValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String[] nameParts = researcherValue.getText().split(",", 2);
                Person pi = new Person();
                if ( !"Unknown".equals(nameParts[0]) )
                    pi.setLastName(nameParts[0]);
                if ( nameParts.length > 1 ) {
                    nameParts = nameParts[1].split(" ", 2);
                    pi.setFirstName(nameParts[0]);
                    if ( nameParts.length > 1 )
                        pi.setMiddle(nameParts[1]);
                }
                instvar.setResearcher(pi);
                markInvalids(null);
            }
        });
        instrumentsValue.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                instvar.setInstrumentNames(new MultiNames(instrumentsValue.getText()));
                markInvalids(null);
            }
        });

        // Finish initialization, including marking invalid fields
        super.finishInitialization();
    }

    @Override
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = ((InstData) vari).invalidFieldNames();

        sampleLocValue.markInvalid(invalids.contains("samplingLocation"));
        sampleElevValue.markInvalid(invalids.contains("samplingElevation"));
        observeTypeValue.markInvalid(invalids.contains("observeType"));
        measureMethodValue.markInvalid(invalids.contains("measureMethod"));
        methodDescValue.markInvalid(invalids.contains("methodDescription"));
        methodRefsValue.markInvalid(invalids.contains("methodReference"));
        manipDescValue.markInvalid(invalids.contains("manipulationDescription"));
        storageMethodValue.markInvalid(invalids.contains("storageMethod"));
        durationValue.markInvalid(invalids.contains("duration"));
        analysisTempValue.markInvalid(invalids.contains("analysisTemperature"));
        replicationValue.markInvalid(invalids.contains("replication"));
        researcherValue.markInvalid(invalids.contains("researcher"));
        instrumentsValue.markInvalid(invalids.contains("instrumentNames"));

        // Finish marking labels and the tab for this panel
        super.markInvalids(invalids);
    }

}
