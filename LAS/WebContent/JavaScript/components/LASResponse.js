/**
 * @fileoverview This file is to be included in any HTML documents that
 * wish to issue LASResponses to a Live Access Server.  This file and
 * the required 'xmldom.js' file should be included in the following
 * manner in the head of the HTML file:
 * <pre>
 * &lt;head>
 *   &lt;script language="JavaScript" src="xmldom.js"&gt;&lt;/script&gt;
 *   &lt;script language="JavaScript" src="LASResponse.js"&gt;&lt;/script&gt;
 *   ...
 * &lt;/head></pre>
 * For more information about LAS and the LASResponse please see:
 * {@link http://ferret.pmel.noaa.gov/armstrong/ Armstrong Documentation}.<br>
 *
 * For more information about xmldom.js please visit:
 * {@link http://xmljs.sourceforge.net/ XML for script} and read the
 * documentation on the '<i>Classic DOM</i>'.
 *
 * @author Jonathan Callahan
 * @version $Revision: 783 $
 */

/**
 * Constructs a new LASResponse object.<br>
 * The LASResponse object contains all the information returned from the LAS
 * product server.  An XML representation of the JSON object returned by the
 * LAS product server would look like this:
 * <pre>
 * <backend_response>
 *   <response>
 *     <result_array>
 *       <result>+
 *         ...
 *       </result>
 *     <result_array>
 *     <ID/>
 *     <date/>
 *   </response>
 * </backend_response>
 * </pre>
 * The LASResponse class defined here provides accessor methods that allow one
 * to get individual pieces of information without navigating the hierarchy.
 * @class This is the basic LASResponse class.
 * @constructor
 * @param {object} JSONObject instantiated from JSON serialization of the LASResponse
 * @return A new LASResponse object
 */
function LASResponse(JSONObject) {

  this.response = JSONObject.backend_response.response;

// Add methods to this object
  this.getDate = LASResponse_getDate;
  this.getID = LASResponse_getID;
  this.getImageURL = LASResponse_getImageURL;
  this.getDebugURL = LASResponse_getDebugURL;
  this.getMapScaleURL = LASResponse_getMapScaleURL;
  this.getRSSURL = LASResponse_getRSSURL;

  this.isError = LASResponse_isError;

  this.getResult = LASResponse_getResult;

// Check for incomplete LASResponse.  Sometimes it looks like:
//
//   {"backend_response":{}}

  if (!this.response) {
    throw("LASResponse is empty.");
  }

}

////////////////////////////////////////////////////////////
// Methods of the LASResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////

/**
 * Returns the Date the LASResponse was generated.
 * @return date the date the LASResponse was generated
 * @type string
 */
function LASResponse_getDate() {
  return this.response.date;
}

/**
 * Returns the ID associated with the overall LASResponse.  Upon success this will
 * correspond to the Operation ID specified in the LASRequest.
 * @return ID the ID associated with the LASResponse
 * @type string
 */
function LASResponse_getID() {
  return this.response.ID;
}

/**
 * Returns the 'url' attribute associated with the 'plot_image' Result
 * or null if not found.
 * @return url the url associated with the LASResponse
 * @type string
 */
function LASResponse_getImageURL() {
// TODO:  Throw exception when Result is not found.
  var Result = this.getResult('plot_image');
  return (Result.url);
}

/**
 * Returns the 'url' attribute associated with the 'plot_image' Result
 * or null if not found.
 * @return url the url associated with the LASResponse
 * @type string
 */
function LASResponse_getDebugURL() {
// TODO:  Throw exception when Result is not found.
  var Result = this.getResult('debug');
  return (Result.url);
}

/**
 * Returns the 'url' attribute associated with the 'plot_image' Result
 * or null if not found.
 * @return url the url associated with the LASResponse
 * @type string
 */
function LASResponse_getMapScaleURL() {
// TODO:  Throw exception when Result is not found.
  var Result = this.getResult('map_scale');
  return (Result.url);
}

/**
 * Returns the 'url' attribute associated with the 'plot_image' Result
 * or null if not found.
 * @return url the url associated with the LASResponse
 * @type string
 */
function LASResponse_getRSSURL() {
// TODO:  Throw exception when Result is not found.
  var Result = this.getResult('rss');
  return (Result.url);
}

/**
 * Returns 1 if response.ID == 'error_response' or if any result
 * has (type == 'error'), 0 otherwise.
 * @return {int} isError state of the LASResponse
 */
function LASResponse_isError() {
  var length = this.response.result.length;
  if ( this.response.ID == 'error_response') {
    return 1;
  } else {
// NOTE:  The result may be either an object or an array
// NOTE:  of objects.  We need to test for that here.
    if (length) {
      for (i=0; i<length; i++) {
        if (this.response.result[i].type == 'error') {
          return 1;
        }
      }
    } else {
      if (this.response.result.type == 'error') {
        return 1;
      }
    }
  }
  return 0;
}

/**
 * Returns the Result object matching ID or null if the
 * specified ID is not found.
 * @param ID ID attribute to match
 * @return {object} Result object from the result array
 */
// NOTE:  This presumes that only one Result of each ID may be included
// NOTE:  in the result array.  TODO:  Check this with Roland.
function LASResponse_getResult(ID) {
// TODO:  Throw exception when Result is not found?
  var length = this.response.result.length;
  if (length) {
    for (i=0; i<length; i++) {
      if (this.response.result[i].ID == ID) {
        return this.response.result[i];
      }
    }
  } else {
    if (this.response.result.ID == ID) {
        return this.response.result;
    }
  }
  return null;
}

/////////////////////////////////////////////////////////////
// Utility methods
/////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////
// Private methods
/////////////////////////////////////////////////////////////

