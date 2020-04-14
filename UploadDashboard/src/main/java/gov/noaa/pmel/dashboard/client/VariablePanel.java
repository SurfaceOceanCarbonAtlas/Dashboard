package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

public class VariablePanel extends Composite {

    interface VariablePanelUiBinder extends UiBinder<FlowPanel,VariablePanel> {
    }

    private static VariablePanelUiBinder uiBinder = GWT.create(VariablePanelUiBinder.class);

    private Variable var;
    private Label header;

    /**
     * Creates a FlowPanel associated with the given Variable.
     *
     * @param var
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header label that should be updated when appropriate values change; cannot be null
     */
    public VariablePanel(Variable var, Label header) {
        initWidget(uiBinder.createAndBindUi(this));

        this.var = var;
        this.header = header;

        header.setText(var.getColName());
    }

    /**
     * @return the updated Variable; never null
     */
    public Variable getUpdatedVariable() {
        return var;
    }
}
