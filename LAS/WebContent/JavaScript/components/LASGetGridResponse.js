/**
 * @fileoverview This file is to be included in any HTML documents that
 * wishes to process the response from a getGrid.do request a Live Access Server.
 * This file and the required 'json.js' file should be included in the following
 * manner in the head of the HTML file:
 * <pre>
 * &lt;head>
 *   &lt;script language="JavaScript" src="json.js"&gt;&lt;/script&gt;
 *   &lt;script language="JavaScript" src="LASGetGridResponse.js"&gt;&lt;/script&gt;
 *   ...
 * &lt;/head></pre>
 * For more information about LAS and the LASGetGridResponse please see:
 * {@link http://ferret.pmel.noaa.gov/armstrong/ Armstrong Documentation}.<br>
 *
 * For more information about json.js please visit:
 * {@link http://www.json.org/json.html}.
 *
 * @author Jonathan Callahan
 * @version $Revision: 783 $
 */

/**
 * Constructs a new LASGetGridResponse object.<br>
 * The LASGetGridResponse object contains all the information returned from a
 * getGrid.do request to the LAS product server .  An XML representation of
 * the JSON object returned by the LAS product server would look like this:
 * <pre>
 * &lt;t&gt;?
 *  &lt;t/&gt;
 *  &lt;units/&gt;
 *  &lt;name/&gt;
 *  &lt;ID/&gt;
 *  &lt;display_type/&gt;
 *  &lt;v&gt;[
 *    &lt;option/&gt;+
 *  ]&lt;/v&gt;
 * &lt;/t&gt;
 * &lt;x&gt;?
 *   &lt;units/&gt;
 *   &lt;ID/&gt;
 *   &lt;arange&gt;
 *     &lt;start/&gt;
 *     &lt;step/&gt;
 *     &lt;size/&gt;
 *     &lt;name/&gt;
 *     &lt;ID/&gt;
 *   &lt;/arange&gt;
 * &lt;/x&gt;?
 * &lt;y&gt;?
 *   &lt;units/&gt;
 *   &lt;ID/&gt;
 *   &lt;arange&gt;
 *     &lt;start/&gt;
 *     &lt;step/&gt;
 *     &lt;size/&gt;
 *     &lt;name/&gt;
 *     &lt;ID/&gt;
 *   &lt;/arange&gt;
 * &lt;/y&gt;?
 * &lt;z&gt;?
 *   &lt;units/&gt;
 *   &lt;ID/&gt;
 *   &lt;arange&gt;
 *     &lt;start/&gt;
 *     &lt;step/&gt;
 *     &lt;size/&gt;
 *     &lt;name/&gt;
 *     &lt;ID/&gt;
 *   &lt;/arange&gt;
 * &lt;/z&gt;?
 * </pre&gt;
 * The LASGetGridResponse class defined here provides accessor methods that allow one
 * to get individual pieces of information without navigating the hierarchy.
 * @class This is the basic LASGetGridResponse class.
 * @constructor
 * @param {object} JSONObject instantiated from JSON serialization of the getGrid.do response
 * @return A new LASGetGridResponse object
 */
function LASGetGridResponse(response) {

/**
 * LAS Grid object returned by the getGrid.do request.
 */
  this.response = response;

// Add methods to this object

  this.hasAxis = LASGetGridResponse_hasAxis;
  this.hasArange = LASGetGridResponse_hasArange;
  this.hasMenu = LASGetGridResponse_hasMenu;
  this.hasView =  LASGetGridResponse_hasView;
  this.getAxis = LASGetGridResponse_getAxis;	
  this.getLo = LASGetGridResponse_getLo;
  this.getHi = LASGetGridResponse_getHi;
  this.getDelta = LASGetGridResponse_getDelta;
  this.getSize = LASGetGridResponse_getSize;
  this.getUnits = LASGetGridResponse_getUnits;
  this.getID = LASGetGridResponse_getID;
   this.getMinuteInterval = LASGetGridResponse_getMinuteInterval;
  this.getDisplayType = LASGetGridResponse_getDisplayType;
  this.getRenderFormat = LASGetGridResponse_getRenderFormat;
  this.getMenu = LASGetGridResponse_getMenu;

// Check for incomplete LASGetGridResponse.

  if (response == null) {
    var error_string = 'getGrid.do returned a null response.';
    throw(error_string);
  } else {
    if (response.status != 'ok') {
      var error_string = response.error ? response.error : 'Unknown error in LASGetGridResponse.';
      throw(error_string);
    }
  }

}

////////////////////////////////////////////////////////////
// Methods of the LASGetGridResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////

/**
 * Returns the specified axis object, OR <b>null</b> if the specified
 * axis doesn't exist in the grid.
 * @param {string} axis axis of interest
 * @return object if the element is present, null otherwise.
 * @type object
 */
