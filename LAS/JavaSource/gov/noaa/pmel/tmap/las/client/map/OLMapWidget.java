/**
 * This software was developed by Roland Schweitzer of Weathertop Consulting, LLC 
 * (http://www.weathertopconsulting.com/) as part of work performed for
 * NOAA Contracts AB113R-04-RP-0068 and AB113R-09-CN-0182.  
 * 
 * The NOAA licensing terms are explained below.
 * 
 * 
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.map;

import java.util.Locale;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.FeatureModifiedEvent;
import gov.noaa.pmel.tmap.las.client.event.MapChangeEvent;
import gov.noaa.pmel.tmap.las.client.openlayers.DrawSingleFeature;
import gov.noaa.pmel.tmap.las.client.openlayers.DrawSingleFeature.FeatureAddedListener;
import gov.noaa.pmel.tmap.las.client.openlayers.DrawSingleFeatureOptions;
import gov.noaa.pmel.tmap.las.client.openlayers.HorizontalPathHandler;
import gov.noaa.pmel.tmap.las.client.openlayers.JumpPathHandler;
import gov.noaa.pmel.tmap.las.client.openlayers.VerticalPathHandler;
import gov.noaa.pmel.tmap.las.client.serializable.RegionSerializable;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.ArgParser;
import org.gwtopenmaps.openlayers.client.control.Attribution;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature.OnModificationEndListener;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature.OnModificationListener;
import org.gwtopenmaps.openlayers.client.control.ModifyFeature.OnModificationStartListener;
import org.gwtopenmaps.openlayers.client.control.ModifyFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.Navigation;
import org.gwtopenmaps.openlayers.client.event.MapClickListener;
import org.gwtopenmaps.openlayers.client.event.MapMoveListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.handler.PointHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandler;
import org.gwtopenmaps.openlayers.client.handler.RegularPolygonHandlerOptions;
import org.gwtopenmaps.openlayers.client.layer.ArcGIS93Rest;
import org.gwtopenmaps.openlayers.client.layer.Boxes;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;
import org.gwtopenmaps.openlayers.client.marker.Box;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OLMapWidget extends Composite {
	private double epsilon = .001;
	private String tool = "";
	private DockPanel dockPanel;
	private MapOptions wmsMapOptions;
	private MapOptions wrapMapOptions;
	private MapOptions mapOptions;
	private Map map;
	private WMS wmsLayer;
	private Vector boxLayer;

	// Add a separate layer for the lines so that the they can have their own
	// style.
	private Vector lineLayer;

	private Vector wrapLayer;

	private Bounds wmsExtent;
	private Bounds wrapExtent;
	private Bounds currentSelection = new Bounds(-180, -90, 180, 90);
	private Bounds lastRectangle = new Bounds(-180, -90, 180, 90);
	private Bounds dataBounds;

	private RegularPolygonHandler regularPolygonHandler;
	private DrawSingleFeatureOptions drawSingleFeatureOptionsForRectangle;
	private RegularPolygonHandlerOptions regularPolygonHandlerOptions;
	private DrawSingleFeature drawRectangle;

	private RegularPolygonHandlerOptions pathHandlerOptions;
	private DrawSingleFeatureOptions drawSingleFeatureOptionsForLines;

	private DrawSingleFeature drawXLine;
	private DrawSingleFeature drawXPoint;
	private DrawSingleFeature drawYLine;
	private DrawSingleFeature drawYPoint;
	private DrawSingleFeature drawPoint;

	private ModifyFeature modifyFeatureXY;
	private ModifyFeature modifyFeatureLine;

	private RegionWidget regionWidget = new RegionWidget();
	private LatLonWidget textWidget = new LatLonWidget();
	private Image helpButtonUp;
	private Image helpButtonDown;
	private ToggleButton helpButton;
	private Image resetButtonUp;
	private Image resetButtonDown;
	private PushButton resetButton;
	private Image drawButtonUp;
	private Image drawButtonDown;
	private ToggleButton drawButton;
	private Image panButtonUp;
	private Image panButtonDown;
	private ToggleButton panButton;
	private Image editButtonUp;
	private Image editButtonDown;
	private ToggleButton editButton;
	private Image zoomInButtonUp;
	private Image zoomInButtonDown;
	private PushButton zoomInButton;
	private Image zoomOutButtonUp;
	private Image zoomOutButtonDown;
	private PushButton zoomOutButton;
	private Image zoomFullButtonUp;
	private Image zoomFullButtonDown;
	private PushButton zoomFullButton;

	private HorizontalPanel buttonPanel;
	private PopupPanel regionPanel;
	private FlowPanel regionInterior;
	private Image regionOpenUp;
	private Image regionOpenDown;
	private ToggleButton regionButton;
	private Image regionCloseUp;
	private Image regionCloseDown;
	private PushButton regionClose;
	private PopupPanel helpPanel;
	private FlowPanel helpInterior;
	private Image helpCloseUp;
	private Image helpCloseDown;
	private PushButton helpClose;
	private Frame help;

	private boolean modulo = true;
	private boolean selectionMade = false;
	private boolean drawing = false;

	Boxes boxes = new Boxes("Valid Region");
	Box box = null;
	double delta;

	boolean editing = false;

	// Explicitly construct and add the controls we want instead of the
	// defaults.
	private Navigation navControl;
	private ArgParser argParser;
	private Attribution attribControl;

	private MapSelectionChangeListener mapListener;
	// public static final String WMS_URL =
	// "http://strider.weathertopconsulting.com:8282/geoserver/wms?";
	// public static final String WMS_URL =
	// "http://labs.metacarta.com/wms/vmap0";
	// private final static String WMS_URL ="http://www.gebco.net/data_and_products/gebco_web_services/web_map_service/mapserv";
	// private final static String WMS_URL ="http://data.nodc.noaa.gov/arcgis/rest/services/NODC.Ocean.basemap1007/MapServer/WMSServer";
	
	private final static String WMS_URL = "http://vmap0.tiles.osgeo.org/wms/vmap0";
	private final static String WMS_LAYER = "basic";
	private ClientFactory clientFactory = GWT.create(ClientFactory.class);
	private EventBus eventBus;

	private final OLMapWidget thisMapWidget;

	public OLMapWidget() {
		thisMapWidget = this;
		init("128px", "256px", WMS_URL, WMS_LAYER);
	}

	public OLMapWidget(String height, String width) {
		thisMapWidget = this;
		init(height, width, WMS_URL, WMS_LAYER);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public OLMapWidget(String height, String width, String tile_url, String layer) {
		thisMapWidget = this;
		init(height, width, tile_url, layer);
	}

	private void init(String height, String width, String tile_url, String layer) {

		eventBus = clientFactory.getEventBus();

		regionWidget.setChangeListener(regionChangeListener);
		textWidget.addSouthChangeListener(southChangeListener);
		textWidget.addNorthChangeListener(northChangeListener);
		textWidget.addEastChangeListener(eastChangeListener);
		textWidget.addWestChangeListener(westChangeListener);

		helpCloseUp = new Image(GWT.getModuleBaseURL()
				+ "../images/close_off.png");
		helpCloseDown = new Image(GWT.getModuleBaseURL()
				+ "../images/close_on.png");

		dockPanel = new DockPanel();
		helpPanel = new PopupPanel();
		helpInterior = new FlowPanel();
		buttonPanel = new HorizontalPanel();

		helpButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/info_off.png");
		helpButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/info_on.png");
		helpButton = new ToggleButton(helpButtonUp, helpButtonDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (helpButton.isDown()) {
							helpPanel.setPopupPosition(
									helpButton.getAbsoluteLeft() + 256,
									helpButton.getAbsoluteTop() + 20);
							helpPanel.show();
						} else {
							helpPanel.hide();
						}
					}
				});
		helpButton.setTitle("Help");
		helpButton.setStylePrimaryName("OL_MAP-PushButton");

		helpClose = new PushButton(helpCloseUp, helpCloseDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						helpPanel.hide();
						helpButton.setDown(false);
					}

				});
		helpClose.setTitle("Close");
		helpClose.setStylePrimaryName("OL_MAP-PushButton");
		helpClose.addStyleName("OL_MAP-CloseButton");

		regionCloseUp = new Image(GWT.getModuleBaseURL()
				+ "../images/close_off.png");
		regionCloseDown = new Image(GWT.getModuleBaseURL()
				+ "../images/close_on.png");
		regionClose = new PushButton(regionCloseUp, regionCloseDown,
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						regionPanel.hide();
						regionButton.setDown(false);
					}

				});
		regionClose.setStylePrimaryName("OL_MAP-PushButton");
		regionClose.addStyleName("OL_MAP-CloseButton");

		regionPanel = new PopupPanel();
		regionInterior = new FlowPanel();
		regionInterior.add(regionClose);
		regionInterior.add(regionWidget);
		regionPanel.add(regionInterior);

		regionOpenUp = new Image(GWT.getModuleBaseURL()
				+ "../images/menu_off.png");
		regionOpenDown = new Image(GWT.getModuleBaseURL()
				+ "../images/menu_on.png");
		regionButton = new ToggleButton(regionOpenUp, regionOpenDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (regionButton.isDown()) {
							regionPanel.setPopupPosition(
									regionButton.getAbsoluteLeft() + 60,
									helpButton.getAbsoluteTop() + 20);
							regionPanel.show();
						} else {
							regionPanel.hide();
						}
					}
				});
		regionButton.setTitle("Select Named Region");
		regionButton.setStylePrimaryName("OL_MAP-ToggleButton");

		resetButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/reset_off.png");
		resetButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/reset_on.png");
		resetButton = new PushButton(resetButtonUp, resetButtonDown,
				new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						boxLayer.destroyFeatures();
						lineLayer.destroyFeatures();
						editing = false;
						setDataExtent(dataBounds.getLowerLeftY(),
								dataBounds.getUpperRightY(),
								dataBounds.getLowerLeftX(),
								dataBounds.getUpperRightX());
						if (mapListener != null) {
							mapListener.onFeatureChanged();
						}
					}

				});
		resetButton.setTitle("Reset Map");
		resetButton.setStylePrimaryName("OL_MAP-PushButton");
		help = new Frame(GWT.getModuleBaseURL() + "../css/maphelp.html");
		help.setHeight("300px");
		helpInterior.add(helpClose);
		helpInterior.add(help);
		helpPanel.add(helpInterior);
		drawButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/draw_off.png");
		drawButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/draw_on.png");
		drawButton = new ToggleButton(drawButtonUp, drawButtonDown,
				drawButtonClickHandler);
		drawButton.setTitle("Select Region with Click and Drag");
		drawButton.setStylePrimaryName("OL_MAP-ToggleButton");
		panButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/pan_off.png");
		panButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/pan_on.png");
		panButton = new ToggleButton(panButtonUp, panButtonDown,
				panButtonClickHandler);
		panButton.setTitle("Pan Map");
		panButton.setStylePrimaryName("OL_MAP-ToggleButton");
		panButton.setDown(true);
		editButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/edit_off.png");
		editButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/edit_on.png");
		editButton = new ToggleButton(editButtonUp, editButtonDown,
				editButtonClickHandler);
		editButton.setTitle("Click Selection to Edit");
		editButton.setStylePrimaryName("OL_MAP-PushButton");
		zoomInButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_in_off.png");
		zoomInButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_in_on.png");
		zoomInButton = new PushButton(zoomInButtonUp, zoomInButtonDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						int zoom = map.getZoom();
						zoom = zoom + 1;
						zoom = zoom % 16; // Hack. Should implement
											// getNumZoomLevels for the map.
						map.zoomTo(zoom);
						panMapToSelection();
					}
				});
		zoomInButton.setStylePrimaryName("OL_MAP-PushButton");
		zoomInButton.setTitle("Zoom In");

		zoomOutButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_out_off.png");
		zoomOutButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_out_on.png");
		zoomOutButton = new PushButton(zoomOutButtonUp, zoomOutButtonDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						int zoom = map.getZoom();
						zoom = zoom - 1;
						if (zoom < 0)
							zoom = 0;
						map.zoomTo(zoom);
						panMapToSelection();
					}
				});
		zoomOutButton.setStylePrimaryName("OL_MAP-PushButton");
		zoomOutButton.setTitle("Zoom Out");

		zoomFullButtonUp = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_out_full_off.png");
		zoomFullButtonDown = new Image(GWT.getModuleBaseURL()
				+ "../images/zoom_out_full_on.png");
		zoomFullButton = new PushButton(zoomFullButtonUp, zoomFullButtonDown,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						zoomOutAndPanToSelection();
					}
				});
		zoomFullButton.setStylePrimaryName("OL_MAP-PushButton");
		zoomFullButton.setTitle("Zoom Out to Full Extent");

		buttonPanel.add(helpButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(editButton);
		buttonPanel.add(panButton);
		buttonPanel.add(drawButton);
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomFullButton);
		buttonPanel.add(zoomOutButton);
		buttonPanel.add(regionButton);

		wmsExtent = new Bounds(-180, -90, 180, 90);
		wrapExtent = new Bounds(-540, -90, 540, 90);
		dataBounds = wmsExtent;
		wmsMapOptions = new MapOptions();
		wmsMapOptions.setMaxExtent(wmsExtent);
		wmsMapOptions.setRestrictedExtent(wmsExtent);
		wrapMapOptions = new MapOptions();
		wrapMapOptions.setMaxExtent(wrapExtent);
		wrapMapOptions.setRestrictedExtent(wrapExtent);
		VectorOptions lineOptions = new VectorOptions();
		Style defaultStyle = new Style();
		defaultStyle.setStrokeWidth(4);
		Style selectedStyle = new Style();
		// Ripped from the OL source. Bad I know, but ...
		// border: 2px solid blue;
		// position: absolute;
		// background-color: white;
		// opacity: 0.50;
		// font-size: 1px;
		// filter: alpha(opacity=50);
		selectedStyle.setStrokeWidth(2);
		selectedStyle.setStrokeColor("blue");
		selectedStyle.setFillColor("blue");
		selectedStyle.setFillOpacity(.5);
		StyleMap styles = new StyleMap(defaultStyle, selectedStyle, new Style());
		lineOptions.setStyleMap(styles);
		boxLayer = new Vector("Box Layer");
		lineLayer = new Vector("Line Layer", lineOptions);
		VectorOptions wrapLayerOptions = new VectorOptions();
		wrapLayerOptions.setIsBaseLayer(true);
		wrapLayerOptions.setMaxExtent(wrapExtent);
		wrapLayer = new Vector("Wrap Layer", wrapLayerOptions);
		// Start with no controls on the map.
		wmsMapOptions.removeDefaultControls();
		MapWidget mapWidget = new MapWidget(width, height, wmsMapOptions);
		map = mapWidget.getMap();
		// Add a WMS layer for a little background
		map.addLayer(wrapLayer);
		
		if ( tile_url == null || (tile_url != null && tile_url.trim().equals("")) ) {
			tile_url = WMS_URL;
		}
		if ( layer == null || (layer != null && layer.trim().equals("")) ) {
			layer = WMS_LAYER;
		}
		setTileServer("Base Layer", tile_url, "image/png", layer);
		map.addLayer(boxLayer);
		map.addLayer(lineLayer);

		/*
		 * We removed the default controls... which are:
		 * 
		 * - OpenLayers.Control.Navigation<http://dev.openlayers.org/docs/files/
		 * OpenLayers/Control/Navigation-js.html#OpenLayers.Control.Navigation>
		 * - OpenLayers.Control.PanZoom<http://dev.openlayers.org/docs/files/
		 * OpenLayers/Control/PanZoom-js.html#OpenLayers.Control.PanZoom> -
		 * OpenLayers
		 * .Control.ArgParser<http://dev.openlayers.org/docs/files/OpenLayers
		 * /Control/ArgParser-js.html#OpenLayers.Control.ArgParser> -
		 * OpenLayers.
		 * Control.Attribution<http://dev.openlayers.org/docs/files/OpenLayers
		 * /Control/Attribution-js.html#OpenLayers.Control.Attribution>
		 * 
		 * Now we add back all of them except pan/zoom control. We don't want
		 * pan buttons, and we want our own zoom behavior.
		 */

		navControl = new Navigation();
		argParser = new ArgParser();
		attribControl = new Attribution();

		map.addControl(navControl);
		map.addControl(argParser);
		map.addControl(attribControl);

		drawSingleFeatureOptionsForRectangle = new DrawSingleFeatureOptions();
		regularPolygonHandlerOptions = new RegularPolygonHandlerOptions();
		regularPolygonHandlerOptions.setSides(4);
		regularPolygonHandlerOptions.setIrregular(true);
		// regularPolygonHandlerOptions.setKeyMask(Handler.MOD_CTRL);
		drawSingleFeatureOptionsForRectangle
				.setHandlerOptions(regularPolygonHandlerOptions);

		regularPolygonHandler = new RegularPolygonHandler();
		drawSingleFeatureOptionsForRectangle
				.onFeatureAdded(featureAddedListener);

		// The rectangle drawing control
		drawRectangle = new DrawSingleFeature(boxLayer, regularPolygonHandler,
				drawSingleFeatureOptionsForRectangle);

		pathHandlerOptions = new RegularPolygonHandlerOptions();
		// pathHandlerOptions.setKeyMask(Handler.MOD_CTRL);
		pathHandlerOptions.setIrregular(true);
		pathHandlerOptions.setSides(4);
		drawSingleFeatureOptionsForLines = new DrawSingleFeatureOptions();
		drawSingleFeatureOptionsForLines.onFeatureAdded(featureAddedListener);
		drawSingleFeatureOptionsForLines.setHandlerOptions(pathHandlerOptions);

		// The X-Line drawing control
		drawXLine = new DrawSingleFeature(lineLayer,
				new HorizontalPathHandler(), drawSingleFeatureOptionsForLines);

		// A tool to place a point that only moves in X
		drawXPoint = new DrawSingleFeature(boxLayer, new JumpPathHandler(),
				drawSingleFeatureOptionsForLines);

		// The Y-Line drawing control
		drawYLine = new DrawSingleFeature(lineLayer, new VerticalPathHandler(),
				drawSingleFeatureOptionsForLines);

		// A tool to place a point that only moves in Y
		drawYPoint = new DrawSingleFeature(boxLayer, new JumpPathHandler(),
				drawSingleFeatureOptionsForLines);

		// The Point drawing control
		drawPoint = new DrawSingleFeature(boxLayer, new PointHandler(),
				drawSingleFeatureOptionsForLines);

		// Setup for modifying an XY shape... Allows RESIZE, DRAG and RESHAPE...
		ModifyFeatureOptions modifyFeatureOptionsXY = new ModifyFeatureOptions();
		modifyFeatureOptionsXY.setDeleteCodes(new int[0]);
		modifyFeatureOptionsXY.setMode(ModifyFeature.RESIZE
				| ModifyFeature.DRAG | ModifyFeature.RESHAPE);
		OnModificationListener onModification = new OnModificationListener() {
			@Override
			public void onModification(VectorFeature vectorFeature) {
				Geometry geo = Geometry.narrowToGeometry(vectorFeature
						.getGeometry().getJSObject());
				trimSelection(geo.getBounds());
				selectionMade = true;
				eventBus.fireEventFromSource(new FeatureModifiedEvent(getYlo(),
						getYhi(), getXlo(), getXhi()), thisMapWidget);
			}

		};
		OnModificationStartListener onModificationStart = new OnModificationStartListener() {
			@Override
			public void onModificationStart(VectorFeature vectorFeature) {
				editing = true;
				drawing = false;
				drawButton.setDown(false);
				drawRectangle.deactivate();
				drawXLine.deactivate();
				drawXPoint.deactivate();
				drawYLine.deactivate();
				drawYPoint.deactivate();
				drawPoint.deactivate();
			}
		};
		OnModificationEndListener onModificationEnd = new OnModificationEndListener() {
			@Override
			public void onModificationEnd(VectorFeature vectorFeature) {
				editing = false;
			}
		};
		modifyFeatureOptionsXY.onModification(onModification);
		modifyFeatureOptionsXY.onModificationStart(onModificationStart);
		modifyFeatureOptionsXY.onModificationEnd(onModificationEnd);
		modifyFeatureXY = new ModifyFeature(boxLayer, modifyFeatureOptionsXY);

		// Setup for modifying line shape... Allows RESIZE, DRAG... RESHAPE is
		// not allowed
		ModifyFeatureOptions modifyFeatureOptionsLine = new ModifyFeatureOptions();
		modifyFeatureOptionsLine.setDeleteCodes(new int[0]);
		modifyFeatureOptionsLine.setMode(ModifyFeature.RESIZE
				| ModifyFeature.DRAG);
		modifyFeatureOptionsLine.onModification(onModification);
		modifyFeatureOptionsLine.onModificationStart(onModificationStart);
		modifyFeatureOptionsLine.onModificationEnd(onModificationEnd);
		modifyFeatureLine = new ModifyFeature(lineLayer,
				modifyFeatureOptionsLine);

		this.map.addControl(drawRectangle);
		this.map.addControl(drawXLine);
		this.map.addControl(drawXPoint);
		this.map.addControl(drawYLine);
		this.map.addControl(drawYPoint);
		this.map.addControl(drawPoint);
		this.map.addControl(modifyFeatureXY);
		this.map.addControl(modifyFeatureLine);

		try {
			drawRectangle.deactivate();
			drawXLine.deactivate();
			drawXPoint.deactivate();
			drawYLine.deactivate();
			drawYPoint.deactivate();
			drawPoint.deactivate();
			modifyFeatureXY.deactivate();
			modifyFeatureLine.deactivate();
		} catch (RuntimeException e) {
			// Catching this exception mainly to allow WindowBuilder to work
			// with fewer errors
			e.printStackTrace();
		}

		map.setCenter(new LonLat(0, 0), 0);
		map.setOptions(wrapMapOptions);
		map.addMapMoveListener(mapMoveListener);
		map.addMapClickListener(mapClickListener);
		dockPanel.add(buttonPanel, DockPanel.NORTH);
		dockPanel.add(mapWidget, DockPanel.CENTER);
		dockPanel.add(textWidget, DockPanel.SOUTH);
		initWidget(dockPanel);
	}

	public Map getMap() {
		return map;
	}

	public void setTileServer(String name, String url, String format, String layers) {
		if (wmsLayer != null) {
			map.removeLayer(wmsLayer);
		}
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat(format);
		wmsParams.setLayers(layers);
		WMSOptions wmsOptions = new WMSOptions();
		wmsOptions.setWrapDateLine(true);
		wmsOptions.setIsBaseLayer(false);
		if (url == null || url.equals("") || !url.startsWith("http://")) {
			url = WMS_URL;
		}
		wmsLayer = new WMS(name, url, wmsParams, wmsOptions);
		map.addLayer(wmsLayer);
	}

	public void resizeMap() {
		// Do a meaningless little calculation to force the map to re-calibrate
		// where it is on the page.
		int zoom = map.getZoom();
		LonLat center = map.getCenter();
		map.setCenter(center, zoom);
	}

	public void setDataExtent(double slat, double nlat, double wlon,
			double elon, double delta) {
		this.delta = delta;
		dataBounds = new Bounds(wlon, slat, elon, nlat);
		double w = dataBounds.getWidth();
		double dt = Math.abs(360. - w);
		if (dt <= 2. * delta) {
			modulo = true;
			selectionMade = false;
		} else {
			modulo = false;
		}
		zoomMap();
		currentSelection = dataBounds;
		lastRectangle = dataBounds;
		boxLayer.destroyFeatures();
		lineLayer.destroyFeatures();
		editing = false;
		setSelection(currentSelection);
		// Add the geometry back on to the map if the tools is not a rectangle.
		if (!tool.contains("xy")) {
			trimSelection(currentSelection);
		}
	}

	// This is to work around the bug that drawing on the map while it's hidden
	// doesn't work
	// You must follow this will a call to set selection.
	public void setDataExtentOnly(double slat, double nlat, double wlon,
			double elon, double delta) {
		this.delta = delta;
		dataBounds = new Bounds(wlon, slat, elon, nlat);
		double w = dataBounds.getWidth();
		double dt = Math.abs(360. - w);
		if (dt <= 2. * delta) {
			modulo = true;
			selectionMade = false;
		} else {
			modulo = false;
		}
	}

	public void setDataExtent(double slat, double nlat, double wlon, double elon) {
		setDataExtent(slat, nlat, wlon, elon, delta);
	}

	public void zoomMap() {
		int zoom = map.getZoomForExtent(dataBounds, false);
		if (box != null) {
			try {
				boxes.destroy();
			} catch (Exception e) {
				// Ok. If the marker has not be set this is throwing an NPE.
				// Give me a break.
			}
		}
		if (!modulo) {
			// map.removeLayer(boxes);
			boxes = new Boxes("Valid Region");
			map.addLayer(boxes);
			box = new Box(dataBounds);
			boxes.addMarker(box);

		}
		// mapOptions = new MapOptions();
		// map.setOptions(mapOptions);
		trimSelection(dataBounds);
		LonLat center = dataBounds.getCenterLonLat();
		// The selected region cannot not be centered exactly.
		// Back out the zoom and do the best you can.
		if (center.lon() + 180. > 360.) {
			zoom = 0;
			center = new LonLat(180., 0.);
		}
		map.setCenter(center, zoom);
	}

	public void zoomMapToSelection() {
		int zoom = map.getZoomForExtent(currentSelection, false);
		LonLat center = currentSelection.getCenterLonLat();
		if (center.lon() + 180. > 540.) {
			zoom = 0;
			center = new LonLat(360., 0.);
		}
		map.setCenter(center, zoom);
	}

	public void zoomOutAndPanToSelection() {
		map.zoomTo(0);
		panMapToSelection();
	}

	public void panMapToSelection() {
		LonLat center = currentSelection.getCenterLonLat();
		map.setCenter(center);
	}

	MapMoveListener mapMoveListener = new MapMoveListener() {

		@Override
		public void onMapMove(MapMoveEvent eventObject) {
			LonLat center = map.getCenter();
			if (modulo && !selectionMade) {
				setSelection(new Bounds(center.lon() - 180.0,
						center.lat() - 90., center.lon() + 180.0,
						center.lat() + 90.));
			}
		}
	};
	MapClickListener mapClickListener = new MapClickListener() {

		// We are going to listen for map clicks and treat them in the very
		// special case where the tool type is px or py and the drawing is on.
		// In that case and that case only, move the the line to the click
		// point.

		@Override
		public void onClick(MapClickEvent mapClickEvent) {
			if (tool.equals("px") || tool.equals("py")) {
				if (drawButton.isDown()) {
					LonLat click = mapClickEvent.getLonLat();
					if (tool.equals("py")) {
						setCurrentSelection(getYlo(), getYhi(), click.lon(),
								click.lon());
						eventBus.fireEventFromSource(new MapChangeEvent(
								getYlo(), getYhi(), click.lon(), click.lon()),
								this);
					} else if (tool.equals("px")) {
						setCurrentSelection(click.lat(), click.lat(), getXlo(),
								getXhi());
						eventBus.fireEventFromSource(
								new MapChangeEvent(click.lat(), click.lat(),
										getXlo(), getXhi()), this);
					}
				}
			}
		}

	};
	FeatureAddedListener featureAddedListener = new FeatureAddedListener() {

		@Override
		public void onFeatureAdded(VectorFeature vectorFeature) {
			// How come there is no narrowToGeometry with a Geometry argument.
			Geometry geo = Geometry.narrowToGeometry(vectorFeature
					.getGeometry().getJSObject());
			trimSelection(geo.getBounds());
			selectionMade = true;
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};

	private void setSelection(Bounds bounds) {
		currentSelection = bounds;
		if (tool.contains("xy")) {
			textWidget.setText(currentSelection.getUpperRightY(),
					currentSelection.getLowerLeftY(),
					currentSelection.getUpperRightX(),
					currentSelection.getLowerLeftX());
			lastRectangle = currentSelection;
		} else if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
				|| tool.equals("pt")) {
			LonLat center = currentSelection.getCenterLonLat();
			textWidget.setText(center.lat(), center.lat(), center.lon(),
					center.lon());
		} else if (tool.equals("x") || tool.equals("xz") || tool.equals("xt")
				|| tool.equals("px")) {
			LonLat c = currentSelection.getCenterLonLat();
			textWidget.setText(c.lat(), c.lat(),
					currentSelection.getUpperRightX(),
					currentSelection.getLowerLeftX());
		} else if (tool.equals("y") || tool.equals("yz") || tool.equals("yt")
				|| tool.equals("py")) {
			LonLat c = currentSelection.getCenterLonLat();
			textWidget.setText(currentSelection.getUpperRightY(),
					currentSelection.getLowerLeftY(), c.lon(), c.lon());
		}
	}

	private void trimSelection(Bounds bounds) {
		editing = false;

		// Always trip the north and south bounds
		double s_data = dataBounds.getLowerLeftY();
		double n_data = dataBounds.getUpperRightY();
		double w_data = dataBounds.getLowerLeftX();
		double e_data = dataBounds.getUpperRightX();
		double w_selection = bounds.getLowerLeftX();
		double e_selection = bounds.getUpperRightX();
		double s_selection = bounds.getLowerLeftY();
		double n_selection = bounds.getUpperRightY();
		// Always check the north/south against the data range.
		if (s_selection < s_data || s_selection > n_data) {
			s_selection = s_data;
		}
		if (n_selection > n_data || n_selection < s_data) {
			n_selection = n_data;
		}

		Bounds selectionBounds;
		lineLayer.destroyFeatures();
		boxLayer.destroyFeatures();
		if (modulo || dataBounds.containsBounds(bounds, false, true)) {
			selectionBounds = new Bounds(w_selection, s_selection, e_selection,
					n_selection);
		} else {
			// Only trip the east west if the data is not modulo
			if (w_selection < w_data || w_selection > e_data) {
				w_selection = w_data;
			}
			if (e_selection > e_data || e_selection < w_data) {
				e_selection = e_data;
			}
			selectionBounds = new Bounds(w_selection, s_selection, e_selection,
					n_selection);

		}
		LonLat center = selectionBounds.getCenterLonLat();
		if (tool.contains("xy")) {
			// Use it.
			boxLayer.addFeature(new VectorFeature(selectionBounds.toGeometry()));
			setSelection(selectionBounds);
		} else if (tool.equals("x") || tool.equals("xz") || tool.equals("xt")
				|| tool.equals("px")) {
			// Modify it then use it.
			selectionBounds = new Bounds(w_selection, center.lat(),
					e_selection, center.lat());
			lineLayer
					.addFeature(new VectorFeature(selectionBounds.toGeometry()));
			setSelection(selectionBounds);
		} else if (tool.equals("y") || tool.equals("yz") || tool.equals("yt")
				|| tool.equals("py")) {
			selectionBounds = new Bounds(center.lon(), s_selection,
					center.lon(), n_selection);
			lineLayer
					.addFeature(new VectorFeature(selectionBounds.toGeometry()));
			setSelection(selectionBounds);
		} else if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
				|| tool.equals("pt")) {
			Point p = new Point(selectionBounds.getCenterLonLat().lon(),
					selectionBounds.getCenterLonLat().lat());
			boxLayer.addFeature(new VectorFeature(p));
			setSelection(selectionBounds);
		}
	}

	public void setCurrentSelection(double slat, double nlat, double wlon,
			double elon) {
		editing = false;
		// Only set this if it is an actual sub-region of the data region of a
		// global data set.
		boolean subRegion = true;
		if (modulo) {
			subRegion = !((Math.abs(dataBounds.getLowerLeftY() - slat) < 1.5)
					&& (Math.abs(dataBounds.getUpperRightY() - nlat) < 1.5)
					&& (Math.abs(dataBounds.getLowerLeftX() - wlon) < 1.5) && (Math
					.abs(dataBounds.getUpperRightX() - elon) < 1.5));
		}
		if (subRegion) {
			selectionMade = true;
			Bounds bounds = new Bounds(wlon, slat, elon, nlat);
			lineLayer.destroyFeatures();
			boxLayer.destroyFeatures();
			if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
					|| tool.equals("pt")) {
				trimSelection(bounds);
			} else if (tool.equals("x") || tool.equals("xz")
					|| tool.equals("xt") || tool.equals("px")) {
				Bounds lineBounds = new Bounds(wlon, bounds.getCenterLonLat()
						.lat(), elon, bounds.getCenterLonLat().lat());
				trimSelection(lineBounds);
			} else if (tool.equals("y") || tool.equals("yz")
					|| tool.equals("yt") || tool.equals("py")) {
				Bounds lineBounds = new Bounds(bounds.getCenterLonLat().lon(),
						slat, bounds.getCenterLonLat().lon(), nlat);
				trimSelection(lineBounds);
			} else {
				trimSelection(bounds);
			}
		} else {
			setDataExtent(dataBounds.getLowerLeftY(),
					dataBounds.getUpperRightY(), dataBounds.getLowerLeftX(),
					dataBounds.getUpperRightX());
		}
	}

	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		if (!this.tool.equals(tool)) {
			editing = false;
			this.tool = tool;
			LonLat l = currentSelection.getCenterLonLat();
			LonLat r = lastRectangle.getCenterLonLat();

			boolean useLast = false;
			if (Math.abs(l.lat() - r.lat()) < epsilon
					&& Math.abs(l.lon() - r.lon()) < epsilon) {
				useLast = true;
			}

			double halfx = Math.min(
					Math.abs(dataBounds.getUpperRightX() - l.lon()) / 2.0,
					Math.abs(l.lon() - dataBounds.getLowerLeftX()) / 2.0);
			double halfy = Math.min(
					Math.abs(dataBounds.getUpperRightY() - l.lat()) / 2.0,
					Math.abs(l.lat() - dataBounds.getLowerLeftY()) / 2.0);

			drawRectangle.deactivate();
			drawXLine.deactivate();
			drawXPoint.deactivate();
			drawYLine.deactivate();
			drawYPoint.deactivate();
			drawPoint.deactivate();

			Bounds b;
			if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
					|| tool.equals("pt")) {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_pt_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_pt_on.png");
				if (drawButton.isDown()) {
					drawPoint.activate();
				}
			} else if (tool.equals("x") || tool.equals("xz")
					|| tool.equals("xt")) {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_x_line_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_x_line_on.png");
				if (drawButton.isDown()) {
					drawXLine.activate();
				}
			} else if (tool.equals("y") || tool.equals("yz")
					|| tool.equals("yt")) {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_y_line_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_y_line_on.png");
				if (drawButton.isDown()) {
					drawYLine.activate();
				}
			} else if (tool.equals("py")) {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_y_line_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_y_line_on.png");
				if (drawButton.isDown()) {
					drawYPoint.activate();
				}
			} else if (tool.equals("px")) {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_x_line_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_x_line_on.png");
				if (drawButton.isDown()) {
					drawXPoint.activate();
				}
			} else {
				drawButtonUp.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_off.png");
				drawButtonDown.setUrl(GWT.getModuleBaseURL()
						+ "../images/draw_on.png");
				if (drawButton.isDown()) {
					drawRectangle.activate();
				}
			}

			// If the center of the current c
			// Draw the box from the center of the current geometry to half of
			// the shortest distance to the edge of the data bounds.
			if (useLast) {
				b = lastRectangle;
			} else {
				b = new Bounds(l.lon() - halfx, l.lat() - halfy, l.lon()
						+ halfx, l.lat() + halfy);
			}
			trimSelection(b);

			if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
					|| tool.equals("pt") || tool.equals("py")
					|| tool.equals("px")) {
				// Disable selecting for points
				editButton.setEnabled(false);
			} else {
				editButton.setEnabled(true);
			}
		}
	}

	// We need to set the tool, but not draw on the map to work around the
	// problem of drawing on the map when it's hidden.
	public void setToolOnly(String tool) {
		editing = false;
		this.tool = tool;
		drawRectangle.deactivate();
		drawXLine.deactivate();
		drawXPoint.deactivate();
		drawYLine.deactivate();
		drawYPoint.deactivate();
		drawPoint.deactivate();
		if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
				|| tool.equals("pt")) {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_pt_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_pt_on.png");
			if (drawButton.isDown()) {
				drawPoint.activate();
			}
		} else if (tool.equals("x") || tool.equals("xz") || tool.equals("xt")) {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_x_line_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_x_line_on.png");
			if (drawButton.isDown()) {
				drawXLine.activate();
			}
		} else if (tool.equals("y") || tool.equals("yz") || tool.equals("yt")) {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_y_line_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_y_line_on.png");
			if (drawButton.isDown()) {
				drawYLine.activate();
			}
		} else if (tool.equals("py")) {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_y_line_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_y_line_on.png");
			if (drawButton.isDown()) {
				drawYPoint.activate();
			}
		} else if (tool.equals("px")) {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_x_line_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_x_line_on.png");
			if (drawButton.isDown()) {
				drawXPoint.activate();
			}
		} else {
			drawButtonUp.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_off.png");
			drawButtonDown.setUrl(GWT.getModuleBaseURL()
					+ "../images/draw_on.png");
			if (drawButton.isDown()) {
				drawRectangle.activate();
			}
		}
		if (tool.equals("t") || tool.equals("z") || tool.equals("zt")
				|| tool.equals("pt") || tool.equals("py") || tool.equals("px")) {
			// Disable selecting for points
			editButton.setEnabled(false);
		} else {
			editButton.setEnabled(true);
		}
	}

	public ClickHandler editButtonClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			drawing = false;
			panButton.setDown(false);
			editButton.setDown(true);
			drawButton.setDown(false);
			drawPoint.deactivate();
			drawRectangle.deactivate();
			drawXLine.deactivate();
			drawXPoint.deactivate();
			drawYLine.deactivate();
			drawYPoint.deactivate();
			if (tool.contains("xy")) {
				if (boxLayer.getFeatures() == null) {
					Window.alert("Make a selection on the map then select this button, to edit it.");
					panButton.setDown(true);
					drawButton.setDown(false);
					editButton.setDown(false);
				} else {
					modifyFeatureXY.activate();
					modifyFeatureLine.deactivate();
				}
			} else if (tool.equals("x") || tool.equals("xz")
					|| tool.equals("xt") || tool.equals("y")
					|| tool.equals("yz") || tool.equals("yt")) {
				if (lineLayer.getFeatures() == null) {
					Window.alert("Make a selection on the map then select button, to edit it.");
					panButton.setDown(true);
					drawButton.setDown(false);
					editButton.setDown(false);
				} else {
					modifyFeatureXY.deactivate();
					modifyFeatureLine.activate();
				}

			} else if (tool.equals("t") || tool.equals("z")
					|| tool.equals("zt") || tool.equals("pt")
					|| tool.equals("py") || tool.equals("px")) {
				// A view of z to t is a point tool type

				modifyFeatureXY.deactivate();
				modifyFeatureLine.deactivate();

			}
		}

	};

	public ClickHandler panButtonClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			drawing = false;
			panButton.setDown(true);
			drawButton.setDown(false);
			editButton.setDown(false);
			drawPoint.deactivate();
			drawRectangle.deactivate();
			drawXLine.deactivate();
			drawXPoint.deactivate();
			drawYLine.deactivate();
			drawYPoint.deactivate();
			modifyFeatureXY.deactivate();
			modifyFeatureLine.deactivate();
		}

	};

	public void toggleDrawing() {
		if (!drawing) {
			// Drawing is not active, so activate it.
			drawing = true;
			panButton.setDown(false);
			editButton.setDown(false);
			drawButton.setDown(true);
			if (tool.contains("xy")) {

				drawRectangle.activate();
				drawXLine.deactivate();
				drawXPoint.deactivate();
				drawYLine.deactivate();
				drawYPoint.deactivate();
				drawPoint.deactivate();

			} else if (tool.equals("x") || tool.equals("xz")
					|| tool.equals("xt")) {

				drawRectangle.deactivate();
				drawXLine.activate();
				drawXPoint.deactivate();
				drawYLine.deactivate();
				drawYPoint.deactivate();
				drawPoint.deactivate();

			} else if (tool.equals("y") || tool.equals("yz")
					|| tool.equals("yt")) {

				drawRectangle.deactivate();
				drawXLine.deactivate();
				drawXPoint.deactivate();
				drawYLine.activate();
				drawYPoint.deactivate();
				drawPoint.deactivate();

			} else if (tool.equals("t") || tool.equals("z")
					|| tool.equals("zt") || tool.equals("pt")) {
				// A view of z to t is a point tool type

				drawRectangle.deactivate();
				drawXLine.deactivate();
				drawXPoint.deactivate();
				drawYLine.deactivate();
				drawYPoint.deactivate();
				drawPoint.activate();

			} else if (tool.equals("py")) {
				drawRectangle.deactivate();
				drawXLine.deactivate();
				drawXPoint.deactivate();
				drawYLine.deactivate();
				drawYPoint.activate();
				drawPoint.deactivate();
			} else if (tool.equals("px")) {
				drawRectangle.deactivate();
				drawXLine.deactivate();
				drawXPoint.activate();
				drawYLine.deactivate();
				drawYPoint.deactivate();
				drawPoint.deactivate();
			}
		} else {
			// Turn off drawing to allow selections
			drawing = false;
			drawButton.setDown(false);
			panButton.setDown(true);
			editButton.setDown(false);

			drawRectangle.deactivate();
			drawXLine.deactivate();
			drawXPoint.deactivate();
			drawYLine.deactivate();
			drawYPoint.deactivate();
			drawPoint.deactivate();

		}
	}

	public boolean isContainedBy(String xlo, String xhi, String ylo, String yhi) {
		double dxl = Double.valueOf(xlo);
		double dxh = Double.valueOf(xhi);
		double dyl = Double.valueOf(ylo);
		double dyh = Double.valueOf(yhi);
		Bounds originalBounds = new Bounds(dxl, dyl, dxh, dyh);
		return originalBounds.containsBounds(currentSelection, false, true);
	}

	public ClickHandler drawButtonClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			toggleDrawing();
		}
	};
	/**
	 * A listener that will handle change events when the user types text into
	 * the TextBox with the south latitude value.
	 */
	public ChangeListener southChangeListener = new ChangeListener() {

		public void onChange(Widget sender) {
			TextBox tb = (TextBox) sender;
			double ylo = textWidget.getYlo();
			double yhi = textWidget.getYhi();
			String oentry = tb.getText();
			String entry = oentry.trim().toLowerCase(Locale.ENGLISH);
			if (entry.contains("s") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"S\" to denote southern latitudes, not both");
			} else if (entry.contains("n") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"S\" to denote southern latitudes, not a minus sign and an \"N\"");
			} else if (entry.contains("s")) {
				entry = entry.substring(0, entry.indexOf("s"));
				try {
					ylo = Double.valueOf(entry.trim());
					ylo = -ylo;
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			} else if (entry.contains("n")) {
				entry = entry.substring(0, entry.indexOf("n"));
				try {
					ylo = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			} else {
				try {
					ylo = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			}
			if (ylo < dataBounds.getLowerLeftY()) {
				ylo = dataBounds.getLowerLeftY();
			}

			// If it's a point or an x tool bring the hi down to the lo that was
			// just set.
			if (tool.equals("t") 
					|| tool.equals("z") 
					|| tool.equals("zt")
					|| tool.equals("pt") 
					|| tool.equals("px")
					|| tool.equals("x") 
					|| tool.equals("xz")
					|| tool.equals("xt")) {
				yhi = ylo;
			} else {
				// Keep the rectangle from going inside out.
				if (ylo > yhi) {
					ylo = yhi;
				}
			}

			setCurrentSelection(ylo, yhi, currentSelection.getLowerLeftX(),
					currentSelection.getUpperRightX());
			panMapToSelection();
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};
	/**
	 * A listener that will handle change events when the user types text into
	 * the TextBox with the north latitude value.
	 */
	public ChangeListener northChangeListener = new ChangeListener() {

		public void onChange(Widget sender) {
			TextBox tb = (TextBox) sender;
			double ylo = textWidget.getYlo();
			double yhi = textWidget.getYhi();
			String oentry = tb.getText();
			String entry = oentry.trim().toLowerCase(Locale.ENGLISH);
			if (entry.contains("s") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"S\" to denote southern latitudes, not both");
			} else if (entry.contains("n") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"S\" to denote southern latitudes, not a minus sign and an \"N\"");
			} else if (entry.contains("s")) {
				entry = entry.substring(0, entry.indexOf("s"));
				try {
					yhi = Double.valueOf(entry.trim());
					yhi = -yhi;
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			} else if (entry.contains("n")) {
				entry = entry.substring(0, entry.indexOf("n"));
				try {
					yhi = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			} else {
				try {
					yhi = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid latitude value.");
				}
			}

			if (yhi > dataBounds.getUpperRightY()) {
				yhi = dataBounds.getUpperRightY();
			}

			// If it's a point or an x tool bring the lo up to the hi.
			if (tool.equals("t") 
					|| tool.equals("z") 
					|| tool.equals("zt")
					|| tool.equals("pt") 
					|| tool.equals("px")
					|| tool.equals("x") 
					|| tool.equals("xz")
					|| tool.equals("xt")) {
				ylo = yhi;
			} else {
				// If the value would turn the rectangle inside out pull the
				// value down to the current ylo.
				if (yhi < ylo) {
					yhi = ylo;
				}
			}
			setCurrentSelection(ylo, yhi, currentSelection.getLowerLeftX(),
					currentSelection.getUpperRightX());
			panMapToSelection();
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};
	/**
	 * A listener that will handle change events when the user types text into
	 * the TextBox with the west longitude value.
	 */
	public ChangeListener westChangeListener = new ChangeListener() {
		public void onChange(Widget sender) {
			TextBox tb = (TextBox) sender;
			double xlo = textWidget.getXlo();
			double xhi = textWidget.getXhi();
			String oentry = tb.getText();
			String entry = oentry.trim().toLowerCase(Locale.ENGLISH);
			if (entry.contains("w") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"W\" to denote west longitudes, not both");
			} else if (entry.contains("e") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"W\" to denote west longitudes, not a minus sign and an \"E\"");
			} else if (entry.contains("w")) {
				entry = entry.substring(0, entry.indexOf("w"));
				try {
					xlo = Double.valueOf(entry.trim());
					xlo = -xlo;
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			} else if (entry.contains("e")) {
				entry = entry.substring(0, entry.indexOf("e"));
				try {
					xlo = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			} else {
				try {
					xlo = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			}
			xlo = GeoUtil.normalizeLon(xlo);
			if (tool.equals("t") 
					|| tool.equals("z") 
					|| tool.equals("zt")
					|| tool.equals("pt") 
					|| tool.equals("px")
					|| tool.equals("py")
					|| tool.equals("y") 
					|| tool.equals("yz")
					|| tool.equals("yt")) {
				// If it's a point or a Y line, move the selection to this
				// location by setting the East to the same value.
				xhi = xlo;

			} else {
				// If it's a rectangle or an x-line check the end points and the
				// redraw.
				if (!modulo) {
					// This is not a global data set, make sure we're not west
					// of the data bounds.
					if (xlo < GeoUtil.normalizeLon(dataBounds.getLowerLeftX())) {
						xlo = dataBounds.getLowerLeftX();
					}
				} else {
					// It's a modulo data set, make the east and west wrap as
					// appropriate.
					if (xlo >= GeoUtil.normalizeLon(xhi)) {

						xhi = GeoUtil.normalizeLon(xhi);
						while (xlo >= xhi) {
							xhi = xhi + 360.;
						}
					}
				}

			}

			setCurrentSelection(currentSelection.getLowerLeftY(),
					currentSelection.getUpperRightY(), xlo, xhi);
			panMapToSelection();
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};
	/**
	 * A listener that will handle change events when the user types text into
	 * the TextBox with the east longitude value.
	 */
	public ChangeListener eastChangeListener = new ChangeListener() {

		public void onChange(Widget sender) {
			TextBox tb = (TextBox) sender;
			double xlo = textWidget.getXlo();
			double xhi = textWidget.getXhi();
			String oentry = tb.getText();
			String entry = oentry.trim().toLowerCase(Locale.ENGLISH);
			if (entry.contains("w") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"W\" to denote west longitudes, not both");
			} else if (entry.contains("e") && entry.contains("-")) {
				Window.alert("Either use a minus sign or an \"W\" to denote west longitudes, not a minus sign and an \"E\"");
			} else if (entry.contains("w")) {
				entry = entry.substring(0, entry.indexOf("w"));
				try {
					xhi = Double.valueOf(entry.trim());
					xhi = -xhi;
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			} else if (entry.contains("e")) {
				entry = entry.substring(0, entry.indexOf("e"));
				try {
					xhi = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			} else {
				try {
					xhi = Double.valueOf(entry.trim());
				} catch (Exception e) {
					Window.alert(oentry + " is not a valid longitude value.");
				}
			}

			xhi = GeoUtil.normalizeLon(xhi);
			if (tool.equals("t") 
					|| tool.equals("z") 
					|| tool.equals("zt")
					|| tool.equals("pt") 
					|| tool.equals("px")
					|| tool.equals("py")
					|| tool.equals("y") 
					|| tool.equals("yz")
					|| tool.equals("yt")) {
				// If it's a point or a X line, move the selection to this
				// location by setting the East to the same value.
				xlo = xhi;

			} else {

				if (!modulo) {
					// This is not a global data set, make sure we're not east
					// of the data bounds.
					if (xhi > GeoUtil.normalizeLon(dataBounds.getUpperRightX())) {
						xhi = dataBounds.getUpperRightX();
					}
				} else {
					// It's a modulo data set, make the east and west wrap as
					// appropriate.
					if (xlo >= GeoUtil.normalizeLon(xhi)) {
						while (xlo >= xhi) {
							xhi = xhi + 360.;
						}
					}
				}
			}

			setCurrentSelection(currentSelection.getLowerLeftY(),
					currentSelection.getUpperRightY(), xlo, xhi);
			panMapToSelection();
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};

	public double getXlo() {
		return textWidget.getXlo();
	}

	public double getXhi() {
		return textWidget.getXhi();
	}

	public double getYhi() {
		return textWidget.getYhi();
	}

	public double getYlo() {
		return textWidget.getYlo();
	}

	public String getYhiFormatted() {
		return textWidget.getYhiFormatted();
	}

	public String getYloFormatted() {
		return textWidget.getYloFormatted();
	}

	public String getXhiFormatted() {
		return textWidget.getXhiFormatted();
	}

	public String getXloFormatted() {
		return textWidget.getXloFormatted();
	}

	public ChangeListener regionChangeListener = new ChangeListener() {
		@Override
		public void onChange(Widget sender) {

			int i = regionWidget.getSelectedIndex();
			String value = regionWidget.getItemText(i);
			double[] reg = regionWidget.getRegion(i, value);
			// s, n, w, e
			if (reg != null) {
				selectionMade = true;
				setCurrentSelection(reg[0], reg[1], reg[2], reg[3]);
				zoomMapToSelection();
			}

			regionWidget.setSelectedIndex(0);

			regionPanel.hide();
			regionButton.setDown(false);
			// This call back is called by the drawing control when a feature is
			// added by drawing.
			// We need to call it ourselves when the region widget fires.
			if (mapListener != null) {
				mapListener.onFeatureChanged();
			}
		}
	};

	public double[] getDataExtent() {
		double[] d = new double[4];
		// s, n, w, e to match setDataExtent...
		d[0] = dataBounds.getLowerLeftY();
		d[1] = dataBounds.getUpperRightY();
		d[2] = dataBounds.getLowerLeftX();
		d[3] = dataBounds.getUpperRightX();
		return d;
	}

	public double[] getCurrentSelection() {
		double[] cs = new double[4];
		// s, n, w, e to match setDataExtent
		cs[0] = currentSelection.getLowerLeftY();
		cs[1] = currentSelection.getUpperRightY();
		cs[2] = currentSelection.getLowerLeftX();
		cs[3] = currentSelection.getUpperRightX();
		return cs;
	}

	public double getDelta() {
		return delta;
	}

	public int getZoom() {
		return map.getZoom();
	}

	public double[] getCenterLatLon() {
		LonLat centerLonLat = map.getCenter();
		double[] center = new double[2];
		center[0] = centerLonLat.lat();
		center[1] = centerLonLat.lon();
		return center;
	}

	public void setCenter(double lat, double lon, int zoom) {
		LonLat c = new LonLat(lon, lat);
		map.setCenter(c, zoom);
	}

	public void setRegions(RegionSerializable[] regions) {
		regionWidget.setRegions(regions);
	}

	public void setNamedRegions(JavaScriptObject r) {
		JSONObject rj = new JSONObject(r);
		JSONObject rs = rj.get("regions").isObject();
		if (rs != null) {
			JSONArray regions = rs.get("region").isArray();
			if (regions != null) {
				RegionSerializable[] wire_regions = new RegionSerializable[regions
						.size()];
				for (int i = 0; i < regions.size(); i++) {
					JSONObject region = regions.get(i).isObject();
					if (region != null) {
						wire_regions[i] = new RegionSerializable();
						wire_regions[i].setName(region.get("name").isString()
								.stringValue());
						wire_regions[i].setWestLon(Double.valueOf(region
								.get("xlo").isString().stringValue()));
						wire_regions[i].setEastLon(Double.valueOf(region
								.get("xhi").isString().stringValue()));
						wire_regions[i].setSouthLat(Double.valueOf(region
								.get("ylo").isString().stringValue()));
						wire_regions[i].setNorthLat(Double.valueOf(region
								.get("yhi").isString().stringValue()));
					}
				}
				regionWidget.setRegions(wire_regions);
			}
		}
	}

	public boolean isEditing() {
		return editing;
	}

	public MapSelectionChangeListener getMapListener() {
		return mapListener;
	}

	public void setMapListener(MapSelectionChangeListener mapListener) {
		this.mapListener = mapListener;
	}

	public void removeFeatures() {
		boxLayer.destroyFeatures();
		lineLayer.destroyFeatures();
	}
}
