package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ERDDAPConstraintGroup implements IsSerializable {

    String name;
    String type;
    String dsid;
 
    List<ERDDAPConstraint> constraints = new ArrayList<ERDDAPConstraint>();
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<ERDDAPConstraint> getConstraints() {
        return constraints;
    }
    public void setConstraints(List<ERDDAPConstraint> constraints) {
        this.constraints = constraints;
    }
    
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
    public void add(ERDDAPConstraint constraint) {
        constraints.add(constraint);
    }
    

}
