package gov.noaa.pmel.tmap.addxml;

public class Util {
	public static final char[][] vectorPatterns = new char[][]{{'x', 'X', 'u', 'U'},{'y', 'Y', 'v', 'V'},{'z', 'Z', 'w', 'W'}};
	public static final String[] vectorRanges = new String[]{"[xyz]", "[XYZ]", "[uvw]", "[UVW]"};
	public static final String zonal = "zonal";
	public static final String meridional = "meridional";
	public static final String[] verticalComponentNames = new String[]{"vertical", "upward", "downward"};
	public static final String numberFormat = "####.#######################";
	public static final String zNumbrerFormat = "###############.###############";
	public static int countOccurrences(String haystack, char needle)
	{
	    int count = 0;
	    for (int i=0; i < haystack.length(); i++)
	    {
	        if (haystack.charAt(i) == needle)
	        {
	             count++;
	        }
	    }
	    return count;
	}
}
