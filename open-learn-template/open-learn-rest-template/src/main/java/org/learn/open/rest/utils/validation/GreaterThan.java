package org.learn.open.rest.utils.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={GreaterThanValidator.class})
@Documented
public @interface GreaterThan
{
  public abstract String message();

  public abstract Class<?>[] groups();

  public abstract Class<? extends Payload>[] payload();

  public abstract int value();
}