/**
 * @fileoverview This file is to be included in any HTML documents that
 * wish to issue LASRequests to a Live Access Server.  This file and
 * the required 'xmldom.js' file should be included in the following
 * manner in the head of the HTML file:
 * <pre>
 * &lt;head>
 *   &lt;script language="JavaScript" src="xmldom.js"&gt;&lt;/script&gt;
 *   &lt;script language="JavaScript" src="LASRequest.js"&gt;&lt;/script&gt;
 *   ...
 * &lt;/head></pre>
 * For more information about LAS and the LASRequest please see:
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
 * Constructs a new LASRequest object.<br>
 * An LASRequest object contains all the information needed to generate an XML
 * representation of an LASRequest.  Javascript within an HTML page can modify
 * this LASRequest object and then obtain the XML string to be sent to a
 * Live Access Server (LAS).
 * @class This is the basic LASRequest class.
 * @constructor
 * @param {string} xml XML representation of an LASRequest used to initialize the LASRequest object
 * @return A new LASRequest object
 */
function LASRequest(xml) {

  if (!xml) xml = '<?xml version=\"1.0\"?><lasRequest href=\"file:las.xml\"><link match=\"/lasdata/operations/shade\"/><args><link match=\"/lasdata/datasets/coads_climatology_cdf/variables/airt\"></link><region><range type=\"x\" low=\"-180.0\" high=\"180.0\"/><range type=\"y\" low=\"-89.5\" high=\"89.5\"/><point type=\"t\" v=\"15-Jan\"/></region></args></lasRequest>';

/**
 * Internal DOM representation of the LASRequest as returned by
 * the XMLDoc() method defined in xmldom.js.
 * @private
 * @type XMLDoc
 */
  this.DOM = new XMLDoc(xml,_LASReq_parseError);
  var operationNode = this.DOM.selectNode("/link");

/*
 * Initialize additional nodes for <properties><ferret> and
 * <properties><product_server> if they are not described by
 * the incoming XML.
 */
  if (!this.DOM.selectNode('/properties')) {
    var newNode = this.DOM.createXMLNode('<properties></properties>');
    this.DOM = this.DOM.insertNodeAfter(operationNode,newNode);
  }

// Add methods to this object
  this.getOperation = LASReq_getOperation;
  this.setOperation = LASReq_setOperation;

  this.addPropertyGroup = LASReq_addPropertyGroup;
  this.removePropertyGroup = LASReq_removePropertyGroup;

  this.getProperty = LASReq_getProperty;
  this.setProperty = LASReq_setProperty;
  this.addProperty = LASReq_addProperty;
  this.removeProperty = LASReq_removeProperty;

  this.getVariable = LASReq_getVariable;
  this.setVariable = LASReq_setVariable;
  this.addVariable = LASReq_addVariable;
  this.removeVariable = LASReq_removeVariable;
  this.removeVariables = LASReq_removeVariables;
  this.replaceVariable = LASReq_replaceVariable;

  this.getDataset = LASReq_getDataset;

  // NOTE:  The current XML representation of Constraints makes it difficult to identify constraints
  // NOTE:  or to return useful stringified versions of them.  The getConstraint(), setConstraint()
  // NOTE:  and removeConstraint() methods will not be implemented at this time.
  //this.getConstraint ??
  //this.setconstraint ??
  this.addTextConstraint = LASReq_addTextConstraint;
  this.addVariableConstraint = LASReq_addVariableConstraint;
  //this.removeConstraint = LASReq_removeConstraints ??
  this.removeConstraints = LASReq_removeConstraints;
  this.getTextConstraints = LASReq_getTextConstraints;
  this.getVariableConstraints = LASReq_getVariableConstraints;

  // NOTE:  Do we need to support a get/setAnalysis(dataset,variable) method?
  this.setAnalysis = LASReq_setAnalysis;
  this.getAnalysis = LASReq_getAnalysis;
  this.removeAnalysis = LASReq_removeAnalysis;

  this.addRegion = LASReq_addRegion;
  this.removeRegion = LASReq_removeRegion;

  this.getRangeLo = LASReq_getRangeLo;
  this.getRangeHi = LASReq_getRangeHi;
  this.getAxisType = LASReq_getAxisType;
  this.setRange = LASReq_setRange;
  this.addRange = LASReq_addRange;
  this.removeRange = LASReq_removeRange;

  this.getXMLText = LASReq_getXMLText;

  this.getVariableNode = LASReq_getVariableNode;
  this.getVariableNodeByIndex = LASReq_getVariableNodeByIndex;
}

////////////////////////////////////////////////////////////
// Methods of the LASRequest object.
////////////////////////////////////////////////////////////

