package org.learn.open.rest.utils.validation;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CollectionValidator implements
		ConstraintValidator<Collection, Integer> {
	private Collection collection;

	public void initialize(Collection constraintAnnotation) {
		this.collection = constraintAnnotation;
	}

	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if (null == value)
			return true;
		int[] allowed = this.collection.values();
		if (Arrays.binarySearch(allowed, value.intValue()) > -1) {
			return true;
		}
		return false;
	}
}
