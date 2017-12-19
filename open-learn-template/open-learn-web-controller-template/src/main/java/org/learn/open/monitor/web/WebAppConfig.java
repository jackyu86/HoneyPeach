package org.learn.open.monitor.web;

import java.util.List;
import java.util.Locale;

import org.learn.open.monitor.web.converter.DemoMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages="org.learn.open")
public class WebAppConfig extends WebMvcConfigurerAdapter{    
	
    public static void main(String[] args) {  
        SpringApplication.run(WebAppConfig.class, args);  
    }

    @Bean
    public InternalResourceViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //viewResolver.setPrefix("/WEB-INF/classes/views/");
        viewResolver.setPrefix("/WEB-INF/classes/views/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setViewClass(JstlView.class);
        return  viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //super.addResourceHandlers(registry);
        //addResourceLocations指的是文件放置的目录，addResourceHandler指的是对外暴露的访问路径
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
    }
    /** 
     * 配置拦截器 
     * @param registry
     */  
    public void addInterceptors(InterceptorRegistry registry) {
//      registry.addInterceptor(new UserSecurityInterceptor()).addPathPatterns("/user/**");
//    	registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public DemoMessageConverter demoMessageConverter(){
    	return new DemoMessageConverter();
    }

	/**
     * cookie区域解析器;
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
       FixedLocaleResolver slr = new FixedLocaleResolver ();
        //设置默认区域,
       slr.setDefaultLocale(Locale.US);
       return slr;
    }
    
    /**
     * 设置SpringBoot项目的session生命周期
     * @return
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
       return new EmbeddedServletContainerCustomizer() {
           @Override
           public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setSessionTimeout(1800);//单位为S
          }
       };
    }
}
