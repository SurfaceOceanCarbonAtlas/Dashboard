package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event should be fired by any widget whose change affects the contents of
 * the plots so that either the button to update can change state to alert the
 * user or the auto update can fire. If the auto boolean is set, the element 
 * firing the event request that the plot refresh happen now automatically.
 * 
 * @author rhs
 * 
 */
public class WidgetSelectionChangeEvent extends GwtEvent<WidgetSelectionChangeEvent.Handler> {

    public static final Type<WidgetSelectionChangeEvent.Handler> TYPE = new Type<WidgetSelectionChangeEvent.Handler>();
    private boolean auto;
    private boolean force;
    private boolean pushHistory;

    public WidgetSelectionChangeEvent(boolean auto, boolean force, boolean pushHistory) {
        super();
        this.auto = auto;
        this.force = force;
        this.pushHistory = pushHistory;
    }
    public WidgetSelectionChangeEvent(boolean auto, boolean force) {
        super();
        this.auto = auto;
        this.force = force;
        this.pushHistory = true;
    }
    public WidgetSelectionChangeEvent(boolean auto) {
        super();
        this.auto = auto;
        this.force = false;
        this.pushHistory = true;
    }
    @Override
    protected void dispatch(WidgetSelectionChangeEvent.Handler handler) {
        handler.onAxisSelectionChange(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public interface Handler extends EventHandler {
        public void onAxisSelectionChange(WidgetSelectionChangeEvent event);
    }

    public boolean isAuto() {
        return auto;
    }
    public boolean isForce() {
        return force;
    }
    public boolean isPushHistory() {
        return pushHistory;
    }
}
