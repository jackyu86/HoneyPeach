package org.learn.open.rest.utils.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GreaterThanValidator implements
		ConstraintValidator<GreaterThan, Integer> {
	private GreaterThan bean;

	public void initialize(GreaterThan bean) {
		this.bean = bean;
	}

	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (null == value) {
			return true;
		}
		int min = this.bean.value();
		if (value.intValue() > min) {
			return true;
		}
		return false;
	}
}
