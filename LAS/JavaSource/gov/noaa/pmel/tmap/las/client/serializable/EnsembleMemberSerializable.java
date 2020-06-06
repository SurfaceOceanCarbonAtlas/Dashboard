package gov.noaa.pmel.tmap.las.client.serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EnsembleMemberSerializable implements IsSerializable {
	String name;
	String ID;
	String url;
	EnsembleVariableSerializable[] variables;
	public EnsembleMemberSerializable() {
		this.name = null;
		this.ID = null;
		this.url = null;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	public String getURL() {
		return url;
	}
	public void setURL(String url) {
		this.url = url;
	}
	public EnsembleVariableSerializable[] getMemberVariables() {
		return variables;
	}
	public void setMemberVariables(EnsembleVariableSerializable[] memberVariables) {
		variables = memberVariables;
	}
}
