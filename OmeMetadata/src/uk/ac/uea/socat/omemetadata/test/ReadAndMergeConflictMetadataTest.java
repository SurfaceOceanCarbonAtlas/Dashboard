/**
 * 
 */
package uk.ac.uea.socat.omemetadata.test;

import static org.junit.Assert.*;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Test;

import uk.ac.uea.socat.omemetadata.BadEntryNameException;
import uk.ac.uea.socat.omemetadata.InvalidConflictException;
import uk.ac.uea.socat.omemetadata.OmeMetadata;

/**
 * @author Karl Smith
 */
public class ReadAndMergeConflictMetadataTest {

	private static final String TEST_EXPOCODE = "Z2HQ20110517";

	private static final String FIRST_XML_STRING = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
					"<x_tags>\r\n" +
					"  <status>draft</status>\r\n" +
					"  <User>\r\n" +
					"    <Name>Karl Smith</Name>\r\n" +
					"    <Organization>NOAA/PMEL</Organization>\r\n" +
					"    <Address />\r\n" +
					"    <Phone />\r\n" +
					"    <Email>karl.smith@noaa.gov</Email>\r\n" +
					"  </User>\r\n" +
					"  <Investigator>\r\n" +
					"    <Name>Takahashi, T.</Name>\r\n" +
					"    <Organization>not NOAA</Organization>\r\n" +
					"  </Investigator>\r\n" +
					"  <Dataset_Info>\r\n" +
					"    <Dataset_ID>Z2HQ20110517</Dataset_ID>\r\n" +
					"    <Funding_Info />\r\n" +
					"    <Submission_Dates>\r\n" +
					"      <Initial_Submission />\r\n" +
					"      <Revised_Submission />\r\n" +
					"    </Submission_Dates>\r\n" +
					"  </Dataset_Info>\r\n" +
					"  <Cruise_Info>\r\n" +
					"    <Experiment>\r\n" +
					"      <Experiment_Name>Healy_11_01</Experiment_Name>\r\n" +
					"      <Experiment_Type />\r\n" +
					"      <Platform_Type />\r\n" +
					"      <Co2_Instrument_type />\r\n" +
					"      <Mooring_ID />\r\n" +
					"      <Cruise>\r\n" +
					"        <Cruise_ID>Z2HQ20110517</Cruise_ID>\r\n" +
					"        <Cruise_Info />\r\n" +
					"        <Section />\r\n" +
					"        <Geographical_Coverage>\r\n" +
					"          <Geographical_Region />\r\n" +
					"          <Bounds>\r\n" +
					"            <Westernmost_Longitude>201.798</Westernmost_Longitude>\r\n" +
					"            <Easternmost_Longitude>237.572</Easternmost_Longitude>\r\n" +
					"            <Northernmost_Latitude>48.515</Northernmost_Latitude>\r\n" +
					"            <Southernmost_Latitude>21.12</Southernmost_Latitude>\r\n" +
					"          </Bounds>\r\n" +
					"        </Geographical_Coverage>\r\n" +
					"        <Temporal_Coverage>\r\n" +
					"          <Start_Date>20110527</Start_Date>\r\n" +
					"          <End_Date>20110604</End_Date>\r\n" +
					"        </Temporal_Coverage>\r\n" +
					"        <Start_Date>20110517</Start_Date>\r\n" +
					"        <End_Date />\r\n" +
					"      </Cruise>\r\n" +
					"    </Experiment>\r\n" +
					"    <Vessel>\r\n" +
					"      <Vessel_Name>Healy</Vessel_Name>\r\n" +
					"      <Vessel_ID>Z2HQ</Vessel_ID>\r\n" +
					"      <Country />\r\n" +
					"      <Vessel_Owner />\r\n" +
					"    </Vessel>\r\n" +
					"  </Cruise_Info>\r\n" +
					"  <Variables_Info />\r\n" +
					"  <CO2_Data_Info>\r\n" +
					"    <xCO2water_equ_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2water_equ_dry>\r\n" +
					"    <xCO2water_SST_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2water_SST_dry>\r\n" +
					"    <pCO2water_equ_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2water_equ_wet>\r\n" +
					"    <pCO2water_SST_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2water_SST_wet>\r\n" +
					"    <fCO2water_equ_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2water_equ_wet>\r\n" +
					"    <fCO2water_SST_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2water_SST_wet>\r\n" +
					"    <xCO2air_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2air_dry>\r\n" +
					"    <pCO2air_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2air_wet>\r\n" +
					"    <fCO2air_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2air_wet>\r\n" +
					"    <xCO2air_dry_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2air_dry_interpolated>\r\n" +
					"    <pCO2air_wet_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2air_wet_interpolated>\r\n" +
					"    <fCO2air_wet_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2air_wet_interpolated>\r\n" +
					"  </CO2_Data_Info>\r\n" +
					"  <Method_Description>\r\n" +
					"    <Equilibrator_Design>\r\n" +
					"      <Depth_of_Sea_Water_Intake />\r\n" +
					"      <Location_of_Sea_Water_Intake />\r\n" +
					"      <Equilibrator_Type />\r\n" +
					"      <Equilibrator_Volume />\r\n" +
					"      <Water_Flow_Rate />\r\n" +
					"      <Headspace_Gas_Flow_Rate />\r\n" +
					"      <Vented>Yes</Vented>\r\n" +
					"      <Drying_Method_for_CO2_in_water />\r\n" +
					"      <Additional_Information />\r\n" +
					"    </Equilibrator_Design>\r\n" +
					"    <CO2_in_Marine_Air>\r\n" +
					"      <Measurement />\r\n" +
					"      <Location_and_Height />\r\n" +
					"      <Drying_Method />\r\n" +
					"    </CO2_in_Marine_Air>\r\n" +
					"    <CO2_Sensors>\r\n" +
					"      <CO2_Sensor>\r\n" +
					"        <Measurement_Method />\r\n" +
					"        <Manufacturer />\r\n" +
					"        <Model />\r\n" +
					"        <Frequency />\r\n" +
					"        <Resolution_Water />\r\n" +
					"        <Uncertainty_Water />\r\n" +
					"        <Resolution_Air />\r\n" +
					"        <Uncertainty_Air />\r\n" +
					"        <Manufacturer_of_Calibration_Gas />\r\n" +
					"        <CO2_Sensor_Calibration />\r\n" +
					"        <Environmental_Control />\r\n" +
					"        <Method_References />\r\n" +
					"        <Details_Co2_Sensing />\r\n" +
					"        <Analysis_of_Co2_Comparision />\r\n" +
					"        <Measured_Co2_Params />\r\n" +
					"      </CO2_Sensor>\r\n" +
					"    </CO2_Sensors>\r\n" +
					"    <Sea_Surface_Temperature>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Sea_Surface_Temperature>\r\n" +
					"    <Equilibrator_Temperature>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Warming />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Equilibrator_Temperature>\r\n" +
					"    <Equilibrator_Pressure>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"      <Normalized>yes</Normalized>\r\n" +
					"    </Equilibrator_Pressure>\r\n" +
					"    <Atmospheric_Pressure>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Atmospheric_Pressure>\r\n" +
					"    <Sea_Surface_Salinity>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Sea_Surface_Salinity>\r\n" +
					"    <Other_Sensors />\r\n" +
					"  </Method_Description>\r\n" +
					"  <Data_set_References />\r\n" +
					"  <Additional_Information />\r\n" +
					"  <Citation />\r\n" +
					"  <Measurement_and_Calibration_Report />\r\n" +
					"  <Preliminary_Quality_control>NA</Preliminary_Quality_control>\r\n" +
					"  <form_type>underway</form_type>\r\n" +
					"  <record_id />\r\n" +
					"</x_tags>\r\n";

