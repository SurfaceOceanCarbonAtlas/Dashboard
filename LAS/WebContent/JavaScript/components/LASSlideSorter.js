/**
 * @fileoverview Javascript object and methods to manage select menu creation
 * for a table with images and select menus in each cell.  The goal of 
 * this code is to emulate a Slide Sorter lightbox which allows
 * users to arrange slides however they want.
 * <p>
 * Inspiration for this functionality came from the 
 * {@link http://www.ngdc.noaa.gov/wist/ Web Image Slide Tray}.
 * <p>
 * Functionality includes automated generation of select menus
 * and automatic replacement of images in the table cells
 * whenever a menu option is selected.
 * <p>
 * Throughout this code the word 'Select' will refer to the 
 * form elements that are updated and the word 'Menu' will refer
 * to an element from the this.InitObject[] array of objects that contain
 * the contents used to populate the Select objects.
 * <p>
 * The basic structure of the HTML table is shown below with
 * individual cell select objects named 'widgetCell#'.  The full suite of
 * available menus are given by the 'lss_G#' objects.
 *
 * <pre>
 *     ____________________
 *     !  G1   G2   G3   !
 *     !_________________!_
 *     !     !     !     !
 *     ! S11 ! S12 ! S13 !
 *     !_____!_____!_____!_
 *     !     !     !     !
 *     ! S21 ! S22 ! S23 !
 *     !_____!_____!_____!_
 *     !     !     !     !</pre>
 *
 * @author Jonathan Callahan
 * @version $Revision: 1485 $
 */ 

/**
 * Construct a new LASSlideSorter object.<br>
 * @class This is the basic LASSlideSorter class.
 * @constructor
 * @param {string} form name of the form in which the LASSlideSorter is located
 * population of the table
 * @return A new LASSlideSorter object
 */
function LASSlideSorter(form,init_object) {

/*
 * The LASSlideSorter object must be attached to the document
 * so that it is available to the 'LASSlideSorter_cellWidgetChoice' event
 * handler which is a function of the document and not of
 * the LASSlideSorter object.
 */

  document.LSS = this;

// Internal Constants


// Public functions

  this.render = LASSlideSorter_render;

  this.createCellWidgets = LASSlideSorter_createCellWidgets;
  this.createGlobalWidgets = LASSlideSorter_createGlobalWidgets;
  this.createGlobalRadios = LASSlideSorter_createGlobalRadios;
  this.loadAllImages = LASSlideSorter_loadAllImages;
  this.loadContentCell = LASSlideSorter_loadContentCell;

  this.getCellAA = LASSlideSorter_getCellAA;
  this.switchMode = LASSlideSorter_switchMode;

// Internal functions

// Callback functions (when a Widget event is triggered)

  document.globalWidgetChoice = LASSlideSorter_globalWidgetChoice;
  document.cellWidgetChoice = LASSlideSorter_cellWidgetChoice;

// Event handlers

  document.radioChoice = LASSlideSorter_radioChoice;
  document.imgComplete = LASSlideSorter_imgComplete;

// Initialization (default settings for rows, cols and chosenMenuName)

  this.form = form;
  this.InitObject = init_object;
  this.numRows = 2;
  this.numCols = 2;
  this.chosenMenuName = 't';
  this.anomalyMode = 0;
  this.Widgets = new Object();
}

////////////////////////////////////////////////////////////
//                                                        //
// Define methods of the LASSlideSorter object          //
//                                                        //
////////////////////////////////////////////////////////////

/**
 * Creates the SlideSorter table with the desired number of
 * rows and columns.  The number of widgets to create is
 * determined from the length of the this.InitObject.
 * @param {string} element_id 'id' attribute of the element 
 *        into which the LASSlideSorter is inserted.
 * @param {int} rows number of rows in the SlideSorter
 * @param {int} cols number of cols in the SlideSorter
 * @param {string} initial_menu name of the 'Menu' that will be used for the initial
 * population of the table
 */
