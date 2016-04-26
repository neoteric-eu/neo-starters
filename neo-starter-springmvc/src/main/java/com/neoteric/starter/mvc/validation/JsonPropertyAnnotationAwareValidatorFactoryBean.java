package com.neoteric.starter.mvc.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.ParameterNameProvider;
import javax.validation.Path;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

@Slf4j
public class JsonPropertyAnnotationAwareValidatorFactoryBean extends LocalValidatorFactoryBean {

    @Override
    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String originalField = determineField(violation);
            String field = determineFieldWithJsonProperty(violation);
            if (StringUtils.isEmpty(field)) {
                field = originalField;
            }
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
                    if (errors instanceof BindingResult) {
                        // Can do custom FieldError registration with invalid value from ConstraintViolation,
                        // as necessary for Hibernate Validator compatibility (non-indexed set path in field)
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if ("".equals(nestedField)) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            bindingResult.addError(new ObjectError(
                                    errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
                        } else {
                            Object rejectedValue = getRejectedValue(field, originalField, violation, bindingResult);
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
                            bindingResult.addError(new FieldError(
                                    errors.getObjectName(), nestedField, rejectedValue, false,
                                    errorCodes, errorArgs, violation.getMessage()));
                        }
                    } else {
                        // got no BindingResult - can only do standard rejectValue call
                        // with automatic extraction of the current field value
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                } catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field +
                            "' does not have a corresponding accessor for Spring data binding - " +
                            "check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

    private Object getRejectedValue(String field, String originalField, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        if (!"".equals(field) && (invalidValue == violation.getLeafBean() ||
                (field.contains(".") && !field.contains("[]")))) {
            // Possibly a bean constraint with property path: retrieve the actual property value.
            // However, explicitly avoid this for "address[]" style paths that we can't handle.
            invalidValue = bindingResult.getRawFieldValue(originalField);
        }
        return invalidValue;
    }

    private String determineFieldWithJsonProperty(ConstraintViolation<Object> violation) {
        Class<?> declaringClass = violation.getRootBeanClass();

        StringBuilder fixedPath = new StringBuilder();
        Path propPath = violation.getPropertyPath();
        boolean addDot = false;
        for (Path.Node node : propPath) {
            Field field;
            if (StringUtils.isEmpty(node.getName())) {
                continue;
            }
            try {
                field = declaringClass.getDeclaredField(node.getName());
                if (node.getIndex() != null) {
                    fixedPath.append('[');
                    fixedPath.append(node.getIndex());
                    fixedPath.append(']');
                }
                if (addDot) {
                    fixedPath.append('.');
                }
                addDot = true;
                fixedPath.append(extractName(field));
            } catch (NoSuchFieldException e) {
                LOG.trace("Unable to find field {} in {} class. Returning original property path.", node.getName(), declaringClass, e);
                return Joiner.on(" ").join(violation.getPropertyPath(), violation.getMessage());
            }
            declaringClass = determineClass(field);
        }

        return fixedPath.toString();
    }

    private Class<?> determineClass(Field field) {
        if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
            ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
            return (Class<?>) stringListType.getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    private String extractName(Field field) {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            LOG.trace("Using jsonProperty from JsonProperty annotation for field [{}]", field.getName());
            return jsonProperty.value();
        }

        LOG.trace("No relevant annotations found on field [{}], returning it's normal name.", field.getName());
        return field.getName();
    }

    @Override
    public ExecutableValidator forExecutables() {
        return null;
    }

    @Override
    public ParameterNameProvider getParameterNameProvider() {
        return null;
    }

}
