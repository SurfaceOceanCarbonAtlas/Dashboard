package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.CancelEvent;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.AlertButton;
import gov.noaa.pmel.tmap.las.client.laswidget.CancelButton;
import gov.noaa.pmel.tmap.las.client.laswidget.ColumnEditorWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintDisplay;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintLabel;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintTextDisplay;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintWidgetGroup;
import gov.noaa.pmel.tmap.las.client.laswidget.CruiseIconWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.DateTimeWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.LASAnnotationsPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.laswidget.LinkButton;
import gov.noaa.pmel.tmap.las.client.laswidget.TextConstraintAnchor;
import gov.noaa.pmel.tmap.las.client.laswidget.UserListBox;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableConstraintAnchor;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableConstraintWidget;
import gov.noaa.pmel.tmap.las.client.serializable.AxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.ui.IESafeImage;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

/**
 * A property property viewer that keeps the initial constraints constant and
 * allows new constraints to be added.
 * 
 * @author rhs
 * 
 */
public class SimplePropPropViewer implements EntryPoint {
    
    ImageData scaledImage;

    private static String YSELECTOR = "y-variable";
    private static String XSELECTOR = "x-variable";
    private static String COLORSELECTOR = "color-selector";
    private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);
    NumberFormat dFormat = NumberFormat.getFormat("########.##");
    CruiseIconWidget cruiseIcons = new CruiseIconWidget();
    VerticalPanel fixedConstraintMainPanel = new VerticalPanel();
    HorizontalPanel fixedConstraintTitle = new HorizontalPanel();
    HTML dataSection;
    Map<String, VariableSerializable> xFilteredDatasetVariables = new HashMap<String, VariableSerializable>();
    Map<String, VariableSerializable> xAllDatasetVariables = new HashMap<String, VariableSerializable>();
    ToggleButton annotationsButton;
    LASAnnotationsPanel lasAnnotationsPanel = new LASAnnotationsPanel();
    HorizontalPanel buttonPanel = new HorizontalPanel();
    HorizontalPanel output = new HorizontalPanel();
    PopupPanel spin;
    HTML spinImage;
    HorizontalPanel coloredBy = new HorizontalPanel();
    ConstraintTextDisplay fixedConstraintPanel = new ConstraintTextDisplay();
    ScrollPanel fixedScroller = new ScrollPanel();
    Label selection = new Label("Current selection:");
    Label horizontalLabel = new Label("Horizontal: ");
    Label verticalLabel = new Label("Vertical: ");
    FlexTable controlPanel = new FlexTable();
    VerticalPanel topPanel = new VerticalPanel();
    FlexTable outputPanel = new FlexTable();
    private UserListBox xVariables = new UserListBox(XSELECTOR, true);
    private UserListBox yVariables = new UserListBox(YSELECTOR, true);
    private UserListBox colorVariables = new UserListBox(COLORSELECTOR, true);
    AlertButton update = new AlertButton("Update Plot", Constants.UPDATE_NEEDED);
    PushButton print = new PushButton("Print");
    CheckBox colorCheckBox = new CheckBox();
    Label warnText;
    PopupPanel warning = new PopupPanel(true);
    PushButton ok;
    PushButton cancel;
    LinkButton link = new LinkButton();
    PushButton table = new PushButton("Data Table");
    PushButton edit = new PushButton("Edit Flags");
    ColumnEditorWidget columnEditor;
    boolean youveBeenWarned = false;
    // The current intermediate file
    String netcdf = null;
    String dsid;
    String varid;
    String varTwoId;
    String varThreeId;
    String currentURL;
    String operationID;
    // Drawing start position
    int startx = -1;
    int starty = -1;
    int endx;
    int endy;
    boolean draw = false;
    IESafeImage plotImage;
    Context2d imageCanvasContext;
    Canvas imageCanvas;
    Context2d drawingCanvasContext;
    Canvas drawingCanvas;
    CssColor randomColor;
    // Have a cancel button ready to go for this panel.
    CancelButton cancelButton;
    final static String operationType = "v7";
    protected int x_image_size;
    protected int y_image_size;
    protected int x_plot_size;
    protected int y_plot_size;
    protected int x_offset_from_left;
    protected int y_offset_from_bottom;
    protected int x_offset_from_right;
    protected int y_offset_from_top;
    protected double x_axis_lower_left;
    protected double y_axis_lower_left;
    protected double x_axis_upper_right;
    protected double y_axis_upper_right;
    protected double world_startx;
    protected double world_starty;
    protected double world_endx;
    protected double world_endy;
    protected double x_per_pixel;
    protected double y_per_pixel;
    protected String time_min;
    protected String time_max;
    protected boolean hasData = false;
    protected String printURL;
    protected String xlo;
    protected String xhi;
    protected String ylo;
    protected String yhi;
    protected String tlo;
    protected String thi;
    protected String zlo;
    protected String zhi;
    protected String defaultx = null;
    protected String defaulty = null;
    protected String defaultcb = null;
    protected String time_origin;
    protected String calendar;
    protected String time_units;
    
    protected String allvariables;
    
    protected List<String> currentIconList = new ArrayList<String>();
    
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

    EventBus eventBus;

    // Some information to control the size of the image as the browser window
    // changes size.
    int topAndBottomPadding = 190;

    int pwidth;

    double image_h = 722.;
    double image_w = 838.;
    int image_w_min = 400;

    int fixedZoom;
    boolean autoZoom = true;;

    double globalMin = 99999999.;
    double globalMax = -99999999.;

    double imageScaleRatio = 1.0; // Keep track of the factor by which the image
    
    ConstraintWidgetGroup constraintWidgetGroup = new ConstraintWidgetGroup();
    
    String initialHistory;
    boolean hasInitialHistory = false;
    
    String dsg_id;
    DialogBox editDialog = new DialogBox(false);
    
    AbsolutePanel canvasDiv = new AbsolutePanel();
    
    boolean outx = false;
    boolean outy = false;

    // Take into account if the window is scrolled
    int yscroll;
    int xscroll;


    @Override
    public void onModuleLoad() {
        editDialog.setText("Flag Editor");
       

        colorVariables.setColorBy(true);
        // Turn it on by default...
        colorCheckBox.setValue(true);
        ClientFactory cf = GWT.create(ClientFactory.class);
        eventBus = cf.getEventBus();

        eventBus.addHandler(WidgetSelectionChangeEvent.TYPE, new WidgetSelectionChangeEvent.Handler() {
            @Override
            public void onAxisSelectionChange(WidgetSelectionChangeEvent event) {
                Object source = event.getSource();
                // The only such event from this source is the close event.
                if (source instanceof ColumnEditorWidget) {
                    editDialog.hide();
                    updatePlot(false, false);
                }
            }
        });
        
        eventBus.addHandler(gov.noaa.pmel.tmap.las.client.event.ResizeEvent.TYPE, new gov.noaa.pmel.tmap.las.client.event.ResizeEvent.Handler() {

            @Override
            public void onResize(gov.noaa.pmel.tmap.las.client.event.ResizeEvent event) {
                resize(Window.getClientWidth(), Window.getClientHeight());
            }
            
        });
       
        // Listen for StringValueChangeEvents from LASAnnotationsPanel(s)
        eventBus.addHandler(StringValueChangeEvent.TYPE, new StringValueChangeEvent.Handler() {
            @Override
            public void onValueChange(StringValueChangeEvent event) {
                Object source = event.getSource();
                if ((source != null) && (source instanceof LASAnnotationsPanel)) {
                    String open = event.getValue();
                    boolean isOpen = Boolean.parseBoolean(open);
                    if (!isOpen) {
                        LASAnnotationsPanel sourceLASAnnotationsPanel = (LASAnnotationsPanel) source;
                        if ((sourceLASAnnotationsPanel != null) && (sourceLASAnnotationsPanel.equals(lasAnnotationsPanel)))
                            synchAnnotationsMode();
                    }
                }
            }
        });

        
        int rndRedColor = 244;
        int rndGreenColor = 154;
        int rndBlueColor = 0;
        double rndAlpha = .45;
        randomColor = CssColor.make("rgba(" + rndRedColor + ", " + rndGreenColor + "," + rndBlueColor + ", " + rndAlpha + ")");
     
        cancelButton = new CancelButton("Correlation");
        eventBus.addHandler(CancelEvent.TYPE, cancelRequestHandler);
       
        String spinImageURL = URLUtil.getImageURL() + "/mozilla_blu.gif";

        annotationsButton = new ToggleButton("Annotations", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showAnnotations(annotationsButton.isDown());
            }

        });
        annotationsButton.addStyleDependentName("SMALLER");
        showAnnotations(true);
        annotationsButton.setTitle("Click to hide the annotations of the plot.");
        spinImage = new HTML("<img src=\"" + spinImageURL + "\" alt=\"Spinner\"/>");
        spin = new PopupPanel();
        spin.add(spinImage);
        update.addStyleDependentName("SMALLER");
        update.setWidth("80px");
        update.ensureDebugId("update");

        table.setTitle("See data for the current plot in a table.");
        table.addStyleDependentName("SMALLER");
        table.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                openTable();
            }
            
        });
        
        edit.setTitle("Edit Flags for the variable on the Y-axis.");
        edit.addStyleDependentName("SMALLER");
        edit.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                
                editFlags();
                
            }
            
        });
        
        // Never show the add button...
        xVariables.setMinItemsForAddButtonToBeVisible(10000);
        yVariables.setMinItemsForAddButtonToBeVisible(10000);
        colorVariables.setMinItemsForAddButtonToBeVisible(10000);

        print.addStyleDependentName("SMALLER");
        print.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                printerFriendly();
            }

        });
        print.setEnabled(false);
        buttonPanel.add(update);
        buttonPanel.add(print);
        buttonPanel.add(link);
        buttonPanel.add(table);
        String profile = DOM.getElementById("las-profile").getPropertyString("content");
        if ( profile.equals(Constants.PROFILE_SOCAT) ) {
            buttonPanel.add(edit);
        }
        dataSection = new HTML("<b>&nbsp;&nbsp;Data Selection: </b>");
        controlPanel.setWidget(0, 0, dataSection);
        controlPanel.getFlexCellFormatter().setColSpan(0, 0, 2);
        controlPanel.setWidget(1, 0, yVariables);
        controlPanel.getFlexCellFormatter().setColSpan(1, 0, 2);
        HTML asA = new HTML(" a");
        controlPanel.setWidget(2, 0, new HTML("&nbsp;&nbsp;&nbsp;as a function of "));
        controlPanel.getFlexCellFormatter().addStyleName(2, 0, "menulabel");
        controlPanel.setWidget(2, 1, xVariables);

        colorCheckBox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                if (colorCheckBox.getValue()) {
                    colorVariables.setEnabled(true);
                } else {
                    colorVariables.setEnabled(false);
                }
                setVariables();
            }

        });
        
        coloredBy.add(new HTML("&nbsp;&nbsp;&nbsp;Colored by "));
        coloredBy.add(colorCheckBox);
        
        controlPanel.setWidget(3, 0, coloredBy);
        controlPanel.getFlexCellFormatter().addStyleName(3, 0, "menulabel");
        controlPanel.setWidget(3, 1, colorVariables);
        topPanel.add(controlPanel);
       
        update.addClickHandler(updateClick);
        String catid = Util.getParameterString("catid");
        String xml = Util.getParameterString("xml");
        if (xml != null && !xml.equals("")) {
            xml = Util.decode(xml);
            lasRequest = new LASRequest(xml);
            dsid = lasRequest.getDataset(0);
            if ( dsid.contains("'") ) dsid = dsid.substring(dsid.indexOf("\'")+1, dsid.lastIndexOf("\'"));
            varid = lasRequest.getVariable(0);
            if ( varid.contains("'") ) varid = varid.substring(varid.indexOf("\'")+1, varid.lastIndexOf("\'"));
            varTwoId = lasRequest.getVariable(1);
            if ( varTwoId != null && varTwoId.contains("'") ) varTwoId = varTwoId.substring(varTwoId.indexOf("\'")+1, varTwoId.lastIndexOf("\'"));
            varThreeId = lasRequest.getVariable(2);
            if ( varThreeId != null && varThreeId.contains("'") ) varThreeId = varThreeId.substring(varThreeId.indexOf("\'")+1, varThreeId.lastIndexOf("\'"));
            xlo = lasRequest.getRangeLo("x", 0);
            xhi = lasRequest.getRangeHi("x", 0);
            ylo = lasRequest.getRangeLo("y", 0);
            yhi = lasRequest.getRangeHi("y", 0);
            tlo = lasRequest.getRangeLo("t", 0);
            thi = lasRequest.getRangeHi("t", 0);

            // No need to wait, these can be set now...
            if (xlo != null && xhi != null && ylo != null && yhi != null) {
                
                setFixedXY(xlo, xhi, ylo, yhi);
                
                
            }
            setFixedT(tlo, thi);
            Util.getRPCService().getConfig(null, catid, dsid, varid, datasetCallback);
        } else {
            Window.alert("This app must be launched from the main interface.");
        }
        initialHistory = getAnchor();
        if ( initialHistory != null && !initialHistory.equals("") ) {
            hasInitialHistory = true;
            initialHistory = Util.decode(initialHistory);
        } else {
            hasInitialHistory = false;
            initialHistory = "";
        }
        eventBus.addHandler(VariableSelectionChangeEvent.TYPE, new VariableSelectionChangeEvent.Handler() {

            @Override
            public void onVariableChange(VariableSelectionChangeEvent event) {
                Object source = event.getSource();
                if (source instanceof UserListBox) {
                    UserListBox u = (UserListBox) source;
                    String id = u.getName();
                    if (id != null && (id.equals(YSELECTOR) || id.equals(XSELECTOR) || id.equals(COLORSELECTOR)) ) {
                        setVariables();    
                    }
                }
            }
        });
        eventBus.addHandler(VariableConstraintEvent.TYPE, new VariableConstraintEvent.Handler() {

            @Override
            public void onChange(VariableConstraintEvent event) {
                String variable = event.getVariable();
                String op1 = event.getOp1();
                String op2 = event.getOp2();
                String lhs = event.getLhs();
                String rhs = event.getRhs();
                String varid = event.getVarid();
                String dsid = event.getDsid();
                boolean apply = event.isApply();
                VariableConstraintAnchor anchor1 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, varid, variable, lhs, variable, lhs, op1);
                VariableConstraintAnchor anchor2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, varid, variable, rhs, variable, rhs, op2);
                VariableConstraintAnchor a = (VariableConstraintAnchor) constraintWidgetGroup.findMatchingAnchor(anchor1);
                VariableConstraintAnchor b = (VariableConstraintAnchor) constraintWidgetGroup.findMatchingAnchor(anchor2);
                ConstraintDisplay afixed = (ConstraintDisplay) fixedConstraintPanel.findMatchingAnchor(anchor1);
                ConstraintDisplay bfixed = (ConstraintDisplay) fixedConstraintPanel.findMatchingAnchor(anchor2);
                if ( afixed != null && !anchor1.getValue().equals("") && !a.getValue().equals("")) {
                    double v1 = Double.valueOf(anchor1.getValue());
                    double a1 = Double.valueOf(afixed.getValue());
                    if ( v1 < a1 ) {
                        a.setValue(afixed.getValue());
                        constraintWidgetGroup.setLhs(afixed.getValue());
                    }
                }
                if ( bfixed != null && !anchor2.getValue().equals("") && !b.getValue().equals("") ) {
                    double v2 = Double.valueOf(anchor2.getValue());
                    double b2 = Double.valueOf(bfixed.getValue());
                    if ( v2 > b2 ) {
                        b.setValue(bfixed.getValue());
                        constraintWidgetGroup.setRhs(bfixed.getValue());
                    }
                }

            }
        });
        outputPanel.setWidget(0, 0, lasAnnotationsPanel);
        output.add(annotationsButton);
        output.add(outputPanel);
        fixedConstraintTitle.add(new HTML("<b>Fixed Constraints from the main interface:</b>&nbsp;"));
        fixedConstraintMainPanel.add(fixedConstraintTitle);
        // TODO style and label
        fixedConstraintPanel.setSize(Constants.CONTROLS_WIDTH-25+"px", "125px");
        fixedScroller.addStyleName(Constants.ALLBORDER);
        fixedScroller.add(fixedConstraintPanel);
        fixedScroller.setSize(Constants.CONTROLS_WIDTH-10+"px", "100px");
        fixedConstraintMainPanel.add(fixedScroller);
        RootPanel.get("icons").add(cruiseIcons);
        RootPanel.get("button_panel").add(buttonPanel);
        RootPanel.get("data_selection").add(topPanel);
        RootPanel.get("data_constraints").add(constraintWidgetGroup);
        RootPanel.get("correlation").add(output);
        RootPanel.get("space_time_constraints").add(fixedConstraintMainPanel);
        lasAnnotationsPanel.setPopupLeft(outputPanel.getAbsoluteLeft());
        lasAnnotationsPanel.setPopupTop(outputPanel.getAbsoluteTop());

        lasAnnotationsPanel.setError("Click \"Update plot\" to refresh the plot.&nbsp;");
        ok = new PushButton("Ok");
        ok.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                warning.hide();
                youveBeenWarned = true;
            }

        });
        ok.setWidth("80px");
        cancel = new PushButton("Cancel");
        cancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                popHistory(undoState.toString());
                warning.hide();
            }

        });
        cancel.setWidth("80px");
        warnText = new Label("This operation will require new data to be extracted from the database.");
        Label go = new Label("  It may take a while.  Do you want to continue?");
        FlexTable layout = new FlexTable();
        layout.getFlexCellFormatter().setColSpan(0, 0, 2);
        layout.getFlexCellFormatter().setColSpan(1, 0, 2);
        layout.setWidget(0, 0, warnText);
        layout.setWidget(1, 0, go);
        layout.setWidget(2, 0, ok);
        layout.setWidget(2, 1, cancel);
        warning.add(layout);
        warning.setPopupPosition(update.getAbsoluteLeft(), update.getAbsoluteTop());
        History.addValueChangeHandler(historyHandler);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                resize(event.getWidth(), event.getHeight());
            }
        });
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            public void onWindowClosing(Window.ClosingEvent closingEvent) {
                if ( columnEditor != null && columnEditor.isDirty() ) {
                    closingEvent.setMessage("If you close or refresh the page your current flags will be lost.");
                }
            }
        });
    }

   

   

  





    /**
     * The LASAnnotationsPanel closed, so make sure the
     * {@link annotationsButton} down state is set appropriately.
     */
    protected void synchAnnotationsMode() {
        showAnnotations(lasAnnotationsPanel.isVisible());
    }
    
    ClickHandler updateClick = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            updatePlot(true, true);
        }

    };

    private void printerFriendly() {
        StringBuilder urlfrag = new StringBuilder(URLUtil.getBaseURL() + "getAnnotations.do?template=image_w_annotations.vm&");
        urlfrag.append(printURL);
        Window.open(urlfrag.toString(), "_blank", Constants.WINDOW_FEATURES);
    }

    private void updatePlot(boolean addHistory, boolean cache) {
        // TODO Before submitting...

        
        setConstraints();
        
        //TODO this should wait until the result comes back and is good and should be an event, right?
        update.removeStyleDependentName("APPLY-NEEDED");

        lasAnnotationsPanel.setError("Fetching plot...");

        spin.setPopupPosition(outputPanel.getAbsoluteLeft(), outputPanel.getAbsoluteTop());
        spin.show();

        makeRequest(true);
       
        imageCanvas = Canvas.createIfSupported();
        drawingCanvas = Canvas.createIfSupported();

        
        
        if (imageCanvas != null) {

            canvasDiv.add(imageCanvas, 0, 0);
            canvasDiv.add(drawingCanvas, 0, 0);
            outputPanel.setWidget(1, 0, canvasDiv);
            imageCanvasContext = imageCanvas.getContext2d();
            drawingCanvasContext = drawingCanvas.getContext2d();
            
        } else {
            outputPanel.setWidget(1, 0 , new HTML(""));
            Window.alert("You are accessing this site with an older, no longer supported browser. "
                    + "Some or all features of this site will not work correctly using your browser. " + "Recommended browsers include these or higher versions of these: "
                    + "IE 9.0   FF 17.0    Chorme 23.0    Safari 5.1");
        }

        String url = Util.getProductServer() + "?xml=" + URL.encode(lasRequest.toString());

        currentURL = url;
        
        if ( cache ) {
            lasRequest.setProperty("product_server", "use_cache", "true");
        } else {
            lasRequest.setProperty("product_server", "use_cache", "false");
        }

        if (addHistory) {
            String x = lasRequest.toString();
            pushHistory(x);
        }

        RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, url);
        try {
            sendRequest.sendRequest(null, lasRequestCallback);
        } catch (RequestException e) {
            HTML error = new HTML(e.toString());
            outputPanel.setWidget(2, 0, error);
        }

    }
    private void openTable() {
        makeRequest(false);
        LASRequest tableRequest = new LASRequest(lasRequest.toString());
        String dsid = tableRequest.getDataset(0);
        String v0 = tableRequest.getVariable(0);
        String v1 = tableRequest.getVariable(1);
        String v2 = null;
        if ( colorCheckBox.getValue() ) {
            v2 = colorVariables.getUserObject(colorVariables.getSelectedIndex()).getID();
        }
        tableRequest.removeVariables();
        tableRequest.addVariable(dsid, v0, 0);
        tableRequest.addVariable(dsid, v1, 0);
        
        // Set the data property...
        if (v0 != null) {
            tableRequest.setProperty("data_0", "url", netcdf);
        }
        if (v1 != null) {
            tableRequest.setProperty("data_1", "url", netcdf);
        }
        if (v2 != null && !v2.equals(dsg_id) && !v2.equals("longitude") && !v2.equals("latitude") && !v2.equals("longitude") ) {
            tableRequest.addVariable(dsid, v2, 0);
            tableRequest.setProperty("data_2", "url", netcdf);
            tableRequest.setProperty("data", "count", "3");
        } else {
            tableRequest.setProperty("data", "count", "2");
        }
        
        tableRequest.setProperty("ferret", "data_format", "csv");
        String url = Util.getProductServer() + "?xml=" + URL.encode(tableRequest.toString());
        Window.open(url, "_blank", Constants.WINDOW_FEATURES);
    }
    private void editFlags() {
        makeRequest(false);
        LASRequest tableRequest = new LASRequest(lasRequest.toString());
        tableRequest.setOperation("EditColumn_setup", "V7");
        String dsid = tableRequest.getDataset(0);
        String v0 = tableRequest.getVariable(1);
        // This is the variable ID.

        // Get the names...
        VariableSerializable var0 = xAllDatasetVariables.get(v0);

        // Just hard-code the two current WOCE flags for SOCAT data
        String woce_water = "WOCE_CO2_water";
        String v1 = woce_water+"-"+dsid;
        String woce_atm = "WOCE_CO2_atm";
        String v2 = woce_atm + "-" + dsid;

        tableRequest.removeVariables();
        tableRequest.addVariable(dsid, v0, 0);
        tableRequest.addVariable(dsid, v1, 0);
        tableRequest.addVariable(dsid, v2, 0);
        columnEditor = new ColumnEditorWidget(dsid, tableRequest.toString(), var0.getShortname(), Arrays.asList(woce_water,woce_atm));
        
        editDialog.setWidget(columnEditor);
        editDialog.show();


        //        String url = Util.getProductServer() + "?xml=" + URL.encode(tableRequest.toString());
        //        Window.open(url, "_blank", Constants.WINDOW_FEATURES);
    }
    
    private void makeRequest(boolean plot) {
        boolean contained = true;

        String zlo = null;
        String zhi = null;
        String vix = xVariables.getUserObject(xVariables.getSelectedIndex()).getID();
        GridSerializable grid = xVariables.getUserObject(xVariables.getSelectedIndex()).getGrid();
        if (grid.hasT()) {

            if (tlo != null && thi != null) {
                lasRequest.setRange("t", tlo, thi, 0);
            }
            
        }
        if (xlo != null && xhi != null) {
            lasRequest.setRange("x", xlo, xhi, 0);
        } else if (xhi != null) {
            xlo = xhi;
            lasRequest.setRange("x", xhi, xhi, 0);
        }

        if (ylo != null && yhi != null) {
            lasRequest.setRange("y", ylo, yhi, 0);
        } else if (yhi != null) {
            ylo = yhi;
            lasRequest.setRange("y", yhi, yhi, 0);
        }

        // Check if the current selection bounding box is contained in by
        // original bounding box.

        
        if (grid.hasZ()) {

            if (zlo != null && zhi != null) {
                lasRequest.setRange("z", zlo, zhi, 0);
            } else if (zhi != null) {
                lasRequest.setRange("z", zhi, zhi, 0);
            }
        }

        lasRequest.setProperty("product_server", "ui_timeout", "20");
        if ( plot ) {
            lasRequest.setProperty("las", "output_type", "xml");
        } else {
            lasRequest.removeProperty("las", "output_type");
        }
        String grid_type = xVariables.getUserObject(0).getAttributes().get("grid_type");
        if (netcdf != null && contained && xVariables.getUserObject(0).isDiscrete() ) {
            if ( plot ) {
                operationID = "Trajectory_correlation_plot"; // No data base access;
            } else {
                operationID = "Trajectory_correlation_table";
            }
            // plot only from
            // existing
            // netCDF file.
            String v0 = lasRequest.getVariable(0);
            String v1 = lasRequest.getVariable(1);
            String v2 = lasRequest.getVariable(2);
            if (v0 != null) {
                lasRequest.setProperty("data_0", "url", netcdf);
            }
            if (v1 != null) {
                lasRequest.setProperty("data_1", "url", netcdf);
            }
            if (v2 != null && plot) {
                lasRequest.setProperty("data_2", "url", netcdf);
                lasRequest.setProperty("data", "count", "3");
            } else {
                lasRequest.setProperty("data", "count", "2");
            }
            lasRequest.removeProperty("ferret", "cruise_list");
            if (!cruiseIcons.getIDs().equals("") ) {
                currentIconList = cruiseIcons.getCheckedIconList();
                lasRequest.setProperty("ferret", "cruise_list", cruiseIcons.getIDs());
            } else {
                currentIconList = new ArrayList<String>();
            }
        } else if ((!contained || netcdf == null) && ( xVariables.getUserObject(0).isDiscrete() )) {
            // This should only occur when the app loads for the first time...
            if ( plot ) {
                operationID = "Trajectory_correlation_extract_and_plot";
            } else {

                // This should never happen, but if it does.
                Window.alert("Cannot make table.");
            }
            // We need to make the initial netCDF file with *all* the variables in each row.

            // Grab the existing variable to be used as y
            String vy = varid;
            if ( defaulty != null ) vy = defaulty;
            String vds = dsid;
            // Because there is no netCDF we know this is the first request.
            if ( varid.equals(dsg_id) ) {
                if ( defaulty != null ) {
                    vy = defaulty;
                } else {
                    // Find one that's not the trajectory_id
                    for (int yin = 0; yin < yVariables.getItemCount(); yin++) {
                        VariableSerializable v = (VariableSerializable) yVariables.getUserObject(yin);
                        if ( !v.getID().equals(vy) && v.getAttributes().get("subset_variable") == null && !v.getName().contains("WOCE") ) {
                            vy = v.getID();
                            vds = v.getDSID();
                        }
                    }
                }
            }

            // If varTwoId
            String vcb = null;
            if ( varTwoId == null || (varTwoId != null && varTwoId.equals("") ) ) {
                lasRequest.removeVariables();
                // Find the first non-sub-set variable that is not already included

                String vx = xVariables.getUserObject(xVariables.getSelectedIndex()).getID();
                


                if ( vx == null || (vx != null && vx.equals(vy))) {
                    // It might be the same as y so set it to null so it finds the first variable that does not match and is not traj id.
                    vx = null;
                    for (int yi = 0; yi < xVariables.getItemCount(); yi++) {
                        VariableSerializable v = (VariableSerializable) xVariables.getUserObject(yi);               
                        // DEBUG don't use WOCE flag as second variable
                        if ( vx == null && !v.getID().equals(vy) && v.getAttributes().get("subset_variable") == null && !v.getName().contains("WOCE") && !v.getID().equals(dsg_id) ) {
                            vx = v.getID();
                            vds = v.getDSID();
                        }
                    }
                } else {
                    vds = xVariables.getUserObject(yVariables.getSelectedIndex()).getDSID();
                }

                if ( vds != null ) {
                    lasRequest.addVariable(vds, vx, 0);
                    if ( vx != null ) {
                        lasRequest.addVariable(vds, vy, 0);
                    }


                    xVariables.setVariable(xAllDatasetVariables.get(vx));
                    yVariables.setVariable(xAllDatasetVariables.get(vy));

                }
            }
            if ( colorCheckBox.getValue() ) {
                vcb = colorVariables.getUserObject(colorVariables.getSelectedIndex()).getID();
                lasRequest.setProperty("data", "count", "3");
                if ( vcb != null && plot ) { 
                    lasRequest.addVariable(vds, vcb, 0);
                }
            } else {
                lasRequest.setProperty("data", "count", "2");
            }

        } else {
            operationID = "prop_prop_plot";
        }
        lasRequest.setOperation(operationID, operationType);
        lasRequest.setProperty("ferret", "annotations", "file");

    }

    RequestCallback lasRequestCallback = new RequestCallback() {

        @Override
        public void onError(Request request, Throwable exception) {

            spin.hide();                    
            Window.alert("Product request failed.");

        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            // If the submitted operation used the compound operation to
            // pull a new data set, we need a new initial state
            String plot_id = lasRequest.getOperation();
            if (plot_id.equals("Trajectory_correlation_plot")) {
                initialState = new LASRequest(lasRequest.toString());
            }
            
            
            
            // Need to warn again...
            youveBeenWarned = false;
            
            print.setEnabled(true);
            String doc = response.getText();
            String imageurl = "";
            String annourl = "";
            // Look at the doc. If it's not obviously XML, treat it as HTML.
            if (doc.length() <= 1 || !doc.substring(0, doc.length()/2).contains("<?xml")) {
                if ( initialHistory == null || initialHistory.equals("") ) { 
                    spin.hide();
                }
                VerticalPanel p = new VerticalPanel();
                ScrollPanel sp = new ScrollPanel();
                // Make the native java script in the HTML error page active.
                HTML result = new HTML(HtmlSanitizerUtil.sanitizeHtml(doc));
                p.add(result);
                PushButton again = new PushButton("Try Again");
                again.setWidth("75px");
                again.addStyleDependentName("SMALLER");
                again.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent click) {
                        updatePlot(false, true);
                    }
                });
                p.add(again);
                sp.add(p);
                sp.setSize((int) image_w + "px", (int) image_h + "px");

                outputPanel.setWidget(2, 0, sp);
            } else {
                doc = doc.replaceAll("\n", "").trim();
                Document responseXML = XMLParser.parse(doc);
                NodeList results = responseXML.getElementsByTagName("result");

                for (int n = 0; n < results.getLength(); n++) {
                    if (results.item(n) instanceof Element) {
                        Element result = (Element) results.item(n);
                        if (result.getAttribute("type").equals("image")) {
                            imageurl = result.getAttribute("url");
                        } else if (result.getAttribute("type").equals("batch")) {
                            String elapsed_time = result.getAttribute("elapsed_time");
                            cancelButton.setTime(Integer.valueOf(elapsed_time));
                            cancelButton.setSize(image_w * imageScaleRatio + "px", image_h * imageScaleRatio + "px");
                            outputPanel.setWidget(2, 0, cancelButton);
                            lasRequest.setProperty("product_server", "ui_timeout", "3");
                            String url = Util.getProductServer() + "?xml=" + URL.encode(lasRequest.toString());
                            if (currentURL.contains("cancel"))
                                url = url + "&cancel=true";
                            RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, url);
                            try {
                                sendRequest.setHeader("Pragma", "no-cache");
                                sendRequest.setHeader("cache-directive", "no-cache");
                                // These are needed or IE will cache and make
                                // infinite requests that always return 304
                                sendRequest.setHeader("If-Modified-Since", new Date().toString());
                                sendRequest.setHeader("max-age", "0");
                                sendRequest.sendRequest(null, lasRequestCallback);
                            } catch (RequestException e) {
                                HTML error = new HTML(e.toString());
                                error.setSize(image_w * imageScaleRatio + "px", image_h * imageScaleRatio + "px");
                                outputPanel.setWidget(2, 0, error);
                            }
                        } else if (result.getAttribute("type").equals("error")) {
                            if (result.getAttribute("ID").equals("las_message")) {
                                Node text = result.getFirstChild();
                                if (text instanceof Text) {
                                    Text t = (Text) text;
                                    HTML error = new HTML(HtmlSanitizerUtil.sanitizeHtml(t.getData().toString().trim()));
                                    outputPanel.setWidget(2, 0, error);
                                }
                            }
                        } else if (result.getAttribute("type").equals("annotations")) {
                            annourl = result.getAttribute("url");
                            lasAnnotationsPanel.setAnnotationsHTMLURL(Util.getAnnotationService(annourl));
                        } else if (result.getAttribute("type").equals("icon_webrowset")) {
                            String iconurl = result.getAttribute("url");
                            cruiseIcons.setCheckedIconList(currentIconList);
                            cruiseIcons.init(iconurl);
                        } else if (result.getAttribute("type").equals("netCDF")) {
                            netcdf = result.getAttribute("file");

                        } else if (result.getAttribute("type").equals("map_scale")) {
                            NodeList map_scale = result.getElementsByTagName("map_scale");
                            for (int m = 0; m < map_scale.getLength(); m++) {
                                if (map_scale.item(m) instanceof Element) {
                                    Element map = (Element) map_scale.item(m);
                                    NodeList children = map.getChildNodes();
                                    for (int l = 0; l < children.getLength(); l++) {
                                        if (children.item(l) instanceof Element) {
                                            Element child = (Element) children.item(l);
                                            if (child.getNodeName().equals("x_image_size")) {
                                                x_image_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_image_size")) {
                                                y_image_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_plot_size")) {
                                                x_plot_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_plot_size")) {
                                                y_plot_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_offset_from_left")) {
                                                x_offset_from_left = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_offset_from_bottom")) {
                                                y_offset_from_bottom = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_offset_from_right")) {
                                                x_offset_from_right = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_offset_from_top")) {
                                                y_offset_from_top = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_axis_lower_left")) {
                                                x_axis_lower_left = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_axis_lower_left")) {
                                                y_axis_lower_left = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_axis_upper_right")) {
                                                x_axis_upper_right = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_axis_upper_right")) {
                                                y_axis_upper_right = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("time_min")) {
                                                time_min = getString(child.getFirstChild());
                                            } else if (child.getNodeName().equals("time_max")) {
                                                time_max = getString(child.getFirstChild());
                                            } else if (child.getNodeName().equals("data_exists")) {
                                                int ex = getNumber(child.getFirstChild());
                                                if ( ex == 1 ) {
                                                    hasData = true;
                                                } else {
                                                    hasData = false;
                                                }
                                            } else if ( child.getNodeName().equals("time_origin") ) {
                                                // This is the base date from the units string.
                                                time_origin = getString(child.getFirstChild());
                                            } else if ( child.getNodeName().equals("calendar") ) {
                                                // This is one of: GREGORIAN, NOLEAP, JULIAN, 360_DAY, ALL_LEAP
                                                calendar = getString(child.getFirstChild());
                                            } else if ( child.getNodeName().equals("time_step_units") ) {
                                                // This is one of: years, months, days, hours, minutes, seconds
                                                time_units = getString(child.getFirstChild());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!imageurl.equals("")) {
                    plotImage = new IESafeImage(HtmlSanitizerUtil.sanitizeHtml(imageurl).asString());
                    x_per_pixel = (x_axis_upper_right - x_axis_lower_left) / Double.valueOf(x_plot_size);
                    y_per_pixel = (y_axis_upper_right - y_axis_lower_left) / Double.valueOf(y_plot_size);
                    // If you are not going to pop the history, hide the spinner.
                    if ( initialHistory == null || initialHistory.equals("") ) { 
                      spin.hide();
                    }
                    if (imageCanvas != null) {
                        outputPanel.setWidget(2, 0, plotImage);
                        plotImage.setVisible(false);
                        
                        plotImage.addLoadHandler(new LoadHandler() {

                            @Override
                            public void onLoad(LoadEvent event) {
                                int width = plotImage.getWidth();
                                int height = plotImage.getHeight();
                                // Set global maximums
                                image_w = width;
                                image_h = height;
                                String w = width - 18 + "px";
                                lasAnnotationsPanel.setPopupWidth(w);
                                lasAnnotationsPanel.setPopupLeft(outputPanel.getAbsoluteLeft());
                                lasAnnotationsPanel.setPopupTop(outputPanel.getAbsoluteTop());
                                imageCanvas.setCoordinateSpaceHeight(height);
                                imageCanvas.setCoordinateSpaceWidth(width);
                                drawToScreenScaled(imageScaleRatio); //frontCanvasContext.drawImage(ImageElement.as(plotImage.getElement()), 0, 0);
                                drawingCanvas.addMouseDownHandler(new MouseDownHandler() {

                                    @Override
                                    public void onMouseDown(MouseDownEvent event) {
                                        outx = false;
                                        outy = false;
                                        
                                        startx = event.getX();
                                        starty = event.getY();

                                        yscroll = Window.getScrollTop();
                                        xscroll = Window.getScrollLeft();
                                        starty = starty - yscroll;
                                        startx = startx - xscroll;

                                        if (startx > x_offset_from_left && starty > y_offset_from_top && startx < x_offset_from_left + x_plot_size && starty < y_offset_from_top + y_plot_size) {

                                            draw = true;
                                            // frontCanvasContext.drawImage(ImageElement.as(image.getElement()),
                                            // 0, 0);
                                            drawingCanvasContext.clearRect(0, 0, drawingCanvas.getCoordinateSpaceWidth(), drawingCanvas.getCoordinateSpaceWidth());
                                            double scaled_x_per_pixel = x_per_pixel / imageScaleRatio;
                                            double scaled_y_per_pixel = y_per_pixel / imageScaleRatio;
                                            world_startx = x_axis_lower_left + (startx - x_offset_from_left * imageScaleRatio) * scaled_x_per_pixel;
                                            world_starty = y_axis_lower_left + ((y_image_size * imageScaleRatio - starty) - y_offset_from_bottom * imageScaleRatio)
                                                    * scaled_y_per_pixel;

                                            world_endx = world_startx;
                                            world_endy = world_starty;

                                            setTextValues();
                                       

                                        }
                                    }
                                });
                                drawingCanvas.addMouseMoveHandler(new MouseMoveHandler() {

                                    @Override
                                    public void onMouseMove(MouseMoveEvent event) {
                                        int currentx = event.getX();
                                        int currenty = event.getY();

                                        yscroll = Window.getScrollTop();
                                        xscroll = Window.getScrollLeft();
                                        currenty = currenty - yscroll;
                                        currentx = currentx - xscroll;

                                        // If you drag it out, we'll stop drawing.

                                        // To text everything has to be multiplied by the scale factor. Depend on Java to make the type promotions work right.
                                        if (currentx < x_offset_from_left*imageScaleRatio || currenty < y_offset_from_top*imageScaleRatio || currentx > x_offset_from_left*imageScaleRatio + x_plot_size*imageScaleRatio || currenty > y_offset_from_top*imageScaleRatio + y_plot_size*imageScaleRatio) {


                                            endx = currentx;
                                            endy = currenty;

                                            // Set the limits for one last drawing of the selection
                                            // rectangle.
                                            if (currentx < x_offset_from_left*imageScaleRatio) {
                                                endx =  (int) (x_offset_from_left*imageScaleRatio);
                                                currentx = (int) (x_offset_from_left*imageScaleRatio);
                                                outx = true;
                                            }
                                            if (currenty < y_offset_from_top*imageScaleRatio) {
                                                endy = (int) (y_offset_from_top*imageScaleRatio);
                                                currenty = (int) (y_offset_from_top*imageScaleRatio);
                                                outy = true;
                                            }
                                            if (currentx > x_offset_from_left*imageScaleRatio + x_plot_size*imageScaleRatio) {
                                                endx = (int) (x_offset_from_left*imageScaleRatio + x_plot_size*imageScaleRatio);
                                                currentx = (int) (x_offset_from_left*imageScaleRatio + x_plot_size*imageScaleRatio);
                                                outx = true;
                                            }
                                            if (currenty > y_offset_from_top*imageScaleRatio + y_plot_size*imageScaleRatio) {
                                                endy = (int) (y_offset_from_top*imageScaleRatio + y_plot_size*imageScaleRatio);
                                                currenty = (int) (y_offset_from_top*imageScaleRatio + y_plot_size*imageScaleRatio);
                                                outy = true;
                                            }
                                            
                                        }
                                        if (draw) {
                                            double scaled_x_per_pixel = x_per_pixel / imageScaleRatio;
                                            double scaled_y_per_pixel = y_per_pixel / imageScaleRatio;
                                            world_endx = x_axis_lower_left + (currentx - x_offset_from_left * imageScaleRatio) * scaled_x_per_pixel;
                                            world_endy = y_axis_lower_left + ((y_image_size * imageScaleRatio - currenty) - y_offset_from_bottom * imageScaleRatio)
                                                    * scaled_y_per_pixel;
                                            setTextValues();
                                            drawingCanvasContext.clearRect(0, 0, drawingCanvas.getCoordinateSpaceWidth(), drawingCanvas.getCoordinateSpaceWidth());
                                            drawingCanvasContext.setFillStyle(randomColor); 
                                            // Can't get this to work for some reason. The rectangle is black.
                                            drawingCanvasContext.fillRect(startx, starty, currentx - startx, currenty - starty);
                                            
                                            // This used to draw the image then the rectangle over and over again.
                                            // drawToScreenScaled(imageScaleRatio);
                                            // frontCanvasContext.strokeRect(startx, starty, currentx - startx, currenty - starty);
                                            
                                            drawingCanvasContext.strokeRect(startx, starty, currentx - startx, currenty - starty);
                                        }
                                        if (outx && outy) {
                                            draw = false;
                                        }
                                    }
                                    
                                });
                                resize(Window.getClientWidth(), Window.getClientHeight());
                            }

                        });
                        drawingCanvas.addMouseUpHandler(new MouseUpHandler() {

                            @Override
                            public void onMouseUp(MouseUpEvent event) {
                                // If we're still drawing when the mouse goes
                                // up, record the position.
                                if (draw) {
                                    int currentx = event.getX();
                                    int currenty = event.getY();
                                    double scaled_x_per_pixel = x_per_pixel / imageScaleRatio;
                                    double scaled_y_per_pixel = y_per_pixel / imageScaleRatio;
                                    world_endx = x_axis_lower_left + (currentx - x_offset_from_left * imageScaleRatio) * scaled_x_per_pixel;
                                    world_endy = y_axis_lower_left + ((y_image_size * imageScaleRatio - currenty) - y_offset_from_bottom * imageScaleRatio)*scaled_y_per_pixel;                                 
                                }
                                draw = false;
                                outx = false;
                                outy = false;
                                setTextValues();
                                setConstraints();
                            }
                        });
                    } else {
                        // Browser cannot handle a canvas tag, so just put up
                        // the image.
                        outputPanel.setWidget(2, 0, plotImage);
                        plotImage.addLoadHandler(new LoadHandler() {

                            @Override
                            public void onLoad(LoadEvent event) {
                                resize(Window.getClientWidth(), Window.getClientHeight());
                                String w = plotImage.getWidth() - 18 + "px";
                                lasAnnotationsPanel.setPopupLeft(outputPanel.getAbsoluteLeft());
                                lasAnnotationsPanel.setPopupTop(outputPanel.getAbsoluteTop());
                                lasAnnotationsPanel.setPopupWidth(w);
                            }
                        });
                        resize(Window.getClientWidth(), Window.getClientHeight());

                    }
                    
                    // set the constraint text values and the world coordinates if we got an image back... 

                    world_startx = x_axis_lower_left;
                    world_endx = x_axis_upper_right;
                    world_starty = y_axis_lower_left;
                    world_endy = y_axis_upper_right;
                   
                    printURL = Util.getAnnotationsFrag(annourl, imageurl);
                  
                }

            }
            
            if ( initialHistory != null && !initialHistory.equals("") ) {                      
                popHistory(initialHistory);
            }
        }
    };

    private void setTextValues() {
        // The should get reset to the proper values by the events below...


        // TODO treat an x or y axis with time differently.  First problem, how do you know it's time.  Check the id against the time id tabledap property?

        constraintWidgetGroup.clearTextFields();
        String yid = yVariables.getUserObject(yVariables.getSelectedIndex()).getID();
        String yname = yVariables.getUserObject(yVariables.getSelectedIndex()).getName();
        String xid = xVariables.getUserObject(xVariables.getSelectedIndex()).getID();
        String xname = xVariables.getUserObject(xVariables.getSelectedIndex()).getName();
        String dsid = xVariables.getUserObject(xVariables.getSelectedIndex()).getDSID();

        VariableConstraintAnchor ctax1;  
        VariableConstraintAnchor ctax2;
        VariableSerializable v = constraintWidgetGroup.getVariable();

        double minx;
        double maxx;
        if ( world_endx > world_startx ) {
            minx = world_startx;
            maxx = world_endx;
        } else {
            minx = world_endx;
            maxx = world_startx;
        }


        if ( xname.toLowerCase(Locale.ENGLISH).contains("time") ) {
            // Calculate time from the map scale...
            String tmin;
            String tmax;
            if ( !draw ) {
                // Only do time calculation if you're not drawing. Too intense for mouse dragging.
                
                if (time_origin != null && time_units != null && calendar != null ) {
                    tmin = DateTimeWidget.formatDate(minx, time_origin, time_units, calendar);
                    tmax = DateTimeWidget.formatDate(maxx, time_origin, time_units, calendar);
                } else {
                    tmin = time_min;
                    tmax = time_max;
                }
            } else {
                tmin = time_min;
                tmax = time_max;
            }
                ctax1 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, xid, xname, tmin, xname, dFormat.format(minx), "gt");      
                ctax2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, xid, xname, tmax, xname, dFormat.format(maxx), "le");
            
        } else {
            ctax1 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, xid, xname, dFormat.format(minx), xname, dFormat.format(minx), "gt");      
            ctax2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, xid, xname, dFormat.format(maxx), xname, dFormat.format(maxx), "le");
        }
        if ( ctax1 != null ) {
            boundByFixed(ctax1);
            if ( v.getName().equals(xname) ) {
                constraintWidgetGroup.setLhs(ctax1.getKeyValue());
            }
            if ( constraintWidgetGroup.contains(ctax1)) {
                constraintWidgetGroup.remove(ctax1);
            }
            constraintWidgetGroup.addConstraint(ctax1);
        }
        if ( ctax2 != null ) {
            boundByFixed(ctax2);
            if ( v.getName().equals(xname) ) {
                constraintWidgetGroup.setRhs(ctax2.getKeyValue());
            }
            if ( constraintWidgetGroup.contains(ctax2) ) {
                constraintWidgetGroup.remove(ctax2);
            }
            constraintWidgetGroup.addConstraint(ctax2);
        }
        double miny;
        double maxy;
        VariableConstraintAnchor ctay1;
        VariableConstraintAnchor ctay2;


        if ( world_endy > world_starty ) {
            miny = world_starty;
            maxy = world_endy;
        } else {
            miny = world_endy;
            maxy = world_starty;
        }
        if ( yname.toLowerCase(Locale.ENGLISH).contains("time") ) {
            // Calculate time from the map scale...

            String tmin;
            String tmax;
            if ( !draw ) {
                if (time_origin != null && time_units != null && calendar != null ) {
                    tmin = DateTimeWidget.formatDate(miny, time_origin, time_units, calendar);
                    tmax = DateTimeWidget.formatDate(maxy, time_origin, time_units, calendar);
                } else {
                    tmin = time_min;
                    tmax = time_max;
                }
            } else {
                tmin = time_min;
                tmax = time_max;
            }
            ctay1 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, yid, yname, tmin, yname, dFormat.format(miny), "gt");
            ctay2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, yid, yname, tmax, yname, dFormat.format(maxy), "le");
        } else {
            ctay1 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, yid, yname, dFormat.format(miny), yname, dFormat.format(miny), "gt");
            ctay2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, yid, yname, dFormat.format(maxy), yname, dFormat.format(maxy), "le");
        }
        if ( ctay1 != null ) {
            boundByFixed(ctay1);
            if ( v.getName().equals(yname) ) {
                constraintWidgetGroup.setLhs(ctay1.getKeyValue());
            }
            if ( constraintWidgetGroup.contains(ctay1) ) {
                constraintWidgetGroup.remove(ctay1);
            }
            constraintWidgetGroup.addConstraint(ctay1);
        }
        if ( ctay2 != null ) {
            boundByFixed(ctay2);    
            if ( v.getName().equals(yname) ) {
                constraintWidgetGroup.setRhs(ctay2.getKeyValue());
            }
            if ( constraintWidgetGroup.contains(ctay2) ) {
                constraintWidgetGroup.remove(ctay2);
            }
            constraintWidgetGroup.addConstraint(ctay2);
        }
    }
    private void boundByFixed(VariableConstraintAnchor a) {        
        ConstraintDisplay match = (ConstraintDisplay) fixedConstraintPanel.findMatchingAnchor(a);
        if ( match != null ) {
            if ( a.getKey().toLowerCase(Locale.ENGLISH).contains("time") ) {
                String av = a.getValue();
                String mv = match.getValue();
                String op = match.getOp();
                if (op.equals("gt") || op.equals("ge")) {
                    if ( av.compareTo(mv) > 0 ) {
                        a.setValue(mv);
                    }
                } else if ( op.equals("lt") || op.equals("le") ) {
                    if ( av.compareTo(mv) > 0 ) {
                        a.setValue(mv);
                    }
                    
                }
            } else {
                double value = Double.valueOf(a.getValue());
                double matchV = Double.valueOf(match.getValue());
                String op = match.getOp();
                if ( op.equals("gt") || op.equals("ge")) {
                    if ( value < matchV ) {
                        a.setValue(match.getValue());
                    }
                } else if ( op.equals("lt") || op.equals("le") ) {
                    if ( value > matchV) {
                        a.setValue(match.getValue());
                    }
                }
            }
        }
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
            
            GridSerializable grid = config.getGrid();
            String zid = "";
            if ( grid != null && grid.hasZ() ) {
            	zid = grid.getZAxis().getID();
            }
            
          
            
            VariableSerializable[] variables = null;
            if (cat != null && cat.isVariableChildren()) {
                DatasetSerializable ds = cat.getDatasetSerializable();
                variables = ds.getVariablesSerializable();
                
                Map<String, String> tabledap = ds.getProperties().get("tabledap_access");
                if ( tabledap != null ) {
                    allvariables = tabledap.get("all_variables").trim();
                }
                
                Map<String, String> cprops = ds.getProperties().get("correlation");
                if ( cprops != null ) {
                    defaultx = cprops.get("default_x");
                    defaulty = cprops.get("default_y");
                    defaultcb = cprops.get("default_color_by");
                }
                List<ERDDAPConstraintGroup> variableOnly = new ArrayList<ERDDAPConstraintGroup>();
                for (Iterator gsIt = gs.iterator(); gsIt.hasNext();) {
                    ERDDAPConstraintGroup erddapConstraintGroup = (ERDDAPConstraintGroup) gsIt.next();
                    if ( erddapConstraintGroup.getType().equals(Constants.VARIABLE_CONSTRAINT) ) {
                        variableOnly.add(erddapConstraintGroup);
                    }
                }
                
                constraintWidgetGroup.init(variableOnly, variables);
            } else {
                Window.alert("Could not get the variables list from the server.");

            }

            if (variables == null) {
                Window.alert("Could not get the variables list from the server.");
            } else {

                VariableSerializable vtosety = null;
                VariableSerializable vtosetx = null;
                VariableSerializable vtosetc = null;
                // Only include variables that are in the all_variables tabledap_access property.
                List<VariableSerializable> included = new ArrayList<VariableSerializable>();
                for (int i = 0; i < variables.length; i++) {
                    VariableSerializable vs = variables[i];
                    
                    String tid = vs.getAttributes().get("trajectory_id");
                    String pid = vs.getAttributes().get("profile_id");
                    String tsid = vs.getAttributes().get("timeseries_id");
                    
                    if ( (tid != null && tid.equals("true")) ||
                    	 ( pid != null && pid.equals("true")) ||
                         ( tsid != null && tsid.equals("true")) ) {
                        dsg_id = vs.getID();
                    }
                    
                   
                    if ( tid != null && tid.equals("true") ) {
                        dsg_id = vs.getID();
                    }
                    if ( !vs.getAttributes().get("grid_type").equals("vector") ) {
                        xAllDatasetVariables.put(vs.getID(), vs);
                    }
                }
                for (Iterator idIt = xAllDatasetVariables.keySet().iterator(); idIt.hasNext();) {
                    String id = (String) idIt.next();
                    VariableSerializable vs = xAllDatasetVariables.get(id);
                    
                    if ( allvariables.contains(vs.getShortname().trim()) ||
                            vs.getShortname().trim().equals("longitude") ||
                            vs.getShortname().trim().equals("latitude")  || 
                            vs.getShortname().trim().equals("depth") || vs.getID().trim().equals(zid) ||
                            vs.getShortname().trim().equals("time") || 
                            vs.getID().equals(dsg_id) ) {
                        included.add(vs);
                    }
                }
                xVariables.setVariables(included);
                yVariables.setVariables(included);
                colorVariables.setVariables(included);
                if ( dsg_id != null && !dsg_id.equals("") ) {
                    colorVariables.setVariable(xAllDatasetVariables.get(dsg_id));
                } else {
                    colorCheckBox.setValue(false);
                }
                
                // These are the variables filtered for vectors and sub-set variables
                List<VariableSerializable> filtered = xVariables.getVariables();
                for (int n = 0; n < filtered.size(); n++) {
                    VariableSerializable variableSerializable = (VariableSerializable) filtered.get(n);
                    xFilteredDatasetVariables.put(variableSerializable.getID(), variableSerializable);
                }
                // If both are set, the first one is x and the second is y.
                // If on is set, it comes from the main interface so it should be on the y-axis
                if ( varTwoId != null && !varTwoId.equals("") ) { 
                	vtosetx = xFilteredDatasetVariables.get(varid);
                	if ( varTwoId != null ) {
                		vtosety = xFilteredDatasetVariables.get(varTwoId);
                	}
                } else {
                	vtosety = xFilteredDatasetVariables.get(varid);
                }
                if ( varThreeId != null && !varThreeId.equals("") ) {
                	vtosetc = xFilteredDatasetVariables.get(varThreeId);
                }
                xVariables.setAddButtonVisible(false);
                yVariables.setAddButtonVisible(false);
                colorVariables.setAddButtonVisible(false);
                
                // vtosety and vtosetx are for the thumbnail to prop-prop, so only take action if both are set.
                if (vtosety != null && vtosetx != null) {
                    yVariables.setVariable(vtosety);
                } else {
                    if ( defaulty != null ) {
                        yVariables.setVariable(xFilteredDatasetVariables.get(defaulty));
                    }
                }
                if (vtosetx != null && vtosety != null) {
                    xVariables.setVariable(vtosetx);
                } else {
                    if ( defaultx != null ) {
                        xVariables.setVariable(xFilteredDatasetVariables.get(defaultx));
                    }
                }
                if (vtosetx != null && vtosety != null && vtosetc != null) {
                    colorVariables.setVariable(vtosetc);
                }
                String grid_type = xVariables.getUserObject(0).getAttributes().get("grid_type");
                if (grid_type.equals("regular")) {
                    operationID = "prop_prop_plot";
                } else if (grid_type.equals("trajectory") || grid_type.equals("profile") ) { // Try it with an OR for now. May require a new operation. //TODO decide if needs a new operation
                    operationID = "Trajectory_correlation_plot";
                }

                setVariables();
                List<Map<String, String>> vcs = lasRequest.getVariableConstraints();
                
                setFixedConstraintsFromRequest(vcs);
                
                // Now that we've done all that work to set up the state, we will over-ride the state with the values in the properties if they exist.
                // Unless we came in with two variables already selected.  :-)
                
                if ( vtosety == null) {
                    if ( defaulty != null ) {
                        yVariables.setSelectedVariableById(defaulty);
                    }
                }
                if ( vtosetx == null) {
                    if ( defaultx != null ) {
                        xVariables.setSelectedVariableById(defaultx);
                    }
                }
                if ( vtosetc == null ) {
                	if ( defaultcb != null ) {
                		colorVariables.setSelectedVariableById(defaultcb);
                	}
                }



                // The request is now set up like a property-property plot
                // request,
                // so save it.
                initialState = new LASRequest(lasRequest.toString());
                updatePlot(true, true);
            }
        }

    };

    private void setConstraintsFromRequest(List<Map<String, String>> vcs) {
        // Start with an empty slate, then add back those from the previous request.
        constraintWidgetGroup.clearConstraints();
        String op1 = "gt";
        String op2 = "le";
        for (Iterator vcIt = vcs.iterator(); vcIt.hasNext();) {
            Map<String, String> con = (Map<String, String>) vcIt.next();
            String varid = con.get("varID");
            String op = con.get("op");
            String value = con.get("value");
            String type = con.get("type");
            if ( type.equals(Constants.VARIABLE_CONSTRAINT) ) {
                VariableSerializable v = xFilteredDatasetVariables.get(varid);        
                VariableConstraintAnchor cta2 = new VariableConstraintAnchor(Constants.VARIABLE_CONSTRAINT, dsid, varid, v.getName(), value, v.getName(), value, op);       
                constraintWidgetGroup.addConstraint(cta2);
            } else if ( type.equals(Constants.TEXT_CONSTRAINT) ) {
                String lhs = con.get("lhs");
                String rhs = con.get("rhs");
                if ( rhs.contains("_ns_") ) {
                    String[] r = rhs.split("_ns_");
                    for (int i = 0; i < r.length; i++) {
                        TextConstraintAnchor cta = new TextConstraintAnchor(Constants.TEXT_CONSTRAINT, dsid, lhs, lhs, r[i], lhs, r[i], "is");
                        constraintWidgetGroup.addConstraint(cta);
                    }

                } else{
                    TextConstraintAnchor cta = new TextConstraintAnchor(Constants.TEXT_CONSTRAINT, dsid, lhs, lhs, rhs, lhs, rhs, "eq");
                    constraintWidgetGroup.addConstraint(cta);
                }
            }
        }
        setConstraints();
    }
    private void setFixedConstraintsFromRequest(List<Map<String, String>> vcs) {
        for (Iterator vcIt = vcs.iterator(); vcIt.hasNext();) {
            Map<String, String> con = (Map<String, String>) vcIt.next();
            String varid = con.get("varID");
            String op = con.get("op");
            String value = con.get("value");
            String id = con.get("id");
            String type = con.get("type");
            if ( type.equals(Constants.VARIABLE_CONSTRAINT) ) {
                VariableSerializable v = xFilteredDatasetVariables.get(varid);
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
        setConstraints();
    }
   
    private void setFixedT(String tlo, String thi) {
        
        ConstraintLabel cta_tlo = new ConstraintLabel(Constants.T_CONSTRAINT, dsid, "time", "time", tlo, "time", tlo, "ge");
        fixedConstraintPanel.add(cta_tlo);   
        ConstraintLabel cta_thi = new ConstraintLabel(Constants.T_CONSTRAINT, dsid, "time", "time", thi, "time", thi, "le");
        fixedConstraintPanel.add(cta_thi);  
        
    }
    private void setFixedXY(String xlo, String xhi, String ylo, String yhi) {
        ConstraintLabel cta_xlo = new ConstraintLabel(Constants.X_CONSTRAINT, dsid, "longitude", "longitude", xlo, "longitude", xlo, "ge");
        fixedConstraintPanel.add(cta_xlo);   
        ConstraintLabel cta_xhi = new ConstraintLabel(Constants.X_CONSTRAINT, dsid, "longitude", "longitude", xhi, "longitude", xhi, "le");
        fixedConstraintPanel.add(cta_xhi);   
        ConstraintLabel cta_ylo = new ConstraintLabel(Constants.Y_CONSTRAINT, dsid, "latitude", "latitude", ylo, "latitude", ylo, "ge");
        fixedConstraintPanel.add(cta_ylo);   
        ConstraintLabel cta_yhi = new ConstraintLabel(Constants.Y_CONSTRAINT, dsid, "latitude", "latitude", yhi, "latitude", yhi, "le");
        fixedConstraintPanel.add(cta_yhi);  
    }
    private int getNumber(Node firstChild) {
        if (firstChild instanceof Text) {
            Text content = (Text) firstChild;
            String value = content.getData().toString().trim();
            return Double.valueOf(value).intValue();
        } else {
            return -999;
        }
    }

    private double getDouble(Node firstChild) {
        if (firstChild instanceof Text) {
            Text content = (Text) firstChild;
            String value = content.getData().toString().trim();
            return Double.valueOf(value).doubleValue();
        } else {
            return -999.;
        }
    }
    private String getString(Node firstChild) {
        String value = "";
        if (firstChild instanceof Text) {
            Text content = (Text) firstChild;
            value = content.getData().toString().trim();
        }
        return value;
    }

    ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {

            String xml = event.getValue();
            if (!xml.equals("")) {
                popHistory(xml);
            } 
            // We are back to the beginning. Do nothing.
        }

    };

    private void setVariables() {
        update.addStyleDependentName("APPLY-NEEDED");
        String vix = xVariables.getUserObject(xVariables.getSelectedIndex()).getID();
        String viy = yVariables.getUserObject(yVariables.getSelectedIndex()).getID();
        lasRequest.removeVariables();
        lasRequest.addVariable(dsid, vix, 0);
        lasRequest.addVariable(dsid, viy, 0);
        if (colorCheckBox.getValue()) {
            String varColor = colorVariables.getUserObject(colorVariables.getSelectedIndex()).getID();
            lasRequest.addVariable(dsid, varColor, 0);
        }
        
    }

    

    private void setConstraints() {
        
        update.addStyleDependentName("APPLY-NEEDED");
        undoState = new LASRequest(lasRequest.toString());
        lasRequest.removeConstraints();
        
        List<ConstraintSerializable> fixedcons = fixedConstraintPanel.getConstraints();
        List<ConstraintSerializable> cons = constraintWidgetGroup.getConstraints();
        // X, Y, Z and T are handled separately
        for (Iterator conIt = fixedcons.iterator(); conIt.hasNext();) {
            ConstraintSerializable constraintSerializable = (ConstraintSerializable) conIt.next();
            if ( constraintSerializable.getType().equals(Constants.VARIABLE_CONSTRAINT) || constraintSerializable.getType().equals(Constants.TEXT_CONSTRAINT)) {
                lasRequest.addConstraint(constraintSerializable);
            }
        }
        lasRequest.addConstraints(cons);
       
    }

   

    private void popHistory(String xml) {

        // If the pop was requested from the undoState in particular and it is
        // not set, return.
        if (xml == null || xml.equals(""))
            return;

        lasRequest = new LASRequest(xml);

        String vx = lasRequest.getVariable(0);
        String vy = lasRequest.getVariable(1);
        String vc = lasRequest.getVariable(2);
        
        xVariables.setSelectedVariableById(vx);
        yVariables.setSelectedVariableById(vy);
        if ( vc != null && !vc.equals("") ) {
            colorVariables.setSelectedVariableById(vc);
        }

        
        String count = lasRequest.getProperty("data", "count");
        int c = 2;
        try {
            c = Integer.valueOf(count);
        } catch (Exception e) {
            // Pretend it's 2.
        }
        if (vc != null && !vc.equals("") && c >= 3) {
            colorVariables.setSelectedVariableById(vc);
            colorCheckBox.setValue(true);
            colorVariables.setEnabled(true);
        } else {
            colorCheckBox.setValue(false);
            colorVariables.setEnabled(false);
        }

        List<Map<String, String>> vcons = lasRequest.getVariableConstraints();
        setConstraintsFromRequest(vcons);

        setVariables();

        String cruise_list = lasRequest.getProperty("ferret", "cruise_list");
        if ( cruise_list != null ) {
            String[] chicons = cruise_list.split(",");
            currentIconList = Arrays.asList(chicons);
            cruiseIcons.clear();
            cruiseIcons.setCheckedIcons(currentIconList);
        } else {
            cruiseIcons.clear();
        }
        initialHistory = "";
        hasInitialHistory = false;
        updatePlot(false, true);

    }

    private void pushHistory(String xml) {
        History.newItem(xml, false);
    }

    private void warn(String message) {
        String grid_type = xVariables.getUserObject(0).getAttributes().get("grid_type");
        // Don't warn if the grid type is regular...
        if (!grid_type.contains("reg")) {
            warnText.setText(message);
            warning.show();
        }
    }

    /**
     * uses {@link plotImage}
     * 
     * @param newPlotImageWidth
     */
    void setPlotWidth(int newPlotImageWidth) {
        autoZoom = true;
        int pwidth = Math.max(newPlotImageWidth, image_w_min);
        if (autoZoom) {
            imageScaleRatio = 1.;

            // If the panel is less than the image, shrink the image.
            double h = ((Double.valueOf(image_h) / Double.valueOf(image_w)) * Double.valueOf(pwidth));
            imageScaleRatio = h / Double.valueOf(image_h);
            // Scale it every time. If it's going to grow set it back to 1.
            if ( imageScaleRatio > 1.0 ) {
                imageScaleRatio = 1.0;
            }

            if (plotImage != null) {
                // set the plotImage to the grid before scaling so plotImage's
                // imageElement will be valid
                outputPanel.setWidget(2, 0, plotImage);
                plotImage.setVisible(false);
                drawToScreenScaled(imageScaleRatio);
            }
        } else {
            setImageSize(fixedZoom);
        }
        if (spin.isVisible()) {
            spinSetPopupPositionCenter(plotImage);
        }
        outputPanel.setWidth(getPlotWidth());
        // Piggy back setting the annotations width onto this method.
        lasAnnotationsPanel.setPopupWidth(getPlotWidth());
    }

    /**
     * Position spin at the center of the plotWidget
     * 
     * @param plotWidget
     */
    void spinSetPopupPositionCenter(Widget plotWidget) {
        int absoluteLeft = plotWidget.getAbsoluteLeft();
        int offsetWidth = plotWidget.getOffsetWidth();
        int absoluteTop = plotWidget.getAbsoluteTop();
        int offsetHeight = plotWidget.getOffsetHeight();
        int left = absoluteLeft + (offsetWidth / 2);
        int top = absoluteTop + (offsetHeight / 2);
        spin.setPopupPosition(left, top);
    }

    private void drawToScreenScaled(double scaleRatio) {
        
            scaledImage = scaleImage(scaleRatio);
            drawToScreen(scaledImage);
        
    }

    private ImageData scaleImage(double scaleToRatio) {
        Canvas canvasTmp = Canvas.createIfSupported();
        Context2d context = canvasTmp.getContext2d();

        int imageHeight = plotImage.getHeight();
        double ch = (imageHeight * scaleToRatio);
        int imageWidth = plotImage.getWidth();
        double cw = (imageWidth * scaleToRatio);

        canvasTmp.setCoordinateSpaceHeight((int) ch);
        canvasTmp.setCoordinateSpaceWidth((int) cw);

        // TODO: make a temp imageElement?
        ImageElement imageElement = ImageElement.as(plotImage.getElement());

        // s = source
        // d = destination
        double sx = 0;
        double sy = 0;
        int imageElementWidth = imageElement.getWidth();
        if (imageElementWidth <= 0) {
            imageElementWidth = imageWidth;
        }
        double sw = imageElementWidth;
        int imageElementHeight = imageElement.getHeight();
        if (imageElementHeight <= 0) {
            imageElementHeight = imageHeight;
        }
        double sh = imageElementHeight;

        double dx = 0;
        double dy = 0;
        double dw = imageElementWidth;
        double dh = imageElementHeight;

        // tell it to scale image
        context.scale(scaleToRatio, scaleToRatio);

        // draw image to canvas
        context.drawImage(imageElement, sx, sy, sw, sh, dx, dy, dw, dh);

        // get image data
        double w = dw * scaleToRatio;
        double h = dh * scaleToRatio;
        ImageData imageData = null;
        try {
            imageData = context.getImageData(0, 0, w, h);
        } catch (Exception e) {
            // well, bummer
        }

        int ht = (int) h + 10;
        int wt = (int) w + 10;
             
        // Clear the div, clear the drawing canvas then reinsert. Otherwise, ghosts of the previous image appear.
        canvasDiv.clear();
                
        imageCanvasContext.clearRect(0, 0, imageCanvas.getCoordinateSpaceWidth(), imageCanvas.getCoordinateSpaceHeight());
        
        canvasDiv.add(imageCanvas, 0, 0);
        canvasDiv.add(drawingCanvas, 0, 0);
        
        imageCanvas.setCoordinateSpaceHeight(ht);
        imageCanvas.setCoordinateSpaceWidth(wt);
        
        drawingCanvas.setCoordinateSpaceHeight(ht);
        drawingCanvas.setCoordinateSpaceWidth(wt);
        
        
        canvasDiv.setSize(wt+"px", ht+"px");
        
        return imageData;
    }

    private void drawToScreen(ImageData imageData) {
        if (imageCanvasContext != null && imageData != null)
            imageCanvasContext.clearRect(0, 0, imageCanvas.getCoordinateSpaceWidth(), imageCanvas.getCoordinateSpaceHeight());
            imageCanvasContext.putImageData(imageData, 0, 0);
    }

    public void setImageSize(int percent) {
        fixedZoom = percent;
        double factor = percent / 100.;
        int w = (int) (Double.valueOf(image_w).doubleValue() * factor);
        int h = (int) (Double.valueOf(image_h).doubleValue() * factor);
        outputPanel.setWidth(w + "px");
        outputPanel.setHeight(h + "px");
        autoZoom = false;
    }

    String getPlotWidth() {
        int antipadding = 0;// 100;
        String w = CONSTANTS.DEFAULT_ANNOTATION_PANEL_WIDTH();
        if (plotImage != null) {
            if (imageScaleRatio == 0.0)
                imageScaleRatio = 1.0;
            int wt = (int) ((plotImage.getWidth() - antipadding) * imageScaleRatio);
            w = wt + "px";
        }
        return w;
    }

    /**
     * @param plotImage
     * @param newPlotImageHeight
     */
    void setPlotHeight(IESafeImage plotImage, int newPlotImageHeight) {
        autoZoom = true;
        pwidth = (int) ((image_w / image_h) * Double.valueOf(newPlotImageHeight));
        setPlotWidth(pwidth);
    }

    /**
     * @param windowWidth
     * @param windowHeight
     */
    void resize(int windowWidth, int windowHeight) {
        try {
            
            IESafeImage plotImage = null;
            if ( outputPanel.getRowCount() > 2 ) {
                plotImage = (IESafeImage) outputPanel.getWidget(2, 0);
            }

            if (plotImage != null) {
                // Check width first
                int leftPaddingWidth = RootPanel.get("leftPadding").getOffsetWidth();
                int leftControlsWidth = RootPanel.get("leftControls").getOffsetWidth();
                int newPlotImageWidth = windowWidth - leftPaddingWidth - leftControlsWidth;
                int plotImageWidth = plotImage.getWidth();
                if (newPlotImageWidth != plotImageWidth) {
                    setPlotWidth(newPlotImageWidth);
                    // Check that height is still small enough
                    int newPlotImageHeight = windowHeight - topAndBottomPadding - lasAnnotationsPanel.getOffsetHeight();
                    int plotImageHeight = plotImage.getHeight();
                    setPlotHeight(plotImage, newPlotImageHeight);
                    
                } else {
                    // It's the correct width, so now check the height
                    int newPlotImageHeight = windowHeight - topAndBottomPadding - lasAnnotationsPanel.getOffsetHeight();
                    int plotImageHeight = plotImage.getHeight();
                    if (newPlotImageHeight != plotImageHeight) {
                        setPlotHeight(plotImage, newPlotImageHeight);
                        // Check that width is still small enough
                        leftPaddingWidth = RootPanel.get("leftPadding").getOffsetWidth();
                        leftControlsWidth = RootPanel.get("leftControls").getOffsetWidth();
                        newPlotImageWidth = windowWidth - leftPaddingWidth - leftControlsWidth;
                        plotImageWidth = plotImage.getWidth();
                        setPlotWidth(newPlotImageWidth);
                    }
                }
            }
        } catch (Exception e) {
            // Well, bummer
        }
    }

    /**
     * @param down
     */
    void showAnnotations(boolean down) {
        annotationsButton.setDown(down);
        lasAnnotationsPanel.setVisible(down);
        if (down) {
            annotationsButton.setTitle("Click to hide the annotations of the plot.");
        } else {
            annotationsButton.setTitle("Click to show the annotations of the plot.");
        }
        resize(Window.getClientWidth(), Window.getClientHeight());
    }

    ChangeHandler constraintChange = new ChangeHandler() {

        @Override
        public void onChange(ChangeEvent event) {
            TextBox w = (TextBox) event.getSource();
            Widget wp = w.getParent();
            Widget gp = wp.getParent();
            if (gp instanceof VariableConstraintWidget) {
                VariableConstraintWidget vcw = (VariableConstraintWidget) gp;
                vcw.setApply(true);
            }
            setConstraints();
        }

    };

    ClickHandler applyHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent arg0) {
            update.addStyleDependentName("APPLY-NEEDED");
            setConstraints();
        }

    };
    CancelEvent.Handler cancelRequestHandler = new CancelEvent.Handler() {

        @Override
        public void onCancel(CancelEvent event) {
            if (event.getID().equals("Correlation")) {
                currentURL = currentURL + "&cancel=true";
                RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, currentURL);
                try {

                    lasAnnotationsPanel.setError("Fetching plot annotations...");
                    sendRequest.sendRequest(null, lasRequestCallback);
                } catch (RequestException e) {
                    Window.alert("Unable to cancel request.");
                }
            }
        }
    };
    private String plotVariable(String id) {
        VariableSerializable varX = xVariables.getUserObject(xVariables.getSelectedIndex());
        VariableSerializable varY = yVariables.getUserObject(yVariables.getSelectedIndex());
        
        if (varX.getID().equals(id))
            return "x";
        if (varY.getID().equals(id))
            return "y";
        return "";
    }
    protected String getAnchor() {
        String url = Window.Location.getHref();
        if (url.contains("#")) {
            return url.substring(url.indexOf("#") + 1, url.length());
        } else {
            return "";
        }

    }
    
}
