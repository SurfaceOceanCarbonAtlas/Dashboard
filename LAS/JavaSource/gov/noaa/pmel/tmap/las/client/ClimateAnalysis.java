package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.event.WidgetSelectionChangeEvent.Handler;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.DatasetFilter;
import gov.noaa.pmel.tmap.las.client.laswidget.DatasetWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.DateTimeWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ClimateAnalysis implements EntryPoint {
    DatasetWidget xDatasetWidget = new DatasetWidget();
    Label xCurrentDataset = new Label("none");
    Label xSecondDataset = new Label("...");
    ListBox xAnalysisType = new ListBox();
    PushButton xSubmit = new PushButton("Submit");
    DateTimeWidget xDateWidget = new DateTimeWidget();
    DateTimeWidget xDateWidgetTwo = new DateTimeWidget();
    LASRequest lasRequest = new LASRequest();
    DatasetFilter cimp5filter = new DatasetFilter(true, "name", "cmip5");
	String xFirstName = "none";
	String xFirstID = "id";
	String xFirstVarID = "id";
	String xSecondName = "none";
	String xInitialId = "x-Unintialized-id";
	String xSecondID = xInitialId;
	String xSecondVarID = xInitialId;
	String xTlo;
	String xThi;
	String[] panelIDs = {"DatasetSelection", "AnalysisSelection", "RegionSelection", "DateSelection", "DateSelection", "SubmitSelection"};
	String[] selectionIDs = {"DatasetWidget", "AnalysisType", "Regions", "DateRange", "DateRange2", "Submit"};
	String highLightStyle = "highlightSelection";
	String selectionStyle = "selection";
	Set<Integer> set = new HashSet<Integer>();
	PacificRegionsByVariable xRegions = new PacificRegionsByVariable();
	EventBus eventBus;
	RootPanel xRegionLabel;
	RootPanel xTimeTitle;
	HTML timeTitle3 = new HTML("<h3>Step 3: Select the Year Range to be compared.</h3>");
    HTML timeTitle4 = new HTML("<h3>Step 4: Select the Year Range to be compared.</h3>");
	@Override
	public void onModuleLoad() {	
	
	    xRegionLabel = RootPanel.get("regionTitle");
	    xTimeTitle = RootPanel.get("timeTitle");
	    xTimeTitle.clear();
        xTimeTitle.add(timeTitle4);
	    ClientFactory cf = GWT.create(ClientFactory.class);
	    eventBus = cf.getEventBus();
	    eventBus.addHandler(WidgetSelectionChangeEvent.TYPE, new WidgetSelectionChangeEvent.Handler() {
           
            @Override
            public void onAxisSelectionChange(WidgetSelectionChangeEvent event) {
                Widget w = (Widget) event.getSource();
                if ( w instanceof DateTimeWidget ) {
                    if ( w.equals(xDateWidget) ) {
                        set.add(3);
                        setBackground(4);
                    } else if (w.equals(xDateWidgetTwo) ) {
                        set.add(4);
                        setBackground(5);   
                    }
                }                
            }
        });
		set.clear();
		xSubmit.addClickHandler(xSubmitClick);
		xSubmit.setWidth("80px");
		RootPanel.get("Submit").add(xSubmit);
		xAnalysisType.setVisibleItemCount(1);
		String tave_spectrum = DOM.getElementProperty(DOM.getElementById("analysis-tave_spectrum"), "content");
		if ( tave_spectrum != null && tave_spectrum.equals("tave_spectrum") ) {
		    xAnalysisType.addItem("Time Averaged Spectrum", "tave_spectrum");
		    xAnalysisType.addClickHandler(new ClickHandler(){

		        @Override
		        public void onClick(ClickEvent arg0) {
		            set.add(1);
		            setBackground(2);
		        }

		    });
		} else {
		    xRegions.setVisible(false);
            xRegionLabel.setVisible(false);
            xTimeTitle.clear();
            xTimeTitle.add(timeTitle3);
		}
		String cdb_zonal = DOM.getElementProperty(DOM.getElementById("analysis-cdb_zonal"), "content");
		if ( cdb_zonal != null && cdb_zonal.equals("cdb_zonal") ) {
		    xAnalysisType.addItem("CDB Zonal", "cdb_zonal");
		    xAnalysisType.addChangeHandler(new ChangeHandler() {

		        @Override
		        public void onChange(ChangeEvent event) {
		            String analysisType = xAnalysisType.getValue(xAnalysisType.getSelectedIndex());
		            if (analysisType.equals("tave_spectrum") ) {
		                xRegions.setVisible(true);
		                xRegionLabel.setVisible(true);
		                xTimeTitle.clear();
		                xTimeTitle.add(timeTitle4);
		            } else {
		                xRegions.setVisible(false);
		                xRegionLabel.setVisible(false);
		                xTimeTitle.clear();
		                xTimeTitle.add(timeTitle3);
		            }
		        }
		    });
		}

		if ( xAnalysisType.getItemCount() == 0 ) {
		    HTML no = new HTML("<h3>No analysis configured for this server.</h3>");
		    xTimeTitle.clear();
		    xTimeTitle.add(no);
		}
		RootPanel.get("AnalysisType").add(xAnalysisType);
		RootPanel.get("Regions").add(xRegions);
		xDatasetWidget.addFilter(cimp5filter);
		String xml = Util.getParameterString("xml");
	    xFirstName = Util.getParameterString("dsname");
		if ( xFirstName != null && !xFirstName.equals("") ) {
			xCurrentDataset.setText(xFirstName);
		}
		xTlo = Util.getParameterString("tlo");
		xThi = Util.getParameterString("thi");
		xDateWidget.init(xTlo, xThi, "y", "360_day", false);
		xDateWidget.setRange(true);
			
		RootPanel.get("DateRange").add(xDateWidget);
		RootPanel.get("DateRange2").add(xDateWidgetTwo);
		RootPanel.get("SecondDataset").add(xSecondDataset);
		RootPanel.get("CurrentDataset").add(xCurrentDataset);
		if ( xml != null && !xml.equals("") ) {
			xml = Util.decode(xml);
			lasRequest = new LASRequest(xml);
			xFirstID = lasRequest.getDataset(0);
			xFirstVarID = lasRequest.getVariable(0);
			DatasetFilter filter = new DatasetFilter(false, "ID", xFirstID);
			xDatasetWidget.addFilter(filter);
		}
		xDatasetWidget.init();
		xDatasetWidget.addSelectionHandler(xSelectionHandler);
		RootPanel.get("DatasetWidget").add(xDatasetWidget);
	}
	private ClickHandler xSubmitClick = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if ( xSecondID.equals(xInitialId) ) {
				Window.alert("Please select a second data set.");
			} else if ( xSecondVarID.equals(xInitialId) ) {
				Window.alert("Second variable not set, try again in a moment.");
			} else {
				set.clear();
				lasRequest = new LASRequest();
				lasRequest.setOperation("Climate_Analysis_Plot", "V7");
				lasRequest.addVariable(xFirstID, xFirstVarID, 0);
				lasRequest.addVariable(xSecondID, xSecondVarID, 0);
				lasRequest.setRange("t", xDateWidget.getFerretDateLo(), xDateWidget.getFerretDateHi(), 0);
				lasRequest.setRange("t", xDateWidgetTwo.getFerretDateLo(), xDateWidgetTwo.getFerretDateHi(), 1);
				String type = xAnalysisType.getValue(xAnalysisType.getSelectedIndex());
				lasRequest.setProperty("climate_analysis", "type", type);
				List<String> activeRegions = xRegions.getActiveVariablesAndRegions();
				for (Iterator arIt = activeRegions.iterator(); arIt.hasNext();) {
					String ar = (String) arIt.next();
					lasRequest.setProperty("climate_analysis_regions", type+"_region_"+ar, ar);
				}
				Window.open(Util.getProductServer()+"?xml="+URL.encode(lasRequest.toString()), "_blank", Constants.WINDOW_FEATURES);
			}
		}
		
	};
	private SelectionHandler<TreeItem> xSelectionHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent event) {
			DatasetWidget source = (DatasetWidget) event.getSource();
			Object uo = source.getCurrentlySelected();
			if ( uo instanceof VariableSerializable ) {
				VariableSerializable v = (VariableSerializable) uo;
				xSecondName = v.getDSName();
				xSecondID = v.getDSID();
				xSecondVarID = v.getID();
				xSecondDataset.setText(xSecondName);
				set.add(0);
				setBackground(1);
				Util.getRPCService().getGrid(xSecondID, xSecondVarID, gridCallback);

			} else if ( uo instanceof CategorySerializable) {
				CategorySerializable c = (CategorySerializable) uo;
				// Category children, open it up.
				
				// Variable children, dagnabit.  We need the variable, so let's go get it.
				Util.getRPCService().getCategories(c.getID(), null, categoryCallback);
			}
		}
		
	};
	AsyncCallback gridCallback = new AsyncCallback() {

		@Override
		public void onFailure(Throwable e) {
			Window.alert("Could not get the grid for the second data set. "+e.getLocalizedMessage());
		}

		@Override
		public void onSuccess(Object result) {
			GridSerializable grid = (GridSerializable) result;
			xDateWidgetTwo.init(grid.getTAxis(), false);
			xDateWidgetTwo.setRange(true);
		}
		
	};
	AsyncCallback categoryCallback = new AsyncCallback() {

		@Override
		public void onFailure(Throwable e) {
			Window.alert("Could not get a variable for the second data set. "+e.getLocalizedMessage());
		}

		@Override
		public void onSuccess(Object result) {
			CategorySerializable[] cats = (CategorySerializable[]) result;
			CategorySerializable cat = cats[0];
			if ( cat.isCategoryChildren() ) {
				Window.alert("Could not get a variable for the second data set.");
			} else {
				DatasetSerializable ds = cat.getDatasetSerializable();
				VariableSerializable[] vars = ds.getVariablesSerializable();	
				// TODO -- this should be "ts" not the first variable...
				xSecondName = vars[0].getDSName();
				xSecondID = vars[0].getDSID();
				xSecondVarID = vars[0].getID();
				xSecondDataset.setText(xSecondName);
				Util.getRPCService().getGrid(xSecondID, xSecondVarID, gridCallback);
				set.add(0);
				setBackground(1);
			}
		}
		
	};
	private void setBackground(int id) {
		for (int i = 0; i < panelIDs.length; i++) {
			RootPanel.get(panelIDs[i]).removeStyleName(highLightStyle);
			RootPanel.get(selectionIDs[i]).removeStyleName(selectionStyle);
		}
		if ( set.contains(0) && set.contains(1) && set.contains(2) && set.contains(3)) {
			RootPanel.get(panelIDs[4]).addStyleName(highLightStyle);
			RootPanel.get(selectionIDs[4]).addStyleName(selectionStyle);
		} else {
			if ( xSecondID.equals(xInitialId) ) {
				RootPanel.get("DatasetSelection").addStyleName(highLightStyle);
				RootPanel.get("DatasetWidget").addStyleName(selectionStyle);
			} else{ 
				if ( !set.contains(id) ) {
					RootPanel.get(panelIDs[id]).addStyleName(highLightStyle);
					RootPanel.get(selectionIDs[id]).addStyleName(selectionStyle);
				}
			}
		}
	}
	private class PacificRegionsByVariable extends Composite {
		FlexTable layout = new FlexTable();
		// Pacific
		CheckBox ts_cb11 = new CheckBox("Nino3");
		CheckBox ts_cb12 = new CheckBox("Nino4");
		CheckBox ts_cb13 = new CheckBox("Nino34");
		CheckBox ts_cb14 = new CheckBox("Nino12");
		// Atlantic
		CheckBox ts_cb18 = new CheckBox("EqAtl");
		CheckBox ts_cb19 = new CheckBox("TNAtl");
		CheckBox ts_cb110 = new CheckBox("TSAtl");
		// Indian
		CheckBox ts_cb111 = new CheckBox("EEqIO");
		CheckBox ts_cb112 = new CheckBox("WEqIO");
		
		// Pacific
		CheckBox tauu_cb21 = new CheckBox("Nino3");
		CheckBox tauu_cb22 = new CheckBox("Nino4");
		CheckBox tauu_cb25 = new CheckBox("WEqPac");
		// Atlantic
		CheckBox tauu_cb28 = new CheckBox("EqAtl");
		// Indian
		CheckBox tauu_cb213 = new CheckBox("EqIO");
		
		// Pacific
		CheckBox ps_cb36 = new CheckBox("Darwin");
		CheckBox ps_cb37 = new CheckBox("Tahiti");
		
		List<CheckBox> ts = new ArrayList<CheckBox>();
		List<CheckBox> tauu = new ArrayList<CheckBox>();
		List<CheckBox> ps = new ArrayList<CheckBox>();
		
		List<CheckBox> pacific = new ArrayList<CheckBox>();
		List<CheckBox> atlantic = new ArrayList<CheckBox>();
		List<CheckBox> indian = new ArrayList<CheckBox>();
		
		CheckBox cb_all = new CheckBox("all");
		CheckBox cb_ts = new CheckBox("ts");
		CheckBox cb_tauu = new CheckBox("tauu");
		CheckBox cb_ps = new CheckBox("ps");
		
		List<CheckBox> variables = new ArrayList<CheckBox>();
		List<CheckBox> regions = new ArrayList<CheckBox>();
		
		CheckBox cb_tp = new CheckBox("Tropical Pacific");
		CheckBox cb_ta = new CheckBox("Tropical Atlantic");
		CheckBox cb_ti = new CheckBox("Tropical Indian");
		
		public PacificRegionsByVariable () {
			FlexCellFormatter formatter = layout.getFlexCellFormatter();
			formatter.setColSpan(0, 1, 7);
			formatter.setColSpan(0, 2, 3);
			formatter.setColSpan(0, 3, 3);
			
			// The FormValue is the string that must be passed to the Python code to get that variable calculated on that region.
			ts_cb11.setFormValue("ts_nino3");
			ts_cb12.setFormValue("ts_nino4");
			ts_cb13.setFormValue("ts_nino34");
			ts_cb14.setFormValue("ts_nino12");
			ts_cb18.setFormValue("ts_eqatl");
			ts_cb19.setFormValue("ts_tnatl");
			ts_cb110.setFormValue("ts_tsatl");
			ts_cb111.setFormValue("ts_eeqio");
			ts_cb112.setFormValue("ts_weqio");
			
			tauu_cb21.setFormValue("tauu_nino3");
			tauu_cb22.setFormValue("tauu_nino4");
			tauu_cb25.setFormValue("tauu_weqpac");
			tauu_cb28.setFormValue("tauu_eqatl");
			tauu_cb213.setFormValue("tauu_eqio");
			
			ps_cb36.setFormValue("ps_darwin");
			ps_cb37.setFormValue("ps_tahiti");
			
			
			ts_cb11.addClickHandler(checkPacific);
			ts_cb11.addClickHandler(checkTS);
			ts_cb12.addClickHandler(checkPacific);
			ts_cb12.addClickHandler(checkTS);
			ts_cb13.addClickHandler(checkPacific);
			ts_cb13.addClickHandler(checkTS);
			ts_cb14.addClickHandler(checkPacific);
			ts_cb14.addClickHandler(checkTS);
			
			ts_cb18.addClickHandler(checkAtlantic);
			ts_cb18.addClickHandler(checkTS);
			ts_cb19.addClickHandler(checkAtlantic);
			ts_cb19.addClickHandler(checkTS);
			ts_cb110.addClickHandler(checkAtlantic);
			ts_cb110.addClickHandler(checkTS);
			
			ts_cb111.addClickHandler(checkIndian);
			ts_cb111.addClickHandler(checkTS);
			ts_cb112.addClickHandler(checkIndian);
			ts_cb112.addClickHandler(checkTS);
			
			tauu_cb21.addClickHandler(checkPacific);
			tauu_cb21.addClickHandler(checkTAUU);
			tauu_cb22.addClickHandler(checkPacific);
			tauu_cb22.addClickHandler(checkTAUU);
			tauu_cb25.addClickHandler(checkPacific);
			tauu_cb25.addClickHandler(checkTAUU);
			// Atlantic
			tauu_cb28.addClickHandler(checkAtlantic);
			tauu_cb28.addClickHandler(checkTAUU);
			// Indian
			tauu_cb213.addClickHandler(checkIndian);
			tauu_cb213.addClickHandler(checkTAUU);

			
			ps_cb36.addClickHandler(checkPacific);
			ps_cb36.addClickHandler(checkPS);
			ps_cb37.addClickHandler(checkPacific);
			ps_cb37.addClickHandler(checkPS);
			
			cb_tp.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_tp.getValue();
					for (Iterator pacIt = pacific.iterator(); pacIt.hasNext();) {
						CheckBox cb = (CheckBox) pacIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			cb_ta.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_ta.getValue();
					for (Iterator atlIt = atlantic.iterator(); atlIt.hasNext();) {
						CheckBox cb = (CheckBox) atlIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			cb_ti.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_ti.getValue();
					for (Iterator inIt = indian.iterator(); inIt.hasNext();) {
						CheckBox cb = (CheckBox) inIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			cb_ts.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_ts.getValue();
					for (Iterator tsIt = ts.iterator(); tsIt.hasNext();) {
						CheckBox cb = (CheckBox) tsIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			cb_tauu.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_tauu.getValue();
					for (Iterator tauuIt = tauu.iterator(); tauuIt.hasNext();) {
						CheckBox cb = (CheckBox) tauuIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			
			cb_ps.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					boolean v = cb_ps.getValue();
					for (Iterator psIt = ps.iterator(); psIt.hasNext();) {
						CheckBox cb = (CheckBox) psIt.next();
						cb.setValue(v);
					}
					checkAll();
				}
				
			});
			
			cb_all.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					set.add(2);
					setBackground(3);
					boolean v = cb_all.getValue();
					for (Iterator tsIt = ts.iterator(); tsIt.hasNext();) {
						CheckBox cb = (CheckBox) tsIt.next();
						cb.setValue(v);
					}
					for (Iterator tauuIt = tauu.iterator(); tauuIt.hasNext();) {
						CheckBox cb = (CheckBox) tauuIt.next();
						cb.setValue(v);
					}
					for (Iterator psIt = ps.iterator(); psIt.hasNext();) {
						CheckBox cb = (CheckBox) psIt.next();
						cb.setValue(v);
					}
					for (Iterator regionsIt = regions.iterator(); regionsIt.hasNext();) {
						CheckBox cb = (CheckBox) regionsIt.next();
						cb.setValue(v);
					}
					for (Iterator variablesIt = variables.iterator(); variablesIt.hasNext();) {
						CheckBox cb = (CheckBox) variablesIt.next();
						cb.setValue(v);
					}
				}
				
			});
						
			ts.add(ts_cb11); 
			ts.add(ts_cb12);
			ts.add(ts_cb13);
			ts.add(ts_cb14);
			ts.add(ts_cb18);
			ts.add(ts_cb19);
			ts.add(ts_cb110);
			ts.add(ts_cb111);
			ts.add(ts_cb112);
			
			tauu.add(tauu_cb21); 
			tauu.add(tauu_cb22);
			tauu.add(tauu_cb25);
			tauu.add(tauu_cb28);
			tauu.add(tauu_cb213);
			
			ps.add(ps_cb36);
			ps.add(ps_cb37);
			
			pacific.add(ts_cb11);
			pacific.add(ts_cb12);
			pacific.add(ts_cb13);
			pacific.add(ts_cb14);
			pacific.add(tauu_cb21);
			pacific.add(tauu_cb22);
			pacific.add(tauu_cb25);
			pacific.add(ps_cb36);
			pacific.add(ps_cb37);
			
			atlantic.add(ts_cb18);
			atlantic.add(ts_cb19);
			atlantic.add(ts_cb110);
			atlantic.add(tauu_cb28);
			
			indian.add(ts_cb111);
			indian.add(ts_cb112);
			indian.add(tauu_cb213);
			
			variables.add(cb_ts);
			variables.add(cb_tauu);
			variables.add(cb_ps);
			
			regions.add(cb_tp);
			regions.add(cb_ta);
			regions.add(cb_ti);
			
			layout.setWidget(0, 0, cb_all);
			layout.setWidget(0, 1, cb_tp);
			layout.setWidget(0, 2, cb_ta);
			layout.setWidget(0, 3, cb_ti);
			
				
			layout.setWidget(1, 0, cb_ts);
			layout.setWidget(1, 1, ts_cb11);
			layout.setWidget(1, 2, ts_cb12);
			layout.setWidget(1, 3, ts_cb13);
			layout.setWidget(1, 4, ts_cb14);
			layout.setWidget(1, 8, ts_cb18);
			layout.setWidget(1, 9, ts_cb19);
			layout.setWidget(1, 10, ts_cb110);
			layout.setWidget(1, 11, ts_cb111);
			layout.setWidget(1, 12, ts_cb112);
			
			layout.setWidget(2, 0, cb_tauu);
			layout.setWidget(2, 1, tauu_cb21);
			layout.setWidget(2, 2, tauu_cb22);
			layout.setWidget(2, 5, tauu_cb25);
			layout.setWidget(2, 8, tauu_cb28);
			layout.setWidget(2, 13, tauu_cb213);
			
			layout.setWidget(3, 0, cb_ps);
			layout.setWidget(3, 6, ps_cb36);
			layout.setWidget(3, 7, ps_cb37);
			
			initWidget(layout);
			
		}
		public List<String> getActiveVariablesAndRegions() {
			List<String> active = new ArrayList<String>();
			for (Iterator tsIt = ts.iterator(); tsIt.hasNext();) {
				CheckBox checkBox = (CheckBox) tsIt.next();
				if ( checkBox.getValue() ) {
					active.add(checkBox.getFormValue());
				}
			}
			for (Iterator tauuIt = tauu.iterator(); tauuIt.hasNext();) {
				CheckBox checkBox = (CheckBox) tauuIt.next();
				if ( checkBox.getValue() ) {
					active.add(checkBox.getFormValue());
				}
			}
			for (Iterator psIt = ps.iterator(); psIt.hasNext();) {
				CheckBox checkBox = (CheckBox) psIt.next();
				if ( checkBox.getValue() ) {
					active.add(checkBox.getFormValue());
				}
			}
			return active;
		}
		ClickHandler checkTS = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean v = true;
				for (Iterator tsIt = ts.iterator(); tsIt.hasNext();) {
					CheckBox cb = (CheckBox) tsIt.next();
					v = v && cb.getValue();
				}
				cb_ts.setValue(v);
				checkAll();
			}
			
		};
		ClickHandler checkTAUU = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean v = true;
				for (Iterator tauuIt = tauu.iterator(); tauuIt.hasNext();) {
					CheckBox cb = (CheckBox) tauuIt.next();
					v = v && cb.getValue();
				}
				cb_tauu.setValue(v);
				checkAll();
			}
			
		};
		ClickHandler checkPS = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean v = true;
				for (Iterator tauuIt = ps.iterator(); tauuIt.hasNext();) {
					CheckBox cb = (CheckBox) tauuIt.next();
					v = v && cb.getValue();
				}
				cb_ps.setValue(v);
				checkAll();
			}
			
		};
		ClickHandler checkPacific = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				checkPacific();
				checkAll();

			}
		};
		private void checkPacific() {
			boolean v = true;
			for (Iterator pacIt = pacific.iterator(); pacIt.hasNext();) {
				CheckBox cb = (CheckBox) pacIt.next();
				v = v && cb.getValue();
			}
			cb_tp.setValue(v);
		}
		ClickHandler checkAtlantic = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				checkAtlantic();
				checkAll();
			}
			
		};
		private void checkAtlantic() {
			boolean v = true;
			for (Iterator atlIt = atlantic.iterator(); atlIt.hasNext();) {
				CheckBox cb = (CheckBox) atlIt.next();
				v = v && cb.getValue();
			}
			cb_ta.setValue(v);
			
		}
		ClickHandler checkIndian = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				checkIndian();
				checkAll();
			}
			
		};
		private void checkIndian() {
			boolean v = true;
			for (Iterator inIt = indian.iterator(); inIt.hasNext();) {
				CheckBox cb = (CheckBox) inIt.next();
				v = v && cb.getValue();
			}
			cb_ti.setValue(v);
		}
		private void checkAll() {
			set.add(2);
			setBackground(3);
			boolean v = true;
			boolean subv = true;
			for (Iterator tsIt = ts.iterator(); tsIt.hasNext();) {
				CheckBox cb = (CheckBox) tsIt.next();
				subv = subv && cb.getValue();
			}
			cb_ts.setValue(subv);
			v = v && subv;
			subv = true;
			for (Iterator tauuIt = tauu.iterator(); tauuIt.hasNext();) {
				CheckBox cb = (CheckBox) tauuIt.next();
				subv = subv && cb.getValue();
			}
			cb_tauu.setValue(subv);
			v = v && subv;
			subv = true;
			for (Iterator psIt = ps.iterator(); psIt.hasNext();) {
				CheckBox cb = (CheckBox) psIt.next();
				subv = subv && cb.getValue();
			}
			cb_ps.setValue(subv);
			v = v && subv;
			checkPacific();
			checkAtlantic();
			checkIndian();
			for (Iterator regionsIt = regions.iterator(); regionsIt.hasNext();) {
				CheckBox cb = (CheckBox) regionsIt.next();
				v = v && cb.getValue();
			}
			for (Iterator variablesIt = variables.iterator(); variablesIt.hasNext();) {
				CheckBox cb = (CheckBox) variablesIt.next();
				v = v && cb.getValue();
			}
			cb_all.setValue(v);
		}
	};
}
