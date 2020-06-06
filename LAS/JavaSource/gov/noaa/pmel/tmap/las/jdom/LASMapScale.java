package gov.noaa.pmel.tmap.las.jdom;

import gov.noaa.pmel.tmap.jdom.LASDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.jdom.Element;

public class LASMapScale extends LASDocument {

    private static final long serialVersionUID = 703675281298155235L;

    /**
     * 
     */

    public LASMapScale() {
        super();
    }

    public LASMapScale(File map_scale) throws Exception {
        BufferedReader scaleReader;

        FileReader f = new FileReader(map_scale);
        scaleReader = new BufferedReader(f);

        HashMap<String, String> scale = new HashMap<String, String>();
        if (scaleReader != null) {

        	try {
        		String line = scaleReader.readLine();
        		while (line != null) {
        			/*
        			 * Look for number followed by a ":" and a "/" surrounded by
        			 * blanks to distinquish from the data URL value. This is the
        			 * old more complicated output style from Ferret.. 1 / 1:
        			 * "DATA_0_URL" "http://www.cdc.noa..." 2 / 2: "DATA_0_VAR"
        			 * "otemp" 3 / 3: "DATA_1_URL" " " 4 / 4: "DATA_1_VAR" " " 5 /
        			 * 5: "PPL$XMIN" "62.50" 6 / 6: "PPL$XMAX" "348.5" 7 / 7:
        			 * "PPL$YMIN" "-64.50" 8 / 8: "PPL$YMAX" "74.50"
        			 */

        			//
        			if (line.contains("[1-9]:") && line.contains(" / ")) {
        				// Split on the : and use the second half...
        				String[] halves = line.split(":");
        				if (halves.length > 1) {
        					// See below...
        					String[] parts = halves[1].split("\"");
        					scale.put(parts[1], parts[3]);
        				}
        			}
        			/*
        			 * Simpler style without the mulitple line numbers, ":" and "/"
        			 * "PPL$XMIN" "122.2" "PPL$XMAX" "288.8" "PPL$YMIN" "-35.00"
        			 * "PPL$YMAX" "45.00" "PPL$XPIXEL" "776" "PPL$YPIXEL" "483"
        			 * "PPL$WIDTH" "12.19" "PPL$HEIGHT" "7.602" "PPL$XORG" "1.200"
        			 * "VP_TOP_MARGIN" "1.4" "VP_RT_MARGIN" "1" "AX_HORIZ" "X"
        			 * "AX_HORIZ_POSTV" " " "AX_VERT" "Z" "AX_VERT_POSTV" "down"
        			 * "DATA_EXISTS" "1" "DATA_MIN" "3.608" "DATA_MAX" "8.209"
        			 */
        			else {
        				// Just split on the quotes
        				// See JavaDoc on split for explanation
        				// of why we get 4 parts with the repeated quotes.
        				String[] parts = line.split("\"");
        				scale.put(parts[1], parts[3]);
        			}
        			line = scaleReader.readLine();
        		}
        	} catch (Exception e) {
        		throw new Exception(e);
        	} finally {
        		scaleReader.close();
        	}

        }

        String s1 = scale.get("PPL$XPIXEL");
        String s2 = scale.get("PPL$WIDTH");
        float xppi = 0.0f;
        if (s1 != null && s2 != null) {
            xppi = Float.valueOf(s1) / Float.valueOf(s2);
        }

        float yppi = 0.0f;
        s1 = scale.get("PPL$YPIXEL");
        s2 = scale.get("PPL$HEIGHT");
        if (s1 != null && s2 != null) {
            yppi = Float.valueOf(s1) / Float.valueOf(s2);
        }

        float x_image_size = 0.0f;
        s1 = scale.get("PPL$XPIXEL");
        if (s1 != null) {
            x_image_size = Float.valueOf(s1);
        }

        float y_image_size = 0.0f;
        s1 = scale.get("PPL$YPIXEL");
        if (s1 != null) {
            y_image_size = Float.valueOf(s1);
        }

        float xOffset_left = 0.0f;
        s1 = scale.get("PPL$XORG");
        if (s1 != null) {
            xOffset_left = xppi * Float.valueOf(s1);
        }

        float yOffset_bottom = 0.0f;
        s1 = scale.get("PPL$YORG");
        if (s1 != null) {
            yOffset_bottom = yppi * Float.valueOf(s1);
        }

        float xOffset_right = 0.0f;
        s1 = scale.get("VP_RT_MARGIN");
        if (s1 != null) {
            xOffset_right = xppi * Float.valueOf(s1);
        }

        float yOffset_top = 0.0f;
        s1 = scale.get("VP_TOP_MARGIN");
        if (s1 != null) {
            yOffset_top = yppi * Float.valueOf(s1);
        }

        float plotWidth = 0.0f;
        s1 = scale.get("PPL$XLEN");
        if (s1 != null) {
            plotWidth = xppi * Float.valueOf(s1);
        }

        float plotHeight = 0.0f;
        s1 = scale.get("PPL$YLEN");
        if (s1 != null) {
            plotHeight = yppi * Float.valueOf(s1);
        }

        float axisLLX = 0.0f;
        s1 = scale.get("XAXIS_MIN");
        if (s1 != null) {
            axisLLX = Float.valueOf(s1);
        }
        float axisLLY = 0.0f;
        s1 = scale.get("YAXIS_MIN");
        if (s1 != null) {
            axisLLY = Float.valueOf(s1);
        }

        float axisURX = 0.0f;
        s1 = scale.get("XAXIS_MAX");
        if (s1 != null) {
            axisURX = Float.valueOf(s1);
        }

        float axisURY = 0.0f;
        s1 = scale.get("YAXIS_MAX");
        if (s1 != null) {
            axisURY = Float.valueOf(s1);
        }

        float data_min = 0.0f;
        s1 = scale.get("DATA_MIN");
        if (s1 != null && !s1.equals(" ") && !s1.equals("")) {
            data_min = Float.valueOf(s1);
        }

        float data_max = 0.0f;
        s1 = scale.get("DATA_MAX");
        if (s1 != null && !s1.equals(" ") && !s1.equals("")) {
            data_max = Float.valueOf(s1);
        }

        int data_exists = 0;
        s1 = scale.get("DATA_EXISTS");
        if (s1 != null) {
            data_exists = Integer.valueOf(s1).intValue();
        }

        int xStride = 0;
        s1 = scale.get("XSTRIDE");
        if (s1 != null) {
            xStride = Integer.valueOf(s1).intValue();
        }

        int yStride = 0;
        s1 = scale.get("YSTRIDE");
        if (s1 != null) {
            yStride = Integer.valueOf(s1).intValue();
        }

        String time_min = null;
        s1 = scale.get("HAXIS_TSTART");
        if (s1 != null) {
            time_min = s1;
        }

        String time_max = null;
        s1 = scale.get("HAXIS_TEND");
        if (s1 != null) {
            time_max = s1;
        }

        if (time_min == null || time_max == null) {
            s1 = scale.get("VAXIS_TSTART");
            if (s1 != null) {
                time_min = s1;
            }

            s1 = scale.get("VAXIS_TEND");
            if (s1 != null) {
                time_max = s1;
            }
        }

        String time_units = null;
        s1 = scale.get("HAXIS_TUNITS");
        if (s1 != null) {
            time_units = s1;
        }
        if (time_units == null) {
            s1 = scale.get("VAXIS_TUNITS");
            if (s1 != null) {
                time_units = s1;
            }
        }

        String time_origin = null;
        s1 = scale.get("HAXIS_TORIGIN");
        if (s1 != null) {
            time_origin = s1;
        }
        if (time_origin == null) {
            s1 = scale.get("VAXIS_TORIGIN");
            if (s1 != null) {
                time_origin = s1;
            }
        }

        String calendar = null;
        s1 = scale.get("HAXIS_TCALENDAR");
        if (s1 != null) {
            calendar = s1;
        }
        if ( calendar == null ) {
            s1 = scale.get("VAXIS_TCALENDAR");
            if ( s1 != null ) {
                calendar = s1;
            }
        }

        Element map_scaleE = new Element("map_scale");
        this.setRootElement(map_scaleE);
        map_scaleE.addContent(makeElement("x_pixels_per_inch", xppi));
        map_scaleE.addContent(makeElement("y_pixels_per_inch", yppi));
        map_scaleE.addContent(makeElement("x_image_size", x_image_size));
        map_scaleE.addContent(makeElement("y_image_size", y_image_size));
        map_scaleE.addContent(makeElement("x_plot_size", plotWidth));
        map_scaleE.addContent(makeElement("y_plot_size", plotHeight));
        map_scaleE.addContent(makeElement("x_offset_from_left", xOffset_left));
        map_scaleE.addContent(makeElement("y_offset_from_bottom", yOffset_bottom));
        map_scaleE.addContent(makeElement("x_offset_from_right", xOffset_right));
        map_scaleE.addContent(makeElement("y_offset_from_top", yOffset_top));
        map_scaleE.addContent(makeElement("x_axis_lower_left", axisLLX));
        map_scaleE.addContent(makeElement("y_axis_lower_left", axisLLY));
        map_scaleE.addContent(makeElement("x_axis_upper_right", axisURX));
        map_scaleE.addContent(makeElement("y_axis_upper_right", axisURY));
        map_scaleE.addContent(makeElement("axis_horizontal", scale.get("AX_HORIZ")));
        map_scaleE.addContent(makeElement("axis_vertical", scale.get("AX_VERT")));
        map_scaleE.addContent(makeElement("axis_vertical_positive", scale.get("AX_VERT_POSTV")));
        map_scaleE.addContent(makeElement("axis_horizontal_positive", scale.get("AX_HORIZ_POSTV")));
        map_scaleE.addContent(makeElement("data_min", data_min));
        map_scaleE.addContent(makeElement("data_max", data_max));
        map_scaleE.addContent(makeElement("data_exists", data_exists));
        map_scaleE.addContent(makeElement("xstride", scale.get("XSTRIDE")));
        map_scaleE.addContent(makeElement("ystride", scale.get("YSTRIDE")));
        if (time_min != null) {
            map_scaleE.addContent(makeElement("time_min", time_min));
        }
        if (time_max != null) {
            map_scaleE.addContent(makeElement("time_max", time_max));
        }
        if (time_units != null) {
            map_scaleE.addContent(makeElement("time_step_units", time_units));
        }
        if (time_origin != null) {
            map_scaleE.addContent(makeElement("time_origin", time_origin));
        }
        if ( calendar != null ) {
            map_scaleE.addContent(makeElement("calendar", calendar));
        }
        if (f != null)
            f.close();
        if (scaleReader != null)
            scaleReader.close();
    }

