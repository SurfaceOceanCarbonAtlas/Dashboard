/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * The model class passed to all View objects (JSPs).
 * 
 */
public class TransactionDetail {

	public String htmltext = "";

	private String contents = "";

	public String filename = null;
	public String filepath = null;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getHtmltext() {
		return htmltext;
	}

	public void setHtmltext(String htmltext) {
		this.htmltext = htmltext;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

}
