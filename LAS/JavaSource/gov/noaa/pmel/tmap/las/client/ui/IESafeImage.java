package gov.noaa.pmel.tmap.las.client.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * An Image class uses a temporary image to report width and height (when such
 * dimensions would be <= 0) so that it will work under Internet Explorer. The
 * image should also be attached. This is because IE loads image elements
 * asynchronously as soon as the src attribute is set. See:
 * http://code.google.com/p/google-web-toolkit/issues/detail?id=6848
 * 
 * @author weusijana
 * 
 */
public class IESafeImage extends Image {
    private HandlerRegistration initLoadHandlerReg = null;

    private static final Logger logger = Logger.getLogger(IESafeImage.class.getName());

    /**
	 * 
	 */
    public IESafeImage() {
        super();
        // hide();
        logger.setLevel(Level.OFF);
    }

    /**
     * @param element
     */
    public IESafeImage(Element element) {
        super(element);
        logger.setLevel(Level.OFF);
    }

    /**
     * @param resource
     */
    public IESafeImage(ImageResource resource) {
        super(resource);
        logger.setLevel(Level.OFF);
    }

    public IESafeImage(LoadHandler loadHandler) {
        this();
        setInitLoadHandlerReg(addLoadHandler(loadHandler));
    }

    /**
     * @param url
     */
    public IESafeImage(SafeUri url) {
        super(url);
        logger.setLevel(Level.OFF);
    }

    /**
     * @param url
     * @param left
     * @param top
     * @param width
     * @param height
     */
    public IESafeImage(SafeUri url, int left, int top, int width, int height) {
        super(url, left, top, width, height);
        logger.setLevel(Level.OFF);
    }

    /**
     * @param url
     */
    public IESafeImage(String url) {
        super(url);
        logger.setLevel(Level.OFF);
    }

    /**
     * @param url
     * @param left
     * @param top
     * @param width
     * @param height
     */
    public IESafeImage(String url, int left, int top, int width, int height) {
        super(url, left, top, width, height);
        logger.setLevel(Level.OFF);
    }

    /**
     * Returns the original height when using IE.
     * 
     * @see com.google.gwt.user.client.ui.Image#getHeight()
     */
    @Override
    public int getHeight() {
        int superHeight = super.getHeight();
        logger.log(Level.INFO, "superHeight:" + superHeight);
        logger.log(Level.INFO, "isAttached():" + isAttached());
        if ( (superHeight <= 0) ) {
            // If this is being run under IE the default answer may be 0 when it
            // shouldn't be, so return the height from a hidden and attached
            // temp image
            Image temp = new Image(this.getUrl());
            temp.getElement().getStyle().setVisibility(Visibility.HIDDEN);
            RootPanel.get().add(temp);
            logger.log(Level.WARNING, "temp.isAttached():" + temp.isAttached());
            int tempHeight = temp.getHeight();
            logger.log(Level.WARNING, "tempHeight:" + tempHeight);
            temp.removeFromParent();
            return tempHeight;
        }
        return superHeight;
    }

    /**
     * @return the {@link HandlerRegistration} of the {@link LoadHandler} set at
     *         construction
     */
    public HandlerRegistration getInitLoadHandlerReg() {
        return initLoadHandlerReg;
    }

    /**
     * Returns the original width when when using IE.
     * 
     * @see com.google.gwt.user.client.ui.Image#getWidth()
     */
    @Override
    public int getWidth() {
        int superWidth = super.getWidth();
        logger.log(Level.INFO, "superWidth:" + superWidth);
        logger.log(Level.INFO, "isAttached():" + isAttached());
        if ( (superWidth <= 0) ) {
            // If this is being run under IE the default answer may be 0 when it
            // shouldn't be, so return the height from a hidden and attached
            // temp image
            Image temp = new Image(this.getUrl());
            temp.getElement().getStyle().setVisibility(Visibility.HIDDEN);
            RootPanel.get().add(temp);
            logger.log(Level.WARNING, "temp.isAttached():" + temp.isAttached());
            int tempWidth = temp.getWidth();
            logger.log(Level.WARNING, "tempWidth:" + tempWidth);
            temp.removeFromParent();
            return tempWidth;
        }
        return superWidth;
    }

