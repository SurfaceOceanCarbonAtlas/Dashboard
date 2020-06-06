package gov.noaa.pmel.tmap.las.service.kml;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Enumeration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * For generating a collection of place marks for LAS data
 *
 * @author Jing Yang Li
 */
public interface LASPlacemarks{
    public ArrayList getPlacemarks();
    public String getLookAtLon();
	public String getLookAtLat();
}
