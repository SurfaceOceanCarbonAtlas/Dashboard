/*
 *
 * @author Jeremy Malczyk
 *
 * A wrapper to handle the LASGetOptionsResponse
 * 
 *
 */

function LASGetOptionsResponse(JSONObject) {

  this.response = JSONObject;
  if(!this.response) return false;
 		
// Add methods to this object
  this.getAllOptions = LASGetOptionsResponse_getAllOptions;  
  this.getOptionCount = LASGetOptionsResponse_getOptionCount;  
  this.getOption = LASGetOptionsResponse_getOption; 
  this.getOptionID = LASGetOptionsResponse_getOptionID;
  this.getOptionByID = LASGetOptionsResponse_getOptionByID;  	  	
  this.getOptionName = LASGetOptionsResponse_getOptionName; 
  this.getOptionType = LASGetOptionsResponse_getOptionType;  
}
////////////////////////////////////////////////////////////
// Methods of the LASGetOptionsResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////




/**
 * Returns an array of all Option objects in the response.
 * @return value if the categories object has properties.
 * @type Object
 */
function LASGetOptionsResponse_getAllOptions() {
	if(this.response.options.option)
		return this.response.options.option;
	else
		return null;
}
/**
 * Returns the number of Options available.
 * @return Value or -1.
 * @type Value
 */
function LASGetOptionsResponse_getOptionCount() {
	if(this.getAllOptions())
		return this.getAllOptions().length;
	else
		return -1;
}
/**
 * Returns a Option object.
 * @param {integer} index of the Option
 * @return Object
 * @type Object or null
 */
function LASGetOptionsResponse_getOption(i) {
	if(i>=0&&i<this.getOptionCount())
		return this.getAllOptions()[i];
	else
		return null;
}

/**
 * Returns a Option ID from the response.
 * @param {integer} index of the Option
 * @return value True if the &lt;v&gt; element is present.
 * @type Boolean
 */
function LASGetOptionsResponse_getOptionID(i) {
   if(this.getOption(i)) {
   	return this.getOption(i).ID;
	}   else
   	return null;
}
/**
 * Returns a Option object from the response.
 * @param {string} the id of the Option
 * @return Object or null
 * @type Object
 */
function LASGetOptionsResponse_getOptionByID(id) {
   for(var i=0;i<this.getOptionCount();i++) {
   	if(this.getOption(i).ID == id)
			return this.getOption(i);
	} 
   	return null;
}
/**
 * Returns the name of a Option
 * @param {int} the index of the Option
 * @return string 
 * @type string
 */
function LASGetOptionsResponse_getOptionName(i) {
  if(this.getOption(i)) {
		return this.getOption(i).title	
	} else
		return null;
}
/**
 * Returns the type of a Option
 * @param {int} the index of the Option
 * @return string 
 * @type string
 */
function LASGetOptionsResponse_getOptionType(i) {
  if(this.getOption(i).menu) {
		return "menu";
	} else if (this.getOption(i).textfield)
		return "text";
	else
		return null;
}
