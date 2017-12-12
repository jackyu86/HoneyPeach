package org.learn.open.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


@Service(value = "springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public static Object getBeanById(String beanId){
        return applicationContext.getBean(beanId);
    }

//    public static <T> T getBeanById(String beanId){
//        return (T)applicationContext.getBean(beanId);
//    }

//
//    public static <T> T getObject(int id){
//        if(id==0){
//            return (T)new String("aaa");
//        }else if(id==1){
//            return (T)new Integer("111");
//        }else if(id==2){
//            return (T)Arrays.asList("111","222");
//        }
//
//        return null;
//    }
//
//    public static void main(String [] argv){
//
//        String s1=getObject(0);
//        Integer i1=getObject(1);
//        List<String> l1=getObject(2);
//
//        System.err.println(s1);
//        System.err.println(i1);
//        System.err.println(l1);
//    }
}
