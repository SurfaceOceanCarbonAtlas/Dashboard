package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for informing objects, that have a member that implements (or implement
 * themselves) the {@link ComparisonModeChangeEvent.Handler} interface, that the
 * application has changed to or from comparison mode.
 * 
 * @author weusijana
 * 
 */
public class ComparisonModeChangeEvent extends GwtEvent<ComparisonModeChangeEvent.Handler> {

    public interface Handler extends EventHandler {
        void onComparisonModeChange(ComparisonModeChangeEvent event);
    }

    public static final Type<ComparisonModeChangeEvent.Handler> TYPE = new Type<ComparisonModeChangeEvent.Handler>();

    private boolean comparing;

    public boolean isComparing() {
        return comparing;
    }

    public void setComparing(boolean comparing) {
        this.comparing = comparing;
    }

    /**
     * @param comparing
     *            Pass in true if the application is in comparison mode, false otherwise.
     */
    public ComparisonModeChangeEvent(boolean comparing) {
        this.setComparing(comparing);
    }

    @Override
    protected void dispatch(ComparisonModeChangeEvent.Handler handler) {
        handler.onComparisonModeChange(this);
    }

    @Override
    public final Type<ComparisonModeChangeEvent.Handler> getAssociatedType() {
        return TYPE;
    }
}
