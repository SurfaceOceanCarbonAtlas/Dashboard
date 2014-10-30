Ext.BLANK_IMAGE_URL = 'mfbase/ext/resources/images/default/s.gif';

var defaultWidth = 300;
var defaultHeight = 300;
var defaultX = 0;
var defaultY = 0;

var mapholder = "map";

var pMap = null;
var wMap = null;
var mapPanel = null;
var mapWindow = null;
var pPolyLayer = null;
var wPolyLayer = null;
var bMapPanel = true;
var bBoxDrawn = true; // true: users draw a polygon or box

var pTGroup = "ptools";
var wTGroup = "wtools";
var pClearID = "pclearaoi";
var wClearID = "wclearaoi";

var westId = "west";
var southId = "south";
var eastId = "east";
var northId = "north";
var geocoder;
var polyStyle = OpenLayers.Util.applyDefaults({
  fill: false,
  strokeWidth: 2,
  strokeColor: "#FF0000"
}, OpenLayers.Feature.Vector.style["default"]);

var polyStyleMap = new OpenLayers.StyleMap({
  "default": new OpenLayers.Style(polyStyle)
}); 	       


Ext.onReady(createMapPanel);

function createPMap(){
    if(pMap != null)
	  pMap.destroy();
	           
            
    pMap = new OpenLayers.Map({
      projection: new OpenLayers.Projection("EPSG:102113"),
	  displayProjection: new OpenLayers.Projection("EPSG:4326"),
	  units: "m",
      maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508.34),
      allOverlays: false,
      theme: null,
      numZoomLevels: 15,
	  controls: []
    });
	 pMap.addControl(new OpenLayers.Control.LayerSwitcher());
	geocoder = new OpenLayers.Control.Geocoder();
    pMap.addControl(geocoder);
    var usgslayer = new OpenLayers.Layer.XYZ("USGS Base Map", 
        "http://basemap.nationalmap.gov/ArcGIS/rest/services/TNM_Vector_Fills_Small/MapServer/tile/${z}/${y}/${x}", {
			 projection: "EPSG:102113", sphericalMercator: true,minZoomLevel: 0 });
    pPolyLayer = new OpenLayers.Layer.Vector("Selected Area", { styleMap: polyStyleMap });
   // pMap.addLayers([gplayer, gmlayer, ghlayer, gslayer,usgslayer]);
    var usgsVectorlayer = new OpenLayers.Layer.XYZ("USGS Vector Map", 
        "http://basemap.nationalmap.gov/ArcGIS/rest/services/TNM_Vector_Small/MapServer/tile/${z}/${y}/${x}", {
 		numZoomLevels: 16,  isBaseLayer: false, sphericalMercator: true
 	});
	var rastermap = new OpenLayers.Layer.XYZ("USGS Base Imagery Map", 
        "http://raster1.nationalmap.gov/ArcGIS/rest/services/TNM_Small_Scale_Imagery/MapServer/tile/${z}/${y}/${x}", {
		numZoomLevels: 16,  sphericalMercator: true
    });
	pMap.addLayers([usgslayer,usgsVectorlayer,rastermap]);
    pMap.addLayer(pPolyLayer);
    pPolyLayer.events.register('beforefeatureadded', null, function(evt) {
      var i, len, toDestroy = [];
      for(i=0,len=pPolyLayer.features.length; i<len; i++) {
        if(pPolyLayer.features[i] != evt.feature) {
          toDestroy.push(pPolyLayer.features[i]);
        }
      }
      pPolyLayer.removeFeatures(toDestroy);
      for(i=toDestroy.length-1; i>=0; i--) {
         toDestroy[i].destroy();
      }
    });                       
    pPolyLayer.events.register('featureadded', null, function(evt) {
      var nof = pPolyLayer.features.length;
      if(nof >= 1) {
		var ngeom = pPolyLayer.features[0].geometry;

        pMap.zoomToExtent(ngeom.getBounds());
		
		// Update BBOX values only if users draw it
		if(bBoxDrawn)
          updateBBOXValuesByBounds(SM2LatLonBnds_Geom(ngeom));

        bBoxDrawn = true; // Recover the indicator

        if(Ext.getCmp(pClearID).disabled)
          Ext.getCmp(pClearID).enable();
	  } else
        alert('no feature added');
    });

    pMap.zoomToMaxExtent();
    pMap.setCenter(new OpenLayers.LonLat(-100,40));
}