function LASSlideSorter_render(element_id,rows,cols,initial_menu) {

  this.element_id = element_id;
  this.numRows = rows;
  this.numCols = cols;
  this.chosenMenuName = initial_menu;

  var node = document.getElementById(this.element_id);
  var children = node.childNodes;
  var num_children = children.length;

  // Remove any children of element_id
  // NOTE:  Start removing children from the end.  Otherwise, what was
  // NOTE:  children[1]  becomes children[0] when children[0] is removed.
  for (var i=num_children-1; i>=0; i--) {
    var child = children[i];
    if (child) {
      node.removeChild(child);
    }
  }

  // Create the LASSlideSorter table

  var id_text;

  // Overall table
  var LSS_table = document.createElement('table');
  node.appendChild(LSS_table);

  var LSS_tbody = document.createElement('tbody');
  LSS_table.appendChild(LSS_tbody);

  // Control row, cell, table and tbody
  var LSS_controlRow = document.createElement('tr');
  id_text = 'LSS_controlRow';
  LSS_controlRow.setAttribute('id',id_text);
  LSS_tbody.appendChild(LSS_controlRow);

  var LSS_controlCell = document.createElement('td');
  id_text = 'LSS_controlCell';
  LSS_controlCell.setAttribute('id',id_text);
  LSS_controlRow.appendChild(LSS_controlCell);

  var LSS_controlTable = document.createElement('table');
  id_text = 'LSS_controlTable';
  LSS_controlTable.setAttribute('id',id_text);
  LSS_controlTable.setAttribute('class','regularBackground');
  LSS_controlCell.appendChild(LSS_controlTable);

  var LSS_controlBody = document.createElement('tbody');
  LSS_controlTable.appendChild(LSS_controlBody);

  // One row for the basic menus and one for instructions
  var LSS_basicMenusRow = document.createElement('tr');
  id_text = 'LSS_basicMenusRow';
  LSS_basicMenusRow.setAttribute('id',id_text);
  LSS_controlBody.appendChild(LSS_basicMenusRow);

  // BEGIN mode selection element
      var LSS_modeCell = document.createElement('td');
      LSS_modeCell.setAttribute('id','LSS_modeCell');
      LSS_modeCell.setAttribute('class','anomalyBackground');
      LSS_basicMenusRow.appendChild(LSS_modeCell);

      var LSS_modeAnchor = document.createElement('a');
      LSS_modeAnchor.setAttribute('id','LSS_modeAnchor');
      var href = 'javascript: LSS.switchMode(1)';
      LSS_modeAnchor.setAttribute('href',href);
      LSS_modeCell.appendChild(LSS_modeAnchor);

      var LSS_modeText = document.createTextNode('to Anomaly mode');
      LSS_modeAnchor.appendChild(LSS_modeText);
  // END mode selection element

  var i=1;
  for (var menuName in this.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof this.InitObject[menuName] !== 'function') { 

/*
    <td id="basicCell'>
      <table id="basicTable#">
        <tbody id="basicbody#">
          <tr id="basicRow#">
            <td id="basicRadioCell#"> </td>
            <td id="basicTitleCell#"> </td>
            <td id="basicWidgetCell#"> </td>
          </tr>
        </tbody>
      </table>
    </td>
*/
      var basicCell = document.createElement('td');
      id_text = 'basicCell' + i;
      basicCell.setAttribute('id',id_text);
      LSS_basicMenusRow.appendChild(basicCell);

      var basicTable = document.createElement('table');
      id_text = 'basicTable' + i;
      basicTable.setAttribute('id',id_text);
      basicCell.appendChild(basicTable);

      var basicBody = document.createElement('tbody');
      basicTable.appendChild(basicBody);

      var basicRow = document.createElement('tr');
      id_text = 'basicRow' + i;
      basicRow.setAttribute('id',id_text);
      basicBody.appendChild(basicRow);

      var basicRadioCell = document.createElement('td');
      id_text = 'basicRadioCell' + i;
      basicRadioCell.setAttribute('id',id_text);
      basicRow.appendChild(basicRadioCell);

// NOTE:  IE 6 & 7 don't support setting the 'name' attribute so we must do some
// NOTE:  browser sniffing at this point and branch the code.
// NOTE:  See http://www.thescripts.com/forum/thread91778.html for more information.

      if (document.all) {
        /* IE only */
        var basicRadioButton = document.createElement('<input type="radio" name="basicRadios">')
      } else {
        var basicRadioButton = document.createElement('input');
        basicRadioButton.setAttribute('type','radio');
        basicRadioButton.setAttribute('name','basicRadios');
      }
      id_text = 'basicRadioButton' + i;
      basicRadioButton.setAttribute('id',id_text);
      basicRadioButton.setAttribute('class','basicRadios');
      basicRadioCell.appendChild(basicRadioButton);
     
      var basicTitleCell = document.createElement('td');
      id_text = 'basicTitleCell' + i;
      basicTitleCell.setAttribute('id',id_text);
      basicRow.appendChild(basicTitleCell);

      var basicWidgetCell = document.createElement('td');
      id_text = 'basicWidgetCell' + i;
      basicWidgetCell.setAttribute('id',id_text);
      basicRow.appendChild(basicWidgetCell);

      i++;
    }
  }


  // Images row, cell, table and tbody
  var LSS_imagesRow = document.createElement('tr');
  id_text = 'LSS_imagesRow';
  LSS_imagesRow.setAttribute('id',id_text);
  LSS_tbody.appendChild(LSS_imagesRow);

  var LSS_imagesCell = document.createElement('td');
  LSS_imagesRow.appendChild(LSS_imagesCell);

  var LSS_imagesTable = document.createElement('table');
  id_text = 'LSS_imagesTable';
  LSS_imagesTable.setAttribute('id',id_text);
  LSS_imagesCell.appendChild(LSS_imagesTable);

  var LSS_imagesBody = document.createElement('tbody');
  LSS_imagesTable.appendChild(LSS_imagesBody);

