package org.learn.open.rest.utils;

/**
* @ClassName: PriorityEnum  
* @Description: 线程优先级枚举
* @author jack-yu
* @date 2016年3月15日 下午2:24:41  
*
 */
public enum PriorityEnum {
	
	//图片上传
	IMAGE_UPLOAD(1),
	//产品上传
	PRODUCT_UPLOAD(2),
	//类目下载
	CATEGORY_DOWNLOAD(3);

	
	private int  priority;
	
	PriorityEnum(int priority){
		this.priority = priority;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	

}
