package org.learn.open.rest.utils; /**
* @Title: FieldInfo.java  
* @Package .soa.rest.boss.utils
* @Description: TODO(用一句话描述该文件做什么)  
* @author jack-yu
* @date 2015年9月15日 下午9:11:13  
* @version V1.0    
*/

/**  
 * @ClassName: FieldInfo  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author jack-yu
 * @date 2015年9月15日 下午9:11:13  
 *    
 */
public class FieldInfo {

	/**
	 * 对象名
	 * */
	public String name;
	
	/**
	 * 对象
	 * */
	public Class clas;
	/**
	 * 对象值
	 * */
	public Object obj;
	
	
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>    
	*/
	public FieldInfo() {
		super();
	}


	/**  
	* <p>Title: </p>  
	* <p>Description: </p>  
	* @param name
	* @param clas
	* @param obj  
	*/
	public FieldInfo(String name, Class clas, Object obj) {
		super();
		this.name = name;
		this.clas = clas;
		this.obj = obj;
	}
	
	
}
