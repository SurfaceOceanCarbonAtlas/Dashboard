/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import uk.ac.uea.socat.metadata.OmeMetadata.OmeMetadata;
import uk.ac.uea.socat.sanitychecker.Output;
import uk.ac.uea.socat.sanitychecker.SanityChecker;
import uk.ac.uea.socat.sanitychecker.config.ColumnConversionConfig;
import uk.ac.uea.socat.sanitychecker.data.ColumnSpec;

/**
 * Test of the sanity checker via function calls.
 * 
 * @author Karl Smith
 */
public class SanityCheckerTest {
	private static final String CONFIG_FILENAME = 
			"/home/flat/ksmith/content/SocatUploadDashboard/SocatUploadDashboard.properties";
	private static final String LOG4J_PROPERTIES_FILENAME = 
			"/home/flat/ksmith/content/SocatUploadDashboard/log4j.properties";

	@Test
	public void testSanityChecker() throws Exception {
		final String expocode = "33RO20030715";

		final String cruiseDocXml = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<Expocode_" + expocode + ">" + 
					"<socat_column name=\"longitude\">" +
						"<input_column index=\"10\">longitude [deg.E]</input_column>" +
						"<input_units>decimal_degrees</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"latitude\">" +
						"<input_column index=\"11\">latitude [deg.N]</input_column>" +
						"<input_units>decimal_degress</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"sample_depth\">" +
						"<input_column index=\"12\">sample_depth [m]</input_column>" +
						"<input_units>meters</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"salinity\">" +
						"<input_column index=\"13\">salinity</input_column>" +
						"<input_units>psu</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"SST\">" +
						"<input_column index=\"14\">SST [deg.C]</input_column>" +
						"<input_units>degC</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"T_equ\">" +
						"<input_column index=\"15\">Tequ [deg.C]</input_column>" +
						"<input_units>degC</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"SLP\">" +
						"<input_column index=\"16\">PPPP [hPa]</input_column>" +
						"<input_units>hPa</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"P_equ\">" +
						"<input_column index=\"17\">Pequ [hPa]</input_column>" +
						"<input_units>hPa</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"xCO2_water_Tequ_dry\">" +
						"<input_column index=\"23\">xCO2_water_Tequ_dry [umol/mol]</input_column>" +
						"<input_units>ppm</input_units>" +
					"</socat_column>" +
					"<socat_column name=\"fCO2_water_SST_wet\">" +
						"<input_column index=\"28\">fCO2_water_SST_wet [uatm]</input_column>" +
						"<input_units>uatm</input_units>" +
					"</socat_column>" +
					"<date_columns>" +
						"<year index=\"4\">yr</year>" +
						"<month index=\"5\">mon</month>" +
						"<day index=\"6\">day</day>" +
						"<hour index=\"7\">hh</hour>" +
						"<minute index=\"8\">mm</minute>" +
						"<second index=\"9\">ss</second>" +
					"</date_columns>" +
				"</Expocode_" + expocode + ">";
		Document cruiseDoc = (new SAXBuilder()).build(
				new ByteArrayInputStream(cruiseDocXml.getBytes()));

		String[] goodCruiseDataStrings = {
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00.00\t342.76300\t32.48200\t5.\t36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00.00\t342.74500\t32.47400\t5.\t36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t24\t00.0\t342.72900\t32.46600\t5.\t36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t378.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00.0\t342.71201\t32.45900\t5.\t36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t374.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00.\t342.64700\t32.43000\t5.\t36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t375.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00.\t342.62900\t32.42300\t5.\t36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t376.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t32.41500\t5.\t36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t377.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t32.40800\t5.\t36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t377.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t32.38300\t5.\t36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t379.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t342.03900\t32.19500\t5.\t36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
		};
		String[] questionableCruiseDataStrings = {
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00\t342.76300\t32.48200\t5.\t36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00\t342.74500\t32.47400\t5.\t36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t24\t00\t342.72900\t32.46600\t5.\t36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t78.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00\t342.71201\t32.45900\t5.\t36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t74.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00\t342.64700\t32.43000\t5.\t36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t75.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00\t342.62900\t32.42300\t5.\t36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t76.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t32.41500\t5.\t36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t77.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t32.40800\t5.\t36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t77.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t32.38300\t5.\t36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t79.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t342.03900\t32.19500\t5.\t36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
		};
		String[] badCruiseDataStrings = {
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t15\t00\t342.76300\t32.48200\t5.\t36.550\t20.440\t21.020\t1017.860\t1017.600\t36.749\t1018.500\t3553.\t663.\t371.151\t396.070\tNaN\tNaN\tNaN\tNaN\t378.410\t377.552\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t20\t00\t342.74500\t32.47400\t5.\t36.560\t20.230\t20.760\t1017.480\t1017.430\t36.749\t1018.500\t3553.\t663.\t371.151\t384.430\tNaN\tNaN\tNaN\tNaN\t368.120\t367.309\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t02\t31\t16\t24\t00\t342.72900\t132.46600\t5.\t36.590\t19.960\t20.610\t1017.440\t1017.490\t36.749\t1018.500\t3803.\t663.\t371.151\t378.350\tNaN\tNaN\tNaN\tNaN\t360.740\t359.769\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t29\t00\t342.71201\t132.45900\t5.\t36.530\t20.060\t20.410\t1017.460\t1017.400\t36.749\t1018.500\t3803.\t664.\t371.151\t374.490\tNaN\tNaN\tNaN\tNaN\t361.200\t360.716\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t45\t00\t342.64700\t132.43000\t5.\t36.550\t20.690\t20.900\t1017.440\t1017.450\t36.749\t1018.500\t3929.\t665.\t371.151\t375.110\tNaN\tNaN\tNaN\tNaN\t363.510\t363.222\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t50\t00\t342.62900\t132.42300\t5.\t36.550\t20.770\t21.010\t1017.630\t1017.460\t36.749\t1018.500\t3947.\t666.\t371.151\t376.850\tNaN\tNaN\tNaN\tNaN\t364.720\t364.389\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t54\t00\t342.60999\t132.41500\t5.\t36.550\t20.740\t21.030\t1017.440\t1017.530\t36.749\t1018.500\t3947.\t666.\t371.150\t377.920\tNaN\tNaN\tNaN\tNaN\t365.180\t364.666\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t16\t59\t00\t342.59299\t132.40800\t5.\t36.540\t20.770\t20.970\t1017.510\t1017.520\t36.749\t1018.500\t3987.\t667.\t371.150\t377.520\tNaN\tNaN\tNaN\tNaN\t366.070\t365.698\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t15\t00\t342.52301\t132.38300\t5.\t36.600\t21.250\t21.420\t1017.440\t1017.480\t36.749\t1018.500\t4087.\t669.\t371.150\t379.350\tNaN\tNaN\tNaN\tNaN\t367.980\t367.678\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t20\t00\t342.50400\t32.37600\t5.\t36.610\t21.590\t21.670\t1017.540\t1017.470\t36.749\t1018.500\t4087.\t669.\t371.150\t381.540\tNaN\tNaN\tNaN\tNaN\t371.160\t371.067\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t24\t00\t342.48599\t32.37000\t5.\t36.650\t21.790\t21.880\t1017.500\t1017.440\t36.749\t1018.500\t4139.\t670.\t371.150\t385.330\tNaN\tNaN\tNaN\tNaN\t374.620\t374.463\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t29\t00\t342.46899\t32.36300\t5.\t36.650\t21.980\t22.070\t1017.610\t1017.480\t36.749\t1018.500\t4199.\t671.\t371.150\t388.780\tNaN\tNaN\tNaN\tNaN\t377.840\t377.719\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t45\t00\t342.39999\t32.33600\t5.\t36.710\t22.270\t22.540\t1017.680\t1017.600\t36.749\t1018.500\t4223.\t673.\t371.150\t398.980\tNaN\tNaN\tNaN\tNaN\t384.990\t384.450\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t50\t00\t342.38300\t32.33000\t5.\t36.700\t22.400\t22.540\t1017.730\t1017.630\t36.749\t1018.500\t4257.\t673.\t371.150\t399.330\tNaN\tNaN\tNaN\tNaN\t387.220\t386.921\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t54\t00\t342.36401\t32.32200\t5.\t36.710\t22.660\t22.740\t1017.700\t1017.690\t36.749\t1018.500\t4273.\t674.\t371.150\t400.970\tNaN\tNaN\tNaN\tNaN\t389.570\t389.396\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t17\t59\t00\t342.34601\t32.31500\t5.\t36.760\t22.890\t22.970\t1017.750\t1017.490\t36.749\t1018.500\t4273.\t674.\t371.150\t403.580\tNaN\tNaN\tNaN\tNaN\t391.830\t391.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t15\t00\t342.27802\t32.28800\t5.\t36.820\t23.140\t23.310\t1017.610\t1017.560\t36.749\t1018.500\t4355.\t677.\t371.150\t410.450\tNaN\tNaN\tNaN\tNaN\t397.060\t396.664\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t20\t00\t342.25900\t32.28000\t5.\t36.830\t23.130\t23.310\t1017.630\t1017.700\t36.749\t1018.500\t4365.\t677.\t371.150\t411.160\tNaN\tNaN\tNaN\tNaN\t397.600\t397.238\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t24\t00\t342.24100\t32.27300\t5.\t36.830\t23.140\t23.320\t1017.720\t1017.570\t36.749\t1018.500\t4365.\t678.\t371.150\t411.400\tNaN\tNaN\tNaN\tNaN\t397.750\t397.411\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t29\t00\t342.22400\t32.26600\t5.\t36.830\t23.160\t23.340\t1017.630\t1017.540\t36.749\t1018.500\t4389.\t678.\t371.150\t411.730\tNaN\tNaN\tNaN\tNaN\t398.030\t397.705\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t45\t00\t342.15601\t32.24300\t5.\t36.860\t23.270\t23.460\t1017.650\t1017.590\t36.749\t1018.500\t4411.\t681.\t371.150\t413.210\tNaN\tNaN\tNaN\tNaN\t399.270\t398.906\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t50\t00\t342.13800\t32.23900\t5.\t36.870\t23.280\t23.470\t1017.730\t1017.640\t36.749\t1018.500\t4411.\t682.\t371.150\t413.500\tNaN\tNaN\tNaN\tNaN\t399.660\t399.199\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t54\t00\t342.12000\t32.23300\t5.\t36.870\t23.270\t23.460\t1017.840\t1017.780\t36.749\t1018.500\t4453.\t683.\t371.150\t413.630\tNaN\tNaN\tNaN\tNaN\t399.800\t399.387\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t18\t59\t00\t342.10300\t32.22500\t5.\t36.860\t23.250\t23.450\t1017.880\t1017.880\t36.749\t1018.500\t4453.\t683.\t371.150\t413.650\tNaN\tNaN\tNaN\tNaN\t399.690\t399.285\t1\t2",
				"33RO20030715\tdoi:10.1594/PANGAEA.813525\t11\t2003\t07\t15\t19\t15\t00\t2.03900\t32.19500\t5.\t36.830\t23.140\t23.350\t1017.970\t1017.920\t36.749\t1018.500\t4503.\t685.\t371.149\t412.220\tNaN\tNaN\tNaN\tNaN\t398.230\t397.819\t1\t2",
		};
		ArrayList<ArrayList<String>> goodCruiseData = new ArrayList<ArrayList<String>>(goodCruiseDataStrings.length);
		for ( String dataString : goodCruiseDataStrings )
			goodCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
		ArrayList<ArrayList<String>> questionableCruiseData = new ArrayList<ArrayList<String>>(questionableCruiseDataStrings.length);
		for ( String dataString : questionableCruiseDataStrings )
			questionableCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));
		ArrayList<ArrayList<String>> badCruiseData = new ArrayList<ArrayList<String>>(badCruiseDataStrings.length);
		for ( String dataString : badCruiseDataStrings )
			badCruiseData.add(new ArrayList<String>(Arrays.asList(dataString.split("\t", -1))));

		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILENAME);
		SanityChecker.initConfig(CONFIG_FILENAME);

		ColumnConversionConfig convConfig = ColumnConversionConfig.getInstance();

		File name = new File(expocode);
		Logger logger = Logger.getLogger("Sanity Checker");
		ColumnSpec colSpec = new ColumnSpec(name, cruiseDoc, convConfig, logger);

		OmeMetadata metadataInput = new OmeMetadata(expocode);
		SanityChecker checker = new SanityChecker(expocode, metadataInput, 
					colSpec, goodCruiseData, "YYYY-MM-DD");
		Output output = checker.process();
		assertTrue( output.processedOK() );
		assertFalse( output.hasWarnings() );
		assertFalse( output.hasErrors() );
		assertEquals( 342.039, Double.parseDouble(metadataInput.getWestmostLongitude()), 0.0001 );
		assertEquals( 342.763, Double.parseDouble(metadataInput.getEastmostLongitude()), 0.0001 );
		assertEquals( 32.195, Double.parseDouble(metadataInput.getSouthmostLatitude()), 0.0001 );
		assertEquals( 32.482, Double.parseDouble(metadataInput.getNorthmostLatitude()), 0.0001 );
		assertEquals( "20030715", metadataInput.getTemporalCoverageStartDate() );
		assertEquals( "20030715", metadataInput.getTemporalCoverageEndDate() );

		// Repeat the check with the already assigned metadata
		checker = new SanityChecker(expocode, metadataInput, 
				colSpec, goodCruiseData, "YYYY-MM-DD");
		output = checker.process();
		assertTrue( output.processedOK() );
		assertFalse( output.hasWarnings() );
		assertFalse( output.hasErrors() );
		assertEquals( 342.039, Double.parseDouble(metadataInput.getWestmostLongitude()), 0.0001 );
		assertEquals( 342.763, Double.parseDouble(metadataInput.getEastmostLongitude()), 0.0001 );
		assertEquals( 32.195, Double.parseDouble(metadataInput.getSouthmostLatitude()), 0.0001 );
		assertEquals( 32.482, Double.parseDouble(metadataInput.getNorthmostLatitude()), 0.0001 );
		assertEquals( "20030715", metadataInput.getTemporalCoverageStartDate() );
		assertEquals( "20030715", metadataInput.getTemporalCoverageEndDate() );

		metadataInput = new OmeMetadata(expocode);
		checker = new SanityChecker(expocode, metadataInput, 
				colSpec, questionableCruiseData, "YYYY-MM-DD");
		output = checker.process();
		assertTrue( output.processedOK() );
		assertTrue( output.hasWarnings() );
		assertFalse( output.hasErrors() );
		assertEquals( 342.039, Double.parseDouble(metadataInput.getWestmostLongitude()), 0.0001 );
		assertEquals( 342.763, Double.parseDouble(metadataInput.getEastmostLongitude()), 0.0001 );
		assertEquals( 32.195, Double.parseDouble(metadataInput.getSouthmostLatitude()), 0.0001 );
		assertEquals( 32.482, Double.parseDouble(metadataInput.getNorthmostLatitude()), 0.0001 );
		assertEquals( "20030715", metadataInput.getTemporalCoverageStartDate() );
		assertEquals( "20030715", metadataInput.getTemporalCoverageEndDate() );

		metadataInput = new OmeMetadata(expocode);
		checker = new SanityChecker(expocode, metadataInput, 
				colSpec, badCruiseData, "YYYY-MM-DD");
		output = checker.process();
		assertTrue( output.processedOK() );
		assertFalse( output.hasWarnings() );
		assertTrue( output.hasErrors() );
		assertEquals( 342.103, Double.parseDouble(metadataInput.getWestmostLongitude()), 0.0001 );
		assertEquals( 2.039, Double.parseDouble(metadataInput.getEastmostLongitude()), 0.0001 );
		assertEquals( 32.195, Double.parseDouble(metadataInput.getSouthmostLatitude()), 0.0001 );
		assertEquals( 32.482, Double.parseDouble(metadataInput.getNorthmostLatitude()), 0.0001 );
		assertEquals( "20030715", metadataInput.getTemporalCoverageStartDate() );
		assertEquals( "20030715", metadataInput.getTemporalCoverageEndDate() );
	}
	
}
