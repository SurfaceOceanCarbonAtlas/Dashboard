package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.AnalysisAxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.AnalysisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A widget for setting up an analysis Widget in LAS --- TODO:NOT YET WORKING.
 * 
 * @author rhs
 * 
 */
public class AnalysisWidget extends Composite {
    ListBox analysisAxis = new ListBox();
    ListBox analysisType = new ListBox();
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    Grid layoutPanel = new Grid();
    // DisclosurePanel mainPanel = new DisclosurePanel("Optional Calculations");
    FlowPanel mainPanel = new FlowPanel();

    // CheckBox apply = new CheckBox("Apply Analysis");

    public AnalysisWidget(String width) {

        mainPanel.setTitle("Select a statistic to compute and the space and time dimensions over which to compute it");
        layoutPanel.resize(2, 2);
        layoutPanel.setTitle("Optional Analysis Controls");
        // layoutPanel.setWidget(0, 0, apply);
        layoutPanel.setWidget(0, 0, new Label("Compute:"));
        analysisType.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireAnalysisTypeChangeEvent(event);
                eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false, false), AnalysisWidget.this);
            }
        });
        analysisAxis.addChangeHandler(new ChangeHandler(){@Override
            public void onChange(ChangeEvent arg0) {
            eventBus.fireEventFromSource(new WidgetSelectionChangeEvent(false, false), AnalysisWidget.this);
        }
        });
        analysisType.addItem("None");
        analysisType.addItem("Average");
        analysisType.addItem("Minimum");
        analysisType.addItem("Maximum");
        analysisType.addItem("Sum");
        analysisType.addItem("Variance");
        analysisType.setSelectedIndex(0);
        layoutPanel.setWidget(0, 1, analysisType);
        layoutPanel.setWidget(1, 0, new Label("over:"));
        layoutPanel.setWidget(1, 1, analysisAxis);

        mainPanel.add(layoutPanel);

        initWidget(mainPanel);

    }

    public void addAnalysisAxesChangeHandler(ChangeHandler analysisAxesChange) {
        analysisAxis.addChangeHandler(analysisAxesChange);

    }

    public void addAnalysisChangeHandler(ChangeHandler analysisTypeChangeHandler) {
        // apply.addClickHandler(analysisActiveChange);
        analysisType.addChangeHandler(analysisTypeChangeHandler);
    }

    public void addAnalysisOpChangeHandler(ChangeHandler analysisOpChange) {
        analysisType.addChangeHandler(analysisOpChange);
    }

    protected void fireAnalysisTypeChangeEvent(ChangeEvent event) {
        eventBus.fireEventFromSource(event, this);
    }

    public String getAnalysisAxis() {
        return analysisAxis.getValue(analysisAxis.getSelectedIndex());
    }
    public String getAnalysisType() {
        return analysisType.getValue(analysisType.getSelectedIndex());
    }
    public AnalysisSerializable getAnalysisSerializable() {
        // Axis state information.
        AnalysisAxisSerializable xAxis = new AnalysisAxisSerializable();
        AnalysisAxisSerializable yAxis = new AnalysisAxisSerializable();
        AnalysisAxisSerializable zAxis = new AnalysisAxisSerializable();
        AnalysisAxisSerializable tAxis = new AnalysisAxisSerializable();
        AnalysisAxisSerializable eAxis = new AnalysisAxisSerializable();
        AnalysisAxisSerializable fAxis = new AnalysisAxisSerializable();

        // The container
        AnalysisSerializable analysis = new AnalysisSerializable();
        String axis = analysisAxis.getValue(analysisAxis.getSelectedIndex());
        String op = analysisType.getValue(analysisType.getSelectedIndex());
        xAxis.setOp(null);
        yAxis.setOp(null);
        zAxis.setOp(null);
        tAxis.setOp(null);
        eAxis.setOp(null);
        fAxis.setOp(null);
        if ( axis.equals("xy") ) {
            xAxis.setType("x");
            xAxis.setOp(op);
            yAxis.setType("y");
            yAxis.setOp(op);
        } else if ( axis.equals("xyt") ) {
            xAxis.setType("x");
            xAxis.setOp(op);
            yAxis.setType("y");
            yAxis.setOp(op);
            tAxis.setType("t");
            tAxis.setOp(op);
        } else if ( axis.equals("x") ) {
            xAxis.setType("x");
            xAxis.setOp(op);
        } else if ( axis.equals("y") ) {
            yAxis.setType("y");
            yAxis.setOp(op);
        } else if ( axis.equals("z") ) {
            zAxis.setType("z");
            zAxis.setOp(op);
        } else if ( axis.equals("t") ) {
            tAxis.setType("t");
            tAxis.setOp(op);
        } else if ( axis.equals("e") ) {
        	eAxis.setType("e");
        	eAxis.setOp(op);
        } else if ( axis.equals("f") ) {
        	fAxis.setType("f");
        	fAxis.setOp(op);
        }
        analysis.getAxes().put("x", xAxis);
        analysis.getAxes().put("y", yAxis);
        analysis.getAxes().put("z", zAxis);
        analysis.getAxes().put("t", tAxis);
        analysis.getAxes().put("e", eAxis);
        analysis.getAxes().put("f", fAxis);
        return analysis;
    }

    public boolean isActive() {
    	if ( analysisType != null ) {
    		return analysisType.getSelectedIndex() > 0;// apply.getValue();
    	} else {
    		return false;
    	}
    }

    public void setActive(boolean active) {
        // apply.setValue(b);
        if ( !active ) {
            // Set to None
            analysisType.setSelectedIndex(0);
        } else {
            // Default to Average
            analysisType.setSelectedIndex(1);
        }
    }

    public void setAnalysisAxes(GridSerializable grid) {
        analysisAxis.clear();
        // You have to have one more axis in order to average out XY. If you only have XY you get a scalar which we can't handle.
        if ( !grid.getIntervals().equals("xy") ) {
        	analysisAxis.addItem("Area", "xy");
        }
        analysisAxis.addItem("Longitude", "x");
        analysisAxis.addItem("Latitude", "y");
        if ( grid.hasZ() ) {
            String zl = grid.getZAxis().getLabel();
            if ( zl != null && !zl.equals("") ) {
                analysisAxis.addItem(zl, "z");
            } else {
                analysisAxis.addItem("Height/Depth", "z");
            }
        }
        if ( grid.hasT() ) {
            analysisAxis.addItem("Time", "t");
        }
        if ( grid.hasE() ) {
        	analysisAxis.addItem("Ensemble Member", "e");
        }
        if ( grid.hasF() ) {
        	analysisAxis.addItem("Forecast Initialization", "f");
        }
    }

    public void setAnalysisType(String atype) {
        int index = 0;
        for ( int i = 0; i <  analysisType.getItemCount(); i++ ) {
            String value = analysisType.getValue(i);
            if ( value.equals(atype) ) {
                index = i;
            }
        }
        analysisType.setSelectedIndex(index);
    }

    public void setAnalysisAxis(String aover) {
        // TODO do we need to setAnalysisAxes(grid) first?
        int index = 0;
        for ( int i = 0; i < analysisAxis.getItemCount(); i++ ) {
            String value = analysisAxis.getValue(i);
            if ( value.equals(aover) ) {
                index = i;
            }
        }
        analysisAxis.setSelectedIndex(index);
    }
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}
