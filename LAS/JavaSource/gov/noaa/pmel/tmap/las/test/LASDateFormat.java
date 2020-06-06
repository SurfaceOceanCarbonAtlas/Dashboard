package gov.noaa.pmel.tmap.las.test;


public class LASDateFormat{
	String dateFormat;
	
    public LASDateFormat(String date){
    	makeDateFormat(date);
    }
    
    //the date formats are case-sensitive
    private void makeDateFormat(String date){
    	
	    String d[];
	    String timeFormat="";
	    
	    if (date.length() == 3){
	        timeFormat="MMM";
	    }else if(date.length() == 5){
	        d = date.split("-");
	        if(d[0].length() == 1){
	            if(d[1].length() != 1){
                 timeFormat="d-MMM";
	            }else if(d[1].length()==1 && d[2].length()==1){
	                timeFormat="y-M-d";
	            }
	        }else if(d[0].length() == 3){
	            timeFormat="MMM-d";
	        }
	    }else if(date.length() == 6){
	        d = date.split("-");
	        if(d[0].length()==2 && d[1].length()==3){
	            timeFormat ="dd-MMM";
	        }else if(d[0].length()==3 && d[1].length()==2){
	            timeFormat ="MMM-dd";
	        }else if(d[0].length()==1 && d[1].length()==2 && d[2].length()==1){
	            timeFormat ="y-MM-d";
	        }else if(d[0].length()==1 && d[1].length()==1 && d[2].length()==2){
	            timeFormat ="y-M-dd";
	        }

	    }else if(date.length() == 7){
	        d = date.split("-");
	        if(d[0].length()==3 && d[1].length()==1 && d[2].length()==1){
             timeFormat ="yyy-M-d";
	        }else if(d[0].length()==2 && d[1].length()==2 && d[2].length()==1){
	            timeFormat ="yy-MM-d";
	        }else if(d[0].length()==2 && d[1].length()==1 && d[2].length()==2){
	            timeFormat ="yy-M-dd";
	        }else if(d[0].length()==1 && d[1].length()==2 && d[2].length()==2){
	            timeFormat ="y-MM-dd";
           }
	    }else if(date.length() == 8){
	            d = date.split("-");
	            if(d[0].length()==4 && d[1].length()==1 && d[2].length()==1){
	                timeFormat ="yyyy-M-d";
	            }else if(d[0].length()==3 && d[1].length()==1 && d[2].length()==2){
	                timeFormat ="yyy-M-dd";
	            }else if(d[0].length()==2 && d[1].length()==2 && d[2].length()==2){
	                timeFormat ="yy-MM-dd";
	            }
	            if(!date.contains("-")){
	                timeFormat ="yyyyMMdd";
	            }
	    }else if(date.length() == 9){
	            d = date.split("-");
	            if(d[0].length()==4 && d[1].length()==2 && d[2].length()==1){
	                timeFormat ="yyyy-MM-d";
	            }else if(d[0].length()==4 && d[1].length()==1 && d[2].length()==2){
	                timeFormat ="yyyy-M-dd";
	            }else if(d[0].length()==3 && d[1].length()==2 && d[2].length()==2){
	                timeFormat ="yyy-MM-dd";
	            }
	            //ferretDate = day+"-"+month+"-"+year;
	    }else if(date.length() == 10){
	            d = date.split("-");
	            if(d[0].length()==4 && d[1].length()==3 && d[2].length()==1){
	                timeFormat ="yyyy-MMM-d";
	            }else if(d[0].length()==1 && d[1].length()==3 && d[2].length()==4){
	                timeFormat ="d-MMM-yyyy";
	            }else if(d[0].length()==4 && d[1].length()==2 && d[2].length()==2){
	                timeFormat ="yyyy-MM-dd";
	            }
            }else if(date.length() == 11){
	            d = date.split("-");
	            if(d[0].length()==4 && d[1].length()==3 && d[2].length()==2){
	                timeFormat ="yyyy-MMM-dd";
	            }else if(d[0].length()==2 && d[1].length()==3 && d[2].length()==4){
	                timeFormat ="dd-MMM-yyyy";
	            }else if(d[0].length()==3 && d[1].length()==2 && d[2].length()==4){
	                timeFormat = "MMM-dd-yyyy";
	            }
             }else if(date.length() == 13){
                    d = date.split("-");
                    if(d[0].length()==4 && d[1].length()==3 && d[2].length()==4){
                        timeFormat ="yyyy-MMM-d HH";
                    }else if(d[0].length()==1 && d[1].length()==3 && d[2].length()==7){
                        timeFormat ="d-MMM-yyyy HH";
                    }else if(d[0].length()==4 && d[1].length()==2 && d[2].length()==5){
                        timeFormat ="yyyy-MM-dd HH";
                    }
	    }else if( (date.length() == 14) && (!date.contains("-")) ){
	            timeFormat = "yyyyMMddhhmmss";
            }else if( (date.length() == 16) && (date.contains("-")) ){
                    timeFormat = "yyyy-MM-dd HH:mm";
	    }else if(date.length() == 17){
	        	timeFormat = "yyyy-M-d HH:mm:ss";   
	    }else if(date.length() == 18){
	        	timeFormat = "yyyy-M-dd HH:mm:ss";
	    }else if(date.length() == 19){
	            timeFormat = "yyyy-MM-dd HH:mm:ss";
	    }
	    dateFormat = timeFormat;
    }
    
    public String getDateFormat(){
	        return dateFormat;
    }

}
