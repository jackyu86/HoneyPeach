package org.learn.open.rest.utils;

/**
 *
 * @author jack
 * @version 1.0, 2015年5月14日 下午5:00:05
 */
public enum ErrorCodeEnum {
	//服务异常
	SERVICEEXCEPTION(1000),
	//参数丢失错误
	PARAMETERMISSINGERROR(1001),
	//参数验证错误
	PARAMETERINVALIDRROR(1002),
	//远程服务异常
	REMOTESERVICEEXCEPTION(1003),
	//SERVICE
	SERVICETIMEOUTERROR(1004,"service time out , please try again later","服务处理超时，请稍后再试"),
	//无授权信息
	AUTHISNULL(10000,"The authorization accounting information is empty"), 
	//授权失败
	AUTHFAILURE(10001, "Authorization failure"), 
	//请求太频繁  100/分
	REQTOOFREQUENTLY(10002, "Request too frequently"), 
	//授权参数错误
	AUTHPARAMERROR(10003,"Authorization parameter error"),
	//请求时间超过五分钟
	TIMESTAMPINVALID(10004,"timeStamp is invalid"),
	//签名错误
	SIGNATUREERROR(10005,"sign error ,please check your params"),
	//无权限访问
	UNAUTHORIZEDACCESS(10006, "unauthorized access","无权限访问此服务"), 
	//accessToken不存在
	ACCESSTOKENERROR(10007,"access token error","access token 错误或为空"),
	//accessToken 关联错误
	ACCESSTOKENTENANTIDERROR(10008,"access token for appkey error","错误的授权"),
	//请求未发现
	REQUESTNOTFOUND(11000, "Request not found"), 
	//内部数据错误无租户id
	DATAERROR4TENANTID(12000,"Data error no tenantid, Contact us","内部参数错误,无租户ID,请联系"),
	
	DATAERROR4CONVERSION(12001),
	
	PRODUCTNOTFIND(20000,"Product not find","产品不存在"),
	
	PAGENUMBERPARAMTER(20001,"Page number paramter error","页码参数错误"),
	
	DOWNLOADCATEGORYFAILE(20002),
	
	ADDPRODUCTEFAILE(20003),
	
	MODIFYPRODUCTEFAILE(20004),
	
	DELETEPRODUCTFAILE(20005),
	
	GETPRODUCTFAILE(20006),
	
	PARAMATERERROR(21000,"paramter error"),
	
	PRODUCTACTIONERROR(22000),
	
	IMAGEUPLOADFAILE(23000),
	
	FINDSHOPSFAILE(24000);
	//error code
	private int value;
	private String enMes;
	private String zhMes;
	private String tenantId;
	ErrorCodeEnum(int value){
		this.value = value;
	}

	ErrorCodeEnum(int value, String enMes) {
		this.value = value;
		this.enMes = enMes;
	}
	ErrorCodeEnum(int value, String enMes,String zhMes) {
		this.value = value;
		this.enMes = enMes;
		this.zhMes=zhMes;
	}

	// 通过value获取对应的枚举对象
	public static ErrorCodeEnum getExamType(int value) {
		for (ErrorCodeEnum examType : ErrorCodeEnum.values()) {
			if (value == examType.getValue()) {
				return examType;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

	public ErrorCodeEnum setValue(int value) {
		this.value = value;
		return this;
	}

	/**
	 * @return the enMes
	 */
	public String getEnMes() {
		return enMes;
	}

	/**
	 * @param enMes the enMes to set
	 */
	public ErrorCodeEnum setEnMes(String enMes) {
		this.enMes = enMes;
		return this;
	}

	/**
	 * @return the zhMes
	 */
	public String getZhMes() {
		return zhMes;
	}

	/**
	 * @param zhMes the zhMes to set
	 */
	public ErrorCodeEnum setZhMes(String zhMes) {
		this.zhMes = zhMes;
		return this;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public ErrorCodeEnum setTenantId(String tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	
}
