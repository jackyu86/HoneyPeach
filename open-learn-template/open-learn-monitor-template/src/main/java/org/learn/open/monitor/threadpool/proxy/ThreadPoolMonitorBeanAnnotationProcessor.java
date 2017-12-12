package org.learn.open.monitor.threadpool.proxy;

import java.lang.reflect.Field;
import java.util.Map;

import org.learn.open.monitor.threadpool.ThreadPoolMonitorSet;
import org.learn.open.monitor.threadpool.annotation.ThreadPoolMonitorAnnotation;
import org.learn.open.utils.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.CollectionUtils;

/**
 * @Author: jack-yu
 * @Description:  spring注解解析
 */
public class ThreadPoolMonitorBeanAnnotationProcessor  implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ThreadPoolMonitorBeanAnnotationProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        try {
            //类上的注解
            Object annClassValue = AnnotationUtil.loadClassAnnotationValues(
                    ThreadPoolMonitorAnnotation.class, "threadPoolName", bean.getClass().getName());
            if (null != annClassValue) {
                LOGGER.info("Find ThreadPoolMonitorAnnotation on Class [{}] and bean name [{}]. Begin to register ThreadPool Name [{}].",
                                bean.getClass().getName(), beanName, annClassValue);
                ThreadPoolMonitorSet.INSTANCE.register((String) annClassValue, bean);
            }

            //属性上的注解
            Map<String, Object> annFieldMap = AnnotationUtil.loadFieldsAnnotationValues(
                    ThreadPoolMonitorAnnotation.class, "threadPoolName", bean.getClass().getName());
            if (!CollectionUtils.isEmpty(annFieldMap)) {

                for (Map.Entry<String, Object> entry : annFieldMap.entrySet()) {

                    String fieldName = entry.getKey();
                    String tpName = (String) entry.getValue();
                    LOGGER.info("Find ThreadPoolMonitorAnnotation on property [{}] in bean [{}]. Begin to register ThreadPool Name [{}]",
                                    fieldName, beanName, tpName);

                    Field field = bean.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object objRef = field.get(bean);
                    if (null != objRef) {
                        ThreadPoolMonitorSet.INSTANCE.register(tpName, objRef);
                    } else {
                        LOGGER.error("property [{}] is null in bean [{}]", fieldName, beanName);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("In postProcessAfterInitialization exception", e);

        }

        return bean;
    }
}
