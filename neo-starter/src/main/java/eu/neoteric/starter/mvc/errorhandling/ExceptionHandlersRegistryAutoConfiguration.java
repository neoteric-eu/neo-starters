package eu.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.Sets;
import eu.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
@AutoConfigureBefore(StarterErrorHandlingAutoConfiguration.class)
@ConditionalOnProperty(prefix = "neostarter.mvc.restErrorHandling", value = "enabled", havingValue = "true", matchIfMissing = true)
@Import(ExceptionHandlersRegistryAutoConfiguration.RestExceptionHandlerRegistryRegistrar.class)
public class ExceptionHandlersRegistryAutoConfiguration {

    static class RestExceptionHandlerRegistryRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(RestExceptionHandlerRegistry.class);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            beanDefinition.getConstructorArgumentValues()
                    .addGenericArgumentValue(Sets.newHashSet());
            beanDefinition.setSynthetic(true);
            registry.registerBeanDefinition(RestExceptionHandlerRegistry.BEAN_NAME, beanDefinition);
        }
    }
}
