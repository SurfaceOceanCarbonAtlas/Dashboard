package gov.noaa.pmel.tmap.las.product.server;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LASServletContextListener implements ServletContextListener {
	private static Logger log = LoggerFactory.getLogger(LASServletContextListener.class.getName());

	
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		ServletContext context = contextEvent.getServletContext();
		Thread fileWatch = (Thread) context.getAttribute("RELOAD_THREAD");
		
		log.error("Shutting down LAS");
		fileWatch.interrupt();

		ScheduledExecutorService scheduler1 = (ScheduledExecutorService) context.getAttribute(LASConfigPlugIn.TEST_SCHEDULER);
		if ( scheduler1 != null ) {
			scheduler1.shutdownNow();
		}
		ScheduledExecutorService scheduler2 = (ScheduledExecutorService) context.getAttribute(LASConfigPlugIn.REAP_SCHEDULER);
		if ( scheduler2 != null ) {
			scheduler2.shutdownNow();
		}


		try {
			fileWatch.join();
		} catch (InterruptedException e) {
			//
		}

	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		
		log.info("Starting the context listener for LAS configuration");
		ServletContext context = contextEvent.getServletContext();
		
		LASConfigPlugIn lasConfigPlugIn = new LASConfigPlugIn();
        ServerConfigPlugIn serverConfigPlugIn = new ServerConfigPlugIn();

        String configFileName = context.getInitParameter("configFileName");		
        lasConfigPlugIn.setConfigFileName(configFileName);   
        
        log.info("Setting configuration file. "+configFileName);
        
        String v7OperationsFileName = context.getInitParameter("v7OperationsFileName");    
		lasConfigPlugIn.setV7OperationsFileName(v7OperationsFileName);
		log.info("Setting operations file name. "+ v7OperationsFileName);
		
		String lasUIFileName = context.getInitParameter("lasUIFileName");
		lasConfigPlugIn.setLasUIFileName(lasUIFileName);
		log.info("Setting UI file. "+ lasUIFileName);
		
		String serverConfigFileName = context.getInitParameter("serverConfigFileName");
		lasConfigPlugIn.setServerConfigFileName(serverConfigFileName);
		serverConfigPlugIn.setConfigFileName(serverConfigFileName);
		log.info("Setting server config file. "+serverConfigFileName);
		
		String lasServers = context.getInitParameter("lasServersFileName");
		lasConfigPlugIn.setLasServersFileName(lasServers);
		log.info("For ESGF, using las servers file. "+lasServers);
		
		String lasServersStaticFileName = context.getInitParameter("lasServersStaticFileName");
		lasConfigPlugIn.setLasServersStaticFileName(lasServersStaticFileName);
		log.info("For ESGF, using las servers static file. "+lasServersStaticFileName);
		
		String version = context.getInitParameter("version");				
		context.setAttribute(LASConfigPlugIn.LAS_VERSION_KEY, version);
		log.info("LAS version "+version);

		
		try {
			
			lasConfigPlugIn.init(context);
			File configFile = new File(configFileName);
	        // Watch the config directory and reload if something changes...
	        WatchService watcher = FileSystems.getDefault().newWatchService();
	        Path conf = configFile.getParentFile().toPath();
	        conf.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
	        Thread fileWatch;
	    	Reload reload;
	        reload = new Reload(watcher, context);
	        fileWatch = new Thread(reload);
	        fileWatch.setDaemon(true);
	        fileWatch.start();
	        context.setAttribute("RELOAD_THREAD", fileWatch);
	        context.setAttribute("RELOAD", reload);
			serverConfigPlugIn.init(context);
		} catch (Exception e) {
			log.error("Error reading the LAS configuration files.  "+e.getMessage());
		}
		
	}

}
