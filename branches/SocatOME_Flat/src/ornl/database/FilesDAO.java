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
 * A data access object (DAO) providing persistence and search support for Files
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see ornl.database.Files
 * @author MyEclipse Persistence Tools
 */

public class FilesDAO extends BaseHibernateDAO {
	private static final Log log = LogFactory.getLog(FilesDAO.class);
	// property constants
	public static final String FILE_LOCATION = "fileLocation";
	public static final String CREATOR_EMAIL = "creatorEmail";
	public static final String FILE_STATUS = "fileStatus";
	public static final String UPDATE_DATE = "updateDate";

	public void save(Files transientInstance) {
		log.debug("saving Files instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Files persistentInstance) {
		log.debug("deleting Files instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Files findById(java.lang.String id) {
		log.debug("getting Files instance with id: " + id);
		try {
			Files instance = (Files) getSession()
					.get("ornl.database.Files", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Files instance) {
		log.debug("finding Files instance by example");
		try {
			List results = getSession().createCriteria("ornl.database.Files")
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
		log.debug("finding Files instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Files as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByFileLocation(Object fileLocation) {
		return findByProperty(FILE_LOCATION, fileLocation);
	}

	public List findByCreatorEmail(Object creatorEmail) {
		return findByProperty(CREATOR_EMAIL, creatorEmail);
	}

	public List findByFileStatus(Object fileStatus) {
		return findByProperty(FILE_STATUS, fileStatus);
	}

	public List findByUpdateDate(Object updateDate) {
		return findByProperty(UPDATE_DATE, updateDate);
	}

	public List findAll() {
		log.debug("finding all Files instances");
		try {
			String queryString = "from Files";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Files merge(Files detachedInstance) {
		log.debug("merging Files instance");
		try {
			Files result = (Files) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Files instance) {
		log.debug("attaching dirty Files instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Files instance) {
		log.debug("attaching clean Files instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}