// <link match="/lasdata/datasets/NOAA-CIRES-CDC-CDC_Derived_NCEP_Reanalysis_Products_Surface_Flux/variables/CDC_DS40-uflx12361" ><analysis label="Monthly Mean of Momentum Flux, U-Component_1" ><axis hi="180.0" type="x" lo="-180.0" op="ave" /></analysis></link>

////////////////////////////////////////////////////////////
// Operation methods
////////////////////////////////////////////////////////////

/**
 * Returns the Operation currently assigned in the top level <code>&lt;link match=...></code> element of the LASRequest.
 * By default, the 'Armstrong' style of Operation XML is assumed, failing over to version6.x style if no operation
 * is found.<br>
 * @param {string} style (optional) style of XML for backwards compatibility:  'LAS6' for version 6.x style requests, 
 * @return named operation defined in <code>operations.xml</code>
 * @see #setOperation
 */
function LASReq_getOperation(style) {
  var operationNode = this.DOM.selectNode("/link");
  var matchString = new String(operationNode.getAttribute("match"));
  var pieces = matchString.split('/');
  var operation = "";
  if (style == 'LAS6') {
    /* /lasdata/operations/operation_name */
    operation = pieces[3];
  } else {
    /* /lasdata/operations/operation[@ID='operation_name'] */
    var operationIDString = new String(pieces[3]);
    var subpieces = operationIDString.split("'");
    operation = subpieces[1];
    if (!operation) {
      operation = this.getOperation('LAS6');
    }
  }
  return operation;
}

/**
 * Replaces the top level <code>&lt;link match=...></code> element in the LASRequest.
 * If no style is provided an 'Armstrong' style is assumed.<br>
 * @param {string} operation named operation defined in operations.xml
 * @param {string} style (optional) style of XML for backwards compatibility:  'LAS6' for version 6.x requests, 
 * anything else for 'Armstrong' requests.
 * @see #getOperation
 */
function LASReq_setOperation(operation,style) {
  var linkMatch = '';
  if (style == 'LAS6') {
    linkMatch = '/lasdata/operations/' + operation;
  } else {
    linkMatch = '/lasdata/operations/operation[@ID=\'' + operation + '\']';
  }
  var operationNode = this.DOM.selectNode("/link");
  operationNode.removeAttribute("match");
  operationNode.addAttribute("match",linkMatch);
}

////////////////////////////////////////////////////////////
// PropertyGroup methods
////////////////////////////////////////////////////////////

/**
 * Adds a <code>&lt;properties>&lt;<i>group</i>>&lt;<i>/group</i>>&lt;/properties></code> element to the LASRequest.<br>
 * If the named PropertyGroup already exists, no action is taken.
 * @param {string} group named PropertyGroup (<i>e.g.</i>ferret, database_access)
 * @see #removePropertyGroup
 * @see #setProperty
 */
function LASReq_addPropertyGroup(group) {
  if (!this.DOM.selectNode('/properties')) {
    var newNode = this.DOM.createXMLNode('<properties></properties>');
    this.DOM = this.DOM.insertNodeAfter(operationNode,newNode);
  }
  var propertiesNode = this.DOM.selectNode('/properties');
  var nodePath = '/properties/' + group;
  if (!this.DOM.selectNode(nodePath)) {
    var nodeXML = '<' + group + '>' + '</' + group + '>';
    var newNode = this.DOM.createXMLNode(nodeXML);
    this.DOM = this.DOM.insertNodeInto(propertiesNode,newNode);
  }
}

/**
 * Removes an entire <code>&lt;properties>&lt;<i>group</i>>&lt;/<i>group</i>>&lt;/properties></code> element from the LASRequest.<br>
 * This will remove all properties within the named <code><i>group</i></code> from the LASRequest.
 * @param {string} group named propertyGroup (<i>e.g.</i>ferret, database_access)
 * @see #addPropertyGroup
 */
function LASReq_removePropertyGroup(group) {
  var nodePath = '/properties/' + group;
  var propertyGroupNode = this.DOM.selectNode(nodePath);
  if ( propertyGroupNode ) {
      this.DOM = this.DOM.removeNodeFromTree(propertyGroupNode);
  }
}

////////////////////////////////////////////////////////////
// Property methods
////////////////////////////////////////////////////////////

/**
 * Returns the value named Property defined in the named PropertyGroup of the LASRequest.<br>
 * A <code>null</code> value is returned if the Property is not found.
 * @param {string} group named propertyGroup (<i>e.g.</i>ferret, database_access)
 * @param {string} property name of the property 
 * @return value or <code>null</code> if not found
 * @see #setProperty
 * @see #addProperty
 * @see #removeProperty
 */
