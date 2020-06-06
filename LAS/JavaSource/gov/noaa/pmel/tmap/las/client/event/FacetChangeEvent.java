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
public class FacetChangeEvent extends GwtEvent<FacetChangeEvent.Handler> {

    public static final Type<FacetChangeEvent.Handler> TYPE = new Type<FacetChangeEvent.Handler>();
    
    
    public FacetChangeEvent() {
        super();
        
    }
    @Override
    protected void dispatch(FacetChangeEvent.Handler handler) {
        handler.onFacetChanged(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    public interface Handler extends EventHandler {
        public void onFacetChanged(FacetChangeEvent event);
    }

}
