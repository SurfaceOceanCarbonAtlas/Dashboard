/**
 * This software was developed by the Thermal Modeling and Analysis
 * Project(TMAP) of the National Oceanographic and Atmospheric
 * Administration's (NOAA) Pacific Marine Environmental Lab(PMEL),
 * hereafter referred to as NOAA/PMEL/TMAP.
 *
 * Access and use of this software shall impose the following
 * obligations and understandings on the user. The user is granted the
 * right, without any fee or cost, to use, copy, modify, alter, enhance
 * and distribute this software, and any derivative works thereof, and
 * its supporting documentation for any purpose whatsoever, provided
 * that this entire notice appears in all copies of the software,
 * derivative works and supporting documentation. Further, the user
 * agrees to credit NOAA/PMEL/TMAP in any publications that result from
 * the use of this software or in any product that includes this
 * software. The names TMAP, NOAA and/or PMEL, however, may not be used
 * in any advertising or publicity to endorse or promote any products
 * or commercial entity unless specific written permission is obtained
 * from NOAA/PMEL/TMAP. The user also understands that NOAA/PMEL/TMAP
 * is not obligated to provide the user with any support, consulting,
 * training or assistance of any kind with regard to the use, operation
 * and performance of this software nor to provide the user with any
 * updates, revisions, new versions or "bug fixes".
 *
 * THIS SOFTWARE IS PROVIDED BY NOAA/PMEL/TMAP "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL NOAA/PMEL/TMAP BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTUOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE 
 */
package gov.noaa.pmel.tmap.las.util;

import gov.noaa.pmel.tmap.las.ui.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Roland Schweitzer
 *
 */
public class Operation extends Container implements OperationInterface {
    
    public Operation(Element element) {
        super(element);
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OperationInterface#getOptions()
     */
    public ArrayList<Option> getOptions() {
        ArrayList<Option> options = new ArrayList<Option>();
        List opts = element.getChildren("option");
        for (Iterator opIt = opts.iterator(); opIt.hasNext();) {
            Element option = (Element) opIt.next();
            options.add(new Option(option));
        }
        return options;
    }

    /* (non-Javadoc)
     * @see gov.noaa.pmel.tmap.las.util.OperationInterface#setOptions(java.util.ArrayList)
     */
    public void setOptions(ArrayList<Option> options) {
        for (Iterator opsIt = options.iterator(); opsIt.hasNext();) {
            Option option = (Option) opsIt.next();
            element.addContent(option.getElement());
        }
    }
    public JSONObject toJSON() throws JSONException {
        ArrayList<String> asArrays = new ArrayList<String>();
        asArrays.add("operation");
        return Util.toJSON(element, asArrays);
    }
}
