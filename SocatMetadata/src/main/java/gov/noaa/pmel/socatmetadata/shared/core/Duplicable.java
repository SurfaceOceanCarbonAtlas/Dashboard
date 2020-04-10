package gov.noaa.pmel.socatmetadata.shared.core;

public interface Duplicable {
    /**
     * Deeply copy the values in this Object to those in the given Object.
     * If the given Object is null, create a new Object of the same class
     * as this instance to copy values into.
     * <p>
     * If this class is a subclass of a Duplicable class, the super.duplicate method
     * should be called with a non-null Object before copying objects in this Object;
     * for instance,
     * <pre>
     * class MySuper implements Duplicable {
     *     ...
     *     @Override
     *     Object duplicate(Object dup) {
     *         MySuper mysuper;
     *         if ( dup == null )
     *             mysuper = new MySuper();
     *         else
     *             mysuper = (MySuper) dup;
     *         ...
     *         return mysuper;
     *     }
     * }
     * class MySub extends MySuper implements Duplicable {
     *     ...
     *     @Override
     *     Object duplicate(Object dup) {
     *         MySub mysub;
     *         if ( dup == null )
     *             mysub = new MySub();
     *         else
     *             mysub = (MySub) dup;
     *         super.duplicate(mysub);
     *         ...
     *         return mysub;
     *     }
     * }
     * </pre>
     *
     * @param dup
     *         the Object to copy values into
     *
     * @return the update Object
     */
    public Object duplicate(Object dup);

}
