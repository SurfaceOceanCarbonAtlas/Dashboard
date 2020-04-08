package gov.noaa.pmel.dashboard.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple pair of a list of known data column types and a dataset with its data
 *
 * @author Karl Smith
 */
public class TypesDatasetDataPair implements Serializable, IsSerializable {

    private static final long serialVersionUID = -3743012223467969206L;

    protected ArrayList<DataColumnType> allKnownTypes;
    protected DashboardDatasetData datasetData;

    public TypesDatasetDataPair() {
        allKnownTypes = null;
        datasetData = null;
    }

    /**
     * @return list of all known data types; the actual list in this instance (not a copy) is returned
     */
    public ArrayList<DataColumnType> getAllKnownTypes() {
        return allKnownTypes;
    }

    /**
     * @param allKnownTypes
     *         the list of all known data types to set; the given list (not a copy) is assigned to this instance
     */
    public void setAllKnownTypes(ArrayList<DataColumnType> allKnownTypes) {
        this.allKnownTypes = allKnownTypes;
    }

    /**
     * @return the dashboard dataset with data; the actual list in this instance (not a copy) is returned
     */
    public DashboardDatasetData getDatasetData() {
        return datasetData;
    }

    /**
     * @param datasetData
     *         the dashboard dataset with data to set; the given dashboard dataset with data (not a copy) is assigned to
     *         this instance
     */
    public void setDatasetData(DashboardDatasetData datasetData) {
        this.datasetData = datasetData;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 0;
        if ( allKnownTypes != null )
            result += allKnownTypes.hashCode();
        result *= prime;
        if ( datasetData != null )
            result += datasetData.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;

        if ( !(obj instanceof TypesDatasetDataPair) )
            return false;
        TypesDatasetDataPair other = (TypesDatasetDataPair) obj;

        if ( allKnownTypes == null ) {
            if ( other.allKnownTypes != null ) {
                return false;
            }
        }
        else if ( !allKnownTypes.equals(other.allKnownTypes) ) {
            return false;
        }

        if ( datasetData == null ) {
            if ( other.datasetData != null ) {
                return false;
            }
        }
        else if ( !datasetData.equals(other.datasetData) ) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "TypesDatasetDataPair[\n" +
                "allKnownTypes=" + allKnownTypes.toString() + ",\n" +
                "datasetData=" + datasetData.toString() + "\n" +
                "]";
    }

}
