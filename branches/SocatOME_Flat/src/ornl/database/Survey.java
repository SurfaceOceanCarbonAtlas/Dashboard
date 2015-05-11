/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * Survey entity. @author MyEclipse Persistence Tools
 */
public class Survey extends AbstractSurvey implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public Survey() {
	}

	/** full constructor */
	public Survey(String surveyName) {
		super(surveyName);
	}

}