    private Element makeElement(String name, double value) {
        Element element = new Element(name);
        element.setText(String.valueOf(value));
        return element;
    }

    private Element makeElement(String name, float value) {
        Element element = new Element(name);
        element.setText(String.valueOf(value));
        return element;
    }

    private Element makeElement(String name, int value) {
        Element element = new Element(name);
        element.setText(String.valueOf(value));
        return element;
    }

    private Element makeElement(String name, String value) {
        Element element = new Element(name);
        element.setText(value);
        return element;
    }

    public String getXPixelsPerInch() {
        return this.getRootElement().getChildTextTrim("x_pixels_per_inch");
    }

    public String getYPixelsPerInch() {
        return this.getRootElement().getChildTextTrim("y_pixels_per_inch");
    }

    public String getXPlotSize() {
        return this.getRootElement().getChildTextTrim("x_plot_size");
    }

    public String getYPlotSize() {
        return this.getRootElement().getChildTextTrim("y_plot_size");
    }

    public String getXImageSize() {
        return this.getRootElement().getChildTextTrim("x_image_size");
    }

    public String getYImageSize() {
        return this.getRootElement().getChildTextTrim("y_image_size");
    }

    public String getXOffsetFromLeft() {
        return this.getRootElement().getChildTextTrim("x_offset_from_left");
    }