    /**
     * 
     */
    public void hide() {
        getElement().getStyle().setVisibility(Visibility.HIDDEN);
    }

    // /**
    // * @see com.google.gwt.user.client.ui.Image#setUrl(java.lang.String)
    // */
    // @Override
    // public void setUrl(final String url) {
    // if ( isHiddenAndAttached() ) {
    // super.setUrl(url);
    // } else {
    // // Do this later to avoid problems in IE
    // hide();
    // this.addAttachHandler(new AttachEvent.Handler() {
    // @Override
    // public void onAttachOrDetach(AttachEvent event) {
    // setUrl(url);
    // }
    // });
    // }
    // }
    //
    // /**
    // * @see
    // com.google.gwt.user.client.ui.Image#setUrlAndVisibleRect(com.google.gwt
    // * .safehtml.shared.SafeUri, int, int, int, int)
    // */
    // @Override
    // public void setUrlAndVisibleRect(final SafeUri url,
    // final int left, final int top, final int width, final int height) {
    // if ( isHiddenAndAttached() ) {
    // super.setUrlAndVisibleRect(url, left, top, width, height);
    // } else {
    // // Do this later to avoid problems in IE
    // hide();
    // this.addAttachHandler(new AttachEvent.Handler() {
    // @Override
    // public void onAttachOrDetach(AttachEvent event) {
    // setUrlAndVisibleRect(url, left, top, width, height);
    // }
    // });
    // }
    // }
    //
    // /**
    // * @see
    // com.google.gwt.user.client.ui.Image#setUrlAndVisibleRect(java.lang.String
    // * , int, int, int, int)
    // */
    // @Override
    // public void setUrlAndVisibleRect(final String url,
    // final int left, final int top, final int width, final int height) {
    // if ( isHiddenAndAttached() ) {
    // super.setUrlAndVisibleRect(url, left, top, width, height);
    // } else {
    // // Do this later to avoid problems in IE
    // hide();
    // this.addAttachHandler(new AttachEvent.Handler() {
    // @Override
    // public void onAttachOrDetach(AttachEvent event) {
    // setUrlAndVisibleRect(url, left, top, width, height);
    // }
    // });
    // }
    // }

    /**
     * @return
     */
    public boolean isHiddenAndAttached() {
        return (Visibility.HIDDEN.getCssName().equalsIgnoreCase(getElement().getStyle().getVisibility()))
                && isAttached();
    }

    /**
     * @param initLoadHandlerReg
     *            the initLoadHandlerReg to set
     */
    private void setInitLoadHandlerReg(HandlerRegistration initLoadHandlerReg) {
        this.initLoadHandlerReg = initLoadHandlerReg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.Image#setResource(com.google.gwt.resources
     * .client.ImageResource)
     */
    @Override
    public void setResource(final ImageResource resource) {
        if ( isHiddenAndAttached() ) {
            super.setResource(resource);
        } else {
            // Do this later to avoid problems in IE
            hide();
            this.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    setResource(resource);
                }
            });
        }
    }

    /**
     * @see com.google.gwt.user.client.ui.Image#setUrl(com.google.gwt.safehtml.shared
     *      .SafeUri)
     */
    @Override
    public void setUrl(final SafeUri url) {
        if ( isHiddenAndAttached() ) {
            super.setUrl(url);
        } else {
            // Do this later to avoid problems in IE
            hide();
            this.addAttachHandler(new AttachEvent.Handler() {
                @Override
                public void onAttachOrDetach(AttachEvent event) {
                    setUrl(url);
                }
            });
        }
    }

}
