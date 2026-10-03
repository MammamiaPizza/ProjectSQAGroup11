package org.mockito.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;

public class MockUtilResetMockTest {

    @Test
    public void shouldKeepInvocationListenersAfterResetMock() {
        final AtomicInteger reportedInvocations = new AtomicInteger();
        InvocationListener listener = new InvocationListener() {
            public void reportInvocation(MethodInvocationReport report) {
                reportedInvocations.incrementAndGet();
            }
        };

        List mock = Mockito.mock(List.class, Mockito.withSettings().invocationListeners(listener));
        MockUtil mockUtil = new MockUtil();

        mock.clear();
        int invocationsBeforeReset = reportedInvocations.get();

        mockUtil.resetMock(mock);
        mock.clear();

        org.junit.Assert.assertEquals(invocationsBeforeReset + 1, reportedInvocations.get());
    }

    @Test
    public void shouldClearInteractionsAndKeepHandlerAvailableAfterResetMock() {
        List mock = Mockito.mock(List.class);
        MockUtil mockUtil = new MockUtil();

        mock.add("before reset");
        mockUtil.resetMock(mock);

        Mockito.verifyZeroInteractions(mock);
        assertNotNull(mockUtil.getMockHandler(mock));

        mock.add("after reset");
        Mockito.verify(mock).add("after reset");
    }

    @Test
    public void shouldRecognizeMockitoMocksOnly() {
        MockUtil mockUtil = new MockUtil();
        List mock = Mockito.mock(List.class);

        assertTrue(mockUtil.isMock(mock));
        assertFalse(mockUtil.isMock(new Object()));
        assertFalse(mockUtil.isMock(null));
    }

    @Test(expected = NotAMockException.class)
    public void shouldRejectNullWhenGettingMockHandler() {
        new MockUtil().getMockHandler(null);
    }

    @Test(expected = NotAMockException.class)
    public void shouldRejectNonMockWhenGettingMockHandler() {
        new MockUtil().getMockHandler(new Object());
    }
}