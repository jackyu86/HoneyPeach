package org.learn.open.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 服务返回结果
 * 
 * @author bailiandong
 *
 * @param <T>
 */
public class Result<T> implements Serializable {
	
	private static final long serialVersionUID = -2300427401174112009L;

	/**
	 * 操作是否成功
	 */
	private boolean success = false;
	
	/**
	 * 业务编码
	 */
	private String businessCode;
	
	/**
	 * 错误提示码
	 */
	private String errorCode;
	
	/**
	 * 错误提示信息
	 */
	private String errorMessage;
	
	/**
	 * 业务对象，用于查询单个、修改单个等
	 */
	private T obj;
	
	/**
	 * 业务对象数组，用于批量查询
	 */
	private List<T> objs;
	
	/**
	 * 失败列表
	 */
	private List<Long> failList;

	public Result(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public List<T> getObjs() {
		return objs;
	}

	public void setObjs(List<T> objs) {
		this.objs = objs;
	}

	public List<Long> getFailList() {
		return failList;
	}

	public void setFailList(List<Long> failList) {
		this.failList = failList;
	}
}
