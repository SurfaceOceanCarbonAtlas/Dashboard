package gov.noaa.pmel.tmap.addxml;

public class ErddapReturn {
	
	public boolean write;
	public String type;
	public String unknown_axis;
	
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnknown_axis() {
		return unknown_axis;
	}
	public void setUnknown_axis(String unknown_axis) {
		this.unknown_axis = unknown_axis;
	}
	
	

}
