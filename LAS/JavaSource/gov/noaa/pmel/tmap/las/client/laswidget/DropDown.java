package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DropDown extends Composite {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    FlexTable current;
    FlexTable itemlist;
    PopupPanel dropdown;
    ScrollPanel scroller;
    HTML down;
    
    List<String> items = new ArrayList<String>();
    String value = null;
    int selectedIndex;
    
    public DropDown() {
        current = new FlexTable();
        current.addStyleName("datatable");
        itemlist = new FlexTable();
        dropdown = new PopupPanel(true);
        scroller = new ScrollPanel();
        scroller.add(itemlist);
        dropdown.add(scroller);
        down =new HTML(" &#9660;");
        down.addStyleName("current-item");
        initWidget(current);
        HTML load = new HTML("loading...");
        load.addStyleName("current-item");
        current.setWidget(0, 0, load);
        current.setWidget(0, 1, down);
        current.addClickHandler(show);
    }
    public ClickHandler show = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            if ( dropdown.isShowing() ) {
                dropdown.hide();
            } else {
                dropdown.setPopupPosition(current.getAbsoluteLeft(), current.getAbsoluteTop()+current.getOffsetHeight());
                for (int i = 0; i < items.size(); i++) {
                    MenuItem menuItem = new MenuItem(items.get(i));
                    itemlist.setWidget(i, 0, menuItem);
                }
                dropdown.show();
            }
            
        }
    };
    public String getValue() {
        return value;
    }
    public int getSelectedIndex() {
        return selectedIndex;
    }
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size() ) {
            value = items.get(index);
            HTML html = new HTML(value);
            html.addStyleName("current-item");
            current.setWidget(0, 0, html);
            selectedIndex = index;
        }
        
    }
  
    public void addItem(String item) {
        items.add(item);
        if ( items.size() == 1 ) {
            value = item;
            HTML currentValue = new HTML(value);
            currentValue.addStyleName("current-item");
            current.setWidget(0, 0, currentValue);
        }
    }
    public class MenuItem extends Composite {
        public MenuItem (String item) {
            final String itemvalue = item;
            HTML html = new HTML(item);
            initWidget(html);
            html.addStyleName("dropdown-item");
            html.addClickHandler(new ClickHandler() {
                
                @Override
                public void onClick(ClickEvent event) {              
                    value = itemvalue;
                    selectedIndex = items.indexOf(value);
                    HTML html = new HTML(itemvalue);
                    html.addStyleName("current-item");
                    current.setWidget(0, 0, html);
                    dropdown.hide();
                    eventBus.fireEventFromSource(new StringValueChangeEvent(itemvalue), DropDown.this);
                }
            });
        }
    }
}
