/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.programs;

import gov.noaa.pmel.socat.dashboard.actions.OmeXmlPdfGenerator;
import gov.noaa.pmel.socat.dashboard.server.DashboardConfigStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

/**
 * Generate PDFs from the PI_OME.xml documents for specified expocode.
 * 
 * @author Karl Smith
 */
public class GenerateOmePDFs {

	/**
	 * Generates PDFs from the PI_OME.xml documents for specified expocodes.
	 * 
	 * @param args
	 * 		ExpocodesFile
	 * 
	 * where ExpocodesFile is a file of expocodes to consider for generating PDFs.
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Generates PDFs from the PI_OME.xml files for the ");
			System.err.println("expocodes specified in ExpocodesFile.  The default ");
			System.err.println("dashboard configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}
		String exposFilename = args[0];

		TreeSet<String> expocodes = new TreeSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
			try {
				String dataline = reader.readLine();
				while ( dataline != null ) {
					dataline = dataline.trim().toUpperCase();
					if ( ! dataline.isEmpty() )
						expocodes.add(dataline);
					dataline = reader.readLine();
				}
			} finally {
				reader.close();
			}
		} catch (Exception ex) {
			System.err.println("Problems reading the file of expocodes '" + 
					exposFilename + "': " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}

		DashboardConfigStore configStore = null;
		try {
			configStore = DashboardConfigStore.get(false);
		} catch (Exception ex) {
			System.err.println("Problems obtaining the default dashboard " +
					"configuration: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			OmeXmlPdfGenerator pdfGenerator = configStore.getOmePdfGenerator();
			for ( String expo : expocodes ) {
				try {
					pdfGenerator.createPiOmePdf(expo);
					System.out.println(expo + " SUCCESS");
				} catch (Exception ex) {
					System.out.println(expo + " FAIL: " + ex.getMessage());
				}
			}
		} finally {
			DashboardConfigStore.shutdown();
		}

		System.exit(0);
	}

}