function createWMap(){
    if(wMap != null)
	  wMap.destroy();
	geocoder = new OpenLayers.Control.Geocoder();
    wMap.addControl(geocoder);
    wMap = new OpenLayers.Map({
      projection: new OpenLayers.Projection("EPSG:102113"),
	  displayProjection: new OpenLayers.Projection("EPSG:4326"),
	  units: "m",
      maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508.34),
      allOverlays: false,
      theme: null,
	  controls: []
    });
    wMap.addControl(new OpenLayers.Control.LayerSwitcher());
    var usgslayer = new OpenLayers.Layer.XYZ("USGS Base Map", 
            "http://basemap.nationalmap.gov/ArcGIS/rest/services/TNM_Vector_Fills_Small/MapServer/tile/${z}/${y}/${x}", {
    			 projection: "EPSG:102113", sphericalMercator: true,minZoomLevel: 0 });
        pPolyLayer = new OpenLayers.Layer.Vector("Selected Area", { styleMap: polyStyleMap });
    var usgsVectorlayer = new OpenLayers.Layer.XYZ("USGS Vector Map", 
            "http://basemap.nationalmap.gov/ArcGIS/rest/services/TNM_Vector_Small/MapServer/tile/${z}/${y}/${x}", {
     		numZoomLevels: 16,  isBaseLayer: false, sphericalMercator: true
     	});
    	var rastermap = new OpenLayers.Layer.XYZ("USGS Base Imagery Map", 
            "http://raster1.nationalmap.gov/ArcGIS/rest/services/TNM_Small_Scale_Imagery/MapServer/tile/${z}/${y}/${x}", {
    		numZoomLevels: 16,  sphericalMercator: true
        });
    	

    wPolyLayer = new OpenLayers.Layer.Vector("Selected Area", { styleMap: polyStyleMap });
  
    wMap.addLayers([usgslayer,usgsVectorlayer,rastermap]);
    wMap.addLayer(wPolyLayer);

    wPolyLayer.events.register('beforefeatureadded', null, function(evt) {
      var i, len, toDestroy = [];
      for(i=0,len=wPolyLayer.features.length; i<len; i++) {
        if(wPolyLayer.features[i] != evt.feature) {
          toDestroy.push(wPolyLayer.features[i]);
        }
      }
      wPolyLayer.removeFeatures(toDestroy);
      for(i=toDestroy.length-1; i>=0; i--) {
         toDestroy[i].destroy();
      }
    });                       
    wPolyLayer.events.register('featureadded', null, function(evt) {
      var nof = wPolyLayer.features.length;
      if(nof >= 1) {
		var ngeom = wPolyLayer.features[0].geometry;

        wMap.zoomToExtent(ngeom.getBounds());

        // Update BBOX values only if users draw it
		if(bBoxDrawn)
          updateBBOXValuesByBounds(SM2LatLonBnds_Geom(ngeom));

        if(Ext.getCmp(wClearID).disabled)
          Ext.getCmp(wClearID).enable();
	  } else
        alert('no feature added');
    });

    wMap.zoomToMaxExtent();
    wMap.setCenter(new OpenLayers.LonLat(-100,40));
}

function createMapPanel() {
	if(pMap == null)
	  createPMap();
	  
	if(mapWindow != null)
	  mapWindow.hide();
	 if(mapPanel == null) {
	mapPanel = new GeoExt.MapPanel({
        map: pMap,
		renderTo: mapholder,
		width: 650,
		height: 400,	
        items: [{
          xtype: "gx_zoomslider",
          aggressive: true,
          vertical: true,
          height: 100,
          plugins: new GeoExt.ZoomSliderTip({
            template: "Scale: 1 : {scale}<br>Resolution: {resolution}"
          })
        }],
        tbar: createPTbarItems(),
        center: LatLong2SM_point(-90,42),
        zoom: 3
        
    }); 
	}
	 
  mapPanel.show();  
  bMapPanel = true;

}

