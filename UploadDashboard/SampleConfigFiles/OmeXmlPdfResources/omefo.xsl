<?xml version="1.0" encoding="UTF-8"?> 

<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format"
				xmlns:html="http://www.w3.org/1999/xhtml">

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/x_tags">

		<xsl:variable name="actexpocode">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Expocode" />
		</xsl:variable>
		<xsl:variable name="altexpocode">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Cruise_ID" />
		</xsl:variable>
		<xsl:variable name="expocode">
			<xsl:choose>
			<xsl:when test="$actexpocode != ''">
				<xsl:value-of select="$actexpocode" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$altexpocode" />
			</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="westlon">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Geographical_Coverage/Bounds/Westernmost_Longitude" />
		</xsl:variable>
		<xsl:variable name="eastlon">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Geographical_Coverage/Bounds/Easternmost_Longitude" />
		</xsl:variable>
		<xsl:variable name="northlat">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Geographical_Coverage/Bounds/Northernmost_Latitude" />
		</xsl:variable>
		<xsl:variable name="southlat">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Geographical_Coverage/Bounds/Southernmost_Latitude" />
		</xsl:variable>

		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

		<!-- defines the layout master -->
		<fo:layout-master-set>
			<fo:simple-page-master
					master-name="masterpage"
					page-height="11.0in"
					page-width="8.5in"
					margin-top="0.25in"
					margin-bottom="0.25in"
					margin-left="0.25in"
					margin-right="0.25in">
				<fo:region-body />
			</fo:simple-page-master>
		</fo:layout-master-set>

		<!-- starts actual layout -->
		<fo:page-sequence master-reference="masterpage">
		<fo:flow flow-name="xsl-region-body">

			<fo:table table-layout="fixed" width="100%">
				<fo:table-column column-width="1.75in"/>
				<fo:table-column column-width="6.25in"/>
				<fo:table-body>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Dataset Expocode </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> 
									<xsl:value-of select="$expocode" />
								</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Primary Contact </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Name: </fo:inline> 
								<xsl:value-of select="User/Name" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Organization: </fo:inline> 
								<xsl:value-of select="User/Organization" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Address: </fo:inline> 
								<xsl:value-of select="User/Address" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Phone: </fo:inline> 
								<xsl:value-of select="User/Phone" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Email: </fo:inline> 
								<xsl:value-of select="User/Email" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<xsl:for-each select="Investigator">
						<fo:table-row>
							<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
								<fo:block>
									<fo:inline font-weight="bold"> Investigator </fo:inline>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
								<fo:block>
									<fo:inline font-weight="bold"> Name: </fo:inline> 
									<xsl:value-of select="Name" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Organization: </fo:inline> 
									<xsl:value-of select="Organization" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Address: </fo:inline> 
									<xsl:value-of select="Address" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Phone: </fo:inline> 
									<xsl:value-of select="Phone" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Email: </fo:inline> 
									<xsl:value-of select="Email" />
								</fo:block>
								<fo:block>
									<fo:inline> </fo:inline> 
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>	

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Dataset </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Funding Info: </fo:inline> 
								<xsl:value-of select="Dataset_Info/Funding_Info" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Initial Submission (yyyymmdd): </fo:inline> 
								<xsl:value-of select="Dataset_Info/Submission_Dates/Initial_Submission" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Revised Submission (yyyymmdd): </fo:inline> 
								<xsl:value-of select="Dataset_Info/Submission_Dates/Revised_Submission" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Campaign/Cruise </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Expocode: </fo:inline> 
								<xsl:value-of select="$expocode" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Campaign/Cruise Name: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Experiment_Name" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Campaign/Cruise Info: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Cruise/Cruise_Info" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Platform Type: </fo:inline> 
								<xsl:value-of select="CruiseInfo/Experiment/Platform_Type" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> CO2 Instrument Type: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Co2_Instrument_type" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Survey Type: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Experiment_Type" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Vessel Name: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Vessel/Vessel_Name" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Vessel Owner: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Vessel/Vessel_Owner" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Vessel Code: </fo:inline> 
								<xsl:value-of select="Cruise_Info/Vessel/Vessel_ID" />
							</fo:block>
							<fo:block>
								<fo:inline> </fo:inline> 
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Coverage </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Start Date (yyyymmdd): </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Cruise/Temporal_Coverage/Start_Date" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> End Date (yyyymmdd): </fo:inline> 
								<xsl:value-of select="Cruise_Info/Experiment/Cruise/Temporal_Coverage/End_Date" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Westernmost Longitude: </fo:inline> 
								<xsl:choose>
									<xsl:when test="$westlon &lt; 0.0">
										<xsl:value-of select="0.0 - $westlon" /> W
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$westlon" /> E
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Easternmost Longitude: </fo:inline> 
								<xsl:choose>
									<xsl:when test="$eastlon &lt; 0.0">
										<xsl:value-of select="0.0 - $eastlon" /> W
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$eastlon" /> E
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Northernmost Latitude: </fo:inline> 
								<xsl:choose>
									<xsl:when test="$northlat &lt; 0.0">
										<xsl:value-of select="0.0 - $northlat" /> S
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$northlat" /> N
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Southernmost Latitude: </fo:inline> 
								<xsl:choose>
									<xsl:when test="$southlat &lt; 0.0">
										<xsl:value-of select="0.0 - $southlat" /> S
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$southlat" /> N
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
							<xsl:for-each select="Cruise_Info/Experiment/Cruise/Ports_of_Call">
								<fo:block>
									<fo:inline font-weight="bold"> Port of Call: </fo:inline> 
									<xsl:value-of select="." />
								</fo:block>
							</xsl:for-each>
						</fo:table-cell>
					</fo:table-row>

					<xsl:for-each select="Variables_Info/Variable">
						<fo:table-row>
							<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
								<fo:block>
									<fo:inline font-weight="bold"> Variable </fo:inline>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
								<fo:block>
									<fo:inline font-weight="bold"> Name: </fo:inline> 
									<xsl:value-of select="Variable_Name" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Unit: </fo:inline> 
									<xsl:value-of select="Unit_of_Variable" />
								</fo:block>
								<fo:block>
									<fo:inline font-weight="bold"> Description: </fo:inline> 
									<xsl:value-of select="Description_of_Variable" />
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Sea Surface Temperature </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Location: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Location" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Manufacturer: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Manufacturer" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Model: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Model" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Accuracy: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Accuracy" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Precision: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Precision" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Calibration: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Calibration" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Comments: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Other_Comments" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Sea Surface Salinity </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Location: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Location" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Manufacturer: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Manufacturer" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Model: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Model" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Accuracy: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Accuracy" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Precision: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Precision" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Calibration: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Calibration" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Comments: </fo:inline> 
								<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Other_Comments" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Atmospheric Pressure </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Location: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Location" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Normalized to Sea Level: </fo:inline> 
								<!-- misleading parent tag for this answer -->
								<xsl:value-of select="Method_Description/Equilibrator_Pressure/Normalized" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Manufacturer: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Manufacturer" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Model: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Model" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Accuracy: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Accuracy" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Precision: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Precision" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Calibration: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Calibration" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Comments: </fo:inline> 
								<xsl:value-of select="Method_Description/Atmospheric_Pressure/Other_Comments" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Atmospheric CO2 </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Measured/Frequency: </fo:inline> 
								<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Measurement" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Intake Location: </fo:inline> 
								<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Location_and_Height" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Drying Method: </fo:inline> 
								<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Drying_Method" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Atmospheric CO2 Accuracy: </fo:inline> 
								<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Uncertainty_Air" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Atmospheric CO2 Precision: </fo:inline> 
								<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Resolution_Air" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

					<fo:table-row>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> Aqueous CO2 Equilibrator Design </fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell border-width="6pt" border-style="solid" border-color="white">
							<fo:block>
								<fo:inline font-weight="bold"> System Manufacturer: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/System_Manufacturer_Description" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Intake Depth: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Depth_of_Sea_Water_Intake" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Intake Location: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Location_of_Sea_Water_Intake" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Equilibration Type: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Equilibrator_Type" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Equilibrator Volume (L): </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Equilibrator_Volume" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Headspace Gas Flow Rate (ml/min): </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Headspace_Gas_Flow_Rate" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Equilibrator Water Flow Rate (L/min): </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Water_Flow_Rate" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Equilibrator Vented: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Vented" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Equilibration Comments: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Additional_Information" />
							</fo:block>
							<fo:block>
								<fo:inline font-weight="bold"> Drying Method: </fo:inline> 
								<xsl:value-of select="Method_Description/Equilibrator_Design/Drying_Method_for_CO2_in_water" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>

				</fo:table-body>
			</fo:table>

	<!-- 

			<tr>
				<td style="vertical-align: top;">
					<b> Aqueous CO<sub>2</sub><br />
						Sensor Details</b>
				</td>
				<td>
					<b> Measurement Method: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Measurement_Method" />
					<br />
					<b> Method details: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Details_Co2_Sensing" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Model" />
					<br />
					<b> Measured CO<sub>2</sub> Values: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Measured_Co2_Params" />
					<br />
					<b> Measurement Frequency: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Frequency" />
					<br />
					<b> Aqueous CO<sub>2</sub> Accuracy: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Uncertainty_Water" />
					<br />
					<b> Aqueous CO<sub>2</sub> Precision: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Resolution_Water" />
					<br />
					<b> Sensor Calibrations: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Sensor_Calibration" />
					<br />
					<b> Calibration of Calibration Gases: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/CO2_Sensor_Calibration" />
					<br />
					<b> Number Non-Zero Gas Standards: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/No_Of_Non_Zero_Gas_Stds" />
					<br />
					<b> Calibration Gases: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Manufacturer_of_Calibration_Gas" />
					<br />
					<b> Comparison to Other CO<sub>2</sub> Analyses: </b>
					++
						Currently comments get mapped to Enviromental_Control and the value for 
						this field is not saved by the OME.  I am assuming that this field should
						be Environmental_Control and comments should go under Other_Comments.
					++
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Environmental_Control" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Other_Comments" />
					<br />
					<b> Method Reference: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Method_References" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Equilibrator Temperature Sensor </b>
				</td>
				<td>
					<b> Location: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Location" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Model" />
					<br />
					<b> Accuracy: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Accuracy" />
					<br />
					<b> Precision: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Precision" />
					<br />
					<b> Calibration: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Calibration" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Temperature/Other_Comments" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Equilibrator Pressure Sensor </b>
				</td>
				<td>
					<b> Location: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Location" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Model" />
					<br />
					<b> Accuracy: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Accuracy" />
					<br />
					<b> Precision: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Precision" />
					<br />
					<b> Calibration: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Calibration" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Other_Comments" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Other Sensor </b>
				</td>
				<td>
					<xsl:for-each select="Method_Description/Other_Sensors/Sensor">
						<b> Description: </b>
						<xsl:value-of select="Description" />
						<br />
						<b> Manufacturer: </b>
						<xsl:value-of select="Manufacturer" />
						<br />
						<b> Model: </b>
						<xsl:value-of select="Model" />
						<br />
						<b> Accuracy: </b>
						<xsl:value-of select="Accuracy" />
						<br />
						<b> Precision: </b>
						<xsl:value-of select="Precision" />
						<br />
						<b> Calibration: </b>
						<xsl:value-of select="Calibration" />
						<br />
						<b> Comments: </b>
						<xsl:value-of select="Other_Comments" />
						<br /><br />
					</xsl:for-each>
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Additional Information </b>
				</td>
				<td>
					<b> Suggested QC flag from Data Provider: </b>
					<xsl:value-of select="Preliminary_Quality_control" />
					<br />
					<b> Additional Comments: </b>
					<xsl:value-of select="Additional_Information" />
					<br />
					<b> Citation for this Dataset: </b>
					<xsl:value-of select="Citation" />
					<br />
					<b> Other References for this Dataset: </b>
					<xsl:value-of select="Data_set_References" />
					<br /><br />
				</td>
			</tr>
	 -->

			</fo:flow>
		</fo:page-sequence>
		</fo:root>

	</xsl:template>
</xsl:stylesheet>
