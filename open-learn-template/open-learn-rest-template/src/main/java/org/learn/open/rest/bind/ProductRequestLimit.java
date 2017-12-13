package org.learn.open.rest.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**  
 * @ClassName: RequestLimit  
 * @Description: TODO(请求限制)  
 * @author jack-yu
 * @date 2015年8月21日 下午12:13:03  
 *    
 */
@NameBinding
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ProductRequestLimit {
	/** JSON属性映射名称 **/
	public String value() default "";
}
