package org.mockito.internal.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;

public class MockUtilTest {

 private final MockUtil mockUtil = new MockUtil();

 private InvocationListener countListener(final AtomicInteger counter) {
     return new InvocationListener() {
         public void reportInvocation(MethodInvocationReport report) {
             counter.incrementAndGet();
         }
     };
 }

 private <T> T createMockWithListener(Class<T> type, InvocationListener listener) {
     MockSettingsImpl settings = (MockSettingsImpl) Mockito.withSettings();
     if (listener != null) {
         settings.invocationListeners(listener);
     }
     return mockUtil.createMock(type, settings);
 }

 @Test
 public void resetMock_withListener_listenerPreservedAfterReset() {
     AtomicInteger counter = new AtomicInteger();
     InvocationListener listener = countListener(counter);
     List<?> mock = createMockWithListener(List.class, listener);

     mock.toString();
     int beforeReset = counter.get();

     mockUtil.resetMock(mock);

     mock.toString();
     int afterReset = counter.get();

     assertThat("listener should still be active after reset", afterReset,
greaterThan(beforeReset));
 }

 @Test
 public void resetMock_multipleResets_listenerStillFiresEachTime() {
     AtomicInteger counter = new AtomicInteger();
     InvocationListener listener = countListener(counter);
     List<?> mock = createMockWithListener(List.class, listener);

     mockUtil.resetMock(mock);
     mock.toString();
     int afterFirstReset = counter.get();

     mockUtil.resetMock(mock);
     mock.toString();
     int afterSecondReset = counter.get();

     assertThat("listener should fire after each reset", afterSecondReset,
greaterThan(afterFirstReset));
 }

 @Test
 public void resetMock_noListener_noException() {
     List<?> mock = createMockWithListener(List.class, null);
     mockUtil.resetMock(mock);
 }

 @Test(expected = NotAMockException.class)
 public void resetMock_null_throwsNotAMockException() {
     mockUtil.resetMock(null);
 }

 @Test(expected = NotAMockException.class)
 public void resetMock_nonMock_throwsNotAMockException() {
     mockUtil.resetMock(new ArrayList<>());
 }

 @Test
 public void createMock_withListener_listenerFiresOnInvocation() {
     AtomicInteger counter = new AtomicInteger();
     InvocationListener listener = countListener(counter);
     List<?> mock = createMockWithListener(List.class, listener);

     mock.toString();
     assertThat("listener should be notified on invocation", counter.get(), greaterThan(0));
 }

 @Test
 public void createMock_withoutListener_succeeds() {
     MockSettingsImpl settings = (MockSettingsImpl) Mockito.withSettings();
     List<?> mock = mockUtil.createMock(List.class, settings);
     assertThat(mock, notNullValue());
     mockUtil.resetMock(mock);
 }

 @Test
 public void getMockHandler_withMock_returnsHandler() {
     List<?> mock = createMockWithListener(List.class, countListener(new AtomicInteger()));
     Object handler = mockUtil.getMockHandler(mock);
     assertThat(handler, notNullValue());
 }

 @Test(expected = NotAMockException.class)
 public void getMockHandler_null_throwsNotAMockException() {
     mockUtil.getMockHandler(null);
 }

 @Test(expected = NotAMockException.class)
 public void getMockHandler_nonMock_throwsNotAMockException() {
     mockUtil.getMockHandler(new ArrayList<>());
 }

 @Test
 public void isMock_onMock_returnsTrue() {
     List<?> mock = createMockWithListener(List.class, null);
     assertThat(mockUtil.isMock(mock), is(true));
 }

 @Test
 public void isMock_onNullAndNonMock_returnsFalse() {
     assertThat(mockUtil.isMock(null), is(false));
     assertThat(mockUtil.isMock(new ArrayList<>()), is(false));
 }

}
