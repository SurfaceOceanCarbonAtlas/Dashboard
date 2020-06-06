package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;


/**
 * This widget will create a map and a set of axes widgets to control all of the
 * XYZT and E(nsemble) dimensions for a given view.
 * 
 * @author rhs
 * 
 */
public class AxesWidgetGroup extends Composite {
    
    int xControlsWidth = (int) Constants.CONTROLS_WIDTH;

    String xControlsWidthPx = xControlsWidth + "px";

    OLMapWidget refMap;
    DateTimeWidget dateTimeWidget;
    DateTimeWidget forecastVerificationTime;
    AxisWidget zWidget;
    EnsembleAxisWidget eWidget;
    HorizontalPanel layout;
    FlexTable menuWidgets;
    FlowPanel panel;
    FlexTable row = new FlexTable();
    HTML plotAxisMessage;
    String title;
    boolean hasZ;
    boolean hasT;
    boolean hasE;
    boolean hasF;
    boolean panelIsOpen = true;
    DisclosurePanel mapPanel;
    ToggleButton toggleMapButton;
    Image toggleUp;
    Image toggleDown;
    List<String> viewAxes = new ArrayList<String>(); // This is just the view,
                                                     // but individual axes

    /**
     * A widget to hold a set of x, y, z, t, and e(nsemble) axis controls and to
     * display them in groups according to the view. Initially the map is at the
     * top and z and t are below, but this can be switched.
     * 
     * @param plot_title
     * @param ortho_title
     * @param layout
     */
    public AxesWidgetGroup(String title, String orientation, String width, String panel_title, String tile_server, String tile_layer, EventBus eventBus) {
        mapPanel = new DisclosurePanel("Map");
        mapPanel.setWidth(xControlsWidthPx);
        menuWidgets = new FlexTable();
        refMap = new OLMapWidget("128px", "256px", tile_server, tile_layer);
        zWidget = new AxisWidget();
        zWidget.setVisible(false);
        eWidget = new EnsembleAxisWidget();
        eWidget.setVisible(false);
        dateTimeWidget = new DateTimeWidget();
        dateTimeWidget.setVisible(false);
        forecastVerificationTime = new DateTimeWidget();
        forecastVerificationTime.setVisible(false);
        panel = new FlowPanel();//new DisclosurePanel(title);
        mapPanel.add(refMap);
        mapPanel.setOpen(true);
        if ( orientation.equals("horizontal") ) {
            row.setWidget(0, 0, mapPanel);
            row.setWidget(0, 1, zWidget);
            row.setWidget(0, 2, dateTimeWidget);
            row.setWidget(0, 3, forecastVerificationTime);
            row.setWidget(0, 4, eWidget);
            row.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            row.getFlexCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
            row.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
            row.getFlexCellFormatter().setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);

            panel.add(row);
        } else {
            layout.add(mapPanel);
            panel.add(layout);
            panel.setVisible(true);//.setOpen(true);
            menuWidgets.setWidget(0, 0, zWidget);
            menuWidgets.setWidget(1, 0, dateTimeWidget);
            menuWidgets.setWidget(2, 0, forecastVerificationTime);
            menuWidgets.setWidget(3, 0, eWidget);
            menuWidgets.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            menuWidgets.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
            layout.add(menuWidgets);      
        }
        initWidget(panel);
    }

    public void init(GridSerializable grid) {
        hasZ = grid.hasZ();
        hasT = grid.hasT();
        hasE = grid.hasE();
        hasF = grid.hasF();
        if ( grid.hasE() ) {
            eWidget.init(grid.getEAxis());
        } else {
            eWidget.setVisible(false);
        }
        if ( grid.hasZ() ) {
            zWidget.init(grid.getZAxis());
            // zWidget = new AxisWidget(grid.getZAxis());
        } else {
            // zWidget = new AxisWidget();
            zWidget.setVisible(false);
        }
        if ( grid.hasT() ) {
        	if ( grid.hasF() ) {
        		dateTimeWidget.init(grid.getTAxis(), false, "Lead Time: ", "Lead Time Start: ", "Lead Time End: ");
        	} else {
        		dateTimeWidget.init(grid.getTAxis(), false);
        	}
            // dateTimeWidget = new DateTimeWidget(grid.getTAxis(), false);
        } else {
            // dateTimeWidget = new DateTimeWidget();
            dateTimeWidget.setVisible(false);
        }
        if ( grid.hasF() ) {
        	forecastVerificationTime.init(grid.getFAxis(), false, "Forecast Verification Time: ", "Start Forecast Verification Time: ", "End Forecast Verification Time: ");
        } else {
        	forecastVerificationTime.setVisible(false);
        }
        if ( grid.hasX() && grid.hasY() ) {
            refMap.setDataExtent(Double.valueOf(grid.getYAxis().getLo()),
                    Double.valueOf(grid.getYAxis().getHi()),
                    Double.valueOf(grid.getXAxis().getLo()),
                    Double.valueOf(grid.getXAxis().getHi()));
        }
    }

    public void setAxisVisible(String type, boolean visible) {
        if ( type.contains("x") ) {
            mapPanel.setVisible(visible);
        }
        if ( type.contains("y") ) {
            mapPanel.setVisible(visible);
        }
        if ( type.equals("z") && hasZ ) {
            zWidget.setVisible(visible);
        }
        if ( type.equals("t") && hasT ) {
            dateTimeWidget.setVisible(visible);
        }
        if ( type.equals("e") && hasE ) {
            eWidget.setVisible(visible);
        }
        if ( type.equals("f") && hasF ) {
        	forecastVerificationTime.setVisible(visible);
        }
    }

    public void setRange(String type, boolean range) {
        // Does not apply to x and y
        if ( type.equals("e") ) {
            eWidget.setRange(range);
        }
        if ( type.equals("z") ) {
            zWidget.setRange(range);
        }
        if ( type.equals("t") ) {
            dateTimeWidget.setRange(range);
        }
        if ( type.equals("f") ) {
        	forecastVerificationTime.setRange(range);
        }
    }

    public OLMapWidget getRefMap() {
        return refMap;
    }

    public DateTimeWidget getTAxis() {
        return dateTimeWidget;
    }
    
    public DateTimeWidget getFAxis() {
    	return forecastVerificationTime;
    }

    public AxisWidget getZAxis() {
        return zWidget;
    }
    
    public EnsembleAxisWidget getEAxis() {
        return eWidget;
    }

    public void showOrthoAxes(String view, List<String> ortho, String analysis, boolean isComparePanel) {
        for ( int i = 0; i < view.length(); i++ ) {
            setAxisVisible(view.substring(i, i + 1), false);
        }
        for ( Iterator orthoIt = ortho.iterator(); orthoIt.hasNext(); ) {
            String ax = (String) orthoIt.next();
            if ( analysis != null ) {
                if ( !analysis.contains(ax) ) {
                    setAxisVisible(ax, true);
                } else {
                    setAxisVisible(ax, false);
                }
            } else {
                setAxisVisible(ax, true);
            }
        }
        // The map is never used in the compare panel.
        if ( isComparePanel ) {
            setAxisVisible("x", false);
            setAxisVisible("y", false);
        }
    }

    public List<String> getViewAxes() {
        return viewAxes;
    }

    public void setOpen(boolean b) {
        panel.setVisible(b);//.setOpen(b);
    }

    public void closePanels() {
        panelIsOpen = panel.isVisible();//.isOpen();
        panel.setVisible(false);//.setOpen(false);
    }

    public void restorePanels() {
        panel.setVisible(panelIsOpen);//.setOpen(panelIsOpen);
    }
}
