package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that indicates the product request pending for the identified panel should be canceled.
 * @author rhs
 *
 */
public class ResizeEvent extends GwtEvent<ResizeEvent.Handler> {
    
    public static final Type<ResizeEvent.Handler> TYPE = new Type<ResizeEvent.Handler>();

    /**
     * Construct a new event for asking for the image to resize
     */
    public ResizeEvent() {
        super();
    }

    public interface Handler extends EventHandler {
        void onResize(ResizeEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onResize(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

}
