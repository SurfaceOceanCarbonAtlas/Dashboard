/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;

import ornl.database.Survey;
import ornl.database.SurveyDAO;

public class AddNewSurvey {

	public boolean addSurvey(String surveyName) {
		boolean added = false;
		boolean exists = false;
		Survey survey = new Survey();
		SurveyDAO surveyDAO = new SurveyDAO();
		List all = surveyDAO.findAll();
		Iterator ite = all.iterator();
		while (ite.hasNext()) {
			Survey ite2 = (Survey) ite.next();
			if (ite2.getSurveyName().equalsIgnoreCase(surveyName)) {
				exists = true;
				return exists;
			} 
		}
		if(!exists&&!added){				
			Transaction tx = surveyDAO.getSession().beginTransaction();				
			if(surveyName!=null&&surveyName.length()>0){
				survey.setSurveyName(surveyName);
			}
			
			surveyDAO.save(survey);
			tx.commit();
			surveyDAO.getSession().close();
			added=true;				
		}

		return added;
	}

}
