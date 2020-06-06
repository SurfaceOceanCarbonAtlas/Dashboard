package gov.noaa.pmel.tmap.las.client.serializable;

public class AnalysisAxisSerializable {
	String hi;
	String lo;
	String op;
	String type;
	
	public String getHi() {
		return hi;
	}
	public void setHi(String hi) {
		this.hi = hi;
	}
	public String getLo() {
		return lo;
	}
	public void setLo(String lo) {
		this.lo = lo;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return "Analysis Axis: type="+type+" op= "+op+" hi="+hi+" lo="+lo;
	}
}
