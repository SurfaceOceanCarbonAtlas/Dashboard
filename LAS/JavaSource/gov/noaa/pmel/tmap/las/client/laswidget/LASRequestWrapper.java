package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.util.Util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;

/**
 * This code wraps the native JavaScript request object and allows easy access
 * to this object in GWT applications. You MUST play a little game of creating
 * the object and removing stuff from it when initializing this Widget since the
 * underlying JavaScript implementation requires it. It looks something like
 * this:
 * 
 * <pre>
 *      {@code
 * 		LASRequestWrapper lasRequest = new LASRequestWrapper();
 *      lasRequest.removeRegion(0);
 *      lasRequest.removeVariables();
 *      lasRequest.removePropertyGroup("ferret");
 *      lasRequest.addVariable("my_dsid", "my_varid");
 *      lasRequest.addRegion();
 *      lasRequest.setOperation(operationID, "v7");
 *      // You'll likely be getting the values of x and y from the map in your AxesWidgetGroup
 *      // xlo = axesWidgets.getRefMap().getXlo()
 *      lasRequest.setRange("x", -180, 180, 0); // The last zero is the "region id", namely the first region in the object
 *      lasRequest.setRange("y", -90, 90, 0);
 *      // You'll likely get the dates from your AxisWidgetGroup as well.
 *      lasRequest.setRange("t", axesWidgets.getTAxis().getFerretDateLo(), axesWidgets.getTAxis().getFerretDateHi(), 0)
 *      String url = Util.getProductServer()+"?xml="+URL.encode(lasRequest.getXMLText());
 *      RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, url);
 *      try {
 *                                        // The AsynCallback that handles the result from the request.
 *          sendRequest.sendRequest(null, lasRequestCallback);
 *      } catch (RequestException e) {
 *          // Do something
 *      }
 * }
 * 
 * <pre>
 * @see gov.noaa.pmel.tmap.las.client.laswidget.AxesWidgetGroup
 * @see gov.noaa.pmel.tmap.las.client.util.Util
 * @author rhs
 * @deprecated  Deprecated due to a bug in the underlying JavaScript. Use
 * {@link gov.noaa.pmel.tmap.las.client.laswidget.LASRequest}
 */
@Deprecated
public class LASRequestWrapper {
	private JavaScriptObject lasRequest;

	/**
	 * Construct a native JavaScript LASRequest Object with the default request.
	 */
	public LASRequestWrapper() {
		lasRequest = init();
	}

	public native static JavaScriptObject init() /*-{
													var lasRequest = new $wnd.LASRequest();
													return lasRequest;
													}-*/;

	/**
	 * Construct a native JavaScript LASRequest Object initializing it with the
	 * request XML contained in the parameter.
	 * 
	 * @param xml
	 *            - any legal LAS UI Request XML as a string.
	 */
	public LASRequestWrapper(String xml) {
		lasRequest = init(xml);
	}

	public native static JavaScriptObject init(String xml) /*-{
															var lasRequest = new $wnd.LASRequest(xml);
															return lasRequest;
															}-*/;

	/**
	 * Adds a new <property>value<property> element inside the named
	 * PropertyGroup element of the LASRequest. If the propertyGroup is missing
	 * it will be added. If the property already exists its value will be
	 * replaced with the incoming value.
	 * 
	 * @param group
	 * @param name
	 * @param value
	 */
	public void addProperty(String group, String name, String value) {
		addPropertyImpl(lasRequest, group, name, value);
	}

	private native static void addPropertyImpl(JavaScriptObject lasRequest,
			String group, String name, String value) /*-{
														lasRequest.addProperty(group, name, value);
														}-*/;

	/**
	 * Adds a <properties><group></group></properties> element to the
	 * LASRequest. If the named PropertyGroup already exists, no action is
	 * taken..
	 * 
	 * @param group
	 */
	public void addPropertyGroup(String group) {
		addPropertyImpl(lasRequest, group);
	}

	private native static void addPropertyImpl(JavaScriptObject lasRequest,
			String group) /*-{
							lasRequest.addPropertyGroup(group);
							}-*/;

	/**
	 * Adds a new Range element to <region> section of the LASRequest. If no
	 * Region with this region_ID is found, one will be created. If a Range
	 * along the desired axis already exists it will be replaced.
	 * 
	 * @param axis
	 * @param lo
	 * @param hi
	 * @param region
	 */
	public void addRange(String axis, String lo, String hi, int region) {
		addRangeImpl(lasRequest, axis, lo, hi, region);
	}

