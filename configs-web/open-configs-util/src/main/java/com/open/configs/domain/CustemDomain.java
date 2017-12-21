package com.open.configs.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="OrderFlowConfig")
public class CustemDomain extends BaseConfig implements ConfigDomainInitBean {
	/**
	 * 是否关闭高校代理的自己支付 支付方式
	 */
	public boolean IsCloseSchoolPersonPay;
	/** 是否启用校园大师推荐人 **/
	public boolean IsCommendAgent;
	/** 是否支持有货先发 **/
	public boolean IsInstockFirstShip;
	/** 一天最多可以提交的的全部订单数 **/
	public int MaxSubmitOrdersOneDay;
	/** 一天最多可以提交的有效订单数 **/
	public int MaxValidOrdersOneDay;
	
	/** 不限制每天订单个数的Pin范围 **/
	@XmlElementWrapper(name="NoLimitSubmitTimesPin")
	@XmlElement(name="string")
	public List<String> NoLimitSubmitTimesPin;
	
	/** 是否图书免费促销 **/
	public boolean IsBookFree;

	public boolean IsLog;

	@Override
	public void initMySelf() {
//		System.out.println("=====================================");
//		System.out.println("=============initMySelf==============");
//		System.out.println("=====================================");
	}
	
	
}
