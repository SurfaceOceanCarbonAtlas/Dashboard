package gov.noaa.pmel.tmap.las.client.activity;

//import gov.noaa.pmel.tmap.las.client.laswidget.%placeName%;
import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.BreadcrumbValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.BreadcrumbValueChangeEvent.Handler;
import gov.noaa.pmel.tmap.las.client.laswidget.DatasetWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataView;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Activities are started and stopped by an ActivityManager associated with a
 * container Widget.
 */
public class VariableMetadataActivity extends AbstractActivity implements VariableMetadataView.Presenter {

    /**
     * This handler of {@link TreeItem} {@link SelectionEvent}s is for updating
     * the bread crumbs within the same {@link OutputPanel}. The resulting value
     * change will likely cause a {@link BreadcrumbValueChangeEvent} to be fired
     * that other {@link OutputPanel}s might listen to on the {@link EventBus}.
     * 
     * @author weusijana
     * 
     */
    public class UpdateBreadcrumbsTreeItemSelectionHandler implements SelectionHandler<TreeItem> {

        private String delimiter = "/";
        private VariableMetadataView view;

        public UpdateBreadcrumbsTreeItemSelectionHandler(VariableMetadataView view) {
            this.view = view;
        }

        /**
         * @param treeItem
         * @return bread crumb text after recursively walking the tree of
         *         treeItem
         */
        String getBreadcrumbsText(TreeItem treeItem) {
            String breadcrumbsText = "";
            TreeItem parentItem = treeItem.getParentItem();
            if ( parentItem != null ) {
                breadcrumbsText = getBreadcrumbsText(parentItem).trim() + delimiter;
            }
            String treeItemText = treeItem.getText();
            if ( treeItemText != null ) {
                // TODO: Fix: If delimiter is whitespace, it will get trimmed
                breadcrumbsText = breadcrumbsText.trim().concat(treeItemText.trim());
            }
            return breadcrumbsText;
        }

        /**
         * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(com.google.gwt.event.logical.shared.SelectionEvent)
         */
        @Override
        public void onSelection(SelectionEvent<TreeItem> event) {
            if ( (view != null) && (event != null) ) {
                Object source = event.getSource();
                // Only proceed if the source was from the same panel
                if ( (source != null) && (source instanceof HasName) ) {
                    String sourceName = ((HasName) source).getName();
                    if ( view.getName().equalsIgnoreCase(sourceName) ) {
                        TextBox breadcrumbs = view.getBreadcrumbs();
                        TreeItem selectedItem = event.getSelectedItem();
                        if ( selectedItem != null ) {
                            String selectedItemText = selectedItem.getText();
                            // Only bother to update the bread crumbs if the
                            // user
                            // has
                            // selected an item that has actually loaded
                            if ( !selectedItemText.equalsIgnoreCase(DatasetWidget.LOADING) ) {
                                // Update bread crumbs by walking the meta data
                                // categories GUI tree already in memory
                                TreeItem parentItem = selectedItem.getParentItem();
                                if ( parentItem != null ) {
                                    // First clear bread crumbs so events fire
                                    // even
                                    // when there is no change in
                                    // breadcrumbsText
                                    // characters, but set boolean fireEvents to
                                    // false to avoid unnecessary event firing.
                                    breadcrumbs.setValue("", false);
                                    String breadcrumbsText = getBreadcrumbsText(parentItem);
                                    breadcrumbs.setValue(breadcrumbsText, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Handler BreadcrumbValueChangeEventHandler;

    private ClientFactory clientFactory;

    private EventBus eventBus;

    private String name;

    private UpdateBreadcrumbsTreeItemSelectionHandler updateBreadcrumbsTreeItemSelectionHandler;

    public VariableMetadataActivity() {
        clientFactory = GWT.create(ClientFactory.class);
        eventBus = clientFactory.getEventBus();
    }

    public VariableMetadataActivity(ClientFactory clientFactory, String id) {
        this.name = id;
        this.clientFactory = clientFactory;
        eventBus = clientFactory.getEventBus();
    }

    /**
     * @return view
     */
    public VariableMetadataView init() {
        final VariableMetadataView view = clientFactory.getVariableMetadataView(name);
        view.setPresenter(this);

        // Listen to BreadcrumbValueChangeEvent events in order to update this
        // view's bread crumbs when the comparePanel's bread crumbs change
        updateBreadcrumbsTreeItemSelectionHandler = new UpdateBreadcrumbsTreeItemSelectionHandler(view);
        eventBus.addHandler(SelectionEvent.getType(), updateBreadcrumbsTreeItemSelectionHandler);

        BreadcrumbValueChangeEventHandler = new BreadcrumbValueChangeEvent.Handler() {
            @Override
            public void onValueChange(BreadcrumbValueChangeEvent event) {
                if ( !view.isOnComparePanel() ) {
                    // Since this view is NOT on the comparePanel, it should
                    // prepare to update its bread crumbs
                    // TODO: set a potential new bread crumb value, instead of
                    // changing it now
                    view.getBreadcrumbs().setValue(event.getValue());
                }
            }
        };
        eventBus.addHandler(BreadcrumbValueChangeEvent.TYPE, BreadcrumbValueChangeEventHandler);
        return view;
    }

    @Override
    public String mayStop() {
        return "Please hold on. This activity is stopping.";
    }

    /**
     * Called by the view.
     * 
     * @see gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataView.Presenter#onBreadcrumbValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent,
     *      gov.noaa.pmel.tmap.las.client.laswidget.VariableMetadataView)
     */
    @Override
    public void onBreadcrumbValueChange(ValueChangeEvent<String> event, VariableMetadataView view) {
        if ( (event != null) && (view.isOnComparePanel()) ) {
            // Since this view is on the comparePanel, fire a
            // BreadcrumbValueChangeEvent to update the other panels' bread
            // crumbs
            BreadcrumbValueChangeEvent breadcrumbValueChangeEvent = new BreadcrumbValueChangeEvent(event.getValue());
            eventBus.fireEvent(breadcrumbValueChangeEvent);
        }
    }

    @Override
    public void openInfo(String dsid) {
        if ( dsid != null )
            Window.open(URLUtil.getBaseURL()+"getMetadata.do?dsid=" + dsid, "_blank", "scrollbars=1");
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        VariableMetadataView view = init();
        containerWidget.setWidget(view.asWidget());
    }
}