/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development.
 */
package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author Roland Schweitzer
 *
 */
public class LASRSSFeed extends LASDocument {
    public LASRSSFeed() {
        super();        
        Element rss = new Element("rss");
        setRootElement(rss);
        Element channel = new Element("channel");
        Element title = new Element("title");
        title.setText("LAS Output");
        channel.setContent(title);
        rss.setContent(channel);
    }
    
    public void setChannelLink(String url) {
        Element channel = getRootElement().getChild("channel");
        Element link = new Element("link");
        link.setText(url);
        channel.addContent(link);
    }
    
    public void addItem(String titleText, String url, String pubDateText) {
        Element channel = getRootElement().getChild("channel");       
        List items = channel.getChildren("item");
        Element item = null;
        for (Iterator itemsIt = items.iterator(); itemsIt.hasNext();) {
            Element item_temp = (Element) itemsIt.next();
            // Found the item already in the channel.  Just update the title and pubDate.
            if (item_temp.getChild("link").getTextNormalize().equals(url)) {
                item = item_temp;
                item.getChild("title").setText(titleText);
                item.getChild("pubDate").setText(pubDateText);
            }
        }
        // Did not find the item in the channel.  Create it and add it.
        if ( item == null ) {    
            item  = new Element("item");
            Element link = new Element("link");
            link.setText(url);
            Element title = new Element("title");
            title.setText(titleText);
            Element pubDate = new Element("pubDate");
            pubDate.setText(pubDateText);
            item.addContent(link);
            item.addContent(title);
            item.addContent(pubDate);
            channel.addContent(item);
        }        
    }

    /**
     * @param title
     */
    public void setChannelTitle(String titleText) {
        Element channel = getRootElement().getChild("channel");
        Element title = channel.getChild("title");
        if ( title == null ) {
            title = new Element("title");
            channel.addContent(title);
        }
        title.setText(titleText);       
    }
}
