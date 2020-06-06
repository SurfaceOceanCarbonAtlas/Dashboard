/**
 * This software is provided by NOAA for full, free and open release.  It is
 * understood by the recipient/user that NOAA assumes no liability for any
 * errors contained in the code.  Although this software is released without
 * conditions or restrictions in its use, it is expected that appropriate
 * credit be given to its author and to the National Oceanic and Atmospheric
 * Administration should the software be included by the recipient as an
 * element in other product development. 
 */
package gov.noaa.pmel.tmap.las.client.serializable;



import java.util.Vector;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author rhs
 *
 */
public class DatasetSerializable extends Serializable implements IsSerializable {

    String CATID;
    public void setCATID(String catid) {
        CATID = catid;
    }
    public String getCATID() {
        return CATID;
    }
     VariableSerializable[] variablesSerializable;
	/**
	 * 
	 */
	public DatasetSerializable() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the variablesSerializable
	 */
	public VariableSerializable[] getVariablesSerializable() {
		return variablesSerializable;
	}
	
	/**
	 * @param variablesSerializable the variablesSerializable to set
	 */
	public void setVariablesSerializable(
			VariableSerializable[] variablesSerializable) {
		this.variablesSerializable = variablesSerializable;
	}

    public Vector<VariableSerializable> getVariablesSerializableAsVector() {
        Vector<VariableSerializable> vecs = new Vector<VariableSerializable>();
        for(int i = 0; i < variablesSerializable.length; i++ ) {
            vecs.add(variablesSerializable[i]);
        }
        return vecs;
    }
}
