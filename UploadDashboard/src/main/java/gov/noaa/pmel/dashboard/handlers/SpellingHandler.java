package gov.noaa.pmel.dashboard.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Corrects misspelled names.  Also provides static methods that "anglicize" non-ASCII letters
 * (for software that cannot handle non-ASCII letters) that will later need to be corrected
 * for final reports.
 */
public class SpellingHandler {

    // Use the Unicode code points to define these characters
    // so we know exactly what value is being used in the String
    public static final String acute = "\u00B4";
    public static final String AGrave = "\u00C0";
    public static final String AAcute = "\u00C1";
    public static final String AHat = "\u00C2";
    public static final String ATilde = "\u00C3";
    public static final String AUmlaut = "\u00C4";
    public static final String ARing = "\u00C5";
    public static final String AEMerge = "\u00C6";
    public static final String CCedilla = "\u00C7";
    public static final String EGrave = "\u00C8";
    public static final String EAcute = "\u00C9";
    public static final String EHat = "\u00CA";
    public static final String EUmlaut = "\u00CB";
    public static final String IGrave = "\u00CC";
    public static final String IAcute = "\u00CD";
    public static final String IHat = "\u00CE";
    public static final String IUmlaut = "\u00CF";
    public static final String DBar = "\u00D0";
    public static final String NTilde = "\u00D1";
    public static final String OGrave = "\u00D2";
    public static final String OAcute = "\u00D3";
    public static final String OHat = "\u00D4";
    public static final String OTilde = "\u00D5";
    public static final String OUmlaut = "\u00D6";
    public static final String OStroke = "\u00D8";
    public static final String UGrave = "\u00D9";
    public static final String UAcute = "\u00DA";
    public static final String UHat = "\u00DB";
    public static final String UUmlaut = "\u00DC";
    public static final String YAcute = "\u00DD";
    public static final String Thorn = "\u00DE";
    public static final String eszett = "\u00DF";
    public static final String aGrave = "\u00E0";
    public static final String aAcute = "\u00E1";
    public static final String aHat = "\u00E2";
    public static final String aTilde = "\u00E3";
    public static final String aUmlaut = "\u00E4";
    public static final String aRing = "\u00E5";
    public static final String aeMerge = "\u00E6";
    public static final String cCedilla = "\u00E7";
    public static final String eGrave = "\u00E8";
    public static final String eAcute = "\u00E9";
    public static final String eHat = "\u00EA";
    public static final String eUmlaut = "\u00EB";
    public static final String iGrave = "\u00EC";
    public static final String iAcute = "\u00ED";
    public static final String iHat = "\u00EE";
    public static final String iUmlaut = "\u00EF";
    public static final String dBar = "\u00F0";
    public static final String nTilde = "\u00F1";
    public static final String oGrave = "\u00F2";
    public static final String oAcute = "\u00F3";
    public static final String oHat = "\u00F4";
    public static final String oTilde = "\u00F5";
    public static final String oUmlaut = "\u00F6";
    public static final String oStroke = "\u00F8";
    public static final String uGrave = "\u00F9";
    public static final String uAcute = "\u00FA";
    public static final String uHat = "\u00FB";
    public static final String uUmlaut = "\u00FC";
    public static final String yAcute = "\u00FD";
    public static final String thorn = "\u00FE";
    public static final String yUmlaut = "\u00FF";

    private static final HashMap<Character,String> ANGLICIZE_MAP;

