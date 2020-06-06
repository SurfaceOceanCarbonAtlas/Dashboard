/**
 * @private
 * @fileoverview 
 * The LongitudeWidget object manages the initialization and rendering of a
 * longitude Selector.
 * <p>
 * The LongitudeWidget object is initialized with a valid range (Lo <--> Hi)
 * and allows the user to select a longitude within this range.
 * (Lo <= Lon <= Hi).
 * <p>
 * TODO:  The LongitudeWidget should create either a single Selector or a pair
 * TODO:  of Selectors depending on whether a point or range is required.
 * TODO:  This would match the functionality provided by the DateWidget.
 * TODO:  In order to release a beta version of the new LAS by July 9, 2007
 * TODO:  (5 weeks away), this version of LongitudeWidget will only support
 * TODO:  a single Selector to satisfy the needs of the ImageSlideSorter.
 * TODO:
 * TODO:  Another way to handle this, of course, would be to use a mult-select
 * TODO:  widget.  Hmmm ... I'll have to think about which is better.
 *
 * @author Jonathan Callahan
 * @version 1.0
 */

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Define the LongitudeWidget object                                           //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @constructor
 * Constructs a new LongitudeWidget object.
 * <p>
 * @param {float} lo end of the domain of valid longitudes in decimal degrees
 * @param {float} hi end of the domain of valid longitudes in decimal degrees
 * @param {float} delta interval between longitudes in decimal degrees
 * @return new LongitudeWidget object
 */
function LongitudeWidget(lo, hi, delta) {

  // Public methods

  this.render = LongitudeWidget_render;
  this.disable = LongitudeWidget_disable;
  this.enable = LongitudeWidget_enable;
  this.show = LongitudeWidget_show;
  this.hide = LongitudeWidget_hide;
  this.getSelectedIndex = LongitudeWidget_getSelectedIndex;
  this.getValue = LongitudeWidget_getValue;
  this.getValues = LongitudeWidget_getValues;

  this.setCallback = LongitudeWidget_setCallback;
  this.setValue = LongitudeWidget_setValue;
  this.setValueByIndex = LongitudeWidget_setValueByIndex;

  // Event handlers      

  this.selectChange = LongitudeWidget_selectChange;

  // Initialization

/**
 * Lowest value displayed in the Select menu.
 */
  this.lo = Number(lo);
/**
 * Highest value displayed in the Select menu.
 */
  this.hi = Number(hi);
/**
 * Spacing beteen Select options (decimal degrees)
 */
  this.delta = Number(delta);
/**
 * Number of Options in the Select menu.
 */
  this.length = 0;
/**
 * Select object type ['select-one' | 'select-multiple'] (currently only 'select-one' is supported)
 */
  this.type = 'select-one';
/**
 * ID string identifying this widget.
 */
  this.widgetType = 'LongitudeWidget';
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
 * Creates the javascript Select object associated with the LongitudeWidget inside the named DOM element.
 * <p>
 * Any children of element_id will be removed and replaced with a Select object
 * <p>
 * @param {string} element_id 'id' attribute of the element into which the Select is inserted.
 * @param {string} type javascript Select type ['select-one' | 'select-multiple']
 * TODO:  For now only supports 'select-one'.
 */
function LongitudeWidget_render(element_id,type) {

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

  // Create and populate the Select object

  var id_text = this.element_id + '_Select';
  var Select = document.createElement('select');
  Select.setAttribute('id',id_text);

  //TODO:  deal with setting the 'type'
  // NOTE:  IE doesn't support the setAttribute() method for the 'type' attribute
  // NOTE:  We just remove this capability for now (defaults to 'select-one'
  //Select.setAttribute('type', this.type);
  Select.setAttribute('name', id_text);

  // NOTE:  Deal with dateline crossing
  var lo = this.lo;
  var hi = this.hi;
  var delta = this.delta;
  if (hi < lo) {
    hi += 360;
  }
  var length = (hi - lo) / delta + 1;
  var value = lo - delta;
  var text = "";
  for (var i=0; i<length; i++) {
    value += delta;
    // NOTE:  Deal with dateline crossing
    if (value > 180) {
      value -= 360;
    }
    if (value < 0) {
      text = Math.abs(value).toString() + ' W';
    } else {
      text = Math.abs(value).toString() + ' E';
    }
    Select.options[i]=new Option(text, value);
  }

  Select.selectedIndex = 0;
  Select.options[0].selected = true;
  Select.widget = this;
  Select.onchange = this.selectChange;
  node.appendChild(Select);

  // Store the pointer to the Select object inside the LongitudeWidget object

  this.Select = Select;
  this.length = this.Select.length;
}

/**
 * Returns the selectedIndex of this LongitudeWidget when it is of type 'select-one'.
 * @return {int} 
 */
function LongitudeWidget_getSelectedIndex() {
  return this.selectedIndex;
}

/**
 * Returns the selected value associated with a LongitudeWidget of type 'select-one'.
 * @return {string} 
 */
function LongitudeWidget_getValue() {
  return this.Select.options[this.Select.selectedIndex].value;
}

/**
 * Returns the selected values associated with a LongitudeWidget of type 'select-multiple'.
 * @return {string} 
 */
function LongitudeWidget_getValues() {
// TODO:  Implement getValues() method for 'select-multiple'
  return this.getValue();
}

/**
 * Disables the Menu widget.
 */
function LongitudeWidget_disable() {
  this.disabled = 1;
  this.Select.disabled = 1;
}

/**
 * Enables the Menu widget.
 */
function LongitudeWidget_enable() {
  this.disabled = 0;
  this.Select.disabled = 0;
}

/**
 * Set's the Widget container's visibility to 'visible'
 */
function LongitudeWidget_show() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'visible';
  this.visible = 1;
}

