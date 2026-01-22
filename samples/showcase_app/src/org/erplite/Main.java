package org.erplite;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String webRoot = new File("WebContent").getAbsolutePath();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("build/tomcat");

        // Add connector to handle requests
        tomcat.getConnector();

        // Add webapp with proper context configuration
        Context ctx = tomcat.addWebapp("/app", webRoot);

        System.out.println("Starting Tomcat...");
        System.out.println("  Web root: " + webRoot);
        System.out.println("  Port: " + port);
        tomcat.start();
        System.out.println("Server started: http://localhost:" + port + "/app/next");
        tomcat.getServer().await();
    }

}
