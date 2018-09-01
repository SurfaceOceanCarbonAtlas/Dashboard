package gov.noaa.pmel.sdimetadata.variable;

/**
 * Types of methods for obtaining values of a variable.
 */
public enum MethodType {
    /**
     * method not yet specified
     */
    UNSPECIFIED,
    /**
     * measured from a sensor during collection
     */
    MEASURED_INSITU,
    /**
     * measured from a sample collected then later analyzed
     */
    MEASURED_DISCRETE,
    /**
     * computed from other values
     */
    COMPUTED
}

