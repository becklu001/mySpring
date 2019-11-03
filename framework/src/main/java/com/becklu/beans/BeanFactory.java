package com.becklu.beans;

import com.becklu.web.mvc.Controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    //添加一个属性用来存储Bean类型到Bean实例的映射？
    //后续拓展时可能会并发处理，所以要用ConcurrentHashMap
    private static Map<Class<?>,Object> classToBean =
            new ConcurrentHashMap<>();
    //添加一个方法，从映射（Map）里获取Bean
    //参数是Class对象，获取该Class的一个Bean对象
    public static Object getBean(Class<?> cls){
        return classToBean.get(cls);
    }
    //Bean 的初始化方法
    public static void initBean(List<Class<?>> classList) throws Exception{
        List<Class<?>> toCreate = new ArrayList<>(classList);
        //遍历classes容器，每成功创建一个Bean对象，就从classes容器中删除
        //该类元素
        while (toCreate.size() != 0){
            //当前容器大小
            int remainSize = toCreate.size();
            //此处使用了 普通的for循环，而没有使用forEach循环
            //否则在 forEach中试图remove ArrayList的元素会发生
            //同步异常？因为增强的for循环是通过迭代器实现的
            for(int i=0;i<toCreate.size();i++){
              if(finishCreate(toCreate.get(i))){
                  toCreate.remove(i);
              }
            }
            //如果本轮遍历 classes容器中的元素个数没有变化，
            //说明有互相依赖，陷入了死循环，要抛出异常
            if(toCreate.size() == remainSize){
                throw new Exception("mutual dependency");
            }
        }
    }

    private static boolean finishCreate(Class<?> cls) throws IllegalAccessException, InstantiationException {
       //如果根本没用@Bean注解，说明不需要初始化为Bean对象，直接删除
        //controller也是一种特殊的Bean，还需要判断时候有@Controller注解
        if(!(cls.isAnnotationPresent(Bean.class) ||
                cls.isAnnotationPresent(Controller.class))){
            return true;
       }
       //为什么要先创建Bean对象呢？不是判断是否需要创建之后才
        //创建吗？虽然这里创建了，但是后边如果bean工厂中没有需要
        //的依赖，会直接返回false，这个创建的bean对象会被gc掉
       Object bean = cls.newInstance();
        //看看它有没有要解决的依赖
        for(Field field:cls.getDeclaredFields()){
            //如果被Autowired注解，说明有依赖需要注入
            if(field.isAnnotationPresent(Autowired.class)){
               //获取依赖的对象 的类，根据类从bean工厂中查看是否存在
                //该 Bean对象
                Class<?> fieldType = field.getType();
                Object reliantBean = BeanFactory.getBean(fieldType);
                if (reliantBean == null) {
                    return  false;
                }
                //如果被依赖的Bean对象存在
                //首先要修改 该字段的可访问性，然后才能给该字段注入
                //依赖
                field.setAccessible(true);
                //把从bean工厂中找到的依赖注入到要创建的Bean对象中
                field.set(bean,reliantBean);
            }
        }
        //创建成功后，把自己也添加到Bean工厂中
        classToBean.put(cls,bean);
        return true;
    }
}
