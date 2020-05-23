package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import gov.noaa.pmel.dashboard.shared.DashboardDataset;
import gov.noaa.pmel.socatmetadata.shared.core.MiscInfo;
import gov.noaa.pmel.socatmetadata.shared.core.MultiString;

import java.util.HashSet;

public class MiscInfoPanel extends Composite {

    interface MiscInfoPanelUiBinder extends UiBinder<ScrollPanel,MiscInfoPanel> {
    }

    private static final MiscInfoPanelUiBinder uiBinder = GWT.create(MiscInfoPanelUiBinder.class);

    @UiField
    Label headerLabel;
    @UiField(provided = true)
    final LabeledTextBox nameValue;
    @UiField(provided = true)
    final LabeledTextBox sectionValue;
    @UiField(provided = true)
    final LabeledTextBox doiValue;
    @UiField(provided = true)
    final LabeledTextBox accessValue;
    @UiField(provided = true)
    final LabeledTextBox citationValue;
    @UiField(provided = true)
    final LabeledTextBox websiteValue;
    @UiField(provided = true)
    final LabeledTextBox downloadValue;
    @UiField(provided = true)
    final LabeledTextBox fundAgencyValue;
    @UiField(provided = true)
    final LabeledTextBox fundIdValue;
    @UiField(provided = true)
    final LabeledTextBox fundTitleValue;
    @UiField(provided = true)
    final LabeledTextBox projectValue;
    @UiField(provided = true)
    final LabeledTextArea synopsisValue;
    @UiField(provided = true)
    final LabeledTextArea purposeValue;
    @UiField(provided = true)
    final LabeledTextArea refsValue;
    @UiField(provided = true)
    final LabeledTextArea portsValue;
    @UiField(provided = true)
    final LabeledTextArea addnInfoValue;

    private final MiscInfo info;

    /**
     * Creates a FlowPanel associated with the given MiscInfo.
     *
     * @param dataset
     *         the dataset associated with this metadata
     * @param info
     *         associate this panel with this MiscInfo; cannot be null
     */
    public MiscInfoPanel(DashboardDataset dataset, MiscInfo info) {
        nameValue = new LabeledTextBox("Dataset name:", "11em", "20em", null, null);
        sectionValue = new LabeledTextBox("Section/Leg:", "9.75em", "20em", null, null);
        //
        doiValue = new LabeledTextBox("Dataset DOI:", "11em", "20em", null, null);
        accessValue = new LabeledTextBox("Accession ID:", "9.75em", "20em", null, null);
        //
        citationValue = new LabeledTextBox("Citation for dataset:", "11em", "52em", null, null);
        //
        websiteValue = new LabeledTextBox("Website for dataset:", "11em", "52em", null, null);
        //
        downloadValue = new LabeledTextBox("Dataset download URL:", "11em", "52em", null, null);
        //
        fundAgencyValue = new LabeledTextBox("Funding agency:", "11em", "52em", null, null);
        //
        fundIdValue = new LabeledTextBox("Funding ID:", "11em", "12em", null, null);
        fundTitleValue = new LabeledTextBox("Funding title:", "6em", "32em", null, null);
        //
        projectValue = new LabeledTextBox("Research project:", "11em", "52em", null, null);
        //
        synopsisValue = new LabeledTextArea("Project synopsis:", "11em", "4em", "52em");
        //
        purposeValue = new LabeledTextArea("Project Purpose:", "11em", "4em", "52em");
        //
        refsValue = new LabeledTextArea("References:", "11em", "8em", "52em");
        //
        portsValue = new LabeledTextArea("Ports of call:", "11em", "4em", "52em");
        //
        addnInfoValue = new LabeledTextArea("Other information:", "11em", "8em", "52em");

        initWidget(uiBinder.createAndBindUi(this));

        headerLabel.setText(EditSocatMetadataPage.MISC_INFO_TAB_TEXT + " for " + dataset.getDatasetId());

        this.info = info;

        nameValue.setText(info.getDatasetName());
        sectionValue.setText(info.getSectionName());
        doiValue.setText(info.getDatasetDoi());
        accessValue.setText(info.getAccessId());
        citationValue.setText(info.getCitation());
        websiteValue.setText(info.getWebsite());
        downloadValue.setText(info.getDownloadUrl());
        fundAgencyValue.setText(info.getFundingAgency());
        fundTitleValue.setText(info.getFundingTitle());
        fundIdValue.setText(info.getFundingId());
        projectValue.setText(info.getResearchProject());
        synopsisValue.setText(info.getSynopsis());
        purposeValue.setText(info.getPurpose());
        refsValue.setText(info.getReferences().asOneString());
        portsValue.setText(info.getPortsOfCall().asOneString());
        addnInfoValue.setText(info.getAddnInfo().asOneString());

        markInvalids();
    }

    @UiHandler("nameValue")
    void nameValueOnValueChange(ValueChangeEvent<String> event) {
        info.setDatasetName(nameValue.getText());
        markInvalids();
    }

