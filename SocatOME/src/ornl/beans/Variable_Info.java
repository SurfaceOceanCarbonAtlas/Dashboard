/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.beans;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlRootElement(name = "Variables_Info")
@XmlType(propOrder={"Variable_Name", "Description_of_Variable"})
public class Variable_Info {
	private String Variable_Name;
	public String getVariable_Name() {
		return Variable_Name;
	}
	public void setVariable_Name(String variable_Name) {
		Variable_Name = variable_Name;
	}
	public String getDescription_of_Variable() {
		return Description_of_Variable;
	}
	public void setDescription_of_Variable(String description_of_Variable) {
		Description_of_Variable = description_of_Variable;
	}
	private String Description_of_Variable;
}
