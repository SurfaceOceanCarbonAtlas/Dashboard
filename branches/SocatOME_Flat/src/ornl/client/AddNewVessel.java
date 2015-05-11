/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;

import ornl.database.Vessel;
import ornl.database.VesselDAO;

public class AddNewVessel {

	public boolean addVessel(String vesselName, String vesselId,
			String country, String owner) {
		boolean added = false;
		boolean updated = false;
		boolean exists = false;
		Vessel vessel = new Vessel();
		VesselDAO vesselDAO = new VesselDAO();
		List all = vesselDAO.findAll();
		Iterator ite = all.iterator();
		while (ite.hasNext()) {
			Vessel ite2 = (Vessel) ite.next();
			if (ite2.getVesselName().equalsIgnoreCase(vesselName)
					&& ite2.getVesselId().equalsIgnoreCase(vesselId)
					&& ite2.getCountry().equalsIgnoreCase(country)
					&& ite2.getVesselOwner().equalsIgnoreCase(owner)) {
				exists = true;
				return exists;
			} else if (ite2.getVesselName().equalsIgnoreCase(vesselName)
					&& (!ite2.getVesselId().equalsIgnoreCase(vesselId)
							|| !ite2.getCountry().equalsIgnoreCase(country)
							|| !ite2.getVesselOwner().equalsIgnoreCase(owner))) {
				// AuthorDAO authorDAO2 = new AuthorDAO();
				// Author findauthor = new Author();
				long vesselid = ite2.getId();

				// AuthorDAO author_enwDAO = new AuthorDAO();
				vessel = vesselDAO.findById(vesselid);

				if (vesselName != null && vesselName.length() > 0) {
					vessel.setVesselName(vesselName);
				}
				
				if (vesselId != null && vesselId.length() > 0) {
					vessel.setVesselId(vesselId);
				}
				
				if (country != null && country.length() > 0) {
					vessel.setCountry(country);
				}
				
				if (owner != null && owner.length() > 0) {
					vessel.setVesselOwner(owner);
				}
				

				Transaction tx = vesselDAO.getSession().beginTransaction();
				vesselDAO.save(vessel);
				tx.commit();
				// authorDAO.getSession().close();
				exists = false;
				updated = true;
				return updated;
				// break;

			}

		}
		if (!exists && !updated) {
			Transaction tx = vesselDAO.getSession().beginTransaction();
			if (vesselName != null && vesselName.length() > 0) {
				vessel.setVesselName(vesselName);
			}
			else
				vessel.setVesselName("");
			if (vesselId != null && vesselId.length() > 0) {
				vessel.setVesselId(vesselId);
			}
			else
				vessel.setVesselId("");
			if (country != null && country.length() > 0) {
				vessel.setCountry(country);
			}
			else
				vessel.setCountry("");
			if (owner != null && owner.length() > 0) {
				vessel.setVesselOwner(owner);
			}
			else
				vessel.setVesselOwner("");
			vesselDAO.save(vessel);
			tx.commit();
			vesselDAO.getSession().close();
			added = true;
		}

		return added;
	}
}
