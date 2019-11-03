package com.becklu.web.handler;

import com.becklu.web.mvc.Controller;
import com.becklu.web.mvc.RequestMapping;
import com.becklu.web.mvc.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerManager {
//    静态属性，存放所有controller中的所有带有注解的方法
    public static List<MappingHandler> mappingHandlerList = new ArrayList<>();
    public static void resolveMappingHandler(List<Class<?>> classList){
//        遍历这些类，找出所有带有Mapping注解的方法
            for(Class cls:classList){
                if(cls.isAnnotationPresent(Controller.class)){
                    parseHandlerFromController(cls);
                }
            }
    }

    //从带有controller注解的类中解析出 MappingHandler
    private static void parseHandlerFromController(Class<?> cls){
        Method[] methods = cls.getDeclaredMethods();
        //遍历这些方法，找到被RequireMapping注解的方法
        for(Method method:methods){
            if(!method.isAnnotationPresent(RequestMapping.class)){
                continue;
            }
            //可以从RequestMapping注解中获取方法对应的uri
            String uri = method.getDeclaredAnnotation(RequestMapping.class).value();
            //可以遍历方法的参数列表，获取带有RequestParam注解的参数
            List<String> paramNameList = new ArrayList<>();
            for(Parameter parameter:method.getParameters()) {
                if (!parameter.isAnnotationPresent(RequestParam.class)) {
                    continue;
                }
//                paramNameList.add(parameter.getName());
                //为什么不能直接放回 parameter.getName()呢？
                System.out.println("parameter.getName()=" + parameter.getName());
                System.out.println("anotherName=" + parameter.getDeclaredAnnotation(RequestParam.class).value());

                paramNameList.add(parameter.getDeclaredAnnotation(RequestParam.class).value());
            }
            //参数名容器转化为数组形式
            String[] params = paramNameList.toArray(new String[paramNameList.size()]);
            //到此uri和参数名都获取了（注意获取的是形参名）
//                构造一个Handler
            MappingHandler mappingHandler = new MappingHandler(uri,method,cls,params);
            //把该handler放入HandlerManager的静态属性中
            HandlerManager.mappingHandlerList.add(mappingHandler);
        }
    }
}
