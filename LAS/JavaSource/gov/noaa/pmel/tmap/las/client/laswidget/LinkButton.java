package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.HtmlSanitizerUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LinkButton extends Composite {
    /*
     * 
.break-word {
  word-wrap: break-word;
}
     */
    PushButton linkButton = new PushButton("Link...");
    DialogBox linkDialog = new DialogBox(false);
    HTML html = new HTML("link");
    VerticalPanel inner = new VerticalPanel();
    PushButton close = new PushButton("Close");
    public LinkButton () {
        linkDialog.setText("Use your mouse to copy the URL.");
        html.setWidth("450px");
        inner.add(html);
        close.addClickHandler(new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                
                linkDialog.hide();
                
            }
        });
        close.setWidth("30px");
        close.addStyleDependentName("SMALLER");
        inner.add(close);
        linkDialog.add(inner);
        linkButton.addStyleDependentName("SMALLER");
        linkButton.setTitle("Get a link to this page.");
        linkButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                
                html.setHTML("<p style=\"word-wrap: break-word;\">"+UriUtils.sanitizeUri(Window.Location.getHref())+"</p>");
                linkDialog.setPopupPosition(linkButton.getAbsoluteLeft(), linkButton.getAbsoluteTop());
                linkDialog.show();
                
            }
            
        });
        initWidget(linkButton);
    }
}
