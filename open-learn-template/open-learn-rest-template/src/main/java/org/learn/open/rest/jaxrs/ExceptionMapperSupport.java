package org.learn.open.rest.jaxrs;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.learn.open.rest.exception.BaseException;
import org.learn.open.rest.utils.ErrorCodeEnum;
import org.learn.open.rest.utils.RestNetUtils;


/**  
 * @ClassName: ExceptionMapperSupport  
 * @Description: 统一异常处理 
 * @author jack-yu
 * @date 2015年8月24日 下午8:31:03  
 *    
 */
@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Exception> {
	private static final Logger LOGGER = Logger
			.getLogger(ExceptionMapperSupport.class);
	@Context
	private HttpServletRequest request;

	@Context
	private ServletContext servletContext;

	/**
	 * 异常处理
	 * 
	 * @param exception
	 * @return 异常处理后的Response对象
	 */
	public Response toResponse(Exception exception) {
		//String message =Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
		//Status statusCode = Status.OK;
		ErrorCodeEnum ErrorCodeEnum=null;
		String entity="";
		//WebAppContext context = (WebAppContext) servletContext;
		// 处理checked exception
		if (exception instanceof BaseException) {
			BaseException baseException = (BaseException) exception;
			 ErrorCodeEnum = baseException.getErrorCodeEnum();
			 entity = RestNetUtils.buildError(ErrorCodeEnum);
		//	message =ErrorCodeEnum.getCodeMes();
			
		} else if (exception instanceof NotFoundException) {
		//	message = Status.NOT_FOUND.getReasonPhrase();
			ErrorCodeEnum = ErrorCodeEnum.REQUESTNOTFOUND;
			//statusCode = Status.NOT_FOUND;
			 entity = RestNetUtils.buildError(ErrorCodeEnum);
		} else{
			ErrorCodeEnum = ErrorCodeEnum.SERVICEEXCEPTION.setEnMes(exception.getMessage());
			 entity = RestNetUtils.buildError(ErrorCodeEnum);
		}
		LOGGER.error(new StringBuilder().append("errorCode:").append(ErrorCodeEnum.getValue()).append(",en:").append(ErrorCodeEnum.getEnMes()).append(",zh:").append(ErrorCodeEnum.getZhMes()).append(",tenantId:").append(ErrorCodeEnum.getTenantId()).toString());
		return Response.ok(entity, MediaType.APPLICATION_JSON).status(Status.OK)
				.build();
	}

}
