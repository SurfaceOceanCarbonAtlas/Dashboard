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
 * Survey entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see ornl.database.Survey
 * @author MyEclipse Persistence Tools
 */

public class SurveyDAO extends BaseHibernateDAO {
	private static final Log log = LogFactory.getLog(SurveyDAO.class);
	// property constants
	public static final String SURVEY_NAME = "surveyName";

	public void save(Survey transientInstance) {
		log.debug("saving Survey instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Survey persistentInstance) {
		log.debug("deleting Survey instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Survey findById(java.lang.Long id) {
		log.debug("getting Survey instance with id: " + id);
		try {
			Survey instance = (Survey) getSession().get("ornl.database.Survey",
					id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Survey instance) {
		log.debug("finding Survey instance by example");
		try {
			List results = getSession().createCriteria("ornl.database.Survey")
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
		log.debug("finding Survey instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Survey as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findBySurveyName(Object surveyName) {
		return findByProperty(SURVEY_NAME, surveyName);
	}

	public List findAll() {
		log.debug("finding all Survey instances");
		try {
			String queryString = "from Survey";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Survey merge(Survey detachedInstance) {
		log.debug("merging Survey instance");
		try {
			Survey result = (Survey) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Survey instance) {
		log.debug("attaching dirty Survey instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Survey instance) {
		log.debug("attaching clean Survey instance");
		try {
			getSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}
