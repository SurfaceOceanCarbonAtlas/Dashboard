package gov.noaa.pmel.tmap.las.client;

import gov.noaa.pmel.tmap.las.client.activity.HelpMenuBar;
import gov.noaa.pmel.tmap.las.client.event.CancelEvent;
import gov.noaa.pmel.tmap.las.client.event.StringValueChangeEvent;
import gov.noaa.pmel.tmap.las.client.laswidget.AxisWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.CancelButton;
import gov.noaa.pmel.tmap.las.client.laswidget.Constants;
import gov.noaa.pmel.tmap.las.client.laswidget.CruiseIconWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.DateTimeWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.HelpPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.LASAnnotationsPanel;
import gov.noaa.pmel.tmap.las.client.laswidget.LASRequest;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableConstraintLayout;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableConstraintWidget;
import gov.noaa.pmel.tmap.las.client.laswidget.VariableListBox;
import gov.noaa.pmel.tmap.las.client.map.MapSelectionChangeListener;
import gov.noaa.pmel.tmap.las.client.map.OLMapWidget;
import gov.noaa.pmel.tmap.las.client.serializable.CategorySerializable;
import gov.noaa.pmel.tmap.las.client.serializable.DatasetSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.GridSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.VariableSerializable;
import gov.noaa.pmel.tmap.las.client.ui.IESafeImage;
import gov.noaa.pmel.tmap.las.client.util.URLUtil;
import gov.noaa.pmel.tmap.las.client.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

/**
 * @author rhs
 * 
 */
public class Correlation implements EntryPoint {
    ImageData scaledImage;
	private static final AppConstants CONSTANTS = GWT.create(AppConstants.class);
	NumberFormat dFormat = NumberFormat.getFormat("########.##");
	CruiseIconWidget cruiseIcons = new CruiseIconWidget();
	FlexTable timePanel = new FlexTable();
	PushButton reset = new PushButton("Reset");
	DateTimeWidget timeConstraint = new DateTimeWidget();
	AxisWidget zAxisWidget = new AxisWidget();
	VerticalPanel spaceTimeConstraints = new VerticalPanel();
	HorizontalPanel spaceTimeTopRow = new HorizontalPanel();
	Map<String, VariableSerializable> xDatasetVariables = new HashMap<String, VariableSerializable>();
	ToggleButton annotationsButton;
	LASAnnotationsPanel lasAnnotationsPanel = new LASAnnotationsPanel();
	HorizontalPanel buttonPanel = new HorizontalPanel();
	HorizontalPanel topRow = new HorizontalPanel();
	HorizontalPanel output = new HorizontalPanel();
	HelpPanel help = new HelpPanel();

	RootPanel rootPanel;
	HelpMenuBar hmb = new HelpMenuBar();
	HelpPanel spaceTimeHelp = new HelpPanel();
	PopupPanel spin;
	HTML spinImage;
	HorizontalPanel coloredBy = new HorizontalPanel();
	VariableConstraintLayout constraintsLayout = new VariableConstraintLayout(
			"Variable Constraints:", true);
	VariableConstraintWidget xVariableConstraint = new VariableConstraintWidget();
	VariableConstraintWidget yVariableConstraint = new VariableConstraintWidget();
	OLMapWidget mapConstraint = new OLMapWidget("128px", "256px");
	Label selection = new Label("Current selection:");
	Label horizontalLabel = new Label("Horizontal: ");
	Label verticalLabel = new Label("Vertical: ");
	FlexTable controlPanel = new FlexTable();
	VerticalPanel topPanel = new VerticalPanel();
	FlexTable outputPanel = new FlexTable();
	VariableListBox xVariables = new VariableListBox();
	VariableListBox yVariables = new VariableListBox();
	VariableListBox colorVariables = new VariableListBox();
	PushButton update = new PushButton("Update Plot");
	PushButton print = new PushButton("Print");
	CheckBox colorCheckBox = new CheckBox();
	Label warnText;
	PopupPanel warning = new PopupPanel(true);
	PushButton ok;
	PushButton cancel;
	boolean youveBeenWarned = false;
	// The current intermediate file
	String netcdf = null;
	String dsid;
	String varid;
	String currentURL;
	String operationID;
	// Drawing start position
	int startx = -1;
	int starty = -1;
	int endx;
	int endy;
	boolean draw = false;
	IESafeImage plotImage;
	
	// This is the canvas and context for the image.
	Context2d imageCanvasContext;
	Canvas imageCanvas;
	
	// This is the canvas and context for the drawing rectangle.
	Canvas drawingCanvas;
	Context2d drawingCanvasContext;
	
	// This is the thingy that will hold them one on top of each other.
	AbsolutePanel canvasDiv = new AbsolutePanel();
	
	
	CssColor randomColor;
	// Have a cancel button ready to go for this panel.
	CancelButton cancelButton;
	final static String operationType = "v7";
	protected int x_image_size;
	protected int y_image_size;
	protected int x_plot_size;
	protected int y_plot_size;
	protected int x_offset_from_left;
	protected int y_offset_from_bottom;
	protected int x_offset_from_right;
	protected int y_offset_from_top;
	protected double x_axis_lower_left;
	protected double y_axis_lower_left;
	protected double x_axis_upper_right;
	protected double y_axis_upper_right;
	protected double world_startx;
	protected double world_starty;
	protected double world_endx;
	protected double world_endy;
	protected double x_per_pixel;
	protected double y_per_pixel;
	protected String printURL;
	protected String xlo;
	protected String xhi;
	protected String ylo;
	protected String yhi;
	protected String tlo;
	protected String thi;
	protected String zlo;
	protected String zhi;
	
	protected int annotationsRow = 0;
	protected int outputPanelRow = 2;
	protected int shadowCanvasRow = 3;
	protected int variablePanelRow = 1;
	
	HTML message = new HTML("Only variables with the same dimensions as the y-axis variable can be selected.");
	

	// There are 3 states we want to track.

	// The state immediately previous to a widget change that might cause the
	// prompt about new data.
	// If the user cancels the change, revert to this state.
	LASRequest undoState;

	// The current request.
	LASRequest lasRequest;

	EventBus eventBus;

	// Some information to control the size of the image as the browser window
	// changes size.
	int topAndBottomPadding = 190;

	int pwidth;

	double image_h = 722.;
	double image_w = 838.;
	int image_w_min = 400;

	int fixedZoom;
	boolean autoZoom = true;;

	double globalMin = 99999999.;
	double globalMax = -99999999.;

	double imageScaleRatio = 1.0; // Keep track of the factor by which the image
									// has been scaled.
	
	boolean outx;
	boolean outy;

	@Override
	public void onModuleLoad() {

		ClientFactory cf = GWT.create(ClientFactory.class);
		eventBus = cf.getEventBus();

		// Add the simple help menu
		rootPanel = RootPanel.get("headerBottomRight");
		hmb.setWidth("55px");
		rootPanel.add(hmb);
		// Listen for StringValueChangeEvents from LASAnnotationsPanel(s)
		eventBus.addHandler(StringValueChangeEvent.TYPE,
				new StringValueChangeEvent.Handler() {
					@Override
					public void onValueChange(StringValueChangeEvent event) {
						Object source = event.getSource();
						if ((source != null)
								&& (source instanceof LASAnnotationsPanel)) {
							String open = event.getValue();
							boolean isOpen = Boolean.parseBoolean(open);
							if (!isOpen) {
								LASAnnotationsPanel sourceLASAnnotationsPanel = (LASAnnotationsPanel) source;
								if ((sourceLASAnnotationsPanel != null)
										&& (sourceLASAnnotationsPanel
												.equals(lasAnnotationsPanel)))
									synchAnnotationsMode();
							}
						}
					}
				});


		
		int rndRedColor = 244;
		int rndGreenColor = 154;
		int rndBlueColor = 0;
		double rndAlpha = .45;
		randomColor = CssColor.make("rgba(" + rndRedColor + ", "
				+ rndGreenColor + "," + rndBlueColor + ", " + rndAlpha + ")");

		cancelButton = new CancelButton("Correlation");
		eventBus.addHandler(CancelEvent.TYPE, cancelRequestHandler);
		mapConstraint.setMapListener(mapListener);

		timePanel.setWidget(0, 0, timeConstraint);
		timePanel.setWidget(0, 1, reset);

		reset.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				timeConstraint.reinit();
				update.addStyleDependentName("APPLY-NEEDED");
			}

		});
		reset.addStyleDependentName("SMALLER");
		reset.setTitle("Reset time to the full range.");
		String spinImageURL = URLUtil.getImageURL() + "/mozilla_blu.gif";

		annotationsButton = new ToggleButton("Annotations", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showAnnotations(annotationsButton.isDown());
			}

		});
		annotationsButton.addStyleDependentName("SMALLER");
		showAnnotations(true);
		annotationsButton
				.setTitle("Click to hide the annotations of the plot.");
		spinImage = new HTML("<img src=\"" + spinImageURL
				+ "\" alt=\"Spinner\"/>");
		spin = new PopupPanel();
		spin.add(spinImage);
		update.addStyleDependentName("SMALLER");
		update.setWidth("80px");
		update.ensureDebugId("update");

		xVariableConstraint.addChangeHandler(constraintChange);
		xVariableConstraint.setDebugId("xVariableConstraint");
		yVariableConstraint.addChangeHandler(constraintChange);
		yVariableConstraint.setDebugId("yVariableConstraint");
		xVariableConstraint.addApplyHandler(applyHandler);
		yVariableConstraint.addApplyHandler(applyHandler);

		constraintsLayout.setVisible(false);
		print.addStyleDependentName("SMALLER");
		print.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				printerFriendly();
			}

		});
		print.setEnabled(false);
		buttonPanel.add(update);
		buttonPanel.add(print);
		topRow.add(help);
