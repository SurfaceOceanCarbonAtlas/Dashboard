package gov.noaa.pmel.tmap.las.client.serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnsembleVariableSerializable implements IsSerializable {
	String ID;
	String name;
	String short_name;
	public EnsembleVariableSerializable() {
		
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setShortName(String short_name) {
		this.short_name = short_name;
	}
	public String getShortName() {
		return this.short_name;
	}
}
