package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import junit.framework.TestCase;

public class ConstructorConstructorTest extends TestCase {

  private enum Day {
    MONDAY,
    TUESDAY
  }

  private static final class PrivateNoArg {
    final String value;

    private PrivateNoArg() {
      value = "constructed";
    }
  }

  private static final class ThrowingNoArg {
    public ThrowingNoArg() {
      throw new IllegalStateException("constructor failure");
    }
  }

  private static final class OnlyParameterizedConstructor {
    int number;

    OnlyParameterizedConstructor(int number) {
      this.number = number;
    }
  }

  private ConstructorConstructor newConstructorConstructor() {
    return new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap());
  }

  public void testExactTypeInstanceCreatorHasPriorityAndReceivesRequestedType() {
    final TypeToken<List<String>> token = new TypeToken<List<String>>() {};
    final List<String> expected = new ArrayList<String>();
    final Type[] receivedType = new Type[1];

    Map<Type, InstanceCreator<?>> creators = new HashMap<Type, InstanceCreator<?>>();
    creators.put(token.getType(), new InstanceCreator<List<String>>() {
      @Override
      public List<String> createInstance(Type type) {
        receivedType[0] = type;
        return expected;
      }
    });
    creators.put(List.class, new InstanceCreator<List<String>>() {
      @Override
      public List<String> createInstance(Type type) {
        fail("The exact type creator must be selected before the raw type creator");
        return null;
      }
    });

    ObjectConstructor<List<String>> constructor =
        new ConstructorConstructor(creators).get(token);

    assertSame(expected, constructor.construct());
    assertSame(token.getType(), receivedType[0]);
  }

  public void testRawTypeInstanceCreatorReceivesParameterizedRequestedType() {
    final TypeToken<List<String>> token = new TypeToken<List<String>>() {};
    final List<String> expected = new ArrayList<String>();
    final Type[] receivedType = new Type[1];

    Map<Type, InstanceCreator<?>> creators = new HashMap<Type, InstanceCreator<?>>();
    creators.put(List.class, new InstanceCreator<List<String>>() {
      @Override
      public List<String> createInstance(Type type) {
        receivedType[0] = type;
        return expected;
      }
    });

    ObjectConstructor<List<String>> constructor =
        new ConstructorConstructor(creators).get(token);

    assertSame(expected, constructor.construct());
    assertSame(token.getType(), receivedType[0]);
  }

  public void testNoArgConstructorCanBePrivateAndIsInvoked() {
    ObjectConstructor<PrivateNoArg> constructor =
        newConstructorConstructor().get(new TypeToken<PrivateNoArg>() {});

    PrivateNoArg result = constructor.construct();

    assertNotNull(result);
    assertEquals("constructed", result.value);
  }

  public void testExceptionThrownByNoArgConstructorIsWrappedWithOriginalCause() {
    ObjectConstructor<ThrowingNoArg> constructor =
        newConstructorConstructor().get(new TypeToken<ThrowingNoArg>() {});

    try {
      constructor.construct();
      fail("Expected constructor exception to be wrapped");
    } catch (RuntimeException expected) {
      assertTrue(expected.getCause() instanceof IllegalStateException);
      assertEquals("constructor failure", expected.getCause().getMessage());
    }
  }

  public void testCollectionInterfacesUseExpectedDefaultImplementations() {
    ConstructorConstructor constructorConstructor = newConstructorConstructor();

    ObjectConstructor<SortedSet<String>> sortedSetConstructor =
        constructorConstructor.get(new TypeToken<SortedSet<String>>() {});
    ObjectConstructor<EnumSet<Day>> enumSetConstructor =
        constructorConstructor.get(new TypeToken<EnumSet<Day>>() {});
    ObjectConstructor<Set<String>> setConstructor =
        constructorConstructor.get(new TypeToken<Set<String>>() {});
    ObjectConstructor<Queue<String>> queueConstructor =
        constructorConstructor.get(new TypeToken<Queue<String>>() {});
    ObjectConstructor<Collection<String>> collectionConstructor =
        constructorConstructor.get(new TypeToken<Collection<String>>() {});

    assertTrue(sortedSetConstructor.construct() instanceof TreeSet);
    assertEquals(EnumSet.noneOf(Day.class), enumSetConstructor.construct());
    assertTrue(setConstructor.construct() instanceof LinkedHashSet);
    assertTrue(queueConstructor.construct() instanceof LinkedList);
    assertTrue(collectionConstructor.construct() instanceof ArrayList);
  }

  public void testRawEnumSetTypeIsRejected() {
    ObjectConstructor<EnumSet> constructor =
        newConstructorConstructor().get(new TypeToken<EnumSet>() {});

    try {
      constructor.construct();
      fail("A raw EnumSet does not provide the enum element type");
    } catch (JsonIOException expected) {
      assertTrue(expected.getMessage().startsWith("Invalid EnumSet type:"));
    }
  }

  public void testMapInterfacesUseExpectedDefaultImplementations() {
    ConstructorConstructor constructorConstructor = newConstructorConstructor();

    ObjectConstructor<SortedMap<String, Integer>> sortedMapConstructor =
        constructorConstructor.get(new TypeToken<SortedMap<String, Integer>>() {});
    ObjectConstructor<Map<Integer, String>> nonStringKeyMapConstructor =
        constructorConstructor.get(new TypeToken<Map<Integer, String>>() {});
    ObjectConstructor<Map<String, Integer>> stringKeyMapConstructor =
        constructorConstructor.get(new TypeToken<Map<String, Integer>>() {});

    assertTrue(sortedMapConstructor.construct() instanceof TreeMap);
    assertTrue(nonStringKeyMapConstructor.construct() instanceof LinkedHashMap);
    assertTrue(stringKeyMapConstructor.construct() instanceof LinkedTreeMap);
  }

  public void testConcurrentMapUsesConcurrentImplementation() {
    ObjectConstructor<ConcurrentMap<String, Integer>> constructor =
        newConstructorConstructor().get(new TypeToken<ConcurrentMap<String, Integer>>() {});

    Object result = constructor.construct();

    assertTrue(
        "ConcurrentMap requests must not be constructed as a non-concurrent Map",
        result instanceof ConcurrentMap);
  }

  public void testConcurrentNavigableMapUsesConcurrentNavigableImplementation() {
    ObjectConstructor<ConcurrentNavigableMap<String, Integer>> constructor =
        newConstructorConstructor().get(
            new TypeToken<ConcurrentNavigableMap<String, Integer>>() {});

    Object result = constructor.construct();

    assertTrue(
        "ConcurrentNavigableMap requests must not be constructed as a TreeMap",
        result instanceof ConcurrentNavigableMap);
  }

  public void testClassWithoutNoArgConstructorCanBeAllocated() {
    ObjectConstructor<OnlyParameterizedConstructor> constructor =
        newConstructorConstructor().get(new TypeToken<OnlyParameterizedConstructor>() {});

    OnlyParameterizedConstructor result = constructor.construct();

    assertNotNull(result);
    assertEquals(0, result.number);
  }

  public void testToStringDelegatesToInstanceCreatorMap() {
    Map<Type, InstanceCreator<?>> creators = new HashMap<Type, InstanceCreator<?>>();
    creators.put(String.class, new InstanceCreator<String>() {
      @Override
      public String createInstance(Type type) {
        return "value";
      }
    });

    ConstructorConstructor constructorConstructor = new ConstructorConstructor(creators);

    assertEquals(creators.toString(), constructorConstructor.toString());
  }
}
