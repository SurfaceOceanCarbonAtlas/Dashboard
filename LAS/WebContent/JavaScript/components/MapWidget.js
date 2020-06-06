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
 
   	this.DOMNode.style.overflow = "hidden";
   	this.rubberBand = document.createElement('DIV');
   	this.rubberBand.style.border = "1px solid black";
   	this.rubberBand.style.position = "absolute";
   	this.rubberBand.style.visibility = "hidden";
   	this.rubberBand.style.overflow = "visible";
   	this.rubberBand.style.zIndex =1;
   	this.rubberBand.style.backgroundColor = "yellow";
   	this.rubberBand.style.opacity = 0.50;
	this.rubberBand.style.filter = "alpha(opacity=50)";
   	this.rubberBand_c = document.createElement('DIV');
   	this.rubberBand_c.style.border = "1px solid black";   
	this.rubberBand_c.style.position = "absolute";
	this.rubberBand_c.style.visibility = "hidden";
	this.rubberBand_c.style.zIndex =1;
	this.rubberBand_c.style.overflow = "hidden";
	this.rubberBand_c.style.backgroundColor = "black";
	this.rubberBand.appendChild(this.rubberBand_c);
	this.DOMNode.appendChild(this.rubberBand);
 
  	window.onbeforeresize = this.onbeforeresize.bindAsEventListener(this);
   	window.onresize = this.onafterresize.bindAsEventListener(this);
  
	this.initImage();
	this.setMaxDrawingArea();
	this.enable();
	
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

}
MapWidget.prototype.enable = function() {
	this.enabled=true;
	this.rubberBand_c.onmouseup=this.stopMoving.bindAsEventListener(this);
	this.rubberBand_c.onmousedown=this.startMoving.bindAsEventListener(this);
	this.rubberBand.onmousedown=this.start.bindAsEventListener(this);
	this.DOMNode.onmousedown = this.start.bindAsEventListener(this);
	document.onmouseup = this.stop.bindAsEventListener(this);
}
MapWidget.prototype.disable = function() {
	this.enabled=false;
	this.rubberBand_c.onmouseup=null;
   	this.rubberBand_c.onmousedown=null;
	this.DOMNode.onmousedown = null;
	document.onmouseup = this.stop.bindAsEventListener(this);
}

MapWidget.prototype.destroy = function() {

	if (this.rubberBand_c) {
		this.rubberBand_c.parentNode.removeChild(this.rubberBand_c);
		//delete this.rubberBand_c;
	}
	if (this.rubberBand) {
		this.rubberBand.parentNode.removeChild(this.rubberBand);
		//delete this.rubberBand;
	}

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
	
	this.getDOMNodeOffsets();
	
	this.setDOMNodePixXMin(this.DOMNode.offsets[0]);
	this.setDOMNodePixXMax(this.getDOMNodePixXMin()+this.DOMNode.offsetWidth);
	this.setDOMNodePixYMin(this.DOMNode.offsets[1]);
	this.setDOMNodePixYMax(this.getDOMNodePixYMin()+this.DOMNode.offsetHeight);

	this.setPlotPixXMin(this.getDOMNodePixXMin()+this.plot_area.offX);
	this.setPlotPixXMax(this.getPlotPixXMin()+this.plot_area.width);
	this.setPlotPixYMin(this.getDOMNodePixXMin()+this.plot_area.offY);
	this.setPlotPixYMax(this.getPlotPixXMin()+this.plot_area.height);
	
	//clone for the selection and data pixel extents.
	//TODO: these should be settable from the args object
	
	this.extents.data.pixel = this.clone(this.extents.plot.pixel);
	this.extents.selection.pixel = this.clone(this.extents.plot.pixel);
	if (this.rubberBand.style.visibility != 'hidden') {
		this.displayBox(true);
		this.displayCentralBox(true);
	}
}

//update the extents to reflect a new DOMNode position/size. Call this when a page change shifts the DOMNode. 
MapWidget.prototype.updatePixelExtents = function(evt) {
	var oldOffsets = this.clone(this.DOMNode.offsets);
	var selection = this.clone(this.extents.selection.grid);
	var data = this.extents.data.grid;
	this.getDOMNodeOffsets();
	if(this.DOMNode.offsets != oldOffsets) {
		this.setDataGridBBox(data);
		this.setSelectionGridBBox(selection);
	}
	if (this.rubberBand.style.visibility != 'hidden') {
		this.displayBox(true);
		this.displayCentralBox(true);
	}

}

// set the drawing area to the full plot area
MapWidget.prototype.setMaxDrawingArea = function() {
	this.getDOMNodeOffsets();
   this.setDataPixXMin(this.getPlotPixXMin());
   this.setDataPixXMax(this.getPlotPixXMin());
   this.setDataPixYMin(this.getPlotPixYMin());
   this.setDataPixYMax(this.getPlotPixYMin());
   this.setCBoxSize(6);
   this.setState("drawing");
}

// set the drawing area to specific area (the data region)
MapWidget.prototype.setDrawingArea = function(minX, minY,maxX,maxY) {
   if(minY>maxY) {
		var temp = maxY;	
		maxY = minY;
		minY = temp;
	}
	if(minX>maxX) {
		var temp = maxX;	
		maxX = minX;
		minX = temp;
	}
	if(minY<this.getPlotPixYMin())
		minY=this.getPlotPixYMin();
	if(minX<this.getPlotPixXMin())
		minX=this.getPlotPixXMin();
	if(maxY>this.getPlotPixYMax())
		maxY=this.getPlotPixYMax();
	if(maxX>this.getPlotPixXMax())
		maxX=this.getPlotPixXMax();

	this.setDataPixXMin(minX);
	this.setDataPixXMax(maxX);
	this.setDataPixYMin(minY);
	this.setDataPixYMax(maxY);
}
	
// set the rubberband color
MapWidget.prototype.setViewColor  = function(color) {
  if (this.rubberBand)
    this.rubberBand.style.borderColor = color;
}
// get the drawing state
MapWidget.prototype.getState = function () {
  return this.state;
}

//set the drawing state
 MapWidget.prototype.setState = function (state) {
  this.prevState = this.state;
  this.state = state;
}

//start moving the selection box
MapWidget.prototype.startMoving = function (evt) {
		//this.resetDrawingArea();
		this.setState('moving');
		document.onmousemove = this.moveBox.bindAsEventListener(this); 
		document.onmouseup = this.stopMoving.bindAsEventListener(this); 
		return false;
}

//stop moving the selection box
MapWidget.prototype.stopMoving = function (evt) {
		this.setState(null); 

		if (this.onafterdraw) this.onafterdraw(this);
}

