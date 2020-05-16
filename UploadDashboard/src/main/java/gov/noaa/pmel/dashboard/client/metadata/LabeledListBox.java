package gov.noaa.pmel.dashboard.client.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import gov.noaa.pmel.dashboard.client.UploadDashboard;

public class LabeledListBox extends Composite implements HasChangeHandlers {

    interface LabeledListBoxUiBinder extends UiBinder<FlowPanel,LabeledListBox> {
    }

    private static final LabeledListBoxUiBinder labeledListBoxUiBinder = GWT.create(LabeledListBoxUiBinder.class);

    @UiField
    HTML prefixHtml;
    @UiField
    ListBox valueBox;
    @UiField
    Label suffixLabel;

    private SafeHtml validValueHtml;
    private SafeHtml invalidValueHtml;
    private boolean valid;

    /**
     * Creates a ListBox with a label before and after it.
     * Text in the labels are aligned so as to be closest to the ListBox.
     *
     * @param prefix
     *         text to appear in the label before the ListBox; cannot be null
     * @param prefixWidth
     *         width, in CSS units, of the label before the ListBox; see: {@link Label#setWidth(String)}.
     *         If null, no specification of the width is made.
     * @param valueWidth
     *         width, in CSS units, of the ListBox; see: {@link ListBox#setWidth(String)}.
     *         If null, no specification of the width is made.
     * @param suffix
     *         text to appear in the label after the ListBox;
     *         if null, this label will be hidden
     * @param suffixWidth
     *         width, in CSS units, of the label after the ListBox; see: {@link Label#setWidth(String)}.
     *         If null, or if suffix is null, no specification of the width is made.
     */
    public LabeledListBox(String prefix, String prefixWidth, String valueWidth, String suffix, String suffixWidth) {
        initWidget(labeledListBoxUiBinder.createAndBindUi(this));

        validValueHtml = SafeHtmlUtils.fromString(prefix);
        invalidValueHtml = UploadDashboard.invalidLabelHtml(validValueHtml);

        prefixHtml.setHTML(validValueHtml);
        valid = true;
        if ( prefixWidth != null )
            prefixHtml.setWidth(prefixWidth);
        if ( valueWidth != null )
            valueBox.setWidth(valueWidth);
        if ( suffix != null ) {
            suffixLabel.setText(suffix);
            if ( suffixWidth != null )
                suffixLabel.setWidth(suffixWidth);
        }
        else
            suffixLabel.setVisible(false);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return valueBox.addChangeHandler(handler);
    }

    /**
     * Add an item to the list of items to select from; see: {@link ListBox#addItem(String)}
     *
     * @param item
     *         item to add
     */
    public void addItem(String item) {
        valueBox.addItem(item);
    }

    /**
     * @return the index of the currently selected item; see: {@link ListBox#getSelectedIndex()}
     */
    public int getSelectedIndex() {
        return valueBox.getSelectedIndex();
    }

    /**
     * Sets the item at the given index as the selected item in the list; see: {@link ListBox#setSelectedIndex(int)}
     *
     * @param index
     *         index of the item to be selected
     */
    public void setSelectedIndex(int index) {
        valueBox.setSelectedIndex(index);
    }

    /**
     * Highlights the prefix text to indicate the ListBox value is invalid.
     */
    public void markInvalid() {
        if ( valid ) {
            prefixHtml.setHTML(invalidValueHtml);
            valid = false;
        }
    }

    /**
     * Removes any highlighting of the prefix text performed by {@link #markInvalid()};
     * thus, resets the prefix text to indicate the ListBox value is acceptable.
     */
    public void markValid() {
        if ( !valid ) {
            prefixHtml.setHTML(validValueHtml);
            valid = true;
        }
    }

}
