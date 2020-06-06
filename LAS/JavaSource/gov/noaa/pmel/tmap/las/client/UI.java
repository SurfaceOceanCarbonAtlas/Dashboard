package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.AddSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.ComparisonModeChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.ControlVisibilityEvent;
import gov.noaa.pmel.tmap.las.client.event.ControlVisibilityEvent.Handler;
import gov.noaa.pmel.tmap.las.client.event.ESGFDatasetAddedEvent;
import gov.noaa.pmel.tmap.las.client.event.FeatureModifiedEvent;
import gov.noaa.pmel.tmap.las.client.event.LASRequestEvent;
import gov.noaa.pmel.tmap.las.client.event.MapChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.OperationChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.UpdateFinishedEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.VariablePluralityEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.AnalysisWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.ConstraintAnchor;
import gov.noaa.pmel.tmap.las.client.laswidget.DatasetWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.LASAnnotationsPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.laswidget.OperationPushButton;
import gov.noaa.pmel.tmap.las.client.laswidget.OperationRadioButton;
import gov.noaa.pmel.tmap.las.client.laswidget.OperationsMenu;
import gov.noaa.pmel.tmap.las.client.laswidget.OptionsWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.OutputPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.TextConstraintAnchor;
import gov.noaa.pmel.tmap.las.client.laswidget.UserListBox;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableConstraintAnchor;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableControls;
import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.AnalysisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ArangeSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * A UI widget with one or more panels containing an LAS product with widgets to
 * interact with the specifications of the products.
 * 
 * Everybody that sub-classes BaseUI must implement three handlers for options
 * OK, operations clicks and data set selections.
 * 
 * @author rhs
 * 
 */
public class UI extends BaseUI implements EntryPoint {
	
    protected boolean loadingModule = true;
    private Timer noLongerRequireUpdateTimer;
    private boolean required_update = false;

    // Keep track of the number of variable widgets that are active.
    int activeVariables = 0;

    /*
     * Button to set the contour levels automatically.
     */
    ToggleButton autoContourButton;
    TextBox autoContourTextBox;
    FlexCellFormatter buttonFormatter;

    boolean changeDataset = false;

    FlexTable compareButtonsLayout = new FlexTable();

    /**
     * Set if the init will be the result of a comparison mode change.
     */
    boolean comparisonModeChange;

    /*
     * Button to make slide sorter compute differences
     */
    ToggleButton differenceButton;


    double globalMax = -999999999.;

    /*
     * Global min and max for setting contour levels.
     */
    double globalMin = 999999999.;
 

    Map<String, String> historyOptions;

    /*
     * When the gallery must be initialized with a server call to apply history,
     * this is the history that will be applied in the initVizGalForHistory
     * callback when the call returns.
     */
    String historyString;
    Map<String, String> historyTokens;
    String previousHistory = "";
    /*
     * The initial variable for when we run off the top off the history stack.
     */
    VariableSerializable initial_var;
    String initialHistory;
    
    // Keep track of the current operations
    OperationSerializable[] ops;

    Map<String, String> optionsMapComparePanel;


    // Keep track of the time and Z set in the config properties.
    String tInitialTime = null;

    String tInitialZ = null;

    Map<String, String> tokenMapComparePanel;

    OperationsMenu tOperationsMenu = new OperationsMenu();

    /*
     * The main panel for this UI, has custom vizGal Buttons and the BaseUI main
     * panel
     */
    HorizontalPanel vVizGalPanel = new HorizontalPanel();
    

