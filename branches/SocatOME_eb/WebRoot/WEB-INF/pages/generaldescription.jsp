
<div id="generaldescription" class="Section1 section">
	<h4 class="alert alert-info">General Description:</h4>
	<ul>	


		<li class="Section1">Sea Surface Temperature (SST):
			<ul>
				<li class="Section2"><label style="width: 300px">Location
						and depth of SST sensor:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input id="field_SST_location" path="field_SST_location"/></li>
				<li class="Section2"><label style="width: 300px">Manufacturer
						SST sensor:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_SST_manufacturer"
						id="field_SST_manufacturer" /></li>
				<li class="Section2"><label style="width: 300px">Model
						SST sensor:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_SST_model" /></li>
				<li class="Section2"><label style="width: 300px">Accuracy
						SST in Degree Celsius :<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_SST_accuracy" type="number" step="any"/></li>
				<li class="Section2"><label style="width: 300px">Precision
						SST in Degree Celcius:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_SST_precision" type="number" step="any"/></li>
				<li class="Section2">Calibration of SST (document traceability
					to an internationally recognized scale, including dates and
					location of calibrations):<span class="form-required"
					title="This field is required.">*</span><br> <form:input
						path="field_SST_calibration" />
				</li>
				<li class="Section2">Additional comments on SST analysis: <br>
					<form:textarea path="field_SST_comments" id="" cols="66" rows="5" />
				</li>
			</ul>
		</li>
		<li class="Section1">Sea Surface Salinity (SSS):
			<ul>
				<li class="Section2"><label>Location
						and depth of SSS sensor:</label> <form:input path="field_SSS_sensor"
						id="field_SSS_sensor" /></li>
				<li class="Section2"><label>Manufacturer
						SSS sensor:</label> <form:input path="field_SSS_manufacturer"
						id="field_SSS_manufacturer" /></li>
				<li class="Section2"><label>Model
						SSS sensor:</label> <form:input path="field_SSS_model"
						id="field_SSS_model" /></li>
				<li class="Section2"><label>Accuracy
						SSS:</label> <form:input path="field_SSS_accuracy" id="field_SSS_accuracy" /></li>
				<li class="Section2"><label>Precision
						SSS:</label> <form:input path="field_SSS_precision"
						id="field_SSS_precision" /></li>
				<li class="Section2">Calibration of SSS (document traceability
					to an internationally recognized scale, including dates and
					location of calibrations):<br> <form:input
						path="field_SSS_calibration" id="field_SSS_calibration" />
				</li>
				<li class="Section2">Additional comments on SSS analysis: <br>
					<form:textarea path="field_SSS_comments" id="field_SSS_comments"
						cols="66" rows="5" />
				</li>
			</ul>
		</li>
		<li class="Section1">Atmospheric Pressure (not equilibrator pressure)(P<sub>atm</sub>):
			<ul>
				<li class="Section2"><label >Location
						and height of P<sub>atm</sub> sensor:<span
						class="form-required req-filter1" title="This field is required.">*</span>
				</label> <form:input path="field_Patm_sensor" id="field_Patm_sensor" /></li>

				<li class="Section2"><label>Atmospheric
						pressure has been normalized to and reported at sea level<span
						class="form-required req-filter1" title="This field is required.">*</span>
				</label> <form:select path="field_Patm_normalized"
						id="field_Patm_normalized">
						<form:option value="yes">yes</form:option>
						<form:option value="no">no</form:option>
					</form:select></li>

				<li class="Section2"><label>Manufacturer
						P<sub>atm</sub>:<span class="form-required req-filter1"
						title="This field is required.">*</span>
				</label> <form:input path="field_Patm_manufacturer"
						id="field_Patm_manufacturer" /></li>
				<li class="Section2"><label>Model
						P<sub>atm</sub>:<span class="form-required req-filter1"
						title="This field is required.">*</span>
				</label> <form:input path="field_Patm_model" id="field_Patm_model" /></li>
				<li class="Section2"><label>Accuracy
						P<sub>atm</sub> (specify unit):<span
						class="form-required req-filter1" title="This field is required.">*</span>
				</label> <form:input path="field_Patm_accuracy" id="field_Patm_accuracy" /></li>
				<li class="Section2"><label>Precision
						P<sub>atm</sub> (specify unit):<span
						class="form-required req-filter1" title="This field is required.">*</span>
				</label> <form:input path="field_Patm_precision" id="field_Patm_precision" /></li>
				<li class="Section2">Calibration of P<sub>atm</sub> (document
					traceability to an internationally recognized scale, including
					dates and location of calibrations):<span
					class="form-required req-filter1" title="This field is required.">*</span><br>
					<form:input path="field_Patm_calibration"
						id="field_Patm_calibration" /></li>
				<li class="Section2">Additional comments on P<sub>atm</sub>
					analysis:<br> <form:textarea path="field_Patm_comments"
						id="field_Patm_comments" cols="66" rows="5" /></li>
			</ul></li>

		<li class="Section1">CO<sub>2</sub> in marine air method:
			<ul>
				<li class="Section2"><label style="width: 450">Measurement
						of CO2 in marine air (yes/no) & frequency:</label> <form:input
						path="field_co2_measurement" name="field_co2_measurement" /></li>
				<li class="Section2"><label style="width: 450">Location
						and height of marine air intake:</label> <form:input
						path="field_marine_airtake" name="field_marine_airtake" /></li>
				<li class="Section2"><label style="width: 450">Drying
						method for CO2 in air and extent of drying:</label> <form:input
						path="field_co2_drying" name="c" /></li>

			</ul>
		</li>
	</ul>
</div>
