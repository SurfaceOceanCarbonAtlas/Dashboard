package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.AquGasConc;

public class AquGasConcVarPanel extends VariablePanel {
    public AquGasConcVarPanel(AquGasConc var, HTML header) {
        super(var, header);
    }
}
