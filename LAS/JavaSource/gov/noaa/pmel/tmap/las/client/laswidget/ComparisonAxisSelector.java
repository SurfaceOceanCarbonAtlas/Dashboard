package gov.noaa.pmel.tmap.las.client.laswidget;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * A simple widget that will display a list of axes names.  Used mostly to allow vizGal users to select 
 * which axis is used as the comparison axis in the panels.
 * @author rhs
 *
 */
public class ComparisonAxisSelector extends Composite {
	FlexTable flex;
	FlexTable layout;
	ListBox axes;
	DisclosurePanel disPanel;
	public ComparisonAxisSelector(String width) {
        // The the other widgets have flextable layouts.  Without it, this one doesn't line up.
		layout = new FlexTable();
		axes = new ListBox();
		flex = new FlexTable();
		// It looks funny without some sort of label on the same line.
		HTML html = new HTML("Axis: ");
		flex.setWidget(0, 0, html);
		flex.setWidget(0, 1, axes);
		disPanel = new DisclosurePanel("Compare");
		disPanel.add(flex);
		disPanel.setOpen(true);
		
//		if ( width != null && !width.equals("") ) {
//		    disPanel.setWidth(width);
//		}
		layout.setWidget(0, 0, disPanel);
		initWidget(layout);
	}
	public void setAxes(List<String> ortho) {		
		if ( axes == null ) {
			axes = new ListBox();
		}
		axes.clear();
		StringBuffer map_axes = new StringBuffer();
		for (Iterator orthoIt = ortho.iterator(); orthoIt.hasNext();) {
			String axis = (String) orthoIt.next();
			if ( !axis.equals("x") && !axis.equals("y") ) {
				axes.addItem(axis, axis);
			} else {
				map_axes.append(axis);
			}
		}
		if ( map_axes.length() == 1 ) {
			axes.addItem(map_axes.toString(), map_axes.toString());
		} else if ( map_axes.length() == 2 ) {
			axes.addItem("xy", "xy");
		}
		if ( axes.getItemCount() == 0 || axes.getItemCount() == 1 ) {
			disPanel.setVisible(false);
		} else {
			disPanel.setVisible(true);
		}
	}
	public void setValue(String value) {
		for( int i = 0; i < axes.getItemCount(); i++) {
			if ( value.equals(axes.getValue(i) ) ) {
				axes.setSelectedIndex(i);
			}
		}
	}
	public String getValue() {
		return axes.getValue(axes.getSelectedIndex());
	}
	public void addAxesChangeHandler(ChangeHandler compareAxisChangeHandler) {
		axes.addChangeHandler(compareAxisChangeHandler);	
	}
	public void setOpen(boolean open) {
		disPanel.setOpen(open);
	}

}
