package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event should be fired by the refresh method and the output panel when the
 * request is made and when it is finished to turn off alert button.
 * N.B. Technically we might want to have an UpdateStarted and UpdateFinished instead.
 * 
 * @author rhs
 * 
 */
public class UpdateFinishedEvent extends GwtEvent<UpdateFinishedEvent.Handler> {

    public static final Type<UpdateFinishedEvent.Handler> TYPE = new Type<UpdateFinishedEvent.Handler>();

    @Override
    protected void dispatch(UpdateFinishedEvent.Handler handler) {
        handler.onUpdateFinished(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public interface Handler extends EventHandler {
        public void onUpdateFinished(UpdateFinishedEvent event);
    }
}