    /**
     * @wbp.parser.entryPoint
     */
    public void onModuleLoad() {
    	super.initialize();
        initialHistory = getAnchor();
        
        int col = 0;

        tOperationsMenu.addClickHandler(tExternalOperationClickHandler);
        // addMenuButtons(compareButtonsLayout);
        int myButtonIndex = getButtonIndex();
        xOtherControls.setWidget(0, myButtonIndex++, tOperationsMenu);
        xOtherControls.getCellFormatter().setWordWrap(0, myButtonIndex - 1, false);
        setTopLeftAlignment(xButtonLayout);
        vVizGalPanel.add(xLeftPanel);
        vVizGalPanel.add(xRightPanel);
        // vVizGalPanel.setWidget(1, 0, xMainPanel);

        addApplyHandler(settingsButtonApplyHandler);

        // Button to turn on and off difference mode.
        differenceButton = new ToggleButton("Difference Mode");
        differenceButton.ensureDebugId("differenceButton");
        differenceButton.setTitle("Toggle Difference Mode");
        differenceButton.addClickListener(differencesClick);

        xAnalysisWidget.addAnalysisAxesChangeHandler(analysisAxesChange);
        // xAnalysisWidget.addAnalysisCheckHandler(analysisActiveChange);
        eventBus.addHandler(ChangeEvent.getType(), analysisActiveChange);
        xAnalysisWidget.addAnalysisOpChangeHandler(analysisOpChange);
        xAnalysisWidget.setActive(false);
        xAxesWidget.getRefMap().setMapListener(mapListener);
        eventBus.addHandler(FeatureModifiedEvent.TYPE, featureModifiedHandler);
        // Register for widget changes the require a plot refresh so we can auto
        // refresh when the button is checked.
        eventBus.addHandler(WidgetSelectionChangeEvent.TYPE, updateNeededEventHandler);
        // xAxesWidget.addApplyHandler(settingsButtonApplyHandler);
        // Comparison Axes Selector

        // xComparisonAxesSelector.addAxesChangeHandler(compareAxisChangeHandler);

        // Sets the contour levels for all plots based on the global min/max of
        // the data (as returned in the map scale file).
        autoContourButton = new ToggleButton("Auto Colors");
        autoContourButton.ensureDebugId("autoContourButton");
        autoContourButton.setTitle("Set consistent contour levels for all panels.");
        autoContourButton.addClickHandler(autoContour);

        autoContourTextBox = new TextBox();
        autoContourTextBox.ensureDebugId("autoContourTextBox");
        autoContourTextBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            }
        });
        differenceButton.addStyleDependentName("SMALLER");
        autoContourButton.addStyleDependentName("SMALLER");
        autoContourTextBox.addStyleDependentName("SMALLER");
        compareButtonsLayout.setWidget(0, col++, differenceButton);
        compareButtonsLayout.setWidget(0, col++, autoContourButton);
        compareButtonsLayout.setWidget(0, col++, autoContourTextBox);

        annotationsControl = new ToggleButton("Annotations", "Annotations", annotationsClickHandler);
        if (showAnnotationsByDefault) {
            annotationsControl.setTitle(ANNOTATIONS_CONTROL_DOWN_TOOLTIP);
        } else {
            annotationsControl.setTitle(ANNOTATIONS_CONTROL_UP_TOOLTIP);
        }
        annotationsControl.addStyleDependentName("SMALLER");
        // annotationsControl.setValue(showAnnotationsByDefault, true);
        annotationsControl.setDown(showAnnotationsByDefault);
        // Listen for StringValueChangeEvents from LASAnnotationsPanel(s)
        eventBus.addHandler(StringValueChangeEvent.TYPE, new StringValueChangeEvent.Handler() {
            @Override
            public void onValueChange(StringValueChangeEvent event) {
                Object source = event.getSource();
                if ((source != null) && (source instanceof LASAnnotationsPanel)) {
                    String open = event.getValue();
                    boolean isOpen = Boolean.parseBoolean(open);
                    setAnnotationsMode(isOpen);
                }
            }
        });

        xDisplayControls.setWidget(1, 1, annotationsControl);

        xDisplayControls.setWidget(2, 0, xOptionsButton);

        xDisplayControls.setWidget(2, 1, xESGFSearchButton);

        compareMenu.addStyleDependentName("SMALLER");
        compareMenu.addItem("One Plot", "1");
        compareMenu.addItem("Compare 2", "2");
        compareMenu.addItem("Compare 4", "4");
        compareMenu.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                compareMenuChanged();
            }

        });
        eventBus.addHandler(VariablePluralityEvent.TYPE, new VariablePluralityEvent.Handler() {
            @Override
            public void onPluralityChange(VariablePluralityEvent event) {
                Object s = event.getSource();
                if ( s instanceof VariableControls ) {
                    VariableControls vc = (VariableControls) s;
                    if (event.isPlural()) {
                        // Disable compareMenu and provide tool tip to
                        // explain why
                        compareMenu.setEnabled(false);
                        compareMenu
                        .setTitle("Comparisons are only available for plots and charts of one variable. Remove such variables on the offending panel(s) to re-enable this control.");

                        List<UserListBox> boxes = vc.getListBoxes();
                        boolean selected = true;
                        for (int ib = 0; ib < boxes.size(); ib++) {
                            UserListBox box = boxes.get(ib);
                            if (box.getSelectedIndex() < 0) {
                                selected = false;
                            }
                        }
                        if (selected) {
                            xAdditionalVariables.clear();
                            for (int boxesIndex = 0; boxesIndex < boxes.size(); boxesIndex++) {
                                UserListBox box = boxes.get(boxesIndex);
                                box.setAddButtonEnabled(true);
                                int boxSelectionIndex = box.getSelectedIndex();
                                if (boxSelectionIndex >= 0) {
                                    VariableSerializable v = box.getUserObject(boxSelectionIndex);
                                    if (boxesIndex == 0) {
                                        // xVariable = v;
                                        xNewVariable = v;
                                    } else {
                                        xAdditionalVariables.add(v);
                                    }
                                }
                            }

                            if (xAdditionalVariables.size() > 0) {
                                xOperationsWidget.setByNumberOfVariables(xAdditionalVariables.size() + 1);
                                setupMenusForOperationChange();
                                eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), vc);
                            }
                        }
                    } else {
                        if (!compareMenu.isEnabled()) {
                            // Enable compareMenu and remove tool tip
                            compareMenu.setEnabled(true);
                            compareMenu.setTitle("");
                            xAdditionalVariables.clear();
                            xOperationsWidget.setZero(xView);
                            setupMenusForOperationChange();
                            eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), event.getSource());
                        }
                    }
                }
            }
        });
        
        xLeftPanel.add(xTrajectoryConstraint);

        xDisplayControls.setWidget(1, 0, compareMenu);

        // RootPanel.get("vizGal").add(xButtonLayout);
        RootPanel.get("vizGal").add(vVizGalPanel);
        // RootPanel.get("PLOT_LINK").setVisible(false);

        // Listen for ControlVisibilityEvents
        Handler controlVisibilityEventHandler = new ControlVisibilityEvent.Handler() {
            @Override
            public void onVisibilityUpdate(ControlVisibilityEvent event) {
                // Makes sure xPanelHeaderHidden changes are saved in the
                // browser History ONLY when needed

                String historyTokenString = getHistoryTokenString();
                if (historyTokenString != null) {
                    String[] settings = historyTokenString.split("token");
                    HashMap<String, String> tokenMap = Util.getTokenMap(settings[0]);
                    String panelHeaderHiddenString = tokenMap.get("panelHeaderHidden");
                    boolean panelHeaderHidden = Boolean.parseBoolean(panelHeaderHiddenString);
                    String currentAnchor = getAnchor();
                    settings = currentAnchor.split("token");
                    tokenMap = Util.getTokenMap(settings[0]);
                    String anchorPanelHeaderHiddenString = tokenMap.get("panelHeaderHidden");
                    boolean anchorPanelHeaderHidden = Boolean.parseBoolean(anchorPanelHeaderHiddenString);
                    if (anchorPanelHeaderHidden == panelHeaderHidden) {
                        // warning
                    } else {
                        if (loadingModule) {
                        	
                            // Don't change the browser History when loading
                            // module/app for the first time

                        } else {
                            // Browser History needs to be updated

                            History.newItem(historyTokenString, false);
                        }
                    }
                    loadingModule = false;
                }
            }
        };

        // Set the required handlers
        eventBus.addHandler(SelectionEvent.getType(), xVisGalDatasetSelectionHandler);
        eventBus.addHandler(VariableSelectionChangeEvent.TYPE, variableChangeHandler);
        eventBus.addHandler(LASRequestEvent.TYPE, outputPanelRequestsHandler);
        setOperationsClickHandler(xVizGalOperationsClickHandler);
        setOptionsOkHandler(optionsOkHandler);
        addPanelMapChangeHandler(mapListener);
        eventBus.addHandler(ControlVisibilityEvent.TYPE, controlVisibilityEventHandler);

        // Initialize the gallery with an asynchronous call to the server to get
        // variable needed.
        String profile = DOM.getElementById("las-profile").getPropertyString("content");
        if (profile != null && profile.equals(Constants.PROFILE_ESGF)) {
            xESGFSearchButton.setVisible(true);
        }
        if (initialHistory != null && !initialHistory.equals("") && xDataURL == null) {
            String[] settings = initialHistory.split("token");
            List<Map<String, String>> tokens = new ArrayList<Map<String, String>>();
            for (int i = 0; i < settings.length; i++) {
                HashMap<String, String> tokenMap = Util.getTokenMap(settings[i]);
                tokens.add(tokenMap);
            }
            HashMap<String, String> tokenMap = (HashMap<String, String>) tokens.get(0);
            xCATID = tokenMap.get("xCATID");
            xDSID = tokenMap.get("xDSID");
            xVarID = tokenMap.get("varid");
            tokenMap = (HashMap<String, String>) tokens.get(1);
            xOperationID = tokenMap.get("operation_id");
            xView = tokenMap.get("view");
            String panelHeaderHiddenString = tokenMap.get("panelHeaderHidden");
            boolean panelHeaderHidden = Boolean.parseBoolean(panelHeaderHiddenString);
            if (panelHeaderHidden != xPanelHeaderHidden) {
                handlePanelShowHide();
            }
            if (profile != null && profile.equals(Constants.PROFILE_ESGF)) {
                List<String> ncats = new ArrayList<String>();
                if (xCATID != null && !xCATID.equals("")) {
                    ncats.add(xCATID);
                }
                for (int i = 0; i < tokens.size(); i++) {
                    HashMap<String, String> map = (HashMap<String, String>) tokens.get(i);
                    String cid = map.get("catid");
                    if (cid != null && !cid.equals("") && !ncats.contains(cid)) {
                        ncats.add(cid);
                    }
                }
                Util.getRPCService().addESGFDatasets(ncats, addESGFDatasetsCallback);
            }
        }
        if (xDataURL != null) {
            setUpdateRequired(true);
            if (xOperationID == null) {
                xOperationID = "Plot_2D_XY_zoom";
            }
            if (xView == null) {
                xView = "xy";
            }
            Util.getRPCService().getIDMap(xDataURL, initFromURL);
        } else {
            // These can come from the initial history or from the dsid=??? and
            // optionally the varid=??? query parameters.
            if (xDSID != null || xCATID != null) {

                setUpdateRequired(true);
                // Supply some reasonable defaults and go...
                if (xOperationID == null) {
                    xOperationID = "Plot_2D_XY_zoom";
                }
                if (xView == null) {
                    xView = "xy";
                }
                Util.getRPCService().getCategories(xCATID, xDSID, initFromDatasetAndVariable);
            } else {
                Util.getRPCService().getPropertyGroup("product_server", initPanelFromDefaultsCallback);
            }
        }
        History.addValueChangeHandler(historyHandler);
    }
  
    public void applyChange() {
        if (changeDataset) {
            // cs = xAxesWidget.getRefMap().getCurrentSelection();
            // This involves a jump across the wire, so the finishApply gets
            // called in the callback from the getGrid.
            turnOffAnalysis();
            changeDataset();
        } else {
            // No jump required, just finish up now.
            finishApply();
        }
    }

    public void changeDataset() {

        if (xNewVariable.isVector() || xNewVariable.isDiscrete()) {
            autoContourTextBox.setText("");
            autoContourButton.setDown(false);
            autoContourButton.setEnabled(false);
            if ( xView.equals("xy") ) {
                differenceButton.setDown(false);
                differenceButton.setEnabled(true);
            } else {
            	differenceButton.setDown(false);
                differenceButton.setEnabled(false);
            }
        } else {
            autoContourButton.setEnabled(true);
            differenceButton.setDown(false);
            differenceButton.setEnabled(true);
        }

        // Since we are changing data sets, go to the default plot and view.

        if (xOperationID == null || xOperationID.equals("") || xVariable == null || (xVariable.getAttributes().get("grid_type") != xNewVariable.getAttributes().get("grid_type")) ) {
            if (xNewVariable.getAttributes().get("grid_type").equals("regular")) {
                xOperationID = "Plot_2D_XY_zoom";
                xTrajectoryConstraint.setActive(false);
                xTrajectoryConstraint.setVisible(false);
//                getComparePanel().getOutputControlPanel().setVisible(true);
                xAnalysisWidget.setVisible(true);
            } else if (xNewVariable.isVector()) {
                xOperationID = "Plot_vector";
                xTrajectoryConstraint.setActive(false);
                xTrajectoryConstraint.setVisible(false);
//                getComparePanel().getOutputControlPanel().setVisible(true);
                xAnalysisWidget.setVisible(true);
            } else if (xNewVariable.isDiscrete()) {
                if ( xNewVariable.getAttributes().get("grid_type").equals("trajectory") ) {
                    xOperationID = "Trajectory_interactive_plot";
                } else if ( xNewVariable.getAttributes().get("grid_type").equals("trajectoryprofile") ) {
                	xOperationID = "Trajectory_interactive_plot";
                } else if ( xNewVariable.getAttributes().get("grid_type").equals("point") ) {
                	xOperationID = "Point_location_value_plot";
                } else if ( xNewVariable.getAttributes().get("grid_type").equals("profile") ) {
                    xOperationID = "Profile_interactive_plot";
                } else if ( xNewVariable.getAttributes().get("grid_type").equals("timeseries") ) {
                        xOperationID = "Timeseries_interactive_plot";
                } 
                if ( xNewVariable.getProperties().get("tabledap_access") != null ) {
                    xTrajectoryConstraint.setActive(true);
                    xTrajectoryConstraint.setVisible(true);
                    xAnalysisWidget.setVisible(false);
                }
           
            } else {
                xOperationID = "Insitu_extract_location_value_plot";
                xTrajectoryConstraint.setActive(false);
                xLeftPanel.setVisible(false);
            }
        }
        // Regardless, if it's a trajectory we need to change the widget values.
        if ( xNewVariable.isDiscrete() && xNewVariable.getProperties().get("tabledap_access") != null ) {
        	xTrajectoryConstraint.clearConstraints();
            xTrajectoryConstraint.init(xNewVariable.getCATID(), xNewVariable.getDSID(), xNewVariable.getID());
//            getComparePanel().getOutputControlPanel().setVisible(false);
        } else {
//            getComparePanel().getOutputControlPanel().setVisible(true);
            xAnalysisWidget.setVisible(true);
        }
        if (xView == null || xView.equals("")) {
            xView = "xy";
        }
        xVariable = xNewVariable;

        // Get all the config info. View is null to get all operations.
        Util.getRPCService().getConfig(null, xVariable.getCATID(), xVariable.getDSID(), xVariable.getID(), getGridForChangeDatasetCallback);

    }

    public void finishApply() {
        // Check to see if the operation changed. If so, change the tool.
        String op_id = xOperationsWidget.getCurrentOperation().getID();
        String op_view = xOperationsWidget.getCurrentView();
        if (!op_id.equals(xOperationID) && !op_view.equals(xView)) {
            xOperationID = op_id;
            xView = op_view;
        }
        if (historyOptions != null && historyOptions.keySet().size() > 0) {
            xOptionsButton.setOptions(xOptionID, historyOptions);
        }
        // The view may have changed if the operation changed before the apply.
        String av;
        if (xAnalysisWidget.isActive()) {
            av = xAnalysisWidget.getAnalysisAxis();
            setTool(av);
        }
        
        // We are finally ready to transfer the current map selection on to the
        // panels, in the dimension where it applies.
        // TODO: Avoid synchronizing the visible NavAxesGroup xAxesWidget in UI
        // and the invisible (never attached to the web page's DOM)
        // AxesWidgetGroup panelAxesWidgets in the main/compare (upper left)
        // OutputPanel, perhaps by sharing the same OLMapWidget refMap member
        // object. Weusijana 2012-09-20
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            if (panel.isComparePanel()) {
                // Set both Lats and Longs because the current map selection in
                // the non-attached axes group must match all four dimensions
                double ytlo = xAxesWidget.getRefMap().getYlo();
                panel.getAxesWidgets().getRefMap().setCurrentSelection(ytlo, xAxesWidget.getRefMap().getYhi(), xAxesWidget.getRefMap().getXlo(), xAxesWidget.getRefMap().getXhi());
                //panel.setLatLon(xAxesWidget.getRefMap().getCurrentSelection());
            } else if (xView.contains("x") && !xView.contains("y")) {
                panel.setLon(String.valueOf(xAxesWidget.getRefMap().getXlo()), String.valueOf(xAxesWidget.getRefMap().getXhi()));
            } else if (!xView.contains("x") && xView.contains("y")) {
                panel.setLat(String.valueOf(xAxesWidget.getRefMap().getYlo()), String.valueOf(xAxesWidget.getRefMap().getYhi()));
            }
        }
        // Not quite ready yet, because the initalHistory has not yet been applied.
        if ( initialHistory == null ) {
            refresh(false, true, false);
        }
    }
    
    private void setConstraintsFromProperties() {
        // Call if the initial load has this property or if variable in the new data set has this property.
        Map<String, String> constraints = xVariable.getProperties().get("constraints");
        if ( constraints != null ) {
            for (Iterator keyIt = constraints.keySet().iterator(); keyIt.hasNext();) {
            	String op = "is";
            	String type = "selection";
                String key = (String) keyIt.next();
                String value = constraints.get(key);
                if ( value.contains(",") ) {
                	String[] parts = value.split(",");
                	if ( parts.length == 3 ) {
                		type= parts[0];
                		op = parts[1];
                		value = parts[2];
                	} else if ( parts.length == 2 ) {
                		op = parts[0];
                		value = parts[1];
                	}
                }
                String key_trim = key;
                if ( key.contains("_cidx") ) {
                    key_trim = key.substring(0, key.indexOf("_cidx"));
                }
                if ( type != null && ( type.equals("selection") || type.equals("text") ) ) {
                	eventBus.fireEventFromSource(new AddSelectionConstraintEvent(key_trim, value, key_trim, value, op), UI.this);
                } else {
                	eventBus.fireEventFromSource(new VariableConstraintEvent(xVariable.getDSID(), xVariable.getID(), value, op, key_trim, value, op, true), UI.this);

                	
                	                                                     //(String dsid, String varid, String lhs, String op1, String variable, String rhs, String op2, boolean apply)
                }
            }
        }
    }


    private void setConstraintsFromHistory(HashMap<String, String> tokenMap) {
        
        /*
         * This history event uses the same data set and variable as the
         * current state, so we can just set apply the gallery settings.
         */
        /*
         * This section is for keeping track of the constraint panel contents.
         * 
         * Seems like a hack, but hey it's what we got to work with.
         * 
         * The constraintPanelIndex is the stack panel index of the active constraint.
         * 
         * The constraintCount is the number of constraints active.
         * 
         * Then there are "constraintCount" strings of the form TYPE_VARIABLE_cr_VALUE_cr_KEY_cr_KEYVALUE with parameter name constraintN.
         */
         String cCount = tokenMap.get("constraintCount");
         List<ConstraintAnchor> cons = new ArrayList<ConstraintAnchor>();
         if ( cCount != null ) {
             xTrajectoryConstraint.setActive(true);
             xTrajectoryConstraint.setVisible(true);
             xAnalysisWidget.setVisible(false);
             int c = Integer.valueOf(cCount);
             for (int i = 0; i < c; i++) {
                 String con = tokenMap.get("constraint"+i);
                 if ( con.startsWith(Constants.VARIABLE_CONSTRAINT) ) {
                     VariableConstraintAnchor constraintSer = new VariableConstraintAnchor(con);
                     cons.add(constraintSer);
                 } else {
                     TextConstraintAnchor constraintSer = new TextConstraintAnchor(con);
                     cons.add(constraintSer);
                 }
                 
             }
             String indx = tokenMap.get("constraintPanelIndex");
             int panelIndex = Integer.valueOf(indx);
             xTrajectoryConstraint.setSelectedPanelIndex(panelIndex);
             xTrajectoryConstraint.setConstraints(cons);
         } else {
             if ( xVariable != null && !xVariable.isDiscrete() ) {
                 xTrajectoryConstraint.setActive(false);
                 xTrajectoryConstraint.setVisible(false);
                 //             getComparePanel().getOutputControlPanel().setVisible(true);
                 xAnalysisWidget.setVisible(true);
             }
         }
         
    }

    /**
     * This has to do with history tokens that apply to the axes controls and
     * navigation in the main panel which are in a separate widget (the l from
     * the other panels.
     * 
     * @param tokenMap
     * @param optionsMap
     */
    public void setFromHistoryToken(Map<String, String> tokenMap, Map<String, String> optionsMap) {
        xView = tokenMap.get("view");
        xOrtho = Util.setOrthoAxes(xView, xVariable, true);
        xOperationID = tokenMap.get("operation_id");
        xOperationsWidget.setOperation(xOperationID, xView);
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            panel.setOperation(xOperationID, xView);
            String aAx = null;
            if ( xAnalysisWidget.isActive() ) {
            	aAx = xAnalysisWidget.getAnalysisAxis();
            }
            panel.showOrthoAxes(xView, aAx, xPanelCount);
        }

        if (xAnalysisWidget.isActive()) {
            setTool(xAnalysisWidget.getAnalysisAxis());
        } else {
            if ( xVariable.isDiscrete() ) {
                xAxesWidget.getRefMap().setTool("xy");
            } else {
                xAxesWidget.getRefMap().setTool(xView);
            }
        }
        // s, n, w, e
        xAxesWidget.getRefMap().setCurrentSelection(Double.valueOf(tokenMap.get("ylo")), Double.valueOf(tokenMap.get("yhi")), Double.valueOf(tokenMap.get("xlo")), Double.valueOf(tokenMap.get("xhi")));

        if (xVariable.getGrid().hasT()) {
            if (xView.contains("t") || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("t"))) {
                String tlo = tokenMap.get("tlo");
                if (tlo != null) {
                    xAxesWidget.getTAxis().setLo(tlo);
                }
                String thi = tokenMap.get("thi");
                if (thi != null) {
                    xAxesWidget.getTAxis().setHi(thi);
                }
            }
        }

        if (xVariable.getGrid().hasZ()) {
            if (xView.contains("z") || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("z"))) {
                String zlo = tokenMap.get("zlo");
                if (zlo != null) {
                    xAxesWidget.getZAxis().setLo(zlo);
                }
                String zhi = tokenMap.get("zhi");
                if (zhi != null) {
                    xAxesWidget.getZAxis().setHi(zhi);
                }
            }
        }

        if (optionsMap.size() >= 1) {
            xOptionsButton.setState(optionsMap);
        }
        if (initialHistory != null && !initialHistory.equals("") && optionsMap.size() >= 1) {
            historyOptions = optionsMap;
        }
    }

    /**
     * Sets up various Widgets based on state objects' values.
     * 
     * @return false if there are no axes orthogonal to the view on which the
     *         data can be compared.
     */
    public boolean setupWidgets() {

        xOperationsWidget.setOperations(xVariable.getGrid(), xOperationID, xView, ops);
        tOperationsMenu.setMenus(ops, xView);
        xOptionsButton.setOptions(xOperationsWidget.getCurrentOperation().getOptionsID(), xOptionsButton.getState());
        GridSerializable ds_grid = xVariable.getGrid();
        double grid_west = Double.valueOf(ds_grid.getXAxis().getLo());
        double grid_east = Double.valueOf(ds_grid.getXAxis().getHi());

        double grid_south = Double.valueOf(ds_grid.getYAxis().getLo());
        double grid_north = Double.valueOf(ds_grid.getYAxis().getHi());

        double delta = 1.d;

        ArangeSerializable arange = ds_grid.getXAxis().getArangeSerializable();
        if (arange != null) {
            String step = arange.getStep();
            if (step != null) {
                delta = Math.abs(Double.valueOf(step));
            }
        }
        if ( xVariable.isDiscrete() ) {
            xAxesWidget.getRefMap().setTool("xy");
        } else {
            xAxesWidget.getRefMap().setTool(xView);
        }
        xAxesWidget.getRefMap().setDataExtent(grid_south, grid_north, grid_west, grid_east, delta);
        getComparePanel().getAxesWidgets().getRefMap().setDataExtent(grid_south, grid_north, grid_west, grid_east, delta);

        xOrtho = Util.setOrthoAxes(xView, xVariable, true);

        if ( xVariable.isDiscrete() ) {
            xTrajectoryConstraint.setActive(true);
            xTrajectoryConstraint.setVisible(true);
            xAnalysisWidget.setVisible(false);
            List<ConstraintAnchor> cl = xTrajectoryConstraint.getAnchors();
            xTrajectoryConstraint.init(xVariable.getCATID(), xVariable.getDSID(), xVariable.getID());
            xTrajectoryConstraint.setConstraints(cl);
        } else {
            xTrajectoryConstraint.setActive(false);
            xTrajectoryConstraint.setVisible(false);
//            getComparePanel().getOutputControlPanel().setVisible(true);
            xAnalysisWidget.setVisible(true);
        }
        if (xOrtho.size() == 0) {

            xAxesWidget.init(xVariable.getGrid());
            xAxesWidget.showViewAxes(xView, xOrtho, getAnalysisAxis());
            return false;
        } else {

            int pos = 0;

            xAxesWidget.init(xVariable.getGrid());
            xAxesWidget.showViewAxes(xView, xOrtho, getAnalysisAxis());

            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.showOrthoAxes(xView, getAnalysisAxis(), xPanelCount);
                panel.setOrthoRanges(xView);
            }
            return true;
        }
    }

    private boolean applyHistory(Map<String, String> tokenMap) {
        boolean switch_axis = false;
        if (tokenMap.containsKey("panelHeaderHidden")) {
            boolean new_panelHeaderHidden = Boolean.valueOf(tokenMap.get("panelHeaderHidden")).booleanValue();
            // If the new state should be different, handle it.
            // otherwise ignore it.
            if (new_panelHeaderHidden != xPanelHeaderHidden) {
                handlePanelShowHide();
            }
        }

        // TODO: ***Save plot annotation visibility state in History

        if (tokenMap.containsKey("differences")) {
            boolean new_difference = Boolean.valueOf(tokenMap.get("differences")).booleanValue();
            if (new_difference != differenceButton.isDown()) {
                differenceButton.setDown(new_difference);
            }
        }

        xAxesWidget.showViewAxes(xView, xOrtho, getAnalysisAxis());
        // Now set view axis from history tokens.
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            panel.showOrthoAxes(xView, getAnalysisAxis(), xPanelCount);
            panel.setOrthoRanges(xView);
        }

        if (tokenMap.containsKey("globalMin")) {
            globalMin = Double.valueOf(tokenMap.get("globalMin")).doubleValue();
        }
        if (tokenMap.containsKey("globalMax")) {
            globalMax = Double.valueOf(tokenMap.get("globalMax")).doubleValue();
        }

        if (tokenMap.containsKey("autoContour")) {
            boolean new_autoContour = Boolean.valueOf(tokenMap.get("autoContour")).booleanValue();
            autoContourButton.setDown(new_autoContour);
            if (autoContourButton.isDown()) {
                autoScale();
            }
        }

        int count = xImageSize.getItemCount();
        for (int i = 0; i < count; i++) {
            String item_value = xImageSize.getValue(i);
            if (item_value.equals(tokenMap.get("imageSize"))) {
                xImageSize.setSelectedIndex(i);
            }
        }

        return switch_axis;
    }

    /**
     * In instances of changing data sets we need to delay the application of
     * the tokens until after the RPC is finished to set up the dataset.
     * 
     * @param settings
     */
    private void applyTokens(String[] settings) {
        HashMap<String, String> tokenMap = Util.getTokenMap(settings[0]);
        
       
        
        String atype = tokenMap.get("compute");
        String aover = tokenMap.get("over");
        if (atype != null) {
            xAnalysisWidget.setAnalysisType(atype);
        }

        if (aover != null) {
            xAnalysisWidget.setAnalysisAxis(aover);
        }
        if (xAnalysisWidget.isActive()) {
            String v = xAnalysisWidget.getAnalysisAxis();
            setAnalysisAxes(v);
        } else {
            turnOffAnalysis();
        }
        HashMap<String, String> tmComparePanel = Util.getTokenMap(settings[1]);
        HashMap<String, String> omComparePanel = Util.getOptionsMap(settings[1]);
        setFromHistoryToken(tmComparePanel, omComparePanel);
        xPanels.get(0).setFromHistoryToken(tmComparePanel, omComparePanel);
        applyHistory(tokenMap);
        if (xPanels.size() > 1) {
            xPanels.get(0).getVariableControls().getLatestListBox().setAddButtonVisible(false);
        }

        if (xVariable.isVector() || (xVariable.isDiscrete() && !xVariable.isTimeSeries()) ) {
            xPanels.get(0).getVariableControls().getLatestListBox().setAddButtonEnabled(false);
        } else {
            xPanels.get(0).getVariableControls().getLatestListBox().setAddButtonEnabled(true);
        }

        for (int t = 1; t < xPanels.size(); t++) {
            HashMap<String, String> panelTokenMap = Util.getTokenMap(settings[t + 1]);
            HashMap<String, String> optionsMap = Util.getOptionsMap(settings[t + 1]);
            xPanels.get(t).setFromHistoryToken(panelTokenMap, optionsMap);
        }
    }

    private void autoScale() {

        // Use the values from the "compare panel" to set the auto contour
        // levels.
        OutputPanel panel = xPanels.get(0);

        if (panel.getMin() < globalMin) {
            globalMin = panel.getMin();
        }
        if (panel.getMax() > globalMax) {
            globalMax = panel.getMax();
        }

        // Algorithm from range.F subroutine in Ferret source code

        double umin = globalMin;
        double umax = globalMax;
        int nints = 20;

        double temp = (umax - umin) / nints;
        if (temp <= 0.0000000001) {
            temp = umax;
        }

        double nt = Math.floor(Math.log(temp) / Math.log(10.));
        if (temp < 1.0) {
            nt = nt - 1;
        }
        double pow = Math.pow(10, nt);
        temp = temp / pow;

        double dint = 10.0 * pow;
        if (temp < Math.sqrt(2.0)) {
            dint = pow;
        } else {
            if (temp < Math.sqrt(10.0)) {
                dint = 2.0 * pow;
            } else {
                if (temp < Math.sqrt(50.0)) {
                    dint = 5.0 * pow;
                }
            }
        }

        double fm = umin / dint;
        double m = Math.floor(fm);
        if (m < 0) {
            m = m - 1;
        }
        double uminr = Math.round(1000000 * dint * m) / 1000000;

        fm = umax / dint;
        m = Math.floor(fm);
        if (m > 0) {
            m = m + 1;
        }
        double umaxr = Math.round(1000000 * dint * m) / 1000000;

        // END OF FERRET ALGORITHM

        // Only use 4 significant digits

        // Modify the optionTextField and submit the request
        String fill_levels = "(-inf)(" + uminr + "," + umaxr + "," + dint + ")(inf)";

        // These are pretty close to zero. I think the min/max did not come back
        // from the server, so stop
        if ((uminr + .00001 < .0001 && umaxr + .00001 < .0001) || globalMax < -9999999. && globalMin > 9999999.) {
            autoContourTextBox.setText(Constants.NO_MIN_MAX);
            autoContourButton.setDown(false);
        } else {
            autoContourTextBox.setText(fill_levels);
        }
        eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
    }

    private String getAnalysisAxis() {
        String aAxis = null;
        if (xAnalysisWidget.isActive()) {
            aAxis = xAnalysisWidget.getAnalysisAxis();
        }
        return aAxis;
    }

    private String getGalleryToken() {
        StringBuilder historyToken = new StringBuilder();

        historyToken.append("panelHeaderHidden=" + xPanelHeaderHidden);
        historyToken.append(";differences=" + differenceButton.isDown());

        historyToken.append(";autoContour=" + autoContourButton.isDown());
        if (xPanels.get(0).getMin() < 9999999.) {
            historyToken.append(";globalMin=" + xPanels.get(0).getMin());
        }
        if (xPanels.get(0).getMax() > -9999999.) {
            historyToken.append(";globalMax=" + xPanels.get(0).getMax());
        }
        historyToken.append(";xCATID=" + xVariable.getCATID());
        historyToken.append(";xDSID=" + xVariable.getDSID());
        historyToken.append(";varid=" + xVariable.getID());

        historyToken.append(";imageSize=" + xImageSize.getValue(xImageSize.getSelectedIndex()));
        historyToken.append(";over=" + xAnalysisWidget.getAnalysisAxis());
        historyToken.append(";compute=" + xAnalysisWidget.getAnalysisType());
        
        // Right now the constraints are in the gallery. That's gonna change, but...
        
        if ( xTrajectoryConstraint.isActive() ) {
            List<ConstraintAnchor> constraints = xTrajectoryConstraint.getAnchors();
            if ( constraints.size() > 0 ) {
                historyToken.append(";constraintCount="+constraints.size());
            }
            int index = 0;
            for (Iterator iterator = constraints.iterator(); iterator.hasNext();) {
                ConstraintAnchor cta = (ConstraintAnchor) iterator.next();
                historyToken.append(";constraint"+index+"=");
                /**
                 * 
                  String[] parts = con.split("_cr_");
        this.type = parts[0];
        this.dsid = parts[1];
        this.varid = parts[2];
        this.variable = parts[3];
        this.value = parts[4]; 
        this.key = parts[5];
        this.keyValue = parts[6];
        this.op = parts[7];
                 */
                if ( cta.getType() != null && !cta.getType().equals("") ) {
                    historyToken.append(cta.getType()+"_cr_");                
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getDsid() != null && !cta.getDsid().equals("") ) {
                    historyToken.append(cta.getDsid()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getVarid() != null && !cta.getVarid().equals("") ) {
                    historyToken.append(cta.getVarid()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getVariable() != null && !cta.getVariable().equals("") ) {
                    historyToken.append(cta.getVariable()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getValue() != null && !cta.getValue().equals("") ) {
                    historyToken.append(cta.getValue()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getKey() != null && !cta.getKey().equals("") ) {
                    historyToken.append(cta.getKey()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getKeyValue() != null && !cta.getKeyValue().equals("") ) {
                    historyToken.append(cta.getKeyValue()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                if ( cta.getOp() != null && !cta.getOp().equals("") ) {
                    historyToken.append(cta.getOp()+"_cr_");    
                } else {
                    historyToken.append("none_cr_");
                }
                index++;
            }
            int panelIndex = xTrajectoryConstraint.getConstraintPanelIndex();
            historyToken.append(";constraintPanelIndex="+panelIndex);
        }
        
        return historyToken.toString();
    }

    private String getHistoryToken() {
        StringBuilder token = new StringBuilder();

        token.append(";xlo=" + xAxesWidget.getRefMap().getXlo());
        token.append(";xhi=" + xAxesWidget.getRefMap().getXhi());

        token.append(";ylo=" + xAxesWidget.getRefMap().getYlo());
        token.append(";yhi=" + xAxesWidget.getRefMap().getYhi());

        if ((xVariable.getGrid().hasF() && xView.contains("f")) || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("f"))) {
            token.append(";flo=" + xAxesWidget.getFAxis().getFerretDateLo());
            token.append(";fhi=" + xAxesWidget.getFAxis().getFerretDateHi());
        }
        
        if ((xVariable.getGrid().hasE() && xView.contains("e")) || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("e"))) {
            token.append(";elo=" + xAxesWidget.getEAxis().getLo());
            token.append(";ehi=" + xAxesWidget.getEAxis().getHi());
        }
        if ((xVariable.getGrid().hasT() && xView.contains("t")) || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("t"))) {
            token.append(";tlo=" + xAxesWidget.getTAxis().getFerretDateLo());
            token.append(";thi=" + xAxesWidget.getTAxis().getFerretDateHi());
        }
        if ((xVariable.getGrid().hasZ() && xView.contains("z")) || (xAnalysisWidget.isActive() && xAnalysisWidget.getAnalysisAxis().contains("z"))) {
            token.append(";zlo=" + xAxesWidget.getZAxis().getLo());
            token.append(";zhi=" + xAxesWidget.getZAxis().getHi());
        }
        if (xOperationsWidget.getCurrentOperation() != null) {
            token.append(";operation_id=" + xOperationsWidget.getCurrentOperation().getID());
            token.append(";view=" + xOperationsWidget.getCurrentView());
        }
        Map<String, String> options = xOptionsButton.getState();
        for (Iterator<String> opIt = options.keySet().iterator(); opIt.hasNext();) {
            String name = opIt.next();
            String value = options.get(name);
            if (!value.equalsIgnoreCase("default")) {
                token.append(";ferret_" + name + "=" + value);
            }
        }
        return token.toString();
    }

    private void popHistory(boolean shouldAutoRefresh, String historyToken) {
        historyToken = URL.decode(historyToken);
        if (!historyToken.equals("")) {
            setUpdateRequired(true);
            // First split out the panel history
            String[] settings = historyToken.split("token");

            // The first token is the "gallery settings", the ones that follow
            // are for each output panel.
            // The number of panels in the history is settings.length - 1

            if (settings.length - 1 != xPanelCount) {
                setupOutputPanels(settings.length - 1, Constants.IMAGE);
                if (settings.length - 1 == 1) {
                    setupMainPanelAndRefreshForceLASRequest(false);
                    compareMenu.setSelectedIndex(0);
                    compareMenuChanged();
                } else {
                    setupPanelsAndRefreshNOforceLASRequestWithCompareMenus(false);
                    if (settings.length - 1 == 2) {
                        compareMenu.setSelectedIndex(1);
                        compareMenuChanged();
                    } else {
                        compareMenu.setSelectedIndex(2);
                        compareMenuChanged();
                    }
                }
                // Set the state of the system...
            }

            HashMap<String, String> tokenMap = Util.getTokenMap(settings[0]);
            
           
            if ((tokenMap.containsKey("xDSID") && tokenMap.get("xDSID").equals(xVariable.getDSID()))
                    && (tokenMap.containsKey("varid") && tokenMap.get("varid").equals(xVariable.getID()))) {
                // We can do this only because we are reusing the current
                // variable.

                applyTokens(settings);
                // Don't request that the plot refresh happen automatically,
                // don't force panels to update if they don't need to, and don't
                // add to the history stack.
                eventBus.fireEvent(new WidgetSelectionChangeEvent(shouldAutoRefresh, false, false));
            } else {
                historyString = historyToken;
                historyTokens = tokenMap;
                Util.getRPCService().getCategories(tokenMap.get("xCATID"), tokenMap.get("xDSID"), requestGridForHistory);
            }
        }
    }

    private void pushHistory() {
        String historyTokenString = getHistoryTokenString();
        if (initialHistory == null) {
            // Try not to add the same history more than once in a row
            if ( !historyTokenString.equals(previousHistory) ) {
               History.newItem(historyTokenString, false);
               previousHistory = historyTokenString;
            }
        } else {
            previousHistory = initialHistory;
        }
    }

    /**
     * Sets the state of various UI Widgets, perhaps calls
     * comparePanel.refreshPlot(options, false, true, forceLASRequest),
     * panel.computeDifference(options, switchAxis, forceLASRequest), and/or
     * panel.refreshPlot(options, switchAxis, true, forceLASRequest), pushes
     * history if it exits, and then finally calls
     * resize(Window.getClientWidth(), Window.getClientHeight().
     * 
     * @param switchAxis
     * @param history
     * @param forceLASRequest
     *            If true will force an {@link LASRequestEvent} request.
     */
    private void refresh(boolean switchAxis, boolean history, boolean forceLASRequest) {
        if (xView.equals("x") || xView.equals("y") || xView.equals("z") || xView.equals("t")) {
            if (autoContourButton.isDown()) {
                autoContourButton.setDown(false);
                autoContourTextBox.setText("");
            }
            autoContourButton.setEnabled(false);
        } else if (differenceButton.isDown()) {
            if (autoContourButton.isDown()) {
                autoContourButton.setDown(false);
                autoContourTextBox.setText("");
            }
            autoContourButton.setEnabled(false);
        } else {
        	if ( xView.length() == 2 ) {
        		// Only makes sense for contour plot type plots...
        		autoContourButton.setEnabled(true);
        	}
        }

        // You should get a another call to refresh if you are waiting for
        // history...
        if (waitForRPC()) {
            return;
        }

        eventBus.fireEventFromSource(new UpdateFinishedEvent(), this);
        if (autoContourTextBox.getText().equals(Constants.NO_MIN_MAX)) {
            autoContourTextBox.setText("");
        }
        OutputPanel comparePanel = getComparePanel();

        Map<String, String> options = null;

        if (historyOptions != null) {
            options = historyOptions;
            historyOptions = null;
            xOptionsButton.setState(options);
        } else {
            options = xOptionsButton.getState();
        }
        if (differenceButton.isDown()) {
            boolean vector = false;
            boolean scattered = false;

            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                if (!panel.isComparePanel()) {
                    vector = vector || panel.getVariable().isVector();
                    scattered = scattered || panel.getVariable().isDiscrete();
                }
            }

            if ((!xVariable.isVector() && vector) || (!xVariable.isDiscrete() && scattered) || xVariable.isVector() && !vector || (xVariable.isDiscrete() && !scattered)) {
                if ( xView.equals("xy") ) {
                    differenceButton.setDown(false);
                    differenceButton.setEnabled(true);
                } else {
                	differenceButton.setDown(false);
                    differenceButton.setEnabled(false);
                }
                for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                    OutputPanel panel = (OutputPanel) panelIt.next();
                    panel.setVizGalState(xVariable, getHistoryToken(), comparePanel.getHistoryToken());
                    panel.setFillLevels(autoContourTextBox.getText());
                    AnalysisSerializable analysis = null;
                    if (xAnalysisWidget.isActive()) {
                        analysis = xAnalysisWidget.getAnalysisSerializable();
                        analysis.setLabel(xVariable.getName());
                    }
                    panel.setAnalysis(analysis);
                    panel.refreshPlot(options, switchAxis, true, forceLASRequest);
                }
            } else {
                if (autoContourButton.isDown()) {
                    autoContourButton.setDown(false);
                    autoContourTextBox.setText("");
                }
                AnalysisSerializable analysis = null;
                if (xAnalysisWidget.isActive()) {
                    analysis = xAnalysisWidget.getAnalysisSerializable();
                    analysis.setLabel(xVariable.getName());
                }

                comparePanel.setAnalysis(analysis);
                comparePanel.setFillLevels(autoContourTextBox.getText());
                comparePanel.setVizGalState(xVariable, getHistoryToken(), comparePanel.getHistoryToken()+addAnalysisMapValues());
                comparePanel.refreshPlot(options, false, true, forceLASRequest);
                for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                    OutputPanel panel = (OutputPanel) panelIt.next();
                    if (!panel.getID().equals(comparePanel.getID())) {
                        panel.setVizGalState(xVariable, getHistoryToken(), comparePanel.getHistoryToken()+addAnalysisMapValues());
                        AnalysisSerializable a = null;
                        if (xAnalysisWidget.isActive()) {
                            a = xAnalysisWidget.getAnalysisSerializable();
                            a.setLabel(xVariable.getName());
                        }
                        panel.setAnalysis(a);
                        panel.computeDifference(options, switchAxis, forceLASRequest);
                    }
                }
            }
        } else {
            if (xVariable.isVector() || xVariable.isDiscrete()) {
                if ( xView.equals("xy") ) {
                    differenceButton.setDown(false);
                    differenceButton.setEnabled(true);
                } else {
                	differenceButton.setDown(false);
                    differenceButton.setEnabled(false);
                }
            } else {
                differenceButton.setDown(false);
                differenceButton.setEnabled(true);
            }
            // Get the current state of the options...
            if (!autoContourButton.isDown()) {
                // If it's not down, the current options value will be used.
                autoContourTextBox.setText("");
            }
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setVizGalState(xVariable, getHistoryToken(), comparePanel.getHistoryToken());
                panel.setFillLevels(autoContourTextBox.getText());
                AnalysisSerializable analysis = null;
                if (xAnalysisWidget.isActive()) {
                    analysis = xAnalysisWidget.getAnalysisSerializable();
                    analysis.setLabel(xVariable.getName());
                }
                panel.setAnalysis(analysis);
                // This list will be empty if nothing is active...
                panel.setConstraints(xTrajectoryConstraint.getConstraints());
                panel.refreshPlot(options, switchAxis, true, forceLASRequest);
            }
        }
        tOperationsMenu.setGoogleEarthButtonEnabled(xView.equals("xy")); // Why the config does not handle this I don't know. The view should be able to deal with this. Had trajectory code here, but off always now.
        if (history) {
            pushHistory();
        }
        if (initialHistory != null) {
            initialHistory = null;
        }
        
        setUpdateRequired(false);
        
    }
    private String addAnalysisMapValues() {
        StringBuilder xy = new StringBuilder();
        if ( xAnalysisWidget.isActive() ) {
            if ( xAnalysisWidget.getAnalysisSerializable().isActive("x") ) {
                xy.append(";xlo=" + xAxesWidget.getRefMap().getXlo());
                xy.append(";xhi=" + xAxesWidget.getRefMap().getXhi());
            }
            if ( xAnalysisWidget.getAnalysisSerializable().isActive("y") ) {
                xy.append(";ylo=" + xAxesWidget.getRefMap().getYlo());
                xy.append(";yhi=" + xAxesWidget.getRefMap().getYhi());
            }
        }
        return xy.toString();
    }
    private void setAnalysisAxes(String v) {

        // Eliminate the transformed axis from the acceptable intervals for the
        // variable.
        String intervals = xVariable.getGrid().getIntervals().replace(v, "");
        String view = xView;
        xOrtho = Util.setOrthoAxes(xView, xVariable, true);
        // Eliminate the transformed axis from the previous view.
        // This works because area is the only 2D analysis axis.
        if (v.equals("xy")) {
            // The old view might only contain one of "x" or "y" so eliminate
            // them one at a time.
            view = view.replace("x", "");
            view = view.replace("y", "");
        } else {
            view = view.replace(v, "");
        }
        // If the view goes blank, find the next best view.
        if (view.equals("")) {
            if (intervals.contains("xy")) {
                xView = "xy";
                xOrtho.remove("x");
                xOrtho.remove("y");
            } else if (intervals.contains("t") && xVariable.getGrid().hasT()) {
                xView = "t";
                xOrtho.remove("t");
            } else if (intervals.contains("z") && xVariable.getGrid().hasZ()) {
                xView = "z";
                xOrtho.remove("z");
            } else if (intervals.contains("x")) {
                xView = "x";
                xOrtho.remove("x");
            } else if (intervals.contains("y")) {
                xView = "y";
                xOrtho.remove("y");
            }
        } else {
            xView = view;
        }

        // Get set the new operations that apply to the remaining views.
        xOperationsWidget.setOperationsForAnalysis(xVariable.getGrid(), intervals, xOperationID, xView, ops);
        tOperationsMenu.setMenus(ops, xView);
        setOperationsClickHandler(xVizGalOperationsClickHandler);

        // Set the default operation.
        xOperationID = xOperationsWidget.setZero(xView);
        

        for (Iterator panIt = xPanels.iterator(); panIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panIt.next();
            panel.setAnalysis(xAnalysisWidget.getAnalysisSerializable());
            panel.setAnalysisAxes(xAnalysisWidget.getAnalysisAxis());
            panel.setOperation(xOperationID, xView);
            panel.showOrthoAxes(xView, getAnalysisAxis(), xPanelCount);
        }
        xAxesWidget.showViewAxes(xView, xOrtho, getAnalysisAxis());

        // Right now the only place where analysis controls
        // exist is in the left-hand nav.

        setTool(v);

        if (xView.contains("t") || v.contains("t")) {
            xAxesWidget.setRange("t", true);
        }

        if (xView.contains("z") || v.contains("z")) {
            xAxesWidget.setRange("z", true);
        }
    }

    /**
     * A helper methods that moves the current state of the map widget to the
     * panels.
     */
    private void setMapRanges(double[] cs) {
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();

            panel.setMapTool(xView);
            if (cs == null) {
                panel.setLatLon(String.valueOf(xAxesWidget.getRefMap().getYlo()), String.valueOf(xAxesWidget.getRefMap().getYhi()),
                        String.valueOf(xAxesWidget.getRefMap().getXlo()), String.valueOf(xAxesWidget.getRefMap().getXhi()));
            } else {
                // cs contains s, n, w, e
                panel.setLatLon(String.valueOf(cs[0]), String.valueOf(cs[1]), String.valueOf(cs[2]), String.valueOf(cs[3]));
            }

        }
    }

    // public ChangeListener fixedAxisMenuChange = new ChangeListener() {
    // public void onChange(Widget sender) {
    // String lo_value = null;
    // String hi_value = null;
    // boolean range = false;
    // if ( fixedAxis.equals("t") ) {
    // lo_value = xAxesWidget.getTAxis().getFerretDateLo();
    // hi_value = xAxesWidget.getTAxis().getFerretDateHi();
    // range = xAxesWidget.getTAxis().isRange();
    // } else if ( fixedAxis.equals("z") ) {
    // lo_value = xAxesWidget.getZAxis().getLo();
    // hi_value = xAxesWidget.getZAxis().getHi();
    // range = xAxesWidget.getZAxis().isRange();
    // }
    // setParentAxis(fixedAxis, lo_value, hi_value, range, false);
    // refresh(false, true);
    // }
    // };
    /**
     * Helper method to set the values of the axes in the panels that correspond
     * to the fixed axis in the gallery.
     * 
     * @param axis
     *            - the axis to set, either z or t.
     * @param lo
     *            - the lo value to set
     * @param hi
     *            - the hi value to set (will be equal to lo if not a range, it
     *            doesn't get used if not a range)
     * @param range
     *            - whether the widget is showing a range of values
     * @param set_local
     *            - whether to set the value of the gallery settings widget (in
     *            response to a history event)
     */
    private void setParentAxis(String axis, String lo, String hi, boolean range, boolean set_local) {
        if (set_local) {
            if (axis.equals("t")) {
                if (range) {
                    xAxesWidget.getTAxis().setLo(lo);
                    xAxesWidget.getTAxis().setHi(hi);
                } else {
                    xAxesWidget.getTAxis().setLo(lo);
                }
            } else if (axis.equals("z")) {
                if (range) {
                    xAxesWidget.getZAxis().setLo(lo);
                    xAxesWidget.getZAxis().setHi(hi);
                } else {
                    xAxesWidget.getZAxis().setLo(lo);
                }
            }
        }
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();

            panel.setAxisRangeValues(axis, lo, hi);

        }
    }

    private void setTool(String analysis_axes) {

        // The widget must display a range if the axis is in the view or in the
        // analysis

        String mapTool = "";
        if (xView.contains("x") || analysis_axes.contains("x")) {
            mapTool = "x";
        }
        if (xView.contains("y") || analysis_axes.contains("y")) {
            mapTool = mapTool + "y";
        }
        if (mapTool.equals("")) {
            mapTool = "pt";
        }

        xAxesWidget.getRefMap().setTool(mapTool);

    }

    private void setupForNewGrid(GridSerializable grid) {
        setUpdateRequired(true);
        xVariable.setGrid(grid);
        xAnalysisWidget.setAnalysisAxes(grid);
        if ( xVariable.isDiscrete() ) {
            xAnalysisWidget.setVisible(false);
        } else {
            xAnalysisWidget.setVisible(true);
        }
        // If the data set has defaults specified, use them...
        Map<String, Map<String, String>> properties = xVariable.getProperties();
        if ( properties != null ) {
        	Map<String, String> product_server = properties.get("product_server");
        	if ( product_server != null ) {
        		String opid = product_server.get("default_operation");
        		String dv = product_server.get("default_view");
        		if ( opid != null && !opid.equals("") && dv != null && !dv.equals("") ) {
        			xOperationID = opid;
        			xView = dv;
        		}
        	}
        }
        xOperationsWidget.setOperations(xVariable.getGrid(), xOperationID, xView, ops);
        xOperationID = xOperationsWidget.getCurrentOperation().getID();
        xOptionID = xOperationsWidget.getCurrentOperation().getOptionsID();
        xOptionsButton.setOptions(xOptionID, xOptionsButton.getState());
        xView = xOperationsWidget.getCurrentView();
        tOperationsMenu.setMenus(ops, xView);
        if (xPanels == null || xPanels.size() == 0) {
            UI.super.setupOutputPanels(1, Constants.IMAGE);
        }
        setupWidgets();
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            // In the case of where multiple variables are being used in a
            // panel, we need to change the 1st UserList?
            panel.setVariable(xVariable, true);
            panel.setOperation(xOperationID, xView);
            panel.setupForCurrentVar(false, ops);
            panel.showOrthoAxes(xView, getAnalysisAxis(), xPanelCount);
            panel.setOrthoRanges(xView);
           
        }
        turnOffAnalysis();
        changeDataset = false;

        // Now that we have the grid, finish applying the changes.
        finishApply();
    }

    /**
     * Only sets up the main (upper-left) OutputPanel and does NOT refresh.
     */
    private void setupMainPanel() {
        if ( xTrajectoryConstraint.isActive() ) {
        	differenceButton.setEnabled(false);
            xAnalysisWidget.setVisible(false);
        } else {
        	differenceButton.setEnabled(true);
            xAxesWidget.setVisible(true);
        }
        autoContourButton.setDown(false);
        autoContourTextBox.setText("");
        int buttonIndex = getButtonIndex();
        Widget compareButtons = xOtherControls.getWidget(0, buttonIndex);
        xOtherControls.remove(compareButtons);
        xOtherControls.setWidget(0, buttonIndex, tOperationsMenu);
        setTopLeftAlignment(xButtonLayout);
    }

    /**
     * @param resetOnlyNewPanels
     */
    private void setupMainPanelAndRefreshForceLASRequest(boolean resetOnlyNewPanels) {
        if ( xTrajectoryConstraint.isActive() ) {
            xAnalysisWidget.setVisible(false);
        } else {
            xAnalysisWidget.setVisible(true);
        }
        autoContourButton.setDown(false);
        autoContourTextBox.setText("");
        setupPanelsAndRefresh(resetOnlyNewPanels, true);
        int buttonIndex = getButtonIndex();
        Widget compareButtons = xOtherControls.getWidget(0, buttonIndex);
        xOtherControls.remove(compareButtons);
        xOtherControls.setWidget(0, buttonIndex, tOperationsMenu);
        setTopLeftAlignment(xButtonLayout);
    }

    private void setupMenusForOperationChange() {
        xView = xOperationsWidget.getCurrentView();
        OperationSerializable oo = xOperationsWidget.getCurrentOperation();
        int min = 1;
        int max = 1;
        if (oo.getAttributes().containsKey("minvars")) {
            min = Integer.valueOf(oo.getAttributes().get("minvars"));
        }
        if (oo.getAttributes().containsKey("maxvars")) {
            max = Integer.valueOf(oo.getAttributes().get("maxvars"));
        }
        eventBus.fireEvent(new OperationChangeEvent(min, max));
        xOperationID = oo.getID();
        Map<String, String> operationChangeOptions = xOptionsButton.getState();
        xOptionsButton.setOptions(xOperationsWidget.getCurrentOperation().getOptionsID(), operationChangeOptions);
        xOrtho = Util.setOrthoAxes(xView, xVariable, true);

        if (xVariable.isVector() || xVariable.isDiscrete()) {
            if ( xView.equals("xy") ) {
                differenceButton.setDown(false);
                differenceButton.setEnabled(true);
            } else {
            	differenceButton.setDown(false);
                differenceButton.setEnabled(false);
            }
        } else {
            differenceButton.setDown(false);
            differenceButton.setEnabled(true);
        }
        // xComparisonAxesSelector.setAxes(xOrtho);

        if (xView.length() != 2) {
            autoContourTextBox.setText("");
            autoContourButton.setDown(false);
            autoContourButton.setEnabled(false);
        } else {
            if (xVariable.isVector() || xVariable.isDiscrete()) {
                autoContourTextBox.setText("");
                autoContourButton.setDown(false);
                autoContourButton.setEnabled(false);
            } else {
                autoContourButton.setEnabled(true);
            }
        }
        xAxesWidget.showViewAxes(xView, xOrtho, getAnalysisAxis());
        for (Iterator panelsIt = xPanels.iterator(); panelsIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelsIt.next();

            panel.setOperation(xOperationID, xView);
            panel.setOrtho();
            panel.showOrthoAxes(xView, getAnalysisAxis(), xPanelCount);        
            panel.setOrthoRanges(xView);
        }
        if (xAnalysisWidget.isActive()) {
            String v = xAnalysisWidget.getAnalysisAxis();
            setAnalysisAxes(v);
            setTool(v);
        } else {
            if ( xVariable.isDiscrete() ) {
                xAxesWidget.getRefMap().setTool("xy");
            } else {
                xAxesWidget.getRefMap().setTool(xView);
            }
        }
        tOperationsMenu.enableByView(xView, xVariable.getGrid().hasT());
    }

    // private void applyTokensToPanelModePanels(String[] settings) {
    // /**
    // * There are 5 sets of tokens.
    // * <OL>
    // * <LI>0. Gallery
    // * <LI>1. Panel 0 (upper left) never in "panel" mode.
    // * <LI>2. Panel 1 (upper right)
    // * <LI>3. Panel 2 (lower left)
    // * <LI>4. Panel 3 (lower right)
    // * </OL>
    // */
    //
    // }

    // TODO: Augment to apply multiple variables/list boxes from the same data
    // set
    /**
     * Clears xOrtho, sets up each OutputPanel by: Setting the panel's selected
     * variable to xVariable and then passing in the current ops to the panel's
     * setupForCurrentVar method, sets panel and xAxesWidget axis ranges and
     * axes, calls panel.setMapTool(xView) and panel.setLatLon(...), and gives
     * each panel the same new instance of BaseUI.Mouse. If there is no initial
     * history and comparisonModeChange == false it calls refresh(false, true,
     * forceLASRequest).
     * 
     * @param resetOnlyNewPanels
     * @param forceLASRequest
     *            If true will force an {@link LASRequestEvent} request.
     */
    private void setupPanelsAndRefresh(boolean resetOnlyNewPanels, boolean forceLASRequest) {
        xOrtho.clear();

        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            if (resetOnlyNewPanels) {
                if (xNewPanels.contains(panel)) {
                    // In the case of where multiple variables are being used in
                    // a
                    // panel, we need to change the 1st UserList
                    panel.setVariable(xVariable, true);
                    panel.setupForCurrentVar(false, ops);
                }
            } else {
                // Reset every panel

                // In the case of where multiple variables are being used in a
                // panel, we need to change the 1st UserList
                panel.setVariable(xVariable, true);
                panel.setupForCurrentVar(false, ops);
            }
        }

        setupWidgets();

        if (xTlo != null && !xTlo.equals("")) {
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                if (xThi != null && !xThi.equals("")) {
                    panel.setAxisRangeValues("t", xTlo, xThi);
                } else {
                    panel.setAxisRangeValues("t", xTlo, xTlo);
                }
            }
            xAxesWidget.getTAxis().setLo(xTlo);
            if (xThi != null && !xThi.equals("")) {
                xAxesWidget.getTAxis().setHi(xThi);
            } else {
                xAxesWidget.getTAxis().setHi(xTlo);
            }
        }
        if (xZlo != null && !xZlo.equals("")) {
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                if (xZhi != null && !xZhi.equals("")) {
                    panel.setAxisRangeValues("z", xZlo, xZhi);
                } else {
                    panel.setAxisRangeValues("z", xZlo, xZlo);
                }
            }
            xAxesWidget.getZAxis().setLo(xZlo);
            if (xZhi != null && !xZhi.equals("")) {
                xAxesWidget.getZAxis().setHi(xZhi);
            } else {
                xAxesWidget.getZAxis().setHi(xZlo);
            }
        }

        // If these limits are not the same as the dataBounds, then set them.
        if (xXlo != null && !xXlo.equals("") && xXhi != null && !xXhi.equals("") && xYlo != null && !xYlo.equals("") && xYhi != null && !xYhi.equals("")) {
            xAxesWidget.getRefMap().setCurrentSelection(Double.valueOf(xYlo), Double.valueOf(xYhi), Double.valueOf(xXlo), Double.valueOf(xXhi));
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                if ( panel.getVariable().isDiscrete() ) {
                    panel.setMapTool("xy");
                } else {
                    panel.setMapTool(xView);
                }
                panel.setLatLon(xYlo, xYhi, xXlo, xXhi);
            }
        } else {
            double tmp_xlo = xAxesWidget.getRefMap().getXlo();
            double tmp_xhi = xAxesWidget.getRefMap().getXhi();
            double tmp_ylo = xAxesWidget.getRefMap().getYlo();
            double tmp_yhi = xAxesWidget.getRefMap().getYhi();
            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                if ( panel.getVariable().isDiscrete() ) {
                    panel.setMapTool("xy");
                } else {
                    panel.setMapTool(xView);
                }
                panel.setLatLon(String.valueOf(tmp_ylo), String.valueOf(tmp_yhi), String.valueOf(tmp_xlo), String.valueOf(tmp_xhi));
            }
        }
        List<Mouse> mice = new ArrayList<Mouse>();
        Mouse m = new Mouse();
        mice.add(m);
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel p = (OutputPanel) panelIt.next();
            p.setMouseMoves(mice);
        }
        // If there is no initial history, we're ready to go...
        if ((initialHistory == null || initialHistory.equals("")) && !comparisonModeChange) {
            refresh(false, true, forceLASRequest);
        }
    }

    private void setupPanelsAndRefreshNOforceLASRequest(boolean resetOnlyNewPanels) {
        setupPanelsAndRefresh(resetOnlyNewPanels, false);
    }

    /**
     * @param resetOnlyNewPanels
     */
    private void setupPanelsAndRefreshNOforceLASRequestWithCompareMenus(boolean resetOnlyNewPanels) {
        
        setupPanelsAndRefreshNOforceLASRequest(resetOnlyNewPanels);
        int buttonIndex = getButtonIndex();
        Widget tOpsMenu = xOtherControls.getWidget(0, buttonIndex);
        xOtherControls.remove(tOpsMenu);
        xOtherControls.setWidget(0, buttonIndex, compareButtonsLayout);
        // xOtherControls.getCellFormatter().setWidth(0, buttonIndex, "271");
        setTopLeftAlignment(xButtonLayout);
    }

    private void turnOffAnalysis() {
    	// Only turn it off if it's already on.
    	xAnalysisWidget.setActive(false);
    	for (Iterator panIt = xPanels.iterator(); panIt.hasNext();) {
    		OutputPanel panel = (OutputPanel) panIt.next();
    		panel.setAnalysis(null);
    	}
    	xOperationID = ops[0].getID();
    	xOperationsWidget.setOperations(xVariable.getGrid(), ops[0].getID(), xView, ops);
    	tOperationsMenu.setMenus(ops, xView);
    	tOperationsMenu.enableByView(xView, xVariable.getGrid().hasT());
    	tOperationsMenu.setCorrelationButtonEnabled(true);
    	setOperationsClickHandler(xVizGalOperationsClickHandler);
    	xOrtho = Util.setOrthoAxes(xView, xVariable, true);
    	xAxesWidget.showViewAxes(xView, xOrtho, null);
    	xOperationID = xOperationsWidget.setZero(xView);
    	if ( xVariable.isDiscrete() ) {
    		xAxesWidget.getRefMap().setTool("xy");
    	} else {
    		xAxesWidget.getRefMap().setTool(xView);
    	}
    	GridSerializable grid = xVariable.getGrid();
    	for (Iterator panIt = xPanels.iterator(); panIt.hasNext();) {
    		OutputPanel panel = (OutputPanel) panIt.next();
    		panel.setOperation(xOperationID, xView);
    		if (grid.hasZ()) {
    			panel.setRange("z", false);
    		}
    		if (grid.hasT()) {
    			panel.setRange("t", false);
    		}
    		panel.showOrthoAxes(xView, null, xPanelCount);
    		panel.setOrthoRanges(xView);
    	}
    	xAxesWidget.setMessage("");
    	xAxesWidget.showMessage(false);
    	xAxesWidget.getRefMap().resizeMap();

    }

    private boolean waitForRPC() {
        boolean wait = false;
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            if (panel.isWaitingForRPC()) {
                wait = true;
            }
        }
        if (xOptionsButton.isWaitingForRPC()) {
            wait = true;
        }
        return wait;
    }

    void compareMenuChanged() {
        // Before re-initializing the panels, grab the global min and
        // max.
        globalMin = xPanels.get(0).getMin();
        globalMax = xPanels.get(0).getMax();
        try {
            String p = compareMenu.getValue(compareMenu.getSelectedIndex());
            Integer numPanels = Integer.valueOf(p);
            int oldNumPanels = xPanelCount;
            if (oldNumPanels > numPanels) {
                // LAS UI will only remove OutputPanels, so after calling
                // setupOutputPanels(numPanels, Constants.IMAGE) LAS should only
                // call applyChange()
                setupOutputPanels(numPanels, Constants.IMAGE);
                // Reset only the new OutputPanels
                if (p.equals("1")) {
                    xNewPanels.clear(); // The remaining main panel is not "new"
                    // Will not call refresh, just setup ONLY the main panel
                    setupMainPanel();
                }
                applyChange();
            } else {
                int numNewPanels = numPanels - oldNumPanels;
                // Adding numNewPanels of new panels

                // The previous way of adding OutputPanels worked when we
                // always wanted the same variable (and dataset) for all
                // OutputPanels as the main/comparePanel,
                // but now we just want the new OutputPanels to behave that way
                // and leave others alone
                setupOutputPanels(numPanels, Constants.IMAGE);
                // resize OutputPanel(s) according to the current Window size
                // resize(Window.getClientWidth(), Window.getClientHeight());
                String galleryHistory = getGalleryToken();
                Map<String, String> galleryTokens = Util.getTokenMap(galleryHistory);
                String comparePanelHistory = getComparePanel().getHistoryToken() + getHistoryToken();
                Map<String, String> tmComparePanel = Util.getTokenMap(comparePanelHistory);
                Map<String, String> omComparePanel = Util.getOptionsMap(comparePanelHistory);
                // Delay call to refresh until the state has been updated to
                // reflect the more recent history event.
                comparisonModeChange = true;
                // Reset only the new OutputPanels
                boolean resetOnlyNewPanels = true;
                if (p.equals("1")) {
                    // Will not call refresh because comparisonModeChange ==
                    // true
                    xNewPanels.clear(); // The remaining main panel is not "new"
                    setupMainPanelAndRefreshForceLASRequest(resetOnlyNewPanels);
                    galleryTokens.put("autoContour", "false");
                } else {
                    xNewPanels.clear();// clears the list and handles the case
                    // of numNewPanels == 0
                    if (numNewPanels == 1) {
                        xNewPanels.add(xPanels.get(1));
                    } else if (numNewPanels == 2) {
                        xNewPanels.add(xPanels.get(2));
                        xNewPanels.add(xPanels.get(3));
                    } else if (numNewPanels == 3) {
                        xNewPanels.add(xPanels.get(1));
                        xNewPanels.add(xPanels.get(2));
                        xNewPanels.add(xPanels.get(3));
                    }
                    setupPanelsAndRefreshNOforceLASRequestWithCompareMenus(resetOnlyNewPanels);
                }

                setFromHistoryToken(tmComparePanel, omComparePanel);
                xPanels.get(0).setFromHistoryToken(tmComparePanel, omComparePanel);
               
                
                // We are leaving the analysis active so we need to reapply it.
                if ( xAnalysisWidget.isActive() ) {
                    String v = xAnalysisWidget.getAnalysisAxis();
                    setAnalysisAxes(v);
                    applyHistory(galleryTokens);
                }
                
                // Set history tokens only to new OutputPanels
                for (int i = 1; i < xPanels.size(); i++) {
                    OutputPanel panel = xPanels.get(i);
                    if (resetOnlyNewPanels) {
                        if (xNewPanels.contains(panel)) {
                            panel.setFromHistoryToken(tmComparePanel, omComparePanel);
                        }
                    } else {
                        panel.setFromHistoryToken(tmComparePanel, omComparePanel);
                    }
                }
                comparisonModeChange = false;
                setUpdateRequired(true);
                xNewPanels.clear();
            }
            // Hide or show controls in panels (because we might have made new
            // panels)
            eventBus.fireEventFromSource(new ControlVisibilityEvent(!xPanelHeaderHidden), this);
            if (p.equals("1")) {
                // Fire a ComparisonModeChangeEvent so listeners know the
                // app has left comparison mode
                eventBus.fireEvent(new ComparisonModeChangeEvent(false));
            } else {
                // Fire a ComparisonModeChangeEvent so listeners know the
                // app has gone into comparison mode
                eventBus.fireEvent(new ComparisonModeChangeEvent(true));
            }
            // Request that the plot refresh happen automatically, don't force
            // panels to update if they don't need to, and do add to the history
            // stack.
            eventBus.fireEvent(new WidgetSelectionChangeEvent(true, false, true));
            setAnnotationsMode(annotationsControl.isDown());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    /**
     * @return The main panel used for comparisons.
     */
    OutputPanel getComparePanel() {
        OutputPanel comparePanel = null;
        for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
            OutputPanel panel = (OutputPanel) panelIt.next();
            if (panel.isComparePanel()) {
                comparePanel = panel;
            }
        }
        return comparePanel;
    }

    /**
     * @return a token String for adding to the browser {@link History}
     */
    String getHistoryTokenString() {
        // First token collection is the gallery settings (mostly in the header
        // of the UI)
        StringBuilder historyToken = new StringBuilder();

        // Build the tokens for the panels.

        // The 0 panel is controlled by the SettingsControl in the gallery.
        historyToken.append(getGalleryToken());

        // The next N tokens are the states of the individual panels.
        historyToken.append("token" + xPanels.get(0).getHistoryToken() + getHistoryToken());

        for (int i = 1; i < xPanels.size(); i++) {
            OutputPanel panel = xPanels.get(i);
            historyToken.append("token" + panel.getHistoryToken());
        }

        String historyTokenString = historyToken.toString();
        return historyTokenString;
    }

    /**
     * @return true if updates are currently required, false if they are not
     */
    boolean isUpdateRequired() {
        return required_update;
    }

    /**
     * Sets whether updates are required or not. Attempts to set that state to
     * false will be delayed to allow the UI finish any {@link History} related
     * actions it might still be doing. Attempts to set the state to true while
     * a false state setting timer is running will cancel the timer. Attempts to
     * set the state to false while a false state setting timer is running will
     * be ignored.
     * 
     * @param shouldUpdateBeRequired
     *            the new boolean state to set
     */
    void setUpdateRequired(boolean shouldUpdateBeRequired) {
        if (shouldUpdateBeRequired) {
            if (noLongerRequireUpdateTimer != null) {
                // Then a noLongerRequireUpdateTimer is running and needs to be
                // cancelled (so updates are not halted) and nulled
                noLongerRequireUpdateTimer.cancel();
                noLongerRequireUpdateTimer = null;
                // Now after this method sets required_update = true,
                // noLongerRequireUpdateTimer will not be set to false before
                // the History related actions are complete
            }
            required_update = true;
        } else {
            if (noLongerRequireUpdateTimer == null) {
                // There is no other noLongerRequireUpdateTimer, so it's safe to
                // make one
                noLongerRequireUpdateTimer = new Timer() {

                    @Override
                    public void run() {
                        if (noLongerRequireUpdateTimer != null) {
                            // Then the field was not cancelled and nulled by a
                            // new setUpdateRequired(true) call that is likely
                            // History related
                            required_update = false;
                            noLongerRequireUpdateTimer = null;
                            loadingModule = false;
                        } else {
                        }
                    }
                };
                // Schedule the timer to run once later.
                noLongerRequireUpdateTimer.schedule(1000);
            } else {
            }
        }
    	
    }
    public AsyncCallback<String[]> addESGFDatasetsCallback = new AsyncCallback<String[]>() {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not add data sets to start this LAS session.");

        }

        @Override
        public void onSuccess(String[] result) {
            eventBus.fireEvent(new ESGFDatasetAddedEvent());
            Util.getRPCService().getCategories(xCATID, xDSID, initFromDatasetAndVariable);
        }

    };
    public AsyncCallback initFromDatasetAndVariable = new AsyncCallback() {

        @Override
        public void onFailure(Throwable caught) {
            // Set some default values...
            xView = "xy";
            xOperationID = "Plot_2D_XY_zoom";
            xOptionID = "Options_2D_image_contour_xy_7";
            Window.alert("Please choose a different data set.");
        } 

        @Override
        public void onSuccess(Object result) {
            CategorySerializable[] cats = (CategorySerializable[]) result;
            if (cats != null) {
                if (cats.length > 1) {
                    Window.alert("Please choose a data set.");
                } else if (cats.length == 1) {
                    CategorySerializable firstCategorySerializable = cats[0];
                    if (firstCategorySerializable != null && !firstCategorySerializable.isVariableChildren()) {
                        // Set some default values...
                        xView = "xy";
                        xOperationID = "Plot_2D_XY_zoom";
                        xOptionID = "Options_2D_image_contour_xy_7";
                        Window.alert("Please choose a data set.");
                    } else if ((firstCategorySerializable != null) && (firstCategorySerializable.isVariableChildren())) {
                        DatasetSerializable ds = firstCategorySerializable.getDatasetSerializable();
                        VariableSerializable[] vars = ds.getVariablesSerializable();
                        variables = new Vector<VariableSerializable>();
                        if (xVarID == null) {
                            xVarID = vars[0].getID();
                        }
                        for (int i = 0; i < vars.length; i++) {
                            variables.add(vars[i]);
                            if (vars[i].getID().equals(xVarID)) {

                                xVariable = vars[i];
                                // View is null to get all operations
                                Util.getRPCService().getConfig(null, xVariable.getCATID(), xVariable.getDSID(), xVariable.getID(), getGridCallback);
                            }
                        }
                    }
                } else {
                	// No such data set was found.
                	Window.alert("Please choose a data set.");
                }
            }
        }
    };
    public AsyncCallback<Map<String, String>> initFromURL = new AsyncCallback<Map<String, String>>() {

        @Override
        public void onFailure(Throwable error) {
            // Set some default values...
            xView = "xy";
            xOperationID = "Plot_2D_XY_zoom";
            xOptionID = "Options_2D_image_contour_xy_7";
            Window.alert("Please choose a data set.");
        }

        @Override
        public void onSuccess(Map<String, String> ids) {
            xCATID = ids.get("catid");
            xDSID = ids.get("dsid");
            xVarID = ids.get("varid");
            Util.getRPCService().getCategories(xCATID, xDSID, initFromDatasetAndVariable);
        }

    };

    public AsyncCallback initPanelFromDefaultsCallback = new AsyncCallback() {

        @Override
        public void onFailure(Throwable caught) {
            // Ok with me. User will just have to select a data set.
        }

        @Override
        public void onSuccess(Object result) {
            HashMap<String, String> product_server = (HashMap<String, String>) result;
            for (Iterator nameIt = product_server.keySet().iterator(); nameIt.hasNext();) {
                String name = (String) nameIt.next();
                String value = product_server.get(name);
                if (name.equals(Constants.DEFAULT_CATID)) {
                    xCATID = value;
                } else if (name.equals(Constants.DEFAULT_DSID)) {
                    xDSID = value;
                } else if (name.equals(Constants.DEFAULT_VARID)) {
                    xVarID = value;
                } else if (name.equals(Constants.DEFAULT_OP)) {
                    xOperationID = value;
                } else if (name.equals(Constants.DEFAULT_OPTION)) {
                    xOptionID = value;
                } else if (name.equals(Constants.DEFAULT_VIEW)) {
                    xView = value;
                } else if (name.equals(Constants.DEFAULT_TIME)) {
                    tInitialTime = value;
                } else if (name.equals(Constants.DEFAULT_Z)) {
                    tInitialZ = value;
                }
            }

            if (xDSID != null || xCATID != null) {

                // Supply some reasonable defaults and go...
                if (xOperationID == null) {
                    xOperationID = "Plot_2D_XY_zoom";
                }
                if (xView == null) {
                    xView = "xy";
                }
                setUpdateRequired(true);
                Util.getRPCService().getCategories(xCATID, xDSID, initFromDatasetAndVariable);
            } else {
                // Set some default values...
                xView = "xy";
                xOperationID = "Plot_2D_XY_zoom";
                xOptionID = "Options_2D_image_contour_xy_7";
                Util.getRPCService().getIDMap(null, initFromURL);
            }
        }
    };

    /*
     * Keep track of which axis is in the plot panels.
     */
    // String compareAxis;

    /*
     * Keep track of which axis is selected in the header.
     */
    // String fixedAxis;

    public ChangeListener panelAxisMenuChange = new ChangeListener() {
        @Override
        public void onChange(Widget sender) {
            refresh(false, true, false);
        }
    };
    

    public ClickHandler tExternalOperationClickHandler = new ClickHandler() {

        String spinImageURL = URLUtil.getImageURL() + "/mozilla_blu.gif";

        @Override
        public void onClick(ClickEvent event) {
            OperationPushButton b = (OperationPushButton) event.getSource();
            OperationSerializable operation = b.getOperation();
            final String target = operation.getAttributes().get("target");
            final String opid = operation.getID();
            final OptionsWidget op = new OptionsWidget();
            final DialogBox optionsDialog = new DialogBox(false);
            optionsDialog.setText("Set options for " + operation.getName());
            op.addOkClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    Map<String, String> options = op.getState();
                    xPanels.get(0).setVizGalState(xVariable, getHistoryToken(), xPanels.get(0).getHistoryToken());
                    LASRequest lasRequest = xPanels.get(0).getRequest();
                    if (options != null) {
                        for (Iterator<String> opIt = options.keySet().iterator(); opIt.hasNext();) {
                            String key = opIt.next();
                            String value = options.get(key);
                            if (!value.toLowerCase(Locale.ENGLISH).equals("default") && !value.equals("")) {
                                lasRequest.setProperty("ferret", key, value);
                            }
                        }
                    }
                    lasRequest.setProperty("las", "output_type", "html");
                    lasRequest.setOperation(opid, "v7");
                   
                    // We used to use the xOperationID.getName() instead of
                    // "_blank" as
                    // the Window name but IE respects only a narrow list of
                    // valid
                    // Window name parameters. Weusijana, 07/06/2012.
                    if (xAnalysisWidget.isActive()) {
                        AnalysisSerializable analysis = xAnalysisWidget.getAnalysisSerializable();
                        if (analysis.isActive("t")) {
                            analysis.getAxes().get("t").setLo(xAxesWidget.getTAxis().getFerretDateLo());
                            analysis.getAxes().get("t").setHi(xAxesWidget.getTAxis().getFerretDateHi());
                        }
                        if (analysis.isActive("z")) {
                            analysis.getAxes().get("z").setLo(xAxesWidget.getZAxis().getLo());
                            analysis.getAxes().get("z").setHi(xAxesWidget.getZAxis().getHi());
                        }
                        if (analysis.isActive("y")) {
                            analysis.getAxes().get("y").setLo(String.valueOf(xAxesWidget.getRefMap().getYlo()));
                            analysis.getAxes().get("y").setHi(String.valueOf(xAxesWidget.getRefMap().getYhi()));
                        }
                        if (analysis.isActive("x")) {
                            analysis.getAxes().get("x").setLo(String.valueOf(xAxesWidget.getRefMap().getXlo()));
                            analysis.getAxes().get("x").setHi(String.valueOf(xAxesWidget.getRefMap().getXhi()));
                        }
                        if ( analysis.isActive("e") ) {
                            analysis.getAxes().get("e").setLo(xAxesWidget.getEAxis().getLo());
                            analysis.getAxes().get("e").setHi(xAxesWidget.getEAxis().getHi());
                        }
                        analysis.setLabel(xVariable.getName());
                        lasRequest.setAnalysis(analysis, 0);
                    }
                    // Allow the operation stop specify a target of "_self" so that the new product
                    // replaces the current window. This forces a back operation which will refresh a potentially stale plot.
                    // No other target is allowed.
                    String t = target;
                    if ( t == null ) {
                        t = "_blank";
                    } else if ( t != null && !t.equals("_self") ) {
                        t = "_blank";
                    }
                    Window.open(Util.getProductServer() + "?catid=" + xVariable.getCATID() + "&xml=" + URL.encode(lasRequest.toString()), t, Constants.WINDOW_FEATURES);
                    optionsDialog.hide();
                }
            });
            op.addCancelHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    optionsDialog.hide();
                }
            });
            op.setOptions(operation.getOptionsID(), false);
            Widget source = (Widget) event.getSource();
            optionsDialog.add(op);
            optionsDialog.setPopupPosition(source.getAbsoluteLeft(), source.getAbsoluteTop());
            Image spinImage = new Image(spinImageURL);
            spinImage.setAltText("Spinner");
            if (op.isEmptyOps()) {
                optionsDialog.setText("Loading...");
                op.hideButtons();
                op.add(spinImage);
            }
            optionsDialog.show();
            if (op.isEmptyOps()) {
                op.ok();
            } else {
                optionsDialog.setText("Set options for " + operation.getName());
                op.showButtons();
                op.remove(spinImage);
            }
        }
    };
    public ClickHandler xVizGalOperationsClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            Widget sender = (Widget) event.getSource();
            if (sender instanceof OperationRadioButton) {
                setupMenusForOperationChange();
                applyChange();
            }
        }
    };

    private FeatureModifiedEvent.Handler featureModifiedHandler = new FeatureModifiedEvent.Handler() {

        @Override
        public void onFeatureModified(FeatureModifiedEvent event) {
            OLMapWidget m = (OLMapWidget) event.getSource();
            if (xAxesWidget.getRefMap().equals(m)) {
                // Event from this map, re-fire it as a map change.
                eventBus.fireEventFromSource(new MapChangeEvent(event.getYlo(), event.getYhi(), event.getXlo(), event.getXhi()), xPanels.get(0));
            }
        }

    };

    private LASRequestEvent.Handler outputPanelRequestsHandler = new LASRequestEvent.Handler() {
        private OutputPanelRequestController oprController = new OutputPanelRequestController();

        @Override
        public void onRequest(LASRequestEvent event) {
            Object source = event.getSource();
            if (source == null) {
            	// Uh oh.
            } else {
                String sourceString = source.toString();
                if ((source instanceof OutputPanel) || (sourceString.contains(".OutputPanel$"))) {
                    oprController.process(event);
                } else {
                	// uh oh.
                }
            }
        }
    };

    ChangeHandler analysisActiveChange = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent event) {
            Object source = event.getSource();
            if ((source != null) && (source instanceof AnalysisWidget)) {
                AnalysisWidget analysis = (AnalysisWidget) source;
                applyButton.addStyleDependentName("APPLY-NEEDED");
                // String v = xAnalysisWidget.getAnalysisAxis();
                if (analysis.isActive()) {
                    tOperationsMenu.setCorrelationButtonEnabled(false);
                    String v = analysis.getAnalysisAxis();
                    setAnalysisAxes(v);
                    for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                    	OutputPanel p = (OutputPanel) panelIt.next();
                    	p.setAnalysis(analysis.getAnalysisSerializable());
                    }
                } else {
                    turnOffAnalysis();
                }
            }
        }
    };

    ChangeHandler analysisAxesChange = new ChangeHandler() {

        @Override
        public void onChange(ChangeEvent event) {
            if (xAnalysisWidget.isActive()) {
                eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), event.getSource());
                ListBox analysisAxis = (ListBox) event.getSource();
                String v = analysisAxis.getValue(analysisAxis.getSelectedIndex());
                setAnalysisAxes(v);
            }
        }
    };

    ChangeHandler analysisOpChange = new ChangeHandler() {

        @Override
        public void onChange(ChangeEvent event) {
            eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), event.getSource());
        }
    };
    CloseHandler<DisclosurePanel> annotationsClose = new CloseHandler<DisclosurePanel>() {

        @Override
        public void onClose(CloseEvent<DisclosurePanel> event) {

            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setAnnotationsOpen(false);
            }

        }

    };
    OpenHandler<DisclosurePanel> annotationsOpen = new OpenHandler<DisclosurePanel>() {

        @Override
        public void onOpen(OpenEvent<DisclosurePanel> event) {

            for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                OutputPanel panel = (OutputPanel) panelIt.next();
                panel.setAnnotationsOpen(true);
            }

        }

    };
    ClickHandler autoContour = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            if (autoContourButton.isDown()) {
                autoScale();
            } else {
                autoContourTextBox.setText("");
                eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            }
        }
    };
    ClickListener differencesClick = new ClickListener() {
        @Override
        public void onClick(Widget sender) {
            refresh(false, true, false);
        }
    };

    AsyncCallback<ConfigSerializable> getGridCallback = new AsyncCallback<ConfigSerializable>() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not fetch grid.  " + caught.getLocalizedMessage());

        }

        @Override
        public void onSuccess(ConfigSerializable config) {

            GridSerializable grid = config.getGrid();
            RegionSerializable[] regions = config.getRegions();
            List<ERDDAPConstraintGroup> constraintGroups = config.getConstraintGroups();
            if ( constraintGroups != null && constraintGroups.size() > 0 ) {
                xTrajectoryConstraint.init(constraintGroups);
                xTrajectoryConstraint.setVisible(true);
            }
            xAxesWidget.getRefMap().setRegions(regions);
            ops = config.getOperations();
            xVariable.setGrid(grid);
            xAnalysisWidget.setAnalysisAxes(grid);

            // if (xPanels == null || xPanels.size() == 0) {
            // UI.super.setupOutputPanels(1, Constants.IMAGE);
            // }


            setupForNewGrid(grid);
            setupPanelsAndRefreshNOforceLASRequest(false);
            if (initialHistory != null && !initialHistory.equals("")) {

                if (initialHistory != null ) {
                    String[] settings = initialHistory.split("token");
                    HashMap<String, String> tokenMap = Util.getTokenMap(settings[0]);
                    setConstraintsFromHistory(tokenMap); 
                }
                popHistory(false, initialHistory);
                
                /*
                 * If this is from an initialHistory URL with constraints, first set up the panel, then set them from the token map.
                 */
               

            }
            
            setConstraintsFromProperties();
            
        }
        
       

    };

    AsyncCallback<ConfigSerializable> getGridForChangeDatasetCallback = new AsyncCallback<ConfigSerializable>() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Could not get grid for new variable." + caught.toString());

        }

        @Override
        public void onSuccess(ConfigSerializable config) {

            GridSerializable grid = config.getGrid();
            xAxesWidget.getRefMap().setRegions(config.getRegions());
            ops = config.getOperations();
            setupForNewGrid(grid);
            setConstraintsFromProperties();
            
        }
    };
    


    AsyncCallback<ConfigSerializable> getGridForChangeVariableCallback = new AsyncCallback<ConfigSerializable>() {

        @Override
        public void onFailure(Throwable e) {
            Window.alert("Could not get grid for new variable." + e.toString());
        }

        @Override
        public void onSuccess(ConfigSerializable config) {
            GridSerializable grid = config.getGrid();
            xAxesWidget.getRefMap().setRegions(config.getRegions());
            if (grid.getID().equals(xVariable.getGrid().getID()) && xNewVariable.getAttributes().get("grid_type").equals(xVariable.getAttributes().get("grid_type"))) {
                xVariable = xNewVariable;
                xVariable.setGrid(grid);
                // In the case of where multiple variables are being used in a
                // panel, we need to change the 1st UserList?
                xPanels.get(0).setVariable(xVariable, true);
                eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            } else if (grid.getID().equals(xVariable.getGrid().getID()) && !xNewVariable.getAttributes().get("grid_type").equals(xVariable.getAttributes().get("grid_type"))) {
                // Requires new operations, will likely replace the current
                // operation with a new one
                xVariable = xNewVariable;
                xVariable.setGrid(grid);
                // In the case of where multiple variables are being used in a
                // panel, we need to change the 1st UserList?
                xPanels.get(0).setVariable(xVariable, true);
                ops = config.getOperations();
                xOperationsWidget.setOperations(xVariable.getGrid(), xOperationID, xView, ops);
                xOperationID = xOperationsWidget.getCurrentOperation().getID();
                xOptionID = xOperationsWidget.getCurrentOperation().getOptionsID();
                xOptionsButton.setOptions(xOptionID, xOptionsButton.getState());
                tOperationsMenu.setMenus(ops, xView);
                for (Iterator panelIt = xPanels.iterator(); panelIt.hasNext();) {
                    OutputPanel p = (OutputPanel) panelIt.next();
                    p.setOperation(xOperationID, xView);
                }
                eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            } else {
                // Not enough is the same. Pretend like it's a brand new data
                // set.
                xVariable = xNewVariable;
                ops = config.getOperations();
                setupForNewGrid(grid);

            }
        }

    };
    ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {
        /**
         * Reloads the browser window at the new URL.
         * 
         * @param historyValueChangeEvent
         */
        @Override
        public void onValueChange(ValueChangeEvent<String> historyValueChangeEvent) {
            // The only safe way to react to a History change under the current
            // architecture is to reload the page
            Window.Location.reload();
        }

    };
    AsyncCallback<ConfigSerializable> initVizGal = new AsyncCallback<ConfigSerializable>() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Failed to initalizes VizGal." + caught.toString());
        }

        @Override
        public void onSuccess(ConfigSerializable config) {

            GridSerializable grid = config.getGrid();
            xAnalysisWidget.setAnalysisAxes(grid);
            ops = config.getOperations();

            xVariable.setGrid(grid);
            if (xVariable.isVector() || xVariable.isDiscrete() ) {
                autoContourTextBox.setText("");
                autoContourButton.setDown(false);
                autoContourButton.setEnabled(false);
                if ( xView.equals("xy") ) {
                    differenceButton.setDown(false);
                    differenceButton.setEnabled(true);
                } else {
                	differenceButton.setDown(false);
                    differenceButton.setEnabled(false);
                }
            }
            setupPanelsAndRefreshNOforceLASRequest(false);
        }
    };
    AsyncCallback initVizGalForHistory = new AsyncCallback() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Failed to initalizes VizGal." + caught.toString());
        }

        @Override
        public void onSuccess(Object result) {
            String[] settings = historyString.split("token");
            GridSerializable grid = (GridSerializable) result;
            xVariable.setGrid(grid);
            xAxesWidget.init(grid);
            xAnalysisWidget.setAnalysisAxes(grid);
            applyTokens(settings);
            // Automatically fire the update, don't force panels to update if
            // they don't need to and don't push history stack since this is a
            // history event.
            eventBus.fireEvent(new WidgetSelectionChangeEvent(false, false, false));
        }
    };

    ClickHandler optionsOkHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent arg0) {
            refresh(false, true, false);
        }

    };
    ClickHandler panelApplyButtonClick = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            refresh(false, true, false);
        }
    };
    AsyncCallback requestGridForHistory = new AsyncCallback() {
        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Failed to initalizes VizGal." + caught.toString());
        }

        @Override
        public void onSuccess(Object result) {
            CategorySerializable[] cats = (CategorySerializable[]) result;
            if (cats.length > 1) {
                Window.alert("Error getting variables for this dataset.");
            } else {
                if (cats[0].isVariableChildren()) {
                    // TODO not a vector.  :-)
                    Vector<VariableSerializable> vars = cats[0].getDatasetSerializable().getVariablesSerializableAsVector();
                    VariableSerializable catsVariable = cats[0].getVariable(historyTokens.get("varid"));
                    xVariable = catsVariable;
                    xPanels.get(0).setVariables(vars, xVariable);
                    initial_var = xVariable;
                    Util.getRPCService().getGrid(historyTokens.get("xDSID"), historyTokens.get("varid"), initVizGalForHistory);
                } else {
                    Window.alert("No variables found in this category");
                }
            }
        }
    };
    ClickHandler settingsButtonApplyHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent arg0) {
            applyChange();
        }

    };

    WidgetSelectionChangeEvent.Handler updateNeededEventHandler = new WidgetSelectionChangeEvent.Handler() {

        @Override
        public void onAxisSelectionChange(WidgetSelectionChangeEvent event) {
            
            if (applyButton.getCheckBoxValue() || event.isAuto() || isUpdateRequired()) {
                refresh(false, event.isPushHistory(), event.isForce());
            }

        }

    };
    VariableSelectionChangeEvent.Handler variableChangeHandler = new VariableSelectionChangeEvent.Handler() {

        @Override
        public void onVariableChange(VariableSelectionChangeEvent event) {
            Object source = event.getSource();
            if (source instanceof UserListBox) {
                UserListBox lb = (UserListBox) source;
                // Only since compare panel events.  Hmmm... better way to do this?
                if ( lb.getName().contains("Panel-0") ) {
                    VariableSerializable v = lb.getUserObject(lb.getSelectedIndex());
                    xNewVariable = v;
                    if (v.isVector() || (v.isDiscrete() && !v.isTimeSeries()) ) {
                        lb.setAddButtonEnabled(false);
                    } else {
                        lb.setAddButtonEnabled(true);
                    }
                    Util.getRPCService().getConfig(null, xNewVariable.getCATID(), xNewVariable.getDSID(), xNewVariable.getID(), getGridForChangeVariableCallback);
                }
            }
        }

    };
    SelectionHandler<TreeItem> xVisGalDatasetSelectionHandler = new SelectionHandler<TreeItem>() {
        @Override
        public void onSelection(SelectionEvent<TreeItem> event) {
            DatasetWidget datasetWidget = xDatasetButton.getDatasetWidget();
            boolean isFromMainDatasetWidget = event.getSource().equals(datasetWidget);
            if (isFromMainDatasetWidget) {
                TreeItem item = event.getSelectedItem();
                Object v = item.getUserObject();
                if (v instanceof VariableSerializable) {
                    variables.clear();
                    List<VariableSerializable> panelVars = new ArrayList<VariableSerializable>();
                    xNewVariable = (VariableSerializable) v;
                    changeDataset = true;
                    // Build the variables list from the selected item.
                    TreeItem parent = item.getParentItem();
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        TreeItem child = parent.getChild(i);
                        Object userObject = child.getUserObject();
                        if ((userObject != null) && (userObject instanceof VariableSerializable)) {
                            variables.add((VariableSerializable) userObject);
                            panelVars.add((VariableSerializable) userObject);
                        }
                    }
                    if (xPanels.size() > 0) {
                        OutputPanel outputPanel = xPanels.get(0);
                        outputPanel.getVariableControls().removeListBoxesExceptFirst();               
                    }
                    for (Iterator panelsIt = xPanels.iterator(); panelsIt.hasNext();) {
                        OutputPanel outputPanel = (OutputPanel) panelsIt.next();
                        outputPanel.setVariables(panelVars, xNewVariable);
                    }
                    changeDataset();
                    // Requested in #1538, which restores the behavior of the original picker which was disliked previously,
                    // "because it closes and I can't tell if I clicked on anything".
                    xDatasetButton.close();
                    pickerCloseActions();
                }
            }
        }
    };

}
