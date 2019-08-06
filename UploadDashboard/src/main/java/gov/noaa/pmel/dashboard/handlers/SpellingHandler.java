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
    // so we know exactly what value is being used
    public static final Character acute = '\u00B4';
    public static final Character AGrave = '\u00C0';
    public static final Character AAcute = '\u00C1';
    public static final Character AHat = '\u00C2';
    public static final Character ATilde = '\u00C3';
    public static final Character AUmlaut = '\u00C4';
    public static final Character ARing = '\u00C5';
    public static final Character Ash = '\u00C6';
    public static final Character CCedilla = '\u00C7';
    public static final Character EGrave = '\u00C8';
    public static final Character EAcute = '\u00C9';
    public static final Character EHat = '\u00CA';
    public static final Character EUmlaut = '\u00CB';
    public static final Character IGrave = '\u00CC';
    public static final Character IAcute = '\u00CD';
    public static final Character IHat = '\u00CE';
    public static final Character IUmlaut = '\u00CF';
    public static final Character DBar = '\u00D0';
    public static final Character NTilde = '\u00D1';
    public static final Character OGrave = '\u00D2';
    public static final Character OAcute = '\u00D3';
    public static final Character OHat = '\u00D4';
    public static final Character OTilde = '\u00D5';
    public static final Character OUmlaut = '\u00D6';
    public static final Character OStroke = '\u00D8';
    public static final Character UGrave = '\u00D9';
    public static final Character UAcute = '\u00DA';
    public static final Character UHat = '\u00DB';
    public static final Character UUmlaut = '\u00DC';
    public static final Character YAcute = '\u00DD';
    public static final Character Thorn = '\u00DE';
    public static final Character eszett = '\u00DF';
    public static final Character aGrave = '\u00E0';
    public static final Character aAcute = '\u00E1';
    public static final Character aHat = '\u00E2';
    public static final Character aTilde = '\u00E3';
    public static final Character aUmlaut = '\u00E4';
    public static final Character aRing = '\u00E5';
    public static final Character ash = '\u00E6';
    public static final Character cCedilla = '\u00E7';
    public static final Character eGrave = '\u00E8';
    public static final Character eAcute = '\u00E9';
    public static final Character eHat = '\u00EA';
    public static final Character eUmlaut = '\u00EB';
    public static final Character iGrave = '\u00EC';
    public static final Character iAcute = '\u00ED';
    public static final Character iHat = '\u00EE';
    public static final Character iUmlaut = '\u00EF';
    public static final Character dBar = '\u00F0';
    public static final Character mu = '\u03BC';
    public static final Character nTilde = '\u00F1';
    public static final Character oGrave = '\u00F2';
    public static final Character oAcute = '\u00F3';
    public static final Character oHat = '\u00F4';
    public static final Character oTilde = '\u00F5';
    public static final Character oUmlaut = '\u00F6';
    public static final Character oStroke = '\u00F8';
    public static final Character uGrave = '\u00F9';
    public static final Character uAcute = '\u00FA';
    public static final Character uHat = '\u00FB';
    public static final Character uUmlaut = '\u00FC';
    public static final Character yAcute = '\u00FD';
    public static final Character thorn = '\u00FE';
    public static final Character yUmlaut = '\u00FF';

    private static final HashMap<Character,String> ANGLICIZE_MAP;

    static {
        ANGLICIZE_MAP = new HashMap<Character,String>();
        ANGLICIZE_MAP.put('"', " ");
        ANGLICIZE_MAP.put('“', " ");
        ANGLICIZE_MAP.put('”', " ");
        ANGLICIZE_MAP.put('\'', " ");
        ANGLICIZE_MAP.put('‘', " ");
        ANGLICIZE_MAP.put('’', " ");
        ANGLICIZE_MAP.put('`', " ");
        ANGLICIZE_MAP.put(acute, " ");
        ANGLICIZE_MAP.put(AGrave, "A");
        ANGLICIZE_MAP.put(AAcute, "A");
        ANGLICIZE_MAP.put(AHat, "A");
        ANGLICIZE_MAP.put(ATilde, "A");
        ANGLICIZE_MAP.put(AUmlaut, "Ae");
        ANGLICIZE_MAP.put(ARing, "Aa");
        ANGLICIZE_MAP.put(Ash, "AE");
        ANGLICIZE_MAP.put(CCedilla, "C");
        ANGLICIZE_MAP.put(EGrave, "E");
        ANGLICIZE_MAP.put(EAcute, "E");
        ANGLICIZE_MAP.put(EHat, "E");
        ANGLICIZE_MAP.put(EUmlaut, "E");
        ANGLICIZE_MAP.put(IGrave, "I");
        ANGLICIZE_MAP.put(IAcute, "I");
        ANGLICIZE_MAP.put(IHat, "I");
        ANGLICIZE_MAP.put(IUmlaut, "I");
        ANGLICIZE_MAP.put(DBar, "Th");
        ANGLICIZE_MAP.put(NTilde, "N");
        ANGLICIZE_MAP.put(OGrave, "O");
        ANGLICIZE_MAP.put(OAcute, "O");
        ANGLICIZE_MAP.put(OHat, "O");
        ANGLICIZE_MAP.put(OTilde, "O");
        ANGLICIZE_MAP.put(OUmlaut, "Oe");
        ANGLICIZE_MAP.put(OStroke, "O");
        ANGLICIZE_MAP.put(UGrave, "U");
        ANGLICIZE_MAP.put(UAcute, "U");
        ANGLICIZE_MAP.put(UHat, "U");
        ANGLICIZE_MAP.put(UUmlaut, "Ue");
        ANGLICIZE_MAP.put(YAcute, "Y");
        ANGLICIZE_MAP.put(Thorn, "Th");
        ANGLICIZE_MAP.put(eszett, "ss");
        ANGLICIZE_MAP.put(aGrave, "a");
        ANGLICIZE_MAP.put(aAcute, "a");
        ANGLICIZE_MAP.put(aHat, "a");
        ANGLICIZE_MAP.put(aTilde, "a");
        ANGLICIZE_MAP.put(aUmlaut, "ae");
        ANGLICIZE_MAP.put(aRing, "aa");
        ANGLICIZE_MAP.put(ash, "ae");
        ANGLICIZE_MAP.put(cCedilla, "c");
        ANGLICIZE_MAP.put(eGrave, "e");
        ANGLICIZE_MAP.put(eAcute, "e");
        ANGLICIZE_MAP.put(eHat, "e");
        ANGLICIZE_MAP.put(eUmlaut, "e");
        ANGLICIZE_MAP.put(iGrave, "i");
        ANGLICIZE_MAP.put(iAcute, "i");
        ANGLICIZE_MAP.put(iHat, "i");
        ANGLICIZE_MAP.put(iUmlaut, "i");
        ANGLICIZE_MAP.put(dBar, "th");
        ANGLICIZE_MAP.put(mu, "u");
        ANGLICIZE_MAP.put(nTilde, "n");
        ANGLICIZE_MAP.put(oGrave, "o");
        ANGLICIZE_MAP.put(oAcute, "o");
        ANGLICIZE_MAP.put(oHat, "o");
        ANGLICIZE_MAP.put(oTilde, "o");
        ANGLICIZE_MAP.put(oUmlaut, "oe");
        ANGLICIZE_MAP.put(oStroke, "oe");
        ANGLICIZE_MAP.put(uGrave, "u");
        ANGLICIZE_MAP.put(uAcute, "u");
        ANGLICIZE_MAP.put(uHat, "u");
        ANGLICIZE_MAP.put(uUmlaut, "ue");
        ANGLICIZE_MAP.put(yAcute, "y");
        ANGLICIZE_MAP.put(thorn, "th");
        ANGLICIZE_MAP.put(yUmlaut, "e");
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
     *
     * @param correctionsFilename
     *         file of lines of tab-separated value pairs, where the first value is the misspelled name
     *         and the second value is the correct spelling of that name.  Blank lines or lines starting
     *         with # are ignored.
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
                if ( dataline.startsWith("#") || dataline.trim().isEmpty() )
                    continue;
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