// Now create rows and columns of imageCells

/*
    		<tr class="lss_imageRow">
          <td class="lss_imageCell">
            <table>
              <tbody>
                <tr> <td id="lss_ContentCell11" colspan="3"> <a id="lss_A11"><img id="lss_Img11"></img></a> </td> </tr>
                <tr>
                  <td id="lss_Widget11"></td>
                  <td><img id="lss_AnimatedGif11" src="JavaScript/components/mozilla_blu.gif"></img></td>
                  <td><a id="lss_Refresh11" href="javascript: LSS.loadContentCell(1,1)">Refresh</a></td>
                </tr>
              </tbody>
            </table>
          </td>
          ...
*/
  for (var i=1; i<=this.numRows; i++) {

    var imageRow = document.createElement('tr');
    imageRow.setAttribute('class','LSS_imageRow');
    LSS_imagesBody.appendChild(imageRow);

    for (var j=1; j<=this.numCols; j++) {

      var imageCell = document.createElement('td');
      var id_text = 'LSS_imageCell' + i + j;
      imageCell.setAttribute('id',id_text);
      imageCell.setAttribute('class','LSS_imageCell');
      imageRow.appendChild(imageCell);

      var imageCellTable = document.createElement('table');
      imageCellTable.setAttribute('class','LSS_imageCellTable regularBackground');
      imageCell.appendChild(imageCellTable);

      var imageCellBody = document.createElement('tbody');
      imageCellTable.appendChild(imageCellBody);

      var imageContentRow = document.createElement('tr');
      imageCellBody.appendChild(imageContentRow);

      // Inserting another table here instead of just using 'colspan'
      // because support for colspan is broken in IE7
      var cell = document.createElement('td');
      cell.setAttribute('class','regularBackground');
      imageContentRow.appendChild(cell);
      var table = document.createElement('table');
      table.setAttribute('class','regularBackground');
      cell.appendChild(table);
      var tbody = document.createElement('tbody');
      table.appendChild(tbody);
      var tr = document.createElement('tr');
      tbody.appendChild(tr);
      // End of IE7 hack

      var contentCell = document.createElement('td');
      id_text = 'LSS_ContentCell' + i + j;
      contentCell.setAttribute('id',id_text);
      contentCell.setAttribute('class','regularBackground');
      tr.appendChild(contentCell);

      var imageWidgetRow = document.createElement('tr');
      imageCellBody.appendChild(imageWidgetRow);

      // Inserting another table here instead of just using 'colspan'
      // because support for colspan is broken in IE7
      var cell = document.createElement('td');
      cell.setAttribute('class','regularBackground')
      imageWidgetRow.appendChild(cell);
      var table = document.createElement('table');
      table.setAttribute('class','regularBackground')
      cell.appendChild(table);
      var tbody = document.createElement('tbody');
      table.appendChild(tbody);
      var tr = document.createElement('tr');
      tbody.appendChild(tr);
      // End of IE7 hack

      var widgetCell = document.createElement('td');
      id_text = 'widgetCell' + i + j;
      widgetCell.setAttribute('id',id_text);
      widgetCell.setAttribute('class','widgetCell');
      tr.appendChild(widgetCell);

      var aGifCell = document.createElement('td');
      aGifCell.setAttribute('class','aGifCell');
      tr.appendChild(aGifCell);

      var aGif = document.createElement('img');
      id_text = 'aGif' + i + j;
      aGif.setAttribute('id',id_text);
      aGif.setAttribute('src','JavaScript/components/mozilla_blu.gif');
      aGifCell.appendChild(aGif);

      var refreshCell = document.createElement('td');
      refreshCell.setAttribute('class','refreshCell');
      tr.appendChild(refreshCell);

      var refresh = document.createElement('a');
      id_text = 'refresh' + i + j;
      refresh.setAttribute('id',id_text);
      var href = 'javascript: LSS.loadContentCell(' + i + ',' + j + ')';
      refresh.setAttribute('href',href);
      refreshCell.appendChild(refresh);

      var textNode = document.createTextNode('Refresh');
      refresh.appendChild(textNode);

    }
  }

  // Now that all the framework elements are in place, populate
  // them with text, widgets, etc.

  this.createGlobalRadios();
  this.createGlobalWidgets();
  this.createCellWidgets(this.chosenMenuName);
  this.loadAllImages();

}


