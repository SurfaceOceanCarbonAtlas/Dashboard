package gov.noaa.pmel.tmap.las.client.laswidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.noaa.pmel.tmap.las.client.serializable.AxisSerializable;

public class EnsembleAxisWidget extends AxisWidget {
    
    public static final String MEAN = "Mean";
    
    public static List<String> ANALYSIS_LABEL = new ArrayList<String>();
    static {
        ANALYSIS_LABEL.add("Mean");
        ///ANALYSIS.add("Weighted Mean");
    }
    public static List<String> ANALYSIS_VALUE = new ArrayList<String>();
    static {        
        ANALYSIS_VALUE.add("Average");
        ///ANALYSIS.add("Weighted Mean");
    }
    public EnsembleAxisWidget() {
        super();   
    }
    @Override
    public void init(AxisSerializable ax) {
        this.range = false;
        initialize(ax);
        for (int i = ANALYSIS_LABEL.size() - 1; i >= 0; i--) {
            String analysis = ANALYSIS_LABEL.get(i);
            lo_axis.insertItem(analysis, 0);
        }
        lo_axis.setSelectedIndex(0);
    }
    public String getSelectedLabel() {
        return lo_axis.getItemText(lo_axis.getSelectedIndex());
    }
    
    
}
