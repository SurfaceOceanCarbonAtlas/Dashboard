/*
 *
 * @author Jeremy Malczyk
 *
 * A wrapper to handle the LASGetOperationsResponse
 * 
 *
 */


function LASGetOperationsResponse(JSONObject) {

  this.response = JSONObject;
  if(!this.response) return false;
 		
// Add methods to this object
  this.getAllOperations = LASGetOperationsResponse_getAllOperations;  
  this.getOperationCount = LASGetOperationsResponse_getOperationCount;  
  this.getOperation = LASGetOperationsResponse_getOperation; 
  this.getOperationID = LASGetOperationsResponse_getOperationID;
  this.getOperationByID = LASGetOperationsResponse_getOperationByID;  	  	
  this.getOperationName = LASGetOperationsResponse_getOperationName;  
  this.getOperationsByView = LASGetOperationsResponse_getOperationsByView;
  this.getOperationsByType = LASGetOperationsResponse_getOperationsByType;
  this.getOperationTypes = LASGetOperationsResponse_getOperationTypes;
  this.getAllOperationTypes = LASGetOperationsResponse_getAllOperationTypes;
  this.getAllIntervals = LASGetOperationsResponse_getAllIntervals;
  this.getOperationIntervals = LASGetOperationsResponse_getOperationIntervals;
  this.hasInterval = LASGetOperationsResponse_hasInterval;
}
////////////////////////////////////////////////////////////
// Methods of the LASGetOperationsResponse object.
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// Public methods
////////////////////////////////////////////////////////////

function  LASGetOperationsResponse_hasInterval(id, interval) {
       
	if(this.getOperationByID(id).region)
	if(this.getOperationByID(id).region.intervals)
	if(this.getOperationByID(id).region.intervals.length) {
		for(var v=0; v<this.getOperationByID(id).region.intervals.length;v++)
                            if(this.getOperationByID(id).region.intervals[v].name==interval)
                            		return true;
	} else if(this.getOperationByID(id).region.intervals.name==interval)
			return true;
	else
		return false;
	else
		return false;
} 

function  LASGetOperationsResponse_getAllIntervals() {
        var intervals = {};
        for (var i=0;i<this.getOperationCount();i++)
                if(this.getOperation(i).region)
                         if(this.getOperation(i).region.intervals)
                                if(this.getOperation(i).region.intervals.length) {
                                        for(var v=0; v<this.getOperation(i).region.intervals.length;v++)
                                               if(!intervals[this.getOperation(i).region.intervals[v].name])
                                                                intervals[this.getOperation(i).region.intervals[v].name]=true;
                                } else if(!intervals[this.getOperation(i).region.intervals.name])
                                                                intervals[this.getOperation(i).region.intervals.name]=true;
        return intervals;
}
function  LASGetOperationsResponse_getOperationIntervals(id) {
        var intervals = {};
        if(this.getOperationByID(id).region)
                         if(this.getOperationByID(id).region.intervals)
                                if(this.getOperationByID(id).region.intervals.length) {
                                        for(var v=0; v<this.getOperationByID(id).region.intervals.length;v++)
                                               if(!intervals[this.getOperationByID(id).region.intervals[v].name])
                                                                intervals[this.getOperationByID(id).region.intervals[v].name]=this.getOperationByID(id).region.intervals[v];
                                } else if(!intervals[this.getOperationByID(id).region.intervals.name])
                                                                intervals[this.getOperationByID(id).region.intervals.name]=this.getOperationByID(id).region.intervals;
        return intervals;
}


function  LASGetOperationsResponse_getOperationsByType(type) {
        var ops = {};
        for (var i=0;i<this.getOperationCount();i++)
		if(this.getOperation(i).region)
			 if(this.getOperation(i).region.intervals) 
				if(this.getOperation(i).region.intervals.length) {
					for(var v=0; v<this.getOperation(i).region.intervals.length;v++)
        	                		if(this.getOperation(i).region.intervals[v].type==type)
                        	        		if(!ops[this.getOperationID(i)])
                        	                		ops[this.getOperationID(i)]=this.getOperation(i);
				} else if(this.getOperation(i).region.intervals.type==type)
					if(!ops[this.getOperationID(i)])
						ops[this.getOperationID(i)]=this.getOperation(i);
        return ops;
}
function  LASGetOperationsResponse_getOperationTypes(i) {
        var types = {};
	if(this.getOperation(i).region)
        	if(this.getOperation(i).region.intervals)
			 if(this.getOperation(i).region.intervals.length) {
                		for(var v=0; v<this.getOperation(i).region.intervals.length;v++)
					if(!types[this.getOperation(i).region.intervals[v].type])
        		                        types[this.getOperation(i).region.intervals[v].type]=true;
			} else if(!types[this.getOperation(i).region.intervals.type])
                             types[this.getOperation(i).region.intervals.type]=true;
        return types;
}
function  LASGetOperationsResponse_getAllOperationTypes(i) {
        var types = {};
	for (var i=0;i<this.getOperationCount();i++)
        	for(var type in this.getOperationTypes(i))
			if(!types[type])
				types[type]=true;
        return types;
}
function  LASGetOperationsResponse_getOperationsByView(view) {
	var ops = {};
	for (var i=0;i<this.getOperationCount();i++)
		if(this.getOperation(i).region)
			if(this.getOperation(i).region.intervals)
				if(this.getOperation(i).region.intervals.interval) {
					if(this.getOperation(i).region.intervals.interval.name==view)
						if(!ops[this.getOperationID(i)])
		                                        ops[this.getOperationID(i)]=this.getOperation(i);
				} else 
					for(var v=0; v<this.getOperation(i).region.intervals.length;v++)
						if(this.getOperation(i).region.intervals[v].name==view)
							if(!ops[this.getOperationID(i)])
								ops[this.getOperationID(i)]=this.getOperation(i);
	return ops;
}

/**
 * Returns an array of all Operation objects in the response.
 * @return value if the categories object has properties.
 * @type Object
 */
function LASGetOperationsResponse_getAllOperations() {
	if(this.response.operations.operation)
		return this.response.operations.operation;
	else
		return null;
}
/**
 * Returns the number of Operations available.
 * @return Value or -1.
 * @type Value
 */
function LASGetOperationsResponse_getOperationCount() {
	if(this.getAllOperations())
		return this.getAllOperations().length;
	else
		return -1;
}
/**
 * Returns a Operation object.
 * @param {integer} index of the Operation
 * @return Object
 * @type Object or null
 */
function LASGetOperationsResponse_getOperation(i) {
	if(i>=0&&i<this.getOperationCount())
		return this.getAllOperations()[i];
	else
		return null;
}

/**
 * Returns a Operation ID from the response.
 * @param {integer} index of the Operation
 * @return value True if the &lt;v&gt; element is present.
 * @type Boolean
 */
function LASGetOperationsResponse_getOperationID(i) {
   if(this.getOperation(i)) {
   	return this.getOperation(i).ID;
	}   else
   	return null;
}
/**
 * Returns a Operation object from the response.
 * @param {string} the id of the Operation
 * @return Object or null
 * @type Object
 */
function LASGetOperationsResponse_getOperationByID(id) {
   for(var i=0;i<this.getOperationCount();i++) {
   	if(this.getOperation(i).ID == id)
			return this.getOperation(i);
	} 
   	return null;
}
/**
 * Returns the name of a Operation
 * @param {int} the index of the Operation
 * @return string 
 * @type string
 */
function LASGetOperationsResponse_getOperationName(i) {
  if(this.getOperation(i)) {
		return this.getOperation(i).name	
	} else
		return null;
}
