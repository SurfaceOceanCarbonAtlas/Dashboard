package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalysisSerializable {

    Logger logger = Logger.getLogger("AnalysisSerializable");

    String label = " ";

    Map<String, AnalysisAxisSerializable> axes = new HashMap<String, AnalysisAxisSerializable>();

    public String getLabel() {
        if ( label == null ) // Avoid stopping IE
            return " ";
        return label;
    }

    public void setLabel(String label) {
    	logger.setLevel(Level.OFF);
        this.label = label;
        logger.info("label set to:");
        if ( label == null )
            logger.warning("null");
        else
            logger.info(this.label);
    }

    public Map<String, AnalysisAxisSerializable> getAxes() {
        return axes;
    }

    public void setAxes(List<AnalysisAxisSerializable> axes) {
        Map<String, AnalysisAxisSerializable> axMap = new HashMap<String, AnalysisAxisSerializable>();
        for ( Iterator axIt = axes.iterator(); axIt.hasNext(); ) {
            AnalysisAxisSerializable axis = (AnalysisAxisSerializable) axIt.next();
            axMap.put(axis.getType(), axis);
        }
        this.axes = axMap;
    }

    public void setAxes(Map<String, AnalysisAxisSerializable> axes) {
        this.axes = axes;
    }

    public void addAxis(AnalysisAxisSerializable axis) {
        this.axes.put(axis.getType(), axis);
    }
    public boolean isActive(String axis) {
        AnalysisAxisSerializable a = axes.get(axis);
        if ( a == null ) {
            return false;
        } else {
            return a.getOp() != null;
        }
    }

    public String getAnalysisAxes() {
        Set<String> keys = axes.keySet();
        StringBuilder al = new StringBuilder();
        if ( keys.contains("x") ) {
            al.append("x");
        }
        if ( keys.contains("y") ) {
            al.append("y");
        }
        if ( keys.contains("z") ) {
            al.append("z");
        }
        if ( keys.contains("t") ) {
            al.append("t");
        }
        if ( keys.contains("e") ) {
            al.append("e");
        }
        if ( keys.contains("f") ) {
        	al.append("f");
        }
        return al.toString();
    }
    public String toString() {
        StringBuilder ts = new StringBuilder();
        for ( Iterator keyIt = axes.keySet().iterator(); keyIt.hasNext(); ) {
            String key = (String) keyIt.next();
            AnalysisAxisSerializable a = axes.get(key);
            if ( a.getOp() != null ) {
                ts.append(a.toString());
            }
        }
        return ts.toString();
    }
}
