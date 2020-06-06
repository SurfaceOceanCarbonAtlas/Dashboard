/**
 * @fileoverview 
 * The MenuWidget object manages the initialization and rendering of a
 * Selector so that new MenuWidgets may be created at will when required
 * by a user ineterface.
 * <p>
 * The MenuWidget object is initialized with a Menu object that consists of
 * an array of [text,vaue] pairs.
 * <pre>
 *   Menu : [
 *     [text0,value0],
 *     [text1,value1],
 *     [text2,value2]
 *   ]
 * </pre>
 * <p>
 *
 * @author Jonathan Callahan
 * @version 2.0
 */

////////////////////////////////////////////////////////////////////////////////
//                                                                            //
// Define the MenuWidget object                                               //
//                                                                            //
////////////////////////////////////////////////////////////////////////////////

/**
 * @constructor
 * Constructs a new MenuWidget object.
 * @param {object} Menu Menu object (Array of [text,value] arrays)
 * @return new MenuWidget object
 * @type Object
 */
function MenuWidget(Menu) {

  // Public methods

  this.render = MenuWidget_render;
  this.disable = MenuWidget_disable;
  this.enable = MenuWidget_enable;
  this.show = MenuWidget_show;
  this.hide = MenuWidget_hide;
  this.getSelectedIndex = MenuWidget_getSelectedIndex;
  this.getValue = MenuWidget_getValue;
  this.getValues = MenuWidget_getValues;
  this.setCallback = MenuWidget_setCallback;
  this.setValue = MenuWidget_setValue;
  this.setValueByIndex = MenuWidget_setValueByIndex;

  // Event handlers      

  this.selectChange = MenuWidget_selectChange;

  // Initialization

/**
 * Menu object of [text,value pairs] passed in during initialization.
 */
  this.Menu = Menu;
/**
 * Number of Options in the Select menu.
 */
  this.length = Menu.length;
/**
 * Index of the currently selected option.
 */
  this.selectedIndex = 0;
/**
 * Select object type ['select-one' | 'select-multiple'] (currently only 'select-one' is supported)
 */
  this.type = 'select-one';
/**
 * ID string identifying this widget.
 */
  this.widgetType = 'MenuWidget';
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
 * Creates the javascript Select object associated with the MenuWidget inside the named DOM element.
 * <p>
 * Any children of element_id will be removed and replaced with a Select object
 * <p>
 * @param {string} element_id 'id' attribute of the element into which the Menu is inserted.
 * @param {string} type javascript Select type ['select-one' | 'select-multiple']
 */
function MenuWidget_render(element_id,type) {

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
  

  with (Select) {
    // TODO:  deal with setting the 'type'
    // NOTE:  IE doesn't support the setAttribute() method for the 'type' attribute
    // NOTE:  We just remove this capability for now (defaults to 'select-one'
    //Select.setAttribute('type', this.type);
    Select.setAttribute('name', id_text);
    for (var i=0; i<this.Menu.length; i++) {
      options[i]=new Option(this.Menu[i][0], this.Menu[i][1]);
      if (i == this.selectedIndex) {
        options[i].selected = true;
      }
    }
  }
  Select.widget = this;
  Select.onchange = this.selectChange;
  node.appendChild(Select);

  this.visible = 1;

  // Store the pointer to the Select object inside the MenuWidget object

  this.Select = Select;
  this.length = this.Select.length;
}

/**
 * Returns the selectedIndex of this MenuWidget when it is of type 'select-one'.
 * @return {int} 
 */
function MenuWidget_getSelectedIndex() {
  return this.selectedIndex;
}

/**
 * Returns the selected value associated with a MenuWidget of type 'select-one'.
 * @return {string} 
 */
function MenuWidget_getValue() {
  return this.Select.options[this.Select.selectedIndex].value;
}

/**
 * Returns the selected values associated with a MenuWidget of type 'select-multiple'.
 * <p>
 * Currently functions identically to getValue().
 * @return {string} 
 */
function MenuWidget_getValues() {
// TODO:  Implement getValues() method for 'select-multiple'
  return this.getValue();
}

/**
 * Disables the Menu widget.
 */
function MenuWidget_disable() {
  this.disabled = 1;
  this.Select.disabled = 1;
}

/**
 * Enables the Menu widget.
 */
function MenuWidget_enable() {
  this.disabled = 0;
  this.Select.disabled = 0;
}

/**
 * Sets the Widget container's visibility to 'visible'.
 */
function MenuWidget_show() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'visible';
  this.visible = 1;
}

