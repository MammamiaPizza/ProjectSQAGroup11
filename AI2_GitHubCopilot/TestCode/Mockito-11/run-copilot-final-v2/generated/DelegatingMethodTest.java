package org.mockito.internal.creation;

 import org.junit.Test;

 import java.lang.reflect.Method;

 import static org.junit.Assert.*;

 public class DelegatingMethodTest {

     /** Helper with assorted method signatures used across tests. */
     public static class Helper {
         public void foo() {}
         public void bar() {}
         public void foo(String s) {}
         public String baz() { return ""; }
     }

     /** Second helper class that declares a method with the same signature as Helper.foo
      *  but a different declaring class. */
     public static class Helper2 {
         public void foo() {}
     }

     // --- fault‑triggering tests -------------------------------------------------

     @Test
     public void equals_should_return_true_when_equal() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm1 = new DelegatingMethod(m);
         DelegatingMethod dm2 = new DelegatingMethod(m);
         assertTrue("Two DelegatingMethods wrapping the same method must be equal",
                 dm1.equals(dm2));
     }

     @Test
     public void equals_should_return_true_when_self() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm = new DelegatingMethod(m);
         assertTrue("DelegatingMethod must be equal to itself",
                 dm.equals(dm));
     }

     // --- normal / documented behaviour -------------------------------------------

     @Test
     public void equals_raw_method() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm = new DelegatingMethod(m);
         assertTrue("Javadoc promises equality with the wrapped raw Method",
                 dm.equals(m));
     }

     // --- negative / boundary cases -----------------------------------------------

     @Test
     public void equals_null() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm = new DelegatingMethod(m);
         assertFalse("Must not be equal to null", dm.equals(null));
     }

     @Test
     public void equals_different_method() throws Exception {
         Method foo = Helper.class.getMethod("foo");
         Method bar = Helper.class.getMethod("bar");
         DelegatingMethod dm1 = new DelegatingMethod(foo);
         DelegatingMethod dm2 = new DelegatingMethod(bar);
         assertFalse("Methods with different names must not be equal",
                 dm1.equals(dm2));
     }

     @Test
     public void equals_same_name_different_params() throws Exception {
         Method fooNoArg = Helper.class.getMethod("foo");
         Method fooString = Helper.class.getMethod("foo", String.class);
         DelegatingMethod dm1 = new DelegatingMethod(fooNoArg);
         DelegatingMethod dm2 = new DelegatingMethod(fooString);
         assertFalse("Methods with same name but different parameter types must not be equal",
                 dm1.equals(dm2));
     }

     @Test
     public void equals_different_return_type() throws Exception {
         Method foo = Helper.class.getMethod("foo");
         Method baz = Helper.class.getMethod("baz");
         DelegatingMethod dm1 = new DelegatingMethod(foo);
         DelegatingMethod dm2 = new DelegatingMethod(baz);
         assertFalse("Methods with different return types must not be equal",
                 dm1.equals(dm2));
     }

     @Test
     public void equals_same_signature_different_class() throws Exception {
         Method foo1 = Helper.class.getMethod("foo");
         Method foo2 = Helper2.class.getMethod("foo");
         DelegatingMethod dm1 = new DelegatingMethod(foo1);
         DelegatingMethod dm2 = new DelegatingMethod(foo2);
         assertFalse("Methods with same signature but different declaring class must not be equal",
                 dm1.equals(dm2));
     }

     @Test
     public void equals_non_method_object() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm = new DelegatingMethod(m);
         assertFalse("Must not be equal to an unrelated type",
                 dm.equals("some string"));
     }

     // --- contract checks ---------------------------------------------------------

     @Test
     public void hashCode_consistent_with_equals() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm1 = new DelegatingMethod(m);
         DelegatingMethod dm2 = new DelegatingMethod(m);
         // dm1 and dm2 should be equal, therefore they must share hashCode
         assertTrue("Equal objects must be equal", dm1.equals(dm2));
         assertEquals("Equal objects must have equal hash codes",
                 dm1.hashCode(), dm2.hashCode());
     }

     @Test
     public void equals_is_symmetric() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod dm1 = new DelegatingMethod(m);
         DelegatingMethod dm2 = new DelegatingMethod(m);
         assertEquals("Equality must be symmetric",
                 dm1.equals(dm2), dm2.equals(dm1));
     }

     @Test
     public void equals_is_transitive() throws Exception {
         Method m = Helper.class.getMethod("foo");
         DelegatingMethod d1 = new DelegatingMethod(m);
         DelegatingMethod d2 = new DelegatingMethod(m);
         DelegatingMethod d3 = new DelegatingMethod(m);
         assertTrue(d1.equals(d2));
         assertTrue(d2.equals(d3));
         assertTrue("Equality must be transitive", d1.equals(d3));
     }
 }