function LASReq_getProperty(group,property) {
  var nodePath = '/properties/' + group + '/' + property;
  var propertyNode = this.DOM.selectNode(nodePath);
  if (propertyNode) {
    return this.DOM.selectNodeText(nodePath);
  } else {
    return null;
  }
}

/**
 * Replaces the value of a Property element in the named PropertyGroup of the LASRequest.<br>
 * If the property is not found a new Property element will be created.
 * @param {string} group named propertyGroup (<i>e.g.</i>ferret, database_access)
 * @param {string} property name of the property 
 * @param {string} value value
 * @see #getProperty
 * @see #addProperty
 * @see #removeProperty
 */
function LASReq_setProperty(group,property,value) {
  var nodePath = '/properties/' + group + '/' + property;
  var propertyNode = this.DOM.selectNode(nodePath);
  if (propertyNode) {
    this.DOM = this.DOM.replaceNodeContents(propertyNode,value);
  } else {
    this.addProperty(group,property,value);
  }
}

/**
 * Adds a new <code>&lt;property>value&lt;property></code> element inside the named PropertyGroup element of the LASRequest.<br>
 * If the propertyGroup is missing it will be added.<br>
 * If the property already exists its value will be replaced with the incoming value.<br>
 * @param {string} group named propertyGroup (<i>e.g.</i>ferret, database_access)
 * @param {string} property name of the property 
 * @see #getProperty
 * @see #setProperty
 * @see #removeProperty
 */
function LASReq_addProperty(group,property,value) {
  var parentPath = '/properties/' + group;
  var parentNode = this.DOM.selectNode(parentPath);
  if (!parentNode) {
    this.addPropertyGroup(group);
  }
  var nodePath = '/properties/' + group + '/' + property;
  var propertyNode = this.DOM.selectNode(nodePath);
  if (propertyNode) {
    this.setProperty(group,property,value);
  } else {
    var parentPath = '/properties/' + group;
    var parentNode = this.DOM.selectNode(parentPath);
    var nodeXML = '<' + property + '>' + value + '</' + property + '>';
    var newNode = this.DOM.createXMLNode(nodeXML);
    this.DOM = this.DOM.insertNodeInto(parentNode,newNode);
  }
}

/**
 * Removes the named Property element defined in the named propertyGroup element of the LASRequest.<br>
 * @param {string} group propertyGroup (e.g. 'database_access', 'ferret', ...)
 * @param {string} property name of the property 
 * @see #getProperty
 * @see #setProperty
 * @see #addProperty
 */
function LASReq_removeProperty(group,property) {
  var nodePath = '/properties/' + group + '/' + property;
  var propertyNode = this.DOM.selectNode(nodePath);
  if ( propertyNode ) {
      this.DOM = this.DOM.removeNodeFromTree(propertyNode);
  }
}

////////////////////////////////////////////////////////////
// Variable methods
////////////////////////////////////////////////////////////

