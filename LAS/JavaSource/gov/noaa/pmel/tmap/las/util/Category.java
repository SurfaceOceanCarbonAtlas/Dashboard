package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.exception.LASException;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.ui.Util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class Category extends Container implements CategoryInterface {
	public Category(Element category) {
		super(category);
	}
	public Category (String name, String ID) {
		super(new Element("category"));
		setName(name);
		setID(ID);
	}
	public Category (String name) {
		super (new Element("category"));
		setName(name);
		try {
			setID(JDOMUtils.MD5Encode(name));
		} catch (UnsupportedEncodingException e) {
			setID(String.valueOf(Math.random()));
		}
	}
	public void addCategory(Category cat) {
		getElement().addContent((Element)cat.getElement().clone());
	}
	public void addCategory(Element cat) {
		getElement().addContent(cat);
	}
	public boolean hasCategoryChildren() {
		return !hasVariableChildren();
	}
	public boolean hasVariableChildren() {
		Element dataset = element.getChild("dataset");
		Element variables = null;
		if ( dataset != null ) {
			variables = dataset.getChild("variables");
		}

		List vars = new ArrayList();

		if ( variables != null ) {
			vars = variables.getChildren("variable");
		}

		if ( variables != null && vars.size() > 0) {
			return true;
		}
		return false;
	}
	public Dataset getDataset() {
		if ( hasVariableChildren() ) {
			return new Dataset(element.getChild("dataset"));
		} else {
			return null;
		}
	}
	public void setDataset(Element dataset) {
		if ( dataset == null ) {
			// remove it...
			Element existingDataset = element.getChild("dataset");
			if ( existingDataset != null ) {
				element.removeContent(existingDataset);
			}
		} else {
			element.addContent(dataset);
		}
	}
	public JSONObject toJSON() throws JSONException {
		ArrayList<String> asArrays = new ArrayList<String>();
		//asArrays.add("dataset");
		asArrays.add("variable");
		asArrays.add("category");
		return Util.toJSON(element, asArrays);
		//return toJSONelement(element, asArrays);
	}
	public CategorySerializable getCategorySerializable() throws LASException {
		CategorySerializable wire_cat = new CategorySerializable();
		wire_cat.setName(getName());
		wire_cat.setID(getID());
		wire_cat.setAttributes(getAttributesAsMap());
		wire_cat.setProperties(getPropertiesAsMap());		
		wire_cat.setCategoryChildren(hasCategoryChildren());
		wire_cat.setVariableChildren(hasVariableChildren());
		if ( hasVariableChildren() ) {
			if ( hasMultipleDatasets() ) {
				wire_cat.setDatasetSerializableArray(getAllDatasetSerializables());
			} else {
				wire_cat.setDatasetSerializable(getDatasetSerializable());
			}
		} 
		return wire_cat;
	}
	public boolean hasMultipleDatasets() {
		if ( element.getChildren("dataset").size() > 1 ) {
			return true;
		} else {
			return false;
		}
	}
	public DatasetSerializable getDatasetSerializable() throws LASException {
		if ( !hasVariableChildren() ) {
			throw new LASException("Attempt to get inner data set where none exists.");
		}
		
		Element dataset = element.getChild("dataset");
		return elementToDatasetSerializable(dataset);
	}
	public ArrayList<Dataset> getAllDatasets() {
		ArrayList<Dataset> datasets = new ArrayList<Dataset>();
		List datasetElements = element.getChildren("dataset");
		for (Iterator dataIt = datasetElements.iterator(); dataIt.hasNext();) {
			Element dataset = (Element) dataIt.next();
			Dataset ds = new Dataset(dataset);
			datasets.add(ds);
		}
		return datasets;
	}
	public DatasetSerializable[] getAllDatasetSerializables() throws LASException {
		List datasetElements = element.getChildren("dataset");
		DatasetSerializable[] datasets = new DatasetSerializable[datasetElements.size()];
		int i = 0;
		for (Iterator dsEIt = datasetElements.iterator(); dsEIt.hasNext();) {
			Element datasetElement = (Element) dsEIt.next();
			datasets[i] = elementToDatasetSerializable(datasetElement);
			i++;
		}
		return datasets;
	}
	private DatasetSerializable elementToDatasetSerializable(Element dataset) {
		DatasetSerializable datasetSerializable = new DatasetSerializable();
		Dataset ds = new Dataset (dataset);
		datasetSerializable.setName(ds.getName());
		datasetSerializable.setID(ds.getID());
		datasetSerializable.setCATID(getID());
		datasetSerializable.setAttributes(ds.getAttributesAsMap());
		datasetSerializable.setProperties(ds.getPropertiesAsMap());
		datasetSerializable.setVariablesSerializable(ds.getVariablesSerializable());
		return datasetSerializable;
	}
	public String getChildren_DatasetID() {
		return getAttributeValue("children_dsid");
	}
	public boolean hasVariableChildrenAttribute() {
		String value = getAttributeValue("children");
		if ( value.equals("variables") ) {
			return true;
		} else {
			return false;
		}
	}
}
