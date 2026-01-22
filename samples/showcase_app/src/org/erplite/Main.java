package org.erplite;

import java.io.File;

import org.apache.catalina.startup.Tomcat;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String webRoot = "WebContent";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("build/tomcat");

        tomcat.addWebapp("", new File(webRoot).getAbsolutePath());

        tomcat.start();
        System.out.println("Server started: http://localhost:" + port);
        tomcat.getServer().await();
    }

}
