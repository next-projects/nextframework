package org.erplite;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.jul.Log4jBridgeHandler;
import org.apache.tomcat.util.scan.StandardJarScanFilter;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class Main {

    public static void main(String[] args) throws Exception {
        // Bridge JUL (used by Tomcat) to Log4j2
        Log4jBridgeHandler.install(true, null, true);

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String webRoot = new File("WebContent").getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("build/tomcat");
        tomcat.getConnector();  // Initialize the default HTTP connector
        
        Context context = tomcat.addWebapp("/app", webRoot);
        configureJarScanner(context);

        System.out.println("Starting Tomcat...");
        System.out.println("  Web root: " + webRoot);
        System.out.println("  Port: " + port);
        tomcat.start();
        System.out.println("Server started: http://localhost:" + port + "/app");
        tomcat.getServer().await();
    }

    /**
     * Speed up startup by skipping JAR scanning for most libraries.
     *
     * Only next-view and next-web jars are scanned as they contain TLDs and web-fragments.
     * If other modules add web-fragment.xml or .tld files, they must be added to the list below.
     */
    private static void configureJarScanner(Context context) {
        String jarsToScan = "next-view-*.jar,next-web-*.jar";

        StandardJarScanner scanner = (StandardJarScanner) context.getJarScanner();
        StandardJarScanFilter filter = new StandardJarScanFilter();
        filter.setDefaultTldScan(false);
        filter.setDefaultPluggabilityScan(false);
        filter.setTldScan(jarsToScan);
        filter.setPluggabilityScan(jarsToScan);
        scanner.setJarScanFilter(filter);
    }

}
