/**
 * @fileoverview 
 * The PlotWidget object manages the initialization and rendering of a
 * clickable/rubber-bandable plot image with borders.  Information 
 * about the pixel locations of the upper-left and lower right corners
 * of the plot within the image must be given.
 * <p>
 * The PlotWidget object is initialized with the following Plot object:
 * <pre>
 *   Plot : {
 *     "src": image source,
 *     "pix_left": pixel positoin of the top edge of the plot area
 *     "pix_width": width in pixels of the plot area
 *     "pix_height": height in pixels of the plot area
 *     "val_top": axis coordinate of the top edge of the plot area
 *     "val_left": axis coordinate of the left edge of the plot area
 *     "val_width": horizontal axis length in axis coordinates
 *     "val_height": vertical axis length in axis coordinates
 *   }
 * </pre>
 * <p>
 *
 * @author Jonathan Callahan
 * @version 2.0
 */

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Define the PlotWidget object                                               //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @constructor
 * Constructs a new PlotWidget object.
 * @param {object} Plot Plot object (see file overview)
 * @return new PlotWidget object
 * @type Object
 */
function PlotWidget(Plot) {

  // Public methods

  this.render = PlotWidget_render;
  this.disable = PlotWidget_disable;
  this.enable = PlotWidget_enable;
  this.show = PlotWidget_show;
  this.hide = PlotWidget_hide;
// //  this.getSelectedIndex = PlotWidget_getSelectedIndex;
  this.getValue = PlotWidget_getValue;
  this.getValues = PlotWidget_getValues;
  this.setCallback = PlotWidget_setCallback;
// //  this.setValue = PlotWidget_setValue;
// //  this.setValueByIndex = PlotWidget_setValueByIndex;

  // Event handlers      

  this.mouseDown = PlotWidget_mouseDown;
  this.mouseMove = PlotWidget_mouseMove;
  this.mouseUp = PlotWidget_mouseUp;

  // Private methods

  this.resizeSelection = PlotWidget_resizeSelection;
  this.EventPosX = PlotWidget_EventPosX;
  this.EventPosY = PlotWidget_EventPosY;
  this.ObjPosX = PlotWidget_ObjPosX;
  this.ObjPosY = PlotWidget_ObjPosY;
  this.pixelToUserX = PlotWidget_pixelToUserX;
  this.pixelToUserY = PlotWidget_pixelToUserY;

  this.showSelectionDiv = PlotWidget_showSelectionDiv;

  // Initialization

/**
 * Plot object with information about the location of a plot within an image
 * These pixel values describe the location of the plot relative the to the
 * image origin.  (All other pixel values are relative to the browser origin.)
 */
  this.Plot = Plot;
// TODO:  deal with reversed axes
  this.Plot.pix_right = this.Plot.pix_left + this.Plot.pix_width;
  this.Plot.pix_bottom = this.Plot.pix_top + this.Plot.pix_height;
  this.Plot.val_right = this.Plot.val_left + this.Plot.val_width;
  this.Plot.val_bottom = this.Plot.val_top - this.Plot.val_height;
/**
 * Information about the location of the plot image within the page
 */
  this.img_pix_top = null;
  this.img_pix_left = null;
  this.plot_pix_top = null;
  this.plot_pix_left = null;
  this.plot_pix_right = null;
  this.plot_pix_bottom = null;
/**
 * Information about the location of mouse clicks within the page
 */
  this.mouseDown_pix_X = null;
  this.mouseDown_pix_Y = null;
  this.mouseUp_pix_X = null;
  this.mouseUp_pix_Y = null;
/**
 * User selected region in pixel and plot axis coordinates.
 */
 this.userDrag_pix_top = null;
 this.userDrag_pix_left = null;
 this.userDrag_pix_width = null;
 this.userDrag_pix_height = null;
 this.userDrag_val_Xlo = null;
 this.userDrag_val_Xhi = null;
 this.userDrag_val_Ylo = null;
 this.userDrag_val_Yhi = null;

/**
 * User selected point in pixel plot axis coordinates.
 */
 this.userClick_pix_X = null;
 this.userClick_pix_Y = null;
 this.userClick_val_X = null;
 this.userClick_val_Y = null;
/**
 * Wheter the user mouse button is currently depressed.
 */
  this.mouseActive = 0;
/**
 * Wheter the mouse has been moved while active.
 */
  this.mouseDrag = 0;
/**
 * Selection box style.
 * NOTE:  Style Could be in a stylesheet but why require an additional file?
 */
/* Jon's favorites 
  this.selectionBorderWidth = '2px';
  this.selectionBorderStyle = 'dotted';
  this.selectionBorderColor = '#FFF';
  this.selectionBackgroundColor = 'transparent';
  this.selectionOpacity = 0.0;
*/
  this.selectionBorderWidth = '1px';
  this.selectionBorderStyle = 'solid';
  this.selectionBorderColor = 'black';
  this.selectionBackgroundColor = 'yellow';
  this.selectionOpacity = 0.5;

/**
 * Number of digits to retain after the decimal point.
 */
  this.decimal_digits = 1;
/**
 * ID string identifying this widget.
 */
  this.widgetType = 'PlotWidget';
/**
 * Specifies the type of region selection ['horizontal' | 'vertical' | 'region']
 */
  this.type = 0;
/**
 * Specifies whether this widget is currently disabled.
 */
  this.disabled = 0;
/**
 * Specifies whether this widget is currently visible.
 */
  this.visible = 0;
/**
 * Callback function attached to the onChange event.
 */
  this.callback = null;
}

