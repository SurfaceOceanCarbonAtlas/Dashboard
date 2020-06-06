package gov.noaa.pmel.tmap.las.client.serializable;


import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OperationSerializable extends Serializable implements IsSerializable {
	OptionSerializable[] options;
	List<String> views;
    String optionsID;
	public String getOptionsID() {
		return optionsID;
	}

	public void setOptionsID(String optionsID) {
		this.optionsID = optionsID;
	}

	/**
	 * @return the views
	 */
	public List<String> getViews() {
		return views;
	}

	/**
	 * @param views the views to set
	 */
	public void setViews(List<String> views) {
		this.views = views;
	}

	/**
	 * @return the options
	 */
	public OptionSerializable[] getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(OptionSerializable[] options) {
		this.options = options;
	}

}
