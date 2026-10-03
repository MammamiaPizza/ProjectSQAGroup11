package org.mockitousage.bugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.exceptions.misusing.MissingMethodInvocationException;

public class StubbingMocksThatAreConfiguredToReturnMocksTest {

 public interface ITest {
     ITest method();
 }

 @Test
 public void shouldAllowStubbingMocksConfiguredWithRETURNS_MOCKS() {
     ITest mock = mock(ITest.class, RETURNS_MOCKS);
     when(mock.method()).thenReturn(mock);
     assertSame(mock, mock.method());
 }

 @Test
 public void shouldReturnMockForUnstubbedCallWhenConfiguredWithRETURNS_MOCKS() {
     ITest mock = mock(ITest.class, RETURNS_MOCKS);
     assertNotNull(mock.method());
 }

 @Test
 public void shouldAllowStubbingReturnsMocksMockToReturnNull() {
     ITest mock = mock(ITest.class, RETURNS_MOCKS);
     when(mock.method()).thenReturn(null);
     assertNull(mock.method());
 }

 @Test
 public void shouldAllowStubbingNormalMock() {
     ITest mock = mock(ITest.class);
     ITest other = mock(ITest.class);
     when(mock.method()).thenReturn(other);
     assertSame(other, mock.method());
 }

 @Test
 public void shouldReturnNullForUnstubbedCallOnNormalMock() {
     ITest mock = mock(ITest.class);
     assertNull(mock.method());
 }

 @Test(expected = MissingMethodInvocationException.class)
 public void shouldThrowMissingMethodInvocationWhenStubbingNonMock() {
     when("not a mock");
 }

}