    static {
        ANGLICIZE_MAP = new HashMap<Character,String>();
        ANGLICIZE_MAP.put("'".charAt(0), " ");
        ANGLICIZE_MAP.put("`".charAt(0), " ");
        ANGLICIZE_MAP.put(acute.charAt(0), " ");
        ANGLICIZE_MAP.put(AGrave.charAt(0), "A");
        ANGLICIZE_MAP.put(AAcute.charAt(0), "A");
        ANGLICIZE_MAP.put(AHat.charAt(0), "A");
        ANGLICIZE_MAP.put(ATilde.charAt(0), "A");
        ANGLICIZE_MAP.put(AUmlaut.charAt(0), "Ae");
        ANGLICIZE_MAP.put(ARing.charAt(0), "Aa");
        ANGLICIZE_MAP.put(AEMerge.charAt(0), "AE");
        ANGLICIZE_MAP.put(CCedilla.charAt(0), "C");
        ANGLICIZE_MAP.put(EGrave.charAt(0), "E");
        ANGLICIZE_MAP.put(EAcute.charAt(0), "E");
        ANGLICIZE_MAP.put(EHat.charAt(0), "E");
        ANGLICIZE_MAP.put(EUmlaut.charAt(0), "E");
        ANGLICIZE_MAP.put(IGrave.charAt(0), "I");
        ANGLICIZE_MAP.put(IAcute.charAt(0), "I");
        ANGLICIZE_MAP.put(IHat.charAt(0), "I");
        ANGLICIZE_MAP.put(IUmlaut.charAt(0), "I");
        ANGLICIZE_MAP.put(DBar.charAt(0), "Th");
        ANGLICIZE_MAP.put(NTilde.charAt(0), "N");
        ANGLICIZE_MAP.put(OGrave.charAt(0), "O");
        ANGLICIZE_MAP.put(OAcute.charAt(0), "O");
        ANGLICIZE_MAP.put(OHat.charAt(0), "O");
        ANGLICIZE_MAP.put(OTilde.charAt(0), "O");
        ANGLICIZE_MAP.put(OUmlaut.charAt(0), "Oe");
        ANGLICIZE_MAP.put(OStroke.charAt(0), "O");
        ANGLICIZE_MAP.put(UGrave.charAt(0), "U");
        ANGLICIZE_MAP.put(UAcute.charAt(0), "U");
        ANGLICIZE_MAP.put(UHat.charAt(0), "U");
        ANGLICIZE_MAP.put(UUmlaut.charAt(0), "Ue");
        ANGLICIZE_MAP.put(YAcute.charAt(0), "Y");
        ANGLICIZE_MAP.put(Thorn.charAt(0), "Th");
        ANGLICIZE_MAP.put(eszett.charAt(0), "ss");
        ANGLICIZE_MAP.put(aGrave.charAt(0), "a");
        ANGLICIZE_MAP.put(aAcute.charAt(0), "a");
        ANGLICIZE_MAP.put(aHat.charAt(0), "a");
        ANGLICIZE_MAP.put(aTilde.charAt(0), "a");
        ANGLICIZE_MAP.put(aUmlaut.charAt(0), "ae");
        ANGLICIZE_MAP.put(aRing.charAt(0), "aa");
        ANGLICIZE_MAP.put(aeMerge.charAt(0), "ae");
        ANGLICIZE_MAP.put(cCedilla.charAt(0), "c");
        ANGLICIZE_MAP.put(eGrave.charAt(0), "e");
        ANGLICIZE_MAP.put(eAcute.charAt(0), "e");
        ANGLICIZE_MAP.put(eHat.charAt(0), "e");
        ANGLICIZE_MAP.put(eUmlaut.charAt(0), "e");
        ANGLICIZE_MAP.put(iGrave.charAt(0), "i");
        ANGLICIZE_MAP.put(iAcute.charAt(0), "i");
        ANGLICIZE_MAP.put(iHat.charAt(0), "i");
        ANGLICIZE_MAP.put(iUmlaut.charAt(0), "i");
        ANGLICIZE_MAP.put(dBar.charAt(0), "th");
        ANGLICIZE_MAP.put(nTilde.charAt(0), "n");
        ANGLICIZE_MAP.put(oGrave.charAt(0), "o");
        ANGLICIZE_MAP.put(oAcute.charAt(0), "o");
        ANGLICIZE_MAP.put(oHat.charAt(0), "o");
        ANGLICIZE_MAP.put(oTilde.charAt(0), "o");
        ANGLICIZE_MAP.put(oUmlaut.charAt(0), "oe");
        ANGLICIZE_MAP.put(oStroke.charAt(0), "oe");
        ANGLICIZE_MAP.put(uGrave.charAt(0), "u");
        ANGLICIZE_MAP.put(uAcute.charAt(0), "u");
        ANGLICIZE_MAP.put(uHat.charAt(0), "u");
        ANGLICIZE_MAP.put(uUmlaut.charAt(0), "ue");
        ANGLICIZE_MAP.put(yAcute.charAt(0), "y");
        ANGLICIZE_MAP.put(thorn.charAt(0), "th");
        ANGLICIZE_MAP.put(yUmlaut.charAt(0), "e");
    }

    /**
     * Returns a new String with replacements for any extended characters, apostrophes, grave symbols, or acute symbols.
     * Extended characters may be replaced by more than one character.  Apostrophes, grave symbols, and acute symbols
     * are replaced by spaces.
     *
     * @param name
     *         String to be copied; can be null
     *
     * @return copy of the String with character replacements, or null if the String was null
     */
    public static String anglicizeName(String name) {
        if ( name == null )
            return null;
        StringBuilder builder = new StringBuilder();
        for (char letter : name.toCharArray()) {
            String replacement = ANGLICIZE_MAP.get(letter);
            if ( replacement != null ) {
                builder.append(replacement);
            }
            else {
                builder.append(letter);
            }
        }
        return builder.toString();
    }

    private HashMap<String,String> nameCorrections;

    /**
     * Corrects names as specified in the given corrections file.
     * Static methods associated with this
     *
     * @param correctionsFilename
     *         file of tab-separated pairs of name, where the first value is the misspelled name
     *         and the second value is the correctly spelling of that name
     *
     * @throws FileNotFoundException
     *         if the file specified in correctionsFilename does not exist
     * @throws IOException
     *         if an entry in the correction file is not a pair of tab-separated values,
     *         if the misspelled name is not unique in all the entries in the corrections file, or
     *         if reading from the corrections file throws one.
     */
    public SpellingHandler(String correctionsFilename) throws FileNotFoundException, IOException {
        nameCorrections = new HashMap<String,String>();
        BufferedReader reader = new BufferedReader(new FileReader(correctionsFilename));
        try {
            for (String dataline = reader.readLine(); dataline != null; dataline = reader.readLine()) {
                String[] parts = dataline.split("\t");
                if ( parts.length != 2 )
                    throw new IOException("entry not a pair of tab-separated values: " + dataline);
                if ( nameCorrections.put(parts[0], parts[1]) != null )
                    throw new IOException("duplicate misspelled name (first value) in: " + dataline);
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Corrects the spelling of the given name where known corrections exist.
     * If the misspelled name is not recognized, the given name is returned;
     * thus, it is safe to call this method on all names, misspelled or not.
     *
     * @param name
     *         possibly misspelled name; if null, null is returned
     *
     * @return corrected name, if the given name was recognized as a misspelled name;
     *         otherwise, the name provided to this method.
     */
    public String correctName(String name) {
        if ( name == null )
            return null;
        String correction = nameCorrections.get(name);
        if ( correction == null )
            return name;
        return correction;
    }

}
