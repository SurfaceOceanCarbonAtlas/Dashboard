package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ERDDAPConstraint implements IsSerializable {
    
    String name;
    String widget;
    String dsid;
  
    Map<String, String> labels;
    List<VariableSerializable> variables;
    String key;
    public String getWidget() {
        return widget;
    }
    public void setWidget(String widget) {
        this.widget = widget;
    }
    public Map<String, String> getLabels() {
        return labels;
    }
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
    public List<VariableSerializable> getVariables() {
        return variables;
    }
    public void setVariables(List<VariableSerializable> variables) {
        this.variables = variables;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
    

}
