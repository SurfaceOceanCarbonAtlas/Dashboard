<div id="surfacewaterdescription" class="section">
	<h4 class="alert alert-info">Surface Water CO2 Method Description</h4>
	<ul>
		<li class="Section1"><label style="width: 350">System
				Manufacturer Description:<span class="form-required"
				title="This field is required.">*</span>
		</label> <form:input path="field_system_manufacturer"
				id="field_system_manufacturer" /></li>
		<li class="Section1">Sampling and Equilibrator Design:
			<ul>


				<li class="Section1"><label style="width: 350">Depth of
						Seawater Intake (m):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_depth_seawater_intake"
						id="field_depth_seawater_intake" /></li>
				<li class="Section2"><label style="width: 350">Location
						of Seawater Intake:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_location_seawater_intake"
						id="field_location_seawater_intake" /></li>
				<li class="Section2"><label for="Equilibrator_Type"
					style="width: 350">Type of equilibration (equilibrator
						design, membrane equilibration):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_equilibrator_type"
						name="Equilibration_Type" /></li>
				<li class="Section2"><label for="Equilibrator_Volume"
					style="width: 350">Equilibrator volume (L):<span
						class="form-required req-filter1" title="This field is required.">*</span></label>
					<form:input path="field_equilibration_volume"
						name="Equilibration_Volume" /></li>

				<li class="Section2"><label for="Headspace_Gas_Flow_Rate"
					style="width: 450">Headspace_Gas_Flow_Rate (mL/min):<span
						class="form-required req-filter1" title="This field is required.">*</span>
				</label> <form:input path="field_gas_flow_rate"
						name="Headspace_Gas_Flow_Rate" /></li>

				<li class="Section2"><label for="Water_Flow_Rate"
					style="width: 350">Equilibrator Water Flow (L/min):<span
						class="form-required" title="This field is required.">*</span></label> <form:input
						path="field_water_flow_rate" name="Water_Flow_Rate" />
				<li class="Section2">Equilibrator Vented:<span
					class="form-required req-filter1" title="This field is required.">*</span>
					<form:select path="field_vented">
						<form:option value="Yes">Yes</form:option>
						<form:option value="No">No</form:option>
					</form:select>
				</li>

				<li class="Section2"><label style="width: 450">Additional
						comments on equilibration:</label><br /> <form:textarea
						path="field_equilibrium_comments"
						name="field_equilibrium_comments" cols="66" rows="3" /></li>

				<li class="Section2"><label style="width: 450">Drying
						method for CO2 in water and extent of drying(>99% dry, partial,
						non-dried etc.):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_drying_method" name="field_drying_method" /></li>
			</ul>
		</li>
		<li class="Section1">CO<sub>2</sub> Sensors:
			<ul>
				<li class="Section2"><label style="width: 450">Measurement
						Method CO<sub>2</sub>:<span class="form-required"
						title="This field is required.">*</span>
				</label> <select type="text" class="select2">
						<option value="Self-propelled surface platform">IR</option>
						<option value="Ship">Spectrophotometric</option>
						<option value="Mooring">CRDS</option>
						<option value="Surface Drifter">GC</option>
						<option value="Others">Others</option>
				</select> <form:input class="short" id="field_measurement_method"
						style="display:none" placeHolder="Describe Others Here"
						path="field_measurement_method" />
				<li class="Section2"><label>Details of CO<sub>2</sub>
						sensing (description of the CO2 measurement, e.g. dyes,
						wavelengths, antifouling)<span class="form-required req-filter2"
						title="This field is required.">*</span>
				</label> <form:input path="field_detail_sensing" id="field_detail_sensing" /></li>
				<li class="Section2"><label style="width: 450">Manufacturer
						of CO<sub>2</sub> sensor:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_manufacturer" id="field_manufacturer" /></li>
				<li class="Section2"><label style="width: 450">Model of
						CO<sub>2</sub> sensor:<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_model" name="Model" /></li>



				<li class="Section2"><label>Measured CO2 parameter,
						e.g. xCO2(dry), xCO2(wet), pCO2(dry), pCO2(wet), fCO2(dry),
						fCO2(wet):<span class="form-required"
						title="This field is required.">*</span>
				</label></br> <form:input path="field_measured_co2_params"
						id="field_measured_co2_params" /></li>

				<li class="Section2"><label>Frequency of CO2
						measurements (e.g. Every 120 sec, except during calibration
						routines):<span class="form-required"
						title="This field is required.">*</span>
				</label></br> <form:input path="field_frequency" id="field_frequency" /></li>

				<li class="Section2"><label style="width: 450">Accuracy
						of CO<sub>2</sub>water (specify parameter and unit):<span
						class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_uncertainity" id="field_uncertainity" /></li>
				<li class="Section2"><label style="width: 450">Precision
						of CO<sub>2</sub>water (specify parameter and unit):<span
						class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_resolution" id="field_resolution" /></li>
				<li class="Section2"><label for="Uncertainty"
					style="width: 450">Accuracy of CO<sub>2</sub>air (specify
						parameter and unit):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_uncertainity_air"
						id="field_uncertainity_air" /></li>


				<li class="Section2"><label style="width: 450">Precision
						of CO<sub>2</sub>air (specify parameter and unit):<span
						class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_resolution_air" id="field_resolution_air" /></li>

				<li class="Section2"><label style="width: 450">Sensor
						calibration pre-deployment, during the deployment and
						post-deployment (detailed description of calibration methods, e.g.
						date, location (before transport or in situ), zero point
						correction?): <span class="form-required"
						title="This field is required.">*</span>
				</label> <form:textarea path="field_sensor_calibration"
						id="field_sensor_calibration" cols="66" rows="2" /></li>

				<li class="Section2"><label>Calibration of CO<sub>2</sub>
						calibration gases (document traceability):<span
						class="form-required" title="This field is required.">*</span><br>
				</label> <select type="text" class="select2">
						<option value="Ship">NOAA</option>
						<option value="Mooring">WMO</option>
						<option value="Others">Others</option>
				</select> <form:input class="short" id="field_calibration"
						style="display:none" placeHolder="Describe Others Here"
						path="field_calibration" />
						
						
				<li class="Section2"><label>Number of non-zero gas
						standards:<br>
				</label> <form:input path="field_no_of_non_zero_gas_stds"
						id="field_no_of_non_zero_gas_stds" type="number" step="" cols="66"
						rows="2" /></li>


				<li class="Section2"><label>CO<sub>2</sub> calibration
						gases (manufacturer, number and approximate mixing ratio of CO<sub>2</sub>
						standards, and frequency of calibration):<span
						class="form-required" title="This field is required.">*</span>
				</label><br> <form:textarea path="field_manufacturer_calibration"
						name="Manufacturer_of_Calibration_Gas" cols="66" rows="5" /></li>


				<li class="Section2"><label>Outcome of any comparison
						to independent CO2 analysis or fCO2 calculated from other
						carbonate parameters:<br>
				</label> <form:textarea path="field_comparision_co2_analysis"
						id="field_comparision_co2_analysis" cols="66" rows="5" /></li>


				<li class="Section2">Additional comments on CO<sub>2</sub>
					analysis:<br> <form:textarea
						path="field_environmental_control" name="Environmental_Control"
						cols="66" rows="5" /></li>

				<li class="Section2">CO<sub>2</sub> method references
					(publications describing method):<span
					class="form-required req-filter2" title="This field is required.">*</span><br>
					<form:textarea path="field_method_references"
						name="Environmental_Control" cols="66" rows="5" /></li>
			</ul>
		</li>

		<li class="Section1">Temperature at place of equilibration (T<sub>equ</sub>):
			<ul>
				<li class="Section2"><label>Way of T<sub>equ</sub>
						measurement, Location of T<sub>equ</sub> sensor:<span
						class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Tequ_location" id="field_Tequ_location" /></li>
				<li class="Section2"><label>Manufacturer T<sub>equ</sub>
						sensor:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Tequ_manufacturer"
						id="field_Tequ_manufacturer" /></li>
				<li class="Section2"><label>Model T<sub>equ</sub>
						sensor:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Tequ_model" id="field_Tequ_model" /></li>
				<li class="Section2"><label>Accuracy T<sub>equ</sub>(degrees
						Celsius):<span class="form-required"
						title="This field is required.">*</span>s
				</label> <form:input path="field_Tequ_accuracy" id="field_Tequ_accuracy"
						type="number" step="any" /></li>
				<li class="Section2"><label>Precision T<sub>equ</sub>(degrees
						Celsius):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_Tequ_precision" id="field_Tequ_precision"
						type="number" step="any" /></li>
				<li class="Section2">Calibration of T<sub>equ</sub> (document
					traceability to an internationally recognized scale, including
					dates and location of calibrations):<span class="form-required"
					title="This field is required.">*</span></br> <form:input
						path="field_Tequ_calibration" id="field_Tequ_calibration" /></li>
				<%-- <li class="Section2">Average warming of water from seawater
									inlet to equilibrator with standard deviation:</br> <form:input
										path="field_Tequ_warming" id="field_Tequ_warming"  /></li>--%>

				<li class="Section2">Additional comments on T<sub>equ</sub>
					analysis:</br> <form:textarea path="field_Tequ_comments"
						id="field_Tequ_comments" cols="66" rows="5" /></li>
			</ul>
		</li>
		<li class="Section1">Equilibrator Pressure (P<sub>equ</sub>):
			<ul>
				<li class="Section2"><label>Location of P<sub>equ</sub>
						sensor:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Pequ_sensor" id="field_Pequ_sensor" /></li>
				<li class="Section2"><label>Manufacturer P<sub>equ</sub>
						sensor:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Pequ_manufacturer"
						id="field_Pequ_manufacturer" /></li>
				<li class="Section2"><label>Model P<sub>equ</sub>
						sensor:<span class="form-required" title="This field is required.">*</span>
				</label> <form:input path="field_Pequ_model" id="field_Pequ_model" /></li>
				<li class="Section2"><label>Accuracy P<sub>equ</sub>
						(in hPa):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_Pequ_accuracy" id="field_Pequ_accuracy"
						type="number" step="any" /></li>
				<li class="Section2"><label>Precision P<sub>equ</sub>
						(in hPa):<span class="form-required"
						title="This field is required.">*</span>
				</label> <form:input path="field_Pequ_precision" id="field_Pequ_precision"
						type="number" step="any" /></li>
				<li class="Section2">Calibration of P<sub>equ</sub> (document
					traceability to an internationally recognized scale, including
					dates and location of calibrations):<span class="form-required"
					title="This field is required.">*</span></br> <form:input
						path="field_Pequ_calibration" id="field_Pequ_calibration" /></li>
				<li class="Section2">Additional comments on P<sub>equ</sub>
					analysis:</br> <form:textarea path="field_Pequ_comments"
						id="field_Pequ_comments" cols="66" rows="5" /></li>
			</ul></li>

		<li class="Section1">Other Sensors # 1:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption" id="field_sensor_desciption" /></li>

				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other" name="Manufacturer_other"
						id="field_manufaturer_other" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other" name="field_accuracy_other" /></li>
				<li class="Section2"><label for="Model_other"
					style="width: 150">Model:</label> <form:input
						path="field_model_other" name="Model_other" /></li>
				<li class="Section2"><label for="Resolution_other"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other" name="Resolution_other" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other" name="Calibration_other" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other" id="field_comment_other" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor0" style="display: none">Other
			Sensors #2:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption1" id="field_sensor_desciption1" /></li>

				<li class="Section2"><label for="Manufacturer_other1"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other1" name="Manufacturer_other1"
						id="field_manufaturer_other1" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other1"
						name="field_accuracy_other1" /></li>
				<li class="Section2"><label for="Model_other1"
					style="width: 150">Model:</label> <form:input
						path="field_model_other1" name="Model_other1" /></li>
				<li class="Section2"><label for="Resolution_other1"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other1" name="Resolution_other1" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other1" name="Calibration_other1" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other1" id="field_comment_other1" cols="66"
						rows="5" /></li>
			</ul>
		</li>
		<li class="Section1" id="sensor1" style="display: none">Other
			Sensors #3:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption2" id="field_sensor_desciption2" /></li>

				<li class="Section2"><label for="Manufacturer_other2"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other2" name="Manufacturer_other2"
						id="field_manufaturer_other2" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other2"
						name="field_accuracy_other2" /></li>
				<li class="Section2"><label for="Model_other2"
					style="width: 150">Model:</label> <form:input
						path="field_model_other2" name="Model_other2" /></li>
				<li class="Section2"><label for="Resolution_other2"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other2" name="Resolution_other2" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other2" name="Calibration_other2" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other2" id="field_comment_other2" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor2" style="display: none">Other
			Sensors #4:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption3" id="field_sensor_desciption3" /></li>

				<li class="Section2"><label for="Manufacturer_other3"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other3" name="Manufacturer_other3"
						id="field_manufaturer_other3" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other3"
						name="field_accuracy_other3" /></li>
				<li class="Section2"><label for="Model_other3"
					style="width: 150">Model:</label> <form:input
						path="field_model_other3" name="Model_other3" /></li>
				<li class="Section2"><label for="Resolution_other3"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other3" name="Resolution_other3" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other3" name="Calibration_other3" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other3" id="field_comment_other3" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor3" style="display: none">Other
			Sensors #5:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption4" id="field_sensor_desciption4" /></li>

				<li class="Section2"><label for="Manufacturer_other4"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other4" name="Manufacturer_other4"
						id="field_manufaturer_other4" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other4"
						name="field_accuracy_other4" /></li>
				<li class="Section2"><label for="Model_other4"
					style="width: 150">Model:</label> <form:input
						path="field_model_other4" name="Model_other4" /></li>
				<li class="Section2"><label for="Resolution_other4"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other4" name="Resolution_other4" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other4" name="Calibration_other4" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other4" id="field_comment_other4" cols="66"
						rows="5" /></li>
			</ul>
		</li>


		<li class="Section1" id="sensor4" style="display: none">Other
			Sensors #6:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption5" id="field_sensor_desciption5" /></li>

				<li class="Section2"><label for="Manufacturer_other5"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other5" name="Manufacturer_other5"
						id="field_manufaturer_other5" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other5"
						name="field_accuracy_other5" /></li>
				<li class="Section2"><label for="Model_other5"
					style="width: 150">Model:</label> <form:input
						path="field_model_other5" name="Model_other5" /></li>
				<li class="Section2"><label for="Resolution_other5"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other5" name="Resolution_other5" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other5" name="Calibration_other5" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other5" id="field_comment_other5" cols="66"
						rows="5" /></li>
			</ul>
		</li>
		<li class="Section1" id="sensor5" style="display: none">Other
			Sensors #7:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption6" id="field_sensor_desciption6" /></li>

				<li class="Section2"><label for="Manufacturer_other6"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other6" name="Manufacturer_other6"
						id="field_manufaturer_other6" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other6"
						name="field_accuracy_other6" /></li>
				<li class="Section2"><label for="Model_other6"
					style="width: 150">Model:</label> <form:input
						path="field_model_other6" name="Model_other6" /></li>
				<li class="Section2"><label for="Resolution_other6"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other6" name="Resolution_other6" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other6" name="Calibration_other6" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other6" id="field_comment_other6" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor6" style="display: none">Other
			Sensors #8:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption7" id="field_sensor_desciption7" /></li>
				<li class="Section2"><label for="Manufacturer_other7"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other7" name="Manufacturer_other7"
						id="field_manufaturer_other7" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other7"
						name="field_accuracy_other7" /></li>
				<li class="Section2"><label for="Model_other7"
					style="width: 150">Model:</label> <form:input
						path="field_model_other7" name="Model_other7" /></li>
				<li class="Section2"><label for="Resolution_other7"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other7" name="Resolution_other7" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other7" name="Calibration_other7" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other7" id="field_comment_other7" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor7" style="display: none">Other
			Sensors #9:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption8" id="field_sensor_desciption8" /></li>
				<li class="Section2"><label for="Manufacturer_other8"
					id="field_manufaturer_other8" style="width: 150">Manufacturer:</label>
					<form:input path="field_manufaturer_other8"
						name="Manufacturer_other8" id="field_manufaturer_other8" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other8"
						name="field_accuracy_other8" /></li>
				<li class="Section2"><label for="Model_other8"
					style="width: 150">Model:</label> <form:input
						path="field_model_other8" name="Model_other8" /></li>
				<li class="Section2"><label for="Resolution_other8"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other8" name="Resolution_other8" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other8" name="Calibration_other8" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other8" id="field_comment_other8" cols="66"
						rows="5" /></li>
			</ul>
		</li>

		<li class="Section1" id="sensor8" style="display: none">Other
			Sensors #10:
			<ul>
				<li class="Section2"><label for="Manufacturer_other"
					style="width: 150">Description:</label> <form:input
						path="field_sensor_desciption9" id="field_sensor_desciption9" /></li>
				<li class="Section2"><label for="Manufacturer_other9"
					style="width: 150">Manufacturer:</label> <form:input
						path="field_manufaturer_other9" id="field_manufaturer_other9"
						name="Manufacturer_other9" /></li>
				<li class="Section2"><label style="width: 150">Accuracy:</label>
					<form:input path="field_accuracy_other9"
						name="field_accuracy_other9" /></li>
				<li class="Section2"><label for="Model_other9"
					style="width: 150">Model:</label> <form:input
						path="field_model_other9" name="Model_other9" /></li>
				<li class="Section2"><label for="Resolution_other9"
					style="width: 150">Precision:</label> <form:input
						path="field_resolution_other9" name="Resolution_other9" /></li>
				<li class="Section2">Calibration (For each sensor of pressure,
					temperature, and salinity, document traceability to an
					internationally recognized scale, including date and place of the
					last calibration):<br> <form:input
						path="field_calibration_other9" name="Calibration_other9" />
				</li>
				<li>Additional Information:</br> <form:textarea
						path="field_comment_other9" id="field_comment_other9" cols="66"
						rows="5" /></li>
			</ul>
		</li>


	</ul>
	<ul>
		<li><label for="SensorAdd"> <span style="font: normal">
					Add More Sensors: </span>
		</label> <select id="SensorAddNumb" onchange="SensorAdd()"
			name="SensorAddNumb">
				<option selected="selected" value="0">1</option>
				<option value="1">2</option>
				<option value="2">3</option>
				<option value="3">4</option>
				<option value="4">5</option>
				<option value="5">6</option>
				<option value="6">7</option>
				<option value="7">8</option>
				<option value="8">9</option>
				<option value="9">10</option>
		</select></li>
	</ul>
</div>
