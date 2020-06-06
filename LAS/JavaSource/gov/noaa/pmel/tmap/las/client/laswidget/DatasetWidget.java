package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.ESGFDatasetAddedEvent;
import gov.noaa.pmel.tmap.las.client.event.ESGFDatasetAddedEvent.Handler;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.Serializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A tree Widget that is the data set picker for GWT LAS clients which
 * understands how to initialize itself and is used by the
 * {@link gov.noaa.pmel.tmap.las.client.laswidget.DatasetButton}
 */
public class DatasetWidget extends Tree implements HasName {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    TreeItem saveSelection;
    public DatasetWidget() {
        super();
        eventBus.addHandler(ESGFDatasetAddedEvent.TYPE, esgfDatasetAddedHandler);
    }
    
    private Handler esgfDatasetAddedHandler = new Handler() {

        @Override
        public void onESGFDatasetAdded(ESGFDatasetAddedEvent event) {
            saveSelection = currentlySelected;
            
            // This will cause the result to be added to the top of the tree...
            currentlySelected = null;
            
            // Clear the tree to re-add all the datasets in this session.
            clear();
            
            Util.getRPCService().getCategories(null, null, categoryCallback);
            
        }
        
    };

    public static final String LOADING = "Loading...";

    List<DatasetFilter> filters = new ArrayList<DatasetFilter>();

