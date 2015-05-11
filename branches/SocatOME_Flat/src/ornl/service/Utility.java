/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

public class Utility {

	/**
	 * @param myNode
	 *            node with new data
	 * @param node
	 *            String name of the destination element as child of root
	 * @param maindoc
	 *            Reference to single Document for modification
	 * @return reference to the modified document
	 * 
	 *         This method uses some slight of hand: given a populated node take
	 *         it apart, adding each of its Constituent elements to the proper
	 *         embedded placeholder in the Document template file, before
	 *         returning a reference to the document in progress.
	 * 
	 *         example usage : putNode(Spat, maindoc, "Spatial")
	 */
	/*
	 * public Document putNode(Map Platforms, Document maindoc) { Set<String>
	 * key = (Set<String>) Platforms.keySet(); Iterator I = key.iterator();
	 * ApplicationContext factory = new ClassPathXmlApplicationContext(
	 * "applicationContext.xml"); Configuration cv = (Configuration)
	 * factory.getBean("propertiesBean"); String fs =
	 * System.getProperty("file.separator"); HashMap hmProps =
	 * cv.getProperties(); String userfile = "",filename="",fileURL="";
	 * while(I.hasNext()){ Element variableE = new Element("Data_Set_Link");
	 * filename = (String)Platforms.get(I.next()); fileURL = (String)
	 * hmProps.get("linkbasepath") + (String) hmProps.get("datafolder") + '/' +
	 * filename; variableE .addContent(new Element("URL") .addContent(fileURL));
	 * variableE.addContent(new Element("Label") .addContent(filename));
	 * variableE.addContent(new Element("Link_Note")
	 * .addContent("User Uploaded File"));
	 * maindoc.getRootElement().addContent(variableE); }
	 * 
	 * 
	 * return maindoc; }
	 */
	public Document putNode(ArrayList<String> Platforms, Document document,
			String fieldName) {

		if (fieldName == "field_port_of_call") {
			// x_tags/ Cruise_Info/ Experiment/ Cruise
			Element temp = document.getRootElement().getChild("Cruise_Info")
					.getChild("Experiment").getChild("Cruise");

			for (int i = 0; i < Platforms.size(); i++) {
				Element E = new Element("Ports_of_Call");
				E.addContent(Platforms.get(i));
				temp.addContent(E);
			}

		}

		return document;
	}

	public Document putConflictNode(HashMap<String, String> conflicts,
			Document document) {
		Set<String> keys = conflicts.keySet();
		for(String key:keys)
		{
			String value = conflicts.get(key);
			String[] values = value.split("#");
			
			{
			
				String[] children = key.split("/");
				Element temp = document.getRootElement();
				Element e = new Element(children[0].trim());
				temp.addContent(e);
				for(int i=1;i<children.length;i++)
				{
					
					if(i == children.length-1)
					{
						for( String val:values)
						{
							if(!val.contains("@@CONFLICT@@"))
							{
								Element eNew = new Element(children[i].trim());
								e.addContent(eNew.addContent(val));	
							}
						}
						
					}
					else
					{
						Element eNew = new Element(children[i].trim());
						e.addContent(eNew);	
						e = eNew;
					}
									
					
				}
				
			}
			
		}
		return document;
	}
}
