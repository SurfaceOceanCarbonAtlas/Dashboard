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
 * Shows a constraint of the form value op variable (@see ConstraintAnchor) expect that this one is not clickable.
 * @author rhs
 *
 */
public class ConstraintDisplay extends Composite {
    String variable;
    String value;
    String key;
    String keyValue;
    
    String type;
    String dsid;
    String varid;
    
    String op;
    
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    
    FlowPanel p = new FlowPanel();
    public ConstraintDisplay(String type, String dsid, String varid, String variable, String value, String key, String keyValue, String op) {
        this.type = type;
        this.dsid = dsid;
        this.varid = varid;
        this.variable = variable;
        this.value = value;
        this.key = key;
        this.keyValue = keyValue;
        this.op = op;
    }
    

    public ConstraintDisplay(String con) {
        
        /* 
         * This is a string that looks like type_cr_variable_cr_op_value_cr_key_cr_keyValue
         * These are stuffed into this form so they can go on the token map for the history.
         * This code unpacks the string and fixes up the thing.
         */
        String[] parts = con.split("_cr_");
        this.type = parts[0];
        this.dsid = parts[1];
        this.varid = parts[2];
        this.variable = parts[3];
        this.value = parts[4]; 
        this.key = parts[5];
        this.keyValue = parts[6];
        this.op = parts[7];
        
    }

    public String getVariable() {
        return variable;
    }


    public void setVariable(String variable) {
        this.variable = variable;
    }


    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getKeyValue() {
        return keyValue;
    }

    public String getOp() {
        return op;
    }


    public void setOp(String op) {
        this.op = op;
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
    public String displayOp() {
        String opv = "";
        if ( op.equals("eq") ) {
            opv = "=";
        } else if ( op.equals("lt") ) {
            opv = "<";
        } else if ( op.equals("le") ) {
            opv = "<=";
        } else if ( op.equals("gt") ) {
            opv = ">";
        } else if ( op.equals("ge") ) {
            opv = ">=";
        } else if ( op.equals("ne") ) {
            opv = "!=";
        } else if ( op.equals("like") ) {
            opv = "=";
        } else if ( op.equals("is") ) {
            opv = "=";
        }
        return opv;
    }
}