//get the current view
MapWidget.prototype.getView = function () {
  return this.view;
}
 

//sets a new view, and remembers a little about the last one
MapWidget.prototype.setView = function (view) {
 	 	if(this.rubberBand && this.rubberBand_c)
  	{	
  		if (!this.last)
  			this.last = {};
  		if(this.X0&&this.X1&&this.Y0&&this.Y1)
	  		switch (view) {
  				case this.g_vLine:
  					if (this.X0!=this.X1) { 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
  					this.X0 = this.X1 = (this.X1+this.X0)/2;
  					if(this.Y0==this.Y1||(this.Y0==this.getDataPixYMin()&&this.Y1==this.getDataPixYMax())&&this.last.Y0&&this.last.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
  					break;
  				case this.g_hLine:
  					if (this.Y0 != this.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
  					this.Y0 = this.Y1 = (this.Y1+this.Y0)/2;
  					if(this.X0==this.X1||(this.X0==this.getDataPixXMin()&&this.X1==this.getDataPixXMax())&&this.last.X0&&this.last.X1){ 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
  					break;
  				case this.g_hRange:
  					if (this.Y0 != this.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
  					this.Y0 = this.getDataPixYMin();
  					this.Y1 = this.getDataPixYMax();
  					if(this.X0==this.X1||(this.X0==this.getDataPixXMin()&&this.X1==this.getDataPixXMax())&&this.last.X0&&this.last.X1) { 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
			  		break;
  				case this.g_vRange:
  					if (this.X0 != this.X1) { 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
  					this.X0 = this.getDataPixXMin();
  					this.X1 = this.getDataPixXMax();
  					if(this.Y0==this.Y1||(this.Y0==this.getDataPixYMin()&&this.Y1==this.getDataPixYMax())&&this.last.Y0&&this.last.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
  					break;
  				case this.g_point:
  					if (this.X0 != this.X1) { 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
  						
  					if (this.Y0 != this.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
  					
  					this.X0 = this.X1 = (this.X1+this.X0)/2;
  					this.Y0 = this.Y1 = (this.Y1+this.Y0)/2;
  					break;
  				case this.g_box:
  					if(this.Y0==this.Y1||(this.Y0==this.getDataPixYMin()&&this.Y1==this.getDataPixYMax())&&this.last.Y0&&this.last.Y1) { 
  						this.last.Y0 = this.Y0;
  						this.last.Y1 = this.Y1;	
  					}
						
					if(this.X0==this.X1||(this.X0==this.getDataPixXMin()&&this.X1==this.getDataPixXMax())&&this.last.X0&&this.last.X1) { 
  						this.last.X0 = this.X0;
  						this.last.X1 = this.X1;	
  					}
										
 					break;
  			}
  			
  			this.setSelectionPixXMin(this.X0);
  			this.setSelectionPixXMax(this.X1);
  			this.setSelectionPixYMin(this.Y0);
  			this.setSelectionPixYMax(this.Y1);
  			this.getSelectionGrid();  			
  			
  			//if(this.rubberBand.style.visibility != "hidden") {
	  			
  				this.displayBox(true);
  				this.displayCentralBox(true);
  			//}
		
  	 		 //if(this.ondraw && this.rubberBand.style.visibility != 'hidden') 
  	 		 	this.ondraw(this);		 
  		}
  	  this.view = view;
}
//set start X pixel location
 MapWidget.prototype.setX0 = function (x) {
  this.X0 = x;
}
//set stop X pixel location
 MapWidget.prototype.setX1 = function (x) {
  this.X1 = x;
}

//set start Y pixel location
 MapWidget.prototype.setY0 = function (y) {
  this.Y0 = y;
}

//set stop Y pixel location
 MapWidget.prototype.setY1 = function (y) {
  this.Y1 = y;
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
 	return  this.extents.selection.grid.x.min;
}

//set the minimum X grid value for the selected region
MapWidget.prototype.setSelectionGridXMin = function (x) {	
    this.extents.selection.grid.x.min = x;
}

//get the minimum Y grid value for the selected region
MapWidget.prototype.getSelectionGridYMin = function () {
   return this.extents.selection.grid.y.min;
}

//set the minimum Y grid value for the selected region
MapWidget.prototype.setSelectionGridYMin = function (y) {
   this.extents.selection.grid.y.min = y;
}

//get the maximum X grid value for the selected region
MapWidget.prototype.getSelectionGridXMax = function () {
 	return this.extents.selection.grid.x.max ;
}

//set the maximum X grid value for the selected region
 MapWidget.prototype.setSelectionGridXMax = function (x) {
  this.extents.selection.grid.x.max = x;
}

//get the maximum Y grid value for the selected region
 MapWidget.prototype.getSelectionGridYMax = function () {
	return this.extents.selection.grid.y.max ;
}
//set the maximum Y grid value for the selected region
MapWidget.prototype.setSelectionGridYMax = function (y) {
  this.extents.selection.grid.y.max = y;
}

//set the top left of object r
MapWidget.prototype.setBoxStartPosition = function (r, left, top) {
  if (r != null && !isNaN(left) && !isNaN(top)) {
       r.style.left = left + 'px';
       r.style.top = top + 'px';
  }
}

//set the width and height of an object r
MapWidget.prototype.setBoxSize = function (r, w, h) {
  if (r != null) {
			r.style.width = w + 'px';
     		r.style.height = h + 'px'; 
  }
}

//toggle visibility of object r
MapWidget.prototype.setBoxVisible = function (r, showIt) {
  if (r != null) {
    if (showIt == true)
      r.style.visibility = 'visible';
    else
      r.style.visibility = 'hidden';
  }
}

//get the X position of the mouse, within the context of this widget
MapWidget.prototype.getX = function (evt) {
   this.getDOMNodeOffsets();
   var x = null;
   
 	if(document.all)
 		x = evt.clientX + document.documentElement.scrollLeft;
	else
		x = evt.clientX + scrollX;
	
	switch(window.navigator.appName) {
    	case "Microsoft Internet Explorer": x-=2;break;
   	case "Netscape":break;
   	case "Safari": break;
  }	
				
  // if (x < this.getDataPixXMin()) 
  //    x = this.getDataPixXMin();
  // else if (x  > this.getDataPixXMax())  	  
  // 	x = this.getDataPixXMax();
   return x;
}

//get the Y position of the mouse, within the context of this widget
 MapWidget.prototype.getY = function (evt) {
   this.getDOMNodeOffsets();
   
   var y = null;
	if(document.all)
      y = evt.clientY + document.documentElement.scrollTop;
	else
		y= evt.clientY + scrollY;
   
   switch(window.navigator.appName) {
    	case "Microsoft Internet Explorer": y-=2;break;
   	case "Netscape":break;
   	case "Safari": break;
   }	
   
  // if (y < this.getDataPixYMin()) 
//		y = this.getDataPixYMin();
 //  else if (y > this.getDataPixYMax()) 
   //   y = this.getDataPixYMax();
   return y;
} 

//display the rubberband box
MapWidget.prototype.displayBox = function (showIt) {
  
  //check to make sure we have pixel start and end coordinates and if not, use the current selection
  if(this.X0==null||this.X1==null||this.Y0==null||this.Y1==null) {
  		this.X0 = this.getSelectionPixXMin();
  		this.X1 = this.getSelectionPixXMax();
   	this.Y0 = this.getSelectionPixYMin(); 
   	this.Y1 = this.getSelectionPixYMax();
	} 
   this.setSelectionPixXMin(Math.min(this.X0, this.X1));
   this.setSelectionPixXMax(Math.max(this.X0, this.X1));
   this.setSelectionPixYMin(Math.min(this.Y0, this.Y1));
   this.setSelectionPixYMax(Math.max(this.Y0, this.Y1));
   
   var left =  this.getSelectionPixXMin();
   var top = this.getSelectionPixYMin();
   var w = Math.abs(this.getSelectionPixXMax() - this.getSelectionPixXMin());
   var h = Math.abs(this.getSelectionPixYMax() - this.getSelectionPixYMin());

   this.setBoxStartPosition(this.rubberBand, left, top);
   this.setBoxSize(this.rubberBand, w, h);
   		
   this.setBoxBorderStyle(this.rubberBand, "solid");
   this.setBoxVisible(this.rubberBand, showIt);
 
   this.getSelectionGrid();
		
}

// get the left pixel of an object
MapWidget.prototype.getBoxStartX = function (o) {
  v = o.style.left;
  i = v.indexOf("px");
  return parseInt(v.substring(0, i));
}

// get the top pixel of an object
MapWidget.prototype.getBoxStartY = function (o) {
  v = o.style.top;
  i = v.indexOf("px");
  return parseInt(v.substring(0, i));
}

 MapWidget.prototype.getBoxWidth = function (o) {
  v = o.style.width;
  i = v.indexOf("px");
  return parseInt(v.substring(0, i));
}

 MapWidget.prototype.getBoxHeight = function (o) {
  v = o.style.height;
  i = v.indexOf("px");
  return parseInt(v.substring(0, i));
}

//assume the central box is always a square
 MapWidget.prototype.getCBoxSize = function (o) {
  v = o.style.width;
  i = v.indexOf("px");
  return parseInt(v.substring(0, i));
}

 MapWidget.prototype.setCBoxSize = function (w) {
  
  if (this.rubberBand_c) {
      this.rubberBand_c.style.height = w + "px";
      this.rubberBand_c.style.width = w + "px";
      this.rubberBand_c.style.cursor = "pointer; hand;";
      //this.rubberBand_c.style.fontSize = "0pt";
   }
}

 MapWidget.prototype.displayCentralBox = function (showIt) {
   if(this.extents.selection.pixel.x.min==null||this.extents.selection.pixel.x.max==null||this.extents.selection.pixel.y.min==null||this.extents.selection.pixel.y.max==null) 
  	return false;
  var s = this.getCBoxSize(this.rubberBand_c);
  if (Math.abs(this.extents.selection.pixel.x.max - this.extents.selection.pixel.x.min) < s && Math.abs(this.extents.selection.pixel.y.max - this.extents.selection.pixel.y.min) < s) {
   var x = -1*(s-Math.abs(this.extents.selection.pixel.x.max - this.extents.selection.pixel.x.min))/2;
   var y = -1*(s-Math.abs(this.extents.selection.pixel.y.max - this.extents.selection.pixel.y.min))/2;
  } else {
  	var x = (Math.abs(this.extents.selection.pixel.x.max - this.extents.selection.pixel.x.min)/2) - s/2;
  	var y = (Math.abs(this.extents.selection.pixel.y.max - this.extents.selection.pixel.y.min)/2) - s/2;
  }
  this.setBoxStartPosition(this.rubberBand_c, x, y);
  this.setBoxVisible(this.rubberBand_c, showIt);
 
}

// draw the selection box
MapWidget.prototype.drawBox = function (evt) {
   if (this.getState() == null || this.view != this.g_box)
	return;
   	
  	this.displayBox(true);   
   	this.displayCentralBox(true);	
  	
  	if (this.ondraw) this.ondraw(this);
}

// draw a horizontal line
MapWidget.prototype.drawHLine = function (evt) {
   window.status = this.getView();

   if (this.getState() == null || this.getView() != this.g_hLine)
	return;
   
   this.Y1 = this.Y0;
   
   this.displayBox(true);   
   this.displayCentralBox(true);
  
  	if (this.ondraw) this.ondraw(this);

}

// draw a vertical line
MapWidget.prototype.drawVLine = function (evt) {
   window.status = this.getView();

   if (this.getState() == null || this.getView() != this.g_vLine)
	return;
   
   this.X1 = this.X0;
   
   this.displayBox(true);   
   this.displayCentralBox(true);

  	
  	if (this.ondraw) this.ondraw(this);
}

// draw a point
MapWidget.prototype.drawPoint = function (evt) {
   window.status = this.getView();

   if (this.getState() == null || this.getView() != this.g_point)
	return;
   this.X0 = this.X1;
   this.Y0 = this.Y1;
   
   this.displayBox(true);
	this.displayCentralBox(true);
  
  	if (this.ondraw) this.ondraw(this);
}

// draw an x or y range
 MapWidget.prototype.drawRange = function (evt) {
   window.status = this.getView();

   if (this.getState() == null || (this.getView() != this.g_vRange && this.getView() != this.g_hRange)) {
     alert("incorrect view");
     return;
   }
   
   
   if (view == this.g_vRange) {
     this.X0 = this.getDataPixelXMin();
     this.X1 = this.getDataPixelXMax();     
     this.Y1 = this.getY(evt);
   } else {
     this.Y0 = this.getDataPixelYMin();
     this.Y1 = this.getDataPixelYMax();
     this.X1 = this.getX(evt);
   }
	
	this.displayBox(true);
   this.displayCentralBox(true);  
  	if (this.ondraw) this.ondraw(this);
}

// set the border style of an object obj
MapWidget.prototype.setBoxBorderStyle = function (obj, s) {
  obj.style.borderStyle = s;
}

// set the border color of an object obj
MapWidget.prototype.setBoxBorderColor = function (obj, c) {
  obj.style.borderColor = c;
}

// start drawing (code branch of IE/FF event model differences)
MapWidget.prototype.start = function (evt) {
	switch(this.getState()){
		case null:
			if(document.all) { 
				if(this.getX(evt)> this.rubberBand.offsetLeft + this.rubberBand_c.offsetLeft &&
					this.getX(evt)< this.rubberBand.offsetLeft + this.rubberBand_c.offsetLeft + this.rubberBand_c.offsetWidth &&
					this.getY(evt)> this.rubberBand.offsetTop + this.rubberBand_c.offsetTop &&
					this.getY(evt)< this.rubberBand.offsetTop + this.rubberBand_c.offsetTop + this.rubberBand_c.offsetHeight)
						this.startMoving(evt);
					else
						this.startDrawing(evt);
					}
			else if(evt.target == this.rubberBand_c) 
				this.startMoving(evt);
			else
				this.startDrawing(evt);
			break;
		case "drawing":
			this.startDrawing(evt);
			break;
		case "moving":
			this.startMoving(evt);
			break;
	}
}

//start drawing a selection feature
 MapWidget.prototype.startDrawing = function (evt) {

    if(document.all) {
    	document.onselectstart="return false;";
    	evt.returnValue = false;
    	evt.cancelBubble = true;
    	//alert(evt.type);
    }
   else {evt.preventDefault(); evt.stopPropagation()};	
   if (this.getState() == "moving") {	 
    	 document.onmousemove = this.moveBox.bindAsEventListener(this);
    	 return false;
   }
	//this.resetDrawingArea(); 
	this.setState("drawing");
   document.onmouseup = this.stopDrawing.bindAsEventListener(this);        
   this.X0 = this.getX(evt);
   this.Y0 = this.getY(evt);  
	if(this.X0 > this.getDataPixXMax() || this.X0 < this.getDataPixXMin()  || this.Y0 > this.getDataPixYMax() || this.Y0 < this.getDataPixYMin()) {
		if(this.onmarginclick) this.onmarginclick();
		this.stopDrawing();
		return;
	}
		
	
	this.displayCentralBox(false);
	this.setBoxStartPosition(this.rubberBand, this.X0, this.Y0);
   this.setBoxSize(this.rubberBand, 0, 0);  
   this.setBoxVisible(this.rubberBand, true);
   //this.clipOverflow();
   if (this.getView()==this.g_point)
		this.drawPoint(evt);
	else
		document.onmousemove = this.draw.bindAsEventListener(this);
	
	return false;
	
}

// draw a selection feature
MapWidget.prototype.draw = function (evt) {
	 if(document.all) {
    	evt.returnValue = false;
    	evt.cancelBubble = true;
    	document.onselectstart="return false;";
    	this.plot.onselectstart="return false;";
    }
    else {evt.preventDefault(); evt.stopPropagation()};
  this.X1 = this.getX(evt);
  this.Y1 = this.getY(evt);
	if(this.X1 > this.getDataPixXMax())
		this.X1 = this.getDataPixXMax();
	if(this.X1 < this.getDataPixXMin())
		this.X1 = this.getDataPixXMin();
	if(this.Y1 > this.getDataPixYMax())
		this.Y1 = this.getDataPixYMax();
	if(this.Y1 < this.getDataPixYMin())
		this.Y1 = this.getDataPixYMin();



	if (this.view == this.g_box)
     this.drawBox(evt);
   else if (this.view == this.g_hLine)
  		this.drawHLine(evt);   
   else if (this.view == this.g_vLine)
     	this.drawVLine(evt);     
   else if (this.view == this.g_point)
     	this.drawPoint(evt);
   else if (this.view == this.g_hRange || this.view == this.g_vRange)
   	this.drawRange(evt); 
   else
     alert("unknown view");
   return false;
}

// stop drawing the selection box
MapWidget.prototype.stopDrawing = function (evt) {
   if (this.getState() == "moving") {
     this.setState(null);
     return;
   }   

   this.setState(null);
   document.onmousemove = null; 
	document.onmouseup = null; 
	if (this.onafterdraw) this.onafterdraw(this);
}


MapWidget.prototype.stop = function (evt) {
	this.setState(null);
}


 MapWidget.prototype.moveBox = function (evt) {
   if (this.getState() != "moving")
     return;
 	 if(document.all) {
    	evt.returnValue = false;
    	evt.cancelBubble = true;
    	document.onselectstart="return false;";
    	this.plot.onselectstart="return false;";
    }
    else {evt.preventDefault(); evt.stopPropagation()};

   var r_w = this.getBoxWidth(this.rubberBand);
   var r_h = this.getBoxHeight(this.rubberBand);
   var m_x = this.getX(evt);
   var m_y = this.getY(evt);
   
   //This is needed for the FireFox
   r_w = isNaN(r_w)? 0 : r_w;
   r_h = isNaN(r_h)? 0 : r_h;   

   //moving the box
   m_x0 = m_x - r_w/2;
   if (m_x - r_w/2 < this.getDataPixXMin())   
  	  m_x0 = this.getDataPixXMin();
   else if (m_x + r_w/2 > this.getDataPixXMax()) 
	m_x0 =this.getDataPixXMax() - r_w;
   
   
   m_y0 = m_y - r_h/2;
   if (m_y - r_h/2 < this.getDataPixYMin())
     m_y0 =  this.getDataPixYMin();
   else if (m_y + r_h/2 > this.getDataPixYMax()) 		m_y0 = this.getDataPixYMax() - r_h;
   
   this.Y0 = m_y0;
   this.Y1 = m_y0 + r_h;
	this.X0 = m_x0;
   this.X1 = m_x0 + r_w;
   
   this.displayBox(true);
   this.displayCentralBox(true);
    
	 if (this.ondraw) this.ondraw(this); 
   
    document.onmouseup = null; 
  
}
//clip to the plot area
 MapWidget.prototype.clipOverflow = function () {
	this.clipOverflow(this.rubberBand);
	this.clipOverflow(this.rubberBand_c);

}
 MapWidget.prototype.clipBoxOverflow = function (obj) {	
   var r_x0 = obj.offsetLeft;
   var r_y0 = obj.offsetTop;
   var r_w = this.getBoxWidth(obj);
   var r_h = this.getBoxHeight(obj);

	var clipStr = "rect(";
   if(r_y0 < this.DOMNode.offsets[1]+this.plot_area.offY)
   	clipStr += (this.DOMNode.offsets[1]+this.plot_area.offY - r_y0) + "px ";
   else
   	clipStr += "auto ";
  
    if(r_x0 + r_w > this.DOMNode.offsets[0]+this.plot_area.offX+this.plot_area.width)
   	clipStr += (this.DOMNode.offsets[0]+this.plot_area.offX+this.plot_area.width-r_x0) + "px ";
   else
   	clipStr += "auto ";
   	
   if(r_y0 + r_h > this.DOMNode.offsets[1]+this.plot_area.offY+this.plot_area.height)
   	clipStr += (this.DOMNode.offsets[1]+this.plot_area.offY+this.plot_area.height-r_y0) + "px ";
   else
   	clipStr += "auto ";
   	

   if(r_x0 < this.DOMNode.offsets[0]+this.plot_area.offX)
   	clipStr += (this.DOMNode.offsets[0]+this.plot_area.offX - r_x0) + "px)";
   else
   	clipStr += "auto)";

	obj.style.clip = clipStr;
   	
}

// initialize an image, or grab a default inmage from ferret
MapWidget.prototype.initImage = function () {
	if(!this.plot) {
		this.plot = document.createElement("IMG");
		this.DOMNode.appendChild(this.plot);
	}	
	 this.plot.id='plot';

	if (this.img) {
		this.plot.src = this.img.src;
		this.plot.onerror = "javascript:setTimeout('this.src = this.src',2000)}";
		this.plot.width = this.img.width;
		this.DOMNode.style.width = this.img.width + 'px';
		this.plot.height =  this.img.height;
		this.DOMNode.style.height = this.img.height + 'px';
		this.plot.width = this.img.width;
		this.plot.height = this.img.height;
		this.plot.GALLERYIMG="no";
		this.plot.onload=this.updatePixelExtents.bindAsEventListener(this);
		this.plot.style.zIndex = 2;
		this.extents.selection.grid = this.clone(this.img.extent); 
		this.extents.data.grid = this.clone(this.img.extent);
		this.extents.plot.grid = this.clone(this.img.extent);
	}
	else {
		this.plot.src = 'java_0_world.gif';
		this.plot.width = this.DOMNode.clientWidth;
		this.plot.height = this.DOMNode.clientHeight;
		this.plot.onload=this.updatePixelExtents.bindAsEventListener(this);
		this.plot.GALLERYIMG="no"; 
		this.extents.plot.grid = {
			'x' : {'min' : -180, 'max' :180},
			'y' : {'min' : -90, 'max' :90}
		}
		this.extents.selection.grid = {
			'x' : {'min' : -180, 'max' :180},
			'y' : {'min' : -90, 'max' :90}
		}
		this.extents.data.grid =  {
			'x' : {'min' : -180, 'max' :180},
			'y' : {'min' : -90, 'max' :90}
		}
		
	}
	
	this.initPixelExtents();
	this.setPlotPixXMin(this.getDOMNodePixXMin() + this.plot_area.offX);
	this.setPlotPixYMin(this.getDOMNodePixYMin() + this.plot_area.offY);
	this.setPlotPixXMax(this.getPlotPixXMin() + this.plot_area.width);
	this.setPlotPixYMax(this.getPlotPixYMin() + this.plot_area.height);
	this.extents.data.pixel = this.clone(this.extents.plot.pixel);
	this.extents.selection.pixel = this.clone(this.extents.plot.pixel);
}

//get and set the selection area grid coordinates
MapWidget.prototype.getSelectionGrid = function () {
	
	if(this.plot_area.invertX){
		this.setSelectionGridXMin((Math.round((this.getPlotGridXMin() + (this.getSelectionPixXMax()-this.getPlotPixXMax())*-1*this.getXPixRes())*10000))/10000);
		this.setSelectionGridXMax((Math.round((this.getPlotGridXMin() + (this.getSelectionPixXMin()-this.getPlotPixXMax())*-1*this.getXPixRes())*10000))/10000);
	}
	else {
		this.setSelectionGridXMin((Math.round((this.getPlotGridXMin() + (this.getSelectionPixXMin()-this.getPlotPixXMin())*this.getXPixRes())*1000))/1000);
		this.setSelectionGridXMax((Math.round((this.getPlotGridXMin() + (this.getSelectionPixXMax()-this.getPlotPixXMin())*this.getXPixRes())*1000))/1000);
	}
	
	if(this.plot_area.invertY){
		this.setSelectionGridYMax((Math.round((this.getPlotGridYMax() - (this.getSelectionPixYMax()-this.getPlotPixYMax())*-1*this.getYPixRes())*1000))/1000);
		this.setSelectionGridYMin((Math.round((this.getPlotGridYMax() - (this.getSelectionPixYMin()-this.getPlotPixYMax())*-1*this.getYPixRes())*1000))/1000);
	} else {
		this.setSelectionGridYMax((Math.round((this.getPlotGridYMax() - (this.getSelectionPixYMin()-this.getPlotPixYMin())*this.getYPixRes())*1000))/1000);
		this.setSelectionGridYMin((Math.round((this.getPlotGridYMax() - (this.getSelectionPixYMax()-this.getPlotPixYMin())*this.getYPixRes())*1000))/1000);
	}	
	return this.extents.selection.grid;
}

//determine the pixel resolution of the x axis
MapWidget.prototype.getXPixRes = function () {
	if (!this.pixRes)
		this.pixRes = {};
	if (!this.pixRes.x)
		this.pixRes.x = 0;
	this.pixRes.x = ((this.extents.plot.grid.x.max - this.extents.plot.grid.x.min)/this.plot_area.width);
	return this.pixRes.x;
}

//determine the pixel resolution of the y axis
MapWidget.prototype.getYPixRes = function () {
	if (!this.pixRes)
		this.pixRes = {};
	if (!this.pixRes.y)
		this.pixRes.y = 0;
	this.pixRes.y = ((this.extents.plot.grid.y.max - this.extents.plot.grid.y.min)/this.plot_area.height);
	return this.pixRes.y;
}

//get the offsets of the DOMNode, and update the DOMNode and plot pixel extents
MapWidget.prototype.getDOMNodeOffsets = function () {
	var curleft = 0;
	var curtop = 0;
	if (this.DOMNode.offsetParent) {
		curleft = this.DOMNode.offsetLeft
		curtop = this.DOMNode.offsetTop
		var obj = this.DOMNode;
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	this.DOMNode.offsets = [curleft,curtop];
	
	this.setDOMNodePixXMin(this.DOMNode.offsets[0]);
	this.setDOMNodePixXMax(this.DOMNode.offsets[0] + this.DOMNode.offsetWidth);
	this.setDOMNodePixYMin(this.DOMNode.offsets[1]);
	this.setDOMNodePixYMax(this.DOMNode.offsets[1] + this.DOMNode.offsetHeight);
	
	this.setPlotPixXMin(this.getDOMNodePixXMin()+this.plot_area.offX);
	this.setPlotPixXMax(this.getPlotPixXMin() + this.plot_area.width);
	this.setPlotPixYMin(this.getDOMNodePixYMin()+this.plot_area.offY);
	this.setPlotPixYMax(this.getPlotPixYMin() + this.plot_area.height);	
}

//update the selection region min y grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridYMin = function (v) {
	this.X0 = null;
	this.Y0 = null;
	this.X1 = null;
	this.Y1 = null;
	
	var Y =  this.getPlotPixYMax()-(v-this.getPlotGridYMin())/this.getYPixRes();
	if (Y >  this.getDataPixYMax()) {
		Y = this.getDataPixYMax();
		v = this.getDataGridYMin();
	 } else if (Y <  this.getDataPixYMin()) {
		Y = this.getDataPixYMin();
		v = this.getDataGridYMax();
	}
		
	
	if(Y<this.getSelectionPixYMin()) {
		this.setSelectionPixYMax(this.getSelectionPixYMin());
		this.setSelectionGridYMin(this.getSelectionGridYMax());		
		this.setSelectionGridYMax(v)		
		this.setSelectionPixYMin(Y);
	
	}
	else {
		this.setSelectionPixYMax(Y);
		this.setSelectionGridYMin(v);	
	}

	if (this.view==this.g_point || this.view == this.g_hLine) {
		this.setSelectionPixYMax(Y);
		this.setSelectionPixYMin(Y);
		this.setSelectionGridYMax(v);
		this.setSelectionGridYMin(v);	
	}

	this.displayBox(true);
	this.displayCentralBox(true);
	
	if (this.ondraw) this.ondraw(this);
	if (this.onafterdraw) this.onafterdraw(this);	

}


//update the selection region max y grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridYMax = function (v) {
	this.X0 = null;
	this.Y0 = null;
	this.X1 = null;
	this.Y1 = null;

	
	var Y = this.getPlotPixYMax()-(v-this.getPlotGridYMin())/this.getYPixRes();
	if (Y > this.getDataPixYMax()) {
		Y = this.getDataPixYMax();
		v = this.getDataGridYMin();	
	}
	else if (Y < this.getDOMNodePixYMin())
		Y = this.getDataPixYMin();
	
	if(Y>this.getSelectionPixYMax()) {
		this.setSelectionPixYMin(this.getSelectionPixYMax());
		this.setSelectionPixYMax(Y);	
	}
	else 
		this.setSelectionPixYMin(Y);

	if (this.view==this.g_point || this.view == this.g_hLine) {
		this.setSelectionPixYMax(Y);
		this.setSelectionPixYMin(Y);
		this.setSelectionGridYMax(v);
		this.setSelectionGridYMin(v);	
	}
	
	this.displayBox(true);
	this.displayCentralBox(true);
	
	if (this.ondraw) this.ondraw(this);
	if (this.onafterdraw) this.onafterdraw(this);	

}

//update the selection region max x grid and pixel coordinates for a given grid coordinate v
MapWidget.prototype.updateSelectionGridXMax = function (v) {
	this.X0 = null;
	this.Y0 = null;
	this.X1 = null;
	this.Y1 = null;

	
	var X = this.getPlotPixXMax()+(v-this.getPlotGridXMax())/this.getXPixRes();
	if (X > this.getDataPixXMax()){
		X = this.getDataPixXMax();
		v = this.getDataGridXMax();	
	}
	else if (X < this.getDataPixXMin()) {
		X = this.getDataPixXMin();
		v = this.getDataGridXMin();	
	}
	
	if(X<this.getSelectionPixXMin()) {
		this.setSelectionPixXMax(this.getSelectionPixXMin());
		this.setSelectionPixXMin(X);
		this.setSelectionGridXMin(v);	
	}
	else  {
		this.setSelectionPixXMax(X);
		this.setSelectionGridXMax(v);	
	}

	if (this.view==this.g_point || this.view == this.g_vLine) {
		this.setSelectionPixXMax(X);
		this.setSelectionPixXMin(X);
		this.setSelectionGridXMin(v);
		this.setSelectionGridXMax(v);
	}
		
	this.displayBox(true);
	this.displayCentralBox(true);
	
	if (this.ondraw) this.ondraw(this);
	if (this.onafterdraw) this.onafterdraw(this);	
}
MapWidget.prototype.updateSelectionGridXMin = function (v) {
	this.X0 = null;
	this.Y0 = null;
	this.X1 = null;
	this.Y1 = null;

	
	var X = this.getPlotPixXMax()+(v-this.getPlotGridXMax())/this.getXPixRes();
	if (X > this.getDataPixXMax()) {
		X = this.getDataPixXMax();
		v = this.getDataGridXMax();
	}
	else if (X < this.getDataPixXMin()) {
		X = this.getDataPixXMin();
		v = this.getDataGridXMin();
	}
	
	if(X>this.getSelectionPixXMax()) {
		this.setSelectionPixXMin(this.getSelectionPixXMax());
		this.setSelectionPixXMax(X);
		this.setSelectionGridXMax(v);	
	}
	else  {
		this.setSelectionPixXMin(X);
		this.setSelectionGridXMin(v);
	}
	
	if (this.view==this.g_point || this.view == this.g_vLine) {
		this.setSelectionPixXMax(X);
		this.setSelectionPixXMin(X);
		this.setSelectionGridXMax(v);
		this.setSelectionGridXMin(v);
	}

	this.displayBox(true);
	this.displayCentralBox(true);
	
	if (this.ondraw) this.ondraw(this);
	if (this.onafterdraw) this.onafterdraw(this);	
}

MapWidget.prototype.setSelectionGridBBox = function (bbox) {
	if(!bbox) return false;
	
	this.extents.selection.grid = this.clone(bbox);
	
	var X0 = this.extents.plot.pixel.x.max+(this.getSelectionGridXMin()-this.extents.plot.grid.x.max)/this.getXPixRes();
	var X1 = this.extents.plot.pixel.x.max+(this.getSelectionGridXMax()-this.extents.plot.grid.x.max)/this.getXPixRes();
	var Y1 = this.extents.plot.pixel.y.max-(this.getSelectionGridYMin()-this.extents.plot.grid.y.min)/this.getYPixRes();
	var Y0 = this.extents.plot.pixel.y.max-(this.getSelectionGridYMax()-this.extents.plot.grid.y.min)/this.getYPixRes();
	
	if(Y0>Y1) {
		var temp = Y1	
		Y1 = Y0;
		Y0 = temp;
	}
	if(X0>X1) {
		var temp = X1	
		X1 = X0;
		X0 = temp;
	}
	this.X0 = X0;
	this.X1 = X1;
	this.Y0 = Y0;
	this.Y1 = Y1;
	
	//refresh the view
	//var view = this.view;	
	//this.view = "";
	//this.setView(view);
	if (this.rubberBand.style)
	if (this.rubberBand.style.visibility != 'hidden') {
		this.displayBox(true);
		this.displayCentralBox(true);
	}

	//if(this.ondraw)this.ondraw(this);
	//if(this.onafterdraw)this.onafterdraw(this);

}

//recenter the map on bbox (TODO recenter and zoom on bbox)
MapWidget.prototype.zoomOnBBox = function (bbox) {
  
	var bbox_width  = (bbox.x.max-bbox.x.min);
	var bbox_height =  (bbox.y.max-bbox.y.min);
	var plot_width  =(this.getPlotGridXMax()-this.getPlotGridXMin());
	var plot_height = (this.getPlotGridYMax()-this.getPlotGridYMin());
	var bbox_aspect = (bbox_height)/(bbox_width);
	var plot_aspect = (plot_height)/(plot_width);
	var bbox_screen_aspect = (bbox_height*this.getXPixRes())/(bbox_width*this.getYPixRes());
	var plot_screen_aspect = (plot_height*this.getXPixRes())/(plot_width*this.getYPixRes());
	var bbox_cx = (bbox.x.max+bbox.x.min)/2;
	var bbox_cy = (bbox.y.max+bbox.y.min)/2;

	
	if(bbox_screen_aspect>plot_screen_aspect) {
		this.setPlotGridYMin(bbox.y.min);
	        this.setPlotGridYMax(bbox.y.max);
		this.setPlotGridXMin(bbox_cx-bbox_height/(plot_aspect*2));
		this.setPlotGridXMax(bbox_cx+bbox_height/(plot_aspect*2));	
		//this.setDataGridYMin(bbox.y.min);
	        //this.setDataGridYMax(bbox.y.max);
		//this.setDataGridXMin(bbox_cx-bbox_height/(plot_aspect*2));
		//this.setDataGridXMax(bbox_cx+bbox_height/(plot_aspect*2));	
	} else {
		this.setPlotGridXMin(bbox.x.min);
		this.setPlotGridXMax(bbox.x.max);
		this.setPlotGridYMin(bbox_cy-bbox_width*(plot_aspect/2));
		this.setPlotGridYMax(bbox_cy+bbox_width*(plot_aspect/2));	
		//this.setDataGridXMin(bbox.x.min);
		//this.setDataGridXMax(bbox.x.max);
		//this.setDataGridYMin(bbox_cy-bbox_width*(plot_aspect/2));
		//this.setDataGridYMax(bbox_cy+bbox_width*(plot_aspect/2));	
	}
	
        var selection = {"x":{},"y":{}};
	if(this.extents.plot.grid.x.min>this.extents.selection.grid.x.min)
		selection.x.min=this.extents.plot.grid.x.min;
	else
		selection.x.min=this.extents.selection.grid.x.min;
	if(this.extents.plot.grid.x.max<this.extents.selection.grid.x.max)
		selection.x.max=this.extents.plot.grid.x.max;
	else
		selection.x.max=this.extents.selection.grid.x.max;
	if(this.extents.plot.grid.y.min>this.extents.selection.grid.y.min)
		selection.y.min=this.extents.plot.grid.y.min;
	else
		selection.y.min=this.extents.selection.grid.y.min;
	if(this.extents.plot.grid.y.max<this.extents.selection.grid.y.max)
		selection.y.max=this.extents.plot.grid.y.max;
	else
		selection.y.max=this.extents.selection.grid.y.max




	this.setSelectionGridBBox(selection);
	this.setView(this.view);
        var req = new LASRequest();
	
	req.removeVariables();
	req.removeRegion();
	req.setOperation("xy_map");
	req.setRange("x",this.getPlotGridXMin(),this.getPlotGridXMax());
	req.setRange("y",this.getPlotGridYMin(),this.getPlotGridYMax());
	this.plot.src = "ProductServer.do?xml=" + escape(req.getXMLText()) + "&stream=true&stream_ID=plot_image";
	
   this.plot.onload = this.updatePixelExtents.bindAsEventListener(this);
   
	
}

//set the data region to bbox. shrink selection box to fit
MapWidget.prototype.setDataGridBBox = function (bbox) {
	if(!bbox) return false;
	

	var minx = this.getPlotPixXMax()+(bbox.x.min-this.getPlotGridXMax())/this.getXPixRes();
	var maxx = this.getPlotPixXMax()+(bbox.x.max-this.getPlotGridXMax())/this.getXPixRes();
	var maxy = this.getPlotPixYMax()-(bbox.y.min-this.getPlotGridYMin())/this.getYPixRes();
	var miny = this.getPlotPixYMax()-(bbox.y.max-this.getPlotGridYMin())/this.getYPixRes();
	this.extents.data.grid = this.clone(bbox);
	this.extents.data.pixel = {
		x : {min : minx, max :maxx},
		y : {min : miny, max :maxy}
	}
	if(minx<this.getPlotPixXMin())
		minx=this.clone(this.extents.plot.pixel.x.min);	
	if(miny<this.getPlotPixYMin())
		miny=this.clone(this.extents.plot.pixel.y.min);	
	if(maxx>this.getPlotPixXMax())
		minx=this.clone(this.extents.plot.pixel.x.max);	
	if(maxy>this.getPlotPixYMax())
		minx=this.clone(this.extents.plot.pixel.y.max);	

	this.setDrawingArea(minx,miny,maxx,maxy);
	this.showDataMask();
	
}
// block out the data free region
MapWidget.prototype.showDataMask = function () {
	//this.updatePixelExtents();
	if(!this.drawingMask) {
		this.drawingMask = document.createElement('div');
		this.drawingMask.style.zIndex = 0;
	   this.drawingMask.style.opacity = 0.7;
	   this.drawingMask.style.filter="alpha(opacity=70)";
		this.drawingMask.style.position = 'absolute';
		this.DOMNode.appendChild(this.drawingMask);	
	}
	
	if(Math.round(this.getDataPixYMin()-this.getPlotPixYMin())>1)		
		this.drawingMask.style.borderTop = Math.round(this.getDataPixYMin()-this.getPlotPixYMin()) + "px solid gray";
	else
		this.drawingMask.style.borderTop = "0pt";
	if(Math.round(this.getDataPixXMin()-this.getPlotPixXMin())>1)
		this.drawingMask.style.borderLeft = Math.round(this.getDataPixXMin()-this.getPlotPixXMin()) + "px solid gray";
	else
		this.drawingMask.style.borderLeft = "0pt";
	if(Math.round(this.getPlotPixXMax()-this.getDataPixXMax())>1)
		this.drawingMask.style.borderRight = Math.round(this.getPlotPixXMax()-this.getDataPixXMax()) + "px solid gray";
	else
		this.drawingMask.style.borderRight = "0pt";
	if(Math.round(this.getPlotPixYMax()-this.getDataPixYMax())>1){
		//alert("this.drawingMask.style.borderBottom= " + Math.round(this.getPlotPixYMax()-this.getDataPixYMax()) + "px solid gray");
		this.drawingMask.style.borderBottom= Math.round(this.getPlotPixYMax()-this.getDataPixYMax()) + "px solid gray";
	}
	else 
		{
		this.drawingMask.style.borderBottom= "0pt";
	}
	this.drawingMask.style.width = Math.round(this.getDataPixXMax()-this.getDataPixXMin()) +  'px';
	this.drawingMask.style.height = Math.round(this.getDataPixYMax() - this.getDataPixYMin()) + 'px';
	this.drawingMask.style.left = Math.round(this.getPlotPixXMin()) +  'px';
	this.drawingMask.style.top = Math.round(this.getPlotPixYMin()) + 'px';
	
}

//generic function to clone objects
MapWidget.prototype.clone = function (obj) {
	if(typeof obj !='object')
		return obj;
	var myclone = new Object();
	
	for(var i in obj)
		myclone[i] = this.clone(obj[i]);
	return myclone;
 }

MapWidget.prototype.onbeforeresize = function(evt) {
	//this.rubberBand.style.visibility = "hidden";
	//this.rubberBand_c.style.visibility = "hidden";
	//this.drawingMask.style.visibility = "hidden";
}

MapWidget.prototype.onafterresize = function(evt) {
	this.disable();	
	this.updatePixelExtents();
	this.enable();
}

//move the map left
MapWidget.prototype.panPlot = function (dx,dy) {
   this.updating=true;
    var pix_dx = this.getXPixRes()*dx;
    var pix_dy = this.getYPixRes()*dy;
	
    if((this.getDataGridXMax()-this.getDataGridXMin())<355 && ((this.getPlotGridXMin() + dx) < this.getDataGridXMin() || (this.getPlotGridXMax() + dx) > this.getDataGridXMax() || (this.getPlotGridYMin() + dy) < this.getDataGridYMin() || (this.getPlotGridYMax() + dy) > this.getDataGridYMax())) 
		return false;


	//reset the plot grid coord
	this.setPlotGridXMin(this.getPlotGridXMin() + dx);
	this.setPlotGridXMax(this.getPlotGridXMax() + dx);
	//reset the plot grid coord
	this.setPlotGridYMin(this.getPlotGridYMin() + dy);
	this.setPlotGridYMax(this.getPlotGridYMax() + dy);
	
   var req = new LASRequest();
	

	req.removeVariables();
	req.removeRegion();
	req.setOperation("xy_map");
	req.setRange("x",this.getPlotGridXMin(),this.getPlotGridXMax());
	req.setRange("y",this.getPlotGridYMin(),this.getPlotGridYMax());
	  if((this.getDataGridXMax()-this.getDataGridXMin())>355) {
   	this.extents.data.grid.x.min+=dx;
   	this.extents.data.grid.x.max+=dx;
	}  
	this.plot.onload = this.onPlotLoad.bindAsEventListener(this);
	this.plot.src = "ProductServer.do?xml=" + escape(req.getXMLText()) + "&stream=true&stream_ID=plot_image";
 	this.updating=false;
}
MapWidget.prototype.onPlotLoad = function (evt) { 		
		//this.setDataGridBBox(this.extents.data.grid);
		this.setSelectionGridBBox(this.extents.selection.grid);
	
		var r_w = this.getBoxWidth(this.rubberBand);
   	var r_h = this.getBoxHeight(this.rubberBand);
   	var m_x = (this.getSelectionPixXMin()+this.getSelectionPixXMax())/2;
   	var m_y = (this.getSelectionPixYMin()+this.getSelectionPixYMax())/2;
   
   	//This is needed for the FireFox
  		r_w = isNaN(r_w)? 0 : r_w;
	   r_h = isNaN(r_h)? 0 : r_h;   

   	//moving the box
  	 	m_x0 = m_x - r_w/2;
  	 	if (m_x - r_w/2 < this.getDataPixXMin())   
  	  		m_x0 = this.getDataPixXMin();
  		else if (m_x + r_w/2 > this.getDataPixXMax()) 
			m_x0 =this.getDataPixXMax() - r_w;
  			m_y0 = m_y - r_h/2;
   	if (m_y - r_h/2 < this.getDataPixYMin())
    		m_y0 =  this.getDataPixYMin();
  	else if (m_y + r_h/2 > this.getDataPixYMax())
		m_y0 = this.getDataPixYMax() - r_h;
	
	this.Y0 = m_y0;
  	this.Y1 = m_y0 + r_h;
	this.X0 = m_x0;
  	this.X1 = m_x0 + r_w;
   
  	if (this.enabled) {
 		this.displayBox(true);
  	 	this.displayCentralBox(true);
		if(!this.updating) {
			if (this.ondraw) this.ondraw(this);	
			if (this.onafterdraw) this.onafterdraw(this);
		}
	}
}
/**
 * Zoom the map by zoom factor f on the selected region, or center of data region.
 */
MapWidget.prototype.zoom = function (f) {
	
	if(f>0&&this.extents.selection.grid==this.extents.plot.grid) {
		var bbox = this.clone(this.extents.plot.grid);
		var width = (bbox.x.max-bbox.x.min);
		var height = (bbox.y.max-bbox.y.min);
		var cx = (bbox.x.min+bbox.x.max)/2;
		var cy = (bbox.y.min+bbox.y.max)/2;
		bbox.x.min = cx - width/(2*f);	
		bbox.x.max = cx + width/(2*f);	
		bbox.y.min = cy - height/(2*f);
		bbox.y.max = cy + height/(2*f);	
		if(!this.extents.last)
			this.extents.last = [];
		this.extents.last.push(this.clone(this.extents.plot.grid));
	} 		
	else if (f>0){
		var bbox = this.clone(this.extents.selection.grid);
		if(!this.extents.last)
			this.extents.last = [];
		this.extents.last.push(this.clone(this.extents.plot.grid));
	}
	else
		if (this.extents.last) {
			if (this.extents.last.length>0) 
				var bbox = this.extents.last.pop();
		} else
			var bbox = this.clone(this.extents.data.grid);
	if(bbox)	
		this.zoomOnBBox(bbox);
	this.displayBox(true);
	
}
