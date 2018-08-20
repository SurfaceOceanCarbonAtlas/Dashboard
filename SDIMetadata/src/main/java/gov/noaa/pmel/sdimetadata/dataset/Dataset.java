package gov.noaa.pmel.sdimetadata.dataset;

import java.util.ArrayList;
import java.util.Date;

public class Dataset {

    protected String id;
    protected String name;
    protected String funding;
    protected ArrayList<Datestamp> history;
    protected Datestamp startDatestamp;
    protected Datestamp endDatestamp;

    public Dataset() {
        id = "";
        name = "";
        funding = "";
        history = new ArrayList<Datestamp>();
        startDatestamp = new Datestamp();
        endDatestamp = new Datestamp();
    }


    /**
     * @return the starting date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getStartDatestamp() {
        return startDatestamp;
    }

    /**
     * @param startDatestamp
     *         assign as the starting date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setStartDatestamp(Datestamp startDatestamp) {
        this.startDatestamp = (startDatestamp != null) ? startDatestamp : new Datestamp();
    }

    /**
     * @return the ending date for this dataset; never null but may be an invalid Datestamp
     */
    public Datestamp getEndDatestamp() {
        return endDatestamp;
    }

    /**
     * @param endDatestamp
     *         assign as the ending date for this dataset;
     *         if null, an invalid Datestamp will be assigned.
     */
    public void setEndDatestamp(Datestamp endDatestamp) {
        this.endDatestamp = (endDatestamp != null) ? endDatestamp : new Datestamp();
    }

}
