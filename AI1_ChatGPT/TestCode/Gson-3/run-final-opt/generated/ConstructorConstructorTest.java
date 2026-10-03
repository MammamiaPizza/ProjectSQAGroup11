package com.google.gson.internal;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

public class ConstructorConstructorTest extends TestCase {

  private ConstructorConstructor newConstructorConstructor() {
    return new ConstructorConstructor(new HashMap<Type, InstanceCreator<?>>());
  }

  public void testConstructsConcurrentMapForConcurrentMapType() {
    ConstructorConstructor constructorConstructor = newConstructorConstructor();
    ObjectConstructor<ConcurrentMap<String, Integer>> objectConstructor =
        constructorConstructor.get(new TypeToken<ConcurrentMap<String, Integer>>() {});

    ConcurrentMap<String, Integer> map = objectConstructor.construct();

    assertNotNull(map);
    assertTrue(map instanceof ConcurrentMap);
    assertNull(map.putIfAbsent("key", Integer.valueOf(1)));
    assertEquals(Integer.valueOf(1), map.get("key"));
  }

  public void testConstructsConcurrentNavigableMapForConcurrentNavigableMapType() {
    ConstructorConstructor constructorConstructor = newConstructorConstructor();
    ObjectConstructor<ConcurrentNavigableMap<String, Integer>> objectConstructor =
        constructorConstructor.get(new TypeToken<ConcurrentNavigableMap<String, Integer>>() {});

    ConcurrentNavigableMap<String, Integer> map = objectConstructor.construct();

    assertNotNull(map);
    assertTrue(map instanceof ConcurrentNavigableMap);
    map.put("b", Integer.valueOf(2));
    map.put("a", Integer.valueOf(1));
    assertEquals("a", map.firstKey());
    assertNull(map.putIfAbsent("c", Integer.valueOf(3)));
  }
}
