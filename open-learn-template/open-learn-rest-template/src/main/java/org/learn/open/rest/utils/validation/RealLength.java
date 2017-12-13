package org.learn.open.rest.utils.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={RealLeagthValidator.class})
@Documented
public @interface RealLength {
  public abstract String message();

  public abstract Class<?>[] groups();

  public abstract int min();

  public abstract int max();
}