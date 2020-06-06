package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.ArangeSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.AxisSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A generic axis widget suitable for a simple numeric access like x, y, or z.
 * Chances are what you really want to work with is an
 * {@link gov.noaa.pmel.tmap.las.client.laswidget.AxesWidgetGroup}
 * 
 * @author rhs
 * 
 */
public class AxisWidget extends Composite {
    String type;
    Label lo_label = new Label();
    Label lo_label_range = new Label();
    Label hi_label_range = new Label();
    ListBox lo_axis = new ListBox();
    ListBox hi_axis = new ListBox();
    FlexTable lo_layout = new FlexTable();
    FlexTable hi_layout = new FlexTable();
    FlowPanel layout = new FlowPanel();
    NumberFormat format = NumberFormat.getFormat("###.##");
    boolean range;
    EventBus eventBus;

    /**
     * Construct an axis using a AxisSerializable object. isRange is false by
     * default.
     * 
     * @param ax
     */
    public AxisWidget(AxisSerializable ax) {
        // Used to source AxisChangeEvents...
        ClientFactory cf = GWT.create(ClientFactory.class);
        eventBus = cf.getEventBus();
        init(ax);
        initWidget(layout);
    }

    /**
     * Construct an axis using a AxisSerializable object and boolean range
     * switch. Range set to false means there is only one widget (or set of
     * widgets in the case of time) visible and the user can only select one
     * point from that axis. Range set to true means that there are two
     * identical coordinated widgets (or set of widgets in the case of time)
     * from which you can select a starting point and an ending point from that
     * axis. The coordination between the widgets is such that you can not
     * select an endpoint that is before the starting point select. The widgets
     * update themselves to prevent this from happening.
     * 
     * @param ax
     * @param range
     * @author weusijana
     */
    public AxisWidget(AxisSerializable ax, boolean range) {
        ClientFactory cf = GWT.create(ClientFactory.class);
        eventBus = cf.getEventBus();
        init(ax, range);
        initWidget(layout);
    }

    /**
     * Construct an empty axis widget.
     */

    public AxisWidget() {
        ClientFactory cf = GWT.create(ClientFactory.class);
        eventBus = cf.getEventBus();
        initWidget(layout);
    }

    /**
     * Initialize an axis using a AxisSerializable object and boolean range
     * switch. Range set to false means there is only one widget (or set of
     * widgets in the case of time) visible and the user can only select one
     * point from that axis. Range set to true means that there are two
     * identical coordinated widgets (or set of widgets in the case of time)
     * from which you can select a starting point and an ending point from that
     * axis. The coordination between the widgets is such that you can not
     * select an endpoint that is before the starting point select. The widgets
     * update themselves to prevent this from happening.
     * 
     * @param ax
     * @param range
     */
    public void init(AxisSerializable ax, boolean range) {
        this.range = range;
        initialize(ax);
    }

    /**
     * Initialize an axis using a AxisSerializable object. isRange is false by
     * default.
     * 
     * @param ax
     */
    public void init(AxisSerializable ax) {
        this.range = false;
        
        initialize(ax);
    }
    protected void clear() {
        lo_axis.clear();
        hi_axis.clear();
    }
    protected void initialize(AxisSerializable ax) {
        clear();
        lo_axis.addChangeHandler(loAxisChangeHandler);
        hi_axis.addChangeHandler(hiAxisChangeHandler);
        String units = ax.getUnits();
        if (ax.getLabel() != null && !ax.getLabel().equals("")) {
            if (units != null && !units.equals("") && !units.equals("null") ) {
                lo_label_range.setText("Start " + ax.getLabel() + "(" + units + "):");
                hi_label_range.setText("End " + ax.getLabel() + "(" + units + "):");
                lo_label.setText(ax.getLabel() + "(" + units + "):");
            } else {
                lo_label_range.setText("Start " + ax.getLabel() + ":");
                hi_label_range.setText("End " + ax.getLabel() + ":");
                lo_label.setText(ax.getLabel() + ":");
            }
        } else {
            if (units != null && !units.equals("")) {
                lo_label_range.setText("Start (" + units + "): ");
                hi_label_range.setText("End (" + units + "): ");
                lo_label.setText("Z (" + units + "): ");
            } else {
                lo_label_range.setText("Start :");
                hi_label_range.setText("End :");
                lo_label.setText("Axis:");
            }
        }

        if (ax.getNames() != null && ax.getNames().length > 0) {
            this.type = ax.getType();
            lo_axis.setName(type);
            String[] names = ax.getNames();
            String[] values = ax.getValues();
            for (int i = 0; i < names.length; i++) {
                lo_axis.addItem(names[i], values[i]);
                hi_axis.addItem(names[i], values[i]);
            }
            lo_axis.setSelectedIndex(0);
            hi_axis.setSelectedIndex(names.length - 1);
        } else {
            this.type = ax.getType();
            lo_axis.setName(type);
            ArangeSerializable arange = ax.getArangeSerializable();
            int size = Integer.valueOf(arange.getSize());
            double start = Double.valueOf(arange.getStart());
            double step = Double.valueOf(arange.getStep());
            for (int i = 0; i < size; i++) {
                double value = start + i * step;
                String v = format.format(value);
                lo_axis.addItem(v);
                hi_axis.addItem(v);
            }
            lo_axis.setSelectedIndex(0);
            hi_axis.setSelectedIndex(hi_axis.getItemCount() - 1);
        }
        load_layout();
    }