	private native static void addRangeImpl(JavaScriptObject lasRequest,
			String axis, String lo, String hi, int region) /*-{
															lasRequest.addRange(axis, lo, hi, region);
															}-*/;

	/**
	 * Adds a Region element to the <args> section of the LASRequest. The region
	 * added will initially be empty.
	 */
	public void addRegion() {
		addRegionImpl(lasRequest);
	}

	private native static void addRegionImpl(JavaScriptObject lasRequest) /*-{
																			lasRequest.addRegion();
																			}-*/;

	/**
	 * Adds a Constraint element of type 'text' to the <args> section of the
	 * LASRequest. Constraints are also known as 'data options' and are used to
	 * modify or subset the data before the product is created.
	 * 
	 * @param variable
	 * @param op
	 * @param value
	 */
	public void addTextConstraint(String variable, String op, String value) {
		addTextConstraintImpl(lasRequest, variable, op, value);
	}

	private native static void addTextConstraintImpl(
			JavaScriptObject LasRequest, String variable, String op,
			String value) /*-{
							lasRequest.addTextConstraint(variable, op, value)
							}-*/;

	/**
	 * Adds a <link match=.../> element to the <args> section of the LASRequest.
	 * This will add a new dataset-variable pair to the LASRequest. Note that
	 * the order in which variables appear in an LASRequest is important as
	 * differencing products (as of 2007-10-24) always subtract the second
	 * variable from the first.
	 * 
	 * @param dsID
	 * @param varID
	 */
	public void addVariable(String dsID, String varID) {
		addVariableImpl(lasRequest, dsID, varID);
	}

	private native static void addVariableImpl(JavaScriptObject lasRequest,
			String dsID, String varID) /*-{
										lasRequest.addVariable(dsID, varID);
										}-*/;

	/**
	 * Adds a Constraint element of type 'variable' to the <args> section of the
	 * LASRequest. Constraints of type 'variable' contain dataset-variable xpath
	 * information as the left hand side.
	 * 
	 * @param dataset
	 * @param variable
	 * @param op
	 * @param value
	 */
	public void addVariableConstraint(String dataset, String variable,
			String op, String value) {
		addVariableConstraintImpl(lasRequest, dataset, variable, op, value);
	}

	private native static void addVariableConstraintImpl(
			JavaScriptObject lasRequest, String dataset, String variable,
			String op, String value) /*-{
										lasRequest.addVariableConstraint(dataset, variable, op, value);
										}-*/;

	/**
	 * 
	 * @param index
	 * @return
	 */
	public JavaScriptObject getAnalysis(int index) {
		return getAnalysisImpl(lasRequest, index);
	}

	private native static JavaScriptObject getAnalysisImpl(
			JavaScriptObject lasRequest, int index) /*-{
													return lasRequest.getAnalysis(index);
													}-*/;

	/**
	 * Returns the axis type ('point' or 'range') if it is found, null
	 * otherwise.
	 * 
	 * @param type
	 * @param region
	 * @return
	 */
	public String getAxisType(String type, int region) {
		return getAxisTypeImpl(lasRequest, type, region);
	}

	private native static String getAxisTypeImpl(JavaScriptObject lasRequest,
			String type, int region) /*-{
										return lasRequest.getAxisType(type, region);
										}-*/;

	/**
	 * Returns the dataset string from the <link match=...> element in the
	 * <args> section of the LASRequest.
	 * 
	 * @param index
	 * @return
	 */
	public String getDataset(int index) {
		return getDatasetImpl(lasRequest, index);
	}

	private native static String getDatasetImpl(JavaScriptObject lasRequest,
			int index) /*-{
						return lasRequest.getDataset(index);
						}-*/;

	/**
	 * Returns the Operation currently assigned in the top level <link
	 * match=...> element of the LASRequest.
	 * 
	 * @param style
	 * @return
	 */
	public String getOperation(String style) {
		return getOperationImpl(lasRequest, style);
	}

	private native static String getOperationImpl(JavaScriptObject lasRequest,
			String style) /*-{
							return lasRequest.getOperation(style);
							}-*/;

