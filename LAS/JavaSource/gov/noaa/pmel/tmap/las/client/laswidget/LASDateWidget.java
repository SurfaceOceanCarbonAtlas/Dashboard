/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.laswidget;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A wrapper around the LAS JavaScript DatePicker which does evil things 
 * to the DOM and doesn't play nice with GWT.
 * @author rhs
 *
 */
public class LASDateWidget extends JavaScriptObject {
	
	public final native LASDateWidget init(String lo, String hi, int deltaMinutes, int offsetMinutes, int add_sub_1, int  add_sub_2) /*-{
	    return new $wnd.DateWidget(lo, hi, deltaMinutes, offsetMinutes, add_sub_1, add_sub_2);
	}-*/;
	public final native void alert() /*-{ this.alert(); }-*/;
	public final native void render(String id, String menu_set_1, String menu_set_2) /*-{ this.render(id, menu_set_1, menu_set_2); }-*/;
	public final native void render(String id, String menu_set_1) /*-{ this.render(id, menu_set_1); }-*/;
	public final native void renderToNode(JavaScriptObject node, String menu_set_1, String menu_set_2) /*-{this.renderToNode(node, menu_set_1, menu_set_2); }-*/;
	public final native void renderToNode(JavaScriptObject node, String menu_set_1) /*-{this.renderToNode(node, menu_set_1); }-*/;
	public final native String getDateHi() /*-{ return this.getDateHi(); }-*/;
	public final native String getDateLo() /*-{ return this.getDateLo(); }-*/;
	public final native String getDate1_Ferret() /*-{ return this.getDate1_Ferret(); }-*/;
	public final native String getDate2_Ferret() /*-{ return this.getDate2_Ferret(); }-*/;
	public final native void setDate1(String date, int add_sub) /*-{ this.setDate1(date, add_sub); }-*/;
	public final native void setDate2(String date, int add_sub) /*-{ this.setDate2(date, add_sub); }-*/;
	public final native void enable(String hilo) /*-{ this.enable(hilo); }-*/;
	public final native void enableNode(String hilo) /*-{ this.enableNode(hilo); }-*/;
	public final native void disable(String hilo) /*-{ this.disable(hilo); }-*/;
	public final native void disableNode(String hilo) /*-{ this.disableNode(hilo); }-*/;
	public final native void hide() /*-{ this.hide(); }-*/;
	public final native void show() /*-{ this.show(); }-*/;
	protected LASDateWidget() {
	}
}
