package org.apache.commons.jxpath.ri.compiler;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;

 import junit.framework.TestCase;

 import org.apache.commons.jxpath.Pointer;
 import org.apache.commons.jxpath.ri.EvalContext;
 import org.apache.commons.jxpath.ri.QName;
 import org.apache.commons.jxpath.ri.model.NodePointer;

 /**
  * Tests for {@link Expression} focusing on the bug JXPATH-50:
  * iterate() over a node-set returned by an extension function must
  * return bean values, not Pointer paths.
  */
 public class ExpressionBug2Test extends TestCase {

     private static final QName QNAME = new QName(null, "value");
     private static final Locale LOCALE = Locale.getDefault();

     // ---------- helpers ----------

     /**
      * Creates a thin Expression stub that returns {@code result} from compute().
      */
     private Expression exprReturning(final Object result) {
         return new Expression() {
             public boolean computeContextDependent() {
                 return false;
             }
             public Object computeValue(EvalContext context) {
                 // when needed, return first element if it is a collection
                 if (result instanceof List && !((List) result).isEmpty()) {
                     return ((List) result).get(0);
                 }
                 return null;
             }
             public Object compute(EvalContext context) {
                 return result;
             }
         };
     }

     /**
      * Wraps a bean into a NodePointer.
      */
     private NodePointer pointerFor(Object bean) {
         return NodePointer.newNodePointer(QNAME, bean, LOCALE);
     }

     /**
      * Collects all values from an iterator into a list.
      */
     private List collect(Iterator it) {
         List collected = new ArrayList();
         while (it.hasNext()) {
             collected.add(it.next());
         }
         return collected;
     }

     // ----- iterate : node-set must unwrap pointers to bean values -----

     /**
      * When an extension function returns a node-set (list of NodePointers),
      * Expression.iterate() must return an iterator that yields the
      * underlying bean values, not the pointers themselves.
      *
      * Bug JXPATH-50: the actual output contained pointer paths like
      * [/beans[1], /beans[2]] instead of the expected bean values.
      */
     public void testIterateUnwrapsPointerNodes() {
         String bean1 = "Nested: Name 1";
         String bean2 = "Nested: Name 2";
         List nodeSet = Arrays.asList(new Object[] {
             pointerFor(bean1), pointerFor(bean2)
         });
         Expression expr = exprReturning(nodeSet);
         Iterator it = expr.iterate(null); // context not needed in our stub
         List values = collect(it);
         assertEquals("iterate() must unwrap pointers to beans", 2, values.size());
         assertTrue("should contain first bean value", values.contains(bean1));
         assertTrue("should contain second bean value", values.contains(bean2));
     }

     public void testIterateUnwrapsSinglePointer() {
         String bean = "single bean";
         List nodeSet = Collections.singletonList(pointerFor(bean));
         Iterator it = exprReturning(nodeSet).iterate(null);
         assertTrue(it.hasNext());
         assertEquals(bean, it.next());
         assertFalse(it.hasNext());
     }

     public void testIterateOnEmptyNodeSet() {
         List nodeSet = Collections.EMPTY_LIST;
         Iterator it = exprReturning(nodeSet).iterate(null);
         assertFalse(it.hasNext());
     }

     public void testIteratePreservesOrder() {
         List nodeSet = new ArrayList();
         for (int i = 0; i < 3; i++) {
             nodeSet.add(pointerFor("order-" + i));
         }
         Iterator it = exprReturning(nodeSet).iterate(null);
         for (int i = 0; i < 3; i++) {
             assertEquals("order-" + i, it.next());
         }
         assertFalse(it.hasNext());
     }

     // ----- iteratePointers : must return pointers unchanged -----

     public void testIteratePointersReturnsPointers() {
         Pointer ptr1 = pointerFor("a");
         Pointer ptr2 = pointerFor("b");
         List nodeSet = Arrays.asList(new Pointer[] { ptr1, ptr2 });
         Iterator it = exprReturning(nodeSet).iteratePointers(null);
         List pointers = collect(it);
         assertEquals(2, pointers.size());
         assertEquals(ptr1, pointers.get(0));
         assertEquals(ptr2, pointers.get(1));
     }

     public void testIteratePointersOnEmpty() {
         Iterator it = exprReturning(Collections.EMPTY_LIST).iteratePointers(null);
         assertFalse(it.hasNext());
     }

     // ----- ValueIterator behaviour -----

     public void testValueIteratorUnwrapsPointers() {
         List items = new ArrayList();
         items.add(pointerFor("abc"));
         items.add(new Integer(42));               // non-pointer passes through
         Iterator vi = new Expression.ValueIterator(items.iterator());
         assertTrue(vi.hasNext());
         assertEquals("abc", vi.next());           // unwrapped
         assertTrue(vi.hasNext());
         assertEquals(new Integer(42), vi.next()); // unchanged
         assertFalse(vi.hasNext());
     }

     public void testValueIteratorRemoveThrows() {
         Iterator vi = new Expression.ValueIterator(Collections.EMPTY_LIST.iterator());
         try {
             vi.remove();
             fail("Should have thrown UnsupportedOperationException");
         } catch (UnsupportedOperationException expected) {
             // expected
         }
     }

     // ----- PointerIterator behaviour -----

     public void testPointerIteratorWrapsPlainObjects() {
         List beans = Arrays.asList(new Object[] { "one", "two" });
         Iterator pi = new Expression.PointerIterator(beans.iterator(), QNAME, LOCALE);
         assertTrue(pi.hasNext());
         Object first = pi.next();
         assertTrue("must be a Pointer", first instanceof Pointer);
         NodePointer np = (NodePointer) first;
         assertEquals("one", np.getValue());

         Object second = pi.next();
         assertTrue(second instanceof Pointer);
         assertEquals("two", ((Pointer) second).getValue());
         assertFalse(pi.hasNext());
     }

     public void testPointerIteratorPreservesExistingPointers() {
         Pointer ptr = pointerFor("original");
         List items = Collections.singletonList(ptr);
         Iterator pi = new Expression.PointerIterator(items.iterator(), QNAME, LOCALE);
         assertTrue(pi.hasNext());
         Object next = pi.next();
         assertSame("should pass existing pointer unchanged", ptr, next);
         assertFalse(pi.hasNext());
     }

     public void testPointerIteratorRemoveThrows() {
         Iterator pi = new Expression.PointerIterator(
             Collections.EMPTY_LIST.iterator(), QNAME, LOCALE);
         try {
             pi.remove();
             fail("Should have thrown UnsupportedOperationException");
         } catch (UnsupportedOperationException expected) {
             // expected
         }
     }
 }