	/**
	 * Returns the value named Property defined in the named PropertyGroup of
	 * the LASRequest. A null value is returned if the Property is not found.
	 * 
	 * @param group
	 * @param property
	 * @return
	 */
	public String getProperty(String group, String property) {
		return getPropertyImpl(lasRequest, group, property);
	}

	private native static String getPropertyImpl(JavaScriptObject lasRequest,
			String group, String property) /*-{
											return lasRequest.getProperty(group, property);
											}-*/;

	/**
	 * Returns the value representing the 'hi' end of the Range if is of type
	 * 'range', or a null if it is of type 'point' or is not defined.
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public String getRangeHi(String type, int id) {
		return getRangeHiImpl(lasRequest, type, id);
	}

	private native static String getRangeHiImpl(JavaScriptObject lasRequest,
			String type, int id) /*-{
									return lasRequest.getRangeHi(type, id);
									}-*/;

	/**
	 * Returns the value representing the 'lo' end of the Range if this axis is
	 * defined, null otherwise.
	 * 
	 * @param type
	 * @param id
	 * @return
	 */
	public String getRangeLo(String type, int id) {
		return getRangeLoImpl(lasRequest, type, id);
	}

	private native static String getRangeLoImpl(JavaScriptObject lasRequest,
			String type, int id) /*-{
									return lasRequest.getRangeLo(type, id);
									}-*/;

	/**
	 * Returns the variable string from the <link match=...> element in the
	 * <args> section of the LASRequest.
	 * 
	 * @param id
	 * @return
	 */
	public String getVariable(int id) {
		return getVariableImpl(lasRequest, id);
	}

	private native static String getVariableImpl(JavaScriptObject lasRequest,
			int id) /*-{
					return lasRequest.getVariable(id);
					}-*/;

	/**
	 * Returns a XML string representation of the LASRequest object.
	 * 
	 * @return
	 */
	public String getXMLText() {
		return getXMLTextImpl(lasRequest);
	}

	private native static String getXMLTextImpl(JavaScriptObject lasRequest) /*-{
																				return lasRequest.getXMLText();
																				}-*/;

	/**
	 * Removes the Analysis element from an existing Variable defined in the
	 * <args> section of the LASRequest.
	 */
	public void removeAnalysis() {
		removeAnalysisImpl(lasRequest);
	}

	private native static void removeAnalysisImpl(JavaScriptObject lasRequest) /*-{
																				lasRequest.removeAnalysis();
																				}-*/;

	/**
	 * Removes all Constraint elements defined in the <args> section of the
	 * LASRequest. No 'data options' will be applied before creating the
	 * product.
	 */
	public void removeConstraints() {
		removeConstraintsImpl(lasRequest);
	}

	private native static void removeConstraintsImpl(JavaScriptObject lasRequest) /*-{
																					lasRequest.removeConstraints();
																					}-*/;

	/**
	 * Removes the named Property element defined in the named propertyGroup
	 * element of the LASRequest.
	 * 
	 * @param group
	 * @param property
	 */
	public void removeProperty(String group, String property) {
		removePropertyImpl(lasRequest, group, property);
	}

	private native static void removePropertyImpl(JavaScriptObject lasRequest,
			String group, String property) /*-{
											lasRequest.removeProperty(group, property);
											}-*/;

	/**
	 * Removes an entire <properties><group></group></properties> element from
	 * the LASRequest. This will remove all properties within the named group
	 * from the LASRequest.
	 * 
	 * @param group
	 */
	public void removePropertyGroup(String group) {
		removePropertyImpl(lasRequest, group);
	}

	private native static void removePropertyImpl(JavaScriptObject lasRequest,
			String group) /*-{
							lasRequest.removePropertyGroup(group);
							}-*/;

	/**
	 * Removes a Range element tfrom the <region> section of the LASRequest.
	 * 
	 * @param type
	 * @param id
	 */
	public void removeRange(String type, int id) {
		removeRangeImpl(lasRequest, type, id);
	}

	private native static void removeRangeImpl(JavaScriptObject lasRequest,
			String type, int id) /*-{
									lasRequest.removeRange(type, id);
									}-*/;

	/**
	 * Removes a Region element, optionally identified by region_ID, from the
	 * <args> section of the LASRequest. If no region_ID is specified, a value
	 * of 0 is assumed resulting in the removal of the first (or only) Region in
	 * the request.
	 * 
	 * @param id
	 */
	public void removeRegion(int id) {
		removeRegionImpl(lasRequest, id);
	}

