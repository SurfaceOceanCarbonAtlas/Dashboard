package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for removing an entire constraint from the constraint panel.
 * @author rhs
 *
 */
public class RemoveVariableEvent extends GwtEvent<RemoveVariableEvent.Handler> {
    
    public static final Type<RemoveVariableEvent.Handler> TYPE = new Type<RemoveVariableEvent.Handler>();

    /**
     * Construct an new event with the name of the constraint.
     */
    public RemoveVariableEvent() {
        
    }
    
    public interface Handler extends EventHandler {
        void onRemove(RemoveVariableEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onRemove(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }
    

}
