package gov.noaa.pmel.tmap.las.ui.state;


import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.util.ArrayList;

/**
 * @author Roland Schweitzer
 *
 */
public class OptionBean {
    
    /** help property - help text for this option. */
    private String help;
    
    /** title property - the human title for this option. */
    private String title;
    
    /** type property - either textarea or menu. */
    private String type;
    
    /** name property - name given to the HTML widget. */
    private String widget_name;
    
    /** value property - value of the current selection (value of item in items or value in the text box. */
    private String value;
    
    /** items property - not null only when menu, list of menu items. */
    
    private StateNameValueList items;

    /**
     * @return Returns the help.
     */
    public String getHelp() {
        return help;
    }

    /**
     * @param help The help to set.
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * @return Returns the items.
     */
    public StateNameValueList getItems() {
        return items;
    }

    /**
     * @param items The items to set.
     */
    public void setItems(StateNameValueList items) {
        this.items = items;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the value.
     * If it's a text field, the value is the value of the OptionBean object.
     * If it's a menu list option, then the value is the current selection in the list.
     */
    public String getValue() {
        if ( value == null && items != null && items.size() > 0 ) {
            return items.getCurrent();
        } else {
           return value;
        }
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return Returns the widget_name.
     */
    public String getWidget_name() {
        return widget_name;
    }

    /**
     * @param widget_name The widget_name to set.
     */
    public void setWidget_name(String widget_name) {
        this.widget_name = widget_name;
    }

}
