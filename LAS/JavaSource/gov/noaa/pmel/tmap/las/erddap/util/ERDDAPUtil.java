package gov.noaa.pmel.tmap.las.erddap.util;

import gov.noaa.pmel.tmap.las.util.Constraint;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;

import com.cohort.util.String2;

public class ERDDAPUtil {
    public static List<String> getLongitudeQuery(boolean is360, String xlo, String xhi) throws UnsupportedEncodingException {
        List<String> queries = new ArrayList<String>();
        StringBuilder xquery1 = new StringBuilder();
        StringBuilder xquery2 = new StringBuilder();
        if ( xlo != null && xlo.length() > 0 && xhi != null && xhi.length() > 0 ) {
            double dxlo = Double.valueOf(xlo);
            double dxhi = Double.valueOf(xhi);
            // Do the full globe and two query dance...
            if ( is360 ) { 
                if ( dxlo < 0 ) { 
                    dxlo = dxlo + 360.; 
                } 
                if ( dxhi < 0 ) { 
                    dxhi = dxhi + 360.; 
                } 
            } 

            if ( Math.abs(dxhi - dxlo ) < 358. ) {

                if ( !is360) {
                    LatLonPoint p = new LatLonPointImpl(0, dxhi);
                    dxhi = p.getLongitude();
                    p = new LatLonPointImpl(0, dxlo);
                    dxlo = p.getLongitude();
                }

                if ( dxhi < dxlo ) {
                    if ( dxhi < 0 && dxlo >= 0 ) {
                        dxhi = dxhi + 360.0d;
                        xquery1.append("&"+URLEncoder.encode("lon360>=" + dxlo, StandardCharsets.UTF_8.name()));
                        xquery1.append("&"+URLEncoder.encode("lon360<=" + dxhi, StandardCharsets.UTF_8.name()));                          
                        xquery2.append("&"+URLEncoder.encode("longitude>="+dxlo, StandardCharsets.UTF_8.name()));
                        xquery2.append("&"+URLEncoder.encode("longitude<"+180, StandardCharsets.UTF_8.name()));
                    } else if ( dxhi < 0 && dxlo < 0 ) {
                    	double t = dxhi;
                    	dxhi = dxlo;
                    	dxlo = t;
                    	xquery1.append("&"+URLEncoder.encode("longitude>="+dxlo, StandardCharsets.UTF_8.name()));
                        xquery1.append("&"+URLEncoder.encode("longitude<="+dxhi, StandardCharsets.UTF_8.name()));
                    }  // else request overlaps, so leave it off
                } else {
                    xquery1.append("&"+URLEncoder.encode("longitude>="+dxlo, StandardCharsets.UTF_8.name()));
                    xquery1.append("&"+URLEncoder.encode("longitude<="+dxhi, StandardCharsets.UTF_8.name()));
                }

            }
        } else {
            // 
            if ( xlo != null && xlo.length() > 0 ) xquery1.append("&"+URLEncoder.encode("longitude>="+xlo, StandardCharsets.UTF_8.name()));
            if ( xhi != null && xhi.length() > 0 ) xquery1.append("&"+URLEncoder.encode("longitude<="+xhi, StandardCharsets.UTF_8.name()));
        }
        queries.add(xquery1.toString());
        if ( xquery2.length() > 0 ) {
            queries.add(xquery2.toString());
        }
        return queries;
    }
    
