package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for removing an entire constraint from the constraint panel.
 * @author rhs
 *
 */
public class RemoveConstraintEvent extends GwtEvent<RemoveConstraintEvent.Handler> {
    private String name;
    public static final Type<RemoveConstraintEvent.Handler> TYPE = new Type<RemoveConstraintEvent.Handler>();

    /**
     * Construct an new event with the name of the constraint.
     */
    public RemoveConstraintEvent(String name) {
        this.name = name;
    }
    
    public interface Handler extends EventHandler {
        void onRemove(RemoveConstraintEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onRemove(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }
    
    public String getName() {
        return name;
    }

}
