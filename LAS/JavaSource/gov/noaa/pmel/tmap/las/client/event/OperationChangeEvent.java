package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for informing objects, that have a member that implements (or implement
 * themselves) the {@link OperationChangeEvent.Handler} interface, that a
 * operation has changed.  The min and max number of {@link VariableSerializable}
 * required by the new operation is included for convenience.
 * 
 * @author rhs
 * 
 */
//TODO include the old and new operation for a more general description of the change?
public class OperationChangeEvent extends GwtEvent<OperationChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onOperationChange(OperationChangeEvent event);
    }

    public static final Type<OperationChangeEvent.Handler> TYPE = new Type<OperationChangeEvent.Handler>();
    
    private int minVars;
    private int maxVars;
    
    public OperationChangeEvent(int min, int max) {
        this.minVars = min;
        this.maxVars = max;
    }
    /**
     * @return the minimum number of
     *         {@link VariableSerializable}s required for the new operation.
     */
    public int getMinVars() {
        return minVars;
    }
    /**
     * @param min
     *            Set the minimum number of 
     *            {@link VariableSerializable}s required for the new operation.
     */
    public void setMinVars(int minVars) {
        this.minVars = minVars;
    }

    /**
     * @return the maximum number of
     *         {@link VariableSerializable}s allowed for the new operation.
     */
    public int getMaxVars() {
        return maxVars;
    }
    /**
     * @param min
     *            Set the maximum number of 
     *            {@link VariableSerializable}s allowed for the new operation.
     */
    public void setMaxVars(int maxVars) {
        this.maxVars = maxVars;
    }

    @Override
    protected void dispatch(OperationChangeEvent.Handler handler) {
        handler.onOperationChange(this);
    }

    @Override
    public final Type<OperationChangeEvent.Handler> getAssociatedType() {
        return TYPE;
    }
}
