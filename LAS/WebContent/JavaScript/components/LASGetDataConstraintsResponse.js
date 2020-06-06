/*
 *
 * @author Jeremy Malczyk
 *
 */

function LASGetDataConstraintsResponse(JSONObject) {

  this.response = JSONObject;
  if(!this.response) return false;
 		
// Add methods to this object
  this.getConstraintID = LASGetDataConstraintsResponse_getConstraintID;  //what kind of Constraint is this -- Constraint or dataset?
  this.getConstraintType = LASGetDataConstraintsResponse_getConstraintType;  //what kind of Constraint is this -- Constraint or dataset?
  this.getConstraintSize = LASGetDataConstraintsResponse_getConstraintSize; //how many children does this Constraint or dataset have?
  this.getChild = LASGetDataConstraintsResponse_getChild;  //return a child of this Constraint (pass index # < getConstraintSize)	
  this.getChildrenType = LASGetDataConstraintsResponse_getChildrenType;  //what kind of children does the Constraint have (dataset, variable, or Constraint)?
  this.getChildID =  LASGetDataConstraintsResponse_getChildID;  //the ID of the child
  this.getChildType =  LASGetDataConstraintsResponse_getChildType;  //the type of the child
  this.getChildName = LASGetDataConstraintsResponse_getChildName;  //what is the name of this child Constraint?
}
////////////////////////////////////////////////////////////
// Methods of the LASGetDataConstraintsResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////




/**
 * Returns the string type of the root Constraint.
 * @return value if the DataConstraints object has properties.
 * @type String or Boolean
 */
function LASGetDataConstraintsResponse_getConstraintType() {
	if(this.response.DataConstraints.Constraint)
		if(this.response.DataConstraints.Constraint[0])
			if(this.response.DataConstraints.Constraint[0].dataset)
				return "dataset";
			else
				return "Constraint";
		else
			return null;
	else
		return null;
}
/**
 * Returns the string type of the root Constraint.
 * @return value if the DataConstraints object has properties.
 * @type String or Boolean
 */
function LASGetDataConstraintsResponse_getConstraintID() {
	if(this.getConstraintType() == "dataset")
		return this.response.DataConstraints.Constraint[0].constraint.ID;
	else
		return "Constraint";
}
/**
 * Returns Integar length of the Constraint children.
 * @return value if this.response.DataConstraints[this.getConstraintType()] exists, else returns Boolean false
 * @type Int or Boolean
 */
function LASGetDataConstraintsResponse_getConstraintSize() {
	if(this.getConstraintType() == "dataset") {
		if(this.response.DataConstraints.Constraint[0].dataset.variables.variable)
			return this.response.DataConstraints.Constraint[0].dataset.variables.variable.length;
	} else 
		if(this.response.DataConstraints[this.getConstraintType()])
			return this.response.DataConstraints[this.getConstraintType()].length;	
		else
			return false;
}

/**
 * Returns a Constraint/dataset or variable object from the response.
 * @param {integer} index of child eleement
 * @return value True if the &lt;v&gt; element is present.
 * @type Boolean
 */
function LASGetDataConstraintsResponse_getChild(i) {
   if(this.getConstraintType()=="dataset")
   	return this.response.DataConstraints.Constraint[0].dataset.variables.variable[i];
   else
   	if(this.response.DataConstraints[this.getConstraintType()]);
   		return this.response.DataConstraints[this.getConstraintType()][i];
   	return false;
}

/**
 * Returns the type of children this Constraint has
 * @return string "dataset", "Constraint", or "variable"
 * @type string
 */
function LASGetDataConstraintsResponse_getChildrenType(i) {
   switch(this.getConstraintType()) {
   	case "Constraint": return "DataConstraints"; break;
   	case "dataset": return "variables"; break;
   	default: return false;
   }
}
/**
 * Returns the type of children this Constraint has
 * @return string "dataset", "Constraint", or "variable"
 * @type string
 */
function LASGetDataConstraintsResponse_getChildType(i) {
   switch(this.getChildChildrenType(i)) {
   	case "variables": return "dataset"; break;
   	case "DataConstraints": return "Constraint"; break;
   	default: return false;
   }
}

/**
 * Returns the ID of the child referenced by the i parameter
 * @param {integer} index of the child
 * @return string id of child
 * @type string
 */
function LASGetDataConstraintsResponse_getChildID(i) {
  if(this.getChild(i).ID) return this.getChild(i).ID;
}


/**
 * Returns the ID of the child referenced by the i parameter
 * @param {integer} index of the child
 * @return string id of child
 * @type string
 */
function LASGetDataConstraintsResponse_getChildName(i) {
  if(this.getChild(i).ID) return this.getChild(i).name;
}
