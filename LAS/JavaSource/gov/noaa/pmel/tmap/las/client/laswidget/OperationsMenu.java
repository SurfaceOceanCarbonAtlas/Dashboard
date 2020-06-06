package gov.noaa.pmel.tmap.las.client.laswidget;

import gov.noaa.pmel.tmap.las.client.serializable.OperationSerializable;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
/**
 * A widget that shows the "non-plot" operations from an LAS (like Google Earth, animation, etc).
 * @author rhs
 *
 */
public class OperationsMenu extends Composite {
	/*
	 * The way this menu is used in the old UI is that all of the buttons appear all at once on the UI, but they are disabled.
	 * Then when the state changes, those buttons which apply to to the new state are enabled.
	 */
	HorizontalPanel buttonBar;
	OperationPushButton animationButton = new OperationPushButton("Animate");
	OperationPushButton compareButton = new OperationPushButton("Compare");
	OperationPushButton correlationButton = new OperationPushButton("Correlation Viewer");
	OperationPushButton googleEarthButton = new OperationPushButton("Google Earth");
	OperationPushButton showValuesButton = new OperationPushButton("Show Values");
	OperationPushButton exportToDesktopButton = new OperationPushButton("Export to Desktop Application");
	OperationPushButton saveAsButton = new OperationPushButton("Save As...");
	OperationPushButton climateAnalysis = new OperationPushButton("Climate Analysis...");
	OperationPushButton dsgTable = new OperationPushButton("Table of Datasets");
	OperationPushButton thumbnailTable = new OperationPushButton("Thumbnails");
	
