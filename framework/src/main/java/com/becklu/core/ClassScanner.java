package com.becklu.core;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {
//    扫描一个包，获取类的列表
    public static List<Class<?>> scanClass(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<>();
//        包名转为路径，.换成斜杠/
        String path = packageName.replace('.','/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //获得一个资源的迭代器
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()){
            URL resource = resources.nextElement();
            //最终的类都要打成jar包存在，所以我们要获取的包其后缀名是jar
            //我们的目的是为了获取类的路径
            System.out.println("resource:"+resource.getPath());

            if(resource.getProtocol().contains("jar")){
                JarURLConnection jarURLConnection =
                        (JarURLConnection) resource.openConnection();
                String jarFilePath = jarURLConnection.getJarFile().getName();
                classList.addAll(getClassesFromJar(jarFilePath,path));
            }else{
                //处理其它资源
            }
        }
        System.out.println("======================");
        return classList;
    }

    //一个jar包中可能有很多类，我们需要根据path来获取必要的类
    private  static List<Class<?>> getClassesFromJar(String jarFilepath,String path) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFilepath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while(jarEntries.hasMoreElements()){
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            //获得的entryName类似 com/becklu/test/Test.class
            //我们需要获得 以我们传入路径开头的文件，而且要以.class结尾
            if(entryName.startsWith(path)&&entryName.endsWith(".class")){
                //获取我们要的类的全限定名
                String classFullName = entryName.replace("/",".")
                        .substring(0,entryName.length()-6);
                //需要类的加载器把它们加载到 jvm中
                //查看源码，我们可以发现 forName方法的返回值类型也是 Class<?>
                classes.add(Class.forName(classFullName));

            }
        }
        return classes;
    }
}
