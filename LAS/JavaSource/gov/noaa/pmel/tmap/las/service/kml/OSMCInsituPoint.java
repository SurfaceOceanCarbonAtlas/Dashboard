package gov.noaa.pmel.tmap.las.service.kml;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTime;
import org.joda.time.Period;

import gov.noaa.pmel.tmap.las.jdom.LASBackendRequest;
import gov.noaa.pmel.tmap.las.jdom.LASUIRequest;

import java.util.HashMap;
import java.io.IOException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A point that represents an OSMC insitu observation
 * @author Jing Yang Li
 */
public class OSMCInsituPoint extends LASInsituPoint{

    private static Logger log = LoggerFactory.getLogger(OSMCInsituPoint.class);

    private String lat;
    private String lon;
    private String time;
    private String country;
    private String platformType;
    private String platformID;

    public OSMCInsituPoint(String lat, String lon){
        super(lat,lon);
    }

    public OSMCInsituPoint(String lat, String lon, String time){
        super(lat,lon, time);
    }

    public void setCountry(String c){
		country = c;
    }

//TODO: need to fix the ferret script, list_grid_osmc.jnl, to list the full name of type
    public void setPlatformType(String pt){
		platformType = pt;

		if(platformType.contains("DRIFTING BUOYS")){
			platformType = "DRIFTING BUOYS (GENERIC)";
		}

		if(platformType.contains("MOORED BUOYS")){
					platformType = "MOORED BUOYS (GENERIC)";
		}

		if(platformType.contains("VOLUNTEER OBSERV")){
					platformType = "VOLUNTEER OBSERVING SHIPS (GENERIC)";
		}

		if(platformType.contains("PROFILING FLOATS")){
					platformType = "PROFILING FLOATS AND GLIDERS";
		}

        if(platformType.contains("UNDERWAY CARBON")){
	        platformType = "UNDERWAY CARBON SHIPS (GENERIC)";
		}

		if(platformType.contains("TIDE GAUGE")){
	        platformType = "TIDE GAUGE STATION";
	    }

	    if(platformType.contains("REAL TIME REPORT")){
			platformType = "REAL TIME REPORTING";
	    }
	}

    public void setPlatformID(String pi){
				platformID = pi;
	}


    public String getCountry(){
		return country;
    }


    public String getPlatformType(){
		return platformType;
    }

    public String getPlatformID(){
			return platformID;
    }

