/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.actions;

import gov.noaa.pmel.socat.dashboard.ferret.FerretConfig;
import gov.noaa.pmel.socat.dashboard.ferret.SocatTool;
import gov.noaa.pmel.socat.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.socat.dashboard.nc.Constants;
import gov.noaa.pmel.socat.dashboard.nc.CruiseDsgNcFile;
import gov.noaa.pmel.socat.dashboard.shared.SocatCruiseData;
import gov.noaa.pmel.socat.dashboard.shared.SocatMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ucar.ma2.InvalidRangeException;

/**
 * Standardizes cruise metadata in the DSG files.  
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

	private static final HashMap<String,String> SHIP_RENAME_MAP;
	static {
		SHIP_RENAME_MAP = new HashMap<String,String>();
		SHIP_RENAME_MAP.put("Akademik Korolev", "Akademik Korolev");
		SHIP_RENAME_MAP.put("Albert Rickmers", "Albert Rickmers");
		SHIP_RENAME_MAP.put("Alligator Hope", "Alligator Hope");
		SHIP_RENAME_MAP.put("Almirante irizar", "Almirante Irizar");
		SHIP_RENAME_MAP.put("Almirante Irizar", "Almirante Irizar");
		SHIP_RENAME_MAP.put("Antares", "Antares");
		SHIP_RENAME_MAP.put("Argo", "Argo");
		SHIP_RENAME_MAP.put("Atlantic  Companion", "Atlantic Companion");
		SHIP_RENAME_MAP.put("Atlantic Companion", "Atlantic Companion");
		SHIP_RENAME_MAP.put("Audace", "Audace");
		SHIP_RENAME_MAP.put("Aurora Australis", "Aurora Australis");
		SHIP_RENAME_MAP.put("A. V. Humboldt", "A.V. Humboldt");
		SHIP_RENAME_MAP.put("A.V. Humboldt", "A.V. Humboldt");
		SHIP_RENAME_MAP.put("Belgica", "Belgica");
		SHIP_RENAME_MAP.put("Bell M. Shimada", "Bell M. Shimada");
		SHIP_RENAME_MAP.put("Benguela Stream", "Benguela Stream");
		SHIP_RENAME_MAP.put("Bold", "Bold");
		SHIP_RENAME_MAP.put("CAPE HATTERAS", "Cape Hatteras");
		SHIP_RENAME_MAP.put("Cape Hatteras", "Cape Hatteras");
		SHIP_RENAME_MAP.put("Cap Victor", "Cap Victor");
		SHIP_RENAME_MAP.put("Carioca", "Carioca");
		SHIP_RENAME_MAP.put("CEFAS ENDEAVOUR", "Cefas Endeavour");
		SHIP_RENAME_MAP.put("Cefas Endeavour", "Cefas Endeavour");
		SHIP_RENAME_MAP.put("Celtic Explorer", "Celtic Explorer");
		SHIP_RENAME_MAP.put("Charles Darwin", "Charles Darwin");
		SHIP_RENAME_MAP.put("Colibri", "Colibri");
		SHIP_RENAME_MAP.put("Columbus Waikato", "Columbus Waikato");
		SHIP_RENAME_MAP.put("David Starr Jordan", "David Starr Jordan");
		SHIP_RENAME_MAP.put("Discoverer", "Discoverer");
		SHIP_RENAME_MAP.put("Discovery", "Discovery");
		SHIP_RENAME_MAP.put("Drifting Buoy", "Drifting buoy");
		SHIP_RENAME_MAP.put("Drifting Bouy", "Drifting buoy");
		SHIP_RENAME_MAP.put("Drifting bouy", "Drifting buoy");
		SHIP_RENAME_MAP.put("Drifting buoy", "Drifting buoy");
		SHIP_RENAME_MAP.put("Explorer of the Seas", "Explorer of the Seas");
		SHIP_RENAME_MAP.put("Falstaff", "Falstaff");
		SHIP_RENAME_MAP.put("Finnmaid", "Finnmaid");
		SHIP_RENAME_MAP.put("Franklin", "Franklin");
		SHIP_RENAME_MAP.put("Gauss", "Gauss");
		SHIP_RENAME_MAP.put("Genetica/Garlicos", "Genetica/Garlicos");
		SHIP_RENAME_MAP.put("Gordon Gunter", "Gordon Gunter");
		SHIP_RENAME_MAP.put("G. O. Sars", "G.O. Sars");
		SHIP_RENAME_MAP.put("G.O. Sars", "G.O. Sars");
		SHIP_RENAME_MAP.put("GULF CHALLENGER", "Gulf Challenger");
		SHIP_RENAME_MAP.put("Gulf Challenger", "Gulf Challenger");
		SHIP_RENAME_MAP.put("Haakon Mosby", "Haakon Mosby");
		SHIP_RENAME_MAP.put("Hakuho Maru", "Hakuho Maru");
		SHIP_RENAME_MAP.put("Hakurei Maru 2", "Hakurei Maru 2");
		SHIP_RENAME_MAP.put("Healy", "Healy");
		SHIP_RENAME_MAP.put("HENRY B. BIGELOW", "Henry B. Bigelow");
		SHIP_RENAME_MAP.put("Henry B. Bigelow", "Henry B. Bigelow");
		SHIP_RENAME_MAP.put("Hesperides", "Hesperides");
		SHIP_RENAME_MAP.put("Hokuto Maru", "Hokuto Maru");
		SHIP_RENAME_MAP.put("Horizon", "Horizon");
		SHIP_RENAME_MAP.put("Hudson", "Hudson");
		SHIP_RENAME_MAP.put("James Clark Ross", "James Clark Ross");
		SHIP_RENAME_MAP.put("Johan Hjort", "Johan Hjort");
		SHIP_RENAME_MAP.put("John P. Tully", "John P. Tully");
		SHIP_RENAME_MAP.put("Ka'imimoana", "Ka imimoana");
		SHIP_RENAME_MAP.put("Ka imimoana", "Ka imimoana");
		SHIP_RENAME_MAP.put("Kaiyo", "Kaiyo");
		SHIP_RENAME_MAP.put("Kaiyo Maru", "Kaiyo Maru");
		SHIP_RENAME_MAP.put("Keifu Maru", "Keifu Maru");
		SHIP_RENAME_MAP.put("Knorr", "Knorr");
		SHIP_RENAME_MAP.put("Kofu Maru", "Kofu Maru");
		SHIP_RENAME_MAP.put("Las Cuevas", "Las Cuevas");
		SHIP_RENAME_MAP.put("L'Astrolabe", "L Astrolabe");
		SHIP_RENAME_MAP.put("L Astrolabe", "L Astrolabe");
		SHIP_RENAME_MAP.put("La Surprise", "La Surprise");
		SHIP_RENAME_MAP.put("L'Atalante", "L Atalante");
		SHIP_RENAME_MAP.put("L Atalante", "L Atalante");
		SHIP_RENAME_MAP.put("L'Atlante", "L Atalante");
		SHIP_RENAME_MAP.put("Lilooet", "Lilooet");
		SHIP_RENAME_MAP.put("L. M. Gould", "Laurence M. Gould");
		SHIP_RENAME_MAP.put("L.M. Gould", "Laurence M. Gould");
		SHIP_RENAME_MAP.put("Laurence M. Gould", "Laurence M. Gould");
		SHIP_RENAME_MAP.put("Malcolm Baldrige", "Malcolm Baldrige");
		SHIP_RENAME_MAP.put("Marcus G. Langseth", "Marcus G. Langseth");
		SHIP_RENAME_MAP.put("Maria S. Merian", "Maria S. Merian");
		SHIP_RENAME_MAP.put("Marion Dufresne", "Marion Dufresne");
		SHIP_RENAME_MAP.put("Maurice Ewing", "Maurice Ewing");
		SHIP_RENAME_MAP.put("McArthur II", "McArthur II");
		SHIP_RENAME_MAP.put("Melville", "Melville");
		SHIP_RENAME_MAP.put("Meteor", "Meteor");
		SHIP_RENAME_MAP.put("Miller Freeman", "Miller Freeman");
		SHIP_RENAME_MAP.put("Mirai", "Mirai");
		SHIP_RENAME_MAP.put("Mooring", "Mooring");
		SHIP_RENAME_MAP.put("Munida", "Munida");
		SHIP_RENAME_MAP.put("Mytilus", "Mytilus");
		SHIP_RENAME_MAP.put("Natalie Schulte", "Natalie Schulte");
		SHIP_RENAME_MAP.put("Nathaniel B. Palmer", "Nathaniel B. Palmer");
		SHIP_RENAME_MAP.put("Natsushima", "Natsushima");
		SHIP_RENAME_MAP.put("Norcliff", "Norcliff");
		SHIP_RENAME_MAP.put("Nuka Arctica", "Nuka Arctica");
		SHIP_RENAME_MAP.put("Oceanographer", "Oceanographer");
		SHIP_RENAME_MAP.put("Oceanus", "Oceanus");
		SHIP_RENAME_MAP.put("Oden", "Oden");
		SHIP_RENAME_MAP.put("Parizeau", "Parizeau");
		SHIP_RENAME_MAP.put("Pelagia", "Pelagia");
		SHIP_RENAME_MAP.put("PMEL/Natalie Schulte", "Natalie Schulte");
		SHIP_RENAME_MAP.put("Polaris II", "Polaris II");
		SHIP_RENAME_MAP.put("Polarstar", "Polarstar");
		SHIP_RENAME_MAP.put("Polar Star", "Polar Star");
		SHIP_RENAME_MAP.put("Polarstern", "Polarstern");
		SHIP_RENAME_MAP.put("Poseidon", "Poseidon");
		SHIP_RENAME_MAP.put("Pride of Bilboa", "Pride of Bilboa");
		SHIP_RENAME_MAP.put("Prince Madog", "Prince Madog");
		SHIP_RENAME_MAP.put("Prince of Seas", "Prince of Seas");
		SHIP_RENAME_MAP.put("Puerto Deseado", "Puerto Deseado");
		SHIP_RENAME_MAP.put("Pyxis", "Pyxis");
		SHIP_RENAME_MAP.put("Quadra", "Quadra");
		SHIP_RENAME_MAP.put("Quima", "Quima");
		SHIP_RENAME_MAP.put("Rabelais", "Rabelais");
		SHIP_RENAME_MAP.put("Rio Blanco", "Rio Blanco");
		SHIP_RENAME_MAP.put("Roger Revelle", "Roger Revelle");
		SHIP_RENAME_MAP.put("Ronald Brown", "Ronald H. Brown");
		SHIP_RENAME_MAP.put("Ronald H. Brown", "Ronald H. Brown");
		SHIP_RENAME_MAP.put("R/V AEGAEO", "R/V Aegaeo");
		SHIP_RENAME_MAP.put("R/V Aegaeo", "R/V Aegaeo");
		SHIP_RENAME_MAP.put("Ryofu Maru", "Ryofu Maru");
		SHIP_RENAME_MAP.put("S. A. Agulhas", "S.A. Agulhas");
		SHIP_RENAME_MAP.put("S.A. Agulhas", "S.A. Agulhas");
		SHIP_RENAME_MAP.put("Santa Lucia", "Santa Lucia");
		SHIP_RENAME_MAP.put("Santa Maria", "Santa Maria");
		SHIP_RENAME_MAP.put("Skaugran", "Skaugran");
		SHIP_RENAME_MAP.put("Skogafoss", "Skogafoss");
		SHIP_RENAME_MAP.put("Sogen Maru", "Sogen Maru");
		SHIP_RENAME_MAP.put("Sonne", "Sonne");
		SHIP_RENAME_MAP.put("Southern Surveyor", "Southern Surveyor");
		SHIP_RENAME_MAP.put("Soyo Maru", "Soyo Maru");
		SHIP_RENAME_MAP.put("Station M", "Mooring");
		SHIP_RENAME_MAP.put("Taisei Maru", "Taisei Maru");
		SHIP_RENAME_MAP.put("Tangaroa", "Tangaroa");
		SHIP_RENAME_MAP.put("Tethys 2", "Tethys II");
		SHIP_RENAME_MAP.put("Tethyss II", "Tethys II");
		SHIP_RENAME_MAP.put("Tethys II", "Tethys II");
		SHIP_RENAME_MAP.put("Thalassa", "Thalassa");
		SHIP_RENAME_MAP.put("Thomas G. Thompson", "Thomas G. Thompson");
		SHIP_RENAME_MAP.put("Tianjin", "Tianjin");
		SHIP_RENAME_MAP.put("Times Series", "Times Series");
		SHIP_RENAME_MAP.put("Trans Carrier", "Trans Carrier");
		SHIP_RENAME_MAP.put("Trans Future 5", "Trans Future 5");
		SHIP_RENAME_MAP.put("Trans Future-5", "Trans Future 5");
		SHIP_RENAME_MAP.put("Unknown", "unknown");
		SHIP_RENAME_MAP.put("unknown", "unknown");
		SHIP_RENAME_MAP.put("Vancouver", "Vancouver");
		SHIP_RENAME_MAP.put("VOS Finnpartner", "VOS Finnpartner");
		SHIP_RENAME_MAP.put("Wakataka Maru", "Wakataka Maru");
		SHIP_RENAME_MAP.put("Weatherbird II", "Weatherbird II");
		SHIP_RENAME_MAP.put("Wecoma", "Wecoma");
		SHIP_RENAME_MAP.put("Wellington Maru", "Wellington Maru");
		SHIP_RENAME_MAP.put("Xue Long", "Xue Long");
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

		// If unchanged, nothing to do
		if ( newPiNames.equals(piNames) ) {
			System.err.println(expocode + ": PI names unchanged");
			return;
		}

		try {
			// Try to just change the names in the existing DSG files
			String varName = Constants.SHORT_NAMES.get(Constants.scienceGroup_VARNAME);
			dsgFile.updateStringVarValue(varName, newPiNames);
			CruiseDsgNcFile decDsgFile = dsgHandler.getDecDsgNcFile(expocode);
			decDsgFile.updateStringVarValue(varName, newPiNames);
			System.err.println(expocode + ": PI names changed in place");
		} catch (InvalidRangeException ex) {
			// Names longer than allotted space; regenerate the DSG files
			dsgFile.read(false);
			ArrayList<SocatCruiseData> dataList = dsgFile.getDataList();
			mdata = dsgFile.getMetadata();
			mdata.setScienceGroup(newPiNames);
			// Re-create the full-data DSG file
			dsgFile.create(mdata, dataList);
			// Call Ferret to add lon360 and tmonth (calculated data should be the same)
			SocatTool tool = new SocatTool(ferretConfig);
			tool.init(dsgFile.getPath(), null, expocode, FerretConfig.Action.COMPUTE);
			tool.run();
			if ( tool.hasError() )
				throw new IllegalArgumentException(expocode + ": Failure adding computed variables: " + 
						tool.getErrorMessage());
			// Re-create the decimated-data DSG file 
			dsgHandler.decimateCruise(expocode);
			System.err.println(expocode + ": PI names changed by regenerating the DSG files");
		}
	}

	/**
	 * Standardize the ship/vessel names for the given cruise.
	 * 
	 * @param expocode
	 * 		expocode of the cruise to standardize
	 * @throws IllegalArgumentException 
	 * 		if the DSG file is invalid, 
	 * 		if the vessel name in the DSG file is not recognized
	 * @throws IOException 
	 * 		if there are problems reading or recreating the DSG file
	 * 		or decimated DSG file.
	 * @throws InvalidRangeException 
	 * 		if recreating the DSG file or decimated DSG file throws one
	 * @throws IllegalAccessException 
	 * 		if recreating the DSG file or decimated DSG file throws one
	 */
	public void standardizeShipNames(String expocode) throws IllegalArgumentException, 
						IOException, IllegalAccessException, InvalidRangeException {
		// Get the new ship name from the saved ship name
		CruiseDsgNcFile dsgFile = dsgHandler.getDsgNcFile(expocode);
		dsgFile.read(true);
		SocatMetadata mdata = dsgFile.getMetadata();

		String shipName = mdata.getVesselName().trim();
		String newShipName = SHIP_RENAME_MAP.get(shipName);
		if ( newShipName == null )
			throw new IllegalArgumentException("Ship name not recognized: '" + shipName + "'");

		// If unchanged, nothing to do
		if ( newShipName.equals(shipName) ) {
			System.err.println(expocode + ": Ship name unchanged");
			return;
		}

		try {
			// Try to just change the names in the existing DSG files
			String varName = Constants.SHORT_NAMES.get(Constants.vesselName_VARNAME);
			dsgFile.updateStringVarValue(varName, newShipName);
			CruiseDsgNcFile decDsgFile = dsgHandler.getDecDsgNcFile(expocode);
			decDsgFile.updateStringVarValue(varName, newShipName);
			System.err.println(expocode + ": Ship name changed in place");
		} catch (InvalidRangeException ex) {
			// Name longer than allotted space; regenerate the DSG files
			dsgFile.read(false);
			ArrayList<SocatCruiseData> dataList = dsgFile.getDataList();
			mdata = dsgFile.getMetadata();
			mdata.setVesselName(newShipName);
			// Re-create the full-data DSG file
			dsgFile.create(mdata, dataList);
			// Call Ferret to add lon360 and tmonth (calculated data should be the same)
			SocatTool tool = new SocatTool(ferretConfig);
			tool.init(dsgFile.getPath(), null, expocode, FerretConfig.Action.COMPUTE);
			tool.run();
			if ( tool.hasError() )
				throw new IllegalArgumentException(expocode + ": Failure adding computed variables: " + 
						tool.getErrorMessage());
			// Re-create the decimated-data DSG file 
			dsgHandler.decimateCruise(expocode);
			System.err.println(expocode + ": Ship name changed by regenerating the DSG files");
		}
	}

}
