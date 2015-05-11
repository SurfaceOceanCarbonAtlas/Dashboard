/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ornl.beans.Configuration;
import ornl.database.Files;
import ornl.database.FilesDAO;

/*
 * Ranjeet Devarakonda
 */
public class SaveFile {
	private ArrayList respValue = new ArrayList();
	public Properties properties = null;
	ApplicationContext factory = new ClassPathXmlApplicationContext(
			"applicationContext.xml");
	Configuration cv = (Configuration) factory.getBean("propertiesBean");
	HashMap hmProps = cv.getProperties();
	String delimiter2 = (String) hmProps.get("delimiter");
	long id = 121;
	private String delimiter = ";";

	public static boolean addfile(String fileid, String location,
			String creator, String status) throws MalformedURLException {
		boolean added = false;
		boolean updated = false;
		boolean exists = false;
		/*
		 * URL url =null; String filename=""; if(location!=null){ url = new
		 * URL(location); filename = url.getFile().replaceAll("/", ""); } else{
		 * filename = "invalid"; }
		 */

		Files files = new Files();
		FilesDAO filesDAO = new FilesDAO();
		List all = filesDAO.findAll();
		Iterator ite = all.iterator();

		Calendar calendar = Calendar.getInstance();
		java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime()
				.getTime());
		// System.out.println(ourJavaDateObject);
		while (ite.hasNext()) {

			Files ite2 = (Files) ite.next();
			if (ite2.getId().equalsIgnoreCase(fileid)
					&& ite2.getFileLocation().equalsIgnoreCase(location)
					&& ite2.getCreatorEmail().equals(creator)
					&& ite2.getFileStatus().equalsIgnoreCase(status)

			) {
				exists = true;
				return exists;
			} else if (ite2.getId().equalsIgnoreCase(fileid)
					&& (!ite2.getFileLocation().equalsIgnoreCase(location)
							|| !ite2.getCreatorEmail().equals(creator) || !ite2
							.getFileStatus().equalsIgnoreCase(status))) {

				// AuthorDAO author_enwDAO = new AuthorDAO();
				files = filesDAO.findById(fileid);
				if (location != null && location.length() > 0) {
					files.setFileLocation(location);
				}
				if (creator != null && creator.length() > 0) {
					files.setCreatorEmail(creator);
				}
				if (status != null && status.length() > 0) {
					files.setFileStatus(status);
				}
				files.setUpdateDate(ourJavaDateObject.toString());

				Transaction tx = filesDAO.getSession().beginTransaction();
				filesDAO.save(files);
				tx.commit();

				exists = false;
				updated = true;
				return updated;
			}

		}
		if (!exists & !updated) {
			Transaction tx = filesDAO.getSession().beginTransaction();
			if (fileid != null && fileid.length() > 0) {
				files.setId(fileid);
			}
			if (location != null && location.length() > 0) {
				files.setFileLocation(location);
			}
			if (creator != null && creator.length() > 0) {
				files.setCreatorEmail(creator);
			}
			if (status != null && status.length() > 0) {
				files.setFileStatus(status);
			}
			files.setUpdateDate(ourJavaDateObject.toString());
			filesDAO.save(files);
			tx.commit();
			filesDAO.getSession().close();
			added = true;
		}

		return added;
	}

	public static String deletefile(String fileid) {
		Files files = new Files();
		FilesDAO filesDAO = new FilesDAO();
		List all = filesDAO.findAll();
		Iterator ite = all.iterator();
		
		while (ite.hasNext()) {

			Files ite2 = (Files) ite.next();
			if (ite2.getId().equals(fileid)) {
				String filename = ite2.getFileLocation();
				Transaction tx = filesDAO.getSession().beginTransaction();				
				filesDAO.delete(ite2);
				tx.commit();
				filesDAO.getSession().close();
				return filename;
			}
		}
		return null;

	}

	public static void main(String args[]) throws MalformedURLException {

		// addfile("http://somethingnew/text.xml",
		// "devarakondar@ornl.gov","approved");
	}
}
