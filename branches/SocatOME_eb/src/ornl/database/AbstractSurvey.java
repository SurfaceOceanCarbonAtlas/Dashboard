/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

/**
 * AbstractSurvey entity provides the base persistence definition of the Survey
 * entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractSurvey implements java.io.Serializable {

	// Fields

	private Long id;
	private String surveyName;

	// Constructors

	/** default constructor */
	public AbstractSurvey() {
	}

	/** full constructor */
	public AbstractSurvey(String surveyName) {
		this.surveyName = surveyName;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSurveyName() {
		return this.surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

}
