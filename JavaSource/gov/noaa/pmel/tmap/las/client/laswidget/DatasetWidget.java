package gov.noaa.pmel.tmap.las.client.laswidget;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.Serializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A tree Widget that is the data set picker for GWT LAS clients which understands how to initialize itself and is used 
 * by the {@link gov.noaa.pmel.tmap.las.client.laswidget.DatasetButton}
 */
public class DatasetWidget extends Tree {
	
	List<DatasetFilter> filters = new ArrayList<DatasetFilter>();
	
    TreeItem currentlySelected = null;
    String openid;
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.TreeListener#onTreeItemSelected(com.google.gwt.user.client.ui.TreeItem)
	 */
	public void onTreeItemSelected(TreeItem item) {
		currentlySelected = item;
		Object u = item.getUserObject();
		if ( u instanceof VariableSerializable ) {
			VariableSerializable v = (VariableSerializable) u;
		}
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.TreeListener#onTreeItemStateChanged(com.google.gwt.user.client.ui.TreeItem)
	 */
//	public void onTreeItemStateChanged(TreeItem item) {
//		currentlySelected = item;
//		if ( item.getChild(0).getText().equals("Loading...") ) {
//			CategorySerializable cat = (CategorySerializable) item.getUserObject();
//			Util.getRPCService().getCategories(cat.getID(), categoryCallback);
//		}
//	}
	OpenHandler<TreeItem> open = new OpenHandler<TreeItem>() {

		@Override
		public void onOpen(OpenEvent<TreeItem> event) {
			TreeItem item = event.getTarget();
			currentlySelected = item;
			if ( item.getChild(0).getText().equals("Loading...") ) {
				CategorySerializable cat = (CategorySerializable) item.getUserObject();
				Util.getRPCService().getCategories(cat.getID(), categoryCallback);
			}
		}
		
	};
	
	SelectionHandler<TreeItem> selection = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			TreeItem item = event.getSelectedItem();
			currentlySelected = item;
			if ( item.getChild(0).getText().equals("Loading...") ) {
				CategorySerializable cat = (CategorySerializable) item.getUserObject();
				Util.getRPCService().getCategories(cat.getID(), categoryCallback);
			}
		}
		
	};
	
	/**
	 * Set up the tree and the associated RPC.
	 */
	public void init() {
		Util.getRPCService().getCategories(null, categoryCallback);
		addOpenHandler(open);
		addSelectionHandler(selection);
	}
	 public void addFilter( DatasetFilter filter ) {
     	filters.add(filter);
     }
     public void removeFilter( DatasetFilter filter ) {
     	filters.remove(filter);
     }
	AsyncCallback categoryCallback = new AsyncCallback() {
		public void onSuccess(Object result) {
			CategorySerializable[] cats = (CategorySerializable[]) result;
			if ( cats != null && cats.length > 0 ) {
				if ( currentlySelected == null ) {
					for (int i = 0; i < cats.length; i++) {
						CategorySerializable cat = cats[i];
						if ( applyFilters(cat) ) {
							TreeItem item = new TreeItem();
							item.addItem("Loading...");
							InnerItem inner = new InnerItem(cat);
							item.setWidget(inner);
							item.setUserObject(cat);
							addItem(item);
						}
					}
				} else {
					for (int i = 0; i < cats.length; i++) {
						CategorySerializable cat = cats[i];
						if ( cat.isCategoryChildren() ) {
							String name = cat.getName();
							TreeItem item;
							if ( i == 0 ) {
							    item = currentlySelected.getChild(0);
							} else {
								item = new TreeItem();
							}
							item.addItem("Loading...");
							InnerItem inner = new InnerItem(cat);
							item.setWidget(inner);
							item.setUserObject(cat);
							if ( i > 0 ) {
								currentlySelected.addItem(item);
							}
						} else {
							// Must have variable children...
							TreeItem item = currentlySelected.getChild(0);
							if ( cat.hasMultipleDatasets() ) {
								DatasetSerializable[] dses = cat.getDatasetSerializableArray();
								DatasetSerializable ds = dses[0];
								VariableSerializable[] vars = ds.getVariablesSerializable();
								loadItem(item, vars);
								for (int j = 1; j < dses.length; j++) {
									ds = dses[j];
									vars = ds.getVariablesSerializable();
									loadItem(vars);
								}
							} else {
								DatasetSerializable ds = cat.getDatasetSerializable();
								VariableSerializable[] vars = ds.getVariablesSerializable();							
								loadItem(item, vars);
							}
						}
					}
				}
			}
		}
       
		private boolean applyFilters(CategorySerializable cat) {
			// Apply any filters.
			boolean include = true;
			if ( filters.size() > 0 ) {
				for (Iterator filterIt = filters.iterator(); filterIt.hasNext();) {
					DatasetFilter filter = (DatasetFilter) filterIt.next();

					// This should be done with introspection, but for now do a big cheat
					String name = "x";
					String value = "y";
					if ( filter.getAttribute().equals("name") ) {
						name = cat.getName().toLowerCase();
						value = filter.getValue().toLowerCase();

					} else if ( filter.getAttribute().equals("ID") ) {
						name = cat.getID();
						value = filter.getValue();
					}
					if ( name.contains(value) ) {
						include = include && filter.isInclude();
					} else {
						include = include && !filter.isInclude();
					}
				} 
			}

			return include;
		}

		public void onFailure(Throwable caught) {
			Window.alert("Server Request Failed: "+caught.getMessage());
		}
		
		private void loadItem(TreeItem item, VariableSerializable[] vars ) {			
			item.setText(vars[0].getName());
			item.setUserObject(vars[0]);
			for (int j = 1; j < vars.length; j++) {
				item = new TreeItem();
				item.setText(vars[j].getName());
				item.setUserObject(vars[j]);
				currentlySelected.addItem(item);
			}
		}
		private void loadItem(VariableSerializable[] vars) {
			for (int j = 0; j < vars.length; j++) {
				TreeItem item = new TreeItem();
				item.setText(vars[j].getName());
				item.setUserObject(vars[j]);
			    currentlySelected.addItem(item);
			}
		}
	};
	public Object getCurrentlySelected() {
		return currentlySelected.getUserObject();
	}

	public void setOpenID(String openid) {
		this.openid = openid;
	}
	
	public class InnerItem extends Composite {
		Grid grid = new Grid(1,2);
		Label label = new Label("Loading data...");
		Image image = new Image(GWT.getModuleBaseURL()+"../images/info.png");
		PopupPanel inner = new PopupPanel(true);
		Button close = new Button("Close");
		VerticalPanel innerLayout = new VerticalPanel();
        public InnerItem(Serializable s) {
        	close.addStyleDependentName("SMALLER");
        	close.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					inner.hide();
				}
        		
        	});
        	innerLayout.add(close);
        	grid.getCellFormatter().setWidth(0, 0, "85%");
        	grid.getCellFormatter().setWidth(0, 1, "5%");
        	label.setText(s.getName());
        	grid.setWidget(0, 0, label);
        	if ( s instanceof CategorySerializable) {
        		CategorySerializable c = (CategorySerializable) s;
        		if ( c.getDoc() != null || c.getAttributes().get("children_dsid") != null) {
        			image.addClickHandler(new ClickHandler() {
        				@Override
        				public void onClick(ClickEvent event) {
        					inner.setPopupPosition(image.getAbsoluteLeft(), image.getAbsoluteTop());
        					inner.show();
        				}
        			});
        			grid.setWidget(0, 1, image);
        			if ( c.getDoc() != null ) {
        				String url = c.getDoc();
        				if ( !c.getDoc().equals("") ) {
        					Anchor link = new Anchor("Documentation", url, "_blank");
        					innerLayout.add(link);
        				}
        			}
        			if ( c.getAttributes().get("children_dsid") != null ) {
        				Anchor meta = new Anchor("Variable and Grid Description", "getMetadata.do?dsid="+c.getAttributes().get("children_dsid"), "_blank");
        				innerLayout.add(meta);
        			}
        			inner.add(innerLayout);
        		}
        	}
        	initWidget(grid);
        }
		
	}

//	@Override
//	public void onSelection(SelectionEvent event) {
//		currentlySelected = (TreeItem) event.getSelectedItem();
//		if ( currentlySelected.getChild(0).getText().equals("Loading...") ) {
//			CategorySerializable cat = (CategorySerializable) currentlySelected.getUserObject();
//			Util.getRPCService().getCategories(cat.getID(), categoryCallback);
//		}
//	}
}