	private static final String SECOND_XML_STRING = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
					"<x_tags>\r\n" +
					"  <status>draft</status>\r\n" +
					"  <User>\r\n" +
					"    <Name>Karl Smith</Name>\r\n" +
					"    <Organization>%%CONFLICT%%</Organization>\r\n" +
					"    <Address />\r\n" +
					"    <Phone />\r\n" +
					"    <Email>%%CONFLICT%%</Email>\r\n" +
					"  </User>\r\n" +
					"  <Investigator>\r\n" +
					"    <Name>Takahashi, T.</Name>\r\n" +
					"    <Organization>%%CONFLICT%%</Organization>\r\n" +
					"  </Investigator>\r\n" +
					"  <Dataset_Info>\r\n" +
					"    <Dataset_ID>Z2HQ20110517</Dataset_ID>\r\n" +
					"    <Funding_Info />\r\n" +
					"    <Submission_Dates>\r\n" +
					"      <Initial_Submission />\r\n" +
					"      <Revised_Submission />\r\n" +
					"    </Submission_Dates>\r\n" +
					"  </Dataset_Info>\r\n" +
					"  <Cruise_Info>\r\n" +
					"    <Experiment>\r\n" +
					"      <Experiment_Name>Healy_11_01</Experiment_Name>\r\n" +
					"      <Experiment_Type />\r\n" +
					"      <Platform_Type />\r\n" +
					"      <Co2_Instrument_type />\r\n" +
					"      <Mooring_ID />\r\n" +
					"      <Cruise>\r\n" +
					"        <Cruise_ID>Z2HQ20110517</Cruise_ID>\r\n" +
					"        <Cruise_Info />\r\n" +
					"        <Section />\r\n" +
					"        <Geographical_Coverage>\r\n" +
					"          <Geographical_Region />\r\n" +
					"          <Bounds>\r\n" +
					"            <Westernmost_Longitude>201.798</Westernmost_Longitude>\r\n" +
					"            <Easternmost_Longitude>237.572</Easternmost_Longitude>\r\n" +
					"            <Northernmost_Latitude>48.515</Northernmost_Latitude>\r\n" +
					"            <Southernmost_Latitude>21.12</Southernmost_Latitude>\r\n" +
					"          </Bounds>\r\n" +
					"        </Geographical_Coverage>\r\n" +
					"        <Temporal_Coverage>\r\n" +
					"          <Start_Date>20110527</Start_Date>\r\n" +
					"          <End_Date>20110604</End_Date>\r\n" +
					"        </Temporal_Coverage>\r\n" +
					"        <Start_Date>20110517</Start_Date>\r\n" +
					"        <End_Date />\r\n" +
					"      </Cruise>\r\n" +
					"    </Experiment>\r\n" +
					"    <Vessel>\r\n" +
					"      <Vessel_Name>Healy</Vessel_Name>\r\n" +
					"      <Vessel_ID>Z2HQ</Vessel_ID>\r\n" +
					"      <Country />\r\n" +
					"      <Vessel_Owner />\r\n" +
					"    </Vessel>\r\n" +
					"  </Cruise_Info>\r\n" +
					"  <Variables_Info />\r\n" +
					"  <CO2_Data_Info>\r\n" +
					"    <xCO2water_equ_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2water_equ_dry>\r\n" +
					"    <xCO2water_SST_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2water_SST_dry>\r\n" +
					"    <pCO2water_equ_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2water_equ_wet>\r\n" +
					"    <pCO2water_SST_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2water_SST_wet>\r\n" +
					"    <fCO2water_equ_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2water_equ_wet>\r\n" +
					"    <fCO2water_SST_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2water_SST_wet>\r\n" +
					"    <xCO2air_dry>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2air_dry>\r\n" +
					"    <pCO2air_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2air_wet>\r\n" +
					"    <fCO2air_wet>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2air_wet>\r\n" +
					"    <xCO2air_dry_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </xCO2air_dry_interpolated>\r\n" +
					"    <pCO2air_wet_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </pCO2air_wet_interpolated>\r\n" +
					"    <fCO2air_wet_interpolated>\r\n" +
					"      <Unit />\r\n" +
					"    </fCO2air_wet_interpolated>\r\n" +
					"  </CO2_Data_Info>\r\n" +
					"  <Method_Description>\r\n" +
					"    <Equilibrator_Design>\r\n" +
					"      <Depth_of_Sea_Water_Intake />\r\n" +
					"      <Location_of_Sea_Water_Intake />\r\n" +
					"      <Equilibrator_Type />\r\n" +
					"      <Equilibrator_Volume />\r\n" +
					"      <Water_Flow_Rate />\r\n" +
					"      <Headspace_Gas_Flow_Rate />\r\n" +
					"      <Vented>Yes</Vented>\r\n" +
					"      <Drying_Method_for_CO2_in_water />\r\n" +
					"      <Additional_Information />\r\n" +
					"    </Equilibrator_Design>\r\n" +
					"    <CO2_in_Marine_Air>\r\n" +
					"      <Measurement />\r\n" +
					"      <Location_and_Height />\r\n" +
					"      <Drying_Method />\r\n" +
					"    </CO2_in_Marine_Air>\r\n" +
					"    <CO2_Sensors>\r\n" +
					"      <CO2_Sensor>\r\n" +
					"        <Measurement_Method />\r\n" +
					"        <Manufacturer />\r\n" +
					"        <Model />\r\n" +
					"        <Frequency />\r\n" +
					"        <Resolution_Water />\r\n" +
					"        <Uncertainty_Water />\r\n" +
					"        <Resolution_Air />\r\n" +
					"        <Uncertainty_Air />\r\n" +
					"        <Manufacturer_of_Calibration_Gas />\r\n" +
					"        <CO2_Sensor_Calibration />\r\n" +
					"        <Environmental_Control />\r\n" +
					"        <Method_References />\r\n" +
					"        <Details_Co2_Sensing />\r\n" +
					"        <Analysis_of_Co2_Comparision />\r\n" +
					"        <Measured_Co2_Params />\r\n" +
					"      </CO2_Sensor>\r\n" +
					"    </CO2_Sensors>\r\n" +
					"    <Sea_Surface_Temperature>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Sea_Surface_Temperature>\r\n" +
					"    <Equilibrator_Temperature>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Warming />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Equilibrator_Temperature>\r\n" +
					"    <Equilibrator_Pressure>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"      <Normalized>yes</Normalized>\r\n" +
					"    </Equilibrator_Pressure>\r\n" +
					"    <Atmospheric_Pressure>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Atmospheric_Pressure>\r\n" +
					"    <Sea_Surface_Salinity>\r\n" +
					"      <Location />\r\n" +
					"      <Manufacturer />\r\n" +
					"      <Model />\r\n" +
					"      <Accuracy />\r\n" +
					"      <Precision />\r\n" +
					"      <Calibration />\r\n" +
					"      <Other_Comments />\r\n" +
					"    </Sea_Surface_Salinity>\r\n" +
					"    <Other_Sensors />\r\n" +
					"  </Method_Description>\r\n" +
					"  <Data_set_References />\r\n" +
					"  <Additional_Information />\r\n" +
					"  <Citation />\r\n" +
					"  <Measurement_and_Calibration_Report />\r\n" +
					"  <Preliminary_Quality_control>NA</Preliminary_Quality_control>\r\n" +
					"  <form_type>underway</form_type>\r\n" +
					"  <record_id />\r\n" +
					"  <CONFLICTS>\r\n" +
					"    <Conflict>\r\n" +
					"      <User>\r\n" +
					"        <Organization>\r\n" +
					"          <VALUE>PMEL/NOAA</VALUE>\r\n" +
					"          <VALUE>NOAA/PMEL</VALUE>\r\n" +
					"        </Organization>\r\n" +
					"      </User>\r\n" +
					"    </Conflict>\r\n" +
					"    <Conflict>\r\n" +
					"      <User>\r\n" +
					"        <Email>\r\n" +
					"          <VALUE>karl.smith@noaa.gov.again</VALUE>\r\n" +
					"          <VALUE>karl.smith@noaa.gov</VALUE>\r\n" +
					"        </Email>\r\n" +
					"      </User>\r\n" +
					"    </Conflict>\r\n" +
					"    <Conflict>\r\n" +
					"      <Investigator Name=\"Takahashi, T.\" Email=\"\">\r\n" +
					"        <Organization>\r\n" +
					"          <VALUE>not NOAA again</VALUE>\r\n" +
					"          <VALUE>not NOAA</VALUE>\r\n" +
					"        </Organization>\r\n" +
					"      </Investigator>\r\n" +
					"    </Conflict>\r\n" +
					"  </CONFLICTS>\r\n" +
					"</x_tags>\r\n";

