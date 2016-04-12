package com.neoteric.starter.mvc;

import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.AbstractControllerUrlHandlerMapping;

public class ApiClassNameHandlerMapping extends AbstractControllerUrlHandlerMapping {

    /**
     * Common suffix at the end of controller implementation classes.
     * Removed when generating the URL path.
     */
    private String classSuffix;
    private boolean caseSensitive = false;
    private String pathPrefix;


    public void setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
    }

    /**
     * Set whether to apply case sensitivity to the generated paths,
     * e.g. turning the class name "BuyForm" into "buyForm".
     * <p>Default is "false", using pure lower case paths,
     * e.g. turning the class name "BuyForm" into "buyform".
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Specify a prefix to prepend to the path generated from the controller name.
     * <p>Default is a plain slash ("/"). A path like "/mymodule" can be specified
     * in order to have controller path mappings prefixed with that path, e.g.
     * "/mymodule/buyform" instead of "/buyform" for the class name "BuyForm".
     */
    public void setPathPrefix(String prefixPath) {
        this.pathPrefix = prefixPath;
        if (StringUtils.hasLength(this.pathPrefix)) {
            if (!this.pathPrefix.startsWith("/")) {
                this.pathPrefix = "/" + this.pathPrefix;
            }
            if (this.pathPrefix.endsWith("/")) {
                this.pathPrefix = this.pathPrefix.substring(0, this.pathPrefix.length() - 1);
            }
        }
    }

    @Override
    protected String[] buildUrlsForHandler(String beanName, Class<?> beanClass) {
        return generatePathMappings(beanClass);
    }

    /**
     * Generate the actual URL paths for the given controller class.
     * <p>Subclasses may choose to customize the paths that are generated
     * by overriding this method.
     *
     * @param beanClass the controller bean class to generate a mapping for
     * @return the URL path mappings for the given controller
     */
    protected String[] generatePathMappings(Class<?> beanClass) {
        StringBuilder pathMapping = buildPathPrefix(beanClass);
        String className = ClassUtils.getShortName(beanClass);
        String path = (className.endsWith(classSuffix) ?
                className.substring(0, className.lastIndexOf(classSuffix)) : className);
        if (path.length() > 0) {
            if (this.caseSensitive) {
                pathMapping.append(path.substring(0, 1).toLowerCase()).append(path.substring(1));
            } else {
                pathMapping.append(path.toLowerCase());
            }
        }
        return new String[]{pathMapping.toString() + "/*"};
    }

    /**
     * Build a path prefix for the given controller bean class.
     *
     * @param beanClass the controller bean class to generate a mapping for
     * @return the path prefix, potentially including subpackage names as path elements
     */
    private StringBuilder buildPathPrefix(Class<?> beanClass) {
        StringBuilder pathMapping = new StringBuilder();
        if (this.pathPrefix != null) {
            pathMapping.append(this.pathPrefix);
            pathMapping.append("/");
        } else {
            pathMapping.append("/");
        }
        return pathMapping;
    }
}
