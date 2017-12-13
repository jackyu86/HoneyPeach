package org.learn.open.rest.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;


/**    
 * This class is used for 运行时名称绑定 错误跟踪日志 (服务器端)
 * @author jack  
 * @version   
 *       1.0, 2015年5月9日 下午2:12:55   
 */
@NameBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ErrorLog {
	
}