	private static final String MERGED_XML_STRING = SECOND_XML_STRING;

	/**
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws BadEntryNameException 
	 */
	@Test
	public void testReadAndMerge() throws JDOMException, IOException, BadEntryNameException, InvalidConflictException {
		OmeMetadata firstOme = new OmeMetadata(TEST_EXPOCODE);
		CharArrayReader omeInput = new CharArrayReader(FIRST_XML_STRING.toCharArray());
		Document omeDoc = (new SAXBuilder()).build(omeInput);
		firstOme.assignFromOmeXmlDoc(omeDoc);

		OmeMetadata secondOme = new OmeMetadata(TEST_EXPOCODE);
		omeInput = new CharArrayReader(SECOND_XML_STRING.toCharArray());
		omeDoc = (new SAXBuilder()).build(omeInput);
		secondOme.assignFromOmeXmlDoc(omeDoc);

		OmeMetadata mergedOme = OmeMetadata.merge(secondOme, firstOme);
		omeDoc = mergedOme.createOmeXmlDoc();
		CharArrayWriter omeOutput = new CharArrayWriter();
		(new XMLOutputter(Format.getPrettyFormat())).output(omeDoc, omeOutput);
		String mergedXml = omeOutput.toString();

		assertEquals(MERGED_XML_STRING, mergedXml);
	}

}
