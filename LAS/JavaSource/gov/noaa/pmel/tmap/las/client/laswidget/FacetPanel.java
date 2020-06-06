package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.FacetChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.FacetMember;
import gov.noaa.pmel.tmap.las.client.serializable.FacetSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FacetPanel extends Composite {
    ScrollPanel mainPanel = new ScrollPanel();
    VerticalPanel panel = new VerticalPanel();
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    public FacetPanel(FacetSerializable facet) {
        List<FacetMember> members = facet.getMembers();
        for ( Iterator memberIt = members.iterator(); memberIt.hasNext(); ) {
            FacetMember member = (FacetMember) memberIt.next();
            String name = member.getName();
            int count = member.getCount();
            CheckBox check = new CheckBox(name+" ("+count+")");
            check.setFormValue(facet.getName());
            check.setName(name);
            check.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent click) {
                    CheckBox source = (CheckBox) click.getSource();
                    eventBus.fireEventFromSource(new FacetChangeEvent(), source);
                }
                
            });
            panel.add(check);
        }
        mainPanel.add(panel);
        initWidget(mainPanel);
    }

}
