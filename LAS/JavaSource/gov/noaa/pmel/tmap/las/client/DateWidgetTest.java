package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.laswidget.DateTimeWidget;
import gov.noaa.pmel.tmap.las.client.serializable.TimeAxisSerializable;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * A GWT EntryPoint class for testing the GWT implementation of the data time widgets.
 * TODO: Add no leap, all leap and 360-day calendars.  Consider GODA Time or others for implementation.
 * @author rhs
 *
 */
public class DateWidgetTest implements EntryPoint {
	VerticalPanel main = new VerticalPanel();
    DateTimeWidget dateWidget;
    DateTimeWidget dateWidget2;
    DateTimeWidget dateWidget3;
    DateTimeWidget dateWidget4;
    DateTimeWidget dateWidget5;
    DateTimeWidget dateWidget6;

	public void onModuleLoad() {
		dateWidget = new DateTimeWidget();
		TimeAxisSerializable taxis = new TimeAxisSerializable();
		taxis.setLo("1991-03-14 12:00");
		taxis.setHi("2010-11-10 15:00");
		taxis.setMinuteInterval(180.0d);
		taxis.setCalendar("proleptic_gregorian");
		taxis.setYearNeeded(true);
		taxis.setMonthNeeded(true);
		taxis.setDayNeeded(true);
		taxis.setHourNeeded(true);
		dateWidget.init(taxis, true);
		main.add(new Label("Proleptic Gregorian from 1991-03-14 12:00 to 2010-11-10 15:00 every 3 hours"));
		main.add(dateWidget);
		dateWidget6 = new DateTimeWidget();
		taxis.setLo("1991-03-14 12:00");
	    taxis.setHi("1991-03-25 18:00");
		dateWidget6.init(taxis, true);
        main.add(new Label("Proleptic Gregorian from 1991-03-14 12:00 to 1991-03-25 18:00 every 3 hours"));
        main.add(dateWidget6);
		dateWidget2 = new DateTimeWidget();
		taxis.setMinuteInterval(6*60d);
		dateWidget2.init(taxis, true);
		main.add(new Label("Proleptic Gregorian 1989-03-29 00:00 to 1994-12-03 18:00 every 6 hours"));
		main.add(dateWidget2);
		taxis.setCalendar("noleap");
		taxis.setLo("1989-03-29 00:00");
		taxis.setHi("1994-12-03 18:00");
		dateWidget3 = new DateTimeWidget();
		dateWidget3.init(taxis, true);
		main.add(new Label("No Leap Calendar 1989-03-29 00:00 to 1994-12-03 18:00 every 6 hours"));
		main.add(dateWidget3);
		dateWidget4 = new DateTimeWidget();
		taxis.setCalendar("all_leap");
        dateWidget4.init(taxis, true);
        main.add(new Label("366 Day Calendar 1989-03-29 00:00 to 1994-12-03 18:00 every 6 hours"));
		main.add(dateWidget4);
		dateWidget5 = new DateTimeWidget();
		taxis.setCalendar("360_day");
		dateWidget5.init(taxis, true);
		main.add(new Label("360 Calendar 1989-03-29 00:00 to 1994-12-03 18:00 every 6 hours"));
		main.add(dateWidget5);
		RootPanel.get("examples").add(main);
	}
}
