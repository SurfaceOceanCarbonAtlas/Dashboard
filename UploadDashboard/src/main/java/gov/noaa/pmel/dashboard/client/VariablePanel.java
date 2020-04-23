package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

public class VariablePanel extends Composite {

    interface VariablePanelUiBinder extends UiBinder<FlowPanel,VariablePanel> {
    }

    private static VariablePanelUiBinder uiBinder = GWT.create(VariablePanelUiBinder.class);

    @UiField
    Label nameLabel;
    @UiField
    TextBox nameBox;

    private Variable var;
    private TextBox header;

    /**
     * Creates a FlowPanel associated with the given Variable.
     *
     * @param var
     *         associate this panel with this Variable; cannot be null
     * @param header
     *         header that should be updated when appropriate values change; cannot be null
     */
    public VariablePanel(Variable var, TextBox header) {
        initWidget(uiBinder.createAndBindUi(this));

        this.var = var;
        this.header = header;

        nameLabel.setText("Name:");
        String name = var.getColName();
        nameBox.setText(name);
        if ( name.isEmpty() )
            name = "Unknown";
        header.setText(name);
    }

    @UiHandler("nameBox")
    void nameBoxOnValueChange(ValueChangeEvent<String> event) {
        String name = nameBox.getText();
        var.setColName(name);
        if ( name.isEmpty() )
            name = "Unknown";
        header.setText(name);
    }

    /**
     * @return the updated Variable; never null
     */
    public Variable getUpdatedVariable() {
        return var;
    }

}
