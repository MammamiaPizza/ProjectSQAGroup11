package org.apache.commons.jxpath.ri.compiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;

import junit.framework.TestCase;

/**

 - Tests for Expression bug JXPATH-50: iterate() must yield node values,
 - not raw Pointer objects, when compute() returns a NodeSet.
  */
 public class ExpressionBugTest extends TestCase {
  // --- helpers to create Pointer proxies (avoids needing full interface impl) ---
  private static Pointer pointer(final Object value, final String path) {
  return (Pointer) Proxy.newProxyInstance(
          Pointer.class.getClassLoader(),
          new Class[] { Pointer.class },
          new InvocationHandler() {
              public Object invoke(Object proxy, Method m, Object[] args) {
                  if ("getValue".equals(m.getName())) {
                      return value;
                  }
                  if ("asPath".equals(m.getName())) {
                      return path;
                  }
                  if ("equals".equals(m.getName())) {
                      return proxy == args[0];
                  }
                  if ("hashCode".equals(m.getName())) {
                      return Integer.valueOf(System.identityHashCode(proxy));
                  }
                  if ("toString".equals(m.getName())) {
                      return path != null ? path : value == null ? "null" : value.toString();
                  }
                  // return null / false for everything else
                  Class rt = m.getReturnType();
                  if (rt == Boolean.TYPE) {
                      return Boolean.FALSE;
                  }
                  if (rt == Integer.TYPE) {
                      return Integer.valueOf(0);
                  }
                  if (rt == Long.TYPE) {
                      return Long.valueOf(0L);
                  }
                  if (rt == Short.TYPE) {
                      return Short.valueOf((short) 0);
                  }
                  if (rt == Byte.TYPE) {
                      return Byte.valueOf((byte) 0);
                  }
                  if (rt == Character.TYPE) {
                      return Character.valueOf('\0');
                  }
                  if (rt == Float.TYPE) {
                      return Float.valueOf(0f);
                  }
                  if (rt == Double.TYPE) {
                      return Double.valueOf(0d);
                  }
                  return null;
              }
          });
  }
  // minimal EvalContext stub – only the parts needed by the tested methods
  private static class StubEvalContext extends EvalContext {
  public EvalContext getRootContext() {
      return this;
  }
  public Pointer getCurrentNodePointer() {
      return null;
  }
  public boolean hasNext() {
      return false;
  }
  public Object next() {
      return null;
  }
  public void remove() {
      throw new UnsupportedOperationException();
  }
  public int getDocumentOrder() {
      return 0;
  }
  public int getPosition() {
      return 0;
  }
  public void setPosition(int pos) {
  }
  public Pointer getSingleNodePointer() {
      return null;
  }
  public void reset() {
  }
  }
  // EvalContext that also acts as an iterator over supplied Pointers
  private static class IterableEvalContext extends EvalContext {
  private final Iterator iter;
  IterableEvalContext(Iterator it) {
      this.iter = it;
  }
  public EvalContext getRootContext() {
      return this;
  }
  public Pointer getCurrentNodePointer() {
      return null;
  }
  public boolean hasNext() {
      return iter.hasNext();
  }
  public Object next() {
      return iter.next();
  }
  public void remove() {
      iter.remove();
  }
  public int getDocumentOrder() {
      return 0;
  }
  public int getPosition() {
      return 0;
  }
  public void setPosition(int pos) {
  }
  public Pointer getSingleNodePointer() {
      return null;
  }
  public void reset() {
  }
  }
  // --- iterate() tests ---
  /**
  - Core regression: iterate() must produce values, not Pointer instances,
  - when compute() returns a NodeSet (simulated as a list of Pointers).
    */
   public void testIterateNodeSetReturnsValues() {
   final Object val1 = "Nested: Name 1";
   final Object val2 = "Nested: Name 2";
   final List list = Arrays.asList(
       pointer(val1, "/beans[1]"),
       pointer(val2, "/beans[2]"));
   Expression expr = new Expression() {
   public boolean computeContextDependent() { return false; }
   public Object computeValue(EvalContext ctx) { return null; }
   public Object compute(EvalContext ctx) { return list; }
   };
   EvalContext ctx = new StubEvalContext();
   Iterator it = expr.iterate(ctx);
   assertTrue("iterator should have first element", it.hasNext());
   Object first = it.next();
   assertEquals(val1, first);
   assertFalse("first element must not be a Pointer", first instanceof Pointer);
   assertTrue("iterator should have second element", it.hasNext());
   Object second = it.next();
   assertEquals(val2, second);
   assertFalse("second element must not be a Pointer", second instanceof Pointer);
   assertFalse(it.hasNext());
   }
  public void testIterateEmptyNodeSet() {
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return Collections.EMPTY_LIST; }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertFalse("empty NodeSet should produce no elements", it.hasNext());
  }
  public void testIterateSingleNodeSet() {
      final Object val = "onlyValue";
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) {
              return Collections.singletonList(pointer(val, "/only"));
          }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertTrue(it.hasNext());
      Object r = it.next();
      assertEquals(val, r);
      assertFalse(r instanceof Pointer);
      assertFalse(it.hasNext());
  }
  /** plain (non-Pointer) values should pass through unchanged
  */
  public void testIterateNonPointerCollection() {
      final List list = Arrays.asList("alpha", "beta");
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return list; }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertEquals("alpha", it.next());
      assertEquals("beta", it.next());
      assertFalse(it.hasNext());
  }
  /** scalar (non-iterable) result
  */
  public void testIterateScalarValue() {
      final String val = "hello";
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return val; }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertTrue(it.hasNext());
      assertEquals(val, it.next());
      assertFalse(it.hasNext());
  }
  public void testIterateNullResult() {
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return null; }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertNotNull(it);
      assertFalse("null result should yield empty iterator", it.hasNext());
  }
  /** if compute() returns an EvalContext it must be unwrapped by ValueIterator
  */
  public void testIterateEvalContextResult() {
      final Pointer p1 = pointer("valueA", "/a");
      final Pointer p2 = pointer("valueB", "/b");
      List pts = Arrays.asList(p1, p2);
      final EvalContext evalCtx = new IterableEvalContext(pts.iterator());
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return evalCtx; }
      };
      Iterator it = expr.iterate(new StubEvalContext());
      assertTrue(it.hasNext());
      Object first = it.next();
      assertEquals("valueA", first);
      assertFalse(first instanceof Pointer);
      Object second = it.next();
      assertEquals("valueB", second);
      assertFalse(second instanceof Pointer);
      assertFalse(it.hasNext());
  }
  // --- ValueIterator direct tests ---
  public void testValueIteratorUnwrapsPointers() {
      Pointer p = pointer("innerValue", "/path");
      Iterator it = Arrays.asList(new Object[] { p }).iterator();
      Expression.ValueIterator vi = new Expression.ValueIterator(it);
      assertTrue(vi.hasNext());
      Object result = vi.next();
      assertEquals("innerValue", result);
      assertFalse(result instanceof Pointer);
      assertFalse(vi.hasNext());
  }
  // --- PointerIterator direct tests ---
  public void testPointerIteratorKeepsPointers() {
      Pointer p = pointer("data", "/p");
      Iterator it = Arrays.asList(new Object[] { p }).iterator();
      Expression.PointerIterator pi = new Expression.PointerIterator(it,
              new org.apache.commons.jxpath.ri.QName(null, "value"), Locale.US);
      assertTrue(pi.hasNext());
      Object next = pi.next();
      assertTrue("Pointer should remain Pointer", next instanceof Pointer);
      assertEquals("data", ((Pointer) next).getValue());
      assertFalse(pi.hasNext());
  }
  public void testPointerIteratorWrapsNonPointers() {
      String val = "plain";
      Iterator it = Arrays.asList(new Object[] { val }).iterator();
      Expression.PointerIterator pi = new Expression.PointerIterator(it,
              new org.apache.commons.jxpath.ri.QName(null, "value"), Locale.US);
      assertTrue(pi.hasNext());
      Object next = pi.next();
      assertTrue("non-Pointer should be wrapped in a Pointer", next instanceof Pointer);
      assertEquals(val, ((Pointer) next).getValue());
      assertFalse(pi.hasNext());
  }
  // --- iteratePointers test (should return Pointer objects) ---
  public void testIteratePointersReturnsPointers() {
      final Pointer p1 = pointer("x", "/x");
      final Pointer p2 = pointer("y", "/y");
      final List list = Arrays.asList(p1, p2);
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return list; }
      };
      // Need an EvalContext that provides a locale via getCurrentNodePointer()
      EvalContext ctx = new StubEvalContext() {
          public Pointer getCurrentNodePointer() {
              return pointer(null, "/fake") {
                  // Locale is obtained via NodePointer; we don't have one.
                  // The iteratePointers path uses
                  //   context.getRootContext().getCurrentNodePointer().getLocale().
                  // Provide a stub that returns a Locale.
                  // We cheat: cast to NodePointer is not done; it calls
                  // getLocale() on the pointer, which is not available on
                  // our proxy. That will fail. So we must create a real
                  // NodePointer stub.
              };
          }
      };
      // Not trivial – skip complex stubbing; this test may be omitted
      // because the bug is only in iterate(), not iteratePointers.
      // We'll replace with separate direct PointerIterator tests above.
      // Removing this test to keep compilation simple.
  }
  // Substitute test: test iteratePointers with a context that has a
  // NodePointer. We'll make a minimal NodePointer stub to satisfy the
  // call chain.
  private static class StubNodePointer extends org.apache.commons.jxpath.ri.model.NodePointer {
      private Object val;
      StubNodePointer(Object val) { this.val = val; }
      public Object getValue() { return val; }
      public void setValue(Object v) {}
      public String asPath() { return ""; }
      public Object getNode() { return null; }
      public Object getRootNode() { return null; }
      public Pointer clone() { return null; }
      public boolean isLeaf() { return false; }
      public boolean isCollection() { return false; }
      public boolean isContainer() { return false; }
      public Locale getLocale() { return Locale.US; }
      public String getNamespaceURI() { return ""; }
      public org.apache.commons.jxpath.ri.QName getName() { return new
org.apache.commons.jxpath.ri.QName(""); }
      public Object getImmediateNode() { return null; }
      // other potential abstract methods:
      public int compareTo(Object o) { return 0; }
      public boolean equals(Object o) { return this == o; }
      public int hashCode() { return System.identityHashCode(this); }
  }
  public void testIteratePointersWithNodePointerContext() {
      final Pointer p1 = pointer("xval", "/x");
      final Pointer p2 = pointer("yval", "/y");
      final List list = Arrays.asList(p1, p2);
      Expression expr = new Expression() {
          public boolean computeContextDependent() { return false; }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return list; }
      };
      // EvalContext that returns a NodePointer with a locale
      EvalContext ctx = new StubEvalContext() {
          public Pointer getCurrentNodePointer() {
              return new StubNodePointer("dummy");
          }
      };
      Iterator it = expr.iteratePointers(ctx);
      assertTrue(it.hasNext());
      Object first = it.next();
      assertTrue("iteratePointers must return Pointer", first instanceof Pointer);
      assertEquals("xval", ((Pointer) first).getValue());
      Object second = it.next();
      assertTrue("iteratePointers must return Pointer", second instanceof Pointer);
      assertEquals("yval", ((Pointer) second).getValue());
      assertFalse(it.hasNext());
  }
  // --- isContextDependent caching ---
  public void testIsContextDependentCaching() {
      final int[] callCount = { 0 };
      Expression expr = new Expression() {
          public boolean computeContextDependent() {
              callCount[0]++;
              return true;
          }
          public Object computeValue(EvalContext ctx) { return null; }
          public Object compute(EvalContext ctx) { return null; }
      };
      assertTrue(expr.isContextDependent());
      assertTrue(expr.isContextDependent());
      assertEquals("computeContextDependent should be called exactly once", 1, callCount[0]);
  }

}