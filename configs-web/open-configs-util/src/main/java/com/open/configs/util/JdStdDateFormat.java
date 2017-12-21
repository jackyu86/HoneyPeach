package com.open.configs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.util.StdDateFormat;

/**
 * 时间格式化类
 * 
 * @author leishouguo
 * 
 */
public class JdStdDateFormat extends SimpleDateFormat {
	private String formatStr;

	public JdStdDateFormat(String formatStr) {
		super(formatStr);
		this.formatStr = formatStr;
	}

	public String getFormatStr() {
		return formatStr;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5497209795386981683L;

	public Date parse(String dateStr) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		try {
			return sdf.parse(dateStr);
		} catch (Exception e) {
			try {
				return StdDateFormat.instance.parse(dateStr);
			} catch (ParseException e1) {
				throw e1;
			}
		}
	}

	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false; // super does class check
		JdStdDateFormat that = (JdStdDateFormat) obj;
		return (formatStr.equals(that.getFormatStr()));
	}
	
	public int hashCode()
    {
        return formatStr.hashCode();
    }
}
