package org.learn.open.rest.exception;


import org.learn.open.rest.utils.ErrorCodeEnum;

/**
 * 异常基类，各个模块的运行期异常均继承与该类
 */
public class BaseException extends RuntimeException {

    /**
     * the serialVersionUID
     */
    private static final long serialVersionUID = 1381325479896057076L;
    
    	/*
    	 */
    private ErrorCodeEnum ErrorCodeEnum;
    
    /**
	 * @return the ErrorCodeEnum
	 */
	public ErrorCodeEnum getErrorCodeEnum() {
		return ErrorCodeEnum;
	}

	/**
	 * @param ErrorCodeEnum the ErrorCodeEnum to set
	 */
	public void setErrorCodeEnum(
			ErrorCodeEnum ErrorCodeEnum) {
		this.ErrorCodeEnum = ErrorCodeEnum;
	}

	public BaseException(String message, Throwable cause, ErrorCodeEnum ErrorCodeEnum) {
        super(message, cause);
        this.ErrorCodeEnum = ErrorCodeEnum;
    }
}
