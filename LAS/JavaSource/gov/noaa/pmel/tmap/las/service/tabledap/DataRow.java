package gov.noaa.pmel.tmap.las.service.tabledap;

import java.util.HashMap;
import java.util.Map;

public class DataRow {
    
    Double time;
    String id;
    Map<String, Object> data;
    Map<String, Object> subsets;
    
    public DataRow() {
        this.data = new HashMap<String, Object>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTime() {
        return time;
    }
    public Map<String, Object> getSubsets() {
        return subsets;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setSubsets(Map<String, Object> subsets) {
        this.subsets = subsets;
    }
}
