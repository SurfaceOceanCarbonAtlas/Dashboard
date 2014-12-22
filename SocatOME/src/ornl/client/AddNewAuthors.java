/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hibernate.Transaction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ornl.beans.Configuration;
import ornl.database.Author;
import ornl.database.AuthorDAO;

public class AddNewAuthors {
	private ArrayList respValue = new ArrayList();
	public Properties properties = null;
	ApplicationContext factory = new ClassPathXmlApplicationContext(
			"applicationContext.xml");
	Configuration cv = (Configuration) factory.getBean("propertiesBean");
	HashMap hmProps = cv.getProperties();
	String delimiter2 = (String) hmProps.get("delimiter");
	long id = 121;
	private String delimiter = ";";

	public boolean addppl(String name, String orgName, String orgAddress,
			String ownerTel, String ownerEmail) {
		boolean added = false;
		boolean updated = false;
		boolean exists = false;
		Author author = new Author();
		AuthorDAO authorDAO = new AuthorDAO();
		List all = authorDAO.findAll();
		Iterator ite = all.iterator();
		while (ite.hasNext()) {
			Author ite2 = (Author) ite.next();
			if (ite2.getFullName().equalsIgnoreCase(name)
					&& ite2.getOrganization().equalsIgnoreCase(orgName)
					&& ite2.getAddress().equalsIgnoreCase(orgAddress)
					&& ite2.getPhone().equalsIgnoreCase(ownerTel)
					&& ite2.getEmail().equalsIgnoreCase(ownerEmail)) {
				exists = true;
				return exists;
			} else if (ite2.getFullName().equalsIgnoreCase(name)
					&& (!ite2.getOrganization().equalsIgnoreCase(orgName)
							|| !ite2.getAddress().equalsIgnoreCase(orgAddress)
							|| !ite2.getPhone().equalsIgnoreCase(ownerTel) || !ite2
							.getEmail().equalsIgnoreCase(ownerEmail))) {
				// AuthorDAO authorDAO2 = new AuthorDAO();
				// Author findauthor = new Author();
				long authorid = ite2.getId();

				// AuthorDAO author_enwDAO = new AuthorDAO();
				author = authorDAO.findById(authorid);

				if (name != null ) {
					author.setFullName(name);
				}

				if (orgName != null ) {
					author.setOrganization(orgName);
				}

				if (orgAddress != null ) {
					author.setAddress(orgAddress);
				}

				if (ownerTel != null ) {
					author.setPhone(ownerTel);
				}

				if (ownerEmail != null ) {
					author.setEmail(ownerEmail);
				}

				Transaction tx = authorDAO.getSession().beginTransaction();
				authorDAO.save(author);
				tx.commit();
				// authorDAO.getSession().close();
				exists = false;
				updated = true;
				return updated;
				// break;

			}

		}
		if (!exists && !updated) {
			Transaction tx = authorDAO.getSession().beginTransaction();
			if (name != null && name.length() > 0) {
				author.setFullName(name);
			}
			else
				author.setFullName("");
			
			
			if (orgName != null && orgName.length() > 0) {
				author.setOrganization(orgName);
			}
			else
				author.setOrganization("");
			
			

			if (orgAddress != null && orgAddress.length() > 0) {
				author.setAddress(orgAddress);
			}
			else
				author.setAddress("");
			
			
			if (ownerTel != null && ownerTel.length() > 0) {
				author.setPhone(ownerTel);
			}
			else
				author.setPhone("");
			
			

			if (ownerEmail != null && ownerEmail.length() > 0) {
				author.setEmail(ownerEmail);
			}
			else
				author.setEmail("");

			authorDAO.save(author);
			tx.commit();
			authorDAO.getSession().close();
			added = true;
		}

		return added;
	}
}
