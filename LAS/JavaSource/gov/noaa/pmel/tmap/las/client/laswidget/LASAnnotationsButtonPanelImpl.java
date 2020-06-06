package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.ButtonBase;

;

/**
 * Default implementation of {@link LASAnnotationsButtonPanel}.
 */
public class LASAnnotationsButtonPanelImpl extends Composite implements LASAnnotationsButtonPanel {
    private final LASAnnotationsPanel annotations = new LASAnnotationsPanel();

    private ToggleButton button;

    private Presenter listener;

    private String name;

    private LASAnnotationsButtonPanelImpl() {
        this(null);
    }

    public LASAnnotationsButtonPanelImpl(String outputPanelID) {
        setName(outputPanelID);
        String moduleBaseURL = GWT.getModuleBaseURL();
        String upImageURL = moduleBaseURL + "../images/i_off.png";
        Image upImage = new Image(upImageURL);
        String downImageURL = moduleBaseURL + "../images/i_on.png";
        Image downImage = new Image(downImageURL);
        button = new ToggleButton(
                upImage,
                downImage);
        button.setTitle("Plot Annotations");
        button.setStylePrimaryName("OL_MAP-ToggleButton");
        button.addStyleDependentName("WIDTH");

        button.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                annotations.setVisible(button.isDown());
            }
        });

        initWidget(button);
    }

    @Override
    public void addClickHandler(ClickHandler clickHandler) {
        button.addClickHandler(clickHandler);
    }

    @Override
    public void setAnnotations(String xml) {
        annotations.setAnnotations(xml);
    }

    @Override
    public void setAnnotationsHTML(String html) {
        annotations.setAnnotationsHTML(html);
    }

    @Override
    public void setAnnotationsHTMLURL(String url) {
        annotations.setAnnotationsHTMLURL(url);
    }

    @Override
    public void setButtonDown(boolean down) {
        if ( button.isVisible() )
            button.setValue(down, true);
    }

    @Override
    public void setError(String html) {
        annotations.setError(html);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setTitle(String name) {
        button.setTitle(name);
    }

    @Override
    public void setPopupWidth(String width) {
        if ( button.isVisible() )
            annotations.setWidth(width);
    }

    @Override
    public void setPresenter(Presenter listener) {
        this.listener = listener;
    }

    @Override
    public void hide() {
        if ( button.isVisible() )
            button.setValue(false, true);
    }

}