/**
 * Associates values and onclick() event handlers with the 'lss_G#Radio' 
 * radio buttons defined in the HTML page.
 */
function LASSlideSorter_createGlobalRadios() {
// NOTE:  Radio buttons are handled directly by the LASSlideSorter.
// NOTE:  There is no 'widget interface' abstraction layer.
  var i = 1;
  var num_radios = 0;

  for (var menuName in this.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof this.InitObject[menuName] !== 'function') { 
      var radioButton_id = 'basicRadioButton' + i;
      var titleCell_id = 'basicTitleCell' + i;
      var Menu = this.InitObject[menuName];
      var RadioButton = document.getElementById(radioButton_id);
      var TitleCell = document.getElementById(titleCell_id);

// TODO:  Is this a robust way of changing the title?
      if (TitleCell.firstchild) {
        if (menuName == this.chosenMenuName) {
          TitleCell.firstChild.nodeValue = 'Compare ' + Menu.title;
        } else {
          TitleCell.firstChild.nodeValue = Menu.title;
        }
      } else {
        var textNode = document.createTextNode(Menu.title);
        if (menuName == this.chosenMenuName) {
          var textNode = document.createTextNode('Compare ' + Menu.title);
        }
        TitleCell.appendChild(textNode);
      }
      RadioButton.value = menuName;
      RadioButton.onclick = document.radioChoice;
      RadioButton.LSS = this;
      if (menuName == this.chosenMenuName) {
        RadioButton.checked = true;
        first = 0;
        TitleCell.style.fontWeight = "bold";
        TitleCell.style.color = "#D33";
      }
      i++;
      num_radios++;
    }
  }

  // NOTE:  Remove global widgets if there is only one.  It would be disabled
  // NOTE:  anyway as it is the one replicated in the imageCells.
  if (num_radios == 1) {
    var RadioButton = document.getElementById('basicRadioButton1');
    RadioButton.style.display = 'none';
//    document.getElementById('LSS_controlRow').style.display = 'none';
  }

}


/**
 * Populates each of the global Widget objects with options
 * from the 'Menu's defined in the HTML page.
 */