    public String getYOffsetFromBottom() {
        return this.getRootElement().getChildTextTrim("y_offset_from_bottom");
    }

    public String getXOffsetFromRight() {
        return this.getRootElement().getChildTextTrim("x_offset_from_right");
    }

    public String getYOffsetFromTop() {
        return this.getRootElement().getChildTextTrim("y_offset_from_top");
    }

    public String getXAxisLowerLeft() {
        return this.getRootElement().getChildTextTrim("x_axis_lower_left");
    }

    public String getYAxisLowerLeft() {
        return this.getRootElement().getChildTextTrim("y_axis_lower_left");
    }

    public String getXAxisUpperRight() {
        return this.getRootElement().getChildTextTrim("x_axis_upper_right");
    }

    public String getYAxisUpperRight() {
        return this.getRootElement().getChildTextTrim("y_axis_upper_right");
    }

    public double getXAxisLowerLeftAsDouble() {
        return Double.valueOf(this.getRootElement().getChildTextTrim("x_axis_lower_left")).doubleValue();
    }

    public double getYAxisLowerLeftAsDouble() {
        return Double.valueOf(this.getRootElement().getChildTextTrim("y_axis_lower_left")).doubleValue();
    }

    public double getXAxisUpperRightAsDouble() {
        return Double.valueOf(this.getRootElement().getChildTextTrim("x_axis_upper_right")).doubleValue();
    }

    public double getYAxisUpperRightAsDouble() {
        return Double.valueOf(this.getRootElement().getChildTextTrim("y_axis_upper_right")).doubleValue();
    }

    public String getAxis_horizontal() {
        return this.getRootElement().getChildText("axis_horizontal");
    }

    public String getAxis_vertical() {
        return this.getRootElement().getChildText("axis_vertical");
    }

    public String getAxis_vertical_positive() {
        return this.getRootElement().getChildText("axis_vertical_positive");
    }

    public String getAxis_horizontal_positive() {
        return this.getRootElement().getChildText("axis_horizontal_positive");
    }

    public String getData_min() {
        return this.getRootElement().getChildText("data_min");
    }

    public String getData_max() {
        return this.getRootElement().getChildText("data_max");
    }

    public String getData_exists() {
        return this.getRootElement().getChildText("data_exists");
    }

    public String getXStride() {
        return this.getRootElement().getChildText("xstride");
    }

    public String getYStride() {
        return this.getRootElement().getChildText("ystride");
    }

}
