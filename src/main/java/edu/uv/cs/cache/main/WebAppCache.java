package edu.uv.cs.cache.main;

import java.io.File;


import jakarta.servlet.http.HttpServlet;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import edu.uv.cs.cache.controller.WebCacheController;
import edu.uv.cs.cache.service.WebCache;
import edu.uv.cs.cache.service.WebCacheFileStorage;

public class WebAppCache {

    public static String configEnv(String var, String valueIfNotPresent){
        String value = System.getenv(var);
        if (value == null)
           value = valueIfNotPresent;
        System.out.println(var + ": " + value);
        return value;
    }


    public static String configEnv(String var){
        String value = System.getenv(var);
        System.out.println(var + ": " + value);
        return value;
    }

    public static void main(String[] args) throws LifecycleException {
        // Build a Tomcat instance
        
        // Obtain port from environment variable. Default: 8080
        int PORT = Integer.parseInt(configEnv("PORT","8080"));

        // Obtain path from environment variable. Default: "/endpoint"
        String URL_PATH = configEnv("URL_PATH", "/cache");    

        Tomcat tomcat = new Tomcat();
        Connector connector1 = tomcat.getConnector();
        connector1.setPort(PORT);

        tomcat.setBaseDir(".");
        
        String contextPath = "/service";
        String docBase = new File(".").getAbsolutePath();
         
        tomcat.addContext(contextPath, docBase);

        // Creamos una instancia con la lógica de negocio (gestor del almacenamiento
        // de los pares de claves)
        // Este es un ejemplo de uso del principio de sustitución de Liskov
        // (la L en SOLID).
        // Si queremos gestionar las claves en una BBDD solo tenemos que hacer una
        // nueva clase que implemente a WebCache y crear una instancia de esa clase
        // aquí en lugar de la que creamos.
        WebCache wc = new WebCacheFileStorage();
        
        HttpServlet servlet = new WebCacheController(wc);         

        // Register WebCacheController Servlet in Tomcat
        Wrapper w = tomcat.addServlet(contextPath, "WebCache", servlet); 
        // Add URL mapping to servlet
        w.addMapping(URL_PATH);
        
        System.out.println(URL_PATH);
        // Start Tomcat
        tomcat.start();
        tomcat.getServer().await();
    }
}
