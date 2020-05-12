package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.Composite;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

public abstract class VariablePanel extends Composite {

    /**
     * @return the updated Variable; never null
     */
    public abstract Variable getUpdatedVariable();

}