/**
 * Creates the DOM elements associated with the PlotWidget inside the named DOM element.
 * <p>
 * Any children of element_id will be removed and replaced with divs and an image
 * <p>
 * @param {string} element_id 'id' attribute of the element into which the PlotWidget is inserted.
 * @param {string} type javascript Select type ['horizontal' | 'vertical' | 'region']
 */
function PlotWidget_render(element_id,type) {

  this.element_id = element_id;
  if (type) { this.type = type; }

  var node = document.getElementById(this.element_id);
  var children = node.childNodes;
  var num_children = children.length;

  // Remove any children of this widget
  // NOTE:  Start removing children from the end.  Otherwise, what was
  // NOTE:  children[1]  becomes children[0] when children[0] is removed.
  for (var i=num_children-1; i>=0; i--) {
    var child = children[i];
    if (child) {
      node.removeChild(child);
    }
  }

  // Create and populate the PlotWidget object

  var id_text = this.element_id + '_PlotContainer';
  var Container = document.createElement('div');
  Container.setAttribute('id',id_text);
  node.appendChild(Container);

  var id_text = this.element_id + '_PlotImage';
  var Img = document.createElement('img');
  Img.setAttribute('id',id_text);
  Img.setAttribute('src',this.Plot.src);
  Img.onmousedown = PlotWidget_mouseDown;
  Img.onmousemove = PlotWidget_mouseMove;
  Img.onmouseup = PlotWidget_mouseUp;
  Img.ondrag = "return false;" 
  Img.GALLERYIMG = "no";
// IE
document.ondragstart = function(e) {
  return false;
}
 // //Img.onmouseout = PlotWidget_mouseUp;
  Container.appendChild(Img);
  
// Now that the <img> has been appended it should have a position.
// Store that position globally so we don't have to recalculate it.
// TODO:  What if someone scrolls the page?
  this.img_pix_top = this.ObjPosY(Img);
  this.img_pix_left = this.ObjPosX(Img);
  this.plot_pix_top = this.img_pix_top + this.Plot.pix_top;
  this.plot_pix_left = this.img_pix_left + this.Plot.pix_left;
  this.plot_pix_right = this.plot_pix_left + this.Plot.pix_width;
  this.plot_pix_bottom = this.plot_pix_top + this.Plot.pix_height;

// Create the transparent DIV to prevent mouse moves from triggering
// the browser 'copy image' behavior.  This DIV should cover the
// entire image.

  var id_text = this.element_id + '_TransparentDiv';
  var TransparentDiv = document.createElement('div');
  TransparentDiv.setAttribute('id',id_text);
  TransparentDiv.style.position = 'absolute';
  TransparentDiv.style.backgroundColor = 'transparent';
  TransparentDiv.style.visibility = 'visible';
  TransparentDiv.onmousedown = PlotWidget_mouseDown;
  TransparentDiv.onmousemove = PlotWidget_mouseMove;
  TransparentDiv.onmouseup = PlotWidget_mouseUp;
  // //TransparentDiv.onmouseout = PlotWidget_mouseUp;
  Container.appendChild(TransparentDiv);

  var top = this.img_pix_top + 'px';
  var left = this.img_pix_left + 'px';
// NOTE:  The bottom and right borders need not be the same height as the top and left.
// NOTE:  But we don't have access to the image width and height at this point so this
// NOTE:  this is about as good as we can do.
  var width = this.Plot.pix_left + this.Plot.pix_width + this.Plot.pix_left + 'px';
  var height = this.Plot.pix_top + this.Plot.pix_height + this.Plot.pix_top + 'px';

  TransparentDiv.style.top = top;
  TransparentDiv.style.left = left;
  TransparentDiv.style.width = width;
  TransparentDiv.style.height = height;

// Create the dotted-outline Div for user selection

  var id_text = this.element_id + '_SelectionDiv';
  var SelectionDiv = document.createElement('div');
  SelectionDiv.setAttribute('id',id_text);
  SelectionDiv.style.position = 'absolute';
  var border_style = this.selectionBorderWidth + ' ' + 
                     this.selectionBorderStyle + ' ' +
                     this.selectionBorderColor;
  SelectionDiv.style.border = border_style;
  SelectionDiv.style.backgroundColor = this.selectionBackgroundColor;
  SelectionDiv.style.opacity = this.selectionOpacity;
  SelectionDiv.style.filter = 'alpha(opacity='+this.selectionOpacity*100+')';
  SelectionDiv.style.visibility = 'hidden';
  SelectionDiv.onmousedown = PlotWidget_mouseDown;
  SelectionDiv.onmousemove = PlotWidget_mouseMove;
  SelectionDiv.onmouseup = PlotWidget_mouseUp;
  Container.appendChild(SelectionDiv);

  var border_width = Number(this.selectionBorderWidth.replace(/px/,''));
  var top = this.img_pix_top + this.Plot.pix_top + 'px';
  var left = this.img_pix_left + this.Plot.pix_left + 'px';
  var width = "0px";// this.Plot.pix_width - border_width - 1 + 'px';
  var height = "0px";//this.Plot.pix_height - border_width + 'px';

  SelectionDiv.style.top = top;
  SelectionDiv.style.left = left;
  SelectionDiv.style.width = width;
  SelectionDiv.style.height = height;

// Create the dark screen Div for disabling

  var id_text = this.element_id + '_DisableDiv';
  var DisableDiv = document.createElement('div');
  DisableDiv.setAttribute('id',id_text);
  DisableDiv.style.position = 'absolute';
  DisableDiv.style.backgroundColor = '#000';
  DisableDiv.style.opacity = 0.6;
  DisableDiv.style.visibility = 'hidden';
  Container.appendChild(DisableDiv);

  var top =  Math.abs(this.img_pix_top + this.Plot.pix_top) + 'px';
  var left =  Math.abs(this.img_pix_left + this.Plot.pix_left) + 'px';
  var width = Math.abs(this.Plot.pix_width - 2 * border_width) + 'px';
  var height =  Math.abs(this.Plot.pix_height - 2 * border_width) + 'px';

  DisableDiv.style.top = top;
  DisableDiv.style.left = left;
  DisableDiv.style.width = width;
  DisableDiv.style.height = height;

// Store the pointers to various DOM elements inside the PlotWidget object

  this.Img = Img;
  this.TransparentDiv = TransparentDiv;
  this.SelectionDiv = SelectionDiv;
  this.DisableDiv = DisableDiv;

// Store the pointer to PlotWidget inside the Img DOM element so that
// Plotwidget methods and attributes can be accessed by the event listener.

  this.Img.widget = this;
  this.TransparentDiv.widget = this;
  this.SelectionDiv.widget = this;
  this.DisableDiv.widget = this;

  this.disabled = 0;
  this.visible = 1;

}

