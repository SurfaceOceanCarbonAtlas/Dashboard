/**
 * MapWidget.js
 * author: Jeremy Malczyk
 * A tool to allow rubberbanding on images and other DOM objects. 
 */

/**
 * The MapWidget class
 */
function MapWidget(args) {
  	for(var f in this)	
		if(typeof this[f] == "function")
			this[f].bindAsEventListener = function(object) {
		var __method = this;
		var args = [];	
		for (var i = 0, length = arguments.length; i < length; i++)
      			args.push(arguments[i]);	
		var object = args.shift();
		return function(event) {
			return __method.apply(object, [event || window.event].concat(args));
		}
	}

   if(typeof args != 'object')
   	args={};
   
   if(args.DOMNode)	
   	this.DOMNode = args.DOMNode;
	else
		this.DOMNode = document.createElement("DIV");
	
	if(args.img)
		this.img = args.img;
	else
		this.img = null;
	if(args.plot_area)
		this.plot_area = args.plot_area;
	else 
		this.plot_area = {};	
						
   if(!this.plot_area.offX)
  		this.plot_area.offX = 0;
  
   if(!this.plot_area.offY)
 	 	this.plot_area.offY = 0;

   if(!this.plot_area.width)
  		this.plot_area.width = this.DOMNode.clientWidth;

  	if(!this.plot_area.height)
  		this.plot_area.height = this.DOMNode.clientHeight;
  						
 	if(args.view)
  		this.setView(args.view);
   else
  		this.setView("xy");
   
   this.pan = false;
   this.panned = {'x' : 0, 'y' : 0};
	
	this.g_box   = "xy";			//"views" ... these are actually MapWidget selection types, not true views.
	this.g_vLine = "y";			
	this.g_hLine = "x";
	this.g_point = "point";
	this.g_vRange = "y_range";
	this.g_hRange = "x_range";

	
	//object to keep track of pixel and grid coordinates
	this.extents = {
		DOMNode : { //area of the widget
			pixel : { x : {min : null,max : null}, y : {min : null, max : null}}
		},
		plot  : { //area of the plot
			pixel : {x : {min : null,max : null},y : {min : null,max : null}},
			grid  : {x : {min : null,max : null},y : {min : null,max : null}} 
		},
		data : { //area the data exists in
			pixel : {x : {min : null,max : null},y : {min : null,max : null}},
			grid  : {x : {min : null,max : null},y : {min : null,max : null}} 
		},
		selection : { //the selected area covered by the rubberBand object
			pixel : {x : {min : null,max : null},y : {min : null,max : null}},
			grid  : {x : {min : null,max : null},y : {min : null,max : null}} 
		}
	}
	if (args.ondraw)
	 	this.ondraw = args.ondraw;
	else
		this.ondraw = function () {};

	if (args.onafterdraw)
	 	this.onafterdraw = args.onafterdraw;
	else
		this.onafterdraw = function () {};  
	
	if (args.onmarginclick)
	 	this.onmarginclick = args.onmarginclick;
	else
		this.onmarginclick = function () {}; 
	this.initImage();
	this.setMaxDrawingArea();
	this.enable();
	


}
MapWidget.prototype.enable = function() {
}
MapWidget.prototype.disable = function() {
}

MapWidget.prototype.destroy = function() {

	this.DOMNode.innerHTML = "";			
	this.DOMNode.onmousedown = null;
	this.DOMNode.onmouseup = null;
	this.DOMNode.onmousemove = null;
	document.onmousedown = null;
	document.onmouseup = null;
	document.onmousemove = null;
	
}


//set the initial extents, this should be called once the image is loaded and page has rendered
MapWidget.prototype.initPixelExtents = function(evt) {
}

//update the extents to reflect a new DOMNode position/size. Call this when a page change shifts the DOMNode. MapWidget.prototype.updatePixelExtents = function(evt) {
	this.setSelectionGridBBox(this.extents.selection.grid);

}


// set the drawing area to the full plot area
MapWidget.prototype.setMaxDrawingArea = function() {
   this.setDataPixXMin(this.getPlotPixXMin());
   this.setDataPixXMax(this.getPlotPixXMin());
   this.setDataPixYMin(this.getPlotPixYMin());
   this.setDataPixYMax(this.getPlotPixYMin());
}

