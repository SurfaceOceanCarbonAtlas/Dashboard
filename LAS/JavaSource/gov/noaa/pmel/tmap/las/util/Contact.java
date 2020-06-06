package gov.noaa.pmel.tmap.las.util;

import org.jdom.Element;

public class Contact {
   Element element;
   public Contact (Element element) {
	   this.element = element;
   }
   public String getEmail() {
	   return element.getAttributeValue("email");
   }
   public String getRole() {
	   return element.getAttributeValue("role");
   }
   public String getName() {
	   return element.getAttributeValue("name");
   }
   public String getURL() {
	   return element.getAttributeValue("url");
   }
} 
