package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class IconCheckBox extends Composite {
	
	FlexTable layout = new FlexTable();
	Image icon;
	CheckBox checkbox;
	Label label;
	
	String id;
	
	public IconCheckBox(int i, String id) {
		this.id = id;
		checkbox = new CheckBox();
		String url = GWT.getModuleBaseURL()+"../images/trajectory_icons/icon"+i+".gif";
		icon = new Image(url);
		label = new Label(id);
		layout.setWidget(0, 0, checkbox);
		layout.setWidget(0, 1, icon);
		layout.setWidget(0, 2, label);
		initWidget(layout);
	}
	public String getID() {
		return id;
	}
	public boolean isChecked() {
		return checkbox.getValue();
	}
	public void setValue(boolean value) {
	    checkbox.setValue(value);
	}
}
