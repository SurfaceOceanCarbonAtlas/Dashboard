package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.ColumnEditorWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.DropDown;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A client interface for editing one of the values in a trajectory database
 * 
 * @author rhs
 * 
 */
public class ColumnEditor implements EntryPoint {

    ColumnEditorWidget columnEditor;

    // There are 3 states we want to track.

    // The values for the first plot. This is used to determine if we need to
    // warn the user about fetching new data.
    LASRequest initialState;

    // The state immediately previous to a widget change that might cause the
    // prompt about new data.
    // If the user cancels the change, revert to this state.
    LASRequest undoState;

    // The current request.
    LASRequest lasRequest;

   
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    /**
     * A module to run the columneditor as a separate module (page). To do this, you must add the name of the variable and its WOCE flag to the URL as datavar and wocevar.
     */
    @Override
    public void onModuleLoad() {
  
        String catid = Util.getParameterString("catid");
        String xml = Util.getParameterString("xml");
        String datavarname = Util.getParameterString("datavar");
        String wocename = Util.getParameterString("wocevar");
      
        if (xml != null && !xml.equals("")) {
            xml = Util.decode(xml);
            lasRequest = new LASRequest(xml);
            String url = Util.getProductServer() + "?xml=" + URL.encode(lasRequest.toString());
            columnEditor = new ColumnEditorWidget(catid, xml, datavarname, Arrays.asList(wocename));
        } else {
            Window.alert("This app must be launched from the main interface.");
        }
        
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                if ( columnEditor.isDirty() ) {
                    closingEvent.setMessage("If you close or refresh the page your current changes will be lost.");
                }
            }
        });
        RootPanel.get("id_list").add(columnEditor);
    }
}