function createMapWindow() {
	if(wMap == null)
	  createWMap(false, wClearID);
	
	if(mapPanel != null) {
	  mapPanel.hide();
	}
	  
    if(mapWindow == null) {
	mapWindow = new Ext.Window({
      title: 'Map Window',
	  x: defaultX,
      y: defaultY,
      width: defaultWidth,
      height: defaultHeight,
      minWidth: 200,
      minHeight: 200,
      layout: 'fit',
      plain: true,
      bodyStyle: 'padding:5px;',
	  closable: true,
	  shadow: false,
	  border: false,
      items: [{
        xtype: 'gx_mappanel',
        map: wMap,
        items: [{
          xtype: "gx_zoomslider",
          aggressive: true,
          vertical: true,
          height: 100,
          plugins: new GeoExt.ZoomSliderTip({
            template: "Scale: 1 : {scale}<br>Resolution: {resolution}"
          })
        }],
        tbar: createWTbarItems()        
      }],
	  listeners: {
		close: toggleBigMap  
	  }
    });
	}

  mapWindow.show();
  bMapPanel = false;
}

function createPTbarItems() {
  var defStyle = {
    graphicName: "square",
    strokeColor: "#00FFFF",
    strokeOpacity: 1,
    strokeWidth: 1,
    fillColor: "#00FFFF",
    fillOpacity: 0.3,
    pointRadius: 4
  };

  var actions = [];
  actions.push(new GeoExt.Action({
      iconCls: "pan",
      map: pMap,
      pressed: true,
      toggleGroup: pTGroup,
      allowDepress: false,
      tooltip: "Navigate",
      control: new OpenLayers.Control.Navigation()
  }));
  actions.push(new GeoExt.Action({
      iconCls: "zoomin",
      map: pMap,
      toggleGroup: pTGroup,
      allowDepress: false,
      tooltip: "Zoom in",
      control: new OpenLayers.Control.ZoomBox({
        out: false
      })
  }));
  actions.push(new GeoExt.Action({
      iconCls: "zoomout",
      map: pMap,
      toggleGroup: pTGroup,
      allowDepress: false,
      tooltip: "Zoom out",
      control: new OpenLayers.Control.ZoomBox({
        out: true
      })
  }));
  actions.push(new GeoExt.Action({
      iconCls: "fullext",
      map: pMap,
      tooltip: "Zoom to full extent",
      control: new OpenLayers.Control.ZoomToMaxExtent()
  }));
  actions.push(new Ext.Toolbar.Separator());
  var ctrl = new OpenLayers.Control.NavigationHistory();
  pMap.addControl(ctrl);
  actions.push(new GeoExt.Action({
      control: ctrl.previous,
      iconCls: "back",
      tooltip: "back",
      disabled: true
  }));
  actions.push(new GeoExt.Action({
      control: ctrl.next,
      iconCls: "next",
      tooltip: "next",
      disabled: true
  }));
  actions.push(new Ext.Toolbar.Separator());
  actions.push(new GeoExt.Action({
	iconCls: "drawpolygon",
    map: pMap,
    toggleGroup: pTGroup,
	allowDepress: false,
    tooltip: "Define Polygon Area",
    control: new OpenLayers.Control.DrawFeature(
	  pPolyLayer, 
	  OpenLayers.Handler.Polygon, 
	  {
        displayClass: "clsControlDrawPolygon",
        handlerOptions: {
          style: polyStyle,
          persist: false
        }
      }
	)
  }));
  actions.push(new GeoExt.Action({
	iconCls: "drawrect",
    map: pMap,
    toggleGroup: pTGroup,
	allowDepress: false,
    tooltip: "Define Rectangle Area",
    control: new OpenLayers.Control.DrawFeature(
	  pPolyLayer, 
	  OpenLayers.Handler.RegularPolygon, 
	  {
        displayClass: "clsControlDrawRectangle",
        handlerOptions: {
          style: polyStyle,
		  sides: 4,
		  irregular: true,
          persist: false
        }
      }
	)
  }));
  actions.push(new GeoExt.Action({
    id: pClearID,
	iconCls: "clearpolygon",
	disabled: true,
	map: pMap,
	tooltip: "Clear Defined Area",
	handler: clearPPoly
  }));
  
  return actions;
};

