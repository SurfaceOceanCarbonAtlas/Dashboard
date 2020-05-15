package gov.noaa.pmel.dashboard.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.socatmetadata.shared.variable.Variable;

import java.util.HashSet;

public abstract class VariablePanel extends Composite {

    protected final Variable vari;
    protected final HTML header;
    protected final VariablesTabPanel parentPanel;

    /**
     * Create an appropriate Panel for this variable.
     *
     * @param vari
     *         variable metadata being edited
     * @param header
     *         tab associated with this Panel
     * @param parentPanel
     *         TabPanel containing and controlling this panel
     */
    public VariablePanel(Variable vari, HTML header, VariablesTabPanel parentPanel) {
        this.vari = vari;
        this.header = header;
        this.parentPanel = parentPanel;
    }

    /**
     * Calls the initWidget method of this Composite with the appropriate Widget
     * and then call the finishInitialization method.
     */
    public abstract void initialize();

    /**
     * Finish the initialization steps that come after the call to initWidget.
     * This method should be extended to initialize widgets that are added in subclasses
     * and then the superclass method called.
     */
    protected void finishInitialization() {
        markInvalids(null);
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     * This method should be extended to indicate invalid and acceptable fields for widgets
     * that are added in subclasses.  The set of invalid field names, if known, is passed
     * along simply to prevent repeated calling of the invalidFieldNames method of the variable.
     *
     * @param invalids
     *         set of invalid field names; if null, set is obtained by calling
     *         the invalidFieldNames method of the variable
     */
    protected void markInvalids(HashSet<String> invalids) {
        if ( invalids == null )
            invalids = vari.invalidFieldNames();

        String oldVal = header.getHTML();
        SafeHtml val = SafeHtmlUtils.fromString(vari.getReferenceName());
        if ( !invalids.isEmpty() )
            val = UploadDashboard.invalidLabelHtml(val);
        if ( !val.asString().equals(oldVal) )
            header.setHTML(val);
    }

    /**
     * @return the updated Variable; never null
     */
    public Variable getUpdatedVariable() {
        return vari;
    }

}
