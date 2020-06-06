package gov.noaa.pmel.tmap.las.client.rpc;

import java.util.List;
import java.util.Map;

import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraint;
import gov.noaa.pmel.tmap.las.client.serializable.ERDDAPConstraintGroup;
import gov.noaa.pmel.tmap.las.client.serializable.ESGFDatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.FacetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.TestSerializable;

import com.google.gwt.user.client.rpc.AsyncCallback;
/**
 * GWT RPC Async definitions for LAS.
 * @author rhs
 *
 */
public interface RPCServiceAsync {
    public void getCategoryWithGrids(String catid, String dsid, AsyncCallback categoryCallback);
	public void getCategories(String catid, String dsid, AsyncCallback categoryCallback);
	public void getVariable(String dsid, String varid, AsyncCallback getVariableCallback);
    public void getGrid(String dsid, String varid, AsyncCallback gridCallback);
    public void getOperations(String view, String dsid, String varid, AsyncCallback opCallback);
	public void getOptions(String opid, AsyncCallback optionsCallback);
    public void getTimeSeries(AsyncCallback timeSeriesCallback);
    public void getConfig(String view, String catid, String dsid, String varid, AsyncCallback<ConfigSerializable> timeSeriesCallback);
    public void getPropertyGroup(String name, AsyncCallback propertyGroupCallback);
	/**
	 * @param dsid
	 * @param varid
	 * regionCallback should get an array of RegionSerializable named Regions such as the Indian Ocean
	 * @throws RPCException
	 */
    public void getRegions(String dsid, String varid, AsyncCallback regionCallback);
	public void getOperations(String view, String[] xpath,	AsyncCallback<OperationSerializable[]> callback);
	public void getIDMap(String data_url, AsyncCallback<Map<String, String>> callback);
	public void getTestResults(String test_key, AsyncCallback<TestSerializable[]> callback);
	public void getFullDataset(String id, AsyncCallback datasetCallback);
	/*
	 * These are services that must attach to an ERDDAP server
	 */
	public void getERDDAPOuterSequenceVariables(String dsid, String varid, AsyncCallback<Map<String, String>> outerSequencVariableCallback);
	public void getERDDAPOuterSequenceValues(String dsid, String varid, String key, ERDDAPConstraint constraint, List<ConstraintSerializable> constraints, AsyncCallback<Map<String, String>> outerSequenceValueCallback);
	public void getERDDAPConstraintGroups(String dsid, AsyncCallback<List<ERDDAPConstraintGroup>> constraints);
	public void getERDDAPJSON(String dsid, String varid, String trajectory_id, String variables, AsyncCallback<String> json);
	public void getERDDAPGeometry(String catid, String dsid, String varid, String variables, AsyncCallback<String> json);
	/*
	 * Everything below this is for the ESGF serach interface.
	 */
	public void getFacets(AsyncCallback<List<FacetSerializable>> facetCallback);
	public void getESGFDatasets(String query, AsyncCallback<List<ESGFDatasetSerializable>> esgfDatasetCallback);
	public void addESGFDataset(String id, AsyncCallback<String> addESGFDatasetCallback);
    public void addESGFDatasets(List<String> ncats, AsyncCallback<String[]> addESGFDatasetsCallback);
}