	boolean hasComparison = false;
	boolean hasAnimation = false;
	boolean hasCorrelation = false;
	boolean hasGoogleEarth = false;
	ClickHandler clickHandler;
	public OperationsMenu() {
		buttonBar = new HorizontalPanel();
		buttonBar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        turnOffButtons();
        animationButton.addStyleDependentName("SMALLER");
        animationButton.setTitle("Interactive interface for making a sequence of plots over time.");
        compareButton.addStyleDependentName("SMALLER");
        correlationButton.addStyleDependentName("SMALLER");
        correlationButton.setTitle("Beta interface to make a scatter plot of a property vs. another property.");
        googleEarthButton.addStyleDependentName("SMALLER");
        googleEarthButton.setTitle("View a plot of data draped over the globe using Google Earth.");
        showValuesButton.addStyleDependentName("SMALLER");
        showValuesButton.setTitle("Look at the data values in a new window.");
        exportToDesktopButton.addStyleDependentName("SMALLER");
        exportToDesktopButton.setTitle("Get a few lines of native script for various analysis packages.");
        saveAsButton.addStyleDependentName("SMALLER");
        saveAsButton.ensureDebugId("saveAsButton");
        saveAsButton.setTitle("Save data in various text and binary formats.");
        climateAnalysis.addStyleDependentName("SMALLER");
        climateAnalysis.setTitle("Perform time average spectrum and other advanced analysis.");
        dsgTable.addStyleDependentName("SMALLER");
        dsgTable.setTitle("See a table of current observations by ID.");
        thumbnailTable.addStyleDependentName("SMALLER");
        thumbnailTable.setTitle("See a table of select property-property plots.");
		buttonBar.add(animationButton);
		buttonBar.add(correlationButton);
		buttonBar.add(googleEarthButton);
		buttonBar.add(showValuesButton);
		buttonBar.add(exportToDesktopButton);
		buttonBar.add(saveAsButton);
		buttonBar.add(climateAnalysis);
		buttonBar.add(dsgTable);
		buttonBar.add(thumbnailTable);
		climateAnalysis.setVisible(false);
		dsgTable.setVisible(false);
		thumbnailTable.setVisible(false);
		initWidget(buttonBar);
		buttonBar.setSize("100%", "100%");
	}
    private void turnOffButtons() {
		animationButton.setEnabled(false);
		animationButton.setOperation(null);
		compareButton.setEnabled(false);
		compareButton.setOperation(null);
		googleEarthButton.setEnabled(false);
		googleEarthButton.setOperation(null);
		showValuesButton.setEnabled(false);
		showValuesButton.setOperation(null);
		exportToDesktopButton.setEnabled(false);
		exportToDesktopButton.setOperation(null);
		saveAsButton.setEnabled(false);
		saveAsButton.setOperation(null);
		correlationButton.setEnabled(false);
		correlationButton.setOperation(null);
	    climateAnalysis.setEnabled(false);
	    climateAnalysis.setOperation(null);
	    dsgTable.setEnabled(false);
	    dsgTable.setOperation(null);
	    thumbnailTable.setEnabled(false);
        thumbnailTable.setOperation(null);
    }
	public void setMenus(OperationSerializable[] ops, String view) {
		turnOffButtons();
		hasComparison = false;
		hasAnimation = false;
		hasGoogleEarth = false;
		hasCorrelation = false;
		
	
	    for (int i = 0; i < ops.length; i++) {
			OperationSerializable op = ops[i];
			String category = op.getAttributes().get("category").toLowerCase(Locale.ENGLISH);
			List<String> views = op.getViews();
			for (Iterator viewIt = views.iterator(); viewIt.hasNext();) {
				String op_view = (String) viewIt.next();
				if ( category.equals("comparison")) {
					if ( op.getName().toLowerCase(Locale.ENGLISH).contains("compar") ) {
						if ( op_view.equals(view) ) {
							if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
								if ( !hasComparison ) {
									hasComparison = true;
									compareButton.setOperation(op);
									compareButton.setEnabled(true);
								}
							}
						}
					}
				} else if ( category.contains("animation") ) {
					if ( op_view.equals(view) ) {
						if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) 
								&& ( op.getAttributes().get("default") != null && op.getAttributes().get("default").equalsIgnoreCase("true") ) ) {	
							if ( !hasAnimation ) {
								hasAnimation = true;
								animationButton.setOperation(op);
								animationButton.setEnabled(true);
							}
						}
					}
				} else if ( category.contains("correlation") ) {
                    if ( op_view.equals(view) ) {
                        if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) 
                                && ( op.getAttributes().get("default") != null && op.getAttributes().get("default").equalsIgnoreCase("true") ) ) {  
                            if ( !hasCorrelation ) {
                                hasCorrelation = true;
                                correlationButton.setOperation(op);
                                correlationButton.setEnabled(true);
                            }
                        }
                    }
				} else if ( category.contains("globe") ) {
					if ( op_view.equals(view) ) {
						if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) 
								&& ( op.getAttributes().get("default") != null && op.getAttributes().get("default").equalsIgnoreCase("true") ) ) {
							if ( !hasGoogleEarth ) {
								hasGoogleEarth = true;
								googleEarthButton.setOperation(op);
								googleEarthButton.setEnabled(true);
							}
						}
					}
				} else if ( category.contains("table") && !category.contains("trajectory") ) {
					if ( op_view.equals(view) ) {
						if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
							if ( op.getName().toLowerCase(Locale.ENGLISH).contains("values") ) {
								showValuesButton.setOperation(op);
								showValuesButton.setEnabled(true);
							}
							if ( op.getName().toLowerCase(Locale.ENGLISH).contains("download") ) {
								saveAsButton.setOperation(op);
								saveAsButton.setEnabled(true);
							}
						}
					}
				} else if ( category.equals("file") ) {
					if ( op_view.equals(view) ) {
						if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
							if ( op.getName().toLowerCase(Locale.ENGLISH).contains("script") ) {
								exportToDesktopButton.setOperation(op);
								exportToDesktopButton.setEnabled(true);
							}
						}
					}
				} else if ( category.equals("climate_analysis") ) {
				    if ( op_view.equals(view) ) {
				        if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
                            if ( op.getName().toLowerCase(Locale.ENGLISH).contains("climate analysis") ) {
                                climateAnalysis.setOperation(op);
                                climateAnalysis.setVisible(true);
                                climateAnalysis.setEnabled(true);
                            }
				        }
				    }
				} else if ( category.equals("trajectory_table") ) {
                    if ( op_view.equals(view) ) {
                        if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
                            if ( op.getName().toLowerCase(Locale.ENGLISH).contains("trajectory table") ) {
                                dsgTable.setOperation(op);
                                dsgTable.setVisible(true);
                                dsgTable.setEnabled(true);
                            }
                        }
                    }
                } else if ( category.equals("thumbnails") ) {
                    if ( op_view.equals(view) ) {
                        if ( (op.getAttributes().get("private") == null || !op.getAttributes().get("private").equalsIgnoreCase("true") ) ) {
                            if ( op.getName().toLowerCase(Locale.ENGLISH).contains("thumbnail") ) {
                               thumbnailTable.setOperation(op);
                               thumbnailTable.setVisible(true);
                               thumbnailTable.setEnabled(true);
                            }
                        }
                    }
                }
			}
		}
	}
    public void addClickHandler(ClickHandler clickHandler) {
    	this.clickHandler = clickHandler;   	
    	compareButton.addClickHandler(clickHandler);
        animationButton.addClickHandler(clickHandler);
        correlationButton.addClickHandler(clickHandler);
        googleEarthButton.addClickHandler(clickHandler);
        showValuesButton.addClickHandler(clickHandler);
        saveAsButton.addClickHandler(clickHandler);
        exportToDesktopButton.addClickHandler(clickHandler);
        climateAnalysis.addClickHandler(clickHandler);
        dsgTable.addClickHandler(clickHandler);
        thumbnailTable.addClickHandler(clickHandler);
    }
    public void setGoogleEarthButtonEnabled(boolean enable) {
        googleEarthButton.setEnabled(enable);
    }
    public void setCorrelationButtonEnabled(boolean b) {
        correlationButton.setEnabled(b);
    }
    public void enableByView(String view, boolean hasT) {
        if ( animationButton.getOperation() != null && animationButton.getOperation().getViews().contains(view) && hasT ) {
            animationButton.setEnabled(true);
        } else {
            animationButton.setEnabled(false);
        }
        if ( compareButton.getOperation() != null && compareButton.getOperation().getViews().contains(view) ) {
            compareButton.setEnabled(true);
        } else {
            compareButton.setEnabled(false);
        }
        if ( googleEarthButton.getOperation() != null && googleEarthButton.getOperation().getViews().contains(view) ) {
            googleEarthButton.setEnabled(true);
        } else {
            googleEarthButton.setEnabled(false);
        }
        if ( showValuesButton.getOperation() != null && showValuesButton.getOperation().getViews().contains(view) ) {
            showValuesButton.setEnabled(true);
        } else {
            showValuesButton.setEnabled(false);
        }
        if ( exportToDesktopButton.getOperation() != null && exportToDesktopButton.getOperation().getViews().contains(view) ) {
            exportToDesktopButton.setEnabled(true);

        } else {
            exportToDesktopButton.setEnabled(false);
        }
        if ( saveAsButton.getOperation() != null && saveAsButton.getOperation().getViews().contains(view) ) {
            saveAsButton.setEnabled(true);
        } else {
            saveAsButton.setEnabled(false);
        }
        if ( correlationButton.getOperation() != null && correlationButton.getOperation().getViews().contains(view) ) {
            correlationButton.setEnabled(true);
        } else {
            correlationButton.setEnabled(false);
        }
        String profile = DOM.getElementById("las-profile").getPropertyString("content");
        if (profile != null && profile.equals("LAS-ESGF")) {
            if ( climateAnalysis != null && climateAnalysis.getOperation() != null && climateAnalysis.getOperation().getViews().contains(view) ) {
                climateAnalysis.setEnabled(true);
            } else {
                climateAnalysis.setVisible(false);
            }
        } else {
            climateAnalysis.setVisible(false);
        }
        if ( dsgTable != null && dsgTable.getOperation() != null && dsgTable.getOperation().getViews().contains(view) ) {
            dsgTable.setEnabled(true);
            dsgTable.setVisible(true);
        } else {
            dsgTable.setVisible(false);
        }
        if ( thumbnailTable != null && thumbnailTable.getOperation() != null && thumbnailTable.getOperation().getViews().contains(view) ) {
            thumbnailTable.setEnabled(true);
            thumbnailTable.setVisible(true);
        } else {
            thumbnailTable.setVisible(false);
        }
    }
}