/**
 * Returns the dataset string from the <code>&lt;link match=...></code> element in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {int} data_ID (optional) index of the <code>&link match=...></code> element.  Defaults to <code>0</code> which is appropriate
 * for single variable requests.
 * @return dataset name or <code>null</code> if not found
 * @see #getVariable
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_getDataset(data_ID) {
  var index = (data_ID) ? data_ID : 0;
  var variableNode = this.DOM.selectNode('/args/link[' + index + ']');
  matchString = new String(variableNode.getAttribute("match"));
  var pieces = matchString.split('/');
  return pieces[3];
}

/**
 * Returns the variable string from the <code>&lt;link match=...></code> element in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {int} data_ID (optional) index of the <code>&lt;link match=...></code> element.  Defaults to <code>0</code> which is appropriate
 * for single variable requests.
 * @return variable name or <code>null</code> if not found
 * @see #getDataset
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_getVariable(data_ID) {
  var index = (data_ID) ? data_ID : 0;
  var variableNode = this.DOM.selectNode('/args/link[' + index + ']');
  if (variableNode) {
    matchString = new String(variableNode.getAttribute("match"));
    var pieces = matchString.split('/');
    return pieces[5];
  } else {
    return;
  }
}

/**
 * Replaces all existing dataset-variable Link elements defined in the <code>&lt;args></code> section of the LASRequest
 * with the incoming pair.<br>
 * <b>NOTE: The <code>setVariable(...)</code> method is deprecated.</b>  Multiple variables
 * are possible and the ability to modify only a single variable would normally require
 * that you identify an existing variable you wish to modify.  The current implementation of
 * the <code>setVariable(...)</code> method removes any additional variables that may be defined in the request.<p>
 * It is recommended, instead, that you clear out all variables explicitly with 
 * removeVariables() and then addVariable(...) for each variable you wish to add.
 * @param {string} dataset dataset name
 * @param {string} variable variable name
 * @see #getDataset
 * @see #getVariable
 * @see #addVariable
 * @see #removeVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_setVariable(dataset,variable) {
  this.removeVariables();
  this.addVariable(dataset,variable);
}

/**
 * Adds a <code>&lt;link match=.../></code> element to the <code>&lt;args></code> section of the LASRequest.<br>
 * This will add a new dataset-variable pair to the LASRequest.<br>
 * Note that the order in which variables appear in an LASRequest is important as differencing products
 * (as of 2007-10-24) always subtract the second variable from the first.  The LASRequest syntax currently
 * has no way of expressing this other than the order of the variables.<br>
 * @param {string} dataset dataset name
 * @param {string} variable variable name
 * @see #getDataset
 * @see #getVariable
 * @see #setVariable
 * @see #removeVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_addVariable(dataset,variable) {
  var nodeXML = '<link match=\"/lasdata/datasets/' + dataset + '/variables/' + variable + '\"></link>';
  var newNode = this.DOM.createXMLNode(nodeXML);
  var argsNode = this.DOM.selectNode("/args");
  var childNodes = argsNode.getElements();

  if ( childNodes.length == 0 ) {
    this.DOM = this.DOM.insertNodeInto(argsNode,newNode);
  } else {
    var lastIndex = childNodes.length - 1;
    var lastChildNode = childNodes[lastIndex];
    this.DOM = this.DOM.insertNodeAfter(lastChildNode,newNode);
  }
}

/**
 * Removes a single <code>&lt;link match=...></code> element defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {string} dataset dataset name
 * @param {string} variable variable name
 * @see #getDataset
 * @see #getVariable
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_OLD_removeVariable(dataset,variable) {
  var argsNode = this.DOM.selectNode("/args");
  var variableNodes = argsNode.getElements('link');
  for (i=0;i<variableNodes.length;i++) {
    matchString = new String(variableNodes[i].getAttribute("match"));
    var pieces = matchString.split('/');
    if (pieces[5] == variable) {
      this.DOM = this.DOM.removeNodeFromTree(variableNodes[i]);
    }
  }
}

/**
 * Removes a single <code>&lt;link match=...></code> element defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {int} data_ID (optional) index of the <code>&lt;link match=...></code> element.  Defaults to <code>0</code> which is appropriate
 * for single variable requests.<br>
 * If the Variables array is not as long as indicated by the data_ID index no action is taken.
 * @see #getDataset
 * @see #getVariable
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariables
 * @see #replaceVariable
 */
function LASReq_removeVariable(data_ID) {
  var index = (data_ID) ? data_ID : 0;
  var argsNode = this.DOM.selectNode("/args");
  var variableNodes = argsNode.getElements('link');
  if (variableNodes.length > index) {
    this.DOM = this.DOM.removeNodeFromTree(variableNodes[index]);
  }
}


/**
 * Removes all <code>&lt;link match=...></code> elements defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * Clears out all dataset-variable pairs defined in the LASRequest.
 * @see #getDataset
 * @see #getVariable
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariable
 * @see #replaceVariable
 */
function LASReq_removeVariables() {
  var argsNode = this.DOM.selectNode("/args");
  var variableNodes = argsNode.getElements('link');
  for (i=0;i<variableNodes.length;i++) {
    this.DOM = this.DOM.removeNodeFromTree(variableNodes[i]);
  }
}

/**
 * Replaces a single <code>&lt;link match=...></code> element defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {string} dataset dataset name in the element to be created
 * @param {string} variable variable name in the element to be created
 * @param {int} data_ID (optional) index of the <code>&lt;link match=...></code> element.  Defaults to <code>0</code> which is appropriate for single variable requests.
 * @see #getDataset
 * @see #getVariable
 * @see #setVariable
 * @see #addVariable
 * @see #removeVariable
 * @see #removeVariables
 */
function LASReq_replaceVariable(dataset,variable,data_ID) {
  var nodeXML = '<link match=\"/lasdata/datasets/' + dataset + '/variables/' + variable + '\"></link>';
  var newNode = this.DOM.createXMLNode(nodeXML);
  var index = (data_ID) ? data_ID : 0;
  var oldNode = this.DOM.selectNode('/args/link[' + index + ']');
  this.DOM = this.DOM.insertNodeAfter(oldNode,newNode);
  this.DOM = this.DOM.removeNodeFromTree(oldNode);
}

////////////////////////////////////////////////////////////
// Constraint methods
////////////////////////////////////////////////////////////

