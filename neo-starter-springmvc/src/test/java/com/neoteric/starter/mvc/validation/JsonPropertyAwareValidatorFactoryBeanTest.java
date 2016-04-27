package com.neoteric.starter.mvc.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.*;

public class JsonPropertyAwareValidatorFactoryBeanTest {

    private static final String BEAN_NAME = "bean";
    private static final String INVALID_VALUE = "invalid";
    private static final String JSON_NAME = "jsonName";
    private JsonPropertyAwareValidatorFactoryBean validatorFactoryBean;

    private TestBean bean;
    private BeanPropertyBindingResult errors;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setupSpringValidatorAdapter() {
        validatorFactoryBean = new JsonPropertyAwareValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();
        bean = new TestBean();
        errors = new BeanPropertyBindingResult(bean, BEAN_NAME);
    }

    @Test
    public void classValidationWithoutProperty_ShouldReturnBeanName() {
        validatorFactoryBean.validate(bean, errors, NoPropertyPathValidation.class);

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getGlobalErrorCount()).isEqualTo(1);
        ObjectError error = errors.getGlobalError();
        assertThat(error.getObjectName()).isEqualTo(BEAN_NAME);
        assertThat(error.getCode()).isEqualTo(NoPropertyPath.class.getSimpleName());
        assertThat(error.getDefaultMessage()).isEqualTo("message");
    }

    @Test
    public void classValidationWithWrongFieldName_ShouldThrowIllegalStateException() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(Matchers.contains("xxx"));
        validatorFactoryBean.validate(bean, errors, PropertyPathWrongNameValidation.class);
    }

    @Test
    public void classValidationWithJsonProperty_ShouldThrowIllegalStateException() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(Matchers.contains(JSON_NAME));
        validatorFactoryBean.validate(bean, errors, PropertyPathWithJsonPropertyValidation.class);
    }

    @Test
    public void classValidationWithoutJsonProperty_ShouldReturnJsonPropertyName() throws Exception {
        validatorFactoryBean.validate(bean, errors, PropertyPathWithoutJsonPropertyValidation.class);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrorCount()).isEqualTo(1);
        FieldError fieldError = errors.getFieldError();
        System.out.println(fieldError.getCode());
        assertThat(fieldError.getObjectName()).isEqualTo(BEAN_NAME);
        assertThat(fieldError.getField()).isEqualTo(JSON_NAME);
        assertThat(fieldError.getCode()).isEqualTo(PropertyPathWithoutJsonProperty.class.getSimpleName());
        assertThat(fieldError.getDefaultMessage()).isEqualTo("message");
    }

    @Test
    public void fieldValidationWithoutJsonProperty() throws Exception {
        validatorFactoryBean.validate(bean, errors, FieldWithoutJsonPropertyValidation.class);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrorCount()).isEqualTo(1);
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError.getObjectName()).isEqualTo(BEAN_NAME);
        assertThat(fieldError.getField()).isEqualTo("field");
        assertThat(fieldError.getCode()).isEqualTo(NotNull.class.getSimpleName());
    }

    @Test
    public void fieldValidationWithJsonProperty() throws Exception {
        validatorFactoryBean.validate(bean, errors, FieldWithJsonPropertyValidation.class);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldErrorCount()).isEqualTo(1);
        FieldError fieldError = errors.getFieldError();
        assertThat(fieldError.getObjectName()).isEqualTo(BEAN_NAME);
        assertThat(fieldError.getField()).isEqualTo("jsonName");
        assertThat(fieldError.getCode()).isEqualTo(NotNull.class.getSimpleName());

    }

    @Data
    @NoPropertyPath(groups = NoPropertyPathValidation.class)
    @PropertyPathWrongName(groups = PropertyPathWrongNameValidation.class)
    @PropertyPathWithJsonProperty(groups = PropertyPathWithJsonPropertyValidation.class)
    @PropertyPathWithoutJsonProperty(groups = PropertyPathWithoutJsonPropertyValidation.class)
    public static class TestBean {

        @NotNull(groups = FieldWithoutJsonPropertyValidation.class)
        private String field;

        @NotNull(groups = FieldWithJsonPropertyValidation.class)
        @JsonProperty("jsonName")
        private String fieldName;
    }

    @Constraint(validatedBy = {NoPropertyPathValidator.class})
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    public @interface NoPropertyPath {
        String message() default "message";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class NoPropertyPathValidator implements ConstraintValidator<NoPropertyPath, Object> {
        public void initialize(NoPropertyPath constraintAnnotation) {
        }

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            return false;
        }
    }

    @Constraint(validatedBy = {PropertyPathWrongNameValidator.class})
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    public @interface PropertyPathWrongName {
        String message() default "message";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class PropertyPathWrongNameValidator implements ConstraintValidator<PropertyPathWrongName, Object> {
        public void initialize(PropertyPathWrongName constraintAnnotation) {
        }

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("message").addPropertyNode("xxx").addConstraintViolation();
            return false;
        }
    }


    @Constraint(validatedBy = {PropertyPathWithJsonPropertyValidator.class})
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    public @interface PropertyPathWithJsonProperty {
        String message() default "message";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class PropertyPathWithJsonPropertyValidator implements ConstraintValidator<PropertyPathWithJsonProperty, Object> {
        public void initialize(PropertyPathWithJsonProperty constraintAnnotation) {
        }

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("message").addPropertyNode(JSON_NAME).addConstraintViolation();
            return false;
        }
    }

    @Constraint(validatedBy = {PropertyPathWithoutJsonPropertyValidator.class})
    @Target(ElementType.TYPE)
    @Retention(RUNTIME)
    public @interface PropertyPathWithoutJsonProperty {
        String message() default "message";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class PropertyPathWithoutJsonPropertyValidator implements ConstraintValidator<PropertyPathWithoutJsonProperty, Object> {
        public void initialize(PropertyPathWithoutJsonProperty constraintAnnotation) {
        }

        public boolean isValid(Object value, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("message").addPropertyNode("fieldName").addConstraintViolation();
            return false;
        }
    }

    public interface NoPropertyPathValidation {
    }

    public interface PropertyPathWrongNameValidation {
    }

    public interface PropertyPathWithJsonPropertyValidation {
    }

    public interface PropertyPathWithoutJsonPropertyValidation {
    }

    public interface FieldWithoutJsonPropertyValidation {
    }

    public interface FieldWithJsonPropertyValidation {
    }
}