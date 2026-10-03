package com.google.gson.internal;

 import java.lang.reflect.Type;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.Map;
 import java.util.SortedMap;
 import java.util.TreeMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.ConcurrentNavigableMap;

 import com.google.gson.InstanceCreator;
 import com.google.gson.reflect.TypeToken;

 import junit.framework.TestCase;

 public class ConstructorConstructorTest extends TestCase {

     private ConstructorConstructor createConstructorConstructor() {
         return new ConstructorConstructor(new HashMap<Type, InstanceCreator<?>>());
     }

     public void testConcurrentMapRawType() {
         ConstructorConstructor cc = createConstructorConstructor();
         TypeToken<ConcurrentMap> token = TypeToken.get(ConcurrentMap.class);
         ObjectConstructor<ConcurrentMap> constructor = cc.get(token);
         ConcurrentMap map = constructor.construct();
         assertTrue("Expected ConcurrentMap instance", map instanceof ConcurrentMap);
     }

     public void testConcurrentMapParameterizedType() {
         ConstructorConstructor cc = createConstructorConstructor();
         TypeToken<ConcurrentMap<String, Integer>> token =
                 new TypeToken<ConcurrentMap<String, Integer>>() {};
         ObjectConstructor<ConcurrentMap<String, Integer>> constructor = cc.get(token);
         ConcurrentMap<String, Integer> map = constructor.construct();
         assertTrue("Expected ConcurrentMap instance", map instanceof ConcurrentMap);
     }

     public void testConcurrentNavigableMapRawType() {
         ConstructorConstructor cc = createConstructorConstructor();
         TypeToken<ConcurrentNavigableMap> token = TypeToken.get(ConcurrentNavigableMap.class);
         ObjectConstructor<ConcurrentNavigableMap> constructor = cc.get(token);
         ConcurrentNavigableMap map = constructor.construct();
         assertTrue("Expected ConcurrentNavigableMap instance",
                 map instanceof ConcurrentNavigableMap);
     }

     public void testSortedMapReturnsTreeMap() {
         ConstructorConstructor cc = createConstructorConstructor();
         TypeToken<SortedMap<String, Object>> token =
                 new TypeToken<SortedMap<String, Object>>() {};
         ObjectConstructor<SortedMap<String, Object>> constructor = cc.get(token);
         SortedMap<String, Object> map = constructor.construct();
         assertTrue("Expected TreeMap", map instanceof TreeMap);
     }

     public void testMapReturnsLinkedHashMap() {
         ConstructorConstructor cc = createConstructorConstructor();
         TypeToken<Map<String, Object>> token = new TypeToken<Map<String, Object>>() {};
         ObjectConstructor<Map<String, Object>> constructor = cc.get(token);
         Map<String, Object> map = constructor.construct();
         assertTrue("Expected LinkedHashMap", map instanceof LinkedHashMap);
     }
 }