function LASGetGridResponse_getAxis(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  for (var i=0;i<this.response.grid.axis.length; i++) {
    if(this.response.grid.axis[i].type==axis_lc)
    	value = this.response.grid.axis[i];
  }
  return value;
}
function LASGetGridResponse_hasView(view) {
    for(var i=0;i<view.length;i++)
	if(!this.hasAxis(view.charAt(i)))
		return false;
    return true;
}
/**
 * Returns Boolean True if the specified axis exists in the grid.
 * @param {string} axis axis of interest
 * @return value True if the &lt;arange&gt; element is present.
 * @type Boolean
 */
function LASGetGridResponse_hasAxis(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = false;
  if (this.getAxis(axis_lc)) {
    	value = true;
  }
  return value;
}

/**
 * Returns Boolean True if the specified axis has an &lt;arange&gt; element.
 * @param {string} axis axis of interest
 * @return value True if the &lt;arange&gt; element is present.
 * @type Boolean
 */
function LASGetGridResponse_hasArange(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = false;
  if(this.hasAxis(axis_lc)) {
    if (this.getAxis(axis_lc).arange) {
   	  value = true;
    }
  }
  return value;
}

/**
 * Returns Boolean True if the specified axis has a &lt;v&gt; element.
 * @param {string} axis axis of interest
 * @return value True if the &lt;v&gt; element is present.
 * @type Boolean
 */
function LASGetGridResponse_hasMenu(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = false;
 	if(this.hasAxis(axis_lc)) {
    if (this.getAxis(axis_lc).v) {
   	  value = true;
    }
  }
  return value;
}

/**
 * Returns the lo value of an &lt;arange&gt; associated with a particular
 * axis of the grid OR the first value associated with the first element
 * of a Menu array (&lt;v&gt; array) OR <b>null</b> if the axis doesn't exist in
 * the grid.<p>
 * @param {string} axis axis of interest
 * @return lo lo value associated with this axis
 * @type string
 */
function LASGetGridResponse_getLo(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (axis_lc == 't') {
    value = this.getAxis(axis_lc).lo;
  } else {
    if (this.hasMenu(axis)) { // <v> array
      value = this.getAxis(axis_lc).v[0];
    } else { // <arange ...>
      var start = Number(this.getAxis(axis_lc).arange.start);
      var size = Number(this.getAxis(axis_lc).arange.size);
      var step = Number(this.getAxis(axis_lc).arange.step);
      if(step<0)
         value = start + (size-1) * step;
      else
	 value = start;
    }
  }
  return value;
}


/**
 * Returns the hi value of an &lt;arange&gt; associated with a particular
 * axis of the grid OR the first value associated with the last element
 * of a Menu array (&lt;v&gt; array) OR <b>null</b> if the axis doesn't exist in
 * the grid.<p>
 * @param {string} axis axis of interest
 * @return hi hi value associated with this axis
 * @type string
 */
function LASGetGridResponse_getHi(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (axis_lc == 't') {
    	if(this.getAxis(axis_lc).hi)
		value = this.getAxis(axis_lc).hi;
	else
		value = this.getAxis(axis_lc).lo;
		
  } else {
    if (this.hasMenu(axis)) { // <v> array
      var index = this.getAxis(axis_lc).v.length - 1;
      value = this.getAxis(axis_lc).v[index];
    } else { // <arange ...>
      var start = Number(this.getAxis(axis_lc).arange.start);
      var size = Number(this.getAxis(axis_lc).arange.size);
      var step = Number(this.getAxis(axis_lc).arange.step);
      if(step<0)
         value=start; 
      else  
         value = start + (size-1) * step;
    }
  }
  return value;
}


/**
 * Returns the delta value of an &lt;arange&gt; associated with a particular
 * axis of the grid OR <b>null</b> if  the axis has a (&lt;v&gt; array) or 
 * if the axis doesn't exist in the grid.<p>
 * @param {string} axis axis of interest
 * @return delta delta value associated with this axis
 * @type int
 */
function LASGetGridResponse_getDelta(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (this.hasArange(axis_lc)) {
    value = this.getAxis(axis_lc).arange.step;
  }
  return Number(value);
}
/**
 * Returns the minutes delta value of an &lt;arange&gt; associated with a particular
 * axis of the grid OR <b>null</b> if  the axis has a (&lt;v&gt; array) or 
 * if the axis doesn't exist in the grid.<p>
 * @param {string} axis axis of interest
 * @return delta delta value associated with this axis
 * @type int
 */
function LASGetGridResponse_getMinuteInterval(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (this.hasArange(axis_lc)) {
    value = Number(this.getAxis(axis_lc).minuteInterval);
  }
  return value;
}

/**
 * Returns the size of an &lt;arange&gt; associated with a particular
 * axis of the grid OR the length of a Menu array (&lt;v&gt; array) 
 * OR <b>null</b> if the axis doesn't exist in the grid.<p>
 * @param {string} axis axis of interest
 * @return lo lo value associated with this axis
 * @type int
 */
