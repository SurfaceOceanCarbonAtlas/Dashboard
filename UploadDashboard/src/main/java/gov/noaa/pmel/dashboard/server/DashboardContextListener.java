package gov.noaa.pmel.dashboard.server;

import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 */
public class DashboardContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            DashboardConfigStore.get(true);
        } catch ( Exception ex ) {
            LogManager.getLogger().error("Unexpected dashboard configuration error: " + ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DashboardConfigStore.shutdown();
    }
}
