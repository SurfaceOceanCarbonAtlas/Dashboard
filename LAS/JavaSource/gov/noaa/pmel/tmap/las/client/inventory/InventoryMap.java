/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.inventory;

import java.util.List;

import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.ui.Breadcrumbs;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.Nav;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.TabListItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.events.center.CenterChangeMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.events.zoom.ZoomChangeMapHandler;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Polyline;
import com.google.gwt.maps.client.overlays.Rectangle;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClusterer;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.HtmlSanitizerUtil;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;


/**
 * @author rhs
 *
 */
public class InventoryMap extends Composite {

	interface InventoryMapBinder extends UiBinder<Widget, InventoryMap> {} {}
	private static InventoryMapBinder uiBinder = GWT.create(InventoryMapBinder.class);

	@UiField
	Breadcrumbs breadcrumbs;

	@UiField
	Breadcrumb home;

	@UiField
	Nav nav;

	@UiField
	Panel mapcanvas;
	//
	@UiField
	TabListItem controlsTabItem;

	@UiField
	TabListItem layersTabItem;

	@UiField
	NavbarBrand navbarheader;

	@UiField
	NavbarBrand navbarfooter;

	@UiField
	Column mapcolumn;
	@UiField
	Column navcolumn;

	@UiField
	PanelGroup variablesPanel;

	@UiField
	LinkedGroup layersTab;

	//    @UiField
	//    Slider loopSpeed;

	@UiField
	Icon spinner;


	//    @UiField
	//    Breadcrumb home;


	int h_fudge_factor = 40;
	int h_fixed_width = 360;
	int v_fixed_height = 190;
	int v_fudge_factor = 130;

	// Includes fudge factor...
	int fudge_for_ad = 240;

	int fudge_for_controls = 160;

	//TODO figure out search

	private MapOptions mapOptions;
	private MapWidget map;
	
	MapWidget iAmNull = null;

	ClientFactory clientFactory = GWT.create(ClientFactory.class);
	EventBus eventBus = clientFactory.getEventBus();

	Widget root;
	
	LatLngBounds zoomBounds;

