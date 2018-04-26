package gov.noaa.pmel.dashboard.server;

import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 *
 */
public class DashboardContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            DashboardConfigStore.get(true);
        } catch (IOException ex) {
            LogManager.getLogger().error("Unexpected configuration error: " + ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DashboardConfigStore.shutdown();
    }
}