/**
 * Returns the selected value associated with a PlotWidget.
 * @return {string} 
 */
function PlotWidget_getValue() {
  alert("PlotWidget_getValue() not implemented yet.");
}

/**
 * Returns the selected values associated with a PlotWidget.
 * <p>
 * Currently functions identically to getValue().
 * @return {string} 
 */
function PlotWidget_getValues() {
// TODO:  Implement getValues() method for 'select-multiple'
  return this.getValue();
}

/**
 * Disables the PlotWidget.
 */
function PlotWidget_disable() {
  this.DisableDiv.style.visibility = 'visible';
  this.disabled = 1;
  var a = 1;
}

/**
 * Enables the Menu widget.
 */
function PlotWidget_enable() {
  this.DisableDiv.style.visibility = 'hidden';
  this.disabled = 0;
  var a = 1;
}

/**
 * Sets the Widget container's visibility to 'visible'.
 */
function PlotWidget_show() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'visible';
// TODO:  Show all child nodes.
  this.visible = 1;
}

/**
 * Sets the Widget SelectionDiv visibility to 'visible'.
 */
function PlotWidget_showSelectionDiv() {
  var id_text = this.element_id + '_SelectionDiv';
  var SelectionDiv = document.getElementById(id_text);
  SelectionDiv.style.visibility = 'visible';
}

