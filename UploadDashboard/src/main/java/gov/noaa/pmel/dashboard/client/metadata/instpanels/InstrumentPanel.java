package gov.noaa.pmel.dashboard.client.metadata.instpanels;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import gov.noaa.pmel.dashboard.client.UploadDashboard;
import gov.noaa.pmel.socatmetadata.shared.instrument.Instrument;

import java.util.HashSet;

public abstract class InstrumentPanel extends Composite {

    protected final Instrument instr;
    protected final HTML header;
    protected final InstrumentsTabPanel parentPanel;

    /**
     * Create an appropriate Panel for this instrument
     *
     * @param instr
     *         instrument metadata being edited
     * @param header
     *         tab associated with this Panel
     * @param parentPanel
     *         TabPanel containing and controlling this panel
     */
    public InstrumentPanel(Instrument instr, HTML header, InstrumentsTabPanel parentPanel) {
        this.instr = instr;
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
     * This method should be extended to initialize widgets that are added in
     * subclasses and then the superclass method called.  The top level version
     * of this method calls the markInvalids method.
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
            invalids = instr.invalidFieldNames();

        String oldVal = header.getHTML();
        SafeHtml val = SafeHtmlUtils.fromString(instr.getReferenceName());
        if ( !invalids.isEmpty() )
            val = UploadDashboard.invalidLabelHtml(val);
        if ( !val.asString().equals(oldVal) )
            header.setHTML(val);
    }

    /**
     * @return the updated Instrument; never null
     */
    public Instrument getUpdatedInstrument() {
        return instr;
    }

}
