package org.learn.open.monitor.web;

import java.util.Locale;

import org.learn.open.monitor.web.converter.DemoMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages="org.learn.open")
public class WebAppConfig extends WebMvcConfigurerAdapter{    
	
    public static void main(String[] args) {  
        SpringApplication.run(WebAppConfig.class, args);  
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
