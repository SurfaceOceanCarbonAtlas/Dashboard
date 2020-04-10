package gov.noaa.pmel.socatmetadata.shared.variable;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Types of methods for obtaining values of a variable.
 */
public enum MethodType implements Serializable, IsSerializable {
    /**
     * method not yet specified
     */
    UNSPECIFIED,
    /**
     * measured from a sensor during sampling
     */
    MEASURED_INSITU,
    /**
     * measured from a sample collected then later analyzed
     */
    MEASURED_DISCRETE,
    /**
     * manipulation condition
     */
    MANIPULATION,
    /**
     * biological response
     */
    RESPONSE,
    /**
     * computed from other values
     */
    COMPUTED
}
