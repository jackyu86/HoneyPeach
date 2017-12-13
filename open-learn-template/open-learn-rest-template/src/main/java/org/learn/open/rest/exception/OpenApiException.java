package org.learn.open.rest.exception;

import org.learn.open.rest.utils.ErrorCodeEnum;

/**
* @ClassName: OpenApiException  
* @author jack-yu
* @date 2015年8月24日 下午8:28:23  
*
 */
public class OpenApiException extends BaseException {

	/**
	 * Constructors  强制传递异常枚举值
	 * 
	 * @param cause
	 *            异常接口
	 * @param code
	 *            错误代码
	 */
	public OpenApiException(Throwable cause, ErrorCodeEnum ErrorCodeEnum) {
		super(ErrorCodeEnum.getEnMes(), cause, ErrorCodeEnum);
	}
	private static final long serialVersionUID = -3711290613973933714L;

}