//		topRow.add(new HTML("<b>&nbsp;&nbsp;Data Selection: </b>"));
//		controlPanel.setWidget(0, 0, topRow);
//		controlPanel.getFlexCellFormatter().setColSpan(0, 0, 5);
//		controlPanel.setWidget(1, 0, yVariables);
//		controlPanel.setWidget(1, 1, new Label(" as a function of "));
//		controlPanel.setWidget(1, 2, xVariables);

		colorCheckBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (colorCheckBox.getValue()) {
					colorVariables.setEnabled(true);
				} else {
					colorVariables.setEnabled(false);
				}
				setVariables();
			}

		});
		constraintsLayout.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String id = constraintsLayout.getValue(constraintsLayout.getSelectedIndex());
				VariableSerializable v = xDatasetVariables.get(id);
				if (v != null) {
					VariableConstraintWidget c = new VariableConstraintWidget(
							true);
					c.addRemoveHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							removeHandlerHelper(event);
						}
					});
					c.setVariable(v);
					c.addApplyHandler(applyHandler);
					c.addChangeHandler(constraintChange);
					constraintsLayout.addWidget(c);
					setConstraints();
				}
			}

		});
//		coloredBy.add(new Label("Colored By "));
//		coloredBy.add(colorCheckBox);
//		controlPanel.setWidget(2, 1, coloredBy);
//		controlPanel.setWidget(2, 2, colorVariables);
		topPanel.add(controlPanel);
		help.setPopupWidth("550px");
		help.setPopupHeight("550px");
		help.setHelpURL("../css/correlation_help.html");
		colorVariables.setEnabled(false);
		colorVariables.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				setVariables();

			}

		});
		update.addClickHandler(updateClick);
		String catid = Util.getParameterString("catid");
		String xml = Util.getParameterString("xml");
		if (xml != null && !xml.equals("")) {
		    xml = Util.decode(xml);
		    lasRequest = new LASRequest(xml);
			dsid = lasRequest.getDataset(0);
			varid = lasRequest.getVariable(0);
			xlo = lasRequest.getRangeLo("x", 0);
			xhi = lasRequest.getRangeHi("x", 0);
			ylo = lasRequest.getRangeLo("y", 0);
			yhi = lasRequest.getRangeHi("y", 0);
			tlo = lasRequest.getRangeLo("t", 0);
			thi = lasRequest.getRangeHi("t", 0);
			zlo = lasRequest.getRangeLo("z", 0);
			zhi = lasRequest.getRangeHi("z", 0);
			Util.getRPCService().getCategoryWithGrids(catid, dsid, datasetCallback);
		} else {

		}
		yVariables.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VariableSerializable varY = yVariables.getVariable(yVariables.getSelectedIndex());
				if (varY.getName().toLowerCase(Locale.ENGLISH).equals("latitude")
						|| varY.getName().toLowerCase(Locale.ENGLISH).equals("longitude")
						|| varY.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
					yVariableConstraint.setVisible(false);
					yVariableConstraint.setApply(false);
					yVariableConstraint.setActive(false);
				} else {
					yVariableConstraint.setVisible(true);
					yVariableConstraint.setVariable(varY);
					yVariableConstraint.setActive(true);

					setVariables();
					resetConstraints("y");
					List<VariableConstraintWidget> remove = new ArrayList<VariableConstraintWidget>();
					for (Iterator vcwIt = constraintsLayout.getWidgets().iterator(); vcwIt.hasNext();) {
						VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt.next();
						if (vcw.getVariable().getID().equals(varY.getID())) {
							String min = vcw.getMin();
							String max = vcw.getMax();
							if (min != null && !min.equals("")) {
								yVariableConstraint.setMin(min);
								yVariableConstraint.setApply(true);
							}
							if (max != null && !max.equals("")) {
								yVariableConstraint.setMax(max);
								yVariableConstraint.setApply(true);
							}
							remove.add(vcw);
						}
					}
					if (remove.size() > 0)
						resetConstraints("y");
					for (Iterator vcwIt = remove.iterator(); vcwIt.hasNext();) {
						VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt.next();
						constraintsLayout.removeWidget(vcw);
					}
				}

				// Now the the constraint widgets are set, set the constraints
				// in the request object.
				setConstraints();

			}
		});
		xVariables.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
			    setXConstraint();
				// Now the the constraint widgets are set, set the constraints
				// in the request object.
				setConstraints();
			}
		});
		outputPanel.setWidget(annotationsRow, 0, lasAnnotationsPanel);
		output.add(annotationsButton);
		output.add(outputPanel);
		FlexTable variablePanel = new FlexTable();
		variablePanel.getFlexCellFormatter().setColSpan(0, 0, 2);
		variablePanel.getFlexCellFormatter().setColSpan(1, 0, 2);
		outputPanel.setWidget(variablePanelRow, 0, variablePanel);
		variablePanel.setWidget(0, 0, help);
		variablePanel.setWidget(1, 0, yVariables);
		variablePanel.setWidget(2, 0, new Label(" as a function of "));
		variablePanel.setWidget(2, 1, xVariables);
		variablePanel.setWidget(2, 3, message);
        coloredBy.add(new Label("Colored By "));
        coloredBy.add(colorCheckBox);
        variablePanel.setWidget(3, 0, coloredBy);
        variablePanel.setWidget(3, 1, colorVariables);
        spaceTimeHelp.setHelpURL("../css/maphelp.html");
        spaceTimeHelp.setPopupLeft(326);
        spaceTimeHelp.setPopupTop(spaceTimeHelp.getAbsoluteTop()+15);
		spaceTimeTopRow.add(spaceTimeHelp);
		spaceTimeTopRow
				.add(new HTML(
						"<b>&nbsp;&nbsp;Latitude, Longitude and Time Constraints:</b>&nbsp;"));
		spaceTimeTopRow.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		spaceTimeConstraints.add(spaceTimeTopRow);
		spaceTimeConstraints.add(mapConstraint);
		spaceTimeConstraints.add(timePanel);
		spaceTimeConstraints.add(zAxisWidget);
		RootPanel.get("icons").add(cruiseIcons);
		RootPanel.get("button_panel").add(buttonPanel);
		RootPanel.get("data_selection").add(topPanel);
		RootPanel.get("data_constraints").add(constraintsLayout);
		RootPanel.get("correlation").add(output);
		RootPanel.get("space_time_constraints").add(spaceTimeConstraints);
		lasAnnotationsPanel.setPopupLeft(outputPanel.getAbsoluteLeft());
		lasAnnotationsPanel.setPopupTop(outputPanel.getAbsoluteTop());

		lasAnnotationsPanel
				.setError("Click \"Update plot\" to refresh the plot.&nbsp;");
		ok = new PushButton("Ok");
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				warning.hide();
				youveBeenWarned = true;
			}

		});
		ok.setWidth("80px");
		cancel = new PushButton("Cancel");
		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popHistory(undoState.toString());
				warning.hide();
			}

		});
		cancel.setWidth("80px");
		warnText = new Label(
				"This operation will require new data to be extracted from the database.");
		Label go = new Label("  It may take a while.  Do you want to continue?");
		FlexTable layout = new FlexTable();
		layout.getFlexCellFormatter().setColSpan(0, 0, 2);
		layout.getFlexCellFormatter().setColSpan(1, 0, 2);
		layout.setWidget(0, 0, warnText);
		layout.setWidget(1, 0, go);
		layout.setWidget(2, 0, ok);
		layout.setWidget(2, 1, cancel);
		warning.add(layout);
		warning.setPopupPosition(update.getAbsoluteLeft(),
				update.getAbsoluteTop());
		History.addValueChangeHandler(historyHandler);

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				resize(event.getWidth(), event.getHeight());
			}
		});
	}
    public void setXConstraint() {
        VariableSerializable varX = xVariables.getVariable(xVariables.getSelectedIndex());
        if (varX.getName().toLowerCase(Locale.ENGLISH).equals("latitude")
                || varX.getName().toLowerCase(Locale.ENGLISH).equals("longitude")
                || varX.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
            xVariableConstraint.setVisible(false);
            xVariableConstraint.setApply(false);
            xVariableConstraint.setActive(false);
        } else {
            xVariableConstraint.setVariable(varX);
            xVariableConstraint.setVisible(true);
            xVariableConstraint.setActive(true);
            setVariables();
            resetConstraints("x");
            List<VariableConstraintWidget> remove = new ArrayList<VariableConstraintWidget>();
            for (Iterator vcwIt = constraintsLayout.getWidgets().iterator(); vcwIt.hasNext();) {
                VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt.next();
                if (vcw.getVariable().getID().equals(varX.getID())) {
                    String min = vcw.getMin();
                    String max = vcw.getMax();
                    if (min != null && !min.equals("")) {
                        xVariableConstraint.setMin(min);
                        xVariableConstraint.setApply(true);
                    }
                    if (max != null && !max.equals("")) {
                        xVariableConstraint.setMax(max);
                        xVariableConstraint.setApply(true);
                    }
                    remove.add(vcw);
                }
            }
            if (remove.size() > 0)
                resetConstraints("x");
            for (Iterator vcwIt = remove.iterator(); vcwIt.hasNext();) {
                VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt .next();
                constraintsLayout.removeWidget(vcw);
            }
        }
    }
	/**
	 * The LASAnnotationsPanel closed, so make sure the
	 * {@link annotationsButton} down state is set appropriately.
	 */
	protected void synchAnnotationsMode() {
		showAnnotations(lasAnnotationsPanel.isVisible());
	}

	protected MapSelectionChangeListener mapListener = new MapSelectionChangeListener() {

		@Override
		public void onFeatureChanged() {
			update.addStyleDependentName("APPLY-NEEDED");
		}
	};
	public ChangeHandler needApply = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			update.addStyleDependentName("APPLY-NEEDED");
		}

	};
	ClickHandler updateClick = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			updatePlot(true);
		}

	};

	private void printerFriendly() {
		StringBuilder urlfrag = new StringBuilder(URLUtil.getBaseURL()
				+ "getAnnotations.do?template=image_w_annotations.vm&");
		urlfrag.append(printURL);
		Window.open(urlfrag.toString(), "_blank", Constants.WINDOW_FEATURES);
	}

	private void updatePlot(boolean addHistory) {
	    // TODO Before submitting...


	    setConstraints();
	    update.removeStyleDependentName("APPLY-NEEDED");

	    lasAnnotationsPanel.setError("Fetching plot annotations...");

	    spin.setPopupPosition(outputPanel.getAbsoluteLeft(),
	            outputPanel.getAbsoluteTop());
	    spin.show();

	    String tlo = null;
	    String thi = null;
	    if (timeConstraint.isVisible()) {
	        tlo = timeConstraint.getFerretDateLo();
	        thi = timeConstraint.getFerretDateHi();
	    }
	    String xlo = String.valueOf(mapConstraint.getXlo());
	    String xhi = String.valueOf(mapConstraint.getXhi());
	    String ylo = String.valueOf(mapConstraint.getYlo());
	    String yhi = String.valueOf(mapConstraint.getYhi());

	    String zlo = null;
	    String zhi = null;
	    if (zAxisWidget.isVisible()) {
	        zlo = zAxisWidget.getLo();
	        zhi = zAxisWidget.getHi();
	    }
	    String vix = xVariables.getVariable(xVariables.getSelectedIndex()).getID();
	    GridSerializable grid = null;

	    if (xlo != null && xhi != null) {
	        lasRequest.setRange("x", xlo, xhi, 0);
	    } else if (xhi != null) {
	        xlo = xhi;
	        lasRequest.setRange("x", xhi, xhi, 0);
	    }

	    if (ylo != null && yhi != null) {
	        lasRequest.setRange("y", ylo, yhi, 0);
	    } else if (yhi != null) {
	        ylo = yhi;
	        lasRequest.setRange("y", yhi, yhi, 0);
	    }

	    // Check grids for all three variables to determine XYZT controls.


	    GridSerializable xVarGrid = xVariables.getVariable(xVariables.getSelectedIndex()).getGrid();
	    GridSerializable yVarGrid = yVariables.getVariable(yVariables.getSelectedIndex()).getGrid();
	    GridSerializable colVarGrid = null;
	    if ( colorCheckBox.getValue() ) {
	        colVarGrid = colorVariables.getVariable(colorVariables.getSelectedIndex()).getGrid();
	    }

	    if ( xVarGrid.hasT() ) {
	        grid = xVarGrid;
	    } else if ( yVarGrid.hasT() ) {
	        grid = yVarGrid;
	    } else if ( colVarGrid != null && colVarGrid.hasT() ) {
	        grid = colVarGrid;
	    }

	    if ( grid != null ) {

	        if (tlo != null && thi != null) {
	            lasRequest.setRange("t", tlo, thi, 0);
	        } else {
	            // Set them to the min and max available in the data set.
	            tlo = timeConstraint.getFerretDateMin();
	            thi = timeConstraint.getFerretDateMax();
	            lasRequest.setRange("t", tlo, thi, 0);

	        }
	    }
	    grid = null;
	    if ( xVarGrid.hasZ() ) {
	        grid = xVarGrid;
	    } else if ( yVarGrid.hasZ() ) {
	        grid = yVarGrid;
	    } else if ( colVarGrid != null && colVarGrid.hasZ() ) {
	        grid = colVarGrid;
	    }

	    if ( grid != null) {

	        if (zlo != null && zhi != null) {
	            lasRequest.setRange("z", zlo, zhi, 0);
	        } else if (zhi != null) {
	            lasRequest.setRange("z", zhi, zhi, 0);
	        }

	    }

	    lasRequest.setProperty("product_server", "ui_timeout", "20");
	    lasRequest.setProperty("las", "output_type", "xml");

	    operationID = "prop_prop_plot";

	    lasRequest.setOperation(operationID, operationType);
	    lasRequest.setProperty("ferret", "annotations", "file");

	    imageCanvas = Canvas.createIfSupported();
	    drawingCanvas = Canvas.createIfSupported();
	    if ( imageCanvas != null ) {

	        canvasDiv.add(imageCanvas, 0, 0);
	        canvasDiv.add(drawingCanvas, 0, 0);
	        
	        outputPanel.setWidget(outputPanelRow, 0, canvasDiv);
	        imageCanvasContext = imageCanvas.getContext2d();
	        drawingCanvasContext = drawingCanvas.getContext2d();
	    } else {
	        Window.alert("You are accessing this site with an older, no longer supported browser. "+
	                "Some or all features of this site will not work correctly using your browser. "+
	                "Recommended browsers include these or higher versions of these: "+
	                "IE 9.0   FF 17.0    Chorme 23.0    Safari 5.1");
	    }

	    String url = Util.getProductServer() + "?xml="
	            + URL.encode(lasRequest.toString());

	    currentURL = url;

	    if (addHistory) {
	        pushHistory(lasRequest.toString());
	    }

	    RequestBuilder sendRequest = new RequestBuilder(RequestBuilder.GET, UriUtils.sanitizeUri(url));
	    try {
	        sendRequest.sendRequest(null, lasRequestCallback);
	    } catch (RequestException e) {
	        HTML error = new HTML(e.toString());
	        outputPanel.setWidget(outputPanelRow, 0, error);
	    }

	}

	RequestCallback lasRequestCallback = new RequestCallback() {

		@Override
		public void onError(Request request, Throwable exception) {

			spin.hide();
			Window.alert("Product request failed.");

		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			// If the submitted operation used the compound operation to
			// pull a new data set, we need a new initial state
			String plot_id = lasRequest.getOperation();
			
			// Need to warn again...
			youveBeenWarned = false;
			spin.hide();
			print.setEnabled(true);
			String doc = response.getText();
			String imageurl = "";
			String annourl = "";
			// Look at the doc. If it's not obviously XML, treat it as HTML.
			if (!doc.substring(0, 100).contains("<?xml")) {

				spin.hide();
				VerticalPanel p = new VerticalPanel();
				ScrollPanel sp = new ScrollPanel();
				HTML result = new HTML(HtmlSanitizerUtil.sanitizeHtml(doc));
				p.add(result);
				PushButton again = new PushButton("Try Again");
				again.setWidth("75px");
				again.addStyleDependentName("SMALLER");
				again.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent click) {
						updatePlot(false);
					}
				});
				p.add(again);
				sp.add(p);
				sp.setSize((int) image_w + "px", (int) image_h + "px");

				outputPanel.setWidget(outputPanelRow, 0, sp);
			} else {
				doc = doc.replaceAll("\n", "").trim();
				Document responseXML = XMLParser.parse(doc);
				NodeList results = responseXML.getElementsByTagName("result");

				for (int n = 0; n < results.getLength(); n++) {
					if (results.item(n) instanceof Element) {
						Element result = (Element) results.item(n);
						if (result.getAttribute("type").equals("image")) {
							imageurl = result.getAttribute("url");
						} else if (result.getAttribute("type").equals("batch")) {
							String elapsed_time = result
									.getAttribute("elapsed_time");
							cancelButton.setTime(Integer.valueOf(elapsed_time));
							cancelButton.setSize(image_w * imageScaleRatio
									+ "px", image_h * imageScaleRatio + "px");
							outputPanel.setWidget(outputPanelRow, 0, cancelButton);
							lasRequest.setProperty("product_server",
									"ui_timeout", "3");
							String url = Util.getProductServer() + "?xml="
									+ URL.encode(lasRequest.toString());
							if (currentURL.contains("cancel"))
								url = url + "&cancel=true";
							RequestBuilder sendRequest = new RequestBuilder(
									RequestBuilder.GET, url);
							try {
								sendRequest.setHeader("Pragma", "no-cache");
								sendRequest.setHeader("cache-directive",
										"no-cache");
								// These are needed or IE will cache and make
								// infinite requests that always return 304
								sendRequest.setHeader("If-Modified-Since",
										new Date().toString());
								sendRequest.setHeader("max-age", "0");
								sendRequest.sendRequest(null,
										lasRequestCallback);
							} catch (RequestException e) {
								HTML error = new HTML(e.toString());
								error.setSize(image_w * imageScaleRatio + "px",
										image_h * imageScaleRatio + "px");
								outputPanel.setWidget(outputPanelRow, 0, error);
							}
						} else if (result.getAttribute("type").equals("error")) {
							if (result.getAttribute("ID").equals("las_message")) {
								Node text = result.getFirstChild();
								if (text instanceof Text) {
									Text t = (Text) text;
									HTML error = new HTML(UriUtils.fromString(t.getData().toString().trim()).asString());
									outputPanel.setWidget(outputPanelRow, 0, error);
								}
							}
						} else if (result.getAttribute("type").equals(
								"annotations")) {
							annourl = result.getAttribute("url");
							lasAnnotationsPanel.setAnnotationsHTMLURL(Util
									.getAnnotationService(annourl));
						} else if (result.getAttribute("type").equals(
								"icon_webrowset")) {
							String iconurl = result.getAttribute("url");
							cruiseIcons.init(iconurl);
						} else if (result.getAttribute("type").equals("netCDF")) {
							netcdf = result.getAttribute("file");

						} else if (result.getAttribute("type").equals(
								"map_scale")) {
							NodeList map_scale = result
									.getElementsByTagName("map_scale");
							for (int m = 0; m < map_scale.getLength(); m++) {
								if (map_scale.item(m) instanceof Element) {
									Element map = (Element) map_scale.item(m);
									NodeList children = map.getChildNodes();
									for (int l = 0; l < children.getLength(); l++) {
                                        if (children.item(l) instanceof Element) {
                                            Element child = (Element) children.item(l);
                                            if (child.getNodeName().equals("x_image_size")) {
                                                x_image_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_image_size")) {
                                                y_image_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_plot_size")) {
                                                x_plot_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_plot_size")) {
                                                y_plot_size = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_offset_from_left")) {
                                                x_offset_from_left = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_offset_from_bottom")) {
                                                y_offset_from_bottom = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_offset_from_right")) {
                                                x_offset_from_right = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_offset_from_top")) {
                                                y_offset_from_top = getNumber(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_axis_lower_left")) {
                                                x_axis_lower_left = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_axis_lower_left")) {
                                                y_axis_lower_left = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("x_axis_upper_right")) {
                                                x_axis_upper_right = getDouble(child.getFirstChild());
                                            } else if (child.getNodeName().equals("y_axis_upper_right")) {
                                                y_axis_upper_right = getDouble(child.getFirstChild());
                                            }
                                        }
									}
								}
							}
						}
					}
				}
				if (!imageurl.equals("")) {
					plotImage = new IESafeImage(HtmlSanitizerUtil.sanitizeHtml(imageurl).asString());
					x_per_pixel = (x_axis_upper_right - x_axis_lower_left)
							/ Double.valueOf(x_plot_size);
					y_per_pixel = (y_axis_upper_right - y_axis_lower_left)
							/ Double.valueOf(y_plot_size);

					if (imageCanvas != null) {
						outputPanel.setWidget(shadowCanvasRow, 0, plotImage);
						plotImage.setVisible(false);
						plotImage.addLoadHandler(new LoadHandler() {

							@Override
							public void onLoad(LoadEvent event) {
								int width = plotImage.getWidth();
								int height = plotImage.getHeight();
								// Set global maximums
								image_w = width;
								image_h = height;
								String w = width - 18 + "px";
								lasAnnotationsPanel.setPopupWidth(w);
								lasAnnotationsPanel.setPopupLeft(outputPanel
										.getAbsoluteLeft());
								lasAnnotationsPanel.setPopupTop(outputPanel
										.getAbsoluteTop());
								drawToScreenScaled(imageScaleRatio);
                                drawingCanvas.addMouseDownHandler(new MouseDownHandler() {

                                    @Override
                                    public void onMouseDown(MouseDownEvent event) {

                                        startx = event.getX();
                                        starty = event.getY();
                                        outx = false;
                                        outy = false;
                                        
                                        if (startx > x_offset_from_left && starty > y_offset_from_top && startx < x_offset_from_left + x_plot_size
                                                && starty < y_offset_from_top + y_plot_size) {

                                            draw = true;

                                            drawToScreenScaled(imageScaleRatio);
                                            double scaled_x_per_pixel = x_per_pixel / imageScaleRatio;
                                            double scaled_y_per_pixel = y_per_pixel / imageScaleRatio;
                                            world_startx = x_axis_lower_left + (startx - x_offset_from_left * imageScaleRatio) * scaled_x_per_pixel;
                                            world_starty = y_axis_lower_left + ((y_image_size * imageScaleRatio - starty) - y_offset_from_bottom * imageScaleRatio)
                                                    * scaled_y_per_pixel;

                                            world_endx = world_startx;
                                            world_endy = world_starty;


                                            setTextValues();
                                            xVariableConstraint.setApply(true);
                                            yVariableConstraint.setApply(true);

                                        }
                                    }
                                });
                                drawingCanvas.addMouseMoveHandler(new MouseMoveHandler() {

                                    @Override
                                    public void onMouseMove(MouseMoveEvent event) {
                                        int currentx = event.getX();
                                        int currenty = event.getY();
                                        // If you drag it out, we'll
                                        // stop
                                        // drawing.
                                        if (currentx < x_offset_from_left * imageScaleRatio || currenty < y_offset_from_top * imageScaleRatio || currentx > x_offset_from_left * imageScaleRatio + x_plot_size * imageScaleRatio || currenty > y_offset_from_top * imageScaleRatio + y_plot_size * imageScaleRatio) {

                                            endx = currentx;
                                            endy = currenty;

                                           
                                            // Set the limits for one last drawing of the selection
                                            // rectangle.
                                            if (currentx < x_offset_from_left * imageScaleRatio) {
                                                endx = (int) (x_offset_from_left * imageScaleRatio);
                                                currentx = (int) (x_offset_from_left * imageScaleRatio);
                                                outx = true;
                                            }
                                            if (currenty < y_offset_from_top * imageScaleRatio) {
                                                endy = (int) (y_offset_from_top * imageScaleRatio);
                                                currenty = (int) (y_offset_from_top * imageScaleRatio);
                                                outy = true;
                                            }
                                            if (currentx > x_offset_from_left * imageScaleRatio + x_plot_size * imageScaleRatio) {
                                                endx = (int) (x_offset_from_left * imageScaleRatio + x_plot_size * imageScaleRatio);
                                                currentx = (int) (x_offset_from_left * imageScaleRatio + x_plot_size * imageScaleRatio);
                                                outx = true;
                                            }
                                            if (currenty > y_offset_from_top * imageScaleRatio+ y_plot_size * imageScaleRatio) {
                                                endy = (int) (y_offset_from_top * imageScaleRatio + y_plot_size * imageScaleRatio);
                                                currenty = (int) (y_offset_from_top * imageScaleRatio + y_plot_size * imageScaleRatio);
                                                outy = true;
                                            }
                                            
                                        }
                                        if (draw) {
                                            double scaled_x_per_pixel = x_per_pixel / imageScaleRatio;
                                            double scaled_y_per_pixel = y_per_pixel / imageScaleRatio;
                                            world_endx = x_axis_lower_left + (currentx - x_offset_from_left * imageScaleRatio) * scaled_x_per_pixel;
                                            world_endy = y_axis_lower_left + ((y_image_size * imageScaleRatio - currenty) - y_offset_from_bottom * imageScaleRatio)
                                                    * scaled_y_per_pixel;
                                            setTextValues();
                                            drawingCanvasContext.setFillStyle(randomColor);
                                            drawingCanvasContext.clearRect(0, 0, drawingCanvas.getCoordinateSpaceWidth(), drawingCanvas.getCoordinateSpaceHeight());
                                            drawingCanvasContext.fillRect(startx, starty, currentx - startx, currenty - starty);
                                            drawingCanvasContext.strokeRect(startx, starty, currentx - startx, currenty - starty);
                                        }
                                        if (outx && outy) {
                                            draw = false;
                                        }
                                    }
                                });
                                resize(Window.getClientWidth(), Window.getClientHeight());
							}

						});
						
						drawingCanvas.addMouseUpHandler(new MouseUpHandler() {

                            @Override
                            public void onMouseUp(MouseUpEvent event) {
                                // If we're still drawing when the mouse goes
                                // up, record the position.
                                if (draw) {
                                    endx = event.getX();
                                    endy = event.getY();
                                }
                                draw = false;
                                outx = false;
                                outy = false;
                                setConstraints();
                            }
                        });
                    } else {
                        // Browser cannot handle a canvas tag, so just put up
                        // the image.
                        outputPanel.setWidget(outputPanelRow, 0, plotImage);
                        plotImage.addLoadHandler(new LoadHandler() {

                            @Override
                            public void onLoad(LoadEvent event) {
                                resize(Window.getClientWidth(), Window.getClientHeight());
                                String w = plotImage.getWidth() - 18 + "px";
                                lasAnnotationsPanel.setPopupLeft(outputPanel.getAbsoluteLeft());
                                lasAnnotationsPanel.setPopupTop(outputPanel.getAbsoluteTop());
                                lasAnnotationsPanel.setPopupWidth(w);
                            }
                        });

                    }
				}
				world_startx = x_axis_lower_left;
				world_endx = x_axis_upper_right;
				world_starty = y_axis_lower_left;
				world_endy = y_axis_upper_right;
				setTextValues();
				printURL = Util.getAnnotationsFrag(annourl, imageurl);
			}
		}
	};

	private void setTextValues() {

		constraintsLayout.setVisible(true);

		if (world_startx <= world_endx) {

			xVariableConstraint.setConstraint(dFormat.format(world_startx),
					dFormat.format(world_endx));

		} else {

			xVariableConstraint.setConstraint(dFormat.format(world_endx),
					dFormat.format(world_startx));

		}

		if (world_starty <= world_endy) {

			yVariableConstraint.setConstraint(dFormat.format(world_starty),
					dFormat.format(world_endy));

		} else {

			yVariableConstraint.setConstraint(dFormat.format(world_endy),
					dFormat.format(world_starty));

		}
	}

	AsyncCallback<CategorySerializable> datasetCallback = new AsyncCallback<CategorySerializable>() {

	    @Override
	    public void onFailure(Throwable caught) {

	        Window.alert("Could not get the variables list from the server.");

	    }

	    @Override
	    public void onSuccess(CategorySerializable cat) {
	        VariableSerializable[] variables = null;
	        if (cat != null && cat.isVariableChildren()) {
	            DatasetSerializable ds = cat.getDatasetSerializable();
	            variables = ds.getVariablesSerializable();
	        } else {
	            Window.alert("Could not get the variables list from the server.");

	        }

	        if ( variables == null ) {
	            Window.alert("Could not get the variables list from the server.");
	        } else {

	            int index = -1;
	            int time_index = -1;
	            constraintsLayout.setHeader("Add a variable constraint...");
	           
	            
	            for (int i = 0; i < variables.length; i++) {
	                if (!variables[i].getAttributes().get("grid_type").equals("vector") ) {
	                    xDatasetVariables.put(variables[i].getID(), variables[i]);
	                    xVariables.addItem(variables[i]);
	                    yVariables.addItem(variables[i]);
	                    colorVariables.addItem(variables[i]);
	                    if (!variables[i].getName().toLowerCase(Locale.ENGLISH).equals("latitude")
	                            && !variables[i].getName().toLowerCase(Locale.ENGLISH).equals("longitude")
	                            && !variables[i].getName().toLowerCase(Locale.ENGLISH).equals("time") ) {
	                        constraintsLayout.addItem(variables[i]);
	                    }
	                    if (variables[i].getID().equals(varid)) {
	                        index = i;
	                    }
	                    if (variables[i].getName().toLowerCase(Locale.ENGLISH).contains("time")) {
	                        time_index = i;
	                    }
	                }
	            }
	            if (index > 0) {
	                yVariables.setSelectedIndex(index);
	            }
	            if (time_index > 0) {
	                xVariables.setSelectedIndex(time_index);
	            } else {
	                VariableSerializable yvar = yVariables.getVariable(yVariables.getSelectedIndex());
	                boolean done = false;
	                // Set x to the first variable that is not y
	                for (int i = 0; i < xVariables.getItemCount(); i++) {
                        VariableSerializable v = xVariables.getVariable(i);
                        if ( !done && !v.getID().equals(varid) ) {
                            done = true;
                            xVariables.setSelectedIndex(i);
                        }
                    }
	               
	            }

	            operationID = "prop_prop_plot";
	           

	            VariableSerializable varY = yVariables.getVariable(yVariables.getSelectedIndex());
	            constraintsLayout.addWidgetForY(yVariableConstraint);
	            if (varY.getName().toLowerCase(Locale.ENGLISH).equals("latitude")) {
	                yVariableConstraint.setVariable(varY);
	                yVariableConstraint.setActive(false);
	                yVariableConstraint.setVisible(false);
	            } else if (varY.getName().toLowerCase(Locale.ENGLISH).equals("longitude")) {
	                yVariableConstraint.setVariable(varY);
	                yVariableConstraint.setActive(false);
	                yVariableConstraint.setVisible(false);
	            } else if (varY.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
	                yVariableConstraint.setVariable(varY);
	                yVariableConstraint.setActive(false);
	                yVariableConstraint.setVisible(false);
	            } else {
	                yVariableConstraint.setVariable(varY);
	                yVariableConstraint.setVisible(true);
	            }

	            VariableSerializable varX = xVariables.getVariable(xVariables.getSelectedIndex());
	            constraintsLayout.addWidgetForX(xVariableConstraint);
	            if (varX.getName().toLowerCase(Locale.ENGLISH).equals("latitude")) {
	                xVariableConstraint.setVariable(varX);
	                xVariableConstraint.setActive(false);
	                xVariableConstraint.setVisible(false);
	            } else if (varX.getName().toLowerCase(Locale.ENGLISH).equals("longitude")) {
	                xVariableConstraint.setVariable(varX);
	                xVariableConstraint.setActive(false);
	                xVariableConstraint.setVisible(false);
	            } else if (varX.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
	                xVariableConstraint.setVariable(varX);
	                xVariableConstraint.setActive(false);
	                xVariableConstraint.setVisible(false);
	            } else {
	                xVariableConstraint.setVariable(varX);
	                xVariableConstraint.setVisible(true);
	            }

	            GridSerializable xVarGrid = varX.getGrid();
	            GridSerializable yVarGrid = varY.getGrid();
	            
	            mapConstraint.setTool("xy");
	            mapConstraint.setDataExtent(
	                    Double.valueOf(xVarGrid.getYAxis().getLo()),
	                    Double.valueOf(xVarGrid.getYAxis().getHi()),
	                    Double.valueOf(xVarGrid.getXAxis().getLo()),
	                    Double.valueOf(xVarGrid.getXAxis().getHi()), 1.0);
	            if (xlo != null && xhi != null && ylo != null && yhi != null) {
	                mapConstraint.setCurrentSelection(Double.valueOf(ylo), Double.valueOf(yhi), Double.valueOf(xlo), Double.valueOf(xhi));
	            }
	            GridSerializable grid = null;
	            if ( xVarGrid.hasT() ) {
	                grid = xVarGrid;
	            } else if ( yVarGrid.hasT() ) {
	                grid = yVarGrid;
	            }
	            if ( grid != null ) {
	                timeConstraint.init(grid.getTAxis(), true);
	                if (tlo != null) {
	                    timeConstraint.setLo(tlo);
	                }
	                if (thi != null) {
	                    timeConstraint.setHi(thi);
	                }
	                timeConstraint.setVisible(true);
	            } else {
	                timeConstraint.setVisible(false);
	            }
	            grid = null;
	            if ( xVarGrid.hasZ() ) {
	                grid = xVarGrid;
	            } else if ( yVarGrid.hasZ() ) {
	                grid = yVarGrid;
	            }
	            if (grid != null) {
	                zAxisWidget.init(grid.getZAxis());
	                zAxisWidget.setVisible(true);
	                zAxisWidget.setRange(true);
	            } else {
	                zAxisWidget.setVisible(false);
	            }

	            if (zlo != null && zhi != null) {
	                zAxisWidget.setLo(zlo);
	                zAxisWidget.setHi(zhi);
	            }

	            setVariables();
	            List<Map<String, String>> vcs = lasRequest.getVariableConstraints();
	            for (Iterator vcIt = vcs.iterator(); vcIt.hasNext();) {
	                Map<String, String> con = (Map<String, String>) vcIt.next();
	                String varid = con.get("varID");
	                String op = con.get("op");
	                String value = con.get("value");
	                String id = con.get("id");
	                String plotv = plotVariable(varid);
	                VariableSerializable v = xDatasetVariables.get(varid);
	                if (v != null && v.getName().toLowerCase(Locale.ENGLISH).equals("latitude")) {
	                    if (op.equals("gt") || op.equals("ge")) {
	                        mapConstraint.setCurrentSelection(
	                                Double.valueOf(value), Double.valueOf(yhi),
	                                Double.valueOf(xlo), Double.valueOf(xhi));
	                    } else if (op.equals("eq")) {
	                        mapConstraint.setCurrentSelection(
	                                Double.valueOf(value), Double.valueOf(value),
	                                Double.valueOf(xlo), Double.valueOf(xhi));
	                    } else {
	                        mapConstraint.setCurrentSelection(Double.valueOf(ylo),
	                                Double.valueOf(value), Double.valueOf(xlo),
	                                Double.valueOf(xhi));
	                    }
	                } else if (v != null
	                        && v.getName().toLowerCase(Locale.ENGLISH).equals("longitude")) {
	                    if (op.equals("gt") || op.equals("ge")) {
	                        mapConstraint.setCurrentSelection(Double.valueOf(ylo),
	                                Double.valueOf(yhi), Double.valueOf(value),
	                                Double.valueOf(xhi));
	                    } else if (op.equals("eq")) {
	                        mapConstraint.setCurrentSelection(Double.valueOf(ylo),
	                                Double.valueOf(yhi), Double.valueOf(value),
	                                Double.valueOf(value));
	                    } else {
	                        mapConstraint.setCurrentSelection(Double.valueOf(ylo),
	                                Double.valueOf(yhi), Double.valueOf(xlo),
	                                Double.valueOf(value));
	                    }
	                } else if (v != null
	                        && v.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
	                    if (op.equals("gt") || op.equals("ge")) {
	                        timeConstraint.setLo(value);
	                    } else if (op.equals("eq")) {
	                        timeConstraint.setLo(value);
	                        timeConstraint.setHi(value);
	                    } else {
	                        timeConstraint.setHi(value);
	                    }
	                } else if (v != null && plotv.equals("x")) {
	                    xVariableConstraint.setApply(true);
	                    if (op.equals("gt") || op.equals("ge")) {
	                        xVariableConstraint.setMin(value);
	                    } else if (op.equals("eq")) {
	                        xVariableConstraint.setMin(value);
	                        xVariableConstraint.setMax(value);
	                    } else {
	                        xVariableConstraint.setMax(value);
	                    }
	                    constraintsLayout.setVisible(true);

	                } else if (plotv.equals("y")) {
	                    yVariableConstraint.setApply(true);
	                    if (op.equals("gt") || op.equals("ge")) {
	                        yVariableConstraint.setMin(value);
	                    } else if (op.equals("eq")) {
	                        yVariableConstraint.setMin(value);
	                        yVariableConstraint.setMax(value);
	                    } else {
	                        yVariableConstraint.setMax(value);
	                    }
	                    constraintsLayout.setVisible(true);

	                } else {
	                    VariableConstraintWidget c = new VariableConstraintWidget(true);
	                    c.addRemoveHandler(new ClickHandler() {

	                        @Override
	                        public void onClick(ClickEvent event) {
	                            removeHandlerHelper(event);
	                        }

	                    });
	                    c.addApplyHandler(applyHandler);
	                    c.addChangeHandler(constraintChange);

	                    if (v != null) {
	                        c.setVariable(v);
	                        c.setApply(true);
	                        if (op.equals("gt") || op.equals("ge")) {
	                            c.setMin(value);
	                        } else if (op.equals("eq")) {
	                            c.setMin(value);
	                            c.setMax(value);
	                        } else {
	                            c.setMax(value);
	                        }
	                    }
	                    constraintsLayout.addWidget(c);
	                }
	                setConstraints();
	            }

	            updatePlot(true);
	            
	        }
	    }

        
	};
	private void disableAndSetXandColor(VariableSerializable yvar) {
        message.setVisible(false);
	    String intervals = yvar.getGrid().getIntervals();
	    for (int i = 0; i < xVariables.getItemCount(); i++ ) {
            VariableSerializable vs = xVariables.getVariable(i);
	        String xintervals = vs.getGrid().getIntervals();
	        if ( intervals.equals(xintervals) ) {
	            xVariables.getElement().getElementsByTagName("option").getItem(i).removeAttribute("disabled");
	        } else {
	            xVariables.getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled","disabled");        
	            message.setVisible(true);
	            List<VariableConstraintWidget> current_widgets = constraintsLayout.getWidgets();
                for (Iterator cwIt = current_widgets.iterator(); cwIt.hasNext();) {
                    VariableConstraintWidget cw = (VariableConstraintWidget) cwIt.next();
                    if (cw.getVariable().getID().equals(vs.getID())) {
                        constraintsLayout.removeWidget(cw);
                    }
                }
	        }
	    }
	    for (int i = 0; i < colorVariables.getItemCount(); i++ ) {

	        String colorIntervals = colorVariables.getVariable(i).getGrid().getIntervals();
	        if ( intervals.equals(colorIntervals) ) {
	            colorVariables.getElement().getElementsByTagName("option").getItem(i).removeAttribute("disabled");
	        } else {
	            colorVariables.getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled","disabled");
	        }
	    }

	    // Make sure the selected variable is still enabled and if it's not change it to the first available.
	    boolean done = false;
	    String disabled = xVariables.getElement().getElementsByTagName("option").getItem(xVariables.getSelectedIndex()).getAttribute("disabled");
	    if ( disabled != null && !disabled.equals("") ) {
	        for (int i = 0; i < xVariables.getItemCount(); i++ ) {
	            String dis = xVariables.getElement().getElementsByTagName("option").getItem(i).getAttribute("disabled");
	            if ( dis != null && !dis.equals("disabled") && !done ) {
	                if ( xVariables.getVariable(i).getID() !=  yvar.getID() ) {
	                    xVariables.setSelectedIndex(i);
	                    setXConstraint();
	                    done = true;
	                }
	            }
	        }
	    }
	    // Change the selection even if it's not turned on...
	    done = false;
	    String cdis = colorVariables.getElement().getElementsByTagName("option").getItem(colorVariables.getSelectedIndex()).getAttribute("disabled");
	    if ( cdis != null && !cdis.equals("") ) {
	        for (int i = 0; i < colorVariables.getItemCount(); i++ ) {
	            String dis = colorVariables.getElement().getElementsByTagName("option").getItem(i).getAttribute("disabled");
	            if ( dis != null && !dis.equals("disabled") && !done ) {
	                // Any enabled variable will do just fine, even the y-variable.
	                colorVariables.setSelectedIndex(i);
	                done = true;
	            }
	        }
	    }
	    

	}
	private int getNumber(Node firstChild) {
		if (firstChild instanceof Text) {
			Text content = (Text) firstChild;
			String value = content.getData().toString().trim();
			return Double.valueOf(value).intValue();
		} else {
			return -999;
		}
	}

	private double getDouble(Node firstChild) {
		if (firstChild instanceof Text) {
			Text content = (Text) firstChild;
			String value = content.getData().toString().trim();
			return Double.valueOf(value).doubleValue();
		} else {
			return -999.;
		}
	}

	ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {

			String xml = event.getValue();
			if (!xml.equals("")) {
				popHistory(xml);
			}
			// Back to the beginning. Chill.
		}

	};

	private void setVariables() {
	    update.addStyleDependentName("APPLY-NEEDED");
	    String vix = xVariables.getVariable(xVariables.getSelectedIndex())
	            .getID();
	    String viy = yVariables.getVariable(yVariables.getSelectedIndex())
	            .getID();
	    lasRequest.removeVariables();
	    lasRequest.addVariable(dsid, vix, 0);
	    lasRequest.addVariable(dsid, viy, 0);
	    lasRequest.setProperty("data", "count", "2");
	    if (colorCheckBox.getValue()) {
	        lasRequest.setProperty("data", "count", "3");
	        String varColor = colorVariables.getVariable(
	                colorVariables.getSelectedIndex()).getID();
	        lasRequest.addVariable(dsid, varColor, 0);
	    }
	    
	    GridSerializable grid = null;
	    GridSerializable colVarGrid = null;
	    GridSerializable xVarGrid = xVariables.getVariable(xVariables.getSelectedIndex()).getGrid();
	    GridSerializable yVarGrid = yVariables.getVariable(yVariables.getSelectedIndex()).getGrid();
	    if ( colorCheckBox.getValue() ) {
	        colVarGrid = colorVariables.getVariable(colorVariables.getSelectedIndex()).getGrid();
	    }

	    if ( xVarGrid.hasT() ) {
	        grid = xVarGrid;
	    } else if ( yVarGrid.hasT() ) {
	        grid = yVarGrid;
	    } else if ( colVarGrid != null && colVarGrid.hasT() ) {
	        grid = colVarGrid;
	    }

	    if ( grid != null ) {
	        timeConstraint.setVisible(true);
        } else {
            timeConstraint.setVisible(false);
        }
	    grid = null;
	    if ( xVarGrid.hasZ() ) {
	        grid = xVarGrid;
	    } else if ( yVarGrid.hasZ() ) {
	        grid = yVarGrid;
	    } else if ( colVarGrid != null && colVarGrid.hasZ() ) {
	        grid = colVarGrid;
	    }

	    if ( grid != null) {
	        // If it's the first time the type will be null and it needs to be set up.
	        if ( zAxisWidget.getType() == null ) {
                zAxisWidget.init(grid.getZAxis());
	        }
	        zAxisWidget.setVisible(true);
            zAxisWidget.setRange(true);
        } else {
            zAxisWidget.setVisible(false);
        }
	}

	private void resetConstraints(String vars) {
		xVariableConstraint.setApply(false);
		yVariableConstraint.setApply(false);
		if (vars.contains("x")) {
			xVariableConstraint.setConstraint("", "");
		}
		if (vars.contains("y")) {
			yVariableConstraint.setConstraint("", "");
		}
	}

	private void setConstraints() {
	    disableAndSetXandColor(yVariables.getVariable(yVariables.getSelectedIndex()));
	    
	    // Remove any variables already showing as constraints from the list.
	    constraintsLayout.restore();
	    // Dim those that don't match...
	    VariableSerializable yvs = yVariables.getVariable(yVariables.getSelectedIndex());
	    String intervals = yvs.getGrid().getIntervals();
	    VariableListBox constraintVariables = constraintsLayout.getConstraintVariables();
	    for (int i = 0; i < constraintVariables.getItemCount(); i++ ) {
	        String id = constraintVariables.getValue(i);
	        if ( !id.equals("-")) { // The "id" of the header which says "Add a variable constraint."               
	            VariableSerializable vs = constraintVariables.getVariable(i-1);
	            if ( vs != null ) {
	                String conIntervals = vs.getGrid().getIntervals();
	                if ( intervals.equals(conIntervals) ) {
	                    constraintVariables.getElement().getElementsByTagName("option").getItem(i).removeAttribute("disabled");
	                } else {
	                    constraintVariables.getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled","disabled");
	                    List<VariableConstraintWidget> current_widgets = constraintsLayout.getWidgets();
	                    for (Iterator cwIt = current_widgets.iterator(); cwIt.hasNext();) {
	                        VariableConstraintWidget cw = (VariableConstraintWidget) cwIt.next();
	                        if (cw.getVariable().getID().equals(vs.getID())) {
	                            constraintsLayout.removeWidget(cw);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    List<VariableConstraintWidget> oc = constraintsLayout.getWidgets();
	    
	    update.addStyleDependentName("APPLY-NEEDED");
        undoState = new LASRequest(lasRequest.toString());
        lasRequest.removeConstraints();
        if ( yVariableConstraint.getApply().getValue() ) {
            String varY = yVariables.getVariable(yVariables.getSelectedIndex()).getID();
            String miny = yVariableConstraint.getMin();
            String maxy = yVariableConstraint.getMax();
            if ( miny != null && !miny.equals("") ) {
                lasRequest.addVariableConstraint(dsid, varY, "gt", miny, "min_"+varY);
            }
            if ( maxy != null && !maxy.equals("") ) {
                lasRequest.addVariableConstraint(dsid, varY, "le", maxy, "max_"+varY);
            }
        }
        if ( xVariableConstraint.getApply().getValue() ) {
            String varX = xVariables.getVariable(xVariables.getSelectedIndex()).getID();
            String minx = xVariableConstraint.getMin();
            String maxx = xVariableConstraint.getMax();
            if ( minx != null && !minx.equals("") ) {
                lasRequest.addVariableConstraint(dsid, varX, "gt", minx, "min_"+varX);
            }
            if ( maxx != null && !maxx.equals("") ) {
                lasRequest.addVariableConstraint(dsid, varX, "le", maxx, "max_"+varX);
            }
        }
        
        for (Iterator cwIt = oc.iterator(); cwIt.hasNext();) {
            VariableConstraintWidget cw = (VariableConstraintWidget) cwIt.next();
            if (cw.getApply().getValue()) {
                String min = cw.getMin();
                String max = cw.getMax();
                String id = cw.getVariable().getID();
                if (min != null && !min.equals("")) {
                    lasRequest.addVariableConstraint(dsid, id, "gt", min,"min_" + id);
                }
                if (max != null && !max.equals("")) {
                    lasRequest.addVariableConstraint(dsid, id, "le", max,"max_" + id);
                }
            }
        }
        constraintsLayout.removeItem(xVariableConstraint.getVariable());
        constraintsLayout.removeItem(yVariableConstraint.getVariable());
        for (Iterator cwIt = oc.iterator(); cwIt.hasNext();) {
            VariableConstraintWidget cw = (VariableConstraintWidget) cwIt.next();
            constraintsLayout.removeItem(cw.getVariable());
        }
	}

	private void clearConstraint(String axis) {
		if (axis.equals("y")) {
			yVariableConstraint.setConstraint("", "");
			yVariableConstraint.setApply(false);
		} else if (axis.equals("x")) {
			xVariableConstraint.setConstraint("", "");
			xVariableConstraint.setApply(false);
		}
	}

	private void popHistory(String xml) {

		// If the pop was requested from the undoState in particular and it is
		// not set, return.
		if (xml == null || xml.equals(""))
			return;

		lasRequest = new LASRequest(xml);

		String vx = lasRequest.getVariable(0);
		String vy = lasRequest.getVariable(1);
		String vc = lasRequest.getVariable(2);
		if (vx != null && !vx.equals("")) {
			xVariables.setSelectedVariable(vx);
			VariableSerializable varX = xVariables.getVariable(xVariables
					.getSelectedIndex());
			xVariableConstraint.setVariable(varX);
			if (varX.getName().toLowerCase(Locale.ENGLISH).equals("latitude")
					|| varX.getName().toLowerCase(Locale.ENGLISH).equals("longitude")
					|| varX.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
				xVariableConstraint.setVisible(false);
				xVariableConstraint.setApply(false);
				xVariableConstraint.setActive(false);
			} else {
				xVariableConstraint.setVisible(true);
				xVariableConstraint.setActive(true);
			}
		}
		if (vy != null && !vy.equals("")) {
			yVariables.setSelectedVariable(vy);
			VariableSerializable varY = yVariables.getVariable(yVariables
					.getSelectedIndex());
			yVariableConstraint.setVariable(varY);
			if (varY.getName().toLowerCase(Locale.ENGLISH).equals("latitude")
					|| varY.getName().toLowerCase(Locale.ENGLISH).equals("longitude")
					|| varY.getName().toLowerCase(Locale.ENGLISH).equals("time")) {
				yVariableConstraint.setVisible(false);
				yVariableConstraint.setApply(false);
				yVariableConstraint.setActive(false);
			} else {
				yVariableConstraint.setVisible(true);
				yVariableConstraint.setActive(true);
			}
		}
		String count = lasRequest.getProperty("data", "count");
		int c = 2;
		try {
		    c = Integer.valueOf(count);
		} catch (Exception e) {
		    // Pretend it's 2.
		}
		if (vc != null && !vc.equals("") && c >= 3 ) {
			colorVariables.setSelectedVariable(vc);
			colorCheckBox.setValue(true);
			colorVariables.setEnabled(true);
		} else {
			colorCheckBox.setValue(false);
			colorVariables.setEnabled(false);
		}

		String xlo = lasRequest.getRangeLo("x", 0);
		String xhi = lasRequest.getRangeHi("x", 0);
		String ylo = lasRequest.getRangeLo("y", 0);
		String yhi = lasRequest.getRangeHi("y", 0);
		if (xlo != null && !xlo.equals("") && xhi != null && !xhi.equals("")
				&& ylo != null && !ylo.equals("") && yhi != null
				&& !yhi.equals("")) {
			mapConstraint.setCurrentSelection(Double.valueOf(ylo),
					Double.valueOf(yhi), Double.valueOf(xlo),
					Double.valueOf(xhi));
		}

		String tlo = lasRequest.getRangeLo("t", 0);
		String thi = lasRequest.getRangeHi("t", 0);
		if (tlo != null && !tlo.equals("") && thi != null && !thi.equals("")) {
			timeConstraint.setLo(tlo);
			timeConstraint.setHi(thi);
		}

		String zlo = lasRequest.getRangeLo("z", 0);
		String zhi = lasRequest.getRangeHi("z", 0);
		if (zlo != null && !zlo.equals("") && zhi != null && !zhi.equals("")) {
			zAxisWidget.setLo(zlo);
			zAxisWidget.setHi(zhi);
		}

		List<Map<String, String>> vcons = lasRequest.getVariableConstraints();
		constraintsLayout.reset();

		if (vcons.size() > 0) {
			for (Iterator vconsIt = vcons.iterator(); vconsIt.hasNext();) {
				Map<String, String> con = (Map<String, String>) vconsIt.next();
				String varid = con.get("varID");
				String op = con.get("op");
				String value = con.get("value");
				String id = con.get("id");
				if (id.equals("minx")) {
					if (varid.equals(vx)) {
						xVariableConstraint.setMin(value);
						xVariableConstraint.setApply(true);
					} else {
						clearConstraint("x");
					}
				} else if (id.equals("maxx")) {
					if (varid.equals(vx)) {
						xVariableConstraint.setMax(value);
						xVariableConstraint.setApply(true);
					} else {
						clearConstraint("x");
					}
				} else if (id.equals("miny")) {
					if (varid.equals(vy)) {
						yVariableConstraint.setMin(value);
						yVariableConstraint.setApply(true);
					} else {
						clearConstraint("y");
					}
				} else if (id.equals("maxy")) {
					if (varid.equals(vy)) {
						yVariableConstraint.setMax(value);
						yVariableConstraint.setApply(true);
					} else {
						clearConstraint("y");
					}
				} else {
					// This is an additional constraint.
					// First see if it's there already.
					VariableConstraintWidget vcw = null;
					for (Iterator varsIt = constraintsLayout.getWidgets()
							.iterator(); varsIt.hasNext();) {
						VariableConstraintWidget v = (VariableConstraintWidget) varsIt
								.next();
						if (v.getVariable().getID().equals(varid)) {
							vcw = v;
						}
					}
					// If not, build it.
					if (vcw == null) {
						vcw = new VariableConstraintWidget(true);
						vcw.addRemoveHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								removeHandlerHelper(event);
							}

						});

						vcw.setVariable(xDatasetVariables.get(varid));
						constraintsLayout.addWidget(vcw);
					}
					if (id.contains("max")) {
						vcw.setMax(value);
						vcw.setApply(true);
					} else {
						vcw.setMin(value);
						vcw.setApply(true);
					}
				}
			}
		} else {
			xVariableConstraint.setApply(false);
			yVariableConstraint.setApply(false);
		}

		setVariables();

		updatePlot(false);

	}

	private void removeHandlerHelper(ClickEvent event) {
		PushButton source = (PushButton) event.getSource();
		String id = source.getElement().getId();
		List<VariableConstraintWidget> remove = new ArrayList<VariableConstraintWidget>();
		for (Iterator vcwIt = constraintsLayout.getWidgets().iterator(); vcwIt
				.hasNext();) {
			VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt
					.next();
			String vid = "other-" + vcw.getVariable().getID();
			if (vid.equals(id)) {
				remove.add(vcw);
			}
		}

		for (Iterator vcwIt = remove.iterator(); vcwIt.hasNext();) {
			VariableConstraintWidget vcw = (VariableConstraintWidget) vcwIt
					.next();
			constraintsLayout.removeWidget(vcw);
		}

		// Now the the constraint widgets are set, set the constraints in the
		// request object.
		setConstraints();
	}

	private void pushHistory(String xml) {
		History.newItem(xml, false);
	}

	private String plotVariable(String id) {
		VariableSerializable varX = xVariables.getVariable(xVariables
				.getSelectedIndex());
		VariableSerializable varY = yVariables.getVariable(yVariables
				.getSelectedIndex());
		if (varX.getID().equals(id))
			return "x";
		if (varY.getID().equals(id))
			return "y";
		return "";
	}

	private void warn(String message) {
		String grid_type = xVariables.getVariable(0).getAttributes()
				.get("grid_type");
		// Don't warn if the grid type is regular...
		if (!grid_type.contains("reg")) {
			warnText.setText(message);
			warning.show();
		}
	}

	/**
	 * uses {@link plotImage}
	 * 
	 * @param newPlotImageWidth
	 */
	void setPlotWidth(int newPlotImageWidth) {
		autoZoom = true;
		int pwidth = Math.max(newPlotImageWidth, image_w_min);
		if (autoZoom) {
		    imageScaleRatio = 1.;

		    // If the panel is less than the image, shrink the image.
		    double h = ((Double.valueOf(image_h) / Double.valueOf(image_w)) * Double
		            .valueOf(pwidth));
		    imageScaleRatio = h / Double.valueOf(image_h);
		    if ( imageScaleRatio > 1.0 ) {
		        imageScaleRatio = 1.0;
		    }
		    if (plotImage != null) {
		        // set the plotImage to the grid before scaling so plotImage's
		        // imageElement will be valid
		        outputPanel.setWidget(shadowCanvasRow, 0, plotImage);
		        plotImage.setVisible(false);
		        drawToScreenScaled(imageScaleRatio);
		    }
		} else {
			setImageSize(fixedZoom);
		}
		if (spin.isVisible()) {
			spinSetPopupPositionCenter(plotImage);
		}
		outputPanel.setWidth(getPlotWidth());
		// Piggy back setting the annotations width onto this method.
		lasAnnotationsPanel.setPopupWidth(getPlotWidth());
	}

	/**
	 * Position spin at the center of the plotWidget
	 * 
	 * @param plotWidget
	 */
	void spinSetPopupPositionCenter(Widget plotWidget) {
		int absoluteLeft = plotWidget.getAbsoluteLeft();
		int offsetWidth = plotWidget.getOffsetWidth();
		int absoluteTop = plotWidget.getAbsoluteTop();
		int offsetHeight = plotWidget.getOffsetHeight();
		int left = absoluteLeft + (offsetWidth / 2);
		int top = absoluteTop + (offsetHeight / 2);
		spin.setPopupPosition(left, top);
	}

	private void drawToScreenScaled(double scaleRatio) {
		scaledImage = scaleImage(scaleRatio);
		if ( scaledImage != null ) {
		    drawToScreen(scaledImage);
		}
	}

	private ImageData scaleImage(double scaleToRatio) {
		Canvas canvasTmp = Canvas.createIfSupported();
		Context2d context = canvasTmp.getContext2d();

		int imageHeight = plotImage.getHeight();
		double ch = (imageHeight * scaleToRatio);
		int imageWidth = plotImage.getWidth();
		double cw = (imageWidth * scaleToRatio);
		
		if ( imageHeight <= 0 || imageWidth <=0 ) {
		    return null;
		}

		canvasTmp.setCoordinateSpaceHeight((int) ch);
		canvasTmp.setCoordinateSpaceWidth((int) cw);

		// TODO: make a temp imageElement?
		ImageElement imageElement = ImageElement.as(plotImage.getElement());

		// s = source
		// d = destination
		double sx = 0;
		double sy = 0;
		int imageElementWidth = imageElement.getWidth();
		if (imageElementWidth <= 0) {
			imageElementWidth = imageWidth;
		}
		double sw = imageElementWidth;
		int imageElementHeight = imageElement.getHeight();
		if (imageElementHeight <= 0) {
			imageElementHeight = imageHeight;
		}
		double sh = imageElementHeight;

		double dx = 0;
		double dy = 0;
		double dw = imageElementWidth;
		double dh = imageElementHeight;

		// tell it to scale image
		context.scale(scaleToRatio, scaleToRatio);

		// draw image to canvas
		context.drawImage(imageElement, sx, sy, sw, sh, dx, dy, dw, dh);

		// get image data
		double w = dw * scaleToRatio;
		double h = dh * scaleToRatio;
		ImageData imageData = null;
		try {
			imageData = context.getImageData(0, 0, w, h);
		} catch (Exception e) {
			// Well bummer
		}
        int ht = (int) h + 10;
        int wt = (int) w + 10;
        
        // Clear the div, clear the drawing canvas then reinsert.  Otherwise, ghosts of the previous image appear.
        canvasDiv.clear();
                
        imageCanvasContext.clearRect(0, 0, imageCanvas.getCoordinateSpaceWidth(), imageCanvas.getCoordinateSpaceHeight());
        
        canvasDiv.add(imageCanvas, 0, 0);
        canvasDiv.add(drawingCanvas, 0, 0);
        
		imageCanvas.setCoordinateSpaceHeight(ht);
		imageCanvas.setCoordinateSpaceWidth(wt);
		drawingCanvas.setCoordinateSpaceHeight(ht);
		drawingCanvas.setCoordinateSpaceWidth(wt);
		canvasDiv.setSize(wt + "px", ht + "px");
	
		return imageData;
	}

	private void drawToScreen(ImageData imageData) {
		if (imageCanvasContext != null)
			imageCanvasContext.putImageData(imageData, 0, 0);
	}

	public void setImageSize(int percent) {
		fixedZoom = percent;
		double factor = percent / 100.;
		int w = (int) (Double.valueOf(image_w).doubleValue() * factor);
		int h = (int) (Double.valueOf(image_h).doubleValue() * factor);
		outputPanel.setWidth(w + "px");
		outputPanel.setHeight(h + "px");
		autoZoom = false;
	}

	String getPlotWidth() {
		int antipadding = 0;// 100;
		String w = CONSTANTS.DEFAULT_ANNOTATION_PANEL_WIDTH();
		if (plotImage != null) {
			if (imageScaleRatio == 0.0)
				imageScaleRatio = 1.0;
			int wt = (int) ((plotImage.getWidth() - antipadding) * imageScaleRatio);
			w = wt + "px";
		}
		return w;
	}

	/**
	 * @param plotImage
	 * @param newPlotImageHeight
	 */
	void setPlotHeight(IESafeImage plotImage, int newPlotImageHeight) {
		autoZoom = true;
		pwidth = (int) ((image_w / image_h) * Double
				.valueOf(newPlotImageHeight));
		setPlotWidth(pwidth);
	}

	/**
	 * @param windowWidth
	 * @param windowHeight
	 */
	void resize(int windowWidth, int windowHeight) {
		try {
			IESafeImage plotImage = (IESafeImage) outputPanel.getWidget(shadowCanvasRow, 0);
			if (plotImage != null) {
				// Check width first
				int leftPaddingWidth = RootPanel.get("leftPadding")
						.getOffsetWidth();
				int leftControlsWidth = RootPanel.get("leftControls")
						.getOffsetWidth();
				int newPlotImageWidth = windowWidth - leftPaddingWidth
						- leftControlsWidth;
				int plotImageWidth = plotImage.getWidth();
				if (newPlotImageWidth != plotImageWidth) {
					setPlotWidth(newPlotImageWidth);
					// Check that height is still small enough
					int newPlotImageHeight = windowHeight - topAndBottomPadding
							- lasAnnotationsPanel.getOffsetHeight();
					int plotImageHeight = plotImage.getHeight();
					if (newPlotImageHeight < plotImageHeight) {
						setPlotHeight(plotImage, newPlotImageHeight);
					}
				} else {
					// It's the correct width, so now check the height
					int newPlotImageHeight = windowHeight - topAndBottomPadding
							- lasAnnotationsPanel.getOffsetHeight();
					int plotImageHeight = plotImage.getHeight();
					if (newPlotImageHeight != plotImageHeight) {
						setPlotHeight(plotImage, newPlotImageHeight);
						// Check that width is still small enough
						leftPaddingWidth = RootPanel.get("leftPadding")
								.getOffsetWidth();
						leftControlsWidth = RootPanel.get("leftControls")
								.getOffsetWidth();
						newPlotImageWidth = windowWidth - leftPaddingWidth
								- leftControlsWidth;
						plotImageWidth = plotImage.getWidth();
						if (newPlotImageWidth < plotImageWidth) {
							setPlotWidth(newPlotImageWidth);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param down
	 */
	void showAnnotations(boolean down) {
		annotationsButton.setDown(down);
		lasAnnotationsPanel.setVisible(down);
		if (down) {
			annotationsButton
					.setTitle("Click to hide the annotations of the plot.");
		} else {
			annotationsButton
					.setTitle("Click to show the annotations of the plot.");
		}
	}

	ChangeHandler constraintChange = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			TextBox w = (TextBox) event.getSource();
			Widget wp = w.getParent();
			Widget gp = wp.getParent();
			if (gp instanceof VariableConstraintWidget) {
				VariableConstraintWidget vcw = (VariableConstraintWidget) gp;
				vcw.setApply(true);
			}
			setConstraints();
		}

	};

	ClickHandler applyHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent arg0) {
			update.addStyleDependentName("APPLY-NEEDED");
			setConstraints();
		}

	};
	CancelEvent.Handler cancelRequestHandler = new CancelEvent.Handler() {

		@Override
		public void onCancel(CancelEvent event) {
			if (event.getID().equals("Correlation")) {
				currentURL = currentURL + "&cancel=true";
				RequestBuilder sendRequest = new RequestBuilder(
						RequestBuilder.GET, currentURL);
				try {

					lasAnnotationsPanel
							.setError("Fetching plot annotations...");
					sendRequest.sendRequest(null, lasRequestCallback);
				} catch (RequestException e) {
					Window.alert("Unable to cancel request.");
				}
			}
		}
	};
 
}