function clearPPoly() {
      var i, len, toDestroy = [];
      for (i = 0, len = pPolyLayer.features.length; i < len; i++) {
        toDestroy.push(pPolyLayer.features[i]);
      }
      pPolyLayer.removeFeatures(toDestroy);
      for (i = toDestroy.length - 1; i >= 0; i--) {
        toDestroy[i].destroy();
      }
	  initBBOXValues();
      Ext.getCmp(pClearID).disable();

}

function createWTbarItems() {
  var defStyle = {
    graphicName: "square",
    strokeColor: "#00FFFF",
    strokeOpacity: 1,
    strokeWidth: 1,
    fillColor: "#00FFFF",
    fillOpacity: 0.3,
    pointRadius: 4
  };

  var actions = [];
  actions.push(new GeoExt.Action({
      iconCls: "pan",
      map: wMap,
      pressed: true,
      toggleGroup: wTGroup,
      allowDepress: false,
      tooltip: "Navigate",
      control: new OpenLayers.Control.Navigation()
  }));
  actions.push(new GeoExt.Action({
      iconCls: "zoomin",
      map: wMap,
      toggleGroup: wTGroup,
      allowDepress: false,
      tooltip: "Zoom in",
      control: new OpenLayers.Control.ZoomBox({
        out: false
      })
  }));
  actions.push(new GeoExt.Action({
      iconCls: "zoomout",
      map: wMap,
      toggleGroup: wTGroup,
      allowDepress: false,
      tooltip: "Zoom out",
      control: new OpenLayers.Control.ZoomBox({
        out: true
      })
  }));
  actions.push(new GeoExt.Action({
      iconCls: "fullext",
      map: wMap,
      tooltip: "Zoom to full extent",
      control: new OpenLayers.Control.ZoomToMaxExtent()
  }));
  actions.push(new Ext.Toolbar.Separator());
  var ctrl = new OpenLayers.Control.NavigationHistory();
  wMap.addControl(ctrl);
  actions.push(new GeoExt.Action({
      control: ctrl.previous,
      iconCls: "back",
      tooltip: "back",
      disabled: true
  }));
  actions.push(new GeoExt.Action({
      control: ctrl.next,
      iconCls: "next",
      tooltip: "next",
      disabled: true
  }));
  actions.push(new Ext.Toolbar.Separator());
  actions.push(new GeoExt.Action({
	iconCls: "drawpolygon",
    map: wMap,
    toggleGroup: wTGroup,
	allowDepress: false,
    tooltip: "Define Polygon Area",
    control: new OpenLayers.Control.DrawFeature(
	  wPolyLayer, 
	  OpenLayers.Handler.Polygon, 
	  {
        displayClass: "clsControlDrawPolygon",
        handlerOptions: {
          style: polyStyle,
          persist: false
        }
      }
	)
  }));
  actions.push(new GeoExt.Action({
	iconCls: "drawrect",
    map: wMap,
    toggleGroup: wTGroup,
	allowDepress: false,
    tooltip: "Define Rectangle Area",
    control: new OpenLayers.Control.DrawFeature(
	  wPolyLayer, 
	  OpenLayers.Handler.RegularPolygon, 
	  {
        displayClass: "clsControlDrawRectangle",
        handlerOptions: {
          style: polyStyle,
		  sides: 4,
		  irregular: true,
          persist: false
        }
      }
	)
  }));
  actions.push(new GeoExt.Action({
    id: wClearID,
	iconCls: "clearpolygon",
	disabled: true,
	map: wMap,
	tooltip: "Clear Defined Area",
	handler: clearWPoly
  }));
  
  return actions;
};

