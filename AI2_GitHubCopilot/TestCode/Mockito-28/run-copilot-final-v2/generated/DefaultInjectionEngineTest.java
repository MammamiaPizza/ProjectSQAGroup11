package org.mockito.internal.configuration;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.mockito.InjectMocks;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DefaultInjectionEngineTest {

 private static Set<Field> getInjectMocksFields(Class<?> clazz) {
     Set<Field> fields = new HashSet<Field>();
     for (Field f : clazz.getDeclaredFields()) {
         if (f.isAnnotationPresent(InjectMocks.class)) {
             f.setAccessible(true);
             fields.add(f);
         }
     }
     return fields;
 }

 // -- test fixtures --

 static class TargetExactAndAncestor {
     Set<?> exactSet;
     Collection<?> ancestorCollection;
 }

 static class ContainerExactAndAncestor {
     @InjectMocks
     TargetExactAndAncestor target;
 }

 static class TargetOnlyAncestor {
     Collection<?> ancestorCollection;
 }

 static class ContainerOnlyAncestor {
     @InjectMocks
     TargetOnlyAncestor target;
 }

 static class TargetTwoExact {
     Set<?> firstSet;
     Set<?> secondSet;
 }

 static class ContainerTwoExact {
     @InjectMocks
     TargetTwoExact target;
 }

 static class TargetNoMatching {
     String name;
 }

 static class ContainerNoMatching {
     @InjectMocks
     TargetNoMatching target;
 }

 static class TargetComplex {
     TreeSet<?> treeSet;
     HashSet<?> hashSet;
     Set<?> set;
     Collection<?> collection;
 }

 static class ContainerComplex {
     @InjectMocks
     TargetComplex target;
 }

 // -- tests --

 @Test
 public void shouldInjectIntoExactTypeFieldWhenBothExactAndAncestorPresent() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerExactAndAncestor testInstance = new ContainerExactAndAncestor();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     Set<Object> mocks = new HashSet<Object>();
     HashSet<Object> mock = new HashSet<Object>();
     mocks.add(mock);
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertSame(mock, testInstance.target.exactSet);
     assertNull(testInstance.target.ancestorCollection);
 }

 @Test
 public void shouldInjectIntoAncestorFieldWhenNoExactTypeField() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerOnlyAncestor testInstance = new ContainerOnlyAncestor();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     HashSet<Object> mock = new HashSet<Object>();
     Set<Object> mocks = new HashSet<Object>();
     mocks.add(mock);
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertSame(mock, testInstance.target.ancestorCollection);
 }

 @Test
 public void shouldInjectIntoFirstExactFieldWhenMultipleExactTypeFields() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerTwoExact testInstance = new ContainerTwoExact();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     HashSet<Object> mock = new HashSet<Object>();
     Set<Object> mocks = new HashSet<Object>();
     mocks.add(mock);
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertSame(mock, testInstance.target.firstSet);
     assertNull(testInstance.target.secondSet);
 }

 @Test
 public void shouldNotInjectWhenNoMatchingTypeField() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerNoMatching testInstance = new ContainerNoMatching();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     HashSet<Object> mock = new HashSet<Object>();
     Set<Object> mocks = new HashSet<Object>();
     mocks.add(mock);
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertNull(testInstance.target.name);
 }

 @Test
 public void shouldDoNothingWhenMocksEmpty() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerExactAndAncestor testInstance = new ContainerExactAndAncestor();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     Set<Object> mocks = new HashSet<Object>();
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertNull(testInstance.target.exactSet);
     assertNull(testInstance.target.ancestorCollection);
 }

 @Test
 public void shouldInjectIntoMostSpecificTypeInComplexHierarchy() throws Exception {
     DefaultInjectionEngine engine = new DefaultInjectionEngine();
     ContainerComplex testInstance = new ContainerComplex();
     Set<Field> injectMocksFields = getInjectMocksFields(testInstance.getClass());
     TreeSet<Object> mock = new TreeSet<Object>();
     Set<Object> mocks = new HashSet<Object>();
     mocks.add(mock);
     engine.injectMocksOnFields(injectMocksFields, mocks, testInstance);
     assertSame(mock, testInstance.target.treeSet);
     assertNull(testInstance.target.hashSet);
     assertNull(testInstance.target.set);
     assertNull(testInstance.target.collection);
 }

}
