package org.learn.open.rest.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: TypeConverter  
* @Description
* @author jack-yu
* @date 2016年1月21日 下午5:45:05  
*
 */
public class TypeConverter implements net.sf.cglib.core.Converter {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.core.Converter#convert(java.lang.Object,
	 * java.lang.Class, java.lang.Object)
	 */
	public Object convert(Object value, Class target, Object context){
	//	System.out.println(value);
		
			try {
				return formatData(value, target);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			}

		return null;
	}
	
	private  Object formatData(Object value, Class<?> field)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException {

		if(value!=null){
			String className="String";
		// 得到属性的类名
				className=field.getName();
		// 得到属性值
		if (field.isAssignableFrom(String.class)) {
			if (value instanceof Date) {
				try{
				return sdf.format(value).toString(); 
				}catch(Exception e){
					System.out.println("Date format Exception...");
					return null;
				}
			}else
			return value.toString();
		}//默认返回ArrayList 
		else if (field.isAssignableFrom(List.class)) {
			if (value instanceof Collection) {
				List list = new ArrayList();
				list.addAll((Collection)value);
				return list; 
			}
			
		} else if (field.isAssignableFrom(Map.class)) {
			// 需手动配置
		} else if (field.isAssignableFrom(Set.class)) {
			// 需手动配置
		} else if (field.isAssignableFrom(boolean.class)||field.isAssignableFrom(Boolean.class)) {
			if (value instanceof Integer) {
				if ((Integer) value == 0) {
					return false;
				} else if ((Integer) value == 1) {
					return true;
				}
			} else if (value instanceof String
					&& StringUtils.isNotEmpty(value.toString())) {
				if (value.toString().equals("false")) {
					return false;
				} else if (value.toString().equals("true")) {
					return true;
				}
			}
		} else if (field.isAssignableFrom(int.class)||field.isAssignableFrom(Integer.class)) {
			if (value instanceof Integer) {
				return (Integer) value;
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Integer.parseInt(value.toString());
			}
			// 损失精度的不处理
		} else if (field.isAssignableFrom(float.class)||field.isAssignableFrom(Float.class)) {
			if (value instanceof Integer) {
				return Float.parseFloat(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Float.parseFloat(value.toString());
			} else if (value instanceof Float) {
				return value;
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Double) {
				return Float.parseFloat(String.valueOf(value));
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return Float.parseFloat(String.valueOf(value));
			}
		} else if (field.isAssignableFrom(double.class)||field.isAssignableFrom(Double.class)) {
			if (value instanceof Integer) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof Float) {
				return Double.parseDouble(String.valueOf(value));
			} else if (value instanceof Double) {
				return value;
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return Double.parseDouble(String.valueOf(value));
			}

		} else if (field.isAssignableFrom(long.class)||field.isAssignableFrom(Long.class)) {
			if (value instanceof Integer) {
				return Long.parseLong(String.valueOf(value));
			} else if (value instanceof String
					&& StringUtils.isNumeric(value.toString())) {
				return Long.parseLong(String.valueOf(value));
			} else if (value instanceof Float) {
				return (Long) value;
			} else if (value instanceof Double) {
				return Long.parseLong(String.valueOf(value));
			}
			/***
			 * @TODO 损失精度
			 */
			else if (value instanceof Long) {
				return value;
			}

		} else if (field.isAssignableFrom(Date.class)) {
			if (value instanceof Date) {
				return value;
			}else if(value instanceof String){
				return sdf.format(value).toString();	 
			  }
			 
		}
		return null;
		}else{
			return null;
		}
	}

}
