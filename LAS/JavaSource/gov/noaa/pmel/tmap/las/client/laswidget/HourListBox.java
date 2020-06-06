package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.map.GeoUtil;

import com.google.gwt.user.client.ui.ListBox;
/**
 * Used by the Date Picker.
 * @author rhs
 *
 */
public class HourListBox extends ListBox {
	public void addItem(int hour, int min) {
		addItem(GeoUtil.format_two(hour)+":"+GeoUtil.format_two(min), GeoUtil.format_two(hour)+":"+GeoUtil.format_two(min));
	}
	public void addItem(int hour) {
		addItem(GeoUtil.format_two(hour));
	}
	public int getHour() {
		String selected_time = getValue(getSelectedIndex());
		if ( selected_time.contains(":") ) {
			return Integer.valueOf(selected_time.substring(0, selected_time.indexOf(":"))).intValue();
		} 
		return Integer.valueOf(selected_time).intValue();
	}
	public int getMin() {
		String selected_time = getValue(getSelectedIndex());
		return Integer.valueOf(selected_time.substring(selected_time.indexOf(":")+1)).intValue();
	}
	public int getHour(int i) {
		String selected_time = getValue(i);
		if ( selected_time.contains(":") ) {
			return Integer.valueOf(selected_time.substring(0, selected_time.indexOf(":"))).intValue();
		} 
		return Integer.valueOf(selected_time).intValue();
	}
	public int getMinute(int i) {
		String selected_time = getValue(i);
		return Integer.valueOf(selected_time.substring(selected_time.indexOf(":")+1)).intValue();
	}
}
