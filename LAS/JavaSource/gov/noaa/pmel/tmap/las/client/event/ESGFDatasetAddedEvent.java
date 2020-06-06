package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event should be fired when a reference map changes so other objects
 * that are managing maps can update themselves for the axes they are
 * responsible for keeping up-to-date.
 * 
 * @author rhs
 * 
 */
public class ESGFDatasetAddedEvent extends GwtEvent<ESGFDatasetAddedEvent.Handler> {

    public static final Type<ESGFDatasetAddedEvent.Handler> TYPE = new Type<ESGFDatasetAddedEvent.Handler>();
    
    
    public ESGFDatasetAddedEvent() {
        super();
        
    }
    @Override
    protected void dispatch(ESGFDatasetAddedEvent.Handler handler) {
        handler.onESGFDatasetAdded(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public interface Handler extends EventHandler {
        public void onESGFDatasetAdded(ESGFDatasetAddedEvent event);
    }

}