// set the drawing area to specific area (the data region)
MapWidget.prototype.setDrawingArea = function(minX, minY,maxX,maxY) {

}
	

//get the current view
MapWidget.prototype.getView = function () {
  return this.view;
}
 

//sets a new view, and remembers a little about the last one
MapWidget.prototype.setView = function (view) {
	if(this.map) { 	
	 	if (!this.last) {
  			this.last = {};
		
		}
  		switch (view) {
  			case this.g_vLine:
				var drawmode = "vline";  
				this.extents.selection.grid.x.min = this.extents.selection.grid.x.max = (this.extents.selection.grid.x.min+this.extents.selection.grid.x.max)/2;
			      break;
  			case this.g_hLine:
				var drawmode = "hline";
				this.extents.selection.grid.y.min = this.extents.selection.grid.y.max = (this.extents.selection.grid.y.min+this.extents.selection.grid.y.max)/2;								
					break;
  			case this.g_point:
				var drawmode = "point"
				this.extents.selection.grid.x.min = this.extents.selection.grid.x.max = (this.extents.selection.grid.x.min+this.extents.selection.grid.x.max)/2;
				this.extents.selection.grid.y.min = this.extents.selection.grid.y.max = (this.extents.selection.grid.y.min+this.extents.selection.grid.y.max)/2;
					break;
  			case this.g_box:
				var drawmode = "box";
				break;
  		}
  		
	for(key in this.map.controls) {
                    var control = this.map.controls[key];
                    if(drawmode == control.title) {
                        control.activate();
                    } else {
                        control.deactivate();
                    }
                }
  	  this.view = view;
		this.setSelectionGridBBox(this.extents.selection.grid);
	}
}
//get the minimum X pixel value for the DOM container
MapWidget.prototype.getDOMNodePixXMin = function () {
 	return  this.extents.DOMNode.pixel.x.min;
}

//set the minimum X pixel value for the DOM container
MapWidget.prototype.setDOMNodePixXMin = function (x) {	
    this.extents.DOMNode.pixel.x.min = x;
}

//get the minimum Y pixel value for the DOM container
MapWidget.prototype.getDOMNodePixYMin = function () {
   return this.extents.DOMNode.pixel.y.min;
}

//set the minimum Y pixel value for the DOM container
MapWidget.prototype.setDOMNodePixYMin = function (y) {
   this.extents.DOMNode.pixel.y.min = y;
}

//get the maximum X pixel value for the DOM container
MapWidget.prototype.getDOMNodePixXMax = function () {
 	return this.extents.DOMNode.pixel.x.max ;
}

//set the maximum X pixel value for the DOM container
 MapWidget.prototype.setDOMNodePixXMax = function (x) {
  this.extents.DOMNode.pixel.x.max = x;
}

//get the maximum Y pixel value for the DOM container
 MapWidget.prototype.getDOMNodePixYMax = function () {
	return this.extents.DOMNode.pixel.y.max ;
}
//set the maximum Y pixel value for the DOM container
 MapWidget.prototype.setDOMNodePixYMax = function (y) {
  this.extents.DOMNode.pixel.y.max = y;
}

//get the minimum X pixel value for the plot area
MapWidget.prototype.getPlotPixXMin = function () {
 	return  this.extents.plot.pixel.x.min;
}

//set the minimum X pixel value for the plot area
MapWidget.prototype.setPlotPixXMin = function (x) {	
    this.extents.plot.pixel.x.min = x;
}

//get the minimum Y pixel value for the plot area
MapWidget.prototype.getPlotPixYMin = function () {
   return this.extents.plot.pixel.y.min;
}

//set the minimum Y pixel value for the plot area
MapWidget.prototype.setPlotPixYMin = function (y) {
   this.extents.plot.pixel.y.min = y;
}

//get the maximum X pixel value for the plot area
MapWidget.prototype.getPlotPixXMax = function () {
 	return this.extents.plot.pixel.x.max ;
}

//set the maximum X pixel value for the plot area
 MapWidget.prototype.setPlotPixXMax = function (x) {
  this.extents.plot.pixel.x.max = x;
}