/**
 * Sets the Widget container's visibility to 'hidden'.
 */
function PlotWidget_hide() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'hidden';
// TODO:  Hide all child nodes.
  this.visible = 0;
}

/**
 * Sets the callback function to be attached to click events.
 * @param {string} function name
 */
function PlotWidget_setCallback(callback) {
  this.callback = callback
}

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Event handlers.                                                            //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * Sets various internal parameters when the user first clicks on the mouse.
 * The mouseDown handler is in charge of adjusting for mouse clicks that are
 * outside the plot boundaries.
 * @param e mouseDown event
 */
function PlotWidget_mouseDown(e) {
  if (!e) e = window.event;  // IE event model
 if(e.preventDefault)
 {
  e.preventDefault();
 } else  if (event.stopPropagation) event.stopPropagation(); // DOM Level 2
    else event.cancelBubble = true;


  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('PlotWidget ERROR:\n> mouseDown:  This browser does not support standard javascript events.');
    return;
  }

  var PW = target.widget;
  PW.SelectionDiv.style.width = "0px";
  PW.SelectionDiv.style.height = "0px";
  var pos_X = PW.EventPosX(e);
  var pos_Y = PW.EventPosY(e);
  if (pos_X > PW.plot_pix_left) {
    if (pos_X < PW.plot_pix_right) {
      PW.mouseDown_pix_X = pos_X;
    } else {
      PW.mouseDown_pix_X = PW.plot_pix_right;
    }
  } else {
    PW.mouseDown_pix_X = PW.plot_pix_left;
  }
  if (pos_Y > PW.plot_pix_top) {
    if (pos_Y < PW.plot_pix_bottom) {
      PW.mouseDown_pix_Y = pos_Y;
    } else {
      PW.mouseDown_pix_Y = PW.plot_pix_bottom;
    }
  } else {
    PW.mouseDown_pix_Y = PW.plot_pix_top;
  }

  PW.mouseActive = 1;
  PW.mouseDrag = 0;
}

/**
 * @private
 * Resizes the SelectionWidget when the user moves the mouse.
 * The mouseMove handler is in charge of adjusting for mouse moves that are
 * outside the plot boundaries.
 * @param e mouseMove event
 */
