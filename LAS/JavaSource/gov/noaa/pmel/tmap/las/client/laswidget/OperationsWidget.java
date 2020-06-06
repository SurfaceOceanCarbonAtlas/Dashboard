package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.VariablePluralityEvent;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The high level widget that displays all of the operations associated with a
 * particular data set and variable.
 * 
 * @author rhs
 * 
 */
public class OperationsWidget extends Composite {
    boolean isOpen;
    FlexTable layout = new FlexTable();
    FlowPanel xyMap = new FlowPanel();
    FlexTable xyMapTable = new FlexTable();
    FlowPanel linePlots = new FlowPanel();
    FlexTable linePlotsTable = new FlexTable();
    FlowPanel sectionPlots = new FlowPanel();
    FlexTable sectionPlotsTable = new FlexTable();
    FlowPanel hofmullerPlots = new FlowPanel();
    FlexTable hofmullerPlotsTable = new FlexTable();
    FlowPanel forecastPlots = new FlowPanel();
    FlexTable forecastPlotsTable = new FlexTable();

    int xyMapRow = 0;
    int linePlotsRow = 0;
    int sectionPlotsRow = 0;
    int hofmullerPlotsRow = 0;
    int forecastPlotsRow = 0;

    boolean hasXYMap = false;
    boolean hasLinePlots = false;
    boolean hasSectionPlots = false;
    boolean hasHofmullerPlots = false;
    boolean hasForecastPlots = false;
    OperationSerializable[] ops;
    OperationSerializable currentOperation;
    String currentView;
    ArrayList<OperationRadioButton> buttons = new ArrayList<OperationRadioButton>();
    ArrayList<ClickHandler> clicks = new ArrayList<ClickHandler>();
    String intervals;
    String initialOp;
    String initialView;
    String groupName;
    GridSerializable grid;

    // Optional OperationsMenu. If set, then it is kept in sync with this
    // widget.
    OperationsMenu operationsMenu = null;
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();

    public OperationsMenu getOperationsMenu() {
        return operationsMenu;
    }

    public void setOperationsMenu(OperationsMenu operationsMenu) {
        this.operationsMenu = operationsMenu;
    }

    /**
     * Set up the StackPanel and the associated RPC.
     */
    public OperationsWidget(String groupName) {
        this.groupName = groupName;
        layout.setWidth("256px");
        initWidget(layout);
    }
    public void setOperations(String intervals, String dsID, String varID, String opID, String view) {

        this.intervals = intervals;
        this.initialOp = opID;
        this.initialView = view;
        this.currentView = initialView;
        Util.getRPCService().getOperations(null, dsID, varID, operationsCallback);
    }

    public void setOperations(GridSerializable grid, String opID, String view, OperationSerializable[] ops) {
        this.grid = grid;
        this.intervals = grid.getIntervals();
        this.initialOp = opID;
        this.initialView = view;
        this.currentView = initialView;
        this.ops = ops;
        setOps();
    }

    AsyncCallback operationsCallback = new AsyncCallback() {
        public void onSuccess(Object result) {
            ops = (OperationSerializable[]) result;
            setOps();
        }

        public void onFailure(Throwable caught) {
            // TODO Alert users...
        }
    };
    ClickListener buttonListener = new ClickListener() {

        public void onClick(Widget sender) {
            OperationRadioButton button = (OperationRadioButton) sender;
            currentOperation = button.getOperation();
            currentView = button.getView();
        }

    };

    private void setMenu() {
        if ( operationsMenu != null ) {
            operationsMenu.setMenus(ops, currentView);
        }
    }

