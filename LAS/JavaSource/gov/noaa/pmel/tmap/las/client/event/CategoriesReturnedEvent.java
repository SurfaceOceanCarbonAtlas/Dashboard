package gov.noaa.pmel.tmap.las.client.event;

import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.ConfigSerializable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CategoriesReturnedEvent extends GwtEvent<CategoriesReturnedEvent.Handler> {
    private CategorySerializable[] categoriesSerializable;
    public static final Type<CategoriesReturnedEvent.Handler> TYPE = new Type<CategoriesReturnedEvent.Handler>();
    public interface Handler extends EventHandler {
        void onCategoriesReturned(CategoriesReturnedEvent event);
    }
    public CategoriesReturnedEvent(CategorySerializable[] categoriesSerializable) {
        this.categoriesSerializable = categoriesSerializable;
    }
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onCategoriesReturned(this);   
    }
    public CategorySerializable[] getCategoriesSerializable() {
        return categoriesSerializable;
    }
    public void setCategoriesSerializable(CategorySerializable[] categoriesSerializable) {
        this.categoriesSerializable = categoriesSerializable;
    }
    
}