/**
 * Adds a Constraint element of type 'text' to the <code>&lt;args></code> section of the LASRequest.<br>
 * Constraints are also known as 'data options' and are used to modify or subset the data before
 * the product is created.
 * @param {string} variable variable name as left hand side
 * @param {string} operator operator (e.g. '&lt; '&lt; etc.)
 * @param {string} value right hand side
 * @see #addVariableConstraint
 * @see #removeConstraints
 */
function LASReq_addTextConstraint(variable,operator,value, id) {
  var argsNode = this.DOM.selectNode("/args");
  var nodeXML = '<constraint id=\"'+id+'\"type=\"text\"><v>' + variable + '</v><v>' + operator + '</v><v>' + value + '</v></constraint>';
  var newNode = this.DOM.createXMLNode(nodeXML);
  this.DOM = this.DOM.insertNodeInto(argsNode,newNode);
}

function LASReq_getTextConstraints() {
    var textConstraints = new Array();
/*
    tc1 ={"name":"subsampling",
          "op":"=",
          "value":"data_24hourly"
    }

    tc2 ={"name":"month",
          "op":"=",
          "value":"3"
    }

    textConstraints[0]=tc1;
    textConstraints[1]=tc2;
*/
    var argsNode = this.DOM.selectNode("/args");
    var constraintNodes = argsNode.getElements('constraint');
    var j=0;
    for (i=0;i<constraintNodes.length;i++) {
        constraintType = new String(constraintNodes[i].getAttribute("type"));
	if(constraintNodes[i].getAttribute("id"))
		var constraintID = constraintNodes[i].getAttribute("id");
        else
		var constraintID = "";
       //alert(constraintType);
        if(constraintType == "text"){
            var vNodes = constraintNodes[i].getElements('v');
            //alert(vNodes[0].getText());
            tc={"name":vNodes[0].getText(),
                "op":vNodes[1].getText(),
                "value":vNodes[2].getText(),
            	"id" : constraintID  
	    }
            textConstraints[j++]=tc;
        }
    }
    return textConstraints;
}

function LASReq_getVariableConstraints() {
    var variableConstraints = new Array();
    /*
      vc ={"dsID":"NDP_088",
           "varID":"pressure_atm",
           "op":"lt",
           "value":"1000"
          };
     */

    var argsNode = this.DOM.selectNode("/args");
    var constraintNodes = argsNode.getElements('constraint');
    var j=0;
    for (i=0;i<constraintNodes.length;i++) {
        constraintType = new String(constraintNodes[i].getAttribute("type"));
	if(constraintNodes[i].getAttribute("id"))
		var constraintID = constraintNodes[i].getAttribute("id");
        else
		var constraintID = "";
	//alert(constraintType);
        if(constraintType == "variable"){
            var the_link = constraintNodes[i].getElements('link');
            var matchString = new String(the_link[0].getAttribute("match"));
            var pieces = matchString.split('/');
            var the_dsID = pieces[3];
            var the_varID = pieces[5];
            //alert(the_dsID+"/"+the_varID);
            var the_op = constraintNodes[i].getAttribute("op");
            var vNodes = constraintNodes[i].getElements('v');
            var the_value = vNodes[0].getText();

            vc={"dsID":the_dsID, 
                "varID":the_varID,
                "op":the_op,
                "value":the_value,
		"id" : constraintID
               };
            variableConstraints[j++]=vc;

        }
    }
    return variableConstraints;
}

/**
 * Adds a Constraint element of type 'variable' to the <code>&lt;args></code> section of the LASRequest.<br>
 * Constraints of type 'variable' contain dataset-variable xpath information as the left hand side.
 * @param {string} dataset dataset name for the left hand side
 * @param {string} variable variable name for the left hand side
 * @param {string} operator operator (e.g. '&lt; '&lt; etc.)
 * @param {string} value right hand side
 * @see #addTextConstraint
 * @see #removeConstraints
 */
function LASReq_addVariableConstraint(dataset,variable,operator,value, id) {
  var argsNode = this.DOM.selectNode("/args");
  var linkXML = '<link match=\"/lasdata/datasets/' + dataset + '/variables/' + variable + '\"/>';
  var nodeXML = '<constraint id=\"'+ id + '\" type=\"variable\" op=\"' + operator + '\">' + linkXML + '<v>' + value + '</v></constraint>';
  var newNode = this.DOM.createXMLNode(nodeXML);
  this.DOM = this.DOM.insertNodeInto(argsNode,newNode);
}

/**
 * Removes all Constraint elements defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * No 'data options' will be applied before creating the product.
 * @see #addTextConstraint
 * @see #addVariableCosntraint
 */
