package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.AirPressure;

public class AirPressureVarPanel extends VariablePanel {
    public AirPressureVarPanel(AirPressure var, HTML header) {
        super(var, header);
    }
}
