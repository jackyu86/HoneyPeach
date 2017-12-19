zk存储配置与监听封装


使用说明

1. jar包:
<groupId>com.open.configs</groupId>
<artifactId>open-configs-util</artifactId>
<version>0.0.1-SNAPSHOT</version>

2.数据格式:
	a.先定义domain
	b.设置自己的配置信息　
	c.生成json串.当然也可以自己手动写json串
		//a. 先定义domain
		public class CustemDomain extends BaseConfig implements ConfigDomainInitBean

		//b.设置自己的配置信息
		//项目配置项
		final CustemDomain custemDomain = new CustemDomain();
		custemDomain.NoLimitSubmitTimesPin = Arrays.asList("aa", "bb", "cc");

		//配置项的其他信息
		final ConfigOther db = new ConfigOther();
		db.setCacheSecond(1000);
		db.setConfigName("shguo");
		db.setYn(1);//要配置成1不然不更新

		custemDomain.setZother(db);

		//c. 生成json串
		String configStr = JsonUtils.writeValue(custemDomain, true);

		//d.将应用所要path与生成的json串通过管理后台保存.

3.程序中获取配置项:
集成spring使用

spring-config-zk.xml

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
					 http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
					 http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd "
	default-autowire="byName">

 	<bean id="zookeeperConfig" class="com.open.configs.domain.ZookeeperConfig">
 		<!-- 配置zk集群地址-->
		<property name="zkConfigs" value="192.168.1.204:2181,192.168.1.204:2182,192.168.1.204:2183"></property>
		<property name="zkCheckInterval" value="10000"></property>
		<property name="environment" value="development"></property>
		<!-- 要tomcat用户可读写  linux下设置文件读写权限-->
		<property name="localPath" value="/home/zookeeper_config"></property>
	</bean>

	<bean id="openZookeeperClient" class="com.open.configs.core.OpenZookeeperClient">
		<constructor-arg index="0" ref="zookeeperConfig"></constructor-arg>
	</bean>

	<bean id="zkConfigService" class="com.open.configs.service.impl.TypeConfigServiceImpl" >
		<property name="jdZookeeperClient" ref="openZookeeperClient"></property>
		<property name="zookeeperConfig" ref="zookeeperConfig"></property>
	</bean>

</beans>

配置信息实体bean
import com.open.configs.domain.BaseConfig;
import com.open.configs.domain.ConfigDomainInitBean;

/**
 * 动态配置实体bean
 *
 */
public class TaskDynamicConfig extends BaseConfig implements ConfigDomainInitBean {

	// redis监控worker 删除大于当前时间60s的数据
	private int interValDate = 60;

	// 是否下发wms2.0
	private boolean wmsOpen = true;

	private String brandURL = "http://java.360buy.com/service/brand.action";
	private String newBrandURL = "http://store.item.jd.local/push/service/brand.action";

	// 积压任务报警类型
	private String type = "7,8,20";

	// 积压任务报警阀值
	private int count = 5000;

	public int getInterValDate() {
		return interValDate;
	}

	public void setInterValDate(int interValDate) {
		this.interValDate = interValDate;
	}

	public boolean isWmsOpen() {
		return wmsOpen;
	}

	public void setWmsOpen(boolean wmsOpen) {
		this.wmsOpen = wmsOpen;
	}

	public String getBrandURL() {
		return brandURL;
	}

	public void setBrandURL(String brandURL) {
		this.brandURL = brandURL;
	}

	public String getNewBrandURL() {
		return newBrandURL;
	}

	public void setNewBrandURL(String newBrandURL) {
		this.newBrandURL = newBrandURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}

public class CommonService {
	private final static Logger logger = LoggerFactory.getLogger(CommonService.class);
	private TypeConfigService zkConfigService;
	private static TaskDynamicConfig taskDynamicConfig = new TaskDynamicConfig();
	private static String path = "/config/exadeliver/deliverConfig";

	public void setZkConfigService(TypeConfigService zkConfigService) {
		this.zkConfigService = zkConfigService;
	}


	/*
	 * 获取配置信息
	 *
	 * @see jd.gms.batchtask.service.CommonService#getDeliverConfig()
	 */
	public TaskDynamicConfig getTaskDynamicConfig() {
		try {
			taskDynamicConfig = zkConfigService.getConfigByClass(path, true, TaskDynamicConfig.class);
		} catch (Exception e) {
			logger.error("CommonServiceImpl.getTaskDynamicConfig error", e);
		}
		return taskDynamicConfig;
	}
}
