package eu.neoteric.starter.mvc.format;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

public class StarterDefaultFormattingConversionService extends FormattingConversionService {

    private static final boolean jsr354Present = ClassUtils.isPresent(
            "javax.money.MonetaryAmount", StarterDefaultFormattingConversionService.class.getClassLoader());

    private static final boolean jsr310Present = ClassUtils.isPresent(
            "java.time.LocalDate", StarterDefaultFormattingConversionService.class.getClassLoader());

    private static final boolean jodaTimePresent = ClassUtils.isPresent(
            "org.joda.time.LocalDate", StarterDefaultFormattingConversionService.class.getClassLoader());


    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and
     * {@linkplain #addDefaultFormatters default formatters}.
     */
    public StarterDefaultFormattingConversionService() {
        this(null, true);
    }

    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
     * based on the value of {@code registerDefaultFormatters}, the set of
     * {@linkplain #addDefaultFormatters default formatters}.
     *
     * @param registerDefaultFormatters whether to register default formatters
     */
    public StarterDefaultFormattingConversionService(boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }

    /**
     * Create a new {@code DefaultFormattingConversionService} with the set of
     * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
     * based on the value of {@code registerDefaultFormatters}, the set of
     * {@linkplain #addDefaultFormatters default formatters}
     *
     * @param embeddedValueResolver     delegated to {@link #setEmbeddedValueResolver(StringValueResolver)}
     *                                  prior to calling {@link #addDefaultFormatters}.
     * @param registerDefaultFormatters whether to register default formatters
     */
    public StarterDefaultFormattingConversionService(StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
        setEmbeddedValueResolver(embeddedValueResolver);
        DefaultConversionService.addDefaultConverters(this);
        if (registerDefaultFormatters) {
            addDefaultFormatters(this);
        }
    }


    /**
     * Add formatters appropriate for most environments: including number formatters,
     * JSR-354 Money & Currency formatters, JSR-310 Date-Time and/or Joda-Time formatters,
     * depending on the presence of the corresponding API on the classpath.
     *
     * @param formatterRegistry the service to register default formatters with
     */
    public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
        // Default handling of number values
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

        // Default handling of monetary values
        if (jsr354Present) {
            formatterRegistry.addFormatter(new CurrencyUnitFormatter());
            formatterRegistry.addFormatter(new MonetaryAmountFormatter());
            formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
        }

        // Default handling of date-time values
        if (jsr310Present) {
            // just handling JSR-310 specific date and time types
            DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
            dateTimeFormatterRegistrar.setUseIsoFormat(true);
            dateTimeFormatterRegistrar.registerFormatters(formatterRegistry);
        }
        if (jodaTimePresent) {
            // handles Joda-specific types as well as Date, Calendar, Long
            new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        } else {
            // regular DateFormat-based Date, Calendar, Long converters
            new DateFormatterRegistrar().registerFormatters(formatterRegistry);
        }
    }
}
