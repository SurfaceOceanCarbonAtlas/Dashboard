<div id="contact" class="section">
	<h4 class="alert alert-info">
		Metadata Creator:<span class="form-required"
			title="This field is required.">*</span>
	</h4>



	<div class="Section2">
		<label>Name:<span class="form-required"
			title="This field is required.">*</span>
		</label>
		<form:input path="field_username" id="field_username"
			name="field_username" value="" />
	</div>
	<div class="Section2">
		<label style="width: 88px"> Organization:<span
			class="form-required" title="This field is required.">*</span></label>
		<form:input path="field_user_organizationame"
			id="field_user_organizationame" disabled="false" />
	</div>
	<div class="Section2">
		<label style="width: 88px"> Address:<span
			class="form-required" title="This field is required.">*</span></label>
		<form:input path="field_user_adress" id="field_user_adress" value="" />
	</div>
	<div class="Section2">
		<label style="width: 88px"> Telephone:<span
			class="form-required" title="This field is required.">*</span></label>
		<form:input path="field_user_telephonenumber"
			id="field_user_telephonenumber" value="" />
	</div>
	<div class="Section2">
		<label for="userEmail" style="width: 88px">Email:<span
			class="form-required">*</span>
		</label>
		<form:input path="field_user_email" id="field_user_email" type="text" />
	</div>
	<div class="description">
		<i>On submission of a metadata record a notification is send to
			the metadata creator with a link to metadata that was created. </i>
	</div>
</div>