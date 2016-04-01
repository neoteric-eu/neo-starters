package com.neoteric.starter.rabbit;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final class RabbitEntityTypeMapper extends DefaultJackson2JavaTypeMapper implements Jackson2JavaTypeMapper, ClassMapper {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitEntityTypeMapper.class);

    private final StarterRabbitProperties rabbitProperties;
    private final AnnotatedClassesProvider annotatedClassesProvider = new AnnotatedClassesProvider();

    RabbitEntityTypeMapper(StarterRabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    @Override
    public JavaType toJavaType(MessageProperties properties) {
        JavaType classType = getClassType(properties, getEntityIdFieldName(), getClassIdFieldName());
        if (!classType.isContainerType() || classType.isArrayType()) {
            return classType;
        }

        JavaType contentClassType = getClassType(properties, getContentEntityIdFieldName(), getContentClassIdFieldName());
        if (classType.getKeyType() == null) {
            return CollectionType.construct(classType.getRawClass(), contentClassType);
        }

        JavaType keyClassType = getClassIdType(retrieveHeader(properties, getKeyClassIdFieldName()));
        return MapType.construct(classType.getRawClass(), keyClassType, contentClassType);
    }

    @Override
    public void fromJavaType(JavaType javaType, MessageProperties properties) {
        this.addHeader(properties, this.getClassIdFieldName(), javaType.getRawClass());
        if (annotatedClassesProvider.getAnnotatedClassesJavaTypes().containsValue(javaType)) {
            this.addHeader(properties, this.getEntityIdFieldName(), javaType.getRawClass().getAnnotation(RabbitEntity.class).value());
        }

        if (javaType.isContainerType() && !javaType.isArrayType()) {
            addHeader(properties, getContentClassIdFieldName(), javaType.getContentType().getRawClass());
            if (annotatedClassesProvider.getAnnotatedClassesJavaTypes().containsValue(javaType.getContentType())) {
                addHeader(properties, getContentEntityIdFieldName(), javaType.getContentType().getRawClass().getAnnotation(RabbitEntity.class).value());
            }
        }

        if (javaType.getKeyType() != null) {
            addHeader(properties, getKeyClassIdFieldName(), javaType.getKeyType().getRawClass());
        }
    }

    private JavaType getClassType(MessageProperties properties, String entityIdHeader, String classIdHeader) {
        if (properties.getHeaders().containsKey(entityIdHeader)) {
            return getEntityIdType(retrieveHeader(properties, entityIdHeader));

        } else {
            return getClassIdType(retrieveHeader(properties, classIdHeader));
        }
    }

    private JavaType getEntityIdType(String entityId) {
        return Optional.ofNullable(annotatedClassesProvider.getAnnotatedClassesJavaTypes().get(entityId))
                .orElseThrow(() -> new IllegalArgumentException("Type: " + entityId + " is not a valid RabbitMQ entity"));
    }

    private JavaType getClassIdType(String classId) {
        if (getIdClassMapping().containsKey(classId)) {
            return TypeFactory.defaultInstance().constructType(getIdClassMapping().get(classId));
        }

        try {
            return TypeFactory.defaultInstance().constructType(ClassUtils.forName(classId, getClass().getClassLoader()));

        } catch (ClassNotFoundException e) {
            throw new MessageConversionException("failed to resolve class name. Class not found [" + classId + "]", e);
        } catch (LinkageError e) {
            throw new MessageConversionException("failed to resolve class name. Linkage error [" + classId + "]", e);
        }
    }

    private String getEntityIdFieldName() {
        return "__EntityId__";
    }

    private String getContentEntityIdFieldName() {
        return "__ContentEntityId__";
    }

    protected void addHeader(MessageProperties properties, String headerName, String headerValue) {
        properties.getHeaders().put(headerName, headerValue);
    }

    private class AnnotatedClassesProvider {

        private Map<String, JavaType> annotatedClasses;

        private Map<String, JavaType> getAnnotatedClassesJavaTypes() {
            if (annotatedClasses == null) {
                Reflections reflections = new Reflections(rabbitProperties.getPackagesToScan());
                annotatedClasses = reflections.getTypesAnnotatedWith(RabbitEntity.class).stream()
                        .collect(Collectors.toMap(
                                annotatedClass -> annotatedClass.getAnnotation(RabbitEntity.class).value(),
                                annotatedClass -> TypeFactory.defaultInstance().constructType(annotatedClass)));
                LOG.info("Loaded RabbitMQ entites: {}", annotatedClasses);
            }
            return annotatedClasses;
        }
    }

}
