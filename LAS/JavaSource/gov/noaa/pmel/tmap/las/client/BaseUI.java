package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.activity.HelpMenuBar;
import gov.noaa.pmel.tmap.las.client.event.ControlVisibilityEvent;
import gov.noaa.pmel.tmap.las.client.event.MapChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.AlertButton;
import gov.noaa.pmel.tmap.las.client.laswidget.AnalysisWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintWidgetGroup;
import gov.noaa.pmel.tmap.las.client.laswidget.DatasetButton;
import gov.noaa.pmel.tmap.las.client.laswidget.ESGFSearchButton;
import gov.noaa.pmel.tmap.las.client.laswidget.LinkButton;
import gov.noaa.pmel.tmap.las.client.laswidget.MultiVariableSelector;
import gov.noaa.pmel.tmap.las.client.laswidget.NavAxesGroup;
import gov.noaa.pmel.tmap.las.client.laswidget.OperationsWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.OptionsButton;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputControlPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControlsOldAndComplicated;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControls;
import gov.noaa.pmel.tmap.las.client.map.MapSelectionChangeListener;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the base class for an LAS user interface. It populates a left-hand
 * navigation all of the LAS controls and places one or more OutputPanels on the
 * screen to catch and display the output from LAS.
 * 
 * @author rhs
 * 
 */
public class BaseUI {
	

	   RootPanel rootPanel;
	   HelpMenuBar hmb = new HelpMenuBar();

	 
	       