function PlotWidget_mouseMove(e) {
  if (!e) e = window.event;  // IE event model
 if(e.preventDefault)
 {
  e.preventDefault();
 } else  if (event.stopPropagation) event.stopPropagation(); // DOM Level 2
    else event.cancelBubble = true;


  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('PlotWidget ERROR:\n> mouseDown:  This browser does not support standard javascript events.');
    return;
  }

  var PW = target.widget;
  if (PW.mouseActive) {
    var pos_X = PW.EventPosX(e);
    var pos_Y = PW.EventPosY(e);

    // Keep pos_X and pos_Y inside the plot boundaries.
    if (pos_X > PW.plot_pix_left) {
      if (pos_X > PW.plot_pix_right) {
        pos_X = PW.plot_pix_right;
      }
    } else {
      pos_X = PW.plot_pix_left;
    }
    if (pos_Y > PW.plot_pix_top) {
      if (pos_Y > PW.plot_pix_bottom) {
        pos_Y = PW.plot_pix_bottom;
      }
    } else {
      pos_Y = PW.plot_pix_top;
    }

    // Now resize the Selection DIV 
    PW.resizeSelection(pos_X,pos_Y);
    PW.mouseDrag = 1;
    PW.showSelectionDiv();
  }
}

/**
 * @private
 * Sets various internal parameters when the user lets go of the mouse button
 * @param e mouseUp event
 */
function PlotWidget_mouseUp(e) {
  if (!e) e = window.event;  // IE event model
 if(e.preventDefault)
 {
  e.preventDefault();
 } else  if (event.stopPropagation) event.stopPropagation(); // DOM Level 2
    else event.cancelBubble = true;


  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('PlotWidget ERROR:\n> mouseDown:  This browser does not support standard javascript events.');
    return;
  }

  var PW = target.widget;

  PW.mouseActive = 0;
  PW.mouseUp_pix_X = PW.EventPosX(e);
  PW.mouseUp_pix_Y = PW.EventPosY(e);

  if (PW.mouseDrag) {
    PW.userDrag_val_Xlo = PW.pixelToUserX(PW.userDrag_pix_left);
    PW.userDrag_val_Xhi = PW.pixelToUserX(PW.userDrag_pix_left + PW.userDrag_pix_width);
    PW.userDrag_val_Yhi = PW.pixelToUserY(PW.userDrag_pix_top);
    PW.userDrag_val_Ylo = PW.pixelToUserY(PW.userDrag_pix_top + PW.userDrag_pix_height);
  } else {
    PW.userClick_val_X = PW.pixelToUserX(PW.mouseDown_pix_X);
    PW.userClick_val_Y = PW.pixelToUserY(PW.mouseDown_pix_Y);
  }

  // Call the callback function if it is defined.
  if (PW.callback) {
    PW.callback(PW);
  }

}

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Utility functions                                                          //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * Resizes the SelectionDiv to extend from the passed in location to the
 * previously stored mouseDown location.
 * @param x x pixel location of an event relative to the browser window
 * @param y x pixel location of an event relative to the browser window
 */
function PlotWidget_resizeSelection(x,y) {
  this.userDrag_pix_top = Math.min(y,this.mouseDown_pix_Y);
  this.userDrag_pix_left = Math.min(x,this.mouseDown_pix_X);
  this.userDrag_pix_width = Math.abs(x - this.mouseDown_pix_X);
  this.userDrag_pix_height = Math.abs(y - this.mouseDown_pix_Y);

  var border_width = Number(this.selectionBorderWidth.replace(/px/,''));

  var top = this.userDrag_pix_top + 'px';
  var left = this.userDrag_pix_left + 'px';
  var width =  Math.abs(this.userDrag_pix_width - border_width - 1) + 'px';
  var height =  Math.abs(this.userDrag_pix_height - border_width) + 'px';

  this.SelectionDiv.style.top = top;
  this.SelectionDiv.style.left = left;
  this.SelectionDiv.style.width = width;
  this.SelectionDiv.style.height = height;
}

/**
 * @private
 * Returns the X positioning of an event relative to the browser window.
 * @param e event
 * @return x horizontal position of this event relative to the browser
 */
function PlotWidget_EventPosX(e) {
  if(e.preventDefault)
 {
  e.preventDefault();
 } else if (event.stopPropagation) event.stopPropagation(); // DOM Level 2
    else event.cancelBubble = true;

  var pix_x;
  if (e.pageX) {
    pix_x = e.pageX;
  } else if (e.clientX) {
    pix_x = e.clientX;
    // NOTE:  This is supposed to be an isIE test.  User browser sniffing if this doesn't work.
    if (document.documentElement.scrollLeft != 0) {
      pix_x += document.documentElement.scrollLeft;
    }
  }
  return pix_x;
}