//get the maximum Y pixel value for the plot area
 MapWidget.prototype.getPlotPixYMax = function () {
	return this.extents.plot.pixel.y.max ;
}
//set the maximum Y pixel value for the plot area
 MapWidget.prototype.setPlotPixYMax = function (y) {
  this.extents.plot.pixel.y.max = y;
}

//get the minimum X pixel value for the data regionMapWidget.prototype.getDataPixXMin = function () {
 	return  this.extents.data.pixel.x.min;
}

//set the minimum X pixel value for the data region
MapWidget.prototype.setDataPixXMin = function (x) {	
    this.extents.data.pixel.x.min = x;
}

//get the minimum Y pixel value for the data region
MapWidget.prototype.getDataPixYMin = function () {
   return this.extents.data.pixel.y.min;
}

//set the minimum Y pixel value for the data region
MapWidget.prototype.setDataPixYMin = function (y) {
   this.extents.data.pixel.y.min = y;
}

//get the maximum X pixel value for the data region
MapWidget.prototype.getDataPixXMax = function () {
 	return this.extents.data.pixel.x.max ;
}

//set the maximum X pixel value for the data region
 MapWidget.prototype.setDataPixXMax = function (x) {
  this.extents.data.pixel.x.max = x;
}

//get the maximum Y pixel value for the data region
 MapWidget.prototype.getDataPixYMax = function () {
	return this.extents.data.pixel.y.max ;
}
//set the maximum Y pixel value for the data region
 MapWidget.prototype.setDataPixYMax = function (y) {
  this.extents.data.pixel.y.max = y;
}


//get the minimum X pixel value for the selected regionMapWidget.prototype.getSelectionPixXMin = function () {
 	return  this.extents.selection.pixel.x.min;
}

//set the minimum X pixel value for the selected region
MapWidget.prototype.setSelectionPixXMin = function (x) {	
    this.extents.selection.pixel.x.min = x;
}

//get the minimum Y pixel value for the selected region
MapWidget.prototype.getSelectionPixYMin = function () {
   return this.extents.selection.pixel.y.min;
}

//set the minimum Y pixel value for the selected region
MapWidget.prototype.setSelectionPixYMin = function (y) {
   this.extents.selection.pixel.y.min = y;
}

//get the maximum X pixel value for the selected region
MapWidget.prototype.getSelectionPixXMax = function () {
 	return this.extents.selection.pixel.x.max ;
}

//set the maximum X pixel value for the selected region
 MapWidget.prototype.setSelectionPixXMax = function (x) {
  this.extents.selection.pixel.x.max = x;
}

//get the maximum Y pixel value for the selected region
 MapWidget.prototype.getSelectionPixYMax = function () {
	return this.extents.selection.pixel.y.max ;
}
//set the maximum Y pixel value for the selected region
MapWidget.prototype.setSelectionPixYMax = function (y) {
  this.extents.selection.pixel.y.max = y;
}

//get the minimum X grid value for the plot area
MapWidget.prototype.getPlotGridXMin = function () {
 	return  this.extents.plot.grid.x.min;
}

//set the minimum X grid value for the plot area
MapWidget.prototype.setPlotGridXMin = function (x) {	
    this.extents.plot.grid.x.min = x;
}

//get the minimum Y grid value for the plot area
MapWidget.prototype.getPlotGridYMin = function () {
   return this.extents.plot.grid.y.min;
}

//set the minimum Y grid value for the plot area
MapWidget.prototype.setPlotGridYMin = function (y) {
   this.extents.plot.grid.y.min = y;
}

//get the maximum X grid value for the plot area
MapWidget.prototype.getPlotGridXMax = function () {
 	return this.extents.plot.grid.x.max;
}

//set the maximum X grid value for the plot area
MapWidget.prototype.setPlotGridXMax = function (x) {
   this.extents.plot.grid.x.max = x;
}

//get the maximum Y grid value for the plot area
 MapWidget.prototype.getPlotGridYMax = function () {
	return this.extents.plot.grid.y.max;
}
//set the maximum Y grid value for the plot area
 MapWidget.prototype.setPlotGridYMax = function (y) {
  this.extents.plot.grid.y.max = y;
}

//get the minimum X grid value for the data regionMapWidget.prototype.getDataGridXMin = function () {
 	return  this.extents.data.grid.x.min;
}

//set the minimum X grid value for the data region
MapWidget.prototype.setDataGridXMin = function (x) {	
    this.extents.data.grid.x.min = x;
}