function LASSlideSorter_createGlobalWidgets() {
  var i = 1;
  for (var menuName in this.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof this.InitObject[menuName] !== 'function') { 
      var basicWidgetCell_id = "basicWidgetCell" + i;
      var Widget;
      var Menu = this.InitObject[menuName];

// TODO:  The javascript components are very close to having a uniform
// TODO:  'interface'.  When that happens then the only things inside the
// TODO:  statement should be the 'new ~Widget' lines.  Everything about
// TODO:  rendering, setting initial values, etc can be moved outside the block.
      switch (Menu.type) {
        case 'latitudeWidget':
          if (Menu.data.delta) {
            Widget = new LatitudeWidget(Menu.data.lo, Menu.data.hi, Menu.data.delta);
          } else {
            Widget = new LatitudeWidget(Menu.data.lo, Menu.data.hi);
          }
          Widget.render(basicWidgetCell_id);
          Widget.setCallback(document.globalWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'longitudeWidget':
          if (Menu.data.delta) {
            Widget = new LongitudeWidget(Menu.data.lo, Menu.data.hi, Menu.data.delta);
          } else {
            Widget = new LongitudeWidget(Menu.data.lo, Menu.data.hi);
          }
          Widget.render(basicWidgetCell_id);
          Widget.setCallback(document.globalWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'menuWidget':
          Widget = new MenuWidget(Menu.data);
          Widget.render(basicWidgetCell_id);
          Widget.setCallback(document.globalWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'dateWidget':
          if(Menu.render_format.indexOf('T') > 0){
            if(Menu.data.tUnits == 'hour'){
                Widget = new DateWidget( Menu.data.lo,  Menu.data.hi, 60*(Menu.data.tDelta));
            }
            if(Menu.data.tUnits == 'minute'){
                Widget = new DateWidget( Menu.data.lo,  Menu.data.hi, Menu.data.tDelta);
            }
          }else{
            Widget = new DateWidget(Menu.data.lo,  Menu.data.hi);
          }

          //Widget = new DateWidget(Menu.data.lo, Menu.data.hi);
          if (Menu.render_format) {
            Widget.render(basicWidgetCell_id,Menu.render_format);
          } else {
            Widget.render(basicWidgetCell_id,"MY");
          }
          Widget.setCallback(document.globalWidgetChoice);
          if (Menu.initial_value) {
            Widget.setValue(Menu.initial_value);
          }
          break;
      }

      if (menuName == this.chosenMenuName) { Widget.disable() };
      this.Widgets[basicWidgetCell_id] = Widget;
      i++;
    }
  }
}


/**
 * Populates each of the image cell Widget objects with the first N
 * options defined in a single 'Menu'.
 */
function LASSlideSorter_createCellWidgets(chosenMenuName) {
  var index = 0;
  for (var i=0; i<this.numRows; i++) {
    for (var j=0; j<this.numCols; j++) {
      var inum = i+1;
      var jnum = j+1;
      var widget_id = "widgetCell" + inum + jnum;
      var Widget;
      var Menu = this.InitObject[chosenMenuName];
      switch (Menu.type) {
        case 'latitudeWidget':
          if (Menu.data.delta) {
            Widget = new LatitudeWidget(Menu.data.lo, Menu.data.hi, Menu.data.delta);
          } else {
            Widget = new LatitudeWidget(Menu.data.lo, Menu.data.hi);
          }
          Widget.render(widget_id);
          Widget.setCallback(document.cellWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'longitudeWidget':
          if (Menu.data.delta) {
            Widget = new LongitudeWidget(Menu.data.lo, Menu.data.hi, Menu.data.delta);
          } else {
            Widget = new LongitudeWidget(Menu.data.lo, Menu.data.hi);
          }
          Widget.render(widget_id);
          Widget.setCallback(document.cellWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'menuWidget':
          Widget = new MenuWidget(Menu.data);
          Widget.render(widget_id);
          Widget.setCallback(document.cellWidgetChoice);
          if (Menu.initial_value) { Widget.setValue(Menu.initial_value); }
          break;
        case 'dateWidget':
          if(Menu.render_format.indexOf('T') > 0){
            if(Menu.data.tUnits == 'hour'){
                Widget = new DateWidget( Menu.data.lo,  Menu.data.hi, 60*(Menu.data.tDelta));
            }
            if(Menu.data.tUnits == 'minute'){
                Widget = new DateWidget( Menu.data.lo,  Menu.data.hi, Menu.data.tDelta);
            }
          }else{
            Widget = new DateWidget(Menu.data.lo,  Menu.data.hi);
          }

          //Widget = new DateWidget(Menu.data.lo, Menu.data.hi);
          if (Menu.render_format) {
            Widget.render(widget_id,Menu.render_format);
          } else {
            Widget.render(widget_id,"MY");
          }
          Widget.setCallback(document.cellWidgetChoice);
          if (Menu.initial_value) {
            Widget.setValue(Menu.initial_value);
          }
          break;
      }

      this.Widgets[widget_id] = Widget;
      index++;
    }
  }
}

/**
 * Reloads all the images in the table based on the current values
 * of the Widget objects.
 */
function LASSlideSorter_loadAllImages() {
  for (var i=1; i<=this.numRows; i++) {
    for (var j=1; j<=this.numCols; j++) {
      this.loadContentCell(i,j);
    }
  }
}

/**
 * Loads in image into a specified cell based on the current values
 * of the Widget objects.
 */
function LASSlideSorter_loadContentCell(row,col) {

// Modify the background color until the image is loaded
// TODO:  Clean up ugly parentNode.parentNode... stuff
// TODO:  use style sheet properties instead of hardcoding the color
  var aGifID = "aGif" + row + col;
  var aGif = document.getElementById(aGifID);

  var a1 = aGif.parentNode.parentNode.parentNode.parentNode.parentNode;
  var a2 = aGif.parentNode.parentNode.parentNode.parentNode;
  var a3 = aGif.parentNode.parentNode.parentNode;
  if (this.anomalyMode == 0) {
    a1.className = a1.className.replace(/regularBackground/,'downloadingBackground');
    a2.className = a2.className.replace(/regularBackground/,'downloadingBackground');
    a3.className = a3.className.replace(/regularBackground/,'downloadingBackground');
  } else {
    a1.className = a1.className.replace(/anomalyBackground/,'downloadingBackground');
    a2.className = a2.className.replace(/anomalyBackground/,'downloadingBackground');
    a3.className = a3.className.replace(/anomalyBackground/,'downloadingBackground');
  }
  aGif.style.visibility = 'visible';

// Create an Associative Array that will consist of name:value pairs

  var AA = new Object;
  AA["row"] = row;
  AA["col"] = col;
  AA.anomalyMode = this.anomalyMode;

// Get the values from the enabled Global Widgets 

  var i = 1;
  for (var menuName in this.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof this.InitObject[menuName] !== 'function') { 
      var widget_id = "basicWidgetCell" + i;
      var Widget = this.Widgets[widget_id];
      if (!Widget.disabled) {
        AA[menuName] = Widget.getValue();
      } 
      i++;
    }
  }

// TODO:  Need to figure out how to add the current 'view' to AA.

// Now get the values from the Cell Widgets
// Pass the Associative Array to the user defined createLASRequest()
// Use the returned LASRequest to create a URL
// Use sarissa.js to send an AJAX request to the server

  var widget_id = "widgetCell" + row + col;
  var Widget = this.Widgets[widget_id];
  AA[this.chosenMenuName] = Widget.getValue();
  var Request = createLASRequest(AA);

// TODO:  The Request prefix needs accessor methods that allow you
// TODO:  to specify the type of Request you are sending
// TODO:    - ProductServer.do
// TODO:    - GetDatasets.do
// TODO:    - GetVariables.do
// TODO:    - GetGrids.do
// TODO:    - etc.
// TODO:  and in what format the  response should come back
// TODO:    - xml
// TODO:    - json
// TODO:    - html
// TODO:    - etc.

  Request.setProperty('las','output_type','json');
  var prefix = Request.prefix; 
  var url = prefix + escape(Request.getXMLText()); 

  // 2) Send LAS Request URL using Sarissa methods
  //    Register handleLASResponse(...) as callback routine

  // Set 'output_type' back to normal (nothing)
  Request.setProperty('las','output_type','');

  var xmlhttp = new XMLHttpRequest();
  xmlhttp.open('GET', url, true);
  xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4) {
      handleLASResponse(xmlhttp.responseText,row,col,Request);
    }
  }
  xmlhttp.send(null);
}

/**
 * Returns the Associative Array of information associated with
 * a particular row and column of the Slidesorter.  This is the
 * same AA structure that used by the loadContentCell() method
 * to communicate with the createLASRequest() method in the 
 * SlideSorter.vm template.
 * @param {int} row row number
 * @param {int} col column number
 * @return AA Associative Array object with widget selection information
 */
function LASSlideSorter_getCellAA(row,col) {

// See the loadContentCell method for more explanation

  var AA = new Object;
  AA["row"] = row;
  AA["col"] = col;
  AA.anomalyMode = this.anomalyMode;

  var i = 1;
  for (var menuName in this.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof this.InitObject[menuName] !== 'function') { 
      var widget_id = "basicWidgetCell" + i;
      var Widget = this.Widgets[widget_id];
      if (!Widget.disabled) {
        AA[menuName] = Widget.getValue();
      } 
      i++;
    }
  }

  var widget_id = "widgetCell" + row + col;
  var Widget = this.Widgets[widget_id];
  AA[this.chosenMenuName] = Widget.getValue();

  return AA;
}

