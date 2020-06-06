package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for informing objects, that have a member that implements (or implement
 * themselves) the {@link VariableSelectionChangeEvent.Handler} interface, that a
 * widget is has changed the currently selected {@link VariableSerializable}.
 * 
 * @author rhs
 * 
 */
public class VariableSelectionChangeEvent extends GwtEvent<VariableSelectionChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onVariableChange(VariableSelectionChangeEvent event);
    }

    public static final Type<VariableSelectionChangeEvent.Handler> TYPE = new Type<VariableSelectionChangeEvent.Handler>();

    @Override
    protected void dispatch(VariableSelectionChangeEvent.Handler handler) {
        handler.onVariableChange(this);
    }

    @Override
    public final Type<VariableSelectionChangeEvent.Handler> getAssociatedType() {
        return TYPE;
    }
}
