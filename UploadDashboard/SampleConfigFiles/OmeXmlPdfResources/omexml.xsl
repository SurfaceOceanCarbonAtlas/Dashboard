<?xml version="1.0" encoding="UTF-8"?> 

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="4.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/x_tags">
		<html>

		<xsl:variable name="expocode">
			<xsl:value-of select="Cruise_Info/Experiment/Cruise/Expocode" />
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

		<head>
		<title><xsl:value-of select="$expocode" /> OME Metadata</title>
		</head>

		<body>
		<table style="width: 100%">

			<tr>
				<td>
					<b> Dataset Expocode: </b>
					<br /><br />
				</td>
				<td style="vertical-align: top;">
					<xsl:value-of select="$expocode" />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b>Primary Contact</b>
				</td>
				<td>
					<b> Name: </b>
					<xsl:value-of select="User/Name" />
					<br />
					<b> Organization: </b>
					<xsl:value-of select="User/Organization" />
					<br />
					<b> Address: </b>
					<xsl:value-of select="User/Address" />
					<br />
					<b> Phone: </b>
					<xsl:value-of select="User/Phone" />
					<br />
				    <b> Email: </b>
					<xsl:value-of select="User/Email" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Investigator(s) </b>
				</td>
				<td>
					<xsl:for-each select="Investigator">
						<b> Name: </b>
						<xsl:value-of select="Name" />
						<br />
						<b> Organization: </b>
						<xsl:value-of select="Organization" />
						<br />
						<b> Address: </b>
						<xsl:value-of select="Address" />
						<br />
						<b> Phone: </b>
						<xsl:value-of select="Phone" />
						<br />
					    <b> Email: </b>
						<xsl:value-of select="Email" />
						<br /><br />
					</xsl:for-each>	
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Dataset </b>
				</td>
				<td>
					<b> Funding Info: </b>
					<xsl:value-of select="Dataset_Info/Funding_Info" />
					<br />
					<b> Initial Submission (yyyymmdd): </b>
					<xsl:value-of select="Dataset_Info/Submission_Dates/Initial_Submission" />
					<br />
					<b> Revised Submission (yyyymmdd): </b>				
					<xsl:value-of select="Dataset_Info/Submission_Dates/Revised_Submission" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Campaign/Cruise </b>
				</td>
				<td>
					<b> Expocode: </b>
					<xsl:value-of select="$expocode" />
					<br />
					<b> Campaign/Cruise Name: </b>
					<xsl:value-of select="Cruise_Info/Experiment/Experiment_Name" />
					<br />
					<b> Campaign/Cruise Info: </b>
					<xsl:value-of select="Cruise_Info/Experiment/Cruise/Cruise_Info" />
					<br />
					<b> Platform Type: </b>
					<xsl:value-of select="CruiseInfo/Experiment/Platform_Type" />
					<br />
					<b> CO<sub>2</sub> Instrument Type: </b>
					<xsl:value-of select="Cruise_Info/Experiment/Co2_Instrument_type" />
					<br />
					<b> Survey Type: </b>
					<xsl:value-of select="Cruise_Info/Experiment/Experiment_Type" />
					<br />
					<b> Vessel Name: </b>
					<xsl:value-of select="Cruise_Info/Vessel/Vessel_Name" />
					<br />
					<b> Vessel Owner: </b>
					<xsl:value-of select="Cruise_Info/Vessel/Vessel_Owner" />
					<br />
					<b> Vessel Code: </b>
					<xsl:value-of select="Cruise_Info/Vessel/Vessel_ID" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Coverage </b>
				</td>
				<td>
					<b> Start Date (yyyymmdd): </b>
					<xsl:value-of select="Cruise_Info/Experiment/Cruise/Temporal_Coverage/Start_Date" />
					<br />
					<b> End Date (yyyymmdd): </b>
					<xsl:value-of select="Cruise_Info/Experiment/Cruise/Temporal_Coverage/End_Date" />
					<br />
					<b> Westernmost Longitude: </b>
					<xsl:choose>
						<xsl:when test="$westlon &lt; 0.0">
							<xsl:value-of select="0.0 - $westlon" /> W
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$westlon" /> E
						</xsl:otherwise>
					</xsl:choose>
					<br />
					<b> Easternmost Longitude: </b>
					<xsl:choose>
						<xsl:when test="$eastlon &lt; 0.0">
							<xsl:value-of select="0.0 - $eastlon" /> W
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$eastlon" /> E
						</xsl:otherwise>
					</xsl:choose>
					<br />
					<b> Northernmost Latitude: </b>
					<xsl:choose>
						<xsl:when test="$northlat &lt; 0.0">
							<xsl:value-of select="0.0 - $northlat" /> S
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$northlat" /> N
						</xsl:otherwise>
					</xsl:choose>
					<br />
					<b> Southernmost Latitude: </b>
					<xsl:choose>
						<xsl:when test="$southlat &lt; 0.0">
							<xsl:value-of select="0.0 - $southlat" /> S
						</xsl:when>
						
						<xsl:otherwise>
							<xsl:value-of select="$southlat" /> N
						</xsl:otherwise>
					</xsl:choose>
					<br />
					<xsl:for-each select="Cruise_Info/Experiment/Cruise/Ports_of_Call">
						<b> Port of Call: </b>
						<xsl:value-of select="." />
						<br />
					</xsl:for-each>
					<br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Variables </b>
				</td>
				<td>
					<xsl:for-each select="Variables_Info/Variable">
						<b> Name: </b>
						<xsl:value-of select="Variable_Name" />
						<br />
						<b> Unit: </b>
						<xsl:value-of select="Unit_of_Variable" />
						<br />
						<b> Description: </b>
						<xsl:value-of select="Description_of_Variable" />
						<br /><br />
					</xsl:for-each>
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Sea Surface Temperature </b>
				</td>
				<td>
					<b> Location: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Location" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Model" />
					<br />
					<b> Accuracy: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Accuracy" />
					<br />
					<b> Precision: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Precision" />
					<br />
					<b> Calibration: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Calibration" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Temperature/Other_Comments" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Sea Surface Salinity </b>
				</td>
				<td>
					<b> Location: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Location" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Model" />
					<br />
					<b> Accuracy: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Accuracy" />
					<br />
					<b> Precision: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Precision" />
					<br />
					<b> Calibration: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Calibration" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/Sea_Surface_Salinity/Other_Comments" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Atmospheric Pressure </b>
				</td>
				<td>
					<b> Location: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Location" />
					<br />
					<b> Normalized to Sea Level: </b>
					<!--  misleading parent tag for this answer -->
					<xsl:value-of select="Method_Description/Equilibrator_Pressure/Normalized" />
					<br />
					<b> Manufacturer: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Manufacturer" />
					<br />
					<b> Model: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Model" />
					<br />
					<b> Accuracy: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Accuracy" />
					<br />
					<b> Precision: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Precision" />
					<br />
					<b> Calibration: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Calibration" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/Atmospheric_Pressure/Other_Comments" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Atmospheric CO<sub>2</sub> </b>
				</td>
				<td>
					<b> Measured/Frequency: </b>
					<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Measurement" />
					<br />
					<b> Intake Location: </b>
					<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Location_and_Height" />
					<br />
					<b> Drying Method: </b>
					<xsl:value-of select="Method_Description/CO2_in_Marine_Air/Drying_Method" />
					<br />
					<b> Atmospheric CO<sub>2</sub> Accuracy: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Uncertainty_Air" />
					<br />
					<b> Atmospheric CO<sub>2</sub> Precision: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Resolution_Air" />
					<br /><br />
				</td>
			</tr>

			<tr>
				<td style="vertical-align: top;">
					<b> Aqueous CO<sub>2</sub> <br />
						Equilibrator Design </b>
				</td>
				<td>
					<b> System Manufacturer: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/System_Manufacturer_Description" />
					<br />
					<b> Intake Depth: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Depth_of_Sea_Water_Intake" />
					<br />
					<b> Intake Location: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Location_of_Sea_Water_Intake" />
					<br />
					<b> Equilibration Type: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Equilibrator_Type" />
					<br />
					<b> Equilibrator Volume (L): </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Equilibrator_Volume" />
					<br />
					<b> Headspace Gas Flow Rate (ml/min): </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Headspace_Gas_Flow_Rate" />
					<br />
					<b> Equilibrator Water Flow Rate (L/min): </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Water_Flow_Rate" />
					<br />
					<b> Equilibrator Vented: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Vented" />
					<br />
					<b> Equilibration Comments: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Additional_Information" />
					<br />
					<b> Drying Method: </b>
					<xsl:value-of select="Method_Description/Equilibrator_Design/Drying_Method_for_CO2_in_water" />
					<br /><br />
				</td>
			</tr>

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
					<!--  don't know the value for this field; not saved by the OME -->
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Comparison" />
					<br />
					<b> Comments: </b>
					<xsl:value-of select="Method_Description/CO2_Sensors/CO2_Sensor/Environmental_Control" />
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

		</table>

		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