    public static List<String> getLonQuery(boolean is360, String xlo, String xhi) {
        List<String> queries = new ArrayList<String>();
        // TODO fix name
        String lonname = "longitude";
        StringBuilder query = new StringBuilder();
        StringBuilder query2 = new StringBuilder();
        if ( !is360 ) {
            if (xlo.length() > 0 && xhi.length() > 0 ) {

                double xhiDbl = String2.parseDouble(xhi);
                double xloDbl = String2.parseDouble(xlo);
                // Check the span before normalizing and if it's big, just forget about the lon constraint all together.
                if ( Math.abs(xhiDbl - xloDbl ) < 358. ) {
                    
                    
                    // This little exercise will normalize the x values to -180, 180.
                    LatLonPoint p = new LatLonPointImpl(0, xhiDbl);
                    xhiDbl = p.getLongitude();
                    p = new LatLonPointImpl(0, xloDbl);
                    xloDbl = p.getLongitude();

                    // Now a wrap around from west to east should be have xhi < xlo;
                    if ( xhiDbl < xloDbl ) {
                        if ( xhiDbl < 0 && xloDbl >=0 ) {


                            // This should be true, otherwise how would to get into this situation unless you wrapped around the entire world and overlapped...

                            // Get the "left" half.  The section between -180 and xhi
                            xhiDbl = xhiDbl + 360.0d;
                            query.append("&lon360>=" + xloDbl);
                            query.append("&lon360<=" + xhiDbl);
                            query2.append("&"+lonname+">="+xloDbl+"&"+lonname+"<180");

                        } // the else block is that you overlapped so leave off the longitude constraint all teogether

                    } else {
                        // This else block is the case where it a query that does not cross the date line.
                        // Still have to use the normalized values.
                        query.append("&"+lonname+">=" + xloDbl);
                        query.append("&"+lonname+"<=" + xhiDbl);
                    }
                }// Span the whole globe so leave off the lon query all together.
            } else {
                //  If they are not both defined, add the one that is...  There will be no difficulties with dateline crossings...
                if (xlo.length() > 0) query.append("&"+lonname+">=" + xlo);
                if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhi);
            }
        } else {

            if (xlo.length() > 0 && xhi.length() > 0 ) {

                double xhiDbl = String2.parseDouble(xhi);
                double xloDbl = String2.parseDouble(xlo);
                
                if ( xloDbl < 0 ) xloDbl = xloDbl + 360;
                if ( xhiDbl < 0 ) xhiDbl = xhiDbl + 360;
                // Check the span before normalizing and if it's big, just forget about the lon constraint all together.
                if ( Math.abs(xhiDbl - xloDbl ) < 358. ) {
                    // Now a wrap around from west to east should be have xhi < xlo;
                    if ( xhiDbl < xloDbl ) {
                        query2 = new StringBuilder(query.toString());
                        query2.append("&"+lonname+">"+0);
                        query2.append("&"+lonname+">="+xhiDbl);
                        query.append("&"+lonname+">"+xloDbl);
                    } else {
                        if (xlo.length() > 0) query.append("&"+lonname+">=" + xloDbl);
                        if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhiDbl);
                    }
                }
                // else it's a global request. Don't constraint on lon at all.
            } else {
                //  If they are not both defined, add the one that is...  There will be no difficulties with dateline crossings...
                if (xlo.length() > 0) query.append("&"+lonname+">=" + xlo);
                if (xhi.length() > 0) query.append("&"+lonname+"<=" + xhi);
            }
        }
        queries.add(query.toString());
        if ( query2.length() > 0 ) {
            queries.add(query2.toString());
        }
        return queries;
    }
    public static boolean isSmallArea(boolean is360, String xlo, String xhi, String ylo, String yhi ) {
        double xhiDbl = String2.parseDouble(xhi);
        double xloDbl = String2.parseDouble(xlo);
        LatLonPoint p = new LatLonPointImpl(0, xhiDbl);
        xhiDbl = p.getLongitude();
        p = new LatLonPointImpl(0, xloDbl);
        xloDbl = p.getLongitude();

        double xspan = Math.abs(xhiDbl - xloDbl);
        double yloDbl = -90.d;
        double yhiDbl = 90.d;
        if (ylo.length() > 0) {
            yloDbl = Double.valueOf(ylo);
        }
        if (yhi.length() > 0) {
            yhiDbl = Double.valueOf(yhi);
        }
        double yspan = Math.abs(yhiDbl - yloDbl);

        double fraction = ((xspan+yspan)/(360.d + 180.d));
        return fraction < .1d;
    }
    public static String getConstraintQuery(List<Constraint> textConstraints) throws UnsupportedEncodingException {
        StringBuilder q = new StringBuilder();
        for (Iterator cit = textConstraints.iterator(); cit.hasNext();) {
            Constraint c = (Constraint) cit.next();
            if ( c.getOp().equals("is") || c.getOp().equals("like") ) {
                q.append("&" + URLEncoder.encode(c.getAsERDDAPString(), StandardCharsets.UTF_8.name())); 
            } else {
                q.append("&" +  URLEncoder.encode(c.getAsString(), StandardCharsets.UTF_8.name())); 
            }
        }
       return q.toString();
    }

}