    private void setOps() {
        isOpen = true;
        hasHofmullerPlots = false;
        hasLinePlots = false;
        hasSectionPlots = false;
        hasXYMap = false;
        hasForecastPlots = false;
        layout.clear();
        buttons.clear();
        xyMap.clear();
        xyMap.add(new Label("Maps"));
        linePlots.clear();
        linePlots.add(new Label("Line Plots"));
        sectionPlots.clear();
        sectionPlots.add(new Label("Vertical Section Plots"));
        hofmullerPlots.clear();      // The o-umlat breaks compiling in some locales without a compiler option.
        hofmullerPlots.add(new Label("Hovmoller Plots"));
        forecastPlots.clear();
        forecastPlots.add(new Label("Forecast Plots"));
        xyMapRow = 0;
        linePlotsRow = 0;
        sectionPlotsRow = 0;
        hofmullerPlotsRow = 0;
        forecastPlotsRow = 0;
        for ( int i = 0; i < ops.length; i++ ) {
            OperationSerializable op = ops[i];
            String category = op.getAttributes().get("category");
            if ( category.equals("visualization") ) {
                List<String> views = op.getViews();
                for ( Iterator viewIt = views.iterator(); viewIt.hasNext(); ) {
                    String view = (String) viewIt.next();
                    if ( !op.getName().contains("omparison") ) {
                        Map<String, String> attrs = op.getAttributes();

                        // A hack to allow zoomable plots in the old interface
                        // and use Plot_2D_XY here.
                        // if ( (attrs != null && attrs.containsKey("default")
                        // && !op.getID().equals("Plot_2D_XY_zoom"))||
                        // op.getID().equals("Plot_2D_XY")) {
                        if ( attrs != null && attrs.get("default") != null && Boolean.valueOf(attrs.get("default")) ) {

                            if ( (view.equals("xy") && (intervals.contains("x") && intervals.contains("y"))) 
                                    || op.getID().contains("Trajectory_interactive_plot") 
                                    || op.getID().contains("Profile_interactive_plot")
                                    || op.getID().contains("Timeseries_interactive_plot")
                                    || op.getID().contains("Point_location_value_plot")) { // TODO, this is a hack for specific operations...
                                if ( !hasXYMap ) {
                                    xyMapTable.clear();
                                    hasXYMap = true;
                                }
                                OperationRadioButton button = new OperationRadioButton(groupName, "Latitude-Longitude");
                                button.setView(view);
                                button.setOperation(op);
                                button.addClickListener(buttonListener);
                                button.setChecked(true);
                                buttons.add(button);
                                currentOperation = button.getOperation();
                                currentView = "xy";
                                xyMapTable.setWidget(xyMapRow, 0, button);
                                xyMapRow++;
                            } else if ( view.equals("tf") ) {
                            	if ( !hasForecastPlots ) {
                            		forecastPlotsTable.clear();
                            		hasForecastPlots = true;
                            	}
                            	OperationRadioButton button = new OperationRadioButton(groupName, "Verification vs Initialization");
                            	if ( button != null ) {
                                    button.setView(view);
                                    button.setOperation(op);
                                    button.addClickListener(buttonListener);
                                    buttons.add(button);
                                    forecastPlotsTable.setWidget(forecastPlotsRow, 0, button);
                                    forecastPlotsRow++;
                                }
                            	
                            } else if ( (view.equals("x") && intervals.contains("x")) || (view.equals("y") && intervals.contains("y")) || (view.equals("z") && intervals.contains("z"))
                                    || (view.equals("t") && intervals.contains("t")) || (view.equals("e") && intervals.contains("e")) 
                                    // Special case where the TE is a bunch of line plots stacked on top of each other...
                                    ||  (view.equals("te") && intervals.contains("e") && intervals.contains("t")) 
                                    // Special case where we stack the plot for timeseries DSG data which has an xyt data view and t plot view
                                    || op.getID().contains("Timeseries_station_plot")) {
                                if ( !hasLinePlots ) {
                                    linePlotsTable.clear();
                                    hasLinePlots = true;
                                }
                                OperationRadioButton button = null;
                                if ( view.equals("x") ) {
                                    button = new OperationRadioButton(groupName, "Longitude");
                                } else if ( view.equals("y") ) {
                                    button = new OperationRadioButton(groupName, "Latitude");
                                } else if ( view.equals("z") ) {
                                    // TODO, get the grid and initialize from
                                    // the grid so you have the z-axis label.
                                    String l = grid.getZAxis().getLabel();
                                    String n = grid.getZAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, l);
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, n);
                                    } else {
                                        button = new OperationRadioButton(groupName, "Z");
                                    }
                                } else if ( view.equals("t") ||
                                		    op.getID().contains("Timeseries_station_plot") ) {
                                    button = new OperationRadioButton(groupName, "Time");
                                } else if ( view.equals("e") ) {
                                    String l = grid.getEAxis().getLabel();
                                    String n = grid.getEAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, l);
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, n);
                                    } else {
                                        button = new OperationRadioButton(groupName, "Ensemble");
                                    }
                                } else if ( view.equals("te")) {
                                    String l = grid.getEAxis().getLabel();
                                    String n = grid.getEAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, l+"-time");
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, n+"-time");
                                    } else {
                                        button = new OperationRadioButton(groupName, "E-time");
                                    }      
                                }
                                if ( button != null ) {
                                    button.setView(view);
                                    button.setOperation(op);
                                    button.addClickListener(buttonListener);
                                    buttons.add(button);
                                    linePlotsTable.setWidget(linePlotsRow, 0, button);
                                    linePlotsRow++;
                                }
                            } else if ( (view.equals("xz") && intervals.contains("x") && intervals.contains("z")) ||
                                    (view.equals("yz") && intervals.contains("y") && intervals.contains("z")) ||
                                    (view.equals("ze") && intervals.contains("z") && intervals.contains("e")) ||
                                    (op.getID().contains("Profile_Plot_2D_zoom") && (view.equals("xzt") || view.contains("yzt")))
                                    ) {
                                if ( !hasSectionPlots ) {
                                    sectionPlotsTable.clear();
                                    hasSectionPlots = true;
                                }
                                OperationRadioButton button = null;
                                if ( view.contains("xz") ) {
                                    String l = grid.getZAxis().getLabel();
                                    String n = grid.getZAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, "Longitue-"+l);
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, "Longitue-"+n);
                                    } else {
                                        button = new OperationRadioButton(groupName, "Longitude-z");
                                    }
                                } else if ( view.contains("yz") ) {
                                    String l = grid.getZAxis().getLabel();
                                    String n = grid.getZAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, "Latitude-"+l);
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, "Latitude-"+n);
                                    } else {
                                        button = new OperationRadioButton(groupName, "Latitude-z");
                                    }
                                } else if ( view.equals("ze") ) {
                                    
                                    // At the moment we won't consider the case where one has a name and one has a label.
                                    String l = grid.getZAxis().getLabel();
                                    String n = grid.getZAxis().getName();
                                    String le = grid.getEAxis().getLabel();
                                    String ne = grid.getEAxis().getName();
                                    if ( l != null && !l.equals("") ) { 
                                        if ( le != null && !le.equals("") ) {
                                            button = new OperationRadioButton(groupName, l+"-"+le);
                                        } else {
                                            button = new OperationRadioButton(groupName, l+"-Ensemble");
                                        }
                                    } else if ( n != null && !n.equals("") ) {
                                        
                                        if ( ne != null && !ne.equals("") ) {
                                            button = new OperationRadioButton(groupName, n+"-"+ne);
                                        } else {
                                            button = new OperationRadioButton(groupName, n+"-Ensemble");
                                        }
                                    } else {
                                        button = new OperationRadioButton(groupName, "Ensemble-z");
                                    }
                                }
                                button.setOperation(op);
                                button.setView(view);
                                button.addClickListener(buttonListener);
                                buttons.add(button);
                                sectionPlotsTable.setWidget(sectionPlotsRow, 0, button);
                                sectionPlotsRow++;
                            } else if ( (view.equals("xt") && intervals.contains("x") && intervals.contains("t")) ||
                                    (view.equals("yt") && intervals.contains("y") && intervals.contains("t")) ||
                                    (view.equals("zt") && intervals.contains("z") && intervals.contains("t") ||
                                    (op.getID().contains("Profile_Plot_2D_zoom") && (view.equals("xt") || view.equals("yt") || view.equals("zt") ) ))
                                   ) {
                                if ( !hasHofmullerPlots ) {
                                    hofmullerPlotsTable.clear();
                                    hasHofmullerPlots = true;
                                }
                                OperationRadioButton button = null;
                                if ( view.equals("xt") ) {
                                    button = new OperationRadioButton(groupName, "Longitude-time");
                                } else if ( view.equals("yt") ) {
                                    button = new OperationRadioButton(groupName, "Latitude-time");
                                } else if ( view.equals("zt")) {
                                    String l = grid.getZAxis().getLabel();
                                    String n = grid.getZAxis().getName();
                                    if ( l != null && !l.equals("") ) {
                                        button = new OperationRadioButton(groupName, l+"-time");
                                    } else if ( n != null && !n.equals("") ) {
                                        button = new OperationRadioButton(groupName, n+"-time");
                                    } else {
                                        button = new OperationRadioButton(groupName, "Z-time");
                                    }      
                                } 
                                if ( button != null ) {
                                    button.setView(view);
                                    button.setOperation(op);
                                    button.addClickListener(buttonListener);
                                    buttons.add(button);
                                    hofmullerPlotsTable.setWidget(hofmullerPlotsRow, 0, button);
                                    hofmullerPlotsRow++;
                                }
                            }
                        }
                    }
                }
            }
        }
        int row = 0;
        if ( hasXYMap ) {
            xyMap.add(xyMapTable);
            layout.setWidget(row, 0, xyMap);
//            xyMap.setVisible(true);// .setOpen(true);
            row++;
        }
        if ( hasLinePlots ) {
            linePlots.add(linePlotsTable);
            layout.setWidget(row, 0, linePlots);
//            linePlots.setVisible(true);// .setOpen(true);
            row++;
        }
        if ( hasSectionPlots ) {
            sectionPlots.add(sectionPlotsTable);
            layout.setWidget(row, 0, sectionPlots);
//            sectionPlots.setVisible(true);// .setOpen(true);
            row++;
        }
        if ( hasHofmullerPlots ) {
            hofmullerPlots.add(hofmullerPlotsTable);
            layout.setWidget(row, 0, hofmullerPlots);
            row++;
//            hofmullerPlots.setVisible(true);// .setOpen(true);
        }
        if ( hasForecastPlots ) {
        	forecastPlots.add(forecastPlotsTable);
        	layout.setWidget(row, 0, forecastPlots);
        	row++;
        }
        for ( Iterator clickIt = clicks.iterator(); clickIt.hasNext(); ) {
            ClickHandler click = (ClickHandler) clickIt.next();
            addClickHandler(click);
        }
        if ( initialOp != null && initialView != null ) {
            boolean set = setOperation(initialOp, initialView);
            if ( !set ) {
                setZero(initialView);
            }
        }
        setMenu();
