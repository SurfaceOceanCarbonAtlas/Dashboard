package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;

public interface GridInterface {

    public abstract ArrayList getAxes();

    public abstract void setAxes(ArrayList<Axis> axes);

    public abstract boolean hasX();

    public abstract boolean hasY();

    public abstract boolean hasZ();

    public abstract boolean hasT();

    public abstract Axis getAxis(String type);

    public abstract TimeAxis getTime();

    public abstract void setTime(TimeAxis time);

    public abstract Object clone();

    /**
     * Find out if the grid has an axis of a particular type.
     * @param analysis_axis_type - the axis to check (x, y, z or t).
     * @return
     */
    public abstract boolean hasAxis(String axis_type);

}