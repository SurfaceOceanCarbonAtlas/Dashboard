package gov.noaa.pmel.tmap.las.client.serializable;


import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OptionSerializable extends Serializable implements IsSerializable {
    String help;
    Map<String, String> menu;
    String title;
    String type;
    String textField;
	/**
	 * @return the textField
	 */
	public String getTextField() {
		return textField;
	}
	/**
	 * @param textField the textField to set
	 */
	public void setTextField(String textField) {
		this.textField = textField;
	}
	/**
	 * @return the help
	 */
	public String getHelp() {
		return help;
	}
	/**
	 * @return the menu
	 */
	public Map<String, String> getMenu() {
		return menu;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param help the help to set
	 */
	public void setHelp(String help) {
		this.help = help;
	}
	/**
	 * @param menu the menu to set
	 */
	public void setMenu(Map<String, String> menu) {
		this.menu = menu;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
