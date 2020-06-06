package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * {@link GwtEvent} for updating the visibility state of controls that have a
 * class that implements (or implement themselves) the
 * {@link ControlVisibilityEvent.Handler} interface.
 * 
 * @author weusijana
 * 
 */
public class ControlVisibilityEvent extends
        GwtEvent<ControlVisibilityEvent.Handler> {

    public interface Handler extends EventHandler {
        void onVisibilityUpdate(ControlVisibilityEvent event);
    }

    public static final Type<ControlVisibilityEvent.Handler> TYPE = new Type<ControlVisibilityEvent.Handler>();

    private final Boolean visible;

    public ControlVisibilityEvent(Boolean newVisibilityState) {
        this.visible = newVisibilityState;
    }

    @Override
    public final Type<ControlVisibilityEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    public Boolean isVisible() {
        return visible;
    }

    @Override
    protected void dispatch(ControlVisibilityEvent.Handler handler) {
        handler.onVisibilityUpdate(this);
    }
}
