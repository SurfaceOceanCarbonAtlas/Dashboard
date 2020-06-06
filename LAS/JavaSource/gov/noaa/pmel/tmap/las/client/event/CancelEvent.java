package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that indicates the product request pending for the identified panel should be canceled.
 * @author rhs
 *
 */
public class CancelEvent extends GwtEvent<CancelEvent.Handler> {
    
    private String ID;
    public static final Type<CancelEvent.Handler> TYPE = new Type<CancelEvent.Handler>();

    /**
     * Construct a new event for {@link OutputPanel} with this id.
     * @param id
     *           the ID of the {@link OutputPanel} for which the operation should be canceled.
     */
    public CancelEvent(String id) {
        this.ID = id;
    }
    /**
     * 
     * @return ID
     *            The ID of the 
     *            {@link OutputPanel} for which the request should be canceled.
     */
    public String getID() {
        return ID;
    }
    /**
     * @param ID
     *            Set the ID the
     *            {@link OutputPanel} for which the ID should be canceled.
     */
    public void setID(String id) {
        ID = id;
    }

    public interface Handler extends EventHandler {
        void onCancel(CancelEvent event);
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onCancel(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

}