	private native static void removeRegionImpl(JavaScriptObject lasRequest,
			int id) /*-{
					lasRequest.removeRegion(id);
					}-*/;

	/**
	 * Removes a single <link match=...> element defined in the <args> section
	 * of the LASRequest.
	 * 
	 * @param id
	 */
	public void removeVariable(int id) {
		removeVariableImpl(lasRequest, id);
	}

	private native static void removeVariableImpl(JavaScriptObject lasRequest,
			int id) /*-{
					lasRequest.removeVariable(id);
					}-*/;

	/**
	 * Removes all <link match=...> elements defined in the <args> section of
	 * the LASRequest. Clears out all dataset-variable pairs defined in the
	 * LASRequest.
	 */
	public void removeVariables() {
		removeVariablesImpl(lasRequest);
	}

	private native static void removeVariablesImpl(JavaScriptObject lasRequest) /*-{
																				lasRequest.removeVariables();
																				}-*/;

	/**
	 * Replaces a single <link match=...> element defined in the <args> section
	 * of the LASRequest.
	 * 
	 * @param dsID
	 * @param varID
	 * @param index
	 */
	public void replaceVariable(String dsID, String varID, int index) {
		replaceVariableImpl(lasRequest, dsID, varID, index);
	}

	private native static void replaceVariableImpl(JavaScriptObject lasRequest,
			String dsID, String varID, int index) /*-{
													lasRequest.replaceVariable(dsID, varID, index);
													}-*/;

	/**
	 * Adds an Analysis element to an existing Variable in the <args> section of
	 * the LASRequest. Each analysis is applied to a single axis and will
	 * typically be a Ferret axis-compressing transform like SUM, AVE, etc.
	 * 
	 * @param index
	 * @param analysis
	 */
	public void setAnalysis(int index, JavaScriptObject analysis) {
		setAnalysisImpl(lasRequest, index, analysis);
	}

	private static native void setAnalysisImpl(JavaScriptObject lasRequest,
			int index, JavaScriptObject analysis) /*-{
													lasRequest.setAnalysis(i, analysis);
													}-*/;

	/**
	 * Replaces the top level <link match=...> element in the LASRequest.
	 * 
	 * @param operation
	 * @param id
	 */
	public void setOperation(String operation, String style) {
		setOperationImpl(lasRequest, operation, style);
	}

	private static native void setOperationImpl(JavaScriptObject lasRequest,
			String operation, String style) /*-{
											lasRequest.setOperation(operation, style);
											}-*/;

	/**
	 * Replaces the value of a Property element in the named PropertyGroup of
	 * the LASRequest. If the property is not found a new Property element will
	 * be created.
	 * 
	 * @param group
	 * @param property
	 * @param value
	 */
	public void setProperty(String group, String property, String value) {
		setPropertyImpl(lasRequest, group, property, value);
	}

	private static native void setPropertyImpl(JavaScriptObject lasRequest,
			String group, String property, String value) /*-{
															lasRequest.setProperty(group, property, value);
															}-*/;

	/**
	 * Adds a new Range element to the <region> section of the LASRequest. If a
	 * Range along the desired axis already exists it will be replaced.
	 * 
	 * @param type
	 * @param lo
	 * @param hi
	 * @param id
	 */
	public void setRange(String type, String lo, String hi, int id) {
		setRangeImpl(lasRequest, type, lo, hi, id);
	}

	private static native void setRangeImpl(JavaScriptObject lasRequest,
			String type, String lo, String hi, int id) /*-{
														lasRequest.setRange(type, lo, hi, id);
														}-*/;

	/**
	 * Replaces all existing dataset-variable Link elements defined in the
	 * <args> section of the LASRequest with the incoming pair. Multiple
	 * variables are possible and the ability to modify only a single variable
	 * would normally require that you identify an existing variable you wish to
	 * modify.
	 * 
	 * @param dsID
	 * @param varID
	 * @deprecated
	 */
	public void setVariable(String dsID, String varID) {
		setVariableImpl(lasRequest, dsID, varID);
	}

	private static native void setVariableImpl(JavaScriptObject lasRequest,
			String dsID, String varID) /*-{
										lasRequest.setVariable(dsID, varID);
										}-*/;

	public String toString() {
		return getXMLTextImpl(lasRequest);
	}
}
