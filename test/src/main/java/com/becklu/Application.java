package com.becklu;

import com.becklu.starter.MiniApplication;
import com.becklu.web.server.TomcatServer;
import org.apache.catalina.LifecycleException;

public class Application {
    public static void main(String[] args) {
        System.out.println("Hello world");
        MiniApplication.run(Application.class,args);

    }
}
