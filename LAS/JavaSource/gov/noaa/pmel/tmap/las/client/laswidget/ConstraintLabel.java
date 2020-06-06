package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.user.client.ui.Label;

public class ConstraintLabel extends ConstraintDisplay { 
    
    Label a = new Label("text");
    public ConstraintLabel(String type, String dsid, String varid, String variable, String value, String key, String keyValue, String op) {
        super(type, dsid, varid, variable, value, key, keyValue, op);
        init();
    }

    public ConstraintLabel(String con) {
        super(con);
        init();
    }

    private void init() {       
        setText();
        p.add(a);
        initWidget(p);
    }
    private void setText() {
        final String anchor_text;
        String opv = displayOp();
          
        if ( variable.equals(key) ) {
            anchor_text = variable+" "+opv+" "+value;
        } else {
            anchor_text = variable+" "+opv+" "+value + " with " + key + " "+opv+" " + keyValue;
        }
       
        a.setText(anchor_text);
      
    }

    public void setValue(String value) {
        this.value = value;
        setText();
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
        setText();
    }


    public String getOp() {
        return op;
    }

}
