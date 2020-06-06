package gov.noaa.pmel.tmap.las.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class GetCategoriesEvent extends GwtEvent<GetCategoriesEvent.Handler> {
    private String catid;
    private String dsid;
    public static final Type<GetCategoriesEvent.Handler> TYPE = new Type<GetCategoriesEvent.Handler>();
    public interface Handler extends EventHandler {
        void onGetCategories(GetCategoriesEvent event);
    }
    public GetCategoriesEvent(String catid, String dsid) {
        this.catid = catid;
        this.dsid = dsid;
    }
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onGetCategories(this);
        
    }
    public String getDsid() {
        return dsid;
    }
    public void setDsid(String dsid) {
        this.dsid = dsid;
    }
    public String getCatid() {
        return catid;
    }
    public void setCatid(String catid) {
        this.catid = catid;
    }

}
