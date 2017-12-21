import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.open.configs.service.TypeConfigService;


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
