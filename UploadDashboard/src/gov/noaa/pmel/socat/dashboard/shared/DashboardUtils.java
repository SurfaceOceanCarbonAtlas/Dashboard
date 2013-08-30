/**
 * 
 */
package gov.noaa.pmel.socat.dashboard.shared;

import java.util.LinkedHashMap;
import java.util.Map;

import com.googlecode.gwt.crypto.client.TripleDesCipher;

/**
 * Static dashboard utility functions 
 * for use on both the client and server side.
 *  
 * @author Karl Smith
 */
public class DashboardUtils {

	/**
	 * Generate the encrypted userhash and passhash for a given 
	 * plaintext username and password.
	 * 
	 * @param username
	 * 		plaintext username to use
	 * @param password
	 * 		plaintext password to use 
	 * @return
	 * 		encrypted username and password as an array of two Strings;
	 * 		these Strings will be empty if an error occurs 
	 */
	public static String[] hashesFromPlainText(String username, String password) {
		// Make sure something reasonable Strings are given
		if ( (username.length() < 4) || (password.length() < 7) ) {
			return new String[] { "", "" };
		}

		// This salt is just to make sure the keys are long enough
		String salt = "4z#Ni!q?F7b0m9nK(uDF[g%T";
		TripleDesCipher cipher = new TripleDesCipher();

		// Encrypt the username
		cipher.setKey((password.substring(0,4) + username + salt)
				.substring(0,24).getBytes());
		String passhash;
		try {
			passhash = cipher.encrypt(password);
		} catch (Exception ex) {
			passhash = "";
		}

		// Encrypt the password
		cipher.setKey((username.substring(0,4) + password + salt)
			  .substring(0,24).getBytes());
		String userhash;
		try {
			userhash = cipher.encrypt(username);
		} catch (Exception ex) {
			userhash = "";
		}

		return new String[] {userhash, passhash};
	}

	/**
	 * Decodes a JSON-encoded array of numbers into a byte array. 
	 * 
	 * @param arrayStr
	 * 		JSON-encoded array of byte values to use
	 * @return
	 * 		a byte array represented arrayStr
	 * @throws NumberFormatException
	 * 		if keyStr does not start with '[', does not end with ']', 
	 * 		or contains values inappropriate for the byte type
	 */
	public static byte[] decodeByteArray(String arrayStr) 
											throws NumberFormatException {
		if ( ! ( arrayStr.startsWith("[") && arrayStr.endsWith("]") ) )
			throw new NumberFormatException(
					"Encoded byte array not enclosed in brackets");
		String[] pieces = arrayStr.substring(1, arrayStr.length()-1)
								  .split("\\s*,\\s*");
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return new byte[0];
		byte[] byteArray = new byte[pieces.length];
		for (int k = 0; k < pieces.length; k++)
			byteArray[k] = Byte.parseByte(pieces[k].trim());
		return byteArray;
	}

	/**
	 * JSON encodes a byte array
	 * 
	 * @param byteArray
	 * 		byte array to encode
	 * @return
	 * 		String containing the JSON-encoded byte array
	 */
	public static String encodeByteArray(byte[] byteArray) {
		StringBuilder strBldr = new StringBuilder();
		boolean first = true;
		strBldr.append("[ ");
		for ( byte b : byteArray ) {
			if ( ! first )
				strBldr.append(", ");
			else
				first = false;
			strBldr.append(Byte.toString(b));
		}
		strBldr.append(" ]");
		return strBldr.toString();
	}

	/**
	 * Decode a JSON-encoded map of strings to strings
	 * 
	 * @param mapStr
	 * 		JSON-encoded map of strings to strings
	 * @return
	 * 		map of strings to strings represented by mapStr
	 * @throws IllegalArgumentException
	 * 		if mapStr does not start with '{', does not end with '}' 
	 */
	public static Map<String,String> decodeStrStrMap(String mapStr) 
										throws IllegalArgumentException {
		if ( ! ( mapStr.startsWith("{") && mapStr.endsWith("}") ) )
			throw new IllegalArgumentException(
					"Encoded map not enclosed in braces");

		LinkedHashMap<String,String> mapping = 
				new LinkedHashMap<String,String>();
		String[] pieces = mapStr.substring(1, mapStr.length()-1).trim()
								.split("\"\\s*,\\s*\"");
		if ( (pieces.length == 1) && pieces[0].trim().isEmpty() )
			return mapping;

		if ( pieces.length > 0 ) {
			if ( ! pieces[0].startsWith("\"") )
				throw new IllegalArgumentException(
						"first map key:value does not start with a double quote");
			pieces[0] = pieces[0].substring(1);
			String lastPiece = pieces[pieces.length - 1];
			if ( ! lastPiece.endsWith("\"") )
				throw new IllegalArgumentException(
						"last map key:value does not end with a double quote");
			pieces[pieces.length - 1] = 
					lastPiece.substring(0, lastPiece.length() - 1);
		}

		for ( String mapPiece : pieces ) {
			String[] strStr = mapPiece.split("\"\\s*:\\s*\"");
			if ( strStr.length != 2 )
				throw new IllegalArgumentException(
						"Invalid string-string mapping " + mapPiece);
			mapping.put(strStr[0], strStr[1]);
		}

		return mapping;
	}

	/**
	 * JSON encode a mapping of strings to strings
	 *  
	 * @param mapping
	 * 		mapping of string to strings to encode
	 * @return
	 * 		String containing the JSON-encoded mapping
	 */
	public static String encodeStrStrMap(Map<String,String> mapping) {
		StringBuilder strBldr = new StringBuilder();
		boolean first = true;
		strBldr.append("{ ");
		for ( Map.Entry<String,String> entry : mapping.entrySet() ) {
			if ( ! first )
				strBldr.append(", ");
			else
				first = false;
			strBldr.append("\"");
			strBldr.append(entry.getKey());
			strBldr.append("\":\"");
			strBldr.append(entry.getValue());
			strBldr.append("\"");
		}
		strBldr.append(" }");
		return strBldr.toString();
	}

}
