package org.learn.open.rest.utils.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationUtils {
	public static <T> StringBuilder validate(T t, Class<?> group) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(t, new Class[] { group });

		StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			sb.append(constraintViolation.getMessage()).append(";");
		}
		return sb;
	}

	public static <T> StringBuilder validate(T t, Class<?>[] groups) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(t, groups);

		StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			sb.append(constraintViolation.getMessage()).append(";");
		}
		return sb;
	}
}