package gov.noaa.pmel.tmap.las.service.kml;

import java.util.HashMap;
import java.util.Hashtable;

import gov.noaa.pmel.tmap.jdom.LASDocument;
import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import org.jdom.Document;
import org.jdom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;

public class LASReqInfoRegular{

    private final static Logger log = LoggerFactory.getLogger(LASReqInfoRegular.class.getName());

    public static HashMap<String,String> getLASReqInfo(String las_req_info_file){
        LASDocument lasReqInfo = new LASDocument();
        String dsID="";
        String varID="";
        String gridLon="";
        String gridLat="";
        String xstride="";
        String ystride="";
        String xLowerLeft="";
        String yLowerLeft="";
        String xUpperRight="";
        String yUpperRight="";

        HashMap<String, String> initLASReq = new HashMap<String, String>();
        File lasReqInfoFile = new File(las_req_info_file);

        try{
            JDOMUtils.XML2JDOM(lasReqInfoFile,lasReqInfo);

            Element dsIDElement = lasReqInfo.getElementByXPath("/las_req_info/dataset_id");
            dsID = dsIDElement.getText();
            Element varIDElement = lasReqInfo.getElementByXPath("/las_req_info/variable_id");
            varID = varIDElement.getText();
            Element viewElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_view");
            String view = viewElement.getText();
            Element dsIntervalsElement = lasReqInfo.getElementByXPath("/las_req_info/data_intervals");
            String dsIntervals = dsIntervalsElement.getText();
            initLASReq.put("dsID", dsID);
            initLASReq.put("varID", varID);
            initLASReq.put("ferret_view", view);
            initLASReq.put("dsIntervals", dsIntervals);

            Element dmsElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_deg_min_sec");
            if(dmsElement != null){initLASReq.put("ferret_deg_min_sec", dmsElement.getText());}

            Element dasElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_dep_axis_scale");
            if(dasElement != null){initLASReq.put("ferret_dep_axis_scale", dasElement.getText());}

            Element expElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_expression");
            if(expElement != null){initLASReq.put("ferret_expression", expElement.getText());}

            Element typElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_fill_type");
            if(typElement != null){initLASReq.put("ferret_fill_type", typElement.getText());}

            Element inpElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_interpolate_data");
            if(inpElement != null){initLASReq.put("ferret_interpolate_data", inpElement.getText());}

            Element colElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_line_color");
            if(colElement != null){initLASReq.put("ferret_line_color", colElement.getText());}

            Element symElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_line_or_sym");
            if(symElement != null){initLASReq.put("ferret_line_or_sym", symElement.getText());}

            Element thkElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_line_thickness");
            if(thkElement != null){initLASReq.put("ferret_line_thickness", thkElement.getText());}

            Element mrgElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_margins");
            if(mrgElement != null){initLASReq.put("ferret_margins", mrgElement.getText());}

            Element sizElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_size");
            if(sizElement != null){initLASReq.put("ferret_size", sizElement.getText());}

            Element grtElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_use_graticules");
            if(grtElement != null){initLASReq.put("ferret_use_graticules", grtElement.getText());}

            Element refElement = lasReqInfo.getElementByXPath("/las_req_info/ferret_use_ref_map");
            if(refElement != null){initLASReq.put("ferret_use_ref_map", refElement.getText());}

         
            //stride values for plot
            //xstride
            Element xstElement = lasReqInfo.getElementByXPath("/las_req_info/x_stride");
            if(xstElement != null){
                xstride = xstElement.getText();
                initLASReq.put("xstride",xstride);
            }
            //ystride
            Element ystElement = lasReqInfo.getElementByXPath("/las_req_info/y_stride");
            if(ystElement != null){
                ystride = ystElement.getText();
                initLASReq.put("ystride",ystride);
            }

            //stride values for GE placemarks
            //xstride_coord
            Element xstcoordElement = lasReqInfo.getElementByXPath("/las_req_info/xstride_coord");
            if(xstcoordElement != null){
                initLASReq.put("xstride_coord",xstcoordElement.getText());
            }
            //ystride_coord
            Element ystcoordElement = lasReqInfo.getElementByXPath("/las_req_info/ystride_coord");
            if(xstcoordElement != null){
                initLASReq.put("ystride_coord",ystcoordElement.getText());
            }

            //xLowerLeft
            Element xllElement = lasReqInfo.getElementByXPath("/las_req_info/x_axis_lower_left");
            if(xllElement != null){initLASReq.put("xLowerLeft",xllElement.getText());}

            //yLowerLeft
            Element yllElement = lasReqInfo.getElementByXPath("/las_req_info/y_axis_lower_left");
            if(yllElement != null){initLASReq.put("yLowerLeft",yllElement.getText());}

            //xUpperRight
            Element xurElement = lasReqInfo.getElementByXPath("/las_req_info/x_axis_upper_right");
            if(xurElement != null){initLASReq.put("xUpperRight",xurElement.getText());}

            //xUpperRight
            Element yurElement = lasReqInfo.getElementByXPath("/las_req_info/y_axis_upper_right");
            if(yurElement != null){initLASReq.put("yUpperRight",yurElement.getText());}

            if(dsIntervals.contains("z")){
                //get Z range
                Element zElement = lasReqInfo.getElementByXPath("/las_req_info/z_region");
                Element zloElement = zElement.getChild("z_lo");
                String zlo  = zloElement.getText();
                Element zhiElement = zElement.getChild("z_hi");
                String zhi  = zhiElement.getText();
                initLASReq.put("zlo", zlo);
                initLASReq.put("zhi", zhi);

                //get user selected Z if there is one (dataset is XYZT and ferret_view is XY)
                Element usrzElement = lasReqInfo.getElementByXPath("/las_req_info/z_user");
                if(usrzElement != null){initLASReq.put("z_user", usrzElement.getText());}
            }

            if(dsIntervals.contains("t")){
                //get t range
                Element tElement = lasReqInfo.getElementByXPath("/las_req_info/t_region");
                Element tloElement = tElement.getChild("t_lo");
                String tlo  = tloElement.getText();
                Element thiElement = tElement.getChild("t_hi");
                String thi  = thiElement.getText();
                initLASReq.put("tlo", tlo);
                initLASReq.put("thi", thi);

                //get user selected t if there is one (dataset is XYZT and ferret_view is XY)
                Element usrtElement = lasReqInfo.getElementByXPath("/las_req_info/t_user");
                if(usrtElement != null){
                    initLASReq.put("t_user", usrtElement.getText());
                }
            }

        } catch (Exception e){
            log.error("error while reading las request info: " + e.toString());
        }

        return initLASReq;
    }
}
