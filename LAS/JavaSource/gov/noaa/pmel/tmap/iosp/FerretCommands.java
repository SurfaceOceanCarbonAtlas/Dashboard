/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.iosp;

import java.util.Locale;

/**
 * This is a helper class that is used to hunt through a file and determine
 * if the file (or String) contains Ferret commands.
 * @author Roland Schweitzer
 *
 */
public class FerretCommands {
	static final String forbidden_command[] = {
		"SPAWN"
	};
    static final String command[] = {
    	    "LETDEQ1",                 // the special alias for LAS data source requests...
            "ALIAS ", 
            "CANCEL ",
            "CONTOUR ",
            "DEFINE ",
            "ELIF ",
            "ELSE ",
            "ENDIF ",
            "EXIT ",
            "FILE ",
            "FILL ",
            "FRAME ",
            "GO ",
            "HELP ",
            "IF ",
            "LABEL ",
            "LET ",
            "LIST ",
            "LOAD ",
            "MESSAGE ",
            "PALETTE ",
            "PATTERN ",
            "PAUSE ",
            "PLOT ",
            "POLYGON ",
            "PPLUS ",
            "QUERY ",
            "QUIT ",
            "REPEAT ",
            "SAVE ",
            "SAY ",
            "SET ",
            "SHADE ",
            "SHOW ",
            "SPAWN ",
            "STATISTICS ",
            "UNALIAS ",
            "USE ",
            "USER ",
            "VECTOR ",
            "WHERE ",
            "WIRE "};
    /**
     * Test if a string contains a Ferret command.
     * @param string - the string to test
     * @return true if the string contains a Ferret command; false if it does not.
     */
    static public boolean containsCommand(String string) {
    	if ( string.length() == 0 ) return false;
        for ( int i=0; i<command.length; i++ ) {
            if ( string.toUpperCase(Locale.ENGLISH).contains(command[i]))  {
                return true;
            }
        }
        return false;
    }
    /**
     * Test if a string contains a Ferret command.
     * @param string - the string to test
     * @return true if the string contains a Ferret command; false if it does not.
     */
    static public boolean containsForbiddenCommand(String string) {
        for ( int i=0; i<forbidden_command.length; i++ ) {
            if ( string.toUpperCase(Locale.ENGLISH).contains(forbidden_command[i]))  {
                return true;
            }
        }
        return false;
    }
}
