<div id="investigators" class="section">
	<h4 class="alert alert-info">Investigators:</h4>
	<div class="Section1 section" id="Investigator1">
		<h4>
			Investigator #1:<span class="form-required">*</span>
		</h4>
		<div>
			<div class="Section2">
				<label>Name: <span
					class="form-required" title="This field is required.">*</span><span class="description">(example: Jones, Dr. Robert W.)</span></label> 
				
				<form:input name="field_ownername" id="field_ownername"
					path="field_ownername" onkeyup="CheckOrg1();" />
				<div id="CodeList" class="auto_complete" onclick="CheckOrg1();"></div>
				<script>
											new Autocompleter.DWR(
													'field_ownername',
													'CodeList',
													AuthNames,
													{
														valueSelector : nameValueSelector,
														partialChars : 0
													});
										</script>
				
			</div>
			<div class="Section2">
				<label for="Organization" style="width: 88px">Organization<span
					class="form-required" title="This field is required.">*</span>:
				</label>
				<form:input path="field_organizationame" id="field_organizationame"
					name="Organization" type="text" />
			</div>
			<div class="Section2">

				<label for="Address" style="width: 88px;">Address<span
					class="form-required" title="This field is required.">*</span>:
				</label><br>
				<form:textarea path="field_owneraddress" name="Address" cols="58"
					rows="4" />
			</div>
			<div class="Section2">
				<label for="Phone" style="width: 88px">Phone<span
					class="form-required" title="This field is required.">*</span>:
				</label>
				<form:input path="field_telephonenumber" id="field_telephonenumber"
					name="Phone" type="text" />
			</div>
			<div class="Section2">
				<label for="Email" style="width: 88px">Email<span
					class="form-required" title="This field is required.">*</span>:
				</label>
				<form:input path="field_email" id="field_email" name="Email"
					type="text" />
			</div>
		</div>
	</div>
	<!--<li class="Section1 section"></li>-->
	<div class="Section1 section" id="Investigator2" style="display: none">
		<h4>Investigator #2:</h4>
		<div>
			<div class="Section2">
				<label for="Name2" style="width: 88px">Name: </label>
				<form:input path="field_ownername2" name="Name2" />
				<div id="CodeList2" class="auto_complete" onclick="CheckOrg2();"></div>
				<script>
											new Autocompleter.DWR(
													'field_ownername2',
													'CodeList2',
													AuthNames,
													{
														valueSelector : nameValueSelector,
														partialChars : 0
													});
										</script>
			</div>
			<div class="Section2">
				<label for="Organization2" style="width: 88px">Organization:</label>
				<form:input path="field_organizationame2" name="Organization2" />
			</div>
			<div class="Section2">
				Address:<br>
				<form:textarea path="field_owneraddress2" name="Address2" cols="58"
					rows="4" />
			</div>
			<div class="Section2">
				<label for="Phone2" style="width: 88px">Phone:</label>
				<form:input path="field_telephonenumber2" name="Phone2" />
			</div>
			<div class="Section2">
				<label for="Email2" style="width: 88px">Email:</label>
				<form:input path="field_email2" name="Email2" />
			</div>
		</div>
	</div>
	<div class="Section1 section" id="Investigator3" style="display: none">
		<h4>Investigator #3:</h4>
		<div>
			<div class="Section2">
				<label for="Name3" style="width: 88px">Name: </label>
				<form:input name="Name3" path="field_ownername3" />
				<div id="CodeList3" class="auto_complete" onclick="CheckOrg3();"></div>
				<script>
											new Autocompleter.DWR(
													'field_ownername3',
													'CodeList3',
													AuthNames,
													{
														valueSelector : nameValueSelector,
														partialChars : 0
													});
										</script>
			</div>
			<div class="Section2">
				<label for="Organization3" style="width: 88px">Organization:</label>
				<form:input path="field_organizationame3" name="Organization3" />
			</div>
			<div class="Section2">
				Address:<br>
				<form:textarea path="field_owneraddress3" name="Address3" cols="58"
					rows="4" />
			</div>
			<div class="Section2">
				<label for="Phone3" style="width: 88px">Phone:</label>
				<form:input path="field_telephonenumber3" name="Phone3" />
			</div>
			<div class="Section2">
				<label for="Email3" style="width: 88px">Email:</label>
				<form:input path="field_email3" name="Email3" />
			</div>
		</div>
	</div>
	<div class="Section1 section" id="Investigator4" style="display: none">
		<h4>Investigator #4:</h4>
		<div>
			<div class="Section2">
				<label style="width: 88px">Name: </label>
				<form:input name="Name4" path="field_ownername4" />
				<div id="CodeList4" class="auto_complete" onclick="CheckOrg4();"></div>
				<script>
											new Autocompleter.DWR(
													'field_ownername4',
													'CodeList4',
													AuthNames,
													{
														valueSelector : nameValueSelector,
														partialChars : 0
													});
										</script>
			</div>
			<div class="Section2">
				<label style="width: 88px">Organization:</label>
				<form:input path="field_organizationame4" name="Organization4" />
			</div>
			<div class="Section2">
				Address:<br>
				<form:textarea path="field_owneraddress4" name="Address4" cols="58"
					rows="4" />
			</div>
			<div class="Section2">
				<label style="width: 88px">Phone:</label>
				<form:input path="field_telephonenumber4" name="Phone4" />
			</div>
			<div class="Section2">
				<label style="width: 88px">Email:</label>
				<form:input path="field_email4" name="Email4" />
			</div>
		</div>
	</div>
	<div class="Section1 section" id="Investigator5" style="display: none">
		<h4>Investigator #5:</h4>
		<div>
			<div class="Section2">
				<label style="width: 88px">Name: </label>
				<form:input name="Name5" path="field_ownername5" />
				<div id="CodeList5" class="auto_complete" onclick="CheckOrg5();"></div>
				<script>
											new Autocompleter.DWR(
													'field_ownername5',
													'CodeList5',
													AuthNames,
													{
														valueSelector : nameValueSelector,
														partialChars : 0
													});
										</script>
			</div>
			<div class="Section2">
				<label style="width: 88px">Organization:</label>
				<form:input path="field_organizationame5" name="Organization5" />
			</div>
			<div class="Section2">
				Address:<br>
				<form:textarea path="field_owneraddress5" name="Address5" cols="58"
					rows="4" />
			</div>
			<div class="Section2">
				<label style="width: 88px">Phone:</label>
				<form:input path="field_telephonenumber5" name="Phone5" />
			</div>
			<div class="Section2">
				<label style="width: 88px">Email:</label>
				<form:input path="field_email5" name="Email5" />
			</div>
		</div>
	</div>
	<div>
		<label for="VarAdd"> <span style="font: normal"> Total
				Investigators in the Data Set: </span>
		</label> <select id="InvAddNumb" name="InvAddNumb" onchange="InvAdd()">
			<option value="2" selected="selected">1</option>
			<option value="3">2</option>
			<option value="4">3</option>
			<option value="5">4</option>
			<option value="6">5</option>
		</select>
	</div>
</div>