package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.UpdateFinishedEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.AlertButton;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintLabel;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintTextDisplay;
import gov.noaa.pmel.tmap.las.client.laswidget.DateTimeWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.laswidget.SelectionConstraintMenu;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * A viewer that will build a table of property-property plots based on the pairs in the LAS config tabledap property <thumbnails>var1_id var2_id,var3_id var4_id</thumbnails>
 * The metadata fields used in the header come from <thumbnail_metadata>investigators-socatV3_c6c1_d431_8194, vessel_name-socatV3_c6c1_d431_8194, qc_flag-socatV3_c6c1_d431_8194</thumbnail_metadata>
 * 
 * @author rhs
 * 
 */
public class ThumbnailPropProp implements EntryPoint {


    Map<String, String> metadata = new HashMap<String, String>();
    // The current request.
    LASRequest lasRequest;

    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();

    RootPanel display;

    HorizontalPanel toprow = new HorizontalPanel();
    AlertButton submit = new AlertButton("Update Plots", Constants.UPDATE_NEEDED);

    SelectionConstraintMenu idMenu = new SelectionConstraintMenu();
    
    String catid;
    String dsid;
    Map<String, VariableSerializable> xAllDatasetVariables = new HashMap<String, VariableSerializable>();
    List<List<String>> plot_pairs;
    List<LASRequest> requests = new ArrayList<LASRequest>();
    ConstraintSerializable idConstraint;
    
    FlexTable plots = new FlexTable();
    
    FlexTable metadataTable = new FlexTable();
    
    String spinurl = URLUtil.getImageURL() + "/mozilla_blu.gif";
    
    int currentPlot = 0;
    int columns = 4;
    
    String netcdf;
    
    int plotwidth = 600;
    int plotheight = 600;
    
    String currentTitle;
    
    LASRequest currentRequest;
    
    String dataurl;
    String erddap_id;
    
    String xlo;
    String xhi;
    String ylo;
    String yhi;
    String tlo;
    String thi;
    
    String varid;
    
    Map<String, String> thumbnail_properties;
    
    ConstraintTextDisplay fixedConstraintPanel = new ConstraintTextDisplay();
    

