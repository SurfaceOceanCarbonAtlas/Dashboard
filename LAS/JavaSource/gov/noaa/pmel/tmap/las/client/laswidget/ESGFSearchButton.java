package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;

public class ESGFSearchButton extends Composite {
    
    PopupPanel popupPanel = new PopupPanel(true);
    PushButton button = new PushButton("ESGF Search");
    ESGFSearchPanel searchPanel = new ESGFSearchPanel();
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    public ESGFSearchButton() {
        
        button.setTitle("Search ESGF for more data to use in this session.");
        button.addStyleDependentName("SMALLER");
        button.addClickHandler(clickHandler);
        searchPanel.addCloseHandler(close);
        popupPanel.add(searchPanel);
        
        initWidget(button);
      
    }
    
    ClickHandler clickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent click) {
            
            popupPanel.setPopupPosition(button.getAbsoluteLeft(), button.getAbsoluteTop());
            popupPanel.show();
            if ( !searchPanel.isReady() ) {
                searchPanel.init();
            } 
            
        }
        
    };
    
    public ClickHandler close = new ClickHandler() {

        @Override
        public void onClick(ClickEvent click) {
            popupPanel.hide();
        }
        
    };
    
    public void addOpenClickHandler(ClickHandler handler) {
        button.addClickHandler(handler);
    }
    
    public void addCloseHandler(ClickHandler handler) {
        searchPanel.addCloseHandler(handler);
    }
}