function clearWPoly() {
      var i, len, toDestroy = [];
      for (i = 0, len = wPolyLayer.features.length; i < len; i++) {
        toDestroy.push(wPolyLayer.features[i]);
      }
      wPolyLayer.removeFeatures(toDestroy);
      for (i = toDestroy.length - 1; i >= 0; i--) {
        toDestroy[i].destroy();
      }
	  initBBOXValues();
      Ext.getCmp(wClearID).disable();

}

function toggleBigMap() {
  if(bMapPanel) {
	createMapWindow();
	clearWPoly();
	var pf = getFirstFeature(pPolyLayer);
	if(pf != null)
	  wPolyLayer.addFeatures([pf.clone()]);
	else
	  wMap.zoomToExtent(pMap.getExtent(), true);
  } else {
	createMapPanel();
	clearPPoly();
	var pf = getFirstFeature(wPolyLayer);
	if(pf != null)
	  pPolyLayer.addFeatures([pf.clone()]);
	else
	  pMap.zoomToExtent(wMap.getExtent(), true);
  }
}

function getFirstFeature(player) {
  if(player.features != null && player.features.length >=1)
    return player.features[0];
  return null;
}

/*
  geom: OpenLayers.Geometry
  return: Array of OpenLayers.LonLat
*/
function SMtoLatLon_Geom(geom) {
  var inVertices = geom.getVertices();
  var outVertices = new Array();
  for(i=0; i<inVertices.length; i++)
    outVertices[i] = OpenLayers.Layer.SphericalMercator.inverseMercator(inVertices[i].x, inVertices[i].y);
  return outVertices;
}

/*
  geom: OpenLayers.Geometry in SpericalMecator projection
  return: OpenLayers.Bounds in LatLon projection
*/
function SM2LatLonBnds_Geom(geom) {
  var gb = geom.getBounds();
  return SM2LonLat_Box(gb);
}

/*
  bounds: OpenLayers.Bounds in LatLon projection
  return: OpenLayers.Bounds in SpericalMecator projection
*/
function LonLat2SM_Box(bounds) {
  ll = OpenLayers.Layer.SphericalMercator.forwardMercator(bounds.left, bounds.bottom);
  ur = OpenLayers.Layer.SphericalMercator.forwardMercator(bounds.right, bounds.top);
  return new OpenLayers.Bounds(ll.lon, ll.lat, ur.lon, ur.lat);
}
/*
	lon, lat: OpenLayers.Bounds in LatLon projection
	return: OpenLayers.Bounds in SpericalMecator projection
*/
function LatLong2SM_point(lon, lat)
{
	SM_point = OpenLayers.Layer.SphericalMercator.forwardMercator( lon ,lat);
	return new OpenLayers.LonLat( SM_point.lon, SM_point.lat);
}
/*
  bounds: OpenLayers.Bounds in SpericalMecator projection
  return: OpenLayers.Bounds in LatLon projection
*/
function SM2LonLat_Box(bounds) {
  ll = OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.left, bounds.bottom);
  ur = OpenLayers.Layer.SphericalMercator.inverseMercator(bounds.right, bounds.top);
  return new OpenLayers.Bounds(ll.lon, ll.lat, ur.lon, ur.lat);
}

/*
* w, s, e, n: BBOX in LonLat projection
*/
function updateBBOXValues(w, s, e, n) {
  document.getElementById(westId).value = w;
  document.getElementById(southId).value = s;
  document.getElementById(eastId).value = e;
  document.getElementById(northId).value = n;
  
  //document.getElementById('complete').value = '' + n + ',' + w + ',' + s + ',' + e;
  
}

/*
* bnds: OpenLayers.Bounds in LonLat projection
*/
function updateBBOXValuesByBounds(bnds) {
  document.getElementById(westId).value = bnds.left;
  document.getElementById(southId).value = bnds.bottom;
  document.getElementById(eastId).value = bnds.right;
  document.getElementById(northId).value = bnds.top;

 // document.getElementById('complete').value = '' + bnds.top + ',' + bnds.left + ',' + bnds.bottom + ',' + bnds.right;
  
 // query();
}

