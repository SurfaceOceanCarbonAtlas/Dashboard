package gov.noaa.pmel.tmap.las.client.map.example;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MapExample implements EntryPoint {
	private static final String region = "region";
	private static final String tool = "tool";
	OLMapWidget mapWidget = new OLMapWidget();
	RadioButton region1 = new RadioButton(region, "Global (0)");
	RadioButton region2 = new RadioButton(region, "Global (180)");
	RadioButton region3 = new RadioButton(region, "Africa");
	RadioButton region4 = new RadioButton(region, "Asia");
	RadioButton region5 = new RadioButton(region, "Australia");
	RadioButton region6 = new RadioButton(region, "Europe");
	RadioButton region7 = new RadioButton(region, "North America");
	RadioButton region8 = new RadioButton(region, "South America");
	RadioButton region9 = new RadioButton(region, "Indian Ocean");
	RadioButton region10 = new RadioButton(region, "North Atlantic");
	RadioButton region11 = new RadioButton(region, "Equatorial Atlantic");
	RadioButton region12 = new RadioButton(region, "South Atlantic");
	RadioButton region13 = new RadioButton(region, "North Pacific");
	RadioButton region14 = new RadioButton(region, "Equatorial Pacific");
	RadioButton region15 = new RadioButton(region, "South Pacific ");

	RadioButton tool1 = new RadioButton(tool, "Lat/Lon Region");
	RadioButton tool2 = new RadioButton(tool, "Latitude Line");
	RadioButton tool3 = new RadioButton(tool, "Longitude Line");
	RadioButton tool4 = new RadioButton(tool, "Point");

	FlexTable tools = new FlexTable();

	FlexTable dataRegions = new FlexTable();
	public void onModuleLoad() {

		mapWidget.setDataExtent(-90, 90, -180, 180, 1);
		mapWidget.setTool("xy");
		
		tool1.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setTool("xy");

			}
		});
		tool2.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setTool("x");
			}
		});
		tool3.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setTool("y");
			}
		});
		tool4.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setTool("pt");
			}
		});

		region1.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-90, 90, -180, 180, 1);		
			}

		});
		region2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-90, 90, 0, 360, 1);		
			}

		});
		region3.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-40, 40, -20, 60, 1);		
			}

		});
		region4.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(0, 80, 40, 180, 1);	
			}

		});
		region5.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-50, 0, 110, 180, 1);	
			}

		});
		region6.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(30, 75, -10, 40, 1);	
			}

		});
		region7.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(10, 75, -170, -50, 1);	
			}

		});
		region8.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-60, 15, -90, -30, 1);	
			}

		});
		region9.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-75, 30, 20, 120, 1);	
			}

		});
		region10.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(0, 70, -80, 20, 1);	
			}

		});
		region11.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-30, 30, -80, 20, 1);
			}

		});
		region12.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-75, 10, -80, 20, 1);
			}

		});
		region13.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(0, 70, 110, 260, 1);	
			}

		});
		region14.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-30, 30, 135, 285, 1);	
			}

		});
		region15.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mapWidget.setDataExtent(-75, 0, 150, 290, 1);	
			}

		});
	
		dataRegions.setWidget(0, 0, region1);
		dataRegions.setWidget(1, 0, region2);
		dataRegions.setWidget(2, 0, region3);
		dataRegions.setWidget(3, 0, region4);
		dataRegions.setWidget(4, 0, region5);
		dataRegions.setWidget(5, 0, region6);
		dataRegions.setWidget(6, 0, region7);
		dataRegions.setWidget(7, 0, region8);

		dataRegions.setWidget(0, 1, region9);
		dataRegions.setWidget(1, 1, region10);
		dataRegions.setWidget(2, 1, region11);
		dataRegions.setWidget(3, 1, region12);
		dataRegions.setWidget(4, 1, region13);
		dataRegions.setWidget(5, 1, region14);
		dataRegions.setWidget(6, 1, region15);
		
		tools.setWidget(0, 0, tool1);
		tools.setWidget(1, 0, tool2);
		tools.setWidget(2, 0, tool3);
		tools.setWidget(3, 0, tool4);

		RootPanel.get("map").add(mapWidget);
		RootPanel.get("region_selection").add(dataRegions);
		RootPanel.get("tool_selection").add(tools);

		region1.setValue(true);
		tool1.setValue(true);
		
	}
}