/**
 * Switches the mode of the SlideSorter between 'Regular' and 
 * 'Anomaly' mode.  In 'Anomaly' mode, the upper left cell
 * behaves the same but all other cells return a difference 
 * between what they specify and what is specified in the upper
 * left cell.<p>
 * This behavior requires intelligence in both LASSlideSorter.js
 * and SlideSorter.vm
 * @param {int} mode mode identifier (0 = Regular, 1 = Anomaly)
 * @param {int} col column number
 * @return AA Associative Array object with widget selection information
 */
function LASSlideSorter_switchMode(mode) {

  if (mode == 0) {
    // Switch things back to 'Regular mode'
    this.anomalyMode = 0;
    var table = document.getElementById('LSS_controlTable');
    table.className = table.className.replace(/anomalyBackground/, 'regularBackground');
    var cell = document.getElementById('LSS_modeCell');
    cell.className = cell.className.replace(/regularBackground/, 'anomalyBackground');
    var anchor = document.getElementById('LSS_modeAnchor');
    anchor.setAttribute('href','javascript: LSS.switchMode(1)');
    anchor.firstChild.nodeValue = 'to Anomaly mode';
    for (var i=1; i<=this.numRows; i++) {
      for (var j=1; j<=this.numCols; j++) {
        if (i != 1 || j != 1) {
          var id_text = 'LSS_imageCell' + i + j;
          var imageCell = document.getElementById(id_text);
          var tables = imageCell.getElementsByTagName('table')
          for (var k=0; k<tables.length; k++) {
            var table = tables[k];
            if (table.className) {
              table.className = table.className.replace(/anomalyBackground/, 'regularBackground');
            }
          }
          var cells = imageCell.getElementsByTagName('td')
          for (var k=0; k<cells.length; k++) {
            var cell = cells[k];
            if (cell.className) {
              cell.className = cell.className.replace(/anomalyBackground/, 'regularBackground');
            }
          }
        }
      }
    }
  } else {
    // Switch things to 'Anomaly mode'
    this.anomalyMode = 1;
    var table = document.getElementById('LSS_controlTable');
    table.className = table.className.replace(/regularBackground/, 'anomalyBackground');
    var cell = document.getElementById('LSS_modeCell');
    cell.className = cell.className.replace(/anomalyBackground/, 'regularBackground');
    var anchor = document.getElementById('LSS_modeAnchor');
    anchor.setAttribute('href','javascript: LSS.switchMode(0)');
    anchor.firstChild.nodeValue = 'to Regular mode';
    for (var i=1; i<=this.numRows; i++) {
      for (var j=1; j<=this.numCols; j++) {
        if (i != 1 || j != 1) {
          var id_text = 'LSS_imageCell' + i + j;
          var imageCell = document.getElementById(id_text);
          var tables = imageCell.getElementsByTagName('table')
          for (var k=0; k<tables.length; k++) {
            var table = tables[k];
            if (table.className) {
              table.className = table.className.replace(/regularBackground/, 'anomalyBackground');
            }
          }
          var cells = imageCell.getElementsByTagName('td')
          for (var k=0; k<cells.length; k++) {
            var cell = cells[k];
            if (cell.className) {
              cell.className = cell.className.replace(/regularBackground/, 'anomalyBackground');
            }
          }
        }
      }
    }
  }

  this.loadAllImages();

}


