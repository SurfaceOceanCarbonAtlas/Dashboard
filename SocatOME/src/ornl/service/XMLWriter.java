/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.service;

import ornl.beans.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XMLWriter {

	private org.jdom.Element titleNode;
	ApplicationContext factory = new ClassPathXmlApplicationContext(
			"applicationContext.xml");
	// ApplicationContext factory2 = new
	// ClassPathXmlApplicationContext("fgdc_template.xml");

	Configuration conf = (Configuration) factory.getBean("propertiesBean");
	HashMap hmConf = conf.getProperties();
	String XMLStyleMap = null;
	String XMLStyleMapDis = null;
	String style = null;
	String outfilePath = null;

	public XMLWriter() {
		style = (String) hmConf.get("xmlStyle");
		outfilePath = (String) hmConf.get("XMLOutPath");

		XMLStyleMap = (String) hmConf.get("XMLStyleMap");		
	}

	public void outputDocToXML(Document myDocument, String username,
			String filename) {

		try {
			Source xmlSource = new JDOMSource(myDocument);
			String xsltFilename = (String) hmConf.get("stylesheet");
			Source xsltSource = new StreamSource(xsltFilename);
			JDOMResult domRes = new JDOMResult();
			// the factory pattern supports different XSLT processors
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, domRes);
			Document doc = domRes.getDocument();

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			BufferedWriter bwout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outfilePath + username + "/"
							+ filename)));
			outputter.output(doc, bwout);
			bwout.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public String outputDocToXMLString(Document myDocument) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		try {
			Source xmlSource = new JDOMSource(myDocument);
			String xsltFilename = (String) hmConf.get("stylesheet");
			Source xsltSource = new StreamSource(xsltFilename);
			JDOMResult domRes = new JDOMResult();
			// the factory pattern supports different XSLT processors
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, domRes);
			Document doc = domRes.getDocument();

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
							
			sb.append(outputter.outputString(doc));
			// bwout.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return sb.toString();
	}

	public Document readDocument(String docname) {
		try {
			SAXBuilder builder = new SAXBuilder();
			Document anotherDocument = builder.build(new File(docname));
			return anotherDocument;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public Document buildDoc(HashMap<String, String> fieldMap, String outDir,
			int ctr, ArrayList<String> PortsMap,
			ArrayList<String> Vars, LinkedHashMap xpath_map,
			LinkedHashMap multi_map) throws IOException {
		//Utility util = new Utility();
		HashMap UserFiles = new HashMap();
		HashMap <String,String> conflicts = new HashMap<String,String>();
		Document template_doc;
		template_doc = readDocument(XMLStyleMap);
		
		// System.out.println(xpath_map);
		Set<String> dbList = (Set<String>) fieldMap.keySet();

		try {
			// iterate list of field names from database query

			for (String dbField : dbList) {

				try {
					// the query response
					if (xpath_map.get(dbField) != null) {
						titleNode = (org.jdom.Element) XPath.selectSingleNode(
								template_doc, (String) xpath_map.get(dbField));
						String temp = fieldMap.get(dbField); // get the new
						if (titleNode != null) {
							if(temp.contains("@@CONFLICT@@")) 
							{
								String key = getConflictTag((String) xpath_map.get(dbField));
								conflicts.put(key, temp);
								titleNode.setText("@@CONFLICT@@");
								
							}
							else
							titleNode.setText(temp);
							
						}// set new value into DOM

					} else if (multi_map.get(dbField) != null) {
						titleNode = (org.jdom.Element) XPath.selectSingleNode(
								template_doc, (String) multi_map.get(dbField));
						String temp = fieldMap.get(dbField); // get the new
													// value

						if (titleNode != null) {
							if(temp.contains("@@CONFLICT@@")) 
							{
								String key = getConflictTag((String) multi_map.get(dbField));
								conflicts.put(key, temp);
								titleNode.setText("@@CONFLICT@@");
								
							}
							else
							titleNode.setText(temp);
							
						}// set new value into DOM
					}
					
				} catch (JDOMException e) {
					e.printStackTrace();

				}
			}
			Utility U = new Utility();
			if(PortsMap!=null &&PortsMap.size()>0)
				U.putNode(PortsMap, template_doc,"field_port_of_call");
			if(conflicts!=null &&conflicts.size()>0)
				U.putConflictNode(conflicts, template_doc);
			 //util.putNode(UserFiles, template_doc);
			// "MeasuredParameters");

		} catch (Exception e) {
			e.printStackTrace();
		}

		// getSpatial(geom, template_doc);

		return template_doc;
	}
	private String getConflictTag(String tag) {
//		/System.out.println(tag);
		String conflictTag = tag.replace("//x_tags",
				"CONFLICTS/ Conflict")+" /VALUE";
		conflictTag = conflictTag.replaceAll("\\[[0-9]\\]", "");
		return conflictTag;
	}

}
