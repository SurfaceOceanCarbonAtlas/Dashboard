/*
 *
 * @author Jeremy Malczyk
 *
 * A wrapper to handle the LASGetViewsResponse
 * 
 *
 */

function LASGetViewsResponse(JSONObject) {

  this.response = JSONObject;
  if(!this.response) return false;
 		
// Add methods to this object
  this.getAllViews = LASGetViewsResponse_getAllViews;  
  this.getViewCount = LASGetViewsResponse_getViewCount;  
  this.getView = LASGetViewsResponse_getView; 
  this.getViewID = LASGetViewsResponse_getViewID;
  this.getViewByID = LASGetViewsResponse_getViewByID;  	  	
  this.getViewName = LASGetViewsResponse_getViewName;  
}
////////////////////////////////////////////////////////////
// Methods of the LASGetViewsResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////




/**
 * Returns an array of all view objects in the response.
 * @return value if the categories object has properties.
 * @type Object
 */
function LASGetViewsResponse_getAllViews() {
	if(this.response.views.view)
		return this.response.views.view;
	else
		return null;
}
/**
 * Returns the number of views available.
 * @return Value or -1.
 * @type Value
 */
function LASGetViewsResponse_getViewCount() {
	if(this.getAllViews())
		return this.getAllViews().length;
	else
		return -1;
}
/**
 * Returns a view object.
 * @param {integer} index of the view
 * @return Object
 * @type Object or null
 */
function LASGetViewsResponse_getView(i) {
	if(i>=0&&i<this.getViewCount())
		return this.getAllViews()[i];
	else
		return null;
}

/**
 * Returns a view ID from the response.
 * @param {integer} index of the view
 * @return value True if the &lt;v&gt; element is present.
 * @type Boolean
 */
function LASGetViewsResponse_getViewID(i) {
   if(this.getView(i)) {
   	return this.getView(i).value;
	}   else
   	return null;
}
/**
 * Returns a view object from the response.
 * @param {string} the id of the view
 * @return Object or null
 * @type Object
 */
function LASGetViewsResponse_getViewByID(id) {
   for(var i=0;i<this.getViewCount();i++) {
   	if(this.getView(i).value == id)
			return this.getView(i);
	} 
   	return null;
}
/**
 * Returns the name of a view
 * @param {int} the index of the view
 * @return string 
 * @type string
 */
function LASGetViewsResponse_getViewName(i) {
  if(this.getView(i)) {
		return this.getView(i).name	
	} else
		return null;
}
