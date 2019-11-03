package com.becklu.starter;

import com.becklu.beans.BeanFactory;
import com.becklu.core.ClassScanner;
import com.becklu.web.handler.HandlerManager;
import com.becklu.web.server.TomcatServer;
import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.util.List;

public class MiniApplication {
    public static void run(Class<?> cls, String[] args){
        System.out.println("Hello MiniSpring");

        TomcatServer tomcatServer = new TomcatServer(args);
        try {
            tomcatServer.startServer();
            //扫描包，获取类的全限定名
            List<Class<?>> classList = ClassScanner.scanClass(
                    cls.getPackage().getName()
            );
            System.out.println(cls.getPackage().getName());
            System.out.println("-----------------------------");
            classList.forEach(it->System.out.println(it.getName()));
            //解析类的字节码文件，获取类中被注解的方法注入 MappingHandler
            //Handler列表整体由 HandlerManager来管理
            HandlerManager.resolveMappingHandler(classList);
            //初始化Bean工厂
            BeanFactory.initBean(classList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
