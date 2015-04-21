/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ornl.beans.Editor;

public class EditorValidator implements Validator {

	@Override
	public boolean supports(Class clazz) {

		return Editor.class.isAssignableFrom(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {

	//	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName","required.userName", "Field name is required.");

		
	}

}
