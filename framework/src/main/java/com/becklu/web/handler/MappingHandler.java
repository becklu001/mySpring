package com.becklu.web.handler;

import com.becklu.beans.BeanFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//每个MappingHandler都是一个请求映射器
public class MappingHandler {
    //需要包存uri
    private String uri;
    //该method是controller中的方法，是通过反射来获取的
    private Method method;
    private Class<?> controller;
    private String[] args;

    public MappingHandler(){}

    public MappingHandler(String uri, Method method, Class<?> controller, String[] args) {
        this.uri = uri;
        this.method = method;
        this.controller = controller;
        this.args = args;
    }

    public boolean handle(ServletRequest req, ServletResponse res) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        //判断 本handler（即this指针指向）能够处理该uri
        String requestUri = ((HttpServletRequest)req).getRequestURI();
        //判断该uri（请求的uri）是否和Handler中存放的uri相等
        if(!requestUri.equals(this.uri)){
            return false;
        }
        //如果相等，就要调用Handler中存放的方法
        //先获取参数名 因为参数名数组存放在 对象的args属性中
        Object[] parameters = new Object[args.length];
        for(int i = 0;i<args.length;i++){
            //给参数数组 赋值，实参从 req中获取
            parameters[i]=req.getParameter(args[i]);
        }

        //然后实例化controller，为啥实例化controller要放在 MappingHandler中
//        进行呢？会不会重复实例化
//        Object ctl = controller.newInstance();
        Object ctl = BeanFactory.getBean(controller);
        //反射的方式调用method方法
        Object response = method.invoke(ctl,parameters);
        res.getWriter().println(response.toString());
        return true;
    }
}
