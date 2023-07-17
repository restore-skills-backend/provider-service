package com.restore.providerservice.validator;

import com.restore.providerservice.validator.impl.ValidateAvailabilityImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidateAvailabilityImpl.class})
public @interface ValidateAvailability {
    String message() default "Error draft";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
