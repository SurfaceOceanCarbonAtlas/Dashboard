/**
 * @fileoverview This file is to be included in any HTML documents that
 * wish to use LASContentCells to intelligently handle the LASResponse
 * coming back from LAS.  DOM elements appropriate to the LASResponse
 * are created and populated with information gleaned from Results
 * within the LASResponse.
 * 
 * This file requires that the LASReponse.js file also be included in 
 * the following manner in the head of the HTML file:
 * <pre>
 * &lt;head>
 *   &lt;script language="JavaScript" src="LASResponse.js"&gt;&lt;/script&gt;
 *   &lt;script language="JavaScript" src="LASContentCell.js"&gt;&lt;/script&gt;
 *   ...
 * &lt;/head></pre>
 * For more information about LAS and the LASContentCell please see:
 * {@link http://ferret.pmel.noaa.gov/armstrong/ Armstrong Documentation}.<br>
 *
 * @author Jonathan Callahan
 * @version $Revision: $
 */

/**
 * Constructs a new LASContentCell object.<br>
 * The LASContentCell object knows how to deal with the following
 * types of information returned in the LASResponse:
 * <ul>
 *   <li> errors
 *   <li> images
 * </ul>
 * Based on the type of response, LASContentCell fills a table cell, 
 * identified by element_id, with an appropriate sub-table and then 
 * displays the results of the LAS response in the sub-table.
 * @class This is the basic LASContentCell class.
 * @constructor
 * @return A new LASContentCell object
 */
function LASContentCell() {

// Add methods to this object
  this.render = LASContentCell_render;
  this.display = LASContentCell_display;

}

////////////////////////////////////////////////////////////
// Methods of the LASContentCell object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////

/**
 * Creates a sub-table within element_id containing elements appropriate to the LASResponse.
 * <p>
 * Any children of element_id will be removed and replaced with new sub-table elements.
 * <p>
 * @param {string} element_id 'id' attribute of the element into which the ContentCell is inserted.
 * @param {string} type ignored for now
 */
function LASContentCell_render(element_id,type) {

  this.element_id = element_id;
  if (type) { this.type = type; }

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

  // Create the table that will contain the content

  var id_text;

  var CC_table = document.createElement('table');
  id_text = this.element_id + '_table';
  CC_table.setAttribute('id',id_text);
  CC_table.setAttribute('border',2);
  node.appendChild(CC_table);

  var CC_tbody = document.createElement('tbody');
  id_text = this.element_id + '_tbody';
  CC_tbody.setAttribute('id',id_text);
  CC_table.appendChild(CC_tbody);

  var CC_tr = document.createElement('tr');
  id_text = this.element_id + '_tr';
  CC_tr.setAttribute('id',id_text);
  CC_tbody.appendChild(CC_tr);

  var CC_td = document.createElement('td');
  id_text = this.element_id + '_td';
  CC_td.setAttribute('id',id_text);
  CC_tr.appendChild(CC_td);

  // Now that the table is in place, the sendLASRequest() method will 
  // be in charge of sending the request and appending the appropriate
  // DOM elements based on the LASResponse.

}

/**
 * Displays the contents of the LASResponse Result of type = 'type'.
 * <p>
 * Any previous results/errors will be removed and replaced with the current results/errors.
 * <p>
 * If the calling javascript adds the <code>large_img_url</code> property to the
 * LASResponse object passed in, displayed images will be clickable and will link
 * to the URL found in <code>LASResponse.large_img_url</code>.
 * @param {Object} LASResponse LASResponse object
 * @param {string} result_type type of result to be displayed
 */
function LASContentCell_display(LASResponse,result_type) {

  // TODO:  Should we use 'type' or 'ID' for this switch in the code?
  // Default behavior is to plot the image found in the LASResponse.
  if (!result_type) { result_type = 'plot_image'; }

  var cell_id = this.element_id + '_td';
  var node = document.getElementById(cell_id);
  var children = node.childNodes;
  var num_children = children.length;

  // Remove any children of cell_id
  // NOTE:  Start removing children from the end.  Otherwise, what was
  // NOTE:  children[1]  becomes children[0] when children[0] is removed.
  for (var i=num_children-1; i>=0; i--) {
    var child = children[i];
    if (child) {
      node.removeChild(child);
    }
  }

  // Check for errors first

//  if (LASResponse.isError()) {
//    result_type = 'error';
//  }

  // Add the appropriate Elements to display the Result

  switch (result_type) {

    case 'full_error':
      var CC_div = document.createElement('div');
      CC_div.setAttribute('class','CCell_las_message');
      node.appendChild(CC_div);
      var text = LASResponse.getResult('las_message').content;
      var textNode = document.createTextNode(text);
      CC_div.appendChild(textNode);

      CC_div = document.createElement('div');
      CC_div.setAttribute('class','CCell_text');
      node.appendChild(CC_div);
      var text = LASResponse.getResult('exception_message').content;
      var sub_header = 'exception message is ' + text.length + ' characters long.  The first 200 are:'
      var textNode = document.createTextNode(sub_header);
      CC_div.appendChild(textNode);

      CC_div = document.createElement('div');
      CC_div.setAttribute('class','CCell_exception_message');
      node.appendChild(CC_div);
      var sub_text = text.slice(0,200) + ' ...';
      var textNode = document.createTextNode(sub_text);
      CC_div.appendChild(textNode);

      break;

    case 'las_message':
      var CC_div = document.createElement('div');
      CC_div.setAttribute('class','CCell_las_message');
      node.appendChild(CC_div);
      var text = LASResponse.getResult('las_message').content;
      var textNode = document.createTextNode(text);
      CC_div.appendChild(textNode);

      CC_div = document.createElement('div');
      CC_div.setAttribute('class','CCell_error_link');
      node.appendChild(CC_div);
      var error_link = document.createElement('a');
      error_link.href = LASResponse.getResult('debug').url;
      textNode = document.createTextNode('More details about this error.');
      error_link.appendChild(textNode);
      CC_div.appendChild(error_link);

      break;

    case 'plot_image':
      var CC_anchor = document.createElement('a');
      CC_anchor.setAttribute('target','_blank');
      if (LASResponse.large_img_url) {
        CC_anchor.href = LASResponse.large_img_url;
      }
      node.appendChild(CC_anchor);

      var CC_img = document.createElement('img');
      CC_img.setAttribute('src',LASResponse.getImageURL());
      CC_anchor.appendChild(CC_img);
      break;

  }

}

////////////////////////////////////////////////////////////
// Utility methods
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Private methods
////////////////////////////////////////////////////////////

