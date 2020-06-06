package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class NavAxesGroup extends Composite {
	private final Logger logger = Logger.getLogger(NavAxesGroup.class.getName());
	OLMapWidget refMap;
	DateTimeWidget dateTimeWidget;
	DateTimeWidget forecastInitializationTime;
	AxisWidget zAxisWidget;
	AxisWidget eAxisWidget;
	FlexTable axesLayout = new FlexTable();
	FlowPanel axesPanel;//DisclosurePanel axesPanel;
	String axesTitle;
	HTML message = new HTML("message");
	boolean hasZ;
	boolean hasT;
	boolean hasE;
	boolean hasF;
	boolean axesPanelIsOpen = true;
	public NavAxesGroup(String title, String width, String tile_server, String tile_layer) {
		refMap = new OLMapWidget("128px", "256px", tile_server, tile_layer);
		zAxisWidget = new AxisWidget();
		zAxisWidget.setVisible(false);
		eAxisWidget = new AxisWidget();
		eAxisWidget.setVisible(false);
		dateTimeWidget = new DateTimeWidget();
		dateTimeWidget.setVisible(false);
		forecastInitializationTime = new DateTimeWidget();
        forecastInitializationTime.setVisible(false);
		message.setVisible(false);
		axesLayout.setWidget(0, 0, message);
		axesLayout.setWidget(1, 0, refMap);
		axesPanel = new FlowPanel();//new DisclosurePanel(title);
		axesPanel.ensureDebugId("axesPanel");
		axesPanel.add(axesLayout);
		axesPanel.setVisible(true);//.setOpen(true);
		message.addStyleName("TinyBlue");
		initWidget(axesPanel);
	}
	public void setMessage(String mess) {
		message.setVisible(true);
		message.setHTML(mess);
	}
	public void init(GridSerializable grid) {
		hasZ = grid.hasZ();
		hasT = grid.hasT();
		hasE = grid.hasE();
		hasF = grid.hasF();
		if ( grid.hasZ() ) {
			zAxisWidget = new AxisWidget(grid.getZAxis());
			zAxisWidget.setVisible(true);
		} else {
			zAxisWidget = new AxisWidget();
			zAxisWidget.setVisible(false);
		}
		if ( grid.hasT() ) {
			if ( grid.hasF() ) {
				dateTimeWidget = new DateTimeWidget(grid.getTAxis(), false, "Lead Time: ", "Lead Time Start: ", "Lead Time End: ");
			} else {
				dateTimeWidget = new DateTimeWidget(grid.getTAxis(), false);
			}
			dateTimeWidget.setVisible(true);
		} else {
			dateTimeWidget = new DateTimeWidget();
			dateTimeWidget.setVisible(false);
		}
		if( grid.hasE() ) {
		    eAxisWidget = new AxisWidget(grid.getEAxis());
		    eAxisWidget.setVisible(true);
		} else {
		    eAxisWidget = new AxisWidget();
		    eAxisWidget.setVisible(false);
		}
		if ( grid.hasF() ) {
			forecastInitializationTime = new DateTimeWidget(grid.getFAxis(), false, "Verification Time: ", "Start Forecast Verification Time: ", "End Forecast Verification Time: ");
			forecastInitializationTime.setVisible(true);
		} else {
			forecastInitializationTime = new DateTimeWidget();
			forecastInitializationTime.setVisible(false);
		}
		axesLayout.setWidget(2, 0, zAxisWidget);
		dateTimeWidget.ensureDebugId("dateTimeWidget");
		axesLayout.setWidget(3, 0, dateTimeWidget);
		axesLayout.setWidget(4, 0, forecastInitializationTime);
		axesLayout.setWidget(5, 0, eAxisWidget);
		logger.warning("axesLayout:"+axesLayout.toString());
		if ( grid.hasX() && grid.hasY() ) {
			refMap.setDataExtent(Double.valueOf(grid.getYAxis().getLo()), 
					             Double.valueOf(grid.getYAxis().getHi()), 
					             Double.valueOf(grid.getXAxis().getLo()), 
					             Double.valueOf(grid.getXAxis().getHi()));
		}
	}
	public DateTimeWidget getTAxis() {
    	return dateTimeWidget;
    }
    public AxisWidget getZAxis() {
    	return zAxisWidget;
    }
    public AxisWidget getEAxis() {
        return eAxisWidget;
    }
    public DateTimeWidget getFAxis() {
    	return forecastInitializationTime;
    }
    public OLMapWidget getRefMap() {
    	return refMap;
    }
    public void setOpen(boolean value) {
    	axesPanel.setVisible(value);//.setOpen(value);
    }
    public void closePanels() {
    	axesPanelIsOpen = axesPanel.isVisible();//.isOpen();
    	axesPanel.setVisible(false);//.setOpen(false);
    }
    public void restorePanels() {
        // There is no option for it to have been open or closed as it was when it was in a disclosure panel so in method it should always be set it visible.
    	axesPanel.setVisible(true);//.setOpen(axesPanelIsOpen);
    }
    public void setRange(String type, boolean range) {
		// Does not apply to x and y
		if ( type.equals("z") ) {
			zAxisWidget.setRange(range);
		}
		if ( type.equals("t") ) {
			dateTimeWidget.setRange(range);
		}
		if ( type.equals("e") ) {
		    eAxisWidget.setRange(range);
		}
		if ( type.equals("f") ) {
			forecastInitializationTime.setRange(range);
		}
	}
	public void showMessage(boolean b) {
		message.setVisible(b);
	}
	public void showViewAxes(String xView, List<String> ortho, String analysis) {
		for (Iterator orthoIt = ortho.iterator(); orthoIt.hasNext();) {
    		String ax = (String) orthoIt.next();
    		setAxisVisible(ax, false);
			setRange(ax, true);
		}
		for (int i = 0; i < xView.length(); i++) {
			String type = xView.substring(i, i+1);
			setAxisVisible(type, true);
			setRange(type, true);
		}
		if ( analysis != null ) {
		    for ( int i = 0; i < analysis.length(); i++ ) {
		        String type = analysis.substring(i, i+1);
		        setAxisVisible(type, true);
		        setRange(type, true);
		    }
		}
		// Always show the map in the nav window since it will always control the compare panel selection.
		setAxisVisible("x", true);
		setAxisVisible("y", true);
	}
	public void setFixedAxis(String xView, List<String> ortho, String compareAxis) {
		if ( compareAxis != null ) {
        	for (Iterator orthoIt = ortho.iterator(); orthoIt.hasNext();) {
        		String ax = (String) orthoIt.next();
        		if ( !compareAxis.contains(ax) ) {
        			setAxisVisible(ax, true);
        		} else {
        			setAxisVisible(ax, false);
        		}
        	}
        } else {
        	// Otherwise just show all the orthogonal axes
        	for (Iterator orthoIt = ortho.iterator(); orthoIt.hasNext();) {
        		String ax = (String) orthoIt.next();
        		setAxisVisible(ax, true);
        	}
        }
	}
	private void setAxisVisible(String type, boolean visible) {
		if ( type.contains("x") ) {
			refMap.setVisible(visible);
		}
		if ( type.contains("y") ) {
			refMap.setVisible(visible);
		}
		if ( type.equals("z") ) {
			zAxisWidget.setVisible(visible);
		}
		if ( type.equals("t") ) {
			dateTimeWidget.setVisible(visible);
		}
		if ( type.equals("e") ) {
		    eAxisWidget.setVisible(visible);
		}
		if ( type.equals("f") ) {
			forecastInitializationTime.setVisible(visible);
		}
	}
	public void setLoByDouble(double tlo, String time_origin, String dateUnits, String calendar) {
	    dateTimeWidget.setLoByDouble(tlo, time_origin, dateUnits, calendar);
	}
	public void setHiByDouble(double thi, String time_origin, String time_min, String dateUnits, String calendar) {
	    dateTimeWidget.setHiByDouble(thi, time_origin, dateUnits, calendar);
	}
}
