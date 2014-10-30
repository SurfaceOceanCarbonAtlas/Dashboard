/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ornl.beans.Configuration;
import ornl.database.Author;
import ornl.database.AuthorDAO;
import ornl.database.Survey;
import ornl.database.SurveyDAO;
import ornl.database.Variables;
import ornl.database.VariablesDAO;
import ornl.database.Vessel;
import ornl.database.VesselDAO;

/**
 * Lookup and auto complete utility.
 * 
 */

public class DWRUtil {

	public Properties properties = null;
	ApplicationContext factory = new ClassPathXmlApplicationContext(
			"applicationContext.xml");
	Configuration cv = (Configuration) factory.getBean("propertiesBean");
	HashMap hmProps = cv.getProperties();
	String delimiter2 = (String) hmProps.get("delimiter");

	private String delimiter = ";";

	/**
	 * Method to be called to set the words
	 */

	public List getMatchedWord(final String partToken) {
		// properties = (Properties) factory.getBean("Util");
		// Configuration cv = (Configuration) factory.getBean("propertiesBean");
		// HashMap hmProps = cv.getProperties();

		List retKeys = new ArrayList();
		AuthorDAO authorDAO = new AuthorDAO();
		List all = authorDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Author ite2 = (Author) ite.next();
			
			hs.add(ite2.getFullName());

		}
		authorDAO.getSession().close();
		retKeys.addAll(hs);
		Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);

		return retKeys;

	}

	public String findauthDetails(final String partToken) {

		AuthorDAO authorDAO = new AuthorDAO();
		List all = authorDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Author ite2 = (Author) ite.next();
			if (partToken.toString().equalsIgnoreCase(ite2.getFullName())) {
				String data = ite2.getOrganization().toString() + "||"
						+ ite2.getAddress().toString() + "||"
						+ ite2.getPhone().toString() + "||"
						+ ite2.getEmail().toString();
				return data;
			}

		}
		authorDAO.getSession().close();

		return "";

	}

	public List getVesselNames(final String partToken) {

		List retKeys = new ArrayList();
		VesselDAO VesselDAO = new VesselDAO();
		List all = VesselDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Vessel ite2 = (Vessel) ite.next();
			if(ite2.getVesselName()!=""&& ite2.getVesselName()!=null)
				hs.add(ite2.getVesselName());

		}
		VesselDAO.getSession().close();
		retKeys.addAll(hs);
		Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);
		return retKeys;
	}

	public String findVesselDetails(final String partToken) {

		VesselDAO vesselDAO = new VesselDAO();
		List all = vesselDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Vessel ite2 = (Vessel) ite.next();
			if (partToken.equalsIgnoreCase(ite2.getVesselName())) {
				String data = ite2.getVesselId() + "||"
						+ ite2.getCountry() + "||"
						+ ite2.getVesselOwner();
				return data;
			}

		}
		vesselDAO.getSession().close();
		return "";

	}
	public List getVariableNames(final String partToken) {

		List retKeys = new ArrayList();
		VariablesDAO varaiblesDAO = new VariablesDAO();
		List all = varaiblesDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Variables ite2 = (Variables) ite.next();
			if(ite2.getName()!="" && ite2.getName()!=null)
			hs.add(ite2.getName());

		}
		varaiblesDAO.getSession().close();
		retKeys.addAll(hs);
		Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);
		return retKeys;
	}
	
	public String findVaraibleDetails(final String partToken) {

		VariablesDAO variableDAO = new VariablesDAO();
		List all = variableDAO.findAll();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Variables ite2 = (Variables) ite.next();
			if (partToken.toString().equalsIgnoreCase(ite2.getName())) {
				String data = ite2.getDescription().toString();
				return data;
			}

		}
		variableDAO.getSession().close();
		return "";

	}
	public List getSurveyNames(final String partToken) {
		SurveyDAO surveyDAO = new SurveyDAO();
		List all = surveyDAO.findAll();

		List retKeys = new ArrayList();
		Iterator ite = all.iterator();
		HashSet hs = new HashSet();
		while (ite.hasNext()) {
			Survey ite2 = (Survey) ite.next();
			if(ite2.getSurveyName()!="" && ite2.getSurveyName()!=null)
			hs.add(ite2.getSurveyName());

		}
		surveyDAO.getSession().close();
		retKeys.addAll(hs);
		Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);

		return retKeys;
		// } else {
		// return null;
		// }
	}

	public List loadKeywords(final String partToken, final String thesarusName) {
		if (null != delimiter2) {
			delimiter = delimiter2;
		}
		properties = (Properties) factory.getBean("NameUtil");
		// String[] sl = properties.getProperty("keywords").split(delimiter);
		String[] sl = null;
		if (thesarusName.equals("USGS Biocomplexity Thesaurus")) {
			sl = properties.getProperty("USGSkeywords").split(delimiter);
		} else if (thesarusName.contains("Base Thesaurus")) {
			sl = properties.getProperty("keywords").split(delimiter);
		} else
			sl = properties.getProperty("USGSkeywords").split(delimiter);
		ArrayList val = new ArrayList();

		if (sl != null) {
			val = new ArrayList(Arrays.asList(sl));

			List retKeys = new ArrayList();
			for (Iterator iter = val.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				retKeys.add(key);

			}
			Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);

			return retKeys;
		} else {
			return null;
		}

	}

	public List LoadThesarus(final String partToken) {

		if (null != delimiter2) {
			delimiter = delimiter2;
		}
		properties = (Properties) factory.getBean("NameUtil");
		String[] sl = properties.getProperty("thesaurus").split(delimiter);
		ArrayList val = new ArrayList();

		if (sl != null) {
			val = new ArrayList(Arrays.asList(sl));

			List retKeys = new ArrayList();
			for (Iterator iter = val.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				retKeys.add(key);

			}
			Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);

			return retKeys;
		} else {
			return null;
		}
	}

	public List loadPlaceKey(final String partToken) {
		if (null != delimiter2) {
			delimiter = delimiter2;
		}
		properties = (Properties) factory.getBean("NameUtil");
		String[] sl = properties.getProperty("placekey").split(delimiter);
		ArrayList val = new ArrayList();

		if (sl != null) {
			val = new ArrayList(Arrays.asList(sl));

			List retKeys = new ArrayList();
			for (Iterator iter = val.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				retKeys.add(key);

			}
			Collections.sort(retKeys, String.CASE_INSENSITIVE_ORDER);

			return retKeys;
		} else {
			return null;
		}

	}

	public static void main(String args[]) {

		// AuthorDAO authordao = new AuthorDAO();
		// Author author = new Author();
		// DB_ServiceImpl dbi = new DB_ServiceImpl();
		// author.setFull_name("Ranjeet");
		// authordao.save(author);
	}
}
