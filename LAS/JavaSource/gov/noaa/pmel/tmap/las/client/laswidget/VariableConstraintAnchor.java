package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.event.VariableConstraintEvent;
import gov.noaa.pmel.tmap.las.client.event.RemoveVariableEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


public class VariableConstraintAnchor extends ConstraintAnchor {
   
    public VariableConstraintAnchor(String con) {
        super(con);
        init();
    }


    public VariableConstraintAnchor(String type, String dsid, String varid, String variable, String value, String key, String keyValue, String op) {
        super(type, dsid, varid, variable, value, key, keyValue, op);
        init();
    }
    
    private void init() {

        setText();


        a.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String lhs = null;
                String rhs = null;
                if ( op.equals("le") ) {
                    lhs = "";
                } else if ( op.equals("gt") ) {
                    rhs = "";
                } else if ( op.equals("ne") ) {
                    lhs = "";
                    rhs = "";
                }
                eventBus.fireEventFromSource(new VariableConstraintEvent(dsid, varid, lhs, op, variable, rhs, op, true), VariableConstraintAnchor.this);
            }
        });
                p.add(a);
                initWidget(p);
    }
    @Override
    public boolean equals(Object object) {
        if ( object instanceof VariableConstraintAnchor ) {
            VariableConstraintAnchor a = (VariableConstraintAnchor) object;
            return a.getKey().equals(key) && a.getOp().equals(op);
        } else {
            return false;
        }
    }
}
