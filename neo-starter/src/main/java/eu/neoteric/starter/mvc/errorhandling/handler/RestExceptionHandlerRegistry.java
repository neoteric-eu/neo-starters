package eu.neoteric.starter.mvc.errorhandling.handler;

import lombok.Getter;
import lombok.ToString;

import java.util.Optional;
import java.util.Set;

@ToString
@Getter
public class RestExceptionHandlerRegistry {

    public static final String BEAN_NAME = "restExceptionHandlerRegistry";

    private final Set<ExceptionHandlerBinding> exceptionHandlerBindings;

    public RestExceptionHandlerRegistry(Set<ExceptionHandlerBinding> bindings) {
        this.exceptionHandlerBindings = bindings;
    }

    public Optional<ExceptionHandlerBinding> findBindingFor(Class<? extends Throwable> exceptionClass) {
        if (exceptionHandlerBindings == null || exceptionHandlerBindings.isEmpty()) {
            return Optional.empty();
        }
        int currentDistance = Integer.MAX_VALUE;
        ExceptionHandlerBinding closestBinding = null;
        for (ExceptionHandlerBinding binding : exceptionHandlerBindings) {
            int tempDistance = getDistanceBetweenExceptions(exceptionClass, binding.getExceptionClass());
            if (tempDistance < currentDistance) {
                currentDistance = tempDistance;
                closestBinding = binding;
                if (currentDistance == 0) {
                    break;
                }
            }
        }
        return Optional.ofNullable(closestBinding);
    }

    private int getDistanceBetweenExceptions(Class<?> clazz, Class<?> mapperTypeClazz) {
        if (!mapperTypeClazz.isAssignableFrom(clazz)) {
            return Integer.MAX_VALUE;
        }

        Class<?> superClazz = clazz;
        int distance = 0;
        while (superClazz != mapperTypeClazz) {
            superClazz = superClazz.getSuperclass();
            distance++;
        }
        return distance;
    }
}
