package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.DataVar;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

public class DataVarPanel extends VariablePanel {

    public DataVarPanel(DataVar var, HTML header) {
        super(var, header);
    }

}
