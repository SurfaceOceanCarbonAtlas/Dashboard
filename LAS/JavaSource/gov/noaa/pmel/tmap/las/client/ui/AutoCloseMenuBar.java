/**
 * 
 */
package gov.noaa.pmel.tmap.las.client.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * Extends the standard menu bar widget. Adds the behavior of closing an open menu when the user resizes the Web browser window.
 * @author weusijana
 *
 */
public class AutoCloseMenuBar extends com.google.gwt.user.client.ui.MenuBar {

    private final class CloseChildrenResizeHandler implements ResizeHandler {
		@Override
		public void onResize(ResizeEvent event) {
		    logger.info("onResize(ResizeEvent event) started with event:"+event.toDebugString());
		    closeAllChildren(false);
		}
	}

	private final Logger logger = Logger.getLogger(AutoCloseMenuBar.class.getName());

    /**
	 * 
	 */
	public AutoCloseMenuBar() {
		super();
        closeChildrenOnResize();
	}

	/**
	 * @param vertical
	 */
	public AutoCloseMenuBar(boolean vertical) {
		super(vertical);
        closeChildrenOnResize();
	}

	/**
	 * @param vertical
	 * @param resources
	 */
	public AutoCloseMenuBar(boolean vertical, Resources resources) {
		super(vertical, resources);
        closeChildrenOnResize();
	}

	/**
	 * @param resources
	 */
	public AutoCloseMenuBar(Resources resources) {
		super(resources);
        closeChildrenOnResize();
	}

	/**
	 * 
	 */
	private void closeChildrenOnResize() {
		logger.setLevel(Level.OFF);
		Window.addResizeHandler(new CloseChildrenResizeHandler());
	}

}
