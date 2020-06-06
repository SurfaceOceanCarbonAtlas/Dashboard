package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A client interface for editing one of the values in a trajectory database
 * 
 * @author rhs
 * 
 */
public class ColumnEditorWidget extends Composite {

    Set<String> trajectoryIDs = new HashSet<String>();

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

    String trajectory_id;

    String[] headers;

    int selectedIndex = 0;
    int selectedWoceNameIndex = 0;

    DropDown flags = new DropDown();
    ScrollPanel datascroll = new ScrollPanel();
    HorizontalPanel expoControls = new HorizontalPanel();
    HorizontalPanel woceControls = new HorizontalPanel();
    HorizontalPanel flagControls = new HorizontalPanel();
    HorizontalPanel commentControls = new HorizontalPanel();
    HorizontalPanel openMinimizeControls = new HorizontalPanel();
    VerticalPanel mainpanel = new VerticalPanel();
    DropDown ids = new DropDown();
    DropDown woces = new DropDown();
    FlexTable datatable = new FlexTable();
    FlexTable headertable = new FlexTable();
    SuggestBox comment; 
    MultiWordSuggestOracle oracle; 
    PushButton submit = new PushButton("Save Flags");
    HTML commentL;
    Map<String, List<String[]>> allrows = new HashMap<String, List<String[]>>();
    
    String datavar; // This is the variable that was on the vertical axis.

    int windowWidth;
    int windowHeight;

    int columnOffset = 3;
    
    int headerRows = 0;
    
    List<String> comments = new ArrayList<String>();

    // The dirty rows for the current ID.
    Map<Integer, String[]> dirtyrows = new HashMap<Integer, String[]>();

    VerticalPanel allNone = new VerticalPanel();
    Anchor all = new Anchor("all");
    Anchor none = new Anchor("none");
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    