function LASGetGridResponse_getSize(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (this.hasArange(axis_lc)) {
    value = this.getAxis(axis_lc).arange.size;
  } else {
    if (this.hasMenu(axis)) {
      value = this.getAxis(axis_lc).v.length;
    }
  }
  return Number(value);
}


/**
 * Returns the units associated with a particular axis of the grid OR
 * <b>null</b> if the axis doesn't exist in the grid.
 * @return units the units associated with a particular axis of the grid
 * @type string
 */
function LASGetGridResponse_getUnits(axis) {
  var value = null;
  var axis_lc = String(axis).toLowerCase();
  if (this.hasAxis(axis_lc)) {
    value = this.getAxis(axis_lc).units;
  }
  return value;
}


/**
 * Returns the ID associated with a particular axis of the grid OR
 * <b>null</b> if the axis doesn't exist in the grid.<p>
 * Possible return values include
 * <ul>
 * <li><b>menu</b> -- use MenuWidget</li>
 * <li><b>widget</b> -- use DateuWidget</li>
 * </ul>
 * @return ID the ID associated with a particular axis of the grid
 * @type string
 */
function LASGetGridResponse_getID(axis) {
  var value = null;
  var axis_lc = String(axis).toLowerCase();
  if (this.hasAxis(axis_lc)) {
    value = this.getAxis(axis_lc).display_type;
  }
  return value;
}

////////////////////////////////////////////////////////////
// Time and Depth-axis specific methods
////////////////////////////////////////////////////////////

/**
 * Returns the "dispay_type" associated with a particular axis of the grid OR
 * <b>null</b> if the "display_type" is not found or the axis doesn't exist in the grid.
 * @return display_type the type of UI widget to be displayed
 * @type string
 */
function LASGetGridResponse_getDisplayType(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (this.hasAxis(axis)) {
    if (this.getAxis(axis_lc).display_type) {
      value = this.getAxis(axis_lc).display_type;
    }
  }
  return value;
}

/**
 * Returns the "render_format" associated with a particular axis of the grid OR
 * <b>null</b> if the "render_format is not found or the axis doesn't exist in the grid.
 * @return display_type the type of UI widget to be displayed
 * @type string
 */
function LASGetGridResponse_getRenderFormat(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  switch (axis_lc) {
    case 't':
      value = "";
      if (this.getAxis('t').yearNeeded == 'true')  { value += "Y"; }
      if (this.getAxis('t').monthNeeded == 'true') { value += "M"; }
      if (this.getAxis('t').dayNeeded == 'true')   { value += "D"; }
      if (this.getAxis('t').hourNeeded == 'true')  { value += "T"; }
      break;
  }
  return value;
}

/**
 * Returns a Menu object of name:value pairs if such a menu exists for the
 * specified axis OR <b>null</b> if no menu object is found or if the axis
 * doesn't exist in the grid.
 * @return Menu menu object to be used with MenuWidget.js
 * @type object
 */
function LASGetGridResponse_getMenu(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  var menu = null;
  value = new Array;
  if (this.getAxis(axis_lc).v && typeof this.getAxis(axis_lc).v == 'object' && !this.getAxis(axis_lc).v.label) {
    menu = this.getAxis(axis_lc).v;
    for (var i=0; i<menu.length; i++) {
      if (axis_lc == 't') {
//TODO:  Remove this hack after resolution of trac ticket #147.
        var month_name = String(menu[i].content).toLowerCase();
        if (month_name == 'jan') { menu[i].content = '15-Jan'; }
        if (month_name == 'feb') { menu[i].content = '15-Feb'; }
        if (month_name == 'mar') { menu[i].content = '15-Mar'; }
        if (month_name == 'apr') { menu[i].content = '15-Apr'; }
        if (month_name == 'may') { menu[i].content = '15-May'; }
        if (month_name == 'jun') { menu[i].content = '15-Jun'; }
        if (month_name == 'jul') { menu[i].content = '15-Jul'; }
        if (month_name == 'aug') { menu[i].content = '15-Aug'; }
        if (month_name == 'sep') { menu[i].content = '15-Sep'; }
        if (month_name == 'oct') { menu[i].content = '15-Oct'; }
        if (month_name == 'nov') { menu[i].content = '15-Nov'; }
        if (month_name == 'dec') { menu[i].content = '15-Dec'; }
        value[i] = new Array(menu[i].label,menu[i].content);
      } else {
        value[i] = new Array(menu[i],menu[i]);
      }
    } 
   } else if (this.getAxis(axis_lc).v) 
		if(this.getAxis(axis_lc).v.label) 
		value[0] = new Array(this.getAxis(axis_lc).v.label,this.getAxis(axis_lc).v.content);
		else
			value[0] = new Array(this.getAxis(axis_lc).v, this.getAxis(axis_lc).v);
	
  
  return value;
}


////////////////////////////////////////////////////////////
// Utility methods
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Private methods
////////////////////////////////////////////////////////////

