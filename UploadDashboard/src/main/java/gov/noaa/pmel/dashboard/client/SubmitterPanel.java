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
        super(submitter, null);
        this.submitter = submitter;
    }

    // Since there are no additional fields in Submitter, and InvestigatorPanel works with exactly
    // the Investigator object passed to it, there is not much to add here.

    /**
     * @return the updated Submitter; never null
     */
    public Submitter getUpdatedSumitter() {
        return submitter;
    }

}
