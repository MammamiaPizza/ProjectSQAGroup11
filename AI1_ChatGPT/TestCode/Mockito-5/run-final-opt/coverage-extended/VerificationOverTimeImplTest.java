package org.mockito.internal.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Test;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.util.Timer;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.verification.VerificationMode;

public class VerificationOverTimeImplTest {

    @Test
    public void shouldNotContainAnyJUnitClassReferenceInItsBytecode() throws Exception {
        InputStream stream = VerificationOverTimeImpl.class.getResourceAsStream("VerificationOverTimeImpl.class");
        assertNotNull("The verification implementation class file should be available", stream);

        byte[] bytes;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            bytes = output.toByteArray();
        } finally {
            stream.close();
        }

        String classFile = new String(bytes, Charset.forName("ISO-8859-1"));
        assertFalse("VerificationOverTimeImpl must not link to JUnit-specific exception classes",
                classFile.contains("junit/"));
    }

    @Test
    public void shouldExposeConstructorConfiguration() {
        VerificationMode delegate = new SuccessfulVerificationMode();
        VerificationOverTimeImpl verification = new VerificationOverTimeImpl(
                17L, 31L, delegate, true, new ControlledTimer());

        assertEquals(17L, verification.getPollingPeriod());
        assertEquals(31L, verification.getDuration());
        assertSame(delegate, verification.getDelegate());
    }

    @Test
    public void shouldReturnImmediatelyAfterFirstSuccessfulVerificationWhenConfiguredToDoSo() {
        CountingSuccessfulVerificationMode delegate = new CountingSuccessfulVerificationMode();
        ControlledTimer timer = new ControlledTimer(true, true, true);
        VerificationOverTimeImpl verification = new VerificationOverTimeImpl(
                0L, 100L, delegate, true, timer);

        verification.verify(null);

        assertEquals(1, delegate.calls);
    }

    @Test
    public void shouldKeepVerifyingUntilTimeExpiresWhenNotConfiguredToReturnOnSuccess() {
        CountingSuccessfulVerificationMode delegate = new CountingSuccessfulVerificationMode();
        ControlledTimer timer = new ControlledTimer(true, true, false);
        VerificationOverTimeImpl verification = new VerificationOverTimeImpl(
                0L, 100L, delegate, false, timer);

        verification.verify(null);

        assertEquals(2, delegate.calls);
    }

    @Test
    public void shouldRetryRecoverableMockitoAssertionFailureAndSucceedWhenDelegateLaterSucceeds() {
        FailsOnceThenSucceedsVerificationMode delegate = new FailsOnceThenSucceedsVerificationMode();
        ControlledTimer timer = new ControlledTimer(true, true, false);
        VerificationOverTimeImpl verification = new VerificationOverTimeImpl(
                0L, 100L, delegate, true, timer);

        verification.verify(null);

        assertEquals(2, delegate.calls);
    }

    @Test
    public void shouldThrowLastRecoverableFailureWhenTimeExpiresBeforeSuccess() {
        MockitoAssertionError expected = new MockitoAssertionError("not yet");
        AlwaysFailingVerificationMode delegate = new AlwaysFailingVerificationMode(expected);
        ControlledTimer timer = new ControlledTimer(true, false);
        VerificationOverTimeImpl verification = new VerificationOverTimeImpl(
                0L, 100L, delegate, true, timer);

        try {
            verification.verify(null);
        } catch (MockitoAssertionError actual) {
            assertSame(expected, actual);
            assertEquals(1, delegate.calls);
            return;
        }

        throw new AssertionError("Expected the delegate verification failure to be rethrown");
    }

    private static class ControlledTimer extends Timer {
        private final boolean[] countingResults;
        private int index;

        ControlledTimer(boolean... countingResults) {
            super(0L);
            this.countingResults = countingResults;
        }

        @Override
        public void start() {
            index = 0;
        }

        @Override
        public boolean isCounting() {
            return index < countingResults.length && countingResults[index++];
        }
    }

    private static class SuccessfulVerificationMode implements VerificationMode {
        @Override
        public void verify(VerificationData data) {
        }
    }

    private static class CountingSuccessfulVerificationMode implements VerificationMode {
        int calls;

        @Override
        public void verify(VerificationData data) {
            calls++;
        }
    }

    private static class FailsOnceThenSucceedsVerificationMode implements VerificationMode {
        int calls;

        @Override
        public void verify(VerificationData data) {
            calls++;
            if (calls == 1) {
                throw new MockitoAssertionError("not yet");
            }
        }
    }

    private static class AlwaysFailingVerificationMode implements VerificationMode {
        private final MockitoAssertionError failure;
        int calls;

        AlwaysFailingVerificationMode(MockitoAssertionError failure) {
            this.failure = failure;
        }

        @Override
        public void verify(VerificationData data) {
            calls++;
            throw failure;
        }
    }
}