//get the minimum Y grid value for the data region
MapWidget.prototype.getDataGridYMin = function () {
   return this.extents.data.grid.y.min;
}

//set the minimum Y grid value for the data region
MapWidget.prototype.setDataGridYMin = function (y) {
   this.extents.data.grid.y.min = y;
}

//get the maximum X grid value for the data region
MapWidget.prototype.getDataGridXMax = function () {
 	return this.extents.data.grid.x.max ;
}

//set the maximum X grid value for the data region
 MapWidget.prototype.setDataGridXMax = function (x) {
  this.extents.data.grid.x.max = x;
}

//get the maximum Y grid value for the data region
 MapWidget.prototype.getDataGridYMax = function () {
	return this.extents.data.grid.y.max ;
}
//set the maximum Y grid value for the data region
 MapWidget.prototype.setDataGridYMax = function (y) {
  this.extents.data.grid.y.max = y;
}


//get the minimum X grid value for the selected regionMapWidget.prototype.getSelectionGridXMin = function () {
	if(this.map.selection)
		if(this.map.selection.left)
	return  this.map.selection.left;
}

//set the minimum X grid value for the selected region
MapWidget.prototype.setSelectionGridXMin = function (x) {	
    this.extents.selection.grid.x.min = x;
}

//get the minimum Y grid value for the selected region
MapWidget.prototype.getSelectionGridYMin = function () {
	if(this.map.selection)
		if(this.map.selection.bottom)
			return  this.map.selection.bottom;
}

//set the minimum Y grid value for the selected region
MapWidget.prototype.setSelectionGridYMin = function (y) {
   this.extents.selection.grid.y.min = y;
}

//get the maximum X grid value for the selected region
MapWidget.prototype.getSelectionGridXMax = function () {
	if(this.map.selection)
		if(this.map.selection.right)
	return  this.map.selection.right;}

//set the maximum X grid value for the selected region
 MapWidget.prototype.setSelectionGridXMax = function (x) {
  this.extents.selection.grid.x.max = x;
}

//get the maximum Y grid value for the selected region
 MapWidget.prototype.getSelectionGridYMax = function () {
	if(this.map.selection)
		if(this.map.selection.top)
	return  this.map.selection.top;
}
//set the maximum Y grid value for the selected region
MapWidget.prototype.setSelectionGridYMax = function (y) {
  this.extents.selection.grid.y.max = y;
}

