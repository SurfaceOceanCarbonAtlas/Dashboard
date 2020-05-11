package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.GasConc;

public class GasConcVarPanel extends VariablePanel {
    public GasConcVarPanel(GasConc var, HTML header) {
        super(var, header);
    }
}