////////////////////////////////////////////////////////////
//                                                        //
// Define event handlers that are functions of document.  //
//                                                        //
////////////////////////////////////////////////////////////

/**
 * Change the background color back to normal after an image is loaded.
 */
function LASSlideSorter_imgComplete(e) {
  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  // NOTE:  'load' events require 'e.currentTarget' instead of 'e.target'. 
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.currentTarget) {
    target = e.currentTarget;
  } else {
    alert('LASSlideSorter ERROR:\n> selectChange:  This browser does not support standard javascript events.');
    return;
  }
  // TODO:  use style sheet properties instead of hardcoding the color

  var Img = target;
  var currentBackground = 'regularBackground';
  if (this.anomalyMode == 1) {
    currentBackground = 'anomlyBackground';
  }
  var ContentCell = Img.parentNode.parentNode.parentNode.parentNode.parentNode;
  ContentCell.className = ContentCell.className.replace(/downloadingBackground/,currentBackground);
  Img.aGif.style.visibility = 'hidden';

}

/**
 * The LASSlideSorter_cellWidgetChoice() function is registered as the
 * 'onchange' event handler for all automatically generated cell Widgets.
 * @param {object} Widget object that responded to the event
 * @ignore
 */
function LASSlideSorter_cellWidgetChoice(Widget) {
  var i = Number(Widget.element_id.charAt(10));
  var j = Number(Widget.element_id.charAt(11));
  var LSS = document.LSS;
  if (LSS.anomalyMode == 1 && i == 1 && j == 1) {
    document.LSS.loadAllImages();
  } else {
    LSS.loadContentCell(i,j);
  }
}

