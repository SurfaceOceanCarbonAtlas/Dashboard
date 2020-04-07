package gov.noaa.pmel.socatmetadata.shared.platform;

public enum PlatformType {
    UNKNOWN,
    SHIP,
    MOORING,
    DRIFTING_BUOY;

    /**
     * The argument is trimmed, all characters converted to uppercase, and all space characters replaced with
     * underscore characters, then {@link PlatformType#valueOf(String)} is called with the resulting string.
     * If successful, that value is returned.  If not successful and the resulting string matches "BUOY",
     * {@link #MOORING} is returned.  Otherwise, {@link #UNKNOWN} is returned.
     *
     * @param repr
     *         string representation to examine
     *
     * @return PlatformType matching this representation, or {@link #UNKNOWN} if repr is null or is not known.
     */
    public static PlatformType parse(String repr) {
        if ( repr == null )
            return UNKNOWN;
        String strVal = repr.trim();
        if ( strVal.isEmpty() )
            return UNKNOWN;
        strVal = strVal.toUpperCase().replaceAll(" ", "_");
        PlatformType type;
        try {
            type = PlatformType.valueOf(strVal);
        } catch ( Exception ex ) {
            // If not known, "BUOY" matches MOORING
            if ( "BUOY".equals(strVal) )
                type = MOORING;
            else
                type = UNKNOWN;
        }
        return type;
    }

    @Override
    public String toString() {
        switch ( this ) {
            case UNKNOWN:
                return "Unknown";
            case SHIP:
                return "Ship";
            case MOORING:
                return "Mooring";
            case DRIFTING_BUOY:
                return "Drifting Buoy";
            default:
                throw new RuntimeException("Unexpected PlatformType of " + super.toString());

        }
    }

}

