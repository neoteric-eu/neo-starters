package com.neoteric.starter.rabbit;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.reflect.ClassPath;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Set;

public class SimpleNameTypeMapper extends DefaultJackson2JavaTypeMapper implements Jackson2JavaTypeMapper, ClassMapper {

    private final TopLevelClassesFinder topLevelClassesFinder = new TopLevelClassesFinder();

    @Value("${spring.rabbitmq.packageToScan}")
    private String packageToScan;

    @Override
    public JavaType toJavaType(MessageProperties properties) {
        JavaType classType = getClassIdType(retrieveHeader(properties, getClassIdFieldName()));
        if (!classType.isContainerType() || classType.isArrayType()) {
            return classType;
        }

        JavaType contentClassType = getClassIdType(retrieveHeader(properties, getContentClassIdFieldName()));
        if (classType.getKeyType() == null) {
            return CollectionType.construct(classType.getRawClass(), contentClassType);
        }

        JavaType keyClassType = getClassIdType(retrieveHeader(properties, getKeyClassIdFieldName()));
        return MapType.construct(classType.getRawClass(), keyClassType, contentClassType);
    }

    private JavaType getClassIdType(String classId) {
        if (getIdClassMapping().containsKey(classId)) {
            return TypeFactory.defaultInstance().constructType(getIdClassMapping().get(classId));
        }

        String className = null;

        try {
            className = topLevelClassesFinder.findAvailableClasses().stream()
                    .filter(classInfo -> classInfo.getSimpleName().equals(classId))
                    .map(ClassPath.ClassInfo::getName)
                    .findFirst().orElse(classId);

            return TypeFactory.defaultInstance().constructType(
                    ClassUtils.forName(className, getClass().getClassLoader())
            );

        } catch (ClassNotFoundException e) {
            throw new MessageConversionException("failed to resolve class name. Class not found [" + className + "]", e);
        } catch (LinkageError e) {
            throw new MessageConversionException("failed to resolve class name. Linkage error [" + className + "]", e);
        }
    }

    @Override
    protected void addHeader(MessageProperties properties, String headerName, Class<?> clazz) {
        properties.getHeaders().put(headerName, clazz.getSimpleName());
    }

    class TopLevelClassesFinder {

        private Set<ClassPath.ClassInfo> classesInPackage = null;

        public Set<ClassPath.ClassInfo> findAvailableClasses() {
            if (classesInPackage == null) {
                ClassLoader cl = getClass().getClassLoader();
                try {
                    classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive(packageToScan);
                } catch (IOException e) {
                    throw new MessageConversionException("failed to scan classes: {}", e);
                }
            }
            return classesInPackage;
        }
    }
}
