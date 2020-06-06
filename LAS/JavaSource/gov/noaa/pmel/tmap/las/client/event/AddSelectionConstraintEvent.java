package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for adding a value for the list of active constraints for a SelectionConstraint.  If the is not display panel, receivers should build the panel in which the values is to be added.
 * @author rhs
 *
 */
public class AddSelectionConstraintEvent extends GwtEvent<AddSelectionConstraintEvent.Handler> {
    private String variable;
    private String value;
    private String key;
    private String keyValue;
    private String op;
    
    public static final Type<AddSelectionConstraintEvent.Handler> TYPE = new Type<AddSelectionConstraintEvent.Handler>();

    /**
     * Construct an new event.
     * @param variable the variable associated with this constraint
     * @param value the value of the variable to which this variable will be constrained.
     * @param key the underlying unique key value upon which the search can be made
     * @param keyValue the value of the key that will constrain the search to the correct value of the variable.
     */
    public AddSelectionConstraintEvent(String variable, String value, String key, String keyValue, String op) {
        this.variable = variable;
        this.value = value;
        this.key = key;
        this.keyValue = keyValue;
        this.op = op;
    }
    
    public interface Handler extends EventHandler {
        void onAdd(AddSelectionConstraintEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onAdd(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
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

    public void setValue(String value) {
        this.value = value;
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

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
    public void setOp(String op) {
        this.op = op;
    }
    public String getOp() {
        return op;
    }
   
}
