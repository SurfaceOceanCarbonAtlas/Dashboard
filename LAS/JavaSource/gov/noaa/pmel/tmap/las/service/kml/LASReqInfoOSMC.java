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

public class LASReqInfoOSMC{

    private final static Logger log = LoggerFactory.getLogger(LASReqInfoOSMC.class.getName());

    public static HashMap<String,String> getLASReqInfo(String las_req_info_file){
        System.out.println("read las req info");
        LASDocument lasReqInfo = new LASDocument();
        String dsID="";
        String varID="";
        String colorBy = "";

        HashMap<String, String> initLASReq = new HashMap<String, String>();
        File lasReqInfoFile = new File(las_req_info_file);

        try{
            JDOMUtils.XML2JDOM(lasReqInfoFile,lasReqInfo);

            Element dsIDElement = lasReqInfo.getElementByXPath("/las_req_info/dataset_id");
            dsID = dsIDElement.getText();
            Element varIDElement = lasReqInfo.getElementByXPath("/las_req_info/variable_id");
            varID = varIDElement.getText();
            initLASReq.put("dsID", dsID);
            initLASReq.put("varID", varID);

            //get colorBy
            Element colorElement = lasReqInfo.getElementByXPath("/las_req_info/colorBy");
            colorBy = colorElement.getText();
            initLASReq.put("colorBy", colorBy);

                //get z range
                Element zElement = lasReqInfo.getElementByXPath("/las_req_info/z_region");
                Element zloElement = zElement.getChild("z_lo");
                String zlo  = zloElement.getText();
                Element zhiElement = zElement.getChild("z_hi");
                String zhi  = zhiElement.getText();
                if(zlo != "" && zlo != null){initLASReq.put("zlo", zlo);}
                if(zhi != "" && zhi != null){initLASReq.put("zhi", zhi);}

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

        } catch (Exception e){
            log.error("error while reading las request info: " + e.toString());
        }
        System.out.println("after read las req info");
        return initLASReq;
    }
}