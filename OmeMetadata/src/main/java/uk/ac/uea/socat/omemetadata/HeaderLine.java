package uk.ac.uea.socat.omemetadata;

/**
 * Utility class for handling header lines.
 * Adds a few extra methods over the standard String class.
 * 
 * @author Steve Jones
 *
 */
public class HeaderLine {
	
	private String itsString;

	public HeaderLine(String string) {
		itsString = string.trim();
	}

	protected boolean doesntContain(String chars) {
		return !contains(chars);
	}
	
	protected boolean contains(String chars) {
		return contains(chars, 0);
	}
	
	protected boolean contains(String chars, int start) {
		int charPosition = getCharIndex(chars, start);
		return (charPosition > -1);
	}
	
	protected boolean containsMultiple(String chars) {
		boolean result = false;
		
		int firstInstance = getCharIndex(chars, 0);
		if (firstInstance > -1) {
			result = contains(chars, firstInstance + 1);
		}
		
		return result;
	}
	
	protected int getCharIndex(String chars) {
		return getCharIndex(chars, 0);
	}
	
	protected int getCharIndex(String chars, int start) {
		int result = -1;
		
		// Loop through all the search characters
		for (int charLoop = 0; result == -1 && charLoop < chars.length(); charLoop++) {

			int currentChar = start;
			while (result == -1 && currentChar != -1 && currentChar < itsString.length()) {
				
				// Find the next index of the character
				currentChar = itsString.indexOf(chars.substring(charLoop, charLoop + 1), currentChar);
				
				// If it's found, check the character before to see if it's escaped
				if (currentChar == start) {
					result = currentChar;
				} else if (currentChar > start) {
					if (!itsString.substring(currentChar - 1, currentChar).equals("\\")) {
						result = currentChar;
					}

					currentChar++;
				}
				
			}
		}
		
		return result;
	}
	
	protected String getBefore(String character) {
		int charPosition = getCharIndex(character, 0);
		return substring(0, charPosition).trim();
	}
	
	protected String getAfter(String character) {
		int charPosition = getCharIndex(character, 0);
		return substring(charPosition + 1).trim();
	}
	
	/*
	 * Standard string methods 
	 */
	
	protected int length() {
		return itsString.length();
	}

	protected String substring(int start) {
		return itsString.substring(start);
	}
	
	protected String substring(int start, int end) {
		return itsString.substring(start, end);
	}
	
	public String toString() {
		return itsString;
	}
}
