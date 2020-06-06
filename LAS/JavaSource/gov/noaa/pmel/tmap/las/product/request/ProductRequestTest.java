package gov.noaa.pmel.tmap.las.product.request;

import gov.noaa.pmel.tmap.las.jdom.JDOMUtils;
import gov.noaa.pmel.tmap.las.jdom.LASConfig;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;

import org.jdom.Element;

public class ProductRequestTest extends TestCase {

    public static void main(String[] args) {
        ProductRequestTest prt = new ProductRequestTest("Test Product Request");
        prt.testProductRequestServerConfigLASRequest();
    }

    public ProductRequestTest(String name) {
        super(name);
    }

    /*
     * Test method for 'gov.noaa.pmel.tmap.las.product.request.ProductRequest.ProductRequest(LASConfig, LASRequest)'
     */
    public void testProductRequestServerConfigLASRequest() {
        
        LASConfig sc = new LASConfig();
        try {
            File file = new File("las.xml");
            JDOMUtils.XML2JDOM(file, sc);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sc.mergeProperites();
        Element output_dir = new Element("output_dir");
        output_dir.setText("output");
        sc.getRootElement().addContent(output_dir);
        
        LASUIRequest lr = new LASUIRequest();
        try {
            JDOMUtils.XML2JDOM("<?xml version=\"1.0\"?><lasRequest package=\"\" href=\"file:las.xml\"><link match=\"/lasdata/operations/operation[@ID='DBExtract']\"/><properties><ferret><size>.5</size><format>gif</format><insitu_image_format>default</insitu_image_format><insitu_palette>default</insitu_palette></ferret></properties><args><constraint type=\"variable\" op=\"ge\"><link match=\"/lasdata/datasets/Indian_Data/variables/Salinity\"/><v>34.5</v></constraint><constraint type=\"text\"><v>CruiseNum</v><v>=</v><v>23</v></constraint><link match=\"/lasdata/datasets/Indian_Data/variables/Salinity\"/><region><range low=\"0.0\" type=\"x\" high=\"129.0\"/><range low=\"-75.0\" type=\"y\" high=\"34.0\"/><range low=\"0\" type=\"z\" high=\"200\"/><range low=\"01-Jan-1977\" type=\"t\" high=\"31-Dec-1989\"/></region></args></lasRequest>", lr);
            //JDOMUtils.XML2JDOM("<?xml version=\"1.0\"?><lasRequest package=\"\" href=\"file:las.xml\"><link match=\"/lasdata/operations/operation[@ID='Plot']\"/><properties><ferret><size>.5</size><format>shade</format><contour_levels></contour_levels><do_contour>default</do_contour><expression></expression><fill_levels></fill_levels><fill_type>default</fill_type><image_format>default</image_format><interpolate_data>false</interpolate_data><land_type>default</land_type><mark_grid>default</mark_grid><palette>default</palette><size>0.5</size><use_graticules>default</use_graticules><use_ref_map>default</use_ref_map></ferret></properties><args><link match=\"/lasdata/datasets/coads_climatology_cdf/variables/airt\"/><region><range low=\"-180.0\" type=\"x\" high=\"180.0\"/><range low=\"-89.0\" type=\"y\" high=\"89.0\"/><point v=\"15-Jan\" type=\"t\"/></region><link match=\"/lasdata/datasets/coads_climatology_cdf/variables/sst\"/><region><range low=\"-180.0\" type=\"x\" high=\"180.0\"/><range low=\"-89.0\" type=\"y\" high=\"89.0\"/><point v=\"15-Jan\" type=\"t\"/></region></args></lasRequest>", lr);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println(lr.toString());
        
        try {
            ProductRequest pr = new ProductRequest(sc,lr, "trace", "JSID");
            for (Iterator prIt = pr.getRequestXML().iterator(); prIt.hasNext(); ) {
                System.out.println(prIt.next());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
