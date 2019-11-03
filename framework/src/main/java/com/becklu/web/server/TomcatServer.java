package com.becklu.web.server;

import com.becklu.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer {
    private Tomcat tomcat;
    private String[] args;

    public TomcatServer(String[] args){
        this.args = args;
    }

    public TomcatServer(){ }

    public void startServer() throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setPort(6699);
        tomcat.start();

        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        Tomcat.addServlet(context,"dispatcherServlet",
                dispatcherServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/","dispatcherServlet");
        tomcat.getHost().addChild(context);

        Thread awaitThread = new Thread(()->{
            TomcatServer.this.tomcat.getServer().await();
        },"tomcat_await_thread");

        awaitThread.setDaemon(false);
        awaitThread.start();
    }

}