	public InventoryMap() {

		root = uiBinder.createAndBindUi(this);
		initWidget(root);

		mapOptions = MapOptions.newInstance();
		mapOptions.setMaxZoom(11);
		mapOptions.setMinZoom(2);
		mapOptions.setZoom(1);
		mapOptions.setStreetViewControl(false);
		mapOptions.setMapTypeId(MapTypeId.TERRAIN);
		mapOptions.setCenter(LatLng.newInstance(0.0, -97.583));
		map = new MapWidget(mapOptions);
		setMapSizeToWindow();
		// loopSpeed.addSlideStopHandler(slideStop);
		mapcanvas.add(map);
		addLayerShowHandler(new TabShowHandler() {
			@Override
			public void onShow(TabShowEvent event) {
				// Nothing to do do now...
			}
		});

		home.setLasid(null);
		home.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectLayers();
				eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false), home);

			}

		});
		RequestBuilder sendHeaderRequest = new RequestBuilder(RequestBuilder.GET, UriUtils.sanitizeUri("productserver/templates/InvHeader.vm"));
		try {
			sendHeaderRequest.sendRequest(null, headerHTMLCallback);
		} catch (RequestException e) {
			e.printStackTrace();
		}
		RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, UriUtils.sanitizeUri("productserver/templates/V7UIFooter.vm"));
		try {
			sendRequest.sendRequest(null, footerHTMLCallback);
		} catch (RequestException e) {
			e.printStackTrace();
		}

	}
	RequestCallback headerHTMLCallback = new RequestCallback() {

		@Override
		public void onError(Request request, Throwable response) {
			
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			String text = response.getText();
			HTML html = new HTML(HtmlSanitizerUtil.sanitizeHtml(text));
			navbarheader.clear();
			navbarheader.add(html);
		}
		
	};
	RequestCallback footerHTMLCallback = new RequestCallback() {

		@Override
		public void onError(Request request, Throwable response) {
			
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			String text = response.getText();
			HTML html = new HTML(HtmlSanitizerUtil.sanitizeHtml(text));
			navbarfooter.clear();
			navbarfooter.add(html);
		}
		
	};
	public void addCenterHandeler(CenterChangeMapHandler handler) {
		map.addCenterChangeHandler(handler);
	}
	public void addZoomHandler(ZoomChangeMapHandler handler) {
		map.addZoomChangeHandler(handler);
	}
	public void addDragEndHandler(DragEndMapHandler handler) {
		map.addDragEndHandler(handler);
	}
	public void clearMaps() {
		if ( map.getOverlayMapTypes() != null ) {
			for ( int i = map.getOverlayMapTypes().getLength() - 1 ; i >= 0 ; i-- ) {
				map.getOverlayMapTypes().removeAt(i);
				map.getOverlayMapTypes().insertAt(i, null);
			}
		}
	}

	public void setMapSizeToWindow() {
		int h = Window.getClientHeight();
		int w = Window.getClientWidth();
		int navwidth = navcolumn.getOffsetWidth();
		if ( navwidth == 0 ) {
			navwidth = navwidth + h_fixed_width;
		}
		w = w - (navwidth + h_fudge_factor);
		int navheight = nav.getOffsetHeight();
		if ( navheight == 0 ) {
			navheight = navheight + v_fixed_height;
		} else {
			navheight = navheight + v_fudge_factor;
		}
		h = h - navheight;
		map.setSize(w + "px", h + "px");
		map.triggerResize();
		int navHeight = Window.getClientHeight() - navbarheader.getOffsetHeight() - navbarfooter.getOffsetHeight() - fudge_for_ad;
		layersTab.setHeight(navHeight+"px");

	}
	public int getZoom() {
		return map.getZoom();
	}
	public void setZoom(int zoom) {
		map.setZoom(zoom);
	}
	public LatLng getCenter() {
		return map.getCenter();
	}
	public void setCenter(LatLng latlon) {
		map.setCenter(latlon);
	}
	public void clearItems() {
		layersTab.clear();
	}
	public void addItem(Widget dataitem) {
		layersTab.add(dataitem);   
	}

	public int getItemCount() {
		return layersTab.getWidgetCount();
	}
	public MapWidget getMap() {
		return map;
	}

	public void addVariable(Widget panel) {
		selectControls();
		variablesPanel.add(panel);
		int i0 = Window.getClientHeight();
		int i1 = navbarheader.getOffsetHeight();
		int i2 = navbarfooter.getOffsetHeight();
		int varHeight =  i0 - i1 - i2 - fudge_for_controls;
		variablesPanel.setHeight(varHeight+"px");
	}


	public void selectControls() {
		controlsTabItem.showTab();
	}
	public void selectLayers() {
		layersTabItem.showTab();
	}



	public void showSpinner() {
		spinner.setVisible(true);
	}

	public void hideSpinner() {
		spinner.setVisible(false);
	}


	public void addLayerShowHandler(TabShowHandler layersShowHandler) {
		layersTabItem.addShowHandler(layersShowHandler);
	}

	public void addCrumb(Breadcrumb crumb) {
		breadcrumbs.add(crumb);
	}

	public void removeLastCrumb() {
		breadcrumbs.remove(breadcrumbs.getWidgetCount() -1);
	}
	public Rectangle getRectangle(String id) {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);

			CategorySerializable cc = panel.getCategory(id);
			String pid = "";
			if ( cc != null ) {
				pid = cc.getID();
			}
			if ( pid.equals(id) ) {
				return panel.getRectangle(id);
			}

		}
		return null;
	}
	public void turnOverlaysOnOff() {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {    		
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);
			for (int j = 0; j < panel.getCheckBoxCount(); j++ ) {
				String catid = panel.getCheckBox(j).getName();
				if ( panel.getCheckBox(j).getValue() ) {
					Rectangle rectangle = (Rectangle) panel.getRectangle(catid);
					rectangle.setMap(map);

					List<Polyline> p = panel.getPolyline(catid);
					if ( p != null ) {
						for ( int pp = 0; pp < p.size(); pp++ ) {
							Polyline pl = p.get(pp);
							pl.setMap(map);
						}
					}
					MarkerClusterer clusterer = panel.getMarkerClusterer(catid);
					
					if ( clusterer != null && clusterer.getMarkers() != null && clusterer.getMarkers().length() == 0 ) {
						// Should have markers on the map, but the size is 0. Add them back.
						// Some panels may not have been switched off (length > 0) so don't add them.
						List<LatLng> markers = panel.getMarkerLocations(catid);
						for (int midx = 0; midx < markers.size(); midx++ ) {						
							MarkerOptions options = MarkerOptions.newInstance();
							options.setPosition(markers.get(midx));
							options.setMap(map);
							Marker marker = Marker.newInstance(options);
							clusterer.addMarker(marker);
						}					
					}
				} else {
					Rectangle rectangle = (Rectangle) panel.getRectangle(catid);
					rectangle.setMap(iAmNull);
					List<Polyline> poly = panel.getPolyline(catid);
					if ( poly != null ) {
						for ( int p = 0; p < poly.size(); p++ ) {
							Polyline pl = poly.get(p);
							pl.setMap(iAmNull);
						}
					}
					MarkerClusterer clusterer = panel.getMarkerClusterer(catid);
					if ( clusterer != null ) {
						clusterer.clearMarkers();
					}
				}
			}
		}

	}

	public void removeBreadcrumbs(Breadcrumb bc) {
		int index = breadcrumbs.getWidgetIndex(bc);
		for (int i = breadcrumbs.getWidgetCount() - 1; i > index; i--) {
			breadcrumbs.remove(i);
		}
	}
	public void removePanel(Widget panel) {
		variablesPanel.remove(panel);
		if ( variablesPanel.getWidgetCount() == 0 ) {
			selectLayers();
			zoomBounds = LatLngBounds.newInstance(LatLng.newInstance(0, 0), LatLng.newInstance(0, 0));
			zoomToBounds();
			map.setZoom(1);
			zoomBounds = null;
		}
	}
	public void addLocationsToPanel(String id, List<LatLng> markers) {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);
			CategorySerializable cc = panel.getCategory(id);
			String pid = "";
			if ( cc != null ) {
				pid = cc.getID();
			}
			if ( pid.equals(id) ) {
				panel.addMarkerLocations(id, markers);
			}
		}
	}
	public void addPolylineToPanel(String id, Polyline polyline) {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);
			CategorySerializable cc = panel.getCategory(id);
			String pid = "";
			if ( cc != null ) {
				pid = cc.getID();
			}
			if ( pid.equals(id) ) {
				panel.addPolyline(id, polyline);
			}

		}

	}
	public void addMarkerClustererToPanel(String id, MarkerClusterer clusterer) {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);
			CategorySerializable cc = panel.getCategory(id);
			String pid = "";
			if ( cc != null ) {
				pid = cc.getID();
			}
			if ( pid.equals(id) ) {
				panel.addMarkerClusterer(id, clusterer);
			}

		}

	}
	public void addRectangleToPanel(String id, Rectangle overlay) {
		for(int i = 0; i < variablesPanel.getWidgetCount(); i++ ) {
			InventoryDatasetPanel panel = (InventoryDatasetPanel) variablesPanel.getWidget(i);
			CategorySerializable cc = panel.getCategory(id);
			String pid = "";
			if ( cc != null ) {
				pid = cc.getID();
			}
			if ( pid.equals(id) ) {
				panel.addRectangle(id, overlay);
			}
		}
	}
	public void extend(LatLng pt) {
		if ( zoomBounds == null ) {
			zoomBounds = LatLngBounds.newInstance(pt, pt);
		}
		zoomBounds.extend(pt);
	}
	public void zoomToBounds() {
		map.fitBounds(zoomBounds);
	}
}
