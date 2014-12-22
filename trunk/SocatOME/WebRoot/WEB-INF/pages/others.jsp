<div id="others" class="section">
	<div class="Section2 section">
		<h4 class="alert alert-info">Preliminary quality control</h4>
		<div class="Section2">
			<label for="SensorAdd"> <span style="font: normal">
					Preliminary cruise flag by data provider:<span
					class="form-required" title="This field is required.">*</span>
			</span>
			</label>

			<form:select path="field_preliminary_quality_control">
				<form:option selected="selected" value="NA">NA</form:option>
				<form:option value="NB">NB</form:option>
				<form:option value="NC">NC</form:option>
				<form:option value="ND">ND</form:option>
				<form:option value="NE">NE</form:option>
				<form:option value="NF">NF</form:option>
			</form:select>
		</div>
		<br />
	</div>

	<div class="Section2 section">
		<h4 class="alert alert-info">Additional Information:</h4>
		<form:textarea path="field_additional_info" name="Data_set_add_info"
			cols="60" rows="5" />
	</div>
	<div class="Section2 section">
		<h4 class="alert alert-info">Data Set References: (Publication(s)
			describing data set)</h4>
		<form:textarea path="field_data_set_references"
			name="Data_set_References" cols="60" rows="5" />
	</div>
	<div class="Section2 section">
		<h4 class="alert alert-info">Citation: (How to cite this data
			set)</h4>
		<form:textarea path="field_citation" name="Citation" cols="60"
			rows="5" />
	</div>
	
</div>