function LASReq_removeConstraints() {
  var argsNode = this.DOM.selectNode("/args");
  var constraintNodes = argsNode.getElements('constraint');
  for (i=0;i<constraintNodes.length;i++) {
    this.DOM = this.DOM.removeNodeFromTree(constraintNodes[i]);
  }
}

////////////////////////////////////////////////////////////
// Analysis methods
////////////////////////////////////////////////////////////

/**
 * Returns an Analysis object with the following attributes:
 * <ul>
 * <li>Analysis.label - identifier for the analysis
 * <li>Analysis.axis - array of axis objects on which this analysis is defined
 * <li>Analysis.axis[#].type - one of [x|y|z|t]
 * <li>Analysis.axis[#].lo - lower bound of region of interest (axis specific)
 * <li>Analysis.axis[#].hi - upper bound of region of interest
 * <li>Analysis.axis[#].op - named Ferret transform (e.g. SUM, AVE, etc.)
 * </ul>
 * @param {int} index position of the Link node (Variable) in the LASRequest
 * @return {object} Analysis Analysis object associated with the Variable at this position or null if no Analysis object is defined
 * @see #removeAnalysis
 * @see #setAnalysis
 */
function LASReq_getAnalysis(index) {
  var Analysis = null;
  var variableNode = this.getVariableNodeByIndex(index);
  if (variableNode) {
    var analysisNode = variableNode.getElements('analysis')[0];
    if (analysisNode) {
      Analysis = new Object;
      Analysis.axis = [];
      Analysis.label = String(analysisNode.getAttribute("label"));
      var axisNodes = analysisNode.getElements('axis');
      for (i=0;i<axisNodes.length;i++) {
        Analysis.axis.push({
        	type : String(axisNodes[i].getAttribute("type")),
        	lo : String(axisNodes[i].getAttribute("lo")),
        	hi : String(axisNodes[i].getAttribute("hi")),
        	op : String(axisNodes[i].getAttribute("op"))
	});
      }
    } else {
      Analysis = null;
    }
  } else {
    Analysis = null;
  }
  return Analysis;
}

/**
 * Adds an Analysis element to an existing Variable in the <code>&lt;args></code> section of the LASRequest.<br>
 * Each analysis is applied to a single axis and will 
 * typically be a Ferret axis-compressing transform like SUM, AVE, etc.
 * The analysis object has the following form:
 * <ul>
 * <li>Analysis.label - identifier for the analysis
 * <li>Analysis.axis - array of axis objects on which this analysis is defined
 * <li>Analysis.axis[#].type - one of [x|y|z|t]
 * <li>Analysis.axis[#].lo - lower bound of region of interest (axis specific)
 * <li>Analysis.axis[#].hi - upper bound of region of interest
 * <li>Analysis.axis[#].op - named Ferret transform (e.g. SUM, AVE, etc.)
 * </ul>
 * Up to four axis objects may be included in the Analysis object.
 * @param {int} index position of the Link node (Variable) in the LASRequest
 * @param {object} Analysis object containing analysis information
 * @see #getAnalysis
 * @see #removeAnalysis
 */
function LASReq_setAnalysis(index,A) {
  var variableNode = this.getVariableNodeByIndex(index);
  var nodeXML = '<analysis label=\"' + A.label + '\"></analysis>';
  var AnalysisNode = this.DOM.createXMLNode(nodeXML);
  for (i=0;i<A.axis.length;i++) {
    var nodeXML = '<axis type=\"' + A.axis[i].type + '\" lo=\"' + A.axis[i].lo + '\" hi=\"' + A.axis[i].hi + '\" op=\"' + A.axis[i].op + '\"/>';
    var AxisNode = this.DOM.createXMLNode(nodeXML);
    AnalysisNode.addElement(AxisNode);    
  }
  this.DOM = this.DOM.insertNodeInto(variableNode,AnalysisNode);
}

/**
 * Removes the Analysis element from an existing Variable defined in the <code>&lt;args></code> section of the LASRequest.<br>
 * @param {int} index position of the Link node (Variable) in the LASRequest
 * @see #getAnalysis
 * @see #setAnalysis
 */
function LASReq_removeAnalysis(index) {
  var variableNode = this.getVariableNodeByIndex(index);
  var analysisNodes = variableNode.getElements('analysis');
  for (i=0;i<analysisNodes.length;i++) {
    this.DOM = this.DOM.removeNodeFromTree(analysisNodes[i]);
  }
}

////////////////////////////////////////////////////////////
// Region methods
////////////////////////////////////////////////////////////