    @UiHandler("sectionValue")
    void sectionValueOnValueChange(ValueChangeEvent<String> event) {
        info.setSectionName(sectionValue.getText());
        markInvalids();
    }

    @UiHandler("doiValue")
    void doiValueOnValueChange(ValueChangeEvent<String> event) {
        info.setDatasetDoi(doiValue.getText());
        markInvalids();
    }

    @UiHandler("accessValue")
    void accessValueOnValueChange(ValueChangeEvent<String> event) {
        info.setAccessId(accessValue.getText());
        markInvalids();
    }

    @UiHandler("citationValue")
    void citationValueOnValueChange(ValueChangeEvent<String> event) {
        info.setCitation(citationValue.getText());
        markInvalids();
    }

    @UiHandler("websiteValue")
    void websiteValueOnValueChange(ValueChangeEvent<String> event) {
        info.setWebsite(websiteValue.getText());
        markInvalids();
    }

    @UiHandler("downloadValue")
    void downloadValueOnValueChange(ValueChangeEvent<String> event) {
        info.setDownloadUrl(downloadValue.getText());
        markInvalids();
    }

    @UiHandler("fundAgencyValue")
    void fundAgencyValueOnValueChange(ValueChangeEvent<String> event) {
        info.setFundingAgency(fundAgencyValue.getText());
        markInvalids();
    }

    @UiHandler("fundIdValue")
    void fundIdValueOnValueChange(ValueChangeEvent<String> event) {
        info.setFundingId(fundIdValue.getText());
        markInvalids();
    }

    @UiHandler("fundTitleValue")
    void fundTitleValueOnValueChange(ValueChangeEvent<String> event) {
        info.setFundingTitle(fundTitleValue.getText());
        markInvalids();
    }

    @UiHandler("projectValue")
    void projectValueOnValueChange(ValueChangeEvent<String> event) {
        info.setResearchProject(projectValue.getText());
        markInvalids();
    }

    @UiHandler("synopsisValue")
    void synopsisValueOnValueChange(ValueChangeEvent<String> event) {
        info.setSynopsis(synopsisValue.getText());
        markInvalids();
    }

    @UiHandler("purposeValue")
    void purposeValueOnValueChange(ValueChangeEvent<String> event) {
        info.setPurpose(purposeValue.getText());
        markInvalids();
    }

    @UiHandler("refsValue")
    void refsValueOnValueChange(ValueChangeEvent<String> event) {
        info.setReferences(new MultiString(refsValue.getText()));
        markInvalids();
    }

    @UiHandler("portsValue")
    void portsValueOnValueChange(ValueChangeEvent<String> event) {
        info.setPortsOfCall(new MultiString(portsValue.getText()));
        markInvalids();
    }

    @UiHandler("addnInfoValue")
    void addnInfoValueOnValueChange(ValueChangeEvent<String> event) {
        info.setAddnInfo(new MultiString(addnInfoValue.getText()));
        markInvalids();
    }

    /**
     * Indicate which fields contain invalid values and which contain acceptable values.
     */
    private void markInvalids() {
        HashSet<String> invalids = info.invalidFieldNames();

        if ( invalids.contains("datasetName") )
            nameValue.markInvalid();
        else
            nameValue.markValid();

        if ( invalids.contains("sectionName") )
            sectionValue.markInvalid();
        else
            sectionValue.markValid();

        if ( invalids.contains("datasetDoi") )
            doiValue.markInvalid();
        else
            doiValue.markValid();

        if ( invalids.contains("accessId") )
            accessValue.markInvalid();
        else
            accessValue.markValid();

        if ( invalids.contains("citation") )
            citationValue.markInvalid();
        else
            citationValue.markValid();

        if ( invalids.contains("website") )
            websiteValue.markInvalid();
        else
            websiteValue.markValid();

        if ( invalids.contains("downloadUrl") )
            downloadValue.markInvalid();
        else
            downloadValue.markValid();

        if ( invalids.contains("fundingAgency") )
            fundAgencyValue.markInvalid();
        else
            fundAgencyValue.markValid();

        if ( invalids.contains("fundingTitle") )
            fundTitleValue.markInvalid();
        else
            fundTitleValue.markValid();

        if ( invalids.contains("fundingId") )
            fundIdValue.markInvalid();
        else
            fundIdValue.markValid();

        if ( invalids.contains("researchProject") )
            projectValue.markInvalid();
        else
            projectValue.markValid();

        if ( invalids.contains("synopsis") )
            synopsisValue.markInvalid();
        else
            synopsisValue.markValid();

        if ( invalids.contains("purpose") )
            purposeValue.markInvalid();
        else
            purposeValue.markValid();

        if ( invalids.contains("references") )
            refsValue.markInvalid();
        else
            refsValue.markValid();

        if ( invalids.contains("portsOfCall") )
            portsValue.markInvalid();
        else
            portsValue.markValid();

        if ( invalids.contains("addnInfo") )
            addnInfoValue.markInvalid();
        else
            addnInfoValue.markValid();
    }

    /**
     * @return the updated MiscInfo; never null
     */
    public MiscInfo getUpdatedMiscInfo() {
        return info;
    }

}
