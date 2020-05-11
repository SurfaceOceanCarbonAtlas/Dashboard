package gov.noaa.pmel.dashboard.client;

import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.BioDataVar;

public class BioDataVarPanel extends VariablePanel {
    public BioDataVarPanel(BioDataVar var, HTML header) {
        super(var, header);
    }
}
