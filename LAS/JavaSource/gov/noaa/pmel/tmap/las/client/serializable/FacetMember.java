package gov.noaa.pmel.tmap.las.client.serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FacetMember implements IsSerializable {
    String name;
    int count;
    public FacetMember() {
        
    }
    public FacetMember(String name, int count) {
        super();
        this.name = name;
        this.count = count;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

}