function initBBOXValues() {
  document.getElementById(westId).value = '';
  document.getElementById(southId).value = '';
  document.getElementById(eastId).value = '';
  document.getElementById(northId).value = '';
}  

function bboxValuesUpdated(ctrl) {
  var w = parseFloat(document.getElementById(westId).value);
  var s = parseFloat(document.getElementById(southId).value);
  var e = parseFloat(document.getElementById(eastId).value);
  var n = parseFloat(document.getElementById(northId).value);
  
  var v = parseFloat(ctrl.value);
  if(isNaN(v)) {
    ctrl.value = '';
	return false;
  }

  if(isNaN(w) || isNaN(s) || isNaN(e) || isNaN(n) ||
     (w>=e) || (s>=n) ||
	 (w>=180.0) || (e<=-180.0) || (s>=90.0) || (n<=-90.0))
 	return false;

  var nb_sm = LonLat2SM_Box(new OpenLayers.Bounds(w, s, e, n));
  var pointList = [
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.bottom),
    new OpenLayers.Geometry.Point(nb_sm.right, nb_sm.bottom),
    new OpenLayers.Geometry.Point(nb_sm.right, nb_sm.top),
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.top),
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.bottom)];
  var linearRing = new OpenLayers.Geometry.LinearRing(pointList);

  bBoxDrawn = false;

  if(bMapPanel)
    pPolyLayer.addFeatures([new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]))]);
  else
    wPolyLayer.addFeatures([new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]))]);

  document.getElementById('complete').value = '' + n + ',' + w + ',' + s + ',' + e;
  
  //query();
}

// Fire the method to update selected bbox
function spatialBounding(placeList) { 
  if(placeList != "Select state/country"){
    var spatial = document.getElementById("placeList").value; 
    Place1.getPlace(spatial, updateSpatialList);
  }
}

// this function is added for the Place Name feature
// 11/3/2011
function drawPlace(spatial)
{
	spatial = spatial.toLowerCase().replace(/\b[a-z]/g, function(letter) {
    return letter.toUpperCase();});
	
    if(spatial=="")
	{
		alert("Please enter a name");
		return;
	}
	//zoomin(spatial); 
	Place1.getPlace(spatial, updateSpatialList);
	
	
}
function zoomin(address)
{
	if (geocoder) {
                geocoder.getLocation(
	                    address,
	                    function(lonlat) {
							if (lonlat) {
																
								pMap.setCenter(lonlat,6);						
								if(bMapPanel)
									clearPPoly();
								else
									clearWPoly();
								
	                        }
							
						
	                    }
	                );
					
	            }
				
}
// Update selected box
function updateSpatialList(data) {
  if(data==null)
	return;
  var splitter =new Array();
  splitter = data.split(" ");       

  var w = parseFloat(splitter[1]);
  var s = parseFloat(splitter[2]);
  var e = parseFloat(splitter[3]);
  var n = parseFloat(splitter[0]);
  
  if(isNaN(w) || isNaN(s) || isNaN(e) || isNaN(n) ||
     (w>=e) || (s>=n) ||
	 (w>=180.0) || (e<=-180.0) || (s>=90.0) || (n<=-90.0))
 	return false;

  var nb_sm = LonLat2SM_Box(new OpenLayers.Bounds(w, s, e, n));
  var pointList = [
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.bottom),
    new OpenLayers.Geometry.Point(nb_sm.right, nb_sm.bottom),
    new OpenLayers.Geometry.Point(nb_sm.right, nb_sm.top),
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.top),
    new OpenLayers.Geometry.Point(nb_sm.left, nb_sm.bottom)];
  var linearRing = new OpenLayers.Geometry.LinearRing(pointList);

  // Update bbox values
  updateBBOXValues(w, s, e, n);
  
  bBoxDrawn = false;
  if(bMapPanel)
  {
    pPolyLayer.addFeatures([new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]))]);
  }
  else
  {
    wPolyLayer.addFeatures([new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]))]);
  }
	
}


