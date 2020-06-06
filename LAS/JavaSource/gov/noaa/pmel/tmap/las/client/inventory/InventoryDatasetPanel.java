package gov.noaa.pmel.tmap.las.client.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelFooter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.overlays.Polyline;
import com.google.gwt.maps.client.overlays.Rectangle;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClusterer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
public class InventoryDatasetPanel extends Composite {

	private static InventoryDatasetPanelUiBinder uiBinder = GWT.create(InventoryDatasetPanelUiBinder.class);

	@UiField
	Panel panel;
	@UiField
	Heading heading;
	@UiField
	PanelBody body;
	@UiField
	PanelFooter footer;
	@UiField
	Icon close;
	
	Map<String, CategorySerializable> categories = new HashMap<String, CategorySerializable>();
	Map<String, Rectangle> rectangles = new HashMap<String, Rectangle>();
// The Poly line needs to be a List<Polyline>
	Map<String, List<Polyline>> polylines = new HashMap<String, List<Polyline>>();
	Map<String, MarkerClusterer> markerClusters = new HashMap<String, MarkerClusterer>();
	Map<String, List<LatLng>> markerLocations = new HashMap<String, List<LatLng>>();
	ClientFactory clientFactory = GWT.create(ClientFactory.class);
	EventBus eventBus = clientFactory.getEventBus();
	
	interface InventoryDatasetPanelUiBinder extends UiBinder<Widget, InventoryDatasetPanel> {
	}

	public InventoryDatasetPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(true), InventoryDatasetPanel.this);
				
			}
			
		});
	}

	public void setHeading(String text) {
		heading.setText(text);
		panel.setTitle(text);
	}
	public void addCheckBox(CheckBox checkBox) {
		body.add(checkBox);		
		checkBox.setValue(true);
	}
	public void addCatalog(String id, CategorySerializable category) {
		categories.put(id, category);
	}
	public void addMarkerLocations(String id, List<LatLng> m) {
		markerLocations.put(id, m);
	}
	public void addRectangle(String id, Rectangle rectangle) {
		rectangles.put(id, rectangle);
	}
	public void addPolyline(String id, Polyline polyline) {
		List<Polyline> polys = polylines.get(id);
		if ( polys == null ) {
			polys = new ArrayList<Polyline>();
		}
		polys.add(polyline);
		polylines.put(id, polys);
	}
	public void addMarkerClusterer(String id, MarkerClusterer clusterer) {
		markerClusters.put(id, clusterer);
	}
	public int getCheckBoxCount() {
		return body.getWidgetCount();
	}
	public Set<String> getCategoryKeySet() {
		return categories.keySet();
	}
	public CategorySerializable getCategory(String id) {
		return categories.get(id);
	}
	public Rectangle getRectangle(String id) {
		return rectangles.get(id);
	}
	public CheckBox getCheckBox(int i) {
		return (CheckBox) body.getWidget(i);
	}
	public MarkerClusterer getMarkerClusterer(String id) {
		return markerClusters.get(id);
	}
	public List<Polyline> getPolyline(String id) {
		return polylines.get(id);
	}
	public List<LatLng> getMarkerLocations(String id) {
		return markerLocations.get(id);
	}
	public int getOverlayCount() {
		return rectangles.size();
	}
	public void setFooter(Widget foot) {
		footer.clear();
		footer.add(foot);
	}
}