/**
 * Sets the Widget container's visibility to 'hidden'.
 */
function MenuWidget_hide() {
  var node = document.getElementById(this.element_id);
  node.style.visibility = 'hidden';
  this.visible = 0;
}

/**
 * Sets the callback function to be attached to onChange events.
 * @param {string} function name
 */
function MenuWidget_setCallback(callback) {
  this.callback = callback
}

/**
 * Sets the selected Option to match the incoming value
 * @param {string} value Option value to match
 * @throws 'Error: MenuWidgetSetValue: value [...] does not match any options.'
 */
function MenuWidget_setValue(value) {
  var no_match = 1;
  for (var i=0; i<this.Select.length; i++) {
    if ( this.Select.options[i].value == value ) {
      this.Select.selectedIndex = i;
      this.selectedIndex = i;
      no_match = 0;
    }
  }

  var numericValue;
  try {
    numericValue = Number(value);
  } catch(e) {
    alert('ERROR:  MenuWidget_setValue: value [' + value + '] is not an available option and is not numeric.');
    throw('ERROR:  MenuWidget_setValue: value [' + value + '] is not an available option and is not numeric.');
  }
    
// No match - look for nearest value in lo -> hi ordering
  if (no_match) {
    for (var i=0; i<this.Select.length-1; i++) {
      var thisOption = Number(this.Select.options[i].value);
      var nextOption = Number(this.Select.options[i+1].value);
      if (numericValue > thisOption && numericValue < nextOption) {
        this.Select.selectedIndex = i;
        this.selectedIndex = i;
        no_match = 0;
      }
    }
  }

// Still no match - look for nearest value in hi -> lo ordering
  if (no_match) {
    for (var i=0; i<this.Select.length-1; i++) {
      var thisOption = Number(this.Select.options[i].value);
      var nextOption = Number(this.Select.options[i+1].value);
      if (numericValue > thisOption && numericValue < nextOption) {
        this.Select.selectedIndex = i;
        this.selectedIndex = i;
        no_match = 0;
      }
    }
  }

  if (no_match) { 
    alert('ERROR:  MenuWidget_setValue: value [' + value + '] is outside the range of options.');
    throw('ERROR:  MenuWidget_setValue: value [' + value + '] is outside the range of options.');
  }
}

/**
 * Sets the selected Option.
 * @param {int} index index into the Options array [0 <= index <= N]
 * @throws 'ERROR:  MenuWidgetsetValueByIndex: index [...] does not match any options.'
 */
function MenuWidget_setValueByIndex(index) {
  if (index < 0 || index >= this.Select.length) {
    alert('ERROR:  MenuWidget_setValueByIndex: index [' + index + '] does not match any options.');
    throw('ERROR:  MenuWidget_setValueByIndex: index [' + index + '] does not match any options.');
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
function MenuWidget_selectChange(e) {

  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('MenuWidget ERROR:\n> selectChange:  This browser does not support standard javascript events.');
    return;
  }

  if (target.nodeName.toLowerCase() != 'select') {
    alert('MenuWidget ERROR:\n> selectChange:  event target [' + target.nodeName.toLowerCase() + '] should instead be [select]');
    return;
  }
// NOTE:  I believe that this function is evaluated outside the context of the MenuWidget.
// NOTE:  Hence the need to extract the Widget from the Select object.
  var MW = target.widget;

  if (MW.callback) {
    MW.callback(MW);
  }
}
