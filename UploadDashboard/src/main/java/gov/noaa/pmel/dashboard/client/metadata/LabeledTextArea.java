package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextArea;
import gov.noaa.pmel.dashboard.client.UploadDashboard;

public class LabeledTextArea extends Composite implements HasText, HasValue<String> {

    interface LabeledTextAreaUiBinder extends UiBinder<FlowPanel,LabeledTextArea> {
    }

    private static final LabeledTextAreaUiBinder labeledTextAreaUiBinder = GWT.create(LabeledTextAreaUiBinder.class);

    @UiField
    HTML prefixHtml;
    @UiField
    TextArea valueArea;

    private SafeHtml validValueHtml;
    private SafeHtml invalidValueHtml;
    private boolean valid;

    /**
     * Creates an HTML widget followed by a TextArea widget.  Text in the HTML widget is right-aligned.
     *
     * @param prefix
     *         safe HTML (in "acceptable value" format) to appear in the HTML before the TextArea; cannot be null
     * @param prefixWidth
     *         width, in CSS units, of the HTML before the TextArea; see: {@link HTML#setWidth(String)}.
     *         If null, no specification of the width is made.
     * @param valueHeight
     *         height, in CSS units, of the TextArea; see: {@link TextArea#setHeight(String)}.
     *         If null, no specification of the width is made.
     * @param valueWidth
     *         width, in CSS units, of the TextArea; see: {@link TextArea#setWidth(String)}.
     *         If null, no specification of the width is made.
     */
    public LabeledTextArea(String prefix, String prefixWidth, String valueHeight, String valueWidth) {
        initWidget(labeledTextAreaUiBinder.createAndBindUi(this));

        validValueHtml = SafeHtmlUtils.fromSafeConstant(SafeHtmlUtils.htmlEscape(prefix));
        invalidValueHtml = UploadDashboard.invalidLabelHtml(validValueHtml);
        prefixHtml.setHTML(validValueHtml);
        valid = true;
        if ( prefixWidth != null )
            prefixHtml.setWidth(prefixWidth);
        if ( valueHeight != null )
            valueArea.setHeight(valueHeight);
        if ( valueWidth != null )
            valueArea.setWidth(valueWidth);
    }

    @Override
    public String getText() {
        return valueArea.getText();
    }

    @Override
    public void setText(String text) {
        valueArea.setText(text);
    }

    @Override
    public String getValue() {
        return valueArea.getValue();
    }

    @Override
    public void setValue(String value) {
        valueArea.setValue(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        valueArea.setValue(value, fireEvents);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return valueArea.addValueChangeHandler(handler);
    }

    /**
     * Highlights the prefix text to indicate the TextArea value is invalid.
     */
    public void markInvalid() {
        if ( valid ) {
            prefixHtml.setHTML(invalidValueHtml);
            valid = false;
        }
    }

    /**
     * Removes any highlighting of the prefix text performed by {@link #markInvalid()};
     * thus, resets the prefix text to indicate the TextArea value is acceptable.
     */
    public void markValid() {
        if ( !valid ) {
            prefixHtml.setHTML(validValueHtml);
            valid = true;
        }
    }

}
