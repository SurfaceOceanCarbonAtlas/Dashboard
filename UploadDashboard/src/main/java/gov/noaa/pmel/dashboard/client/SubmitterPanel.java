package gov.noaa.pmel.dashboard.client;

import gov.noaa.pmel.socatmetadata.shared.person.Submitter;

public class SubmitterPanel extends InvestigatorPanel {

    private Submitter submitter;

    /**
     * Creates a FlowPanel associated with the given Investigator.
     *
     * @param submitter
     *         associate this panel with this Investigator; cannot be null
     */
    public SubmitterPanel(Submitter submitter) {
        super(submitter);
        this.submitter = submitter;
    }

    // The fields in submitter is directly modified by the InvestigatorPanel superclass
    // so there is little more to add here.

    /**
     * @return the updated Submitter; never null
     */
    public Submitter getUpdatedSumitter() {
        return submitter;
    }

}
