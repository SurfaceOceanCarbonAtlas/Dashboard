package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.activity.MultiVariableSelectorActivity;
import gov.noaa.pmel.tmap.las.client.activity.VariableMetadataActivity;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Default implementation of {@link VariableControlsOldAndComplicated}.
 */
public class VariableControlsImpl extends HorizontalPanel implements VariableControlsOldAndComplicated {
    private ClientFactory clientFactory;
    private Presenter listener;
    private MultiVariableSelector multiVariableSelector;
    private String name;
    private VariableSerializable variable;

    private VariableControlsImpl() {
        this(null);
    }

    /**
     * @wbp.parser.constructor
     */
    public VariableControlsImpl(String outputPanelID) {
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        setSpacing(5);
        clientFactory = GWT.create(ClientFactory.class);

        MultiVariableSelectorActivity multiVariableSelectorPresenter = new MultiVariableSelectorActivity(clientFactory, outputPanelID);
        multiVariableSelector = multiVariableSelectorPresenter.init();
        add(multiVariableSelector);
    }

    /**
     * @return the multiVariableSelector
     */
    @Override
    public MultiVariableSelector getMultiVariableSelector() {
        return multiVariableSelector;
    }

    /**
     * @return the variableMetadataView
     */
//    @Override
//    public VariableMetadataView getVariableMetadataView() {
//        return variableMetadataView;
//    }

    @Override
    public void setName(String helloName) {
        this.name = helloName;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    @Override
    public void setVariable(VariableSerializable variable) {
        this.variable = variable;
        listener.update(this);
    }

    /**
     * @return the variable
     */
    @Override
    public VariableSerializable getVariable() {
        return variable;
    }

}
