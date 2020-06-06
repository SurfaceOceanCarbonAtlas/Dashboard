package gov.noaa.pmel.tmap.las.client.event;

import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for informing objects, that have a member that implements (or implement
 * themselves) the {@link GridChangeEvent.Handler} interface, that the Grid
 * has changed.  The new {@link GridSerializable} is included.
 * 
 * @author rhs
 * 
 */
//TODO include the old and new operation for a more general description of the change?
public class GridChangeEvent extends GwtEvent<GridChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onGridChange(GridChangeEvent event);
    }

    public static final Type<GridChangeEvent.Handler> TYPE = new Type<GridChangeEvent.Handler>();
    
    private GridSerializable grid;
    
    public GridChangeEvent(GridSerializable grid) {
        this.grid = grid;
    }
   
    
  
    public GridSerializable getGrid() {
        return grid;
    }



    public void setGrid(GridSerializable grid) {
        this.grid = grid;
    }



    protected void dispatch(GridChangeEvent.Handler handler) {
        handler.onGridChange(this);
    }

    @Override
    public final Type<GridChangeEvent.Handler> getAssociatedType() {
        return TYPE;
    }
}
