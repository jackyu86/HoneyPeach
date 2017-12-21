
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
	
	public static void main(String[] args) {
		TaskDynamicConfig t = new TaskDynamicConfig();
		System.out.println(JacksonUtils.toJson(t, true));
	}
}