    /**
     * get the icon URL based on country and platform type
     */
    public String getIconURL(String baseURL, String colorBy){
        String iconURL = baseURL.replaceFirst("output","images");

        //color by platform type
        if(colorBy.equalsIgnoreCase("platform")){
			if(platformType.contains("SHIPS") || platformType.contains("VOSCLIM") ||
                    platformType.contains("VOLUNTEER OBSERV")){
		 	    iconURL += "/osmc/ship/ship.PNG";
  		    }else if(platformType.contains("DRIFTING BUOYS") || platformType.contains("ICE BUOYS") ||
		         platformType.contains("HURRICANE DRIFTERS")){
			    iconURL += "/osmc/drifting_buoys//drifting_buoys.PNG";
	        }else if(platformType.contains("MOORED") || platformType.contains("TSUNAMI") ||
	             platformType.contains("WEATHER BUOYS")){
			    iconURL += "/osmc/moored_buoys/moored_buoys.PNG";
	        }else if(platformType.contains("C-MAN") || platformType.contains("TIDE GAUGE") ||
	             platformType.contains("NWLON") || platformType.contains("GLOSS") ||
	             platformType.contains("REAL TIME REPORTING") ){
			    iconURL += "/osmc/cman/cman.PNG";
	        }else if(platformType.contains("UNKNOWN")){
			    iconURL += "/osmc/unknown/unknown.png";
		    }else if(platformType.contains("PROFILING FLOATS")){
				iconURL += "/osmc/argo/argo.PNG";
		    }else{
		        iconURL = "http://maps.google.com/mapfiles/kml/pal4/icon29.png";
		    }
        //color by country
	    }else if(colorBy.equalsIgnoreCase("country")){
		    if(platformType.contains("SHIPS") || platformType.contains("VOSCLIM") ||
               platformType.contains("VOLUNTEER OBSERV")){
 			    if(country.equalsIgnoreCase("AU")){
			        iconURL += "/osmc//ship/au_ship.png";
			    }else if(country.equalsIgnoreCase("BR")){
			        iconURL += "/osmc/ship/br_ship.png";
			    }else if(country.equalsIgnoreCase("CA")){
			        iconURL += "/osmc/ship/ca_ship.png";
			    }else if(country.equalsIgnoreCase("EU")){
			        iconURL += "/osmc/ship/eu_ship.png";
			    }else if(country.equalsIgnoreCase("DE")){
			        iconURL += "/osmc/ship/de_ship.png";
			    }else if(country.equalsIgnoreCase("FR")){
			        iconURL += "/osmc/ship/fr_ship.png";
			    }else if(country.equalsIgnoreCase("IN")){
				    iconURL += "/osmc/ship/in_ship.png";
			    }else if(country.equalsIgnoreCase("IE")){
				    iconURL += "/osmc/ship/ie_ship.png";
			    }else if(country.equalsIgnoreCase("JP")){
				    iconURL += "/osmc/ship/jp_ship.png";
			    }else if(country.equalsIgnoreCase("NL")){
			        iconURL += "/osmc/ship/nl_ship.png";
			    }else if(country.equalsIgnoreCase("NO")){
				    iconURL += "/osmc/ship/no_ship.png";
			    }else if(country.equalsIgnoreCase("NZ")){
			        iconURL += "/osmc/ship/nz_ship.png";
			    }else if(country.equalsIgnoreCase("SA")){
				    iconURL += "/osmc/ship/sa_ship.png";
			    }else if(country.equalsIgnoreCase("UK")){
			        iconURL += "/osmc/ship/uk_ship.png";
			    }else if(country.equalsIgnoreCase("US")){
			        iconURL += "/osmc/ship/us_ship.png";
		        }else{
			        iconURL += "/osmc/ship/other_ship.png";
			    }
	        }else if(platformType.contains("DRIFTING BUOYS") || platformType.contains("ICE BUOYS") ||
					         platformType.contains("HURRICANE DRIFTERS")){
			    if(country.equalsIgnoreCase("AU")){
					iconURL += "/osmc/drifting_buoys/au_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("BR")){
					iconURL += "/osmc/drifting_buoys/br_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("CA")){
					iconURL += "/osmc/drifting_buoys/ca_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("EU")){
					iconURL += "/osmc/drifting_buoys/eu_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("DE")){
					iconURL += "/osmc/drifting_buoys/de_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("FR")){
					iconURL += "/osmc/drifting_buoys/fr_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("IN")){
					iconURL += "/osmc/drifting_buoys/in_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("IE")){
					iconURL += "/osmc/drifting_buoys/ie_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("JP")){
					iconURL += "/osmc/drifting_buoys/jp_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("NL")){
					iconURL += "/osmc/drifting_buoys/nl_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("NO")){
					iconURL += "/osmc/drifting_buoys/no_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("NZ")){
					iconURL += "/osmc/drifting_buoys/nz_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("SA")){
					iconURL += "/osmc/drifting_buoys/sa_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("UK")){
					iconURL += "/osmc/drifting_buoys/uk_drifting_buoys.PNG";
				}else if(country.equalsIgnoreCase("US")){
					iconURL += "/osmc/drifting_buoys/us_drifting_buoys.PNG";
				}else{
				    iconURL += "/osmc/drifting_buoys/other_drifting_buoys.PNG";
			    }
			}else if(platformType.contains("MOORED") || platformType.contains("TSUNAMI") ||
				             platformType.contains("WEATHER BUOYS")){

				if(country.equalsIgnoreCase("AU")){
				    iconURL += "/osmc/moored_buoys/au_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("BR")){
				    iconURL += "/osmc/moored_buoys/br_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("CA")){
				    iconURL += "/osmc/moored_buoys/ca_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("EU")){
				    iconURL += "/osmc/moored_buoys/eu_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("DE")){
				    iconURL += "/osmc/moored_buoys/de_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("FR")){
				    iconURL += "/osmc/moored_buoys/fr_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("IN")){
					iconURL += "/osmc/moored_buoys/in_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("IE")){
					iconURL += "/osmc/moored_buoys/ie_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("JP")){
					iconURL += "/osmc/moored_buoys/jp_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("NL")){
				    iconURL += "/osmc/moored_buoys/nl_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("NO")){
					iconURL += "/osmc/moored_buoys/no_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("NZ")){
				    iconURL += "/osmc/moored_buoys/nz_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("SA")){
					iconURL += "/osmc/moored_buoys/sa_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("UK")){
				    iconURL += "/osmc/moored_buoys/uk_moored_buoys.PNG";
				}else if(country.equalsIgnoreCase("US")){
				    iconURL += "/osmc/moored_buoys/us_moored_buoys.PNG";
				}else{
				    iconURL += "/osmc/moored_buoys/other_moored_buoys.PNG";
				}
			}else if(platformType.contains("C-MAN") || platformType.contains("TIDE GAUGE") ||
	             platformType.contains("NWLON") || platformType.contains("GLOSS") ||
	             platformType.contains("REAL TIME REPORTING") ){
				if(country.equalsIgnoreCase("AU")){
				    iconURL += "/osmc/cman/au_cman.PNG";
				}else if(country.equalsIgnoreCase("BR")){
				    iconURL += "/osmc/cman/br_cman.PNG";
				}else if(country.equalsIgnoreCase("CA")){
				    iconURL += "/osmc/cman/ca_cman.PNG";
				}else if(country.equalsIgnoreCase("EU")){
				    iconURL += "/osmc/cman/eu_cman.PNG";
				}else if(country.equalsIgnoreCase("DE")){
				    iconURL += "/osmc/cman/de_cman.PNG";
				}else if(country.equalsIgnoreCase("FR")){
				    iconURL += "/osmc/cman/fr_cman.PNG";
				}else if(country.equalsIgnoreCase("IN")){
					iconURL += "/osmc/cman/in_cman.PNG";
				}else if(country.equalsIgnoreCase("IE")){
					iconURL += "/osmc/cman/ie_cman.PNG";
				}else if(country.equalsIgnoreCase("JP")){
					iconURL += "/osmc/cman/jp_cman.PNG";
				}else if(country.equalsIgnoreCase("NL")){
				    iconURL += "/osmc/cman/nl_cman.PNG";
				}else if(country.equalsIgnoreCase("NO")){
					iconURL += "/osmc/cman/no_cman.PNG";
				}else if(country.equalsIgnoreCase("NZ")){
				    iconURL += "/osmc/cman/nz_cman.PNG";
				}else if(country.equalsIgnoreCase("SA")){
					iconURL += "/osmc/cman/sa_cman.PNG";
				}else if(country.equalsIgnoreCase("UK")){
				    iconURL += "/osmc/cman/uk_cman.PNG";
				}else if(country.equalsIgnoreCase("US")){
				    iconURL += "/osmc/cman/us_cman.PNG";
				}else{
				    iconURL += "/osmc/cman/other_cman.PNG";
				}
			}else if(platformType.contains("PROFILING FLOATS")){
			    if(country.equalsIgnoreCase("AU")){
				    iconURL += "/osmc/argo/au_argo.PNG";
				}else if(country.equalsIgnoreCase("BR")){
				    iconURL += "/osmc/argo/br_argo.PNG";
				}else if(country.equalsIgnoreCase("CA")){
				    iconURL += "/osmc/argo/ca_argo.PNG";
				}else if(country.equalsIgnoreCase("EU")){
				    iconURL += "/osmc/argo/eu_argo.PNG";
				}else if(country.equalsIgnoreCase("DE")){
				    iconURL += "/osmc/argo/de_argo.PNG";
				}else if(country.equalsIgnoreCase("FR")){
				    iconURL += "/osmc/argo/fr_argo.PNG";
				}else if(country.equalsIgnoreCase("IN")){
					iconURL += "/osmc/argo/in_argo.PNG";
				}else if(country.equalsIgnoreCase("IE")){
					iconURL += "/osmc/argo/ie_argo.PNG";
				}else if(country.equalsIgnoreCase("JP")){
					iconURL += "/osmc/argo/jp_argo.PNG";
				}else if(country.equalsIgnoreCase("NL")){
				    iconURL += "/osmc/argo/nl_argo.PNG";
				}else if(country.equalsIgnoreCase("NO")){
					iconURL += "/osmc/argo/no_argo.PNG";
				}else if(country.equalsIgnoreCase("NZ")){
				    iconURL += "/osmc/argo/nz_argo.PNG";
				}else if(country.equalsIgnoreCase("SA")){
					iconURL += "/osmc/argo/sa_argo.PNG";
				}else if(country.equalsIgnoreCase("UK")){
				    iconURL += "/osmc//argo/uk_argo.PNG";
				}else if(country.equalsIgnoreCase("US")){
				    iconURL += "/osmc//argo/us_argo.PNG";
				}else{
				    iconURL += "/osmc/argo/other_argo.PNG";
				}
			}else if(platformType.contains("UNKNOWN")){
				iconURL += "/osmc/unknown/unknown.png";
			}else{
				iconURL = "http://maps.google.com/mapfiles/kml/pal4/icon29.png";
		    }
		}else{
		    iconURL = "http://maps.google.com/mapfiles/kml/pal4/icon29.png";
		}
		return iconURL;
	}
}
