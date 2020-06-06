package gov.noaa.pmel.tmap.las.service.tabledap;

import gov.noaa.pmel.tmap.exception.LASException;

import java.io.File;
import java.io.IOException;

import ucar.ma2.InvalidRangeException;

public class TestMerge {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            TabledapTool tool = new TabledapTool();
            File t1 = new File("nc1.nc");
            File t2 = new File("nc2.nc");
            String out = "out.nc";
            tool.merge(out, t1, t2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LASException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidRangeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
