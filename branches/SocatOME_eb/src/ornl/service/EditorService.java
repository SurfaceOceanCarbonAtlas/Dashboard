/**
 * Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
 * Contact: zzr@ornl.gov 
 */
package ornl.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;

import ornl.beans.Configuration;
import ornl.beans.DynamicMapBean;
import ornl.beans.FormElements;
import ornl.beans.MapBean;
import ornl.beans.Metadata_Editor;
import ornl.beans.MultiMapBean;
import ornl.client.AddNewAuthors;
import ornl.client.AddNewSurvey;
import ornl.client.AddNewVariables;
import ornl.client.AddNewVessel;
import ornl.client.SaveFile;
import ornl.controller.EmailController;

public class EditorService {

	private ApplicationContext ctx = null;
	private MapBean xmlBean = new MapBean();
	private MultiMapBean multiMapBean = new MultiMapBean();
	public LinkedHashMap<String, String> xpath_map = null;
	public LinkedHashMap<String, String> multi_map = null;
	private HashMap fieldMap = new HashMap();

	private Document xmlDoc = null;
	public String filename = null;
	public String filepath = null;

	ApplicationContext factory = new ClassPathXmlApplicationContext(
			"applicationContext.xml");

	// find logged in user
	User user = (User) SecurityContextHolder.getContext().getAuthentication()
			.getPrincipal();

	String loggedin_username = user.getUsername();

	Configuration cv = (Configuration) factory.getBean("propertiesBean");

	HashMap hmProps = cv.getProperties();

	String linkbasepath = (String) hmProps.get("linkbasepath");
	StringBuffer sb = new StringBuffer();

	public String getFilename() {
		return filename;
	}

	protected void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	XMLWriter xmlWriter = new XMLWriter();

	public EditorService() {
		ctx = new ClassPathXmlApplicationContext("mergeConfig.xml");
		xmlBean = (MapBean) ctx.getBean("fgdc_merge_map");
		multiMapBean = (MultiMapBean) ctx.getBean("multiTags");
		xpath_map = xmlBean.getFgdc();
		multi_map = multiMapBean.getMultiBean();

		// sb.append("New file saved: ");
	}