    @Override
    public void onModuleLoad() {
        
        

        idMenu.setVisibleItemCount(1);
        // Do not allow the widget to adjust its size.
        idMenu.setAdjust(false);
        // Send a change event when the menu has finished loading.
        idMenu.setNotify(true);
        toprow.add(idMenu);
        

        submit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                firePlots();
            }

        });
        toprow.add(new HTML("&nbsp;&nbsp"));
        toprow.add(submit);
        toprow.setStyleName("controls");
        RootPanel.get("controls").add(toprow);
        RootPanel.get("constraints").add(fixedConstraintPanel);
        RootPanel.get("metadata").add(metadataTable);
        RootPanel.get("plots").add(plots);

        // The main div where the plots are to be displayed.
        display = RootPanel.get("display");


        catid = Util.getParameterString("catid");
        String xml = Util.getParameterString("xml");
        dataurl = Util.getParameterString("dataurl");
        varid = null;
        if (xml != null && !xml.equals("")) {
            xml = Util.decode(xml);
            lasRequest = new LASRequest(xml);
            xlo = lasRequest.getRangeLo("x", 0);
            xhi = lasRequest.getRangeHi("x", 0);
            ylo = lasRequest.getRangeLo("y", 0);
            yhi = lasRequest.getRangeHi("y", 0);
            tlo = lasRequest.getRangeLo("t", 0);
            thi = lasRequest.getRangeHi("t", 0);
            dsid = lasRequest.getDataset(0);
            varid = lasRequest.getVariable(0);
            Util.getRPCService().getConfig(null, catid, dsid, varid, getGridCallback);
        } else {
            Window.alert("This app must be launched from the main interface.");
        }
        
        ConstraintLabel cta_xlo = new ConstraintLabel(Constants.X_CONSTRAINT, dsid, "longitude", "longitude", xlo, "longitude", xlo, "ge");
        fixedConstraintPanel.add(cta_xlo);   
        ConstraintLabel cta_xhi = new ConstraintLabel(Constants.X_CONSTRAINT, dsid, "longitude", "longitude", xhi, "longitude", xhi, "le");
        fixedConstraintPanel.add(cta_xhi);   
        ConstraintLabel cta_ylo = new ConstraintLabel(Constants.Y_CONSTRAINT, dsid, "latitude", "latitude", ylo, "latitude", ylo, "ge");
        fixedConstraintPanel.add(cta_ylo);   
        ConstraintLabel cta_yhi = new ConstraintLabel(Constants.Y_CONSTRAINT, dsid, "latitude", "latitude", yhi, "latitude", yhi, "le");
        fixedConstraintPanel.add(cta_yhi);   
        ConstraintLabel cta_tlo = new ConstraintLabel(Constants.T_CONSTRAINT, dsid, "time", "time", tlo, "time", tlo, "ge");
        fixedConstraintPanel.add(cta_tlo);   
        ConstraintLabel cta_thi = new ConstraintLabel(Constants.T_CONSTRAINT, dsid, "time", "time", thi, "time", thi, "le");
        fixedConstraintPanel.add(cta_thi); 
        
        Util.getRPCService().getConfig(null, catid, dsid, varid, datasetCallback);
        
        eventBus.addHandler(AddSelectionConstraintEvent.TYPE, new AddSelectionConstraintEvent.Handler() {

            @Override
            public void onAdd(AddSelectionConstraintEvent event) {
                boolean fire = false;
                String value = event.getValue();
                String key = event.getKey();
                String op = event.getOp();
                if ( idConstraint == null ) {
                    // First time, so fire the plot
                    fire = true;
                }
                idConstraint = new ConstraintSerializable(Constants.TEXT_CONSTRAINT, null, null, key, op, value, key+"_"+value, key);
                if ( fire ) {
                    firePlots();
                }
                if ( submit.getCheckBoxValue() ) {
                    // Auto updates on, go
                    firePlots();
                } else {
                    // Make the button red.
                    eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(submit.getCheckBoxValue(), false, true), ThumbnailPropProp.this);
                }
            }
        });


    }
    AsyncCallback<ConfigSerializable> datasetCallback = new AsyncCallback<ConfigSerializable>() {

        @Override
        public void onFailure(Throwable caught) {

            Window.alert("Could not get the variables list from the server.");

        }

        @Override
        public void onSuccess(ConfigSerializable config) {
            List<ERDDAPConstraintGroup> gs = config.getConstraintGroups();
            
            CategorySerializable cat = config.getCategorySerializable();
            
            
            String defaulty = null;
            String defaultcb = null;
            
            VariableSerializable[] variables = null;
            if (cat != null && cat.isVariableChildren()) {
                DatasetSerializable ds = cat.getDatasetSerializable();
                variables = ds.getVariablesSerializable();
                
                
            }
            
            
        }
    };
    private void setFixedConstraintsFromRequest(List<Map<String, String>> vcs) {
        for (Iterator vcIt = vcs.iterator(); vcIt.hasNext();) {
            Map<String, String> con = (Map<String, String>) vcIt.next();
            String varid = con.get("varID");
            String op = con.get("op");
            String value = con.get("value");
            String id = con.get("id");
            String type = con.get("type");
            if ( type.equals(Constants.VARIABLE_CONSTRAINT) ) {
                VariableSerializable v = xAllDatasetVariables.get(varid);
                ConstraintLabel cta = new ConstraintLabel(Constants.VARIABLE_CONSTRAINT, dsid, varid, v.getName(), value, v.getName(), value, op);
                fixedConstraintPanel.add(cta);
            } else if ( type.equals(Constants.TEXT_CONSTRAINT) ) {
                String lhs = con.get("lhs");
                String rhs = con.get("rhs");
                if ( rhs.contains("_ns_") ) {
                    String[] r = rhs.split("_ns_");
                    for (int i = 0; i < r.length; i++) {
                        ConstraintLabel cta = new ConstraintLabel(Constants.TEXT_CONSTRAINT, dsid, lhs, lhs, r[i], lhs, r[i], "is");
                        fixedConstraintPanel.add(cta);
                    }

                } else{
                    ConstraintLabel cta = new ConstraintLabel(Constants.TEXT_CONSTRAINT, dsid, lhs, lhs, rhs, lhs, rhs, "eq");
                    fixedConstraintPanel.add(cta);
                }
            }
        }
    }
    private void firePlots() {
        if ( plot_pairs != null ) {
            fetchMetadata();
            currentPlot = 0;
            plots.clear(); 
            // Start the cascade by getting the netCDF file and the first plot done.   
            LASRequest lr0 = makePlot(0);
            sendPlot(lr0);
            plots.setWidget(row(currentPlot), column(currentPlot), new Image(spinurl));
        } else {
            Window.alert("No plots defined in this data set configuration.");
        }
    }
    private void sendPlot(LASRequest lr0) {
        currentRequest = lr0;
        String url = Util.getProductServer() + "?xml=" + URL.encode(lr0.toString());
        RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, url);
        try {
            sendRequest.sendRequest(null, lasRequestCallback);
        } catch (RequestException e) {
            Window.alert("Unable to make plot requests.");
        }
    }
    private LASRequest makePlot(int plotindex) {
        LASRequest lr0 = lasRequest;
        if (plotindex == 0 ) {
            lr0.setOperation("Trajectory_thumbnail_extract_and_plot", "V7");
            lr0.removeProperty("data_0", "url");
            lr0.removeProperty("data_1", "url");
            lr0.removeProperty("data_2", "url");
            lr0.removeProperty("data_0", "file");
            lr0.removeProperty("data_1", "file");
            lr0.removeProperty("data_2", "file");
        } else {
            // Set the netcdf file and operation for a plot from the cached file.
            lr0.setOperation("Trajectory_correlation_plot", "V7");
            lr0.setProperty("data_0", "url", netcdf);
            lr0.setProperty("data_1", "url", netcdf);
            lr0.setProperty("data_2", "url", netcdf);
        }
        lr0.setProperty("ferret", "thumb", "1");
        lr0.removeVariables();
        String id0 = plot_pairs.get(plotindex).get(0);
        String id1 = plot_pairs.get(plotindex).get(1);
        String id2 = null;
        if ( plot_pairs.get(plotindex).size() > 2 ) {
        	id2 = plot_pairs.get(plotindex).get(2);
        }
        lr0.addVariable(dsid, id0, 0);
        lr0.addVariable(dsid, id1, 0);
        if ( id2 != null ) {
            lr0.addVariable(dsid, id2, 0);
        }
        lr0.removeConstraints();
        lr0.addConstraint(idConstraint);
        VariableSerializable v0 = xAllDatasetVariables.get(id0);
        VariableSerializable v1 = xAllDatasetVariables.get(id1);
        VariableSerializable v2 = null;
        if ( id2 != null ) {
            v2 = xAllDatasetVariables.get(id2);
        }
        currentTitle = v1.getName()+" vs "+v0.getName();
        if ( v2 != null ) {
        	currentTitle = currentTitle + " colored by " + v2.getName();
        }
        GridSerializable g0 = v0.getGrid();
        lr0.setRange("x", g0.getXAxis().getLo(), g0.getXAxis().getHi(), 0);
        lr0.setRange("y", g0.getYAxis().getLo(), g0.getYAxis().getHi(), 0);
        String tlo = g0.getTAxis().getLo();
        String thi = g0.getTAxis().getHi();
        tlo = DateTimeWidget.reformat(tlo);
        thi = DateTimeWidget.reformat(thi);
        lr0.setRange("t", tlo, thi, 0);
        lr0.setProperty("las", "output_type", "xml");
        lr0.setProperty("data", "count", "2");
        if ( id2 != null ) {
            lr0.setProperty("data", "count", "3");
        }
        return lr0;
    }
    private int row (int index) {
        return index/columns;
    }
    private int column (int index) {
        return index%columns;
    }
    
    RequestCallback lasRequestCallback = new RequestCallback() {

        @Override
        public void onError(Request request, Throwable exception) {
                    
            Window.alert("Product request failed.");

        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            String doc = response.getText();
            String imageurl = "";
            String annourl = "";
            // Look at the doc. If it's not obviously XML, treat it as HTML.
            if (doc.length() <= 1 || !doc.substring(0, doc.length()/2).contains("<?xml")) {
                
                ScrollPanel sp = new ScrollPanel();
                HTML result = new HTML(HtmlSanitizerUtil.sanitizeHtml(doc));
                sp.add(result);
                plots.setWidget(0, 0, sp);
                
            } else {
               
                eventBus.fireEventFromSource(new UpdateFinishedEvent(), this);
                doc = doc.replaceAll("\n", "").trim();
                Document responseXML = XMLParser.parse(doc);
                NodeList results = responseXML.getElementsByTagName("result");
                for (int n = 0; n < results.getLength(); n++) {
                    if (results.item(n) instanceof Element) {
                        Element result = (Element) results.item(n);
                        if (result.getAttribute("type").equals("image")) {
                            imageurl = result.getAttribute("url");
                        } else if (result.getAttribute("type").equals("netCDF")) {
                            netcdf = result.getAttribute("file");
                        } else if (result.getAttribute("type").equals("annotations")) {
                            annourl = result.getAttribute("url");
                        }
                    }
                }
               
                Image plot = new Image(imageurl);
                currentRequest.setOperation("SPPV", "V7");
                currentRequest.removeProperty("ferret", "thumb");
                plotwidth = (Window.getClientWidth()-80)/4;
                plotheight = (Window.getClientHeight()-80)/3;
                final String url = Util.getProductServer() + "?xml=" + URL.encode(currentRequest.toString()) + "&catid="+catid;
                plot.addClickHandler(new ClickHandler() {
                    
                    @Override
                    public void onClick(ClickEvent event) {
                        Window.open(url, "blank_", Constants.WINDOW_FEATURES);
                        
                    }
                });
                plot.setSize(plotwidth+"px", plotheight+"px");
                plot.setTitle(currentTitle);
                plots.setWidget(row(currentPlot), column(currentPlot), plot);              
                currentPlot++;
                if ( currentPlot > 0 ) {
                    String stream = "&stream=true&stream_ID=plot_image";
                    while ( currentPlot < plot_pairs.size() ) {
                       LASRequest r = makePlot(currentPlot);
                       String nexturl = Util.getProductServer() + "?xml=" + URL.encode(r.toString()) + stream;
                       Image nextplot = new Image(nexturl);
                       r.setOperation("SPPV", "V7");
                       r.removeProperty("ferret", "thumb");
                       r.removeProperty("data_0", "file");
                       r.removeProperty("data_1", "file");
                       r.removeProperty("data_2", "file");
                       r.removeProperty("data_3", "file");
                       r.removeProperty("data_0", "url");
                       r.removeProperty("data_1", "url");
                       r.removeProperty("data_2", "url");
                       r.removeProperty("data_3", "url");
                       final String nurl = Util.getProductServer() + "?xml=" + URL.encode(r.toString()) + "&catid="+catid;
                       nextplot.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            Window.open(nurl, "blank_", Constants.WINDOW_FEATURES);
                            
                        }
                           
                       });
                       nextplot.setSize(plotwidth+"px", plotheight+"px");
                       nextplot.setTitle(currentTitle);
                       plots.setWidget(row(currentPlot), column(currentPlot), nextplot);
                       currentPlot++;
                    } 
                        netcdf = null;
                    
                }
            }
        }

    };
    
    AsyncCallback<ConfigSerializable> getGridCallback = new AsyncCallback<ConfigSerializable>() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not fetch grid.  " + caught.getLocalizedMessage());

        }

        @Override
        public void onSuccess(ConfigSerializable config) {
            thumbnail_properties = null;
            CategorySerializable cat = config.getCategorySerializable();
            VariableSerializable[] variables = null;
            if (cat != null && cat.isVariableChildren()) {
                DatasetSerializable ds = cat.getDatasetSerializable();
                thumbnail_properties = ds.getProperties().get("thumbnails");
                
                erddap_id = ds.getProperties().get("tabledap_access").get("id");
                
                variables = ds.getVariablesSerializable();
            }
            if ( variables == null ) {
                Window.alert("Could not get varaibles from server.");
            } else {
                for (int i = 0; i < variables.length; i++) {
                    VariableSerializable vs = variables[i];
                    xAllDatasetVariables.put(vs.getID(), vs);
                }
                setFixedConstraintsFromRequest(lasRequest.getVariableConstraints());

                List<ERDDAPConstraintGroup> constraintGroups = config.getConstraintGroups();
                
                for (Iterator iterator = constraintGroups.iterator(); iterator.hasNext();) {
                    ERDDAPConstraintGroup constraintGroup = (ERDDAPConstraintGroup) iterator.next();
                    if ( constraintGroup.getType().equals("selection") ) {
                        idMenu.init(constraintGroup.getConstraints().get(0), constraintGroup.getDsid(), constraintGroup.getConstraints().get(0).getKey());
                        // Constrain the list according of the incoming request.
                        idMenu.setConstraints(fixedConstraintPanel.getConstraints());
                        idMenu.load(constraintGroup.getConstraints().get(0).getVariables().get(0).getID(), constraintGroup.getConstraints().get(0).getVariables().get(0).getName());
                    }
                }
            }
            plot_pairs = new ArrayList<List<String>>();
            if ( thumbnail_properties != null) {
                String p = thumbnail_properties.get("coordinate_pairs");
                if ( p != null && !p.equals("") ) {
                	String[] pair_strings = p.split("\\s+");
                	for (int i = 0; i < pair_strings.length; i++) {
                		String[] pseparate = pair_strings[i].split(",");
                		List<String> apair = new ArrayList<String>();
                		for (int j = 0; j < pseparate.length; j++) {
                			apair.add(pseparate[j].trim());
                		}
                		plot_pairs.add(apair);
                	}
                }
                String d = thumbnail_properties.get("variable_pairs");
                if ( d != null && !d.equals("") ) {
                	String[] data_strings = d.split("\\s+");
                	for (int i = 0; i < data_strings.length; i++) {
                		String[] pseparate = data_strings[i].split(",");
                		List<String> apair = new ArrayList<String>();
                		// Add only those data pairs that match the current variable.
                		if (pseparate.length > 1 ) {
                			if ( varid.equals(pseparate[0]) || varid.equals(pseparate[1]) ) {
                				for (int j = 0; j < pseparate.length; j++) {
                					apair.add(pseparate[j].trim());
                				}
                				plot_pairs.add(apair);
                			}
                		}                
                	}
                }
            }
        }
    };
    AsyncCallback<String> jsonCallback = new AsyncCallback<String>() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not fetch metadata.  " + caught.getLocalizedMessage());

        }

        @Override
        public void onSuccess(String json) {
            /*
             

{
  "table": {
    "columnNames": ["expocode", "vessel_name", "investigators", "qc_flag"],
    "columnTypes": ["String", "String", "String", "String"],
    "columnUnits": [null, null, null, null],
    "rows": [
      ["01AA20110928", "Nuka Arctica", "Truls Johannessen ; Abdirahman Omar ; Ingunn Skjelvan", "N"]
    ]
  }
}             
             
             
             */
            JSONValue jsonV = JSONParser.parseStrict(json);
            JSONObject jsonO = jsonV.isObject();
            if ( jsonO != null) {
                JSONObject table = (JSONObject) jsonO.get("table");
                JSONArray names = (JSONArray) table.get("columnNames");
                JSONArray rows = (JSONArray) table.get("rows");
                JSONArray r0 = (JSONArray) rows.get(0);
                for (int i = 0; i < names.size(); i++) {
                    
                    String name = names.get(i).toString();
                    if  ( name.endsWith("") ) name = name.substring(0,name.length()-1);
                    if  ( name.startsWith("") ) name = name.substring(1,name.length());

                    String value = r0.get(i).toString();
                    if  ( value.endsWith("") ) value = value.substring(0,value.length()-1);
                    if  ( value.startsWith("") ) value = value.substring(1,value.length());
                    
                    metadata.put(name.toUpperCase(Locale.ENGLISH), value);
                }
            }
            showMetadata();
        }
        
            
    };
    private void fetchMetadata() {
        String metadata = thumbnail_properties.get("metadata");        
        String id0 = xAllDatasetVariables.keySet().iterator().next();
        String trajectory_id = idConstraint.getRhs();
        clearMetadata();
        Util.getRPCService().getERDDAPJSON(dsid, id0, trajectory_id, metadata, jsonCallback);
     
        
    }
    private void clearMetadata() {
        metadata.clear();
        metadataTable.clear();
    }
    private void showMetadata() {
        int index = 0;
        for (Iterator metaIt = metadata.keySet().iterator(); metaIt.hasNext();) {
            String name = (String) metaIt.next();
            String value = metadata.get(name);
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            builder.appendHtmlConstant("<strong>").append(HtmlSanitizerUtil.sanitizeHtml(name)).appendHtmlConstant(":</strong>&nbsp;&nbsp;&nbsp;").append(HtmlSanitizerUtil.sanitizeHtml(value));
            metadataTable.setWidget(0, index, new HTML(builder.toSafeHtml()));
            index++;
        }
    }
}