/**
 * Adds a Region element to the <code>&lt;args></code> section of the LASRequest.<br>
 * The region added will initially be empty.
 * The <code>setRange()</code> method is used to populate the 'region' with axis ranges<br>
 * @see #removeRegion
 */
function LASReq_addRegion() {
  var nodeXML = '<region></region>';
  var newNode = this.DOM.createXMLNode(nodeXML);
  var argsNode = this.DOM.selectNode("/args");
  var childNodes = argsNode.getElements();

  if ( childNodes.length == 0 ) {
    this.DOM = this.DOM.insertNodeInto(argsNode,newNode);
  } else {
    var lastIndex = childNodes.length - 1;
    var lastChildNode = childNodes[lastIndex];
    this.DOM = this.DOM.insertNodeAfter(lastChildNode,newNode);
  }
}

/**
 * Removes a Region element, optionally identified by <code>region_ID</code>, from the <code>&lt;args></code> section of the LASRequest.<br>
 * If no <code>region_ID</code> is specified, a value of <code>0</code> is assumed resulting in the removal of the
 * first (or only) Region in the request.
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @see #addRegion
 */
function LASReq_removeRegion(region_ID) {
  var index = (region_ID) ? region_ID : 0;
  var argsNode = this.DOM.selectNode("/args");
  var regionNodes = argsNode.getElements('region');
  if (regionNodes.length > index) {
    this.DOM = this.DOM.removeNodeFromTree(regionNodes[index]);
  }
}

////////////////////////////////////////////////////////////
// Range methods
////////////////////////////////////////////////////////////

/**
 * Returns the value representing the 'lo' end of the Range if this axis is defined, <code>null</code> otherwise.<br>
 * @param {string} xyzt axis with which this range is associated: 'x','y','z' or 't'
 * @param {int} region_ID (optional) index of the Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @return lo value of the Range or <code>null</code>
 * @see #getRangeHi
 * @see #getAxisType
 * @see #setRange
 * @see #addRange
 * @see #removeRange
 */
function LASReq_getRangeLo(xyzt,region_ID) {
  var lo = null;
  var type = this.getAxisType(xyzt,region_ID);
  if (type == 'range') {
    var index = (region_ID) ? region_ID : 0;
    var axisNode = this.DOM.selectNode('/args/region[' + index + ']/range[@type=\"' + xyzt + '\"]');
    lo = axisNode.getAttribute('low');
  } else if (type == 'point') {
    var index = (region_ID) ? region_ID : 0;
    var axisNode = this.DOM.selectNode('/args/region[' + index + ']/point[@type=\"' + xyzt + '\"]');
    lo = axisNode.getAttribute('v');
  } else {
    lo = null;
  } 
  return lo;
}

/**
 * Returns the value representing the 'hi' end of the Range if is of type 'range', 
 * or a <code>null</code> if it is of type 'point' or is not defined.
 * @param {string} xyzt axis with which this range is associated: 'x','y','z' or 't'
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @return hi value of the Range or <code>null</code>
 * @see #getRangeLo
 * @see #getAxisType
 * @see #setRange
 * @see #addRange
 * @see #removeRange
 */
function LASReq_getRangeHi(xyzt,region_ID) {
  var hi = null;
  var type = this.getAxisType(xyzt,region_ID);
  if (type == 'range') {
    var index = (region_ID) ? region_ID : 0;
    var axisNode = this.DOM.selectNode('/args/region[' + index + ']/range[@type=\"' + xyzt + '\"]');
    hi = axisNode.getAttribute('high');
  } else {
    hi = null;
  }
  return hi;
}

/**
 * Returns the axis type ('point' or 'range') if it is found, <code>null</code> otherwise.
 * @param {string} xyzt axis with which this Range is associated: 'x','y','z' or 't'
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @return axis type ('point' or 'range') or <code>null</code> 
 * @see #getRangeLo
 * @see #getRangeHi
 * @see #setRange
 * @see #addRange
 * @see #removeRange
 */
function LASReq_getAxisType(xyzt,region_ID) {
  var type = null;
  var index = (region_ID) ? region_ID : 0;
  var axisNode = this.DOM.selectNode('/args/region[' + index + ']/range[@type=\"' + xyzt + '\"]');
  if (axisNode) {
    type = 'range';
  } else {
    axisNode = this.DOM.selectNode('/args/region[' + index + ']/point[@type=\"' + xyzt + '\"]');
    if (axisNode) {
      type = 'point';
    }
  }
  return type;
}

/**
 * Adds a new Range element to the <code>&lt;region></code> section of the LASRequest.<br>
 * If a Range along the desired axis already exists it will be replaced.
 * Otherwise, the incoming range will be added to the existing <code>&lt;region></code>.<br>
 * @param {string} xyzt axis with which this region is associated: 'x','y','z' or 't'
 * @param {string} lo lower bound of region of interest (axis specific)
 * @param {string} hi upper bound of region of interest
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @see #getRangeLo
 * @see #getRangeHi
 * @see #getAxisType
 * @see #addRange
 * @see #removeRange
 */
