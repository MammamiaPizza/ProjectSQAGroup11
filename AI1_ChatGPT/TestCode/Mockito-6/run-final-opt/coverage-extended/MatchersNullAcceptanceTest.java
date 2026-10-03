import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;

public class MatchersNullAcceptanceTest {

    private interface ReferenceService {
        String stringValue(String value);
        String collectionValue(Collection<?> value);
        String listValue(List<?> value);
        String setValue(Set<?> value);
        String mapValue(Map<?, ?> value);
    }

    private interface PrimitiveWrapperService {
        Boolean booleanValue(Boolean value);
        Byte byteValue(Byte value);
        Character charValue(Character value);
        Integer intValue(Integer value);
        Long longValue(Long value);
        Float floatValue(Float value);
        Double doubleValue(Double value);
        Short shortValue(Short value);
    }

    @Test
    public void anyStringShouldMatchNonNullStringsButNotNull() {
        ReferenceService service = mock(ReferenceService.class);
        when(service.stringValue(Matchers.anyString())).thenReturn("matched");

        assertEquals("matched", service.stringValue("text"));
        assertNull(service.stringValue(null));
    }

    @Test
    public void collectionMatchersShouldMatchTheirNonNullTypesButNotNull() {
        ReferenceService service = mock(ReferenceService.class);

        when(service.collectionValue(Matchers.anyCollection())).thenReturn("collection");
        when(service.listValue(Matchers.anyList())).thenReturn("list");
        when(service.setValue(Matchers.anySet())).thenReturn("set");
        when(service.mapValue(Matchers.anyMap())).thenReturn("map");

        assertEquals("collection", service.collectionValue(new ArrayList<String>()));
        assertEquals("list", service.listValue(new ArrayList<String>()));
        assertEquals("set", service.setValue(new HashSet<String>()));
        assertEquals("map", service.mapValue(Collections.<String, String>singletonMap("key", "value")));

        assertNull(service.collectionValue(null));
        assertNull(service.listValue(null));
        assertNull(service.setValue(null));
        assertNull(service.mapValue(null));
    }

    @Test
    public void primitiveWrapperMatchersShouldMatchNonNullValuesButNotNull() {
        PrimitiveWrapperService service = mock(PrimitiveWrapperService.class);

        when(service.booleanValue(Matchers.anyBoolean())).thenReturn(Boolean.TRUE);
        when(service.byteValue(Matchers.anyByte())).thenReturn(Byte.valueOf((byte) 7));
        when(service.charValue(Matchers.anyChar())).thenReturn(Character.valueOf('x'));
        when(service.intValue(Matchers.anyInt())).thenReturn(Integer.valueOf(17));
        when(service.longValue(Matchers.anyLong())).thenReturn(Long.valueOf(19L));
        when(service.floatValue(Matchers.anyFloat())).thenReturn(Float.valueOf(2.5F));
        when(service.doubleValue(Matchers.anyDouble())).thenReturn(Double.valueOf(3.5D));
        when(service.shortValue(Matchers.anyShort())).thenReturn(Short.valueOf((short) 11));

        assertEquals(Boolean.TRUE, service.booleanValue(Boolean.FALSE));
        assertEquals(Byte.valueOf((byte) 7), service.byteValue(Byte.valueOf((byte) 1)));
        assertEquals(Character.valueOf('x'), service.charValue(Character.valueOf('a')));
        assertEquals(Integer.valueOf(17), service.intValue(Integer.valueOf(1)));
        assertEquals(Long.valueOf(19L), service.longValue(Long.valueOf(1L)));
        assertEquals(Float.valueOf(2.5F), service.floatValue(Float.valueOf(1.0F)));
        assertEquals(Double.valueOf(3.5D), service.doubleValue(Double.valueOf(1.0D)));
        assertEquals(Short.valueOf((short) 11), service.shortValue(Short.valueOf((short) 1)));

        assertEquals(Boolean.FALSE, service.booleanValue(null));
        assertEquals(Byte.valueOf((byte) 0), service.byteValue(null));
        assertEquals(Character.valueOf((char) 0), service.charValue(null));
        assertEquals(Integer.valueOf(0), service.intValue(null));
        assertEquals(Long.valueOf(0L), service.longValue(null));
        assertEquals(Float.valueOf(0.0F), service.floatValue(null));
        assertEquals(Double.valueOf(0.0D), service.doubleValue(null));
        assertEquals(Short.valueOf((short) 0), service.shortValue(null));
    }

@org.junit.Test
public void anyClassMatcherShouldMatchTypedNonNullValuesButNotNull() {
    java.util.Map<Object, String> map = org.mockito.Mockito.mock(java.util.Map.class);

    org.mockito.Mockito.when(map.get(org.mockito.Matchers.any(String.class))).thenReturn("matched");

    org.junit.Assert.assertEquals("matched", map.get("key"));
    org.junit.Assert.assertNull(map.get(null));
}

@org.junit.Test
public void genericAnyMatcherShouldMatchNullAndNonNullValues() {
    java.util.Map<Object, String> map = org.mockito.Mockito.mock(java.util.Map.class);

    org.mockito.Mockito.when(map.get(org.mockito.Matchers.<Object>any())).thenReturn("matched");

    org.junit.Assert.assertEquals("matched", map.get("key"));
    org.junit.Assert.assertEquals("matched", map.get(null));
}

@org.junit.Test
public void genericCollectionMatcherAliasesShouldNotMatchNull() {
    java.util.List<String> list = org.mockito.Mockito.mock(java.util.List.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(list)
            .addAll(org.mockito.Matchers.<String>anyListOf(String.class));
    try {
        list.addAll(new java.util.ArrayList<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    list.addAll(null);

    java.util.Set<String> set = org.mockito.Mockito.mock(java.util.Set.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(set)
            .addAll(org.mockito.Matchers.<String>anySetOf(String.class));
    try {
        set.addAll(new java.util.HashSet<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    set.addAll(null);

    java.util.List<String> collection = org.mockito.Mockito.mock(java.util.List.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(collection)
            .addAll(org.mockito.Matchers.<String>anyCollectionOf(String.class));
    try {
        collection.addAll(new java.util.ArrayList<String>());
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    collection.addAll(null);

    java.util.Map<String, String> map = org.mockito.Mockito.mock(java.util.Map.class);
    org.mockito.Mockito.doThrow(new RuntimeException()).when(map)
            .putAll(org.mockito.Matchers.<String, String>anyMapOf(String.class, String.class));
    try {
        map.putAll(java.util.Collections.singletonMap("key", "value"));
        org.junit.Assert.fail();
    } catch (RuntimeException expected) {
    }
    map.putAll(null);
}
}
