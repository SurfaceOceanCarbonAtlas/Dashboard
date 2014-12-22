
<div id="cruiseinfo" class="Section1 section">
	<h4 class="alert alert-info">Cruise Info:</h4>
	<ul>

		<li class="section1">Cruise Information for the Expocode:
			<ul>
				<li class="Section2"><span style="width: 300px">NODC
						Platform (Ship) Code List:<span class="form-required"
						title="This field is required.">*</span>
				</span> <form:input path="field_vessel_id" name="Vessel_ID" size="2"
						MAXLENGTH="4" class="short" onkeyup="displayExpo()" /> <span><a class="fancy"
					href="http://www.nodc.noaa.gov/General/NODC-Archive/platformlist.txt">4
						character NODC</a></li></span>
				<li class="Section2"><span style="width: 350px">Start
						Date (cruise or data set, for Expocode)<span class="form-required"
						title="This field is required.">*</span>:
				</span> <form:input path="field_start_date" id="field_start_date"
						name="Start_Date" size="9" class="short" onchange="displayExpo()" /><span class="description">(yyyymmdd)</span>
					<form:input path="field_start_date_dup" id="field_start_date_dup"
						type="hidden" /></li>
				<li class="Section2"><span style="width: 300px">Cruise
						Information for the expocode:</span> <form:input path="field_cruise_id"
						style="background:#D3D3D3;" id="field_cruise_id"  class="short"
						onfocus="blur()" type="text" /></li>

			</ul>
		</li>
		<li>Other Cruise Info:
			<ul>
				<li class="Section2">Platform Type:<span class="form-required"
					title="This field is required.">*</span></br> <select type="text" 
						class="select2">
						<option value="Self-propelled surface platform">Self-propelled surface platform</option>
						<option value="Ship">Ship</option>
						<option value="Mooring">Mooring</option>
						<option value="Surface Drifter">Surface Drifter</option>						
						<option value="Others">Others</option>
					</select>
				
				   <form:input class="short" id="field_platform_type" style="display:none" placeHolder="Describe Others Here" path="field_platform_type"/>
					
				</li>


				<li class="Section2">CO<sub>2</sub> Instrument Type <span
					class="form-required" title="This field is required.">*</span>:</br> <select type="text" id="select_co2_instr_type"
						class="select2"	onchange="filterReq()">
						<option value="Equilibrator-IR or CRDS or GC">Equilibrator-IR or CRDS or GC</option>
						<option value="Membrane-IR">Membrane-IR</option>
						<option value="Spectrophotometry">Spectrophotometry</option>
						<option value="Others">Others</option>
					</select>
					<form:input class="short" id="field_co2_instr_type" style="display:none" placeHolder="Describe Others Here" path="field_co2_instr_type"/>
				</li>
				<li class="Section2">Survey type (e.g. VOS Lines, Research
					Cruise, Moored Buoy, Drifting Buoy):</br> <form:input
						path="field_survey_type" name="survey_type" />
				</li>
			</ul>
		</li>

		<li class="Section2"><label for="Vessel_Name">Vessel
				Name:<span class="form-required" title="This field is required.">*</span>
		</label> </br> <form:input path="field_vessel_name" name="Vessel_Name" /></li>
		<li class="Section2"><label for="Vessel_Owner" style="width: 150">Vessel
				Owner: </label> <form:input path="field_vessel_owner" name="Vessel_Owner" /></li>

		<li class="Section2"><label for="Cruise_Info">Campaign
				Info/Cruise Info (e.g. SAVE, TTO-NAS, SOIREE, AMT08, Antares,
				EisenEx):</label></br> <form:input path="field_cruise_info" name="Cruise_Info"
				type="text" /></li>

		<li class="Section2"><label for="Section">Campaign
				name/Cruise name(including Leg) (E.g. ANTV-2, Biscay_979815C, D198)<span
				class="form-required" title="This field is required.">*</span><span
				class="form-required" title="This field is required.">*</span>:
		</label></br> <form:input path="field_experiment_name" name="field_experiment_name"
				type="text" /></li>

		<li class="Section2"><label for="End_Date">End Date:</label> <form:input
				path="field_end_date" id="field_end_date" class="short" size="9" />
			<span class="description">(yyyymmdd)</span> <form:input path="field_end_date_dup"
				id="field_end_date_dup" type="hidden" /></li>

		<li class="Section2">Ports of Call: (One per line)<br> <form:textarea
				path="field_port_of_call" name="Ports_of_Call" cols="63" rows="3" /></li>
		<%--  <li class="Section2">Mooring ID if applicable:</br> <form:input
								path="field_mooring_id" name="mooring_id"  />--%>

		</li>
		<li class="Section1">Geographical Coverage:
			<ul>				
				<li class="Section1">Bounds:
					<ul>
						<li class="Section2">Westernmost Longitude:<br> Enter
							decimal fractions of degrees: <form:input path="field_west"
								class="short" name="Westernmost_Longitude" size="9" /><span class="description">(+ = E, - = W)</span> 
						</li>
						<li class="Section2">Easternmost Longitude:<br> Enter
							decimal fractions of degrees: <form:input path="field_east"
								class="short" name="Easternmost_Longitude" size="9" /><span class="description">(+ = E, - = W)</span>
						</li>
						<li class="Section2">Northernmost Latitude:<br> Enter
							decimal fractions of degrees: <form:input path="field_north"
								class="short" name="Northernmost_Latitude" size="8" /><span class="description">(+ = N, - = S)</span>
						</li>
						<li class="Section2">Southernmost Latitude:<br> Enter
							decimal fractions of degrees: <form:input path="field_south"
								class="short" name="Southernmost_Latitude" size="8" /><span class="description">(+ = N, - = S)</span>
						</li>
					</ul>
				</li>
			</ul>
		</li>
	</ul>

</div>
