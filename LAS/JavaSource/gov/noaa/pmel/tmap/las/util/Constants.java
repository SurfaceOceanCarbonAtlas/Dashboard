package gov.noaa.pmel.tmap.las.util;

public class Constants {
	
	public static final String NAME_SPACE_SPARATOR = "_ns_";
	
	public static final String GET_CATEGORIES = "/getCategories.do";
	public static final String GET_DATASETS = "/getDatasets.do";
	public static final String GET_DATACONSTRAINTS = "/getDataConstraints.do";
	public static final String GET_GRID = "/getGrid.do";  
	public static final String GET_METADATA = "/getMetadata.do";  
	public static final String GET_OPERATIONS = "/getOperations.do"; 
	public static final String GET_OPTIONS = "/getOptions.do";
	public static final String GET_REGIONS = "/getRegions.do";
	public static final String GET_UI = "/getUI.do";
	public static final String GET_VARIABLE = "/getVariable.do";
	public static final String GET_VARIABLES = "/getVariables.do";
	public static final String GET_VIEWS = "/getViews.do";
	public static final String GET_AUTH = "/auth.do";
	public static final String GET_ANNOTATIONS = "/getAnnotations.do";
	public static final String RESOLVE_URL = "/resolveURL.do";
	
    public static final String GE_SERVLET = "/GEServer.do";
	public static final String PRODUCT_SERVER = "/ProductServer.do";
	public static final String LOCAL_PRODUCT_SERVER = "/LocalProductServer.do";
	
	public static final String LOCAL_PRODUCT_SERVER_KEY = "LocalProductServer";

	public static final String GET_CATEGORIES_KEY = "localGetCategories";	
	public static final String GET_DATASETS_KEY = "localGetDatasets";
	public static final String GET_DATACONSTRAINTS_KEY = "localGetDataConstraints";
	public static final String GET_GRID_KEY = "localGetGrid";  
	public static final String GET_METADATA_KEY = "localGetMetadata";  
	public static final String GET_OPERATIONS_KEY = "localGetOperations"; 
	public static final String GET_OPTIONS_KEY = "localGetOptions";
	public static final String GET_REGIONS_KEY = "localGetRegions";
	public static final String GET_UI_KEY = "localGetUI";
	public static final String GET_VARIABLES_KEY = "localGetVariables";
	public static final String GET_VARIABLE_KEY = "localGetVariable";
	public static final String GET_VIEWS_KEY = "localGetViews";	
	public static final String GET_ANNOTATIONS_KEY = "localGetAnnotations";
	public static final String GET_AUTH_KEY = "auth";	
	public static final String CATEGORIES_REQUIRED_KEY = "categories_required";
	
	public static final String GE_OP_ID = "GE";
	public static final String DOWNLOAD_OP_ID = "Interactive_Download"; // The id of the interactive download operation.
	public static final String ANIMATION_OP_ID = "Animation";
	
	//public static final String[] SEARCH_URL={"http://pcmdi9.llnl.gov/esg-search/search", "http://esgdata.gfdl.noaa.gov/esg-search/search", "http://esg-datanode.jpl.nasa.gov/esg-search/search"};
	public static final String ESGF_REPLICAS = "false";
    //public static final String SEARCH_URL="http://esg-datanode.jpl.nasa.gov/esg-search/search";

	// Services to be run by the product server...
	public static final String FERRET_SERVICE = "ferret";
	public static final String CLIMATE_ANALYSIS_SERVICE = "climate_analysis";
	public static final String SHAPE_SERVICE = "shape";
	public static final String DRDS_SERVICE = "drds";
	public static final String DATABASE_SERVICE = "database";
	public static final String OBIS_SERVICE = "obis";
	public static final String IOBIS_SERVICE = "iobis";
	public static final String KML_SERVICE = "kml";
	public static final String FREGRID_SERVICE = "fregrid";
	public static final String TABLEDAP_SERVICE = "tabledap";
	
	
	
}
