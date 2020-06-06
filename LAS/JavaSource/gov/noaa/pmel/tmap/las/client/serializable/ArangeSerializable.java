package gov.noaa.pmel.tmap.las.client.serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ArangeSerializable implements IsSerializable {
	String start;
	String step;
	String size;
	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}
	/**
	 * @return the step
	 */
	public String getStep() {
		return step;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}
	/**
	 * @param step the step to set
	 */
	public void setStep(String step) {
		this.step = step;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

}
