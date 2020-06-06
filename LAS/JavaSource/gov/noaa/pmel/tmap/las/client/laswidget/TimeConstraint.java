package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TimeConstraint extends Composite {
	DateTimeWidget dateTimeWidget = new DateTimeWidget();
	CheckBox apply = new CheckBox("Apply Time");
	VerticalPanel layout = new VerticalPanel();
    public TimeConstraint() {
    	layout.add(dateTimeWidget);
    	layout.add(apply);
    	initWidget(layout);
    }
	public DateTimeWidget getDateTimeWidget() {
		return dateTimeWidget;
	}
	public boolean isActive() {
		return apply.getValue();
	}
	public void setApply(boolean b) {
		apply.setValue(b);
	}
}
