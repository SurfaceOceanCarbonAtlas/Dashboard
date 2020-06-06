package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.event.RemoveSelectionConstraintEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


public class TextConstraintAnchor extends ConstraintAnchor {
   
    public TextConstraintAnchor(String con) {
        super(con);
        init();
    }

    public TextConstraintAnchor(String type, String dsid, String varid, String variable, String value, String key, String keyValue, String op) {
        super(type, dsid, varid, variable, value, key, keyValue, op);
        init();
    }
    private void init() {
        
        setText();
        
        
        a.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                eventBus.fireEventFromSource(new RemoveSelectionConstraintEvent(TextConstraintAnchor.this.variable, TextConstraintAnchor.this.value, TextConstraintAnchor.this.key, TextConstraintAnchor.this.keyValue), TextConstraintAnchor.this);              
            }
            
        });
        p.add(a);
        initWidget(p);
    }
    @Override
    public boolean equals(Object object) {
        if ( object instanceof TextConstraintAnchor ) {
            TextConstraintAnchor a = (TextConstraintAnchor) object;
            return a.getKey().equals(key) && a.getKeyValue().equals(keyValue) && a.getOp().equals(op);
        } else {
            return false;
        }
    }
}
