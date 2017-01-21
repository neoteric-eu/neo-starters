package eu.neoteric.starter.test.clock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FixedClockListenerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final ZoneId DEFAULT_TIMEZONE = TimeZone.getDefault().toZoneId();
    private static final String INSTANT_STRING = "2011-11-11T10:00:00Z";
    private static final Instant DEFAULT_INSTANT;

    static {
        try {
            DEFAULT_INSTANT = Instant.parse(FixedClock.class.getDeclaredMethod("value").getDefaultValue().toString());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Should never happen", e);
        }
    }

    @Mock
    private ApplicationContext applicationContext;

    private FixedClockListener listener = new FixedClockListener();

    @Test
    public void shouldDoNothing_WithoutClassAnnotation_OnBeforeTestClass() throws Exception {
        WithoutFixedClockClassAnnotation instance = new WithoutFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.beforeTestClass(mockTestClassContext(instance));
        assertThat(mockedClock.instant()).isNull();
        assertThat(mockedClock.getZone()).isNull();
    }

    @Test
    public void shouldSetUpMockedClock_OnBeforeTestClass() throws Exception {
        WithFixedClockClassAnnotation instance = new WithFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.beforeTestClass(mockTestClassContext(instance));
        assertThat(mockedClock.instant()).isEqualTo(DEFAULT_INSTANT);
        assertThat(mockedClock.getZone()).isEqualTo(DEFAULT_TIMEZONE);
    }


    @Test
    public void shouldDoNothing_WithoutClassAnnotation_OnBeforeTestMethod() throws Exception {
        WithoutFixedClockClassAnnotation instance = new WithoutFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.beforeTestMethod(mockTestMethodContext(instance, "aMethod"));
        assertThat(mockedClock.instant()).isNull();
        assertThat(mockedClock.getZone()).isNull();
    }

    @Test
    public void shouldThrowException_WhenOnlyMethodIsAnnotated_OnBeforeTestMethod() throws Exception {
        FixedClockOnlyMethodAnnotated instance = new FixedClockOnlyMethodAnnotated();
        expectedException.expect(IllegalStateException.class);
        listener.beforeTestMethod(mockTestMethodContext(instance, "aMethod"));
    }

    @Test
    public void shouldSetUpMockedClock_WithAnnotatedMethodValue_OnBeforeTestMethod() throws Exception {
        WithFixedClockClassAndMethodAnnotation instance = new WithFixedClockClassAndMethodAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.beforeTestMethod(mockTestMethodContext(instance, "aMethod"));
        assertThat(mockedClock.instant()).isEqualTo(Instant.parse(INSTANT_STRING));
        assertThat(mockedClock.getZone()).isEqualTo(DEFAULT_TIMEZONE);
    }

    @Test
    public void shouldDoNothing_WithoutClassAnnotation_OnAfterTestMethod() throws Exception {
        WithoutFixedClockClassAnnotation instance = new WithoutFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.afterTestMethod(mockTestMethodContext(instance, "aMethod"));
        assertThat(mockedClock.instant()).isNull();
        assertThat(mockedClock.getZone()).isNull();
    }

    @Test
    public void shouldThrowException_WhenOnlyMethodIsAnnotated_OnAfterTestMethod() throws Exception {
        FixedClockOnlyMethodAnnotated instance = new FixedClockOnlyMethodAnnotated();
        expectedException.expect(IllegalStateException.class);
        listener.afterTestMethod(mockTestMethodContext(instance, "aMethod"));
    }

    @Test
    public void shouldSetUpMock_WithAnnotatedClassValue_OnAfterTestMethod() throws Exception {
        WithFixedClockClassAndMethodAnnotation instance = new WithFixedClockClassAndMethodAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.afterTestMethod(mockTestMethodContext(instance, "aMethod"));
        assertThat(mockedClock.instant()).isEqualTo(DEFAULT_INSTANT);
        assertThat(mockedClock.getZone()).isEqualTo(DEFAULT_TIMEZONE);
    }

    @Test
    public void shouldDoNothing_WithoutClassAnnotation_OnAfterTestClass() throws Exception {
        WithoutFixedClockClassAnnotation instance = new WithoutFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(mockedClock.getZone()).willReturn(DEFAULT_TIMEZONE);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.afterTestClass(mockTestClassContext(instance));
        assertThat(mockedClock.getZone()).isEqualTo(DEFAULT_TIMEZONE);
    }

    @Test
    public void shouldResetMock_OnAfterTestClass() throws Exception {
        WithFixedClockClassAnnotation instance = new WithFixedClockClassAnnotation();
        Clock mockedClock = mock(Clock.class);
        given(mockedClock.getZone()).willReturn(DEFAULT_TIMEZONE);
        given(applicationContext.getBean(Clock.class)).willReturn(mockedClock);
        listener.afterTestClass(mockTestClassContext(instance));
        assertThat(mockedClock.getZone()).isNull();
    }


    private TestContext mockTestClassContext(Object instance) {
        TestContext testContext = mock(TestContext.class);
        given(testContext.getTestInstance()).willReturn(instance);
        given(testContext.getTestClass()).willReturn((Class) instance.getClass());
        given(testContext.getApplicationContext()).willReturn(this.applicationContext);
        return testContext;
    }

    private TestContext mockTestMethodContext(Object instance, String methodName) throws Exception {
        TestContext testContext = mockTestClassContext(instance);
        given(testContext.getTestMethod()).willReturn(instance.getClass().getDeclaredMethod(methodName));
        return testContext;
    }

    @FixedClock
    static class WithFixedClockClassAnnotation {
        public void aMethod() {
        }
    }

    static class FixedClockOnlyMethodAnnotated {
        @FixedClock
        public void aMethod() {
        }
    }

    static class WithoutFixedClockClassAnnotation {
        public void aMethod() {
        }
    }

    @FixedClock
    static class WithFixedClockClassAndMethodAnnotation {
        @FixedClock(INSTANT_STRING)
        public void aMethod() {
        }
    }
}