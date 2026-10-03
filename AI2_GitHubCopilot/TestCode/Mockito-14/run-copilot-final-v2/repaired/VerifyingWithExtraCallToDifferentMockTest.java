package org.mockitousage.bugs;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.util.List;
 import java.util.Map;

 import org.junit.Test;

 /**
  * Tests for bug 138: verify(...) fails with an extra call to a different mock
  * inside the verification expression.
  */
 public class VerifyingWithExtraCallToDifferentMockTest {

     /**
      * The exact failing scenario from the bug report.
      * verify(mock1).method(mock2.something()) must not throw.
      */
     @Test
     public void shouldAllowVerifyingWhenOtherMockCallIsInTheSameLine() {
         List<Object> listMock = mock(List.class);
         Map<String, Object> mapMock = mock(Map.class);

         listMock.add(mapMock.get("key"));
         verify(listMock).add(mapMock.get("key"));
     }

     /**
      * Baseline: plain verification without any extra mock calls.
      */
     @Test
     public void shouldVerifyWithoutExtraCalls() {
         @SuppressWarnings("unchecked")
         List<String> mock = mock(List.class);
         mock.add("test");
         verify(mock).add("test");
     }

     /**
      * Extra call on the same mock in the argument position.
      */
     @Test
     public void shouldVerifyWithExtraCallOnSameMock() {
         @SuppressWarnings("unchecked")
         List<Integer> mock = mock(List.class);
         when(mock.size()).thenReturn(42);
         mock.add(mock.size());
         verify(mock).add(mock.size());
     }

     /**
      * Two different other mocks are called inside the verification expression.
      */
     @SuppressWarnings("unchecked")
     @Test
     public void shouldVerifyWithMultipleExtraCallsToDifferentMocks() {
         List<Object> listMock = mock(List.class);
         Map<String, Object> mapMock = mock(Map.class);
         java.util.Set<Object> setMock = mock(java.util.Set.class);

         listMock.add(mapMock.get(setMock.iterator()));
         verify(listMock).add(mapMock.get(setMock.iterator()));
     }

     /**
      * verifyNoMoreInteractions after a verification line that contained an extra call.
      */
     @Test
     public void shouldVerifyNoMoreInteractionsAfterExtraCalls() {
         @SuppressWarnings("unchecked")
         List<String> listMock = mock(List.class);
         @SuppressWarnings("unchecked")
         Map<String, String> mapMock = mock(Map.class);

         listMock.add(mapMock.get("key"));
         verify(listMock).add(mapMock.get("key"));
         verifyNoMoreInteractions(listMock, mapMock);
     }

     /**
      * The mock being verified has never received the expected invocation,
      * but the verification expression still contains an extra call.
      */
     @Test
     public void shouldFailVerificationWhenMockNeverCalledWithExtraCall() {
         @SuppressWarnings("unchecked")
         List<String> listMock = mock(List.class);
         @SuppressWarnings("unchecked")
         Map<String, String> mapMock = mock(Map.class);

         try {
             verify(listMock).add(mapMock.get("key"));
             fail("Should have thrown verification error");
         } catch (AssertionError expected) {
             // expected behavior
         }
     }

     /**
      * Extra call returns a stubbed value and the verification matches the
      * returned value.
      */
     @Test
     public void shouldVerifyWithStubbedExtraCall() {
         @SuppressWarnings("unchecked")
         List<String> listMock = mock(List.class);
         @SuppressWarnings("unchecked")
         Map<String, String> mapMock = mock(Map.class);

         when(mapMock.get("key")).thenReturn("value");

         listMock.add(mapMock.get("key"));
         verify(listMock).add("value");
     }

     /**
      * Extra call returns null (default for mock without stubbing).
      */
     @Test
     public void shouldVerifyWithExtraCallReturningNull() {
         @SuppressWarnings("unchecked")
         List<String> listMock = mock(List.class);
         @SuppressWarnings("unchecked")
         Map<String, String> mapMock = mock(Map.class);

         listMock.add(mapMock.get("key"));
         verify(listMock).add(null);
     }

     /**
      * Extra call is inside an InOrder verification block.
      */
     @SuppressWarnings("unchecked")
     @Test
     public void shouldVerifyInOrderWithExtraCall() {
         List<String> listMock = mock(List.class);
         Map<String, String> mapMock = mock(Map.class);

         listMock.add(mapMock.get("first"));
         listMock.add(mapMock.get("second"));

         org.mockito.InOrder inOrder = inOrder(listMock);
         inOrder.verify(listMock).add(mapMock.get("first"));
         inOrder.verify(listMock).add(mapMock.get("second"));
     }

     /**
      * Extra call uses a real argument captor (or matcher) implicitly.
      * Here we verify with eq() that requires an extra call.
      */
     @SuppressWarnings("unchecked")
     @Test
     public void shouldVerifyWithMatcherDerivedFromExtraCall() {
         List<String> listMock = mock(List.class);
         Map<String, String> mapMock = mock(Map.class);

         String value = "matcherTest";
         when(mapMock.get(anyString())).thenReturn(value);

         listMock.add(mapMock.get("input"));
         verify(listMock).add(eq(mapMock.get("input")));
     }
 }
