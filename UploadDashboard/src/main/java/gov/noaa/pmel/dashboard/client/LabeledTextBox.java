package gov.noaa.pmel.dashboard.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class LabeledTextBox extends Composite implements HasText, HasValue<String> {

    interface LabeledTextBoxUiBinder extends UiBinder<FlowPanel,LabeledTextBox> {
    }

    private static final LabeledTextBoxUiBinder labeledTextBoxUiBinder = GWT.create(LabeledTextBoxUiBinder.class);

    @UiField
    Label prefixLabel;
    @UiField
    TextBox valueBox;
    @UiField
    Label suffixLabel;

    /**
     * Creates a TextBox with a label before and after it.
     * Text in the labels are aligned so as to be closest to the TextBox.
     *
     * @param prefix
     *         text to appear in the label before the TextBox
     * @param prefixWidth
     *         width, in CSS units, of the label before the TextBox;
     *         see: {@link Label#setWidth(String)}.
     *         If null, no specification of the width is made.
     * @param valueWidth
     *         visible number of characters in the TextBox
     * @param suffix
     *         text to appear in the label after the TextBox; cannot be null but can be empty
     */
    public LabeledTextBox(String prefix, String prefixWidth, int valueWidth, String suffix) {
        initWidget(labeledTextBoxUiBinder.createAndBindUi(this));
        prefixLabel.setText(prefix);
        if ( prefixWidth != null )
            prefixLabel.setWidth(prefixWidth);
        valueBox.setVisibleLength(valueWidth);
        suffixLabel.setText(suffix);
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
     * Highlights the prefix text to indicate the TextBox value is invalid.
     */
    public void markInvalid() {
        prefixLabel.addStyleDependentName("invalid");
    }

    /**
     * Removes any highlighting of the prefix text performed by {@link #markInvalid()};
     * thus, resets the prefix text to indicate the TextBox value is acceptable.
     */
    public void markValid() {
        prefixLabel.removeStyleDependentName("invalid");
    }

}