    public ColumnEditorWidget(String catid, String xml, String datavarname, List<String> wocenames) {

        oracle = new MultiWordSuggestOracle();
        comment = new SuggestBox(oracle);
        
        // Save the initial size of the browser.
        windowWidth = Window.getClientWidth();
        windowHeight = Window.getClientHeight();

        allNone.add(all);
        allNone.add(none);
        all.addStyleName("nowrap");
        none.addStyleName("nowrap");

        // Big font.
        submit.addStyleName("nowrap");
        
        all.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                for (int i = headerRows; i < datatable.getRowCount(); i ++ ) {
                    CheckBox box = (CheckBox) datatable.getWidget(i, 0);
                    box.setValue(true);
                    setValue(box, false);
                }
            }

        });

        none.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
               
                for (Iterator<Integer> dirtyIt = dirtyrows.keySet().iterator(); dirtyIt.hasNext();) {
                    Integer row = (Integer) dirtyIt.next();
                    // Programatically turn off the check box.
                    CheckBox box = (CheckBox) datatable.getWidget(row, 0);
                    box.setValue(false);
                    turnOffRow(row);
                    
                }
                dirtyrows.clear();
            }
            
        });

        // Add a listener for browser resize events. 
        Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {

                int height = event.getHeight();
                int width = event.getWidth();
                windowWidth = width; 
                windowHeight = height;

                // shorten the window for the new browser size.
                resize();
            }

        });

        HTML expointro = new HTML("&nbsp;&nbsp;Choose an EXPOCODE to edit:&nbsp;&nbsp;");
        expointro.addStyleName("nowrap");
        expoControls.add(expointro);
        ids.addStyleName("nowrap");
        expoControls.add(ids);

        HTML woceintro = new HTML("&nbsp;&nbsp;Choose an WOCE flag to edit:&nbsp;&nbsp;");
        woceintro.addStyleName("nowrap");
        woceControls.add(woceintro);
        for ( String name : wocenames )
        	woces.addItem(name);
        woces.addStyleName("nowrap");
        woceControls.add(woces);

        commentL = new HTML("&nbsp;&nbsp;Comment:&nbsp;&nbsp;");
        commentL.addStyleName("nowrap");
        commentControls.add(commentL);
        comment.setWidth("50em");
        comment.setStyleName("nowrap");
        comment.getValueBox().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
              comment.showSuggestionList();
            }
          });
        commentControls.add(comment);       

        flags.addItem("2");
        flags.addItem("3");
        flags.addItem("4");
        flags.addStyleName("nowrap");
        HTML flagL = new HTML("&nbsp;&nbsp;Set flag of selected rows to:&nbsp;&nbsp;");
        flagL.addStyleName("nowrap");
        flagControls.add(flagL);
        flagControls.add(flags);

        submit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
               
                String id = ids.getValue();
                List<String[]> currentRows = allrows.get(id);
                JSONObject message = new JSONObject();
                String temp_file = lasRequest.getData(0);
                if ( temp_file != null ) {
                    message.put("temp_file", new JSONString(temp_file));
                }
                String comment_text = comment.getText();
                if ( comment_text != null && !comment_text.equals("") ) {
                    String encodedText = hexEncode(comment_text);
                    message.put("comment", new JSONString(encodedText));
                    oracle.add(comment_text);
                    comments.add(comment_text);
                    oracle.setDefaultSuggestionsFromText(comments);
                    comment.setText("");
                } else {
                    Window.alert("You must include a comment with your flags.");
                    return;
                }
                JSONArray edits = new JSONArray();
                int index = 0;
                for (Iterator<Integer> dirtyIt = dirtyrows.keySet().iterator(); dirtyIt.hasNext();) {
                    Integer widgetRow = (Integer) dirtyIt.next();
                    int datarow = widgetRow - headerRows;
                    String[] parts = currentRows.get(datarow);
                    JSONObject row = new JSONObject();
                    for (int i = 0; i < parts.length; i++) {
                    	String lcheader = headers[i].toLowerCase();
                    	// Skip WOCE flag columns other than the currently selected one
                    	if ( lcheader.startsWith("woce_") && ! lcheader.equals(woces.getValue().toLowerCase()) )
                    		continue;
                        row.put(headers[i], new JSONString(parts[i]));
                    }
                    edits.set(index, row);
                    index++;
                }
                message.put("edits", edits);
                String url = Util.getProductServer().replace("/ProductServer.do", "")+"/saveEdits.do";
                RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.POST, url);
                try {
                    sendRequest.sendRequest(message.toString(), editCallback);
                } catch (RequestException e) {
                    Window.alert("Request to save edits failed.");
                }
            }

        });

        flagControls.add(new HTML("&nbsp;&nbsp"));
        flagControls.add(submit);
        flagControls.setStyleName("controls");
        
        openMinimizeControls.addStyleName("nowrap-brown");
        openMinimizeControls.setWidth("100%");
        openMinimizeControls.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        mainpanel.setSpacing(2);
        mainpanel.add(openMinimizeControls);
        mainpanel.add(expoControls);
        mainpanel.add(woceControls);
        mainpanel.add(flagControls);
        mainpanel.add(commentControls);
        
       
        PushButton close = new PushButton();
        Image closeUp = new Image(GWT.getModuleBaseURL()
                + "../images/close_off.png");
        Image closeDown = new Image(GWT.getModuleBaseURL()
                + "../images/close_on.png");
        close = new PushButton(closeUp, closeDown,new ClickHandler() {         
            public void onClick(ClickEvent event) { 
                if ( !isDirty() ) {
                    eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(true), ColumnEditorWidget.this);    
                } else {
                    Window.alert("You have unsaved changes. Clear or save your edits if you want to change EXPOCODE.");
                }
            }
        });
        close.setStylePrimaryName("EDITOR-PushButton");
        close.setTitle("Close the editor");
        close.setSize("22px", "22px");
        close.addStyleName("top-right");
        
        Image minimizeUp = new Image(GWT.getModuleBaseURL()
                + "../images/minus_off.png");
        Image minimizeDown = new Image(GWT.getModuleBaseURL()
                + "../images/plus_on.png");
       
        ToggleButton minimize = new ToggleButton(minimizeUp, minimizeDown,new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                if ( isTableVisible() ) {
                    setTableVisible(false);
                } else {
                    setTableVisible(true);
                }
                
            }
        });
        minimize.setStylePrimaryName("EDITOR-PushButton");
        minimize.setTitle("Hide the table to see the plot underneath");
        minimize.setSize("22px", "22px");
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(minimize);
        buttons.add(close);
        
        openMinimizeControls.add(buttons);
               
        headertable.addStyleName("headertable");
        mainpanel.add(headertable);
        datascroll.add(datatable);
        mainpanel.add(datascroll);
        datatable.setStyleName("datatable");
        datatable.setWidget(0, 0, new HTML("Requesting data to edit..."));
        if (xml != null && !xml.equals("")) {
            xml = Util.decode(xml);
            lasRequest = new LASRequest(xml);
            datavar = datavarname;
            initialState = lasRequest;
            lasRequest.setProperty("ferret", "data_format", "csv");
            lasRequest.setOperation("Trajectory_Corrrelation_File", "V7");
            lasRequest.setProperty("product_server", "use_cache", "false");
            String url = Util.getProductServer() + "?xml=" + URL.encode(lasRequest.toString());
            // Test file any CSV trajectory data: String url = Util.getProductServer().replace("/ProductServer.do", "")+"/output/short_ferret_listing.txt";
            RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, url);
            try {
                sendRequest.sendRequest(null, lasRequestCallback);
            } catch (RequestException e) {
                Window.alert("Failed to send request to load table data.");
            }
        } else {
            Window.alert("This app must be launched from the main interface.");
        }
        
        eventBus.addHandler(StringValueChangeEvent.TYPE, IdChangeHandler);
        
        
        initWidget(mainpanel);
    }

    /**
     * Encode a string as a hex string to ensure contents are not interpreted
     * as something other than a string parameter.  Written in JavaScript to
     * ensure to no questions about the encoding in the JavaScript created 
     * and for consistency with SOCAT_QC_input.vm
     *
     * @param original
     *     string to be encoded
     * @returns
     *     hex string
     */
    private static native String hexEncode(String original) /*-{
        var hex, k;
        var result = "";
        for (k = 0; k < original.length; k++) {
        hex = original.charCodeAt(k).toString(16);
            result += ("000" + hex).slice(-4);
        }
        return result;
    }-*/;

    private void columns() {
        CellFormatter cellFormatter = datatable.getCellFormatter();
        // The first thing is not a header, so you have to go one more.
        for (int i = 0; i < headers.length + 1; i++) {
            int hwidth = headertable.getWidget(0, i).getOffsetWidth();        
            int dwidth = datatable.getWidget(0, i).getOffsetWidth();
            int fwidth = Math.max(hwidth, dwidth);        
            headertable.getWidget(0, i).setWidth(fwidth+"px");
            
            for (int j = 0; j < datatable.getRowCount(); j++) {
                cellFormatter.setWidth(j, i, fwidth+"px");
                //datatable.getWidget(j, i).setWidth(fwidth+"px");
            }
            
        }
        resize();
    }
    private void resize() {
        // Set the size of data display scroll panel so that it fills the browser. 
        int width = datatable.getOffsetWidth() + 20;
        int height = Math.max(windowHeight - datascroll.getAbsoluteTop(), 0);
        height = height - 120;
        if ( height < 0 ) height = 120;
        datascroll.setSize(width + "px", height + "px");
        int cwidth = width - commentL.getOffsetWidth();
        comment.setWidth(cwidth+"px");
    }
    StringValueChangeEvent.Handler IdChangeHandler = new StringValueChangeEvent.Handler() {

        @Override
        public void onValueChange(StringValueChangeEvent event) {
            Object source = event.getSource();
            if (source.equals(ids)) {
                if ( dirtyrows.size() > 0 ) {
                    ids.setSelectedIndex(selectedIndex);
                    Window.alert("You have unsaved changes. Clear or save your edits if you want to change EXPOCODE.");
                } else {
                    String id = event.getValue();
                    List<String[]> rows = allrows.get(id);
                    datascroll.remove(datatable);
                    datatable = new FlexTable();
                    datatable.addStyleName("datatable");
                    datascroll.add(datatable);
                    setHeaders();
                    int datarow = headerRows;
                    for (int i = 0; i < rows.size(); i++) {
                        String[] parts = rows.get(i);
                        addRow(parts, datarow);
                        datarow++;
                    }
                    selectedIndex = ids.getSelectedIndex();
                    columns();
                }
            } else if ( source.equals(woces) ) {
                if ( dirtyrows.size() > 0 ) {
                    woces.setSelectedIndex(selectedWoceNameIndex);
                    Window.alert("You have unsaved changes. Clear or save your edits if you want to change WOCE flag.");
                } else {
                	// Rebuild the table to show the newly selected WOCE flag
                    String id = ids.getValue();
                    List<String[]> rows = allrows.get(id);
                    datascroll.remove(datatable);
                    datatable = new FlexTable();
                    datatable.addStyleName("datatable");
                    datascroll.add(datatable);
                    setHeaders();
                    int datarow = headerRows;
                    for (int i = 0; i < rows.size(); i++) {
                        String[] parts = rows.get(i);
                        addRow(parts, datarow);
                        datarow++;
                    }
                    selectedWoceNameIndex = woces.getSelectedIndex();
                    columns();
                }
            } else if ( source.equals(flags) ) {
                for (Iterator<Integer> dirtyIt = dirtyrows.keySet().iterator(); dirtyIt.hasNext();) {
                    Integer widgetRow = (Integer) dirtyIt.next();
                    CheckBox box = (CheckBox) datatable.getWidget(widgetRow, 0);
                    setValue(box, false);
                }

            }
        }

    };
    private void turnOffRow(int row) {
        CellFormatter formatter = datatable.getCellFormatter();
        int dataRow = row - headerRows;
        
        
        String[] oldnew = dirtyrows.get(row);
        HTML html = new HTML(oldnew[0]);
        html.setTitle(oldnew[0]);
        int width = headertable.getWidget(0, 1).getOffsetWidth();
        html.setWidth(width+"px");
        // Put the old value back in the data structure used to make the JSON payload.
        List<String[]> affectedrow = allrows.get(ids.getValue());
        String[] parts = affectedrow.get(dataRow);
        for (int i = 0; i < parts.length; i++) {
            if ( headers[i].contains("WOCE") ) {
                parts[i] = oldnew[0];                        
            }
        }
        formatter.removeStyleName(row, 1, "dirty");
        datatable.setWidget(row, 1, html);
      
        
        
    }
    private void addRow(String[] parts, int datarow) {
        CheckBox box = new CheckBox();
        box.addStyleName("nowrap");
        box.setFormValue(String.valueOf(datarow));
        box.setTitle("Shift-click to select multiple rows");
        datatable.setWidget(datarow, 0, box);
        box.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                CheckBox box = (CheckBox) event.getSource();
                boolean shift = event.isShiftKeyDown();
                setValue(box, shift);
            }

        });

        CellFormatter cellformatter = datatable.getCellFormatter();
        int column = 0;
        for (int p = 0; p < parts.length; p++) {
            // The + columnOffset for the column with the check box and the column with flag
            String part = parts[p];
            if ( part.trim().equals("") ) part = "(none)";
            String wocevar = woces.getValue();
            if ( headers[p].toLowerCase().equals(wocevar.toLowerCase()) ) {
                HTML html = new HTML(part);
                html.setTitle(part);
                datatable.setWidget(datarow, 1, html);
                cellformatter.addStyleName(datarow, 1, "nowrap");
            } else if ( headers[p].toLowerCase().equals(datavar.toLowerCase()) ) {
                HTML html = new HTML(part);
                html.setTitle(part);
                datatable.setWidget(datarow, 2, html);
                cellformatter.addStyleName(datarow, 2, "nowrap");

            } else if ( ! headers[p].toLowerCase().startsWith("woce_") ) {
                // Do not show WOCE flags other than the selected one
                HTML html = new HTML(part);
                datatable.setWidget(datarow, column + columnOffset, html);
                cellformatter.addStyleName(datarow, column + columnOffset, "nowrap");
                column++;
            }
           
        }
        RowFormatter formatter = datatable.getRowFormatter();
        if ( datarow%2 == 0 ) {
            formatter.addStyleName(datarow, "nowrap");
        } else {
            formatter.addStyleName(datarow, "nowrap-brown");
        }
    }
    private void setValue(CheckBox box, boolean shift) {
        String value = flags.getValue();
        String row = box.getFormValue();
        int widgetRow = Integer.valueOf(row);
        int dataRow = widgetRow - headerRows;
        CellFormatter cellFormatter = datatable.getCellFormatter();
        if ( box.getValue() ) {
            String old = datatable.getWidget(widgetRow, 1).getTitle();
            HTML html = new HTML(value);
            html.setTitle(value);
            int width = headertable.getWidget(0, 1).getOffsetWidth();
            html.setWidth(width+"px");
            datatable.setWidget(widgetRow, 1, html);
            cellFormatter.addStyleName(widgetRow, 1, "dirty");
            // We're using this value for to build the save JSON payload.  :-)
            List<String[]> affectedrow = allrows.get(ids.getValue());
            String[] parts = affectedrow.get(dataRow);
            for (int i = 0; i < parts.length; i++) {
                if ( headers[i].contains("WOCE") ) {
                    parts[i] = value;
                }
            }
            //
            
            String[] oldAndNew = dirtyrows.get(widgetRow);
            if ( oldAndNew == null ) {
                oldAndNew = new String[]{old, value};
            } else {
                // Just set the new value. This is already dirty.
                oldAndNew[1] = value;
            }
            dirtyrows.put(widgetRow, oldAndNew);
            int startrow = -10;
            if ( shift ) {
                // Look backwards and set all between if found
                for (int i = widgetRow - 1; i >= headerRows; i--) {
                    CheckBox rowbox = (CheckBox) datatable.getWidget(i, 0);
                    if ( rowbox.getValue() ) {
                        startrow = i;
                        break;
                    }
                }
                if ( startrow > 0 ) {
                    for (int i = startrow + 1 ; i < widgetRow; i++ ) {
                        // Do the same thing you do for the "all" button to these rows.
                        CheckBox shiftbox = (CheckBox) datatable.getWidget(i, 0);
                        shiftbox.setValue(true);
                        setValue(shiftbox, false);
                    }
                }
            }

        } else {    
            turnOffRow(widgetRow);
            dirtyrows.remove(widgetRow);
            
            if ( shift ) {
                int startrow = -10;
                // Look back to first unchecked row and uncheck between.
                for (int i = widgetRow - 1; i >= headerRows; i--) {
                    CheckBox rowbox = (CheckBox) datatable.getWidget(i, 0);
                    if ( !rowbox.getValue() ) {
                        startrow = i;
                        break;
                    }
                }
                if ( startrow > 0 ) {
                    for (int i = startrow + 1 ; i < widgetRow; i++ ) {
                        
                        // Do the same thing you do for the "none" button to these rows.
                        
                        CheckBox shiftbox = (CheckBox) datatable.getWidget(i, 0);
                        shiftbox.setValue(false);
                        turnOffRow(i);
                        dirtyrows.remove(i);
                        
                    }
                }
            }
        }
    }
    RequestCallback editCallback = new RequestCallback() {

        @Override
        public void onResponseReceived(Request request, Response response) {
            String text = response.getText();
            PopupPanel popup = new PopupPanel(true);
            popup.add(new HTML("<strong>Saved edits for:<p></p></strong>"+text+"<p></p>Click outside box to dismiss."));
            popup.setPopupPosition(200, Window.getClientHeight()/3);
            popup.show();
            CellFormatter formatter = datatable.getCellFormatter();
            for (Iterator<Integer> dirtyIt = dirtyrows.keySet().iterator(); dirtyIt.hasNext();) {
                Integer widgetrow = (Integer) dirtyIt.next();
                for (int i = 0; i < headers.length; i++) {
                    formatter.removeStyleName(widgetrow, i, "dirty");
                }
                
                CheckBox box = (CheckBox) datatable.getWidget(widgetrow, 0);
                box.setValue(false);
            }
            dirtyrows.clear();
        }

        @Override
        public void onError(Request request, Throwable exception) {
            // TODO Auto-generated method stub
            
        }
        
    };
    RequestCallback lasRequestCallback = new RequestCallback() {

        @Override
        public void onError(Request request, Throwable exception) {


            Window.alert("Product request failed.");

        }

        @Override
        public void onResponseReceived(Request request, Response response) {

            String doc = response.getText();

            if ( doc != null && doc.length() > 300 && doc.substring(0, 300).contains("<html>") ) {
                HTML html = new HTML(doc);
                evalScripts(html.getElement());
                datatable.setWidget(0,0,html);
            } else {
                datatable.setWidget(0,0,new HTML("Loading editing table..."));
                boolean data = false;
                boolean header = false;
                boolean column_descriptions = false;
                int index = Integer.MAX_VALUE;
                String[] lines = doc.split("\n");
                int datarow = -1;
                String firstid = null;
                for (int i = 0; i < lines.length; i++) {

                    String line = lines[i].trim();

                    if ( line.startsWith("Column") ) {
                        column_descriptions = true;
                    } else if ( column_descriptions ) {
                        header = true;
                    }

                    if ( column_descriptions && header && !data ) {
                        headers = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        data = true;
                        index = i;
                        datarow = headerRows;
                        setHeaders();

                    } else if (i > index) {
                        String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        String id = parts[0];
                        if ( id.startsWith("\"")) id = id.substring(1, id.length());
                        if ( id.endsWith("\"")) id = id.substring(0,id.length()-1);
                        trajectoryIDs.add(id);
                        List<String[]> members;
                        if ( allrows.keySet().contains(id) ) {
                            members = allrows.get(id);
                        } else {
                            if ( firstid == null ) firstid = id;
                            members = new ArrayList<String[]>();
                            allrows.put(id, members);
                        }


                        for (int p = 0; p < parts.length; p++) {
                            if ( parts[p].startsWith("\"")) parts[p] = parts[p].substring(1, parts[p].length());
                            if ( parts[p].endsWith("\"")) parts[p] = parts[p].substring(0,parts[p].length()-1);
                        }
                        members.add(parts);
                        if ( id.equals(firstid) ) {
                            addRow(parts, datarow);
                            datarow++;
                        }
                    }

                }
                for (Iterator<String> idIt = trajectoryIDs.iterator(); idIt.hasNext();) {
                    String id = (String) idIt.next();
                    ids.addItem(id);
                }
            }
            columns();

        }
    };
    /**
     * Set the column headers.
     * 
     */
    private void setHeaders() {
        CellFormatter cellFormatter = headertable.getCellFormatter();
        headertable.setWidget(0, 0, new HTML(""));
        int column = 0;
        for (int p = 0; p < headers.length; p++) {
            if ( headers[p].startsWith("\"")) headers[p] = headers[p].substring(1, headers[p].length());
            if ( headers[p].endsWith("\"")) headers[p] = headers[p].substring(0,headers[p].length()-1);
            if ( headers[p].endsWith("_")) headers[p] = headers[p].substring(0, headers[p].length()-1);
            String wocevar = woces.getValue();
            if ( headers[p].toLowerCase().equals(wocevar.toLowerCase()) ) {
                headertable.setWidget(0, 1, new HTML(headers[p]));
                cellFormatter.addStyleName(0, 1, "nowrap");
            } else if (headers[p].toLowerCase().equals(datavar.toLowerCase()) ) {
                headertable.setWidget(0, 2, new HTML(headers[p]));
                cellFormatter.addStyleName(0, 2, "nowrap");
            } else if ( ! headers[p].toLowerCase().startsWith("woce_") ) {
                // Do not show WOCE flags other than the selected one
                headertable.setWidget(0, column + columnOffset, new HTML(headers[p]));
                cellFormatter.addStyleName(0, column + columnOffset, "nowrap");
                column++;
            }
        }
        headertable.setWidget(0, 0, allNone);
        RowFormatter formatter = headertable.getRowFormatter();
        formatter.addStyleName(0, "nowrap");
    }

    public boolean isDirty() {
        return dirtyrows.size() > 0;
    }
    
    /**
     * Evaluate scripts in an HTML string. Will eval both <script
     * src=""></script> and <script>javascript here</scripts>.
     * 
     * @param element
     *            a new HTML(text).getElement()
     */
    public static native void evalScripts(Element element)
    /*-{
        var scripts = element.getElementsByTagName("script");

        for (i = 0; i < scripts.length; i++) {
            // if src, eval it, otherwise eval the body
            if (scripts[i].hasAttribute("src")) {
                var src = scripts[i].getAttribute("src");
                var script = $doc.createElement('script');
                script.setAttribute("src", src);
                $doc.getElementsByTagName('body')[0].appendChild(script);
            } else {
                $wnd.eval(scripts[i].innerHTML);
            }
        }
    }-*/;


    public boolean isTableVisible() {
       return datascroll.isAttached();
    }


    public void setTableVisible(boolean b) {
        // Remove it or add it back...
        if ( b ) {
            mainpanel.add(datascroll);
        } else {
            mainpanel.remove(datascroll);
        }
    }
}
