package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.AppConstants;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.activity.MultiVariableSelectorActivity.UpdateVariablesTreeItemSelectionHandler;
import gov.noaa.pmel.tmap.las.client.activity.VariableMetadataActivity.UpdateBreadcrumbsTreeItemSelectionHandler;
import gov.noaa.pmel.tmap.las.client.event.BreadcrumbValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.MultiVariableSelector;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class MultiVariableSelectorActivity extends AbstractActivity implements MultiVariableSelector.Presenter {
    private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);

    /**
     * This handler of {@link TreeItem} {@link SelectionEvent}s is for updating
     * the {@link MultiVariableSelector}s within the same {@link OutputPanel}.
     * TODO: The resulting value change will likely cause a {@link ?} to be
     * fired that other {@link OutputPanel}s might listen to on the
     * {@link EventBus}.
     * 
     * @author weusijana
     */
    public class UpdateVariablesTreeItemSelectionHandler implements SelectionHandler<TreeItem> {
        private MultiVariableSelector view;

        public UpdateVariablesTreeItemSelectionHandler(MultiVariableSelector view) {
            this.view = view;
        }

        @Override
        public void onSelection(SelectionEvent<TreeItem> event) {
            if ( (view != null) && (event != null) ) {
                Object source = event.getSource();
                // Only proceed if the source was from the same panel or from
                // the comparePanel
                if ( (source != null) && (source instanceof HasName) ) {
                    String sourceName = ((HasName) source).getName();
                    String viewName = view.getName();
                    if ( ((viewName != null) && viewName.equalsIgnoreCase(sourceName)) || CONSTANTS.comparePanelName().equalsIgnoreCase(sourceName) ) { //$NON-NLS-1$
                        // Get variables from the tree
                        TreeItem selectedItem = event.getSelectedItem();
                        if ( selectedItem != null ) {
                            Object userObject = selectedItem.getUserObject();
                            if ( (userObject != null) && (userObject instanceof VariableSerializable) ) {
                                VariableSerializable selectedVariable = (VariableSerializable) userObject;
                                // Find all siblings if any
                                Vector<VariableSerializable> variables = new Vector<VariableSerializable>();
                                // Vector<TreeItem> children = new
                                // Vector<TreeItem>();
                                int selectedIndex = 0;
                                TreeItem parent = selectedItem.getParentItem();
                                if ( parent == null ) {
                                    // There are no reachable siblings without a
                                    // parent, so just use the selectedVariable
                                    // and leave selectedIndex == 0
                                    variables.add(selectedVariable);
                                    // children.add(selectedItem);
                                } else {
                                    for ( int i = 0; i < parent.getChildCount(); i++ ) {
                                        TreeItem child = parent.getChild(i);
                                        userObject = child.getUserObject();
                                        if ( (userObject != null) && (userObject instanceof VariableSerializable) ) {
                                            variables.add((VariableSerializable) userObject);
                                            // children.add(child);
                                        }
                                    }
                                    selectedIndex = variables.indexOf(selectedVariable);
                                }
                                // Add the variables to the view
                                view.setVariables(variables, selectedIndex);
                            }
                        }
                    }
                }
            }
        }
    }

    private ClientFactory clientFactory;

    private String name;

    private UpdateVariablesTreeItemSelectionHandler updateVariablesTreeItemSelectionHandler;

    private EventBus eventBus;

    private MultiVariableSelectorActivity() {
        clientFactory = GWT.create(ClientFactory.class);
    }

    public MultiVariableSelectorActivity(ClientFactory clientFactory, String id) {
        this.name = id;
        this.clientFactory = clientFactory;
        eventBus = clientFactory.getEventBus();
    }

    /**
     * @return view
     */
    public MultiVariableSelector init() {
        MultiVariableSelector view = clientFactory.getMultiVariableSelector(name);
        view.setPresenter(this);

        updateVariablesTreeItemSelectionHandler = new UpdateVariablesTreeItemSelectionHandler(view);
        eventBus.addHandler(SelectionEvent.getType(), updateVariablesTreeItemSelectionHandler);

        return view;
    }

    @Override
    public String mayStop() {
        return "Please hold on. This activity is stopping.";
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        MultiVariableSelector view = init();
        containerWidget.setWidget(view.asWidget());
    }
}
