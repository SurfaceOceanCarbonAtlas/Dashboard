package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConstraintSerializable implements IsSerializable {
	String label;
    String type;
    String dsid;
    String varid;
    String lhs;
    String op;  // should be one of gt, ge, eq, lt, le, or like
    String rhs;
    String id;
    public ConstraintSerializable() {
        super();
    }
    public ConstraintSerializable(String type, String dsid, String varid, String lhs, String op, String rhs, String id, String label) {
        super();
        this.label = label;
        this.type = type;
        this.dsid = dsid;
        this.varid = varid;
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
        this.id = id;
    }
    
    public String getLabel() {
    	return this.label;
    }
    public void setLabel(String label) {
    	this.label = label;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLhs() {
        return lhs;
    }
    public void setLhs(String lhs) {
        this.lhs = lhs;
    }
    public String getOp() {
        return op;
    }
    public void setOp(String op) {
        this.op = op;
    }
    public String getRhs() {
        return rhs;
    }
    public void setRhs(String rhs) {
        this.rhs = rhs;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
    public String getVarid() {
        return varid;
    }
    public void setVarid(String varid) {
        this.varid = varid;
    }
}
