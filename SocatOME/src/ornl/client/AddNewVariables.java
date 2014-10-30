/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;
import ornl.database.Variables;
import ornl.database.VariablesDAO;

public class AddNewVariables {
	public boolean addVariable(String name, String description) {
		boolean added = false;
		boolean updated = false;
		boolean exists = false;
		Variables variable = new Variables();
		VariablesDAO variablesDAO = new VariablesDAO();
		List all = variablesDAO.findAll();
		Iterator ite = all.iterator();
		while (ite.hasNext()) {
			Variables ite2 = (Variables) ite.next();
			if (ite2.getName().equalsIgnoreCase(name) && ite2.getDescription().equalsIgnoreCase(description)
					) {
				exists = true;
				return exists;
			} else if (ite2.getName().equalsIgnoreCase(name)
					&& (!ite2.getDescription().equalsIgnoreCase(description)
							) ){
				// AuthorDAO authorDAO2 = new AuthorDAO();
				// Author findauthor = new Author();
				long variableid = ite2.getId();

				// AuthorDAO author_enwDAO = new AuthorDAO();
				variable = variablesDAO.findById(variableid);

				if (name != null && name.length() > 0) {
					variable.setName(name);
				} 
					
				if (description != null && description.length() > 0) {
					variable.setDescription(description);
				}
								

				Transaction tx = variablesDAO.getSession().beginTransaction();
				variablesDAO.save(variable);
				tx.commit();
				// authorDAO.getSession().close();
				exists = false;
				updated = true;
				variablesDAO.getSession().close();
				return updated;
				// break;

			}

		}
		if (!exists && !updated) {
			Transaction tx = variablesDAO.getSession().beginTransaction();
			if (name != null && name.length() > 0) {
				variable.setName(name);
			}
			if (description != null && description.length() > 0) {
				variable.setDescription(description);
			}
			else
				variable.setDescription("");			
			variablesDAO.save(variable);
			tx.commit();
			variablesDAO.getSession().close();
			added = true;
		}

		return added;
	}
}
