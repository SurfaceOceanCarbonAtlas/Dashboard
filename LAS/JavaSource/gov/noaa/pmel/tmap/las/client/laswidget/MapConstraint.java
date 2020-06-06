package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A XY selection widget that also has a checkbox to control whether the constraint is applied in a request.
 * @author rhs
 *
 */
public class MapConstraint extends Composite {
	VerticalPanel layout = new VerticalPanel();
	OLMapWidget map = new OLMapWidget("128px", "256px");
	CheckBox applyX = new CheckBox("Apply Longitude");
	CheckBox applyY = new CheckBox("Apply Latitude");
	HorizontalPanel apply = new HorizontalPanel();
	public MapConstraint() {
		layout.add(map);
		apply.add(applyX);
		applyX.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setTool();
			}
		});
		apply.add(applyY);
		applyY.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				setTool();	
			}
		});
		layout.add(apply);
		initWidget(layout);
	}
	public OLMapWidget getMap() {
		return map;
	}
	public void setApply(String axis, boolean b) {
		if ( axis.equals("x") ) {
			applyX.setValue(b);
		} else if ( axis.equals("y") ) {
			applyY.setValue(b);
		}
	}
	public boolean isActive(String axis) {
		if ( axis.equals("x") ) {
			return applyX.getValue();
		} else if ( axis.equals("y") ) {
			return applyY.getValue(); 
		} else {
			return false;
		}
	}
	private void setTool() {
		StringBuilder tool = new StringBuilder();
		if ( applyX.getValue() ) {
			tool.append("x");
		}
		if ( applyY.getValue() ) {
			tool.append("y");
		}
		if ( tool.length() > 0 ) {
			map.setTool(tool.toString());
		} else {
			map.setTool("xy");
		}
	}
}
