package gov.noaa.pmel.tmap.las.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gwtbootstrap3.client.ui.CheckBox;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.events.resize.ResizeMapEvent;
import com.google.gwt.maps.client.events.resize.ResizeMapHandler;
import com.google.gwt.maps.client.mvc.MVCArray;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Polyline;
import com.google.gwt.maps.client.overlays.PolylineOptions;
import com.google.gwt.maps.client.overlays.Rectangle;
import com.google.gwt.maps.client.overlays.RectangleOptions;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClusterer;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClustererOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.inventory.Breadcrumb;
import gov.noaa.pmel.tmap.las.client.inventory.CategoryItem;
import gov.noaa.pmel.tmap.las.client.inventory.InventoryDatasetPanel;
import gov.noaa.pmel.tmap.las.client.inventory.InventoryMap;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

public class Inventory implements EntryPoint {

	ClientFactory clientFactory = GWT.create(ClientFactory.class);
	EventBus eventBus = clientFactory.getEventBus();
	InventoryMap mapUI;
	CategorySerializable clickedCategory = null;

	MapWidget iAmNull = null;
	
	@Override
	public void onModuleLoad() {
		mapUI = new InventoryMap();
		RootPanel.get("main").add(mapUI);
		Util.getRPCService().getCategories(null, null, categoryCallback);
		eventBus.addHandler(WidgetSelectionChangeEvent.TYPE, new WidgetSelectionChangeEvent.Handler() {

			@Override
			public void onAxisSelectionChange(WidgetSelectionChangeEvent event) {
				Object source = event.getSource();
				if ( source instanceof CheckBox ) {
					mapUI.turnOverlaysOnOff();

				} else if ( source instanceof InventoryDatasetPanel ) {
					InventoryDatasetPanel panel = (InventoryDatasetPanel) source;
					Set<String> keys = panel.getCategoryKeySet();
					for (Iterator keyIt = keys.iterator(); keyIt.hasNext();) {
						String id = (String) keyIt.next();
						Rectangle rectangle = (Rectangle) panel.getRectangle(id);
						rectangle.setMap(null);
						List<Polyline> polyline = panel.getPolyline(id);
						if ( polyline != null) {
							for (int p = 0; p < polyline.size(); p++ ) {
								Polyline ppp = polyline.get(p);
								ppp.setMap(null);
							}
						}
						MarkerClusterer clusterer = panel.getMarkerClusterer(id);
						if ( clusterer !=  null ) {
							
//							List<Marker> mList = (List<Marker>) panel.getMarkers(id);
//							for(int midx = 0; midx < mList.size(); midx++ ) {
//								Marker mk = mList.get(midx);
//								mk.setMap(iAmNull);
//								clusterer.removeMarker(mk);
//							}
							clusterer.clearMarkers();
							clusterer.setMap(iAmNull);
						}

					}
					mapUI.removePanel(panel);
				} else if ( source instanceof Breadcrumb ) {
					Breadcrumb bc = (Breadcrumb) source;
					clickedCategory = null;
					mapUI.removeBreadcrumbs(bc);
					String catid = bc.getLasid();
					Util.getRPCService().getCategories(catid, catid, categoryCallback);
				}
			}
		});
		mapUI.getMap().addResizeHandler(new ResizeMapHandler() {

			@Override
			public void onEvent(ResizeMapEvent event) {
				mapUI.setMapSizeToWindow();				
			}

			
		});
	}
	AsyncCallback categoryCallback = new AsyncCallback() {
		public void onSuccess(Object result) {
			CategorySerializable[] cats = (CategorySerializable[]) result;
			if ( clickedCategory != null ) {
				final Breadcrumb crumb = new Breadcrumb(clickedCategory.getName(), clickedCategory.getID());
				mapUI.addCrumb(crumb);
				crumb.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), crumb);		
						mapUI.selectLayers();
						Util.getRPCService().getCategories(crumb.getLasid(), crumb.getLasid(), categoryCallback);
					}
				});

			}
			if ( cats != null && cats.length > 0 ) {  	
				// Make a panel in case we need it.
				InventoryDatasetPanel panel = null;
				for ( int i = 0; i < cats.length; i++ ) {
					CategorySerializable cat = cats[i];
					if ( cat.isCategoryChildren() ) {
						if ( i == 0 ) mapUI.clearItems();
						final CategoryItem item = new CategoryItem(cat);

						item.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								// Write the current item to the breadcrumb (need an event on it to be able to go back).
								clickedCategory = item.getCategory();
								Util.getRPCService().getCategories(item.getCategory().getID(), item.getCategory().getID(), categoryCallback);

							}

						});
						mapUI.addItem(item);

					} else if ( cat.isVariableChildren() ) {

						if ( i == 0 ) {
							panel = new InventoryDatasetPanel();
							panel.setHeading(clickedCategory.getName());
							mapUI.addVariable(panel);
							mapUI.selectControls();
							mapUI.removeLastCrumb();
							
						}
						panel.addCatalog(cat.getID(), cat);
						
						String html = cat.getName() + "<a target=\"_blank\" href=\"UI.vm?catid="+cat.getID()+"\"> (View in LAS)</a>";						
						final CheckBox checkBox = new CheckBox(html, true);
						
						checkBox.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(true), checkBox);

							}

						});
						
						checkBox.setName(cat.getID());
						panel.addCheckBox(checkBox);
						Util.getRPCService().getCategories(cat.getID(), cat.getID(), variableCategoryCallback);

					}
				}
			}
		}

		@Override
		public void onFailure(Throwable arg0) {
			Window.alert("We didn't find any data sets to inventory.");
		}
	};
	AsyncCallback variableCategoryCallback = new AsyncCallback() {

		@Override
		public void onFailure(Throwable error) {

			Window.alert("Unable to get variable information.");

		}

		@Override
		public void onSuccess(Object result) {
			CategorySerializable[] cats = (CategorySerializable[]) result;
			if ( cats != null && cats.length > 0 ) {
				CategorySerializable c = cats[0];
				DatasetSerializable d = c.getDatasetSerializable();
				VariableSerializable v = d.getVariablesSerializable()[0];
				plotRange(c.getID(), d.getID(), v.getID());
				plotDataType(c.getID(), v);
			}
		}

	};
	private void plotRange(String catid, String dsid, String varid) {
		Util.getRPCService().getConfig(null, catid, dsid, varid, getConfigCallback);
	}

	AsyncCallback<ConfigSerializable> getConfigCallback = new AsyncCallback<ConfigSerializable>() {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Could not get grid for new variable." + caught.toString());

		}

		@Override
		public void onSuccess(ConfigSerializable config) {

			GridSerializable grid = config.getGrid();

			// This will become a custom object with the variable, the rectangle and the specialized overlay for the grid type.
			RectangleOptions rectangleOptions = RectangleOptions.newInstance();


			String catid = config.getCategorySerializable().getID();
			String color = color(catid);
			rectangleOptions.setFillColor(color);
			rectangleOptions.setFillOpacity(0.0d);
			rectangleOptions.setStrokeColor(color);
			rectangleOptions.setStrokeOpacity(1.0d);

			String slat = grid.getYAxis().getLo();
			String wlng = grid.getXAxis().getLo();

			String nlat = grid.getYAxis().getHi();
			String elng = grid.getXAxis().getHi();

			double east = Double.valueOf(elng);
			double west = Double.valueOf(wlng);
			double north = Double.valueOf(nlat);
			double south = Double.valueOf(slat);



			LatLng sw = LatLng.newInstance(south, west);
			LatLng ne = LatLng.newInstance(north, east);

			LatLngBounds bounds = LatLngBounds.newInstance(sw, ne);
		

			rectangleOptions.setBounds(bounds);
			rectangleOptions.setMap(mapUI.getMap());

			Rectangle rectangle = Rectangle.newInstance(rectangleOptions);
			mapUI.extend(sw);
			mapUI.extend(ne);
			mapUI.addRectangleToPanel(catid, rectangle);
			mapUI.getMap().getOverlayMapTypes().setAt(mapUI.getMap().getOverlayMapTypes().getLength()+1, rectangle);
			mapUI.zoomToBounds();

		}
	};
	private void plotDataType(String catid, VariableSerializable variable) {
		String grid_type = variable.getAttributes().get("grid_type");
		if ( grid_type.equals("point") ) {
			Util.getRPCService().getERDDAPGeometry(catid, variable.getDSID(), variable.getID(), "latitude,longitude", locationCallback);
		} else if ( grid_type.equals("trajectory") ) {
			Util.getRPCService().getERDDAPGeometry(catid, variable.getDSID(), variable.getID(), "latitude,longitude,time", trajectoryCallback);
		} else if ( grid_type.equals("profile") ) {
			Util.getRPCService().getERDDAPGeometry(catid, variable.getDSID(), variable.getID(), "latitude,longitude", locationCallback);
		} else if ( grid_type.equals("timeseries") ) {
			Util.getRPCService().getERDDAPGeometry(catid, variable.getDSID(), variable.getID(), "latitude,longitude", locationCallback);
		}
		//
	}
	AsyncCallback<String> locationCallback = new AsyncCallback<String>() {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Could not plot data for variable." + caught.toString());

		}

		@Override
		public void onSuccess(String json) {

			JSONValue jsonV = JSONParser.parseStrict(json);
			JSONObject jsonO = jsonV.isObject();
			MarkerClustererOptions clusterOptions = MarkerClustererOptions.newInstance();
			clusterOptions.setIgnoreHidden(true);
			MarkerClusterer cluster = MarkerClusterer.newInstance(mapUI.getMap(), clusterOptions);
			List<LatLng> locations = new ArrayList<LatLng>();
			if ( jsonO != null) {
				JSONObject table = (JSONObject) jsonO.get("table");
				JSONValue id = (JSONValue) table.get("catid");
				String catid = id.toString().trim().replace("\"", "");
				JSONArray names = (JSONArray) table.get("columnNames");
				JSONArray rows = (JSONArray) table.get("rows");

				int index = 0;
				for(int i = 1; i < rows.size(); i++) {
					JSONArray row = (JSONArray) rows.get(i);
					String latitude = row.get(0).toString();
					String longitude = row.get(1).toString();
					if ( latitude != null && longitude != null & !latitude.equals("null") && !longitude.equals("null")) {
						LatLng p = LatLng.newInstance(Double.valueOf(latitude), Double.valueOf(longitude));
						MarkerOptions options = MarkerOptions.newInstance();
						options.setPosition(p);
						options.setMap(mapUI.getMap());
						Marker marker = Marker.newInstance(options);
						locations.add(p);
						cluster.addMarker(marker);
					}
					index++;
				}

				mapUI.addMarkerClustererToPanel(catid, cluster);
				mapUI.addLocationsToPanel(catid, locations);
			}
		}
	};
	AsyncCallback<String> trajectoryCallback = new AsyncCallback<String>() {
		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Could not plot data for variable." + caught.toString());

		}

		@Override
		public void onSuccess(String json) {

			JSONValue jsonV = JSONParser.parseStrict(json);
			JSONObject jsonO = jsonV.isObject();
			if ( jsonO != null) {
				JSONObject table = (JSONObject) jsonO.get("table");
				JSONValue id = (JSONValue) table.get("catid");
				String catid = id.toString().trim().replace("\"", "");
				JSONArray names = (JSONArray) table.get("columnNames");
				JSONArray rows = (JSONArray) table.get("rows");

				MVCArray<LatLng> path = (MVCArray<LatLng>) MVCArray.createArray();
				
				List<MVCArray> paths = new ArrayList<MVCArray>();

                                int plen = 0;

				String prevID = null;
				int index = 0;
				for(int i = 1; i < rows.size(); i++) {
					JSONArray row = (JSONArray) rows.get(i);
 					String latitude = row.get(0).toString();
					String longitude = row.get(1).toString();
                                        // 2 is time
					String trajID = row.get(3).toString();
					if ( i == 1 ) prevID = trajID;
					if ( !trajID.equals(prevID) ) {
						// Start a new path
                                                MVCArray<LatLng> pc = (MVCArray<LatLng>) MVCArray.createArray();
                                                for ( int u = 0; u < plen; u++ ) {
                                                   pc.push(path.pop());
                                                }
                                                plen = 0;
						paths.add(pc);
						path = (MVCArray<LatLng>) MVCArray.createArray();
						prevID = trajID;
                                                
					}
					if ( latitude != null && longitude != null & !latitude.equals("null") && !longitude.equals("null")) {
						LatLng p = LatLng.newInstance(Double.valueOf(latitude), Double.valueOf(longitude));
						plen = path.push(p);
					}
					index++;
				}
                                // Add the path you were working on
                                paths.add(path);

				for ( int pc = 0; pc < paths.size(); pc++ ) {
					MVCArray<LatLng> pppath = paths.get(pc);
					PolylineOptions options = PolylineOptions.newInstance();
					options.setStrokeColor(color(catid));
					options.setStrokeOpacity(1.0d);
					options.setPath(pppath);
					options.setMap(mapUI.getMap());
					Polyline polyline = Polyline.newInstance(options);
					mapUI.getMap().getOverlayMapTypes().setAt(mapUI.getMap().getOverlayMapTypes().getLength()+1, polyline);
					mapUI.addPolylineToPanel(catid, polyline);
				}
			}
		}
	};
	private String color(String lasid) {
		int hash = 0;
		for (int i = 0; i < lasid.length(); i++) {
			hash = lasid.charAt(i) + ((hash << 5) - hash);
		}
		int r = (hash & 0xFF0000) >> 16;
			int g = (hash & 0x00FF00) >> 8;
		int b = hash & 0x0000FF;
		String ctext = "#" + hexPad(r) + hexPad(g) + hexPad(b);
		return ctext;
	}

	private String hexPad(int v) {
		String text = Integer.toHexString(v);
		if ( text.length() == 1 ) {
			text = "0" + text;
		}
		return text;
	}
}
