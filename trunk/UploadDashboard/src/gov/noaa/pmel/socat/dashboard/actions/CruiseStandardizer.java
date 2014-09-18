/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.server.DashboardDataStore;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import ucar.ma2.InvalidRangeException;

/**
 * Standardizes cruise metadata in the DSG files.  
 * Currently only the PI names are standardized from the actual current list of names.
 * 
 * @author Karl Smith
 */
public class CruiseStandardizer {

	private static final String yAcute = "\u00FD";

	private static final HashMap<String,String> PI_RENAME_MAP;
	static {
		PI_RENAME_MAP = new HashMap<String,String>();
		PI_RENAME_MAP.put("", "unknown");
		PI_RENAME_MAP.put("Abdirahman Omar", "Omar, A.");
		PI_RENAME_MAP.put("Adrienne J. Sutton", "Sutton, A.");
		PI_RENAME_MAP.put("Adrienne Sutton", "Sutton, A.");
		PI_RENAME_MAP.put("Agneta Fransson ; Melissa Chierici", "Fransson, A. : Chierici, M.");
		PI_RENAME_MAP.put("Aida F. Rios", "Rios A.F.");
		PI_RENAME_MAP.put("Aida F. Rios ; Fiz F. Perez", "Rios A.F. : Perez, F.F.");
		PI_RENAME_MAP.put("Akihiko Murata", "Murata, A.");
		PI_RENAME_MAP.put("Akira Nakadate", "Nakadate, A.");
		PI_RENAME_MAP.put("Alan Poisson", "Poisson, A.");
		PI_RENAME_MAP.put("Alberto Borges", "Borges, A.");
		PI_RENAME_MAP.put("Andrew Watson", "Watson, A.");
		PI_RENAME_MAP.put("Are Olsen", "Olsen, A.");
		PI_RENAME_MAP.put("Are Olsen ; Sara Jutterstrom ; Truls Johannessen", "Olsen, A. : Jutterstrom, S. : Johannessen, T.");
		PI_RENAME_MAP.put("Are Olsen ; Truls Johannessen", "Olsen, A. : Johannessen, T.");
		PI_RENAME_MAP.put("Arne Koertzinger", "Koertzinger, A.");
		PI_RENAME_MAP.put("Bakker, D.", "Bakker, D.");
		PI_RENAME_MAP.put("Begovic, M.", "Begovic, M.");
		PI_RENAME_MAP.put("B" + SocatMetadata.eAcute + "govic, M.", "Begovic, M.");
		PI_RENAME_MAP.put("B" + yAcute + "govic, M.", "Begovic, M.");
		PI_RENAME_MAP.put("Bellerby, R. : de Baar, H.J.W.", "Bellerby, R. : de Baar, H.J.W.");
		PI_RENAME_MAP.put("Bellerby, R. : Hoppema, M.", "Bellerby, R. : Hoppema, M.");
		PI_RENAME_MAP.put("Bernd Schneider", "Schneider, B.");
		PI_RENAME_MAP.put("Bianchi, A.", "Bianchi, A.");
		PI_RENAME_MAP.put("BODC", "BODC");
		PI_RENAME_MAP.put("Borges, A.", "Borges, A.");
		PI_RENAME_MAP.put("Boutin, J.", "Boutin, J.");
		PI_RENAME_MAP.put("Bozec, Y.", "Bozec, Y.");
		PI_RENAME_MAP.put("Bronte Tilbrook", "Tilbrook, B.");
		PI_RENAME_MAP.put("Cai, W.-J.", "Cai, W.-J.");
		PI_RENAME_MAP.put("Catherine Goyet", "Goyet, C.");
		PI_RENAME_MAP.put("Cathy Cosca", "Cosca, C.");
		PI_RENAME_MAP.put("Chen, L.", "Chen, L.");
		PI_RENAME_MAP.put("Christopher Sabine", "Sabine, C.");
		PI_RENAME_MAP.put("Claire Copin-Montegut", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Claustre, H.", "Claustre, H.");
		PI_RENAME_MAP.put("Copin-Montegut, C.", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Copin-Mont" + SocatMetadata.eAcute + "gut, C.", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Copin-Mont" + yAcute + "gut, C.", "Copin-Montegut, C.");
		PI_RENAME_MAP.put("Cosca, C.", "Cosca, C.");
		PI_RENAME_MAP.put("C. S. Wong", "Wong, C.S.");
		PI_RENAME_MAP.put("Currie, K.I.", "Currie, K.I.");
		PI_RENAME_MAP.put("D. Vandemark", "Vandemark, D.");
		PI_RENAME_MAP.put("Dandonneau, Y.", "Dandonneau, Y.");
		PI_RENAME_MAP.put("David Hydes", "Hydes. D.");
		PI_RENAME_MAP.put("de Baar, H.J.W", "de Baar, H.J.W.");
		PI_RENAME_MAP.put("de Baar, H.J.W.", "de Baar, H.J.W.");
		PI_RENAME_MAP.put("Dorothee Bakker", "Bakker, D.");
		PI_RENAME_MAP.put("Douglas Wallace", "Wallace, D.");
		PI_RENAME_MAP.put("Doug Vandemark ; Joe Salisbury", "Vandemark, D. : Salisbury, J.");
		PI_RENAME_MAP.put("Doug Vandemark ; Joe Salisbury ; Christopher W. Hunt", "Vandemark, D. : Salisbury, J. : Hunt C.W.");
		PI_RENAME_MAP.put("Feely, R.", "Feely, R.");
		PI_RENAME_MAP.put("Fiz F. Perez", "Perez, F.F.");
		PI_RENAME_MAP.put("Frankignoulle, M.", "Frankignoulle, M.");
		PI_RENAME_MAP.put("Fransson, A.", "Fransson, A.");
		PI_RENAME_MAP.put("Fransson, A. : Chierici, M.", "Fransson, A. : Chierici, M.");
		PI_RENAME_MAP.put("Gonzalez-Davila, M. : Santana-Casiano, J.M.", "Gonzalez-Davila, M. : Santana-Casiano, J.M.");
		PI_RENAME_MAP.put("Goyet, C", "Goyet, C.");
		PI_RENAME_MAP.put("Goyet, C.", "Goyet, C.");
		PI_RENAME_MAP.put("Greenwood, N.", "Greenwood, N.");
		PI_RENAME_MAP.put("Hardman-Mountford, N.J.", "Hardman-Mountford, N.J.");
		PI_RENAME_MAP.put("Harlay, J.", "Harlay, J.");
		PI_RENAME_MAP.put("Helmuth Thomas", "Thomas, H.");
		PI_RENAME_MAP.put("Hisayuki Inoue", "Inoue, H.");
		PI_RENAME_MAP.put("Hood, E.M.", "Hood, E.M.");
		PI_RENAME_MAP.put("Hoppema, M.", "Hoppema, M.");
		PI_RENAME_MAP.put("Hydes. D.", "Hydes. D.");
		PI_RENAME_MAP.put("Ingunn Skjelvan", "Skjelvan, I.");
		PI_RENAME_MAP.put("Inoue, H.", "Inoue, H.");
		PI_RENAME_MAP.put("Jane Robertson", "Robertson, J.");
		PI_RENAME_MAP.put("Jaqueline Boutin", "Boutin, J.");
		PI_RENAME_MAP.put("Jeremy Matthis", "Mathis, J.");
		PI_RENAME_MAP.put("Johannessen, T.", "Johannessen, T.");
		PI_RENAME_MAP.put("Johannessen, T. : Omar, A. : Skjelvan, I.", "Johannessen, T. : Omar, A. : Skjelvan, I.");
		PI_RENAME_MAP.put("Johnson, R.", "Johnson, R.");
		PI_RENAME_MAP.put("Keeling, R.", "Keeling, R.");
		PI_RENAME_MAP.put("Key, R.", "Key, R.");
		PI_RENAME_MAP.put("Kim Currie", "Currie, K.I.");
		PI_RENAME_MAP.put("Kitidis, V.", "Kitidis, V.");
		PI_RENAME_MAP.put("Koertzinger, A.", "Koertzinger, A.");
		PI_RENAME_MAP.put("Krasakopoulou, E.", "Krasakopoulou, E.");
		PI_RENAME_MAP.put("Lauvset, S.", "Lauvset, S.");
		PI_RENAME_MAP.put("Lefevre, N.", "Lefevre, N.");
		PI_RENAME_MAP.put("Lendt, R.", "Lendt, R.");
		PI_RENAME_MAP.put("Lefevre, N.", "Lefevre, N.");
		PI_RENAME_MAP.put("Liliane Merlivat", "Merlivat, L.");
		PI_RENAME_MAP.put("Ludger Mintrop", "Mintrop, L.");
		PI_RENAME_MAP.put("Mackey, D.J.", "Mackey, D.J.");
		PI_RENAME_MAP.put("Mario Hoppema", "Hoppema, M.");
		PI_RENAME_MAP.put("Mathis, J.", "Mathis, J.");
		PI_RENAME_MAP.put("Melchor Gonzalez-Davila ; J. Magdalena Santana-Casiano", "Gonzalez-Davila, M. : Santana-Casiano, J.M.");
		PI_RENAME_MAP.put("Merlivat, L.", "Merlivat, L.");
		PI_RENAME_MAP.put("Metzl, N.", "Metzl, N.");
		PI_RENAME_MAP.put("Michel Frankignoulle", "Frankignoulle, M.");
		PI_RENAME_MAP.put("Michel Stoll ; Hein de Baar", "Stoll, M. : de Baar, H.J.W.");
		PI_RENAME_MAP.put("Milena Begovic", "Begovic, M.");
		PI_RENAME_MAP.put("Millero, F.J.", "Millero, F.J.");
		PI_RENAME_MAP.put("Mintrop, L.", "Mintrop, L.");
		PI_RENAME_MAP.put("Monteiro, P.", "Monteiro, P.");
		PI_RENAME_MAP.put("Murata, A.", "Murata, A.");
		PI_RENAME_MAP.put("Nakadate, A.", "Nakadate, A.");
		PI_RENAME_MAP.put("Naoami Greenwood", "Greenwood, N.");
		PI_RENAME_MAP.put("Nathalie Lefevre", "Lefevre, N.");
		PI_RENAME_MAP.put("Nick Hardman-Mountford", "Hardman-Mountford, N.J.");
		PI_RENAME_MAP.put("Nicolas Metzl", "Metzl, N.");
		PI_RENAME_MAP.put("Nobuo, T.", "Nobuo, T.");
		PI_RENAME_MAP.put("Nojiri, Y.", "Nojiri, Y.");
		PI_RENAME_MAP.put("Olsen, A.", "Olsen, A.");
		PI_RENAME_MAP.put("Olsen, A. : Jutterstrom, S. : Johannessen, T.", "Olsen, A. : Jutterstrom, S. : Johannessen, T.");
		PI_RENAME_MAP.put("Olsen, A. : Johannessen, T.", "Olsen, A. : Johannessen, T.");
		PI_RENAME_MAP.put("Omar, A.", "Omar, A.");
		PI_RENAME_MAP.put("OMEX Project Members", "OMEX Project Members");
		PI_RENAME_MAP.put("Ono, T.", "Ono, T.");
		PI_RENAME_MAP.put("Pedro Monteiro", "Monteiro, P.");
		PI_RENAME_MAP.put("Perez, F.F.", "Perez, F.F.");
		PI_RENAME_MAP.put("Poisson, A.", "Poisson, A.");
		PI_RENAME_MAP.put("Ray Weiss", "Weiss, R.");
		PI_RENAME_MAP.put("Richard Bellerby ; Hein de Baar", "Bellerby, R. : de Baar, H.J.W.");
		PI_RENAME_MAP.put("Richard Bellerby ; Mario Hoppema", "Bellerby, R. : Hoppema, M.");
		PI_RENAME_MAP.put("Richard Feely", "Feely, R.");
		PI_RENAME_MAP.put("Rik Wanninkhof", "Wanninkhof, R.");
		PI_RENAME_MAP.put("Rios A.F.", "Rios A.F.");
		PI_RENAME_MAP.put("Rios A.F. : Perez, F.F.", "Rios A.F. : Perez, F.F.");
		PI_RENAME_MAP.put("Robbins, L.L.", "Robbins, L.L.");
		PI_RENAME_MAP.put("Robert Key", "Key, R.");
		PI_RENAME_MAP.put("Robertson, J.", "Robertson, J.");
		PI_RENAME_MAP.put("Sabine, C.", "Sabine, C.");
		PI_RENAME_MAP.put("Saito, S.", "Saito, S.");
		PI_RENAME_MAP.put("Schneider, B.", "Schneider, B.");
		PI_RENAME_MAP.put("Schuster, U.", "Schuster, U.");
		PI_RENAME_MAP.put("Schuster, U. : Watson, A.", "Schuster, U. : Watson, A.");
		PI_RENAME_MAP.put("Skjelvan, I.", "Skjelvan, I.");
		PI_RENAME_MAP.put("S. Saito", "Saito, S.");
		PI_RENAME_MAP.put("Steinhoff, T.", "Steinhoff, T.");
		PI_RENAME_MAP.put("Steinhoff, T. : Koertzinger, A.", "Steinhoff, T. : Koertzinger, A.");
		PI_RENAME_MAP.put("Stoll, M. : de Baar, H.J.W.", "Stoll, M. : de Baar, H.J.W.");
		PI_RENAME_MAP.put("Sutton, A.", "Sutton, A.");
		PI_RENAME_MAP.put("Sweeney, C.", "Sweeney, C.");
		PI_RENAME_MAP.put("Takahashi, T.", "Takahashi, T.");
		PI_RENAME_MAP.put("Taro Takahashi", "Takahashi, T.");
		PI_RENAME_MAP.put("Thomas, H.", "Thomas, H.");
		PI_RENAME_MAP.put("Tilbrook, B.", "Tilbrook, B.");
		PI_RENAME_MAP.put("Tobias Steinhoff ; Arne Koertzinger", "Steinhoff, T. : Koertzinger, A.");
		PI_RENAME_MAP.put("Treguer, P.", "Treguer, P.");
		PI_RENAME_MAP.put("Tr" + SocatMetadata.eAcute + "guer, P.", "Treguer, P.");
		PI_RENAME_MAP.put("Tr" + yAcute + "guer, P.", "Treguer, P.");
		PI_RENAME_MAP.put("Truls Johannessen ; Abdirahman Omar ; Ingunn Skjelvan", "Johannessen, T. : Omar, A. : Skjelvan, I.");
		PI_RENAME_MAP.put("Tsuneo Ono", "Ono, T.");
		PI_RENAME_MAP.put("Tsurushima Nobuo", "Nobuo, T.");
		PI_RENAME_MAP.put("unknown", "unknown");
		PI_RENAME_MAP.put("Ute Schuster", "Schuster, U.");
		PI_RENAME_MAP.put("Ute Schuster ; Andrew Watson", "Schuster, U. : Watson, A.");
		PI_RENAME_MAP.put("van Heuven, S.", "van Heuven, S.");
		PI_RENAME_MAP.put("Vandemark, D.", "Vandemark, D.");
		PI_RENAME_MAP.put("Vandemark, D. : Salisbury, J.", "Vandemark, D. : Salisbury, J.");
		PI_RENAME_MAP.put("Vandemark, D. : Salisbury, J. : Hunt C.W.", "Vandemark, D. : Salisbury, J. : Hunt C.W.");
		PI_RENAME_MAP.put("Vassilis Kitidis", "Kitidis, V.");
		PI_RENAME_MAP.put("Wallace, D.", "Wallace, D.");
		PI_RENAME_MAP.put("Wannikhof, R.", "Wanninkhof, R.");
		PI_RENAME_MAP.put("Wanninkhof, R.", "Wanninkhof, R.");
		PI_RENAME_MAP.put("Watson, A.", "Watson, A.");
		PI_RENAME_MAP.put("Ward, B.", "Ward, B.");
		PI_RENAME_MAP.put("Wei-Jun Cai", "Cai, W.-J.");
		PI_RENAME_MAP.put("W.-J. Cai", "Cai, W.-J.");
		PI_RENAME_MAP.put("Weiss, R.", "Weiss, R.");
		PI_RENAME_MAP.put("Wong, C.S.", "Wong, C.S.");
		PI_RENAME_MAP.put("Yukihiro Nojiri", "Nojiri, Y.");
		PI_RENAME_MAP.put("Yves Dandonneau", "Dandonneau, Y.");
	}

	DsgNcFileHandler dsgHandler;
	FerretConfig ferretConfig;

	/**
	 * Standardize metadata for cruise DSG files obtained from
	 * the given DSG file handler.
	 * 
	 * @param dsgHandler
	 * 		the DSG file handler to use
	 */
	public CruiseStandardizer(DsgNcFileHandler dsgHandler, FerretConfig ferretConfig) {
		this.dsgHandler = dsgHandler;
		this.ferretConfig = ferretConfig;
	}

	/**
	 * Standardize the PI names for the given cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise to standardize
	 * @throws IllegalArgumentException 
	 * 		if the DSG file is invalid, 
	 * 		if the PI name(s) in the DSG file is/are not recognized
	 * @throws IOException 
	 * 		if there are problems reading or recreating the DSG file
	 * 		or decimated DSG file.
	 * @throws InvalidRangeException 
	 * 		if recreating the DSG file or decimated DSG file throws one
	 * @throws IllegalAccessException 
	 * 		if recreating the DSG file or decimated DSG file throws one
	 */
	public void standardizePINames(String expocode) throws IllegalArgumentException, 
						IOException, IllegalAccessException, InvalidRangeException {
		// Get the new PI names from the saved PI names
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.read(true);
		SocatMetadata mdata = dsgFile.getMetadata();

		String piNames = mdata.getScienceGroup().trim();
		String newPiNames = PI_RENAME_MAP.get(piNames);
		if ( newPiNames == null )
			throw new IllegalArgumentException("PI name(s) not recognized: '" + piNames + "'");
		newPiNames = newPiNames.trim();

		// If unchanged, nothing to do
		if ( newPiNames.equals(piNames) ) {
			System.err.println("PI names unchanged for " + expocode);
			return;
		}

		try {
			// Try to just change the names in the existing DSG files
			dsgFile.updatePINames(newPiNames);
			CruiseDsgNcFile decDsgFile = dsgHandler.getDecDsgNcFile(expocode);
			decDsgFile.updatePINames(newPiNames);
			System.err.println("PI names changed in place for " + expocode);
		} catch (InvalidRangeException ex) {
			// Names longer than allotted space; regenerate the DSG files
			dsgFile.read(false);
			ArrayList<SocatCruiseData> dataList = dsgFile.getDataList();
			mdata = dsgFile.getMetadata();
			mdata.setScienceGroup(newPiNames);
			// Re-create the full-data DSG file
			dsgFile.create(mdata, dataList);
			// Call Ferret to add lon360 and tmonth (calculated data should be the same
			SocatTool tool = new SocatTool(ferretConfig);
			tool.init(dsgFile.getPath(), null, expocode, FerretConfig.Action.COMPUTE);
			tool.run();
			if ( tool.hasError() )
				throw new IllegalArgumentException("Failure adding computed variables: " + 
						tool.getErrorMessage());
			// Re-create the decimated-data DSG file 
			dsgHandler.decimateCruise(expocode);
			System.err.println("PI names changed by regenerating the DSG files");
		}
	}

	/**
	 * @param args
	 * 		ExpocodesFile
	 * 
	 * 		Standardizes the PI names for cruises specified in ExpocodesFile. 
	 * 		The default dashboard configuration is used for this process.  
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ) {
			System.err.println("Arguments:  ExpocodesFile");
			System.err.println();
			System.err.println("Standardizes the PI names for cruises specified in ExpocodesFile. ");
			System.err.println("The default dashboard configuration is used for this process. ");
			System.err.println();
			System.exit(1);
		}
		String expocodesFilename = args[0];
		boolean success = true;

		// Get the default dashboard configuration
		DashboardDataStore dataStore = null;		
		try {
			dataStore = DashboardDataStore.get();
		} catch (Exception ex) {
			System.err.println("Problems reading the default dashboard " +
					"configuration file: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		try {
			DsgNcFileHandler dsgHandler = dataStore.getDsgNcFileHandler();
			CruiseStandardizer standardizer = new CruiseStandardizer(dsgHandler, 
													dataStore.getFerretConfig());

			// Get the expocodes of the cruises to standardize
			TreeSet<String> allExpocodes = new TreeSet<String>();
			try {
				BufferedReader expoReader = 
						new BufferedReader(new FileReader(expocodesFilename));
				try {
					String dataline = expoReader.readLine();
					while ( dataline != null ) {
						dataline = dataline.trim();
						if ( ! ( dataline.isEmpty() || dataline.startsWith("#") ) )
							allExpocodes.add(dataline);
						dataline = expoReader.readLine();
					}
				} finally {
					expoReader.close();
				}
			} catch (Exception ex) {
				System.err.println("Error getting expocodes from " + 
						expocodesFilename + ": " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}

			// standardize the PI names in each of these cruises
			for ( String expocode : allExpocodes ) {
				try {
					standardizer.standardizePINames(expocode);
				} catch (Exception ex) {
					System.err.println("Error updating " + expocode + " : " + ex.getMessage());
					ex.printStackTrace();
					System.err.println("===================================================");
					success = false;
				}
			}
			dsgHandler.flagErddap(true);
		} finally {
			dataStore.shutdown();
		}
		if ( ! success )
			System.exit(1);
		System.exit(0);
	}

}
