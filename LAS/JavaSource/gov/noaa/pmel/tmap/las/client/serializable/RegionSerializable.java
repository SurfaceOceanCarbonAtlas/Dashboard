package gov.noaa.pmel.tmap.las.client.serializable;

public class RegionSerializable extends Serializable {
	double westLon;
	double eastLon;
	double southLat;
	double northLat;
	public double getWestLon() {
		return westLon;
	}
	public void setWestLon(double westLon) {
		this.westLon = westLon;
	}
	public double getEastLon() {
		return eastLon;
	}
	public void setEastLon(double eastLon) {
		this.eastLon = eastLon;
	}
	public double getSouthLat() {
		return southLat;
	}
	public void setSouthLat(double southLat) {
		this.southLat = southLat;
	}
	public double getNorthLat() {
		return northLat;
	}
	public void setNorthLat(double northLat) {
		this.northLat = northLat;
	}
}
