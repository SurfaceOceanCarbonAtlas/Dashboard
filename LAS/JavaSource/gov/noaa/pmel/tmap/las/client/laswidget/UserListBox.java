package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.ClientFactory;
import gov.noaa.pmel.tmap.las.client.event.RemoveVariableEvent;
import gov.noaa.pmel.tmap.las.client.event.VariablePluralityEvent;
import gov.noaa.pmel.tmap.las.client.event.VariableSelectionChangeEvent;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Widget;

public class UserListBox extends Composite {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    PushButton add = new PushButton("+");
    ListBox list;
    private int minItemsForAddButtonToBeVisible = 2;
    private String name;
    FlexTable panel = new FlexTable();
    PushButton remove = new PushButton("X");
    List<VariableSerializable> allVariables = new ArrayList<VariableSerializable>();
    List<VariableSerializable> vectorVariables = new ArrayList<VariableSerializable>();
    private VariableMetadataView variableMetadataView;
    private boolean colorBy = false;
    private UserListBox() {
        super();
        list = new ListBox();
        init(null, true);
    }

    /**
     * @wbp.parser.constructor
     */
    public UserListBox(String id, boolean addChangeHandler) {
        super();
        list = new ListBox();
        init(id, addChangeHandler);
    }
    private ClickHandler addButtonClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {

            eventBus.fireEventFromSource(new VariablePluralityEvent(true), UserListBox.this);

        }

    };
    private ClickHandler removeButtonClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {

            eventBus.fireEventFromSource(new RemoveVariableEvent(), UserListBox.this);

        }

    };

    private ChangeHandler variableChangeHandler = new ChangeHandler() {

        @Override
        public void onChange(ChangeEvent event) {

            eventBus.fireEventFromSource(new VariableSelectionChangeEvent(), UserListBox.this);

        }

    };
    public UserListBox(String id, boolean isMultipleSelect, boolean addChangeHandler) {
        super();
        list = new ListBox(isMultipleSelect);
        init(id, addChangeHandler);
    }
    public void addAddButtonClickHandler(ClickHandler handler) {
        add.addClickHandler(handler);
    }

    public void addItem(String name, String value) {
        list.addItem(name, value);
        if ( list.getItemCount() == minItemsForAddButtonToBeVisible ) {
            add.setVisible(true);
        }
    }

    public void addRemoveButtonClickHandler(ClickHandler handler) {
        remove.addClickHandler(handler);
    }

    public void addUserObject(VariableSerializable var) {
        allVariables.add(var);
    }

    public void addVectors() {
        for ( int i = 0; i < vectorVariables.size(); i++ ) {
            VariableSerializable v = vectorVariables.get(i);
            allVariables.add(v);
            list.addItem(v.getName(), v.getID());
        }
        vectorVariables.clear();
    }

    public void clear() {
        allVariables.clear();
        list.clear();
    }

    public int getItemCount() {
        return allVariables.size();
    }

    /**
     * @return the minItemsForAddButtonToBeVisible
     */
     public int getMinItemsForAddButtonToBeVisible() {
         return minItemsForAddButtonToBeVisible;
     }

     /**
      * @return the name that identifies this instance
      */
     public String getName() {
         return name;
     }

     public String getName(int i) {
         return list.getItemText(i);
     }

     public int getSelectedIndex() {
         return list.getSelectedIndex();
     }

     public VariableSerializable getUserObject(int index) {
         if ( index >= 0 && index < allVariables.size() ) {
             return allVariables.get(index);
         } else {
             GWT.log("Out of bounds index is:" + index);
             return null;
         }
     }

     public String getValue(int i) {
         return list.getValue(i);
     }

     public List<VariableSerializable> getVariables() {
         return allVariables;
     }

     public void init(String id, boolean addChangeHandler) {
         add.addClickHandler(addButtonClickHandler);
         remove.addClickHandler(removeButtonClickHandler);
         add.setWidth("30px");
         remove.setWidth("30px");
         if (addChangeHandler) list.addChangeHandler(variableChangeHandler);
         add.addStyleDependentName("SMALLER");
         remove.addStyleDependentName("SMALLER");
         setName(id);
         add.setTitle("Click to add another variable.");
         panel.setWidget(0, 0, add);
         remove.setTitle("Click to remove this variable.");
         panel.setWidget(0, 1, remove);
         add.setVisible(false);
         remove.setVisible(false);
         add.addClickHandler(new ClickHandler() {

             @Override
             public void onClick(ClickEvent event) {
                 // When this handler is activated,
                 // this button should disappear
                 // and the the remove button should appear
                 add.setVisible(false);
             }

         });
         panel.setWidget(0, 2, list);
         initWidget(panel);
         panel.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
         panel.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
         panel.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
         panel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
         panel.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
         panel.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
     }

     public void addDataSetInfoWidget(VariableMetadataView variableMetadataView) {
         if ( variableMetadataView != null ) {
             this.variableMetadataView = variableMetadataView;
             panel.setWidget(0, 3, variableMetadataView);
             panel.getCellFormatter().setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);
             panel.getCellFormatter().setHorizontalAlignment(0, 3, HasHorizontalAlignment.ALIGN_CENTER);
         }
     }

     public void removeVectors() {
         vectorVariables.clear();
         List<Integer> r = new ArrayList<Integer>();
         for ( int i = 0; i < list.getItemCount(); i++ ) {
             VariableSerializable v = allVariables.get(i);
             if ( v.isVector() ) {
                 vectorVariables.add(v);
                 r.add(i);
             }
         }
         for ( Iterator rIt = r.iterator(); rIt.hasNext(); ) {
             Integer index = (Integer) rIt.next();
             list.removeItem(index.intValue());
         }
         for ( int i = 0; i < vectorVariables.size(); i++ ) {
             VariableSerializable v = vectorVariables.get(i);
             allVariables.remove(v);
         }
     }

     public void setAddButtonVisible(boolean v) {
         add.setVisible(v);
     }

     /**
      * @param minItemsForAddButtonToBeVisible
      *            the minItemsForAddButtonToBeVisible to set
      */
     public void setMinItemsForAddButtonToBeVisible(int minItemsForAddButtonToBeVisible) {
         this.minItemsForAddButtonToBeVisible = minItemsForAddButtonToBeVisible;
     }

     /**
      * @param name
      *            the name to set that identifies this instance
      */
     public void setName(String name) {
         this.name = name;
     }

     public void setRemoveButtonVisible(boolean v) {
         remove.setVisible(v);
     }

     public void setSelectedIndex(int index) {
         list.setSelectedIndex(index);
     }

     public void setVariables(List<VariableSerializable> variables) {
         clear();
         allVariables.clear();
         for ( int i = 0; i < variables.size(); i++ ) {
        	 VariableSerializable v = variables.get(i);

        	 String uunnts = v.getAttributes().get("units");
        	 // Time series sub-set variables are not very interesting to plot.
        	 
        	 add(v);
        	 if((i==0) && (variableMetadataView!=null)){
        		 variableMetadataView.setDSID(v.getDSID());
        	 }

         }
         setAddButtonVisible(variables.size() > 1);
     }
     private void add(VariableSerializable v) {
         allVariables.add(v);
         list.addItem(v.getName(), v.getID());
        
     }
     public void setColorBy (boolean colorBy) {
         this.colorBy = colorBy;
     }
     public void setVariable(VariableSerializable variable) {
         String id = variable.getID();
         setSelectedVariableById(id);
     }
     public void setVariables(Vector<VariableSerializable> variables) {
         List<VariableSerializable> variableList = new ArrayList<VariableSerializable>(variables);
         setVariables(variableList);
     }

     public void setAddButtonEnabled(boolean b) {
         add.setEnabled(b);
     }

     public void setSelectedVariableById(String id) {
         int index = 0;
         for (int i = 0; i < list.getItemCount(); i++) {
             VariableSerializable v = getUserObject(i);
             if ( v.getID().equals(id) ) {
                 index = i;
             }
         }
         setSelectedIndex(index);
     }
     public void setEnabled(boolean b) {
         list.setEnabled(b);
     }
}