/**
 * @private
 * Returns the Y positioning of an event relative to the browser window.
 * @param e event
 * @return y horizontal position of this event relative to the browser
 */
function PlotWidget_EventPosY(e) {
 if(e.preventDefault)
 {
  e.preventDefault();
 } else  if (event.stopPropagation) event.stopPropagation(); // DOM Level 2
    else event.cancelBubble = true; 
 
 var pix_y;
  if (e.pageY) {
    pix_y = e.pageY;
  } else if (e.clientY) {
    pix_y = e.clientY;
    // NOTE:  This is supposed to be an isIE test.  User browser sniffing if this doesn't work.
	
    if (document.documentElement.scrollTop != 0) {
      pix_y += document.documentElement.scrollTop;
    }
  }
  return pix_y;
}

/**
 * @private
 * Returns the X positioning of an HTML element relative to the browser window.
 * @param obj HTML element
 * @return x horizontal position of this element relative to the browser
 */
function PlotWidget_ObjPosX(obj) {
  var pix_x = 0;
  if (obj.offsetParent) {
    do {
      pix_x += obj.offsetLeft;
    } while (obj = obj.offsetParent);
  } else if (obj.x) {
    pix_x += obj.x;
  }
  return pix_x;
}

/**
 * @private
 * Returns the Y positioning of an HTML element relative to the browser window.
 * @param obj HTML element
 * @return y vertical position of this element relative to the browser
 */
function PlotWidget_ObjPosY(obj) {
  var pix_y = 0;
  if (obj.offsetParent) {
    do {
      pix_y += obj.offsetTop;
    } while (obj = obj.offsetParent);
  } else if (obj.y) {
    pix_y += obj.y;
  }
  return pix_y;
}

/**
 * @private
 * Returns the x coordinate value assoicated with pixel pix_x
 * @param pix_x horizontal pixel position relative to the browser window
 * @return val_x coordinate associated with this pixel position
 */
function PlotWidget_pixelToUserX(pix_x) {
// TODO:  deal with reversed axes
  var plot_relative_pix_x = pix_x - this.plot_pix_left;
  var units_per_pixel = this.Plot.val_width / this.Plot.pix_width;
  var val_x = this.Plot.val_left + (plot_relative_pix_x * units_per_pixel);
  var str_x = String(val_x)
  var val_x_trimmed = str_x;
  if (str_x.indexOf('.') > -1) {
    val_x_trimmed = str_x.substr(0,str_x.indexOf('.') + 1 + this.decimal_digits);
  } else {
    val_x_trimmed += '.';
    for (i=0;i<this.decimal_digits;i++) {
      val_x_trimmed += '0';
    }
  }
  return Number(val_x_trimmed);
}

/**
 * @private
 * Returns the y coordinate value assoicated with pixel pix_y.  This method
 * deals with the fact that image coordinates go from top to bottom while
 * plot coordinates go from bottom to top.
 * @param pix_y vertical pixel position relative to the browser window
 * @return val_y coordinate associated with this pixel position
 */
function PlotWidget_pixelToUserY(pix_y) {
// TODO:  deal with reversed axes
  var units_per_pixel = this.Plot.val_height / this.Plot.pix_height;
  // NOTE:  y pixels start at the top and go down but coordinates start at the bottom and go up
  var plot_relative_pix_y_from_bottom = this.plot_pix_top + this.Plot.pix_height - pix_y;
  var val_y = this.Plot.val_bottom + (plot_relative_pix_y_from_bottom * units_per_pixel);
  var str_y = String(val_y)
  var val_y_trimmed = str_y;
  if (str_y.indexOf('.') > -1) {
    val_y_trimmed = str_y.substr(0,str_y.indexOf('.') + 1 + this.decimal_digits);
  } else {
    val_y_trimmed += '.';
    for (i=0;i<this.decimal_digits;i++) {
      val_y_trimmed += '0';
    }
  }
  return Number(val_y_trimmed);
}