/**
 * Set's the Widget container's visibility to 'hidden'
 */
function LongitudeWidget_hide() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'hidden';
  this.visible = 0;
}

/**
 * Sets the callback function to be attached to onChange events.
 * @param {string} function name
 */
function LongitudeWidget_setCallback(callback) {
  this.callback = callback
}

/**
 * Sets the selected option of the Select menu to the value.
 * closest to the incoming value.
 * @param {float} lat numeric value to be selected
 */
function LongitudeWidget_setValue(lon) {
  var internal_lon = Number(lon)%360;
  internal_lon = (internal_lon >= 0) ? internal_lon : internal_lon+360;
  var i = 0;
  while (i <= this.Select.length) {
    var option_val = Number(this.Select.options[i].value);
    option_val = (option_val >= 0) ? option_val : option_val+360;
    if (internal_lon <= option_val) {
      this.Select.selectedIndex = i;
      this.Select.options[i].selected = true;
      return;
    // TODO:  include logic to find closest match if lon is in between option_vals
    }
    i++;
  }
}

/**
 * Sets the selected Option.
 * @param {int} index index into the Options array [0 <= index <= N]
 * @throws 'ERROR:  LongitudeWidgetsetValueByIndex: index [...] does not match any options.'
 */
function LongitudeWidget_setValueByIndex(index) {
  if (index < 0 || index >= this.Select.length) {
    throw('ERROR:  LongitudeWidget_setValueByIndex: index [' + index + '] does not match any options.');
  } else {
    this.Select.selectedIndex = index;
    this.selectedIndex = index;
  }
}

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Event handlers.                                                            //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * Event handler that calls the user specified callback function
 * if one has been defined.
 * @param e event
 */
function LongitudeWidget_selectChange(e) {

  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('LongitudeWidget ERROR:\n> selectChange:  This browser does not support standard javascript events.');
    return;
  }

  if (target.nodeName.toLowerCase() != 'select') {
    alert('LongitudeWidget ERROR:\n> selectChange:  event target [' + target.nodeName.toLowerCase() + '] should instead be [select]');
    return;
  }
// NOTE:  I believe that this function is evaluated outside the context of the LongitudeWidget.
// NOTE:  Hence the need to extract the Widget from the Select object.
  var MW = target.widget;

  if (MW.callback) {
    MW.callback(MW);
  }
}