//        int totOps = xyMapRow + linePlotsRow + sectionPlotsRow + hofmullerPlotsRow;
        
//        if ( totOps == 1 ) {
//            // There is no operations choice to be made...
//            this.setVisible(false);
//        } else {
//            this.setVisible(true);
//        }
    }

    public OperationSerializable[] getOperationsSerializable() {
        return ops;
    }

    public OperationSerializable getCurrentOperation() {
        return currentOperation;
    }

    public String getCurrentView() {
        return currentView;
    }

    public void addClickHandler(ClickHandler operationsClickHandler) {
        if ( !clicks.contains(operationsClickHandler) ) {
            clicks.add(operationsClickHandler);
        }
        for ( Iterator buttonIt = buttons.iterator(); buttonIt.hasNext(); ) {
            OperationRadioButton button = (OperationRadioButton) buttonIt.next();
            button.addClickHandler(operationsClickHandler);
        }
    }

    public boolean setOperation(String id, String view) {
        boolean set = false;
        for ( Iterator buttonId = buttons.iterator(); buttonId.hasNext(); ) {
            OperationRadioButton button = (OperationRadioButton) buttonId.next();
            if ( button.getOperation().getID().equals(id) && button.getView().equals(view) ) {
                button.setValue(true);
                currentOperation = button.getOperation();
                currentView = button.getView();
                set = true;
            } else {
                button.setValue(false);
            }
        }
        return set;
    }

    public void setOpen(boolean open) {// TODO Auto-generated method stub
        
        isOpen = open;
        xyMap.setVisible(open);// .setOpen(open);
        linePlots.setVisible(open);// .setOpen(open);
        sectionPlots.setVisible(open);// .setOpen(open);
        hofmullerPlots.setVisible(open);// .setOpen(open);
        forecastPlots.setVisible(open);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String setZero(String view) {
        String id = findZero(view);
        if ( id != null ) {
            return id;
        }
        
        // No operation was found using the view requested.
        // Find the first operation available by progressing through the most common views.
        id = findZero("xy");
        id = findZero("xyt");
        if ( id != null ) {
            return id;
        }
        id = findZero("t");
        if ( id != null ) {
            return id;
        }
        id = findZero("x");
        if ( id != null ) {
            return id;
        }
        id = findZero("y");
        if ( id != null ) {
            return id;
        }
        id = findZero("z");
        if ( id != null ) {
            return id;
        }
        id = findZero("xz");
        if ( id != null ) {
            return id;
        }
        id = findZero("xt");
        if ( id != null ) {
            return id;
        }
        id = findZero("yz" );
        if ( id != null ) {
            return id;
        }
        id = findZero("yt" );
        if ( id != null ) {
            return id;
        }
        id = findZero("zt" );
        if ( id != null ) {
            return id;
        }
        id = findZero("xyz" );
        if ( id != null ) {
            return id;
        }
        id = findZero("xyt");
        if ( id != null ) {
            return id;
        }
        id = findZero("xzt" );
        if ( id != null ) {
            return id;
        }
        id = findZero("yzt" );
        if ( id != null ) {
            return id;
        }
        id = findZero("xyzt");
        if ( id != null ) {
            return id;
        }
        return null;
    }
    private String findZero(String view) {
        for ( Iterator buttonIt = buttons.iterator(); buttonIt.hasNext(); ) {
            OperationRadioButton zero = (OperationRadioButton) buttonIt.next();// TODO Auto-generated method stub
            
            if ( zero.getOperation().getViews().contains(view) && zero.getView().equals(view) ) {
                currentOperation = zero.getOperation();
                currentView = zero.getView();
                zero.setValue(true);
                return currentOperation.getID();
            }
        }
        return null;
    }
    public void setByNumberOfVariables(int i) {
        // First see if the current operation supports the number of variables.
        if ( isInRange(currentOperation, i) ) {
            return;
        }      
        //  Need to find a new operation, so start searching.
        for ( Iterator buttonsIt = buttons.iterator(); buttonsIt.hasNext(); ) {
            OperationRadioButton button = (OperationRadioButton) buttonsIt.next();
            OperationSerializable op = button.getOperation();
            if ( isInRange(op, i) ) {
                button.setValue(true);
                currentOperation = op;
                currentView = button.getView();
                break;
            }
        }       
    }
    public boolean isInRange(OperationSerializable op, int i) {
        int min;
        int max;
        if ( op.getAttributes().containsKey("minvars") ) {
            String mmin = op.getAttributes().get("minvars");
            min = Integer.valueOf(mmin);
        } else { 
            min = 1;
        }
        if ( op.getAttributes().containsKey("maxvars") ) {
            String mmax = op.getAttributes().get("maxvars");
            max = Integer.valueOf(mmax);
        } else { 
            max = 1;
        }
        if ( i >= min && i <= max ) {
            return true;
        }
        return false;
    }

    public void setOperationsForAnalysis(GridSerializable grid, String intervals, String id, String xView, OperationSerializable[] ops) {
        this.grid = grid;
        this.intervals = intervals;
        this.initialOp = id;
        this.initialView = xView;
        this.currentView = initialView;
        this.ops = ops;
        setOps();
    }
}
