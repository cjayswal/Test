package test.automation.report;

import javax.servlet.MultipartConfigElement;

import org.seleniumhq.jetty9.server.Handler;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.server.handler.HandlerList;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;

public class ReportServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		// ChannelConnector connector = new SelectChannelConnector();
		// connector.setPort(8080);
		// server.addConnector(connector);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "dashboard.html" });
		resource_handler.setResourceBase(".");

		HandlerList handlers = new HandlerList();
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/report");
		handlers.setHandlers(new Handler[] { context, resource_handler });
		server.setHandler(handlers);
		ServletHolder fileUploadServletHolder = new ServletHolder(new ReportUploadServelate());
		context.addServlet(fileUploadServletHolder, "/upload");
		fileUploadServletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement("data/tmp"));
		server.start();
		server.join();
	}
}
