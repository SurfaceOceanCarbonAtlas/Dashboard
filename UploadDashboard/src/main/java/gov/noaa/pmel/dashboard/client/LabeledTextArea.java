package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextArea;

public class LabeledTextArea extends Composite implements HasText, HasValue<String> {

    interface LabeledTextAreaUiBinder extends UiBinder<CaptionPanel,LabeledTextArea> {
    }

    private static final LabeledTextAreaUiBinder labeledTextAreaUiBinder = GWT.create(LabeledTextAreaUiBinder.class);

    @UiField
    CaptionPanel valuePanel;
    @UiField
    TextArea valueBox;

    private SafeHtml validValueHtml;
    private SafeHtml invalidValueHtml;
    private boolean valid;

    /**
     * Creates a CaptionPanel containing a TextArea
     *
     * @param caption
     *         text to appear in the label of the CaptionPanel; cannot be null
     * @param valueHeight
     *         height, in CSS units, of the TextArea;
     *         see: {@link TextArea#setWidth(String)}.
     *         If null, no specification of the height is made.
     * @param valueWidth
     *         width, in CSS units, of the TextArea;
     *         see: {@link TextArea#setWidth(String)}.
     *         If null, no specification of the width is made.
     */
    public LabeledTextArea(String caption, String valueHeight, String valueWidth) {
        initWidget(labeledTextAreaUiBinder.createAndBindUi(this));

        validValueHtml = SafeHtmlUtils.fromSafeConstant("&nbsp;" + SafeHtmlUtils.htmlEscape(caption) + "&nbsp;");
        invalidValueHtml = UploadDashboard.invalidLabelHtml(validValueHtml);
        valuePanel.setCaptionHTML(validValueHtml);
        valid = true;
        if ( valueHeight != null )
            valueBox.setHeight(valueHeight);
        if ( valueWidth != null )
            valueBox.setWidth(valueWidth);
    }

    @Override
    public String getText() {
        return valueBox.getText();
    }

    @Override
    public void setText(String text) {
        valueBox.setText(text);
    }

    @Override
    public String getValue() {
        return valueBox.getValue();
    }

    @Override
    public void setValue(String value) {
        valueBox.setValue(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        valueBox.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return valueBox.addValueChangeHandler(handler);
    }

    /**
     * Make the text area read-only
     */
    public void markReadOnly() {
        valueBox.setEnabled(false);
    }

    /**
     * Highlights the prefix text to indicate the TextArea value is invalid.
     */
    public void markInvalid() {
        if ( valid ) {
            valuePanel.setCaptionHTML(invalidValueHtml);
            valid = false;
        }
    }

    /**
     * Removes any highlighting of the prefix text performed by {@link #markInvalid()};
     * thus, resets the prefix text to indicate the TextArea value is acceptable.
     */
    public void markValid() {
        if ( !valid ) {
            valuePanel.setCaptionHTML(validValueHtml);
            valid = true;
        }
    }

}