	public void buildXML(FormElements fe) {

		Iterator I = fe.getElements().keySet().iterator();
		ArrayList<String> portsList = new ArrayList<String>();
		while (I.hasNext()) {
			String key = (String) I.next();
			String value = (String) fe.getElements().get(key);
			if (key.contains("field_port_of_call")) {
				String[] values = value.split("\n");

				for (int i = 0; i < values.length; i++) {

					portsList.add(values[i].trim());
					// System.out.println(values[i].trim());
				}
			} else
				fieldMap.put(key, value);

		}
		try {
			// adding varaibles
			try {
				AddNewVariables ADV = new AddNewVariables();
				String name = "";
				String description = "";
				for (int i = 0; i < 15; i++) {
					if (fieldMap.containsKey("field_variable" + i)) {
						name = (String) fieldMap.get("field_variable" + i);
						description = (String) fieldMap
								.get("field_variable_description" + i);
						if (name != null && name != "") {
							if (description == null)
								description = "";

							ADV.addVariable(name, description);

						}
					}

				}
			} catch (Exception E) {
				E.printStackTrace();
			}
			// Add Authors

			String authorname = (String) fe.getElements()
					.get("field_ownername");
			String orgname = (String) fe.getElements().get(
					"field_organizationame");
			String oweneraddress = (String) fe.getElements().get(
					"field_owneraddress");
			String owenertel = (String) fe.getElements().get(
					"field_telephonenumber");
			String owneremail = (String) fe.getElements().get("field_email");

			String filestatus = (String) fe.getElements().get(
					"field_filestatus");
			String emailAddress = (String) fe.getElements().get(
					"field_user_email");
			try {

				AddNewAuthors addNewAuthors = new AddNewAuthors();
				if (authorname != null || orgname != null
						|| oweneraddress != null || owenertel != null
						|| owneremail != null) {
					addNewAuthors.addppl(authorname, orgname, oweneraddress,
							owenertel, owneremail);

				}

				authorname = (String) fe.getElements().get("field_ownername2");
				orgname = (String) fe.getElements().get(
						"field_organizationame2");
				oweneraddress = (String) fe.getElements().get(
						"field_owneraddress2");
				owenertel = (String) fe.getElements().get(
						"field_telephonenumber2");
				owneremail = (String) fe.getElements().get("field_email2");
				if (authorname != null || orgname != null
						|| oweneraddress != null || owenertel != null
						|| owneremail != null)
					addNewAuthors.addppl(authorname, orgname, oweneraddress,
							owenertel, owneremail);

				authorname = (String) fe.getElements().get("field_ownername3");
				orgname = (String) fe.getElements().get(
						"field_organizationame3");
				oweneraddress = (String) fe.getElements().get(
						"field_owneraddress3");
				owenertel = (String) fe.getElements().get(
						"field_telephonenumber3");
				owneremail = (String) fe.getElements().get("field_email3");
				if (authorname != null) {
					if (orgname == null)
						orgname = "";
					if (oweneraddress == null)
						oweneraddress = "";
					if (owenertel == null)
						owenertel = "";
					if (owneremail == null)
						owneremail = "";

					addNewAuthors.addppl(authorname, orgname, oweneraddress,
							owenertel, owneremail);

				}
			} catch (Exception E) {
				E.printStackTrace();
			}
			// adding SurveyName
			AddNewSurvey addNewSurvey = new AddNewSurvey();

			String surveyName = (String) fe.getElements().get(
					"field_experiment_name");
			if (surveyName != null && surveyName != "") {
				try {
					addNewSurvey.addSurvey(surveyName);
				} catch (Exception E) {
					E.printStackTrace();
				}

			}

			// adding vessel info
			AddNewVessel addNewVessel = new AddNewVessel();
			String vesselName = (String) fe.getElements().get(
					"field_vessel_name");
			String vesselId = (String) fe.getElements().get("field_vessel_id");
			String country = (String) fe.getElements().get(
					"field_vessel_country");
			String vesselOwner = (String) fe.getElements().get(
					"field_vessel_owner");

			if (vesselName != null && vesselName != "") {
				if (vesselId == null)
					vesselId = "";
				if (country == null)
					country = "";
				if (vesselOwner == null)
					vesselOwner = "";

				try {
					addNewVessel.addVessel(vesselName, vesselId, country,
							vesselOwner);
				} catch (Exception E) {
					E.printStackTrace();
				}
			}
			// create record_id
			String record_id = "";
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
			// DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String sessionId = RequestContextHolder.currentRequestAttributes()
					.getSessionId();
			if ((String) (fe.getElements().get("field_record_id")) == null
					|| (String) (fe.getElements().get("field_record_id")) == "") {
				record_id = dateFormat.format(date) + "_" + sessionId;
				fieldMap.put("field_record_id", record_id);

			} else
				record_id = (String) fe.getElements().get("field_record_id");

			// create file name
			String fileName = (String) fieldMap.get("field_filename");
			String field_ownername = (String) (fe.getElements()
					.get("field_ownername"));
			String field_dataset_id = (String) (fe.getElements()
					.get("field_title"));

			if (fileName == null || fileName == "") {

				if (field_dataset_id != null) {
					fileName = field_dataset_id;
				} else {
					if (field_ownername.length() > 100)
						fileName = field_ownername.subSequence(0, 100) + "_"
								+ dateFormat.format(date);
					else
						fileName = field_ownername + "_"
								+ dateFormat.format(date);

				}

			} else if (!fileName.contains(field_dataset_id)
					&& field_dataset_id != null) {
				fileName = fileName.replace(".xml", "");
				fileName = field_dataset_id;

			} else
				fileName = fileName.replace(".xml", "");

			if (fileName.length() > 255) {
				fileName = (String) fileName.subSequence(0, 255);
			}
			if (filestatus == "approve")
				if (!fileName.contains(field_dataset_id))
					fileName = field_dataset_id + fileName;

			fileName = fileName.replaceAll("[^A-Za-z0-9]+", "_");

			// creating xml
			xmlDoc = xmlWriter.buildDoc(fieldMap, "", 0, portsList, null,
					xpath_map, multi_map);

			// creating file location
			// String formtype = (String)
			// fe.getElements().get("field_form_type");
			// filepath = (String) hmProps.get("foldername");
			filepath = loggedin_username;

			// saving the file
			xmlWriter.outputDocToXML(xmlDoc, filepath, fileName + ".xml");
			this.setFilepath(filepath + "/" + fileName + ".xml");
			this.setFilename(fileName + ".xml");
			sb.append(linkbasepath + filepath);

			// sending emails
			try {
				// send emails
				if (filestatus != "" && filestatus != null) {
					filestatus = (String) fe.getElements().get(
							"field_filestatus");
				}
				if (emailAddress != "" && emailAddress != null) {

					try {
						EmailController.sendEmail(filestatus, fileName, sb,
								emailAddress);
					} catch (Exception E) {
						E.printStackTrace();
					}

				}

			} catch (Exception e) {
			}

			// saving fiel in the database
			try {
				String creator = (String) fe.getElements().get(
						"field_user_email");
				if (creator != null) {
				} else {
					creator = "not entered";
				}
				SaveFile saveFile = new SaveFile();
				if (record_id != null && filepath != null)
					saveFile.addfile(record_id, linkbasepath + filepath,
							creator,
							(String) fe.getElements().get("field_filestatus"));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception E) {
			E.printStackTrace();
		}
	}

	public String buildXMLString(FormElements fe) throws IOException {

		StringBuilder sb = new StringBuilder();
		Iterator I = fe.getElements().keySet().iterator();
		ArrayList<String> portsList = new ArrayList<String>();
		while (I.hasNext()) {
			String key = (String) I.next();
			String value = (String) fe.getElements().get(key);
			if (key.contains("field_port_of_call")) {
				String[] values = value.split("\n");

				for (int i = 0; i < values.length; i++) {
					portsList.add(values[i].trim());
					// System.out.println(values[i].trim());
				}
			} else if (key.contains("field_filename")
					|| key.contains("field_record_id")) {

			} else
				fieldMap.put(key, value);
		}

		try {

			xmlDoc = xmlWriter.buildDoc(fieldMap, "", 0, portsList, null,
					xpath_map, multi_map);
			// create file name
			sb.append(xmlWriter.outputDocToXMLString(xmlDoc));
		} catch (Exception E) {
			E.printStackTrace();
		}

		return sb.toString();
	}

	public void writeFGDC(Metadata_Editor med, String outFile) {

		HashMap fieldMap2 = med.getLhm();
		Set<String> myKeys2 = fieldMap2.keySet();
		for (String s2 : myKeys2) {
			try {
				xmlDoc = xmlWriter.buildDoc(fieldMap2, "", 0, null, null,
						xpath_map, multi_map);
				xmlWriter.outputDocToXML(xmlDoc, filepath, outFile);
			} catch (Exception E) {
				E.printStackTrace();
			}
		}
	}

	public Metadata_Editor readURI(String uri) {
		BufferedReader in = null;
		Metadata_Editor med = new Metadata_Editor();
		ModelMap model = new ModelMap();
		try {
			URL lURL = new URL(uri);
			URLConnection lURLconn = lURL.openConnection();
			in = new BufferedReader(new InputStreamReader(
					lURLconn.getInputStream(), "UTF-8"));
		} catch (Exception exc) {
			String message = "There is a problem with the requested URL.";
			model.addAttribute("message", message);
		}

		Document doc = null;

		SAXBuilder builder = new SAXBuilder();
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature(
				"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
				false);
		builder.setFeature(
				"http://apache.org/xml/features/nonvalidating/load-external-dtd",
				false);
		try {
			doc = builder.build(in);
			med = read(doc);
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return med;
	}

	public Metadata_Editor readFGDCFromXML(String fgdcText)
			throws UnsupportedEncodingException {
		Metadata_Editor med = new Metadata_Editor();
		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		builder.setFeature("http://xml.org/sax/features/validation", false);
		builder.setFeature(
				"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
				false);
		builder.setFeature(
				"http://apache.org/xml/features/nonvalidating/load-external-dtd",
				false);

		byte[] bytes = fgdcText.getBytes("UTF-8");
		ByteArrayInputStream BAI = new ByteArrayInputStream(bytes);
		try {
			doc = builder.build(BAI);
			med = read(doc);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return med;
	}

	public Metadata_Editor readFGDC(String inFile)
			throws UnsupportedEncodingException, FileNotFoundException {

		Document doc = null;
		Metadata_Editor med = null;
		SAXBuilder builder = new SAXBuilder();
		/*
		 * builder.setFeature("http://xml.org/sax/features/validation", false);
		 * builder.setFeature(
		 * "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
		 * false); builder.setFeature(
		 * "http://apache.org/xml/features/nonvalidating/load-external-dtd",
		 * false);
		 */
		File in = new File(inFile);
		try {
			doc = builder.build(in);
			med = read(doc);

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return med;

	}

	public Metadata_Editor read(Document doc) {
		Metadata_Editor med = new Metadata_Editor();

		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"mergeConfig.xml");
		HashMap fromXML = new HashMap();
		String value = "";

		String xexprBean = "";
		XPath beans = null;
		Set<String> set1 = xpath_map.keySet();

		MapBean xmlBean = (MapBean) ctx.getBean("fgdc_merge_map");
		LinkedHashMap<String, String> xpath_map = xmlBean.getFgdc();

		MultiMapBean multiPBean = (MultiMapBean) ctx.getBean("multiTags");
		LinkedHashMap<String, String> multiMap = multiPBean.getMultiBean();
		Set<String> set2 = multiMap.keySet();

		DynamicMapBean DMB = (DynamicMapBean) ctx.getBean("dynamicTags");
		LinkedHashMap<String, String> dynamicMap = DMB.getDynamicBean();
		Set<String> set3 = dynamicMap.keySet();

		ArrayList<String> conflicts = new ArrayList<String>();
		for (String s1 : set1) {
			xexprBean = s1;
			try {
				beans = XPath.newInstance(xpath_map.get(xexprBean));
				value = beans.valueOf(doc);

				if ((null != value) && (value.trim().length() > 0)) {

					if (value.contains("%%CONFLICT%%")) {
						String conflictXpath = getConflictTag(beans.getXPath());
						beans = XPath.newInstance(conflictXpath);
						System.out.println(conflictXpath);
						Element parent = (Element) beans.selectSingleNode(doc,
								conflictXpath);
						List<Element> elements = parent.getChildren("VALUE");

						value = "";
						int count = 0;
						do {
							value = value
									+ elements.get(count).getValue().trim();
							count = count + 1;
							if (count < elements.size())
								value = value + '#';
						} while (count < elements.size());
						// System.out.println(value);
						conflicts.add(s1);
						fromXML.put(s1, value);

					} else {
						value = beans.valueOf(doc);
						fromXML.put(s1, value);
					}

				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
		for (String s2 : set2) {
			xexprBean = s2;
			try {
				beans = XPath.newInstance(multiMap.get(xexprBean));
				value = beans.valueOf(doc);
				beans = XPath.newInstance(beans.getXPath());
				// System.out.println(conflictXpath);
				List<Element> elements = beans.selectNodes(doc,
						beans.getXPath());
				for (Element e : elements) {
					value = e.getValue();
					

					if ((null != value) && (value.trim().length() > 0)) {
						if (value.contains("%%CONFLICT%%")) {

							String conflictXpath = getConflictTag(beans
									.getXPath());
							String key = "";
							String keyVal = "";
							String xpath = multiMap.get(xexprBean);
							
							if (xpath.contains("Investigator")) {
								key = "Email";
								keyVal = e.getParentElement().getChild("Email")
										.getValue();
							} else if (xpath.contains("Variable")) {
								key = "Variable_Name";
								keyVal = e.getParentElement()
										.getChild("Variable_Name").getValue();
							} else if (xpath.contains("Sensor")) {
								key = "Model";
								keyVal = e.getParentElement().getChild("Model")
										.getValue();
							}
							//System.out.println("key: "+key);
							//System.out.println("keyVal: "+keyVal);
							
							List<Element> parents = beans.selectNodes(doc,
									conflictXpath);
							for (Element element : parents) {
								//System.out.println(element.getParentElement().getAttributeValue(key));
								if (keyVal.contains(element.getParentElement().getAttributeValue(key).trim())) {
									List<Element> elms = element.getChildren("VALUE");
									value = "";
									int count = 0;
									do {
										value = value
												+ elms.get(count).getValue()
														.trim();
										count = count + 1;
										if (count < elms.size())
											value = value + '#';
									} while (count < elms.size());
								}

							}
							conflicts.add(s2);
							fromXML.put(s2, value);
						} else {
							value = beans.valueOf(doc);
							fromXML.put(s2, value);
						}

					}
				}

			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
		for (String s3 : set3) {
			xexprBean = s3;
			try {
				beans = XPath.newInstance(dynamicMap.get(xexprBean));

				String strValue = "";
				// only keep entries which are not empty
				List<Element> elements = beans.selectNodes(doc,
						dynamicMap.get(xexprBean));
				for (Element e : elements) {
					strValue = strValue + e.getValue().trim() + "\n";

				}
				if ((null != strValue) && (strValue.trim().length() > 0)) {
					fromXML.put(s3, strValue);
				}

			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}

		fromXML.put("field_conflicts", conflicts);
		med.setLhm(fromXML);
		return med;
	}

	private String getConflictTagForRepeatSection(String xPath, String key,
			String keyVal) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getKeys() {

		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		ArrayList<String> myList = new ArrayList<String>();
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"mergeConfig.xml");
		MapBean xmlBean = (MapBean) ctx.getBean("fgdc_merge_map");
		LinkedHashMap<String, String> xpath_map = xmlBean.getFgdc();
		Set<String> al = xpath_map.keySet();
		for (String s1 : al) {
			// private String field_dataform;
			myList.add("private String " + s1 + ";");
			sb.append("private String " + s1 + ";\n");
			// this.field_authorname = lhm.get("field_authorname");
			sb2.append("this." + s1 + " = lhm.get(\"" + s1 + "\");\n");
		}
		// System.out.println(sb.toString());
		// System.out.println(sb2.toString());
		return myList;
	}

	// //////////////////////////

	public static void main(String args[]) throws UnsupportedEncodingException,
			FileNotFoundException {

		EditorService eds = new EditorService();

		// test editor I/O using DAO
		Metadata_Editor med = new Metadata_Editor();
		String name = "zzr";
		med.setField_ownername("Red Hots");
		// med.setField_datasetdescription("The truth is out there.");

		XMLWriter xmlWriter = new XMLWriter();
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"mergeConfig.xml");
		MapBean xmlBean = (MapBean) ctx.getBean("fgdc_merge_map");
		LinkedHashMap<String, String> xpath_map = xmlBean.getFgdc();
		HashMap fromXML = new HashMap();
		HashMap fieldMap = med.getLhm();
		Document xmlDoc = null;
		try {
			xmlDoc = xmlWriter.buildDoc(fieldMap, "", 0, null, null, xpath_map,
					null);
			xmlWriter.outputDocToXML(xmlDoc, name, "fgdc_xml_test.xml");
		} catch (Exception E) {
			E.printStackTrace();
		}

		// Test logic here for reading a single value back from the file
		// written above

		String value = "";
		File fi = new File("fgdc_xml_test.xml");
		SAXBuilder builder = new SAXBuilder();
		ArrayList<String> al1 = new ArrayList<String>();
		String xexprBean = "//metadata/ idinfo/ descript/ abstract";
		XPath beans = null;
		Document doc = null;
		try {
			beans = XPath.newInstance(xexprBean);
			doc = builder.build(fi);
			value = beans.valueOf(doc);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// test looping structure with defining element list
		// this tests the logic for re-populating values to the object used by
		// the jsp form
		// method expects:
		// filename so Document object (doc) can be constructed
		// returns med object ?? or just lhm ??
		// public HashMap<String,String> getMergeFields(String inFile){
		Set<String> set1 = xpath_map.keySet();

		for (String s1 : set1) {
			xexprBean = s1;
			try {
				beans = XPath.newInstance(xpath_map.get(xexprBean));
				value = beans.valueOf(doc);
				// only keep entries which are not empty
				if ((null != value) && (value.trim().length() > 0)) {
					fromXML.put(s1, value);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			}
		}
		Set<String> myKeys = fromXML.keySet();
		for (String s2 : myKeys) {

		}

		// test logic for re-writing file using the map object as will
		// be built by the controller / form tag code
		// open new file in editor to see
		// inputs:
		// USGS_Core_Science_Metadata_Editor med -- our Model object)
		// File outFile filename to write to, including path
		// output: new metadata file
		med.setLhm(fromXML);
		String outFile = "fgdc_xml_test2.xml";

		HashMap fieldMap2 = med.getLhm();
		Set<String> myKeys2 = fieldMap2.keySet();
		for (String s2 : myKeys2) {
			try {
				xmlDoc = xmlWriter.buildDoc(fieldMap2, "", 0, null, null,
						xpath_map, null);
				xmlWriter.outputDocToXML(xmlDoc, name, outFile);
			} catch (Exception E) {
				E.printStackTrace();
			}
		}

		Metadata_Editor umed = eds.readFGDC("fgdc_xml_test.xml");
		eds.writeFGDC(umed, "fgdc_xml_test4.xml");

	}

	public Boolean deleteRecord(String record_id) {

		SaveFile saveFile = new SaveFile();
		String file = saveFile.deletefile(record_id);
		if (file == null)
			return false;
		String linkbasepath = (String) hmProps.get("linkbasepath");
		// System.out.println(file);
		String doc_base = "";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {

			doc_base = (String) hmProps.get("win_base");

		}

		else {

			doc_base = (String) hmProps.get("lin_base");

		}
		file = file.replace(linkbasepath, doc_base);
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			file = file.replace("/", "\\");

		}

		File f = new File(file);
		try {
			f.delete();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private String getConflictTag(String tag) {
		String conflictTag = tag.replace("x_tags",
				"x_tags/ CONFLICTS/ Conflict");
		conflictTag = conflictTag.replaceAll("\\[[0-9]\\]", "");
		return conflictTag;
	}

}
