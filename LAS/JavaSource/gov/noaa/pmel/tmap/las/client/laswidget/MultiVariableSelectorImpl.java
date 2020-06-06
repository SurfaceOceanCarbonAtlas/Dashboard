package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.activity.VariableSelectorActivity;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Default implementation of {@link MultiVariableSelector}.
 */
// TODO: Move non-view logic to the activity presenter/listener
public class MultiVariableSelectorImpl extends Composite implements MultiVariableSelector {

    private ClientFactory clientFactory;
    private VariableSelector firstVariableSelector;
    private Presenter listener;
    private String name;
    private SelectionHandler<TreeItem> treeItemSelectionEventHandler;
    private Vector<VariableSerializable> variables = new Vector<VariableSerializable>();
    private HorizontalPanel variableSelectors;

    private MultiVariableSelectorImpl() {
        this(null);
    }

    /**
     * @wbp.parser.constructor
     */
    public MultiVariableSelectorImpl(String id) {
        setName(id);
        clientFactory = GWT.create(ClientFactory.class);

        variableSelectors = new HorizontalPanel();

        // HorizontalPanel variable_container = new HorizontalPanel();
        VariableSelectorActivity variableSelectorPresenter = new VariableSelectorActivity(clientFactory, id);
        firstVariableSelector = variableSelectorPresenter.init(id);

        variableSelectors.add(firstVariableSelector);

        initWidget(variableSelectors);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the {@link SelectionHandler}<{@link TreeItem}> Handler
     */
    @Override
    public SelectionHandler<TreeItem> getSelectionHandler() {
        return treeItemSelectionEventHandler;
    }

    @Override
    public Vector<VariableSerializable> getVariables() {
        return variables;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    @Override
    public void setSelectionHandler(SelectionHandler<TreeItem> treeItemSelectionEventHandler) {
        this.treeItemSelectionEventHandler = treeItemSelectionEventHandler;
    }

    @Override
    public void setVariables(Vector<VariableSerializable> variables, int selected) {
        this.variables = variables;
        firstVariableSelector.setVariables(variables, selected);
    }

    @Override
    public VariableSelector getVariableSelector() {
       return firstVariableSelector;      
    }

}
