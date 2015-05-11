/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.Example;

/**
 * A data access object (DAO) providing persistence and search support for
 * Vessel entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see ornl.database.Vessel
 * @author MyEclipse Persistence Tools
 */

public class VesselDAO extends BaseHibernateDAO {
	private static final Log log = LogFactory.getLog(VesselDAO.class);
	// property constants
	public static final String VESSEL_NAME = "vesselName";
	public static final String VESSEL_ID = "vesselId";
	public static final String COUNTRY = "country";
	public static final String VESSEL_OWNER = "vesselOwner";

	public void save(Vessel transientInstance) {
		log.debug("saving Vessel instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Vessel persistentInstance) {
		log.debug("deleting Vessel instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Vessel findById(java.lang.Long id) {
		log.debug("getting Vessel instance with id: " + id);
		try {
			Vessel instance = (Vessel) getSession().get("ornl.database.Vessel",
					id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Vessel instance) {
		log.debug("finding Vessel instance by example");
		try {
			List results = getSession().createCriteria("ornl.database.Vessel")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Vessel instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Vessel as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByVesselName(Object vesselName) {
		return findByProperty(VESSEL_NAME, vesselName);
	}

	public List findByVesselId(Object vesselId) {
		return findByProperty(VESSEL_ID, vesselId);
	}

	public List findByCountry(Object country) {
		return findByProperty(COUNTRY, country);
	}

	public List findByVesselOwner(Object vesselOwner) {
		return findByProperty(VESSEL_OWNER, vesselOwner);
	}

	public List findAll() {
		log.debug("finding all Vessel instances");
		try {
			String queryString = "from Vessel";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Vessel merge(Vessel detachedInstance) {
		log.debug("merging Vessel instance");
		try {
			Vessel result = (Vessel) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Vessel instance) {
		log.debug("attaching dirty Vessel instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Vessel instance) {
		log.debug("attaching clean Vessel instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}
