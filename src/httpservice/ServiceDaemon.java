/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpservice;

import api.utils.FunctionUtils;
import api.configuration.ConfigInfo;
import com.kyt.framework.util.ConvertUtils;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

/**
 *
 * @author Y Sa
 */
public class ServiceDaemon {
    
    public static void main(String[] args) throws Exception {
        
        FunctionUtils.warmListRewriteEntity();
        
        QueuedThreadPool threadPool = new QueuedThreadPool();
        
        threadPool.setMinThreads(ConfigInfo.MIN_THREAD);
        threadPool.setMaxThreads(ConfigInfo.MAX_THREAD);
        
        Server server = new Server(threadPool);

        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
        server.addBean(new ScheduledExecutorScheduler());
        
        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);
        
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendServerVersion(true);
        httpConfig.setSendDateHeader(true);
        
        String host_listen = ConfigInfo.HOST_LISTEN;
        int port_listen = ConvertUtils.toInt(ConfigInfo.PORT_LISTEN);
        
        Logger.getLogger(ServiceDaemon.class).info("Start service with host: " + host_listen + " - port: " + port_listen);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setHost(host_listen);
        connector.setPort(port_listen);
        
        server.setConnectors(new Connector[]{connector});

        // URL Rewrite handler
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        
        RewriteRegexRule rewriteRegexRule_shorten_version_withPrams = new RewriteRegexRule();     
        
        rewriteRegexRule_shorten_version_withPrams.setRegex("^/v([0-9.]{1,})\\/([a-zA-Z0-9-_\\-.]*)(\\/([0-9]{1,}))?(\\/([a-zA-Z0-9-_\\-.]*)(\\/([a-zA-Z0-9_\\-]{1,}))?)?$");
        rewriteRegexRule_shorten_version_withPrams.setReplacement("/$2?version=$1&mainId=$4&action=$6&subId=$8");
        rewriteHandler.addRule(rewriteRegexRule_shorten_version_withPrams);
        // Servlet Handlers
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        // Servlet Mapping URL
        handler.addServlet("api.servlet.CommonServlet", "/commons");
        handler.addServlet("api.servlet.UserServlet", "/users");
        
        handler.addServlet("api.servlet.ErrorServlet", "/errors");

        // Vesion     
        handler.addServlet("api.servlet.RedirectServlet", "/*");

        //Add GZip
        handler.addFilter(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));

        //// Set handlers
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(rewriteHandler);
        handlers.addHandler(handler);
        handlers.addHandler(stats);
        server.setHandler(handlers);
        
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        
        server.start();
        server.join();
    }
}
