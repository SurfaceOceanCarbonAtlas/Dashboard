/**
 * @fileoverview This file is to be included in any Velocity Template that
 * wishes to access grid information available while generating an LAS product.
 * This information is identical to the information returned by the getGrid.do
 * servlet.<p>
 * Here is some example template code that demonstrates accessing and interpreting
 * the grid:
 * <pre>
 * ## The tepmlating language has no access to javascript variables
 * ## so we must get dsID and varID the 'template way'
 * #set($dsID = $las_request.datasetIDs.get(0))
 * #set($varID = $las_request.variableIDs.get(0))
 * #set($grid = $las_config.getGrid($dsID,$varID))
 * #set($grid_JSON = $grid.toJSON().toString())
 * var gridJSON = '$grid_JSON';
 *
 * // First, make sure we can parse the LASResponse
 * var Grid;
 * var JSONObject;
 * try {
 *   var JSONObject = gridJSON.parseJSON();
 * } catch(e) {
 *   alert('Error parsing gridJSON as JSON.');
 *   return;
 * }
 *
 * try {
 *   Grid = new LASGrid(JSONObject);
 * } catch(e) {
 *   alert(e);
 *   return;
 * }</pre>
 * For more information about LAS and the LASGrid please see:
 * {@link http://ferret.pmel.noaa.gov/armstrong/ Armstrong Documentation}.<br>
 *
 * For more information about json.js please visit:
 * {@link http://www.json.org/json.html}.
 *
 * @author Jonathan Callahan
 */

/**
 * Constructs a new LASGrid object.<br>
 * The LASGrid object contains all the information associated with an LAS Grid.
 * An XML representation of
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
 * The LASGrid class defined here provides accessor methods that allow one
 * to get individual pieces of information without navigating the hierarchy.
 * @class This is the basic LASGrid class.
 * @constructor
 * @param {object} JSONObject instantiated from JSON serialization of the LAS Grid.
 * @return A new LASGrid object
 */
function LASGrid(response) {

/**
 * LAS Grid object obtained from the Velocity template.
 */
  this.response = response;

// Add methods to this object

  this.hasAxis = LASGrid_hasAxis;
  this.hasArange = LASGrid_hasArange;
  this.hasMenu = LASGrid_hasMenu;

  this.getAxis = LASGrid_getAxis;	
  this.getLo = LASGrid_getLo;
  this.getHi = LASGrid_getHi;
  this.getDelta = LASGrid_getDelta;
  this.getSize = LASGrid_getSize;
  this.getUnits = LASGrid_getUnits;
  this.getID = LASGrid_getID;

  this.getDisplayType = LASGrid_getDisplayType;
  this.getRenderFormat = LASGrid_getRenderFormat;
  this.getMenu = LASGrid_getMenu;

// Check for incomplete LASGrid.

  if (response == null) {
    var error_string = 'The LASGrid is empty';
    throw(error_string);
  }

}

////////////////////////////////////////////////////////////
// Methods of the LASGrid object.
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
function LASGrid_getAxis(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  for (var i=0;i<this.response.grid.axis.length; i++) {
    if(this.response.grid.axis[i].type==axis_lc)
    	value = this.response.grid.axis[i];
  }
  return value;
}

/**
 * Returns Boolean True if the specified axis exists in the grid.
 * @param {string} axis axis of interest
 * @return value True if the &lt;arange&gt; element is present.
 * @type Boolean
 */
function LASGrid_hasAxis(axis) {
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
function LASGrid_hasArange(axis) {
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
function LASGrid_hasMenu(axis) {
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
function LASGrid_getLo(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (axis_lc == 't') {
    value = this.getAxis(axis_lc).lo;
  } else {
    if (this.hasMenu(axis)) { // <v> array
      value = this.getAxis(axis_lc).v[0];
    } else { // <arange ...>
   	  value = this.getAxis(axis_lc).arange.start;
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
function LASGrid_getHi(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (axis_lc == 't') {
    value = this.getAxis(axis_lc).hi;
  } else {
    if (this.hasMenu(axis)) { // <v> array
      var index = this.getAxis(axis_lc).v.length - 1;
      value = this.getAxis(axis_lc).v[index];
    } else { // <arange ...>
      var start = Number(this.getAxis(axis_lc).arange.start);
      var size = Number(this.getAxis(axis_lc).arange.size);
      var step = Number(this.getAxis(axis_lc).arange.step);
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
function LASGrid_getDelta(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  if (this.hasArange(axis_lc)) {
    value = this.getAxis(axis_lc).arange.step;
  }
  return Number(value);
}


/**
 * Returns the size of an &lt;arange&gt; associated with a particular
 * axis of the grid OR the length of a Menu array (&lt;v&gt; array) 
 * OR <b>null</b> if the axis doesn't exist in the grid.<p>
 * @param {string} axis axis of interest
 * @return lo lo value associated with this axis
 * @type int
 */
function LASGrid_getSize(axis) {
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
function LASGrid_getUnits(axis) {
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
function LASGrid_getID(axis) {
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
function LASGrid_getDisplayType(axis) {
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
function LASGrid_getRenderFormat(axis) {
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
function LASGrid_getMenu(axis) {
  var axis_lc = String(axis).toLowerCase();
  var value = null;
  var menu = null;
  if (this.getAxis(axis_lc).v) {
    value = new Array;
    menu = this.getAxis(axis_lc).v;
// NOTE:  The <v> object of the response looks like this:
// NOTE:    "v":[
// NOTE:          {"label1":"content1"},
// NOTE:          {"label2":"content2"},
// NOTE:          ...
// NOTE:          {"labelN":"contentN"}],

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
  }
  return value;
}


////////////////////////////////////////////////////////////
// Utility methods
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Private methods
////////////////////////////////////////////////////////////

