package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.Temperature;

public class TemperatureVarPanel extends VariablePanel {

    public TemperatureVarPanel(Temperature var, HTML header) {
        super(var, header);
    }

}
