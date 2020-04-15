package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import gov.noaa.pmel.socatmetadata.shared.person.Investigator;

public class InvestigatorPanel extends Composite {

    interface InvestigatorPanelUiBinder extends UiBinder<FlowPanel,InvestigatorPanel> {
    }

    private static InvestigatorPanelUiBinder uiBinder = GWT.create(InvestigatorPanelUiBinder.class);

    @UiField
    Label firstLabel;
    @UiField
    TextBox firstBox;
    @UiField
    Label middleLabel;
    @UiField
    TextBox middleBox;
    @UiField
    Label lastLabel;
    @UiField
    TextBox lastBox;
    @UiField
    Label idLabel;
    @UiField
    TextBox idBox;
    @UiField
    Label idTypeLabel;
    @UiField
    TextBox idTypeBox;
    @UiField
    Label organizationLabel;
    @UiField
    TextBox organizationBox;

    private Investigator pi;
    private Label header;

    /**
     * Creates a FlowPanel associated with the given Investigator.
     *
     * @param pi
     *         associate this panel with this Investigator; cannot be null
     * @param header
     *         header label that should be updated when appropriate values change; cannot be null.
     */
    public InvestigatorPanel(Investigator pi, Label header) {
        this(pi);

        this.header = header;
        header.setText(pi.getReferenceName());
    }

    /**
     * Creates a FlowPanel associated with the given Investigator
     * but without an associated header label.
     *
     * @param pi
     *         associate this panel with this Investigator; cannot be null
     */
    protected InvestigatorPanel(Investigator pi) {
        initWidget(uiBinder.createAndBindUi(this));

        this.pi = pi;
        this.header = null;

        firstLabel.setText("First name:");
        firstBox.setText(pi.getFirstName());
        middleLabel.setText("Middle initial(s):");
        middleBox.setText(pi.getMiddle());
        lastLabel.setText("Last name:");
        lastBox.setText(pi.getLastName());
        idLabel.setText("ID:");
        idBox.setText(pi.getId());
        idTypeLabel.setText("ID type:");
        idTypeBox.setText(pi.getIdType());
        organizationLabel.setText("Organization:");
        organizationBox.setText(pi.getOrganization());
    }

    // TODO: complete this panel

    @UiHandler("firstBox")
    void firstBoxOnChange(ChangeEvent event) {
        pi.setFirstName(firstBox.getText());
        if ( header != null )
            header.setText(pi.getReferenceName());
    }

    @UiHandler("middleBox")
    void middleBoxOnChange(ChangeEvent event) {
        pi.setMiddle(middleBox.getText());
        if ( header != null )
            header.setText(pi.getReferenceName());
    }

    @UiHandler("lastBox")
    void lastBoxOnChange(ChangeEvent event) {
        pi.setLastName(lastBox.getText());
        if ( header != null )
            header.setText(pi.getReferenceName());
    }

    @UiHandler("idBox")
    void idBoxOnChange(ChangeEvent event) {
        pi.setId(idBox.getText());
    }

    @UiHandler("idTypeBox")
    void idTypeBoxOnChange(ChangeEvent event) {
        pi.setIdType(idTypeBox.getText());
    }

    @UiHandler("organizationBox")
    void organizationBoxOnChange(ChangeEvent event) {
        pi.setOrganization(organizationBox.getText());
    }

    /**
     * @return the updated Investigator; never null
     */
    public Investigator getUpdatedInvestigator() {
        return pi;
    }

}