    TreeItem currentlySelected = null;
    String openid;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.TreeListener#onTreeItemSelected(com.google
     * .gwt.user.client.ui.TreeItem)
     */
    public void onTreeItemSelected(TreeItem item) {
        currentlySelected = item;
        Object u = item.getUserObject();
        if ( u instanceof VariableSerializable ) {
            VariableSerializable v = (VariableSerializable) u;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.TreeListener#onTreeItemStateChanged(com
     * .google.gwt.user.client.ui.TreeItem)
     */
    OpenHandler<TreeItem> open = new OpenHandler<TreeItem>() {

        @Override
        public void onOpen(OpenEvent<TreeItem> event) {
            TreeItem item = event.getTarget();
            currentlySelected = item;
            if ( item.getChild(0).getText().equals(DatasetWidget.LOADING) ) {
                CategorySerializable cat = (CategorySerializable) item.getUserObject();
                Util.getRPCService().getCategories(cat.getID(), null, categoryCallback);
            }
        }

    };

    SelectionHandler<TreeItem> selection = new SelectionHandler<TreeItem>() {

        @Override
        public void onSelection(SelectionEvent<TreeItem> event) {
            TreeItem item = event.getSelectedItem();
            currentlySelected = item;
            TreeItem child = item.getChild(0);
            if ( child != null && child.getText().equals(DatasetWidget.LOADING) ) {
                CategorySerializable cat = (CategorySerializable) item.getUserObject();
                Util.getRPCService().getCategories(cat.getID(), null, categoryCallback);
            }
            // Open the item.  Work around double firing bug.
            // http://code.google.com/p/google-web-toolkit/issues/detail?id=3660&q=Tree%20selection&colspec=ID%20Type%20Status%20Owner%20Milestone%20Summary%20Stars
            TreeItem selItem = event.getSelectedItem();
            TreeItem parent = selItem.getParentItem();
            selItem.getTree().setSelectedItem(parent, false); // null is ok
            if(parent != null)
                parent.setSelected(false);  // not compulsory
            selItem.setState(!selItem.getState(), false);

        }

    };

    /**
     * Set up the tree and the associated RPC.
     */
    public void init() {
        try {
			Util.getRPCService().getCategories(null, null, categoryCallback);
		} catch (RuntimeException e) {
			// Catching this exception mainly to allow WindowBuilder to work without error
			e.printStackTrace();
		}
        addOpenHandler(open);
        addSelectionHandler(selection);
    }

    public void addFilter(DatasetFilter filter) {
        filters.add(filter);
    }

    public void removeFilter(DatasetFilter filter) {
        filters.remove(filter);
    }

    AsyncCallback categoryCallback = new AsyncCallback() {
        public void onSuccess(Object result) {
            CategorySerializable[] cats = (CategorySerializable[]) result;
            if ( cats != null && cats.length > 0 ) {
                if ( currentlySelected == null ) {
                    for ( int i = 0; i < cats.length; i++ ) {
                        CategorySerializable cat = cats[i];
                        String children = cat.getAttributes().get("children");
                        boolean empty = false;
                        if ( children != null && children.equals("none") ) empty = true;
                        if ( applyFilters(cat) && !empty ) {
                            TreeItem item = new TreeItem();
                            item.addItem(new SafeHtmlBuilder().appendEscaped(DatasetWidget.LOADING).toSafeHtml());
                            InnerItem inner = new InnerItem(cat);
                            item.setWidget(inner);
                            item.setUserObject(cat);
                            addItem(item);
                        }
                    }
                } else {
                    for ( int i = 0; i < cats.length; i++ ) {
                        CategorySerializable cat = cats[i];
                        if ( cat.isCategoryChildren() ) {
                            String name = cat.getName();
                            TreeItem item;
                            if ( i == 0 ) {
                                item = currentlySelected.getChild(0);
                            } else {
                                item = new TreeItem();
                            }
                            item.addItem(new SafeHtmlBuilder().appendEscaped(DatasetWidget.LOADING).toSafeHtml());
                            InnerItem inner = new InnerItem(cat);
                            item.setWidget(inner);
                            item.setUserObject(cat);
                            if ( i > 0 ) {
                                currentlySelected.addItem(item);
                            }
                        } else if ( cat.isVariableChildren() ) {
                            // Must have variable children...
                            TreeItem item = currentlySelected.getChild(0);
                            if ( cat.hasMultipleDatasets() ) {
                                DatasetSerializable[] dses = cat.getDatasetSerializableArray();
                                DatasetSerializable ds = dses[0];
                                VariableSerializable[] vars = ds.getVariablesSerializable();
                                currentlySelected.removeItems();
                                for ( int j = 0; j < dses.length; j++ ) {
                                    ds = dses[j];
                                    vars = ds.getVariablesSerializable();
                                    loadItem(vars);
                                }
                            } else {
                                DatasetSerializable ds = cat.getDatasetSerializable();
                                VariableSerializable[] vars = ds.getVariablesSerializable();
                                currentlySelected.removeItems();
                                loadItem(vars);
                            }
                        } 
                    }
                }
            } else {
                // A category was selected, but it came back empty...
                if ( currentlySelected != null ) {
                    TreeItem item = currentlySelected.getChild(0);
                    item.setText("No data sets found.");
                }
            }
            if ( saveSelection != null ) {
                currentlySelected = saveSelection;
                saveSelection = null;
            }
        }

        private boolean applyFilters(CategorySerializable cat) {
            // Apply any filters.
            boolean include = true;
            if ( filters.size() > 0 ) {
                for ( Iterator filterIt = filters.iterator(); filterIt.hasNext(); ) {
                    DatasetFilter filter = (DatasetFilter) filterIt.next();

                    // This should be done with introspection, but for now do a
                    // big cheat
                    String name = "x";
                    String value = "y";
                    if ( filter.getAttribute().equals("name") ) {
                        name = cat.getName().toLowerCase(Locale.ENGLISH);
                        value = filter.getValue().toLowerCase(Locale.ENGLISH);

                    } else if ( filter.getAttribute().equals("ID") ) {
                        name = cat.getID();
                        value = filter.getValue();
                    }
                    if ( name.contains(value) ) {
                        include = include && filter.isInclude();
                    } else {
                        include = include && !filter.isInclude();
                    }
                }
            }

            return include;
        }

        public void onFailure(Throwable caught) {
            Window.alert("Server Request Failed: " + caught.getMessage());
        }

        private void loadItem(VariableSerializable[] vars) {
            for ( int j = 0; j < vars.length; j++ ) {
            	
            	
             if ( !vars[j].isTimeSeries() || !vars[j].isSubset() ) {
                    TreeItem item = new TreeItem();
                    item.setText(vars[j].getName());
                    item.setUserObject(vars[j]);
                    currentlySelected.addItem(item);
                }
            }
        }
    };

    public Object getCurrentlySelected() {
        return currentlySelected.getUserObject();
    }

    public void setOpenID(String openid) {
        this.openid = openid;
    }

    public class InnerItem extends Composite {
        Grid grid = new Grid(1, 2);
        Label label = new Label("Loading data...");
        Image image = new Image(GWT.getModuleBaseURL() + "../images/info.png");
        PopupPanel inner = new PopupPanel(true);
        Button close = new Button("Close");
        VerticalPanel innerLayout = new VerticalPanel();

        public InnerItem(Serializable s) {
            close.addStyleDependentName("SMALLER");
            close.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    inner.hide();
                }

            });
            innerLayout.add(close);
            grid.getCellFormatter().setWidth(0, 0, "85%");
            grid.getCellFormatter().setWidth(0, 1, "5%");
            label.setText(s.getName());
            grid.setWidget(0, 0, label);
            if ( s instanceof CategorySerializable ) {
                CategorySerializable c = (CategorySerializable) s;
                if ( c.getDoc() != null || c.getAttributes().get("children_dsid") != null ) {
                    image.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            inner.setPopupPosition(image.getAbsoluteLeft(), image.getAbsoluteTop());
                            inner.show();
                        }
                    });
                    grid.setWidget(0, 1, image);
                    if ( c.getDoc() != null ) {
                        String url = c.getDoc();
                        if ( !c.getDoc().equals("") ) {
                            Anchor link = new Anchor("Documentation", UriUtils.sanitizeUri(url), "_blank");
                            innerLayout.add(link);
                        }
                    }
                    String csid =  c.getAttributes().get("children_dsid");
                    if ( csid != null ) {
                        Anchor meta = new Anchor("Variable and Grid Description", "getMetadata.do?dsid=" + csid, "_blank");
                        innerLayout.add(meta);
                    }
                    inner.add(innerLayout);
                }
            }
            initWidget(grid);
        }

    }

    private String name;

    public void setName(String name) {
        this.name = name;        
    }

    public String getName() {
        return name;
    }
}
