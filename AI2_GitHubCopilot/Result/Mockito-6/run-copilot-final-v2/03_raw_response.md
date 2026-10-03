package org.mockitousage.matchers;

  import static org.junit.Assert.assertEquals;
  import static org.junit.Assert.assertNull;
  import static org.mockito.Mockito.mock;
  import static org.mockito.Mockito.when;

  import java.util.Collection;
  import java.util.List;
  import java.util.Map;
  import java.util.Set;

  import org.junit.Test;
  import org.mockito.Matchers;

  public class AnyXMatchersNullTest {

      interface TestService {
          String byBoolean(Boolean b);
          String byByte(Byte b);
          String byChar(Character c);
          String byShort(Short s);
          String byInt(Integer i);
          String byLong(Long l);
          String byFloat(Float f);
          String byDouble(Double d);
          String byString(String s);
          String byList(List<?> l);
          String bySet(Set<?> s);
          String byMap(Map<?, ?> m);
          String byCollection(Collection<?> c);
          String byVararg(String... args);
      }

      @Test
      public void shouldNotAcceptNullInAllAnyPrimitiveWrapperMatchers() {
          TestService mock = mock(TestService.class);
          when(mock.byBoolean(Matchers.anyBoolean())).thenReturn("matched");
          when(mock.byByte(Matchers.anyByte())).thenReturn("matched");
          when(mock.byChar(Matchers.anyChar())).thenReturn("matched");
          when(mock.byShort(Matchers.anyShort())).thenReturn("matched");
          when(mock.byInt(Matchers.anyInt())).thenReturn("matched");
          when(mock.byLong(Matchers.anyLong())).thenReturn("matched");
          when(mock.byFloat(Matchers.anyFloat())).thenReturn("matched");
          when(mock.byDouble(Matchers.anyDouble())).thenReturn("matched");

          assertNull("anyBoolean should not match null", mock.byBoolean(null));
          assertNull("anyByte should not match null", mock.byByte(null));
          assertNull("anyChar should not match null", mock.byChar(null));
          assertNull("anyShort should not match null", mock.byShort(null));
          assertNull("anyInt should not match null", mock.byInt(null));
          assertNull("anyLong should not match null", mock.byLong(null));
          assertNull("anyFloat should not match null", mock.byFloat(null));
          assertNull("anyDouble should not match null", mock.byDouble(null));
      }

      @Test
      public void shouldNotAcceptNullInAnyXMatchers() {
          TestService mock = mock(TestService.class);
          when(mock.byString(Matchers.anyString())).thenReturn("matched");
          when(mock.byList(Matchers.anyList())).thenReturn("matched");
          when(mock.bySet(Matchers.anySet())).thenReturn("matched");
          when(mock.byMap(Matchers.anyMap())).thenReturn("matched");
          when(mock.byCollection(Matchers.anyCollection())).thenReturn("matched");

          assertNull("anyString should not match null", mock.byString(null));
          assertNull("anyList should not match null", mock.byList(null));
          assertNull("anySet should not match null", mock.bySet(null));
          assertNull("anyMap should not match null", mock.byMap(null));
          assertNull("anyCollection should not match null", mock.byCollection(null));
      }

      @Test
      public void shouldNotAcceptNullInTypedAnyMatchers() {
          TestService mock = mock(TestService.class);
          when(mock.byList(Matchers.anyListOf(String.class))).thenReturn("matched");
          when(mock.bySet(Matchers.anySetOf(String.class))).thenReturn("matched");
          when(mock.byMap(Matchers.anyMapOf(String.class, String.class))).thenReturn("matched");
          when(mock.byCollection(Matchers.anyCollectionOf(String.class))).thenReturn("matched");

          assertNull("anyListOf should not match null", mock.byList(null));
          assertNull("anySetOf should not match null", mock.bySet(null));
          assertNull("anyMapOf should not match null", mock.byMap(null));
          assertNull("anyCollectionOf should not match null", mock.byCollection(null));
      }

      @Test
      public void shouldMatchNonNullPrimitiveWrapperArguments() {
          TestService mock = mock(TestService.class);
          when(mock.byBoolean(Matchers.anyBoolean())).thenReturn("matched");
          when(mock.byByte(Matchers.anyByte())).thenReturn("matched");
          when(mock.byChar(Matchers.anyChar())).thenReturn("matched");
          when(mock.byShort(Matchers.anyShort())).thenReturn("matched");
          when(mock.byInt(Matchers.anyInt())).thenReturn("matched");
          when(mock.byLong(Matchers.anyLong())).thenReturn("matched");
          when(mock.byFloat(Matchers.anyFloat())).thenReturn("matched");
          when(mock.byDouble(Matchers.anyDouble())).thenReturn("matched");

          assertEquals("anyBoolean should match non-null Boolean", "matched", mock.byBoolean(true));
          assertEquals("anyByte should match non-null Byte", "matched", mock.byByte((byte) 1));
          assertEquals("anyChar should match non-null Character", "matched", mock.byChar('a'));
          assertEquals("anyShort should match non-null Short", "matched", mock.byShort((short) 2));
          assertEquals("anyInt should match non-null Integer", "matched", mock.byInt(3));
          assertEquals("anyLong should match non-null Long", "matched", mock.byLong(4L));
          assertEquals("anyFloat should match non-null Float", "matched", mock.byFloat(5.0f));
          assertEquals("anyDouble should match non-null Double", "matched", mock.byDouble(6.0));
      }

      @Test
      public void shouldMatchNonNullAnyXMatchers() {
          TestService mock = mock(TestService.class);
          when(mock.byString(Matchers.anyString())).thenReturn("matched");
          when(mock.byList(Matchers.anyList())).thenReturn("matched");
          when(mock.bySet(Matchers.anySet())).thenReturn("matched");
          when(mock.byMap(Matchers.anyMap())).thenReturn("matched");
          when(mock.byCollection(Matchers.anyCollection())).thenReturn("matched");

          assertEquals("anyString should match non-null String", "matched", mock.byString("test"));
          assertEquals("anyList should match non-null List", "matched",
 mock.byList(mock(List.class)));
          assertEquals("anySet should match non-null Set", "matched", mock.bySet(mock(Set.class)));
          assertEquals("anyMap should match non-null Map", "matched", mock.byMap(mock(Map.class)));
          assertEquals("anyCollection should match non-null Collection", "matched",
 mock.byCollection(mock(Collection.class)));
      }

      @Test
      public void shouldMatchNonNullTypedAnyMatchers() {
          TestService mock = mock(TestService.class);
          when(mock.byList(Matchers.anyListOf(String.class))).thenReturn("matched");
          when(mock.bySet(Matchers.anySetOf(String.class))).thenReturn("matched");
          when(mock.byMap(Matchers.anyMapOf(String.class, String.class))).thenReturn("matched");
          when(mock.byCollection(Matchers.anyCollectionOf(String.class))).thenReturn("matched");

          assertEquals("anyListOf should match non-null List", "matched",
 mock.byList(mock(List.class)));
          assertEquals("anySetOf should match non-null Set", "matched",
mock.bySet(mock(Set.class)));
          assertEquals("anyMapOf should match non-null Map", "matched",
mock.byMap(mock(Map.class)));
          assertEquals("anyCollectionOf should match non-null Collection", "matched",
 mock.byCollection(mock(Collection.class)));
      }

      @Test
      public void anyShouldMatchNull() {
          TestService mock = mock(TestService.class);
          when(mock.byString(Matchers.any())).thenReturn("matched");
          assertEquals("any() should match null", "matched", mock.byString(null));
      }

      @Test
      public void anyObjectShouldNotMatchNull() {
          TestService mock = mock(TestService.class);
          when(mock.byString(Matchers.anyObject())).thenReturn("matched");
          assertNull("anyObject() should not match null", mock.byString(null));
      }

      @Test
      public void anyClassShouldRejectNull() {
          TestService mock = mock(TestService.class);
          when(mock.byString(Matchers.any(String.class))).thenReturn("matched");
          assertNull("any(Class) should not match null", mock.byString(null));
      }

      @Test
      public void anyVarargShouldMatchNull() {
          TestService mock = mock(TestService.class);
          when(mock.byVararg(Matchers.anyVararg())).thenReturn("matched");
          assertEquals("anyVararg() should match null varargs", "matched", mock.byVararg((String[])
 null));
      }
  }