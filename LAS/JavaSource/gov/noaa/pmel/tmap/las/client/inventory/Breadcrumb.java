package gov.noaa.pmel.tmap.las.client.inventory;

import org.gwtbootstrap3.client.ui.AnchorListItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

import gov.noaa.pmel.tmap.las.client.ClientFactory;

public class Breadcrumb extends AnchorListItem {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();

    String lasid;
    
    public Breadcrumb () {
        super();
    }
    
    public Breadcrumb(String name) {
    	super();
    	setText(name);
    	setTitle(name);
    }

	public Breadcrumb(String name, String catid) {
		super();
		setText(name);
		setTitle(name);
		setLasid(catid);
	}

	public String getLasid() {
		return lasid;
	}

	public void setLasid(String lasid) {
		this.lasid = lasid;
	}

}
