/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.AxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;

import java.util.HashMap;
import java.util.Iterator;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.Markers;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * An OpenLayers based map for displaying time series points that are connected to an LAS for plotting of
 * time series graphs.
 * @author rhs
 *
 */
public class OLTimeSeriesMap extends Composite {
	Map map;
	private MapOptions wmsMapOptions;
	Button reset = new Button("Reset Map");
	FlexTable layout_grid = new FlexTable();
	CategorySerializable cat;
	Label select = new Label("Select a marker on the map:");
	String currentGridID;
	Point sw;
	Point ne;
	int default_zoom;
	Point default_center;
	Markers pointsLayer = new Markers("Point Features");
	OLMapWidget mapWidget;
	/**
	 * 
	 */
	public OLTimeSeriesMap() {
		
		Point center = new Point(0.0, 0.0);
		mapWidget = new OLMapWidget( "350px", "520px");
		map = mapWidget.getMap();
		map.addLayer(pointsLayer);
		layout_grid.setWidget(0, 0, reset);
		layout_grid.setWidget(0, 1, select);
		FlexCellFormatter formatter = layout_grid.getFlexCellFormatter();
		formatter.addStyleName(0, 1, "right-small-banner");
		select.addStyleName("right-small-banner");
		layout_grid.setWidget(1, 0, mapWidget);
		formatter.setColSpan(1, 0, 2);
		reset.addClickListener(resetListener);
		initWidget(layout_grid);
	}
	public void update(CategorySerializable categorySerializable) {
		cat = categorySerializable;
		mapWidget.setTool("xy");
		double sw_lat = 9999.;
		double sw_lon = 9999.;
		double ne_lat = -9999.;
		double ne_lon = -9999.;
		HashMap<String, HashMap<String, GridSerializable>> grids = new HashMap<String, HashMap<String, GridSerializable>>();
		if ( cat.hasMultipleDatasets() ) {
			DatasetSerializable[] ds = cat.getDatasetSerializableArray();
			for (int i = 0; i < ds.length; i++) {
				HashMap<String, GridSerializable> dsGrids = new HashMap<String, GridSerializable>();
				DatasetSerializable d = ds[i];
				VariableSerializable[] vars = d.getVariablesSerializable();
				for (int j = 0; j < vars.length; j++) {
					VariableSerializable var = vars[j];
					GridSerializable grid = var.getGrid();
					dsGrids.put(grid.getID(), grid);
				}
				grids.put(d.getName(), dsGrids);
			}
		} else {
			DatasetSerializable ds = cat.getDatasetSerializable();
			HashMap<String, GridSerializable> dsGrids = new HashMap<String, GridSerializable>();
			VariableSerializable[] vars = ds.getVariablesSerializable();
			for (int j = 0; j < vars.length; j++) {
				VariableSerializable var = vars[j];
				GridSerializable grid = var.getGrid();
				dsGrids.put(grid.getID(), grid);
			}
			grids.put(ds.getName(), dsGrids);
		}
		for (Iterator gridIt = grids.keySet().iterator(); gridIt.hasNext();) {
			String key = (String) gridIt.next();
			HashMap<String, GridSerializable> dsGrids = grids.get(key);
			for (Iterator dsgIt = dsGrids.keySet().iterator(); dsgIt.hasNext();) {
				String gridid = (String) dsgIt.next();
				GridSerializable grid = dsGrids.get(gridid);

				AxisSerializable xAxis = grid.getXAxis();
				AxisSerializable yAxis = grid.getYAxis();
				double y = Double.valueOf(yAxis.getLo()).doubleValue();
				double x = Double.valueOf(xAxis.getLo()).doubleValue();

				if ( y < sw_lat ) sw_lat = y;
				if ( y > ne_lat ) ne_lat = y;
				if ( x < sw_lon ) sw_lon = x;
				if ( x > ne_lon ) ne_lon = x;
			
				// options.setTitle(key); this is the name of the station.  We need to get it
				// in the hover for this marker somehow.
				
				
		        
				Marker marker = new Marker(new LonLat(x, y));
				//marker.addBrowserEventListener(BrowserEvent., listener)
				final String gridID = grid.getID();
				
				// This is the marker click listener to handle the marker event.
//				marker.addMarkerClickHandler(new MarkerClickHandler() {
//					String id = gridID;
//					public void onDoubleClick(Marker sender) {
//					}
//
//					public void onClick(MarkerClickEvent event) {
//						setCurrentGridID(gridID);
//					}
//				});
				pointsLayer.addMarker(marker);
			}
		}

		double center_x = sw_lon + ((ne_lon - sw_lon)/2.0);
		double center_y = sw_lat + ((ne_lat - sw_lat)/2.0);
		mapWidget.setDataExtent(sw_lat, ne_lat, sw_lon, ne_lon);
		mapWidget.zoomMapToSelection();
//		.setCenter(default_center, default_zoom);

	}
	ClickListener resetListener = new ClickListener() {
		public void onClick(Widget button) {
			mapWidget.zoomMapToSelection();
		}
	};
	public void setCurrentGridID(String id) {
		currentGridID = id;
	}
	public String getCurrentGridID() {
		return this.currentGridID;
	}
	
//	public void addMapClickHandler(MapClickHandler handler) {
//		map.addMapClickHandler(handler);
//	}
}
