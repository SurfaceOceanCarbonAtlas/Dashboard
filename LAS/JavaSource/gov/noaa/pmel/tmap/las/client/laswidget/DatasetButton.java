package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A button that will open a data picker that has automatically initialized
 * itself via RPC with the data sets for the LAS.
 * 
 * @author rhs
 * 
 */
public class DatasetButton extends Composite {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    PushButton choose;
    PopupPanel datasetPanel;
    Label dataset_name = new Label("");
    Label variable_name = new Label("");
    DecoratorPanel selection = new DecoratorPanel();
    VerticalPanel verticalPanel = new VerticalPanel();
    private DatasetWidget datasetWidget;
    VariableSerializable selectedVariable;
    ScrollPanel treePanel = new ScrollPanel();
    Grid popupGrid;
    Button close;
    int offset = 0;

    public DatasetButton() {
        choose = new PushButton("Data Collections");
        choose.setTitle("Select a data collection to examine.");
        choose.addStyleDependentName("SMALLER");
        choose.addClickHandler(openClick);
        datasetPanel = new PopupPanel(false);
        datasetPanel.setAnimationEnabled(true);
        datasetWidget = new DatasetWidget();
        datasetWidget.setName(name);
        datasetWidget.setHeight("650px");
        datasetWidget.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                TreeItem item = event.getSelectedItem();
                Object u = item.getUserObject();
                if ( u instanceof VariableSerializable ) {
                    selectedVariable = (VariableSerializable) u;
                    dataset_name.setText(selectedVariable.getDSName());
                    variable_name.setText(selectedVariable.getName());
                    datasetWidget.setName(name); // setting the name of this datasetWidget as it will be the source of the re-firing of event
                    eventBus.fireEvent(event); // this datasetWidget is now the source                    
                }
            }
        });
        datasetWidget.init();
        popupGrid = new Grid(3, 1);
        close = new Button("close");
        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                datasetPanel.hide();
            }
        });
        verticalPanel.add(dataset_name);
        verticalPanel.add(variable_name);
        selection.add(verticalPanel);
        treePanel.add(datasetWidget);
        popupGrid.setWidget(0, 0, close);
        popupGrid.setWidget(1, 0, selection);
        popupGrid.setWidget(2, 0, treePanel);
        datasetPanel.add(popupGrid);
        datasetPanel.hide();
        initWidget(choose);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    public void close() {
        datasetPanel.hide();
    }
    public ClickHandler openClick = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            datasetPanel.setPopupPosition(choose.getAbsoluteLeft() + offset, choose.getAbsoluteTop());
            datasetPanel.show();
        }
    };

    public VariableSerializable getSelectedVariable() {
        return selectedVariable;
    }

    public void addOpenClickHandler(ClickHandler handler) {
        choose.addClickHandler(handler);
    }

    public void addCloseClickHandler(ClickHandler handler) {
        close.addClickHandler(handler);
    }

    public HandlerRegistration addOpenHandler(OpenHandler<TreeItem> handler) {
        return datasetWidget.addOpenHandler(handler);
    }

    public void setOpenID(String openid) {
        datasetWidget.setOpenID(openid);
    }

    public void setSelectedText(String dataset, String variable) {
        dataset_name.setText(dataset);
        variable_name.setText(variable);
    }

    public void setAuthenticated(String auth_url, String value) {
        for ( int i = 0; i < datasetWidget.getItemCount(); i++ ) {
            TreeItem item = datasetWidget.getItem(i);
            Object u = item.getUserObject();
            if ( u instanceof CategorySerializable ) {
                CategorySerializable cat = (CategorySerializable) u;
                String url = cat.getAttributes().get("remote_las");
                if ( url != null && url.equals(auth_url) ) {
                    cat.setAttribute("authenticated", value);
                }
            }
        }
    }

    private String name;

    public void setName(String name) {
        this.name = name;
        datasetWidget.setName(name); // TODO: Is this necessary?
    }

    public String getName() {
        return name;
    }

    /**
     * @return the datasetWidget
     */
    public DatasetWidget getDatasetWidget() {
        return datasetWidget;
    }

    /**
     * @param datasetWidget
     *            the datasetWidget to set
     */
    public void setDatasetWidget(DatasetWidget datasetWidget) {
        this.datasetWidget = datasetWidget;
    }
}
