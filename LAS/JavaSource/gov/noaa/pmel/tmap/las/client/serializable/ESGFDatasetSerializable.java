package gov.noaa.pmel.tmap.las.client.serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ESGFDatasetSerializable implements IsSerializable {
    
    String name;
    String id;
    String LASID;
    String node;
    int total;
    int position;
    boolean alreadyAdded;
    
    public ESGFDatasetSerializable() {
        
    }

    public ESGFDatasetSerializable(String name, String id, String node, int total, int position, String lasid, boolean alreadyAdded) {
        super();
        this.name = name;
        this.id = id;
        this.node = node;
        this.total = total;
        this.position = position;
        this.LASID = lasid;
        this.alreadyAdded = alreadyAdded;
    }

    public void setAlreadyAdded(boolean added) {
        this.alreadyAdded = added;
    }
    public boolean isAlreadyAdded() {
        return this.alreadyAdded;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    
    public String getLASID() {
        return LASID;
    }
    public void setLASID(String lasid) {
        LASID = lasid;
    }
    
}
