package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.RemoveSelectionConstraintEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ConstraintSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
/**
 * A clickable display for a constraint which is shown of the form value op variable.
 * @author rhs
 *
 */
public class ConstraintAnchor extends ConstraintDisplay {
    
    Anchor a = new Anchor();
    public ConstraintAnchor(String type, String dsid, String varid, String variable, String value, String key, String keyValue, String op) {
        super(type, dsid, varid, variable, value, key, keyValue, op);
    }
    

    
    protected void setText() {
        final String anchor_text;
        String opv = displayOp();
       
        a.addStyleDependentName("CONSTRAINT");
        
        if ( variable.equals(key) ) {
            anchor_text = "(x) "+variable+" "+opv+" "+value;
        } else {
            anchor_text = "(x) "+variable+" "+opv+" "+value + " with " + key + " "+opv+" " + keyValue;
        }
       
        a.setText(anchor_text);
        
    }

    public ConstraintAnchor(String con) {
        /* 
         * This is a string that looks like type_cr_variable_cr_op_value_cr_key_cr_keyValue
         * These are stuffed into this form so they can go on the token map for the history.
         * This code unpacks the string and fixes up the thing.
         */
        super(con);
    }
    public void setValue(String value) {
        this.value = value;
        setText();
    }
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
        setText();
    }
}
