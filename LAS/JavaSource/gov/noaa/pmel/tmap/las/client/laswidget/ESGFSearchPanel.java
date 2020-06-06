package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.ESGFDatasetAddedEvent;
import gov.noaa.pmel.tmap.las.client.event.FacetChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ESGFDatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.FacetSerializable;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ESGFSearchPanel extends Composite {
    
    PopupPanel spin = new PopupPanel(false, true);
    HorizontalPanel spinInterior = new HorizontalPanel();
    Label spinLabel = new Label("Getting search facet information...");
    Image spinImage;
    
    PushButton close = new PushButton("Close");
    PopupPanel mainPanel = new PopupPanel();
    VerticalPanel leftPanel = new VerticalPanel();
    HorizontalPanel interiorPanel = new HorizontalPanel();
    VerticalPanel dataPanel = new VerticalPanel();
    VerticalPanel datasetPanel = new VerticalPanel();
    FlowPanel northPanel = new FlowPanel();
    StackLayoutPanel facets = new StackLayoutPanel(Unit.EM);
    List<CheckBox> activeFacets = new ArrayList<CheckBox>();
    FlowPanel activePanel = new FlowPanel();
    FlowPanel activeTextPanel = new FlowPanel();
    HorizontalPanel searchPanel = new HorizontalPanel();
    PushButton searchButton = new PushButton("Search");
    TextBox searchText = new TextBox();
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    int limit = 15;
    int offset = 0;
    static boolean ready = false;
    public ESGFSearchPanel() {
        
        spinImage = new Image(URLUtil.getImageURL() + "/mozilla_blu.gif");
        
        spinImage.setSize("18px", "18px");
        spinInterior.setSpacing(15);
        spinInterior.add(spinImage);
        spinInterior.add(spinLabel);
        spin.add(spinInterior);
        
        spin.setHeight("22px");
        
        close.addStyleDependentName("SMALLER");
        close.setWidth("60px");
        mainPanel.add(interiorPanel);
        mainPanel.setSize("950px", "820px");
        searchText.setVisibleLength(80);
        searchPanel.add(searchButton);
        searchPanel.add(searchText);
        searchButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent click) {
                String querytext = searchText.getValue();
                String query = getQuery();
                if ( querytext != null && !querytext.equals("")) {
                    Anchor remove = new Anchor("(x) text: "+querytext);
                    remove.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent remove) {
                            searchText.setText("");
                            activeTextPanel.clear();
                            spinLabel.setText("Searching...");
                            spin.show();
                            offset=0;
                            Util.getRPCService().getESGFDatasets(getQuery()+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);
                        }
                    });
                    activeTextPanel.clear();
                    activeTextPanel.add(remove);
                }
                offset = 0;
                spinLabel.setText("Searching...");
                spin.show();
                Util.getRPCService().getESGFDatasets(query+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);
            }
            
        });
        HTML title = new HTML("Search the ESGF Data Collection");
        HTML instructions = new HTML("After searching, click the data set name to add it to the session. See all the data sets you've added by clicking the Data Set button above");
        northPanel.add(title);
        northPanel.add(instructions);
        northPanel.add(activePanel);
        northPanel.add(new InlineLabel("   "));
        northPanel.add(activeTextPanel);
        northPanel.add(new HTML("<HR>"));
        northPanel.setStyleDependentName("allBoarder", true);
        facets.setSize("200px", "820px");
        datasetPanel.setWidth("650px");
        datasetPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        datasetPanel.setSpacing(20);
        leftPanel.add(close);
        leftPanel.add(facets);
        interiorPanel.add(leftPanel);
        dataPanel.add(northPanel);
        dataPanel.add(datasetPanel);   
        dataPanel.add(searchPanel);
        interiorPanel.add(dataPanel);
        eventBus.addHandler(FacetChangeEvent.TYPE, facetChangeEventHandler);
        initWidget(mainPanel);
    }
    
    public void init() {
        spin.setPopupPosition(mainPanel.getAbsoluteLeft()+475, mainPanel.getAbsoluteTop()+410);
        spin.show();
        Util.getRPCService().getFacets(facetCallback);
    }
    public boolean isReady() {
        return ready;
    }
    private AsyncCallback<List<FacetSerializable>> facetCallback = new AsyncCallback<List<FacetSerializable>> (){

        @Override
        public void onFailure(Throwable arg0) {
            Window.alert("Search initialization failed");
        }

        @Override
        public void onSuccess(List<FacetSerializable> results) {
            spinLabel.setText("Initializing facet widget...");
            for ( Iterator facetIt = results.iterator(); facetIt.hasNext(); ) {
                FacetSerializable facetSerializable = (FacetSerializable) facetIt.next();
                FacetPanel facetPanel = new FacetPanel(facetSerializable);
                facets.add(facetPanel, facetSerializable.getPrettyName(), 2);
            }
            ready = true;
            spin.hide();
        }
        
    };
    private FacetChangeEvent.Handler facetChangeEventHandler = new FacetChangeEvent.Handler() {

        @Override
        public void onFacetChanged(FacetChangeEvent event) {
            CheckBox checkBox = (CheckBox) event.getSource();
            if ( checkBox.getValue() ) {
                addFacet(checkBox);
            } else {
                removeFacet(checkBox);
            }
            
        }
        
    };

    protected void addFacet(CheckBox checkBox) {
        if ( !activeFacets.contains(checkBox) ) {
            activeFacets.add(checkBox);
        }
        String query = URL.encode(showFacets());     
        if ( query.length() ==0 ) {
            datasetPanel.clear();
        } else {
            spinLabel.setText("Searching...");
            spin.show();
            Util.getRPCService().getESGFDatasets(query+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);
        }
    }

    protected void removeFacet(CheckBox checkBox) {
        checkBox.setValue(false, false);
        activeFacets.remove(checkBox);
        String query = URL.encode(showFacets());  
        offset=0;
        if ( query.length() == 0 ) {
            datasetPanel.clear();
        } else {
            spinLabel.setText("Searching...");
            spin.show();
            Util.getRPCService().getESGFDatasets(query+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);
        }
    }

    private String showFacets() {
        int i = 0;
        activePanel.clear();
        for ( Iterator cbIt = activeFacets.iterator(); cbIt.hasNext(); ) {
            final CheckBox box = (CheckBox) cbIt.next();
            Anchor remove = new Anchor("(x) "+box.getFormValue()+":"+box.getName());
            remove.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent remove) {
                    removeFacet(box);
                }
            });
            activePanel.add(remove);
            if ( cbIt.hasNext() ) {
                activePanel.add(new InlineLabel("   "));  
            }
            i++;
        }
        return getQuery();
    }
    private String getQuery() {
        StringBuffer query = new StringBuffer();
        for ( Iterator cbIt = activeFacets.iterator(); cbIt.hasNext(); ) {
            CheckBox box = (CheckBox) cbIt.next();
            query.append(box.getFormValue()+"="+box.getName());
            if ( cbIt.hasNext() ) {
                query.append("&");
            }
        }
        String queryText = searchText.getText();
        if ( queryText != null && !queryText.equals("") ) {
            query = query.append("&query="+URL.encode(queryText));
        }
        return query.toString();
    }
    private AsyncCallback<String> addDatasetCallback = new AsyncCallback<String>() {

        @Override
        public void onFailure(Throwable error) {
            spin.hide();
            Window.alert("Failed to add dataset.");
        }

        @Override
        public void onSuccess(String id) {
            
            spin.hide();
            eventBus.fireEvent(new ESGFDatasetAddedEvent());
            for ( int i = 0; i < datasetPanel.getWidgetCount(); i++) {
                HorizontalPanel entry = (HorizontalPanel) datasetPanel.getWidget(i);
                if ( entry.getWidgetCount() == 1 && entry.getElement().getId().equals(id)) {
                    String added = "&nbsp;&nbsp(Data set ready.)";
                    HTML addedlabel = new HTML(added);
                    entry.add(addedlabel);
                }
            }
        }
        
    };
    private AsyncCallback<List<ESGFDatasetSerializable>> datasetCallback= new AsyncCallback<List<ESGFDatasetSerializable>>() {

        @Override
        public void onFailure(Throwable arg0) {
            
            spin.hide();
            Window.alert("Dataset search failed to return any results.");
            
        }

        @Override
        public void onSuccess(List<ESGFDatasetSerializable> datasets) {
            spin.hide();
            datasetPanel.clear();
            for ( Iterator datasetIt = datasets.iterator(); datasetIt.hasNext(); ) {
                ESGFDatasetSerializable esgfDatasetSerializable = (ESGFDatasetSerializable) datasetIt.next();
                int position = esgfDatasetSerializable.getPosition()+1;
                final String id = esgfDatasetSerializable.getId();

                HorizontalPanel entry = new HorizontalPanel();
                Anchor dataset = new Anchor(position+". " +esgfDatasetSerializable.getName());
                entry.getElement().setId(esgfDatasetSerializable.getLASID());
              
                entry.add(dataset);
                if ( esgfDatasetSerializable.isAlreadyAdded() ) {
                    String added = "&nbsp;&nbsp(Data set ready.)";
                    HTML addedlabel = new HTML(added);
                    entry.add(addedlabel);
                }
                dataset.addClickHandler(new ClickHandler(){

                    @Override
                    public void onClick(ClickEvent click) {

                        spinLabel.setText("Searching for dataset catalog for dataset "+id+" ... ");
                        spin.show();
                        Util.getRPCService().addESGFDataset(id, addDatasetCallback);

                    }

                });
                datasetPanel.add(entry);
            }
            if ( datasets.size() > 0 ) {
                final int start = datasets.get(0).getPosition();
                ESGFDatasetSerializable last = datasets.get(datasets.size()-1);
                final int end = last.getPosition() + 1;
                int total = last.getTotal();

                HorizontalPanel page = new HorizontalPanel();
                if ( start >= limit ) {
                    int previousstart = start - limit;
                    if ( previousstart == 0 ) previousstart = 1;
                    Anchor previous = new Anchor(previousstart+"..."+start);
                    previous.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent arg0) {
                            String query = getQuery();
                            offset = offset - limit;
                            spinLabel.setText("Getting previous list...");
                            spin.show();
                            Util.getRPCService().getESGFDatasets(query+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);

                        }

                    });
                    page.add(previous);

                }

                final int nextend;
                if ( end+limit > total ) {
                    nextend = total;
                } else {
                    nextend = end + limit;
                }
                final int nextstart = end+1;
                if ( nextstart < total ) {
                    Anchor next = new Anchor(nextstart+"..."+nextend);
                    next.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent click) {
                            String query = getQuery();
                            offset = end;
                            spinLabel.setText("Getting next list...");
                            spin.show();
                            Util.getRPCService().getESGFDatasets(query+"&access=LAS&limit="+limit+"&offset="+offset, datasetCallback);

                        }

                    });
                    if ( page.getWidgetCount() > 0 ) {
                        page.add(new HTML("&nbsp;&nbsp;&nbsp...&nbsp;&nbsp;&nbsp"));
                    }
                    page.add(next);    
                }
                datasetPanel.add(page);
            }  
        }
    };
    public void addCloseHandler(ClickHandler handler) {
        close.addClickHandler(handler);
    }
}
