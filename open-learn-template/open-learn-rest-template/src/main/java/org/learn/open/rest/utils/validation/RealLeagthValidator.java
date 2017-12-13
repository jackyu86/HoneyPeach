package org.learn.open.rest.utils.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RealLeagthValidator implements
		ConstraintValidator<RealLength, String> {
	private RealLength realLength;

	public void initialize(RealLength arg0) {
		this.realLength = arg0;
	}

	public boolean isValid(String field, ConstraintValidatorContext arg1) {
		if (null == field)
			return true;
		int count = realLength(field);
		if (this.realLength.min() > count)
			return false;
		if (this.realLength.max() < count)
			return false;
		return true;
	}

	private int realLength(String str) {
		int count = str.replaceAll("[^\\x00-\\xff]", "..").length();
		return count;
	}
}