// initialize an image, or grab a default inmage from ferret
MapWidget.prototype.initImage = function () {

		this.extents.selection.grid = {
			'x' : {'min' : -180, 'max' :180},
			'y' : {'min' : -90, 'max' :90}
		}
		this.extents.data.grid =  {
			'x' : {'min' : -180, 'max' :180},
			'y' : {'min' : -90, 'max' :90}
		}
		
		/* INIT OPENLAYERS MAP */
		mapoptions0   = {
                    		maxExtent: new OpenLayers.Bounds(-180, -90, 180, 90),
				restrictedExtent: new OpenLayers.Bounds(-180, -90, 180, 90)
				};
				
		mapoptions180 = {
				maxExtent: new OpenLayers.Bounds(-400, -90, 400, 90),
				restrictedExtent: new OpenLayers.Bounds(-400, -90, 400, 90)
				};
                 
                this.map = new OpenLayers.Map(this.DOMNode.id, mapoptions0);
		this.map.MW = this;
		
		/* add the layers */
                var wmsLayer = new OpenLayers.Layer.WMS( "OpenLayers WMS", "http://labs.metacarta.com/wms/vmap0", {layers: 'basic'}, {wrapDateLine: true, isBaseLayer:false});
                var vector = new OpenLayers.Layer.Vector("Vector Layer",{ rendererOptions : {  extent :  new OpenLayers.Bounds(-180, -90, 360, 90)}});
		vector.events.register("sketchstarted", vector, function(evt) {evt.object.destroyFeatures();});
		/*vector.events.register("sketchmodified", vector, function(evt) {
				evt.object.map.selection = evt.feature.geometry.bounds;
				evt.object.map.MW.extents.selection.grid.x.min = evt.feature.geometry.bounds.left;
				evt.object.map.MW.extents.selection.grid.x.max = evt.feature.geometry.bounds.right;
				evt.object.map.MW.extents.selection.grid.y.max = evt.feature.geometry.bounds.top;
				evt.object.map.MW.extents.selection.grid.y.min = evt.feature.geometry.bounds.bottom; 
				evt.object.map.MW.ondraw();});*/
		vector.events.register("featureadded", vector, function(evt) {
				evt.object.map.selection = evt.feature.geometry.bounds;  
				evt.object.map.MW.extents.selection.grid.x.min = evt.feature.geometry.bounds.left;
				evt.object.map.MW.extents.selection.grid.x.max = evt.feature.geometry.bounds.right;
				evt.object.map.MW.extents.selection.grid.y.max = evt.feature.geometry.bounds.top;
				evt.object.map.MW.extents.selection.grid.y.min = evt.feature.geometry.bounds.bottom;
				evt.object.map.MW.ondraw(); 
				evt.object.map.MW.onafterdraw();
				});
		vector.events.register("afterfeaturemodified", vector, function(evt) {
				evt.object.map.selection = evt.feature.geometry.bounds;  
				evt.object.map.MW.extents.selection.grid.x.min = evt.feature.geometry.bounds.left;
				evt.object.map.MW.extents.selection.grid.x.max = evt.feature.geometry.bounds.right;
				evt.object.map.MW.extents.selection.grid.y.max = evt.feature.geometry.bounds.top;
				evt.object.map.MW.extents.selection.grid.y.min = evt.feature.geometry.bounds.bottom;
				evt.object.map.MW.ondraw(); 
				evt.object.map.MW.onafterdraw();
		});

		var base = new OpenLayers.Layer.Vector("Base Layer",{isBaseLayer:true, rendererOptions : { extent :  new OpenLayers.Bounds(-180, -90, 360, 90)}});

                this.map.addLayers([base, wmsLayer, vector]);
                
               this.map.addControl(new OpenLayers.Control.MousePosition());
 
               controls = {
                    point: new OpenLayers.Control.DrawFeature(vector, OpenLayers.Handler.Point, {title : "point"}),
                    hline: new OpenLayers.Control.DrawFeature(vector, OpenLayers.Handler.HorizontalPath,{title : "hline" }),
                    vline: new OpenLayers.Control.DrawFeature(vector, OpenLayers.Handler.VerticalPath,{title : "vline"}),
		    box: new OpenLayers.Control.DrawFeature(vector, OpenLayers.Handler.RegularPolygon, { title : "box", handlerOptions: { sides: 4, irregular: true}}), 
                    drag: new OpenLayers.Control.DragFeature(vector,{title:"map_change"}),
		    nav: new OpenLayers.Control.Navigation({title : "map_nav"})
                };
 
                for(var key in controls) {
                    this.map.addControl(controls[key]);
                }
                /*dateline WMS trick*/
                this.map.setCenter(new OpenLayers.LonLat(0, 0), 1);
                this.map.setOptions(mapoptions180);
		this.map.zoomToExtent(new OpenLayers.Bounds(0,  -90, 360, 90), true);
		this.extents.data.grid.x.min=0;
		this.extents.data.grid.x.max=360;
		this.extents.data.grid.y.min=-90;
		this.extents.data.grid.y.max=90;
		this.extents.selection.grid.x.min=0;
		this.extents.selection.grid.x.max=360;
		this.extents.selection.grid.y.min=-90;
		this.extents.selection.grid.y.max=90;		
              
}
MapWidget.prototype.setControl = function (title) {
	if(title=="map_select")
		this.setView(this.view);
	else
		for(key in this.map.controls)
			if(this.map.controls[key].title==title)
				this.map.controls[key].activate();
			else
				this.map.controls[key].deactivate();
		
}