    private void load_layout() {
        lo_layout.clear();
        hi_layout.clear();
        layout.clear();
        if (range) {
            lo_layout.setWidget(0, 0, lo_label_range);
            lo_layout.setWidget(0, 1, lo_axis);
            hi_layout.setWidget(0, 0, hi_label_range);
            hi_layout.setWidget(0, 1, hi_axis);
            layout.add(lo_layout);
            layout.add(hi_layout);
//            layout.setWidget(0, 0, lo_layout);
//            layout.setWidget(1, 0, hi_layout);
            lo_layout.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            lo_layout.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
            hi_layout.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            hi_layout.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
//            layout.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
//            layout.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        } else {
            
            lo_layout.setWidget(0, 0, lo_label);
            lo_layout.setWidget(0, 1, lo_axis);
//            layout.setWidget(0, 0, lo_layout);
            layout.add(lo_layout);
            
            lo_layout.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            lo_layout.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
            
//            layout.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        }
    }

    public int getSelectedIndex() {
        return lo_axis.getSelectedIndex();
    }

    public String getValue(int i) {
        return lo_axis.getValue(i);
    }

    public int getItemCount() {
        return lo_axis.getItemCount();
    }
    public void setEnabled(boolean b) {
        lo_axis.setEnabled(b);
    }

    public String getLo() {
        return lo_axis.getValue(lo_axis.getSelectedIndex());
    }

    public String getHi() {
        if (range) {
            return hi_axis.getValue(hi_axis.getSelectedIndex());
        } else {
            return getLo();
        }
    }

    public String getType() {
        return this.type;
    }

    public void setLo(String lo) {
        for (int i = 0; i < lo_axis.getItemCount(); i++) {
            String value = lo_axis.getValue(i);
            if (lo.equals(value)) {
                lo_axis.setSelectedIndex(i);
            }
        }
        checkOrderLo();
    }

    /**
     * @param hi
     */
    public void setHi(String hi) {
        for (int i = 0; i < hi_axis.getItemCount(); i++) {
            String value = hi_axis.getValue(i);
            if (hi.equals(value)) {
                hi_axis.setSelectedIndex(i);
            }
        }
        checkOrderHi();
    }

    /**
     * Range set to false means there is only one widget (or set of widgets in
     * the case of time) visible and the user can only select one point from
     * that axis. isRange set to true means that there are two identical
     * coordinated widgets (or set of widgets in the case of time) from which
     * you can select a starting point and an ending point from that axis. The
     * coordination between the widgets is such that you can not select an
     * ending point that is before the selected starting point. The widgets update
     * themselves to prevent this from happening.
     * 
     * @param isRange
     */
    public void setRange(boolean isRange) {
        range = isRange;
        load_layout();
    }

    /**
     * Range set to false means there is only one widget (or set of widgets in
     * the case of time) visible and the user can only select one point from
     * that axis. Range set to true means that there are two identical
     * coordinated widgets (or set of widgets in the case of time) from which
     * you can select a starting point and an ending point from that axis. The
     * coordination between the widgets is such that you can not select an
     * endpoint that is before the starting point select. The widgets update
     * themselves to prevent this from happening.
     * 
     * @return
     */
    public boolean isRange() {
        return range;
    }

    public void checkOrderLo() {
        if (hi_axis.getSelectedIndex() < lo_axis.getSelectedIndex()) {
            if (lo_axis.getSelectedIndex() < lo_axis.getItemCount() - 2) {
                hi_axis.setSelectedIndex(lo_axis.getSelectedIndex() + 1);
            } else {
                hi_axis.setSelectedIndex(lo_axis.getSelectedIndex());
            }
        }
    }

    public void checkOrderHi() {
        if (hi_axis.getSelectedIndex() < lo_axis.getSelectedIndex()) {
            if (hi_axis.getSelectedIndex() > 1) {
                lo_axis.setSelectedIndex(hi_axis.getSelectedIndex() - 1);
            } else {
                lo_axis.setSelectedIndex(hi_axis.getSelectedIndex());
            }
        }
    }
    public boolean isContainedBy(String lo, String hi) {
        double dlo = Double.valueOf(lo);
        double dhi = Double.valueOf(hi);
        double cl = Double.valueOf(getLo());
        double ch = Double.valueOf(getHi());
        return cl >= dlo && cl <= dhi && ch >= dlo && ch <= dhi;
    }
    public ChangeHandler loAxisChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent arg0) {
            eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            checkOrderLo();
        }
    };
    public ChangeHandler hiAxisChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent arg0) {
            eventBus.fireEvent(new WidgetSelectionChangeEvent(false));
            checkOrderHi();
        }
    };
    public void setNearestLo(double zlo) {
        String top = lo_axis.getValue(0);
        String bottom = lo_axis.getValue(lo_axis.getItemCount() - 1);
        double t = Double.valueOf(top);
        double b = Double.valueOf(bottom);
        if ( t > b ) {
            if ( zlo >= t ) {
                lo_axis.setSelectedIndex(0);
                return;
            }
            if ( zlo <= b ) {
                lo_axis.setSelectedIndex(lo_axis.getItemCount() - 1);
                return;
            }
            for ( int i = 0; i < lo_axis.getItemCount() - 1; i++ ) {
                String v1 = lo_axis.getValue(i);
                String v2 = lo_axis.getValue(i+1);
                double dv1 = Double.valueOf(v1);
                double dv2 = Double.valueOf(v2);
                if ( zlo < dv1 && zlo >= dv2  ) {
                    lo_axis.setSelectedIndex(i+1);
                    return;
                }
            }
        } else {
            if ( zlo <= t ) {
                lo_axis.setSelectedIndex(0);
                return;
            }
            if ( zlo >= b ) {
                lo_axis.setSelectedIndex(lo_axis.getItemCount() - 1);
                return;
            }
            for ( int i = 0; i < lo_axis.getItemCount() - 1; i++ ) {
                String v1 = lo_axis.getValue(i);
                String v2 = lo_axis.getValue(i+1);
                double dv1 = Double.valueOf(v1);
                double dv2 = Double.valueOf(v2);
                if ( zlo > dv1 && zlo <= dv2 ) {
                    lo_axis.setSelectedIndex(i);
                    return;
                }
            }
        }
    }
    public void setNearestHi(double zhi) {
        String top = hi_axis.getValue(0);
        String bottom = hi_axis.getValue(hi_axis.getItemCount() - 1);
        double t = Double.valueOf(top);
        double b = Double.valueOf(bottom);
        if ( t > b ) {
            if ( zhi >= t ) {
                hi_axis.setSelectedIndex(0);
                return;
            }
            if ( zhi <= b ) {
                hi_axis.setSelectedIndex(hi_axis.getItemCount() - 1);
                return;
            }
            for ( int i = 0; i < hi_axis.getItemCount() - 1; i++ ) {
                String v1 = hi_axis.getValue(i);
                String v2 = hi_axis.getValue(i+1);
                double dv1 = Double.valueOf(v1);
                double dv2 = Double.valueOf(v2);
                if ( zhi < dv1 && zhi >= dv2  ) {
                    hi_axis.setSelectedIndex(i+1);
                    return;
                }
            }
        } else {
            if ( zhi <= t ) {
                hi_axis.setSelectedIndex(0);
                return;
            }
            if ( zhi >= b ) {
                hi_axis.setSelectedIndex(hi_axis.getItemCount() - 1);
                return;
            }
            for ( int i = 0; i < hi_axis.getItemCount() - 1; i++ ) {
                String v1 = hi_axis.getValue(i);
                String v2 = hi_axis.getValue(i+1);
                double dv1 = Double.valueOf(v1);
                double dv2 = Double.valueOf(v2);
                if ( zhi > dv1 && zhi <= dv2 ) {
                    hi_axis.setSelectedIndex(i+1);
                }
            }
        }
    }
}