function LASReq_setRange(xyzt,lo,hi,region_ID) {
  this.removeRange(xyzt,region_ID);
  this.addRange(xyzt,lo,hi,region_ID);
}

/**
 * Adds a new Range element to <code>&lt;region></code> section of the LASRequest.<br>
 * If no Region with this region_ID is found, one will be created.<br>
 * If a Range along the desired axis already exists it will be replaced.
 * @param {string} xyzt axis with which this region is associated: 'x','y','z' or 't'
 * @param {string} lo lower bound of region of interest (axis specific)
 * @param {string} hi upper bound of region of interest
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @see #getRangeLo
 * @see #getRangeHi
 * @see #getAxisType
 * @see #setRange
 * @see #removeRange
 */
function LASReq_addRange(xyzt,lo,hi,region_ID) {
  var index = (region_ID) ? region_ID : 0;
  var regionNode = this.DOM.selectNode('/args/region[' + index + ']');
  if (!regionNode) {
    this.addRegion();
  }
  var nodeXML = '';
  if (hi != null && (hi != lo)) {
    nodeXML = '<range type=\"' + xyzt + '\" low=\"' + lo + '\" high=\"' + hi + '\"/>';
  } else {
    nodeXML = '<point type=\"' + xyzt + '\" v=\"' + lo + '\"/>';
  }
  var newNode = this.DOM.createXMLNode(nodeXML);
  regionNode = this.DOM.selectNode('/args/region[' + index + ']');
  this.DOM = this.DOM.insertNodeInto(regionNode,newNode);
}

/**
 * Removes a Range element tfrom the <code>&lt;region></code> section of the LASRequest.<br>
 * @param {string} xyzt axis with which this region is associated: 'x','y','z' or 't'
 * @param {int} region_ID (optional) index of Region.  Defaults to <code>0</code> which is appropriate for
 * single region requests.
 * @see #getRangeLo
 * @see #getRangeHi
 * @see #getAxisType
 * @see #setRange
 * @see #addRange
 */
function LASReq_removeRange(xyzt,region_ID) {
  var index = (region_ID) ? region_ID : 0;
  var regionNode = this.DOM.selectNode('/args/region[' + index + ']');
  // If the regionNode is found, remove the <range...> or <point...> node whose
  // 'type' attribute = xyzt.
  if (regionNode) {
    var axisNode = this.DOM.selectNode('/args/region[' + index + ']/range[@type=\"' + xyzt + '\"]');
    if (axisNode) {
      this.DOM = this.DOM.removeNodeFromTree(axisNode);
    } else {
      var axisNode = this.DOM.selectNode('/args/region[' + index + ']/point[@type=\"' + xyzt + '\"]');
      if (axisNode) {
        this.DOM = this.DOM.removeNodeFromTree(axisNode);
      }
    }
  }
}

////////////////////////////////////////////////////////////
// Utility methods
////////////////////////////////////////////////////////////

/**
 * Returns a XML string representation of the LASRequest object.
 * @return XML version of LASRequest
 * @type string
 */
function LASReq_getXMLText() {
  return this.DOM.getUnderlyingXMLText();
}

////////////////////////////////////////////////////////////
// Private methods
////////////////////////////////////////////////////////////

/**
 * Returns the Link node associated with a particular dataset,variable pair.<br>
 * @private
 * @param {string} dataset dataset name
 * @param {string} variable variable name
 */
function LASReq_getVariableNode(dataset,variable) {
  var argsNode = this.DOM.selectNode("/args");
  var variableNodes = argsNode.getElements('link');
  for (i=0;i<variableNodes.length;i++) {
    matchString = new String(variableNodes[i].getAttribute("match"));
    var pieces = matchString.split('/');
    if (pieces[5] == variable) {
      return variableNodes[i];
    }
  }
}

/**
 * Returns the n'th Link node or null if there aren't that many Link nodes defined in the request.
 * @private
 * @param {int} index position of the Link node in the LASRequest (begins with 0)
 */
function LASReq_getVariableNodeByIndex(index) {
  var argsNode = this.DOM.selectNode("/args");
  var variableNodes = argsNode.getElements('link');
  if (variableNodes[index]) {
    return variableNodes[index];
  } else {
    return null;
  }
}

/**
 * Error handler passed to XMLDoc creation method.
 * @private
 */
function _LASReq_parseError(e) {
  alert(e);
}

