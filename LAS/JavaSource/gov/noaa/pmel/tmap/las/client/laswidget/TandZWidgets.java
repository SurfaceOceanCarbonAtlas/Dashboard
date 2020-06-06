package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
/**
 * T and Z widget for a panel... Need all axes in the panel.
 * @deprecated
 * @author rhs
 *
 */
public class TandZWidgets extends Composite {
	HorizontalPanel layout = new HorizontalPanel();
	DateTimeWidget dateTimeWidget = new DateTimeWidget();
	AxisWidget zAxisWidget = new AxisWidget();
	
	public TandZWidgets() {
		layout.add(dateTimeWidget);
		layout.add(zAxisWidget);
		initWidget(layout);
	}
	public DateTimeWidget getDateWidget() {
		return dateTimeWidget;
	}
	public AxisWidget getZWidget() {
		return zAxisWidget;
	}
	public void addAxis(String axis) {
		if ( axis.equals("t") ) {
			if ( dateTimeWidget != null ) {
				layout.add(dateTimeWidget);
			}
		} else if ( axis.equals("z") ) {
			if ( zAxisWidget != null ) {
				layout.add(zAxisWidget);
			}
		}
	}
	public void removeAxes() {
		if ( dateTimeWidget != null ) {
			layout.remove(dateTimeWidget);
		}
		if ( zAxisWidget != null ) {
			layout.remove(zAxisWidget);
		}		
	}
	public void setRange(String type, boolean b) {
		if ( type.equals("z") ) {
			zAxisWidget.setRange(b);
		} else if ( type.equals("t") ) {
			dateTimeWidget.setRange(b);
		}
	}
}
