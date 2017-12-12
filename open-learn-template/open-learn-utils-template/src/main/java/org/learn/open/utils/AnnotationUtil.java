package org.learn.open.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jack-yu
 * @Description:  注解工具
 */
public class AnnotationUtil {

    /**
     *
     * @param annotationClass  注解类名称
     * @param annotationField  注解类的方法名称(属性名称)
     * @param className  类名称
     * @return  返回类上注解annotationClass 的 annotationField属性的值
     * @throws Exception
     */
    public static Object loadClassAnnotationValues(Class annotationClass,
                                                                  String annotationField,
                                                                  String className)
            throws Exception{

        if(!Class.forName(className).isAnnotationPresent(annotationClass)){
            return null;
        }

        Annotation annotation=Class.forName(className).getAnnotation(annotationClass);

        Method method=annotation.getClass().getDeclaredMethod(annotationField, null);
        Object value=method.invoke(annotation,null);

        return value;

    }

    /**
     *
     * @param annotationClass
     * @param annotationField
     * @param className
     * @return 返回bean 中所有打了annotationClass 注解的field 的annotationField 的值
     * Map<String(fieldName),Object(注解值)>
     * @throws Exception
     */
    public static Map<String, Object> loadFieldsAnnotationValues(Class annotationClass,
                                                                String annotationField,
                                                                String className)
            throws Exception{

        Map<String,Object> result=new HashMap<String, Object>();

        Field [] fields=Class.forName(className).getDeclaredFields();
        for(Field f:fields){
            if(f.isAnnotationPresent(annotationClass)){
                Annotation annotation=f.getAnnotation(annotationClass);
                Method m=annotation.getClass().getDeclaredMethod(annotationField);
                Object value=m.invoke(annotation);
                result.put(f.getName(),value);
            }
        }

        return result;
    }
}