//update the selection region min y grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridYMin = function (v) {
	if(!this.map.selection)
		return false;
	this.map.selection.bottom = v;
	this.extents..selection.grid.y.min = v;
	if(this.extents.selection.grid.x.min!=null&&this.extents.selection.grid.x.max!=null&&this.extents.selection.grid.y.min!=null&&this.extents.selection.grid.y.max!=null)
		if(this.extents.selection.grid.x.min<=this.extents.selection.grid.x.max&&this.extents.selection.grid.y.min<=this.extents.selection.grid.y.max)
			this.setSelectionGridBBox(this.extents.selection.grid);

}
//update the selection region max y grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridYMax = function (v) {
	if(!this.map.selection)
		return false;
	this.map.selection.top = v;
	this.extents.selection.grid.y.max = v;
	if(this.extents.selection.grid.x.min!=null&&this.extents.selection.grid.x.max!=null&&this.extents.selection.grid.y.min!=null&&this.extents.selection.grid.y.max!=null)
		if(this.extents.selection.grid.x.min<=this.extents.selection.grid.x.max&&this.extents.selection.grid.y.min<=this.extents.selection.grid.y.max)
			this.setSelectionGridBBox(this.extents.selection.grid);
}
//update the selection region max x grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridXMax = function (v) {
	if(!this.map.selection)
		return false;
	this.map.selection.right = v;
	this.extents.selection.grid.x.max = v;
	if(this.extents.selection.grid.x.min!=null&&this.extents.selection.grid.x.max!=null&&this.extents.selection.grid.y.min!=null&&this.extents.selection.grid.y.max!=null)
		if(this.extents.selection.grid.x.min<=this.extents.selection.grid.x.max&&this.extents.selection.grid.y.min<=this.extents.selection.grid.y.max)
			this.setSelectionGridBBox(this.extents.selection.grid);

}
MapWidget.prototype.updateSelectionGridXMin = function (v) {
	if(!this.map.selection)
		return false;	
	this.map.selection.bottom = v;
	this.extents.selection.grid.x.min = v;
	if(this.extents.selection.grid.x.min!=null&&this.extents.selection.grid.x.max!=null&&this.extents.selection.grid.y.min!=null&&this.extents.selection.grid.y.max!=null)
		if(this.extents.selection.grid.x.min<=this.extents.selection.grid.x.max&&this.extents.selection.grid.y.min<=this.extents.selection.grid.y.max)
			this.setSelectionGridBBox(this.extents.selection.grid);
}

MapWidget.prototype.setSelectionGridBBox = function (bbox) {
	if(!this.map.selection)
		return false;	
	this.map.selection.bottom = bbox.y.min;
	this.map.selection.top = bbox.y.max;
	this.map.selection.left = bbox.x.min;
	this.map.selection.right = bbox.x.max;
	this.extents.selection.grid = bbox;
	this.map.getLayersByName("Vector Layer")[0].destroyFeatures();	
	this.map.getLayersByName("Vector Layer")[0].addFeatures([new OpenLayers.Feature.Vector(this.map.selection.toGeometry())]);
	this.ondraw();
	this.onafterdraw();
}
//recenter the map on bbox (TODO recenter and zoom on bbox)
MapWidget.prototype.zoomOnBBox = function (bbox) {
	if(!bbox) return false;	
  	this.map.setOptions({maxExtent: new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max), restrictedExtent: new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max)});
	this.map.zoomToExtent(new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max), true);
	this.extents.data.grid.x.min=bbox.x.min;
	this.extents.data.grid.x.max=bbox.x.max;
	this.extents.data.grid.y.min=bbox.y.min;
	this.extents.data.grid.y.max=bbox.y.max;
	this.extents.selection.grid.x.min=bbox.x.min;
	this.extents.selection.grid.x.max=bbox.x.max;
	this.extents.selection.grid.y.min=bbox.y.min;
	this.extents.selection.grid.y.max=bbox.y.max;
	this.setView(this.view);
}

//set the data region to bbox. shrink selection box to fit
MapWidget.prototype.setDataGridBBox = function (bbox) {
	if(!bbox) return false;
 	this.map.setOptions({maxExtent: new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max), restrictedExtent: new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max)});
	this.map.zoomToExtent(new OpenLayers.Bounds(bbox.x.min, bbox.y.min, bbox.x.max, bbox.y.max), true);

	
}
// block out the data free region
MapWidget.prototype.showDataMask = function () {
	
	
}



//move the map left
MapWidget.prototype.panPlot = function (dx,dy) {
 	//deprecated
}
MapWidget.prototype.onPlotLoad = function (evt) { 		
		//this.setDataGridBBox(this.extents.data.grid);
		this.setSelectionGridBBox(this.extents.selection.grid);
}