    private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);

    static final String ANNOTATIONS_CONTROL_DOWN_TOOLTIP = "Click to hide the annotations of the plots.";

    static final String ANNOTATIONS_CONTROL_UP_TOOLTIP = "Click to show the annotations of the plots.";

    static final String XTOGGLE_CONTROL_DOWN_TOOLTIP = "Click to show the controls.";

    static final String XTOGGLE_CONTROL_UP_TOOLTIP = "Click to hide the controls.";
    
    ConstraintWidgetGroup xTrajectoryConstraint = new ConstraintWidgetGroup();
    
    VerticalPanel xLeftPanel = new VerticalPanel();
    VerticalPanel xRightPanel = new VerticalPanel();
    ListBox compareMenu = new ListBox();
   
    public ClickHandler annotationsClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            boolean showAnnotations = annotationsControl.isDown();
            setAnnotationsMode(annotationsControl, showAnnotations);
        }
    };
    protected MapSelectionChangeListener mapListener = new MapSelectionChangeListener() {

        @Override
        public void onFeatureChanged() {
            eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            double ylo = xAxesWidget.getRefMap().getYlo();
            double yhi = xAxesWidget.getRefMap().getYhi();
            double xlo = xAxesWidget.getRefMap().getXlo();
            double xhi = xAxesWidget.getRefMap().getXhi();
            eventBus.fireEventFromSource(new MapChangeEvent(ylo, yhi, xlo, xhi), xPanels.get(0));
        }
    };

    private int xButtonLayoutIndex = 0;
    private int xOtherControlsIndex = 0;

    // A "global" annotations toggle button. If you want it in your UI, place it
    // somewhere in the layout.
    // Having it in the superclass allows it to be toggled in the handler for
    // any annotations button push.
    ToggleButton annotationsControl = new ToggleButton("Annotations", "Annotations", annotationsClickHandler);

    /*
     * A global "apply" control
     */
    AlertButton applyButton = new AlertButton("Update Plots", Constants.UPDATE_NEEDED);
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    Boolean showAnnotationsByDefault = true;
    /*
     * Classes that extend BaseUI can keep track of xVariable's siblings
     */
    List<VariableSerializable> variables = new ArrayList<VariableSerializable>();
    List<VariableSerializable> xAdditionalVariables = new ArrayList<VariableSerializable>();
    // Analysis controls in the navigation panel
    AnalysisWidget xAnalysisWidget;
    CheckBox xApplyAnalysis = new CheckBox("Apply Analysis");
    // The controls themselves
    NavAxesGroup xAxesWidget;
    int xBottomPad = 72;
    ClickHandler xButtonCloseHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent arg0) {
            
            pickerCloseActions();
            
        }

    };
    protected void pickerCloseActions() {
        xAxesWidget.restorePanels();
        xOperationsWidget.setOpen(true);
        if ( xVariable != null && xVariable.isDiscrete() ) {
            xAnalysisWidget.setVisible(false);
        } else {
            xAnalysisWidget.setVisible(true);
        }
    }
    /*
     * Button layout for the required button controls for any UI
     */
    FlexTable xButtonLayout = new FlexTable();
    ClickHandler xButtonOpenHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent arg0) {
            closeLeftNav();
        }

    };
    /*
     * Keep track of any incoming ESGF catagory ids.
     */
    String[] xCatIDs;
    String xContainerType = Constants.FRAME;
    int xControlsWidth = (int) Constants.CONTROLS_WIDTH;

    String xControlsWidthPx = xControlsWidth + "px";

    /*
     * Every Plot UI must have a data set and a plot options button
     */
    DatasetButton xDatasetButton = new DatasetButton();

    /*
     * Every UI that wants to extent this base must register their own data set
     * selection handler.
     */
    SelectionHandler<TreeItem> xDatasetSelectionHandler;
    String xDataURL;
    int xDefaultImageWidth = 765;

    FlexTable xDisplayControls = new FlexTable();

    String xCATID;
    String xDSID = null;
    ESGFSearchButton xESGFSearchButton;
    /**
     * Control widget that sets the size of the image in the panel. If set to
     * auto the panel resizes with the browser up to the size of the actual
     * image. If set to 90% to 30% the image is set to a fixed size as a
     * percentage of the actual image If set to 100% the image is fixed at full
     * size.
     * 
     * Any size transformation to get to less than 100% is done by the browser.
     **/
    ListBox xImageSize = new ListBox();

    Label xImageSizeLabel = new Label("Image zoom: ");

    // Left-side navigation widgets
    FlexTable xNavigationControls = new FlexTable();

    /**
     * Contains the visualization panels for this UI that have just been
     * created. This list is used and cleared out by the
     * {@link setupPanelsAndRefresh} method.
     **/
    List<OutputPanel> xNewPanels = new ArrayList<OutputPanel>();
    VariableSerializable xNewVariable;

    String xOperationID;
    ClickHandler xOperationsClickHandler = null;

    // ComparisonAxisSelector xComparisonAxesSelector;
    OperationsWidget xOperationsWidget = new OperationsWidget("Operations");

    String xOptionID;
    OptionsButton xOptionsButton;

    ClickHandler xOptionsClickHandler;

    /*
     * Keep track of the axes orthogonal to the view.
     */
    List<String> xOrtho = new ArrayList<String>();

    FlexTable xOtherControls = new FlexTable();

    // Number of panels, controls behavior of some listeners.
    int xPanelCount;
    boolean xPanelHeaderHidden = false;

    /**
     * Contains the visualization panels for this UI. They are laid out in the
     * xPanelTable
     **/
    List<OutputPanel> xPanels = new ArrayList<OutputPanel>();

    /**
     * Layout container for the visualization panels for this UI. They are
     * listed in xPanels
     **/
    FlexTable xPanelTable = new FlexTable();

    int xPanelWidth;

    /*
     * Make an HTML only popup page that can be printed.
     */
    PushButton xPrinterFriendlyButton;
    
    // Get a link to the current page..
    LinkButton xLinkButton;

    // Controls for the panel sizes and to hide and show the headers.

    /*
     * Keep track of the open handler so it can be removed
     */
    HandlerRegistration xRegisterDatasetOpenHandler;
    /*
     * Keep track of the selection handler so it can be removed
     */
    HandlerRegistration xRegisterDatasetSelectionHandler;
    // Same with the operations click handler...
    HandlerRegistration xRegisterOperationsHandler;
    // The default handler when OK is clicked. Everybody has to implement their
    // own.
    HandlerRegistration xRegisterOptionsHandler;
    int xRightPad = 70;
    // Disclosure panel to open and close all the left-hand controls
    FlowPanel xSettingsHeader = new FlowPanel();// DisclosurePanel("General Controls");
    String xThi;

    /*
     * Get a tile server from the native JavaScript and use it if it has been
     * set.
     */
    String xTileServer;
    String xTileLayer;

    String xTlo;

    // Top level button to hide/show every control.
    ToggleButton xToggleControls = new ToggleButton("<", ">", new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            handlePanelShowHide();
        }
    });
    int xTopPad = 90;

    /*
     * Keep track of the current variable and the new variable when switching.
     */
    VariableSerializable xVariable;

    String xVarID;

    /*
     * Keep track of where you are in the button panel for when sub-classes add
     * to it.
     */

    String xView;

    String xXhi;

    // private int pheightMin = 144;

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */

    /*
     * These are optional parameters that can be used to set the xyzt ranges for
     * the initial plot in the panels.
     */
    String xXlo;

    String xYhi;

    String xYlo;

    String xZhi;

    String xZlo;

    public native void activateNativeHooks()/*-{
		var localObject = this;
		$wnd.updateMapSelection = function(slat, nlat, wlon, elon) {
			$entry(localObject.@gov.noaa.pmel.tmap.las.client.BaseUI::setMapSelection(DDDD)(slat, nlat, wlon, elon));
		}
		$wnd.updateCurrentURL = function(url) {
			$entry(localObject.@gov.noaa.pmel.tmap.las.client.BaseUI::setCurrentURL(Ljava/lang/String;)(url));
		}
		$wnd.setAnnotationURLfromJS = function(url) {
			$entry(localObject.@gov.noaa.pmel.tmap.las.client.BaseUI::setAnnotationsHTMLURL(Ljava/lang/String;)(url));
		}
    }-*/;

    public void addApplyHandler(ClickHandler handler) {
        applyButton.setVisible(true);
        applyButton.addClickHandler(handler);
    }

    public void addMenuButtons(Widget buttons) {
        xButtonLayout.setWidget(0, xButtonLayoutIndex++, buttons);
        setTopLeftAlignment(xButtonLayout);
    }

    public void addPanelMapChangeHandler(MapSelectionChangeListener handler) {
        for (int i = 0; i < xPanelCount; i++) {
            xPanels.get(i).setMapSelectionChangeLister(handler);
        }
    }

    // TODO: Standardize and reuse this method or remove it after putting its
    // code back into xButtonOpenHandler
    public void closeLeftNav() {
        xAxesWidget.closePanels();
        xOperationsWidget.setOpen(false);
        xAnalysisWidget.setVisible(false);

    }

    public int getPanelWidth(int count) {
        int cols = 1;
        if (count > 1) {
            cols = 2;
        }
        int width;
        int win = Window.getClientWidth();
        if (xPanelHeaderHidden) {
            width = (win - xRightPad) / cols;
        } else {
            width = (win - (xRightPad + xControlsWidth)) / cols;
        }
        if (width <= 0) {
            width = 400;
        }
        return width;
    }

    public void handlePanelShowHide() {
        if (xPanelHeaderHidden) {
            xNavigationControls.setVisible(true);
            xDatasetButton.setVisible(true);
            applyButton.setVisible(true);
            annotationsControl.setVisible(true);
            xOptionsButton.setVisible(true);
            compareMenu.setVisible(true);
            if (xCatIDs != null && xCatIDs.length > 0) {
                xESGFSearchButton.setVisible(true);
            }
            if ( xTrajectoryConstraint.isActive() ) {
                xTrajectoryConstraint.setVisible(true);
            } else {
                // Redundantly make sure it is hidden...
                xTrajectoryConstraint.setVisible(false);
            }
            xToggleControls.setTitle(XTOGGLE_CONTROL_UP_TOOLTIP);
            // Must flip this flag BEFORE firing events
            xPanelHeaderHidden = !xPanelHeaderHidden;
            // Show the controls and wait for event notification to resize
            eventBus.fireEventFromSource(new ControlVisibilityEvent(true), this);
        } else {
           
            xNavigationControls.setVisible(false);
            xDatasetButton.setVisible(false);
            applyButton.setVisible(false);
            annotationsControl.setVisible(false);
            xOptionsButton.setVisible(false);
            xESGFSearchButton.setVisible(false);
            compareMenu.setVisible(false);
            // Hide it, perhaps redundantly
            xTrajectoryConstraint.setVisible(false);
            xToggleControls.setTitle(XTOGGLE_CONTROL_DOWN_TOOLTIP);
            // Must flip this flag BEFORE firing events
            xPanelHeaderHidden = !xPanelHeaderHidden;
            // Hide the controls
            eventBus.fireEventFromSource(new ControlVisibilityEvent(false), this);
        }
        // resize OutputPanel(s) according to the current Window size
      
        resize(Window.getClientWidth(), Window.getClientHeight());
    }

    /**
     * @wbp.parser.entryPoint
     */
    public void initialize() {
    	
    	// Add the help menu...
    	 
        rootPanel = RootPanel.get("headerBottomRight");
        hmb.setWidth("55px");
        rootPanel.add(hmb);
       
        Element wmsserver = DOM.getElementById("wms-server");
        if ( wmsserver != null )
        	xTileServer = wmsserver.getPropertyString("content");
        Element wmslayer = DOM.getElementById("wms-layer");
        if ( wmslayer != null )
        	xTileLayer = wmslayer.getPropertyString("content");
        xAnalysisWidget = new AnalysisWidget(xControlsWidthPx);
        // Somebody might have already set these. Only get them from the query
        // string if they are null.
        if ( xCATID == null ) {
            xCATID = Util.getParameterString("catid");
        }
        if (xDSID == null) {
            xDSID = Util.getParameterString("dsid");
        }
        if (xVarID == null) {
            xVarID = Util.getParameterString("vid");
        }
        if (xVarID == null) {
            xVarID = Util.getParameterString("varid");
        }
        if (xOperationID == null) {
            xOperationID = Util.getParameterString("opid");
        }
        if (xOptionID == null) {
            xOptionID = Util.getParameterString("optionid");
        }
        if (xView == null) {
            xView = Util.getParameterString("view");
        }
        if (xDataURL == null) {
            xDataURL = Util.getParameterString("data_url");
        }

        // When loading data sets with data_url, if they are not processed the value is the string "null" if it fails.
        if ( xDSID != null && xDSID.equals("null") ) {
            Window.alert("LAS was unable to load the data set you requested. There may be a problem with the CF conventions compliance or data access.");
        }

        // Probably same here...
        xXlo = Util.getParameterString("xlo");
        xXhi = Util.getParameterString("xhi");
        xYlo = Util.getParameterString("ylo");
        xYhi = Util.getParameterString("yhi");
        xZlo = Util.getParameterString("zlo");
        xZhi = Util.getParameterString("zhi");
        xTlo = Util.getParameterString("tlo");
        xThi = Util.getParameterString("thi");

        xCatIDs = Util.getParameterStrings("catid");

        xAxesWidget = new NavAxesGroup("Plot Coordinates", xControlsWidthPx, xTileServer, xTileLayer);
        xAxesWidget.ensureDebugId("xAxesWidget");
        xNavigationControls.ensureDebugId("xNavigationControls");

        xAxesWidget.getRefMap().setMapListener(mapListener);

        xButtonLayoutIndex = 0;
 
        int navControlIndex = 0;
        xNavigationControls.setWidget(navControlIndex++, 0, xSettingsHeader);

        // Make it visible only if it has a handler attached.
        applyButton.setVisible(false);

        xNavigationControls.setWidget(navControlIndex++, 0, xAxesWidget);
        xNavigationControls.setWidget(navControlIndex++, 0, xAnalysisWidget);
        xNavigationControls.setWidget(navControlIndex++, 0, xOperationsWidget);

        xToggleControls.setTitle(XTOGGLE_CONTROL_UP_TOOLTIP);

        xDatasetButton.ensureDebugId("xDatasetButton");

        // This is all to get around the fact that the OpenLayers map is always
        // in front.
        xDatasetButton.addOpenClickHandler(xButtonOpenHandler);
        xDatasetButton.addCloseClickHandler(xButtonCloseHandler);
        xOptionsButton = new OptionsButton(0);
        xOptionsButton.addOpenClickHandler(xButtonOpenHandler);
        xOptionsButton.addCloseClickHandler(xButtonCloseHandler);
        xOptionsButton.ensureDebugId("xOptionsButton");

        xESGFSearchButton = new ESGFSearchButton();
        xESGFSearchButton.addCloseHandler(xButtonCloseHandler);

        xESGFSearchButton.addOpenClickHandler(xButtonOpenHandler);

        xESGFSearchButton.setVisible(false);

        xOptionsClickHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Your user clicked OK on the options panel. You must implement and register your own handler.");
            }

        };

        xRegisterOptionsHandler = xOptionsButton.addOkClickHandler(xOptionsClickHandler);

        xPrinterFriendlyButton = new PushButton("Print...");
        xPrinterFriendlyButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent print) {

                printerFriendly();

            }

        });

        xLinkButton = new LinkButton();
        
        xLeftPanel.add(xButtonLayout);
        xButtonLayout.setWidget(0, xButtonLayoutIndex++, xDisplayControls);
        xButtonLayout.getCellFormatter().setWidth(0, xButtonLayoutIndex - 1, "268");
        xDisplayControls.setWidget(0, 0, xDatasetButton);
        xDisplayControls.setWidget(0, 1, applyButton);
        xDisplayControls.setWidget(0, 2, xToggleControls);
        // xDisplayControls.getCellFormatter().setHeight(0, 2, "18");
        // xDisplayControls.getCellFormatter().setWidth(0, 2, "18");
        // xButtonLayout.setWidget(1, 1, annotationsControl);

        // Make it visible only if it has a handler attached.
        applyButton.setVisible(false);

        applyButton.addStyleDependentName("SMALLER");

        xDatasetButton.ensureDebugId("xDatasetButton");

        // This is all to get around the fact that the OpenLayers map is always
        // in front.
        xDatasetButton.addOpenClickHandler(xButtonOpenHandler);
        xDatasetButton.addCloseClickHandler(xButtonCloseHandler);
        xPrinterFriendlyButton.addStyleDependentName("SMALLER");
        xLinkButton.addStyleDependentName("SMALLER");
        // Other buttons have their style handled in the widget itself.
        //xButtonLayout.setWidget(0, xButtonLayoutIndex++, xOtherControls);
        xRightPanel.add(xOtherControls);
        xButtonLayout.getCellFormatter().setWordWrap(0, xButtonLayoutIndex - 1, false);

        xOtherControls.setWidget(0, xOtherControlsIndex++, xPrinterFriendlyButton);
        xOtherControls.getCellFormatter().setWordWrap(0, xOtherControlsIndex - 1, false);
        xOtherControls.setWidget(0, xOtherControlsIndex++, xLinkButton);
        xOtherControls.getCellFormatter().setWordWrap(0, xOtherControlsIndex - 1, false);
        // xButtonLayout.addStyleName("HEADER-WIDTH");
        setTopLeftAlignment(xButtonLayout);

        
        xLeftPanel.add(xNavigationControls);
        xPanelTable.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        xRightPanel.add(xPanelTable);
        
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                resize(event.getWidth(), event.getHeight());
            }
        });

        // Listen for StringValueChangeEvents from plotImages
        eventBus.addHandler(StringValueChangeEvent.TYPE, new StringValueChangeEvent.Handler() {
            @Override
            public void onValueChange(StringValueChangeEvent event) {
                Object source = event.getSource();
                if ((source != null) && (source instanceof HasName)) {
                    HasName namedSource = (HasName) source;
                    String name = namedSource.getName();
                    if ((name != null) && (name.length() > 6)) {
                        String substring = name.substring(0, 6);
                        if (substring.equalsIgnoreCase("Panel-")) {
                            String imageURL = event.getValue();
                            if (imageURL != null) {
                                resize(Window.getClientWidth(), Window.getClientHeight());
                            }
                        }
                    }
                }
            }
        });
    }

    

    public void setDatasetOpenHandler(OpenHandler<TreeItem> handler) {
        // There is no default open handler, so if it's null don't remove it.
        if (xRegisterDatasetOpenHandler != null) {
            xRegisterDatasetOpenHandler.removeHandler();
        }
        xRegisterDatasetOpenHandler = xDatasetButton.addOpenHandler(handler);
    }

    /**
     * @param handler
     * @deprecated Currently there are no listeners for {@link SelectionHandler
     *             <TreeItem>} handlers in this class. TODO: Fix that
     */
    @Deprecated
    public void setDatasetSelectionHandler(SelectionHandler<TreeItem> handler) {
        xRegisterDatasetSelectionHandler.removeHandler();
        // xRegisterDatasetSelectionHandler = xDatasetButton
        // .addSelectionHandler(handler);
        xRegisterDatasetSelectionHandler = eventBus.addHandler(SelectionEvent.getType(), handler);
    }

    public void setOperationsClickHandler(ClickHandler handler) {
        // This gets pushed on to every radio button in the operations widget.
        // Right now it seems to unwieldy to keep track of them all so I'll just
        // check that I have one.
        xOperationsClickHandler = handler;
        xOperationsWidget.addClickHandler(handler);
    }

    public void setOptionsOkHandler(ClickHandler handler) {
        xRegisterOptionsHandler.removeHandler();
        xRegisterOptionsHandler = xOptionsButton.addOkClickHandler(handler);
    }

    /**
     * Sets up OutputPanel(s) by, based on numPanels, setting applyButton's text
     * to "Update Plot" or "Update Plots", removing OutputPanels or adding new
     * ones (using container_type and setting their widths). It then sets up the
     * xImageSize control widget that sets the size of the image in the panel.
     * 
     * @param numPanels
     * @param container_type
     */
    public void setupOutputPanels(int numPanels, String container_type) {
        xContainerType = container_type;

        int col = 0;
        int row = 0;
        if (numPanels == 1) {
            applyButton.setText("Update Plot");
        } else {
            applyButton.setText("Update Plots");
        }

        if (xPanelCount > numPanels) {
            // Remove panels
            for (int p = 0; p < numPanels; p++) {
                if (p % 2 == 0) {
                    col++; // go to the next column
                }
                // If it's odd
                if (p % 2 != 0) {
                    row++; // next row
                    col--; // previous column
                }
            }
            for (int i = numPanels; i < xPanelCount; i++) {
                OutputPanel panel = (OutputPanel) xPanelTable.getWidget(row, col);
                xPanelTable.remove(panel);
                xPanels.remove(panel);
                if (i % 2 == 0) {
                    col++; // go to the next column
                }
                // If it's odd
                if (i % 2 != 0) {
                    row++; // next row
                    col--; // previous column
                }
            }
        } else {
            // Add new panels using current main state objects
            for (int p = 0; p < xPanelCount; p++) {
                if (p % 2 == 0) {
                    col++; // go to the next column
                }
                // If it's odd
                if (p % 2 != 0) {
                    row++; // next row
                    col--; // previous column
                }
            }
            for (int i = xPanelCount; i < numPanels; i++) {
                String title = "Panel-" + i;
                boolean compare_panel = false;
                if (i == 0) {
                    compare_panel = true;
                    // Make sure main data set button has the same name for
                    // event filtering purposes
                    xDatasetButton.setName(title);
                }

                // Put all the controls on the left when the thing only has one panel.
//                boolean singlePanel = true;
//                if ( numPanels > 1 ) {
//                    singlePanel = false;
//                }
                
                // We cannot be in single panel mode for XY views because we'll miss out the ortho axes widgets.
                boolean singlePanel = false;


                OutputPanel panel = new OutputPanel(title, compare_panel, xOperationID, xOptionID, xView, singlePanel, xContainerType, xTileServer, xTileLayer, annotationsControl.isDown());

                xPanelTable.setWidget(row, col, panel);
                xPanelTable.getFlexCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
                xPanels.add(panel);

                // TODO: Utilize MVP design patterns to avoid such tight
                // couplings
//                OutputControlPanel outputControlPanel = panel.getOutputControlPanel();
//                outputControlPanel.getDisplayButton().setVisible(numPanels > 1);
//                VariableControls variableControls = outputControlPanel.getVariableControls();
                // VariableMetadataView variableMetadataView =
                // variableControls.getVariableMetadataView();
                if (xVariable != null) {
                    // Set the data set name in the OutputControlPanel's
                    // VariableControls
                    // TODO: Is there a better way of doing this?
                    // variableMetadataView.getBreadcrumbs().setValue(xVariable.getDSName(),
                    // compare_panel);

                    // Set the variables in this panel's
                    // OutputControlPanel's MultiVariableSelector
                    //final MultiVariableSelector multiVariableSelector = variableControls.getMultiVariableSelector();
                    final VariableControls variableControls = panel.getVariableControls();
                    if ((variables != null) && (variables.size() > 0) && (variables.indexOf(xVariable) >= 0)) {
                        List<VariableSerializable> vs = new ArrayList<VariableSerializable>();
                        vs.addAll(variables);
                        panel.setVariables(vs, xVariable);
                    } else {
                        // Update variables an then set the variables in this
                        // panels' OutputControlPanel's MultiVariableSelector
                        boolean mustUpdateVariablesFromServer = true;
                        if (!compare_panel) {
                            // First try to get the variables from the compare
                            // panel's MultiVariableSelector before bothering
                            // the server
                            OutputPanel comparePanel = (OutputPanel) xPanelTable.getWidget(0, 0);
                            if (comparePanel != null) {
                                
                                variables = comparePanel.getVariables();
                                List<VariableSerializable> vs = new ArrayList<VariableSerializable>();
                                vs.addAll(variables);
                                if ((variables != null) && (variables.size() > 0) && (variables.indexOf(xVariable) >= 0)) {
                                    panel.setVariables(vs, xVariable);
                                    mustUpdateVariablesFromServer = false;
                                }
                            }
                        }
                        if (mustUpdateVariablesFromServer)
                            updateVariablesFromServer(variableControls);
                    }
                }

                if (i % 2 == 0) {
                    col++; // go to the next column
                }
                // If it's odd
                if (i % 2 != 0) {
                    row++; // next row
                    col--; // previous column
                }

                xPanelWidth = getPanelWidth(numPanels);
                panel.setWidth(xPanelWidth);

            }
        }
        xPanelCount = numPanels;
        if ( numPanels == 1 ) {
            xPanelWidth = getPanelWidth(numPanels);
            xPanels.get(0).setWidth(xPanelWidth);
        }
        // xComparisonAxesSelector.setVisible(false);
        // //xAxesWidget.setOrthoTitle("Other Axes");
        // } else {
        // xComparisonAxesSelector.setVisible(true);
        // }
        xImageSize.addItem("Auto", "auto");
        xImageSize.addItem("100%", "100");
        xImageSize.addItem(" 90%", "90");
        xImageSize.addItem(" 80%", "80");
        xImageSize.addItem(" 70%", "70");
        xImageSize.addItem(" 60%", "60");
        xImageSize.addItem(" 50%", "50");
        xImageSize.addItem(" 40%", "40");
        xImageSize.addItem(" 30%", "30");
    }

    protected int getButtonIndex() {
        return xOtherControlsIndex;
    }

    /**
     * @param showAnnotations
     */
    protected void setAnnotationsMode(boolean showAnnotations) {
        setAnnotationsMode(null, showAnnotations);
    }

   

    private void printerFriendly() {
        StringBuilder urlfrag = new StringBuilder(URLUtil.getBaseURL() + "getAnnotations.do?");
        if (xPanelCount == 1) {
            urlfrag.append("template=image_w_annotations.vm&");
        } else {
            urlfrag.append("template=images_w_annotations.vm&");
        }
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            urlfrag.append(panel.getPrintURL());
            if (panelIt.hasNext())
                urlfrag.append("&");
        }
        Window.open(urlfrag.toString(), "_blank", Constants.WINDOW_FEATURES);
    }

    private void resizeOutputPanels() {
        xPanelWidth = getPanelWidth(xPanelCount);
        if (xImageSize.getValue(xImageSize.getSelectedIndex()).equals("auto")) {
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setWidth(xPanelWidth);
            }
        }
    }

    private void setAnnotationsHTMLURL(String url) {

        for (int i = 0; i < xPanelCount; i++) {
            OutputPanel panel = xPanels.get(i);

            xPanels.get(i).setAnnotationsHTMLURL(url);

        }
    }

    /**
     * @param annotationsControl
     * @param showAnnotations
     */
    private void setAnnotationsMode(ToggleButton annotationsControl, boolean showAnnotations) {
        if (annotationsControl == null)
            annotationsControl = this.annotationsControl;
        if (showAnnotations) {
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setAnnotationsOpen(true);
                // panel.setAnnotationsButtonDown(true);
                annotationsControl.setDown(true);
                annotationsControl.setTitle(ANNOTATIONS_CONTROL_DOWN_TOOLTIP);
            }
        } else {
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setAnnotationsOpen(false);
                // panel.setAnnotationsButtonDown(false);
                annotationsControl.setDown(false);
                annotationsControl.setTitle(ANNOTATIONS_CONTROL_UP_TOOLTIP);
            }
        }
    }

    private void setCurrentURL(String url) {
        for (int i = 0; i < xPanelCount; i++) {
            OutputPanel panel = xPanels.get(i);

            xPanels.get(i).setURL(url);

        }
    }

    private void setMapSelection(double s, double n, double w, double e) {
        xAxesWidget.getRefMap().setCurrentSelection(s, n, w, e);
    }

    /**
     * Resize OutputPanel(s) according to the Window size parameters
     * 
     * @param windowWidth
     * @param windowHeight
     */
    void resize(int windowWidth, int windowHeight) {
        if (xPanelCount > 1) {
            resizeOutputPanels();
        } else {
            // First set the outputPanel's height so all of it and some
            // of the output controls are visible
            OutputPanel outputPanel = null;
            try {
                outputPanel = xPanels.get(0);
                if (outputPanel != null) {
                    // set the outputPanel's width so all of it is
                    // visible
                    int xNavCtrlsWidth = xNavigationControls.getOffsetWidth();
                    int pwidth = (windowWidth - xRightPad - xNavCtrlsWidth);
                    outputPanel.setWidth(pwidth);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param flexTable
     * 
     */
    void setTopLeftAlignment(FlexTable flexTable) {
        if (flexTable != null) {
            int rows = flexTable.getRowCount();
            for (int row = 0; row < rows; row++) {
                int cellCount = flexTable.getCellCount(row);
                for (int col = 0; col < cellCount; col++) {
                    if (flexTable.isCellPresent(row, col)) {
                        flexTable.getCellFormatter().setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT);
                        flexTable.getCellFormatter().setVerticalAlignment(row, col, HasVerticalAlignment.ALIGN_TOP);
                        Widget widget = flexTable.getWidget(row, col);
                        if ((widget != null) && (widget.getClass().getName().equals("FlexTable"))) {
                            try {
                                setTopLeftAlignment((FlexTable) widget);
                            } catch (Exception e) {
                               // e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param multiVariableSelector
     */
    void updateVariablesFromServer(final VariableControls variableControls) {
        AsyncCallback updateSubPanelVarsCallback = new AsyncCallback() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failed to get variables from the server for a MultiVariableSelector." + caught.toString());
            }

            @Override
            public void onSuccess(Object result) {
                CategorySerializable[] cats = (CategorySerializable[]) result;
                if (cats != null) {
                    if (cats.length > 1) {
                        Window.alert("Multiple categories found.");
                    } else if (cats.length == 1) {
                        CategorySerializable firstCategorySerializable = cats[0];
                        if ((firstCategorySerializable != null) && (firstCategorySerializable.isVariableChildren())) {
                            DatasetSerializable ds = firstCategorySerializable.getDatasetSerializable();
                            VariableSerializable[] vars = ds.getVariablesSerializable();
                            variables = new Vector<VariableSerializable>();
                            if (xVariable != null)
                                xVarID = xVariable.getID();
                            VariableSerializable tv = null;
                            for (int i = 0; i < vars.length; i++) {
                                variables.add(vars[i]);
                                if (vars[i].getID().equals(xVarID))
                                    tv = vars[i];
                            }
                            List<VariableSerializable> vs = new ArrayList<VariableSerializable>();
                            vs.addAll(variables);
                            variableControls.setVariables(vs, tv);
                        }
                    }
                }
            }
        };
        Util.getRPCService().getCategories(null, xVariable.getDSID(), updateSubPanelVarsCallback);
    }

    public class Mouse {
        public void applyNeeded() {
            applyButton.addStyleDependentName("APPLY-NEEDED");
        }

        public void setZ(double zlo, double zhi) {
            xAxesWidget.getZAxis().setNearestLo(zlo);
            xAxesWidget.getZAxis().setNearestHi(zhi);
        }

        public void updateLat(double ylo, double yhi) {
            xAxesWidget.getRefMap().setCurrentSelection(ylo, yhi, xAxesWidget.getRefMap().getXlo(), xAxesWidget.getRefMap().getXhi());
        }

        public void updateLon(double xlo, double xhi) {
            xAxesWidget.getRefMap().setCurrentSelection(xAxesWidget.getRefMap().getYlo(), xAxesWidget.getRefMap().getYhi(), xlo, xhi);
        }

        public void updateMap(double ylo, double yhi, double xlo, double xhi) {
            xAxesWidget.getRefMap().setCurrentSelection(ylo, yhi, xlo, xhi);
        }
        
        public void updateTime(double tlo, double thi, String time_origin, String unitsString, String calendar) {
            xAxesWidget.getTAxis().setLoByDouble(tlo, time_origin, unitsString, calendar);
            xAxesWidget.getTAxis().setHiByDouble(thi, time_origin, unitsString, calendar);
        }
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