/**
 * The LASSlideSorter_globalWidgetChoice() function is registered as the
 * callback function called during event processing for all automatically 
 * generated global Widgets.
 * @param {object} Widget object that responded to the event
 * @ignore
 */
function LASSlideSorter_globalWidgetChoice(Widget) {
  document.LSS.loadAllImages();
}

/**
 * The LASSlideSorter_radioChoice() function is registered as the
 * 'onclick' event handler for all global radio buttons.
 * These are the buttons that cause a different 'Menu' to be used
 * to populate the individual cell Widgets.
 *
 * Inside of this function 'this' refers to the widget that registered
 * the event, not to the LASSlideSorter object.  Hence the
 * need to access 'document.LSS'.
 * @param {Event} e selection event
 * @ignore
 */
function LASSlideSorter_radioChoice(e) {

  // Cross-browser discovery of the event target
  // By Stuart Landridge in "DHTML Utopia ..."
  var target;
  if (window.event && window.event.srcElement) {
    target = window.event.srcElement;
  } else if (e && e.target) {
    target = e.target;
  } else {
    alert('LASSlideSorter ERROR:\n> selectChange:  This browser does not support standard javascript events.');
    return;
  }

  var Radio = target;
  var menuName = Radio.value;
  var LSS = Radio.LSS;

// Create new select objects for each cell
  LSS.createCellWidgets(menuName);
  LSS.chosenMenuName = menuName;

// Dis/en-able Global selects so that only those
// 'orthogonal' to the rows and columns are available.

  var i = 0;
  for (var menuName in LSS.InitObject) {
// NOTE:  json.js breaks for loops by adding the toJSONString() method.
// NOTE:  See:  http://yuiblog.com/blog/2006/09/26/for-in-intrigue/
    if (typeof LSS.InitObject[menuName] !== 'function') { 
      var num = i+1;
      var widget_id = "basicWidgetCell" + num;
      var title_id = "basicTitleCell" + num;
      var radio_id = "basicRadioButton" + num;
      var Widget = LSS.Widgets[widget_id];
      var Title = document.getElementById(title_id);
      var Radio = document.getElementById(radio_id);
      if (Radio.checked) {
        Widget.disable();
        Title.firstChild.nodeValue = 'Compare ' + LSS.InitObject[menuName].title;
        Title.style.fontWeight = "bold";
        Title.style.color = "#D33";
      } else {
        Widget.enable();
        Title.firstChild.nodeValue = LSS.InitObject[menuName].title;
        Title.style.fontWeight = "normal";
        Title.style.color = "#000";
      }
      i++;
    }
  }

  LSS.loadAllImages();
}
